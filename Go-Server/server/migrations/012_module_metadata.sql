-- Module dependency metadata: declares that a module needs other modules' bundles co-delivered.
-- Keys are BARE module ids (matching entitlements.enabled_modules and the client's
-- externalizedModuleDeps), e.g. 'commission' depends on 'combat' and 'mining'. The entitlement
-- resolver expands a license's module set by the transitive closure of these edges so a dependent's
-- dep bundles always ship with it, and the signed manifest carries depends_on for client enforcement.
CREATE TABLE IF NOT EXISTS module_metadata (
    module_name TEXT PRIMARY KEY,
    depends_on  TEXT[] NOT NULL DEFAULT '{}',
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Seed the known edge. Idempotent.
INSERT INTO module_metadata (module_name, depends_on)
VALUES ('commission', ARRAY['combat', 'mining'])
ON CONFLICT (module_name) DO UPDATE SET depends_on = EXCLUDED.depends_on, updated_at = now();
