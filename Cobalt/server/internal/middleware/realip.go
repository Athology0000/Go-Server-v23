package middleware

import "github.com/gofiber/fiber/v2"

func RealIP() fiber.Handler {
	return func(c *fiber.Ctx) error {
		c.Locals("realip", c.IP())
		return c.Next()
	}
}

func GetRealIP(c *fiber.Ctx) string {
	if ip, ok := c.Locals("realip").(string); ok {
		return ip
	}
	return c.IP()
}
