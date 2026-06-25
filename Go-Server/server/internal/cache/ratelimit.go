package cache

import (
	"context"
	"math"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

// slidingWindowAllowed is the pure rate-limit decision. Given the previous and current
// sub-window counts (currCount INCLUDES the request being decided), the limit, the time
// elapsed into the current sub-window, and the sub-window length, it reports whether the
// request is permitted and the weighted estimated usage. The previous bucket is weighted
// by the fraction of it still inside the trailing `window`, so a burst straddling the
// sub-window boundary is counted against both buckets — no 2x boundary burst. The estimate
// is rounded UP (math.Ceil) so a partial bucket is never under-counted.
func slidingWindowAllowed(prevCount, currCount, limit int, elapsed, window time.Duration) (bool, int) {
	weight := 0.0
	if elapsed < window && window > 0 {
		weight = float64(window-elapsed) / float64(window)
	}
	estimated := int(math.Ceil(float64(currCount) + float64(prevCount)*weight))
	return estimated <= limit, estimated
}

// CheckRateLimit applies a sliding-window-counter limit keyed by `key`. A single atomic UPSERT
// rolls the per-key buckets server-side (using the DB's now(), so concurrent requests serialize
// on the row): a request in the current sub-window increments curr_count; a request one window
// later rolls curr->prev and starts curr at 1; a request two windows later resets both buckets.
// The allow/deny decision and the remaining allowance are then computed exclusively by the pure
// slidingWindowAllowed (the UPSERT returns raw counts + elapsed, never re-reading the clock in Go),
// so the X-RateLimit headers can never diverge from the decision. The legacy `count` column is
// kept written (= curr_count) so the NOT NULL constraint holds and the expires_at sweeper is
// unchanged. Reports whether the request is within `limit`, the remaining allowance, and the
// current sub-window's reset time.
func CheckRateLimit(ctx context.Context, pool *pgxpool.Pool, key string, limit int, window time.Duration) (bool, int, time.Time, error) {
	var prevCount, currCount int
	var elapsedSeconds float64
	var windowStart time.Time
	err := pool.QueryRow(ctx, `
		INSERT INTO rate_limits (key, count, curr_count, prev_count, window_start, expires_at)
		VALUES ($1, 1, 1, 0, now(), now() + ($2::float8 * interval '1 second') * 2)
		ON CONFLICT (key) DO UPDATE SET
			prev_count = CASE
				WHEN now() - rate_limits.window_start <  ($2::float8 * interval '1 second') THEN rate_limits.prev_count
				WHEN now() - rate_limits.window_start < 2 * ($2::float8 * interval '1 second') THEN rate_limits.curr_count
				ELSE 0 END,
			curr_count = CASE
				WHEN now() - rate_limits.window_start <  ($2::float8 * interval '1 second') THEN rate_limits.curr_count + 1
				ELSE 1 END,
			count = CASE
				WHEN now() - rate_limits.window_start <  ($2::float8 * interval '1 second') THEN rate_limits.curr_count + 1
				ELSE 1 END,
			window_start = CASE
				WHEN now() - rate_limits.window_start <  ($2::float8 * interval '1 second') THEN rate_limits.window_start
				WHEN now() - rate_limits.window_start < 2 * ($2::float8 * interval '1 second') THEN rate_limits.window_start + ($2::float8 * interval '1 second')
				ELSE now() END,
			expires_at = (CASE
				WHEN now() - rate_limits.window_start <  ($2::float8 * interval '1 second') THEN rate_limits.window_start
				WHEN now() - rate_limits.window_start < 2 * ($2::float8 * interval '1 second') THEN rate_limits.window_start + ($2::float8 * interval '1 second')
				ELSE now() END) + 2 * ($2::float8 * interval '1 second')
		RETURNING prev_count, curr_count, EXTRACT(EPOCH FROM (now() - window_start))::float8, window_start
	`, key, window.Seconds()).Scan(&prevCount, &currCount, &elapsedSeconds, &windowStart)
	if err != nil {
		return false, 0, time.Time{}, err
	}
	elapsed := time.Duration(elapsedSeconds * float64(time.Second))
	allowed, estimated := slidingWindowAllowed(prevCount, currCount, limit, elapsed, window)
	remaining := limit - estimated
	if remaining < 0 {
		remaining = 0
	}
	return allowed, remaining, windowStart.Add(window), nil
}
