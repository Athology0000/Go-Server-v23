package enrollment

import (
	"errors"

	"github.com/gofiber/fiber/v2"
	"github.com/cobalt/server/internal/middleware"
)

type redeemRequest struct {
	LicenseKey string `json:"license_key"`
	AccountID  string `json:"account_id"`
}

type handshakeRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
	HWID     string `json:"hwid"`
}

func RegisterRoutes(app *fiber.App, svc *Service) {
	app.Post("/enroll/redeem", handleRedeem(svc))
	app.Post("/enroll/handshake", handleHandshake(svc))
}

func handleRedeem(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req redeemRequest
		if err := c.BodyParser(&req); err != nil || req.LicenseKey == "" || req.AccountID == "" {
			return c.Status(400).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		ip := middleware.GetRealIP(c)
		err := svc.Redeem(c.Context(), req.LicenseKey, req.AccountID, ip)
		if errors.Is(err, ErrKeyNotFound) || errors.Is(err, ErrKeyNotAvailable) {
			return c.Status(400).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		return c.Status(200).JSON(fiber.Map{"status": "redeemed"})
	}
}

func handleHandshake(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req handshakeRequest
		if err := c.BodyParser(&req); err != nil || req.Username == "" || req.Password == "" || req.HWID == "" {
			return c.Status(400).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		ip := middleware.GetRealIP(c)
		secret, err := svc.Handshake(c.Context(), req.Username, req.Password, req.HWID, ip)
		if errors.Is(err, ErrIPMismatch) || errors.Is(err, ErrBadCredentials) {
			return c.Status(401).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		return c.Status(200).JSON(fiber.Map{"device_secret": secret})
	}
}
