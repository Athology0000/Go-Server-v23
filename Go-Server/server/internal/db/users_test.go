package db

import (
	"strings"
	"testing"
)

// The aggregated-user query is the contract the admin panel depends on: a fixed
// three-table join, a created_at DESC ordering, and stable $1/$2/$3 parameter
// positions for q/limit/offset. There's no DB-free harness for the join itself
// (the other DB-backed tests gate on a live Postgres URL and skip when unset),
// so this asserts the query strings rather than inventing a Postgres dependency.
func TestUserSelectSQLShape(t *testing.T) {
	for _, want := range []string{
		"FROM accounts a",
		"LEFT JOIN licenses l ON l.account_id = a.id AND l.status = 'active'",
		"LEFT JOIN devices d ON d.account_id = a.id",
		"(d.hwid_hash IS NOT NULL) AS hwid_bound",
	} {
		if !strings.Contains(userSelectSQL, want) {
			t.Errorf("userSelectSQL missing %q", want)
		}
	}
}

func TestUserListSQLShape(t *testing.T) {
	if !strings.HasPrefix(userListSQL, userSelectSQL) {
		t.Fatal("userListSQL must build on the shared userSelectSQL projection")
	}
	for _, want := range []string{
		"a.username ILIKE '%'||$1||'%'",
		"COALESCE(a.email,'') ILIKE '%'||$1||'%'",
		"ORDER BY a.created_at DESC",
		"LIMIT $2 OFFSET $3",
	} {
		if !strings.Contains(userListSQL, want) {
			t.Errorf("userListSQL missing %q", want)
		}
	}
	// Empty q must match all rows, not just the empty-string account.
	if !strings.Contains(userListSQL, "$1 = ''") {
		t.Error("userListSQL must treat empty q as match-all")
	}
}

func TestUserGetSQLShape(t *testing.T) {
	if !strings.HasPrefix(userGetSQL, userSelectSQL) {
		t.Fatal("userGetSQL must build on the shared userSelectSQL projection")
	}
	if !strings.Contains(userGetSQL, "WHERE a.id = $1") {
		t.Error("userGetSQL must filter by account id on $1")
	}
}
