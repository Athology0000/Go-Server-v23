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

// StartSweeper launches a background goroutine that periodically reaps expired/aged rows so those
// tables don't grow unbounded: rate-limit counters and auth challenges (by expires_at), expired
// game + panel sessions, and audit-log rows older than auditRetentionDays (email + IP are a
// retention liability). It returns immediately; the goroutine stops when ctx is cancelled. Reads
// already ignore expired sessions via `expires_at > now()`, so the sweep is purely housekeeping.
func StartSweeper(ctx context.Context, pool *pgxpool.Pool, every time.Duration, auditRetentionDays int) {
	if every <= 0 {
		every = 5 * time.Minute
	}
	if auditRetentionDays <= 0 {
		auditRetentionDays = 90
	}
	go func() {
		ticker := time.NewTicker(every)
		defer ticker.Stop()
		for {
			select {
			case <-ctx.Done():
				return
			case <-ticker.C:
				sweep(ctx, pool, auditRetentionDays)
			}
		}
	}()
}

// sweepStmt is a DELETE plus its bound args — pure data, no DB and no clock.
type sweepStmt struct {
	sql  string
	args []any
}

// sweepStatements is the pure sweep decision: given an audit retention in days, what gets reaped?
// Expired game sessions and their session_activity children are removed in ONE statement so a
// single now() snapshot drives both — the FK session_activity.session_id -> sessions.id is NO
// ACTION (checked at end of statement), so deleting children then the parent in the same statement
// can never FK-violate, regardless of CTE execution order. (Two separate deletes, each with their
// own now(), could orphan a session that ages out between them.)
func sweepStatements(auditRetentionDays int) []sweepStmt {
	return []sweepStmt{
		{`DELETE FROM rate_limits WHERE expires_at < now()`, nil},
		{`DELETE FROM auth_challenges WHERE expires_at < now()`, nil},
		{`WITH expired AS (SELECT id FROM sessions WHERE expires_at < now()),
		      _children AS (DELETE FROM session_activity WHERE session_id IN (SELECT id FROM expired))
		 DELETE FROM sessions WHERE id IN (SELECT id FROM expired)`, nil},
		{`DELETE FROM panel_sessions WHERE expires_at < now()`, nil},
		{`DELETE FROM audit_log WHERE created_at < now() - make_interval(days => $1)`, []any{auditRetentionDays}},
	}
}

func sweep(ctx context.Context, pool *pgxpool.Pool, auditRetentionDays int) {
	c, cancel := context.WithTimeout(ctx, 30*time.Second)
	defer cancel()
	for _, s := range sweepStatements(auditRetentionDays) {
		if _, err := pool.Exec(c, s.sql, s.args...); err != nil {
			log.Printf("[cache.sweeper] %v", err)
		}
	}
}
