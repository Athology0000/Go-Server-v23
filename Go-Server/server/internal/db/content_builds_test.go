package db

import (
	"context"
	"crypto/rand"
	"encoding/hex"
	"os"
	"testing"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

// TestContentBuildLifecycle exercises the build row state transitions against a live Postgres.
// Gated on WATERMARK_IT_DB_URL (the docker-compose stack); skips cleanly when unset.
func TestContentBuildLifecycle(t *testing.T) {
	dbURL := os.Getenv("WATERMARK_IT_DB_URL")
	if dbURL == "" {
		t.Skip("WATERMARK_IT_DB_URL unset; skipping content_builds DB test")
	}

	ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
	defer cancel()

	pool, err := pgxpool.New(ctx, dbURL)
	if err != nil {
		t.Fatalf("connect: %v", err)
	}
	// Register Close via t.Cleanup (not defer) so it runs AFTER the row-delete cleanups
	// below — t.Cleanup is LIFO and fires after the test returns, whereas a defer would
	// close the pool before those deletes could use it.
	t.Cleanup(pool.Close)
	if err := pool.Ping(ctx); err != nil {
		t.Skipf("postgres not reachable (%v); skipping", err)
	}

	bid := "build-test-" + randHex(t)
	by := "forge-test-operator"
	t.Cleanup(func() {
		c, cancel := context.WithTimeout(context.Background(), 10*time.Second)
		defer cancel()
		_, _ = pool.Exec(c, `DELETE FROM content_builds WHERE build_id = $1`, bid)
	})

	// building
	created, err := CreateBuild(ctx, pool, bid, "phantom-buildtest", &by)
	if err != nil {
		t.Fatalf("CreateBuild: %v", err)
	}
	if created.Status != "building" {
		t.Fatalf("status = %q, want building", created.Status)
	}
	if created.CreatedBy == nil || *created.CreatedBy != by {
		t.Fatalf("created_by not persisted: %v", created.CreatedBy)
	}

	// pending_approval with staged artifacts
	pending, err := MarkBuildPending(ctx, pool, bid, "/stage/x.jar", "jarsha", "/stage/x.dll", "dllsha")
	if err != nil {
		t.Fatalf("MarkBuildPending: %v", err)
	}
	if pending.Status != "pending_approval" {
		t.Fatalf("status = %q, want pending_approval", pending.Status)
	}
	if pending.JarSHA256 == nil || *pending.JarSHA256 != "jarsha" ||
		pending.DLLSHA256 == nil || *pending.DLLSHA256 != "dllsha" {
		t.Fatal("staged checksums not persisted")
	}

	// listed under pending_approval
	list, err := ListBuildsByStatus(ctx, pool, "pending_approval", 100)
	if err != nil {
		t.Fatalf("ListBuildsByStatus: %v", err)
	}
	found := false
	for _, b := range list {
		if b.BuildID == bid {
			found = true
		}
	}
	if !found {
		t.Fatal("build not found in pending_approval list")
	}

	// fetch by id
	got, err := GetBuildByBuildID(ctx, pool, bid)
	if err != nil {
		t.Fatalf("GetBuildByBuildID: %v", err)
	}
	if got.Module != "phantom-buildtest" {
		t.Fatalf("module = %q", got.Module)
	}

	// failure path on a separate build
	fbid := "build-fail-" + randHex(t)
	t.Cleanup(func() {
		c, cancel := context.WithTimeout(context.Background(), 10*time.Second)
		defer cancel()
		_, _ = pool.Exec(c, `DELETE FROM content_builds WHERE build_id = $1`, fbid)
	})
	if _, err := CreateBuild(ctx, pool, fbid, "phantom-buildtest", nil); err != nil {
		t.Fatalf("CreateBuild(fail): %v", err)
	}
	if err := MarkBuildFailed(ctx, pool, fbid, "boom: obfuscator exploded"); err != nil {
		t.Fatalf("MarkBuildFailed: %v", err)
	}
	failed, err := GetBuildByBuildID(ctx, pool, fbid)
	if err != nil {
		t.Fatalf("GetBuildByBuildID(fail): %v", err)
	}
	if failed.Status != "failed" || failed.Error == nil || *failed.Error == "" {
		t.Fatalf("failed build not recorded: status=%q err=%v", failed.Status, failed.Error)
	}
}

func randHex(t *testing.T) string {
	t.Helper()
	b := make([]byte, 6)
	if _, err := rand.Read(b); err != nil {
		t.Fatalf("rand: %v", err)
	}
	return hex.EncodeToString(b)
}
