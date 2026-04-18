package content

import (
	"context"
	"errors"
	"path/filepath"
	"slices"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/cobalt/server/internal/db"
	"github.com/cobalt/server/internal/entitlement"
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
	return &Service{pool: pool, entSvc: entSvc, contentDir: contentDir}
}

func (s *Service) GetManifest(ctx context.Context, accountID, manifestID string) (*db.ContentManifest, error) {
	ent, err := s.entSvc.Resolve(ctx, accountID)
	if err != nil || !ent.Authorized {
		return nil, ErrNotEntitled
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

func (s *Service) ModulePath(ctx context.Context, accountID, name string) (string, error) {
	ent, err := s.entSvc.Resolve(ctx, accountID)
	if err != nil || !ent.Authorized {
		return "", ErrNotEntitled
	}
	if !slices.Contains(ent.EnabledModules, name) {
		return "", ErrNotEntitled
	}
	return filepath.Join(s.contentDir, "modules", filepath.Base(name)), nil
}

func (s *Service) NativePath(ctx context.Context, accountID, name string) (string, error) {
	ent, err := s.entSvc.Resolve(ctx, accountID)
	if err != nil || !ent.Authorized {
		return "", ErrNotEntitled
	}
	if !slices.Contains(ent.NativeComponents, name) {
		return "", ErrNotEntitled
	}
	return filepath.Join(s.contentDir, "native", filepath.Base(name)), nil
}
