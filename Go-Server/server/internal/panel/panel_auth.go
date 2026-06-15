package panel

import (
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/db"
	"github.com/phantom/server/internal/middleware"
)

// PanelAuth validates the panel bearer token (panel_sessions) and stores the
// session in request locals for handlers to use.
func PanelAuth(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		raw, err := middleware.ParseBearerToken(c.Get("Authorization"))
		if err != nil {
			return c.Status(401).JSON(fiber.Map{"error": "unauthorized", "message": "Missing or invalid token"})
		}
		hash, err := crypto.HashToken(raw)
		if err != nil {
			return c.Status(401).JSON(fiber.Map{"error": "unauthorized", "message": "Invalid token"})
		}
		sess, err := db.GetPanelSessionByTokenHash(c.Context(), pool, hash)
		if err != nil || sess.Revoked || time.Now().After(sess.ExpiresAt) {
			return c.Status(401).JSON(fiber.Map{"error": "unauthorized", "message": "Session expired"})
		}
		c.Locals("panel_session", sess)
		return c.Next()
	}
}

func GetPanelSession(c *fiber.Ctx) *db.PanelSession {
	s, _ := c.Locals("panel_session").(*db.PanelSession)
	return s
}
