package cache

import (
	"testing"
	"time"
)

// TestSlidingWindowNoBoundaryBurst proves end-to-end that the 2x boundary burst is gone.
// It seeds a "just rolled" state — the PREVIOUS sub-window was filled to `limit` and the
// current sub-window just began (window_start = now()) — then asserts the very first request
// in the new sub-window is DENIED, because the full previous bucket is weighted ~1.0 into the
// trailing window. A fixed-window limiter would have allowed `limit` fresh requests here.
// Skips when TEST_DB_URL is unset (matches the repo's DB-skip integration idiom).
func TestSlidingWindowNoBoundaryBurst(t *testing.T) {
	ctx, pool := testPool(t)

	// Self-contained schema (mirrors migration 011 + 013) so the test runs against a fresh DB.
	if _, err := pool.Exec(ctx, `
		CREATE TABLE IF NOT EXISTS rate_limits (
			key          TEXT PRIMARY KEY,
			count        INTEGER NOT NULL,
			expires_at   TIMESTAMPTZ NOT NULL,
			window_start TIMESTAMPTZ NOT NULL DEFAULT now(),
			curr_count   INTEGER NOT NULL DEFAULT 0,
			prev_count   INTEGER NOT NULL DEFAULT 0
		)`); err != nil {
		t.Fatalf("ensure rate_limits: %v", err)
	}

	const key = "test-sliding-boundary"
	const limit = 5
	window := time.Minute
	_, _ = pool.Exec(ctx, `DELETE FROM rate_limits WHERE key = $1`, key)

	// Seed: previous window full (prev_count = limit), current window just started.
	if _, err := pool.Exec(ctx, `
		INSERT INTO rate_limits (key, count, expires_at, window_start, curr_count, prev_count)
		VALUES ($1, $2, now() + interval '2 minutes', now(), 0, $2)
	`, key, limit); err != nil {
		t.Fatalf("seed: %v", err)
	}

	allowed, remaining, _, err := CheckRateLimit(ctx, pool, key, limit, window)
	if err != nil {
		t.Fatalf("CheckRateLimit: %v", err)
	}
	if allowed {
		t.Fatalf("first request after a full previous window must be DENIED (boundary burst), got allowed=true remaining=%d", remaining)
	}
}
