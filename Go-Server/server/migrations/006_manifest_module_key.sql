ALTER TABLE content_manifests
    ADD COLUMN IF NOT EXISTS module_key TEXT NOT NULL DEFAULT '';
