package middleware

import (
	"log"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/db"
)

// tokenIssuer hashes presented bearer tokens through the TokenIssuer module so the
// middleware asks for a token's stored-hash form instead of calling HashToken raw.
var tokenIssuer = crypto.NewTokenIssuer()

// rejectForIPMismatch decides whether the strict-IP gate must reject a request.
// It fires only when strict IP-binding is enabled, the session already has a
// pinned last_seen_ip (so there is something to compare against — the first
// request of a session pins, it never rejects), and the request's source IP
// differs from that pin. When strictIP is false this always returns false, so
// non-strict sessions keep their refresh-and-continue behaviour. Kept as a pure
// function so the enforcement contract is unit-testable without a database.
func rejectForIPMismatch(strictIP bool, lastSeenIP *string, realIP string) bool {
	return strictIP && lastSeenIP != nil && *lastSeenIP != realIP
}

// SessionAuth validates the bearer token. When strictIP is true the request's
// source IP must match the session's last_seen_ip: once a session has pinned a
// last_seen_ip, a request from a different IP is rejected with 401 (the
// last_seen_ip is NOT refreshed and the request does not continue). When
// strictIP is false the IP is not enforced — last_seen_ip is refreshed on every
// pass so the token alone is the credential, which avoids false 401s for clients
// behind carrier-grade NAT or rotating egress IPs (Railway/Cloudflare edges).
// strictIP is OFF by default (see config.StrictSessionIP); set
// STRICT_SESSION_IP=true to enable enforcement.
func SessionAuth(pool *pgxpool.Pool, strictIP bool, livenessWindow time.Duration) fiber.Handler {
	return func(c *fiber.Ctx) error {
		raw, err := ParseBearerToken(c.Get("Authorization"))
		if err != nil {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}
		hash, err := tokenIssuer.HashPresented(raw)
		if err != nil {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}
		session, err := db.GetSessionByTokenHash(c.Context(), pool, hash)
		if err != nil || !db.SessionLive(session, time.Now(), livenessWindow) {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}

		// A banned/suspended account must lose access immediately, even with a
		// live session token and an unexpired license. Entitlement resolution
		// only checks the license, so this is the gate that enforces account bans
		// for the game client. db.AccountStatusActive is the single source of the
		// rule (shared with /auth/heartbeat, /auth/finish, and the panel gate) and
		// is fail-closed, so any non-"active" status is denied.
		if !db.AccountStatusActive(session.AccountStatus) {
			return c.Status(403).JSON(fiber.Map{
				"error":   "account_" + session.AccountStatus,
				"message": "Account " + session.AccountStatus,
			})
		}

		realIP := GetRealIP(c)
		if rejectForIPMismatch(strictIP, session.LastSeenIP, realIP) {
			log.Printf("[session_auth] ip_changed session_id=%s old=%q new=%q (rejected; STRICT_SESSION_IP enforced)",
				session.ID,
				*session.LastSeenIP,
				realIP,
			)
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
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
