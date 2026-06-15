package auth

import (
	"context"
	"crypto/rand"
	"encoding/hex"
	"errors"
	"testing"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/db"
	"github.com/phantom/server/internal/entitlement"
)

// These exercise the real challenge-response handshake (Start -> Finish) against a live
// Postgres + Redis. The device secret is seeded encrypted under the same master key the
// Service holds, so a genuine HMAC proof can be forged the way the loader's native does.
// Skips without TEST_DB_URL/TEST_REDIS_URL.

// seedEnrolledDevice provisions account -> device(hwid_pending, known secret) -> pro license.
func seedEnrolledDevice(t *testing.T, ctx context.Context, pool *pgxpool.Pool, masterKey []byte) (username, accountID string, secret []byte) {
	t.Helper()
	suffix := make([]byte, 8)
	if _, err := rand.Read(suffix); err != nil {
		t.Fatal(err)
	}
	username = "cr-it-" + hex.EncodeToString(suffix)

	account, err := db.CreateAccount(ctx, pool, username, "x-not-a-real-hash", nil)
	if err != nil {
		t.Fatalf("create account: %v", err)
	}
	t.Cleanup(func() { cleanupAccount(pool, account.ID) })

	secret = make([]byte, 32)
	if _, err := rand.Read(secret); err != nil {
		t.Fatal(err)
	}
	enc, err := crypto.EncryptAESGCM(masterKey, secret)
	if err != nil {
		t.Fatalf("encrypt device secret: %v", err)
	}
	device, err := db.CreateDevice(ctx, pool, account.ID, "127.0.0.1", enc)
	if err != nil {
		t.Fatalf("create device: %v", err)
	}
	if err := db.MarkEnrolled(ctx, pool, device.ID); err != nil {
		t.Fatalf("mark enrolled: %v", err)
	}
	licExpiry := time.Now().Add(24 * time.Hour)
	if _, err := db.CreateLicense(ctx, pool, account.ID, "pro", time.Now(), &licExpiry); err != nil {
		t.Fatalf("create license: %v", err)
	}
	return username, account.ID, secret
}

// seedManifestForChannel inserts a non-expired manifest so Finish's manifest resolution succeeds.
func seedManifestForChannel(t *testing.T, ctx context.Context, pool *pgxpool.Pool, channel string) {
	t.Helper()
	created, err := db.CreateManifest(ctx, pool, &db.ContentManifest{
		BuildID:          "cr-test",
		Channel:          channel,
		MinLoaderVersion: "1",
		ModuleKey:        "",
		Modules:          []db.ManifestModule{},
		NativeComponents: []db.ManifestNative{},
		Signature:        "test-sig",
		ExpiresAt:        time.Now().Add(time.Hour),
		Epoch:            1,
	})
	if err != nil {
		t.Fatalf("seed manifest: %v", err)
	}
	t.Cleanup(func() {
		_, _ = pool.Exec(context.Background(), `DELETE FROM content_manifests WHERE id = $1`, created.ID)
	})
}

// A valid HMAC proof over the issued challenge yields an authorized session.
func TestChallengeResponseAuthFlow(t *testing.T) {
	ctx, pool, rdb := testEnv(t)
	masterKey := make([]byte, 32)
	svc := newTestService(pool, rdb, 15*time.Minute)
	username, accountID, secret := seedEnrolledDevice(t, ctx, pool, masterKey)

	// Finish resolves a manifest for the entitlement's channel — seed exactly that channel.
	ent, err := entitlement.New(pool).Resolve(ctx, accountID)
	if err != nil {
		t.Fatalf("resolve entitlement: %v", err)
	}
	if !ent.Authorized {
		t.Fatalf("seeded pro account not authorized: %s", ent.Reason)
	}
	seedManifestForChannel(t, ctx, pool, ent.ContentChannel)

	start, err := svc.Start(ctx, username, "Tester", "127.0.0.1")
	if err != nil {
		t.Fatalf("start: %v", err)
	}
	if start.Challenge == "" {
		t.Fatal("start returned empty challenge")
	}

	proof := crypto.HMACHash(secret, []byte(start.Challenge))
	fin, err := svc.Finish(ctx, username, proof, "127.0.0.1", "Tester")
	if err != nil {
		t.Fatalf("finish: %v", err)
	}
	if !fin.Authorized || fin.SessionToken == "" {
		t.Fatalf("expected an authorized session, got authorized=%t token_present=%t reason=%q",
			fin.Authorized, fin.SessionToken != "", fin.Reason)
	}
	if fin.PlanTier != "pro" {
		t.Errorf("plan tier = %q, want pro", fin.PlanTier)
	}
}

// A bad proof is rejected, and five consecutive failures suspend the device.
func TestFinishRejectsBadProofAndSuspends(t *testing.T) {
	ctx, pool, rdb := testEnv(t)
	masterKey := make([]byte, 32)
	svc := newTestService(pool, rdb, 15*time.Minute)
	username, accountID, _ := seedEnrolledDevice(t, ctx, pool, masterKey)

	device, err := db.GetDeviceByAccountID(ctx, pool, accountID)
	if err != nil {
		t.Fatalf("load device: %v", err)
	}

	for i := 0; i < 5; i++ {
		if _, err := svc.Start(ctx, username, "Tester", "127.0.0.1"); err != nil {
			t.Fatalf("start %d: %v", i, err)
		}
		if _, err := svc.Finish(ctx, username, "deadbeef", "127.0.0.1", "Tester"); !errors.Is(err, ErrBadProof) {
			t.Fatalf("finish %d: got err=%v, want ErrBadProof", i, err)
		}
	}

	suspended, err := db.GetDeviceByID(ctx, pool, device.ID)
	if err != nil {
		t.Fatalf("reload device: %v", err)
	}
	if suspended.BindingStatus != "suspended" {
		t.Errorf("device binding_status = %q, want suspended after 5 bad proofs", suspended.BindingStatus)
	}
}
