package panel

import (
	"errors"
	"strings"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/audit"
	"github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/enrollment"
	"github.com/phantom/server/internal/middleware"
)

func RegisterRoutes(app *fiber.App, pool *pgxpool.Pool, auditSvc *audit.Service, masterKey []byte) {
	// Keep this fairly low; users should rarely be redeeming more than a couple keys.
	redeemLimit := middleware.RateLimit(pool, 5, time.Minute, middleware.IPKey("panel-redeem"))
	app.Post("/license/redeem", redeemLimit, PanelAuth(pool), handleRedeemLicense(pool, auditSvc, masterKey))
}

type redeemRequest struct {
	Key string `json:"key"`
}

func handleRedeemLicense(pool *pgxpool.Pool, auditSvc *audit.Service, masterKey []byte) fiber.Handler {
	return func(c *fiber.Ctx) error {
		sess := GetPanelSession(c)
		if sess == nil {
			return c.Status(401).JSON(fiber.Map{"error": "unauthorized", "message": "Unauthorized"})
		}

		var req redeemRequest
		if err := c.BodyParser(&req); err != nil || strings.TrimSpace(req.Key) == "" {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request", "message": "Key required"})
		}

		ip := middleware.GetRealIP(c)

		tx, err := pool.Begin(c.Context())
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error", "message": "Failed to start transaction"})
		}
		defer tx.Rollback(c.Context())

		// The redemption rules — key lock+validate, the device unbound-guard, device create/seal,
		// license create/extend, and the double-spend-guarded key mark — are the canonical
		// enrollment redemption core (enrollment.RedeemOnTx). The panel is a thin adapter over it:
		// it owns the tx and resolves the account from the authenticated panel session (never a
		// caller-supplied id), and runs the core with BOTH enrollment-only knobs off — the panel
		// does not advance the device to hwid_pending and does not open/return the device secret.
		// Typed errors map to the panel's existing HTTP codes; behaviour is byte-identical to the
		// previous inline implementation (the 409 device_not_redeemable guard included).
		res, coreErr := enrollment.RedeemOnTx(c.Context(), tx, crypto.NewDeviceSecret(masterKey), sess.AccountID, req.Key, ip, enrollment.CoreOptions{})
		if coreErr != nil {
			switch {
			case errors.Is(coreErr, enrollment.ErrKeyNotFound):
				return c.Status(400).JSON(fiber.Map{"error": "invalid_key", "message": "Invalid key"})
			case errors.Is(coreErr, enrollment.ErrKeyNotAvailable):
				return c.Status(400).JSON(fiber.Map{"error": "key_unavailable", "message": "Key already used or revoked"})
			case errors.Is(coreErr, enrollment.ErrAlreadyEnrolled):
				return c.Status(409).JSON(fiber.Map{"error": "device_not_redeemable", "message": "Device is already enrolled"})
			default:
				return c.Status(500).JSON(fiber.Map{"error": "internal_error", "message": "Failed to redeem key"})
			}
		}

		if auditSvc != nil {
			auditSvc.Log(audit.EventPanelKeyRedeemSuccess, &sess.AccountID, nil, nil, &ip,
				map[string]any{"plan_tier": res.PlanTier, "duration_days": res.DurationDays, "expires_at": res.ExpiresAt})
		}

		if err := tx.Commit(c.Context()); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error", "message": "Failed to commit redemption"})
		}

		return c.JSON(fiber.Map{
			"success": true,
			"plan":    res.PlanTier,
			"expiry":  res.ExpiresAt,
		})
	}
}
