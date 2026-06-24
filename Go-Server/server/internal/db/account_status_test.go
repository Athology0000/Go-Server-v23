package db

import "testing"

// AccountStatusActive is the single source of truth for "may this account's
// session authorize work?". The accounts.status domain is {active, suspended,
// banned} (migrations/001_initial.sql), and the rule is fail-closed: only the
// exact string "active" is permitted, so banned/suspended — and any future or
// malformed status — are denied. Every auth gate (game session middleware,
// /auth/heartbeat, /auth/finish, panel auth) funnels through this predicate so
// the ban rule lives in exactly one place.
func TestAccountStatusActive(t *testing.T) {
	cases := []struct {
		status string
		want   bool
	}{
		{"active", true},
		{"banned", false},
		{"suspended", false},
		{"", false},        // empty (status not JOINed) fails closed
		{"ACTIVE", false},  // exact match only; not case-folded
		{" active", false}, // no trimming; exact match only
		{"pending", false}, // unknown/future status fails closed
		{"deactivated", false},
	}
	for _, c := range cases {
		t.Run(c.status, func(t *testing.T) {
			if got := AccountStatusActive(c.status); got != c.want {
				t.Fatalf("AccountStatusActive(%q) = %v, want %v", c.status, got, c.want)
			}
		})
	}
}
