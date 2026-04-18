package db

import (
	"context"
	"time"
	"github.com/jackc/pgx/v5/pgxpool"
)

type License struct {
	ID             string     `json:"id"`
	AccountID      string     `json:"account_id"`
	PlanTier       string     `json:"plan_tier"`
	Status         string     `json:"status"`
	StartsAt       time.Time  `json:"starts_at"`
	ExpiresAt      *time.Time `json:"expires_at"`
	GraceExpiresAt *time.Time `json:"grace_expires_at"`
	MaxDevices     *int       `json:"max_devices"`
	Notes          *string    `json:"notes"`
	CreatedAt      time.Time  `json:"created_at"`
	UpdatedAt      time.Time  `json:"updated_at"`
}

func GetLicenseByAccountID(ctx context.Context, pool *pgxpool.Pool, accountID string) (*License, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, account_id, plan_tier, status, starts_at, expires_at, grace_expires_at, max_devices, notes, created_at, updated_at
		 FROM licenses WHERE account_id = $1`, accountID)
	l := &License{}
	err := row.Scan(&l.ID, &l.AccountID, &l.PlanTier, &l.Status, &l.StartsAt, &l.ExpiresAt,
		&l.GraceExpiresAt, &l.MaxDevices, &l.Notes, &l.CreatedAt, &l.UpdatedAt)
	return l, err
}

func CreateLicense(ctx context.Context, pool *pgxpool.Pool, accountID, planTier string, startsAt time.Time, expiresAt *time.Time) (*License, error) {
	row := pool.QueryRow(ctx,
		`INSERT INTO licenses (account_id, plan_tier, starts_at, expires_at)
		 VALUES ($1, $2, $3, $4)
		 RETURNING id, account_id, plan_tier, status, starts_at, expires_at, grace_expires_at, max_devices, notes, created_at, updated_at`,
		accountID, planTier, startsAt, expiresAt)
	l := &License{}
	err := row.Scan(&l.ID, &l.AccountID, &l.PlanTier, &l.Status, &l.StartsAt, &l.ExpiresAt,
		&l.GraceExpiresAt, &l.MaxDevices, &l.Notes, &l.CreatedAt, &l.UpdatedAt)
	return l, err
}

func UpdateLicenseStatus(ctx context.Context, pool *pgxpool.Pool, licenseID, status string) error {
	_, err := pool.Exec(ctx,
		`UPDATE licenses SET status = $1, updated_at = now() WHERE id = $2`, status, licenseID)
	return err
}
