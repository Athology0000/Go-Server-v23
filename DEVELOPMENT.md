# Go-Server-v23 Developer Guide

This repository contains three main pieces:

- `bootstrapper/` — Rust launcher that enrolls/authenticates the client, verifies/jars, and writes the Cobalt session token.
- `server/` — Go backend API and admin panel backend.
- `obfuscator/` — Java obfuscator tooling for post-build protection.

There are also two frontend apps under `server/panel/` and `server/admin/`.

---

## 1. High-level architecture

### `bootstrapper/`
This is the client-side loader.

Key files:
- `src/main.rs` — application entrypoint and step flow.
- `src/auth.rs` — calls `/auth/start` and `/auth/finish` on the server, computes HMAC proof, and returns session/auth results.
- `src/enrollment.rs` — first-time setup, license/key redemption, handshake enrollment.
- `src/manifest.rs` — downloads the content manifest and verifies an Ed25519 signature.
- `src/jar.rs` — downloads `cobalt.jar` and verifies SHA-256.
- `src/session.rs` — writes `config/cobalt/session.token` safely, with permissions.
- `src/credentials.rs` — stores and loads encrypted device credentials on the local machine.
- `src/launch.rs` — launches Minecraft with the session token path.
- `src/hwid.rs` — collects and normalizes the local HWID.

Build / run:
- Install Rust toolchain.
- From `bootstrapper/`: `cargo build --release`
- Executable: `target/release/bootstrapper`

### `server/`
This is the backend API and admin panel backend.

Key directories:
- `cmd/server/main.go` — server startup and route wiring.
- `internal/auth/` — authentication flow and auth endpoints.
- `internal/enrollment/` — enrollment and license redemption.
- `internal/content/` — content serving and manifest endpoints.
- `internal/entitlement/` — plan tier entitlement checks.
- `internal/db/` — database persistence and SQL helpers.
- `internal/middleware/` — request validation, bearer token parsing, session auth, rate limits, security headers.
- `internal/admin/` and `internal/panel/` — backend routes for admin/panel features.
- `internal/config/` — loads required server environment variables.
- `internal/crypto/` — token hashing, password hashing, AES/HMAC helpers.
- `internal/audit/` — audit logging.
- `internal/cache/` — challenge caching and rate limiting.

Other useful files:
- `server/go.mod` — Go module and dependencies.
- `server/CobaltSession-realauth.kt` — Java-side session loading logic for the client.

Build / run:
- Install Go 1.22 or later.
- From `server/`: `go mod tidy`
- Build server: `go build ./cmd/server`
- Start server executable and ensure env vars are set.

Environment variables required by the server:
- `MASTER_KEY` — base64, 32 bytes.
- `SERVER_PEPPER` — base64, 32 bytes.
- `MANIFEST_SIGNING_KEY` — base64, 64 bytes.
- `DB_URL`
- `REDIS_URL`
- `ADMIN_API_SECRET`
- Optional: `PUBLIC_PORT`, `ADMIN_PORT`, `CONTENT_DIR`, `BASE_URL`, `STRICT_SESSION_IP`, `APP_ENV`.

The config loader will panic if required env vars are missing.

### `server/panel/` and `server/admin/`
These are web frontends built with Vite / React / TypeScript.

If you want to work on the admin UI or panel UI, open the corresponding directory.

- `server/panel/` — main panel app.
- `server/admin/` — admin dashboard.

Each has its own `package.json`, `vite.config.ts`, and `src/` directory.

---

## 2. Where to start coding

### If you want to change authentication behavior
- `server/internal/auth/service.go` — main auth logic.
- `server/internal/auth/handler.go` — API handlers for `/auth/start`, `/auth/finish`, and verification endpoints.
- `bootstrapper/src/auth.rs` — client-side auth request flow.
- `bootstrapper/src/enrollment.rs` — enrollment flow for initial device pairing.

### If you want to improve manifest or content protection
- `server/internal/db/manifests.go` — manifest persistence.
- `bootstrapper/src/manifest.rs` — manifest download/verification logic.
- `server/internal/config/config.go` — server signing key config.
- `server/internal/auth/service.go` — where the manifest URL / signature are returned to the client.
- `bootstrapper/src/jar.rs` — binary download and SHA-256 verification.

### If you want to add or modify content endpoints
- `server/internal/content/handler.go` — manifest and module/native handlers.
- `server/internal/content/service.go` — authorization checks around content access.

### If you want to change enrollment/licensing
- `server/internal/enrollment/service.go` — license redemption and device binding.
- `server/internal/enrollment/handler.go` — enrollment REST endpoints.
- `bootstrapper/src/enrollment.rs` — client flow for redeeming or binding.

### If you want to work on the admin panel
- `server/internal/panel/` — backend panel auth and endpoints.
- `server/panel/src/` — frontend panel UI pages and components.
- `server/admin/src/` — admin UI.

---

## 3. Recommended first tasks

1. Pick a small change.
   - e.g. “better error message for invalid manifest signature” or “strengthen session file permissions”.
2. Locate the existing logic in the list above.
3. Edit the code and run the related binary.
4. Test both the server and bootstrapper flows together if the change spans both sides.

---

## 4. Useful notes

- `bootstrapper` is Rust and is independent of the backend code, but it communicates through the server API.
- `server` is Go and handles business logic, persistence, and content access.
- The Kotlin file `server/CobaltSession-realauth.kt` is client-side runtime code used by the Cobalt loader to read the session token.
- `obfuscator/` is separate and not required for core server/bootstrapper development.

---

## 5. Quick start commands

```bash
# prepare the Go server (inside server/)
cd server
go mod tidy
go build ./cmd/server

# prepare the Rust bootstrapper
cd ../bootstrapper
cargo build --release
```

If Go or Cargo are not installed, install them first.

---

## 6. Where to look for database and auth state

- `server/internal/db/` — SQL models for accounts, devices, sessions, licenses, manifests.
- `server/migrations/` — database schema migrations.
- `server/internal/entitlement/service.go` — plan-tier authorization logic.
- `server/internal/audit/` — audit logging for auth and enrollment events.

---

## 7. Best file targets for starting changes

- `server/cmd/server/main.go` — app bootstrap and route registration.
- `server/internal/config/config.go` — env and configuration.
- `server/internal/auth/service.go` — critical auth logic.
- `bootstrapper/src/main.rs` — high-level bootstrapper flow.
- `bootstrapper/src/manifest.rs` — manifest validation.
- `bootstrapper/src/jar.rs` — jar download/verification.
## next prompt 
dont make heart beat extend by a hour make it a requirement for it to be open also make it so heart doesnt add time to session just keeps it open under the conidition heartbeat and even better have the api log what the bootstrapper is sending for heart beat cause this is how were gonna do server sided storage on the stats of what macros players used for how long at a time and what happened during there session for the stats page