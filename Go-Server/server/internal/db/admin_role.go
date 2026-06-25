package db

import (
	"context"
	"errors"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

// LookupAccountAdminRole returns the account's bound admin role and whether the account exists. A
// non-existent account (exists == false) is distinct from an existing account with no admin role
// (exists == true, role == ""): the former is a service/API token with no login identity, the latter
// is an account explicitly not (or no longer) an admin. accounts.admin_role is the authoritative
// role for BOTH admin login and (as a cap) API bearer authorization, so a stale high-role token can
// never out-rank the account's current role.
func LookupAccountAdminRole(ctx context.Context, pool *pgxpool.Pool, username string) (role string, exists bool, err error) {
	var r *string
	err = pool.QueryRow(ctx, `SELECT admin_role FROM accounts WHERE username = $1`, username).Scan(&r)
	if errors.Is(err, pgx.ErrNoRows) {
		return "", false, nil
	}
	if err != nil {
		return "", false, err
	}
	if r == nil {
		return "", true, nil
	}
	return *r, true, nil
}

// SetAccountAdminRole sets (or clears, when role == "") the account's bound admin role — the
// authoritative grant used by admin login and the API-auth role cap. Returns the rows affected so
// callers can tell whether the username matched an account (an admin token may be issued for a
// username with no account row; that token still works as an API bearer, but there is no login
// identity to bind a role to).
func SetAccountAdminRole(ctx context.Context, pool *pgxpool.Pool, username, role string) (int64, error) {
	var arg any
	if role != "" {
		arg = role
	}
	tag, err := pool.Exec(ctx,
		`UPDATE accounts SET admin_role = $2, updated_at = now() WHERE username = $1`, username, arg)
	if err != nil {
		return 0, err
	}
	return tag.RowsAffected(), nil
}
