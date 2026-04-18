package db

import (
	"context"
	"time"
	"github.com/jackc/pgx/v5/pgxpool"
)

type AdminToken struct {
	ID            string
	TokenHash     string
	AdminUsername string
	Role          string
	ExpiresAt     time.Time
	Revoked       bool
	LastUsedAt    *time.Time
	CreatedAt     time.Time
}

func CreateAdminToken(ctx context.Context, pool *pgxpool.Pool, tokenHash, adminUsername, role string, expiresAt time.Time) (*AdminToken, error) {
	row := pool.QueryRow(ctx,
		`INSERT INTO admin_tokens (token_hash, admin_username, role, expires_at)
		 VALUES ($1, $2, $3, $4)
		 RETURNING id, token_hash, admin_username, role, expires_at, revoked, last_used_at, created_at`,
		tokenHash, adminUsername, role, expiresAt)
	t := &AdminToken{}
	err := row.Scan(&t.ID, &t.TokenHash, &t.AdminUsername, &t.Role, &t.ExpiresAt, &t.Revoked, &t.LastUsedAt, &t.CreatedAt)
	return t, err
}

func GetAdminTokenByHash(ctx context.Context, pool *pgxpool.Pool, tokenHash string) (*AdminToken, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, token_hash, admin_username, role, expires_at, revoked, last_used_at, created_at
		 FROM admin_tokens WHERE token_hash = $1`, tokenHash)
	t := &AdminToken{}
	err := row.Scan(&t.ID, &t.TokenHash, &t.AdminUsername, &t.Role, &t.ExpiresAt, &t.Revoked, &t.LastUsedAt, &t.CreatedAt)
	return t, err
}

func TouchAdminToken(ctx context.Context, pool *pgxpool.Pool, tokenID string) error {
	_, err := pool.Exec(ctx, `UPDATE admin_tokens SET last_used_at = now() WHERE id = $1`, tokenID)
	return err
}

func RevokeAdminToken(ctx context.Context, pool *pgxpool.Pool, tokenID string) error {
	_, err := pool.Exec(ctx, `UPDATE admin_tokens SET revoked = true WHERE id = $1`, tokenID)
	return err
}
