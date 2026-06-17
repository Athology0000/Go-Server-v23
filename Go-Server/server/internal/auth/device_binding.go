package auth

import (
	"context"
	"strings"

	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/db"
)

// device_binding.go holds the DeviceBinding module: the one place that owns the
// legal transitions of a device's binding lifecycle.
//
// Before this module the lifecycle (unbound -> hwid_pending -> fully_bound, plus the
// suspended/banned dead ends) was re-derived by hand in four auth endpoints — Start,
// Finish, VerifyMinecraft, VerifySession — each hand-comparing the raw BindingStatus
// string and sequencing raw db calls. That made the state machine shallow and smeared:
// a rule change meant editing four call sites, and illegal transitions were representable
// anywhere. This module pulls that knowledge behind a small interface so the endpoints
// ask it questions ("does this state permit X?") and ask it to perform the one real
// transition (fully-bind), instead of each re-implementing the rules. The db wrappers
// stay as the persistence implementation behind the FullyBinder seam, giving the module
// locality over the lifecycle without owning storage.

// BindingState is the typed lifecycle state. Classifying the raw BindingStatus string
// into this enum once, at the module seam, is what lets every call site stop matching
// string literals.
type BindingState int

const (
	// StateUnknown covers any persisted status the module does not recognise. It is
	// treated as not-enrolled / not-eligible everywhere, matching the prior default arms.
	StateUnknown BindingState = iota
	// StateUnbound is a freshly provisioned device that enrollment has not advanced yet.
	StateUnbound
	// StateHWIDPending is enrolled and awaiting its first full bind (HWID + minecraft name).
	StateHWIDPending
	// StateFullyBound is bound to a minecraft identity and eligible for auth.
	StateFullyBound
	// StateBlocked is a suspended or banned device — a lifecycle dead end until admin reset.
	StateBlocked
)

// Raw binding_status strings as persisted in the devices table. Centralised here so the
// mapping between storage and the typed lifecycle lives in exactly one place.
const (
	bindingStatusUnbound     = "unbound"
	bindingStatusHWIDPending = "hwid_pending"
	bindingStatusFullyBound  = "fully_bound"
	bindingStatusSuspended   = "suspended"
	bindingStatusBanned      = "banned"
)

// classifyBindingStatus maps a persisted binding_status string to the typed lifecycle state.
func classifyBindingStatus(status string) BindingState {
	switch status {
	case bindingStatusUnbound:
		return StateUnbound
	case bindingStatusHWIDPending:
		return StateHWIDPending
	case bindingStatusFullyBound:
		return StateFullyBound
	case bindingStatusSuspended, bindingStatusBanned:
		return StateBlocked
	default:
		return StateUnknown
	}
}

// UsernameAction is the verb the username-binding planner hands back to a caller. It
// names the legal move for the (state, incoming-username) pair without the caller having
// to know the underlying rules.
type UsernameAction int

const (
	// UsernameNoop: nothing to do (no incoming name, or a non-bindable state at this seam).
	UsernameNoop UsernameAction = iota
	// UsernameBind: perform a full bind to the incoming name (first bind, or repair of a
	// missing/empty stored name, or an offline-placeholder rebind).
	UsernameBind
	// UsernameMatches: device is already bound and the incoming name matches — accept as-is.
	UsernameMatches
	// UsernameMismatch: device is bound to a different, real name — reject.
	UsernameMismatch
)

// FullyBinder is the persistence seam the module drives to commit a fully-bound transition.
// *db.Device's pool satisfies it via the existing db.FullyBind wrapper, so storage stays
// behind the module rather than inside it (a thin seam, a deep module).
type FullyBinder interface {
	FullyBind(ctx context.Context, deviceID, minecraftUsername, sourceIP string) error
}

// poolFullyBinder adapts the package-level db.FullyBind wrapper to the FullyBinder seam.
type poolFullyBinder struct{ pool *pgxpool.Pool }

func (b poolFullyBinder) FullyBind(ctx context.Context, deviceID, minecraftUsername, sourceIP string) error {
	return db.FullyBind(ctx, b.pool, deviceID, minecraftUsername, sourceIP)
}

// DeviceBinding owns the binding lifecycle's legal transitions and the questions the auth
// endpoints used to answer by hand. It is deliberately deep: a small surface in front of
// the scattered BindingStatus rules.
type DeviceBinding struct {
	store FullyBinder
}

// NewDeviceBinding wires the module to the live pool's db wrappers as its persistence.
func NewDeviceBinding(pool *pgxpool.Pool) *DeviceBinding {
	return &DeviceBinding{store: poolFullyBinder{pool: pool}}
}

// newDeviceBindingWithStore lets tests drive the module against an in-memory FullyBinder.
func newDeviceBindingWithStore(store FullyBinder) *DeviceBinding {
	return &DeviceBinding{store: store}
}

// State classifies a device's persisted status into the typed lifecycle state.
func (m *DeviceBinding) State(device *db.Device) BindingState {
	return classifyBindingStatus(device.BindingStatus)
}

// IsBlocked reports whether the device is in a suspended/banned dead end. Replaces the
// hand-written `status == "suspended" || status == "banned"` checks in Start and VerifySession.
func (m *DeviceBinding) IsBlocked(device *db.Device) bool {
	return m.State(device) == StateBlocked
}

// PermitsAuthStart reports whether a device may begin the challenge handshake — i.e. it is
// enrolled (hwid_pending) or already fully bound. Replaces Start's
// `status != "hwid_pending" && status != "fully_bound"` not-enrolled guard. Callers are
// expected to have already rejected blocked devices via IsBlocked, mirroring prior ordering.
func (m *DeviceBinding) PermitsAuthStart(device *db.Device) bool {
	switch m.State(device) {
	case StateHWIDPending, StateFullyBound:
		return true
	default:
		return false
	}
}

// IsEligibleForSession reports whether a device is in a state that may resolve into a session
// at the minecraft-verify seam — fully bound, or hwid_pending (which is upgraded to fully
// bound as a side effect of verifying). Replaces VerifyMinecraft's default not-eligible arm.
func (m *DeviceBinding) IsEligibleForSession(device *db.Device) bool {
	switch m.State(device) {
	case StateHWIDPending, StateFullyBound:
		return true
	default:
		return false
	}
}

// PlanUsernameBinding decides the legal move for an incoming minecraft username given the
// device's current lifecycle state. It centralises the per-endpoint username logic
// (first bind vs. match vs. mismatch vs. skip) without performing any persistence; callers
// act on the returned verb and, for UsernameBind, call FullyBind.
//
// isOfflinePlaceholder lets the caller pass in the offline-name predicate so the module
// stays free of the auth package's regexp detail while preserving the silent-rebind rule.
func (m *DeviceBinding) PlanUsernameBinding(device *db.Device, incoming string, isOfflinePlaceholder func(string) bool) UsernameAction {
	incoming = strings.TrimSpace(incoming)
	if incoming == "" {
		return UsernameNoop
	}

	switch m.State(device) {
	case StateHWIDPending:
		// First full bind: hwid_pending -> fully_bound, adopting the incoming name.
		return UsernameBind
	case StateFullyBound:
		stored := ""
		if device.MinecraftUsername != nil {
			stored = strings.TrimSpace(*device.MinecraftUsername)
		}
		if stored == "" {
			// Bound but with no real name on record — repair by binding the incoming one.
			return UsernameBind
		}
		if strings.EqualFold(stored, incoming) {
			return UsernameMatches
		}
		// Names differ. Offline placeholders rotate per launch, so silently rebind rather
		// than perma-mismatch; a genuine name change is a hard mismatch.
		if isOfflinePlaceholder(incoming) || isOfflinePlaceholder(stored) {
			return UsernameBind
		}
		return UsernameMismatch
	default:
		// unbound / blocked / unknown: not a state that binds a name at this seam.
		return UsernameNoop
	}
}

// FullyBind commits the fully-bound transition through the persistence seam. This is the
// single lifecycle write the endpoints now route through the module instead of calling the
// raw db wrapper inline.
func (m *DeviceBinding) FullyBind(ctx context.Context, deviceID, minecraftUsername, sourceIP string) error {
	return m.store.FullyBind(ctx, deviceID, minecraftUsername, sourceIP)
}
