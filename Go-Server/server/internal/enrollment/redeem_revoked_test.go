package enrollment

import (
	"context"
	"errors"
	"testing"
	"time"
)

// licenseStatusBlocksRedemption is the rule that stops a key redemption from
// silently resurrecting an admin-terminated license. Only the admin-applied
// terminal states (revoked, suspended) block; a naturally lapsed "expired"
// license — and active/trial — must still be renewable, since renewal is the
// normal reason a user redeems a key.
func TestLicenseStatusBlocksRedemption(t *testing.T) {
	cases := []struct {
		status string
		want   bool
	}{
		{"revoked", true},
		{"suspended", true},
		{"expired", false}, // a lapsed license must still renew
		{"active", false},  // extend
		{"trial", false},   // upgrade
		{"", false},        // domain is CHECK-constrained; don't over-block
	}
	for _, c := range cases {
		t.Run(c.status, func(t *testing.T) {
			if got := licenseStatusBlocksRedemption(c.status); got != c.want {
				t.Fatalf("licenseStatusBlocksRedemption(%q) = %v, want %v", c.status, got, c.want)
			}
		})
	}
}

// Redeeming against an admin-REVOKED license must fail with ErrLicenseLocked and
// must NOT run the license/key UPDATEs — the script intentionally stops after the
// license lookup, so any write past the guard fails the test (fakeTx Fatals on an
// unscripted query). This is the regression for the status-clobber finding: the
// old extend branch ran `UPDATE licenses SET status='active'` unconditionally.
func TestRedeemOnTxRevokedLicenseBlocked(t *testing.T) {
	future := time.Now().AddDate(0, 0, 10)
	tx := &fakeTx{t: t, script: []queryMatcher{
		{contains: "FROM license_keys", row: fakeRow{vals: []any{"pro", 30, keyAvailable}}},
		{contains: "FROM devices", row: fakeRow{vals: []any{"dev-1", "unbound", []byte(nil)}}},
		{contains: "FROM licenses", row: fakeRow{vals: []any{future, "revoked"}}},
	}}

	_, err := RedeemOnTx(context.Background(), tx, testDeviceSecret(), "acct-1", "KEY-1", "1.2.3.4", CoreOptions{})
	if !errors.Is(err, ErrLicenseLocked) {
		t.Fatalf("err = %v, want ErrLicenseLocked", err)
	}
}

// Same guard for an admin-SUSPENDED license.
func TestRedeemOnTxSuspendedLicenseBlocked(t *testing.T) {
	future := time.Now().AddDate(0, 0, 10)
	tx := &fakeTx{t: t, script: []queryMatcher{
		{contains: "FROM license_keys", row: fakeRow{vals: []any{"pro", 30, keyAvailable}}},
		{contains: "FROM devices", row: fakeRow{vals: []any{"dev-2", "unbound", []byte(nil)}}},
		{contains: "FROM licenses", row: fakeRow{vals: []any{future, "suspended"}}},
	}}

	_, err := RedeemOnTx(context.Background(), tx, testDeviceSecret(), "acct-2", "KEY-2", "2.2.2.2", CoreOptions{})
	if !errors.Is(err, ErrLicenseLocked) {
		t.Fatalf("err = %v, want ErrLicenseLocked", err)
	}
}

// A naturally EXPIRED license must still renew: the guard blocks only admin
// terminal states, so the redemption proceeds through the license + key UPDATEs.
func TestRedeemOnTxExpiredLicenseRenews(t *testing.T) {
	past := time.Now().AddDate(0, 0, -5)
	tx := &fakeTx{t: t, script: []queryMatcher{
		{contains: "FROM license_keys", row: fakeRow{vals: []any{"pro", 30, keyAvailable}}},
		{contains: "FROM devices", row: fakeRow{vals: []any{"dev-3", "unbound", []byte(nil)}}},
		{contains: "FROM licenses", row: fakeRow{vals: []any{past, "expired"}}},
		{contains: "UPDATE licenses", tag: redeemedTag()},
		{contains: "UPDATE license_keys", tag: redeemedTag()},
	}}

	res, err := RedeemOnTx(context.Background(), tx, testDeviceSecret(), "acct-3", "KEY-3", "3.3.3.3", CoreOptions{})
	if err != nil {
		t.Fatalf("RedeemOnTx: unexpected err %v (expired must renew)", err)
	}
	if res.PlanTier != "pro" {
		t.Errorf("PlanTier = %q, want pro", res.PlanTier)
	}
	// Sanity: a 30-day renewal from now (not from the past expiry).
	if res.ExpiresAt == nil || res.ExpiresAt.Before(time.Now()) {
		t.Errorf("ExpiresAt = %v, want a future renewal", res.ExpiresAt)
	}
}
