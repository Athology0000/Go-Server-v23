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
	DurationDays int        `json:"duration_days"`
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
		`SELECT id, key_hash, plan_tier, duration_days, status, redeemed_by, redeemed_at, enrollment_ip, created_by, notes, created_at
		 FROM license_keys WHERE key_hash = $1`, keyHash)
	k := &LicenseKey{}
	err := row.Scan(&k.ID, &k.KeyHash, &k.PlanTier, &k.DurationDays, &k.Status, &k.RedeemedBy, &k.RedeemedAt,
		&k.EnrollmentIP, &k.CreatedBy, &k.Notes, &k.CreatedAt)
	return k, err
}

func RedeemLicenseKey(ctx context.Context, pool *pgxpool.Pool, keyHash, accountID, sourceIP string) error {
	_, err := pool.Exec(ctx,
		`UPDATE license_keys SET status = 'redeemed', redeemed_by = $1, redeemed_at = now(), enrollment_ip = $2
		 WHERE key_hash = $3 AND status = 'available'`, accountID, sourceIP, keyHash)
	return err
}

func CreateLicenseKey(ctx context.Context, pool *pgxpool.Pool, keyHash, planTier string, durationDays int, createdBy string, notes *string) (*LicenseKey, error) {
	row := pool.QueryRow(ctx,
		`INSERT INTO license_keys (key_hash, plan_tier, duration_days, created_by, notes)
		 VALUES ($1, $2, $3, $4, $5)
		 RETURNING id, key_hash, plan_tier, duration_days, status, redeemed_by, redeemed_at, enrollment_ip, created_by, notes, created_at`,
		keyHash, planTier, durationDays, createdBy, notes)
	k := &LicenseKey{}
	err := row.Scan(&k.ID, &k.KeyHash, &k.PlanTier, &k.DurationDays, &k.Status, &k.RedeemedBy, &k.RedeemedAt,
		&k.EnrollmentIP, &k.CreatedBy, &k.Notes, &k.CreatedAt)
	return k, err
}

func RevokeLicenseKey(ctx context.Context, pool *pgxpool.Pool, keyID string) error {
	_, err := pool.Exec(ctx, `UPDATE license_keys SET status = 'revoked' WHERE id = $1`, keyID)
	return err
}

func GetRedeemedKeyHashPrefix(ctx context.Context, pool *pgxpool.Pool, accountID string) string {
	var hash string
	err := pool.QueryRow(ctx,
		`SELECT key_hash FROM license_keys WHERE redeemed_by = $1 ORDER BY redeemed_at DESC LIMIT 1`,
		accountID).Scan(&hash)
	if err != nil || hash == "" {
		return ""
	}
	if len(hash) > 16 {
		return hash[:16]
	}
	return hash
}

func ListLicenseKeys(ctx context.Context, pool *pgxpool.Pool, limit, offset int) ([]*LicenseKey, error) {
	rows, err := pool.Query(ctx,
		`SELECT id, key_hash, plan_tier, duration_days, status, redeemed_by, redeemed_at, enrollment_ip, created_by, notes, created_at
		 FROM license_keys ORDER BY created_at DESC LIMIT $1 OFFSET $2`, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var keys []*LicenseKey
	for rows.Next() {
		k := &LicenseKey{}
		if err := rows.Scan(&k.ID, &k.KeyHash, &k.PlanTier, &k.DurationDays, &k.Status, &k.RedeemedBy, &k.RedeemedAt,
			&k.EnrollmentIP, &k.CreatedBy, &k.Notes, &k.CreatedAt); err != nil {
			return nil, err
		}
		keys = append(keys, k)
	}
	return keys, nil
}
