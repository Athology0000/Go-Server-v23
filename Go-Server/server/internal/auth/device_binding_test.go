package auth

import (
	"context"
	"testing"

	"github.com/phantom/server/internal/db"
)

// These exercise the DeviceBinding module directly — the one owner of the binding
// lifecycle's legal transitions — without a database. The module classifies a device's
// persisted binding_status into a typed lifecycle state and answers the questions the four
// auth endpoints used to hand-derive from BindingStatus. A fake FullyBinder stands in for
// the persistence seam so the legal/illegal transitions are asserted in isolation.

// fakeFullyBinder records FullyBind calls so a test can assert the module committed (or did
// not commit) the fully-bound transition, without touching Postgres.
type fakeFullyBinder struct {
	calls int
	lastDeviceID, lastUsername, lastIP string
}

func (f *fakeFullyBinder) FullyBind(_ context.Context, deviceID, username, sourceIP string) error {
	f.calls++
	f.lastDeviceID, f.lastUsername, f.lastIP = deviceID, username, sourceIP
	return nil
}

func strptr(s string) *string { return &s }

// neverOffline is the offline-placeholder predicate stubbed off; offline rebind has its own case.
func neverOffline(string) bool { return false }

func deviceWith(status string, mc *string) *db.Device {
	return &db.Device{ID: "dev-1", BindingStatus: status, MinecraftUsername: mc}
}

// State classification and the lifecycle gates the endpoints query.
func TestDeviceBindingStateAndGates(t *testing.T) {
	m := newDeviceBindingWithStore(&fakeFullyBinder{})

	cases := []struct {
		status            string
		wantState         BindingState
		wantBlocked       bool
		wantPermitsStart  bool
		wantEligibleSess  bool
	}{
		{"unbound", StateUnbound, false, false, false},
		{"hwid_pending", StateHWIDPending, false, true, true},
		{"fully_bound", StateFullyBound, false, true, true},
		{"suspended", StateBlocked, true, false, false},
		{"banned", StateBlocked, true, false, false},
		{"weird_unknown", StateUnknown, false, false, false},
	}
	for _, c := range cases {
		d := deviceWith(c.status, nil)
		if got := m.State(d); got != c.wantState {
			t.Errorf("State(%q) = %d, want %d", c.status, got, c.wantState)
		}
		if got := m.IsBlocked(d); got != c.wantBlocked {
			t.Errorf("IsBlocked(%q) = %t, want %t", c.status, got, c.wantBlocked)
		}
		if got := m.PermitsAuthStart(d); got != c.wantPermitsStart {
			t.Errorf("PermitsAuthStart(%q) = %t, want %t", c.status, got, c.wantPermitsStart)
		}
		if got := m.IsEligibleForSession(d); got != c.wantEligibleSess {
			t.Errorf("IsEligibleForSession(%q) = %t, want %t", c.status, got, c.wantEligibleSess)
		}
	}
}

// The username-binding planner: which (state, incoming-name) pairs are a legal bind, a
// match, a hard mismatch, or a no-op. This is the logic the four endpoints used to inline.
func TestPlanUsernameBinding(t *testing.T) {
	m := newDeviceBindingWithStore(&fakeFullyBinder{})

	cases := []struct {
		name      string
		device    *db.Device
		incoming  string
		offline   func(string) bool
		want      UsernameAction
	}{
		{"empty incoming is noop", deviceWith("hwid_pending", nil), "", neverOffline, UsernameNoop},
		{"hwid_pending first bind", deviceWith("hwid_pending", nil), "Steve", neverOffline, UsernameBind},
		{"fully_bound empty stored repairs", deviceWith("fully_bound", strptr("")), "Steve", neverOffline, UsernameBind},
		{"fully_bound nil stored repairs", deviceWith("fully_bound", nil), "Steve", neverOffline, UsernameBind},
		{"fully_bound matching accepts", deviceWith("fully_bound", strptr("Steve")), "steve", neverOffline, UsernameMatches},
		{"fully_bound real mismatch rejects", deviceWith("fully_bound", strptr("Steve")), "Alex", neverOffline, UsernameMismatch},
		{"fully_bound offline-incoming rebinds", deviceWith("fully_bound", strptr("Steve")), "Player123",
			isOfflinePlaceholderName, UsernameBind},
		{"fully_bound offline-stored rebinds", deviceWith("fully_bound", strptr("Player99")), "Steve",
			isOfflinePlaceholderName, UsernameBind},
		{"unbound is noop", deviceWith("unbound", nil), "Steve", neverOffline, UsernameNoop},
		{"suspended is noop", deviceWith("suspended", nil), "Steve", neverOffline, UsernameNoop},
		{"unknown is noop", deviceWith("weird", nil), "Steve", neverOffline, UsernameNoop},
	}
	for _, c := range cases {
		t.Run(c.name, func(t *testing.T) {
			if got := m.PlanUsernameBinding(c.device, c.incoming, c.offline); got != c.want {
				t.Errorf("PlanUsernameBinding = %d, want %d", got, c.want)
			}
		})
	}
}

// The FullyBind transition is the one lifecycle write routed through the module; it must
// reach the persistence seam with the device id, name and source ip intact.
func TestDeviceBindingFullyBindRoutesToStore(t *testing.T) {
	store := &fakeFullyBinder{}
	m := newDeviceBindingWithStore(store)

	if err := m.FullyBind(context.Background(), "dev-7", "Steve", "10.0.0.1"); err != nil {
		t.Fatalf("FullyBind: %v", err)
	}
	if store.calls != 1 {
		t.Fatalf("FullyBind store calls = %d, want 1", store.calls)
	}
	if store.lastDeviceID != "dev-7" || store.lastUsername != "Steve" || store.lastIP != "10.0.0.1" {
		t.Errorf("FullyBind forwarded (%q,%q,%q), want (dev-7,Steve,10.0.0.1)",
			store.lastDeviceID, store.lastUsername, store.lastIP)
	}
}
