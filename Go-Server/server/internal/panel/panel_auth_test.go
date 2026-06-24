package panel

import (
	"encoding/json"
	"io"
	"net/http/httptest"
	"testing"

	"github.com/gofiber/fiber/v2"
)

// The panel auth gate must reject a banned/suspended account even when its
// 30-day panel token is otherwise valid. The DB-bound resolution
// (GetPanelSessionByTokenHash) needs Postgres, so the security-relevant
// decision + response is factored into rejectIfBlocked and exercised here
// through a real fiber request cycle without a database — mirroring
// middleware.TestStrictIPGateHTTP. This pins the enforcement contract:
//
//	banned/suspended account status -> 403, body error "account_<status>"
//	active                          -> request continues to the handler
func TestRejectIfBlocked(t *testing.T) {
	app := fiber.New()
	app.Get("/p/:status", func(c *fiber.Ctx) error {
		if rejectIfBlocked(c, c.Params("status")) {
			return nil
		}
		return c.SendString("ok")
	})

	cases := []struct {
		status   string
		wantCode int
		wantErr  string // expected "error" field, or "" when the request should pass
	}{
		{"banned", 403, "account_banned"},
		{"suspended", 403, "account_suspended"},
		{"active", 200, ""},
	}

	for _, tc := range cases {
		t.Run(tc.status, func(t *testing.T) {
			resp, err := app.Test(httptest.NewRequest("GET", "/p/"+tc.status, nil))
			if err != nil {
				t.Fatalf("app.Test: %v", err)
			}
			if resp.StatusCode != tc.wantCode {
				t.Fatalf("status = %d, want %d", resp.StatusCode, tc.wantCode)
			}
			body, _ := io.ReadAll(resp.Body)
			if tc.wantErr == "" {
				if string(body) != "ok" {
					t.Fatalf("body = %q, want \"ok\"", string(body))
				}
				return
			}
			var got map[string]any
			if err := json.Unmarshal(body, &got); err != nil {
				t.Fatalf("unmarshal %q: %v", string(body), err)
			}
			if got["error"] != tc.wantErr {
				t.Fatalf("error = %v, want %q", got["error"], tc.wantErr)
			}
		})
	}
}
