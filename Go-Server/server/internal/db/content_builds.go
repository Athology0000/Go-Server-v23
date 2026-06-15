package db

import (
	"context"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

// ContentBuild is a forge build row (see migrations/010_content_builds.sql).
type ContentBuild struct {
	ID        string     `json:"id"`
	BuildID   string     `json:"build_id"`
	Module    string     `json:"module"`
	Status    string     `json:"status"`
	JarPath   *string    `json:"-"`
	JarSHA256 *string    `json:"jar_sha256"`
	DLLPath   *string    `json:"-"`
	DLLSHA256 *string    `json:"dll_sha256"`
	Error     *string    `json:"error,omitempty"`
	CreatedBy *string    `json:"created_by,omitempty"`
	CreatedAt time.Time  `json:"created_at"`
	DecidedBy *string    `json:"decided_by,omitempty"`
	DecidedAt *time.Time `json:"decided_at,omitempty"`
	Notes     *string    `json:"notes,omitempty"`
}

const contentBuildCols = `id, build_id, module, status, jar_path, jar_sha256, dll_path, dll_sha256,
	error, created_by, created_at, decided_by, decided_at, notes`

func scanContentBuild(row scannable) (*ContentBuild, error) {
	b := &ContentBuild{}
	err := row.Scan(&b.ID, &b.BuildID, &b.Module, &b.Status, &b.JarPath, &b.JarSHA256,
		&b.DLLPath, &b.DLLSHA256, &b.Error, &b.CreatedBy, &b.CreatedAt,
		&b.DecidedBy, &b.DecidedAt, &b.Notes)
	return b, err
}

// CreateBuild inserts a new build row in the 'building' state. createdBy is optional.
func CreateBuild(ctx context.Context, pool *pgxpool.Pool, buildID, module string, createdBy *string) (*ContentBuild, error) {
	row := pool.QueryRow(ctx,
		`INSERT INTO content_builds (build_id, module, status, created_by)
		 VALUES ($1, $2, 'building', $3)
		 RETURNING `+contentBuildCols,
		buildID, module, createdBy)
	return scanContentBuild(row)
}

// MarkBuildPending records the staged artifacts and moves the build to pending_approval.
func MarkBuildPending(ctx context.Context, pool *pgxpool.Pool, buildID, jarPath, jarSHA256, dllPath, dllSHA256 string) (*ContentBuild, error) {
	row := pool.QueryRow(ctx,
		`UPDATE content_builds
		 SET status = 'pending_approval', jar_path = $2, jar_sha256 = $3, dll_path = $4, dll_sha256 = $5, error = NULL
		 WHERE build_id = $1
		 RETURNING `+contentBuildCols,
		buildID, jarPath, jarSHA256, dllPath, dllSHA256)
	return scanContentBuild(row)
}

// MarkBuildFailed records a build failure with its error message.
func MarkBuildFailed(ctx context.Context, pool *pgxpool.Pool, buildID, errMsg string) error {
	_, err := pool.Exec(ctx,
		`UPDATE content_builds SET status = 'failed', error = $2 WHERE build_id = $1`,
		buildID, errMsg)
	return err
}

// GetBuildByBuildID fetches a single build by its forge build id.
func GetBuildByBuildID(ctx context.Context, pool *pgxpool.Pool, buildID string) (*ContentBuild, error) {
	row := pool.QueryRow(ctx, `SELECT `+contentBuildCols+` FROM content_builds WHERE build_id = $1`, buildID)
	return scanContentBuild(row)
}

// ListBuildsByStatus returns builds in a given status, newest first (drives the panel
// pending-approval list).
func ListBuildsByStatus(ctx context.Context, pool *pgxpool.Pool, status string, limit int) ([]*ContentBuild, error) {
	rows, err := pool.Query(ctx,
		`SELECT `+contentBuildCols+` FROM content_builds WHERE status = $1 ORDER BY created_at DESC LIMIT $2`,
		status, limit)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var out []*ContentBuild
	for rows.Next() {
		b, err := scanContentBuild(rows)
		if err != nil {
			return nil, err
		}
		out = append(out, b)
	}
	return out, rows.Err()
}
