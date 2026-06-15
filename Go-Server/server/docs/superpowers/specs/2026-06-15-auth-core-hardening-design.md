# Auth-Core Hardening — Liveness Heartbeat, Activity Logging, Atomic Challenge

Date: 2026-06-15
Status: Draft (implementing under the "finish the scope" goal)
Repo: `Go-Server-v23/Go-Server/server`

## Goal

Three server-side changes to the auth core, all grounded in existing direction:

1. **Liveness heartbeat** — implement the `DEVELOPMENT.md` directive: heartbeat must
   *not* extend the session by an hour or "add time"; the session stays open **only
   while heartbeats keep arriving**. This mirrors, server-side, the client loader's
   monotonic liveness lease.
2. **Activity logging** — log what the bootstrapper/loader sends in each heartbeat so
   we have server-side storage for the future stats page ("what macros players used,
   for how long, what happened during their session").
3. **Atomic challenge consumption** — close the confirmed replay race: challenge
   consumption today is a non-atomic `GetChallenge` → check `Used` → `DeleteChallenge`,
   so two concurrent `/auth/finish` calls can consume the same challenge.

Out of scope (their own sub-projects): rollback protection, HWID/TOFU binding, the
stats *page/endpoint* itself, broad auth test coverage, Railway deploy.

## Current behavior (what changes)

- `auth/service.go:395` — `Heartbeat` sets `newExpiresAt := time.Now().Add(time.Hour)`
  every beat (rolling, effectively unbounded session life). **Removed.**
- `auth/handler.go:556` — `handleHeartbeat` parses `req.Activity []string` but never
  forwards it to the service. **Now forwarded and stored.**
- `middleware/session.go:31` — `SessionAuth` validates only `revoked` + `expires_at`.
  **Now also enforces liveness.**
- `cache/challenges.go` — `GetChallenge`/`DeleteChallenge` split, `Used` flag never set.
  **Replaced by atomic `ConsumeChallenge` (Redis `GETDEL`).**
- `config.go:89` — `SESSION_TTL_HOURS` default `1`, used as a rolling window.
  **Reinterpreted as the absolute hard cap, default bumped to `12`.**

## Design

### 1. Session liveness model

Two independent gates on every session check:

- **Absolute cap** — `expires_at = created_at + SESSION_TTL_HOURS` set once at session
  creation and **never rolled forward**. Bounds the lifetime of even a continuously
  heartbeated (e.g. stolen) token. Default raised `1 → 12` hours so a normal play
  session is never kicked mid-game. (Old behavior rolled `expires_at` each beat, so it
  was effectively unbounded; a 12 h cap is strictly tighter, not a UX regression.)
- **Liveness window** — a session is open only if
  `now - last_heartbeat_at <= HEARTBEAT_LIVENESS_WINDOW`. Each authorized heartbeat sets
  `last_heartbeat_at = now()`. Default window `900 s` (15 min): comfortably above the
  client's 240 s heartbeat interval (tolerates ~3 missed beats), and short enough that a
  closed client's session dies within 15 min instead of lingering up to the old hour.

A session is **live** iff: `!revoked AND now < expires_at AND now - last_heartbeat_at <= window`.

Pure decision function (test anchor), placed in `db` (imported by both `auth` and
`middleware`, avoiding an import cycle):

```go
// db/session_liveness.go
func SessionLive(s *Session, now time.Time, window time.Duration) bool {
    if s.Revoked || !now.Before(s.ExpiresAt) {
        return false
    }
    return now.Sub(s.LastHeartbeatAt) <= window
}
```

Enforced in: `Service.Heartbeat`, `Service.VerifySession`, `Service.VerifyMinecraft`,
and `middleware.SessionAuth` (content endpoints). On a stale/expired session these
return `ErrSessionInvalid` / `401` exactly as a revoked session does today.

### 2. Schema — migration `008_session_liveness_activity.sql`

```sql
-- Liveness: when the last heartbeat arrived. Backfill existing rows to created_at.
ALTER TABLE sessions
    ADD COLUMN last_heartbeat_at TIMESTAMPTZ NOT NULL DEFAULT now();
UPDATE sessions SET last_heartbeat_at = created_at;

-- Per-heartbeat activity for the stats page. activity is whatever the client sends,
-- stored verbatim (JSONB) so richer macro/duration/event payloads need no new migration.
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

`db.Session` gains `LastHeartbeatAt time.Time`; every `sessions` SELECT/RETURNING column
list and `Scan` is extended (CreateSession, GetSessionByTokenHash, ListActiveSessions).

### 3. Heartbeat service + DB changes

- `UpdateSessionHeartbeat` drops the `expires_at` write; sets
  `last_heartbeat_at = now(), plan_tier, enabled_modules, enabled_features`. (Keeps the
  stored entitlement view in step with what the client is told — still useful — but no
  longer extends lifetime.)
- New `db.InsertSessionActivity(ctx, pool, sessionID, accountID, deviceID, activity)`.
- `Service.Heartbeat(ctx, sessionToken, sourceIP, activity json.RawMessage)`:
  1. Look up session; reject if `!SessionLive(...)`.
  2. Re-resolve entitlements; revoke + reject if unauthorized (unchanged).
  3. `UpdateSessionHeartbeat(...)` (refresh liveness, not lifetime).
  4. `InsertSessionActivity(...)` with the raw activity payload (default `[]` when empty).
- `heartbeatRequest.Activity` changes `[]string → json.RawMessage` (forward-compatible;
  the current client sends `["alive"]`, stored verbatim). Handler forwards it.

### 4. Atomic challenge consumption

```go
// cache/challenges.go
func ConsumeChallenge(ctx context.Context, rdb *redis.Client, deviceID string) (*Challenge, error) {
    data, err := rdb.GetDel(ctx, challengeKey(deviceID)).Bytes() // atomic get+delete
    if err != nil { return nil, err }
    c := &Challenge{}
    return c, json.Unmarshal(data, c)
}
```

`Service.Finish` replaces `GetChallenge` + `ch.Used` check + later `DeleteChallenge`
calls with a single `ConsumeChallenge`. Two racing `/auth/finish` calls: exactly one
gets the value, the other gets `redis.Nil → ErrNoChallenge`. On IP mismatch the
challenge is already consumed (correctly burned). The dead `Used` field is removed.

### 5. Config

- `Config.HeartbeatLivenessWindow time.Duration` from
  `HEARTBEAT_LIVENESS_WINDOW_SECONDS` (default `900`).
- `SESSION_TTL_HOURS` default `1 → 12`, documented as the **absolute** session cap.
- `SessionAuth(pool, strictIP, livenessWindow)` and the auth `Service` (already holds
  `cfg`) receive the window; threaded from `main.go`.

## Testing

- **Unit (no DB, written first):** `db.SessionLive` table-driven — fresh passes; stale
  beat fails; revoked fails; past hard-cap fails; exact-window boundary.
- **Integration (gated on `TEST_DB_URL`/`TEST_REDIS_URL`, via `docker compose up -d
  postgres redis`):**
  - `ConsumeChallenge` is single-use under concurrency (one winner).
  - Heartbeat refreshes `last_heartbeat_at` but does **not** move `expires_at`.
  - A session with a stale `last_heartbeat_at` 401s on a content endpoint.
  - A `session_activity` row is written per authorized beat with the sent payload.
  Tests skip (not fail) when the test DB env is unset, matching the existing
  `PhantomAuthNativeSmokeTest` skip convention.

## Risk / compatibility

- Wire contract unchanged for the client: `/auth/heartbeat` still accepts
  `{session_token, activity}` and returns `{status, plan_tier, enabled_modules,
  enabled_features}`. `activity` widening from string-array to arbitrary JSON is a
  superset — the current `["alive"]` still parses.
- Behavior change: sessions now die ≤15 min after the client stops (was: up to a rolling
  hour) and hard-cap at 12 h. Both are tighter than today; the client treats the
  resulting `session_invalid` exactly as it already treats revocation (lockdown +
  forceDisarm).
- An operator who explicitly set `SESSION_TTL_HOURS=1` now gets a hard 1 h cap instead of
  a rolling one — must raise it. Documented in `.env.example` and a config comment.
```
