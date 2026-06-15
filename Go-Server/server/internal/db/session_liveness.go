package db

import "time"

// SessionLive reports whether a session may currently authorize work. A session
// is live only while heartbeats keep arriving: it must not be revoked, must be
// within its absolute expires_at cap, and its last heartbeat must be no older
// than window. expires_at is a fixed cap set at creation and is never rolled
// forward — heartbeats refresh last_heartbeat_at, not the lifetime.
func SessionLive(s *Session, now time.Time, window time.Duration) bool {
	if s.Revoked || !now.Before(s.ExpiresAt) {
		return false
	}
	return now.Sub(s.LastHeartbeatAt) <= window
}
