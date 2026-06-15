package cache

import (
	"context"
	"errors"
	"time"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

type Challenge struct {
	DeviceID  string `json:"device_id"`
	Challenge string `json:"challenge"`
	SourceIP  string `json:"source_ip"`
}

const challengeTTL = 30 * time.Second

// ErrChallengeNotFound is returned when no live challenge exists for a device (absent or expired).
var ErrChallengeNotFound = errors.New("challenge not found")

// StoreChallenge upserts the (single) live challenge for a device, valid for challengeTTL.
func StoreChallenge(ctx context.Context, pool *pgxpool.Pool, c *Challenge) error {
	_, err := pool.Exec(ctx, `
		INSERT INTO auth_challenges (device_id, challenge, source_ip, expires_at)
		VALUES ($1, $2, $3, now() + ($4::float8 * interval '1 second'))
		ON CONFLICT (device_id) DO UPDATE SET
			challenge  = EXCLUDED.challenge,
			source_ip  = EXCLUDED.source_ip,
			expires_at = EXCLUDED.expires_at
	`, c.DeviceID, c.Challenge, c.SourceIP, challengeTTL.Seconds())
	return err
}

// GetChallenge returns the live challenge for a device without consuming it. Returns
// ErrChallengeNotFound if absent or expired.
func GetChallenge(ctx context.Context, pool *pgxpool.Pool, deviceID string) (*Challenge, error) {
	c := &Challenge{}
	err := pool.QueryRow(ctx, `
		SELECT device_id, challenge, source_ip FROM auth_challenges
		WHERE device_id = $1 AND expires_at > now()
	`, deviceID).Scan(&c.DeviceID, &c.Challenge, &c.SourceIP)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, ErrChallengeNotFound
	}
	if err != nil {
		return nil, err
	}
	return c, nil
}

// DeleteChallenge removes a device's challenge (no-op if absent).
func DeleteChallenge(ctx context.Context, pool *pgxpool.Pool, deviceID string) error {
	_, err := pool.Exec(ctx, `DELETE FROM auth_challenges WHERE device_id = $1`, deviceID)
	return err
}

// ConsumeChallenge atomically fetches and deletes a live challenge for deviceID. The
// DELETE ... RETURNING hands the row to exactly one caller, so concurrent /auth/finish calls
// cannot double-consume (the equivalent of Redis GETDEL). Returns ErrChallengeNotFound if the
// challenge is absent or expired.
func ConsumeChallenge(ctx context.Context, pool *pgxpool.Pool, deviceID string) (*Challenge, error) {
	c := &Challenge{}
	err := pool.QueryRow(ctx, `
		DELETE FROM auth_challenges
		WHERE device_id = $1 AND expires_at > now()
		RETURNING device_id, challenge, source_ip
	`, deviceID).Scan(&c.DeviceID, &c.Challenge, &c.SourceIP)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, ErrChallengeNotFound
	}
	if err != nil {
		return nil, err
	}
	return c, nil
}
