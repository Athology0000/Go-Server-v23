package admin

import "testing"

// resolveAdminLoginRole is the authoritative admin-login policy: a session's role is the account's
// bound admin_role, used verbatim, and an absent role (not an admin) denies login. This pins that
// the role is NOT derived from the set of issued tokens — so a stale high-role token can't re-grant.
func TestResolveAdminLoginRole(t *testing.T) {
	cases := []struct {
		name        string
		bound       string
		wantRole    string
		wantAllowed bool
	}{
		{"no bound role denies login", "", "", false},
		{"super_admin granted verbatim", "super_admin", "super_admin", true},
		{"support granted verbatim", "support", "support", true},
		{"viewer granted verbatim", "viewer", "viewer", true},
	}
	for _, c := range cases {
		t.Run(c.name, func(t *testing.T) {
			role, allowed := resolveAdminLoginRole(c.bound)
			if role != c.wantRole || allowed != c.wantAllowed {
				t.Fatalf("resolveAdminLoginRole(%q) = (%q,%v); want (%q,%v)", c.bound, role, allowed, c.wantRole, c.wantAllowed)
			}
		})
	}
}

// validAdminRole gates the role string a super_admin may grant, so an unknown role is a 400 at the
// handler rather than a DB CHECK-constraint 500. Case-sensitive; the frontend aliases (admin /
// superadmin) are NOT storage roles.
func TestValidAdminRole(t *testing.T) {
	for _, r := range []string{"super_admin", "support", "viewer"} {
		if !validAdminRole(r) {
			t.Errorf("validAdminRole(%q) = false, want true", r)
		}
	}
	for _, r := range []string{"", "admin", "superadmin", "root", "Super_Admin", "SUPPORT"} {
		if validAdminRole(r) {
			t.Errorf("validAdminRole(%q) = true, want false", r)
		}
	}
}
