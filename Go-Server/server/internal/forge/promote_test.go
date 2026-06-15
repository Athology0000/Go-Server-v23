package forge

import (
	"bytes"
	"context"
	"crypto/rand"
	"encoding/hex"
	"os"
	"path/filepath"
	"testing"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/db"
)

func promoteTestPool(t *testing.T) *pgxpool.Pool {
	t.Helper()
	dbURL := os.Getenv("WATERMARK_IT_DB_URL")
	if dbURL == "" {
		t.Skip("WATERMARK_IT_DB_URL unset; skipping promotion DB test")
	}
	ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
	defer cancel()
	pool, err := pgxpool.New(ctx, dbURL)
	if err != nil {
		t.Fatalf("connect: %v", err)
	}
	t.Cleanup(pool.Close) // registered first → runs after row-delete cleanups (LIFO)
	if err := pool.Ping(ctx); err != nil {
		t.Skipf("postgres not reachable (%v); skipping", err)
	}
	return pool
}

func rhex(t *testing.T) string {
	t.Helper()
	b := make([]byte, 6)
	if _, err := rand.Read(b); err != nil {
		t.Fatalf("rand: %v", err)
	}
	return hex.EncodeToString(b)
}

// stagePendingBuild writes staged jar+dll bytes and records a pending_approval build pointing
// at them, returning the row and the staged bytes.
func stagePendingBuild(t *testing.T, ctx context.Context, pool *pgxpool.Pool, stagingRoot, module string) (*db.ContentBuild, []byte, []byte) {
	t.Helper()
	buildID := "build-promote-" + rhex(t)
	jarBytes := []byte("FORGED-JAR-" + buildID)
	dllBytes := MarkDLL([]byte("NATIVE-"+buildID), buildID, "marker-secret")

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
	if _, err := db.MarkBuildPending(ctx, pool, buildID, jarPath, sha256Hex(jarBytes), dllPath, sha256Hex(dllBytes)); err != nil {
		t.Fatalf("MarkBuildPending: %v", err)
	}
	b, err := db.GetBuildByBuildID(ctx, pool, buildID)
	if err != nil {
		t.Fatalf("GetBuildByBuildID: %v", err)
	}
	return b, jarBytes, dllBytes
}

func TestPromoteInstallsAndGoesLive(t *testing.T) {
	pool := promoteTestPool(t)
	ctx := context.Background()

	contentDir := t.TempDir()
	p := &Promoter{ContentDir: contentDir}
	module := "phantom-promote-" + rhex(t)

	b, jarBytes, dllBytes := stagePendingBuild(t, ctx, pool, t.TempDir(), module)

	updated, err := p.Promote(ctx, pool, b, "super-tester")
	if err != nil {
		t.Fatalf("Promote: %v", err)
	}
	if updated.Status != "live" {
		t.Fatalf("status = %q, want live", updated.Status)
	}
	if updated.DecidedBy == nil || *updated.DecidedBy != "super-tester" {
		t.Fatalf("decided_by not recorded: %v", updated.DecidedBy)
	}

	// installed and byte-exact in the live content dir.
	gotJar := mustRead(t, filepath.Join(contentDir, "modules", module+".jar"))
	if !bytes.Equal(gotJar, jarBytes) {
		t.Error("installed jar bytes differ from staged")
	}
	gotDll := mustRead(t, filepath.Join(contentDir, "native", module+".dll"))
	if !bytes.Equal(gotDll, dllBytes) {
		t.Error("installed dll bytes differ from staged")
	}
}

func TestPromoteSupersedesPriorLive(t *testing.T) {
	pool := promoteTestPool(t)
	ctx := context.Background()

	contentDir := t.TempDir()
	p := &Promoter{ContentDir: contentDir}
	module := "phantom-promote-" + rhex(t)

	// promote build A -> live
	a, _, _ := stagePendingBuild(t, ctx, pool, t.TempDir(), module)
	if _, err := p.Promote(ctx, pool, a, "super-tester"); err != nil {
		t.Fatalf("promote A: %v", err)
	}

	// promote build B (same module) -> live; A must become superseded
	b, _, _ := stagePendingBuild(t, ctx, pool, t.TempDir(), module)
	if _, err := p.Promote(ctx, pool, b, "super-tester"); err != nil {
		t.Fatalf("promote B: %v", err)
	}

	aAfter, _ := db.GetBuildByBuildID(ctx, pool, a.BuildID)
	if aAfter.Status != "superseded" {
		t.Fatalf("prior build A status = %q, want superseded", aAfter.Status)
	}
	live, err := db.GetLiveBuild(ctx, pool, module)
	if err != nil {
		t.Fatalf("GetLiveBuild: %v", err)
	}
	if live.BuildID != b.BuildID {
		t.Fatalf("live build = %s, want %s", live.BuildID, b.BuildID)
	}
}

func TestPromoteRejectsTamperedStaging(t *testing.T) {
	pool := promoteTestPool(t)
	ctx := context.Background()

	contentDir := t.TempDir()
	p := &Promoter{ContentDir: contentDir}
	module := "phantom-promote-" + rhex(t)

	b, _, _ := stagePendingBuild(t, ctx, pool, t.TempDir(), module)
	// tamper with the staged jar after its sha256 was recorded.
	if err := os.WriteFile(*b.JarPath, []byte("TAMPERED"), 0o644); err != nil {
		t.Fatal(err)
	}

	if _, err := p.Promote(ctx, pool, b, "super-tester"); err == nil {
		t.Fatal("expected promotion to fail on a tampered staged jar")
	}
	// nothing should have been installed.
	if _, err := os.Stat(filepath.Join(contentDir, "modules", module+".jar")); err == nil {
		t.Error("tampered build must not install a live jar")
	}
}

func TestDenyMarksDeniedAndPurgesStaging(t *testing.T) {
	pool := promoteTestPool(t)
	ctx := context.Background()

	p := &Promoter{ContentDir: t.TempDir()}
	module := "phantom-promote-" + rhex(t)

	b, _, _ := stagePendingBuild(t, ctx, pool, t.TempDir(), module)
	stageDir := filepath.Dir(*b.JarPath)

	updated, err := p.Deny(ctx, pool, b, "super-tester")
	if err != nil {
		t.Fatalf("Deny: %v", err)
	}
	if updated.Status != "denied" {
		t.Fatalf("status = %q, want denied", updated.Status)
	}
	if _, err := os.Stat(stageDir); !os.IsNotExist(err) {
		t.Error("denied build's staged dir should be purged")
	}
}
