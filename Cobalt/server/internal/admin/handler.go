package admin

import (
	"strings"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/cobalt/server/internal/audit"
	"github.com/cobalt/server/internal/crypto"
	"github.com/cobalt/server/internal/db"
	"github.com/cobalt/server/internal/middleware"
)

func RegisterRoutes(app *fiber.App, pool *pgxpool.Pool, signingKey []byte, auditSvc *audit.Service) {
	viewer := middleware.AdminAuth(pool, "viewer")
	support := middleware.AdminAuth(pool, "support")
	super := middleware.AdminAuth(pool, "super_admin")

	// Accounts
	app.Post("/admin/accounts", super, handleCreateAccount(pool, auditSvc))
	app.Get("/admin/accounts", viewer, handleListAccounts(pool))
	app.Get("/admin/accounts/:id", viewer, handleGetAccount(pool))
	app.Patch("/admin/accounts/:id/status", support, handleUpdateAccountStatus(pool, auditSvc))

	// Licenses
	app.Post("/admin/licenses", super, handleCreateLicense(pool, auditSvc))
	app.Get("/admin/licenses/:account_id", viewer, handleGetLicense(pool))
	app.Patch("/admin/licenses/:id/status", support, handleUpdateLicenseStatus(pool, auditSvc))

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

	// Sessions
	app.Get("/admin/sessions", viewer, handleListSessions(pool))
	app.Delete("/admin/sessions/:id", support, handleRevokeSession(pool, auditSvc))

	// Admin tokens
	app.Post("/admin/tokens", super, handleCreateAdminToken(pool, auditSvc))
	app.Delete("/admin/tokens/:id", super, handleRevokeAdminToken(pool, auditSvc))

	// Audit log
	app.Get("/admin/audit", viewer, handleListAudit(pool))
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
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
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
			PlanTier string  `json:"plan_tier"`
			Count    int     `json:"count"`
			Notes    *string `json:"notes"`
		}
		if err := c.BodyParser(&body); err != nil || body.PlanTier == "" || body.Count <= 0 {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		if body.Count > 500 {
			return c.Status(400).JSON(fiber.Map{"error": "count_too_large"})
		}
		tok := middleware.GetAdminToken(c)
		keys := make([]string, 0, body.Count)
		for i := 0; i < body.Count; i++ {
			raw, hash, err := crypto.GenerateLicenseKey()
			if err != nil {
				return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
			}
			if _, err := db.CreateLicenseKey(c.Context(), pool, hash, body.PlanTier, tok.AdminUsername, body.Notes); err != nil {
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

		payload := struct {
			BuildID          string              `json:"build_id"`
			Channel          string              `json:"channel"`
			MinLoaderVersion string              `json:"minimum_loader_version"`
			Modules          []db.ManifestModule `json:"modules"`
			NativeComponents []db.ManifestNative `json:"native_components"`
		}{body.BuildID, body.Channel, body.MinLoaderVersion, body.Modules, body.NativeComponents}

		sig, err := crypto.SignManifest(signingKey, payload)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}

		m := &db.ContentManifest{
			BuildID:          body.BuildID,
			Channel:          body.Channel,
			MinLoaderVersion: body.MinLoaderVersion,
			Modules:          body.Modules,
			NativeComponents: body.NativeComponents,
			Signature:        sig,
			ExpiresAt:        time.Now().Add(time.Duration(body.ExpiresIn) * time.Hour),
		}
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
