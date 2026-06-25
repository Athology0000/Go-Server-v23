package main

import (
	"context"
	"crypto/ed25519"
	"encoding/base64"
	"io"
	"log"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/phantom/server/internal/admin"
	"github.com/phantom/server/internal/audit"
	"github.com/phantom/server/internal/auth"
	"github.com/phantom/server/internal/cache"
	"github.com/phantom/server/internal/config"
	"github.com/phantom/server/internal/content"
	"github.com/phantom/server/internal/db"
	"github.com/phantom/server/internal/enrollment"
	"github.com/phantom/server/internal/entitlement"
	"github.com/phantom/server/internal/logbuf"
	"github.com/phantom/server/internal/middleware"
	"github.com/phantom/server/internal/panel"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/cors"
)

// trustedProxyRanges are the private/CGNAT ranges Railway's edge + internal mesh
// originate from. Fiber only honors X-Forwarded-For when the TCP peer falls in one
// of these, so an internet client connecting directly can never spoof its IP — its
// untrusted peer makes c.IP() fall back to RemoteAddr.
var trustedProxyRanges = []string{
	"100.64.0.0/10", // Railway internal CGNAT mesh (the rotating hop)
	"10.0.0.0/8",
	"172.16.0.0/12",
	"192.168.0.0/16",
	"127.0.0.1/32",
	"::1/128",
}

func main() {
	log.SetOutput(io.MultiWriter(os.Stdout, logbuf.Writer()))

	cfg, err := config.Load()
	if err != nil {
		log.Fatalf("config: %v", err)
	}

	// Parse the trusted-proxy ranges once for middleware.RealIP, which resolves
	// the client IP as the rightmost X-Forwarded-For entry that is not one of
	// these proxies (peeling Railway's hops). Fail fast on a bad CIDR rather than
	// silently shrinking the trusted set.
	trustedCIDRs, err := middleware.ParseTrustedCIDRs(trustedProxyRanges)
	if err != nil {
		log.Fatalf("trusted proxy ranges: %v", err)
	}

	log.Println("BOOTING PHANTOM SERVER VERSION: entitlement-workflow-v2-xff")
	manifestPublicKey := base64.StdEncoding.EncodeToString(
		ed25519.PrivateKey(cfg.ManifestSigningKey).Public().(ed25519.PublicKey),
	)

	ctx := context.Background()

	pool, err := db.NewPool(ctx, cfg.DBURL)
	if err != nil {
		log.Fatalf("db: %v", err)
	}
	defer pool.Close()

	if err := db.RunMigrations(ctx, pool, cfg.MigrationsDir); err != nil {
		log.Fatalf("migrate: %v", err)
	}

	// Ephemeral state (rate-limit counters + auth challenges) lives in Postgres now — no Redis
	// dependency. This background sweep reaps expired rows.
	cache.StartSweeper(ctx, pool, 5*time.Minute, cfg.AuditRetentionDays)

	entSvc := entitlement.New(pool)
	auditSvc := audit.New(pool)

	authSvc := auth.New(
		pool,
		entSvc,
		auditSvc,
		cfg.MasterKey,
		cfg.ServerPepper,
		cfg.BaseURL,
		cfg,
	)

	enrollSvc := enrollment.New(
		pool,
		auditSvc,
		cfg.MasterKey,
		cfg.ServerPepper,
	)

	redemption := enrollment.NewRedemption(
		pool,
		auditSvc,
		cfg.MasterKey,
	)

	contentSvc := content.New(
		pool,
		entSvc,
		cfg.ContentDir,
		cfg.ManifestSigningKey,
		cfg.ModuleEncryptionKey,
		cfg.BaseURL,
	)

	// Server-side per-license generation: when MODULE_WM_SECRET/MODULE_WM_PEPPER are set, the content
	// service watermarks + encrypts each license's bundles in pure Go from CONTENT_DIR/_jars on first
	// request (no JVM, no per-customer build/redeploy).
	if cfg.ModuleWmSecret != "" && cfg.ModuleWmPepper != "" {
		contentSvc.SetGeneration(cfg.ModuleWmSecret, cfg.ModuleWmPepper)
		log.Printf("[content] server-side per-license generation enabled (jars=%s/_jars)", cfg.ContentDir)
	}

	// Per-user watermarking of .jar downloads. Gated behind WATERMARK_ENABLED and a fully
	// configured obfuscator jar/config/secret; otherwise downloads stay byte-exact.
	if cfg.WatermarkEnabled {
		wm := &content.Watermarker{
			JavaPath:   cfg.WatermarkJavaPath,
			ObfJar:     cfg.WatermarkObfJar,
			ConfigPath: cfg.WatermarkConfigPath,
			Secret:     cfg.WatermarkSecret,
		}
		if wm.Enabled() {
			contentSvc.SetWatermarker(wm)
			log.Printf("[content] per-user jar watermarking enabled (obfuscator=%s)", cfg.WatermarkObfJar)
		} else {
			log.Printf("[content] WATERMARK_ENABLED=true but watermarker is not fully configured " +
				"(need WATERMARK_OBFUSCATOR_JAR, WATERMARK_CONFIG, WATERMARK_SECRET) — serving un-watermarked jars")
		}
	}

	// =========================
	// Public API server
	// =========================
	pub := fiber.New(fiber.Config{
		DisableStartupMessage: true,
		BodyLimit:             cfg.BodyLimit,
		// The security-relevant client IP is resolved by middleware.RealIP
		// (rightmost untrusted X-Forwarded-For entry, peeling trustedProxyRanges).
		// These Fiber settings still govern the OTHER forwarded headers
		// (X-Forwarded-Proto/Host via EnableTrustedProxyCheck) and make Fiber's own
		// c.IP() a validated fallback. EnableIPValidation is set so c.IP() never
		// returns the raw, unsplit header — but note c.IP() returns the LEFTMOST
		// (client-settable) entry, which is why RealIP does its own rightmost
		// resolution and is the single source GetRealIP reads.
		ProxyHeader:             fiber.HeaderXForwardedFor,
		EnableTrustedProxyCheck: true,
		EnableIPValidation:      true,
		TrustedProxies:          trustedProxyRanges,
	})

	pub.Use(middleware.RealIP(trustedCIDRs))

	pub.Use(cors.New(cors.Config{
		AllowOrigins: cfg.PublicCORSAllowOrigins,
		AllowHeaders: "Content-Type,Authorization",
		AllowMethods: "GET,POST,PUT,PATCH,DELETE,OPTIONS",
	}))

	pub.Use(middleware.RateLimit(
		pool,
		120,
		time.Minute,
		middleware.IPKey("global"),
	))

	pub.Use(middleware.SecurityHeaders())

	pub.Get("/", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{
			"ok":      true,
			"service": "phantom-public-api",
			"message": "Phantom public API is online",
			"version": "entitlement-workflow-v2-xff",
		})
	})

	pub.Get("/health", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{
			"ok":                  true,
			"service":             "phantom-public-api",
			"timestamp":           time.Now().UTC().Format(time.RFC3339),
			"version":             "entitlement-workflow-v2-xff",
			"manifest_public_key": manifestPublicKey,
		})
	})

	auth.RegisterRoutes(pub, authSvc, pool, auditSvc, cfg)
	enrollment.RegisterRoutes(pub, enrollSvc, redemption, pool)
	panel.RegisterRoutes(pub, pool, auditSvc, cfg.MasterKey)
	content.RegisterRoutes(pub, contentSvc, pool, cfg.StrictSessionIP, cfg.HeartbeatLivenessWindow)
	// Also expose the admin API on the public port so the admin panel can
	// reach it without a second Railway domain. Each /admin/* route still
	// enforces its own admin-token middleware, so this is a routing change,
	// not an auth bypass.
	admin.RegisterRoutes(pub, pool, cfg.ManifestSigningKey, auditSvc, cfg.AdminAPISecret, cfg.ContentDir)

	// =========================
	// Admin API server
	// =========================
	adm := fiber.New(fiber.Config{
		DisableStartupMessage: true,
		BodyLimit:             cfg.BodyLimit,
		// Same trusted-proxy IP resolution as the public app (see pub config above).
		ProxyHeader:             fiber.HeaderXForwardedFor,
		EnableTrustedProxyCheck: true,
		EnableIPValidation:      true,
		TrustedProxies:          trustedProxyRanges,
	})

	adm.Use(middleware.RealIP(trustedCIDRs))

	adm.Use(cors.New(cors.Config{
		AllowOrigins: cfg.AdminCORSAllowOrigins,
		AllowHeaders: "Content-Type,Authorization",
		AllowMethods: "GET,POST,PUT,PATCH,DELETE,OPTIONS",
	}))

	adm.Use(middleware.RateLimit(
		pool,
		60,
		time.Minute,
		middleware.IPKey("admin-global"),
	))

	adm.Use(middleware.SecurityHeaders())

	adm.Get("/", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{
			"ok":      true,
			"service": "phantom-admin-api",
			"message": "Phantom admin API is online",
			"version": "entitlement-workflow-v2-xff",
		})
	})

	adm.Get("/health", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{
			"ok":                  true,
			"service":             "phantom-admin-api",
			"timestamp":           time.Now().UTC().Format(time.RFC3339),
			"version":             "entitlement-workflow-v2-xff",
			"manifest_public_key": manifestPublicKey,
		})
	})

	admin.RegisterRoutes(
		adm,
		pool,
		cfg.ManifestSigningKey,
		auditSvc,
		cfg.AdminAPISecret,
		cfg.ContentDir,
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
