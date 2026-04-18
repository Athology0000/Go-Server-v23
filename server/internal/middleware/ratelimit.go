package middleware

import (
	"fmt"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/redis/go-redis/v9"
	"github.com/cobalt/server/internal/cache"
)

func RateLimit(rdb *redis.Client, limit int, window time.Duration, keyFn func(*fiber.Ctx) string) fiber.Handler {
	return func(c *fiber.Ctx) error {
		key := keyFn(c)
		allowed, err := cache.CheckRateLimit(c.Context(), rdb, key, limit, window)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		if !allowed {
			return c.Status(429).JSON(fiber.Map{"error": "rate_limited"})
		}
		return c.Next()
	}
}

func IPKey(prefix string) func(*fiber.Ctx) string {
	return func(c *fiber.Ctx) string {
		return fmt.Sprintf("%s:%s", prefix, GetRealIP(c))
	}
}

func IPAndUsernameKey(prefix string) func(*fiber.Ctx) string {
	return func(c *fiber.Ctx) string {
		body := struct {
			Username string `json:"username"`
		}{}
		c.BodyParser(&body)
		return fmt.Sprintf("%s:%s:%s", prefix, GetRealIP(c), body.Username)
	}
}
