-- Pilot (Model B): the pro tier carries the server-delivered AutoWalk module.
-- Canonical entitlement ids are bare macro ids ("autowalk", not "phantom-autowalk").
-- Upsert so a database missing the 005 pro row still ends up seeded; the WHERE
-- guard keeps it from clobbering a pro row already customized via the admin panel.
INSERT INTO entitlements (plan_tier, enabled_features, enabled_modules, native_components, content_channel)
VALUES ('pro', '[]', '["autowalk"]', '["*"]', 'stable')
ON CONFLICT (plan_tier) DO UPDATE SET
    enabled_modules   = EXCLUDED.enabled_modules,
    native_components = EXCLUDED.native_components,
    updated_at        = now()
WHERE entitlements.enabled_modules = '[]';
