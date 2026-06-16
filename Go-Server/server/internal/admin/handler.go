package admin

import (
	"crypto/subtle"
	"errors"
	"strings"
	"time"
	"unicode"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/audit"
	"github.com/phantom/server/internal/content"
	"github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/db"
	"github.com/phantom/server/internal/forge"
	"github.com/phantom/server/internal/logbuf"
	"github.com/phantom/server/internal/middleware"
)

func RegisterRoutes(app *fiber.App, pool *pgxpool.Pool, signingKey []byte, auditSvc *audit.Service, adminAPISecret string, contentDir string) {
	viewer := middleware.AdminAuth(pool, "viewer")
	support := middleware.AdminAuth(pool, "support")
	super := middleware.AdminAuth(pool, "super_admin")
	setupLimit := middleware.RateLimit(pool, 3, time.Hour, middleware.IPKey("admin-setup"))
	loginLimit := middleware.RateLimit(pool, 5, time.Minute, middleware.IPAndUsernameKey("admin-login"))
	keyGenLimit := middleware.RateLimit(pool, 60, time.Hour, middleware.IPKey("admin-keygen"))

	// Bootstrap — seed the first super_admin (protected by ADMIN_API_SECRET)
	app.Post("/admin/setup", setupLimit, handleAdminSetup(pool, adminAPISecret))

	// Auth
	app.Post("/admin/auth/login", loginLimit, handleAdminLogin(pool))

	// Users (frontend-facing aggregated endpoints)
	app.Get("/admin/users", viewer, handleListUsers(pool))
	app.Get("/admin/users/:id", viewer, handleGetUser(pool))
	app.Post("/admin/users/:id/ban", support, handleBanUser(pool, auditSvc))
	app.Post("/admin/users/:id/unban", support, handleUnbanUser(pool, auditSvc))
	app.Post("/admin/users/:id/add-time", support, handleAddTime(pool, auditSvc))
	app.Post("/admin/users/:id/upgrade", support, handleUpgradePlan(pool, auditSvc))

	// Keys (frontend-facing)
	app.Get("/admin/keys", viewer, handleListKeys(pool))
	// Key generation mints revenue-bearing keys, so it requires support+ (not
	// read-only viewers, matching the super-only bulk endpoint's intent) and is
	// rate-limited per IP to blunt scripted mass-minting.
	app.Post("/admin/keys/generate", keyGenLimit, support, handleGenerateKey(pool, auditSvc))

	// Accounts
	app.Post("/admin/accounts", super, handleCreateAccount(pool, auditSvc))
	app.Get("/admin/accounts", viewer, handleListAccounts(pool))
	app.Get("/admin/accounts/:id", viewer, handleGetAccount(pool))
	app.Patch("/admin/accounts/:id/status", support, handleUpdateAccountStatus(pool, auditSvc))

	// Licenses
	app.Post("/admin/licenses", super, handleCreateLicense(pool, auditSvc))
	app.Get("/admin/licenses/:account_id", viewer, handleGetLicense(pool))
	app.Patch("/admin/licenses/:id/status", support, handleUpdateLicenseStatus(pool, auditSvc))

	// Leak tracing: map a recovered bundle watermark id back to the license it was built for.
	app.Get("/admin/trace/watermark/:wmid", support, handleTraceWatermark(contentDir))

	// License keys
	app.Post("/admin/license-keys", super, handleGenerateLicenseKeys(pool, auditSvc))
	app.Delete("/admin/license-keys/:id", super, handleRevokeLicenseKey(pool, auditSvc))

	// Devices
	app.Get("/admin/devices/:id", viewer, handleGetDevice(pool))
	app.Post("/admin/devices/:id/reset", support, handleResetDevice(pool, auditSvc))
	app.Patch("/admin/devices/:id/status", support, handleUpdateDeviceStatus(pool, auditSvc))

	// Entitlements
	app.Post("/admin/entitlements", super, handleUpsertEntitlement(pool, auditSvc))
	app.Get("/admin/entitlements/:plan", viewer, handleGetEntitlement(pool))
	app.Post("/admin/accounts/:id/override", super, handleUpsertOverride(pool, auditSvc))
	app.Delete("/admin/accounts/:id/override", super, handleDeleteOverride(pool, auditSvc))

	// Manifests
	app.Post("/admin/manifests", super, handleCreateManifest(pool, signingKey, auditSvc))
	app.Get("/admin/manifests", viewer, handleGetLatestManifest(pool))

	// Forge builds (super_admin): list pending forged builds, approve (promote live) or deny.
	forgePromoter := &forge.Promoter{ContentDir: contentDir}
	app.Get("/admin/builds", super, handleListBuilds(pool))
	app.Get("/admin/builds/:id", super, handleGetBuild(pool))
	app.Post("/admin/builds/:id/approve", super, handleApproveBuild(pool, auditSvc, forgePromoter))
	app.Post("/admin/builds/:id/deny", super, handleDenyBuild(pool, auditSvc, forgePromoter))

	// Sessions
	app.Get("/admin/sessions", viewer, handleListSessions(pool))
	app.Delete("/admin/sessions/:id", support, handleRevokeSession(pool, auditSvc))

	// Admin tokens
	app.Post("/admin/tokens", super, handleCreateAdminToken(pool, auditSvc))
	app.Delete("/admin/tokens/:id", super, handleRevokeAdminToken(pool, auditSvc))

	// Audit log
	app.Get("/admin/audit", viewer, handleListAudit(pool))

	// Activity log (user-facing events — visible to all staff)
	app.Get("/admin/activity", viewer, handleActivityLog(pool))

	// Server log stream (super_admin only)
	app.Get("/admin/server-logs", super, handleServerLogs())
}

// ── Accounts ──────────────────────────────────────────────────────────────────

func handleCreateAccount(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var body struct {
			Username string  `json:"username"`
			Password string  `json:"password"`
			Email    *string `json:"email"`
		}
		if err := c.BodyParser(&body); err != nil || body.Username == "" || body.Password == "" {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request", "message": "Username and password required"})
		}
		if err := validateAdminPassword(body.Password); err != nil {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_password", "message": err.Error()})
		}
		hash, err := crypto.HashPassword(body.Password)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		username := strings.ToLower(strings.TrimSpace(body.Username))
		account, err := db.CreateAccount(c.Context(), pool, username, hash, body.Email)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		tok := middleware.GetAdminToken(c)
		auditSvc.Log("admin.account.create", &account.ID, nil, &tok.AdminUsername, nil,
			map[string]any{"username": account.Username})
		return c.Status(201).JSON(account)
	}
}

func handleListAccounts(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		limit := c.QueryInt("limit", 50)
		offset := c.QueryInt("offset", 0)
		accounts, err := db.ListAccounts(c.Context(), pool, limit, offset)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.JSON(accounts)
	}
}

func handleGetAccount(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		account, err := db.GetAccountByID(c.Context(), pool, c.Params("id"))
		if err != nil {
			return c.Status(404).JSON(fiber.Map{"error": "not_found"})
		}
		return c.JSON(account)
	}
}

func handleUpdateAccountStatus(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var body struct {
			Status string `json:"status"`
		}
		if err := c.BodyParser(&body); err != nil || body.Status == "" {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		id := c.Params("id")
		if err := db.UpdateAccountStatus(c.Context(), pool, id, body.Status); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		tok := middleware.GetAdminToken(c)
		auditSvc.Log("admin.account.status", &id, nil, &tok.AdminUsername, nil,
			map[string]any{"status": body.Status})
		return c.JSON(fiber.Map{"status": "updated"})
	}
}

func handleTraceWatermark(contentDir string) fiber.Handler {
	return func(c *fiber.Ctx) error {
		wmid := c.Params("wmid")
		licenseID, err := content.LookupWatermark(contentDir, wmid)
		if err != nil {
			return c.Status(404).JSON(fiber.Map{"error": "not_found", "wmid": wmid})
		}
		return c.JSON(fiber.Map{"wmid": wmid, "license_id": licenseID})
	}
}

// ── Licenses ──────────────────────────────────────────────────────────────────

func handleCreateLicense(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var body struct {
			AccountID string  `json:"account_id"`
			PlanTier  string  `json:"plan_tier"`
			StartsAt  string  `json:"starts_at"`
			ExpiresAt *string `json:"expires_at"`
		}
		if err := c.BodyParser(&body); err != nil || body.AccountID == "" || body.PlanTier == "" {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		startsAt := time.Now()
		if body.StartsAt != "" {
			t, err := time.Parse(time.RFC3339, body.StartsAt)
			if err != nil {
				return c.Status(400).JSON(fiber.Map{"error": "invalid_starts_at"})
			}
			startsAt = t
		}
		var expiresAt *time.Time
		if body.ExpiresAt != nil {
			t, err := time.Parse(time.RFC3339, *body.ExpiresAt)
			if err != nil {
				return c.Status(400).JSON(fiber.Map{"error": "invalid_expires_at"})
			}
			expiresAt = &t
		}
		license, err := db.CreateLicense(c.Context(), pool, body.AccountID, body.PlanTier, startsAt, expiresAt)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		tok := middleware.GetAdminToken(c)
		auditSvc.Log("admin.license.create", &body.AccountID, nil, &tok.AdminUsername, nil,
			map[string]any{"plan_tier": body.PlanTier})
		return c.Status(201).JSON(license)
	}
}

func handleGetLicense(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		license, err := db.GetLicenseByAccountID(c.Context(), pool, c.Params("account_id"))
		if err != nil {
			return c.Status(404).JSON(fiber.Map{"error": "not_found"})
		}
		return c.JSON(license)
	}
}

func handleUpdateLicenseStatus(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var body struct {
			Status string `json:"status"`
		}
		if err := c.BodyParser(&body); err != nil || body.Status == "" {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		id := c.Params("id")
		if err := db.UpdateLicenseStatus(c.Context(), pool, id, body.Status); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		tok := middleware.GetAdminToken(c)
		auditSvc.Log("admin.license.status", nil, nil, &tok.AdminUsername, nil,
			map[string]any{"license_id": id, "status": body.Status})
		return c.JSON(fiber.Map{"status": "updated"})
	}
}

// ── License Keys ───────────────────────────────────────────────────────────────

func handleGenerateLicenseKeys(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var body struct {
			PlanTier     string  `json:"plan_tier"`
			Count        int     `json:"count"`
			DurationDays int     `json:"duration_days"`
			Notes        *string `json:"notes"`
		}
		if err := c.BodyParser(&body); err != nil || body.PlanTier == "" || body.Count <= 0 {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		if body.Count > 500 {
			return c.Status(400).JSON(fiber.Map{"error": "count_too_large"})
		}

		// Keep bulk generation backwards compatible: if duration isn't specified, use 30 days,
		// but treat the "lifetime" tier as non-expiring.
		if body.PlanTier == "lifetime" {
			body.DurationDays = 0
		} else if body.DurationDays <= 0 {
			body.DurationDays = 30
		}

		tok := middleware.GetAdminToken(c)
		keys := make([]string, 0, body.Count)
		for i := 0; i < body.Count; i++ {
			raw, hash, err := crypto.GenerateLicenseKey()
			if err != nil {
				return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
			}
			if _, err := db.CreateLicenseKey(c.Context(), pool, hash, body.PlanTier, body.DurationDays, tok.AdminUsername, body.Notes); err != nil {
				return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
			}
			keys = append(keys, raw)
		}
		auditSvc.Log("admin.license_keys.generate", nil, nil, &tok.AdminUsername, nil,
			map[string]any{"plan_tier": body.PlanTier, "count": body.Count})
		return c.Status(201).JSON(fiber.Map{"keys": keys})
	}
}

func handleRevokeLicenseKey(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		id := c.Params("id")
		if err := db.RevokeLicenseKey(c.Context(), pool, id); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		tok := middleware.GetAdminToken(c)
		auditSvc.Log("admin.license_key.revoke", nil, nil, &tok.AdminUsername, nil,
			map[string]any{"key_id": id})
		return c.JSON(fiber.Map{"status": "revoked"})
	}
}

// ── Devices ────────────────────────────────────────────────────────────────────

func handleGetDevice(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		device, err := db.GetDeviceByID(c.Context(), pool, c.Params("id"))
		if err != nil {
			return c.Status(404).JSON(fiber.Map{"error": "not_found"})
		}
		return c.JSON(device)
	}
}

func handleResetDevice(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		id := c.Params("id")
		tok := middleware.GetAdminToken(c)
		if err := db.ResetDeviceBinding(c.Context(), pool, id, tok.AdminUsername); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		auditSvc.Log("admin.device.reset", nil, &id, &tok.AdminUsername, nil, nil)
		return c.JSON(fiber.Map{"status": "reset"})
	}
}

func handleUpdateDeviceStatus(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var body struct {
			Status string `json:"status"`
		}
		if err := c.BodyParser(&body); err != nil || body.Status == "" {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		id := c.Params("id")
		if err := db.UpdateDeviceStatus(c.Context(), pool, id, body.Status); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		tok := middleware.GetAdminToken(c)
		auditSvc.Log("admin.device.status", nil, &id, &tok.AdminUsername, nil,
			map[string]any{"status": body.Status})
		return c.JSON(fiber.Map{"status": "updated"})
	}
}

// ── Entitlements ───────────────────────────────────────────────────────────────

func handleUpsertEntitlement(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var e db.Entitlement
		if err := c.BodyParser(&e); err != nil || e.PlanTier == "" {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		if e.ContentChannel == "" {
			e.ContentChannel = "stable"
		}
		if err := db.UpsertEntitlement(c.Context(), pool, &e); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		tok := middleware.GetAdminToken(c)
		auditSvc.Log("admin.entitlement.upsert", nil, nil, &tok.AdminUsername, nil,
			map[string]any{"plan_tier": e.PlanTier})
		return c.JSON(fiber.Map{"status": "ok"})
	}
}

func handleGetEntitlement(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		e, err := db.GetEntitlement(c.Context(), pool, c.Params("plan"))
		if err != nil {
			return c.Status(404).JSON(fiber.Map{"error": "not_found"})
		}
		return c.JSON(e)
	}
}

func handleUpsertOverride(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var o db.PlanOverride
		if err := c.BodyParser(&o); err != nil {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		o.AccountID = c.Params("id")
		tok := middleware.GetAdminToken(c)
		o.CreatedBy = tok.AdminUsername
		if err := db.UpsertPlanOverride(c.Context(), pool, &o); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		auditSvc.Log("admin.override.upsert", &o.AccountID, nil, &tok.AdminUsername, nil, nil)
		return c.JSON(fiber.Map{"status": "ok"})
	}
}

func handleDeleteOverride(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		id := c.Params("id")
		if err := db.DeletePlanOverride(c.Context(), pool, id); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		tok := middleware.GetAdminToken(c)
		auditSvc.Log("admin.override.delete", &id, nil, &tok.AdminUsername, nil, nil)
		return c.JSON(fiber.Map{"status": "deleted"})
	}
}

// ── Manifests ─────────────────────────────────────────────────────────────────

func handleCreateManifest(pool *pgxpool.Pool, signingKey []byte, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var body struct {
			BuildID          string              `json:"build_id"`
			Channel          string              `json:"channel"`
			MinLoaderVersion string              `json:"minimum_loader_version"`
			ModuleKey        string              `json:"module_key"`
			Modules          []db.ManifestModule `json:"modules"`
			NativeComponents []db.ManifestNative `json:"native_components"`
			ExpiresIn        int                 `json:"expires_in_hours"`
		}
		if err := c.BodyParser(&body); err != nil || body.BuildID == "" || body.Channel == "" {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		if body.ExpiresIn <= 0 {
			body.ExpiresIn = 72
		}

		expiresAt := time.Now().Add(time.Duration(body.ExpiresIn) * time.Hour)

		m := &db.ContentManifest{
			BuildID:          body.BuildID,
			Channel:          body.Channel,
			MinLoaderVersion: body.MinLoaderVersion,
			ModuleKey:        body.ModuleKey,
			Modules:          body.Modules,
			NativeComponents: body.NativeComponents,
			ExpiresAt:        expiresAt,
			ExpiresAtMillis:  expiresAt.UnixMilli(),
			// Server-assigned monotonic epoch (creation time): a new admin manifest for a channel
			// outranks the previous one, advancing the client's rollback high-water-mark.
			Epoch: time.Now().UnixMilli(),
		}
		// One canonicalizer signs both stable and admin paths so they cannot drift apart.
		sig, err := content.SignManifest(signingKey, m)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		m.Signature = sig
		created, err := db.CreateManifest(c.Context(), pool, m)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		tok := middleware.GetAdminToken(c)
		auditSvc.Log("admin.manifest.create", nil, nil, &tok.AdminUsername, nil,
			map[string]any{"build_id": body.BuildID, "channel": body.Channel})
		return c.Status(201).JSON(created)
	}
}

func handleGetLatestManifest(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		channel := c.Query("channel", "stable")
		m, err := db.GetLatestManifest(c.Context(), pool, channel)
		if err != nil {
			return c.Status(404).JSON(fiber.Map{"error": "not_found"})
		}
		return c.JSON(m)
	}
}

// ── Sessions ───────────────────────────────────────────────────────────────────

func handleListSessions(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		limit := c.QueryInt("limit", 50)
		offset := c.QueryInt("offset", 0)
		sessions, err := db.ListActiveSessions(c.Context(), pool, limit, offset)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.JSON(sessions)
	}
}

func handleRevokeSession(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		id := c.Params("id")
		if err := db.RevokeSession(c.Context(), pool, id); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		tok := middleware.GetAdminToken(c)
		auditSvc.Log("admin.session.revoke", nil, nil, &tok.AdminUsername, nil,
			map[string]any{"session_id": id})
		return c.JSON(fiber.Map{"status": "revoked"})
	}
}

// ── Admin Tokens ───────────────────────────────────────────────────────────────

func handleCreateAdminToken(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var body struct {
			AdminUsername string `json:"admin_username"`
			Role          string `json:"role"`
			ExpiresIn     int    `json:"expires_in_days"`
		}
		if err := c.BodyParser(&body); err != nil || body.AdminUsername == "" || body.Role == "" {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		if body.ExpiresIn <= 0 {
			body.ExpiresIn = 90
		}
		raw, hash, err := crypto.GenerateToken()
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		expiresAt := time.Now().Add(time.Duration(body.ExpiresIn) * 24 * time.Hour)
		token, err := db.CreateAdminToken(c.Context(), pool, hash, body.AdminUsername, body.Role, expiresAt)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		tok := middleware.GetAdminToken(c)
		auditSvc.Log("admin.token.create", nil, nil, &tok.AdminUsername, nil,
			map[string]any{"for": body.AdminUsername, "role": body.Role})
		return c.Status(201).JSON(fiber.Map{"token": raw, "id": token.ID, "expires_at": token.ExpiresAt})
	}
}

func handleRevokeAdminToken(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		id := c.Params("id")
		if err := db.RevokeAdminToken(c.Context(), pool, id); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		tok := middleware.GetAdminToken(c)
		auditSvc.Log("admin.token.revoke", nil, nil, &tok.AdminUsername, nil,
			map[string]any{"token_id": id})
		return c.JSON(fiber.Map{"status": "revoked"})
	}
}

// ── Users (aggregated) ────────────────────────────────────────────────────────

type userRow struct {
	ID                string     `json:"id"`
	Username          string     `json:"username"`
	Email             *string    `json:"email"`
	Plan              *string    `json:"plan"`
	PlanExpiry        *time.Time `json:"planExpiry"`
	HwidBound         bool       `json:"hwidBound"`
	Hwid              *string    `json:"hwid"`
	Banned            bool       `json:"banned"`
	BannedReason      *string    `json:"bannedReason"`
	CreatedAt         time.Time  `json:"createdAt"`
	LastSeen          *time.Time `json:"lastSeen"`
	MinecraftUsername *string    `json:"minecraftUsername"`
}

func scanUserRow(row interface{ Scan(...any) error }) (*userRow, error) {
	u := &userRow{}
	var status string
	var hwidBound bool
	err := row.Scan(
		&u.ID, &u.Username, &u.Email, &status, &u.CreatedAt,
		&u.Plan, &u.PlanExpiry,
		&hwidBound, &u.MinecraftUsername, &u.LastSeen,
	)
	if err != nil {
		return nil, err
	}
	u.Banned = status == "banned"
	u.HwidBound = hwidBound
	return u, nil
}

const userSelectSQL = `
SELECT a.id, a.username, a.email, a.status, a.created_at,
       l.plan_tier, l.expires_at,
       (d.hwid_hash IS NOT NULL) AS hwid_bound,
       d.minecraft_username, d.last_login_at
FROM accounts a
LEFT JOIN licenses l ON l.account_id = a.id AND l.status = 'active'
LEFT JOIN devices d ON d.account_id = a.id`

func handleListUsers(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		q := c.Query("q")
		limit := c.QueryInt("limit", 50)
		offset := c.QueryInt("offset", 0)

		rows, err := pool.Query(c.Context(),
			userSelectSQL+`
WHERE ($1 = '' OR a.username ILIKE '%'||$1||'%' OR COALESCE(a.email,'') ILIKE '%'||$1||'%')
ORDER BY a.created_at DESC LIMIT $2 OFFSET $3`, q, limit, offset)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		defer rows.Close()

		var users []*userRow
		for rows.Next() {
			u, err := scanUserRow(rows)
			if err != nil {
				return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
			}
			users = append(users, u)
		}
		if users == nil {
			users = []*userRow{}
		}
		return c.JSON(users)
	}
}

func handleGetUser(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		row := pool.QueryRow(c.Context(),
			userSelectSQL+" WHERE a.id = $1", c.Params("id"))
		u, err := scanUserRow(row)
		if err != nil {
			return c.Status(404).JSON(fiber.Map{"error": "not_found"})
		}
		return c.JSON(u)
	}
}

func handleBanUser(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		id := c.Params("id")
		if err := db.UpdateAccountStatus(c.Context(), pool, id, "banned"); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		// Cut off any live game-client sessions immediately. SessionAuth also
		// rejects banned accounts on the next request, but revoking now is instant
		// and closes the cached-entitlement window.
		if err := db.RevokeAccountSessions(c.Context(), pool, id); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		tok := middleware.GetAdminToken(c)
		var body struct {
			Reason string `json:"reason"`
		}
		_ = c.BodyParser(&body)
		auditSvc.Log("admin.user.ban", &id, nil, &tok.AdminUsername, nil,
			map[string]any{"reason": body.Reason})
		return c.JSON(fiber.Map{"status": "banned"})
	}
}

func handleUnbanUser(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		id := c.Params("id")
		if err := db.UpdateAccountStatus(c.Context(), pool, id, "active"); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		tok := middleware.GetAdminToken(c)
		auditSvc.Log("admin.user.unban", &id, nil, &tok.AdminUsername, nil, nil)
		return c.JSON(fiber.Map{"status": "active"})
	}
}

func handleAddTime(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		id := c.Params("id")
		var body struct {
			Days int    `json:"days"`
			Plan string `json:"plan"`
		}
		if err := c.BodyParser(&body); err != nil || body.Days <= 0 {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		if body.Plan == "" {
			body.Plan = "pro"
		}
		newExpiry, err := db.ExtendLicense(c.Context(), pool, id, body.Plan, body.Days)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		tok := middleware.GetAdminToken(c)
		auditSvc.Log("admin.user.add_time", &id, nil, &tok.AdminUsername, nil,
			map[string]any{"days": body.Days, "new_expiry": newExpiry})
		return c.JSON(fiber.Map{"status": "ok", "new_expiry": newExpiry})
	}
}

func handleUpgradePlan(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		id := c.Params("id")
		var body struct {
			Plan string `json:"plan"`
		}
		if err := c.BodyParser(&body); err != nil || body.Plan == "" {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		if err := db.UpgradeLicensePlan(c.Context(), pool, id, body.Plan); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		tok := middleware.GetAdminToken(c)
		auditSvc.Log("admin.user.upgrade", &id, nil, &tok.AdminUsername, nil,
			map[string]any{"plan": body.Plan})
		return c.JSON(fiber.Map{"status": "ok", "plan": body.Plan})
	}
}

// ── Keys (frontend-facing) ─────────────────────────────────────────────────────

type keyRow struct {
	ID string `json:"id"`
	// key is the only field older frontends understand.
	// For list endpoints: it's the key hash prefix (safe to display).
	// For generate endpoint: it's the raw key (only shown once).
	Key           string     `json:"key"`
	KeyHash       string     `json:"keyHash,omitempty"`
	KeyHashPrefix string     `json:"keyHashPrefix,omitempty"`
	Plan          string     `json:"plan"`
	DurationDays  int        `json:"durationDays"`
	CreatedAt     time.Time  `json:"createdAt"`
	UsedBy        *string    `json:"usedBy"`
	UsedAt        *time.Time `json:"usedAt"`
}

func handleListKeys(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		limit := c.QueryInt("limit", 50)
		offset := c.QueryInt("offset", 0)
		keys, err := db.ListLicenseKeys(c.Context(), pool, limit, offset)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		rows := make([]keyRow, 0, len(keys))
		for _, k := range keys {
			display := k.KeyHash
			prefix := display
			if len(prefix) > 16 {
				prefix = prefix[:16]
			}
			rows = append(rows, keyRow{
				ID:            k.ID,
				Key:           prefix,
				KeyHash:       display,
				KeyHashPrefix: prefix,
				Plan:          k.PlanTier,
				DurationDays:  k.DurationDays,
				CreatedAt:     k.CreatedAt,
				UsedBy:        k.RedeemedBy,
				UsedAt:        k.RedeemedAt,
			})
		}
		return c.JSON(rows)
	}
}

func handleGenerateKey(pool *pgxpool.Pool, auditSvc *audit.Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var body struct {
			Plan         string `json:"plan"`
			DurationDays int    `json:"durationDays"`
		}
		if err := c.BodyParser(&body); err != nil || body.Plan == "" {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}

		if body.Plan == "lifetime" {
			body.DurationDays = 0
		} else if body.DurationDays <= 0 {
			body.DurationDays = 30
		}

		tok := middleware.GetAdminToken(c)
		raw, hash, err := crypto.GenerateLicenseKey()
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		hashPrefix := hash
		if len(hashPrefix) > 16 {
			hashPrefix = hashPrefix[:16]
		}
		k, err := db.CreateLicenseKey(c.Context(), pool, hash, body.Plan, body.DurationDays, tok.AdminUsername, nil)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		auditSvc.Log("admin.key.generate", nil, nil, &tok.AdminUsername, nil,
			map[string]any{"plan": body.Plan})
		return c.Status(201).JSON(keyRow{
			ID:            k.ID,
			Key:           raw,
			KeyHash:       hash,
			KeyHashPrefix: hashPrefix,
			Plan:          k.PlanTier,
			DurationDays:  body.DurationDays,
			CreatedAt:     k.CreatedAt,
			UsedBy:        nil,
			UsedAt:        nil,
		})
	}
}

// ── Login ─────────────────────────────────────────────────────────────────────

func handleAdminLogin(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var body struct {
			Username string `json:"username"`
			Password string `json:"password"`
		}
		if err := c.BodyParser(&body); err != nil || body.Username == "" || body.Password == "" {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}

		account, err := db.GetAccountByUsername(c.Context(), pool, strings.ToLower(strings.TrimSpace(body.Username)))
		if err != nil {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Invalid username or password"})
		}

		ok, err := crypto.VerifyPassword(body.Password, account.PasswordHash)
		if err != nil || !ok {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Invalid username or password"})
		}

		role, err := db.GetHighestAdminRole(c.Context(), pool, account.Username)
		if err != nil {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}

		raw, hash, err := crypto.GenerateToken()
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		sessionToken, err := db.CreateAdminToken(c.Context(), pool, hash, account.Username, role, time.Now().Add(8*time.Hour))
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}

		frontendRole := "admin"
		if sessionToken.Role == "super_admin" {
			frontendRole = "superadmin"
		}

		return c.JSON(fiber.Map{
			"token": raw,
			"user": fiber.Map{
				"id":       account.ID,
				"username": account.Username,
				"email":    account.Email,
				"role":     frontendRole,
			},
		})
	}
}

// ── Audit ──────────────────────────────────────────────────────────────────────

func handleListAudit(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		limit := c.QueryInt("limit", 100)
		offset := c.QueryInt("offset", 0)
		events, err := db.ListAuditLog(c.Context(), pool, limit, offset)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.JSON(events)
	}
}

// ── Activity log (user-facing events) ────────────────────────────────────────

func handleActivityLog(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		limit := c.QueryInt("limit", 100)
		offset := c.QueryInt("offset", 0)
		events, err := db.ListActivityLog(c.Context(), pool, limit, offset)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		if events == nil {
			events = []*db.AuditRecord{}
		}
		return c.JSON(events)
	}
}

// ── Server log stream ─────────────────────────────────────────────────────────

func handleServerLogs() fiber.Handler {
	return func(c *fiber.Ctx) error {
		after := c.QueryInt("after", 0)
		lines, seq := logbuf.Global.Lines(after)
		if lines == nil {
			lines = []string{}
		}
		return c.JSON(fiber.Map{"lines": lines, "seq": seq})
	}
}

// ── Bootstrap ─────────────────────────────────────────────────────────────────

func handleAdminSetup(pool *pgxpool.Pool, adminAPISecret string) fiber.Handler {
	return func(c *fiber.Ctx) error {
		if adminAPISecret == "" {
			return c.Status(503).JSON(fiber.Map{"error": "setup_disabled"})
		}

		hasAdmin, err := db.HasActiveAdminToken(c.Context(), pool)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		if hasAdmin {
			return c.Status(403).JSON(fiber.Map{"error": "setup_disabled"})
		}

		raw, err := middleware.ParseBearerToken(c.Get("Authorization"))
		if err != nil || subtle.ConstantTimeCompare([]byte(raw), []byte(adminAPISecret)) != 1 {
			return c.Status(401).JSON(fiber.Map{"error": "invalid_secret"})
		}

		var body struct {
			Username string `json:"username"`
			Password string `json:"password"`
		}
		if err := c.BodyParser(&body); err != nil || body.Username == "" || body.Password == "" {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request", "message": "Username and password required"})
		}
		if err := validateAdminPassword(body.Password); err != nil {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_password", "message": err.Error()})
		}

		hash, err := crypto.HashPassword(body.Password)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}

		username := strings.ToLower(strings.TrimSpace(body.Username))
		account, err := db.GetAccountByUsername(c.Context(), pool, username)
		if err != nil {
			account, err = db.CreateAccount(c.Context(), pool, username, hash, nil)
			if err != nil {
				return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
			}
		} else if err := db.UpdateAccountPassword(c.Context(), pool, account.ID, hash); err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}

		_, tokenHash, err := crypto.GenerateToken()
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}

		expiresAt := time.Now().Add(365 * 24 * time.Hour * 10)
		_, err = db.CreateAdminToken(c.Context(), pool, tokenHash, account.Username, "super_admin", expiresAt)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "seed_failed"})
		}

		return c.JSON(fiber.Map{"status": "ok", "username": account.Username, "role": "super_admin"})
	}
}
func validateAdminPassword(password string) error {
	if len(password) < 12 {
		return errors.New("Password must be at least 12 characters")
	}
	var hasLetter, hasDigit bool
	for _, r := range password {
		if unicode.IsLetter(r) {
			hasLetter = true
		}
		if unicode.IsDigit(r) {
			hasDigit = true
		}
	}
	if !hasLetter || !hasDigit {
		return errors.New("Password must contain letters and digits")
	}
	return nil
}
