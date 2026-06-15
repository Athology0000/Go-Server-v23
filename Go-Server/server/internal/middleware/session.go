package middleware

import (
	"log"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/db"
)

// SessionAuth validates the bearer token. When strictIP is true the request's
// source IP must match the session's last_seen_ip — but we always refresh
// last_seen_ip on a successful pass so subsequent requests in the same launch
// from the same IP succeed even after a rotation. Clients behind carrier-grade
// NAT or rotating egress IPs (Railway/Cloudflare edges) get false 401s under
// the previous behavior; the strictIP gate now logs a warning instead of
// rejecting when the IP changes, leaving the token itself as the credential.
func SessionAuth(pool *pgxpool.Pool, strictIP bool, livenessWindow time.Duration) fiber.Handler {
	return func(c *fiber.Ctx) error {
		raw, err := ParseBearerToken(c.Get("Authorization"))
		if err != nil {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}
		hash, err := crypto.HashToken(raw)
		if err != nil {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}
		session, err := db.GetSessionByTokenHash(c.Context(), pool, hash)
		if err != nil || !db.SessionLive(session, time.Now(), livenessWindow) {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}

		realIP := GetRealIP(c)
		if strictIP && session.LastSeenIP != nil && *session.LastSeenIP != realIP {
			log.Printf("[session_auth] ip_changed session_id=%s old=%q new=%q (allowing through; token is the credential)",
				session.ID,
				*session.LastSeenIP,
				realIP,
			)
		}
		if session.LastSeenIP == nil || *session.LastSeenIP != realIP {
			if updateErr := db.UpdateSessionLastSeenIP(c.Context(), pool, session.ID, realIP); updateErr != nil {
				log.Printf("[session_auth] last_seen_ip_update_failed session_id=%s err=%v", session.ID, updateErr)
			} else {
				stored := realIP
				session.LastSeenIP = &stored
			}
		}

		c.Locals("session", session)
		return c.Next()
	}
}

func GetSession(c *fiber.Ctx) *db.Session {
	s, _ := c.Locals("session").(*db.Session)
	return s
}
