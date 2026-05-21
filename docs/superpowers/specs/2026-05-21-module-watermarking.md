# Per-Session Module Watermarking + Revocation Testing — Design Note

Date: 2026-05-21
Status: Design note — not yet scheduled

## Goal

Two objectives for Phase 3:

1. **Watermarking** — when the server delivers a protected module bundle to an
   entitled user, the bundle should carry a traceable, per-user marker so that a
   leaked decrypted bundle can be attributed to the specific buyer.
2. **Revocation testing** — a concrete end-to-end test procedure that confirms
   the existing server-side revocation column and loader heartbeat path work
   correctly together.

Neither objective is yet implemented. This note records the design decisions
needed before scheduling implementation work.

## Current state

Today every user who downloads `phantom-core.enc` (or any other protected
bundle) receives **the same encrypted bytes**. The server builds one `.enc` file
per bundle and serves it to all entitled users. The AES-GCM key is embedded in
the Ed25519-signed manifest; the loader Ed25519-verifies the manifest, decrypts
in memory, and loads the classes. No per-user differentiation exists anywhere in
the pipeline.

The `content_manifests` table already has `revoked` and `expires_at` columns and
a `module_key`. The loader's heartbeat path already calls
`AddonLoader.unloadLoadedAddons()` and sets `Auth.state = AuthState.FAILED` on
repeated heartbeat failure, but no test has exercised the revocation → heartbeat
→ unload path end-to-end.

## Part A — Per-Session / Per-User Watermarking

### What the marker must survive

A watermark is only useful if it survives:
1. AES-GCM encryption/decryption (trivially: any bytecode change is preserved
   byte-for-byte by AES-GCM).
2. An attacker extracting and sharing the decrypted `.jar`. The attacker will
   almost certainly not obfuscate or re-compile; they dump what they got.

A watermark need **not** survive recompilation by the attacker. That is beyond
practical deterrence scope.

### Candidate marker techniques

**A1. Benign per-user constant (simplest)**
Inject a `static final String PHANTOM_WM = "<base64(account_id + nonce)>";`
field into one class in each bundle (e.g. a synthetic `__Wm` inner class, or
a constant in an existing utility class). The value changes per download.

Trade-offs: trivial to spot and strip with a hex editor if the attacker knows to
look. Provides low friction for casual leakers; provides no protection against
a determined analyst.

**A2. Synthetic annotation or marker class (medium)**
Add a `@PhantomLicense(user = "…", issued = "…")` annotation (with a retention
of `RUNTIME` or `CLASS`) to every top-level class, or inject a synthetic
`$phantom$wm` class into the jar. The marker is spread across many classes,
making bulk removal noisier.

Trade-offs: larger diff per user; obfuscation may rename or remove synthetic
annotations depending on keep-rules. More robust against casual stripping.

**A3. Constant-pool / ordering perturbations (steganographic)**
Rearrange the order of constant-pool entries, static field initializers, or
switch case orderings within the bytecode in a way that encodes bits of
`account_id` without changing observable behaviour. Requires an ASM-based
post-processing pass.

Trade-offs: survives obfuscation renaming better than named fields; significantly
harder to implement and verify for correctness; still visible to a bytecode diff
tool. Fragile if the obfuscator reorders constants itself.

**A4. Dead-code byte stuffing**
Insert unique, unreachable sequences of bytecode (dead stores, NOP sleds of
varying length) that encode bits of `account_id`. The JVM optimises them away
at runtime.

Trade-offs: similar to A3; more brittle across JVM versions; verifier may reject
structurally invalid dead code.

### Recommended approach

Start with **A1 + A2** in combination: one synthetic marker class containing a
`PHANTOM_WM` constant, plus the same value stamped as a `@PhantomUser` annotation
on each top-level class in the bundle. This covers the easy case (casual leaker
who shares a zip without modification) and requires explicit tool use to strip.

Reserve A3/A4 for a future hardening pass if needed.

### Watermark injection ordering: before or after obfuscation?

This is the key open question.

**Option 1 — Watermark BEFORE obfuscation**
The per-user marker is injected into the plain `.class` files, then the
obfuscator runs, then AES-GCM encryption.

- The obfuscator sees the marker. Depending on keep-rules:
  - Named constants / annotations survive if kept; they are renamed/removed if
    not explicitly kept.
  - Ordering perturbations (A3) may be reordered by the obfuscator.
  - String constants in a `PHANTOM_WM` field survive obfuscation if the field is
    kept; the value is opaque to the obfuscator.
- Advantage: the obfuscator's output is per-user, so two leaked copies differ in
  both the marker *and* in any nondeterministic obfuscator output (e.g. random
  name seeds). This maximises diff surface.
- Disadvantage: the obfuscator must run once **per user per download**, which is
  expensive if obfuscation is slow. The server cannot pre-compute obfuscated
  bundles and cache them.

**Option 2 — Watermark AFTER obfuscation (last step before encryption)**
The obfuscator runs once on the plain classes; the obfuscated output is cached.
At download time, the server applies a lightweight ASM pass to inject the
per-user marker into the already-obfuscated bytecode, then AES-GCM-encrypts.

- The marker must not break obfuscated bytecode. This is safe for A1/A2 (adding
  a new field or annotation to an existing class is always valid). It is risky
  for A3/A4 (reordering constants in already-obfuscated code requires careful
  ASM bookkeeping).
- Advantage: obfuscation is a build-time step; the server only does a fast
  per-request ASM pass. Scales to many concurrent downloads.
- Disadvantage: two leaked copies of the same version share identical obfuscated
  structure and differ only in the injected constant. A diff-aware attacker can
  mechanically strip the marker.

**Recommendation:** Option 2, with A1+A2 markers only. Obfuscation is a
build-time product step; watermarking is a lightweight per-request server step.
The current `Go-Server/obfuscator/` pipeline should produce a cached obfuscated
`.jar`; a Go ASM helper (using `asm-go` or a small custom reader/writer) injects
the `PHANTOM_WM` constant at serve time. If obfuscation proves fast enough
(< 1 s per bundle), revisit Option 1.

### Manifest implications

The Ed25519-signed manifest today contains the SHA-256 of each bundle. A
per-user bundle has a **different SHA-256**. Therefore:

- The server must **produce a per-user manifest** at download time: re-compute
  the SHA-256 of the watermarked `.enc`, update the manifest JSON, and re-sign
  it with the Ed25519 private key before returning it to the loader.
- The AES-GCM encryption key may remain shared (the marker is injected before
  encryption) or be per-user (an additional hardening option: if the key leaks,
  it only unlocks that user's bundle). Per-user AES keys add server-side key
  storage complexity; revisit in a later pass.
- The loader already verifies the manifest signature and SHA-256 before
  decrypting. No loader changes are required to support per-user manifests —
  the verification logic is identical.

### Out of scope for this note

- Watermark extraction tooling (the recovery path if a leak is detected).
- Legal/ToS enforcement downstream of attribution.
- Anti-tamper detection at runtime.

---

## Part B — Build-Revocation Test Path

### What "revocation" means here

Server-side revocation means flipping `revoked = true` on a row in
`content_manifests`. The server's manifest endpoint checks this flag and returns
an error (currently HTTP 403 or a manifest with `revoked: true`) so the loader
cannot obtain a valid manifest. The next heartbeat failure triggers the unload
path.

### Concrete test steps

**Prerequisites:**
- A development instance of the Go server running locally (or in staging).
- A Minecraft client running with `phantom.jar` loaded, authenticated, and
  modules active — i.e. the Phase 1 + 2 integration is working.
- Direct database access (psql or the admin panel).

**Step 1 — Baseline**
Confirm the client is running normally: modules are loaded, HUD is visible, the
heartbeat is firing every N seconds (check server logs or add a log line to
`BootstrapHeartbeatClient`). Note the `manifest_id` in use (logged at startup or
visible in the session token response).

**Step 2 — Revoke the manifest**
In psql (or via the admin panel's revoke endpoint if implemented):
```sql
UPDATE content_manifests SET revoked = true WHERE id = '<manifest_id>';
```
Do not restart the server or the client.

**Step 3 — Observe heartbeat failure**
The next heartbeat `POST /auth/heartbeat` should return a non-200 response (or
the server should return a `revoked: true` payload — confirm which signal the
server sends). The loader's `BootstrapHeartbeatClient` increments its failure
counter.

**Step 4 — Confirm unload threshold**
After the configured number of consecutive heartbeat failures (check
`BootstrapHeartbeatClient` for the threshold constant), confirm in client logs
that `AddonLoader.unloadLoadedAddons()` is called and that
`Auth.state = AuthState.FAILED` is set.

**Step 5 — Confirm in-game effect**
The protected modules (macros, HUD) should be unavailable. The loader should
surface an auth-failure state to the user (UI message or overlay). Confirm no
protected class is re-loadable without a fresh authentication.

**Step 6 — Restore and re-authenticate (negative test)**
Set `revoked = false` again. Restart the Minecraft client (do not hot-reload).
Confirm the full auth + module download + load cycle completes successfully,
proving that revocation is a state change and not permanent corruption.

### What this test does NOT cover

- Graceful handling of a revocation mid-session where the manifest endpoint
  itself is the signal (vs. the heartbeat endpoint). The server may need a
  separate code path to propagate revocation to `POST /auth/heartbeat` — verify
  the actual server logic before writing the test.
- Watermarked bundles (Part A is not yet implemented; tests here use the shared
  bundle).

### Automation

Once the manual test passes, the steps above translate directly into an
integration test fixture: seed a manifest, authenticate a test session, revoke,
tick the heartbeat N times, assert `AuthState.FAILED`. This can live in a
`Go-Server/server/integration/` test package alongside the existing server
handler tests.

## Risks

- **Obfuscator nondeterminism:** if the obfuscator generates nondeterministic
  output (random name seeds per run), Option 2 requires a stable cached artifact.
  Pin the obfuscator's random seed at build time and treat the obfuscated bundle
  as a build artifact to be stored, not regenerated per request.
- **ASM correctness for per-user injection:** injecting into obfuscated bytecode
  must be tested with the actual obfuscator output. Use `javap` / `ASMifier` to
  validate the injected class before shipping.
- **Manifest signing key availability at serve time:** the Ed25519 private key
  must be accessible to the server process to re-sign per-user manifests. Ensure
  it is in the server's key store and not a build-time-only secret.
- **Revocation signal ambiguity:** the heartbeat endpoint and the manifest
  endpoint are different; confirm which one the server uses to signal revocation
  and that the loader handles it consistently.

## Out of scope

- Watermark extraction / forensic tooling.
- Per-user AES-GCM keys (future hardening pass).
- Any client changes — the loader's manifest verification path handles per-user
  manifests without modification.
- New anti-piracy defenses beyond what is described here.
