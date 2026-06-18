package content

import (
	"context"
	"errors"
	"fmt"
	"log"
	"os"
	"path/filepath"
	"slices"
	"strings"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/db"
	"github.com/phantom/server/internal/entitlement"
)

var (
	ErrNotEntitled = errors.New("not entitled")
	ErrExpired     = errors.New("manifest expired or revoked")
	ErrNotFound    = errors.New("not found")
)

type Service struct {
	pool        *pgxpool.Pool
	entSvc      *entitlement.Service
	contentDir  string
	signingKey  []byte
	moduleKey   []byte
	baseURL     string
	wmSecret    string // server-side per-license generation; empty = generation off (serve on-disk only)
	wmPepper    string
	watermarker *Watermarker // optional; when set+enabled, .jar downloads are stamped per-account
}

// SetGeneration enables server-side per-license bundle generation (pure Go) from CONTENT_DIR/_jars.
func (s *Service) SetGeneration(wmSecret, wmPepper string) {
	s.wmSecret = wmSecret
	s.wmPepper = wmPepper
}

// SetWatermarker enables per-user watermarking of .jar downloads. nil/unconfigured leaves
// downloads byte-exact (the prior behavior).
func (s *Service) SetWatermarker(w *Watermarker) { s.watermarker = w }

func New(pool *pgxpool.Pool, entSvc *entitlement.Service, contentDir string, signingKey, moduleKey []byte, baseURL string) *Service {
	return &Service{
		pool:       pool,
		entSvc:     entSvc,
		contentDir: contentDir,
		signingKey: signingKey,
		moduleKey:  moduleKey,
		baseURL:    strings.TrimRight(baseURL, "/"),
	}
}

func (s *Service) GetManifest(ctx context.Context, accountID, manifestID string) (any, error) {
	ent, err := s.entSvc.Resolve(ctx, accountID)
	if err != nil || !ent.Authorized {
		return nil, ErrNotEntitled
	}

	// Dev/test fallback.
	// This prevents "stable" from being sent to Postgres as a UUID.
	if manifestID == "stable" {
		return s.GetStableManifest(ctx, accountID)
	}

	m, err := db.GetManifestByID(ctx, s.pool, manifestID)
	if err != nil {
		return nil, ErrNotFound
	}

	if m.Revoked || time.Now().After(m.ExpiresAt) {
		return nil, ErrExpired
	}

	return m, nil
}

func (s *Service) GetStableManifest(ctx context.Context, accountID string) (*db.ContentManifest, error) {
	ent, err := s.entSvc.Resolve(ctx, accountID)
	if err != nil || !ent.Authorized {
		return nil, ErrNotEntitled
	}

	// Generate this license's watermarked bundles on first sight (no-op once cached), so the manifest
	// below hashes the freshly-generated per-license .enc.
	if err := EnsureLicenseBundles(s.contentDir, ent.LicenseID, s.moduleKey, s.wmSecret, s.wmPepper); err != nil {
		return nil, err
	}

	return BuildStableManifest(ctx, s.contentDir, ent.LicenseID, s.baseURL, ent.ContentChannel, s.signingKey, s.moduleKey, ent.EnabledModules)
}

func (s *Service) ModuleBytes(ctx context.Context, accountID, name string) ([]byte, string, error) {
	ent, err := s.entSvc.Resolve(ctx, accountID)
	if err != nil || !ent.Authorized {
		return nil, "", ErrNotEntitled
	}

	moduleName := NormalizeModuleName(name)

	if !ModuleAllowed(moduleName, ent.EnabledModules) {
		return nil, "", ErrNotEntitled
	}

	// Generate this license's bundles on first sight (no-op once cached) so the download below resolves.
	if err := EnsureLicenseBundles(s.contentDir, ent.LicenseID, s.moduleKey, s.wmSecret, s.wmPepper); err != nil {
		return nil, "", err
	}

	// Per-license routing: serve from the requesting account's CONTENT_DIR/modules/<licenseId>
	// subtree when present (its uniquely-watermarked bundles), else the shared dir.
	path, err := moduleFilePath(effectiveModulesDir(s.contentDir, ent.LicenseID), moduleName)
	if err != nil {
		return nil, "", err
	}

	// Serve the artifact exactly as stored. Encrypted .enc bundles stay
	// encrypted on the wire — the loader decrypts them in memory after
	// verifying the signed manifest. Encryption is not stripped server-side.
	raw, err := os.ReadFile(path)
	if err != nil {
		return nil, "", err
	}

	// Per-user watermark: only plain .jar artifacts. Encrypted .enc bundles must stay byte-exact
	// (the signed manifest covers their hash and the loader decrypts them verbatim), so they are
	// never re-stamped here.
	raw, err = s.stampJar(ctx, raw, path, accountID)
	if err != nil {
		return nil, "", err
	}

	return raw, filepath.Base(path), nil
}

// stampJar embeds the per-account watermark into a plain .jar artifact. When watermarking is
// enabled and the artifact is a .jar, a stamping failure is FATAL (fail-closed): we return the
// error rather than serving an un-watermarked, untraceable jar. Serving an unmarked jar would
// silently defeat leak tracing, so an availability hiccup must not erode traceability. Non-.jar
// artifacts and the disabled-watermarker case pass through byte-exact.
func (s *Service) stampJar(ctx context.Context, raw []byte, path, accountID string) ([]byte, error) {
	if !s.watermarker.Enabled() || !strings.HasSuffix(strings.ToLower(path), ".jar") {
		return raw, nil
	}
	stamped, err := s.watermarker.Apply(ctx, raw, accountID)
	if err != nil {
		log.Printf("[content.module] watermark failed account_id=%s module=%s err=%v (refusing to serve un-watermarked)",
			accountID, filepath.Base(path), err)
		return nil, fmt.Errorf("watermark module %s for account %s: %w", filepath.Base(path), accountID, err)
	}
	return stamped, nil
}

func (s *Service) NativePath(ctx context.Context, accountID, name string) (string, error) {
	ent, err := s.entSvc.Resolve(ctx, accountID)
	if err != nil || !ent.Authorized {
		return "", ErrNotEntitled
	}

	safeName := filepath.Base(name)
	nativeID := safeName

	if ext := filepath.Ext(nativeID); ext != "" {
		nativeID = nativeID[:len(nativeID)-len(ext)]
	}

	allowed :=
		len(ent.NativeComponents) == 0 ||
			slices.Contains(ent.NativeComponents, "*") ||
			slices.Contains(ent.NativeComponents, safeName) ||
			slices.Contains(ent.NativeComponents, nativeID)

	if !allowed {
		return "", ErrNotEntitled
	}

	path := filepath.Join(s.contentDir, "native", safeName)

	if _, err := os.Stat(path); err != nil {
		return "", ErrNotFound
	}

	return path, nil
}
