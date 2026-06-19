package audit

import (
	"sort"
	"testing"
)

// hardcodedActivityList is a verbatim copy of the event-type list that used to
// live in db.ListActivityLog's `event_type IN (...)` clause before the taxonomy
// existed. It is intentionally duplicated here as an independent golden value so
// the test fails if the taxonomy-derived list ever drifts from the historical
// behavior. Do NOT regenerate this from the taxonomy — that would defeat the lock.
var hardcodedActivityList = []string{
	"panel.login.success", "panel.login.fail",
	"auth.start.success", "auth.start.fail",
	"auth.finish.success", "auth.finish.fail",
	"auth.device.suspended",
	"panel.key.redeem.success",
}

func TestActivityEventTypesMatchHardcodedList(t *testing.T) {
	got := ActivityEventTypes()

	want := make([]string, len(hardcodedActivityList))
	copy(want, hardcodedActivityList)

	// Compare as sets (order-independent): ListActivityLog uses set membership
	// (event_type = ANY(...)), so the contract is the set, not the order.
	gotSorted := append([]string(nil), got...)
	wantSorted := append([]string(nil), want...)
	sort.Strings(gotSorted)
	sort.Strings(wantSorted)

	if len(gotSorted) != len(wantSorted) {
		t.Fatalf("activity event count drift: taxonomy has %d, hardcoded list has %d\n got=%v\nwant=%v",
			len(gotSorted), len(wantSorted), gotSorted, wantSorted)
	}
	for i := range gotSorted {
		if gotSorted[i] != wantSorted[i] {
			t.Fatalf("activity event-type set drift at %d: got %q, want %q\n got=%v\nwant=%v",
				i, gotSorted[i], wantSorted[i], gotSorted, wantSorted)
		}
	}
}

// TestActivityEventTypesReturnsCopy ensures callers cannot mutate the taxonomy
// through the returned slice.
func TestActivityEventTypesReturnsCopy(t *testing.T) {
	a := ActivityEventTypes()
	if len(a) == 0 {
		t.Fatal("expected non-empty activity event types")
	}
	a[0] = "mutated"
	b := ActivityEventTypes()
	if b[0] == "mutated" {
		t.Fatal("ActivityEventTypes must return a fresh copy; mutation leaked into the taxonomy")
	}
}

func TestIsActivity(t *testing.T) {
	for _, e := range activityEvents {
		if !IsActivity(e) {
			t.Errorf("IsActivity(%q) = false, want true", e)
		}
	}
	for _, e := range []EventType{
		EventAuthHeartbeat,
		EventAdminUserBan,
		EventEnrollRedeemSuccess,
		"nonexistent.event",
	} {
		if IsActivity(e) {
			t.Errorf("IsActivity(%q) = true, want false", e)
		}
	}
}

// TestEventConstantStringValues pins that each activity constant carries the
// exact wire string stored in audit_log.event_type. Pairing each constant with
// its literal here means renaming a constant's value without updating callers /
// stored rows is caught.
func TestEventConstantStringValues(t *testing.T) {
	cases := map[EventType]string{
		EventPanelLoginSuccess:     "panel.login.success",
		EventPanelLoginFail:        "panel.login.fail",
		EventAuthStartSuccess:      "auth.start.success",
		EventAuthStartFail:         "auth.start.fail",
		EventAuthFinishSuccess:     "auth.finish.success",
		EventAuthFinishFail:        "auth.finish.fail",
		EventAuthDeviceSuspended:   "auth.device.suspended",
		EventPanelKeyRedeemSuccess: "panel.key.redeem.success",
	}
	for c, want := range cases {
		if string(c) != want {
			t.Errorf("constant value drift: got %q, want %q", string(c), want)
		}
		if c.String() != want {
			t.Errorf("String() drift: got %q, want %q", c.String(), want)
		}
	}
}
