# Manifest Rollback / Downgrade Protection — Design

Date: 2026-06-15
Status: Draft (implementing under the "finish the scope" goal)
Repos: server `Go-Server-v23/Go-Server/server` **and** client `Full phantom Recode`

## Goal

Stop a controlled URL / MITM / stale mirror from serving an older, still-validly-signed,
non-expired manifest to roll a client back to a previous bundle. Add a **signed monotonic
epoch** to the manifest and enforce a **tamper-resistant per-channel high-water-mark (HWM)**
in the native, rejecting any manifest whose epoch is below the highest the device has
already accepted. Also finish the prior session's half-built improvement of signing
`expires_at_millis` so a MITM cannot extend a manifest's expiry.

## Inherited state (must reconcile)

The signed-payload byte contract is currently consistent across three implementations and
**works at HEAD**:

- Go `content/manifest.go::signedManifestPayload` (committed form)
- Java `phantom.loader.manifest.SignedManifestPayload::encode`
- C++ `natives/src/auth/phantom_manifest.cpp::canonicalize`

All three sign `{build_id, channel, minimum_loader_version, module_key?, modules,
native_components}`. `revoked` + `expires_at_millis` are passed alongside and checked as
separate short-circuits (not signed).

A prior **uncommitted** Go change moved `revoked` + `expires_at_millis` *into* the Go signed
payload without updating Java/C++ → if deployed, every manifest fails Ed25519 verification
on the loader (BAD_SIGNATURE → loader bricks). SP2 supersedes that half-change with one
coherent tri-language contract.

## New signed-payload contract (identical bytes in Go, Java, C++)

Field order (Go `encoding/json` struct order; Java/C++ append in the same order):

```
build_id, channel, minimum_loader_version, module_key (omitempty),
modules, native_components, expires_at_millis, epoch
```

- `expires_at_millis` — `int64`, no omitempty (zero still emitted). Now signed → un-extendable.
- `epoch` — `int64`, no omitempty. Monotonic per channel. The rollback ordering key.
- `revoked` — **removed from the signed payload.** Its signed value is always `false` (the
  server never re-signs on revoke), so signing it is security theater. It stays an unsigned
  response field and a separate REVOKED short-circuit, exactly as at HEAD. (A real signed
  revocation list is a separate, out-of-scope feature.)

Example bytes (compact, Go-escaped):
`{"build_id":"stable-filesystem","channel":"stable","minimum_loader_version":"1","module_key":"…","modules":[…],"native_components":[],"expires_at_millis":1750000000000,"epoch":1750000000000}`

## Epoch assignment (server)

- **Admin-created manifests** (`POST /admin/manifests` → `db.CreateManifest`): `epoch =
  time.Now().UnixMilli()` at creation, stored in a new `content_manifests.epoch BIGINT`
  column (migration `009`). Monotonic by creation time.
- **Stable filesystem manifest** (`BuildStableManifest`, rebuilt per request, unversioned):
  `epoch = max(modtime_millis)` over the served module/native files. Deterministic for
  unchanged content (re-serving the same files yields the same epoch ≥ HWM → accepted) and
  monotonic as content is redeployed (newer mtimes → higher epoch). Both schemes are
  unix-millis so they are mutually comparable.
- Trade-off (documented): restoring older files (mtime decrease) lowers the stable epoch and
  a client with a higher HWM will reject it until content is re-touched. Operators must not
  roll content backwards. Acceptable for an anti-downgrade control.

## HWM enforcement (client)

- **Native (release path, tamper-resistant)** — `phantom_auth` stores a per-channel HWM in
  its sealed store (same AES-256-GCM-under-HWID-key mechanism as the device secret). On a
  manifest that passes signature + expiry + version: if `epoch < storedHWM[channel]` →
  return a new `ROLLBACK` result; else set `storedHWM[channel] = max(stored, epoch)` and
  return OK. Because the check is native, patching the Java bytecode cannot bypass it.
- **Java (dev / non-release-key fallback `verifyJava`)** — in-process per-channel HWM map,
  same rule, returns `Result.ROLLBACK`. Not persistent/hardened (dev path only).
- New result code `ROLLBACK` added to both `ManifestResult` (C++ enum) and
  `ManifestVerifier.Result` (Java enum) — **appended last** so existing ordinals (the
  Java↔native ordinal contract) are unchanged.

## Code changes

### Server (Go) — verifiable with `go test`
1. `content/manifest.go`: `signedManifestPayload` — drop `Revoked`, keep `ExpiresAtMillis`,
   add `Epoch int64 json:"epoch"`. `BuildStableManifest` computes `epoch` from max file
   mtime and sets `ExpiresAtMillis`/`Epoch` on both payload and returned `ContentManifest`.
2. `db/manifests.go`: `ContentManifest` keeps `ExpiresAtMillis`, drop the inline `Revoked`
   reorder (keep `Revoked` field, unsigned), add `Epoch int64`. `scanManifest` /
   `GetLatestManifest` / `GetManifestByID` / `CreateManifest` read+write `epoch`.
3. `admin/handler.go` (`handleCreateManifest`): set `epoch = now millis` before signing; the
   signed payload it constructs must match the new contract.
4. Migration `009_manifest_epoch.sql`: `ALTER TABLE content_manifests ADD COLUMN epoch
   BIGINT NOT NULL DEFAULT 0;` (existing rows → 0, below any real epoch, so they are treated
   as oldest — harmless; new manifests get real epochs).
5. `content/manifest_test.go`: update the signature + golden-bytes tests to the new contract
   (this also fixes the 2 currently-failing tests) + add an epoch-present assertion.

### Client Java — verifiable with `./gradlew test`
6. `ContentManifest.java`: add `long epoch`. Parser (`ManifestClient`) reads `"epoch"`.
7. `SignedManifestPayload.java::encode`: after `native_components`, append
   `,"expires_at_millis":<long>,"epoch":<long>`.
8. `ManifestVerifier`: add `ROLLBACK` to `Result`; thread `epoch` into the native call and
   the `verifyJava` path; implement the dev-path in-memory per-channel HWM check.
9. `LoaderCryptoParityTest` (or a new test): assert `SignedManifestPayload.encode` equals the
   exact Go golden bytes for a fixed manifest (cross-language byte lock), and that a
   lower-epoch manifest yields `ROLLBACK`.

### Native C++ — code now, rebuild + in-game later
10. `phantom_manifest.{hpp,cpp}`: `ManifestC` gains `expiresAtMillis`, `epoch`;
    `canonicalize` appends the two new fields in order; `verify_manifest` adds the HWM check
    + `ROLLBACK` result; per-channel HWM persisted via the existing sealed store.
11. `phantom_manifest_jni.cpp` + `PhantomAuthNative.java` native decl: add the `epoch`
    parameter to `nativeVerifyManifest`.
12. Rebuild `phantom_auth.dll` (vcvars + NMake, per the known native-build recipe) and run
    `PhantomAuthNativeSmokeTest` + the manifest parity test to confirm Java↔native byte
    agreement. **If the MSVC toolchain is unavailable in this environment, the native rebuild
    + in-game verification is the one remaining user-gated step** (the Go + Java halves are
    fully landed and unit-verified, and the byte contract is pinned by the parity golden so a
    native drift is caught the moment the rebuilt DLL runs the smoke test).

## Testing strategy

- **Byte lock:** one fixed manifest, hardcode the exact Go `json.Marshal` bytes as a golden;
  assert Go, Java (`encode`), and (post-rebuild) C++ (`canonicalize` via smoke test) all
  produce those bytes. A single source of truth prevents tri-language drift.
- **Rollback:** unit-test that epoch `< HWM` → `ROLLBACK`, epoch `== HWM` → OK (idempotent
  re-serve), epoch `> HWM` → OK and advances HWM.
- **Regression:** the 2 currently-failing `manifest_test.go` tests pass under the new contract.

## Risk / compatibility

- Wire: `epoch` is an additive JSON field; older clients ignore it (forward-compatible) but
  do NOT get rollback protection until updated — acceptable, it is a new control.
- Blast radius: a byte mismatch between the three canonicalizers = universal BAD_SIGNATURE.
  Mitigated by the golden byte-lock test gating Go + Java now and the native at rebuild time.
  All changes are local/uncommitted on feature branches; nothing deploys without the user's
  gated SP5 step, and the loader is verified in-game before any deploy.
```
