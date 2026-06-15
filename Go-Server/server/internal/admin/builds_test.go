package admin

import (
	"context"
	"crypto/rand"
	"crypto/sha256"
	"encoding/hex"
	"net/http/httptest"
	"os"
	"path/filepath"
	"testing"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/audit"
	ccrypto "github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/db"
	"github.com/phantom/server/internal/forge"
	"github.com/phantom/server/internal/middleware"
)

func buildsTestPool(t *testing.T) *pgxpool.Pool {
	t.Helper()
	dbURL := os.Getenv("WATERMARK_IT_DB_URL")
	if dbURL == "" {
		t.Skip("WATERMARK_IT_DB_URL unset; skipping admin builds HTTP test")
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

// seedAdminToken creates an admin_tokens row with the given role and returns the raw bearer token.
func seedAdminToken(t *testing.T, ctx context.Context, pool *pgxpool.Pool, role string) string {
	t.Helper()
	raw, hash, err := ccrypto.GenerateToken()
	if err != nil {
		t.Fatalf("GenerateToken: %v", err)
	}
	username := "admin-" + role + "-" + rh(t)
	if _, err := db.CreateAdminToken(ctx, pool, hash, username, role, time.Now().Add(time.Hour)); err != nil {
		t.Fatalf("CreateAdminToken: %v", err)
	}
	t.Cleanup(func() {
		c, cancel := context.WithTimeout(context.Background(), 10*time.Second)
		defer cancel()
		_, _ = pool.Exec(c, `DELETE FROM admin_tokens WHERE admin_username = $1`, username)
	})
	return raw
}

// seedPendingBuild stages jar+dll files and records a pending_approval build for them.
func seedPendingBuild(t *testing.T, ctx context.Context, pool *pgxpool.Pool, stagingRoot, module string) *db.ContentBuild {
	t.Helper()
	buildID := "build-http-" + rh(t)
	jarBytes := []byte("FORGED-" + buildID)
	dllBytes := []byte("NATIVE-" + buildID)
	stageDir := filepath.Join(stagingRoot, buildID)
	if err := os.MkdirAll(stageDir, 0o755); err != nil {
		t.Fatal(err)
	}
	jarPath := filepath.Join(stageDir, module+".jar")
	dllPath := filepath.Join(stageDir, module+".dll")
	if err := os.WriteFile(jarPath, jarBytes, 0o644); err != nil {
		t.Fatal(err)
	}
	if err := os.WriteFile(dllPath, dllBytes, 0o644); err != nil {
		t.Fatal(err)
	}

	if _, err := db.CreateBuild(ctx, pool, buildID, module, nil); err != nil {
		t.Fatalf("CreateBuild: %v", err)
	}
	t.Cleanup(func() {
		c, cancel := context.WithTimeout(context.Background(), 10*time.Second)
		defer cancel()
		_, _ = pool.Exec(c, `DELETE FROM content_builds WHERE build_id = $1`, buildID)
	})
	if _, err := db.MarkBuildPending(ctx, pool, buildID, jarPath, sha256Of(jarBytes), dllPath, sha256Of(dllBytes)); err != nil {
		t.Fatalf("MarkBuildPending: %v", err)
	}
	b, err := db.GetBuildByBuildID(ctx, pool, buildID)
	if err != nil {
		t.Fatalf("GetBuildByBuildID: %v", err)
	}
	return b
}

func buildsApp(pool *pgxpool.Pool, contentDir string) *fiber.App {
	super := middleware.AdminAuth(pool, "super_admin")
	promoter := &forge.Promoter{ContentDir: contentDir}
	auditSvc := audit.New(pool)
	app := fiber.New()
	app.Get("/admin/builds", super, handleListBuilds(pool))
	app.Get("/admin/builds/:id", super, handleGetBuild(pool))
	app.Post("/admin/builds/:id/approve", super, handleApproveBuild(pool, auditSvc, promoter))
	app.Post("/admin/builds/:id/deny", super, handleDenyBuild(pool, auditSvc, promoter))
	return app
}

func TestAdminApproveBuildHTTP(t *testing.T) {
	pool := buildsTestPool(t)
	ctx := context.Background()

	superTok := seedAdminToken(t, ctx, pool, "super_admin")
	contentDir := t.TempDir()
	module := "phantom-http-" + rh(t)
	b := seedPendingBuild(t, ctx, pool, t.TempDir(), module)

	app := buildsApp(pool, contentDir)
	req := httptest.NewRequest("POST", "/admin/builds/"+b.BuildID+"/approve", nil)
	req.Header.Set("Authorization", "Bearer "+superTok)
	resp, err := app.Test(req, 10000)
	if err != nil {
		t.Fatalf("Test: %v", err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("approve status = %d, want 200", resp.StatusCode)
	}

	// installed live + row live
	if _, err := os.Stat(filepath.Join(contentDir, "modules", module+".jar")); err != nil {
		t.Errorf("approved jar not installed: %v", err)
	}
	if _, err := os.Stat(filepath.Join(contentDir, "native", module+".dll")); err != nil {
		t.Errorf("approved dll not installed: %v", err)
	}
	after, _ := db.GetBuildByBuildID(ctx, pool, b.BuildID)
	if after.Status != "live" {
		t.Fatalf("row status = %q, want live", after.Status)
	}

	// approving again (now live, not pending) must 409
	req2 := httptest.NewRequest("POST", "/admin/builds/"+b.BuildID+"/approve", nil)
	req2.Header.Set("Authorization", "Bearer "+superTok)
	resp2, _ := app.Test(req2, 10000)
	if resp2.StatusCode != 409 {
		t.Fatalf("re-approve status = %d, want 409", resp2.StatusCode)
	}
}

func TestAdminDenyBuildHTTP(t *testing.T) {
	pool := buildsTestPool(t)
	ctx := context.Background()

	superTok := seedAdminToken(t, ctx, pool, "super_admin")
	module := "phantom-http-" + rh(t)
	b := seedPendingBuild(t, ctx, pool, t.TempDir(), module)

	app := buildsApp(pool, t.TempDir())
	req := httptest.NewRequest("POST", "/admin/builds/"+b.BuildID+"/deny", nil)
	req.Header.Set("Authorization", "Bearer "+superTok)
	resp, err := app.Test(req, 10000)
	if err != nil {
		t.Fatalf("Test: %v", err)
	}
	if resp.StatusCode != 200 {
		t.Fatalf("deny status = %d, want 200", resp.StatusCode)
	}
	after, _ := db.GetBuildByBuildID(ctx, pool, b.BuildID)
	if after.Status != "denied" {
		t.Fatalf("row status = %q, want denied", after.Status)
	}
}

func TestAdminBuildAuth(t *testing.T) {
	pool := buildsTestPool(t)
	ctx := context.Background()

	module := "phantom-http-" + rh(t)
	b := seedPendingBuild(t, ctx, pool, t.TempDir(), module)
	app := buildsApp(pool, t.TempDir())

	// no token -> 401
	noTok := httptest.NewRequest("POST", "/admin/builds/"+b.BuildID+"/approve", nil)
	if resp, _ := app.Test(noTok, 10000); resp.StatusCode != 401 {
		t.Fatalf("no-token status = %d, want 401", resp.StatusCode)
	}

	// viewer token (insufficient role) -> 403
	viewerTok := seedAdminToken(t, ctx, pool, "viewer")
	low := httptest.NewRequest("POST", "/admin/builds/"+b.BuildID+"/approve", nil)
	low.Header.Set("Authorization", "Bearer "+viewerTok)
	if resp, _ := app.Test(low, 10000); resp.StatusCode != 403 {
		t.Fatalf("viewer status = %d, want 403", resp.StatusCode)
	}

	// build still pending (no privileged action succeeded)
	after, _ := db.GetBuildByBuildID(ctx, pool, b.BuildID)
	if after.Status != "pending_approval" {
		t.Fatalf("row status = %q, want pending_approval (unauthorized calls must not promote)", after.Status)
	}
}

func sha256Of(b []byte) string {
	sum := sha256.Sum256(b)
	return hex.EncodeToString(sum[:])
}
