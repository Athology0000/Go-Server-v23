-- Keep deployed databases aligned with runtime entitlement rules.
-- Pro and lifetime accounts are full-access tiers.

INSERT INTO entitlements (plan_tier, enabled_features, enabled_modules, native_components, content_channel)
VALUES
    ('lifetime', '["*"]', '["*"]', '["*"]', 'stable'),
    ('pro',      '["*"]', '["*"]', '["*"]', 'stable')
ON CONFLICT (plan_tier) DO UPDATE SET
    enabled_features = EXCLUDED.enabled_features,
    enabled_modules = EXCLUDED.enabled_modules,
    native_components = EXCLUDED.native_components,
    content_channel = COALESCE(NULLIF(entitlements.content_channel, ''), EXCLUDED.content_channel),
    updated_at = now();
