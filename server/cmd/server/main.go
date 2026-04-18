package main

import (
	"context"
	"log"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/cobalt/server/internal/admin"
	"github.com/cobalt/server/internal/audit"
	"github.com/cobalt/server/internal/auth"
	"github.com/cobalt/server/internal/cache"
	"github.com/cobalt/server/internal/config"
	"github.com/cobalt/server/internal/content"
	"github.com/cobalt/server/internal/db"
	"github.com/cobalt/server/internal/enrollment"
	"github.com/cobalt/server/internal/entitlement"
	"github.com/cobalt/server/internal/middleware"
)

func main() {
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
	authSvc := auth.New(pool, rdb, entSvc, auditSvc, cfg.MasterKey, cfg.ServerPepper, cfg.BaseURL)
	enrollSvc := enrollment.New(pool, auditSvc, cfg.MasterKey, cfg.ServerPepper)
	contentSvc := content.New(pool, entSvc, cfg.ContentDir)

	// Public server
	pub := fiber.New(fiber.Config{DisableStartupMessage: true})
	pub.Use(middleware.RealIP())
	pub.Use(middleware.RateLimit(rdb, 120, time.Minute, middleware.IPKey("global")))

	auth.RegisterRoutes(pub, authSvc, rdb)
	enrollment.RegisterRoutes(pub, enrollSvc, rdb)
	content.RegisterRoutes(pub, contentSvc, pool, rdb, cfg.StrictSessionIP)

	// Admin server (internal network only — no rate limiting needed)
	adm := fiber.New(fiber.Config{DisableStartupMessage: true})
	admin.RegisterRoutes(adm, pool, cfg.ManifestSigningKey, auditSvc)

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)

	go func() {
		log.Printf("public  listening on :%s", cfg.PublicPort)
		if err := pub.Listen(":" + cfg.PublicPort); err != nil {
			log.Printf("public server error: %v", err)
		}
	}()
	go func() {
		log.Printf("admin   listening on :%s", cfg.AdminPort)
		if err := adm.Listen(":" + cfg.AdminPort); err != nil {
			log.Printf("admin server error: %v", err)
		}
	}()

	<-quit
	log.Println("shutting down")
	pub.ShutdownWithTimeout(10 * time.Second)
	adm.ShutdownWithTimeout(10 * time.Second)
}
