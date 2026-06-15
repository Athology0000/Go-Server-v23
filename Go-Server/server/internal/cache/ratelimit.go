package cache

import (
	"context"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

// CheckRateLimit applies a fixed-window limit keyed by `key`. A single UPSERT atomically either
// starts a new window (count=1) or increments the current one, resetting when the prior window
// has expired — the same fixed-window semantics the previous Redis INCR+EXPIRE provided. It
// reports whether the request is within `limit`, the remaining allowance, and the window reset
// time.
func CheckRateLimit(ctx context.Context, pool *pgxpool.Pool, key string, limit int, window time.Duration) (bool, int, time.Time, error) {
	var count int
	var expiresAt time.Time
	err := pool.QueryRow(ctx, `
		INSERT INTO rate_limits (key, count, expires_at)
		VALUES ($1, 1, now() + ($2::float8 * interval '1 second'))
		ON CONFLICT (key) DO UPDATE SET
			count = CASE WHEN rate_limits.expires_at < now() THEN 1 ELSE rate_limits.count + 1 END,
			expires_at = CASE WHEN rate_limits.expires_at < now()
				THEN now() + ($2::float8 * interval '1 second')
				ELSE rate_limits.expires_at END
		RETURNING count, expires_at
	`, key, window.Seconds()).Scan(&count, &expiresAt)
	if err != nil {
		return false, 0, time.Time{}, err
	}
	remaining := limit - count
	if remaining < 0 {
		remaining = 0
	}
	return count <= limit, remaining, expiresAt, nil
}
