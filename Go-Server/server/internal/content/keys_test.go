package content

import (
	"bytes"
	"testing"

	ccrypto "github.com/phantom/server/internal/crypto"
)

// Two different licenseIDs must derive two different per-license content keys from
// the same server module key, so a bundle encrypted for license A cannot be
// decrypted with license B's key. This is the access-control property the global
// key never provided.
func TestDeriveLicenseKeyIsPerLicense(t *testing.T) {
	serverKey := make([]byte, 32)
	for i := range serverKey {
		serverKey[i] = byte(i)
	}

	keyA := DeriveLicenseKey(serverKey, "lic-A")
	keyB := DeriveLicenseKey(serverKey, "lic-B")

	if len(keyA) != 32 || len(keyB) != 32 {
		t.Fatalf("derived keys must be 32 bytes; got %d and %d", len(keyA), len(keyB))
	}
	if bytes.Equal(keyA, keyB) {
		t.Fatal("two different licenseIDs must derive different keys")
	}
	if bytes.Equal(keyA, serverKey) {
		t.Fatal("a derived per-license key must not equal the raw server key")
	}
}

// Derivation is deterministic: the same (serverKey, licenseID) always yields the
// same key, so a cached bundle re-decrypts on later requests without regeneration.
func TestDeriveLicenseKeyDeterministic(t *testing.T) {
	serverKey := bytes.Repeat([]byte{0xAB}, 32)
	first := DeriveLicenseKey(serverKey, "lic-XYZ")
	second := DeriveLicenseKey(serverKey, "lic-XYZ")
	if !bytes.Equal(first, second) {
		t.Fatal("DeriveLicenseKey must be deterministic for the same inputs")
	}
}

// An empty licenseID returns the server key unchanged. This preserves the shared
// (non-per-license) content path and the DB/admin manifest path, which encrypt and
// advertise under the raw server key.
func TestDeriveLicenseKeyEmptyLicenseIsServerKey(t *testing.T) {
	serverKey := bytes.Repeat([]byte{0x11}, 32)
	got := DeriveLicenseKey(serverKey, "")
	if !bytes.Equal(got, serverKey) {
		t.Fatal("empty licenseID must return the raw server key (shared path unchanged)")
	}
}

// The end-to-end access-control property: a bundle encrypted for license A does NOT
// decrypt with license B's derived key, but DOES decrypt with A's own key.
func TestPerLicenseKeyIsolatesBundles(t *testing.T) {
	serverKey := bytes.Repeat([]byte{0x42}, 32)
	keyA := DeriveLicenseKey(serverKey, "lic-A")
	keyB := DeriveLicenseKey(serverKey, "lic-B")

	plaintext := []byte("PK\x03\x04 secret module bundle for license A")
	enc, err := ccrypto.EncryptAESGCM(keyA, plaintext)
	if err != nil {
		t.Fatalf("encrypt for A: %v", err)
	}

	if _, err := ccrypto.DecryptAESGCM(keyB, enc); err == nil {
		t.Fatal("a bundle encrypted for license A must NOT decrypt with license B's key")
	}

	back, err := ccrypto.DecryptAESGCM(keyA, enc)
	if err != nil {
		t.Fatalf("a bundle encrypted for A must decrypt with A's own key: %v", err)
	}
	if !bytes.Equal(back, plaintext) {
		t.Fatal("round-trip plaintext mismatch")
	}
}
