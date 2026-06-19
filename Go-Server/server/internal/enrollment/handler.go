package enrollment

import (
	"errors"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/middleware"
)

// redeemRequest binds redemption to a credential proof. The account redeemed onto is derived
// server-side from the verified username/password — a body-supplied account_id is no longer
// accepted (issue #1: it let a caller redeem onto, and read the device_secret of, an account
// they did not own). The bootstrapper already collects username/password for this step (the
// sibling /enroll/handshake path uses the same credentials).
type redeemRequest struct {
	LicenseKey string `json:"license_key"`
	Username   string `json:"username"`
	Password   string `json:"password"`
}

type handshakeRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

func RegisterRoutes(app *fiber.App, svc *Service, redemption Redeemer, pool *pgxpool.Pool) {
	// 5 attempts per minute per IP — enrollment is a one-time operation
	enrollLimit := middleware.RateLimit(pool, 5, time.Minute, middleware.IPKey("enroll"))

	redeem := handleRedeem(redemption)
	handshake := handleHandshake(svc)

	app.Post("/enroll/redeem", enrollLimit, redeem)
	app.Post("/enroll/handshake", enrollLimit, handshake)

	// Compatibility for older bootstrappers that joined a trailing-slash base URL
	// with slash-prefixed API paths and sent requests like //enroll/handshake.
	app.Post("//enroll/redeem", enrollLimit, redeem)
	app.Post("//enroll/handshake", enrollLimit, handshake)
}

func handleRedeem(redemption Redeemer) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req redeemRequest
		if err := c.BodyParser(&req); err != nil || req.LicenseKey == "" || req.Username == "" || req.Password == "" {
			return c.Status(400).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		ip := middleware.GetRealIP(c)

		res, err := redemption.Redeem(c.Context(), RedeemRequest{
			RawKey:   req.LicenseKey,
			Username: req.Username,
			Password: req.Password,
			SourceIP: ip,
		})
		// All key-related failures collapse to one opaque response with no reason. Returning a
		// per-state reason (key_not_found / key_not_available / already_enrolled / key_invalid)
		// was a key-existence enumeration oracle (issue #6): it told an attacker which guessed
		// keys exist and their state. The fine-grained distinction is kept server-side in
		// audit.Log inside the redemption transaction; the client gets nothing to distinguish on.
		if errors.Is(err, ErrKeyNotFound) || errors.Is(err, ErrKeyNotAvailable) || errors.Is(err, ErrAlreadyEnrolled) {
			return c.Status(400).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		if errors.Is(err, ErrBadCredentials) || errors.Is(err, ErrIPMismatch) {
			return c.Status(401).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		return c.Status(200).JSON(fiber.Map{
			"status":        "redeemed",
			"username":      res.Username,
			"device_secret": res.DeviceSecret,
			"plan_tier":     res.PlanTier,
			"expires_at":    res.ExpiresAt,
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
