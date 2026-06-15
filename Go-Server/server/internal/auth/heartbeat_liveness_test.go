package auth

import (
	"context"
	"crypto/rand"
	"encoding/hex"
	"encoding/json"
	"errors"
	"os"
	"testing"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/audit"
	"github.com/phantom/server/internal/config"
	"github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/db"
	"github.com/phantom/server/internal/entitlement"
	"github.com/redis/go-redis/v9"
)

// These integration tests exercise the real auth.Service heartbeat path against a live
// Postgres + Redis (the docker-compose stack). They prove the liveness-heartbeat behaviour
// introduced for the session-liveness work end-to-end: a heartbeat refreshes liveness without
// extending the absolute session cap, writes an activity row, and a session that stops
// heartbeating (or loses its entitlement) is rejected.
//
// Point TEST_DB_URL + TEST_REDIS_URL at the stack, e.g.
//   TEST_DB_URL=postgres://phantom:phantom@localhost:5432/phantom?sslmode=disable
//   TEST_REDIS_URL=redis://localhost:6379
// Skips cleanly when either is unset.

func testEnv(t *testing.T) (context.Context, *pgxpool.Pool, *redis.Client) {
	t.Helper()
	dbURL := os.Getenv("TEST_DB_URL")
	redisURL := os.Getenv("TEST_REDIS_URL")
	if dbURL == "" || redisURL == "" {
		t.Skip("TEST_DB_URL/TEST_REDIS_URL unset; skipping auth integration test")
	}
	ctx := context.Background()
	pool, err := pgxpool.New(ctx, dbURL)
	if err != nil {
		t.Fatalf("connect postgres: %v", err)
	}
	if err := pool.Ping(ctx); err != nil {
		pool.Close()
		t.Skipf("postgres not reachable (%v); skipping", err)
	}
	t.Cleanup(pool.Close)

	opt, err := redis.ParseURL(redisURL)
	if err != nil {
		t.Fatalf("parse TEST_REDIS_URL: %v", err)
	}
	rdb := redis.NewClient(opt)
	t.Cleanup(func() { _ = rdb.Close() })
	return ctx, pool, rdb
}

// newTestService builds a real auth.Service with a fixed liveness window.
func newTestService(pool *pgxpool.Pool, rdb *redis.Client, livenessWindow time.Duration) *Service {
	cfg := &config.Config{
		SessionTTLHours:         12,
		HeartbeatLivenessWindow: livenessWindow,
	}
	return New(pool, rdb, entitlement.New(pool), audit.New(pool),
		make([]byte, 32), make([]byte, 32), "http://localhost:8080", cfg)
}

// seedSession provisions account -> device -> pro license -> session and returns the raw token
// and session id. The account (and everything FK'd to it) is cleaned up when the test ends.
func seedSession(t *testing.T, ctx context.Context, pool *pgxpool.Pool) (rawToken, sessionID string, originalExpiry time.Time) {
	t.Helper()
	suffix := make([]byte, 8)
	if _, err := rand.Read(suffix); err != nil {
		t.Fatal(err)
	}
	username := "hb-it-" + hex.EncodeToString(suffix)

	account, err := db.CreateAccount(ctx, pool, username, "x-not-a-real-hash", nil)
	if err != nil {
		t.Fatalf("create account: %v", err)
	}
	t.Cleanup(func() { cleanupAccount(pool, account.ID) })

	device, err := db.CreateDevice(ctx, pool, account.ID, "127.0.0.1", []byte{0})
	if err != nil {
		t.Fatalf("create device: %v", err)
	}

	licExpiry := time.Now().Add(24 * time.Hour)
	if _, err := db.CreateLicense(ctx, pool, account.ID, "pro", time.Now(), &licExpiry); err != nil {
		t.Fatalf("create license: %v", err)
	}

	raw, tokenHash, err := crypto.GenerateToken()
	if err != nil {
		t.Fatalf("generate token: %v", err)
	}
	sessExpiry := time.Now().Add(1 * time.Hour)
	sess, err := db.CreateSession(ctx, pool, tokenHash, device.ID, account.ID,
		"pro", []string{"*"}, []string{"*"}, &licExpiry, sessExpiry, "127.0.0.1")
	if err != nil {
		t.Fatalf("create session: %v", err)
	}
	return raw, sess.ID, sess.ExpiresAt
}

func cleanupAccount(pool *pgxpool.Pool, accountID string) {
	ctx := context.Background()
	for _, q := range []string{
		`DELETE FROM session_activity WHERE account_id = $1`,
		`DELETE FROM sessions WHERE account_id = $1`,
		`DELETE FROM licenses WHERE account_id = $1`,
		`DELETE FROM audit_log WHERE account_id = $1`,
		`DELETE FROM devices WHERE account_id = $1`,
		`DELETE FROM accounts WHERE id = $1`,
	} {
		_, _ = pool.Exec(ctx, q, accountID)
	}
}

// A heartbeat must refresh liveness (last_heartbeat_at) and log activity, but must NOT roll the
// absolute expires_at cap forward — and a session that has gone stale (no recent heartbeat) is
// rejected even though its cap has not yet elapsed.
func TestHeartbeatLivenessAndActivity(t *testing.T) {
	ctx, pool, rdb := testEnv(t)
	svc := newTestService(pool, rdb, 15*time.Minute)
	rawToken, sessionID, _ := seedSession(t, ctx, pool)

	before, err := db.GetSessionByTokenHash(ctx, pool, mustHash(t, rawToken))
	if err != nil {
		t.Fatalf("load session: %v", err)
	}

	res, err := svc.Heartbeat(ctx, rawToken, "127.0.0.1", json.RawMessage(`["alive"]`))
	if err != nil {
		t.Fatalf("heartbeat: %v", err)
	}
	if res.PlanTier != "pro" {
		t.Errorf("plan tier = %q, want pro", res.PlanTier)
	}

	after, err := db.GetSessionByTokenHash(ctx, pool, mustHash(t, rawToken))
	if err != nil {
		t.Fatalf("reload session: %v", err)
	}
	if !after.ExpiresAt.Equal(before.ExpiresAt) {
		t.Errorf("heartbeat must not extend expires_at: before=%s after=%s",
			before.ExpiresAt, after.ExpiresAt)
	}
	if !after.LastHeartbeatAt.After(before.LastHeartbeatAt) {
		t.Errorf("heartbeat must advance last_heartbeat_at: before=%s after=%s",
			before.LastHeartbeatAt, after.LastHeartbeatAt)
	}

	var activityRows int
	if err := pool.QueryRow(ctx,
		`SELECT count(*) FROM session_activity WHERE session_id = $1`, sessionID,
	).Scan(&activityRows); err != nil {
		t.Fatalf("count activity: %v", err)
	}
	if activityRows != 1 {
		t.Errorf("expected 1 session_activity row, got %d", activityRows)
	}

	// Stale liveness: push last_heartbeat_at past the window; the session is now closed even
	// though its absolute expires_at is still in the future.
	if _, err := pool.Exec(ctx,
		`UPDATE sessions SET last_heartbeat_at = now() - interval '1 hour' WHERE id = $1`, sessionID,
	); err != nil {
		t.Fatalf("stale update: %v", err)
	}
	if _, err := svc.Heartbeat(ctx, rawToken, "127.0.0.1", nil); !errors.Is(err, ErrSessionInvalid) {
		t.Errorf("stale session: got err=%v, want ErrSessionInvalid", err)
	}
}

// When the account's license is revoked, the next heartbeat must deny and revoke the session.
func TestHeartbeatRevokesOnEntitlementLoss(t *testing.T) {
	ctx, pool, rdb := testEnv(t)
	svc := newTestService(pool, rdb, 15*time.Minute)
	rawToken, sessionID, _ := seedSession(t, ctx, pool)

	// First beat authorizes.
	if _, err := svc.Heartbeat(ctx, rawToken, "127.0.0.1", nil); err != nil {
		t.Fatalf("first heartbeat: %v", err)
	}

	// Revoke the license out from under the live session.
	if _, err := pool.Exec(ctx,
		`UPDATE licenses SET status = 'revoked' WHERE account_id = (SELECT account_id FROM sessions WHERE id = $1)`,
		sessionID,
	); err != nil {
		t.Fatalf("revoke license: %v", err)
	}

	if _, err := svc.Heartbeat(ctx, rawToken, "127.0.0.1", nil); !errors.Is(err, ErrSessionInvalid) {
		t.Errorf("after revoke: got err=%v, want ErrSessionInvalid", err)
	}

	var revoked bool
	if err := pool.QueryRow(ctx, `SELECT revoked FROM sessions WHERE id = $1`, sessionID).Scan(&revoked); err != nil {
		t.Fatalf("read revoked: %v", err)
	}
	if !revoked {
		t.Error("heartbeat must mark the session revoked when entitlement is lost")
	}
}

func mustHash(t *testing.T, rawToken string) string {
	t.Helper()
	h, err := crypto.HashToken(rawToken)
	if err != nil {
		t.Fatalf("hash token: %v", err)
	}
	return h
}
