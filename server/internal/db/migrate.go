
package db

import (
	"context"
	"log"

	"github.com/jackc/pgx/v5/pgxpool"
)

func RunMigrations(ctx context.Context, pool *pgxpool.Pool) error {
	log.Println("running database migrations")

	_, err := pool.Exec(ctx, `
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS accounts (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username      TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    email         TEXT,
    status        TEXT NOT NULL DEFAULT 'active'
                  CHECK (status IN ('active','suspended','banned')),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS license_keys (
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

CREATE TABLE IF NOT EXISTS licenses (
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

CREATE TABLE IF NOT EXISTS devices (
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

CREATE TABLE IF NOT EXISTS sessions (
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

CREATE TABLE IF NOT EXISTS entitlements (
    plan_tier         TEXT PRIMARY KEY,
    enabled_features  JSONB NOT NULL DEFAULT '[]',
    enabled_modules   JSONB NOT NULL DEFAULT '[]',
    native_components JSONB NOT NULL DEFAULT '[]',
    manifest_id       TEXT,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS manifests (
    id          TEXT PRIMARY KEY,
    build_id    TEXT NOT NULL,
    channel     TEXT NOT NULL DEFAULT 'stable',
    content     JSONB NOT NULL,
    signature   TEXT NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS panel_sessions (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    panel_token_hash   TEXT UNIQUE NOT NULL,
    account_id         UUID NOT NULL REFERENCES accounts(id),
    expires_at         TIMESTAMPTZ NOT NULL,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS admin_users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username      TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role          TEXT NOT NULL DEFAULT 'viewer'
                  CHECK (role IN ('viewer','support','super_admin')),
    status        TEXT NOT NULL DEFAULT 'active'
                  CHECK (status IN ('active','disabled')),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS admin_sessions (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    admin_token_hash   TEXT UNIQUE NOT NULL,
    admin_user_id      UUID NOT NULL REFERENCES admin_users(id),
    expires_at         TIMESTAMPTZ NOT NULL,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event        TEXT NOT NULL,
    account_id   UUID,
    device_id    UUID,
    admin_id     UUID,
    ip           TEXT,
    metadata     JSONB NOT NULL DEFAULT '{}',
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);
`)

	if err != nil {
		return err
	}

	log.Println("database migrations complete")
	return nil
}
