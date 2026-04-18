package db

import (
	"context"
	"time"
	"github.com/jackc/pgx/v5/pgxpool"
)

type License struct {
	ID             string
	AccountID      string
	PlanTier       string
	Status         string
	StartsAt       time.Time
	ExpiresAt      *time.Time
	GraceExpiresAt *time.Time
	MaxDevices     *int
	Notes          *string
	CreatedAt      time.Time
	UpdatedAt      time.Time
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
