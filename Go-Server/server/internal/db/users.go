package db

import (
	"context"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

// UserRow is the aggregated, frontend-facing view of an account joined with its
// active license and bound device. The JSON field names/shape are the contract
// the admin panel consumes — do not rename them.
type UserRow struct {
	ID                string     `json:"id"`
	Username          string     `json:"username"`
	Email             *string    `json:"email"`
	Plan              *string    `json:"plan"`
	PlanExpiry        *time.Time `json:"planExpiry"`
	HwidBound         bool       `json:"hwidBound"`
	Hwid              *string    `json:"hwid"`
	Banned            bool       `json:"banned"`
	BannedReason      *string    `json:"bannedReason"`
	CreatedAt         time.Time  `json:"createdAt"`
	LastSeen          *time.Time `json:"lastSeen"`
	MinecraftUsername *string    `json:"minecraftUsername"`
}

// userSelectSQL aggregates schema knowledge for three tables (accounts + active
// licenses + devices) into the single projection the admin users endpoints need.
const userSelectSQL = `
SELECT a.id, a.username, a.email, a.status, a.created_at,
       l.plan_tier, l.expires_at,
       (d.hwid_hash IS NOT NULL) AS hwid_bound,
       d.minecraft_username, d.last_login_at
FROM accounts a
LEFT JOIN licenses l ON l.account_id = a.id AND l.status = 'active'
LEFT JOIN devices d ON d.account_id = a.id`

// userListSQL filters the aggregated projection by an optional free-text query
// (empty q matches everything) over username/email and pages by created_at DESC.
const userListSQL = userSelectSQL + `
WHERE ($1 = '' OR a.username ILIKE '%'||$1||'%' OR COALESCE(a.email,'') ILIKE '%'||$1||'%')
ORDER BY a.created_at DESC LIMIT $2 OFFSET $3`

// userGetSQL selects a single aggregated user row by account id.
const userGetSQL = userSelectSQL + " WHERE a.id = $1"

func scanUserRow(row interface{ Scan(...any) error }) (*UserRow, error) {
	u := &UserRow{}
	var status string
	var hwidBound bool
	err := row.Scan(
		&u.ID, &u.Username, &u.Email, &status, &u.CreatedAt,
		&u.Plan, &u.PlanExpiry,
		&hwidBound, &u.MinecraftUsername, &u.LastSeen,
	)
	if err != nil {
		return nil, err
	}
	u.Banned = status == "banned"
	u.HwidBound = hwidBound
	return u, nil
}

// ListUsers returns the aggregated user view, filtered by the free-text query q
// (empty string matches all) and paged by limit/offset, ordered created_at DESC.
// An empty result is returned as a non-nil empty slice.
func ListUsers(ctx context.Context, pool *pgxpool.Pool, q string, limit, offset int) ([]*UserRow, error) {
	rows, err := pool.Query(ctx, userListSQL, q, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	users := []*UserRow{}
	for rows.Next() {
		u, err := scanUserRow(rows)
		if err != nil {
			return nil, err
		}
		users = append(users, u)
	}
	return users, rows.Err()
}

// GetUser returns the aggregated user view for a single account id, or an error
// (pgx.ErrNoRows) when no such account exists.
func GetUser(ctx context.Context, pool *pgxpool.Pool, id string) (*UserRow, error) {
	row := pool.QueryRow(ctx, userGetSQL, id)
	return scanUserRow(row)
}
