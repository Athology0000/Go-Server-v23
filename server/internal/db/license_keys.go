package db

import (
	"context"
	"time"
	"github.com/jackc/pgx/v5/pgxpool"
)

type LicenseKey struct {
	ID           string     `json:"id"`
	KeyHash      string     `json:"-"`
	PlanTier     string     `json:"plan_tier"`
	Status       string     `json:"status"`
	RedeemedBy   *string    `json:"redeemed_by"`
	RedeemedAt   *time.Time `json:"redeemed_at"`
	EnrollmentIP *string    `json:"enrollment_ip"`
	CreatedBy    string     `json:"created_by"`
	Notes        *string    `json:"notes"`
	CreatedAt    time.Time  `json:"created_at"`
}

func GetLicenseKeyByHash(ctx context.Context, pool *pgxpool.Pool, keyHash string) (*LicenseKey, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, key_hash, plan_tier, status, redeemed_by, redeemed_at, enrollment_ip, created_by, notes, created_at
		 FROM license_keys WHERE key_hash = $1`, keyHash)
	k := &LicenseKey{}
	err := row.Scan(&k.ID, &k.KeyHash, &k.PlanTier, &k.Status, &k.RedeemedBy, &k.RedeemedAt,
		&k.EnrollmentIP, &k.CreatedBy, &k.Notes, &k.CreatedAt)
	return k, err
}

func RedeemLicenseKey(ctx context.Context, pool *pgxpool.Pool, keyHash, accountID, sourceIP string) error {
	_, err := pool.Exec(ctx,
		`UPDATE license_keys SET status = 'redeemed', redeemed_by = $1, redeemed_at = now(), enrollment_ip = $2
		 WHERE key_hash = $3 AND status = 'available'`, accountID, sourceIP, keyHash)
	return err
}

func CreateLicenseKey(ctx context.Context, pool *pgxpool.Pool, keyHash, planTier, createdBy string, notes *string) (*LicenseKey, error) {
	row := pool.QueryRow(ctx,
		`INSERT INTO license_keys (key_hash, plan_tier, created_by, notes)
		 VALUES ($1, $2, $3, $4)
		 RETURNING id, key_hash, plan_tier, status, redeemed_by, redeemed_at, enrollment_ip, created_by, notes, created_at`,
		keyHash, planTier, createdBy, notes)
	k := &LicenseKey{}
	err := row.Scan(&k.ID, &k.KeyHash, &k.PlanTier, &k.Status, &k.RedeemedBy, &k.RedeemedAt,
		&k.EnrollmentIP, &k.CreatedBy, &k.Notes, &k.CreatedAt)
	return k, err
}

func RevokeLicenseKey(ctx context.Context, pool *pgxpool.Pool, keyID string) error {
	_, err := pool.Exec(ctx, `UPDATE license_keys SET status = 'revoked' WHERE id = $1`, keyID)
	return err
}
