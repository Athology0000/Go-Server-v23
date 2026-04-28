# Server Docker Local Hosting — Design

**Date:** 2026-04-28
**Goal:** Run the full Cobalt server stack locally in Docker for end-to-end testing of all components.

---

## Architecture

Six Docker services managed by a single `docker-compose.yml` in `server/`:

```
┌─────────────┐   ┌─────────────┐
│ nginx-admin │   │ nginx-panel │
│  :3001      │   │  :3002      │
│ admin/dist/ │   │ panel/dist/ │
└─────────────┘   └─────────────┘

┌──────────────────────────────────┐
│            api (Go)              │
│  public :8080  │  admin :8081    │
└──────────────────────────────────┘

┌─────────────┐   ┌─────────────┐
│  postgres   │   │    redis    │
│   :5432     │   │   :6379     │
└─────────────┘   └─────────────┘

┌─────────────┐
│   migrate   │  (init, exits 0 after applying migrations)
└─────────────┘
```

## Services

### postgres
- Image: `postgres:16-alpine`
- Persistent named volume: `cobalt_pgdata`
- Health check: `pg_isready`
- Host port 5432 exposed for DB inspection tools (e.g. TablePlus, psql)

### redis
- Image: `redis:7-alpine`
- Persistent named volume: `cobalt_redisdata`
- Host port 6379 exposed

### migrate
- Image: `postgres:16-alpine` (reuses already-pulled image, has `psql`)
- Mounts `server/migrations/` read-only
- Runs `server/docker/migrate.sh` — iterates SQL files in lexicographic order, runs each with `psql`, skips already-applied files via a `schema_migrations` tracking table
- `depends_on: postgres` with health check condition
- `restart: on-failure` so compose re-runs it if postgres isn't ready yet

### api
- Built from `server/Dockerfile` (multi-stage: `golang:1.22-alpine` builder → `alpine:3.20` runtime)
- Reads all config from environment variables (loaded from `.env` via docker-compose)
- `depends_on: migrate` with `condition: service_completed_successfully`
- `CONTENT_DIR` points to a bind-mounted `./content` volume so mod JARs can be dropped in without rebuilding
- Exposes 8080 (public API) and 8081 (admin API) to host

### nginx-admin
- Image: `nginx:alpine`
- Serves `server/admin/dist/` as static files on port 3001
- Config in `server/docker/nginx-admin.conf`: `try_files $uri $uri/ /index.html` for SPA routing
- No dependency on `api` — starts independently

### nginx-panel
- Image: `nginx:alpine`
- Serves `server/panel/dist/` as static files on port 3002
- Config in `server/docker/nginx-panel.conf`: same SPA routing
- No dependency on `api`

## Startup Order

```
postgres (healthy) → migrate (exits 0) → api (starts)
                                        ↗
nginx-admin, nginx-panel (start immediately, no deps)
```

## Files

| Path | Purpose |
|---|---|
| `server/Dockerfile` | Multi-stage Go build |
| `server/docker-compose.yml` | All 6 services |
| `server/.env.example` | Template with all required env vars |
| `server/docker/migrate.sh` | Applies SQL migrations in order |
| `server/docker/nginx-admin.conf` | Nginx config for admin SPA |
| `server/docker/nginx-panel.conf` | Nginx config for panel SPA |
| `server/docker/generate-secrets.sh` | One-time helper to generate `.env` secrets |

## Environment Variables

Loaded from `server/.env` (git-ignored). All required unless a default is noted.

| Variable | Description | Default |
|---|---|---|
| `MASTER_KEY` | AES-256 key, base64(32 bytes) | — |
| `SERVER_PEPPER` | HMAC key, base64(32 bytes) | — |
| `MANIFEST_SIGNING_KEY` | Ed25519 private key, base64(64 bytes) | — |
| `DB_URL` | Postgres connection string | set by compose |
| `REDIS_URL` | Redis connection string | set by compose |
| `ADMIN_API_SECRET` | Shared secret for admin API | — |
| `PUBLIC_PORT` | Public server port | `8080` |
| `ADMIN_PORT` | Admin server port | `8081` |
| `CONTENT_DIR` | Path to content directory | `./content` |
| `BASE_URL` | Public base URL | `http://localhost:8080` |
| `STRICT_SESSION_IP` | Enforce session IP binding | `false` (local dev) |

`DB_URL` and `REDIS_URL` are constructed by docker-compose using the internal service hostnames (`postgres`, `redis`) and injected directly — they do not need to appear in `.env`.

## Secret Generation

`server/docker/generate-secrets.sh` uses `openssl rand` to produce base64-encoded keys and prints a ready-to-paste block:

```bash
./docker/generate-secrets.sh >> .env
```

Run once on first setup. The script is idempotent (checks if keys already exist in `.env` before appending).

## Migration Tracking

`migrate.sh` creates a `schema_migrations` table on first run:

```sql
CREATE TABLE IF NOT EXISTS schema_migrations (
    filename TEXT PRIMARY KEY,
    applied_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
```

Each SQL file is applied only if its filename is not already in this table. This makes re-running `docker compose up` safe — existing migrations are skipped.

## Data Flow (Testing)

| What to test | URL |
|---|---|
| Public API (auth, enrollment, content) | `http://localhost:8080` |
| Admin API (accounts, manifests, tokens) | `http://localhost:8081` |
| Admin UI | `http://localhost:3001` |
| Panel UI | `http://localhost:3002` |
| Postgres direct | `localhost:5432` |
| Redis direct | `localhost:6379` |

## Error Handling

- If postgres is not yet healthy when `migrate` starts, the container exits non-zero and docker-compose retries (`restart: on-failure`, max 5 retries)
- If any migration SQL fails, `migrate.sh` exits non-zero, blocking `api` from starting
- `api` will fail fast on startup if any required env var is missing (handled by `config.Load()`)

## Out of Scope

- TLS / HTTPS (not needed for local testing)
- Production hardening (resource limits, secrets management)
- Hot-reload of Go server on code changes
