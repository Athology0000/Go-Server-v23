package forge

import (
	"context"
	"crypto/sha256"
	"encoding/hex"
	"fmt"
	"os"
	"path/filepath"

	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/db"
)

// Promoter installs an approved build's staged artifacts into the live content dir. Serving
// (module download + the on-demand signed manifest) is filesystem-derived, so installing the
// files into content/modules and content/native IS the go-live; the DB row update is
// record-keeping. The promoter never re-signs a manifest — BuildStableManifest rebuilds and
// signs from the filesystem on the next request, picking up the new jar + dll hashes.
type Promoter struct {
	ContentDir string
}

// Promote verifies a pending build's staged artifacts against their recorded checksums, atomically
// installs them as the live module + native, supersedes any prior live build for the module, and
// marks this build live. decidedBy is the approving superadmin.
func (p *Promoter) Promote(ctx context.Context, pool *pgxpool.Pool, build *db.ContentBuild, decidedBy string) (*db.ContentBuild, error) {
	if p.ContentDir == "" {
		return nil, fmt.Errorf("promoter content dir not configured")
	}
	if build.Status != "pending_approval" {
		return nil, fmt.Errorf("build %s is %s, not pending_approval", build.BuildID, build.Status)
	}
	if build.JarPath == nil || build.JarSHA256 == nil || build.DLLPath == nil || build.DLLSHA256 == nil {
		return nil, fmt.Errorf("build %s is missing staged artifacts", build.BuildID)
	}

	modulesDir := filepath.Join(p.ContentDir, "modules")
	nativeDir := filepath.Join(p.ContentDir, "native")

	// Install both before touching the DB. A failure here leaves the prior live artifacts in
	// place (rename is atomic) and the row still pending — safe to retry.
	if err := installVerified(*build.JarPath, *build.JarSHA256, modulesDir, build.Module+".jar"); err != nil {
		return nil, fmt.Errorf("install jar: %w", err)
	}
	if err := installVerified(*build.DLLPath, *build.DLLSHA256, nativeDir, build.Module+".dll"); err != nil {
		return nil, fmt.Errorf("install dll: %w", err)
	}

	if err := db.SupersedePriorLive(ctx, pool, build.Module, build.BuildID); err != nil {
		return nil, fmt.Errorf("supersede prior live: %w", err)
	}
	return db.ApproveBuild(ctx, pool, build.BuildID, decidedBy)
}

// Deny marks a build denied and purges its staged artifacts (the whole staged build dir).
func (p *Promoter) Deny(ctx context.Context, pool *pgxpool.Pool, build *db.ContentBuild, decidedBy string) (*db.ContentBuild, error) {
	updated, err := db.DenyBuild(ctx, pool, build.BuildID, decidedBy)
	if err != nil {
		return nil, err
	}
	// Best-effort purge of the staged build dir (parent of the staged jar).
	if build.JarPath != nil {
		_ = os.RemoveAll(filepath.Dir(*build.JarPath))
	}
	return updated, nil
}

// installVerified reads srcPath, confirms its sha256 matches wantSHA (rejecting a staged artifact
// that was tampered with after the build), and atomically places it at destDir/destName via a
// temp-write + rename (rename replaces any prior live artifact in one step).
func installVerified(srcPath, wantSHA, destDir, destName string) error {
	data, err := os.ReadFile(srcPath)
	if err != nil {
		return err
	}
	sum := sha256.Sum256(data)
	if got := hex.EncodeToString(sum[:]); got != wantSHA {
		return fmt.Errorf("staged artifact %s sha256 %s != recorded %s", srcPath, got, wantSHA)
	}
	if err := os.MkdirAll(destDir, 0o755); err != nil {
		return err
	}
	tmp := filepath.Join(destDir, "."+destName+".tmp")
	if err := os.WriteFile(tmp, data, 0o644); err != nil {
		return err
	}
	if err := os.Rename(tmp, filepath.Join(destDir, destName)); err != nil {
		_ = os.Remove(tmp)
		return err
	}
	return nil
}
