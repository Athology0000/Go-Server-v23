package middleware

import (
	"context"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/db"
)

var roleRank = map[string]int{"viewer": 1, "support": 2, "super_admin": 3}

// effectiveAdminRole caps a bearer token's role by the account's authoritative bound admin_role.
// The account is the source of truth: a token whose account was demoted (lower bound role) or
// de-adminned (account exists, bound == "") authorizes only at the bound role, so a stale high-role
// token can't out-rank the account's current role and a demotion takes effect immediately even on an
// already-issued session token. A token whose username has no account row (accountExists == false)
// stands on its own role (service/API tokens; pre-backfill safety). The cap only ever LOWERS.
func effectiveAdminRole(tokenRole, boundRole string, accountExists bool) string {
	if !accountExists {
		return tokenRole
	}
	if roleRank[boundRole] < roleRank[tokenRole] {
		return boundRole
	}
	return tokenRole
}

func AdminAuth(pool *pgxpool.Pool, minRole string) fiber.Handler {
	return func(c *fiber.Ctx) error {
		raw, err := ParseBearerToken(c.Get("Authorization"))
		if err != nil {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}
		hash, err := tokenIssuer.HashPresented(raw)
		if err != nil {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}
		token, err := db.GetAdminTokenByHash(c.Context(), pool, hash)
		if err != nil || token.Revoked || time.Now().After(token.ExpiresAt) {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}
		// The account's bound admin_role caps the token's effective role: a token whose account was
		// demoted/de-adminned authorizes only at the current account role, so a stale high-role token
		// can't out-rank a demotion. Fail closed on a DB error rather than honoring the raw token role.
		boundRole, accountExists, lookErr := db.LookupAccountAdminRole(c.Context(), pool, token.AdminUsername)
		if lookErr != nil {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}
		if roleRank[effectiveAdminRole(token.Role, boundRole, accountExists)] < roleRank[minRole] {
			return c.Status(403).JSON(fiber.Map{"error": "not_authorized", "message": "Not authorized"})
		}
		// Detach from the request context: c.Context() is cancelled/recycled when the handler
		// returns, which would race this fire-and-forget last-used update and silently drop it.
		go db.TouchAdminToken(context.Background(), pool, token.ID)
		c.Locals("admin_token", token)
		return c.Next()
	}
}

func GetAdminToken(c *fiber.Ctx) *db.AdminToken {
	t, _ := c.Locals("admin_token").(*db.AdminToken)
	return t
}
