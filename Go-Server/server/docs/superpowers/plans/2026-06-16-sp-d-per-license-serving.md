---
date: 2026-06-16
description: "SP-D: Go-Server-v23 per-license content serving + per-license signed manifests + watermark trace, so per-client watermarked .enc bundles can be served and leaks traced. Retargets the per-client-watermark-serve contract onto this repo."
status: ready
tags:
  - plan
  - phantom
  - server
  - externalization
  - per-client
---

# SP-D: Per-License Content Serving (Go-Server-v23) — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: superpowers:executing-plans. Steps use `- [ ]` checkboxes.

**Goal:** Serve each license its own watermarked `.enc` bundles and a manifest signed over *that client's* bundle hashes, so a leaked bundle traces to one license — retargeting the per-client-watermark-serve contract (built once in the old repo) onto Go-Server-v23.

**Architecture:** Content lives at `CONTENT_DIR/modules/<licenseId>/<module>.enc` per license, with `CONTENT_DIR/modules/<module>.enc` as the shared fallback. The server resolves `accountID → licenseId` (via the existing license row), serves from the per-license subtree when it exists, and `BuildStableManifest` hashes *that* subtree. Module URLs stay `/content/module/<name>` — the per-license routing happens server-side from the session's account, so a manifest never leaks the licenseId. `SignManifest`/`signedManifestPayload` are reused verbatim, so the only per-license difference in the signed bytes is the SHA256 values; canonicalization cannot drift. A `GET /admin/trace/watermark/:wmid` endpoint reads the build's `watermark-map.json` ledger to map a recovered watermark id back to its license.

**Tech Stack:** Go 1.22, pgx, Fiber, the existing `internal/content` + `internal/entitlement` packages, Ed25519 (`internal/crypto`). Tests are filesystem-based table tests (no DB) plus the existing DB-gated integration suite.

Implements sub-project SP-D of [[2026-06-15-full-externalization-server-delivered-design]]; retargets the contract from [[2026-06-13-per-client-watermark-serve]] onto this repo; the signed-byte shape is shared with [[2026-06-15-manifest-rollback-protection-design]].

**Out of scope (follow-up SP-D+):** the runtime `POST /admin/content/publish` upload endpoint + a Railway persistent volume at `CONTENT_DIR` (so per-license content updates without a redeploy). Until then, per-license subtrees are baked into the image. Also deferred: a per-license manifest cache keyed `(licenseId, contentVersion)`.

---

## File Structure

- **Modify** `internal/entitlement/service.go` — add `LicenseID` to `Result`; set it in `Resolve` for every authorized path.
- **Modify** `internal/content/manifest.go` — `effectiveModulesDir(contentDir, licenseID)`; `BuildStableManifest` takes a `licenseID`; `moduleFilePath` takes the effective modules dir.
- **Modify** `internal/content/service.go` — `GetStableManifest` + `ModuleBytes` pass `ent.LicenseID` through the per-license dir.
- **Create** `internal/content/trace.go` — `LookupWatermark(contentDir, wmid)` reads `watermark-map.json`.
- **Modify** `internal/admin/handler.go` — register `GET /admin/trace/watermark/:wmid` (support role).
- **Create** `internal/content/perlicense_test.go` — filesystem table tests for routing + per-license manifest + trace.

---

## Task 1: Per-license module dir + manifest (filesystem, no DB)

**Files:** modify `internal/content/manifest.go`; create `internal/content/perlicense_test.go`

- [ ] **Step 1 — failing test** (`perlicense_test.go`): build a temp `CONTENT_DIR` with shared `phantom.enc`+`phantom-autowalk.enc` (encrypted with a test module key) and a `modules/lic-A/` subtree with different bytes; assert `BuildStableManifest(ctx, dir, "lic-A", …)` lists the per-license autowalk SHA256 and `BuildStableManifest(ctx, dir, "", …)` lists the shared one, and the two SHA256s differ; both signatures verify against the test public key.
- [ ] **Step 2** — run: `go -C server test ./internal/content/ -run PerLicense -v` → FAIL (signature mismatch / arg count).
- [ ] **Step 3 — implement** in `manifest.go`:
  - `func effectiveModulesDir(contentDir, licenseID string) string` — if `licenseID != ""` and `filepath.Join(contentDir,"modules",licenseID)` is a directory, return it; else `filepath.Join(contentDir,"modules")`.
  - Change `BuildStableManifest(_ context.Context, contentDir, licenseID, baseURL, channel string, signingKey, moduleKey []byte, enabledModules []string)`; inside, set `modulesDir := effectiveModulesDir(contentDir, licenseID)` and read it (replacing the hardcoded `filepath.Join(contentDir,"modules")`). Everything else unchanged.
  - Change `moduleFilePath(modulesDir, name string)` to join `modulesDir` directly (drop the internal `"modules"` join); callers pass the effective dir.
- [ ] **Step 4** — run the test → PASS.
- [ ] **Step 5 — commit** `git add internal/content/manifest.go internal/content/perlicense_test.go && git commit -m "feat(content): per-license module dir + manifest (SP-D)"`

## Task 2: Wire licenseId through entitlement + service

**Files:** modify `internal/entitlement/service.go`, `internal/content/service.go`

- [ ] **Step 1 — implement** `Result.LicenseID string`; in `Resolve`, after the license is loaded, set the field on each authorized return (`res := fullAccessResult(...); res.LicenseID = license.ID; return res, nil` and the normal path).
- [ ] **Step 2 — implement** in `service.go`: `GetStableManifest` passes `ent.LicenseID` to `BuildStableManifest`; `ModuleBytes` computes `dir := effectiveModulesDir(s.contentDir, ent.LicenseID)` then `moduleFilePath(dir, moduleName)`.
- [ ] **Step 3** — run `go -C server build ./...` and `go -C server test ./internal/content/ ./internal/entitlement/` → PASS (existing tests still green; `fullAccessResult` unit tests unchanged since LicenseID is set by the caller).
- [ ] **Step 4 — commit** `git add internal/entitlement/service.go internal/content/service.go && git commit -m "feat(content): route module downloads + manifest per license (SP-D)"`

## Task 3: Watermark trace endpoint

**Files:** create `internal/content/trace.go`; modify `internal/admin/handler.go`; extend `perlicense_test.go`

- [ ] **Step 1 — failing test**: write a temp `CONTENT_DIR/watermark-map.json` with lines `aabbccdd00112233=lic-A` (the buildModules ledger format, `wm-id=licenseId` per line); assert `LookupWatermark(dir, "aabbccdd00112233") == "lic-A"` and an unknown id returns `ErrNotFound`.
- [ ] **Step 2** — run → FAIL (undefined).
- [ ] **Step 3 — implement** `internal/content/trace.go`: `func LookupWatermark(contentDir, wmid string) (string, error)` — read `filepath.Join(contentDir,"watermark-map.json")`, split lines on `=`, return the licenseId whose wm-id matches (trim/space-safe); `ErrNotFound` if absent.
- [ ] **Step 4** — run → PASS.
- [ ] **Step 5 — wire** `GET /admin/trace/watermark/:wmid` in `handler.go` under the `support` middleware → `{wmid, license_id}` or 404; commit.

## Verification

- [ ] `go -C server test ./...` green.
- [ ] `go -C server vet ./...` clean.
- [ ] Manual: a temp content dir with a `lic-A` subtree yields a different (valid, verifying) manifest for `lic-A` vs the shared default.

## Then (release, separate step)

1. `./gradlew buildModules -Pphantom.licenses=<minted-license-ids> -Pphantom.moduleKey=<K> -Pphantom.wmSecret=<S> -Pphantom.wmPepper=<P>` → per-license `.enc` under `build/phantom-modules/<licenseId>/` + `watermark-map.json`.
2. Copy each `<licenseId>/` subtree (the 3 externalized bundles) into `server/content/modules/<licenseId>/`, and `watermark-map.json` into `server/content/`.
3. Commit content → push the Railway-connected branch → auto-deploy bakes it.
4. Create/serve the per-license signed manifest (the stable path now does this automatically once content is present); verify with the smoke that two licenses get different bundle hashes + both verify.
