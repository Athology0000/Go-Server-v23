package crypto

import (
	"bytes"
	"crypto/ed25519"
	"crypto/rand"
	"encoding/json"
	"strings"
	"testing"
)

func mustKey(t *testing.T, n int) []byte {
	t.Helper()
	k := make([]byte, n)
	if _, err := rand.Read(k); err != nil {
		t.Fatalf("rand: %v", err)
	}
	return k
}

// --- Ed25519 manifest signing ---

func TestSignVerifyManifestRoundTrip(t *testing.T) {
	pub, priv, err := ed25519.GenerateKey(rand.Reader)
	if err != nil {
		t.Fatalf("genkey: %v", err)
	}
	manifest := map[string]any{"build_id": "b1", "epoch": 100, "modules": []string{"core"}}
	sig, err := SignManifest(priv, manifest)
	if err != nil {
		t.Fatalf("sign: %v", err)
	}
	data, _ := json.Marshal(manifest)
	if !VerifyManifest(pub, data, sig) {
		t.Fatal("expected signature to verify over the exact marshaled bytes")
	}
}

func TestVerifyManifestRejectsTamper(t *testing.T) {
	pub, priv, _ := ed25519.GenerateKey(rand.Reader)
	manifest := map[string]any{"epoch": 100}
	sig, _ := SignManifest(priv, manifest)
	data, _ := json.Marshal(manifest)

	tampered := append([]byte(nil), data...)
	tampered[len(tampered)-2] ^= 0xFF // flip a byte inside the payload
	if VerifyManifest(pub, tampered, sig) {
		t.Fatal("tampered payload must not verify")
	}
}

func TestVerifyManifestRejectsWrongKey(t *testing.T) {
	_, priv, _ := ed25519.GenerateKey(rand.Reader)
	otherPub, _, _ := ed25519.GenerateKey(rand.Reader)
	manifest := map[string]any{"epoch": 100}
	sig, _ := SignManifest(priv, manifest)
	data, _ := json.Marshal(manifest)
	if VerifyManifest(otherPub, data, sig) {
		t.Fatal("signature must not verify under a different public key")
	}
}

func TestVerifyManifestRejectsMalformedSignature(t *testing.T) {
	pub, _, _ := ed25519.GenerateKey(rand.Reader)
	if VerifyManifest(pub, []byte("data"), "not!base64!!") {
		t.Fatal("malformed base64 signature must return false, not panic")
	}
}

// --- HMAC-SHA256 (auth challenge proof) ---

func TestHMACDeterministicAndVerify(t *testing.T) {
	key := mustKey(t, 32)
	data := []byte("challenge-payload")
	h1 := HMACHash(key, data)
	h2 := HMACHash(key, data)
	if h1 != h2 {
		t.Fatal("HMAC must be deterministic for the same key+data")
	}
	if !HMACVerify(key, data, h1) {
		t.Fatal("HMACVerify must accept the matching hex digest")
	}
}

func TestHMACVerifyRejects(t *testing.T) {
	key := mustKey(t, 32)
	data := []byte("payload")
	good := HMACHash(key, data)

	if HMACVerify(key, []byte("other"), good) {
		t.Fatal("digest of different data must not verify")
	}
	if HMACVerify(mustKey(t, 32), data, good) {
		t.Fatal("digest under a different key must not verify")
	}
	if HMACVerify(key, data, good[:len(good)-1]) {
		t.Fatal("length-mismatched digest must not verify")
	}
	if HMACVerify(key, data, "") {
		t.Fatal("empty digest must not verify")
	}
}

// --- AES-256-GCM (module bundle encryption) ---

func TestAESGCMRoundTrip(t *testing.T) {
	key := mustKey(t, 32)
	plaintext := []byte("the secret module bytes")
	ct, err := EncryptAESGCM(key, plaintext)
	if err != nil {
		t.Fatalf("encrypt: %v", err)
	}
	// Wire layout the loader + native depend on: 12-byte nonce || ciphertext || 16-byte tag.
	if want := 12 + len(plaintext) + 16; len(ct) != want {
		t.Fatalf("expected len nonce(12)+pt(%d)+tag(16)=%d, got %d", len(plaintext), want, len(ct))
	}
	got, err := DecryptAESGCM(key, ct)
	if err != nil {
		t.Fatalf("decrypt: %v", err)
	}
	if !bytes.Equal(got, plaintext) {
		t.Fatalf("round-trip mismatch: got %q want %q", got, plaintext)
	}
}

func TestAESGCMNonceIsRandom(t *testing.T) {
	key := mustKey(t, 32)
	pt := []byte("same plaintext")
	a, _ := EncryptAESGCM(key, pt)
	b, _ := EncryptAESGCM(key, pt)
	if bytes.Equal(a, b) {
		t.Fatal("two encryptions of the same plaintext must differ (random nonce)")
	}
}

func TestAESGCMWrongKeyFails(t *testing.T) {
	ct, _ := EncryptAESGCM(mustKey(t, 32), []byte("data"))
	if _, err := DecryptAESGCM(mustKey(t, 32), ct); err == nil {
		t.Fatal("decrypt under a different key must fail authentication")
	}
}

func TestAESGCMTamperFails(t *testing.T) {
	key := mustKey(t, 32)
	ct, _ := EncryptAESGCM(key, []byte("data"))
	ct[len(ct)-1] ^= 0xFF // corrupt the auth tag
	if _, err := DecryptAESGCM(key, ct); err == nil {
		t.Fatal("tampered ciphertext must fail authentication")
	}
}

func TestAESGCMShortCiphertext(t *testing.T) {
	key := mustKey(t, 32)
	if _, err := DecryptAESGCM(key, []byte("short")); err == nil {
		t.Fatal("ciphertext shorter than the nonce must error")
	}
}

func TestAESGCMInvalidKeySize(t *testing.T) {
	if _, err := EncryptAESGCM(mustKey(t, 31), []byte("data")); err == nil {
		t.Fatal("a non-16/24/32-byte key must error")
	}
}

// --- Argon2id password hashing ---

func TestPasswordHashVerify(t *testing.T) {
	const pw = "correct horse battery staple"
	hash, err := HashPassword(pw)
	if err != nil {
		t.Fatalf("hash: %v", err)
	}
	if !strings.HasPrefix(hash, "$argon2id$v=19$") {
		t.Fatalf("unexpected encoded format: %s", hash)
	}
	ok, err := VerifyPassword(pw, hash)
	if err != nil || !ok {
		t.Fatalf("expected verify ok, got ok=%v err=%v", ok, err)
	}
	ok, err = VerifyPassword("wrong password", hash)
	if err != nil {
		t.Fatalf("verify wrong: unexpected err %v", err)
	}
	if ok {
		t.Fatal("wrong password must not verify")
	}
}

func TestPasswordHashIsSalted(t *testing.T) {
	a, _ := HashPassword("same")
	b, _ := HashPassword("same")
	if a == b {
		t.Fatal("two hashes of the same password must differ (random salt)")
	}
}

func TestVerifyPasswordMalformed(t *testing.T) {
	cases := []string{
		"",
		"plain",
		"$argon2id$v=19$m=65536,t=3,p=2$onlyfiveparts",
		"$bcrypt$v=19$m=1,t=1,p=1$c2FsdA$aGFzaA",
	}
	for _, c := range cases {
		if ok, err := VerifyPassword("pw", c); ok || err == nil {
			t.Fatalf("malformed encoded %q must return (false, err), got ok=%v err=%v", c, ok, err)
		}
	}
}

// --- Bearer/session tokens ---

func TestTokenHashMatchesGenerate(t *testing.T) {
	raw, hash, err := GenerateToken()
	if err != nil {
		t.Fatalf("generate: %v", err)
	}
	got, err := HashToken(raw)
	if err != nil {
		t.Fatalf("hashtoken: %v", err)
	}
	if got != hash {
		t.Fatalf("HashToken(raw) %s != GenerateToken hash %s", got, hash)
	}
}

func TestHashTokenInvalidBase64(t *testing.T) {
	if _, err := HashToken("not valid base64 url!!!"); err == nil {
		t.Fatal("invalid base64url token must error")
	}
}

// --- License keys ---

func TestLicenseKeyGenerateAndHash(t *testing.T) {
	raw, hash, err := GenerateLicenseKey()
	if err != nil {
		t.Fatalf("generate: %v", err)
	}
	if !strings.HasPrefix(raw, "PHANTOM-") {
		t.Fatalf("license key must start with PHANTOM-, got %s", raw)
	}
	if HashLicenseKey(raw) != hash {
		t.Fatal("HashLicenseKey(raw) must equal the generated hash")
	}
}

func TestHashLicenseKeyNormalizes(t *testing.T) {
	raw, _, _ := GenerateLicenseKey()
	lowerSpaced := "  " + strings.ToLower(raw) + "  "
	if HashLicenseKey(lowerSpaced) != HashLicenseKey(raw) {
		t.Fatal("HashLicenseKey must be case- and whitespace-insensitive")
	}
}
