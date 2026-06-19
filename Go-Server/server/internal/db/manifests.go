package db

import (
	"context"
	"encoding/json"
	"errors"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

// ErrManifestNotFound is returned when a manifest id does not exist.
var ErrManifestNotFound = errors.New("manifest not found")

type ManifestModule struct {
	Name      string   `json:"name"`
	URL       string   `json:"url"`
	SHA256    string   `json:"sha256"`
	Required  bool     `json:"required"`
	InitOrder int      `json:"init_order"`
	// DependsOn lists the bare ids of modules this one needs co-loaded (e.g. commission -> combat,
	// mining). omitempty keeps modules without deps byte-identical in the signed payload; the client
	// (Java + native) reproduces it and refuses to activate a module whose deps are absent.
	DependsOn []string `json:"depends_on,omitempty"`
}

type ManifestNative struct {
	Name     string `json:"name"`
	URL      string `json:"url"`
	SHA256   string `json:"sha256"`
	Required bool   `json:"required"`
}

type ContentManifest struct {
	ID               string           `json:"id"`
	BuildID          string           `json:"build_id"`
	Channel          string           `json:"channel"`
	MinLoaderVersion string           `json:"minimum_loader_version"`
	ModuleKey        string           `json:"module_key,omitempty"`
	Modules          []ManifestModule `json:"modules"`
	NativeComponents []ManifestNative `json:"native_components"`
	Signature        string           `json:"signature"`
	ExpiresAt        time.Time        `json:"expires_at"`
	// Epoch-millis mirror of ExpiresAt: it is inside the signed payload, so the client reads this
	// exact integer (not the RFC3339 expires_at) when reconstructing the bytes to verify.
	ExpiresAtMillis int64 `json:"expires_at_millis"`
	// Epoch is the signed monotonic per-channel ordering key for rollback protection. The client
	// rejects any manifest whose epoch is below the highest it has already accepted (its native
	// high-water-mark). Stored in its own column; 0 for legacy rows (treated as oldest).
	Epoch     int64     `json:"epoch"`
	Revoked   bool      `json:"revoked"`
	CreatedAt time.Time `json:"created_at"`
}

func GetLatestManifest(ctx context.Context, pool *pgxpool.Pool, channel string) (*ContentManifest, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, build_id, channel, min_loader_version, module_key, modules, native_components, signature, expires_at, epoch, revoked, created_at
		 FROM content_manifests
		 WHERE channel = $1 AND revoked = false AND expires_at > now()
		 ORDER BY created_at DESC LIMIT 1`, channel)
	return scanManifest(row)
}

func GetManifestByID(ctx context.Context, pool *pgxpool.Pool, id string) (*ContentManifest, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, build_id, channel, min_loader_version, module_key, modules, native_components, signature, expires_at, epoch, revoked, created_at
		 FROM content_manifests WHERE id = $1`, id)
	return scanManifest(row)
}

func scanManifest(row scannable) (*ContentManifest, error) {
	m := &ContentManifest{}
	var modulesJSON, nativesJSON []byte
	err := row.Scan(&m.ID, &m.BuildID, &m.Channel, &m.MinLoaderVersion, &m.ModuleKey, &modulesJSON, &nativesJSON,
		&m.Signature, &m.ExpiresAt, &m.Epoch, &m.Revoked, &m.CreatedAt)
	if err != nil {
		return nil, err
	}
	json.Unmarshal(modulesJSON, &m.Modules)
	json.Unmarshal(nativesJSON, &m.NativeComponents)
	// Derive the signed epoch-millis from the stored instant so the client verifies the exact
	// integer that was signed (the millis floor round-trips through timestamptz).
	m.ExpiresAtMillis = m.ExpiresAt.UnixMilli()
	return m, nil
}

// RevokeManifest marks a content manifest revoked so GetLatestManifest stops returning it (the
// resolver then falls through to the per-license stable manifest). Idempotent on already-revoked rows.
func RevokeManifest(ctx context.Context, pool *pgxpool.Pool, id string) error {
	tag, err := pool.Exec(ctx, `UPDATE content_manifests SET revoked = true WHERE id = $1`, id)
	if err != nil {
		return err
	}
	if tag.RowsAffected() == 0 {
		return ErrManifestNotFound
	}
	return nil
}

func CreateManifest(ctx context.Context, pool *pgxpool.Pool, m *ContentManifest) (*ContentManifest, error) {
	modulesJSON, _ := json.Marshal(m.Modules)
	nativesJSON, _ := json.Marshal(m.NativeComponents)
	row := pool.QueryRow(ctx,
		`INSERT INTO content_manifests (build_id, channel, min_loader_version, module_key, modules, native_components, signature, expires_at, epoch)
		 VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
		 RETURNING id, build_id, channel, min_loader_version, module_key, modules, native_components, signature, expires_at, epoch, revoked, created_at`,
		m.BuildID, m.Channel, m.MinLoaderVersion, m.ModuleKey, modulesJSON, nativesJSON, m.Signature, m.ExpiresAt, m.Epoch)
	return scanManifest(row)
}
