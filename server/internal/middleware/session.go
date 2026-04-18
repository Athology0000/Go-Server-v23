package middleware

import (
	"strings"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/cobalt/server/internal/crypto"
	"github.com/cobalt/server/internal/db"
)

func SessionAuth(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		header := c.Get("Authorization")
		if !strings.HasPrefix(header, "Bearer ") {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}
		raw := strings.TrimPrefix(header, "Bearer ")
		hash, err := crypto.HashToken(raw)
		if err != nil {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}
		session, err := db.GetSessionByTokenHash(c.Context(), pool, hash)
		if err != nil || session.Revoked || time.Now().After(session.ExpiresAt) {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}
		c.Locals("session", session)
		return c.Next()
	}
}

func GetSession(c *fiber.Ctx) *db.Session {
	s, _ := c.Locals("session").(*db.Session)
	return s
}
