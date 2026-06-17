package middleware

import (
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/db"
)

var roleRank = map[string]int{"viewer": 1, "support": 2, "super_admin": 3}

func AdminAuth(pool *pgxpool.Pool, minRole string) fiber.Handler {
	return func(c *fiber.Ctx) error {
		raw, err := ParseBearerToken(c.Get("Authorization"))
		if err != nil {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}
		hash, err := tokenIssuer.HashPresented(raw)
		if err != nil {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}
		token, err := db.GetAdminTokenByHash(c.Context(), pool, hash)
		if err != nil || token.Revoked || time.Now().After(token.ExpiresAt) {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}
		if roleRank[token.Role] < roleRank[minRole] {
			return c.Status(403).JSON(fiber.Map{"error": "not_authorized", "message": "Not authorized"})
		}
		go db.TouchAdminToken(c.Context(), pool, token.ID)
		c.Locals("admin_token", token)
		return c.Next()
	}
}

func GetAdminToken(c *fiber.Ctx) *db.AdminToken {
	t, _ := c.Locals("admin_token").(*db.AdminToken)
	return t
}
