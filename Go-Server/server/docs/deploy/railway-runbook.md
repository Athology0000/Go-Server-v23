# Phantom Server — Railway Deploy + Live Runbook

Date: 2026-06-15

Deploys the Go auth/content server to Railway and validates the full
custody → heartbeat-lease → revocation flow against the live instance (North Star).
Everything except the credentialed steps (creating the Railway project, pasting
secrets, clicking deploy) is pre-scripted here.

`railway.toml` (this directory) configures the build (Dockerfile) and a `/health`
healthcheck. The admin API is mounted on the public port, so only port **8080** is
exposed.

---

## 0. Prerequisites

- A Railway account + project, with this repo connected and the service **root
  directory set to `Go-Server/server`** (so `railway.toml` + `Dockerfile` are found).
- `openssl` and `curl` locally (for secret generation + the live test).
- The server's domain after first deploy, referred to below as `$BASE`
  (e.g. `https://phantom-server-production.up.railway.app`).

> **Dockerfile Go version:** the builder pins `golang:1.22-alpine`. If `go.mod`'s
> `go` directive is newer than 1.22, bump the builder image to match or the build
> fails. (Pre-existing; verify before the first deploy.)

---

## 1. Provision the datastore

In the Railway project, add one plugin:

1. **PostgreSQL** → exposes `DATABASE_URL`.

(No Redis: rate-limit counters and short-lived auth challenges live in Postgres
as of migration 011 — the server needs only one datastore.)

---

## 2. Generate secrets

From `server/`:

```bash
./docker/generate-secrets.sh > .env.railway
cat .env.railway
```

This emits `MASTER_KEY`, `SERVER_PEPPER` (base64 32-byte), `MANIFEST_SIGNING_KEY`
(base64 32-byte Ed25519 seed), and `ADMIN_API_SECRET` (hex).

### ⚠️ CRITICAL: the signing key must match the client's pinned public key

The loader pins the manifest **public** key in
`ManifestVerifier.java` (`RELEASE_PINNED_KEY = 2sRfSwCZJKJLJ1T/r1gSG2/0oVjGN57iszhpsLf48K0=`)
**and** baked into `phantom_auth.dll`. A manifest signed with a `MANIFEST_SIGNING_KEY`
whose public half ≠ that pin fails verification on every client (`BAD_SIGNATURE`).

So **do one of**:

- **(A) Reuse the existing release signing key** that already corresponds to the
  pinned public key (recommended for an existing client fleet). Do **not** let
  `generate-secrets.sh` overwrite it — paste the known-good value instead.
- **(B) Use a fresh key** (greenfield / new client build): then update
  `RELEASE_PINNED_KEY` (Java) **and** the pinned key in the native, rebuild
  `phantom_auth.dll`, and ship a new client. Confirm the public half before deploy:

  ```bash
  # Print the base64 public key for a given MANIFEST_SIGNING_KEY seed (base64):
  python3 - <<'PY'
  import base64, nacl.signing  # pip install pynacl
  seed_b64 = "PASTE_MANIFEST_SIGNING_KEY"
  pub = nacl.signing.SigningKey(base64.b64decode(seed_b64)).verify_key.encode()
  print(base64.b64encode(pub).decode())
  PY
  ```

  After deploy, `GET $BASE/health` returns `manifest_public_key` — it must equal the
  client's pinned key.

---

## 3. Set service variables

In the Railway service **Variables**, set:

| Variable | Value |
|---|---|
| `MASTER_KEY` | from `.env.railway` |
| `SERVER_PEPPER` | from `.env.railway` |
| `MANIFEST_SIGNING_KEY` | **the release key** (see §2) |
| `ADMIN_API_SECRET` | from `.env.railway` |
| `DB_URL` | `${{Postgres.DATABASE_URL}}` |
| `APP_ENV` | `production` |
| `BASE_URL` | `$BASE` (your Railway https domain — must be `https://` in production) |
| `PUBLIC_PORT` | `8080` |
| `STRICT_SESSION_IP` | `false` (Railway edges rotate egress IPs) |
| `SESSION_TTL_HOURS` | `12` (absolute session cap; optional, this is the default) |
| `HEARTBEAT_LIVENESS_WINDOW_SECONDS` | `900` (optional, default; session dies this long after the last heartbeat) |
| `HWID_TOFU_ENABLED` | defaults **on** when `APP_ENV=production` (device hardware pinning). Set `false` to disable, or `true` in a non-prod env to enable. Recovery on hardware change = admin reset + re-enroll (§8). |

> Railway targets port **8080**; the server reads `PUBLIC_PORT`. If the healthcheck
> fails, confirm the service's exposed/target port is 8080.

---

## 4. Deploy + verify

Deploy (push to the connected branch, or "Deploy" in Railway). On boot the server
runs `db.RunMigrations` against the fresh Postgres, building schema 001..009.

```bash
curl -s $BASE/health | jq
# { "ok": true, "service": "phantom-public-api", "manifest_public_key": "..." , ... }
```

Confirm `manifest_public_key` == the client's pinned key (§2). If not, stop — fix
the signing key before anything else.

---

## 5. Bootstrap admin + a license key

```bash
# Create the first super_admin (one-time; guarded by ADMIN_API_SECRET).
curl -s -X POST $BASE/admin/setup \
  -H "Authorization: Bearer $ADMIN_API_SECRET" \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"<a-strong-12+char-password>"}' | jq

# Log in to get an admin bearer token.
ADMIN_TOKEN=$(curl -s -X POST $BASE/admin/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"<same-password>"}' | jq -r .token)

# Mint a pro license key.
curl -s -X POST $BASE/admin/license-keys \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"plan_tier":"pro","count":1,"duration_days":30}' | jq
# -> { "keys": ["PHANTOM-...."] }
```

---

## 6. Live custody → heartbeat-lease → revocation test (the North Star)

Run the loader against `$BASE` (point `LoaderConfig` server URL at it, or use a
local script that hits the same endpoints). Validate, in order:

1. **Enrollment / custody** — first-run redeem of the license key returns a
   `device_secret`; it is stored in the native sealed store (never in the JVM heap).
2. **Auth** — `/auth/start` → native HMAC proof → `/auth/finish` returns a session
   token + `manifest_url` + entitlements. The manifest signature verifies against the
   pinned key (confirms §2).
3. **Heartbeat liveness (SP1)** — while the loader heartbeats, the session stays open.
   Watch a session row: `last_heartbeat_at` advances each beat but `expires_at` does
   **not** move. Stop the loader → the session is rejected within
   `HEARTBEAT_LIVENESS_WINDOW_SECONDS` (default 15 min), not the old rolling hour.
4. **Activity stats (SP1)** — `SELECT * FROM session_activity WHERE session_id = …`
   shows one row per heartbeat (foundation for the stats page).
5. **Revocation (SP1)** — `PATCH /admin/licenses/:id/status {"status":"revoked"}`
   (or ban the account); the next heartbeat returns `session_invalid`, the session is
   marked revoked, and armed macros force-disarm.

### Rollback protection (SP2) — gated on the native rebuild

The server now signs a monotonic `epoch` and the Java verifier enforces a
per-channel high-water-mark. **Full rollback protection on the release path also
needs `phantom_auth.dll` rebuilt** with the epoch canonicalizer + native
high-water-mark (see task #2 / the rollback design spec). **Until that ships, keep
serving the previous (non-epoch) signed contract, or the release native path will
`BAD_SIGNATURE`.** Do not flip to the new contract in production before the rebuilt
native is verified in-game.

---

## 7. Rollback / teardown

- A bad deploy: redeploy the previous Railway deployment (instant rollback).
- Rotating a leaked `ADMIN_API_SECRET` / `MASTER_KEY`: set the new value and
  redeploy. Rotating `MASTER_KEY` invalidates stored device secrets (users
  re-enroll). Rotating `MANIFEST_SIGNING_KEY` requires a client pin update + rebuild.

---

## 8. HWID hardware-change recovery (when `HWID_TOFU_ENABLED`)

With HWID pinning on, a device's hardware ID is pinned on the first verify-session
and must match afterward. A **legitimate hardware change** — renaming the computer,
replacing the C: drive, or reinstalling Windows — changes the HWID, so the next
verify-session returns `authorized:false, reason:"hwid_mismatch"` and the loader
locks down. This is by design; recovery is a one-step admin action + re-enroll:

1. **Diagnose** via the audit log. Mismatch events carry sanitized prefixes (never the
   full hash): `event_type='auth.verify_session.fail'` with `reason='hwid_mismatch'`
   (and `stored_prefix`/`got_prefix`); the original pin is
   `event_type='auth.verify_session.hwid_pinned'`.
2. **Reset the device** (clears the pinned HWID; support+ admin token):
   ```bash
   curl -s -X POST $BASE/admin/devices/<device_id>/reset \
     -H "Authorization: Bearer $ADMIN_TOKEN"
   ```
   This sets `binding_status='unbound'`, `hwid_hash=NULL`.
3. **User re-enrolls** on the new hardware (redeem a key, or username/password
   handshake). Re-enroll returns the device secret again — it is stored server-side
   under `MASTER_KEY`, not bound to the old machine — so the user can re-auth.
4. **Re-auth** → the next verify-session **re-pins the new HWID** (TOFU first-sight).
   The account and license are untouched; no data loss.

> The reset does not regenerate the device secret, so the old machine could still hold
> it — but the old machine's (now different) HWID fails the pin, so it cannot use the
> session. To fully invalidate the old machine, delete the device row and re-enroll
> from scratch.
