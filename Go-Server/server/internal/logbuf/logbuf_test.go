package logbuf

import (
	"strings"
	"testing"
)

// logbuf.Global is mirrored to GET /admin/server-logs, so any secret that reaches
// a log line is readable over HTTP by a super_admin (or anyone with the log
// pipeline). redactSecrets is the buffer-side scrub that keeps the network-served
// ring from ever holding a plaintext bearer/session token: it blanks the values
// of known secret JSON fields and Authorization: Bearer tokens, while leaving
// non-secret diagnostics (IDs, hashes, IPs) intact.
func TestRedactSecrets(t *testing.T) {
	cases := []struct {
		name     string
		in       string
		wantHas  []string // substrings that must remain
		wantGone []string // substrings (the secrets) that must be gone
	}{
		{
			name:     "session_token JSON value",
			in:       `body={"session_token":"AbC-123_xyz=","minecraft_username":"steve"}`,
			wantHas:  []string{"session_token", "[REDACTED]", "minecraft_username"},
			wantGone: []string{"AbC-123_xyz="},
		},
		{
			name:     "token and proof fields",
			in:       `{"token":"raw-tok_en=","proof":"deadbeefcafe"}`,
			wantHas:  []string{"[REDACTED]"},
			wantGone: []string{"raw-tok_en=", "deadbeefcafe"},
		},
		{
			name:     "Authorization Bearer header",
			in:       `req Authorization: Bearer eyJ.abc-123_DEF=/x then more`,
			wantHas:  []string{"Bearer [REDACTED]", "then more"},
			wantGone: []string{"eyJ.abc-123_DEF=/x"},
		},
		{
			name:     "non-secret diagnostics untouched",
			in:       `[auth] account_id=11111111-2222 token_hash=abcdef0123 ip=1.2.3.4 status=banned`,
			wantHas:  []string{"account_id=11111111-2222", "token_hash=abcdef0123", "ip=1.2.3.4", "status=banned"},
			wantGone: []string{"[REDACTED]"},
		},
	}
	for _, c := range cases {
		t.Run(c.name, func(t *testing.T) {
			got := redactSecrets(c.in)
			for _, s := range c.wantHas {
				if !strings.Contains(got, s) {
					t.Errorf("redactSecrets(%q) = %q; want it to contain %q", c.in, got, s)
				}
			}
			for _, s := range c.wantGone {
				if strings.Contains(got, s) {
					t.Errorf("redactSecrets(%q) = %q; secret %q must be redacted", c.in, got, s)
				}
			}
		})
	}
}

// The buffer must store the redacted form, since Lines() is what the HTTP handler
// serves.
func TestBufferWriteRedacts(t *testing.T) {
	b := &Buffer{}
	if _, err := b.Write([]byte(`[auth.verify_session.route] body={"session_token":"SUPERSECRETTOKEN"}` + "\n")); err != nil {
		t.Fatalf("Write: %v", err)
	}
	lines, _ := b.Lines(0)
	joined := strings.Join(lines, "\n")
	if strings.Contains(joined, "SUPERSECRETTOKEN") {
		t.Fatalf("buffer retained the plaintext token: %q", joined)
	}
	if !strings.Contains(joined, "[REDACTED]") {
		t.Fatalf("buffer did not redact the token: %q", joined)
	}
}
