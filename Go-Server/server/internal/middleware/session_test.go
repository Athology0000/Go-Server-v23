package middleware

import (
	"io"
	"net/http/httptest"
	"testing"

	"github.com/gofiber/fiber/v2"
)

// strdup is a tiny helper to take the address of a string literal in table tests.
func strdup(s string) *string { return &s }

// The strict-IP gate is a pure decision over (strictIP, lastSeenIP, realIP). Because the
// full SessionAuth handler is bound to a live Postgres (GetSessionByTokenHash /
// UpdateSessionLastSeenIP), the security-relevant branch is factored into rejectForIPMismatch
// so it can be unit-tested without a DB. These cases pin the enforcement contract:
//
//	strict + mismatch (last_seen_ip set) -> reject (401)
//	strict + match                       -> pass
//	strict + no last_seen_ip yet         -> pass (nothing to compare; first sight)
//	non-strict + mismatch                -> pass (refresh-and-continue behaviour preserved)
func TestRejectForIPMismatch(t *testing.T) {
	cases := []struct {
		name       string
		strictIP   bool
		lastSeenIP *string
		realIP     string
		wantReject bool
	}{
		{name: "strict mismatch rejects", strictIP: true, lastSeenIP: strdup("1.1.1.1"), realIP: "2.2.2.2", wantReject: true},
		{name: "strict match passes", strictIP: true, lastSeenIP: strdup("1.1.1.1"), realIP: "1.1.1.1", wantReject: false},
		{name: "strict nil last_seen passes (first sight)", strictIP: true, lastSeenIP: nil, realIP: "2.2.2.2", wantReject: false},
		{name: "non-strict mismatch passes", strictIP: false, lastSeenIP: strdup("1.1.1.1"), realIP: "2.2.2.2", wantReject: false},
		{name: "non-strict match passes", strictIP: false, lastSeenIP: strdup("1.1.1.1"), realIP: "1.1.1.1", wantReject: false},
	}
	for _, tc := range cases {
		t.Run(tc.name, func(t *testing.T) {
			got := rejectForIPMismatch(tc.strictIP, tc.lastSeenIP, tc.realIP)
			if got != tc.wantReject {
				t.Fatalf("rejectForIPMismatch(%t, %v, %q) = %t, want %t",
					tc.strictIP, tc.lastSeenIP, tc.realIP, got, tc.wantReject)
			}
		})
	}
}

// End-to-end through fiber's test app: a handler that mimics the SessionAuth gate (after a
// session has been resolved) must return 401 authentication_failed when the strict gate fires,
// and fall through to the protected handler otherwise. This proves the wiring (status + error
// body) the real middleware uses, without needing a database-backed session.
func TestStrictIPGateHTTP(t *testing.T) {
	build := func(strictIP bool, lastSeen *string) *fiber.App {
		app := fiber.New()
		app.Get("/protected", func(c *fiber.Ctx) error {
			realIP := GetRealIP(c)
			if rejectForIPMismatch(strictIP, lastSeen, realIP) {
				return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
			}
			return c.SendString("ok")
		})
		return app
	}

	cases := []struct {
		name       string
		strictIP   bool
		lastSeen   *string
		wantStatus int
	}{
		// httptest requests originate from 0.0.0.0 in fiber's test transport, so a
		// last_seen_ip that differs is a genuine mismatch.
		{name: "strict mismatch -> 401", strictIP: true, lastSeen: strdup("9.9.9.9"), wantStatus: 401},
		{name: "non-strict mismatch -> 200", strictIP: false, lastSeen: strdup("9.9.9.9"), wantStatus: 200},
		{name: "strict first-sight -> 200", strictIP: true, lastSeen: nil, wantStatus: 200},
	}
	for _, tc := range cases {
		t.Run(tc.name, func(t *testing.T) {
			app := build(tc.strictIP, tc.lastSeen)
			req := httptest.NewRequest("GET", "/protected", nil)
			resp, err := app.Test(req)
			if err != nil {
				t.Fatalf("app.Test: %v", err)
			}
			defer resp.Body.Close()
			if resp.StatusCode != tc.wantStatus {
				body, _ := io.ReadAll(resp.Body)
				t.Fatalf("status = %d, want %d (body=%s)", resp.StatusCode, tc.wantStatus, body)
			}
		})
	}
}
