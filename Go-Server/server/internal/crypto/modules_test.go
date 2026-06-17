package crypto

import "testing"

// These exercise the intent-named crypto modules directly as units — the seams the
// auth/enrollment flows now depend on. They are the crypto-identity proof for the
// relocation: a module must compute the exact same MAC / ciphertext / token-hash as
// the raw primitive it wraps.

// ProofValidator accepts a correct HMAC proof over the challenge and rejects a
// tampered one, identically to the raw HMACVerify it owns.
func TestProofValidatorAcceptsAndRejects(t *testing.T) {
	pv := NewProofValidator()
	secret := mustKey(t, 32)
	challenge := []byte("challenge-bytes")

	good := HMACHash(secret, challenge)
	if !pv.Valid(secret, challenge, good) {
		t.Fatal("ProofValidator must accept the genuine HMAC proof")
	}

	// Tampered proof.
	if pv.Valid(secret, challenge, good[:len(good)-1]+"0") {
		t.Fatal("ProofValidator must reject a tampered proof")
	}
	// Wrong secret.
	if pv.Valid(mustKey(t, 32), challenge, good) {
		t.Fatal("ProofValidator must reject a proof made with a different secret")
	}
	// Wrong challenge.
	if pv.Valid(secret, []byte("other-challenge"), good) {
		t.Fatal("ProofValidator must reject a proof over a different challenge")
	}
}

// TokenIssuer: an issued token's stored hash matches HashPresented(raw), and a wrong
// token hashes to something else. Crypto-identity vs GenerateToken/HashToken.
func TestTokenIssuerIssueAndVerify(t *testing.T) {
	ti := NewTokenIssuer()

	raw, hash, err := ti.Issue()
	if err != nil {
		t.Fatalf("Issue: %v", err)
	}
	if raw == "" || hash == "" {
		t.Fatal("Issue must return a non-empty raw token and hash")
	}

	got, err := ti.HashPresented(raw)
	if err != nil {
		t.Fatalf("HashPresented: %v", err)
	}
	if got != hash {
		t.Fatalf("HashPresented(raw)=%s must equal Issue hash=%s", got, hash)
	}

	// A different (validly-encoded) token must not collide with the stored hash.
	other, _, err := ti.Issue()
	if err != nil {
		t.Fatalf("Issue other: %v", err)
	}
	wrong, err := ti.HashPresented(other)
	if err != nil {
		t.Fatalf("HashPresented other: %v", err)
	}
	if wrong == hash {
		t.Fatal("a different token must not hash to the issued token's stored hash")
	}
}

// DeviceSecret seals then opens a secret round-trip under the master key, and the
// sealed bytes match EncryptAESGCM/DecryptAESGCM under the same key (crypto-identity).
func TestDeviceSecretSealOpenRoundTrip(t *testing.T) {
	masterKey := mustKey(t, 32)
	ds := NewDeviceSecret(masterKey)
	plain := []byte("device-secret-32-bytes-or-so....")

	sealed, err := ds.Seal(plain)
	if err != nil {
		t.Fatalf("Seal: %v", err)
	}

	opened, err := ds.Open(sealed)
	if err != nil {
		t.Fatalf("Open: %v", err)
	}
	if string(opened) != string(plain) {
		t.Fatalf("Open(Seal(x)) = %q, want %q", opened, plain)
	}

	// Identity with the raw primitive: a ciphertext sealed by the module opens with
	// the raw DecryptAESGCM under the same master key, and vice-versa.
	viaRaw, err := DecryptAESGCM(masterKey, sealed)
	if err != nil {
		t.Fatalf("raw DecryptAESGCM of module ciphertext: %v", err)
	}
	if string(viaRaw) != string(plain) {
		t.Fatalf("raw decrypt = %q, want %q", viaRaw, plain)
	}

	rawSealed, err := EncryptAESGCM(masterKey, plain)
	if err != nil {
		t.Fatalf("raw EncryptAESGCM: %v", err)
	}
	openedRaw, err := ds.Open(rawSealed)
	if err != nil {
		t.Fatalf("module Open of raw ciphertext: %v", err)
	}
	if string(openedRaw) != string(plain) {
		t.Fatalf("module Open of raw ciphertext = %q, want %q", openedRaw, plain)
	}

	// A wrong master key must fail to open.
	if _, err := NewDeviceSecret(mustKey(t, 32)).Open(sealed); err == nil {
		t.Fatal("Open under a different master key must fail")
	}
}
