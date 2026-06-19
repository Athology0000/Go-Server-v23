package middleware

import (
	"net/http/httptest"
	"strings"
	"testing"

	"github.com/gofiber/fiber/v2"
)

// IPAndUsernameKey must normalize the username (lower-case + trim) so cased/whitespace
// variants of one username collapse to a single rate-limit bucket — otherwise an attacker
// multiplies the brute-force budget by varying the casing per request.
func TestIPAndUsernameKeyNormalizesUsername(t *testing.T) {
	keyFor := func(t *testing.T, jsonBody string) string {
		t.Helper()
		app := fiber.New()
		var got string
		app.Post("/x", func(c *fiber.Ctx) error {
			got = IPAndUsernameKey("auth")(c)
			return c.SendStatus(200)
		})
		req := httptest.NewRequest("POST", "/x", strings.NewReader(jsonBody))
		req.Header.Set("Content-Type", "application/json")
		if _, err := app.Test(req); err != nil {
			t.Fatalf("app.Test: %v", err)
		}
		return got
	}

	variants := []string{
		`{"username":"victim"}`,
		`{"username":"Victim"}`,
		`{"username":"VICTIM"}`,
		`{"username":"  victim  "}`,
	}
	want := keyFor(t, variants[0])
	if !strings.HasSuffix(want, ":victim") {
		t.Fatalf("expected key to end with normalized username, got %q", want)
	}
	for _, v := range variants[1:] {
		if got := keyFor(t, v); got != want {
			t.Errorf("variant %s -> key %q, want %q (must collapse to one bucket)", v, got, want)
		}
	}
}
