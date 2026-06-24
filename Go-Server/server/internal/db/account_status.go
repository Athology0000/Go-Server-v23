package db

// AccountStatusActive reports whether an account status permits authenticated
// access. The accounts.status domain is {active, suspended, banned}
// (migrations/001_initial.sql); the rule is fail-closed, so only the exact
// string "active" is allowed and every other (including unknown/future) value
// denies access.
//
// This is the single source of truth for ban/suspension enforcement. The game
// session middleware, /auth/heartbeat, /auth/finish, and the panel auth gate
// all funnel through it so the rule cannot drift between call sites.
func AccountStatusActive(status string) bool {
	return status == "active"
}
