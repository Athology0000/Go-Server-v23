package middleware

import (
	"fmt"
	"net"
	"strings"

	"github.com/gofiber/fiber/v2"
)

// ParseTrustedCIDRs parses operator-supplied trusted-proxy CIDRs into IPNets.
// It fails on any malformed entry rather than silently dropping it: a smaller
// trusted set would change which X-Forwarded-For hops get peeled and could let a
// spoofed address resolve as the client.
func ParseTrustedCIDRs(cidrs []string) ([]*net.IPNet, error) {
	out := make([]*net.IPNet, 0, len(cidrs))
	for _, c := range cidrs {
		_, n, err := net.ParseCIDR(strings.TrimSpace(c))
		if err != nil {
			return nil, fmt.Errorf("trusted proxy CIDR %q: %w", c, err)
		}
		out = append(out, n)
	}
	return out, nil
}

func ipInRanges(ip net.IP, ranges []*net.IPNet) bool {
	for _, r := range ranges {
		if r.Contains(ip) {
			return true
		}
	}
	return false
}

// resolveClientIP returns the real client IP using the rightmost-untrusted rule.
//
// peer is the actual TCP peer (fasthttp RemoteIP) — unspoofable, since it is the
// socket the request arrived on. If the peer is not one of our trusted proxies,
// the connection is direct and the peer IS the client, so X-Forwarded-For is
// ignored entirely (a direct internet client cannot spoof its IP via a header).
//
// If the peer IS a trusted proxy (e.g. Railway's CGNAT mesh), we walk the
// X-Forwarded-For chain from the RIGHT — the end closest to us, written by our
// trusted edge — and return the first address that is not itself a trusted proxy.
// That is the real client our edge recorded. Any addresses a client prepended sit
// to the LEFT of it and are never reached, so an inbound XFF prefix cannot change
// the result. Falls back to the peer when every XFF entry is trusted or empty.
func resolveClientIP(peer, xffHeader string, trusted []*net.IPNet) string {
	peerIP := net.ParseIP(strings.TrimSpace(peer))
	if peerIP == nil || !ipInRanges(peerIP, trusted) {
		return peer
	}
	parts := strings.Split(xffHeader, ",")
	for i := len(parts) - 1; i >= 0; i-- {
		s := strings.TrimSpace(parts[i])
		if s == "" {
			continue
		}
		ip := net.ParseIP(s)
		if ip == nil {
			continue // skip malformed entries
		}
		if !ipInRanges(ip, trusted) {
			return s
		}
	}
	return peer
}

// RealIP resolves the client IP once per request (rightmost-untrusted over
// X-Forwarded-For, peeling the supplied trusted-proxy ranges) and stores it in
// request locals. Every IP consumer (rate-limit keys, the auth challenge IP-pin,
// STRICT_SESSION_IP, audit source_ip) reads it via GetRealIP, so the resolution
// rule lives in exactly one place and is not the leftmost, client-settable XFF
// value that Fiber's c.IP() returns under a trusted proxy.
func RealIP(trusted []*net.IPNet) fiber.Handler {
	return func(c *fiber.Ctx) error {
		peer := c.Context().RemoteIP().String()
		c.Locals("realip", resolveClientIP(peer, c.Get(fiber.HeaderXForwardedFor), trusted))
		return c.Next()
	}
}

func GetRealIP(c *fiber.Ctx) string {
	if ip, ok := c.Locals("realip").(string); ok {
		return ip
	}
	// Fallback only when RealIP middleware did not run: the raw TCP peer, never
	// the spoofable header.
	return c.Context().RemoteIP().String()
}
