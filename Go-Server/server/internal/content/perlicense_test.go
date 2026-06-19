package content

import (
	"context"
	"crypto/ed25519"
	"crypto/rand"
	"encoding/base64"
	"encoding/json"
	"os"
	"path/filepath"
	"strings"
	"testing"

	ccrypto "github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/db"
)

func writeEnc(t *testing.T, path string, key, plaintext []byte) {
	t.Helper()
	enc, err := ccrypto.EncryptAESGCM(key, plaintext)
	if err != nil {
		t.Fatalf("encrypt %s: %v", path, err)
	}
	if err := os.WriteFile(path, enc, 0o644); err != nil {
		t.Fatalf("write %s: %v", path, err)
	}
}

func moduleSHA(t *testing.T, m *db.ContentManifest, name string) string {
	t.Helper()
	for _, mod := range m.Modules {
		if mod.Name == name {
			return mod.SHA256
		}
	}
	t.Fatalf("module %s not in manifest", name)
	return ""
}

// A license with its own subtree gets a manifest hashed over that subtree; one without falls back to
// the shared dir. URLs never leak the licenseId, and both manifests verify against the signing key.
func TestBuildStableManifestPerLicense(t *testing.T) {
	key := make([]byte, 32)
	if _, err := rand.Read(key); err != nil {
		t.Fatal(err)
	}
	pub, priv, err := ed25519.GenerateKey(rand.Reader)
	if err != nil {
		t.Fatal(err)
	}

	dir := t.TempDir()
	modules := filepath.Join(dir, "modules")
	if err := os.MkdirAll(modules, 0o755); err != nil {
		t.Fatal(err)
	}
	writeEnc(t, filepath.Join(modules, "phantom.enc"), key, []byte("shared-core"))
	writeEnc(t, filepath.Join(modules, "phantom-autowalk.enc"), key, []byte("shared-autowalk"))

	// The per-license subtree's bundles are sealed under license A's DERIVED key —
	// that is what BuildStableManifest now advertises and decrypt-checks for lic-A.
	licDir := filepath.Join(modules, "lic-A")
	if err := os.MkdirAll(licDir, 0o755); err != nil {
		t.Fatal(err)
	}
	keyA := DeriveLicenseKey(key, "lic-A")
	writeEnc(t, filepath.Join(licDir, "phantom.enc"), keyA, []byte("A-core"))
	writeEnc(t, filepath.Join(licDir, "phantom-autowalk.enc"), keyA, []byte("A-autowalk-distinct"))

	ctx := context.Background()
	mShared, err := BuildStableManifest(ctx, dir, "", "https://x", "stable", priv, key, []string{"*"}, nil)
	if err != nil {
		t.Fatalf("shared manifest: %v", err)
	}
	mA, err := BuildStableManifest(ctx, dir, "lic-A", "https://x", "stable", priv, key, []string{"*"}, nil)
	if err != nil {
		t.Fatalf("lic-A manifest: %v", err)
	}

	if moduleSHA(t, mShared, "phantom-autowalk") == moduleSHA(t, mA, "phantom-autowalk") {
		t.Fatal("per-license autowalk hash must differ from the shared one")
	}
	// The per-license manifest must advertise license A's DERIVED key, not the raw
	// server key the shared manifest carries — that key isolation is the fix.
	wantSharedKey := base64.StdEncoding.EncodeToString(key)
	wantAKey := base64.StdEncoding.EncodeToString(DeriveLicenseKey(key, "lic-A"))
	if mShared.ModuleKey != wantSharedKey {
		t.Fatalf("shared manifest module_key = %q; want raw server key %q", mShared.ModuleKey, wantSharedKey)
	}
	if mA.ModuleKey != wantAKey {
		t.Fatalf("lic-A manifest module_key = %q; want derived key %q", mA.ModuleKey, wantAKey)
	}
	if mA.ModuleKey == mShared.ModuleKey {
		t.Fatal("per-license manifest must not advertise the global key")
	}
	for _, mod := range mA.Modules {
		if strings.Contains(mod.URL, "lic-A") {
			t.Fatalf("module URL must not leak the licenseId: %s", mod.URL)
		}
	}
	for _, m := range []*db.ContentManifest{mShared, mA} {
		payload, _ := json.Marshal(signedPayloadOf(m))
		if !ccrypto.VerifyManifest(pub, payload, m.Signature) {
			t.Fatalf("manifest signature must verify (build_id=%s)", m.BuildID)
		}
	}
}

func TestLookupWatermark(t *testing.T) {
	dir := t.TempDir()
	ledger := "# wm-id=licenseId\naabbccdd00112233=lic-A\n11223344aabbccdd=lic-B\n"
	if err := os.WriteFile(filepath.Join(dir, "watermark-map.json"), []byte(ledger), 0o644); err != nil {
		t.Fatal(err)
	}
	if lic, err := LookupWatermark(dir, "aabbccdd00112233"); err != nil || lic != "lic-A" {
		t.Fatalf("LookupWatermark = %q, %v; want lic-A", lic, err)
	}
	if lic, err := LookupWatermark(dir, " 11223344aabbccdd "); err != nil || lic != "lic-B" {
		t.Fatalf("trimmed lookup = %q, %v; want lic-B", lic, err)
	}
	if _, err := LookupWatermark(dir, "deadbeefdeadbeef"); err != ErrNotFound {
		t.Fatalf("unknown wmid must be ErrNotFound, got %v", err)
	}
}

func TestEffectiveModulesDirFallsBackWhenNoSubtree(t *testing.T) {
	dir := t.TempDir()
	modules := filepath.Join(dir, "modules")
	if err := os.MkdirAll(filepath.Join(modules, "lic-A"), 0o755); err != nil {
		t.Fatal(err)
	}
	if got := effectiveModulesDir(dir, "lic-A"); got != filepath.Join(modules, "lic-A") {
		t.Errorf("present subtree: got %s", got)
	}
	if got := effectiveModulesDir(dir, "lic-MISSING"); got != modules {
		t.Errorf("absent subtree must fall back to shared: got %s", got)
	}
	if got := effectiveModulesDir(dir, ""); got != modules {
		t.Errorf("empty licenseId must use shared: got %s", got)
	}
}
