-- Pilot (Model B): the pro tier carries the server-delivered AutoWalk module.
-- Canonical entitlement ids are bare macro ids ("autowalk", not "phantom-autowalk").
UPDATE entitlements
SET enabled_modules   = '["autowalk"]',
    native_components = '["*"]',
    updated_at        = now()
WHERE plan_tier = 'pro'
  AND enabled_modules = '[]';
