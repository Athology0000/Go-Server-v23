package content

import (
	"context"
	"errors"
	"os"
	"path/filepath"
	"slices"
	"strings"
	"time"

	"github.com/phantom/server/internal/db"
	"github.com/phantom/server/internal/entitlement"
	"github.com/jackc/pgx/v5/pgxpool"
)

var (
	ErrNotEntitled = errors.New("not entitled")
	ErrExpired     = errors.New("manifest expired or revoked")
	ErrNotFound    = errors.New("not found")
)

type Service struct {
	pool       *pgxpool.Pool
	entSvc     *entitlement.Service
	contentDir string
	signingKey []byte
	moduleKey  []byte
	baseURL    string
}

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

	return BuildStableManifest(ctx, s.contentDir, s.baseURL, ent.ContentChannel, s.signingKey, s.moduleKey)
}

func (s *Service) ModuleBytes(ctx context.Context, accountID, name string) ([]byte, string, error) {
	ent, err := s.entSvc.Resolve(ctx, accountID)
	if err != nil || !ent.Authorized {
		return nil, "", ErrNotEntitled
	}

	moduleName := NormalizeModuleName(name)

	allowed :=
		moduleName == "phantom-core" ||
		slices.Contains(ent.EnabledModules, "*") ||
			slices.Contains(ent.EnabledModules, moduleName) ||
			slices.Contains(ent.EnabledModules, moduleName+".jar")

	if !allowed {
		return nil, "", ErrNotEntitled
	}

	path, err := moduleFilePath(s.contentDir, moduleName)
	if err != nil {
		return nil, "", err
	}

	bytecode, err := readModuleArtifact(path, s.moduleKey)
	if err != nil {
		return nil, "", err
	}

	return bytecode, moduleName + ".jar", nil
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

