# Server: Drop HWID Requirement at Enrollment + /auth/start — Design

Date: 2026-05-25
Status: Draft (pending review)

## Goal

Unbreak the Phantom server's enrollment + auth flow after the bootstrapper stopped sending HWID (commit `14ac1c7e` on `cobalt-goserver-6/go-server`). Minimum-scope patch: server stops requiring `hwid` at `/enroll/handshake`, `/enroll/redeem`, and `/auth/start`. Existing devices keep working. The full HWID-TOFU-at-verify-session design is deferred.

## What's deferred (explicitly out of scope)

- `/auth/verify-session` accepting and TOFU-pinning HWID. The loader (`Cobalt` commit `33260c0e`) already sends `hwid` to the server, but the server will ignore the field after this patch. **HWID derivation in the loader is dead code until a future cycle wires up verify-session handling.** The loader still works — it just never pins/compares HWID.
- Changing `ResetDeviceBinding` semantics. Stays as-is (`binding_status='unbound'`).
- Auth-package unit tests.
- Bootstrapper redesign (the "just downloads + runs the loader" end-state).

These deferred items get their own spec/plan cycle when wanted.

## Current state (immediate problem)

- `enrollment/handler.go:100` — `handleHandshake` rejects with 400 if `req.HWID == ""`. The current bootstrapper never sends HWID, so this 400 is what the user is hitting.
- `auth/handler.go:306` — `handleStart` rejects with 401 if `req.HWID == ""`. Bootstrapper hits this immediately after enrollment.
- `auth/service.go:145-151` — `Service.Start` compares `device.HWIDHash` against the request's HWID; returns `ErrHWIDMismatch` on mismatch.
- `auth/service.go:137-143` — `Service.Start` checks request IP against `device.EnrollmentIP` when device is `hwid_pending`. Blocks roaming users on first auth.

## New state semantics

Same five states, slightly looser meaning:

| State | Meaning | hwid_hash | minecraft_username |
|---|---|---|---|
| `unbound` | Fresh row from `CreateDevice` | NULL | NULL |
| `hwid_pending` | Enrolled, has device_secret, **HWID may be NULL or set** | NULL or set | NULL |
| `fully_bound` | MC username pinned via `auth/finish` | NULL or set | set |
| `suspended` / `banned` | Disabled | unchanged | unchanged |

The only semantic shift: `hwid_pending` no longer guarantees `hwid_hash != NULL`. Existing devices with `hwid_hash` set keep that value; the field is just no longer consulted.

## Code changes

### A. `server/internal/db/devices.go`

Add one function:

```go
// MarkEnrolled moves a device from unbound to hwid_pending without setting hwid_hash.
func MarkEnrolled(ctx context.Context, pool *pgxpool.Pool, deviceID string) error {
    _, err := pool.Exec(ctx,
        `UPDATE devices SET binding_status = 'hwid_pending', updated_at = now() WHERE id = $1`, deviceID)
    return err
}
```

No changes to `BindHWID`, `ResetDeviceBinding`, or anything else in this file.

### B. `server/internal/enrollment/service.go`

- `Handshake(ctx, username, password, sourceIP)` (currently line 297) — drop `rawHWID` parameter. Replace the `hwidHash := crypto.HMACHash(...)` line (line 325) and the `db.BindHWID(...)` call (line 326) with a single `db.MarkEnrolled(ctx, s.pool, device.ID)` call.
- `RedeemWithHWID(ctx, rawKey, accountID, sourceIP)` (currently line 135) — drop `rawHWID` parameter. Replace the `hwidHash := crypto.HMACHash(...)` line (line 233) and the `UPDATE devices SET hwid_hash = $1, binding_status = 'hwid_pending'` block (lines 234-239) with `UPDATE devices SET binding_status = 'hwid_pending', updated_at = now() WHERE id = $1` (no hwid_hash write). Rename function to `Redeem`. Delete the legacy `Redeem` at line 59 — its only caller is the `req.HWID == ""` fallback in `handleRedeem`, which is also collapsed below.
- Delete the `normalizeHWID` helper at line 338 (no longer referenced after the above).

### C. `server/internal/enrollment/handler.go`

- `redeemRequest` (line 12) — drop the `HWID` field.
- `handshakeRequest` (line 20) — drop the `HWID` field.
- `handleRedeem` (line 42) — collapse the `if req.HWID != "" { ... RedeemWithHWID ... } else { ... Redeem ... }` to a single `username, secret, planTier, expiresAt, err := svc.Redeem(c.Context(), req.LicenseKey, req.AccountID, ip)` call (using the renamed function). Return the full `{status, username, device_secret, plan_tier, expires_at}` response — the legacy "redeem only" two-step flow's response shape is no longer reachable.
- `handleHandshake` (line 97) — drop `req.HWID == ""` from the rejection condition (line 100). Drop the HWID argument from `svc.Handshake(...)` (line 104).

### D. `server/internal/auth/service.go`

`Service.Start(ctx, username, minecraftUsername, sourceIP)` (currently line 90) — drop `rawHWID` parameter. Delete lines 137-143 (`hwid_pending` IP-mismatch check) and lines 145-151 (HWID compare). Everything else stays: account active, device exists, device not suspended/banned, MC username match for `fully_bound`, challenge generation.

Delete `ErrHWIDMismatch` (line 28) and `ErrIPMismatch` (line 27) **only if** grep shows no remaining callers across the codebase. Conservative default: leave both errors defined (they're unused vars, the Go compiler warns but doesn't error on this in a package, and removing them across multiple files is incidental cleanup). Decision lives in implementation.

### E. `server/internal/auth/handler.go`

- `startRequest` (line 19) — drop the `HWID` field.
- `handleStart` (line 302) — drop `req.HWID == ""` from the rejection condition (line 306). Pass-through to updated `svc.Start(ctx, req.Username, req.MinecraftUsername, ip)`. Remove `ErrHWIDMismatch` and `ErrIPMismatch` from the `errors.Is` switch (lines 313-318) if those errors were deleted in Section D; otherwise leave the switch arms in place (they just become unreachable).
- `verifySessionRequest` — **no change.** The loader sends a `hwid` field; Go's JSON parser silently ignores unknown fields. The field arrives, gets dropped, and the loader's request is processed normally.
- `handleVerifySession` — **no change.**

## Wire compatibility

- `/enroll/handshake` and `/enroll/redeem`: requests with or without `hwid` field work. Field is ignored if sent.
- `/auth/start`: same.
- `/auth/verify-session`: same.

Old bootstrappers still in the wild (sending HWID at enrollment + `auth/start`) work unchanged. New bootstrapper (commit `14ac1c7e`, no HWID anywhere) works after this patch deploys.

## Deploy ordering

1. Land sections A–E in one PR.
2. Deploy server to Railway.
3. Bootstrapper + loader (already pushed) become live-correct.

Until step 2, new enrollments fail. Existing enrolled users may also break depending on whether their stored `hwid_hash` matches what the old bootstrapper would have sent — should be fine for normal users since the bootstrapper computes HWID deterministically per machine, but a user with a new build of the bootstrapper that doesn't send HWID will hit the 401 at `auth/start`.

## Failure modes

| Trigger | Server behavior |
|---|---|
| Bootstrapper enrolls without HWID | Device row created with `binding_status='hwid_pending'`, `hwid_hash=NULL`. Success response. |
| Bootstrapper calls `/auth/start` on enrolled device (no HWID, no MC match yet) | 200 with challenge. |
| `/auth/finish` with valid proof | Existing flow: MC bind if `hwid_pending`, session token returned. |
| Loader calls `/auth/verify-session` with `hwid` | Server ignores `hwid`. Existing verify-session logic runs unchanged. `authorized: true` (or whatever the existing path decides). |
| Admin `POST /admin/devices/:id/reset` | Existing semantics: `binding_status='unbound'`. **The user has to re-enroll after admin reset.** Out of scope to change. |

## Smoke test (post-deploy)

1. Delete account in DB (or use a fresh test account). Bootstrapper run option 1 (username + password) → handshake succeeds, returns `device_secret`. DB row: `binding_status='hwid_pending'`, `hwid_hash=NULL`.
2. Bootstrapper proceeds to `/auth/start` → 200 with challenge. `/auth/finish` → 200 with `session_token`.
3. Bootstrapper writes `session.token`, launches Minecraft. Loader reads session, calls `/auth/verify-session` (with its `hwid` field, ignored). 200 `authorized: true`.
4. Modules load.

## Open items (future work, not this spec)

- **HWID TOFU at `/auth/verify-session`** — pin `hwid_hash` on first sight, compare on subsequent. Currently spec'd in the design doc revision history (git blame this file). Schedule when ready to make the loader's HWID derivation actually do something.
- **`ResetDeviceBinding` setting `hwid_pending` instead of `unbound`** — so users with new hardware can keep using the account without re-enrolling. Goes with the HWID TOFU work.
- **Auth-package unit tests** — no `*_test.go` files exist for `auth/`. Adding sqlmock or test-postgres harness is multi-hour, separate cycle.
- **Bootstrapper simplification** — the "just downloads + runs the loader" vision. Substantial rewrite; not started.
