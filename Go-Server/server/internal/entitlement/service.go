package entitlement

import (
	"context"
	"errors"
	"strings"
	"time"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/phantom/server/internal/db"
)

type Result struct {
	Authorized           bool
	Reason               string
	LicenseID            string
	PlanTier             string
	EnabledModules       []string
	EnabledFeatures      []string
	NativeComponents     []string
	ContentChannel       string
	EntitlementExpiresAt *time.Time
}

type Service struct{ pool *pgxpool.Pool }

func New(pool *pgxpool.Pool) *Service { return &Service{pool: pool} }

func (s *Service) Resolve(ctx context.Context, accountID string) (*Result, error) {
	license, err := db.GetLicenseByAccountID(ctx, s.pool, accountID)
	if err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			return &Result{Authorized: false, Reason: "no_license"}, nil
		}
		return nil, err
	}

	now := time.Now()
	switch license.Status {
	case "revoked", "suspended":
		return &Result{Authorized: false, Reason: "license_" + license.Status}, nil
	case "expired":
		if license.GraceExpiresAt == nil || now.After(*license.GraceExpiresAt) {
			return &Result{Authorized: false, Reason: "license_expired"}, nil
		}
	case "active", "trial":
		if license.ExpiresAt != nil && now.After(*license.ExpiresAt) {
			return &Result{Authorized: false, Reason: "license_expired"}, nil
		}
	default:
		return &Result{Authorized: false, Reason: "entitlement_inactive"}, nil
	}

	fullAccess := isFullAccessTier(license.PlanTier)
	ent, err := db.GetEntitlement(ctx, s.pool, license.PlanTier)
	if err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			// A full-access tier is a runtime rule, not a DB row: a missing row
			// still grants it. Anything else without a row has no entitlement.
			if fullAccess {
				res := fullAccessResult(license.PlanTier, "stable", license.ExpiresAt)
				res.LicenseID = license.ID
				return res, nil
			}
			return &Result{Authorized: false, Reason: "no_entitlement"}, nil
		}
		// Transient DB error: propagate (handler 500). Collapsing it into a
		// verdict would let a pgx blip revoke a healthy heartbeat session.
		return nil, err
	}

	contentChannel := strings.TrimSpace(ent.ContentChannel)
	if contentChannel == "" {
		contentChannel = "stable"
	}
	if fullAccess {
		res := fullAccessResult(license.PlanTier, contentChannel, license.ExpiresAt)
		res.LicenseID = license.ID
		return res, nil
	}

	modules := make([]string, len(ent.EnabledModules))
	copy(modules, ent.EnabledModules)
	features := make([]string, len(ent.EnabledFeatures))
	copy(features, ent.EnabledFeatures)
	nativeComponents := make([]string, len(ent.NativeComponents))
	copy(nativeComponents, ent.NativeComponents)

	override, err := db.GetPlanOverride(ctx, s.pool, accountID)
	if err != nil && !errors.Is(err, pgx.ErrNoRows) {
		return nil, err
	}
	if err == nil && override != nil {
		modules = applyOverride(modules, override.AdditionalModules, override.RemovedModules)
		features = applyOverride(features, override.AdditionalFeatures, override.RemovedFeatures)
	}

	// Transitively include each entitled module's declared dependencies, so a dependent (e.g.
	// commission) is never delivered without the bundles it drives (combat/mining). Full-access
	// tiers returned earlier with ["*"], so they never reach here; an empty module_metadata table
	// leaves the set unchanged. A DB error fails closed (propagated -> handler 500 -> the heartbeat
	// lease keeps existing sessions alive), consistent with the entitlement-read posture above.
	depMap, derr := db.GetAllModuleDeps(ctx, s.pool)
	if derr != nil {
		return nil, derr
	}
	modules = ExpandDependencies(depMap, modules)

	return &Result{
		Authorized:           true,
		LicenseID:            license.ID,
		PlanTier:             license.PlanTier,
		EnabledModules:       modules,
		EnabledFeatures:      features,
		NativeComponents:     nativeComponents,
		ContentChannel:       contentChannel,
		EntitlementExpiresAt: license.ExpiresAt,
	}, nil
}

func isFullAccessTier(planTier string) bool {
	switch strings.ToLower(strings.TrimSpace(planTier)) {
	case "lifetime", "pro":
		return true
	default:
		return false
	}
}

func fullAccessResult(planTier, contentChannel string, expiresAt *time.Time) *Result {
	contentChannel = strings.TrimSpace(contentChannel)
	if contentChannel == "" {
		contentChannel = "stable"
	}
	return &Result{
		Authorized:           true,
		PlanTier:             planTier,
		EnabledModules:       []string{"*"},
		EnabledFeatures:      []string{"*"},
		NativeComponents:     []string{"*"},
		ContentChannel:       contentChannel,
		EntitlementExpiresAt: expiresAt,
	}
}

// ExpandDependencies returns modules plus the transitive closure of their declared dependencies.
// The traversal is cycle-safe (a visited guard, so a -> b -> a terminates), order-stable (inputs in
// order, each followed by its newly discovered deps), and deduplicated. Modules with no edges (absent
// from deps) contribute only themselves. A wildcard "*" has no edges and is returned unchanged.
func ExpandDependencies(deps map[string][]string, modules []string) []string {
	seen := make(map[string]bool, len(modules))
	out := make([]string, 0, len(modules))
	var visit func(string)
	visit = func(m string) {
		if seen[m] {
			return
		}
		seen[m] = true
		out = append(out, m)
		for _, d := range deps[m] {
			visit(d)
		}
	}
	for _, m := range modules {
		visit(m)
	}
	return out
}

func applyOverride(base, add, remove []string) []string {
	set := make(map[string]bool, len(base))
	for _, v := range base {
		set[v] = true
	}
	for _, v := range add {
		set[v] = true
	}
	for _, v := range remove {
		delete(set, v)
	}
	result := make([]string, 0, len(set))
	for v := range set {
		result = append(result, v)
	}
	return result
}
