package panel

import (
	"context"
	"crypto/rand"
	"encoding/hex"
	"net/http/httptest"
	"os"
	"strings"
	"testing"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/audit"
	ccrypto "github.com/phantom/server/internal/crypto"
)

// These exercise the panel redeem handler against a real Postgres. The handler owns its
// transaction inline (it is DB-bound: pool.Begin + raw tx queries), so unlike the enrollment
// Redeemer seam it cannot be driven by a fake — the contract under test (a non-unbound device
// is rejected, an unbound/no device succeeds) lives in SQL. Following the repo's existing
// DB-bound HTTP tests (internal/admin/builds_test.go), these skip when no Postgres is reachable
// so `go test ./...` stays green without a database.

func redeemTestPool(t *testing.T) *pgxpool.Pool {
	t.Helper()
	dbURL := os.Getenv("WATERMARK_IT_DB_URL")
	if dbURL == "" {
		t.Skip("WATERMARK_IT_DB_URL unset; skipping panel redeem HTTP test")
	}
	ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
	defer cancel()
	pool, err := pgxpool.New(ctx, dbURL)
	if err != nil {
		t.Fatalf("connect: %v", err)
	}
	t.Cleanup(pool.Close)
	if err := pool.Ping(ctx); err != nil {
		t.Skipf("postgres not reachable (%v); skipping", err)
	}
	return pool
}

func rh(t *testing.T) string {
	t.Helper()
	b := make([]byte, 6)
	if _, err := rand.Read(b); err != nil {
		t.Fatalf("rand: %v", err)
	}
	return hex.EncodeToString(b)
}

// seedAccount creates an active account and returns its id.
func seedAccount(t *testing.T, ctx context.Context, pool *pgxpool.Pool) string {
	t.Helper()
	pwHash, err := ccrypto.HashPassword("pw-" + rh(t))
	if err != nil {
		t.Fatalf("HashPassword: %v", err)
	}
	username := "panel-redeem-" + rh(t)
	var id string
	if err := pool.QueryRow(ctx,
		`INSERT INTO accounts (username, password_hash, status) VALUES ($1, $2, 'active') RETURNING id`,
		username, pwHash,
	).Scan(&id); err != nil {
		t.Fatalf("insert account: %v", err)
	}
	t.Cleanup(func() {
		c, cancel := context.WithTimeout(context.Background(), 10*time.Second)
		defer cancel()
		_, _ = pool.Exec(c, `DELETE FROM accounts WHERE id = $1`, id)
	})
	return id
}

// seedPanelSession creates a panel_sessions row for the account and returns the raw bearer token.
func seedPanelSession(t *testing.T, ctx context.Context, pool *pgxpool.Pool, accountID string) string {
	t.Helper()
	raw, hash, err := ccrypto.GenerateToken()
	if err != nil {
		t.Fatalf("GenerateToken: %v", err)
	}
	if _, err := pool.Exec(ctx,
		`INSERT INTO panel_sessions (token_hash, account_id, expires_at) VALUES ($1, $2, $3)`,
		hash, accountID, time.Now().Add(time.Hour),
	); err != nil {
		t.Fatalf("insert panel_session: %v", err)
	}
	t.Cleanup(func() {
		c, cancel := context.WithTimeout(context.Background(), 10*time.Second)
		defer cancel()
		_, _ = pool.Exec(c, `DELETE FROM panel_sessions WHERE token_hash = $1`, hash)
	})
	return raw
}

// seedDevice inserts a device for the account in the given binding_status.
func seedDevice(t *testing.T, ctx context.Context, pool *pgxpool.Pool, masterKey []byte, accountID, bindingStatus string) {
	t.Helper()
	secret := make([]byte, 32)
	if _, err := rand.Read(secret); err != nil {
		t.Fatalf("rand secret: %v", err)
	}
	enc, err := ccrypto.EncryptAESGCM(masterKey, secret)
	if err != nil {
		t.Fatalf("EncryptAESGCM: %v", err)
	}
	if _, err := pool.Exec(ctx,
		`INSERT INTO devices (account_id, binding_status, enrollment_ip, device_secret_encrypted)
		 VALUES ($1, $2, '203.0.113.1', $3)`,
		accountID, bindingStatus, enc,
	); err != nil {
		t.Fatalf("insert device: %v", err)
	}
	// Cleaned up by the account cascade DELETE only if FKs cascade; delete explicitly to be safe.
	t.Cleanup(func() {
		c, cancel := context.WithTimeout(context.Background(), 10*time.Second)
		defer cancel()
		_, _ = pool.Exec(c, `DELETE FROM devices WHERE account_id = $1`, accountID)
	})
}

// seedAvailableKey inserts an available license key and returns the raw key.
func seedAvailableKey(t *testing.T, ctx context.Context, pool *pgxpool.Pool, planTier string, durationDays int) string {
	t.Helper()
	raw := "PANEL-KEY-" + rh(t)
	hash := ccrypto.HashLicenseKey(raw)
	if _, err := pool.Exec(ctx,
		`INSERT INTO license_keys (key_hash, plan_tier, duration_days, status, created_by)
		 VALUES ($1, $2, $3, 'available', 'test')`,
		hash, planTier, durationDays,
	); err != nil {
		t.Fatalf("insert license_key: %v", err)
	}
	t.Cleanup(func() {
		c, cancel := context.WithTimeout(context.Background(), 10*time.Second)
		defer cancel()
		_, _ = pool.Exec(c, `DELETE FROM license_keys WHERE key_hash = $1`, hash)
	})
	return raw
}

func redeemApp(pool *pgxpool.Pool, masterKey []byte) *fiber.App {
	auditSvc := audit.New(pool)
	app := fiber.New()
	app.Post("/license/redeem", PanelAuth(pool), handleRedeemLicense(pool, auditSvc, masterKey))
	return app
}

func postPanelRedeem(t *testing.T, app *fiber.App, token, key string) int {
	t.Helper()
	req := httptest.NewRequest("POST", "/license/redeem", strings.NewReader(`{"key":"`+key+`"}`))
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Authorization", "Bearer "+token)
	resp, err := app.Test(req, 10000)
	if err != nil {
		t.Fatalf("app.Test: %v", err)
	}
	return resp.StatusCode
}

var redeemMasterKey = func() []byte {
	k := make([]byte, 32)
	for i := range k {
		k[i] = byte(i + 1)
	}
	return k
}()

// A redeem against an account whose existing device is no longer unbound must be rejected,
// matching the canonical enrollment redemption (ErrAlreadyEnrolled). Before the fix the panel
// path only checked EXISTS and reused a suspended/fully_bound device's secret while stacking
// the license.
func TestPanelRedeemRejectsNonUnboundDevice(t *testing.T) {
	pool := redeemTestPool(t)
	ctx := context.Background()

	for _, status := range []string{"hwid_pending", "fully_bound", "suspended", "banned"} {
		t.Run(status, func(t *testing.T) {
			acct := seedAccount(t, ctx, pool)
			tok := seedPanelSession(t, ctx, pool, acct)
			seedDevice(t, ctx, pool, redeemMasterKey, acct, status)
			key := seedAvailableKey(t, ctx, pool, "pro", 30)

			if got := postPanelRedeem(t, redeemApp(pool, redeemMasterKey), tok, key); got != 409 {
				t.Fatalf("redeem with %s device: status = %d, want 409", status, got)
			}

			// The transaction must have rolled back: key stays available, no license created.
			var keyStatus string
			if err := pool.QueryRow(ctx, `SELECT status FROM license_keys WHERE key_hash = $1`, ccrypto.HashLicenseKey(key)).Scan(&keyStatus); err != nil {
				t.Fatalf("reload key: %v", err)
			}
			if keyStatus != "available" {
				t.Errorf("key status = %q, want available (redemption must not consume the key)", keyStatus)
			}
			var licenseCount int
			if err := pool.QueryRow(ctx, `SELECT count(*) FROM licenses WHERE account_id = $1`, acct).Scan(&licenseCount); err != nil {
				t.Fatalf("count licenses: %v", err)
			}
			if licenseCount != 0 {
				t.Errorf("license count = %d, want 0 (no license stacked on a bound device)", licenseCount)
			}
		})
	}
}

// Redeeming for an account with no device creates the device and consumes the key; redeeming for
// an account whose device is still unbound also succeeds (it reuses the unbound device). Both
// mirror the enrollment path's accept conditions.
func TestPanelRedeemSucceedsForUnboundOrMissingDevice(t *testing.T) {
	pool := redeemTestPool(t)
	ctx := context.Background()

	t.Run("no device", func(t *testing.T) {
		acct := seedAccount(t, ctx, pool)
		tok := seedPanelSession(t, ctx, pool, acct)
		key := seedAvailableKey(t, ctx, pool, "pro", 30)
		// Ensure any device rows are cleaned even though none are seeded here.
		t.Cleanup(func() {
			c, cancel := context.WithTimeout(context.Background(), 10*time.Second)
			defer cancel()
			_, _ = pool.Exec(c, `DELETE FROM devices WHERE account_id = $1`, acct)
		})

		if got := postPanelRedeem(t, redeemApp(pool, redeemMasterKey), tok, key); got != 200 {
			t.Fatalf("redeem with no device: status = %d, want 200", got)
		}
		var keyStatus string
		if err := pool.QueryRow(ctx, `SELECT status FROM license_keys WHERE key_hash = $1`, ccrypto.HashLicenseKey(key)).Scan(&keyStatus); err != nil {
			t.Fatalf("reload key: %v", err)
		}
		if keyStatus != "redeemed" {
			t.Errorf("key status = %q, want redeemed", keyStatus)
		}
	})

	t.Run("unbound device", func(t *testing.T) {
		acct := seedAccount(t, ctx, pool)
		tok := seedPanelSession(t, ctx, pool, acct)
		seedDevice(t, ctx, pool, redeemMasterKey, acct, "unbound")
		key := seedAvailableKey(t, ctx, pool, "pro", 30)

		if got := postPanelRedeem(t, redeemApp(pool, redeemMasterKey), tok, key); got != 200 {
			t.Fatalf("redeem with unbound device: status = %d, want 200", got)
		}
		var keyStatus string
		if err := pool.QueryRow(ctx, `SELECT status FROM license_keys WHERE key_hash = $1`, ccrypto.HashLicenseKey(key)).Scan(&keyStatus); err != nil {
			t.Fatalf("reload key: %v", err)
		}
		if keyStatus != "redeemed" {
			t.Errorf("key status = %q, want redeemed", keyStatus)
		}
	})
}
