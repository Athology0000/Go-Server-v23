# Bootstrapper

The Phantom bootstrapper is a Rust executable that authenticates the user, verifies the starter jar, writes the session token, and launches Minecraft directly (via Fabric Loader) without requiring PrismLauncher to be running.

## Build

```sh
cargo build --release
```

The resulting binary is `target/release/bootstrapper.exe` (Windows).

## Configuration

The bootstrapper reads `config/phantom/bootstrap.toml` from the working directory (i.e., the Minecraft instance folder). See `src/config.rs` for the full schema.

## Integrity model

### Bootstrapper self-check

At startup the bootstrapper SHA-256-hashes itself and compares against the digest supplied at runtime or baked in at compile time via the `PHANTOM_BOOTSTRAPPER_SHA256` environment variable. If the digest is absent, the check emits a warning and continues. If the digest is present but malformed or does not match, the check fails closed.

---

## Bundled JRE verification

### Goal

Phase 3 of Phantom hardening ships a pinned JRE alongside the bootstrapper. Before launching Minecraft, the bootstrapper will SHA-256-hash `java.exe` and `jvm.dll` and compare them against known-good digests. This prevents an attacker from substituting an instrumented JVM to intercept the session token or decrypt class files.

### Digest supply

Digests are supplied via two environment variables:

| Variable | File verified |
|---|---|
| `PHANTOM_JAVA_EXE_SHA256` | `<jre_root>/bin/java.exe` |
| `PHANTOM_JVM_DLL_SHA256` | `<jre_root>/bin/server/jvm.dll` |

Each variable is read at runtime via `std::env::var` with an `option_env!` compile-time fallback baked into the binary at release-build time (the same pattern used by `PHANTOM_BOOTSTRAPPER_SHA256`).

The digest value must be a lowercase 64-character hex string (SHA-256). If it is present but malformed the check fails closed immediately.

### Failure behaviour

On a hash mismatch the bootstrapper returns an error that propagates up through `launch::run()` and is treated as a fatal launch failure — the process exits with a non-zero code.

### SCAFFOLD status

**This check is currently a non-breaking scaffold.** Until the bundled JRE is actually shipped and the digests are configured, the check is a **no-op that emits a warning and continues**. This is a deliberate divergence from the `PHANTOM_BOOTSTRAPPER_SHA256` check (which fails closed when unset) — failing closed here would break every launch before the JRE bundle exists.

The scaffold lives in `src/launch.rs` as `verify_runtime()`. The following items are deferred to Phase 3:

- **Ship the bundled JRE** at a known relative path alongside the bootstrapper.
- **Resolve the absolute path** of `java.exe` from the bundled JRE root (instead of the bare `"java.exe"` command name currently resolved via `PATH`).
- **Hash `jvm.dll`** once its path is derivable from the bundled JRE location (`<jre_root>/bin/server/jvm.dll` on Windows).
- **Make missing digests fail closed** in release builds (mirror `verify_integrity()`).
- **Add the digests to CI** so they are baked in at build time via `option_env!`.
