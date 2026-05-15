# Server Docker Local Hosting â€” Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Run the full Phantom server stack (Go API + Postgres + Redis + admin UI + panel UI) locally in Docker with a single `docker compose up`.

**Architecture:** Six services managed by `server/docker-compose.yml`. Postgres and Redis are standard images. The Go API is built from a multi-stage Dockerfile. A one-shot `migrate` container applies SQL migrations before the API starts. Two nginx containers serve the pre-built `admin/dist/` and `panel/dist/` SPAs as static files.

**Tech Stack:** Docker Compose v2, Go 1.22, postgres:16-alpine, redis:7-alpine, nginx:alpine, bash (migrate script), openssl (secret generation)

---

## File Map

| File | Action | Purpose |
|---|---|---|
| `server/Dockerfile` | Create | Multi-stage Go build â†’ minimal alpine runtime |
| `server/docker-compose.yml` | Create | All 6 services, volumes, env wiring |
| `server/.env.example` | Create | Template showing all required vars |
| `server/.gitignore` | Create/modify | Ignore `.env` and `content/` |
| `server/docker/migrate.sh` | Create | Apply SQL migrations in order, track applied files |
| `server/docker/nginx-admin.conf` | Create | Nginx SPA config for admin frontend |
| `server/docker/nginx-panel.conf` | Create | Nginx SPA config for panel frontend |
| `server/docker/generate-secrets.sh` | Create | Print ready-to-paste `.env` secrets block |

---

### Task 1: Dockerfile for the Go API

**Files:**
- Create: `server/Dockerfile`

- [ ] **Step 1: Create the Dockerfile**

```dockerfile
FROM golang:1.22-alpine AS builder
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 GOOS=linux go build -o phantom-server ./cmd/server

FROM alpine:3.20
RUN apk add --no-cache ca-certificates
WORKDIR /app
COPY --from=builder /app/phantom-server .
EXPOSE 8080 8081
CMD ["./phantom-server"]
```

- [ ] **Step 2: Verify it builds**

Run from `server/`:
```bash
docker build -t phantom-api-test .
```
Expected: image builds successfully, no errors. Final image should be ~20-30 MB.

- [ ] **Step 3: Commit**

```bash
git add server/Dockerfile
git commit -m "feat(docker): add multi-stage Dockerfile for Go API"
```

---

### Task 2: Migration script

**Files:**
- Create: `server/docker/migrate.sh`

- [ ] **Step 1: Create the migrations directory and script**

```bash
mkdir -p server/docker
```

Create `server/docker/migrate.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail

: "${DATABASE_URL:?DATABASE_URL is required}"
: "${MIGRATIONS_DIR:=/migrations}"

echo "Waiting for postgres..."
until psql "$DATABASE_URL" -c '\q' 2>/dev/null; do
  sleep 1
done
echo "Postgres is ready."

psql "$DATABASE_URL" <<'SQL'
CREATE TABLE IF NOT EXISTS schema_migrations (
    filename TEXT PRIMARY KEY,
    applied_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
SQL

for f in $(ls "$MIGRATIONS_DIR"/*.sql | sort); do
  name=$(basename "$f")
  exists=$(psql "$DATABASE_URL" -t -c "SELECT 1 FROM schema_migrations WHERE filename = '$name'" | tr -d '[:space:]')
  if [ "$exists" = "1" ]; then
    echo "Skipping $name (already applied)"
  else
    echo "Applying $name..."
    psql "$DATABASE_URL" -f "$f"
    psql "$DATABASE_URL" -c "INSERT INTO schema_migrations (filename) VALUES ('$name')"
    echo "Applied $name"
  fi
done

echo "Migrations complete."
```

- [ ] **Step 2: Make it executable**

```bash
chmod +x server/docker/migrate.sh
```

- [ ] **Step 3: Commit**

```bash
git add server/docker/migrate.sh
git commit -m "feat(docker): add migration runner script"
```

---

### Task 3: Nginx configs for frontends

**Files:**
- Create: `server/docker/nginx-admin.conf`
- Create: `server/docker/nginx-panel.conf`

- [ ] **Step 1: Create nginx-admin.conf**

```nginx
server {
    listen 80;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    gzip on;
    gzip_types text/plain text/css application/javascript application/json;
}
```

Save to `server/docker/nginx-admin.conf`.

- [ ] **Step 2: Create nginx-panel.conf**

Identical content â€” save to `server/docker/nginx-panel.conf`:

```nginx
server {
    listen 80;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    gzip on;
    gzip_types text/plain text/css application/javascript application/json;
}
```

- [ ] **Step 3: Commit**

```bash
git add server/docker/nginx-admin.conf server/docker/nginx-panel.conf
git commit -m "feat(docker): add nginx SPA configs for admin and panel"
```

---

### Task 4: Secret generation helper

**Files:**
- Create: `server/docker/generate-secrets.sh`

- [ ] **Step 1: Create the script**

```bash
#!/usr/bin/env bash
set -euo pipefail

# Check if .env already exists and warn about existing keys
ENV_FILE="${1:-.env}"

warn_if_exists() {
  local key="$1"
  if [ -f "$ENV_FILE" ] && grep -q "^${key}=" "$ENV_FILE" 2>/dev/null; then
    echo "WARNING: $key already exists in $ENV_FILE â€” skipping" >&2
    return 1
  fi
  return 0
}

echo "# Generated by generate-secrets.sh on $(date -u +%Y-%m-%dT%H:%M:%SZ)"

for key in MASTER_KEY SERVER_PEPPER; do
  if warn_if_exists "$key"; then
    echo "${key}=$(openssl rand -base64 32)"
  fi
done

if warn_if_exists "MANIFEST_SIGNING_KEY"; then
  echo "MANIFEST_SIGNING_KEY=$(openssl rand -base64 64 | tr -d '\n')"
fi

if warn_if_exists "ADMIN_API_SECRET"; then
  echo "ADMIN_API_SECRET=$(openssl rand -hex 32)"
fi
```

Save to `server/docker/generate-secrets.sh`.

- [ ] **Step 2: Make it executable**

```bash
chmod +x server/docker/generate-secrets.sh
```

- [ ] **Step 3: Commit**

```bash
git add server/docker/generate-secrets.sh
git commit -m "feat(docker): add secret generation helper script"
```

---

### Task 5: .env.example and .gitignore

**Files:**
- Create: `server/.env.example`
- Create/modify: `server/.gitignore`

- [ ] **Step 1: Create .env.example**

```bash
# Copy this file to .env and fill in values.
# Generate MASTER_KEY, SERVER_PEPPER, MANIFEST_SIGNING_KEY, and ADMIN_API_SECRET with:
#   ./docker/generate-secrets.sh >> .env

# Required secrets (generate with ./docker/generate-secrets.sh)
MASTER_KEY=
SERVER_PEPPER=
MANIFEST_SIGNING_KEY=
ADMIN_API_SECRET=

# Optional overrides (docker-compose sets DB_URL and REDIS_URL automatically)
# PUBLIC_PORT=8080
# ADMIN_PORT=8081
# BASE_URL=http://localhost:8080
# STRICT_SESSION_IP=false
# CONTENT_DIR=./content
```

Save to `server/.env.example`.

- [ ] **Step 2: Create server/.gitignore**

```
.env
content/
```

Save to `server/.gitignore`.

- [ ] **Step 3: Commit**

```bash
git add server/.env.example server/.gitignore
git commit -m "feat(docker): add .env.example and .gitignore"
```

---

### Task 6: docker-compose.yml

**Files:**
- Create: `server/docker-compose.yml`

- [ ] **Step 1: Create docker-compose.yml**

```yaml
services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_USER: phantom
      POSTGRES_PASSWORD: phantom
      POSTGRES_DB: phantom
    volumes:
      - phantom_pgdata:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U phantom"]
      interval: 3s
      timeout: 5s
      retries: 10

  redis:
    image: redis:7-alpine
    volumes:
      - phantom_redisdata:/data
    ports:
      - "6379:6379"

  migrate:
    image: postgres:16-alpine
    depends_on:
      postgres:
        condition: service_healthy
    environment:
      DATABASE_URL: postgres://phantom:phantom@postgres:5432/phantom?sslmode=disable
      MIGRATIONS_DIR: /migrations
    volumes:
      - ./migrations:/migrations:ro
      - ./docker/migrate.sh:/migrate.sh:ro
    entrypoint: ["/bin/sh", "/migrate.sh"]
    restart: on-failure

  api:
    build:
      context: .
      dockerfile: Dockerfile
    depends_on:
      migrate:
        condition: service_completed_successfully
    env_file: .env
    environment:
      DB_URL: postgres://phantom:phantom@postgres:5432/phantom?sslmode=disable
      REDIS_URL: redis://redis:6379
      PUBLIC_PORT: "8080"
      ADMIN_PORT: "8081"
      STRICT_SESSION_IP: "false"
      CONTENT_DIR: /content
    volumes:
      - ./content:/content
    ports:
      - "8080:8080"
      - "8081:8081"

  nginx-admin:
    image: nginx:alpine
    volumes:
      - ./admin/dist:/usr/share/nginx/html:ro
      - ./docker/nginx-admin.conf:/etc/nginx/conf.d/default.conf:ro
    ports:
      - "3001:80"

  nginx-panel:
    image: nginx:alpine
    volumes:
      - ./panel/dist:/usr/share/nginx/html:ro
      - ./docker/nginx-panel.conf:/etc/nginx/conf.d/default.conf:ro
    ports:
      - "3002:80"

volumes:
  phantom_pgdata:
  phantom_redisdata:
```

Save to `server/docker-compose.yml`.

- [ ] **Step 2: Commit**

```bash
git add server/docker-compose.yml
git commit -m "feat(docker): add docker-compose with all 6 services"
```

---

### Task 7: First run and smoke test

- [ ] **Step 1: Create the content directory**

```bash
mkdir -p server/content
```

- [ ] **Step 2: Generate secrets and create .env**

```bash
cd server
./docker/generate-secrets.sh > .env
cat .env   # verify 4 variables were generated
```

Expected output: 5 lines â€” 1 comment + `MASTER_KEY`, `SERVER_PEPPER`, `MANIFEST_SIGNING_KEY`, `ADMIN_API_SECRET`.

- [ ] **Step 3: Start all services**

```bash
cd server
docker compose up --build
```

Watch logs. Expected sequence:
1. `postgres` becomes healthy
2. `migrate` container runs, prints `Applying 001_initial.sql...`, `Applying 002_manifest_min_loader_version.sql...`, `Migrations complete.`, exits 0
3. `api` starts, prints `public  listening on :8080` and `admin   listening on :8081`
4. `nginx-admin` and `nginx-panel` start

- [ ] **Step 4: Smoke test public API**

```bash
curl -s http://localhost:8080/health || curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/auth/login
```

Expected: any HTTP response (200, 400, or 404 â€” as long as the server responds, not a connection refused).

- [ ] **Step 5: Smoke test admin API**

```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/admin/accounts
```

Expected: `401` (unauthorized â€” server is up, auth is working).

- [ ] **Step 6: Smoke test frontends**

Open in browser:
- `http://localhost:3001` â€” should show the admin UI
- `http://localhost:3002` â€” should show the panel UI

- [ ] **Step 7: Verify migrations are tracked**

```bash
docker compose exec postgres psql -U phantom -d phantom -c "SELECT filename, applied_at FROM schema_migrations ORDER BY applied_at;"
```

Expected: two rows, `001_initial.sql` and `002_manifest_min_loader_version.sql`.

- [ ] **Step 8: Verify re-run is safe (migrations skipped)**

```bash
docker compose restart migrate
docker compose logs migrate
```

Expected: both files logged as `Skipping ... (already applied)`.

- [ ] **Step 9: Commit content directory placeholder**

```bash
touch server/content/.gitkeep
git add server/content/.gitkeep
git commit -m "feat(docker): add content directory placeholder"
```

---

## Usage Reference

```bash
# First time setup
cd server
./docker/generate-secrets.sh > .env
docker compose up --build

# Subsequent starts
docker compose up

# Tear down (keeps volumes/data)
docker compose down

# Tear down and wipe all data
docker compose down -v

# View logs for a specific service
docker compose logs -f api

# Connect to postgres directly
docker compose exec postgres psql -U phantom -d phantom
```
