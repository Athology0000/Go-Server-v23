-- Move ephemeral Redis state into Postgres so the server needs only one datastore.
-- Two things lived in Redis: fixed-window rate-limit counters and short-lived auth
-- challenges. Both are key -> value-with-expiry, expressed here as tables with an
-- expires_at column; a small background sweeper deletes expired rows.

CREATE TABLE IF NOT EXISTS rate_limits (
    key        TEXT PRIMARY KEY,
    count      INTEGER NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_rate_limits_expires_at ON rate_limits(expires_at);

CREATE TABLE IF NOT EXISTS auth_challenges (
    device_id  TEXT PRIMARY KEY,
    challenge  TEXT NOT NULL,
    source_ip  TEXT NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_auth_challenges_expires_at ON auth_challenges(expires_at);
