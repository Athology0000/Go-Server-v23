-- Liveness: timestamp of the last heartbeat. Backfill existing rows to created_at
-- so already-open sessions are evaluated from their creation, not the upgrade time.
ALTER TABLE sessions
    ADD COLUMN last_heartbeat_at TIMESTAMPTZ NOT NULL DEFAULT now();
UPDATE sessions SET last_heartbeat_at = created_at;

-- Per-heartbeat activity for the stats page. activity is whatever the loader sends,
-- stored verbatim as JSONB so richer macro/duration/event payloads need no new migration.
CREATE TABLE session_activity (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id  UUID NOT NULL REFERENCES sessions(id),
    account_id  UUID NOT NULL REFERENCES accounts(id),
    device_id   UUID NOT NULL REFERENCES devices(id),
    activity    JSONB NOT NULL DEFAULT '[]',
    reported_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_session_activity_account ON session_activity(account_id, reported_at DESC);
CREATE INDEX idx_session_activity_session ON session_activity(session_id);
