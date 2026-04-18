CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE accounts (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username      TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    email         TEXT,
    status        TEXT NOT NULL DEFAULT 'active'
                  CHECK (status IN ('active','suspended','banned')),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE license_keys (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    key_hash      TEXT UNIQUE NOT NULL,
    plan_tier     TEXT NOT NULL,
    status        TEXT NOT NULL DEFAULT 'available'
                  CHECK (status IN ('available','redeemed','revoked')),
    redeemed_by   UUID REFERENCES accounts(id),
    redeemed_at   TIMESTAMPTZ,
    enrollment_ip TEXT,
    created_by    TEXT NOT NULL,
    notes         TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE licenses (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id       UUID UNIQUE NOT NULL REFERENCES accounts(id),
    plan_tier        TEXT NOT NULL,
    status           TEXT NOT NULL DEFAULT 'active'
                     CHECK (status IN ('active','expired','suspended','revoked','trial')),
    starts_at        TIMESTAMPTZ NOT NULL,
    expires_at       TIMESTAMPTZ,
    grace_expires_at TIMESTAMPTZ,
    max_devices      INT,
    notes            TEXT,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE devices (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id               UUID NOT NULL REFERENCES accounts(id),
    binding_status           TEXT NOT NULL DEFAULT 'unbound'
                             CHECK (binding_status IN ('unbound','hwid_pending','fully_bound','suspended','banned')),
    hwid_hash                TEXT,
    minecraft_username       TEXT,
    enrollment_ip            TEXT,
    device_secret_encrypted  BYTEA NOT NULL,
    failed_attempts          INT NOT NULL DEFAULT 0,
    last_seen_ip             TEXT,
    last_login_at            TIMESTAMPTZ,
    binding_reset_at         TIMESTAMPTZ,
    binding_reset_by         TEXT,
    created_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at               TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE sessions (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_token_hash     TEXT UNIQUE NOT NULL,
    device_id              UUID NOT NULL REFERENCES devices(id),
    account_id             UUID NOT NULL REFERENCES accounts(id),
    plan_tier              TEXT NOT NULL,
    enabled_modules        JSONB NOT NULL DEFAULT '[]',
    enabled_features       JSONB NOT NULL DEFAULT '[]',
    entitlement_expires_at TIMESTAMPTZ,
    expires_at             TIMESTAMPTZ NOT NULL,
    revoked                BOOLEAN NOT NULL DEFAULT false,
    last_seen_ip           TEXT,
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE entitlements (
    plan_tier         TEXT PRIMARY KEY,
    enabled_features  JSONB NOT NULL DEFAULT '[]',
    enabled_modules   JSONB NOT NULL DEFAULT '[]',
    native_components JSONB NOT NULL DEFAULT '[]',
    content_channel   TEXT NOT NULL DEFAULT 'stable',
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE plan_overrides (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id          UUID UNIQUE NOT NULL REFERENCES accounts(id),
    additional_modules  JSONB NOT NULL DEFAULT '[]',
    removed_modules     JSONB NOT NULL DEFAULT '[]',
    additional_features JSONB NOT NULL DEFAULT '[]',
    removed_features    JSONB NOT NULL DEFAULT '[]',
    notes               TEXT,
    created_by          TEXT NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE content_manifests (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    build_id          TEXT NOT NULL,
    channel           TEXT NOT NULL,
    modules           JSONB NOT NULL DEFAULT '[]',
    native_components JSONB NOT NULL DEFAULT '[]',
    signature         TEXT NOT NULL,
    expires_at        TIMESTAMPTZ NOT NULL,
    revoked           BOOLEAN NOT NULL DEFAULT false,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE admin_tokens (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token_hash     TEXT UNIQUE NOT NULL,
    admin_username TEXT NOT NULL,
    role           TEXT NOT NULL CHECK (role IN ('super_admin','support','viewer')),
    expires_at     TIMESTAMPTZ NOT NULL,
    revoked        BOOLEAN NOT NULL DEFAULT false,
    last_used_at   TIMESTAMPTZ,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE audit_log (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_type   TEXT NOT NULL,
    account_id   UUID REFERENCES accounts(id),
    device_id    UUID REFERENCES devices(id),
    admin_name   TEXT,
    ip           TEXT,
    details      JSONB,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_devices_account_id ON devices(account_id);
CREATE INDEX idx_sessions_token_hash ON sessions(session_token_hash);
CREATE INDEX idx_sessions_device_id ON sessions(device_id);
CREATE INDEX idx_audit_log_created_at ON audit_log(created_at);
CREATE INDEX idx_audit_log_account_id ON audit_log(account_id);
