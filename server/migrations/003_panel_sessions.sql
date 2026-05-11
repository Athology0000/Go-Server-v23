CREATE TABLE panel_sessions (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token_hash  TEXT UNIQUE NOT NULL,
    account_id  UUID NOT NULL REFERENCES accounts(id),
    expires_at  TIMESTAMPTZ NOT NULL,
    revoked     BOOLEAN NOT NULL DEFAULT false,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_panel_sessions_token ON panel_sessions(token_hash);
CREATE INDEX idx_accounts_email ON accounts(LOWER(email));
