-- Signed monotonic per-channel ordering key for rollback/downgrade protection.
-- Existing rows get 0 (treated as oldest); new manifests are assigned a real epoch
-- (admin: creation-time millis; stable filesystem manifest: newest content mtime).
ALTER TABLE content_manifests ADD COLUMN epoch BIGINT NOT NULL DEFAULT 0;
