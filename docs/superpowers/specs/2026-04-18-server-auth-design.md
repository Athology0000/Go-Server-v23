# Server Authentication & Entitlements â€” Design Spec

**Date:** 2026-04-18
**Status:** Approved for implementation

---

## Overview

A zero-trust Go backend service that authenticates enrolled Phantom client devices,
validates entitlements, and delivers signed content manifests for protected modules
and native components. No protected payload is ever served before both authentication
and entitlement validation succeed.

**Stack:** Go + Fiber Â· PostgreSQL Â· Redis Â· Caddy (TLS reverse proxy) Â· Windows VPS

---

## Architecture

Single Go binary (`phantom-server.exe`) split into clean internal packages:

```
cmd/server/          â† entry point, service wiring
internal/
  enrollment/        â† redeem, handshake
  auth/              â† start, finish, challenge
  entitlement/       â† plan resolution, overrides
  content/           â† manifest, module, native download
  admin/             â† admin API, token auth, role enforcement
  audit/             â† append-only audit log writer
  crypto/            â† argon2id, HMAC, AES-GCM, Ed25519 helpers
  db/                â† PostgreSQL queries (sqlc or pgx)
  cache/             â† Redis helpers (challenges, rate limits)
  middleware/        â† session auth, admin token auth, rate limit, zero-trust
```

**Two listeners:**
- `:8080` â€” public client-facing routes (proxied by Caddy over HTTPS)
- `127.0.0.1:8081` â€” admin routes (localhost only, admin token auth required)

---

## Device Binding Lifecycle

Devices progress through five states stored in `devices.binding_status`:

| State | Trigger | What is locked |
|---|---|---|
| `unbound` | License key redeemed in panel | `enrollment_ip` captured from connection |
| `hwid_pending` | Bootstrapper handshake accepted | `hwid_hash` bound to account |
| `fully_bound` | First `/auth/finish` succeeds | `minecraft_username` locked; IP check released |
| `reset` | Admin resets binding | All bindings cleared, returns to `unbound` |
| `suspended` / `banned` | Admin action | All auth blocked |

**IP enforcement rule:** `enrollment_ip` must match the live connection IP at every step
from key redemption through the first `/auth/finish`. After `fully_bound`, IP is no
longer checked â€” users may authenticate from any network. The stored `enrollment_ip`
is retained for audit purposes only.

---

## Enrollment Flow

### Step 1 â€” License key redemption (`POST /enroll/redeem`, panel)
1. User submits license key + Phantom credentials in the panel
2. Server hashes the key, looks up `license_keys` record
3. Validates: key exists, status is `available`, account is active
4. Captures real source IP from connection (never from request body)
5. Creates `devices` record: `binding_status=unbound`, `enrollment_ip=<source_ip>`
6. Marks key `redeemed`, writes audit log
7. Admin must have already created a `licenses` record for the account (via `/admin/accounts` or `/admin/keys` flow) â€” redemption validates the key exists, the license record is a prerequisite

### Step 2 â€” Bootstrapper handshake (`POST /enroll/handshake`)
1. Bootstrapper sends: `phantom_username`, `phantom_password`, `hwid`
2. Server validates credentials (argon2id verify)
3. Reads real source IP; verifies it matches `devices.enrollment_ip` â€” reject if different
4. Normalizes and HMAC-hashes incoming HWID with `SERVER_PEPPER`
5. Stores `hwid_hash`, generates 32-byte `device_secret` (crypto/rand)
6. Encrypts `device_secret` with AES-256-GCM using `MASTER_KEY`, stores in `devices`
7. Returns `device_secret` (plaintext, base64) **once** â€” never transmitted again
8. Sets `binding_status=hwid_pending`, writes audit log
9. Bootstrapper stores `device_secret` encrypted via Windows DPAPI

---

## Authentication Flow

### `/auth/start`
**Input:** `phantom_username`, `hwid`, `minecraft_username`, `client_version`, `bootstrap_build_id`

Server actions:
1. Normalize username; load device record
2. Reject if device not found, not in `hwid_pending` or `fully_bound` state
3. Reject if account/device is suspended or banned
4. Read real source IP from connection
5. If `hwid_pending`: verify source IP matches `enrollment_ip`
6. Normalize + HMAC-hash incoming HWID; compare to stored `hwid_hash` (constant-time)
7. If `fully_bound`: verify `minecraft_username` matches stored value
8. Generate 32-byte challenge (crypto/rand, base64), store in Redis with 30s TTL bound to source IP
9. Return `{ challenge, expires_in: 30, auth_context_id }`

### `/auth/finish`
**Input:** `phantom_username`, `proof` (HMAC-SHA256 hex)

Server actions:
1. Normalize username; load pending challenge from Redis
2. Reject if no challenge, expired, or already used
3. Read real source IP; reject if different from challenge source IP
4. Decrypt `device_secret` from DB using `MASTER_KEY`
5. Compute `expected = HMAC-SHA256(device_secret, challenge)`
6. Compare proof using constant-time comparison; reject if mismatch
7. Mark challenge used / delete from Redis
8. Increment `devices.failed_attempts` on failure; auto-suspend at 5 failures

**On success (first auth â€” `hwid_pending` device):**
- Lock `minecraft_username` from request into device record
- Set `binding_status=fully_bound`
- Clear IP enforcement (enrollment_ip kept for audit, no longer validated)

**On success (any auth):**
- Run entitlement validation (see below)
- If authorized: create session, return session token + manifest URL
  - Manifest URL points to the latest non-expired, non-revoked `content_manifests` record matching the account's `content_channel` â€” selected server-side, never client-chosen
- If not authorized: return `{ authenticated: true, authorized: false, reason: "entitlement_inactive" }`

---

## Entitlement Validation

Runs as a required step after successful proof verification. Authentication alone
does not grant access to protected content.

Validation checks (all must pass):
1. License record exists for account
2. License status is `active` or within `grace_expires_at`
3. License not expired
4. Plan tier is valid and has an `entitlements` record
5. Device/account not suspended or revoked
6. Max device count not exceeded (if enforced)

**Effective entitlement resolution:**
```
base = entitlements[plan_tier]
effective_modules = (base.enabled_modules + override.additional_modules) - override.removed_modules
effective_features = (base.enabled_features + override.additional_features) - override.removed_features
```

Session record stores the resolved `enabled_modules` + `enabled_features` + `plan_tier`
+ `entitlement_expires_at`.

---

## Content Delivery

All content endpoints require a valid session token (Bearer header). Zero trust:
session is re-verified against DB on every request â€” no trust in token claims alone.

### `GET /content/manifest/:id`
1. Verify session token (DB lookup, check `revoked`, check `expires_at`)
2. Re-check entitlement from DB (not from session cache)
3. Load manifest record; reject if expired or revoked
4. Verify requested manifest is allowed by current entitlement
5. Return signed manifest JSON

### `GET /content/module/:name` and `GET /content/native/:name`
1. Verify session (DB)
2. Re-check entitlement (DB)
3. Verify requested name appears in current non-expired manifest AND in `enabled_modules`
4. Stream file from `content/modules/` or `content/native/`

---

## Zero-Trust Admin API (`127.0.0.1:8081`)

Every admin request requires `Authorization: Bearer <admin-token>` regardless of
source IP. Localhost binding is defense-in-depth only â€” not the trust gate.

**Admin roles:**

| Role | Permissions |
|---|---|
| `super_admin` | Full access â€” all endpoints |
| `support` | Reset bindings, view accounts/sessions/audit â€” no key gen, no content upload |
| `viewer` | Read-only â€” accounts, audit, sessions |

**Admin token policy:** 32 random bytes (crypto/rand), stored as SHA-256 hash in
`admin_tokens` table, 8h expiry, revocable. Every request writes `last_used_at`.

### Admin endpoints

| Method | Path | Min role |
|---|---|---|
| `POST` | `/admin/accounts` | super_admin |
| `GET` | `/admin/accounts` | viewer |
| `PATCH` | `/admin/accounts/:id` | support |
| `POST` | `/admin/keys` | super_admin |
| `GET` | `/admin/keys` | viewer |
| `PATCH` | `/admin/keys/:id` | super_admin |
| `POST` | `/admin/devices/:id/reset` | support | Clears: `hwid_hash`, `minecraft_username`, `enrollment_ip`, `failed_attempts`; sets `binding_status=unbound`; writes audit log with admin identity |
| `GET` | `/admin/sessions` | viewer |
| `DELETE` | `/admin/sessions/:id` | super_admin |
| `POST` | `/admin/content/modules` | super_admin |
| `POST` | `/admin/content/native` | super_admin |
| `GET` | `/admin/audit` | viewer |
| `GET` | `/admin/entitlements/:tier` | viewer |
| `PUT` | `/admin/entitlements/:tier` | super_admin |
| `POST` | `/admin/overrides/:account_id` | super_admin |
| `DELETE` | `/admin/overrides/:account_id` | super_admin |
| `POST` | `/admin/tokens` | super_admin |
| `DELETE` | `/admin/tokens/:id` | super_admin |

All admin actions write to `audit_log` with admin identity, action, and affected resource.

---

## Data Model

### `accounts`
```sql
id            UUID PRIMARY KEY DEFAULT gen_random_uuid()
username      TEXT UNIQUE NOT NULL         -- normalized (lowercase, trimmed)
password_hash TEXT NOT NULL                -- argon2id
email         TEXT
status        TEXT NOT NULL DEFAULT 'active'  -- active | suspended | banned
created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
```

### `license_keys`
```sql
id              UUID PRIMARY KEY DEFAULT gen_random_uuid()
key_hash        TEXT UNIQUE NOT NULL       -- SHA-256 of raw key, never store raw
plan_tier       TEXT NOT NULL
status          TEXT NOT NULL DEFAULT 'available'  -- available | redeemed | revoked
redeemed_by     UUID REFERENCES accounts(id)
redeemed_at     TIMESTAMPTZ
enrollment_ip   TEXT                       -- captured at redemption
created_by      TEXT NOT NULL              -- admin username
notes           TEXT
created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
```

### `devices`
```sql
id                      UUID PRIMARY KEY DEFAULT gen_random_uuid()
account_id              UUID NOT NULL REFERENCES accounts(id)
binding_status          TEXT NOT NULL DEFAULT 'unbound'
hwid_hash               TEXT                   -- HMAC-SHA256(pepper, normalized_hwid)
minecraft_username      TEXT                   -- locked at fully_bound
enrollment_ip           TEXT                   -- validated through binding, kept for audit
device_secret_encrypted BYTEA NOT NULL         -- AES-256-GCM encrypted
failed_attempts         INT NOT NULL DEFAULT 0
last_seen_ip            TEXT
last_login_at           TIMESTAMPTZ
binding_reset_at        TIMESTAMPTZ
binding_reset_by        TEXT                   -- admin username
created_at              TIMESTAMPTZ NOT NULL DEFAULT now()
updated_at              TIMESTAMPTZ NOT NULL DEFAULT now()
```

### `sessions`
```sql
id                    UUID PRIMARY KEY DEFAULT gen_random_uuid()
session_token_hash    TEXT UNIQUE NOT NULL   -- SHA-256 of raw token
device_id             UUID NOT NULL REFERENCES devices(id)
account_id            UUID NOT NULL REFERENCES accounts(id)
plan_tier             TEXT NOT NULL
enabled_modules       JSONB NOT NULL DEFAULT '[]'
enabled_features      JSONB NOT NULL DEFAULT '[]'
entitlement_expires_at TIMESTAMPTZ
expires_at            TIMESTAMPTZ NOT NULL
revoked               BOOLEAN NOT NULL DEFAULT false
last_seen_ip          TEXT
created_at            TIMESTAMPTZ NOT NULL DEFAULT now()
```

### `entitlements`
```sql
plan_tier          TEXT PRIMARY KEY
enabled_features   JSONB NOT NULL DEFAULT '[]'
enabled_modules    JSONB NOT NULL DEFAULT '[]'
native_components  JSONB NOT NULL DEFAULT '[]'
content_channel    TEXT NOT NULL DEFAULT 'stable'
updated_at         TIMESTAMPTZ NOT NULL DEFAULT now()
```

### `plan_overrides`
```sql
id                  UUID PRIMARY KEY DEFAULT gen_random_uuid()
account_id          UUID UNIQUE NOT NULL REFERENCES accounts(id)
additional_modules  JSONB NOT NULL DEFAULT '[]'
removed_modules     JSONB NOT NULL DEFAULT '[]'
additional_features JSONB NOT NULL DEFAULT '[]'
removed_features    JSONB NOT NULL DEFAULT '[]'
notes               TEXT
created_by          TEXT NOT NULL
created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
```

### `licenses`
```sql
id                UUID PRIMARY KEY DEFAULT gen_random_uuid()
account_id        UUID UNIQUE NOT NULL REFERENCES accounts(id)
plan_tier         TEXT NOT NULL
status            TEXT NOT NULL DEFAULT 'active'  -- active | expired | suspended | revoked | trial
starts_at         TIMESTAMPTZ NOT NULL
expires_at        TIMESTAMPTZ
grace_expires_at  TIMESTAMPTZ
max_devices       INT
notes             TEXT
created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
```

### `content_manifests`
```sql
id                UUID PRIMARY KEY DEFAULT gen_random_uuid()
build_id          TEXT NOT NULL
channel           TEXT NOT NULL
modules           JSONB NOT NULL DEFAULT '[]'
native_components JSONB NOT NULL DEFAULT '[]'
signature         TEXT NOT NULL              -- Ed25519 signature, base64
expires_at        TIMESTAMPTZ NOT NULL
revoked           BOOLEAN NOT NULL DEFAULT false
created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
```

### `admin_tokens`
```sql
id             UUID PRIMARY KEY DEFAULT gen_random_uuid()
token_hash     TEXT UNIQUE NOT NULL   -- SHA-256 of raw token
admin_username TEXT NOT NULL
role           TEXT NOT NULL          -- super_admin | support | viewer
expires_at     TIMESTAMPTZ NOT NULL
revoked        BOOLEAN NOT NULL DEFAULT false
last_used_at   TIMESTAMPTZ
created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
```

### `audit_log`
```sql
id           UUID PRIMARY KEY DEFAULT gen_random_uuid()
event_type   TEXT NOT NULL
account_id   UUID REFERENCES accounts(id)
device_id    UUID REFERENCES devices(id)
admin_name   TEXT
ip           TEXT
details      JSONB
created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
```

---

## Crypto Primitives

| Purpose | Algorithm / Parameters |
|---|---|
| Password hashing | Argon2id â€” memory 64MB, iterations 3, parallelism 2 |
| HWID storage | `HMAC-SHA256(SERVER_PEPPER, normalize(hwid))` |
| Device secret generation | `crypto/rand` 32 bytes |
| Device secret at rest | AES-256-GCM with `MASTER_KEY` |
| Auth proof | `HMAC-SHA256(device_secret, challenge)` â€” constant-time compare |
| Challenge | `crypto/rand` 32 bytes, base64, 30s TTL in Redis |
| Session / admin tokens | `crypto/rand` 32 bytes, stored as SHA-256 hash |
| License keys | `crypto/rand` 32 bytes, base32 formatted as `PHANTOM-XXXX-XXXX-XXXX-XXXX` |
| Manifest signing | Ed25519 â€” server signs with `MANIFEST_SIGNING_KEY`, client verifies with embedded public key |

---

## Rate Limiting

Redis-backed sliding window, keys never include client-provided identity alone:

| Endpoint | Limit | Key |
|---|---|---|
| `POST /enroll/redeem` | 5 / hour | per source IP |
| `POST /enroll/handshake` | 10 / hour | per source IP + username |
| `POST /auth/start` | 10 / min | per source IP + username |
| `POST /auth/finish` | 5 / min | per source IP + username |
| `GET /content/*` | 60 / min | per session token hash |
| Admin endpoints | 30 / min | per admin token hash |

Failed `/auth/finish` proofs also increment `devices.failed_attempts`.
At **5 failures** device is auto-suspended and an audit log entry is written.
Suspension requires manual admin lift.

---

## Error Handling

External responses are always generic â€” clients never learn which field failed:

```json
{ "error": "authentication_failed" }
{ "error": "not_authorized" }
{ "error": "enrollment_failed" }
{ "error": "rate_limited" }
```

Exception â€” entitlement failure after successful auth:
```json
{ "authenticated": true, "authorized": false, "reason": "entitlement_inactive" }
```

Precise failure reasons are recorded in `audit_log` only.

Constant-time comparison is used for all secret comparisons (proof, token lookup)
to prevent timing oracles.

---

## Secrets Management

All secrets are environment variables set on the Windows service â€” never in files
committed to git.

| Variable | Purpose |
|---|---|
| `MASTER_KEY` | AES-256-GCM key for device secret encryption (base64, 32 bytes) |
| `SERVER_PEPPER` | HMAC key for HWID hashing (base64, 32 bytes) |
| `MANIFEST_SIGNING_KEY` | Ed25519 private key (base64) |
| `DB_URL` | PostgreSQL connection string |
| `REDIS_URL` | Redis connection string |
| `ADMIN_API_SECRET` | Bootstrap secret for creating the first super_admin token |

---

## Deployment Layout

```
C:\phantom-server\
  phantom-server.exe       â† single static binary (go build)
  logs\
  content\
    modules\              â† uploaded module payloads
    native\               â† uploaded native components
```

- **Caddy** as HTTPS reverse proxy on port 443 â†’ forwards to `localhost:8080`
- Admin API on `127.0.0.1:8081` â€” not proxied, manage via SSH tunnel
- PostgreSQL + Redis as Windows services, listening on localhost only
- `phantom-server.exe` runs as a Windows service via **NSSM**
- TLS 1.2 minimum, HSTS enabled via Caddy

---

## Logging

Structured JSON logs (Go `slog`). Written to `logs\server.log`, rotated daily.

Always log:
- Auth start / finish success and failure (with audit reason)
- IP mismatch at any enrollment or auth stage
- HWID mismatch
- Minecraft username mismatch
- Expired or reused challenge
- Invalid proof
- Entitlement failure reason
- Content download attempts and denials
- Session revocations
- Admin actions (also written to `audit_log` table)
- Device auto-suspension on failed attempts

Never log:
- Raw device secret
- Raw session or admin token
- Raw HMAC proof
- Raw HWID

---

## Acceptance Criteria

- [ ] Enrollment requires matching IP through all pre-binding steps
- [ ] HWID is stored as HMAC hash, never raw
- [ ] Device secret generated server-side, returned once, encrypted at rest
- [ ] Auth uses one-time challenge with 30s TTL, single-use, IP-bound
- [ ] Proof verified with constant-time comparison
- [ ] `minecraft_username` locked at first `/auth/finish`; IP check released after
- [ ] Entitlement validation is a distinct required step after auth
- [ ] Session re-verified against DB on every protected request
- [ ] Entitlement re-checked at content download time
- [ ] No protected content served to expired, revoked, or suspended accounts
- [ ] Admin API requires signed token on every request regardless of source IP
- [ ] Admin roles enforced per endpoint
- [ ] All admin actions written to audit_log
- [ ] Device auto-suspended after 5 failed proof attempts
- [ ] Rate limiting applied to all public endpoints
- [ ] Generic error messages externally; precise reasons in audit log only
- [ ] All secrets in environment variables, never in code or git
