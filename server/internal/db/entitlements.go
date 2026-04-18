package db

import (
	"context"
	"time"
	"github.com/jackc/pgx/v5/pgxpool"
)

type Entitlement struct {
	PlanTier         string    `json:"plan_tier"`
	EnabledFeatures  []string  `json:"enabled_features"`
	EnabledModules   []string  `json:"enabled_modules"`
	NativeComponents []string  `json:"native_components"`
	ContentChannel   string    `json:"content_channel"`
	UpdatedAt        time.Time `json:"updated_at"`
}

type PlanOverride struct {
	ID                 string    `json:"id"`
	AccountID          string    `json:"account_id"`
	AdditionalModules  []string  `json:"additional_modules"`
	RemovedModules     []string  `json:"removed_modules"`
	AdditionalFeatures []string  `json:"additional_features"`
	RemovedFeatures    []string  `json:"removed_features"`
	Notes              *string   `json:"notes"`
	CreatedBy          string    `json:"created_by"`
	CreatedAt          time.Time `json:"created_at"`
	UpdatedAt          time.Time `json:"updated_at"`
}

func GetEntitlement(ctx context.Context, pool *pgxpool.Pool, planTier string) (*Entitlement, error) {
	row := pool.QueryRow(ctx,
		`SELECT plan_tier, enabled_features, enabled_modules, native_components, content_channel, updated_at
		 FROM entitlements WHERE plan_tier = $1`, planTier)
	e := &Entitlement{}
	err := row.Scan(&e.PlanTier, &e.EnabledFeatures, &e.EnabledModules, &e.NativeComponents, &e.ContentChannel, &e.UpdatedAt)
	return e, err
}

func GetPlanOverride(ctx context.Context, pool *pgxpool.Pool, accountID string) (*PlanOverride, error) {
	row := pool.QueryRow(ctx,
		`SELECT id, account_id, additional_modules, removed_modules, additional_features, removed_features,
		        notes, created_by, created_at, updated_at
		 FROM plan_overrides WHERE account_id = $1`, accountID)
	o := &PlanOverride{}
	err := row.Scan(&o.ID, &o.AccountID, &o.AdditionalModules, &o.RemovedModules,
		&o.AdditionalFeatures, &o.RemovedFeatures, &o.Notes, &o.CreatedBy, &o.CreatedAt, &o.UpdatedAt)
	return o, err
}

func UpsertEntitlement(ctx context.Context, pool *pgxpool.Pool, e *Entitlement) error {
	_, err := pool.Exec(ctx,
		`INSERT INTO entitlements (plan_tier, enabled_features, enabled_modules, native_components, content_channel)
		 VALUES ($1, $2, $3, $4, $5)
		 ON CONFLICT (plan_tier) DO UPDATE SET
		   enabled_features = EXCLUDED.enabled_features,
		   enabled_modules = EXCLUDED.enabled_modules,
		   native_components = EXCLUDED.native_components,
		   content_channel = EXCLUDED.content_channel,
		   updated_at = now()`,
		e.PlanTier, e.EnabledFeatures, e.EnabledModules, e.NativeComponents, e.ContentChannel)
	return err
}

func UpsertPlanOverride(ctx context.Context, pool *pgxpool.Pool, o *PlanOverride) error {
	_, err := pool.Exec(ctx,
		`INSERT INTO plan_overrides (account_id, additional_modules, removed_modules, additional_features, removed_features, notes, created_by)
		 VALUES ($1, $2, $3, $4, $5, $6, $7)
		 ON CONFLICT (account_id) DO UPDATE SET
		   additional_modules = EXCLUDED.additional_modules,
		   removed_modules = EXCLUDED.removed_modules,
		   additional_features = EXCLUDED.additional_features,
		   removed_features = EXCLUDED.removed_features,
		   notes = EXCLUDED.notes,
		   updated_at = now()`,
		o.AccountID, o.AdditionalModules, o.RemovedModules, o.AdditionalFeatures, o.RemovedFeatures, o.Notes, o.CreatedBy)
	return err
}

func DeletePlanOverride(ctx context.Context, pool *pgxpool.Pool, accountID string) error {
	_, err := pool.Exec(ctx, `DELETE FROM plan_overrides WHERE account_id = $1`, accountID)
	return err
}
