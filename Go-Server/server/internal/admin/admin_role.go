package admin

// resolveAdminLoginRole maps an account's bound admin_role to the session role granted at login.
// The role is used verbatim; an empty role (the account has no admin_role — not an admin) denies
// login. Admin login no longer derives the role from the set of issued admin_tokens (the old
// MAX-over-valid-tokens rule), so issuing a lower-role token demotes and a stale high-role token
// can never re-grant a higher session at login.
func resolveAdminLoginRole(accountAdminRole string) (role string, allowed bool) {
	if accountAdminRole == "" {
		return "", false
	}
	return accountAdminRole, true
}

// validAdminRole reports whether role is a grantable storage role. Used to reject an unknown role at
// the handler (400) rather than letting it hit the accounts/admin_tokens CHECK constraint (500).
// Case-sensitive: the frontend aliases ("admin"/"superadmin") are display-only, not storage roles.
func validAdminRole(role string) bool {
	switch role {
	case "super_admin", "support", "viewer":
		return true
	default:
		return false
	}
}
