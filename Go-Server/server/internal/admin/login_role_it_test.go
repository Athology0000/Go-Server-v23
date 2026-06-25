package admin

import (
	"bytes"
	"context"
	"encoding/json"
	"net/http/httptest"
	"testing"
	"time"

	"github.com/gofiber/fiber/v2"
	ccrypto "github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/db"
	"github.com/phantom/server/internal/middleware"
)

// TestAdminLoginRoleIsAccountBound proves the L2 fix end-to-end: the login session role comes from
// accounts.admin_role, NOT from the account's pile of valid admin_tokens. An account bound to
// support that ALSO still holds a stale, valid super_admin token must get a support session (the old
// MAX-over-valid-tokens rule would have re-granted super_admin). A regular account with no bound
// admin_role is denied even with a correct password. Skips when WATERMARK_IT_DB_URL is unset.
func TestAdminLoginRoleIsAccountBound(t *testing.T) {
	pool := buildsTestPool(t)
	ctx := context.Background()

	const password = "correct-horse-battery-staple"
	hash, err := ccrypto.HashPassword(password)
	if err != nil {
		t.Fatalf("HashPassword: %v", err)
	}
	username := "l2-admin-" + rh(t)
	acct, err := db.CreateAccount(ctx, pool, username, hash, nil)
	if err != nil {
		t.Fatalf("CreateAccount: %v", err)
	}
	t.Cleanup(func() {
		c, cancel := context.WithTimeout(context.Background(), 10*time.Second)
		defer cancel()
		_, _ = pool.Exec(c, `DELETE FROM admin_tokens WHERE admin_username = $1`, username)
		_, _ = pool.Exec(c, `DELETE FROM accounts WHERE id = $1`, acct.ID)
	})

	// Account is authoritatively bound to SUPPORT...
	if _, err := db.SetAccountAdminRole(ctx, pool, username, "support"); err != nil {
		t.Fatalf("SetAccountAdminRole: %v", err)
	}
	// ...but still holds a stale, valid SUPER_ADMIN token. The old rule would have re-granted from it.
	_, stHash, err := ccrypto.GenerateToken()
	if err != nil {
		t.Fatalf("GenerateToken: %v", err)
	}
	if _, err := db.CreateAdminToken(ctx, pool, stHash, username, "super_admin", time.Now().Add(time.Hour)); err != nil {
		t.Fatalf("seed stale super_admin token: %v", err)
	}

	app := fiber.New()
	app.Post("/admin/auth/login", handleAdminLogin(pool))

	if role := loginRole(t, app, username, password); role != "admin" {
		t.Fatalf("login role = %q, want \"admin\" (support); a stale super_admin token must NOT re-grant", role)
	}

	// A regular account (no bound admin_role) is denied even with the correct password.
	plainUser := "l2-plain-" + rh(t)
	puHash, err := ccrypto.HashPassword(password)
	if err != nil {
		t.Fatalf("HashPassword plain: %v", err)
	}
	pu, err := db.CreateAccount(ctx, pool, plainUser, puHash, nil)
	if err != nil {
		t.Fatalf("CreateAccount plain: %v", err)
	}
	t.Cleanup(func() {
		c, cancel := context.WithTimeout(context.Background(), 10*time.Second)
		defer cancel()
		_, _ = pool.Exec(c, `DELETE FROM accounts WHERE id = $1`, pu.ID)
	})
	resp := postLogin(t, app, plainUser, password)
	if resp.Code != 401 {
		t.Fatalf("non-admin login status = %d, want 401 (no bound admin_role)", resp.Code)
	}
}

// TestAdminAuthCapsStaleTokenByAccountRole proves the API-bearer half of the L2 fix: a still-valid
// super_admin token whose account was demoted to viewer is capped to viewer by AdminAuth, so it can
// no longer reach a super_admin-guarded route. This is what makes demotion actually take effect
// (the stale token would otherwise re-grant super_admin on every API call). Skips without the DB.
func TestAdminAuthCapsStaleTokenByAccountRole(t *testing.T) {
	pool := buildsTestPool(t)
	ctx := context.Background()

	username := "l2-cap-" + rh(t)
	hash, err := ccrypto.HashPassword("irrelevant-pw")
	if err != nil {
		t.Fatalf("HashPassword: %v", err)
	}
	acct, err := db.CreateAccount(ctx, pool, username, hash, nil)
	if err != nil {
		t.Fatalf("CreateAccount: %v", err)
	}
	t.Cleanup(func() {
		c, cancel := context.WithTimeout(context.Background(), 10*time.Second)
		defer cancel()
		_, _ = pool.Exec(c, `DELETE FROM admin_tokens WHERE admin_username = $1`, username)
		_, _ = pool.Exec(c, `DELETE FROM accounts WHERE id = $1`, acct.ID)
	})

	// Account demoted to viewer, but still holds a valid super_admin token.
	if _, err := db.SetAccountAdminRole(ctx, pool, username, "viewer"); err != nil {
		t.Fatalf("SetAccountAdminRole: %v", err)
	}
	staleRaw, staleHash, err := ccrypto.GenerateToken()
	if err != nil {
		t.Fatalf("GenerateToken: %v", err)
	}
	if _, err := db.CreateAdminToken(ctx, pool, staleHash, username, "super_admin", time.Now().Add(time.Hour)); err != nil {
		t.Fatalf("CreateAdminToken: %v", err)
	}

	app := fiber.New()
	app.Get("/admin/super-only", middleware.AdminAuth(pool, "super_admin"), func(c *fiber.Ctx) error {
		return c.SendStatus(200)
	})
	req := httptest.NewRequest("GET", "/admin/super-only", nil)
	req.Header.Set("Authorization", "Bearer "+staleRaw)
	resp, err := app.Test(req, 10000)
	if err != nil {
		t.Fatalf("Test: %v", err)
	}
	if resp.StatusCode != 403 {
		t.Fatalf("stale super_admin token on a viewer-demoted account = %d, want 403 (capped by account role)", resp.StatusCode)
	}
}

func postLogin(t *testing.T, app *fiber.App, username, password string) *httptest.ResponseRecorder {
	t.Helper()
	body, err := json.Marshal(map[string]string{"username": username, "password": password})
	if err != nil {
		t.Fatalf("marshal: %v", err)
	}
	req := httptest.NewRequest("POST", "/admin/auth/login", bytes.NewReader(body))
	req.Header.Set("Content-Type", "application/json")
	resp, err := app.Test(req, 10000)
	if err != nil {
		t.Fatalf("Test: %v", err)
	}
	rec := httptest.NewRecorder()
	rec.Code = resp.StatusCode
	if resp.Body != nil {
		_, _ = rec.Body.ReadFrom(resp.Body)
	}
	return rec
}

func loginRole(t *testing.T, app *fiber.App, username, password string) string {
	t.Helper()
	resp := postLogin(t, app, username, password)
	if resp.Code != 200 {
		t.Fatalf("login status = %d, want 200; body=%s", resp.Code, resp.Body.String())
	}
	var out struct {
		User struct {
			Role string `json:"role"`
		} `json:"user"`
	}
	if err := json.Unmarshal(resp.Body.Bytes(), &out); err != nil {
		t.Fatalf("decode: %v", err)
	}
	return out.User.Role
}
