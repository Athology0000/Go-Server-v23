package db

import (
	"context"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

type PanelSession struct {
	ID        string    `json:"id"`
	TokenHash string    `json:"-"`
	AccountID string    `json:"account_id"`
	ExpiresAt time.Time `json:"expires_at"`
	Revoked   bool      `json:"revoked"`
	CreatedAt time.Time `json:"created_at"`
}

func CreatePanelSession(ctx context.Context, pool *pgxpool.Pool, tokenHash, accountID string, expiresAt time.Time) (*PanelSession, error) {
	row := pool.QueryRow(ctx,
		`INSERT INTO panel_sessions (token_hash, account_id, expires_at)
		 VALUES ($1, $2, $3)
		 RETURNING id, token_hash, account_id, expires_at, revoked, created_at`,
		tokenHash, accountID, expiresAt)
	s := &PanelSession{}
	err := row.Scan(&s.ID, &s.TokenHash, &s.AccountID, &s.ExpiresAt, &s.Revoked, &s.CreatedAt)
	return s, err
}

func GetPanelSessionByTokenHash(ctx context.Context, pool *pgxpool.Pool, tokenHash string) (*PanelSession, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, token_hash, account_id, expires_at, revoked, created_at
		 FROM panel_sessions
		 WHERE token_hash = $1 AND revoked = false AND expires_at > now()`,
		tokenHash)
	s := &PanelSession{}
	err := row.Scan(&s.ID, &s.TokenHash, &s.AccountID, &s.ExpiresAt, &s.Revoked, &s.CreatedAt)
	return s, err
}

func GetAccountByEmail(ctx context.Context, pool *pgxpool.Pool, email string) (*Account, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, username, password_hash, email, status, created_at, updated_at
		 FROM accounts WHERE LOWER(email) = LOWER($1)`, email)
	a := &Account{}
	err := row.Scan(&a.ID, &a.Username, &a.PasswordHash, &a.Email, &a.Status, &a.CreatedAt, &a.UpdatedAt)
	return a, err
}
