package content

import (
	"context"
	"errors"
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
	watermarker *Watermarker // optional; when set+enabled, .jar downloads are stamped per-account
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

	return BuildStableManifest(ctx, s.contentDir, s.baseURL, ent.ContentChannel, s.signingKey, s.moduleKey, ent.EnabledModules)
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

	path, err := moduleFilePath(s.contentDir, moduleName)
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
	// never re-stamped here. Fail-open: a watermark hiccup serves the un-watermarked jar rather
	// than breaking the download.
	if s.watermarker.Enabled() && strings.HasSuffix(strings.ToLower(path), ".jar") {
		stamped, werr := s.watermarker.Apply(ctx, raw, accountID)
		if werr != nil {
			log.Printf("[content.module] watermark failed account_id=%s module=%s err=%v (serving un-watermarked)",
				accountID, moduleName, werr)
		} else {
			raw = stamped
		}
	}

	return raw, filepath.Base(path), nil
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
