# Auth-Core Hardening Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make the server session stay open only while heartbeats arrive (no rolling time extension), log per-heartbeat activity for the stats page, and close the challenge double-consume replay race.

**Architecture:** Add a `last_heartbeat_at` liveness column + a pure `db.SessionLive` gate enforced in the heartbeat/verify paths and the content `SessionAuth` middleware; `expires_at` becomes an absolute cap. Add a `session_activity` JSONB table fed by the heartbeat handler (which currently drops the payload). Replace the racy `GetChallenge`+`DeleteChallenge` with an atomic Redis `GETDEL`.

**Tech Stack:** Go 1.26, Fiber v2, pgx v5 (PostgreSQL), go-redis v9, filename-ordered SQL migrations.

**Spec:** `docs/superpowers/specs/2026-06-15-auth-core-hardening-design.md`

**Working dir:** all paths are relative to `Go-Server-v23/Go-Server/server`. Run `go`/`git` from there.

**Test scope note:** The pure liveness logic is unit-tested (no infra). The atomic challenge is integration-tested against a local Redis (gated on `TEST_REDIS_URL`). The Postgres-backed behaviors (no-extend, stale-session 401, activity row) are verified by a documented `docker compose` smoke check here; a full test-postgres harness for the auth package is sub-project 4's job, not this plan's.

---

### Task 1: Pure liveness gate `db.SessionLive` (+ `LastHeartbeatAt` field)

**Files:**
- Modify: `internal/db/sessions.go` (add `LastHeartbeatAt` field to `Session`)
- Create: `internal/db/session_liveness.go`
- Test: `internal/db/session_liveness_test.go`

- [ ] **Step 1: Add the field to the `Session` struct.** In `internal/db/sessions.go`, add to `type Session struct` after the `ExpiresAt` field:

```go
	ExpiresAt            time.Time  `json:"expires_at"`
	LastHeartbeatAt      time.Time  `json:"last_heartbeat_at"`
```

- [ ] **Step 2: Write the failing test** at `internal/db/session_liveness_test.go`:

```go
package db

import (
	"testing"
	"time"
)

func TestSessionLive(t *testing.T) {
	now := time.Date(2026, 6, 15, 12, 0, 0, 0, time.UTC)
	window := 15 * time.Minute

	cases := []struct {
		name      string
		revoked   bool
		expiresAt time.Time
		lastBeat  time.Time
		want      bool
	}{
		{"fresh session is live", false, now.Add(time.Hour), now.Add(-time.Minute), true},
		{"revoked is dead", true, now.Add(time.Hour), now, false},
		{"past hard cap is dead", false, now.Add(-time.Second), now, false},
		{"stale heartbeat is dead", false, now.Add(time.Hour), now.Add(-16 * time.Minute), false},
		{"exactly at window boundary is live", false, now.Add(time.Hour), now.Add(-window), true},
		{"one second past window is dead", false, now.Add(time.Hour), now.Add(-window - time.Second), false},
	}
	for _, c := range cases {
		t.Run(c.name, func(t *testing.T) {
			s := &Session{Revoked: c.revoked, ExpiresAt: c.expiresAt, LastHeartbeatAt: c.lastBeat}
			if got := SessionLive(s, now, window); got != c.want {
				t.Fatalf("SessionLive=%v want %v", got, c.want)
			}
		})
	}
}
```

- [ ] **Step 3: Run it; verify it fails to compile** (`SessionLive` undefined).

Run: `go test ./internal/db/ -run TestSessionLive`
Expected: FAIL — `undefined: SessionLive`.

- [ ] **Step 4: Implement** `internal/db/session_liveness.go`:

```go
package db

import "time"

// SessionLive reports whether a session may currently authorize work. A session
// is live only while heartbeats keep arriving: it must not be revoked, must be
// within its absolute expires_at cap, and its last heartbeat must be no older
// than window. expires_at is a fixed cap set at creation and is never rolled
// forward — heartbeats refresh last_heartbeat_at, not the lifetime.
func SessionLive(s *Session, now time.Time, window time.Duration) bool {
	if s.Revoked || !now.Before(s.ExpiresAt) {
		return false
	}
	return now.Sub(s.LastHeartbeatAt) <= window
}
```

- [ ] **Step 5: Run; verify pass.**

Run: `go test ./internal/db/ -run TestSessionLive -v`
Expected: PASS (all 6 sub-tests).

- [ ] **Step 6: Commit.**

```bash
git add internal/db/session_liveness.go internal/db/session_liveness_test.go internal/db/sessions.go
git commit -m "feat(auth): add pure SessionLive liveness gate"
```

---

### Task 2: Migration `008_session_liveness_activity.sql`

**Files:**
- Create: `migrations/008_session_liveness_activity.sql`

- [ ] **Step 1: Write the migration.** (`007_` is already used twice, so the next ordinal is `008_`.)

```sql
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
```

- [ ] **Step 2: Sanity-check SQL parses** by applying to the local compose DB later (Task 9). No code step here.

- [ ] **Step 3: Commit.**

```bash
git add migrations/008_session_liveness_activity.sql
git commit -m "feat(db): migration 008 — session liveness column + session_activity table"
```

---

### Task 3: DB layer — scan new column, repurpose heartbeat update, add activity insert

**Files:**
- Modify: `internal/db/sessions.go`
- Create: `internal/db/session_activity.go`

- [ ] **Step 1: Scan `last_heartbeat_at` in every sessions read/return.** In `internal/db/sessions.go`:

In `CreateSession`, add `last_heartbeat_at` to the `RETURNING` list (after `expires_at`) and add `&s.LastHeartbeatAt` to the `row.Scan(...)` (after `&s.ExpiresAt`):

```go
			entitlement_expires_at,
			expires_at,
			last_heartbeat_at,
			revoked,
			last_seen_ip,
			created_at
```
```go
		&s.EntitlementExpiresAt,
		&s.ExpiresAt,
		&s.LastHeartbeatAt,
		&s.Revoked,
		&s.LastSeenIP,
		&s.CreatedAt,
```

Apply the identical two edits (SELECT column list + Scan target) to `GetSessionByTokenHash` and to `ListActiveSessions`.

- [ ] **Step 2: Repurpose `UpdateSessionHeartbeat`** to refresh liveness, not lifetime. Replace the whole function:

```go
func UpdateSessionHeartbeat(
	ctx context.Context,
	pool *pgxpool.Pool,
	sessionID string,
	planTier string,
	modules []string,
	features []string,
) error {
	_, err := pool.Exec(ctx, `
		UPDATE sessions
		SET last_heartbeat_at = now(), plan_tier = $1, enabled_modules = $2, enabled_features = $3
		WHERE id = $4
	`, planTier, modules, features, sessionID)

	return err
}
```

- [ ] **Step 3: Add the activity insert** at `internal/db/session_activity.go`:

```go
package db

import (
	"context"
	"encoding/json"

	"github.com/jackc/pgx/v5/pgxpool"
)

// InsertSessionActivity appends one heartbeat activity record. activity is stored
// verbatim as JSONB (pgx's jsonb codec marshals the value), so whatever shape the
// loader reports — today a string array, later macro/duration/event objects — is
// preserved without a schema change. Callers pass json.RawMessage("[]") when empty.
func InsertSessionActivity(
	ctx context.Context,
	pool *pgxpool.Pool,
	sessionID string,
	accountID string,
	deviceID string,
	activity json.RawMessage,
) error {
	_, err := pool.Exec(ctx, `
		INSERT INTO session_activity (session_id, account_id, device_id, activity)
		VALUES ($1, $2, $3, $4)
	`, sessionID, accountID, deviceID, activity)

	return err
}
```

- [ ] **Step 4: Build to verify the db package still compiles.**

Run: `go build ./internal/db/`
Expected: no output (success). (Callers of `UpdateSessionHeartbeat` break until Task 6 — that's expected; do not build the whole module yet.)

- [ ] **Step 5: Commit.**

```bash
git add internal/db/sessions.go internal/db/session_activity.go
git commit -m "feat(db): scan last_heartbeat_at, refresh-not-extend heartbeat, add session_activity insert"
```

---

### Task 4: Atomic challenge consumption (`cache.ConsumeChallenge`)

**Files:**
- Modify: `internal/cache/challenges.go`
- Test: `internal/cache/challenges_test.go`

- [ ] **Step 1: Write the failing integration test** (gated on a local Redis) at `internal/cache/challenges_test.go`:

```go
package cache

import (
	"context"
	"os"
	"sync"
	"testing"

	"github.com/redis/go-redis/v9"
)

func testRedis(t *testing.T) *redis.Client {
	url := os.Getenv("TEST_REDIS_URL")
	if url == "" {
		t.Skip("TEST_REDIS_URL not set; skipping redis integration test")
	}
	opt, err := redis.ParseURL(url)
	if err != nil {
		t.Fatalf("parse TEST_REDIS_URL: %v", err)
	}
	return redis.NewClient(opt)
}

func TestConsumeChallengeIsSingleUse(t *testing.T) {
	ctx := context.Background()
	rdb := testRedis(t)
	deviceID := "test-device-consume"
	_ = DeleteChallenge(ctx, rdb, deviceID)

	if err := StoreChallenge(ctx, rdb, &Challenge{DeviceID: deviceID, Challenge: "abc", SourceIP: "1.2.3.4"}); err != nil {
		t.Fatalf("store: %v", err)
	}

	const racers = 8
	var wg sync.WaitGroup
	wins := make(chan *Challenge, racers)
	for i := 0; i < racers; i++ {
		wg.Add(1)
		go func() {
			defer wg.Done()
			if c, err := ConsumeChallenge(ctx, rdb, deviceID); err == nil {
				wins <- c
			}
		}()
	}
	wg.Wait()
	close(wins)

	if got := len(wins); got != 1 {
		t.Fatalf("expected exactly 1 winner, got %d", got)
	}
}
```

- [ ] **Step 2: Run it; verify it fails to compile** (`ConsumeChallenge` undefined).

Run: `go test ./internal/cache/ -run TestConsumeChallenge`
Expected: FAIL — `undefined: ConsumeChallenge`.

- [ ] **Step 3: Implement.** In `internal/cache/challenges.go`, drop the dead `Used` field from `Challenge` and add `ConsumeChallenge`:

Change the struct to:
```go
type Challenge struct {
	DeviceID  string `json:"device_id"`
	Challenge string `json:"challenge"`
	SourceIP  string `json:"source_ip"`
}
```

Add:
```go
// ConsumeChallenge atomically fetches and deletes the challenge for deviceID via
// Redis GETDEL. Concurrent /auth/finish calls cannot double-consume: exactly one
// caller receives the value, the rest get redis.Nil. Returns redis.Nil if absent.
func ConsumeChallenge(ctx context.Context, rdb *redis.Client, deviceID string) (*Challenge, error) {
	data, err := rdb.GetDel(ctx, challengeKey(deviceID)).Bytes()
	if err != nil {
		return nil, err
	}
	c := &Challenge{}
	return c, json.Unmarshal(data, c)
}
```

- [ ] **Step 4: Run with a local Redis.** Bring up Redis (`docker compose up -d redis` from `server/`), then:

Run: `TEST_REDIS_URL=redis://localhost:6379 go test ./internal/cache/ -run TestConsumeChallenge -v`
Expected: PASS (`exactly 1 winner`).

- [ ] **Step 5: Commit.**

```bash
git add internal/cache/challenges.go internal/cache/challenges_test.go
git commit -m "feat(cache): atomic ConsumeChallenge via GETDEL; drop dead Used flag"
```

---

### Task 5: Config — liveness window + absolute cap default

**Files:**
- Modify: `internal/config/config.go`
- Modify: `.env.example`

- [ ] **Step 1: Add the field.** In `type Config struct`, after `SessionTTLHours int`:

```go
	SessionTTLHours       int
	HeartbeatLivenessWindow time.Duration
```

Add `"time"` to the import block.

- [ ] **Step 2: Populate it and change the cap default.** In the `cfg := &Config{...}` literal, change the `SessionTTLHours` line and add the window line:

```go
		// Absolute session cap (NOT rolling). Heartbeats no longer extend this;
		// liveness is governed by HeartbeatLivenessWindow. Default 12h so a normal
		// play session is never kicked mid-game.
		SessionTTLHours:       getEnvIntOr("SESSION_TTL_HOURS", 12),
		HeartbeatLivenessWindow: time.Duration(getEnvIntOr("HEARTBEAT_LIVENESS_WINDOW_SECONDS", 900)) * time.Second,
```

- [ ] **Step 3: Document in `.env.example`.** Append:

```
# Absolute session lifetime cap in hours. Heartbeats keep a session alive but do
# NOT extend this cap (changed 2026-06-15). Default 12.
# SESSION_TTL_HOURS=12
# A session dies this many seconds after its last heartbeat. Must exceed the
# client heartbeat interval (240s) with margin. Default 900 (15 min).
# HEARTBEAT_LIVENESS_WINDOW_SECONDS=900
```

- [ ] **Step 4: Build the config package.**

Run: `go build ./internal/config/`
Expected: success.

- [ ] **Step 5: Commit.**

```bash
git add internal/config/config.go .env.example
git commit -m "feat(config): HeartbeatLivenessWindow + SESSION_TTL_HOURS as absolute cap (default 12h)"
```

---

### Task 6: Auth service — atomic challenge, liveness heartbeat, activity, verify gates

**Files:**
- Modify: `internal/auth/service.go`

- [ ] **Step 1: Use `ConsumeChallenge` in `Finish`.** Replace the block from `ch, err := cache.GetChallenge(...)` through the standalone `cache.DeleteChallenge(ctx, s.rdb, device.ID)` (the lines that fetch, check `ch.Used`, check IP + delete, then delete again) with:

```go
	ch, err := cache.ConsumeChallenge(ctx, s.rdb, device.ID)
	if err != nil {
		s.auditSvc.Log("auth.finish.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
			"reason": "no_challenge_or_expired",
		})
		return nil, ErrNoChallenge
	}

	if ch.SourceIP != sourceIP {
		s.auditSvc.Log("auth.finish.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
			"reason":   "ip_mismatch",
			"expected": ch.SourceIP,
		})
		return nil, ErrIPMismatch
	}
```

(The challenge is already consumed by `ConsumeChallenge`, so the old explicit `DeleteChallenge` calls are gone.)

- [ ] **Step 2: Change the `Heartbeat` signature and body.** Add `"encoding/json"` to the import block. Replace the `Heartbeat` method with:

```go
func (s *Service) Heartbeat(ctx context.Context, sessionToken, sourceIP string, activity json.RawMessage) (*HeartbeatResult, error) {
	tokenHash, err := crypto.HashToken(sessionToken)
	if err != nil {
		return nil, ErrSessionInvalid
	}

	session, err := db.GetSessionByTokenHash(ctx, s.pool, tokenHash)
	if err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			return nil, ErrSessionInvalid
		}
		return nil, err
	}

	if !db.SessionLive(session, time.Now(), s.cfg.HeartbeatLivenessWindow) {
		return nil, ErrSessionInvalid
	}

	// Re-resolve entitlements every beat: a revoked/expired license takes
	// effect mid-session instead of surviving until restart.
	ent, err := s.entSvc.Resolve(ctx, session.AccountID)
	if err != nil {
		return nil, err
	}

	if !ent.Authorized {
		if revokeErr := db.RevokeSession(ctx, s.pool, session.ID); revokeErr != nil {
			log.Printf("[auth.heartbeat] revoke failed session_id=%s err=%v", session.ID, revokeErr)
		}
		s.auditSvc.Log("auth.heartbeat.entitlement_revoked", &session.AccountID, &session.DeviceID, nil, &sourceIP, map[string]any{
			"session_id": session.ID,
			"reason":     ent.Reason,
		})
		return nil, ErrSessionInvalid
	}

	planTier := strings.TrimSpace(ent.PlanTier)
	if planTier == "" {
		planTier = "unknown"
	}

	// Refresh liveness (last_heartbeat_at) and the stored entitlement view, but do
	// NOT extend expires_at — the session stays open only while heartbeats arrive.
	if err := db.UpdateSessionHeartbeat(ctx, s.pool, session.ID, planTier, ent.EnabledModules, ent.EnabledFeatures); err != nil {
		return nil, err
	}

	// Persist what the loader reported for the stats page. Default to an empty
	// JSON array when the client sent nothing parseable.
	if len(activity) == 0 {
		activity = json.RawMessage("[]")
	}
	if err := db.InsertSessionActivity(ctx, s.pool, session.ID, session.AccountID, session.DeviceID, activity); err != nil {
		log.Printf("[auth.heartbeat] activity insert failed session_id=%s err=%v", session.ID, err)
	}

	s.auditSvc.Log("auth.heartbeat", &session.AccountID, &session.DeviceID, nil, &sourceIP, map[string]any{
		"session_id": session.ID,
	})

	return &HeartbeatResult{
		PlanTier: planTier,
		Modules:  ent.EnabledModules,
		Features: ent.EnabledFeatures,
	}, nil
}
```

- [ ] **Step 3: Add the liveness gate to `VerifyMinecraft` and `VerifySession`.** In `VerifyMinecraft`, replace:

```go
	sess, err := db.GetSessionByTokenHash(ctx, s.pool, tokenHash)
	if err != nil || sess.Revoked || time.Now().After(sess.ExpiresAt) {
		return nil, ErrSessionInvalid
	}
```
with:
```go
	sess, err := db.GetSessionByTokenHash(ctx, s.pool, tokenHash)
	if err != nil || !db.SessionLive(sess, time.Now(), s.cfg.HeartbeatLivenessWindow) {
		return nil, ErrSessionInvalid
	}
```

In `VerifySession`, the revoked/expiry checks are split across logged branches (`if sess.Revoked` and `if time.Now().After(sess.ExpiresAt)`). Leave those two as-is for their logging, and add a liveness check immediately after the existing expiry block:

```go
	if !db.SessionLive(sess, time.Now(), s.cfg.HeartbeatLivenessWindow) {
		log.Printf("[auth.verify_session] session_stale ip=%s session_id=%s last_heartbeat=%s",
			sourceIP, sess.ID, sess.LastHeartbeatAt.Format(time.RFC3339))
		return nil, ErrSessionInvalid
	}
```

- [ ] **Step 4: Build the auth package.**

Run: `go build ./internal/auth/`
Expected: FAIL — `handleHeartbeat` still calls the old 3-arg `Heartbeat`. Proceed to Task 7 (handler) before the whole-module build.

- [ ] **Step 5: Commit.**

```bash
git add internal/auth/service.go
git commit -m "feat(auth): atomic challenge consume, liveness heartbeat + activity logging, verify liveness gates"
```

---

### Task 7: Auth handler — widen activity, forward it

**Files:**
- Modify: `internal/auth/handler.go`

- [ ] **Step 1: Widen the request type.** Add `"encoding/json"` to the import block. Change `heartbeatRequest`:

```go
type heartbeatRequest struct {
	SessionToken string          `json:"session_token"`
	Activity     json.RawMessage `json:"activity"`
}
```

- [ ] **Step 2: Forward the payload.** In `handleHeartbeat`, change the service call from `svc.Heartbeat(c.Context(), req.SessionToken, ip)` to:

```go
		res, err := svc.Heartbeat(c.Context(), req.SessionToken, ip, req.Activity)
```

- [ ] **Step 3: Build the auth package.**

Run: `go build ./internal/auth/`
Expected: success.

- [ ] **Step 4: Commit.**

```bash
git add internal/auth/handler.go
git commit -m "feat(auth): forward heartbeat activity payload (json.RawMessage) to the service"
```

---

### Task 8: Middleware + wiring — enforce liveness on content endpoints

**Files:**
- Modify: `internal/middleware/session.go`
- Modify: `internal/content/handler.go`
- Modify: `cmd/server/main.go`

- [ ] **Step 1: Add the window param + gate to `SessionAuth`.** In `internal/middleware/session.go`, change the signature and the validity check. Signature:

```go
func SessionAuth(pool *pgxpool.Pool, strictIP bool, livenessWindow time.Duration) fiber.Handler {
```

Replace:
```go
		session, err := db.GetSessionByTokenHash(c.Context(), pool, hash)
		if err != nil || session.Revoked || time.Now().After(session.ExpiresAt) {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}
```
with:
```go
		session, err := db.GetSessionByTokenHash(c.Context(), pool, hash)
		if err != nil || !db.SessionLive(session, time.Now(), livenessWindow) {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed", "message": "Authentication failed"})
		}
```

- [ ] **Step 2: Thread the window through content route registration.** In `internal/content/handler.go`, change `RegisterRoutes`:

```go
func RegisterRoutes(app *fiber.App, svc *Service, pool *pgxpool.Pool, rdb *redis.Client, strictIP bool, livenessWindow time.Duration) {
	sessAuth := middleware.SessionAuth(pool, strictIP, livenessWindow)
```

- [ ] **Step 3: Pass it from `main.go`.** In `cmd/server/main.go`, change the content registration line:

```go
	content.RegisterRoutes(pub, contentSvc, pool, rdb, cfg.StrictSessionIP, cfg.HeartbeatLivenessWindow)
```

- [ ] **Step 4: Build the whole module.**

Run: `go build ./...`
Expected: success (no output).

- [ ] **Step 5: Vet.**

Run: `go vet ./...`
Expected: no findings.

- [ ] **Step 6: Run all unit tests.**

Run: `go test ./...`
Expected: PASS; `internal/cache` redis test SKIPs (no `TEST_REDIS_URL`); `internal/db` SessionLive test PASSES.

- [ ] **Step 7: Commit.**

```bash
git add internal/middleware/session.go internal/content/handler.go cmd/server/main.go
git commit -m "feat(auth): enforce session liveness on content endpoints via SessionAuth"
```

---

### Task 9: End-to-end smoke verification against local compose

**Files:** none (verification only).

- [ ] **Step 1: Bring up Postgres + Redis and apply migrations.** From `server/`:

```bash
docker compose up -d postgres redis
```

Generate throwaway secrets and run the server once so `RunMigrations` applies `008`:

```bash
export MASTER_KEY=$(openssl rand -base64 32)
export SERVER_PEPPER=$(openssl rand -base64 32)
export MANIFEST_SIGNING_KEY=$(openssl rand -base64 32)
export ADMIN_API_SECRET=devsecret
export DB_URL="postgres://phantom:phantom@localhost:5432/phantom?sslmode=disable"
export REDIS_URL="redis://localhost:6379"
go run ./cmd/server &
SERVER_PID=$!
sleep 3
```

- [ ] **Step 2: Confirm migration 008 applied and the new schema exists.**

Run:
```bash
docker compose exec -T postgres psql -U phantom -d phantom -c "\d sessions" -c "\dt session_activity" -c "SELECT filename FROM schema_migrations WHERE filename='008_session_liveness_activity.sql';"
```
Expected: `sessions` shows a `last_heartbeat_at` column; `session_activity` table is listed; the migration filename row is present.

- [ ] **Step 3: Confirm the run-token integration test passes now that Redis is up.**

Run: `TEST_REDIS_URL=redis://localhost:6379 go test ./internal/cache/ -run TestConsumeChallenge -v`
Expected: PASS.

- [ ] **Step 4: Tear down.**

```bash
kill $SERVER_PID
docker compose down
```

- [ ] **Step 5: Commit any doc note** (if Task notes were added). Otherwise no-op.

---

### Task 10: Wrap-up — spec/plan cross-check + final commit

- [ ] **Step 1: Re-read the spec** `docs/superpowers/specs/2026-06-15-auth-core-hardening-design.md` and confirm each of the three goals (liveness heartbeat, activity logging, atomic challenge) has landed and the middleware/verify gates are wired.

- [ ] **Step 2: Final `go build ./... && go vet ./... && go test ./...`** and confirm green.

- [ ] **Step 3: Commit** any remaining changes:

```bash
git add -A
git commit -m "chore(auth): finish auth-core hardening (liveness + activity + atomic challenge)"
```

---

## Self-Review

**Spec coverage:**
- Liveness heartbeat → Tasks 1 (gate), 2 (column), 3 (no-extend update), 5 (window), 6 (heartbeat body), 8 (middleware). ✔
- Activity logging → Tasks 2 (table), 3 (insert), 6 (call), 7 (forward payload). ✔
- Atomic challenge → Task 4 (GETDEL), 6 (Finish uses it). ✔
- `SESSION_TTL_HOURS` reinterpretation/default → Task 5. ✔
- Verify-session/verify-minecraft gates → Task 6 step 3. ✔

**Placeholder scan:** No TBD/TODO; every code step shows the actual code. ✔

**Type consistency:** `SessionLive(*db.Session, time.Time, time.Duration) bool` used identically in db test, auth service (×3), and middleware. `Heartbeat(ctx, token, ip, json.RawMessage)` defined in Task 6 and called in Task 7. `UpdateSessionHeartbeat(ctx, pool, id, planTier, modules, features)` redefined in Task 3 and called with that arity in Task 6. `RegisterRoutes(..., strictIP, livenessWindow)` updated in Task 8 across content + main. ✔
