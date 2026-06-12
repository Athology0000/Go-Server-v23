package middleware

import (
	"fmt"
	"strconv"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/redis/go-redis/v9"
	"github.com/phantom/server/internal/cache"
)

func RateLimit(rdb *redis.Client, limit int, window time.Duration, keyFn func(*fiber.Ctx) string) fiber.Handler {
	return func(c *fiber.Ctx) error {
		key := keyFn(c)
		allowed, remaining, reset, err := cache.CheckRateLimit(c.Context(), rdb, key, limit, window)
		c.Set("X-RateLimit-Limit", strconv.Itoa(limit))
		c.Set("X-RateLimit-Remaining", strconv.Itoa(remaining))
		c.Set("X-RateLimit-Reset", strconv.FormatInt(reset.Unix(), 10))
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		if !allowed {
			retryAfter := int(reset.Sub(time.Now()).Seconds())
			if retryAfter < 1 {
				retryAfter = 1
			}
			c.Set("Retry-After", strconv.Itoa(retryAfter))
			return c.Status(429).JSON(fiber.Map{"error": "rate_limited", "message": "Too many requests"})
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
