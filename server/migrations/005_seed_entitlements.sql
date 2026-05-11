-- Seed default plan-tier entitlement definitions.
-- These are required for auth to succeed; without them every authenticated
-- user gets "no_entitlement" regardless of license status.
--
-- Modules/features left empty by default — override via POST /admin/entitlements
-- once the admin panel is up.  The lifetime tier gets full access by convention.

INSERT INTO entitlements (plan_tier, enabled_features, enabled_modules, native_components, content_channel)
VALUES
    ('lifetime', '["*"]', '["*"]', '["*"]', 'stable'),
    ('pro',      '[]',    '[]',    '[]',     'stable'),
    ('trial',    '[]',    '[]',    '[]',     'stable')
ON CONFLICT (plan_tier) DO NOTHING;
