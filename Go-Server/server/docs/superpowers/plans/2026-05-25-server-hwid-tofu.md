# Server HWID Requirement Removal Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Stop the Phantom server from requiring `hwid` at `/enroll/handshake`, `/enroll/redeem`, and `/auth/start` so the new bootstrapper (no HWID) can authenticate.

**Architecture:** Five-file edit inside `cobalt-goserver-6/Go-Server/server/internal/`. Decouple `binding_status` from `hwid_hash`: enrollment moves the device to `hwid_pending` without setting `hwid_hash`. `/auth/start` drops its HWID compare and IP-mismatch check. `/auth/verify-session` is intentionally **not** changed — the loader's `hwid` field is silently ignored at the server.

**Tech Stack:** Go 1.21+, Fiber v2, pgx/v5 for Postgres, Redis. No new dependencies.

**Spec:** `cobalt-goserver-6/Go-Server/server/docs/superpowers/specs/2026-05-25-server-hwid-tofu-design.md`

---

## File touch summary

- **Modify:** `cobalt-goserver-6/Go-Server/server/internal/db/devices.go` — add `MarkEnrolled` function.
- **Modify:** `cobalt-goserver-6/Go-Server/server/internal/enrollment/service.go` — drop HWID from `Handshake`, rename `RedeemWithHWID` → `Redeem`, delete legacy `Redeem`, delete `normalizeHWID`.
- **Modify:** `cobalt-goserver-6/Go-Server/server/internal/enrollment/handler.go` — drop `HWID` fields, collapse `handleRedeem`, fix `handleHandshake` rejection.
- **Modify:** `cobalt-goserver-6/Go-Server/server/internal/auth/service.go` — drop `rawHWID` parameter and IP-check/HWID-compare blocks from `Start`. `normalizeHWID` stays (used elsewhere? no — also delete it after confirming).
- **Modify:** `cobalt-goserver-6/Go-Server/server/internal/auth/handler.go` — drop `HWID` field from `startRequest`, drop `req.HWID == ""` rejection in `handleStart`, drop `ErrHWIDMismatch` from `errors.Is` switch.

All paths absolute from the repo root (`cobalt-goserver-6/`).

---

## Task 1: Add `MarkEnrolled` to `db/devices.go`

**Files:**
- Modify: `cobalt-goserver-6/Go-Server/server/internal/db/devices.go` — add a new exported function after `BindHWID`.

- [ ] **Step 1: Add the function**

Open `cobalt-goserver-6/Go-Server/server/internal/db/devices.go`. After the existing `BindHWID` function (around line 73), insert:

```go
// MarkEnrolled moves a device from unbound to hwid_pending without touching hwid_hash.
// Called by enrollment after the device row exists and device_secret is set.
func MarkEnrolled(ctx context.Context, pool *pgxpool.Pool, deviceID string) error {
	_, err := pool.Exec(ctx,
		`UPDATE devices SET binding_status = 'hwid_pending', updated_at = now()
		 WHERE id = $1`, deviceID)
	return err
}
```

- [ ] **Step 2: Verify it compiles**

Run from `cobalt-goserver-6/Go-Server/server/`:

```bash
go build ./internal/db/...
```

Expected: exits 0, no output.

- [ ] **Step 3: Commit**

```bash
cd cobalt-goserver-6
git add Go-Server/server/internal/db/devices.go
git commit -m "feat(server/db): add MarkEnrolled for HWID-less enrollment"
```

---

## Task 2: Update `enrollment/service.go` — drop HWID from `Handshake`

**Files:**
- Modify: `cobalt-goserver-6/Go-Server/server/internal/enrollment/service.go` — change `Handshake` signature and body.

- [ ] **Step 1: Update the `Handshake` function signature and body**

Find the `Handshake` function (currently around line 297). Replace it entirely with:

```go
func (s *Service) Handshake(ctx context.Context, username, password, sourceIP string) (string, error) {
	account, err := db.GetAccountByUsername(ctx, s.pool, normalize(username))
	if err != nil {
		s.auditSvc.Log("enroll.handshake.fail", nil, nil, nil, &sourceIP, map[string]any{"reason": "account_not_found", "username": username})
		return "", ErrBadCredentials
	}
	if account.Status != "active" {
		s.auditSvc.Log("enroll.handshake.fail", &account.ID, nil, nil, &sourceIP, map[string]any{"reason": "account_blocked"})
		return "", ErrBadCredentials
	}
	ok, err := crypto.VerifyPassword(password, account.PasswordHash)
	if err != nil || !ok {
		s.auditSvc.Log("enroll.handshake.fail", &account.ID, nil, nil, &sourceIP, map[string]any{"reason": "bad_credentials"})
		return "", ErrBadCredentials
	}
	device, err := db.GetDeviceByAccountID(ctx, s.pool, account.ID)
	if err != nil {
		s.auditSvc.Log("enroll.handshake.fail", &account.ID, nil, nil, &sourceIP, map[string]any{"reason": "device_not_found"})
		return "", ErrBadCredentials
	}
	if device.BindingStatus != "unbound" {
		s.auditSvc.Log("enroll.handshake.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{"reason": "device_already_bound", "status": device.BindingStatus})
		return "", ErrBadCredentials
	}
	if device.EnrollmentIP == nil || *device.EnrollmentIP != sourceIP {
		s.auditSvc.Log("enroll.handshake.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{"reason": "ip_mismatch"})
		return "", ErrIPMismatch
	}
	if err := db.MarkEnrolled(ctx, s.pool, device.ID); err != nil {
		return "", err
	}
	plain, err := crypto.DecryptAESGCM(s.masterKey, device.DeviceSecretEncrypted)
	if err != nil {
		return "", err
	}
	s.auditSvc.Log("enroll.handshake.success", &account.ID, &device.ID, nil, &sourceIP, nil)
	return base64.StdEncoding.EncodeToString(plain), nil
}
```

Changes from the old version: removed `rawHWID string` parameter, removed `hwidHash := crypto.HMACHash(...)` line, replaced `db.BindHWID(ctx, s.pool, device.ID, hwidHash)` with `db.MarkEnrolled(ctx, s.pool, device.ID)`.

The `device.EnrollmentIP` check stays — this is the enrollment-time IP binding (set when `CreateDevice` was called during prior `/enroll/redeem`), distinct from the `/auth/start` IP check the spec drops.

- [ ] **Step 2: Don't compile yet**

This file still has a build error because `RedeemWithHWID` isn't updated until Task 3 and `handleHandshake` still passes 4 args until Task 4. We'll verify build after Task 5.

---

## Task 3: Rename `RedeemWithHWID` → `Redeem`, delete legacy `Redeem`, delete `normalizeHWID`

**Files:**
- Modify: `cobalt-goserver-6/Go-Server/server/internal/enrollment/service.go` — same file as Task 2.

- [ ] **Step 1: Delete the legacy `Redeem` function**

Find the existing `Redeem` function (around line 59, returns `error`). Delete it entirely — from the `func (s *Service) Redeem(ctx context.Context, rawKey, accountID, sourceIP string) error {` line through its closing `}` and the blank line after.

- [ ] **Step 2: Update `RedeemWithHWID` to drop HWID and rename to `Redeem`**

Find the `RedeemWithHWID` function (around line 135). Change the signature from:

```go
func (s *Service) RedeemWithHWID(ctx context.Context, rawKey, accountID, rawHWID, sourceIP string) (string, string, string, *time.Time, error) {
```

to:

```go
func (s *Service) Redeem(ctx context.Context, rawKey, accountID, sourceIP string) (string, string, string, *time.Time, error) {
```

In the same function body, find the HWID-binding block (around lines 232-239):

```go
	// Bind HWID (device becomes eligible for auth/start).
	hwidHash := crypto.HMACHash(s.pepper, []byte(normalizeHWID(rawHWID)))
	if _, err := tx.Exec(ctx,
		`UPDATE devices SET hwid_hash = $1, binding_status = 'hwid_pending', updated_at = now()
		 WHERE id = $2`,
		hwidHash, deviceID); err != nil {
		return "", "", "", nil, err
	}
```

Replace with:

```go
	// Mark device enrolled (eligible for auth/start). HWID is pinned later by the loader at /auth/verify-session.
	if _, err := tx.Exec(ctx,
		`UPDATE devices SET binding_status = 'hwid_pending', updated_at = now()
		 WHERE id = $1`,
		deviceID); err != nil {
		return "", "", "", nil, err
	}
```

- [ ] **Step 3: Delete `normalizeHWID`**

At the bottom of `enrollment/service.go` (around line 338), delete the line:

```go
func normalizeHWID(s string) string { return strings.ToUpper(strings.TrimSpace(s)) }
```

- [ ] **Step 4: Don't compile yet**

Handler still calls the old signatures; will fix in Task 4.

---

## Task 4: Update `enrollment/handler.go` — drop HWID fields, collapse `handleRedeem`, fix `handleHandshake`

**Files:**
- Modify: `cobalt-goserver-6/Go-Server/server/internal/enrollment/handler.go`

- [ ] **Step 1: Drop the `HWID` field from request structs**

Replace the two request struct definitions at the top of the file. Old (lines 12-24):

```go
type redeemRequest struct {
	LicenseKey string `json:"license_key"`
	AccountID  string `json:"account_id"`
	// Optional: if provided, the server will bind the HWID and return the device_secret immediately
	// (so the bootstrapper can complete setup without prompting for username/password).
	HWID       string `json:"hwid"`
}

type handshakeRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
	HWID     string `json:"hwid"`
}
```

New:

```go
type redeemRequest struct {
	LicenseKey string `json:"license_key"`
	AccountID  string `json:"account_id"`
}

type handshakeRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
}
```

- [ ] **Step 2: Collapse `handleRedeem` into the single `Redeem` call**

Find the `handleRedeem` function (line 42). Replace its body entirely so it always calls the renamed `svc.Redeem` and returns the full response shape. The new function body:

```go
func handleRedeem(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req redeemRequest
		if err := c.BodyParser(&req); err != nil || req.LicenseKey == "" || req.AccountID == "" {
			return c.Status(400).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		ip := middleware.GetRealIP(c)

		username, secret, planTier, expiresAt, err := svc.Redeem(c.Context(), req.LicenseKey, req.AccountID, ip)
		if errors.Is(err, ErrKeyNotFound) || errors.Is(err, ErrKeyNotAvailable) || errors.Is(err, ErrAlreadyEnrolled) {
			reason := "key_invalid"
			if errors.Is(err, ErrKeyNotFound) {
				reason = "key_not_found"
			} else if errors.Is(err, ErrKeyNotAvailable) {
				reason = "key_not_available"
			} else if errors.Is(err, ErrAlreadyEnrolled) {
				reason = "already_enrolled"
			}
			return c.Status(400).JSON(fiber.Map{"error": "enrollment_failed", "reason": reason})
		}
		if errors.Is(err, ErrBadCredentials) || errors.Is(err, ErrIPMismatch) {
			return c.Status(401).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		return c.Status(200).JSON(fiber.Map{
			"status":        "redeemed",
			"username":      username,
			"device_secret": secret,
			"plan_tier":     planTier,
			"expires_at":    expiresAt,
		})
	}
}
```

- [ ] **Step 3: Drop HWID from `handleHandshake`**

Find `handleHandshake` (line 97). Replace its body with:

```go
func handleHandshake(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req handshakeRequest
		if err := c.BodyParser(&req); err != nil || req.Username == "" || req.Password == "" {
			return c.Status(400).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		ip := middleware.GetRealIP(c)
		secret, err := svc.Handshake(c.Context(), req.Username, req.Password, ip)
		if errors.Is(err, ErrIPMismatch) || errors.Is(err, ErrBadCredentials) {
			return c.Status(401).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "enrollment_failed"})
		}
		return c.Status(200).JSON(fiber.Map{"device_secret": secret})
	}
}
```

Changes from the old `handleHandshake`: removed `|| req.HWID == ""` from the rejection condition; removed the `req.HWID` argument from `svc.Handshake(...)`.

- [ ] **Step 4: Verify the enrollment package compiles**

Run from `cobalt-goserver-6/Go-Server/server/`:

```bash
go build ./internal/enrollment/...
```

Expected: exits 0, no output. If you see "undefined: RedeemWithHWID" or "undefined: normalizeHWID" or a type-mismatch error in `Handshake`, recheck the changes in Tasks 2 and 3.

---

## Task 5: Update `auth/service.go` — drop HWID/IP checks from `Service.Start`

**Files:**
- Modify: `cobalt-goserver-6/Go-Server/server/internal/auth/service.go`

- [ ] **Step 1: Update `Service.Start` signature and body**

Find `Service.Start` (line 90). Replace it entirely with the version below. Changes from the old: removed `rawHWID string` parameter (was the third positional arg); deleted the `hwid_pending` IP-check block (old lines 137-143); deleted the HWID-compare block (old lines 145-151); deleted the now-unused `hwidHash :=` calculation.

```go
func (s *Service) Start(ctx context.Context, username, minecraftUsername, sourceIP string) (*StartResult, error) {
	account, err := db.GetAccountByUsername(ctx, s.pool, normalize(username))
	if errors.Is(err, pgx.ErrNoRows) {
		s.auditSvc.Log("auth.start.fail", nil, nil, nil, &sourceIP, map[string]any{
			"reason":   "account_not_found",
			"username": username,
		})
		return nil, ErrNotFound
	}
	if err != nil {
		return nil, err
	}

	if account.Status != "active" {
		s.auditSvc.Log("auth.start.fail", &account.ID, nil, nil, &sourceIP, map[string]any{
			"reason": "account_blocked",
		})
		return nil, ErrDeviceBlocked
	}

	device, err := db.GetDeviceByAccountID(ctx, s.pool, account.ID)
	if errors.Is(err, pgx.ErrNoRows) {
		s.auditSvc.Log("auth.start.fail", &account.ID, nil, nil, &sourceIP, map[string]any{
			"reason": "device_not_found",
		})
		return nil, ErrNotFound
	}
	if err != nil {
		return nil, err
	}

	if device.BindingStatus == "suspended" || device.BindingStatus == "banned" {
		s.auditSvc.Log("auth.start.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
			"reason": "device_blocked",
			"status": device.BindingStatus,
		})
		return nil, ErrDeviceBlocked
	}

	if device.BindingStatus != "hwid_pending" && device.BindingStatus != "fully_bound" {
		s.auditSvc.Log("auth.start.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
			"reason": "device_not_enrolled",
		})
		return nil, ErrNotFound
	}

	if device.BindingStatus == "fully_bound" && minecraftUsername != "" {
		if device.MinecraftUsername == nil || !strings.EqualFold(*device.MinecraftUsername, minecraftUsername) {
			s.auditSvc.Log("auth.start.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{
				"reason": "username_mismatch",
			})
			return nil, ErrUsernameMismatch
		}
	}

	challenge := make([]byte, 32)
	if _, err := rand.Read(challenge); err != nil {
		return nil, err
	}

	challengeB64 := base64.StdEncoding.EncodeToString(challenge)

	if err := cache.StoreChallenge(ctx, s.rdb, &cache.Challenge{
		DeviceID:  device.ID,
		Challenge: challengeB64,
		SourceIP:  sourceIP,
	}); err != nil {
		return nil, err
	}

	s.auditSvc.Log("auth.start.success", &account.ID, &device.ID, nil, &sourceIP, nil)

	return &StartResult{
		Challenge: challengeB64,
		ExpiresIn: 30,
	}, nil
}
```

- [ ] **Step 2: Decide on `normalizeHWID` in this file**

`auth/service.go` has its own `normalizeHWID` helper at line 825. Grep it from `cobalt-goserver-6/Go-Server/server/`:

```bash
grep -rn 'normalizeHWID' internal/auth/
```

If only one reference remains (the `func` definition itself), delete it. Expected after this task: zero references → delete the helper. If grep shows other references inside `auth/`, leave it.

To delete: remove the three lines

```go
func normalizeHWID(s string) string {
	return strings.ToUpper(strings.TrimSpace(s))
}
```

at the bottom of `service.go`.

- [ ] **Step 3: Don't compile yet**

`handler.go` still calls `svc.Start` with four arguments. Fix in Task 6.

---

## Task 6: Update `auth/handler.go` — drop HWID field + caller fixes + commit + build

**Files:**
- Modify: `cobalt-goserver-6/Go-Server/server/internal/auth/handler.go`

- [ ] **Step 1: Drop the `HWID` field from `startRequest`**

Find `startRequest` (line 19). Replace:

```go
type startRequest struct {
	Username          string `json:"username"`
	HWID              string `json:"hwid"`
	MinecraftUsername string `json:"minecraft_username"`
	ClientVersion     string `json:"client_version"`
	BootstrapBuildID  string `json:"bootstrap_build_id"`
}
```

with:

```go
type startRequest struct {
	Username          string `json:"username"`
	MinecraftUsername string `json:"minecraft_username"`
	ClientVersion     string `json:"client_version"`
	BootstrapBuildID  string `json:"bootstrap_build_id"`
}
```

- [ ] **Step 2: Fix `handleStart` to match the new `svc.Start` signature**

Find `handleStart` (line 302). Replace its body with:

```go
func handleStart(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req startRequest

		if err := c.BodyParser(&req); err != nil || req.Username == "" {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}

		ip := middleware.GetRealIP(c)

		result, err := svc.Start(c.Context(), req.Username, req.MinecraftUsername, ip)
		if errors.Is(err, ErrNotFound) ||
			errors.Is(err, ErrDeviceBlocked) ||
			errors.Is(err, ErrUsernameMismatch) {
			return c.Status(401).JSON(fiber.Map{"error": "authentication_failed"})
		}

		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "authentication_failed"})
		}

		return c.JSON(fiber.Map{
			"challenge":  result.Challenge,
			"expires_in": result.ExpiresIn,
		})
	}
}
```

Changes from the old `handleStart`: removed `|| req.HWID == ""` from the rejection condition; removed `req.HWID` from the `svc.Start(...)` call; removed `ErrIPMismatch` and `ErrHWIDMismatch` from the `errors.Is` switch (they can no longer be returned by `Start`).

Note: `handleFinish` at line 332 still uses `ErrIPMismatch` because `Service.Finish` still has its own IP check at line 220 of `service.go`. **Don't touch `handleFinish`.**

- [ ] **Step 3: Verify the full server compiles**

Run from `cobalt-goserver-6/Go-Server/server/`:

```bash
go build ./...
```

Expected: exits 0, no output. If there's a compile error, the most likely culprits are:
- A missed reference to the old `Handshake(.., hwid, ..)` signature.
- A missed reference to `RedeemWithHWID` (Task 3 renamed it).
- The `errors.Is(err, ErrHWIDMismatch)` arm that needs removing.

- [ ] **Step 4: Run `go vet`**

```bash
go vet ./...
```

Expected: exits 0. Investigate any warnings before continuing — `vet` will flag unused variables left behind by the deletions.

- [ ] **Step 5: Commit the full server diff**

```bash
cd cobalt-goserver-6
git add Go-Server/server/internal/db/devices.go \
        Go-Server/server/internal/enrollment/service.go \
        Go-Server/server/internal/enrollment/handler.go \
        Go-Server/server/internal/auth/service.go \
        Go-Server/server/internal/auth/handler.go
git commit -m "$(cat <<'EOF'
feat(server): drop HWID requirement at enrollment + /auth/start

The bootstrapper no longer sends hwid (cobalt-goserver-6@14ac1c7e). Server
relaxed to match:

- enrollment.Handshake and enrollment.Redeem (renamed from RedeemWithHWID)
  drop the rawHWID parameter; device moves to hwid_pending via the new
  db.MarkEnrolled helper without writing hwid_hash.
- /auth/start drops the HWID field, HMAC compare, and hwid_pending
  IP-mismatch check. Devices in hwid_pending with hwid_hash=NULL are now
  auth-eligible.
- /auth/verify-session is intentionally not changed. The loader sends
  hwid; the server silently ignores the field. Future cycle will pin
  hwid_hash here (TOFU) and reject mismatches.

Existing devices with hwid_hash set continue to work — the field is just
no longer consulted.
EOF
)"
```

---

## Task 7: Deploy + smoke test (manual)

**Files:** none — operational.

- [ ] **Step 1: Push the commit and deploy to Railway**

The server deploys via the `go-server-v23` remote.

```bash
cd cobalt-goserver-6
git push go-server-v23 go-server
```

Then trigger the Railway deploy via the existing CI hook or dashboard. Wait for the new instance to come up healthy (check `/healthz` if exposed, or watch Railway logs for "listening on :8080" or equivalent).

- [ ] **Step 2: Run the bootstrapper smoke test**

On a test machine (not the one that hit the original error):

1. Delete `~/.config/phantom/credentials*` (or whichever path the bootstrapper writes to — see `bootstrapper/src/credentials.rs`) so the bootstrapper treats this as first-time setup.
2. Run the bootstrapper binary. Choose option 1 (username + password). Enter test account creds.
3. Expected: "Enrollment complete. Welcome, <username>!". No HTTP 400.

If this still fails with `enrollment_failed`, check Railway logs for the inbound request body and which rejection path fired.

- [ ] **Step 3: Confirm DB row shape**

Connect to the production Postgres (read-only is fine) and verify:

```sql
SELECT id, binding_status, hwid_hash, minecraft_username
FROM devices
WHERE account_id = (SELECT id FROM accounts WHERE username = '<test-user>')
ORDER BY created_at DESC LIMIT 1;
```

Expected: `binding_status = 'hwid_pending'`, `hwid_hash IS NULL`, `minecraft_username IS NULL`.

- [ ] **Step 4: Continue the bootstrapper run through Minecraft launch**

The bootstrapper should now proceed to `/auth/start`, `/auth/finish`, write `session.token`, download the loader jar, start heartbeat, and launch Minecraft. Watch for any 401 or 500 along the way.

The loader should then call `/auth/verify-session` with its `hwid` field. The server logs that as the existing verify-session path; the `hwid` field is silently ignored. Expected response: `authorized: true`.

After Minecraft loads, check the DB again:

```sql
SELECT binding_status, hwid_hash, minecraft_username FROM devices WHERE account_id = (...);
```

Expected: `binding_status = 'fully_bound'`, `hwid_hash IS NULL` (intentionally — HWID is dead code at the server today), `minecraft_username = '<actual MC name>'`.

- [ ] **Step 5: Done**

If all four steps pass, the patch is live and the original `HTTP 400 Bad Request: enrollment_failed` is fixed.

If any step fails, do **not** revert. Capture Railway logs and the exact error; come back to this plan with the failure details before changing anything.

---

## Open items (future work)

- **HWID TOFU at `/auth/verify-session`.** Future cycle. Until then, the loader's `hwid` payload is ignored, and `hwid_hash` stays NULL for new devices forever.
- **`ResetDeviceBinding` setting `hwid_pending` instead of `unbound`.** Currently admin reset forces the user to re-enroll via the bootstrapper. Goes with the TOFU work.
- **Auth-package unit tests.** `auth/` has no `*_test.go`. Manual smoke test (Task 7) is the only verification today.
- **Delete `db.BindHWID` and `auth.ErrHWIDMismatch`.** Dead code after this patch but kept to minimize blast radius. Sweep in a follow-up commit.