package auth

import (
	"errors"

	"github.com/gofiber/fiber/v2"
	"github.com/cobalt/server/internal/middleware"
)

type startRequest struct {
	Username          string `json:"username"`
	HWID              string `json:"hwid"`
	MinecraftUsername string `json:"minecraft_username"`
	ClientVersion     string `json:"client_version"`
	BootstrapBuildID  string `json:"bootstrap_build_id"`
}

type finishRequest struct {
	Username          string `json:"username"`
	Proof             string `json:"proof"`
	MinecraftUsername string `json:"minecraft_username"`
}

func RegisterRoutes(app *fiber.App, svc *Service) {
	app.Post("/auth/start", handleStart(svc))
	app.Post("/auth/finish", handleFinish(svc))
}

func handleStart(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req startRequest
		if err := c.BodyParser(&req); err != nil || req.Username == "" || req.HWID == "" {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}
		ip := middleware.GetRealIP(c)
		result, err := svc.Start(c.Context(), req.Username, req.HWID, req.MinecraftUsername, ip)
		if errors.Is(err, ErrIPMismatch) || errors.Is(err, ErrHWIDMismatch) ||
			errors.Is(err, ErrNotFound) || errors.Is(err, ErrDeviceBlocked) || errors.Is(err, ErrUsernameMismatch) {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "authentication_failed"})
		}
		return c.JSON(fiber.Map{"challenge": result.Challenge, "expires_in": result.ExpiresIn})
	}
}

func handleFinish(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req finishRequest
		if err := c.BodyParser(&req); err != nil || req.Username == "" || req.Proof == "" {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}
		ip := middleware.GetRealIP(c)
		result, err := svc.Finish(c.Context(), req.Username, req.Proof, ip, req.MinecraftUsername)
		if errors.Is(err, ErrIPMismatch) || errors.Is(err, ErrBadProof) ||
			errors.Is(err, ErrNoChallenge) || errors.Is(err, ErrNotFound) {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "authentication_failed"})
		}
		if !result.Authorized {
			return c.JSON(fiber.Map{"authenticated": true, "authorized": false, "reason": result.Reason})
		}
		return c.JSON(fiber.Map{
			"authenticated":    true,
			"authorized":       true,
			"session_token":    result.SessionToken,
			"expires_in":       result.ExpiresIn,
			"plan_tier":        result.PlanTier,
			"enabled_modules":  result.Modules,
			"enabled_features": result.Features,
			"manifest_url":     result.ManifestURL,
		})
	}
}
