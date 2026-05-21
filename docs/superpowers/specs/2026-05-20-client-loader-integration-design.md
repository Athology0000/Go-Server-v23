# Phantom Client → Loader Integration & Hardening — Design

Date: 2026-05-20
Status: Approved design, pending implementation plan

## Goal

Ship the Phantom client as a sellable product where a paying customer installs
**one** thing — a bootstrapper executable. The bootstrapper authenticates the
user against the Go server, downloads the loader jar, and launches Minecraft.
The loader jar then does a second authentication stage, downloads the protected
client modules entitled to that user, and loads them **in memory** — never to
disk — wiping the bytes afterward.

Two outcomes:

1. **Integration** — the full Phantom client loads end-to-end through the
   loader pipeline instead of the legacy `PhantomAuthService` + `AddonLoader`
   startup path.
2. **Hardening** — apply the defenses in `java-loader-threat-model-notes.md`:
   block debugging/javaagents, in-memory-only module loading, byte wiping,
   pinned signing keys, no auth bypasses.

## Naming

The product is **Phantom**. All `cobalt` naming is removed and replaced with
`phantom` (see Phase 0). This document uses the final `phantom` names.

## Current state

Three pieces already exist:

- **`Go-Server/server`** (Go) — auth server, license panel, admin panel,
  Postgres + Redis. Already exposes:
  - `POST /auth/start`, `POST /auth/finish` — HWID-bound stage-1 auth used by
    the bootstrapper. `finish` returns `session_token`, `manifest_url`,
    `manifest_signature`, `plan_tier`, `enabled_modules`, `entitlement_expires_at`.
  - `POST /auth/verify-session` — stage-2 auth: takes a `session_token` +
    `minecraft_username`, returns `authorized`, `enabled_modules`,
    `manifest_url`, `manifest_signature`.
  - `POST /auth/heartbeat`.
  - `GET /content/manifest/:id`, `GET /content/module/:name`,
    `GET /content/native/:name` — all session-authed (Bearer token).
  - Already signs manifests (Ed25519) and stores AES-GCM encrypted modules in
    `content/modules/*.enc`.
- **`Go-Server/bootstrapper`** (Rust) — does enrollment + stage-1 auth,
  downloads the loader jar from `/content/module/<loader>`, SHA-256 verifies it,
  writes the session token to `config/<app>/session.token`, builds a Fabric
  classpath from the Prism install, and launches Minecraft directly.
- **`Loader/` project** (Kotlin/Fabric) — a clean, standalone loader mod with
  bootstrap auth, Ed25519 manifest verification, a module downloader, and a
  `CobaltRuntimeModule` API. Currently a stub: it does not know about the real
  Phantom client and caches downloaded modules to disk.

The **main repo (`src/`)** is the actual Phantom client. It already has a
build-time split: `splitProtectedModules` strips `org/phantom/internal/**` from
the public runtime jar and packages those packages as separate addon jars,
which are AES-GCM encrypted into `phantom-core/mining/slayer/diana.enc` and
synced to the server. The main repo currently boots via its own
`PhantomAuthService` + `internal/loader/AddonLoader` + `InMemoryAddonClassLoader`.

### The core problem

There are two parallel, incompatible loader designs (the main repo's built-in
one, and the standalone `Loader/` project), and the client is not yet delivered
through either in a sellable form.

### Hard constraint: mixins

Fabric mixins must be registered when Minecraft launches. They **cannot** be
hot-loaded from a jar downloaded mid-game. The Phantom client relies heavily on
mixins (`src/main/java/org/phantom/mixin/**`), plus a native pathfinder DLL and
the NanoVG/event/API layer. Therefore the loader jar **must already contain**
the entire public layer (mixins + API + bridge + native + render + pathfinder
JNI). Only the pure-Kotlin business logic in `org/phantom/internal/**` — the
actual macros, the real IP — can be downloaded after authentication.

## Decisions (locked)

- **Repo layout:** two Gradle projects. `Loader/` stays separate. It gains a
  Gradle dependency on a `client-public-api` artifact extracted from the main
  repo and shades that artifact into the loader jar.
- **Module crypto:** AES-GCM `.enc` modules + Ed25519-signed manifest. Keep
  encryption at rest and on the wire; the loader decrypts in memory.
- **Module loading:** in memory only. No disk cache. Decrypted jar bytes and
  per-class bytecode arrays are zero-filled after use.
- **Loader name:** `phantom.jar`, mod id `phantom`, package `org.phantom.loader`.

## Target runtime flow

```
bootstrapper.exe
  → /auth/start + /auth/finish        (HWID-bound stage-1 auth)
  → downloads phantom.jar             (SHA-256 verified)
  → writes config/phantom/session.token
  → launches MC with LOCKED hardened JVM args:
      rejects -javaagent / -agentlib / -agentpath / -Xdebug / -Xrunjdwp
      adds -XX:+DisableAttachMechanism
           -XX:-EnableDynamicAgentLoading
           -Djdk.attach.allowAttachSelf=false
           -Dcom.sun.management.jmxremote=false
  → starts heartbeat

phantom.jar (inside Minecraft)
  → RuntimeGuard: scan JVM input args, fail closed on agent/debug flags
  → init public infrastructure (pathfinder, events, commands) — NO modules yet
  → read config/phantom/session.token
  → stage-2 auth: POST /auth/verify-session {session_token, minecraft_username}
  → GET /content/manifest/:id  → Ed25519-verify vs PINNED release public key
  → per entitled module:
       GET /content/module/:name → .enc bytes held IN MEMORY
       → AES-GCM decrypt (key from signed manifest) → SHA-256 verify
       → AddonLoader.loadFromBytes(...) → InMemoryAddonClassLoader.defineClass
       → zero-fill each class byte array after defineClass
       → zero-fill decrypted jar bytes in finally
       (nothing written to disk)
  → GET /content/native/:name → native DLL written to config/phantom/natives/
       (the only unavoidable disk artifact; SHA-256 verified)
  → each loaded Addon.getModules() registers Modules into ModuleManager
  → start heartbeat; repeated failure → unload + wipe protected modules
```

## Phase 0 — Rebrand `cobalt` → `phantom`

Cross-cutting prerequisite. Replace all `cobalt` naming with `phantom`:

- **Loader project:** `org.cobalt.loader.*` → `org.phantom.loader.*`; Gradle
  root `CobaltLoaderProject` → `PhantomLoaderProject`; `cobalt-loader` →
  `phantom-loader`; mod id `cobalt_loader` → `phantom`.
- **Session/config paths:** `config/cobalt/` → `config/phantom/`,
  `config/cobalt/session.token` → `config/phantom/session.token`.
- **Loader artifact / endpoint:** `cobalt.jar` → `phantom.jar`;
  `/content/module/cobalt` → `/content/module/phantom`.
- **Main repo auth:** consolidate `org.cobalt.internal.auth.CobaltSession-realauth`
  into the existing `org.phantom.internal.auth.PhantomSession` (the main repo
  already references `PhantomSession`; the `Cobalt` file is a stale duplicate).
- **Go server:** Go module path `github.com/cobalt/server` → `github.com/phantom/server`;
  service names (`cobalt-public-api` → `phantom-public-api`); any `COBALT_*`
  env vars; `content/` paths.
- **Bootstrapper:** banner, `cobalt.jar` references, `config/cobalt`,
  credential file names.

Notes / risk:
- The Go module-path rename touches every `import` in the server — invasive but
  mechanical; do it in one commit with `go build` as the gate.
- The root working directory `…/Cobalt` is **not** renamed by this work (it is
  only a local path; renaming it breaks IDE/Gradle caches). Optional manual step
  for the user later.
- Drop the `Loader/` project's `shared-api`, `sample-module`, and
  `CobaltRuntimeModule` entirely — they are superseded by the real Phantom
  `Addon` API (Phase 1, section C).

## Phase 1 — Client → loader integration

### A. Main repo: `client-public-api` artifact

- Add a Gradle artifact/configuration that publishes the **public layer** as a
  consumable, Loom-remapped artifact: `org/phantom/{api,bridge,init,render,
  pathfinder,mixin}/**`, `phantom.mixins.json`, the native DLL, and `assets/`.
  It **excludes** `org/phantom/internal/**`. This is the existing
  `splitProtectedModules` output, exposed as an artifact the loader can consume.
- Split `Phantom.kt`: extract a `PhantomPublicInit` that performs only
  infrastructure setup (NativePathfinder, ChunkSerializer, HypixelManager,
  EventBus core listeners, command registration) and does **not** register any
  `internal/**` modules. The loader calls `PhantomPublicInit` after auth.
- Remove the legacy startup: `PhantomAuthService.start(...)` and
  `registerModules()` (the embedded-module path). Keep a dev-only escape hatch
  behind `isDevelopmentEnvironment` so `runClient` still works without a server.

### B. Main repo: protected module bundles

- Confirm and tidy the existing pipeline: `internal/**` → `phantom-core`,
  `phantom-mining`, `phantom-slayer`, `phantom-diana` addon jars → AES-GCM
  `.enc` → `Go-Server/server/content/modules/`.
- The encryption key flows: build-time `MODULE_ENCRYPTION_KEY` encrypts; the
  server holds the same key; the loader receives the decryption key inside the
  Ed25519-signed manifest (so a leaked manifest without a valid signature is
  useless).
- Each protected bundle keeps an `Addon` entrypoint (the existing
  `PhantomCoreAddon` / `PhantomMiningAddon` / etc. in `RemoteModuleAddons.kt`)
  whose `getModules()` returns the bundle's `Module`s.

### C. `Loader/` project becomes `phantom.jar`

- **Mappings:** switch `Loader/` from yarn mappings to `loom.officialMojangMappings()`
  to match the main repo. Mixins and public-layer classes compiled against one
  mapping set are not binary-compatible with the other. This is the single
  biggest build risk — validate early.
- **Drop** `shared-api`, `sample-module`, `CobaltRuntimeModule`. The loader uses
  the real `org.phantom.api.addon.Addon` API from the shaded `client-public-api`.
- **Depend on + shade** `client-public-api` into `phantom.jar`. Merge resources:
  the final `fabric.mod.json` declares `phantom.mixins.json`, the `preLaunch`
  entrypoint, and a single `client` entrypoint = the loader's initializer.
- **Loader bootstrap** (`org.phantom.loader`): keep and adapt the existing
  `Loader/` network/auth code —
  - `PhantomSession` — read `-Dphantom.session` / `config/phantom/session.token`.
  - `BootstrapAuthClient` — `POST /auth/verify-session`.
  - `BootstrapManifestVerifier` — fetch `/content/manifest/:id`, Ed25519-verify.
  - `BootstrapModuleDownloader` — `GET /content/module/:name`; **remove all disk
    caching** (the current `cacheDir` write path is deleted); return bytes only.
  - A decrypt step — AES-GCM decrypt `.enc` bytes with the manifest-supplied key.
  - Hand decrypted jar bytes to `AddonLoader.loadFromBytes(...)` (the real
    in-memory loader in the public layer) instead of the `Loader/` project's
    own `BootstrapModuleLoader`.
  - `BootstrapHeartbeatClient` — `POST /auth/heartbeat`; on repeated failure,
    `AddonLoader` unload.
- **Initializer order:** RuntimeGuard → `PhantomPublicInit` → stage-2 auth →
  module download/decrypt/load → register modules → heartbeat.

### Phase 1 exit criteria

A user runs the bootstrapper; Minecraft launches with `phantom.jar`; the loader
authenticates with a real session token, downloads the entitled modules, and
the full Phantom client (modules, HUD, commands) is usable in-game. No protected
class is written to disk.

## Phase 2 — Hardening

### D. Loader runtime protections

- **`RuntimeGuard`** — on init, read `ManagementFactory.getRuntimeMXBean()
  .inputArguments`; if any contains `-javaagent`, `-agentlib`, `-agentpath`,
  `-Xdebug`, `-Xrunjdwp`, `--patch-module`, or `-Xbootclasspath`, fail closed
  (throw before auth). Documented as a tripwire, not a guarantee.
- **`InMemoryAddonClassLoader`** — `entries.remove(path)` then zero-fill the
  byte array in a `finally` after `defineClass`. Never keep a long-lived
  `Map<String, ByteArray>` of class bytes.
- **Byte wiping** — decrypted jar bytes and the session-token byte array are
  zero-filled in `finally` blocks. Avoid `String` for the token where practical.
- **Pinned secrets** — release builds use compile-time constants for the server
  base URL and the manifest Ed25519 public key. The `System.getProperty` /
  `System.getenv` overrides are allowed **only** when
  `FabricLoader.isDevelopmentEnvironment` is true.
- **Remove bypasses** — delete `TEMP_BYPASS_ALIAS` (`"Iamaperson2004"`) and any
  alias-only auth bypass. Release auth always requires a valid signed manifest.

### E. Bootstrapper protections

- Launch Minecraft with a locked JVM-args file; the bootstrapper never passes
  through user-supplied or agent/debug flags.
- Add `-XX:+DisableAttachMechanism`, `-XX:-EnableDynamicAgentLoading`,
  `-Djdk.attach.allowAttachSelf=false`, `-Dcom.sun.management.jmxremote=false`.
- Implement the currently-stubbed `verify_integrity()` — compare the running
  executable's SHA-256 against a value embedded at build time.

## Phase 3 — Polish (later)

- Bundle a known Java runtime with the bootstrapper and hash-check
  `java.exe` / `jvm.dll` before launch.
- Wire the `Go-Server/obfuscator` pipeline into the protected-module build so
  `internal/**` bundles ship obfuscated.
- Per-session / per-user watermarked modules; server-side build revocation
  testing.

## Risks

- **Mapping mismatch (high):** yarn vs official Mojang mappings between the two
  Gradle projects. Mitigation: align `Loader/` to Mojang mappings in Phase 1.C
  and validate a shaded build before doing the rest of the integration.
- **Shading a remapped Fabric mod into another Fabric mod (medium):** mixin
  config discovery and the merged `fabric.mod.json` must be exactly right or
  mixins silently do not apply. Validate with a trivial mixin first.
- **Go module-path rename (low, invasive):** mechanical; `go build` is the gate.
- **Threat model honesty:** none of the loader-side defenses stop an attacker
  who fully controls the JVM/machine. They raise cost. The real protection is
  that protected bytecode is server-gated, short-lived, signature-verified, and
  never persisted client-side.

## Out of scope

- Renaming the root working directory.
- New client features / modules.
- Anti-cheat / game-detection behavior — this work is anti-piracy DRM only.
- Payment processing (handled by the existing license-key panel).
