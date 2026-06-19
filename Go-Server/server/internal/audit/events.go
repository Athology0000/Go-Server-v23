package audit

// Event taxonomy.
//
// Audit event types used to be free-form strings scattered across emit sites
// (auth, panel, admin, enrollment, …) while the "activity feed" subset was
// duplicated as a hardcoded `event_type IN (...)` list in package db. Those two
// lists lived in different packages and silently drifted: a new activity event
// that nobody remembered to add to the SQL list never showed up in the feed.
//
// This file is the single source of truth. Event types are named constants, and
// the activity-feed subset is derived from the taxonomy via ActivityEventTypes()
// (consumed by db.ListActivityLog). Migrate emit sites to these constants so the
// filter can never drift from the emitters again.

// EventType is a typed audit event identifier. Its underlying string is the value
// stored in audit_log.event_type, so existing rows and the wire format are
// unchanged.
type EventType string

func (e EventType) String() string { return string(e) }

// ── Activity-feed events ─────────────────────────────────────────────────────
// These are the user-facing events surfaced by the panel activity log. The set
// below MUST equal the previously-hardcoded list in db.ListActivityLog; the
// taxonomy test pins this so future drift is caught.
const (
	EventPanelLoginSuccess     EventType = "panel.login.success"
	EventPanelLoginFail        EventType = "panel.login.fail"
	EventAuthStartSuccess      EventType = "auth.start.success"
	EventAuthStartFail         EventType = "auth.start.fail"
	EventAuthFinishSuccess     EventType = "auth.finish.success"
	EventAuthFinishFail        EventType = "auth.finish.fail"
	EventAuthDeviceSuspended   EventType = "auth.device.suspended"
	EventPanelKeyRedeemSuccess EventType = "panel.key.redeem.success"
)

// ── Non-activity events ──────────────────────────────────────────────────────
// Emitted to the audit log but not surfaced in the activity feed. Migrated to
// constants opportunistically; the activity-feed contract does not depend on
// this section being exhaustive.
const (
	// auth
	EventAuthFinishEntitlementDenied        EventType = "auth.finish.entitlement_denied"
	EventAuthHeartbeat                      EventType = "auth.heartbeat"
	EventAuthHeartbeatEntitlementRevoked    EventType = "auth.heartbeat.entitlement_revoked"
	EventAuthVerifyMCSuccess                EventType = "auth.verify_mc.success"
	EventAuthVerifyMCFail                   EventType = "auth.verify_mc.fail"
	EventAuthVerifyMCEntitlementDenied      EventType = "auth.verify_mc.entitlement_denied"
	EventAuthVerifySessionSuccess           EventType = "auth.verify_session.success"
	EventAuthVerifySessionFail              EventType = "auth.verify_session.fail"
	EventAuthVerifySessionHWIDPinned        EventType = "auth.verify_session.hwid_pinned"
	EventAuthVerifySessionEntitlementDenied EventType = "auth.verify_session.entitlement_denied"

	// enrollment
	EventEnrollHandshakeSuccess       EventType = "enroll.handshake.success"
	EventEnrollHandshakeFail          EventType = "enroll.handshake.fail"
	EventEnrollHandshakeDeviceCreated EventType = "enroll.handshake.device_created"
	EventEnrollRedeemSuccess          EventType = "enroll.redeem.success"
	EventEnrollRedeemFail             EventType = "enroll.redeem.fail"

	// admin
	EventAdminBuildApprove        EventType = "admin.build.approve"
	EventAdminBuildDeny           EventType = "admin.build.deny"
	EventAdminAccountCreate       EventType = "admin.account.create"
	EventAdminAccountStatus       EventType = "admin.account.status"
	EventAdminManifestRevoke      EventType = "admin.manifest.revoke"
	EventAdminManifestCreate      EventType = "admin.manifest.create"
	EventAdminTraceJar            EventType = "admin.trace.jar"
	EventAdminLicenseCreate       EventType = "admin.license.create"
	EventAdminLicenseStatus       EventType = "admin.license.status"
	EventAdminLicenseKeysGenerate EventType = "admin.license_keys.generate"
	EventAdminLicenseKeyRevoke    EventType = "admin.license_key.revoke"
	EventAdminDeviceReset         EventType = "admin.device.reset"
	EventAdminDeviceStatus        EventType = "admin.device.status"
	EventAdminEntitlementUpsert   EventType = "admin.entitlement.upsert"
	EventAdminOverrideUpsert      EventType = "admin.override.upsert"
	EventAdminOverrideDelete      EventType = "admin.override.delete"
	EventAdminSessionRevoke       EventType = "admin.session.revoke"
	EventAdminTokenCreate         EventType = "admin.token.create"
	EventAdminTokenRevoke         EventType = "admin.token.revoke"
	EventAdminUserBan             EventType = "admin.user.ban"
	EventAdminUserUnban           EventType = "admin.user.unban"
	EventAdminUserAddTime         EventType = "admin.user.add_time"
	EventAdminUserUpgrade         EventType = "admin.user.upgrade"
	EventAdminKeyGenerate         EventType = "admin.key.generate"
)

// activityEvents is the ordered activity-feed subset. The order matches the
// previously-hardcoded SQL IN(...) list; ListActivityLog uses set membership so
// order is not load-bearing, but it is kept stable for readability and diffing.
var activityEvents = []EventType{
	EventPanelLoginSuccess,
	EventPanelLoginFail,
	EventAuthStartSuccess,
	EventAuthStartFail,
	EventAuthFinishSuccess,
	EventAuthFinishFail,
	EventAuthDeviceSuspended,
	EventPanelKeyRedeemSuccess,
}

// activitySet is the membership view of activityEvents.
var activitySet = func() map[EventType]struct{} {
	m := make(map[EventType]struct{}, len(activityEvents))
	for _, e := range activityEvents {
		m[e] = struct{}{}
	}
	return m
}()

// IsActivity reports whether an event type is surfaced in the activity feed.
func IsActivity(e EventType) bool {
	_, ok := activitySet[e]
	return ok
}

// ActivityEventTypes returns the activity-feed event types as plain strings,
// suitable for passing to db.ListActivityLog as the WHERE filter. A fresh copy
// is returned so callers cannot mutate the taxonomy.
func ActivityEventTypes() []string {
	out := make([]string, len(activityEvents))
	for i, e := range activityEvents {
		out[i] = string(e)
	}
	return out
}
