package content

import (
	"errors"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/redis/go-redis/v9"
	"github.com/cobalt/server/internal/middleware"
)

func RegisterRoutes(app *fiber.App, svc *Service, pool *pgxpool.Pool, rdb *redis.Client, strictIP bool) {
	sessAuth := middleware.SessionAuth(pool, strictIP)
	// 30 fetches per minute per IP for content endpoints
	contentLimit := middleware.RateLimit(rdb, 30, time.Minute, middleware.IPKey("content"))

	app.Get("/content/manifest/:id", sessAuth, contentLimit, handleManifest(svc))
	app.Get("/content/module/:name", sessAuth, contentLimit, handleModule(svc))
	app.Get("/content/native/:name", sessAuth, contentLimit, handleNative(svc))
}

func handleManifest(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		sess := middleware.GetSession(c)
		m, err := svc.GetManifest(c.Context(), sess.AccountID, c.Params("id"))
		if errors.Is(err, ErrNotEntitled) {
			return c.Status(403).JSON(fiber.Map{"error": "not_entitled"})
		}
		if errors.Is(err, ErrExpired) {
			return c.Status(410).JSON(fiber.Map{"error": "manifest_expired"})
		}
		if errors.Is(err, ErrNotFound) {
			return c.Status(404).JSON(fiber.Map{"error": "not_found"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.JSON(m)
	}
}

func handleModule(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		sess := middleware.GetSession(c)
		path, err := svc.ModulePath(c.Context(), sess.AccountID, c.Params("name"))
		if errors.Is(err, ErrNotEntitled) {
			return c.Status(403).JSON(fiber.Map{"error": "not_entitled"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.SendFile(path)
	}
}

func handleNative(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		sess := middleware.GetSession(c)
		path, err := svc.NativePath(c.Context(), sess.AccountID, c.Params("name"))
		if errors.Is(err, ErrNotEntitled) {
			return c.Status(403).JSON(fiber.Map{"error": "not_entitled"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.SendFile(path)
	}
}
