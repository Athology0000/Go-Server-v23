package main

import (
	"context"
	"io"
	"log"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/cobalt/server/internal/admin"
	"github.com/cobalt/server/internal/audit"
	"github.com/cobalt/server/internal/auth"
	"github.com/cobalt/server/internal/cache"
	"github.com/cobalt/server/internal/config"
	"github.com/cobalt/server/internal/content"
	"github.com/cobalt/server/internal/db"
	"github.com/cobalt/server/internal/enrollment"
	"github.com/cobalt/server/internal/entitlement"
	"github.com/cobalt/server/internal/logbuf"
	"github.com/cobalt/server/internal/middleware"
	"github.com/cobalt/server/internal/panel"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/cors"
)

func main() {
	log.SetOutput(io.MultiWriter(os.Stdout, logbuf.Writer()))

	cfg, err := config.Load()
	if err != nil {
		log.Fatalf("config: %v", err)
	}

	ctx := context.Background()

	pool, err := db.NewPool(ctx, cfg.DBURL)
	if err != nil {
		log.Fatalf("db: %v", err)
	}
	defer pool.Close()

	rdb, err := cache.NewClient(cfg.RedisURL)
	if err != nil {
		log.Fatalf("redis: %v", err)
	}
	defer rdb.Close()

	entSvc := entitlement.New(pool)
	auditSvc := audit.New(pool)

	authSvc := auth.New(
		pool,
		rdb,
		entSvc,
		auditSvc,
		cfg.MasterKey,
		cfg.ServerPepper,
		cfg.BaseURL,
	)

	enrollSvc := enrollment.New(
		pool,
		auditSvc,
		cfg.MasterKey,
		cfg.ServerPepper,
	)

	contentSvc := content.New(
		pool,
		entSvc,
		cfg.ContentDir,
	)

	// =========================
	// Public API server
	// =========================
	pub := fiber.New(fiber.Config{
		DisableStartupMessage: true,
		BodyLimit:             cfg.BodyLimit,
	})

	pub.Use(middleware.RealIP())

	pub.Use(cors.New(cors.Config{
		AllowOrigins: cfg.PublicCORSAllowOrigins,
		AllowHeaders: "Content-Type,Authorization",
		AllowMethods: "GET,POST,PUT,PATCH,DELETE,OPTIONS",
	}))

	pub.Use(middleware.RateLimit(
		rdb,
		120,
		time.Minute,
		middleware.IPKey("global"),
	))

	pub.Use(middleware.SecurityHeaders())

	// Root route so Railway "/" shows the server is alive.
	pub.Get("/", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{
			"ok":      true,
			"service": "cobalt-public-api",
			"message": "Cobalt public API is online",
		})
	})

	// Simple health check.
	pub.Get("/health", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{
			"ok":        true,
			"service":   "cobalt-public-api",
			"timestamp": time.Now().UTC().Format(time.RFC3339),
		})
	})

	// Debug body parser route.
	// Remove this after you confirm JSON bodies work.
	pub.Post("/debug/body", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{
			"ok":           true,
			"content_type": c.Get("Content-Type"),
			"body_len":     len(c.Body()),
			"body":         string(c.Body()),
		})
	})

	// Register public routes.
	auth.RegisterRoutes(pub, authSvc, pool, rdb, auditSvc)
	enrollment.RegisterRoutes(pub, enrollSvc, rdb)
	panel.RegisterRoutes(pub, pool, rdb, auditSvc, cfg.MasterKey)
	content.RegisterRoutes(pub, contentSvc, pool, rdb, cfg.StrictSessionIP)

	// =========================
	// Admin API server
	// =========================
	adm := fiber.New(fiber.Config{
		DisableStartupMessage: true,
		BodyLimit:             cfg.BodyLimit,
	})

	adm.Use(middleware.RealIP())

	adm.Use(cors.New(cors.Config{
		AllowOrigins: cfg.AdminCORSAllowOrigins,
		AllowHeaders: "Content-Type,Authorization",
		AllowMethods: "GET,POST,PUT,PATCH,DELETE,OPTIONS",
	}))

	adm.Use(middleware.RateLimit(
		rdb,
		60,
		time.Minute,
		middleware.IPKey("admin-global"),
	))

	adm.Use(middleware.SecurityHeaders())

	adm.Get("/", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{
			"ok":      true,
			"service": "cobalt-admin-api",
			"message": "Cobalt admin API is online",
		})
	})

	adm.Get("/health", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{
			"ok":        true,
			"service":   "cobalt-admin-api",
			"timestamp": time.Now().UTC().Format(time.RFC3339),
		})
	})

	admin.RegisterRoutes(
		adm,
		pool,
		cfg.ManifestSigningKey,
		auditSvc,
		cfg.AdminAPISecret,
	)

	// =========================
	// Start servers
	// =========================
	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)

	go func() {
		log.Printf("public listening on :%s", cfg.PublicPort)

		if err := pub.Listen(":" + cfg.PublicPort); err != nil {
			log.Printf("public server stopped: %v", err)
		}
	}()

	go func() {
		log.Printf("admin listening on :%s", cfg.AdminPort)

		if err := adm.Listen(":" + cfg.AdminPort); err != nil {
			log.Printf("admin server stopped: %v", err)
		}
	}()

	<-quit

	log.Println("shutting down")

	if err := pub.ShutdownWithTimeout(10 * time.Second); err != nil {
		log.Printf("public shutdown error: %v", err)
	}

	if err := adm.ShutdownWithTimeout(10 * time.Second); err != nil {
		log.Printf("admin shutdown error: %v", err)
	}
}
