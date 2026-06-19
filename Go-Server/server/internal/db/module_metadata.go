package db

import (
	"context"

	"github.com/jackc/pgx/v5/pgxpool"
)

// GetAllModuleDeps returns the full module_name -> depends_on edge map (bare ids). The table is small
// (one row per module that declares dependencies), so the whole map is fetched at once.
func GetAllModuleDeps(ctx context.Context, pool *pgxpool.Pool) (map[string][]string, error) {
	rows, err := pool.Query(ctx, `SELECT module_name, depends_on FROM module_metadata`)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	out := make(map[string][]string)
	for rows.Next() {
		var name string
		var deps []string
		if err := rows.Scan(&name, &deps); err != nil {
			return nil, err
		}
		out[name] = deps
	}
	return out, rows.Err()
}

// UpsertModuleMetadata sets a module's declared dependencies (bare ids). A nil slice is stored as an
// empty array (the module declares no dependencies).
func UpsertModuleMetadata(ctx context.Context, pool *pgxpool.Pool, name string, dependsOn []string) error {
	if dependsOn == nil {
		dependsOn = []string{}
	}
	_, err := pool.Exec(ctx,
		`INSERT INTO module_metadata (module_name, depends_on) VALUES ($1, $2)
		 ON CONFLICT (module_name) DO UPDATE SET depends_on = EXCLUDED.depends_on, updated_at = now()`,
		name, dependsOn)
	return err
}
