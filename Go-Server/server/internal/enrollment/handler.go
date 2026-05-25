package enrollment

import (
	"errors"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/phantom/server/internal/middleware"
	"github.com/redis/go-redis/v9"
)

type redeemRequest struct {
	LicenseKey string `json:"license_key"`
	AccountID  string `json:"account_id"`
}

type handshakeRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

func RegisterRoutes(app *fiber.App, svc *Service, rdb *redis.Client) {
	// 5 attempts per minute per IP — enrollment is a one-time operation
	enrollLimit := middleware.RateLimit(rdb, 5, time.Minute, middleware.IPKey("enroll"))

	redeem := handleRedeem(svc)
	handshake := handleHandshake(svc)

	app.Post("/enroll/redeem", enrollLimit, redeem)
	app.Post("/enroll/handshake", enrollLimit, handshake)

	// Compatibility for older bootstrappers that joined a trailing-slash base URL
	// with slash-prefixed API paths and sent requests like //enroll/handshake.
	app.Post("//enroll/redeem", enrollLimit, redeem)
	app.Post("//enroll/handshake", enrollLimit, handshake)
}

func handleRedeem(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req redeemRequest
		if err := c.BodyParser(&req); err != nil || req.LicenseKey == "" || req.AccountID == "" {
			return c.Status(400).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		ip := middleware.GetRealIP(c)

		username, secret, planTier, expiresAt, err := svc.Redeem(c.Context(), req.LicenseKey, req.AccountID, ip)
		if errors.Is(err, ErrKeyNotFound) || errors.Is(err, ErrKeyNotAvailable) || errors.Is(err, ErrAlreadyEnrolled) {
			reason := "key_invalid"
			if errors.Is(err, ErrKeyNotFound) {
				reason = "key_not_found"
			} else if errors.Is(err, ErrKeyNotAvailable) {
				reason = "key_not_available"
			} else if errors.Is(err, ErrAlreadyEnrolled) {
				reason = "already_enrolled"
			}
			return c.Status(400).JSON(fiber.Map{"error": "enrollment_failed", "reason": reason})
		}
		if errors.Is(err, ErrBadCredentials) || errors.Is(err, ErrIPMismatch) {
			return c.Status(401).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		return c.Status(200).JSON(fiber.Map{
			"status":        "redeemed",
			"username":      username,
			"device_secret": secret,
			"plan_tier":     planTier,
			"expires_at":    expiresAt,
		})
	}
}

func handleHandshake(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req handshakeRequest
		if err := c.BodyParser(&req); err != nil || req.Username == "" || req.Password == "" {
			return c.Status(400).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		ip := middleware.GetRealIP(c)
		secret, err := svc.Handshake(c.Context(), req.Username, req.Password, ip)
		if errors.Is(err, ErrIPMismatch) || errors.Is(err, ErrBadCredentials) {
			return c.Status(401).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		return c.Status(200).JSON(fiber.Map{"device_secret": secret})
	}
}
