package db

import (
	"testing"
	"time"
)

func TestSessionLive(t *testing.T) {
	now := time.Date(2026, 6, 15, 12, 0, 0, 0, time.UTC)
	window := 15 * time.Minute

	cases := []struct {
		name      string
		revoked   bool
		expiresAt time.Time
		lastBeat  time.Time
		want      bool
	}{
		{"fresh session is live", false, now.Add(time.Hour), now.Add(-time.Minute), true},
		{"revoked is dead", true, now.Add(time.Hour), now, false},
		{"past hard cap is dead", false, now.Add(-time.Second), now, false},
		{"stale heartbeat is dead", false, now.Add(time.Hour), now.Add(-16 * time.Minute), false},
		{"exactly at window boundary is live", false, now.Add(time.Hour), now.Add(-window), true},
		{"one second past window is dead", false, now.Add(time.Hour), now.Add(-window - time.Second), false},
	}
	for _, c := range cases {
		t.Run(c.name, func(t *testing.T) {
			s := &Session{Revoked: c.revoked, ExpiresAt: c.expiresAt, LastHeartbeatAt: c.lastBeat}
			if got := SessionLive(s, now, window); got != c.want {
				t.Fatalf("SessionLive=%v want %v", got, c.want)
			}
		})
	}
}
