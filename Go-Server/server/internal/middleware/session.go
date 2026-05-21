package middleware

import (
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/db"
)

// SessionAuth validates the bearer token. When strictIP is true, the request
// source IP must match the IP recorded at session creation (auth finish IP).
func SessionAuth(pool *pgxpool.Pool, strictIP bool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		raw, err := ParseBearerToken(c.Get("Authorization"))
		if err != nil {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}
		hash, err := crypto.HashToken(raw)
		if err != nil {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}
		session, err := db.GetSessionByTokenHash(c.Context(), pool, hash)
		if err != nil || session.Revoked || time.Now().After(session.ExpiresAt) {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}
		if strictIP && (session.LastSeenIP == nil || *session.LastSeenIP != GetRealIP(c)) {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}
		c.Locals("session", session)
		return c.Next()
	}
}

func GetSession(c *fiber.Ctx) *db.Session {
	s, _ := c.Locals("session").(*db.Session)
	return s
}
