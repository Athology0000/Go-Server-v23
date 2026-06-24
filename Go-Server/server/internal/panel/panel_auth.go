package panel

import (
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/db"
	"github.com/phantom/server/internal/middleware"
)

// rejectIfBlocked writes the 403 ban/suspension response when the account's
// status forbids access and reports whether it fired. The rule is centralised
// in db.AccountStatusActive so it cannot drift from the game session gate
// (middleware.SessionAuth) or /auth/heartbeat. Extracted from PanelAuth so the
// gate's contract (status code + error body) is unit-testable without a
// database, mirroring middleware.rejectForIPMismatch. The error/message shape
// matches the game middleware (account_<status>).
func rejectIfBlocked(c *fiber.Ctx, accountStatus string) bool {
	if db.AccountStatusActive(accountStatus) {
		return false
	}
	_ = c.Status(403).JSON(fiber.Map{
		"error":   "account_" + accountStatus,
		"message": "Account " + accountStatus,
	})
	return true
}

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
		// A banned/suspended account must lose panel access immediately, even with
		// a still-valid 30-day token. The token state alone is not enough.
		if rejectIfBlocked(c, sess.AccountStatus) {
			return nil
		}
		c.Locals("panel_session", sess)
		return c.Next()
	}
}

func GetPanelSession(c *fiber.Ctx) *db.PanelSession {
	s, _ := c.Locals("panel_session").(*db.PanelSession)
	return s
}
