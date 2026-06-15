// Package cache holds the server's ephemeral, TTL'd state — rate-limit counters and short-lived
// auth challenges. It is backed by Postgres (not Redis), so the server needs only one datastore.
// Expired rows are reaped by a background sweeper (StartSweeper).
package cache

import (
	"context"
	"log"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

// StartSweeper launches a background goroutine that periodically deletes expired rate_limit and
// auth_challenge rows so those tables don't grow unbounded from keys that never recur. It returns
// immediately; the goroutine stops when ctx is cancelled. (Reads already ignore expired rows via
// `expires_at > now()`, so the sweep is purely housekeeping.)
func StartSweeper(ctx context.Context, pool *pgxpool.Pool, every time.Duration) {
	if every <= 0 {
		every = 5 * time.Minute
	}
	go func() {
		ticker := time.NewTicker(every)
		defer ticker.Stop()
		for {
			select {
			case <-ctx.Done():
				return
			case <-ticker.C:
				sweep(ctx, pool)
			}
		}
	}()
}

func sweep(ctx context.Context, pool *pgxpool.Pool) {
	c, cancel := context.WithTimeout(ctx, 30*time.Second)
	defer cancel()
	for _, q := range []string{
		`DELETE FROM rate_limits WHERE expires_at < now()`,
		`DELETE FROM auth_challenges WHERE expires_at < now()`,
	} {
		if _, err := pool.Exec(c, q); err != nil {
			log.Printf("[cache.sweeper] %v", err)
		}
	}
}
