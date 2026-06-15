package content

import (
	"context"
	"crypto/ed25519"
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
	"os"
	"path/filepath"
	"testing"

	ccrypto "github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/db"
)

// fixedModuleKey returns a deterministic 32-byte AES key for tests.
func fixedModuleKey() []byte {
	key := make([]byte, 32)
	for i := range key {
		key[i] = byte(i)
	}
	return key
}

// fixedSigningKey returns a deterministic Ed25519 private key for tests.
func fixedSigningKey() ed25519.PrivateKey {
	seed := make([]byte, ed25519.SeedSize)
	for i := range seed {
		seed[i] = byte(i + 7)
	}
	return ed25519.NewKeyFromSeed(seed)
}

// The loader downloads the .enc bundle and verifies its sha256 over those exact
// (encrypted) bytes BEFORE decrypting in the native. So the manifest's sha256 for
// an encrypted bundle must be the hash of the stored ciphertext, not the plaintext.
func TestBuildStableManifest_HashesServedCiphertext(t *testing.T) {
	dir := t.TempDir()
	modulesDir := filepath.Join(dir, "modules")
	if err := os.MkdirAll(modulesDir, 0o755); err != nil {
		t.Fatal(err)
	}

	moduleKey := fixedModuleKey()
	plaintext := []byte("PK\x03\x04 fake jar bytes for the phantom-autowalk bundle")
	enc, err := ccrypto.EncryptAESGCM(moduleKey, plaintext)
	if err != nil {
		t.Fatalf("encrypt fixture: %v", err)
	}
	if err := os.WriteFile(filepath.Join(modulesDir, "phantom-autowalk.enc"), enc, 0o644); err != nil {
		t.Fatal(err)
	}
	// A core bundle must exist or the misdeploy guard rejects the manifest.
	if err := os.WriteFile(filepath.Join(modulesDir, "phantom.jar"), []byte("core"), 0o644); err != nil {
		t.Fatal(err)
	}

	m, err := BuildStableManifest(context.Background(), dir, "https://example.test", "stable", fixedSigningKey(), moduleKey, []string{"*"})
	if err != nil {
		t.Fatalf("BuildStableManifest: %v", err)
	}
	var mod db.ManifestModule
	found := false
	for _, candidate := range m.Modules {
		if candidate.Name == "phantom-autowalk" {
			mod = candidate
			found = true
		}
	}
	if !found {
		t.Fatalf("phantom-autowalk missing from manifest modules: %v", m.Modules)
	}

	cipherHash := sha256.Sum256(enc)
	plainHash := sha256.Sum256(plaintext)
	wantCipher := hex.EncodeToString(cipherHash[:])
	if mod.SHA256 != wantCipher {
		hint := ""
		if mod.SHA256 == hex.EncodeToString(plainHash[:]) {
			hint = " (it is the PLAINTEXT hash — the loader hashes the ciphertext it downloads)"
		}
		t.Errorf("module sha256 = %s; want ciphertext hash %s%s", mod.SHA256, wantCipher, hint)
	}

	if mod.Name != "phantom-autowalk" {
		t.Errorf("module name = %q; want %q", mod.Name, "phantom-autowalk")
	}
	if want := "https://example.test/content/module/phantom-autowalk"; mod.URL != want {
		t.Errorf("module url = %q; want %q", mod.URL, want)
	}
}

// A wrong module key must fail manifest construction loudly rather than emitting a
// manifest for a bundle no client can decrypt.
func TestBuildStableManifest_WrongModuleKeyFails(t *testing.T) {
	dir := t.TempDir()
	modulesDir := filepath.Join(dir, "modules")
	if err := os.MkdirAll(modulesDir, 0o755); err != nil {
		t.Fatal(err)
	}
	enc, err := ccrypto.EncryptAESGCM(fixedModuleKey(), []byte("payload"))
	if err != nil {
		t.Fatal(err)
	}
	if err := os.WriteFile(filepath.Join(modulesDir, "phantom-autowalk.enc"), enc, 0o644); err != nil {
		t.Fatal(err)
	}

	wrongKey := make([]byte, 32)
	for i := range wrongKey {
		wrongKey[i] = byte(255 - i)
	}
	if _, err := BuildStableManifest(context.Background(), dir, "https://example.test", "stable", fixedSigningKey(), wrongKey, []string{"*"}); err == nil {
		t.Fatal("expected BuildStableManifest to fail with a wrong module key, got nil error")
	}
}

// The Ed25519 signature must verify over exactly json.Marshal(signedManifestPayload).
func TestBuildStableManifest_SignatureVerifies(t *testing.T) {
	dir := t.TempDir()
	modulesDir := filepath.Join(dir, "modules")
	if err := os.MkdirAll(modulesDir, 0o755); err != nil {
		t.Fatal(err)
	}
	enc, err := ccrypto.EncryptAESGCM(fixedModuleKey(), []byte("payload"))
	if err != nil {
		t.Fatal(err)
	}
	if err := os.WriteFile(filepath.Join(modulesDir, "phantom-autowalk.enc"), enc, 0o644); err != nil {
		t.Fatal(err)
	}
	// A core bundle must exist or the misdeploy guard rejects the manifest.
	if err := os.WriteFile(filepath.Join(modulesDir, "phantom.jar"), []byte("core"), 0o644); err != nil {
		t.Fatal(err)
	}

	priv := fixedSigningKey()
	m, err := BuildStableManifest(context.Background(), dir, "https://example.test", "stable", priv, fixedModuleKey(), []string{"*"})
	if err != nil {
		t.Fatalf("BuildStableManifest: %v", err)
	}

	signed, err := json.Marshal(signedPayloadOf(m))
	if err != nil {
		t.Fatal(err)
	}
	pub := priv.Public().(ed25519.PublicKey)
	if !ccrypto.VerifyManifest(pub, signed, m.Signature) {
		t.Error("manifest signature does not verify over the signed payload bytes")
	}
}

// Golden-bytes guard: the exact byte sequence json.Marshal(signedManifestPayload)
// produces is what the client's SignedManifestPayload reconstructs to verify the
// signature. Pin it so any field-order/tag/escaping drift is caught here rather
// than as a silent BAD_SIGNATURE on the client.
func TestSignedManifestPayload_GoldenBytes(t *testing.T) {
	payload := signedManifestPayload{
		BuildID:          "stable-filesystem",
		Channel:          "stable",
		MinLoaderVersion: "1",
		ModuleKey:        "bW9kdWxlS2V5",
		Modules: []db.ManifestModule{{
			Name:      "phantom-autowalk",
			URL:       "https://example.test/content/module/phantom-autowalk",
			SHA256:    "abc123",
			Required:  false,
			InitOrder: 0,
		}},
		NativeComponents: []db.ManifestNative{},
		ExpiresAtMillis:  1750000000000,
		Epoch:            1750000000000,
	}
	got, err := json.Marshal(payload)
	if err != nil {
		t.Fatal(err)
	}
	want := `{"build_id":"stable-filesystem","channel":"stable","minimum_loader_version":"1","module_key":"bW9kdWxlS2V5","modules":[{"name":"phantom-autowalk","url":"https://example.test/content/module/phantom-autowalk","sha256":"abc123","required":false,"init_order":0}],"native_components":[],"expires_at_millis":1750000000000,"epoch":1750000000000}`
	if string(got) != want {
		t.Errorf("signed bytes drift:\n got=%s\nwant=%s", string(got), want)
	}

	// module_key is omitempty: dropped entirely when empty.
	payload.ModuleKey = ""
	got2, err := json.Marshal(payload)
	if err != nil {
		t.Fatal(err)
	}
	if string(got2) != `{"build_id":"stable-filesystem","channel":"stable","minimum_loader_version":"1","modules":[{"name":"phantom-autowalk","url":"https://example.test/content/module/phantom-autowalk","sha256":"abc123","required":false,"init_order":0}],"native_components":[],"expires_at_millis":1750000000000,"epoch":1750000000000}` {
		t.Errorf("omitempty module_key not dropped: %s", string(got2))
	}
}
