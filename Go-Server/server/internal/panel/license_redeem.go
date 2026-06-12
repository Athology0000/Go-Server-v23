package panel

import (
	"crypto/rand"
	"errors"
	"strings"
	"time"

	"github.com/phantom/server/internal/audit"
	"github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/middleware"
	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/redis/go-redis/v9"
)

func RegisterRoutes(app *fiber.App, pool *pgxpool.Pool, rdb *redis.Client, auditSvc *audit.Service, masterKey []byte) {
	// Keep this fairly low; users should rarely be redeeming more than a couple keys.
	redeemLimit := middleware.RateLimit(rdb, 5, time.Minute, middleware.IPKey("panel-redeem"))
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
		now := time.Now()

		tx, err := pool.Begin(c.Context())
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error", "message": "Failed to start transaction"})
		}
		defer tx.Rollback(c.Context())

		// Lock the key row so concurrent redeems can't double-spend it.
		keyHash := crypto.HashLicenseKey(req.Key)
		var planTier string
		var durationDays int
		var status string
		if err := tx.QueryRow(c.Context(),
			`SELECT plan_tier, duration_days, status
			 FROM license_keys
			 WHERE key_hash = $1
			 FOR UPDATE`,
			keyHash,
		).Scan(&planTier, &durationDays, &status); err != nil {
			if errors.Is(err, pgx.ErrNoRows) {
				return c.Status(400).JSON(fiber.Map{"error": "invalid_key", "message": "Invalid key"})
			}
			return c.Status(500).JSON(fiber.Map{"error": "internal_error", "message": "Failed to validate key"})
		}
		if status != "available" {
			return c.Status(400).JSON(fiber.Map{"error": "key_unavailable", "message": "Key already used or revoked"})
		}

		// Ensure the account has an enrollment device secret.
		// If a device already exists, we keep it (panel expects a single-device model right now).
		var hasDevice bool
		if err := tx.QueryRow(c.Context(), `SELECT EXISTS(SELECT 1 FROM devices WHERE account_id = $1)`, sess.AccountID).
			Scan(&hasDevice); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error", "message": "Failed to check device status"})
		}
		if !hasDevice {
			secret := make([]byte, 32)
			if _, err := rand.Read(secret); err != nil {
				return c.Status(500).JSON(fiber.Map{"error": "internal_error", "message": "Failed to generate device secret"})
			}
			encrypted, err := crypto.EncryptAESGCM(masterKey, secret)
			if err != nil {
				return c.Status(500).JSON(fiber.Map{"error": "internal_error", "message": "Failed to encrypt device secret"})
			}
			if _, err := tx.Exec(c.Context(),
				`INSERT INTO devices (account_id, enrollment_ip, device_secret_encrypted)
				 VALUES ($1, $2, $3)`,
				sess.AccountID, ip, encrypted,
			); err != nil {
				return c.Status(500).JSON(fiber.Map{"error": "internal_error", "message": "Failed to create device"})
			}
		}

		// Create or extend the license on the account.
		var existingExpires *time.Time
		licenseErr := tx.QueryRow(c.Context(),
			`SELECT expires_at
			 FROM licenses
			 WHERE account_id = $1
			 FOR UPDATE`,
			sess.AccountID,
		).Scan(&existingExpires)
		if licenseErr != nil && !errors.Is(licenseErr, pgx.ErrNoRows) {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error", "message": "Failed to load existing license"})
		}

		// duration_days <= 0 means "never expires" (lifetime).
		var newExpires *time.Time
		if licenseErr == nil && existingExpires == nil {
			// Existing lifetime license: never downgrade.
			newExpires = nil
		} else if durationDays <= 0 {
			newExpires = nil
		} else {
			base := now
			if existingExpires != nil && existingExpires.After(now) {
				base = *existingExpires
			}
			t := base.AddDate(0, 0, durationDays)
			newExpires = &t
		}

		if errors.Is(licenseErr, pgx.ErrNoRows) {
			if _, err := tx.Exec(c.Context(),
				`INSERT INTO licenses (account_id, plan_tier, starts_at, expires_at)
				 VALUES ($1, $2, $3, $4)`,
				sess.AccountID, planTier, now, newExpires,
			); err != nil {
				return c.Status(500).JSON(fiber.Map{"error": "internal_error", "message": "Failed to create license"})
			}
		} else {
			if _, err := tx.Exec(c.Context(),
				`UPDATE licenses
				 SET plan_tier = $1, status = 'active', expires_at = $2, updated_at = now()
				 WHERE account_id = $3`,
				planTier, newExpires, sess.AccountID,
			); err != nil {
				return c.Status(500).JSON(fiber.Map{"error": "internal_error", "message": "Failed to update license"})
			}
		}

		// Mark the key as redeemed.
		if tag, err := tx.Exec(c.Context(),
			`UPDATE license_keys
			 SET status = 'redeemed', redeemed_by = $1, redeemed_at = now(), enrollment_ip = $2
			 WHERE key_hash = $3 AND status = 'available'`,
			sess.AccountID, ip, keyHash,
		); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error", "message": "Failed to redeem key"})
		} else if tag.RowsAffected() != 1 {
			return c.Status(400).JSON(fiber.Map{"error": "key_unavailable", "message": "Key already used or revoked"})
		}

		if auditSvc != nil {
			auditSvc.Log("panel.key.redeem.success", &sess.AccountID, nil, nil, &ip,
				map[string]any{"plan_tier": planTier, "duration_days": durationDays, "expires_at": newExpires})
		}

		if err := tx.Commit(c.Context()); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error", "message": "Failed to commit redemption"})
		}

		return c.JSON(fiber.Map{
			"success": true,
			"plan":    planTier,
			"expiry":  newExpires,
		})
	}
}
