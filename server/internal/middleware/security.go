package middleware

import (
	"crypto/sha256"
	"errors"
	"fmt"
	"strings"

	"github.com/gofiber/fiber/v2"
)

// ParseBearerToken extracts a bearer token from an Authorization header.
// It accepts case-insensitive "Bearer" and rejects malformed values.
func ParseBearerToken(header string) (string, error) {
	if header == "" {
		return "", errors.New("missing authorization header")
	}
	parts := strings.Fields(header)
	if len(parts) != 2 || !strings.EqualFold(parts[0], "Bearer") || strings.TrimSpace(parts[1]) == "" {
		return "", errors.New("invalid authorization header")
	}
	return parts[1], nil
}

func SecurityHeaders() fiber.Handler {
	return func(c *fiber.Ctx) error {
		c.Set("Strict-Transport-Security", "max-age=63072000; includeSubDomains; preload")
		c.Set("X-Frame-Options", "DENY")
		c.Set("X-Content-Type-Options", "nosniff")
		c.Set("Referrer-Policy", "strict-origin-when-cross-origin")
		c.Set("Permissions-Policy", "interest-cohort=()")
		c.Set("X-XSS-Protection", "0")
		return c.Next()
	}
}
