package content

import (
	"context"
	"crypto/sha256"
	"encoding/hex"
	"errors"
	"os"
	"path/filepath"
	"slices"
	"time"

	"github.com/cobalt/server/internal/db"
	"github.com/cobalt/server/internal/entitlement"
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
}

func New(pool *pgxpool.Pool, entSvc *entitlement.Service, contentDir string) *Service {
	return &Service{
		pool:       pool,
		entSvc:     entSvc,
		contentDir: contentDir,
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
		return s.GetDevStableManifest(ctx, accountID)
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

func (s *Service) GetDevStableManifest(ctx context.Context, accountID string) (*AddonManifest, error) {
	ent, err := s.entSvc.Resolve(ctx, accountID)
	if err != nil || !ent.Authorized {
		return nil, ErrNotEntitled
	}

	starterJarPath := filepath.Join(s.contentDir, "modules", "cobalt-starter.jar")

	hash, err := fileSHA256(starterJarPath)
	if err != nil {
		return nil, ErrNotFound
	}

	return &AddonManifest{
		BuildID:   "1.156.0-dev",
		Channel:   "stable",
		ExpiresAt: time.Now().Add(15 * time.Minute),
		Addons: []ManifestAddon{
			{
				ID:       "cobalt",
				URL:      "/content/module/cobalt-starter.jar",
				SHA256:   hash,
				Required: true,
			},
		},
	}, nil
}

func (s *Service) ModulePath(ctx context.Context, accountID, name string) (string, error) {
	ent, err := s.entSvc.Resolve(ctx, accountID)
	if err != nil || !ent.Authorized {
		return "", ErrNotEntitled
	}

	safeName := filepath.Base(name)

	// Starter-only policy:
	// The bootstrapper may request "cobalt" because that is the manifest module ID.
	// We always map it to cobalt-starter.jar.
	// This blocks cobalt-pro.jar and every other random module.
	var fileName string
	switch safeName {
	case "cobalt", "cobalt.jar", "cobalt-starter", "cobalt-starter.jar":
		fileName = "cobalt-starter.jar"
	default:
		return "", ErrNotEntitled
	}

	path := filepath.Join(s.contentDir, "modules", fileName)

	if _, err := os.Stat(path); err != nil {
		return "", ErrNotFound
	}

	return path, nil
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

func fileSHA256(path string) (string, error) {
	data, err := os.ReadFile(path)
	if err != nil {
		return "", err
	}

	sum := sha256.Sum256(data)
	return hex.EncodeToString(sum[:]), nil
}