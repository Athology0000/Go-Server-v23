package middleware

import (
	"net"
	"testing"
)

func mustCIDRs(t *testing.T, ss ...string) []*net.IPNet {
	t.Helper()
	out := make([]*net.IPNet, 0, len(ss))
	for _, s := range ss {
		_, n, err := net.ParseCIDR(s)
		if err != nil {
			t.Fatalf("ParseCIDR(%q): %v", s, err)
		}
		out = append(out, n)
	}
	return out
}

// resolveClientIP must derive the real client IP with the rightmost-untrusted
// rule, so an attacker-supplied X-Forwarded-For PREFIX can never change the
// result. Railway's edge + internal mesh hops (100.64/10 + private ranges) are
// the trusted proxies that get peeled; the rightmost address that is NOT a
// trusted proxy is the real client our trusted edge recorded. The unspoofable
// TCP peer decides whether XFF is consulted at all.
func TestResolveClientIP(t *testing.T) {
	trusted := mustCIDRs(t,
		"100.64.0.0/10", "10.0.0.0/8", "172.16.0.0/12", "192.168.0.0/16", "127.0.0.1/32", "::1/128")

	cases := []struct {
		name string
		peer string
		xff  string
		want string
	}{
		{"direct internet client: XFF ignored entirely", "8.8.8.8", "1.2.3.4", "8.8.8.8"},
		{"direct client with a spoofed XFF chain", "203.0.113.9", "6.6.6.6, 7.7.7.7", "203.0.113.9"},
		{"trusted edge, real client appended", "100.64.1.1", "9.9.9.9", "9.9.9.9"},
		{"attacker prepends a fake IP — the prefix must NOT win", "100.64.1.1", "6.6.6.6, 9.9.9.9", "9.9.9.9"},
		{"attacker appends a trusted-looking IP — it is skipped", "100.64.1.1", "9.9.9.9, 10.0.0.5", "9.9.9.9"},
		{"all XFF entries trusted: fall back to the peer", "100.64.1.1", "10.0.0.1, 100.64.2.2", "100.64.1.1"},
		{"garbage XFF entries are skipped", "100.64.1.1", "not-an-ip, 9.9.9.9", "9.9.9.9"},
		{"empty XFF, trusted peer: the peer", "100.64.1.1", "", "100.64.1.1"},
		{"multiple trusted hops then the client", "100.64.0.5", "9.9.9.9, 100.64.0.9, 10.1.2.3", "9.9.9.9"},
	}
	for _, c := range cases {
		t.Run(c.name, func(t *testing.T) {
			if got := resolveClientIP(c.peer, c.xff, trusted); got != c.want {
				t.Fatalf("resolveClientIP(peer=%q, xff=%q) = %q, want %q", c.peer, c.xff, got, c.want)
			}
		})
	}
}

// ParseTrustedCIDRs must reject a malformed CIDR (the config is operator-supplied
// and a silent skip would shrink the trusted set, breaking IP resolution).
func TestParseTrustedCIDRs(t *testing.T) {
	got, err := ParseTrustedCIDRs([]string{"10.0.0.0/8", "::1/128"})
	if err != nil {
		t.Fatalf("unexpected err: %v", err)
	}
	if len(got) != 2 {
		t.Fatalf("len = %d, want 2", len(got))
	}
	if _, err := ParseTrustedCIDRs([]string{"not-a-cidr"}); err == nil {
		t.Fatal("expected an error for a malformed CIDR")
	}
}
