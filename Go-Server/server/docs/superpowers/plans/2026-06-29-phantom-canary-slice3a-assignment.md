# phantom-canary Slice 3a (backend: signed canary_assignment) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make the Phantom backend issue a **per-license, Ed25519-signed `canary_assignment`** (beaconId, buildKey, ingestUrl) attached to each stable content manifest, so the client can later (Slice 3b) replace its Slice-1 fixture credentials with real per-license ones — MITM-safe, with **zero native change**.

**Architecture:** The chosen delivery mechanism is **Option B** (a separate signed blob, NOT fields inside the signed manifest payload — that was confirmed infeasible without a coordinated C++ native rebuild). A new `internal/content/canary.go` derives `beaconId = hex(HMAC-SHA256(SERVER_PEPPER,"canary:"+licenseId))[:16]` and `buildKey = base64(HKDF-SHA256(SERVER_PEPPER, info="phantom/beacon-key/v1|"+licenseId))`, builds a `canaryAssignmentPayload` (with a `"phantom/canary-assignment/v1"` domain-separation tag), and signs it with the **existing `MANIFEST_SIGNING_KEY`** via the generic `crypto.SignManifest`. The assignment is attached to `db.ContentManifest` as an **unsigned outer field** (`canary_assignment,omitempty`) — exactly the `revoked`-field precedent — so it appears in the JSON response with zero handler change and is invisible to the native's hardcoded 8-field signature check.

**Tech Stack:** Go 1.22+, stdlib `crypto/hmac`, `crypto/sha256`, `golang.org/x/crypto/hkdf` (already an indirect dep via the module graph; confirm with `go mod tidy`), `encoding/json`, `crypto/ed25519` (via the existing `internal/crypto`).

## Grounding (verified against the real code)

- Backend repo: `C:/Users/aeare/Desktop/Phantom/Go-Server-v23/Go-Server/server`, Go module `github.com/phantom/server`, branch `go-server`.
- `signedManifestPayload` / `signedPayloadOf` / `SignManifest` (`internal/content/manifest.go:22-58`) — the 8-field signed subset; **do NOT touch it** (the native + Java + a golden-bytes test pin it byte-for-byte).
- `crypto.SignManifest(privateKey []byte, manifest any) (string, error)` (`internal/crypto/ed25519.go:9`) = `json.Marshal → ed25519.Sign → base64.StdEncoding`. Generic; signs any struct.
- `db.ContentManifest` (`internal/db/manifests.go:34-53`) — the wire struct; `Revoked` is the precedent for a field present in the response but **absent from the signed payload**. Stable-path manifests are built in memory (not persisted), so a new in-memory-only field never hits the DB INSERT/SELECT.
- `content.Service.GetStableManifest` (`internal/content/service.go:86-103`) resolves `ent` and returns `BuildStableManifest(...)`. `ent.LicenseID` is the authoritative per-license id. The service already has setters `SetGeneration`/`SetWatermarker` (the pattern to mirror).
- `cfg.ServerPepper []byte` (32-byte HMAC key, `config.go:21`) is loaded but **consumed by no method** — the available slot; the `"canary:"` prefix domain-separates it. `cfg.ManifestSigningKey []byte` (Ed25519, `config.go:22`). `cfg.BaseURL` (`config.go:29`). main.go wires `content.New(...)` at `cmd/server/main.go:108` with `SetGeneration` at `:121`.

## Global Constraints

- **Workspace isolation:** the backend branch `go-server` may be touched by other tooling — work in an **isolated git worktree** of this repo (use `superpowers:using-git-worktrees`) before implementing; commit there. If you commit on `go-server` directly, scope every `git add` to your exact files.
- **Do NOT modify the signed manifest contract** (`signedManifestPayload`, `signedPayloadOf`, `SignManifest`, the `signedManifestPayload` golden-bytes test). The canary assignment is a **separate** signed object with its **own** key-reuse-safe domain tag; it never touches the manifest's signed bytes.
- **Wire-format parity (frozen):** the assignment payload signed bytes are Go `json.Marshal` of the struct in **declaration order**: `{"domain":"phantom/canary-assignment/v1","license_id":"<lic>","beacon_id":"<16hex>","build_key":"<base64>","ingest_url":"<url>","epoch":<int>}`. Field order and JSON tags are load-bearing — the Slice-3b Java client reconstructs these exact bytes to verify. No `omitempty` on payload fields (a zero/empty must still emit so the client reproduces identically).
- **beaconId is 16 hex chars** (`[:16]` of the HMAC hex) to match the phantom-canary ingest server's `canarycore.DeriveBeaconID` and its `beacon-map.json` 16-hex keys. buildKey is the full base64 of 32 HKDF bytes.
- **Reuse `SERVER_PEPPER`** for both derivations (domain-separated by the `"canary:"` and `"phantom/beacon-key/v1|"` prefixes); **no new pepper env**. The ingest URL comes from a new `CANARY_INGEST_URL` env defaulting to `cfg.BaseURL`.
- Attachment is **stable-path only** (`GetStableManifest`, the per-license production path) for this slice; the by-ID/admin path is a follow-on.
- TDD: every task red → green → commit. `go test ./...` and `go vet ./...` stay green.

---

### Task 1: Per-license derivation (`BeaconID` + `buildKey`)

**Files:**
- Create: `internal/content/canary.go`
- Test: `internal/content/canary_test.go`

**Interfaces:**
- Consumes: `cfg.ServerPepper` (passed in as `[]byte`).
- Produces:
  - `func CanaryBeaconID(pepper []byte, licenseID string) string` — first 16 lowercase-hex chars of `HMAC-SHA256(pepper, "canary:"+licenseID)`.
  - `func canaryBuildKey(pepper []byte, licenseID string) (string, error)` — base64-std of 32 bytes from `HKDF-SHA256(pepper, nil, "phantom/beacon-key/v1|"+licenseID)`.

- [ ] **Step 1: Add the hkdf dep if needed**

```bash
go get golang.org/x/crypto/hkdf && go mod tidy
```

- [ ] **Step 2: Write the failing test**

`internal/content/canary_test.go`:

```go
package content

import "testing"

func TestCanaryBeaconID(t *testing.T) {
	pepper := []byte("test-pepper-0000000000000000000000")
	got := CanaryBeaconID(pepper, "LIC-0001")
	if len(got) != 16 {
		t.Fatalf("want 16 hex chars, got %d (%q)", len(got), got)
	}
	if got != CanaryBeaconID(pepper, "LIC-0001") {
		t.Fatal("not deterministic")
	}
	if got == CanaryBeaconID(pepper, "LIC-0002") {
		t.Fatal("different license must yield different beacon id")
	}
	if got == CanaryBeaconID([]byte("other-pepper"), "LIC-0001") {
		t.Fatal("different pepper must yield different beacon id")
	}
}

func TestCanaryBuildKey(t *testing.T) {
	pepper := []byte("test-pepper-0000000000000000000000")
	k1, err := canaryBuildKey(pepper, "LIC-0001")
	if err != nil {
		t.Fatal(err)
	}
	if k1 == "" {
		t.Fatal("empty build key")
	}
	if k1 != mustKey(t, pepper, "LIC-0001") {
		t.Fatal("not deterministic")
	}
	if k1 == mustKey(t, pepper, "LIC-0002") {
		t.Fatal("different license must yield different build key")
	}
}

func mustKey(t *testing.T, pepper []byte, lic string) string {
	t.Helper()
	k, err := canaryBuildKey(pepper, lic)
	if err != nil {
		t.Fatal(err)
	}
	return k
}
```

- [ ] **Step 3: Run test to verify it fails**

Run: `go test ./internal/content/ -run 'TestCanaryBeaconID|TestCanaryBuildKey' -v`
Expected: FAIL — `undefined: CanaryBeaconID`.

- [ ] **Step 4: Write the implementation**

`internal/content/canary.go`:

```go
package content

import (
	"crypto/hmac"
	"crypto/sha256"
	"encoding/base64"
	"encoding/hex"
	"io"

	"golang.org/x/crypto/hkdf"
)

// CanaryBeaconID derives the per-license canary beacon id: the first 16 lowercase-hex chars of
// HMAC-SHA256(SERVER_PEPPER, "canary:"+licenseID). This mirrors the phantom-canary ingest server's
// canarycore.DeriveBeaconID so a beacon's id resolves to its license there.
func CanaryBeaconID(pepper []byte, licenseID string) string {
	mac := hmac.New(sha256.New, pepper)
	mac.Write([]byte("canary:" + licenseID))
	return hex.EncodeToString(mac.Sum(nil))[:16]
}

// canaryBuildKey derives the per-license HMAC signing key the client uses to sign its beacons:
// base64-std of 32 bytes from HKDF-SHA256(SERVER_PEPPER, info="phantom/beacon-key/v1|"+licenseID).
// Distinct from the watermark/module keys by its info prefix.
func canaryBuildKey(pepper []byte, licenseID string) (string, error) {
	r := hkdf.New(sha256.New, pepper, nil, []byte("phantom/beacon-key/v1|"+licenseID))
	key := make([]byte, 32)
	if _, err := io.ReadFull(r, key); err != nil {
		return "", err
	}
	return base64.StdEncoding.EncodeToString(key), nil
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `go test ./internal/content/ -run 'TestCanaryBeaconID|TestCanaryBuildKey' -v`
Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add internal/content/canary.go internal/content/canary_test.go go.mod go.sum
git commit -m "feat(canary): per-license beaconId + buildKey derivation"
```

---

### Task 2: Signed `canary_assignment` payload + wire type

**Files:**
- Modify: `internal/content/canary.go` (payload + `BuildCanaryAssignment`)
- Modify: `internal/db/manifests.go` (the `CanaryAssignment` wire type)
- Test: `internal/content/canary_test.go` (golden bytes + sign/verify round-trip)

**Interfaces:**
- Consumes: `CanaryBeaconID`, `canaryBuildKey`, `crypto.SignManifest`, `crypto.VerifyManifest`.
- Produces:
  - `db.CanaryAssignment` — the wire struct (payload fields + `Signature`), with `canary_assignment,omitempty` on a new `ContentManifest` field.
  - `func BuildCanaryAssignment(signingKey, pepper []byte, licenseID, ingestURL string, epoch int64) (*db.CanaryAssignment, error)` — derives ids, marshals the canonical payload, signs it, returns the wire object.

- [ ] **Step 1: Add the wire type + field**

In `internal/db/manifests.go`, add the type and the field on `ContentManifest`:

```go
// CanaryAssignment is the per-license canary credential bundle. It is signed with the SAME Ed25519
// manifest key but over its OWN canonical bytes (a "phantom/canary-assignment/v1" domain tag prevents
// cross-replay), and is delivered as an UNSIGNED outer field on the manifest response — it is NOT part
// of signedManifestPayload, so the native's signature check ignores it and no native rebuild is needed.
type CanaryAssignment struct {
	Domain    string `json:"domain"`
	LicenseID string `json:"license_id"`
	BeaconID  string `json:"beacon_id"`
	BuildKey  string `json:"build_key"`
	IngestURL string `json:"ingest_url"`
	Epoch     int64  `json:"epoch"`
	Signature string `json:"signature"`
}
```

Add to the `ContentManifest` struct (after `CreatedAt`):

```go
	// CanaryAssignment is attached only on the stable per-license path (in memory, never persisted).
	// omitempty so DB-loaded / by-id manifests serialize unchanged.
	CanaryAssignment *CanaryAssignment `json:"canary_assignment,omitempty"`
```

- [ ] **Step 2: Write the failing test**

Add to `internal/content/canary_test.go`:

```go
import (
	"crypto/ed25519"
	"encoding/json"
	"strings"
	"testing"

	ccrypto "github.com/phantom/server/internal/crypto"
)

func TestBuildCanaryAssignment_GoldenFieldOrderAndVerifies(t *testing.T) {
	seed := make([]byte, ed25519.SeedSize) // deterministic test key
	priv := ed25519.NewKeyFromSeed(seed)
	pub := priv.Public().(ed25519.PublicKey)
	pepper := []byte("test-pepper-0000000000000000000000")

	a, err := BuildCanaryAssignment(priv, pepper, "LIC-0001", "http://localhost:8080", 5000)
	if err != nil {
		t.Fatal(err)
	}

	// Field-order / tag contract the Java client must reproduce byte-for-byte.
	want := `{"domain":"phantom/canary-assignment/v1","license_id":"LIC-0001","beacon_id":"` +
		a.BeaconID + `","build_key":"` + a.BuildKey + `","ingest_url":"http://localhost:8080","epoch":5000}`
	canonical := canaryCanonicalBytes(a)
	if string(canonical) != want {
		t.Fatalf("canonical bytes mismatch:\n got=%s\nwant=%s", canonical, want)
	}

	// The signature must verify over exactly those canonical bytes.
	if !ccrypto.VerifyManifest(pub, canonical, a.Signature) {
		t.Fatal("signature does not verify over the canonical bytes")
	}
	if a.Domain != "phantom/canary-assignment/v1" || a.LicenseID != "LIC-0001" {
		t.Fatalf("unexpected fields: %+v", a)
	}
	if !strings.HasPrefix(want, `{"domain":`) {
		t.Fatal("domain must be the first signed field")
	}
}
```

> Note: `canaryCanonicalBytes` is the helper used both for signing (Task 2 Step 3) and by this test; it marshals the payload (the assignment minus its `Signature`). Define it in `canary.go`.

- [ ] **Step 3: Run test to verify it fails**

Run: `go test ./internal/content/ -run TestBuildCanaryAssignment -v`
Expected: FAIL — `undefined: BuildCanaryAssignment` / `canaryCanonicalBytes`.

- [ ] **Step 4: Write the implementation**

Append to `internal/content/canary.go`:

```go
import (
	// add to the existing import block:
	"encoding/json"

	ccrypto "github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/db"
)

const canaryDomain = "phantom/canary-assignment/v1"

// canaryPayload is the exact field set + order that is Ed25519-signed (no Signature). Its json.Marshal
// bytes are the canonical bytes the client must reproduce. No omitempty: zeros must still emit.
type canaryPayload struct {
	Domain    string `json:"domain"`
	LicenseID string `json:"license_id"`
	BeaconID  string `json:"beacon_id"`
	BuildKey  string `json:"build_key"`
	IngestURL string `json:"ingest_url"`
	Epoch     int64  `json:"epoch"`
}

// canaryCanonicalBytes is the single source of truth for the signed bytes (sign + verify + test).
func canaryCanonicalBytes(a *db.CanaryAssignment) []byte {
	b, _ := json.Marshal(canaryPayload{
		Domain:    a.Domain,
		LicenseID: a.LicenseID,
		BeaconID:  a.BeaconID,
		BuildKey:  a.BuildKey,
		IngestURL: a.IngestURL,
		Epoch:     a.Epoch,
	})
	return b
}

// BuildCanaryAssignment derives the per-license canary credentials and signs them with the manifest
// key over the domain-tagged canonical payload. The returned object is attached, unsigned-outer, to
// the manifest response.
func BuildCanaryAssignment(signingKey, pepper []byte, licenseID, ingestURL string, epoch int64) (*db.CanaryAssignment, error) {
	buildKey, err := canaryBuildKey(pepper, licenseID)
	if err != nil {
		return nil, err
	}
	a := &db.CanaryAssignment{
		Domain:    canaryDomain,
		LicenseID: licenseID,
		BeaconID:  CanaryBeaconID(pepper, licenseID),
		BuildKey:  buildKey,
		IngestURL: ingestURL,
		Epoch:     epoch,
	}
	sig, err := ccrypto.SignManifest(signingKey, canaryPayload{
		Domain: a.Domain, LicenseID: a.LicenseID, BeaconID: a.BeaconID,
		BuildKey: a.BuildKey, IngestURL: a.IngestURL, Epoch: a.Epoch,
	})
	if err != nil {
		return nil, err
	}
	a.Signature = sig
	return a, nil
}
```

(Merge the new imports into `canary.go`'s existing import block.)

- [ ] **Step 5: Run test to verify it passes**

Run: `go test ./internal/content/ -run TestBuildCanaryAssignment -v && go vet ./internal/content/`
Expected: PASS; vet clean.

- [ ] **Step 6: Commit**

```bash
git add internal/content/canary.go internal/content/canary_test.go internal/db/manifests.go
git commit -m "feat(canary): signed canary_assignment payload + wire type"
```

---

### Task 3: Wire into the service + config + main

**Files:**
- Modify: `internal/content/service.go` (fields + `SetCanary` + attach in `GetStableManifest`)
- Modify: `internal/config/config.go` (`CanaryIngestURL`)
- Modify: `cmd/server/main.go` (call `SetCanary`)
- Test: `internal/content/canary_test.go` (attach helper test)

**Interfaces:**
- Consumes: `BuildCanaryAssignment`.
- Produces: `Service.SetCanary(pepper []byte, ingestURL string)`; a private `func (s *Service) attachCanary(m *db.ContentManifest, licenseID string)` that sets `m.CanaryAssignment` when a pepper is configured (no-op otherwise, fail-open — a canary build error must never block serving the manifest); `cfg.CanaryIngestURL`.

- [ ] **Step 1: Write the failing test**

Add to `internal/content/canary_test.go`:

```go
import "github.com/phantom/server/internal/db" // ensure imported

func TestAttachCanary_SetsValidAssignment(t *testing.T) {
	seed := make([]byte, ed25519.SeedSize)
	priv := ed25519.NewKeyFromSeed(seed)
	s := &Service{signingKey: priv}
	s.SetCanary([]byte("test-pepper-0000000000000000000000"), "http://ingest.example")

	m := &db.ContentManifest{ID: "stable", Epoch: 7}
	s.attachCanary(m, "LIC-0001")

	if m.CanaryAssignment == nil {
		t.Fatal("expected an attached canary assignment")
	}
	if m.CanaryAssignment.BeaconID != CanaryBeaconID([]byte("test-pepper-0000000000000000000000"), "LIC-0001") {
		t.Fatal("beacon id mismatch")
	}
	if m.CanaryAssignment.IngestURL != "http://ingest.example" || m.CanaryAssignment.Epoch != 7 {
		t.Fatalf("bad assignment: %+v", m.CanaryAssignment)
	}
}

func TestAttachCanary_NoPepperNoOp(t *testing.T) {
	s := &Service{} // no SetCanary
	m := &db.ContentManifest{ID: "stable"}
	s.attachCanary(m, "LIC-0001")
	if m.CanaryAssignment != nil {
		t.Fatal("must be a no-op when no canary pepper is configured")
	}
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `go test ./internal/content/ -run TestAttachCanary -v`
Expected: FAIL — `s.SetCanary` / `s.attachCanary` undefined.

- [ ] **Step 3: Add the service fields, setter, and attach helper**

In `internal/content/service.go`, add fields to `Service`:

```go
	beaconPepper    []byte // SERVER_PEPPER; empty = canary assignment off
	canaryIngestURL string
```

Add the setter (next to `SetGeneration`):

```go
// SetCanary enables per-license canary assignment on the stable manifest. pepper is SERVER_PEPPER;
// ingestURL is the canary ingest base the client posts beacons to. Empty pepper leaves it off.
func (s *Service) SetCanary(pepper []byte, ingestURL string) {
	s.beaconPepper = pepper
	s.canaryIngestURL = ingestURL
}

// attachCanary sets m.CanaryAssignment for the given license. No-op when no pepper is configured.
// Fail-open: a canary build error is logged and swallowed — it must never block serving the manifest.
func (s *Service) attachCanary(m *db.ContentManifest, licenseID string) {
	if len(s.beaconPepper) == 0 {
		return
	}
	a, err := BuildCanaryAssignment(s.signingKey, s.beaconPepper, licenseID, s.canaryIngestURL, m.Epoch)
	if err != nil {
		log.Printf("[content.canary] assignment build failed license=%s err=%v (serving manifest without it)", licenseID, err)
		return
	}
	m.CanaryAssignment = a
}
```

In `GetStableManifest`, attach before returning. Change:

```go
	return BuildStableManifest(ctx, s.contentDir, ent.LicenseID, s.baseURL, ent.ContentChannel, s.signingKey, s.moduleKey, ent.EnabledModules, moduleDeps)
```

to:

```go
	m, err := BuildStableManifest(ctx, s.contentDir, ent.LicenseID, s.baseURL, ent.ContentChannel, s.signingKey, s.moduleKey, ent.EnabledModules, moduleDeps)
	if err != nil {
		return nil, err
	}
	s.attachCanary(m, ent.LicenseID)
	return m, nil
```

(`log` is already imported in service.go.)

- [ ] **Step 4: Add the config field**

In `internal/config/config.go`, add `CanaryIngestURL string` to the `Config` struct (near `BaseURL`) and populate it in `Load()`:

```go
		CanaryIngestURL: getEnvOr("CANARY_INGEST_URL", getEnvOr("BASE_URL", "http://localhost:8080")),
```

(Place it consistently with how `BaseURL` is set so the default tracks the base URL.)

- [ ] **Step 5: Wire it in main**

In `cmd/server/main.go`, right after the `contentSvc.SetGeneration(...)` call (around line 121), add:

```go
	contentSvc.SetCanary(cfg.ServerPepper, cfg.CanaryIngestURL)
```

- [ ] **Step 6: Run tests + build + vet**

Run: `go build ./... && go vet ./... && go test ./internal/content/ ./internal/config/...`
Expected: build + vet clean; content + config tests PASS (incl. the two new attach tests). Then `go test ./...` to confirm nothing else broke (the manifest golden-bytes test must STILL pass — the signed payload is untouched).

- [ ] **Step 7: Commit**

```bash
git add internal/content/service.go internal/config/config.go cmd/server/main.go internal/content/canary_test.go
git commit -m "feat(canary): attach signed per-license canary_assignment to stable manifest"
```

---

## Follow-on (not in this plan)

- **Slice 3b — client consume:** parse `canary_assignment` in `ManifestClient.parse`; verify with `LoaderCrypto.ed25519Verify` + the pinned manifest key + a dedicated canonical encoder (domain tag first, exact field order from this plan's Global Constraints); feed `beaconId` / **base64-decoded** `buildKey` (via `LoaderCrypto.base64Std`, NOT `getBytes(UTF_8)`) / `ingestUrl` into `CanaryBeacon.production()` at the `loadAndVerifyManifest` seam, replacing the `CanaryConfig` fixtures; in-process epoch high-water-mark check. **Live end-to-end** against this backend + the phantom-canary ingest server.
- **Ingest beacon-map (needed for 3b's live test):** the phantom-canary ingest server must attribute the new per-license beaconIds — generate its `beacon-map.json` from the same `SERVER_PEPPER` + license list (beaconId/buildKey are fully derivable; reuse `CanaryBeaconID`/`canaryBuildKey`). A tiny `cmd/canary-beaconmap` generator or an admin endpoint.
- By-ID/admin manifest path (`scopeManifestForEntitlement`) attachment; `GET /admin/trace/beacon/:beaconid`; a dedicated canary signing key + rotation; persistent epoch-rollback storage.

## Self-Review notes

- **Spec coverage:** implements the SERVER half of Slice 3 under the human-chosen Mechanism B (separate signed blob) and server-delivered ingest URL. The signed manifest contract is untouched (no native change). Client consume + the ingest beacon-map are explicitly deferred to Slice 3b.
- **Boundary preservation:** `signedManifestPayload`/`signedPayloadOf`/`SignManifest` and the manifest golden-bytes test are not modified; the canary assignment is a separate signed object on an `omitempty` outer field (the `revoked` precedent). `attachCanary` is fail-open (never blocks manifest serving). SERVER_PEPPER reuse is domain-separated.
- **Wire parity:** the canonical signed bytes are pinned by a field-order golden test (`{"domain":...,"license_id":...,"beacon_id":...,"build_key":...,"ingest_url":...,"epoch":...}`) so Slice 3b's Java encoder can match byte-for-byte; `crypto.SignManifest`/`VerifyManifest` are reused (not reimplemented); beaconId is `[:16]` to match the ingest server.
- **Type consistency:** `CanaryBeaconID`, `canaryBuildKey`, `canaryPayload`, `canaryCanonicalBytes`, `BuildCanaryAssignment`, `db.CanaryAssignment`, `Service.SetCanary`/`attachCanary`, `cfg.CanaryIngestURL` are used identically across producing and consuming tasks.
- **Placeholder scan:** every code step shows complete code. No TODOs.
