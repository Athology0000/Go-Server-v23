ALTER TABLE content_manifests
    ADD COLUMN IF NOT EXISTS min_loader_version TEXT NOT NULL DEFAULT '';
