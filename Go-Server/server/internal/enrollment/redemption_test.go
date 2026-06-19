package enrollment

import (
	"context"
	"encoding/json"
	"io"
	"net/http/httptest"
	"strings"
	"testing"
	"time"

	"github.com/gofiber/fiber/v2"
)

// These exercise the Redemption seam without a database. The redemption transaction is deep
// enough to own its own module behind the Redeemer interface; a fake satisfies that seam (the
// second adapter that makes it real) so the handler's accept/reject contract — which error maps
// to which status and reason, and the success response shape — is asserted in isolation. The
// redemption's pure lifetime rule (computeNewExpiry) is asserted directly.

// fakeRedeemer stands in for the live *Redemption at the seam. It records the request it was
// handed and returns a scripted result/error so the handler's mapping can be checked.
type fakeRedeemer struct {
	gotReq RedeemRequest
	result RedeemResult
	err    error
}

func (f *fakeRedeemer) Redeem(_ context.Context, req RedeemRequest) (RedeemResult, error) {
	f.gotReq = req
	return f.result, f.err
}

// redeemApp wires just the redeem route to a fake Redeemer so the handler can be driven over
// HTTP without the rate limiter or pool that RegisterRoutes needs.
func redeemApp(r Redeemer) *fiber.App {
	app := fiber.New()
	app.Post("/enroll/redeem", handleRedeem(r))
	return app
}

func postRedeem(t *testing.T, app *fiber.App, body string) (int, map[string]any) {
	t.Helper()
	req := httptest.NewRequest("POST", "/enroll/redeem", strings.NewReader(body))
	req.Header.Set("Content-Type", "application/json")
	resp, err := app.Test(req, 10000)
	if err != nil {
		t.Fatalf("app.Test: %v", err)
	}
	raw, _ := io.ReadAll(resp.Body)
	var parsed map[string]any
	if len(raw) > 0 {
		_ = json.Unmarshal(raw, &parsed)
	}
	return resp.StatusCode, parsed
}

// A malformed or incomplete body is rejected at the handler before the seam is ever touched.
// Redemption is now bound to a credential proof: license_key + username + password are all
// required. A request that omits any of them (including the old account_id-only shape) is
// rejected before the seam.
func TestRedeemHandlerRejectsBadInput(t *testing.T) {
	fake := &fakeRedeemer{}
	app := redeemApp(fake)

	for _, body := range []string{
		`not json`,
		`{"license_key":"","username":"neo","password":"pw"}`,
		`{"license_key":"KEY","username":"","password":"pw"}`,
		`{"license_key":"KEY","username":"neo","password":""}`,
		// Account-takeover shape: a raw account_id with no credential proof.
		`{"license_key":"KEY","account_id":"victim-uuid"}`,
	} {
		status, parsed := postRedeem(t, app, body)
		if status != 400 {
			t.Errorf("body %q: status = %d, want 400", body, status)
		}
		if parsed["error"] != "enrollment_failed" {
			t.Errorf("body %q: error = %v, want enrollment_failed", body, parsed["error"])
		}
	}
	if (fake.gotReq != RedeemRequest{}) {
		t.Errorf("seam was called for bad input: %+v", fake.gotReq)
	}
}

// Regression for the /enroll/redeem account-takeover (issue #1): the handler must derive the
// redeemed identity from a proven credential, never from a caller-supplied account_id. It must
// forward username + password to the seam and never forward an attacker-controlled account_id.
func TestRedeemHandlerBindsToCredentialNotAccountID(t *testing.T) {
	fake := &fakeRedeemer{result: RedeemResult{Username: "neo", DeviceSecret: "c2VjcmV0", PlanTier: "pro"}}
	app := redeemApp(fake)

	// Body smuggles a victim account_id alongside the attacker's own credentials. The seam
	// must redeem onto the credential-proven identity and never the smuggled id — RedeemRequest
	// has no AccountID field at all, so a body account_id is structurally unreachable; here we
	// assert the proven credentials are what reaches the seam.
	status, _ := postRedeem(t, app,
		`{"license_key":"KEY-123","account_id":"victim-uuid","username":"neo","password":"pw"}`)
	if status != 200 {
		t.Fatalf("status = %d, want 200", status)
	}
	if fake.gotReq.Username != "neo" || fake.gotReq.Password != "pw" {
		t.Errorf("seam got Username=%q Password=%q, want neo/pw", fake.gotReq.Username, fake.gotReq.Password)
	}
	if fake.gotReq.RawKey != "KEY-123" {
		t.Errorf("seam got RawKey=%q, want KEY-123", fake.gotReq.RawKey)
	}
}

// Each redemption error maps to a fixed status, and every key-related failure collapses to one
// opaque response with NO reason field. Returning a per-state reason (key_not_found vs
// key_not_available vs already_enrolled vs key_invalid) was a key-existence enumeration oracle
// (issue #6): it confirmed to an attacker which guessed keys exist and their state. The
// server-side audit.Log keeps the fine-grained distinction; the client does not.
func TestRedeemHandlerErrorMapping(t *testing.T) {
	cases := []struct {
		name       string
		err        error
		wantStatus int
	}{
		{"key not found", ErrKeyNotFound, 400},
		{"key not available", ErrKeyNotAvailable, 400},
		{"already enrolled", ErrAlreadyEnrolled, 400},
		{"bad credentials", ErrBadCredentials, 401},
		{"ip mismatch", ErrIPMismatch, 401},
		{"unexpected", context.DeadlineExceeded, 500},
	}
	for _, c := range cases {
		t.Run(c.name, func(t *testing.T) {
			app := redeemApp(&fakeRedeemer{err: c.err})
			status, parsed := postRedeem(t, app, `{"license_key":"KEY","username":"neo","password":"pw"}`)
			if status != c.wantStatus {
				t.Errorf("status = %d, want %d", status, c.wantStatus)
			}
			if parsed["error"] != "enrollment_failed" {
				t.Errorf("error = %v, want enrollment_failed", parsed["error"])
			}
			// No failure mode may leak a distinguishing reason to the client.
			if got, ok := parsed["reason"]; ok {
				t.Errorf("reason = %v leaked, want absent", got)
			}
		})
	}
}

// The key-existence oracle (issue #6): ErrKeyNotFound, ErrKeyNotAvailable, and ErrAlreadyEnrolled
// must produce a byte-identical client response, so a key's existence/state cannot be inferred
// from the reply.
func TestRedeemHandlerKeyFailuresAreByteIdentical(t *testing.T) {
	keyErrs := []struct {
		name string
		err  error
	}{
		{"key not found", ErrKeyNotFound},
		{"key not available", ErrKeyNotAvailable},
		{"already enrolled", ErrAlreadyEnrolled},
	}

	rawResponse := func(t *testing.T, err error) (int, string) {
		t.Helper()
		app := redeemApp(&fakeRedeemer{err: err})
		req := httptest.NewRequest("POST", "/enroll/redeem", strings.NewReader(`{"license_key":"KEY","username":"neo","password":"pw"}`))
		req.Header.Set("Content-Type", "application/json")
		resp, rerr := app.Test(req, 10000)
		if rerr != nil {
			t.Fatalf("app.Test: %v", rerr)
		}
		body, _ := io.ReadAll(resp.Body)
		return resp.StatusCode, string(body)
	}

	wantStatus, wantBody := rawResponse(t, keyErrs[0].err)
	for _, c := range keyErrs[1:] {
		status, body := rawResponse(t, c.err)
		if status != wantStatus {
			t.Errorf("%s: status = %d, want %d (identical to %s)", c.name, status, wantStatus, keyErrs[0].name)
		}
		if body != wantBody {
			t.Errorf("%s: body = %q, want %q (byte-identical to %s)", c.name, body, wantBody, keyErrs[0].name)
		}
	}
}

// On success the handler forwards the typed request to the seam and marshals the typed result
// into the documented response shape.
func TestRedeemHandlerSuccessShape(t *testing.T) {
	exp := time.Date(2031, 1, 2, 3, 4, 5, 0, time.UTC)
	fake := &fakeRedeemer{result: RedeemResult{
		Username:     "neo",
		DeviceSecret: "c2VjcmV0",
		PlanTier:     "premium",
		ExpiresAt:    &exp,
	}}
	app := redeemApp(fake)

	status, parsed := postRedeem(t, app, `{"license_key":"KEY-123","username":"neo","password":"pw"}`)
	if status != 200 {
		t.Fatalf("status = %d, want 200", status)
	}
	if fake.gotReq.RawKey != "KEY-123" || fake.gotReq.Username != "neo" || fake.gotReq.Password != "pw" {
		t.Errorf("seam got %+v, want RawKey=KEY-123 Username=neo Password=pw", fake.gotReq)
	}
	if parsed["status"] != "redeemed" {
		t.Errorf("status field = %v, want redeemed", parsed["status"])
	}
	if parsed["username"] != "neo" || parsed["device_secret"] != "c2VjcmV0" || parsed["plan_tier"] != "premium" {
		t.Errorf("response body = %+v, want neo/c2VjcmV0/premium", parsed)
	}
	if parsed["expires_at"] == nil {
		t.Errorf("expires_at missing, want %v", exp)
	}
}

// computeNewExpiry is the redemption's lifetime rule. It must keep lifetime licenses lifetime,
// treat duration_days<=0 as lifetime, extend from now for a fresh/expired license, and stack
// onto an existing future expiry.
func TestComputeNewExpiry(t *testing.T) {
	now := time.Now()
	future := now.AddDate(0, 0, 10)
	past := now.AddDate(0, 0, -10)

	// Existing lifetime (hasExisting, nil expiry) stays lifetime regardless of duration.
	if got := computeNewExpiry(true, nil, 30); got != nil {
		t.Errorf("existing lifetime: got %v, want nil", got)
	}

	// duration_days <= 0 means lifetime even with no existing license.
	if got := computeNewExpiry(false, nil, 0); got != nil {
		t.Errorf("zero duration: got %v, want nil", got)
	}
	if got := computeNewExpiry(false, nil, -5); got != nil {
		t.Errorf("negative duration: got %v, want nil", got)
	}

	// Fresh license: extend from now.
	got := computeNewExpiry(false, nil, 30)
	if got == nil {
		t.Fatal("fresh 30d: got nil, want a date")
	}
	if want := now.AddDate(0, 0, 30); got.Sub(want) > time.Minute || want.Sub(*got) > time.Minute {
		t.Errorf("fresh 30d: got %v, want ~%v", got, want)
	}

	// Existing future expiry: stack onto it, not onto now.
	got = computeNewExpiry(true, &future, 30)
	if got == nil {
		t.Fatal("stack onto future: got nil")
	}
	if want := future.AddDate(0, 0, 30); got.Sub(want) > time.Minute || want.Sub(*got) > time.Minute {
		t.Errorf("stack onto future: got %v, want ~%v", got, want)
	}

	// Existing but already expired: base off now, not the past expiry.
	got = computeNewExpiry(true, &past, 30)
	if got == nil {
		t.Fatal("expired existing: got nil")
	}
	if want := now.AddDate(0, 0, 30); got.Sub(want) > time.Minute || want.Sub(*got) > time.Minute {
		t.Errorf("expired existing: got %v, want ~%v", got, want)
	}
}
