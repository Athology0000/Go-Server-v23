package crypto

// This file holds the intent-named DEEP modules that sit in front of the raw
// crypto primitives (hmac.go, aes.go, tokens.go). Callers express WHAT they want
// — "is this proof valid?", "issue me a token", "seal this device secret" —
// instead of knowing the master key, the HMAC algorithm, or the token-hash
// derivation. The low-level funcs remain the private implementation behind these
// types, so key/algorithm locality lives here and nowhere else.
//
// These are mock-seam deepenings: each module is a one-purpose surface with two
// adapters in mind — the real crypto path below, and a test fake — so proof
// verification, token issuing, and device-secret sealing can each be exercised
// as a unit without leaking primitives to every caller.

// ---------------------------------------------------------------------------
// ProofValidator — owns HMAC challenge-proof verification.
// ---------------------------------------------------------------------------

// ProofValidator verifies a presented HMAC proof over an issued challenge. The
// per-proof key is the device's decrypted secret (the loader's native signs the
// challenge with it), so the secret is supplied per call rather than held; what
// the module owns is the MAC algorithm and the constant-time comparison, which
// no longer leak to the auth flow.
type ProofValidator struct{}

// NewProofValidator constructs the real HMAC-backed proof validator.
func NewProofValidator() *ProofValidator { return &ProofValidator{} }

// Valid reports whether proofHex is the correct HMAC-SHA256 of challenge under
// secret. Behaviour is identical to HMACVerify(secret, challenge, proofHex).
func (ProofValidator) Valid(secret, challenge []byte, proofHex string) bool {
	return HMACVerify(secret, challenge, proofHex)
}

// ---------------------------------------------------------------------------
// TokenIssuer — owns GenerateToken + HashToken together.
// ---------------------------------------------------------------------------

// TokenIssuer issues opaque session/panel tokens and derives the stored hash for
// a presented token. It pairs token generation and token hashing behind one
// surface so the token-hash algorithm has a single owner: a caller issues a
// token and stores its hash as one operation, then later asks the issuer to hash
// a presented token for lookup, never touching sha256/base64 itself.
type TokenIssuer struct{}

// NewTokenIssuer constructs the real token issuer.
func NewTokenIssuer() *TokenIssuer { return &TokenIssuer{} }

// Issue returns a fresh raw token and the hash to persist. Identical to
// GenerateToken().
func (TokenIssuer) Issue() (raw string, hash string, err error) {
	return GenerateToken()
}

// HashPresented derives the stored-hash form of a raw token presented by a
// client, for a token-hash lookup. Identical to HashToken(raw).
func (TokenIssuer) HashPresented(raw string) (string, error) {
	return HashToken(raw)
}

// ---------------------------------------------------------------------------
// DeviceSecret — owns AES-GCM seal/open of the device secret under the master key.
// ---------------------------------------------------------------------------

// DeviceSecret seals and opens a device's secret under the server master key.
// It is the one place that knows the master key for device-secret crypto:
// callers ask to seal a freshly generated secret or open a stored ciphertext and
// never see the key. Identical bytes to EncryptAESGCM/DecryptAESGCM(masterKey, …).
type DeviceSecret struct {
	masterKey []byte
}

// NewDeviceSecret binds the module to the server master key, constructed once
// where the key is loaded and threaded into the auth / enrollment modules.
func NewDeviceSecret(masterKey []byte) *DeviceSecret {
	return &DeviceSecret{masterKey: masterKey}
}

// Seal encrypts a plaintext device secret under the master key. Identical to
// EncryptAESGCM(masterKey, plain).
func (d *DeviceSecret) Seal(plain []byte) ([]byte, error) {
	return EncryptAESGCM(d.masterKey, plain)
}

// Open decrypts a stored device-secret ciphertext under the master key.
// Identical to DecryptAESGCM(masterKey, ciphertext).
func (d *DeviceSecret) Open(ciphertext []byte) ([]byte, error) {
	return DecryptAESGCM(d.masterKey, ciphertext)
}
