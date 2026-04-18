# Bootstrapper & Pre-Auth Client Design

**Date:** 2026-04-18
**Status:** Approved

---

## Overview

The bootstrapper is a standalone Rust `.exe` that the user runs instead of launching Minecraft directly. It is the sole security perimeter — Minecraft never starts unless the bootstrapper completes auth and JAR verification successfully.

After launching Minecraft, the Fabric mod performs a secondary in-game auth step to bind and verify the user's Minecraft account, then downloads and loads entitled modules before lifting the title screen gate.

---

## Components

### 1. Bootstrapper (`bootstrapper.exe`, Rust)

Runs every time before Minecraft. Responsible for:

1. Loading stored credentials (`config/cobalt/creds.json`)
2. First-run enrollment if no credentials exist
3. HWID collection
4. Challenge-response auth against the Cobalt server
5. Manifest fetch and Ed25519 signature verification
6. JAR digest check; download and re-verify if missing or outdated
7. Writing session token to `config/cobalt/.session`
8. Direct JVM invocation — spawning Minecraft as a child process
9. Deleting the session file after the Minecraft process exits

### 2. Fabric Mod (Kotlin, in-process)

Runs inside Minecraft. Responsible for:

1. Reading and deleting the session file at early init
2. Gating the title screen until auth and module load complete
3. Calling `/auth/verify-minecraft` with the live `mc.user.name`
4. Fetching the signed manifest, downloading entitled modules, verifying digests
5. Loading modules via `AddonLoader`, enabling only entitled ones
6. Displaying auth/loading status on the title screen welcome overlay

---

## File Layout

```
config/cobalt/
  creds.json              ← username + DPAPI-encrypted device_secret
  .session                ← temp: session token, deleted by mod on first read
  cache/
    cobalt.jar            ← verified protected mod JAR
    cobalt.jar.sig        ← stored Ed25519 signature for re-verification
    payloads/             ← entitled module JARs downloaded by mod
```

---

## HWID Generation

Collected by the bootstrapper on every run (Windows-only):

```
machine_guid  = HKLM\SOFTWARE\Microsoft\Cryptography\MachineGuid
volume_serial = GetVolumeInformationW("C:\")  → decimal string
hwid          = uppercase(machine_guid + ":" + volume_serial)
```

The bootstrapper sends this normalized string as `hwid` in `/auth/start`. The server applies its own `HMAC(SERVER_PEPPER, normalize(hwid))` before storing or comparing — the client sends the raw string, the server hashes it. `normalizeHWID()` on the server is uppercase + trim, which this format satisfies directly.

---

## Auth Flow

### Bootstrapper — challenge-response (no MC username)

1. Load `username` + decrypt `device_secret` from `creds.json` (DPAPI)
2. Collect HWID
3. `POST /auth/start` — `{ username, hwid, minecraft_username: "", client_version, bootstrap_build_id }`
   - Server skips MC username check when field is empty *(small server change required)*
4. Receive `challenge`
5. Compute `proof = HMAC-SHA256(device_secret_bytes, challenge_bytes)` → hex string
6. `POST /auth/finish` — `{ username, proof, minecraft_username: "" }`
7. Receive `session_token`, `manifest_url`, `manifest_signature`
8. If `authorized: false` → print reason, exit

### Mod — MC username binding/verification

Fires once on a background thread when the title screen is shown:

1. Read session file: `session_token`
2. `POST /auth/verify-minecraft` — `{ session_token, minecraft_username }` using `mc.user.name`
   - Server: if `hwid_pending` → `FullyBind(mc_username)`, return full entitlement
   - Server: if `fully_bound` → verify MC username matches, return full entitlement
3. Receive `plan_tier`, `enabled_modules`, `enabled_features`, `manifest_url`, `manifest_signature`
4. If denied → display reason on title screen, keep gate locked

---

## Enrollment (First-Run, Bootstrapper)

Triggered when `creds.json` does not exist:

```
Enter your Cobalt account ID (from dashboard):  <account_id>
Enter your license key:                          <license_key>
Enter your Cobalt username:                      <username>
Enter your Cobalt password:                      <password>
```

1. `POST /enroll/redeem` — `{ license_key, account_id }`
2. `POST /enroll/handshake` — `{ username, password, hwid }` → receive `device_secret`
3. Encrypt `device_secret` with Windows DPAPI, write `creds.json`

---

## TLS Pinning (Bootstrapper)

- Custom `rustls::client::ServerCertVerifier` implementation
- Hard-codes the SHA-256 SPKI fingerprint of the server's TLS public key
- SPKI pinning survives certificate renewal as long as the key pair is unchanged
- Bootstrapper fails closed and exits on pin mismatch — no override in production builds

---

## Credential Storage

`config/cobalt/creds.json` is encrypted with Windows DPAPI (`CryptProtectData`):

```json
{
  "username": "...",
  "device_secret_b64": "..."
}
```

DPAPI ties the ciphertext to the current Windows user account — unreadable by other users or after OS reimaging.

---

## JAR Verification & Download (Bootstrapper)

On every run, after auth:

1. Fetch manifest from `manifest_url` using `Authorization: Bearer <session_token>`
2. Verify manifest Ed25519 signature against hard-coded server public key
3. Extract `cobalt` module entry: `{ sha256, signature, download_url }`
4. If `cache/cobalt.jar` exists → compute SHA-256 and compare
5. If digest matches → skip download
6. If missing or mismatch → `GET /content/module/cobalt` with session token, save to `cache/cobalt.jar`
7. Re-verify SHA-256 + Ed25519 signature of downloaded JAR
8. Fail closed (delete file, exit) if either check fails

**Manifest format:**
```json
{
  "id": "...",
  "modules": [
    { "name": "cobalt", "sha256": "...", "signature": "...", "min_loader_version": "1.0" }
  ],
  "natives": [],
  "issued_at": "...",
  "expires_at": "..."
}
```

---

## Minecraft Launch (Bootstrapper)

After JAR verified:

1. Copy `cache/cobalt.jar` into the Fabric mods directory (path configurable via `config/cobalt/bootstrap.json` or a CLI flag, e.g. `--mods-dir`)
2. Write session JSON to `config/cobalt/.session`:
   ```json
   { "session_token": "..." }
   ```
   Only the token is stored — full entitlement is fetched fresh by the mod via `/auth/verify-minecraft`.
3. Spawn Minecraft via direct JVM invocation:
   - Main class: `net.fabricmc.loader.launch.knot.KnotClient`
   - JVM flag: `-Dcobalt.session=<absolute path to .session>`
4. Wait for process exit, then delete `config/cobalt/.session`

Direct JVM invocation is used (not Prism or official launcher) to maintain full control over classpath and JVM arguments with no intermediary process.

---

## Mod Session Reading

At `PreLaunch` (before any module init):

1. Read `-Dcobalt.session` system property → file path
2. Parse JSON into `CobaltSession { session_token }`
3. Delete the file immediately
4. If missing, unreadable, or malformed → `CobaltSession.INVALID`, all features locked

---

## Title Screen Gate

- `TitleScreenMixin` intercepts Singleplayer and Multiplayer button clicks
- Buttons are blocked until `AuthState == READY`
- `TitleScreenRenderer` shows a status overlay:
  - `VERIFYING` → "Verifying account…"
  - `LOADING` → "Loading modules… (N/M)"
  - `READY` → overlay fades, buttons unlock
  - `FAILED` → "Auth failed: <reason>" — buttons stay locked

---

## Module Download & Load (Mod)

After `/auth/verify-minecraft` succeeds:

1. Fetch manifest at `manifest_url` using session token
2. Verify manifest Ed25519 signature (same server public key, hard-coded in mod)
3. For each module in `enabled_modules`:
   - Check `config/cobalt/cache/payloads/<name>.jar` digest
   - Download from `/content/module/<name>` if missing or digest mismatch
   - Verify SHA-256 against manifest entry
4. Load each verified JAR via `AddonLoader`
5. Enable only modules listed in `enabled_modules` — all others remain disabled
6. Set `AuthState.READY`

Any verification failure → `AuthState.FAILED`, title screen stays locked.

---

## Server Changes Required

Two small changes to the existing server:

### 1. `auth/service.go` — make `minecraft_username` optional in `Start()`

When `minecraft_username` is empty string, skip the MC username check for `fully_bound` devices. The bootstrapper legitimately omits this field; verification happens later via `/auth/verify-minecraft`.

### 2. New endpoint: `POST /auth/verify-minecraft`

```
Request:  { session_token: string, minecraft_username: string }
Response: { authorized: bool, reason?: string, plan_tier?: string,
            enabled_modules?: []string, enabled_features?: []string,
            manifest_url?: string, manifest_signature?: string,
            entitlement_expires_at?: string }
```

Logic:
- Look up session by token hash
- Load device for that session
- If `hwid_pending` → call `FullyBind(mc_username, ip)`, resolve entitlement, return
- If `fully_bound` → verify `mc_username` matches `device.minecraft_username`, resolve entitlement, return
- If mismatch → `authorized: false, reason: "minecraft_username_mismatch"`

---

## Failure Behavior

All failure paths fail closed:

| Failure | Bootstrapper | Mod |
|---|---|---|
| TLS pin mismatch | Exit with error | — |
| Auth rejected | Exit with reason | Gate locked, reason shown |
| Manifest sig invalid | Exit with error | Gate locked |
| JAR digest mismatch | Delete JAR, exit | Gate locked |
| Session file missing | — | Gate locked |
| `/auth/verify-minecraft` denied | — | Gate locked, reason shown |
| Module download fails | — | Gate locked |

---

## Acceptance Criteria

- User runs `bootstrapper.exe`; Minecraft never opens unless auth and JAR verification pass
- First run prompts for account ID, license key, username, password; subsequent runs are silent
- `creds.json` is DPAPI-encrypted and not human-readable
- TLS pin mismatch causes immediate exit, no network data sent to wrong server
- `cobalt.jar` is always SHA-256 + Ed25519 verified before Minecraft starts
- Session file is deleted on first read by the mod; never persists
- Title screen Singleplayer/Multiplayer buttons are locked until mod auth and module load complete
- Only entitled modules are loaded and enabled
- Any verification failure produces a clear error and locks the client
