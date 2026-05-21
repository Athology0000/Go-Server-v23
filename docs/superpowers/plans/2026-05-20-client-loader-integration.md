# Phantom Client → Loader Integration & Hardening — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Finish delivering the Phantom client as a sellable bootstrapper → `phantom.jar` loader pipeline, with the threat-model hardening from the design doc applied.

**Architecture:** Two Gradle projects. The main repo (`src/`) compiles the full client and publishes a `phantom-client-public` artifact — the public layer (mixins, API, native, `internal/auth` + `internal/loader` infrastructure) with the protected `internal/**` macro packages stripped. The `Loader/` project shades that artifact into `phantom.jar`, which authenticates at runtime, downloads + decrypts the protected module bundles in memory, and registers them. The Rust bootstrapper does stage-1 auth, downloads `phantom.jar`, and launches Minecraft with locked hardened JVM args.

**Tech Stack:** Kotlin 2.3 / Fabric Loom 1.16 / MC 1.21.11, Gradle, Go (server), Rust (bootstrapper). Verification in this environment is **Gradle build only** — Go and Rust toolchains are absent; native DLL build is skipped (`-x buildNative`).

---

## Current State Audit (verified 2026-05-21, branch `loader`)

Already complete — **do not redo**:

- **Phase 0 (rebrand):** done. `org.phantom.*` everywhere; Go backend, bootstrapper, loader project rebranded. Commits `f08703a`, `272a1b4`, `8f465a1`.
- **Phase 1.A (public init split):** `PhantomPublicInit` extracted (`src/main/kotlin/org/phantom/PhantomPublicInit.kt`); `Phantom.onInitializeClient()` calls it. Commit `750ed98`.
- **Phase 1.A (artifact):** `clientPublicApiJar` task + `clientPublicApi` maven publication exist in `build.gradle.kts`. Commit `8a17ce6`.
- **Phase 1.B:** the `splitProtectedModules` / `packagePhantom*ModuleJar` / `encryptPhantom*Module` / `syncPhantomModulesToServer` pipeline exists and is intact in `build.gradle.kts`.
- **Phase 1.C (loader code):** the entire `Loader/loader/src/main/kotlin/org/phantom/loader/**` package is written — `PhantomLoaderClient`, `RuntimeGuard`, `PhantomSession`, `LoaderConfig`, and `bootstrap/` (`BootstrapStarter`, `BootstrapAuthClient`, `BootstrapManifestVerifier`, `BootstrapContentClient`, `BootstrapHeartbeatClient`, `BootstrapModels`, `ModuleCrypto`, `HttpSupport`). The old `org/cobalt/loader/**`, `shared-api`, `sample-module` are deleted. `Loader/loader/build.gradle.kts` shades `phantom-client-public` and uses `loom.officialMojangMappings()`. `fabric.mod.json` declares the `phantom` mod id, `PhantomLoaderClient` client entrypoint, `org.phantom.PreLaunch` preLaunch, and `phantom.mixins.json`.
- **Phase 2.D (partial):** `RuntimeGuard` exists and is called first in `PhantomLoaderClient.onInitializeClient()`. `InMemoryAddonClassLoader.findClass()` does `entries.remove(path)` + `bytecode.fill(0)` in a `finally`. `BootstrapStarter.loadModule()` and `ModuleCrypto.decryptAesGcm()` zero-fill encrypted/decrypted/key/nonce buffers. `internal/auth/PhantomSession.kt` `isTempAliasBypass()` already returns `false` (the `TEMP_BYPASS_ALIAS` constant is gone).
- **Phase 2.E (bootstrapper):** **done.** `launch.rs` writes the locked JVM-args file with `-XX:+DisableAttachMechanism`, `-XX:-EnableDynamicAgentLoading`, `-Djdk.attach.allowAttachSelf=false`, `-Dcom.sun.management.jmxremote=false`. `main.rs` `verify_integrity()` compares the running exe SHA-256 against `PHANTOM_BOOTSTRAPPER_SHA256`. `manifest.rs` threads `module_key` through the signed payload.
- **Server `module_key` plumbing:** `db/manifests.go` (column + scan + insert), `content/manifest.go`, `admin/handler.go` all carry `module_key`. Migration `Go-Server/server/migrations/006_manifest_module_key.sql` exists.

Outstanding work — **this plan**:

- **Dead code from the legacy startup path is still present:** `PhantomAuthService.kt` (0 callers), `DevUnlock.kt` (0 callers), and `Phantom.registerModules()` (private, 0 callers) with its large unused `internal/**` import block.
- The `phantom-client-public` artifact in mavenLocal may be stale; the Loader has never been verified to build against it.
- Phase 2.D pinned secrets: `LoaderConfig.RELEASE_MANIFEST_PUBLIC_KEY_B64` is the literal placeholder `"REPLACE_WITH_BASE64_ED25519_PUBLIC_KEY"`; the main repo's `PhantomAuthService` placeholder goes away when that file is deleted.
- Phase 3 is entirely unstarted (obfuscator wiring, bundled JRE, watermarking).

### Known build-environment facts

- `gradle.properties` auto-bumps `modVersion` (minor) whenever the `build` or `buildRelease` task runs on the release channel, and rewrites `gradle.properties`. Running `clientPublicApiJar` / `publishToMavenLocal` directly does **not** trigger the bump — prefer those for integration work.
- `Loader/gradle.properties` pins `phantom_client_version` (currently `1.320.0`); it must equal the published main-repo version or the Loader's dependency resolution fails.
- The native build (`buildNative`) requires CMake + a configured `natives/build` dir; pass `-x buildNative` and `-x copyNativeDll` if it is not configured.

---

## Task 1: Remove the legacy startup path (Phase 1.A)

**Files:**
- Delete: `src/main/kotlin/org/phantom/internal/auth/PhantomAuthService.kt`
- Delete: `src/main/kotlin/org/phantom/internal/auth/DevUnlock.kt`
- Modify: `src/main/kotlin/org/phantom/Phantom.kt`

- [ ] **Step 1: Confirm both files are dead code**

Run:
```bash
grep -rn "PhantomAuthService\|DevUnlock" src/ --include=*.kt --include=*.java | grep -v "object PhantomAuthService" | grep -v "object DevUnlock"
```
Expected: no output. (If anything prints, stop and inspect the caller before deleting.)

- [ ] **Step 2: Delete the two dead files**

```bash
git rm src/main/kotlin/org/phantom/internal/auth/PhantomAuthService.kt
git rm src/main/kotlin/org/phantom/internal/auth/DevUnlock.kt
```

- [ ] **Step 3: Rewrite `Phantom.kt` to drop `registerModules()` and its dead imports**

Replace the entire file with:

```kotlin
package org.phantom

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.phantom.internal.BuiltinModules
import org.phantom.internal.loader.AddonLoader

/**
 * Dev-only client entrypoint. In production the loader project's
 * [org.phantom.loader.PhantomLoaderClient] owns initialization and module
 * registration; this entrypoint exists so `runClient` works without a server.
 */
@Suppress("UNUSED")
object Phantom : ClientModInitializer {

  override fun onInitializeClient() {
    PhantomPublicInit.init()
    if (shouldRegisterEmbeddedModules()) {
      BuiltinModules.register()
    }
    AddonLoader.activateLoadedAddons()

    println("Phantom Client Initialized")
  }

  private fun shouldRegisterEmbeddedModules(): Boolean {
    // Production module registration is owned by org.phantom.loader.PhantomLoaderClient.
    if (FabricLoader.getInstance().isDevelopmentEnvironment) return true

    return (System.getProperty("phantom.embeddedModules")
      ?.trim()
      ?.lowercase()
      in setOf("1", "true", "yes", "on")
      )
  }
}
```

- [ ] **Step 4: Verify the main repo still compiles**

Run:
```bash
./gradlew compileKotlin -x buildNative -x copyNativeDll
```
Expected: `BUILD SUCCESSFUL`. (If `BuiltinModules` or `AddonLoader` no longer resolve, stop — that signals an unexpected protected-package split.)

- [ ] **Step 5: Commit**

```bash
git add -A src/main/kotlin/org/phantom/
git commit -m "refactor(client): delete legacy PhantomAuthService startup path

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>"
```

---

## Task 2: Publish and verify the `phantom-client-public` artifact (Phase 1.A)

**Files:**
- Read-only: `build.gradle.kts` (the `clientPublicApiJar` task + `publishing` block — already present)

- [ ] **Step 1: Build and publish the public-layer artifact**

Run:
```bash
./gradlew clientPublicApiJar publishToMavenLocal -x buildNative -x copyNativeDll
```
Expected: `BUILD SUCCESSFUL`. This compiles + remaps the client, strips the protected packages, and installs `org.phantom:phantom-client-public:<modVersion>` into `~/.m2`. It does **not** auto-bump the version (no `build` task in the request).

- [ ] **Step 2: Confirm the published version matches the Loader's pin**

Run:
```bash
grep '^modVersion=' gradle.properties
grep '^phantom_client_version=' Loader/gradle.properties
```
Expected: the two version numbers are equal. If they differ, edit `Loader/gradle.properties` so `phantom_client_version` equals `modVersion`.

- [ ] **Step 3: Verify the artifact stripped the protected packages but kept the public layer**

Run (substitute the real version):
```bash
unzip -l ~/.m2/repository/org/phantom/phantom-client-public/*/phantom-client-public-*.jar | grep -E "org/phantom/internal/(mining|combat|diana|garden)/" | head
```
Expected: **no output** — protected macro packages are absent.

Run:
```bash
unzip -l ~/.m2/repository/org/phantom/phantom-client-public/*/phantom-client-public-*.jar | grep -E "org/phantom/internal/loader/AddonLoader|org/phantom/internal/auth/AuthState|org/phantom/PhantomPublicInit|phantom.mixins.json" 
```
Expected: all four entries present — these are the public-layer classes the loader depends on.

- [ ] **Step 4: Commit (only if `Loader/gradle.properties` changed)**

```bash
git add Loader/gradle.properties
git commit -m "build: sync loader phantom_client_version to published artifact

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>"
```

---

## Task 3: Build the Loader as `phantom.jar` (Phase 1.C)

**Files:**
- Read-only first: `Loader/loader/build.gradle.kts`, `Loader/loader/src/main/resources/fabric.mod.json`

- [ ] **Step 1: Build the Loader project**

Run:
```bash
cd Loader && ./gradlew build && cd ..
```
Expected: `BUILD SUCCESSFUL`, producing `Loader/loader/build/libs/phantom-<version>.jar`.

- [ ] **Step 2: If compilation fails on mapping mismatch**

The highest-risk failure is yarn-vs-Mojang mapping incompatibility. Symptoms: unresolved Minecraft symbols (`Minecraft.getInstance`, `user.name`) in `BootstrapStarter.kt`. The Loader is already on `loom.officialMojangMappings()` (matching the main repo) — if symbols still fail, confirm `Loader/gradle.properties` `minecraft_version`/`loader_version` match the main repo's `libs.versions.toml`. Fix the version mismatch, do not change mappings.

- [ ] **Step 3: If compilation fails on a missing `org.phantom.*` symbol**

That means the symbol was stripped into a protected bundle. Cross-check the failing class against `phantomProtectedIncludes` in `build.gradle.kts`. The loader may only depend on non-protected packages: `org/phantom/{api,bridge,init,render,pathfinder,mixin}/**`, `org/phantom/PhantomPublicInit`, `org/phantom/PreLaunch`, `org/phantom/internal/auth/**`, `org/phantom/internal/loader/**`. If a genuinely-needed class is protected, the fix is to remove that package from `phantomProtectedIncludes` and re-run Task 2 — not to weaken the loader.

- [ ] **Step 4: Verify the merged jar contents**

Run:
```bash
unzip -l Loader/loader/build/libs/phantom-*.jar | grep -E "phantom.mixins.json|org/phantom/loader/PhantomLoaderClient|org/phantom/mixin/|fabric.mod.json"
```
Expected: exactly one `fabric.mod.json`, `phantom.mixins.json` present, the loader classes present, and `org/phantom/mixin/**` classes present (public-layer mixins were shaded in).

- [ ] **Step 5: Verify only the loader's `fabric.mod.json` survived the merge**

Run:
```bash
unzip -p Loader/loader/build/libs/phantom-*.jar fabric.mod.json | grep -E '"id"|PhantomLoaderClient'
```
Expected: `"id": "phantom"` and the `PhantomLoaderClient` entrypoint — confirming the `clientPublicLayer` shade excluded the public layer's own `fabric.mod.json` (the `exclude(...)` in `Loader/loader/build.gradle.kts` `jar` task).

- [ ] **Step 6: Commit**

```bash
git add -A Loader/
git commit -m "build(loader): verify phantom.jar builds against shaded client-public-api

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>"
```

---

## Task 4: Phase 1 exit — review the loader bootstrap flow

No code unless a defect is found; this is a correctness review of `BootstrapStarter.start()` against the design's "Target runtime flow".

- [ ] **Step 1: Verify the init order**

Read `BootstrapStarter.start()`. Confirm the order is: read session → `verify-session` → `fetchAndVerify` manifest → install required natives → `PhantomPublicInit.init()` → per-module download/decrypt/`loadFromBytes(activate=false)` → `activateLoadedAddons()` → `start` heartbeat. Confirm `PhantomPublicInit.init()` runs **before** any `loadFromBytes` (modules' `getModules()` may touch public infrastructure).

- [ ] **Step 2: Verify the manifest signature payload matches the server**

Compare `BootstrapManifestVerifier.SignedManifestPayload` field order/names against `signedManifestPayload` in `Go-Server/server/internal/content/manifest.go`: `build_id, channel, minimum_loader_version, module_key (omitempty), modules, native_components`. The loader tries both with-key and without-key payloads, so an empty `module_key` still verifies. Confirm `ManifestModule`/`ManifestNative` field sets match `db.ManifestModule`/`db.ManifestNative`. If they diverge, fix the Kotlin `@Serializable` models to match the Go JSON.

- [ ] **Step 3: Verify no protected bytecode touches disk**

Confirm `BootstrapContentClient.downloadModule()` returns bytes only (no disk write), `BootstrapStarter.loadModule()` passes bytes straight to `AddonLoader.loadFromBytes(...)`, and the only disk write is `NativeLoader.installDownloaded(...)` for the DLL. Confirm there is no `cacheDir` write path anywhere under `Loader/loader/src/main/kotlin/org/phantom/loader/`.

- [ ] **Step 4: Verify the heartbeat unload path**

Confirm `BootstrapHeartbeatClient` calls `AddonLoader.unloadLoadedAddons()` after `LoaderConfig.heartbeatFailureLimit` consecutive failures, and that `unloadLoadedAddons()` removes modules from `ModuleManager`.

- [ ] **Step 5: Record findings**

If all four checks pass, write a one-line note in the commit for Task 5. If a defect is found, fix it, re-run Task 3's build, and commit separately with a `fix(loader):` message.

---

## Task 5: Phase 2.D — hardening verification and the pinned-key guard

**Files:**
- Read-only: `Loader/loader/src/main/kotlin/org/phantom/loader/RuntimeGuard.kt`, `InMemoryAddonClassLoader.kt`
- Modify: `Loader/loader/src/main/kotlin/org/phantom/loader/LoaderConfig.kt`
- Modify: `Loader/loader/src/main/kotlin/org/phantom/loader/bootstrap/BootstrapManifestVerifier.kt` (verify only)

- [ ] **Step 1: Verify RuntimeGuard coverage**

Confirm `RuntimeGuard.blockedArgs` contains `-javaagent`, `-agentlib`, `-agentpath`, `-Xdebug`, `-Xrunjdwp`, `--patch-module`, `-Xbootclasspath`, and that `RuntimeGuard.verify()` is the first statement in `PhantomLoaderClient.onInitializeClient()`. No change expected.

- [ ] **Step 2: Verify the in-memory classloader wipe**

Confirm `InMemoryAddonClassLoader.findClass()` does `entries.remove(path)` and `bytecode.fill(0)` in a `finally`. No long-lived `Map<String, ByteArray>` of class bytes is retained after `defineClass`. No change expected.

- [ ] **Step 3: Confirm the pinned manifest key is still a placeholder**

Run:
```bash
grep RELEASE_MANIFEST_PUBLIC_KEY_B64 Loader/loader/src/main/kotlin/org/phantom/loader/LoaderConfig.kt
```
If it still reads `REPLACE_WITH_BASE64_ED25519_PUBLIC_KEY`, the real key cannot be filled in here — it is the public half of the server's Ed25519 signing key and is an operator/deployment secret. Leave the placeholder; `BootstrapManifestVerifier.verifySignature()` already fails closed on a `REPLACE_WITH_` prefix. **Document this as a release-blocking manual step** (see Task 9 deliverable / repo `Loader/docs/SERVER_CONTRACT.md`).

- [ ] **Step 4: Add a fail-closed comment marker to LoaderConfig**

In `LoaderConfig.kt`, change the placeholder constant line so the release-blocking nature is unmissable. Replace:
```kotlin
    private const val RELEASE_MANIFEST_PUBLIC_KEY_B64 = "REPLACE_WITH_BASE64_ED25519_PUBLIC_KEY"
```
with:
```kotlin
    // RELEASE BLOCKER: paste the base64 Ed25519 public key (public half of the
    // server's manifest signing key) before shipping. BootstrapManifestVerifier
    // fails closed while this is the placeholder value.
    private const val RELEASE_MANIFEST_PUBLIC_KEY_B64 = "REPLACE_WITH_BASE64_ED25519_PUBLIC_KEY"
```

- [ ] **Step 5: Verify dev-only override gating**

Confirm in `LoaderConfig.kt` that `serverBaseUrl`, `manifestPublicKeyB64`, `moduleKeyB64`, and `allowLocalhostHttp` all funnel through `devOverride(...)` / `isDev`, so `System.getProperty`/`getenv` overrides are inert in a release (`FabricLoader.isDevelopmentEnvironment == false`) build. No change expected.

- [ ] **Step 6: Rebuild the Loader and commit**

```bash
cd Loader && ./gradlew build && cd ..
git add Loader/loader/src/main/kotlin/org/phantom/loader/LoaderConfig.kt
git commit -m "chore(loader): flag the pinned manifest key as a release blocker

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>"
```

---

## Task 6: Phase 2.E — bootstrapper hardening verification

Verification only — the Rust toolchain is absent, so no `cargo build`. Confirm the committed/staged changes are coherent.

- [ ] **Step 1: Verify the locked JVM args**

Confirm `Go-Server/bootstrapper/src/launch.rs` `write_java_args_file()` emits `-XX:+DisableAttachMechanism`, `-XX:-EnableDynamicAgentLoading`, `-Djdk.attach.allowAttachSelf=false`, `-Dcom.sun.management.jmxremote=false`, and that the args file is built from a fixed template (no pass-through of user-supplied flags).

- [ ] **Step 2: Verify `verify_integrity()`**

Confirm `Go-Server/bootstrapper/src/main.rs` `verify_integrity()` reads `PHANTOM_BOOTSTRAPPER_SHA256` (env or `option_env!`), requires a 64-char hex digest, compares it to the running exe's SHA-256, and fails closed in release builds when unset (`cfg!(debug_assertions)` is the only escape hatch).

- [ ] **Step 3: Note the un-runnable verification**

Record in the Task 7 commit message that the bootstrapper was reviewed but not compiled (no Cargo in this environment). No code change.

---

## Task 7: Phase 3 — wire the obfuscator into the protected-module build

**Files:**
- Read-only first: `Go-Server/obfuscator/build.gradle`, `Go-Server/obfuscator/obfuscate.bat`, `Go-Server/obfuscator/src/**`
- Modify: `build.gradle.kts` (main repo)

- [ ] **Step 1: Determine the obfuscator's invocation contract**

Read `Go-Server/obfuscator/obfuscate.bat` and `build.gradle` to learn how it is invoked (input jar, output jar, library path `-li`). The main repo already has a `collectObfLibs` task that stages the compile classpath into `build/obf-libs` for exactly this `-li` argument.

- [ ] **Step 2: Add a Gradle task that obfuscates each protected bundle**

In `build.gradle.kts`, after the `packagePhantom*ModuleJar` tasks and before `encryptPhantom*Module`, register one `Exec`/`JavaExec` task per bundle (or a single task looping the four jars) that runs the obfuscator on `build/phantom-modules/plain/<bundle>.jar`, writing `build/phantom-modules/obfuscated/<bundle>.jar`, with `collectObfLibs` as a dependency. Use the exact invocation contract from Step 1 — do not invent flags.

- [ ] **Step 3: Repoint the encrypt tasks at the obfuscated jars**

Change each `encryptPhantom*Module` task's `plainJar` input from `phantom-modules/plain/<bundle>.jar` to `phantom-modules/obfuscated/<bundle>.jar`, and add the obfuscation task as a `dependsOn`. Gate the whole obfuscation step behind a Gradle property (e.g. `phantomObfuscate`, default `!isDevBuild`) so dev builds stay fast and debuggable.

- [ ] **Step 4: Verify the wiring resolves**

Run:
```bash
./gradlew packagePhantomModules -x buildNative -x copyNativeDll
```
Expected: `BUILD SUCCESSFUL`. (A full `encryptPhantomModules` run also needs `MODULE_ENCRYPTION_KEY`; only run that if the key is available — otherwise stop at `packagePhantomModules`.)

- [ ] **Step 5: Commit**

```bash
git add build.gradle.kts
git commit -m "build: obfuscate protected module bundles before encryption

Bootstrapper Phase 2.E changes reviewed (not compiled — no Cargo locally).

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>"
```

---

## Task 8: Phase 3 — bundled-JRE hash check (design + bootstrapper stub)

The full feature ships a known JRE with the bootstrapper. The Rust toolchain is absent, so this task delivers the **design and a compile-guarded stub**, not a verified build.

- [ ] **Step 1: Document the approach**

Append a "Bundled JRE" section to `Go-Server/bootstrapper/README.md` (create the file if absent): the bootstrapper ships a pinned JRE under a known relative path; before launch it SHA-256s `java.exe` and `jvm.dll` against digests embedded via `option_env!` (`PHANTOM_JAVA_EXE_SHA256`, `PHANTOM_JVM_DLL_SHA256`), failing closed on mismatch in release builds — mirroring `verify_integrity()`.

- [ ] **Step 2: Add the stub function**

In `Go-Server/bootstrapper/src/launch.rs`, add a `verify_runtime() -> Result<(), String>` function shaped exactly like `verify_integrity()` (env/`option_env!` lookup, 64-hex validation, `cfg!(debug_assertions)` escape hatch), checking the two runtime files. Call it from the launch path next to `verify_integrity()`. Mark it with a `// TODO(phase3): enable once the bundled JRE path is finalized` comment.

- [ ] **Step 3: Note the verification gap**

This cannot be `cargo build`-verified here. Flag in the commit message and in the final summary that the bootstrapper changes need a `cargo build` + `cargo test` pass on a machine with the Rust toolchain.

- [ ] **Step 4: Commit**

```bash
git add Go-Server/bootstrapper/
git commit -m "feat(bootstrapper): scaffold bundled-JRE hash verification (phase 3)

Not compiled locally — needs cargo build on a Rust-equipped machine.

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>"
```

---

## Task 9: Phase 3 — per-session watermarking + revocation (design note)

Watermarking touches build infrastructure and the Go server; it is design-level here. Deliver a written spec, not an implementation.

- [ ] **Step 1: Write the design note**

Create `docs/superpowers/specs/2026-05-21-module-watermarking.md` describing: (a) per-session watermarked module bundles — the server injects a per-download marker (e.g. a benign constant or class annotation keyed to `account_id`) so a leaked bundle is traceable; (b) the build-revocation test path — revoke a manifest server-side, confirm the loader's next heartbeat triggers `unloadLoadedAddons()`. Note the open question: watermarking after obfuscation vs. before.

- [ ] **Step 2: Update the integration design doc status**

In `docs/superpowers/specs/2026-05-20-client-loader-integration-design.md`, change the `Status:` line to reflect that Phases 1–2 are implemented and Phase 3 is partially scaffolded, and link the watermarking spec.

- [ ] **Step 3: Commit**

```bash
git add docs/superpowers/specs/
git commit -m "docs: watermarking spec + integration design status update

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>"
```

---

## Final verification

- [ ] **Step 1: Full main-repo build**

Run:
```bash
./gradlew build -x buildNative -x copyNativeDll
```
Expected: `BUILD SUCCESSFUL`. Note: this auto-bumps `modVersion`. If it bumps, re-run Task 2 (publish) and Task 3 (Loader build) so the Loader consumes the new version, and update `Loader/gradle.properties`.

- [ ] **Step 2: Full Loader build**

Run:
```bash
cd Loader && ./gradlew build && cd ..
```
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Confirm clean tree**

Run `git status` — every change should be committed. Summarize per-phase what was verified vs. what remains a documented manual/toolchain-blocked step (pinned key, `cargo build`, `MODULE_ENCRYPTION_KEY`-gated encryption, native DLL).

## Out of scope

- Renaming the root working directory.
- New client features / modules.
- Anti-cheat behavior — this is anti-piracy DRM only.
- Payment processing.
- Generating or distributing real signing keys.
