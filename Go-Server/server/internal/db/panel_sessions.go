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
	// AccountStatus is populated by GetPanelSessionByTokenHash (JOIN accounts) so
	// the panel auth gate can reject banned/suspended accounts without a second
	// query. Empty for sessions loaded by other queries (e.g. CreatePanelSession).
	AccountStatus string `json:"-"`
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
		`SELECT ps.id, ps.token_hash, ps.account_id, ps.expires_at, ps.revoked, ps.created_at, a.status
		 FROM panel_sessions ps
		 JOIN accounts a ON a.id = ps.account_id
		 WHERE ps.token_hash = $1 AND ps.revoked = false AND ps.expires_at > now()`,
		tokenHash)
	s := &PanelSession{}
	err := row.Scan(&s.ID, &s.TokenHash, &s.AccountID, &s.ExpiresAt, &s.Revoked, &s.CreatedAt, &s.AccountStatus)
	return s, err
}

// RevokePanelSessions revokes every live panel (web) session for an account.
// Called alongside RevokeAccountSessions when an account is banned/suspended so
// the 30-day panel bearer token cannot keep authorizing panel routes after the
// ban. PanelAuth also rejects blocked accounts on the next request via the
// JOINed account status, but revoking now is instant and belt-and-suspenders.
func RevokePanelSessions(ctx context.Context, pool *pgxpool.Pool, accountID string) error {
	_, err := pool.Exec(ctx,
		`UPDATE panel_sessions SET revoked = true WHERE account_id = $1 AND revoked = false`, accountID)
	return err
}

func GetAccountByEmail(ctx context.Context, pool *pgxpool.Pool, email string) (*Account, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, username, password_hash, email, status, created_at, updated_at
		 FROM accounts WHERE LOWER(email) = LOWER($1)`, email)
	a := &Account{}
	err := row.Scan(&a.ID, &a.Username, &a.PasswordHash, &a.Email, &a.Status, &a.CreatedAt, &a.UpdatedAt)
	return a, err
}
