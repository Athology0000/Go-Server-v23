package db

import (
	"context"
	"errors"
	"fmt"
	"regexp"

	"github.com/jackc/pgx/v5/pgxpool"
)

// ErrInvalidModuleMetadata is returned by ValidateModuleMetadata/UpsertModuleMetadata when a module
// name or a declared dependency is not a well-formed bare module id. The admin write path maps it to
// a 400 (vs a 500 for storage errors).
var ErrInvalidModuleMetadata = errors.New("invalid module metadata")

// moduleIDPattern is the bare entitlement-id shape shared by entitlements.enabled_modules and
// module_metadata.depends_on: lowercase letters, digits, underscore and hyphen only.
var moduleIDPattern = regexp.MustCompile(`^[a-z0-9_-]+$`)

// validModuleID reports whether id is a well-formed bare module id (1..64 chars, [a-z0-9_-]).
func validModuleID(id string) bool {
	return len(id) >= 1 && len(id) <= 64 && moduleIDPattern.MatchString(id)
}

// ValidateModuleMetadata enforces that a module name and every declared dependency are well-formed
// bare module ids, with no self-dependency or duplicates. This is a security boundary, not just
// hygiene: depends_on is signed into the Ed25519 manifest, and the Go signer, the Java verifier and
// the C++ native verifier must canonicalize it to byte-identical JSON. Allowing arbitrary bytes (e.g.
// 0x08/0x0c, which Go's encoding/json escapes as \b/\f, or HTML/control/Unicode) would let an operator
// store a dependency id that signs server-side but reconstructs to different bytes on the client,
// failing the signature and bricking the whole channel fail-closed. Constraining ids to [a-z0-9_-]
// keeps every dependency in the deliverable entitlement-id namespace and removes that divergence
// surface entirely. Returns ErrInvalidModuleMetadata (wrapped with a human-readable detail) on any
// violation.
func ValidateModuleMetadata(name string, dependsOn []string) error {
	if !validModuleID(name) {
		return fmt.Errorf("%w: module_name %q must match [a-z0-9_-] (1-64 chars)",
			ErrInvalidModuleMetadata, name)
	}
	seen := make(map[string]struct{}, len(dependsOn))
	for _, dep := range dependsOn {
		if !validModuleID(dep) {
			return fmt.Errorf("%w: dependency %q must match [a-z0-9_-] (1-64 chars)",
				ErrInvalidModuleMetadata, dep)
		}
		if dep == name {
			return fmt.Errorf("%w: module %q cannot depend on itself", ErrInvalidModuleMetadata, name)
		}
		if _, dup := seen[dep]; dup {
			return fmt.Errorf("%w: duplicate dependency %q", ErrInvalidModuleMetadata, dep)
		}
		seen[dep] = struct{}{}
	}
	return nil
}

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
// empty array (the module declares no dependencies). The name and every dependency are validated first
// (see ValidateModuleMetadata); an ill-formed id is rejected with ErrInvalidModuleMetadata and nothing
// is written, so the signed manifest can never carry a signature-bricking dependency id.
func UpsertModuleMetadata(ctx context.Context, pool *pgxpool.Pool, name string, dependsOn []string) error {
	if err := ValidateModuleMetadata(name, dependsOn); err != nil {
		return err
	}
	if dependsOn == nil {
		dependsOn = []string{}
	}
	_, err := pool.Exec(ctx,
		`INSERT INTO module_metadata (module_name, depends_on) VALUES ($1, $2)
		 ON CONFLICT (module_name) DO UPDATE SET depends_on = EXCLUDED.depends_on, updated_at = now()`,
		name, dependsOn)
	return err
}
