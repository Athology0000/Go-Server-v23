package content

import (
	"os"
	"path/filepath"
	"testing"

	ccrypto "github.com/phantom/server/internal/crypto"
)

// EnsureLicenseBundles must encrypt each license's .enc under that license's
// DERIVED key, not the raw server key. A bundle generated for a license therefore
// decrypts with DeriveLicenseKey(serverKey, license) and NOT with the server key.
func TestEnsureLicenseBundlesUsesDerivedKey(t *testing.T) {
	serverKey := make([]byte, 32)
	for i := range serverKey {
		serverKey[i] = byte(i)
	}
	dir := t.TempDir()
	jarsDir := filepath.Join(dir, "_jars")
	if err := os.MkdirAll(jarsDir, 0o755); err != nil {
		t.Fatal(err)
	}
	jar := makeJar(t, map[string]string{"META-INF/MANIFEST.MF": "Manifest-Version: 1.0\r\n\r\n"})
	if err := os.WriteFile(filepath.Join(jarsDir, "phantom-autowalk.jar"), jar, 0o644); err != nil {
		t.Fatal(err)
	}

	lic := "lic-DERIVE"
	if err := EnsureLicenseBundles(dir, lic, serverKey, "secret", "pepper"); err != nil {
		t.Fatalf("EnsureLicenseBundles: %v", err)
	}

	enc, err := os.ReadFile(filepath.Join(dir, "modules", lic, "phantom-autowalk.enc"))
	if err != nil {
		t.Fatalf("read generated bundle: %v", err)
	}

	// Must NOT decrypt with the raw server key (the old, broken behavior).
	if _, err := ccrypto.DecryptAESGCM(serverKey, enc); err == nil {
		t.Fatal("bundle must NOT decrypt with the raw server key; it must use the derived key")
	}
	// Must decrypt with this license's derived key.
	if _, err := ccrypto.DecryptAESGCM(DeriveLicenseKey(serverKey, lic), enc); err != nil {
		t.Fatalf("bundle must decrypt with the derived per-license key: %v", err)
	}
}

// Stale .enc bundles encrypted under the OLD global key (no scheme marker present)
// must be regenerated under the derived key on the next EnsureLicenseBundles call,
// not served stale. Modeled by pre-seeding the license dir with a global-key bundle
// and no marker, exactly as an upgrade-in-place deployment would have on disk.
func TestEnsureLicenseBundlesRegeneratesStaleGlobalKeyBundles(t *testing.T) {
	serverKey := make([]byte, 32)
	for i := range serverKey {
		serverKey[i] = byte(i)
	}
	dir := t.TempDir()
	jarsDir := filepath.Join(dir, "_jars")
	if err := os.MkdirAll(jarsDir, 0o755); err != nil {
		t.Fatal(err)
	}
	jar := makeJar(t, map[string]string{"META-INF/MANIFEST.MF": "Manifest-Version: 1.0\r\n\r\n"})
	if err := os.WriteFile(filepath.Join(jarsDir, "phantom-autowalk.jar"), jar, 0o644); err != nil {
		t.Fatal(err)
	}

	lic := "lic-STALE"
	licDir := filepath.Join(dir, "modules", lic)
	if err := os.MkdirAll(licDir, 0o755); err != nil {
		t.Fatal(err)
	}
	encPath := filepath.Join(licDir, "phantom-autowalk.enc")
	// Pre-seed a bundle encrypted under the OLD global (raw server) key, no marker.
	writeEnc(t, encPath, serverKey, []byte("stale-global-key-bundle"))

	if err := EnsureLicenseBundles(dir, lic, serverKey, "secret", "pepper"); err != nil {
		t.Fatalf("EnsureLicenseBundles: %v", err)
	}

	enc, err := os.ReadFile(encPath)
	if err != nil {
		t.Fatalf("read regenerated bundle: %v", err)
	}
	// The stale global-key bundle must be gone: it must now decrypt with the
	// derived key and NOT with the raw server key.
	if _, err := ccrypto.DecryptAESGCM(serverKey, enc); err == nil {
		t.Fatal("stale global-key bundle was not regenerated; still decrypts with server key")
	}
	if _, err := ccrypto.DecryptAESGCM(DeriveLicenseKey(serverKey, lic), enc); err != nil {
		t.Fatalf("regenerated bundle must decrypt with the derived key: %v", err)
	}
}

// Once the scheme marker is present, EnsureLicenseBundles is idempotent: a second
// call regenerates nothing.
func TestEnsureLicenseBundlesIdempotentWithMarker(t *testing.T) {
	serverKey := make([]byte, 32)
	for i := range serverKey {
		serverKey[i] = byte(i)
	}
	dir := t.TempDir()
	jarsDir := filepath.Join(dir, "_jars")
	if err := os.MkdirAll(jarsDir, 0o755); err != nil {
		t.Fatal(err)
	}
	jar := makeJar(t, map[string]string{"META-INF/MANIFEST.MF": "Manifest-Version: 1.0\r\n\r\n"})
	if err := os.WriteFile(filepath.Join(jarsDir, "phantom-autowalk.jar"), jar, 0o644); err != nil {
		t.Fatal(err)
	}

	lic := "lic-IDEM"
	if err := EnsureLicenseBundles(dir, lic, serverKey, "secret", "pepper"); err != nil {
		t.Fatal(err)
	}
	encPath := filepath.Join(dir, "modules", lic, "phantom-autowalk.enc")
	info1, err := os.Stat(encPath)
	if err != nil {
		t.Fatal(err)
	}
	if err := EnsureLicenseBundles(dir, lic, serverKey, "secret", "pepper"); err != nil {
		t.Fatal(err)
	}
	info2, _ := os.Stat(encPath)
	if !info1.ModTime().Equal(info2.ModTime()) {
		t.Error("EnsureLicenseBundles must be idempotent once the scheme marker is written")
	}
}
