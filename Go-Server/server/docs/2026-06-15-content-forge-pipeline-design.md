# Content Forge Pipeline — Design Spec

- **Date:** 2026-06-15
- **Status:** Draft for review (no code yet)
- **Owner:** Phantom
- **Repos touched:** `Go-Server` (server: Go), `obfuscator` (one prod config file)

## 1. Goal

Let a dev **deliver a payload to the server and have it forged into a production-ready
loader**, gated by superadmin approval, then served per-user-watermarked on download.

Concretely: a dev hands the server a `raw.jar` (the module/loader source) plus a
**pre-built `.dll`** (native-lifted locally on a Clang box). The server runs the
aggressive obfuscation passes on the jar itself ("makes its own jars"), watermarks the
jar with a build id, **binds the delivered `.dll` to the build (embed a marker AND sign
its hash in the manifest)**, stages the result, and raises a **superadmin approve/deny**
notification. On approval the build goes live; on a user download the live build is
served with a **fresh per-user watermark** (the already-shipped download path).

## 2. Trust model & division of labor

Native lifting needs Clang; the prod server (Railway, Go container) has `java` but **no
Clang**. So the toolchain split is:

| Stage | Where | Produces |
|---|---|---|
| Native lift + compile (Clang) | **Dev local box** | the `.dll` (sensitive methods compiled out of bytecode) |
| Deliver artifacts | dev → server | `raw.jar` + `built.dll` land on the forge host |
| Aggressive jar obfuscation + jar build-watermark | **Server (forge CLI)** | hardened, watermarked `out.jar` |
| `.dll` build-marker embed + hash | **Server (forge CLI)** | marked `out.dll` + sha256 |
| Manifest sign (jar + dll hashes) | **Server** | Ed25519-signed manifest entry |
| Superadmin approve/deny | **Server (admin panel)** | build promoted to `live` or discarded |
| Per-user watermark | **Server (download path, already built)** | per-account stamped `.jar` |

**Invariant — what's on the artifacts before any non-dev touches them:** the jar is
name+string+record+number+flatten+runtimeMangle obfuscated and carries a build watermark;
the dll carries a traceable build marker and its hash is covered by the signed manifest;
no master/license key ships in plaintext (per-user key derivation, `MasterKey.fromSeed`).
The per-user watermark is the final layer, added at download.

## 3. End-to-end flow (state machine)

```
dev local:  raw.jar  + (clang) built.dll
                │ deliver to forge host
                ▼
[forge CLI]  phantom-forge build --name <mod> --in-jar raw.jar --in-dll built.dll
                │ (runs in background; inserts content_builds row status=building)
                ▼
  1. obfuscator: aggressive passes + watermark-id=<buildId>  → out.jar
  2. dll marker: append signed trailer (buildId+HMAC)        → out.dll
  3. hash out.jar, out.dll; stage under content/_staging/<buildId>/
  4. row → status=pending_approval, post superadmin notification
                ▼
[superadmin panel]  GET /admin/builds?status=pending_approval  (the "notification")
        ├── POST /admin/builds/:id/approve ─► promote: install jar→modules, dll→native,
        │                                     refresh+sign manifest (jar+dll hashes),
        │                                     supersede prior live, row→live
        └── POST /admin/builds/:id/deny    ─► row→denied, staged artifacts purged
                ▼
[user download]  GET /content/module/:name ─► serve LIVE build's jar + NEW per-user
                                              watermark (existing ModuleBytes path)
```

`content_builds.status`: `building → pending_approval → (approved→) live | denied | failed`.
At most one `live` row per module; approving a new build moves the previous `live` → `superseded`.

## 4. Components

### 4.1 Obfuscator — one new prod config (`obfuscator/forge-prod.json`)
Aggressive Fabric-compatible passes **plus** the watermark stage enabled (id/secret
supplied per-build via CLI flags). No class encryption (Fabric-incompatible), no native
(done locally). Based on the existing `phantom-fabric-aggressive.json`:

```json
{
  "passes": [
    { "name": "name" }, { "name": "string" }, { "name": "record" },
    { "name": "number" }, { "name": "flatten" }, { "name": "runtimeMangle" }
  ],
  "stages": [
    { "name": "watermark", "enabled": true },
    { "name": "classEncryption", "enabled": false },
    { "name": "pack", "enabled": false },
    { "name": "integrity", "enabled": true }
  ],
  "crypto": { "keySplitParts": 3 },
  "watermark": { "enabled": true, "id": "forge-unset", "secret": "set-via---watermark-secret" },
  "bootstrapAllowlist": [],
  "nativeMode": "disabled"
}
```

**CLI contract (already exists, confirmed in `Main.java`):**
```
java -jar obfuscator.jar --config forge-prod.json \
     --watermark-id <buildId> --watermark-secret <FORGE_SECRET> \
     in.jar in.dll out.jar out.dll
```
The forge passes the real delivered dll as `in.dll`; the obfuscator's `out.dll` is a
throwaway (native disabled), so the **dll the forge ships is the delivered one after the
Go-side marker step**, not the obfuscator's dll output.

### 4.2 `internal/forge` (Go) — the build engine
Mirrors the existing `internal/content/watermark.go` shell-out pattern.

```go
type Forge struct {
    JavaPath, ObfJar, ConfigPath, WatermarkSecret string
    StagingDir string // e.g. <contentDir>/_staging
}
type BuildResult struct {
    BuildID  string
    Module   string
    JarPath  string; JarSHA256 string
    DLLPath  string; DLLSHA256 string
}
func (f *Forge) Build(ctx, module string, jarBytes, dllBytes []byte) (*BuildResult, error)
```
`Build`: write inputs to a temp dir → run obfuscator (4.1) → take `out.jar` → embed dll
marker (4.3) into the delivered dll → sha256 both → move into
`StagingDir/<buildId>/{module.jar, module.dll}` → return result. `buildId` = a ULID-ish
id generated server-side (also the jar watermark id).

### 4.3 DLL build-marker (embed) + manifest hash (sign) — "both"
The obfuscator watermarks **jars only**, so the dll marker is done in Go by appending a
trailer that PE loaders ignore (the OS maps a DLL from its PE headers; trailing bytes are
never executed/loaded):

```
trailer = MAGIC(8 "PHANTMK1") || buildIdLen(2) || buildId || HMAC_SHA256(secret, dllBody||buildId)(32) || totalLen(4)
markedDll = originalDll || trailer
```
- **Embed (marker):** appended at EOF → traceable to the build; extractable by reading the
  trailer from the end and verifying the HMAC with `FORGE_SECRET`.
- **Sign (manifest):** sha256 of the **marked** dll is recorded in the Ed25519-signed
  content manifest's `native_components` (the `content_manifests` table already has a
  `native_components` JSONB column). The loader verifies the manifest signature and the
  dll hash → a swapped/tampered dll is rejected.

Helpers: `MarkDLL(dll []byte, buildId, secret string) []byte` and
`ExtractDLLMarker(dll []byte, secret string) (buildId string, ok bool)`.

### 4.4 DB — `content_builds` table (new migration)
```sql
CREATE TABLE content_builds (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    build_id        TEXT UNIQUE NOT NULL,          -- also the jar watermark id + dll marker id
    module          TEXT NOT NULL,
    status          TEXT NOT NULL DEFAULT 'building'
                    CHECK (status IN ('building','pending_approval','live','denied','failed','superseded')),
    jar_path        TEXT, jar_sha256 TEXT,
    dll_path        TEXT, dll_sha256 TEXT,
    error           TEXT,
    created_by      TEXT,                            -- forge CLI operator label
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    decided_by      TEXT, decided_at TIMESTAMPTZ,    -- superadmin who approved/denied
    notes           TEXT
);
CREATE INDEX idx_content_builds_status ON content_builds(status);
CREATE INDEX idx_content_builds_module_status ON content_builds(module, status);
```

### 4.5 `cmd/forge` — the server-side CLI
```
phantom-forge build  --name <module> --in-jar <path> --in-dll <path> [--by <operator>]
phantom-forge status [--module <m>] [--id <buildId>]
```
`build` reads config from env (same `WATERMARK_*` + a `FORGE_*` set), inserts a
`content_builds` row (`building`), runs `forge.Build` in the background (detached so the
operator returns immediately — the "background job" choice), then sets the row to
`pending_approval` (or `failed` + `error`). The CLI links into the same Go module, so it
reuses `db`, `forge`, config.

### 4.6 Admin API — superadmin approve/deny (the panel "notification")
Registered in `internal/admin` under the existing `super_admin` middleware:
```
GET    /admin/builds?status=pending_approval   (super)  -> list (drives the panel badge)
GET    /admin/builds/:id                        (super)  -> detail
POST   /admin/builds/:id/approve                (super)  -> promote (4.7), audit-logged
POST   /admin/builds/:id/deny                   (super)  -> mark denied, purge staged, audit-logged
```
"Notification on the superadmin panel" = the panel polls `?status=pending_approval` and
shows a badge/list with Approve/Deny buttons. (UI is increment 3; API is increment 2.)

### 4.7 Promotion (on approve)
Atomic, supersede-aware:
1. Verify staged artifacts still match `jar_sha256`/`dll_sha256` (tamper check).
2. Install: copy staged `module.jar` → `content/modules/<module>.jar`, `module.dll` →
   `content/native/<module>.dll` (write-temp + rename for atomicity).
3. Rebuild + Ed25519-sign the content manifest including the dll hash in
   `native_components`; persist to `content_manifests`.
4. `UPDATE content_builds SET status='superseded' WHERE module=$m AND status='live'`,
   then this row → `live`, set `decided_by/at`.
5. Audit-log the promotion.
Rollback is "approve the previous build" (its staged artifacts are retained until purged).

### 4.8 Serve (unchanged)
`GET /content/module/:name` already serves `content/modules/<name>.jar` and applies the
per-user watermark in `ModuleBytes`. Native served via `GET /content/native/:name`. No
change needed beyond pointing at the promoted artifacts (which live at the standard paths).

## 5. Config / env additions
```
FORGE_ENABLED=true
FORGE_OBFUSCATOR_JAR=/opt/phantom/obfuscator.jar
FORGE_CONFIG=/opt/phantom/forge-prod.json
FORGE_SECRET=<hmac secret for jar watermark + dll marker>   # may reuse WATERMARK_SECRET
FORGE_STAGING_DIR=/content/_staging
FORGE_JAVA=java
```
The download-time `WATERMARK_*` set stays as-is (per-user watermark). `FORGE_SECRET` and
`WATERMARK_SECRET` may be the same value or distinct (build-trace vs per-user-trace).

## 6. Background-job mechanics
The forge CLI runs the obfuscator in the background and records state in `content_builds`,
so the panel reads progress from the DB — no in-process job queue needed for v1. (A future
option: an API-process worker goroutine that drains `building`/queued rows, decoupling
delivery from execution. Out of scope for v1.)

## 7. Security considerations
- Approve/deny restricted to `super_admin` (existing middleware), audit-logged.
- Promotion verifies staged hashes before install (no TOCTOU swap).
- `_staging` lives under `contentDir` but is **not** route-reachable (only `modules/` and
  `native/` are served); staged builds are never downloadable.
- `FORGE_SECRET` only on the forge host; never shipped. Jar watermark + dll marker are
  HMAC-signed, so forged marks fail verification (already proven for the jar watermark).
- Fail-closed promotion: any step failing aborts the swap, leaving the current `live`
  build intact.

## 8. Testing strategy (mirror the watermark test suite)
- **forge unit (Go, skips without java/obf jar):** `Build` on a sample jar+dll →
  assert `out.jar` extracts `build_id` (via `--extract-watermark`) and `out.dll` extracts
  the marker; staged files exist with matching sha256.
- **dll marker unit (Go, pure):** `MarkDLL`/`ExtractDLLMarker` round-trip; wrong secret
  rejected; original PE bytes unchanged (marker is strictly appended).
- **promotion integration (Go + docker Postgres):** seed a `pending_approval` build with
  staged artifacts → `approve` → assert files installed at `content/modules|native`,
  manifest signed with the dll hash, row `live`, prior `live` → `superseded`; `deny` →
  staged purged, nothing installed.
- **end-to-end (optional):** forge a payload → approve → `GET /content/module` returns the
  forged jar with a per-user watermark (compose with the existing HTTP download test).

## 9. Increments (recommended build order)
1. **Forge core** — `forge-prod.json`, `internal/forge` (`Build` + `MarkDLL`/`Extract`),
   `content_builds` migration + `db` helpers, `cmd/forge build`, inserts `pending_approval`.
   Tests: forge unit + dll-marker unit. *Fully testable with no UI.*
2. **Approval backend** — `/admin/builds` list/approve/deny + promotion (4.7) + manifest
   sign. Test: promotion integration.
3. **Superadmin panel UI** — pending-builds badge + Approve/Deny in `admin/admin/src`
   (thin layer over increment 2's API).
4. **(exists)** per-user watermark on download — already shipped.

## 10. Open questions / risks
- **MC-loadability of the forge-prod pass set (highest risk):** only `name+string+record+
  number` is confirmed loadable in-game so far. `forge-prod.json` here also enables
  `flatten`, `runtimeMangle`, and the `integrity` stage — these are **not yet MC-verified**.
  Before declaring forge-prod "production," the operator (in-game verifier) must confirm a
  forged build still loads in Minecraft; if any pass breaks loading, trim it from the config.
  Recommended: start `forge-prod.json` at the proven set and add passes one at a time with
  an in-game check each.
- **Manifest builder reuse:** promotion must call the existing stable-manifest builder so
  the dll hash lands in `native_components` correctly — confirm the builder accepts a
  native component + hash, or extend it. (Resolve when implementing increment 2.)
- **Module naming:** forge `--name` must map to the served module id used by entitlements
  (`NormalizeModuleName`/`ModuleAllowed`). Use the same normalization the content service
  uses so a forged module is immediately entitle-able.
- **Staging retention/GC:** denied/superseded staged dirs need a purge policy (manual on
  deny for v1; a TTL sweeper later).
- **Branch hygiene:** all forge work on a dedicated feature branch (not `go-server`, which
  is pushed/redeployed), per prior guidance.
```
