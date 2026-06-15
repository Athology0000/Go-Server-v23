-- Forge builds: a delivered payload forged into a watermarked, approval-gated module build.
-- Lifecycle: building -> pending_approval -> (approve) live | (deny) denied | failed;
-- approving a new build moves the prior live build for that module to 'superseded'.
CREATE TABLE IF NOT EXISTS content_builds (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    build_id    TEXT UNIQUE NOT NULL,           -- also the jar watermark id + dll marker id
    module      TEXT NOT NULL,
    status      TEXT NOT NULL DEFAULT 'building'
                CHECK (status IN ('building','pending_approval','live','denied','failed','superseded')),
    jar_path    TEXT,
    jar_sha256  TEXT,
    dll_path    TEXT,
    dll_sha256  TEXT,
    error       TEXT,
    created_by  TEXT,                            -- forge CLI operator label
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    decided_by  TEXT,                            -- superadmin who approved/denied
    decided_at  TIMESTAMPTZ,
    notes       TEXT
);

CREATE INDEX IF NOT EXISTS idx_content_builds_status ON content_builds(status);
CREATE INDEX IF NOT EXISTS idx_content_builds_module_status ON content_builds(module, status);
