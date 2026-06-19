package admin

import (
	"errors"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/audit"
	"github.com/phantom/server/internal/db"
	"github.com/phantom/server/internal/forge"
	"github.com/phantom/server/internal/middleware"
)

// handleListBuilds lists forge builds in a status (default pending_approval) — the data the
// superadmin panel polls to surface the "build awaiting approval" notification.
func handleListBuilds(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		status := c.Query("status", "pending_approval")
		list, err := db.ListBuildsByStatus(c.Context(), pool, status, 200)
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		if list == nil {
			list = []*db.ContentBuild{}
		}
		return c.JSON(fiber.Map{"builds": list})
	}
}

func handleGetBuild(pool *pgxpool.Pool) fiber.Handler {
	return func(c *fiber.Ctx) error {
		b, err := db.GetBuildByBuildID(c.Context(), pool, c.Params("id"))
		if err != nil {
			if errors.Is(err, pgx.ErrNoRows) {
				return c.Status(404).JSON(fiber.Map{"error": "not_found"})
			}
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		return c.JSON(b)
	}
}

// handleApproveBuild promotes a pending build to live (installs its staged artifacts into the
// content dir) and records who approved it.
func handleApproveBuild(pool *pgxpool.Pool, auditSvc *audit.Service, promoter *forge.Promoter) fiber.Handler {
	return func(c *fiber.Ctx) error {
		id := c.Params("id")
		b, err := db.GetBuildByBuildID(c.Context(), pool, id)
		if err != nil {
			if errors.Is(err, pgx.ErrNoRows) {
				return c.Status(404).JSON(fiber.Map{"error": "not_found"})
			}
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}

		updated, err := promoter.Promote(c.Context(), pool, b, adminName(c))
		if err != nil {
			// wrong-state / missing-artifact / sha mismatch — a conflict, not a server fault.
			return c.Status(409).JSON(fiber.Map{"error": "promote_failed", "message": err.Error()})
		}

		auditSvc.Log(audit.EventAdminBuildApprove, nil, nil, adminNamePtr(c), ipPtr(c),
			map[string]any{"build_id": id, "module": b.Module})
		return c.JSON(updated)
	}
}

// handleDenyBuild rejects a build and purges its staged artifacts.
func handleDenyBuild(pool *pgxpool.Pool, auditSvc *audit.Service, promoter *forge.Promoter) fiber.Handler {
	return func(c *fiber.Ctx) error {
		id := c.Params("id")
		b, err := db.GetBuildByBuildID(c.Context(), pool, id)
		if err != nil {
			if errors.Is(err, pgx.ErrNoRows) {
				return c.Status(404).JSON(fiber.Map{"error": "not_found"})
			}
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}

		updated, err := promoter.Deny(c.Context(), pool, b, adminName(c))
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}

		auditSvc.Log(audit.EventAdminBuildDeny, nil, nil, adminNamePtr(c), ipPtr(c),
			map[string]any{"build_id": id, "module": b.Module})
		return c.JSON(updated)
	}
}

func adminName(c *fiber.Ctx) string {
	if tok := middleware.GetAdminToken(c); tok != nil {
		return tok.AdminUsername
	}
	return ""
}

func adminNamePtr(c *fiber.Ctx) *string {
	if n := adminName(c); n != "" {
		return &n
	}
	return nil
}

func ipPtr(c *fiber.Ctx) *string {
	ip := middleware.GetRealIP(c)
	if ip == "" {
		return nil
	}
	return &ip
}
