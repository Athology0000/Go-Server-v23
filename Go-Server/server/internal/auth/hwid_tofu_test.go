package auth

import (
	"testing"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/audit"
	"github.com/phantom/server/internal/config"
	"github.com/phantom/server/internal/db"
	"github.com/phantom/server/internal/entitlement"
	"github.com/redis/go-redis/v9"
)

// HWID trust-on-first-use at /auth/verify-session, gated by HWID_TOFU_ENABLED. Exercised against a
// live Postgres + Redis. Skips without TEST_DB_URL/TEST_REDIS_URL.

func newTestServiceHwid(pool *pgxpool.Pool, rdb *redis.Client, hwidTofu bool) *Service {
	cfg := &config.Config{
		SessionTTLHours:         12,
		HeartbeatLivenessWindow: 15 * time.Minute,
		HwidTofuEnabled:         hwidTofu,
	}
	return New(pool, rdb, entitlement.New(pool), audit.New(pool),
		make([]byte, 32), make([]byte, 32), "http://localhost:8080", cfg)
}

// With TOFU enabled: the first HWID is pinned, a matching HWID passes, a different one is denied.
func TestVerifySessionHwidTofu(t *testing.T) {
	ctx, pool, rdb := testEnv(t)
	rawToken, _, _ := seedSession(t, ctx, pool)
	sess, err := db.GetSessionByTokenHash(ctx, pool, mustHash(t, rawToken))
	if err != nil {
		t.Fatalf("load session: %v", err)
	}
	deviceID := sess.DeviceID

	svc := newTestServiceHwid(pool, rdb, true)

	// First sight pins the HWID.
	res, err := svc.VerifySession(ctx, rawToken, "127.0.0.1", "", "hwid-aaa")
	if err != nil {
		t.Fatalf("verify (first): %v", err)
	}
	if !res.Authorized {
		t.Fatalf("first verify not authorized: %s", res.Reason)
	}
	dev, err := db.GetDeviceByID(ctx, pool, deviceID)
	if err != nil {
		t.Fatalf("load device: %v", err)
	}
	if dev.HWIDHash == nil || *dev.HWIDHash != "hwid-aaa" {
		t.Fatalf("HWID not pinned on first sight: %v", dev.HWIDHash)
	}

	// A matching HWID is accepted.
	res2, err := svc.VerifySession(ctx, rawToken, "127.0.0.1", "", "hwid-aaa")
	if err != nil {
		t.Fatalf("verify (match): %v", err)
	}
	if !res2.Authorized {
		t.Errorf("matching HWID not authorized: %s", res2.Reason)
	}

	// A different HWID is denied.
	res3, err := svc.VerifySession(ctx, rawToken, "127.0.0.1", "", "hwid-bbb")
	if err != nil {
		t.Fatalf("verify (mismatch): %v", err)
	}
	if res3.Authorized || res3.Reason != "hwid_mismatch" {
		t.Errorf("mismatched HWID: authorized=%t reason=%q, want authorized=false reason=hwid_mismatch",
			res3.Authorized, res3.Reason)
	}
}

// With TOFU disabled (the default), the HWID the loader sends is ignored — nothing is pinned and a
// changed HWID does not block. Preserves the deliberate no-HWID-enforcement behaviour.
func TestVerifySessionHwidIgnoredWhenDisabled(t *testing.T) {
	ctx, pool, rdb := testEnv(t)
	rawToken, _, _ := seedSession(t, ctx, pool)
	sess, err := db.GetSessionByTokenHash(ctx, pool, mustHash(t, rawToken))
	if err != nil {
		t.Fatalf("load session: %v", err)
	}
	deviceID := sess.DeviceID

	svc := newTestServiceHwid(pool, rdb, false)

	res, err := svc.VerifySession(ctx, rawToken, "127.0.0.1", "", "hwid-aaa")
	if err != nil {
		t.Fatalf("verify: %v", err)
	}
	if !res.Authorized {
		t.Fatalf("not authorized with TOFU disabled: %s", res.Reason)
	}
	dev, err := db.GetDeviceByID(ctx, pool, deviceID)
	if err != nil {
		t.Fatalf("load device: %v", err)
	}
	if dev.HWIDHash != nil && *dev.HWIDHash != "" {
		t.Errorf("HWID was pinned while TOFU disabled: %v", *dev.HWIDHash)
	}
}
