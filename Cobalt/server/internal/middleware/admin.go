package middleware

import (
	"strings"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/cobalt/server/internal/crypto"
	"github.com/cobalt/server/internal/db"
)

var roleRank = map[string]int{"viewer": 1, "support": 2, "super_admin": 3}

func AdminAuth(pool *pgxpool.Pool, minRole string) fiber.Handler {
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
		token, err := db.GetAdminTokenByHash(c.Context(), pool, hash)
		if err != nil || token.Revoked || time.Now().After(token.ExpiresAt) {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}
		if roleRank[token.Role] < roleRank[minRole] {
			return c.Status(403).JSON(fiber.Map{"error": "not_authorized"})
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
