package cache

import (
	"strings"
	"testing"
)

// TestSweepStatementsCoversRetentionTargets pins the pure sweep decision: which tables get
// reaped, that the audit retention is a bound parameter (not hardcoded), and — critically —
// that expired game sessions and their session_activity children are deleted in ONE statement.
// The single-statement CTE means a single now() snapshot drives both deletes, so a session that
// ages out mid-sweep can't be selected by the parent delete while its children survive (the FK
// session_activity.session_id -> sessions.id is NO ACTION, checked at end of statement).
func TestSweepStatementsCoversRetentionTargets(t *testing.T) {
	stmts := sweepStatements(90)

	want := []string{"rate_limits", "auth_challenges", "session_activity", "sessions", "panel_sessions", "audit_log"}
	for _, tbl := range want {
		found := false
		for _, s := range stmts {
			if strings.Contains(s.sql, tbl) {
				found = true
				break
			}
		}
		if !found {
			t.Errorf("no sweep statement targets %q", tbl)
		}
	}

	// FK safety: the statement deleting the sessions parent must also delete the
	// session_activity children in the SAME statement, never as a separate FK-racy delete.
	var sessionsStmt string
	for _, s := range stmts {
		if strings.Contains(s.sql, "DELETE FROM sessions") {
			sessionsStmt = s.sql
		}
	}
	if sessionsStmt == "" {
		t.Fatal("no statement deletes expired sessions")
	}
	if !strings.Contains(sessionsStmt, "session_activity") {
		t.Error("the sessions delete must also remove session_activity children in the same statement (FK-safe single snapshot)")
	}

	// Audit retention must be a bound arg equal to the configured value.
	auditFound := false
	for _, s := range stmts {
		if strings.Contains(s.sql, "audit_log") {
			auditFound = true
			if len(s.args) != 1 {
				t.Fatalf("audit_log sweep must bind retention as an arg, got args=%v", s.args)
			}
			if s.args[0] != 90 {
				t.Errorf("audit retention arg = %v, want 90", s.args[0])
			}
		}
	}
	if !auditFound {
		t.Fatal("no statement targets audit_log")
	}
}
