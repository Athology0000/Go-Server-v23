package content

import (
	"errors"
	"log"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/cache"
	"github.com/phantom/server/internal/middleware"
)

func RegisterRoutes(app *fiber.App, svc *Service, pool *pgxpool.Pool, store *cache.Store, strictIP bool) {
	sessAuth := middleware.SessionAuth(pool, strictIP)

	// 30 fetches per minute per IP for content endpoints.
	contentLimit := middleware.RateLimit(store, 30, time.Minute, middleware.IPKey("content"))

	manifest := handleManifest(svc)
	module := handleModule(svc)
	native := handleNative(svc)

	app.Get("/content/manifest/:id", sessAuth, contentLimit, manifest)
	app.Get("/content/module/:name", sessAuth, contentLimit, module)
	app.Get("/content/native/:name", sessAuth, contentLimit, native)

	// Compatibility for older bootstrappers that sent double-slash API paths.
	app.Get("//content/manifest/:id", sessAuth, contentLimit, manifest)
	app.Get("//content/module/:name", sessAuth, contentLimit, module)
	app.Get("//content/native/:name", sessAuth, contentLimit, native)

	log.Printf("[content.routes] registered routes: /content/manifest/:id, /content/module/:name, /content/native/:name")
}

func handleManifest(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		sess := middleware.GetSession(c)
		manifestID := c.Params("id")
		ip := middleware.GetRealIP(c)

		log.Printf("[content.manifest] request ip=%s account_id=%s manifest_id=%s",
			ip,
			sess.AccountID,
			manifestID,
		)

		m, err := svc.GetManifest(c.Context(), sess.AccountID, manifestID)

		if errors.Is(err, ErrNotEntitled) {
			log.Printf("[content.manifest] denied ip=%s account_id=%s manifest_id=%s reason=not_entitled",
				ip,
				sess.AccountID,
				manifestID,
			)

			return c.Status(403).JSON(fiber.Map{
				"error": "not_entitled",
			})
		}

		if errors.Is(err, ErrExpired) {
			log.Printf("[content.manifest] expired ip=%s account_id=%s manifest_id=%s",
				ip,
				sess.AccountID,
				manifestID,
			)

			return c.Status(410).JSON(fiber.Map{
				"error": "manifest_expired",
			})
		}

		if errors.Is(err, ErrNotFound) {
			log.Printf("[content.manifest] not_found ip=%s account_id=%s manifest_id=%s",
				ip,
				sess.AccountID,
				manifestID,
			)

			return c.Status(404).JSON(fiber.Map{
				"error": "not_found",
			})
		}

		if err != nil {
			log.Printf("[content.manifest] internal_error ip=%s account_id=%s manifest_id=%s err=%v",
				ip,
				sess.AccountID,
				manifestID,
				err,
			)

			return c.Status(500).JSON(fiber.Map{
				"error": "internal_error",
			})
		}

		log.Printf("[content.manifest] success ip=%s account_id=%s manifest_id=%s",
			ip,
			sess.AccountID,
			manifestID,
		)

		return c.JSON(m)
	}
}

func handleModule(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		sess := middleware.GetSession(c)
		moduleName := c.Params("name")
		ip := middleware.GetRealIP(c)

		log.Printf("[content.module] request ip=%s account_id=%s module=%s",
			ip,
			sess.AccountID,
			moduleName,
		)

		bytecode, fileName, err := svc.ModuleBytes(c.Context(), sess.AccountID, moduleName)

		if errors.Is(err, ErrNotEntitled) {
			log.Printf("[content.module] denied ip=%s account_id=%s module=%s reason=not_entitled",
				ip,
				sess.AccountID,
				moduleName,
			)

			return c.Status(403).JSON(fiber.Map{
				"error": "not_entitled",
			})
		}

		if errors.Is(err, ErrNotFound) {
			log.Printf("[content.module] not_found ip=%s account_id=%s module=%s",
				ip,
				sess.AccountID,
				moduleName,
			)

			return c.Status(404).JSON(fiber.Map{
				"error": "not_found",
			})
		}

		if err != nil {
			log.Printf("[content.module] internal_error ip=%s account_id=%s module=%s err=%v",
				ip,
				sess.AccountID,
				moduleName,
				err,
			)

			return c.Status(500).JSON(fiber.Map{
				"error": "internal_error",
			})
		}

		log.Printf("[content.module] success ip=%s account_id=%s module=%s path=%s",
			ip,
			sess.AccountID,
			moduleName,
			fileName,
		)

		c.Set("Content-Type", "application/java-archive")
		c.Set("Content-Disposition", `attachment; filename="`+fileName+`"`)
		return c.Send(bytecode)
	}
}

func handleNative(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		sess := middleware.GetSession(c)
		nativeName := c.Params("name")
		ip := middleware.GetRealIP(c)

		log.Printf("[content.native] request ip=%s account_id=%s native=%s",
			ip,
			sess.AccountID,
			nativeName,
		)

		path, err := svc.NativePath(c.Context(), sess.AccountID, nativeName)

		if errors.Is(err, ErrNotEntitled) {
			log.Printf("[content.native] denied ip=%s account_id=%s native=%s reason=not_entitled",
				ip,
				sess.AccountID,
				nativeName,
			)

			return c.Status(403).JSON(fiber.Map{
				"error": "not_entitled",
			})
		}

		if errors.Is(err, ErrNotFound) {
			log.Printf("[content.native] not_found ip=%s account_id=%s native=%s",
				ip,
				sess.AccountID,
				nativeName,
			)

			return c.Status(404).JSON(fiber.Map{
				"error": "not_found",
			})
		}

		if err != nil {
			log.Printf("[content.native] internal_error ip=%s account_id=%s native=%s err=%v",
				ip,
				sess.AccountID,
				nativeName,
				err,
			)

			return c.Status(500).JSON(fiber.Map{
				"error": "internal_error",
			})
		}

		log.Printf("[content.native] success ip=%s account_id=%s native=%s path=%s",
			ip,
			sess.AccountID,
			nativeName,
			path,
		)

		return c.SendFile(path)
	}
}
