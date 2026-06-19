package enrollment

import (
	"context"
	"errors"
	"strings"
	"testing"
	"time"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/phantom/server/internal/crypto"
)

// These exercise the shared redemption core (RedeemOnTx) directly against a scripted fake tx —
// no Postgres. RedeemOnTx is the one owner of the device/key/license rules that the enrollment
// and panel adapters now both run through; a fake satisfying the small redeemTx interface
// (QueryRow + Exec) is the second adapter that makes that seam testable, so the unbound-guard,
// the double-spend guard, and the enrollment-only hwid_pending knob are asserted without a DB.

// fakeRow scripts a single QueryRow result: either an error, or values to copy into Scan dests.
type fakeRow struct {
	err  error
	vals []any
}

func (r fakeRow) Scan(dest ...any) error {
	if r.err != nil {
		return r.err
	}
	for i := range dest {
		if i >= len(r.vals) {
			break
		}
		switch d := dest[i].(type) {
		case *string:
			*d = r.vals[i].(string)
		case *int:
			*d = r.vals[i].(int)
		case *[]byte:
			if r.vals[i] == nil {
				*d = nil
			} else {
				*d = r.vals[i].([]byte)
			}
		case **time.Time:
			if r.vals[i] == nil {
				*d = nil
			} else {
				t := r.vals[i].(time.Time)
				*d = &t
			}
		default:
			panic("fakeRow: unhandled dest type")
		}
	}
	return nil
}

// queryMatcher matches a scripted response to a QueryRow/Exec by a substring of the SQL, so the
// script reads in redemption order rather than by exact whitespace-sensitive text.
type queryMatcher struct {
	contains string
	row      fakeRow           // for QueryRow
	tag      pgconn.CommandTag // for Exec
	execErr  error
}

// fakeTx is a redeemTx that walks a script of queryMatchers in order. Each QueryRow/Exec consumes
// the next matcher whose SQL substring matches; a non-matching or exhausted script fails the test.
type fakeTx struct {
	t      *testing.T
	script []queryMatcher
	i      int
}

func (f *fakeTx) next(sql string) *queryMatcher {
	for f.i < len(f.script) {
		m := &f.script[f.i]
		f.i++
		if strings.Contains(normalizeSQL(sql), m.contains) {
			return m
		}
	}
	f.t.Fatalf("fakeTx: no scripted response for SQL: %q", normalizeSQL(sql))
	return nil
}

func normalizeSQL(s string) string { return strings.Join(strings.Fields(s), " ") }

func (f *fakeTx) QueryRow(_ context.Context, sql string, _ ...any) pgx.Row {
	return f.next(sql).row
}

func (f *fakeTx) Exec(_ context.Context, sql string, _ ...any) (pgconn.CommandTag, error) {
	m := f.next(sql)
	return m.tag, m.execErr
}

// ds is a DeviceSecret built on a fixed 32-byte master key so Seal/Open round-trips in tests.
func testDeviceSecret() *crypto.DeviceSecret {
	key := make([]byte, 32)
	for i := range key {
		key[i] = byte(i)
	}
	return crypto.NewDeviceSecret(key)
}

// redeemedTag is a CommandTag whose RowsAffected() == 1 (a successful single-row UPDATE).
func redeemedTag() pgconn.CommandTag { return pgconn.NewCommandTag("UPDATE 1") }

// lostRaceTag is a CommandTag whose RowsAffected() == 0 (the double-spend lost the race).
func lostRaceTag() pgconn.CommandTag { return pgconn.NewCommandTag("UPDATE 0") }

const (
	keyAvailable = "available"
)

// Success path with NO existing device and NO existing license: the core creates+seals a device,
// inserts a license, and marks the key redeemed. With the enrollment knobs off (panel shape) the
// device is NOT advanced to hwid_pending and no secret is returned.
func TestRedeemOnTxSuccessCreate(t *testing.T) {
	tx := &fakeTx{t: t, script: []queryMatcher{
		{contains: "FROM license_keys", row: fakeRow{vals: []any{"pro", 30, keyAvailable}}},
		{contains: "FROM devices", row: fakeRow{err: pgx.ErrNoRows}},
		{contains: "INSERT INTO devices", row: fakeRow{vals: []any{"dev-1", "unbound", []byte(nil)}}},
		{contains: "FROM licenses", row: fakeRow{err: pgx.ErrNoRows}},
		{contains: "INSERT INTO licenses", tag: redeemedTag()},
		{contains: "UPDATE license_keys", tag: redeemedTag()},
	}}

	res, err := RedeemOnTx(context.Background(), tx, testDeviceSecret(), "acct-1", "KEY-1", "1.2.3.4", CoreOptions{})
	if err != nil {
		t.Fatalf("RedeemOnTx: unexpected err %v", err)
	}
	if res.PlanTier != "pro" || res.DurationDays != 30 {
		t.Errorf("res = %+v, want plan=pro duration=30", res)
	}
	if res.ExpiresAt == nil {
		t.Errorf("ExpiresAt = nil, want a 30-day expiry")
	}
	if res.DeviceSecret != "" {
		t.Errorf("DeviceSecret = %q, want empty (OpenDeviceSecret off)", res.DeviceSecret)
	}
	if res.DeviceID != "dev-1" {
		t.Errorf("DeviceID = %q, want dev-1", res.DeviceID)
	}
}

// Success path EXTENDING an existing unbound device + existing license, with both enrollment knobs
// ON: the device is advanced to hwid_pending, the secret is opened+returned, and the new expiry
// stacks onto the existing future expiry (computeNewExpiry stacking rule).
func TestRedeemOnTxSuccessExtendWithKnobs(t *testing.T) {
	future := time.Now().AddDate(0, 0, 10)
	sealed, err := testDeviceSecret().Seal([]byte("0123456789abcdef0123456789abcdef"))
	if err != nil {
		t.Fatalf("seal: %v", err)
	}

	tx := &fakeTx{t: t, script: []queryMatcher{
		{contains: "FROM license_keys", row: fakeRow{vals: []any{"premium", 30, keyAvailable}}},
		{contains: "FROM devices", row: fakeRow{vals: []any{"dev-9", "unbound", sealed}}},
		{contains: "UPDATE devices SET enrollment_ip", tag: redeemedTag()},                   // ip re-align (knob)
		{contains: "UPDATE devices SET binding_status = 'hwid_pending'", tag: redeemedTag()}, // advance (knob)
		{contains: "FROM licenses", row: fakeRow{vals: []any{future}}},
		{contains: "UPDATE licenses", tag: redeemedTag()},
		{contains: "UPDATE license_keys", tag: redeemedTag()},
	}}

	res, err := RedeemOnTx(context.Background(), tx, testDeviceSecret(), "acct-9", "KEY-9", "9.9.9.9",
		CoreOptions{AdvanceToHWIDPending: true, OpenDeviceSecret: true})
	if err != nil {
		t.Fatalf("RedeemOnTx: unexpected err %v", err)
	}
	if res.DeviceSecret == "" {
		t.Errorf("DeviceSecret empty, want the opened secret (base64)")
	}
	if res.ExpiresAt == nil {
		t.Fatal("ExpiresAt nil, want a stacked expiry")
	}
	if want := future.AddDate(0, 0, 30); res.ExpiresAt.Sub(want) > time.Minute || want.Sub(*res.ExpiresAt) > time.Minute {
		t.Errorf("ExpiresAt = %v, want ~%v (stacked onto existing future expiry)", res.ExpiresAt, want)
	}
}

// The unbound-guard: an existing device that is NOT unbound rejects with ErrAlreadyEnrolled — the
// same guard both the enrollment and panel adapters surface (panel as 409 device_not_redeemable).
// No license/key write must happen, so the script intentionally stops after the device lookup.
func TestRedeemOnTxUnboundGuard(t *testing.T) {
	tx := &fakeTx{t: t, script: []queryMatcher{
		{contains: "FROM license_keys", row: fakeRow{vals: []any{"pro", 30, keyAvailable}}},
		{contains: "FROM devices", row: fakeRow{vals: []any{"dev-7", "fully_bound", []byte(nil)}}},
	}}

	_, err := RedeemOnTx(context.Background(), tx, testDeviceSecret(), "acct-7", "KEY-7", "7.7.7.7", CoreOptions{})
	if !errors.Is(err, ErrAlreadyEnrolled) {
		t.Fatalf("err = %v, want ErrAlreadyEnrolled", err)
	}
}

// A key that does not exist surfaces ErrKeyNotFound (panel maps this to 400 invalid_key).
func TestRedeemOnTxKeyNotFound(t *testing.T) {
	tx := &fakeTx{t: t, script: []queryMatcher{
		{contains: "FROM license_keys", row: fakeRow{err: pgx.ErrNoRows}},
	}}

	_, err := RedeemOnTx(context.Background(), tx, testDeviceSecret(), "acct-1", "NOPE", "1.1.1.1", CoreOptions{})
	if !errors.Is(err, ErrKeyNotFound) {
		t.Fatalf("err = %v, want ErrKeyNotFound", err)
	}
}

// A key whose status != available surfaces ErrKeyNotAvailable (panel maps this to 400 key_unavailable).
func TestRedeemOnTxKeyUnavailable(t *testing.T) {
	tx := &fakeTx{t: t, script: []queryMatcher{
		{contains: "FROM license_keys", row: fakeRow{vals: []any{"pro", 30, "redeemed"}}},
	}}

	_, err := RedeemOnTx(context.Background(), tx, testDeviceSecret(), "acct-1", "USED", "1.1.1.1", CoreOptions{})
	if !errors.Is(err, ErrKeyNotAvailable) {
		t.Fatalf("err = %v, want ErrKeyNotAvailable", err)
	}
}

// The lost double-spend race: the key passed the FOR UPDATE read as available, but the final
// guarded UPDATE ... WHERE status='available' affects 0 rows (a concurrent redeem won). The core
// must surface ErrKeyNotAvailable rather than reporting success.
func TestRedeemOnTxLostDoubleSpendRace(t *testing.T) {
	tx := &fakeTx{t: t, script: []queryMatcher{
		{contains: "FROM license_keys", row: fakeRow{vals: []any{"pro", 30, keyAvailable}}},
		{contains: "FROM devices", row: fakeRow{err: pgx.ErrNoRows}},
		{contains: "INSERT INTO devices", row: fakeRow{vals: []any{"dev-1", "unbound", []byte(nil)}}},
		{contains: "FROM licenses", row: fakeRow{err: pgx.ErrNoRows}},
		{contains: "INSERT INTO licenses", tag: redeemedTag()},
		{contains: "UPDATE license_keys", tag: lostRaceTag()},
	}}

	_, err := RedeemOnTx(context.Background(), tx, testDeviceSecret(), "acct-1", "KEY-1", "1.1.1.1", CoreOptions{})
	if !errors.Is(err, ErrKeyNotAvailable) {
		t.Fatalf("err = %v, want ErrKeyNotAvailable (lost race)", err)
	}
}
