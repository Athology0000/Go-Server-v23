package db

import (
	"context"
	"encoding/json"
	"time"
	"github.com/jackc/pgx/v5/pgxpool"
)

type ManifestModule struct {
	Name     string `json:"name"`
	URL      string `json:"url"`
	SHA256   string `json:"sha256"`
	Required bool   `json:"required"`
}

type ManifestNative struct {
	Name     string `json:"name"`
	URL      string `json:"url"`
	SHA256   string `json:"sha256"`
	Required bool   `json:"required"`
}

type ContentManifest struct {
	ID               string
	BuildID          string
	Channel          string
	Modules          []ManifestModule
	NativeComponents []ManifestNative
	Signature        string
	ExpiresAt        time.Time
	Revoked          bool
	CreatedAt        time.Time
}

func GetLatestManifest(ctx context.Context, pool *pgxpool.Pool, channel string) (*ContentManifest, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, build_id, channel, modules, native_components, signature, expires_at, revoked, created_at
		 FROM content_manifests
		 WHERE channel = $1 AND revoked = false AND expires_at > now()
		 ORDER BY created_at DESC LIMIT 1`, channel)
	return scanManifest(row)
}

func GetManifestByID(ctx context.Context, pool *pgxpool.Pool, id string) (*ContentManifest, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, build_id, channel, modules, native_components, signature, expires_at, revoked, created_at
		 FROM content_manifests WHERE id = $1`, id)
	return scanManifest(row)
}

func scanManifest(row scannable) (*ContentManifest, error) {
	m := &ContentManifest{}
	var modulesJSON, nativesJSON []byte
	err := row.Scan(&m.ID, &m.BuildID, &m.Channel, &modulesJSON, &nativesJSON,
		&m.Signature, &m.ExpiresAt, &m.Revoked, &m.CreatedAt)
	if err != nil {
		return nil, err
	}
	json.Unmarshal(modulesJSON, &m.Modules)
	json.Unmarshal(nativesJSON, &m.NativeComponents)
	return m, nil
}

func CreateManifest(ctx context.Context, pool *pgxpool.Pool, m *ContentManifest) (*ContentManifest, error) {
	modulesJSON, _ := json.Marshal(m.Modules)
	nativesJSON, _ := json.Marshal(m.NativeComponents)
	row := pool.QueryRow(ctx,
		`INSERT INTO content_manifests (build_id, channel, modules, native_components, signature, expires_at)
		 VALUES ($1, $2, $3, $4, $5, $6)
		 RETURNING id, build_id, channel, modules, native_components, signature, expires_at, revoked, created_at`,
		m.BuildID, m.Channel, modulesJSON, nativesJSON, m.Signature, m.ExpiresAt)
	return scanManifest(row)
}
