package db

import (
	"context"
	"time"
	"github.com/jackc/pgx/v5/pgxpool"
)

type Session struct {
	ID                   string     `json:"id"`
	SessionTokenHash     string     `json:"-"`
	DeviceID             string     `json:"device_id"`
	AccountID            string     `json:"account_id"`
	PlanTier             string     `json:"plan_tier"`
	EnabledModules       []string   `json:"enabled_modules"`
	EnabledFeatures      []string   `json:"enabled_features"`
	EntitlementExpiresAt *time.Time `json:"entitlement_expires_at"`
	ExpiresAt            time.Time  `json:"expires_at"`
	Revoked              bool       `json:"revoked"`
	LastSeenIP           *string    `json:"last_seen_ip"`
	CreatedAt            time.Time  `json:"created_at"`
}

func CreateSession(ctx context.Context, pool *pgxpool.Pool, tokenHash, deviceID, accountID, planTier string,
	modules, features []string, entitlementExpiry *time.Time, expiresAt time.Time, ip string) (*Session, error) {
	row := pool.QueryRow(ctx,
		`INSERT INTO sessions (session_token_hash, device_id, account_id, plan_tier, enabled_modules, enabled_features,
		                       entitlement_expires_at, expires_at, last_seen_ip)
		 VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
		 RETURNING id, session_token_hash, device_id, account_id, plan_tier, enabled_modules, enabled_features,
		           entitlement_expires_at, expires_at, revoked, last_seen_ip, created_at`,
		tokenHash, deviceID, accountID, planTier, modules, features, entitlementExpiry, expiresAt, ip)
	s := &Session{}
	err := row.Scan(&s.ID, &s.SessionTokenHash, &s.DeviceID, &s.AccountID, &s.PlanTier,
		&s.EnabledModules, &s.EnabledFeatures, &s.EntitlementExpiresAt,
		&s.ExpiresAt, &s.Revoked, &s.LastSeenIP, &s.CreatedAt)
	return s, err
}

func GetSessionByTokenHash(ctx context.Context, pool *pgxpool.Pool, tokenHash string) (*Session, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, session_token_hash, device_id, account_id, plan_tier, enabled_modules, enabled_features,
		        entitlement_expires_at, expires_at, revoked, last_seen_ip, created_at
		 FROM sessions WHERE session_token_hash = $1`, tokenHash)
	s := &Session{}
	err := row.Scan(&s.ID, &s.SessionTokenHash, &s.DeviceID, &s.AccountID, &s.PlanTier,
		&s.EnabledModules, &s.EnabledFeatures, &s.EntitlementExpiresAt,
		&s.ExpiresAt, &s.Revoked, &s.LastSeenIP, &s.CreatedAt)
	return s, err
}

func RevokeSession(ctx context.Context, pool *pgxpool.Pool, sessionID string) error {
	_, err := pool.Exec(ctx, `UPDATE sessions SET revoked = true WHERE id = $1`, sessionID)
	return err
}

func ListActiveSessions(ctx context.Context, pool *pgxpool.Pool, limit, offset int) ([]*Session, error) {
	rows, err := pool.Query(ctx,
		`SELECT id, session_token_hash, device_id, account_id, plan_tier, enabled_modules, enabled_features,
		        entitlement_expires_at, expires_at, revoked, last_seen_ip, created_at
		 FROM sessions WHERE revoked = false AND expires_at > now()
		 ORDER BY created_at DESC LIMIT $1 OFFSET $2`, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var sessions []*Session
	for rows.Next() {
		s := &Session{}
		if err := rows.Scan(&s.ID, &s.SessionTokenHash, &s.DeviceID, &s.AccountID, &s.PlanTier,
			&s.EnabledModules, &s.EnabledFeatures, &s.EntitlementExpiresAt,
			&s.ExpiresAt, &s.Revoked, &s.LastSeenIP, &s.CreatedAt); err != nil {
			return nil, err
		}
		sessions = append(sessions, s)
	}
	return sessions, nil
}
