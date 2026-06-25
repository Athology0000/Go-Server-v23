package middleware

import "testing"

// effectiveAdminRole caps a bearer token's role by the account's authoritative bound admin_role.
// The account is the source of truth: a token whose account was demoted (lower bound role) or
// de-adminned (account exists, bound == "") authorizes only at the bound role, so a stale high-role
// token can't out-rank the account's current role — and a demotion takes effect immediately, even on
// an already-issued session token. A token whose username has no account row (accountExists == false)
// stands on its own role (service/API tokens, and pre-backfill safety). The cap only ever LOWERS.
func TestEffectiveAdminRole(t *testing.T) {
	cases := []struct {
		name          string
		tokenRole     string
		boundRole     string
		accountExists bool
		want          string
	}{
		{"no account row: token stands alone", "super_admin", "", false, "super_admin"},
		{"demoted account caps a stale higher token", "super_admin", "viewer", true, "viewer"},
		{"de-adminned account (null role) caps to nothing", "super_admin", "", true, ""},
		{"cap never raises a lower token", "viewer", "super_admin", true, "viewer"},
		{"equal roles unchanged", "support", "support", true, "support"},
		{"support token under super account stays support", "support", "super_admin", true, "support"},
	}
	for _, c := range cases {
		t.Run(c.name, func(t *testing.T) {
			if got := effectiveAdminRole(c.tokenRole, c.boundRole, c.accountExists); got != c.want {
				t.Fatalf("effectiveAdminRole(%q,%q,%v) = %q; want %q", c.tokenRole, c.boundRole, c.accountExists, got, c.want)
			}
		})
	}
}
