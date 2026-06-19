package content

import (
	"crypto/sha256"
	"io"

	"golang.org/x/crypto/hkdf"
)

// keyDerivationScheme versions the per-license key derivation. It is written into
// each per-license content subtree (see keySchemeMarker) so that a change to how
// keys are derived — or the very first migration from the old single global key —
// forces stale .enc bundles to be regenerated rather than served undecryptable.
//
// v1 = HKDF-SHA256(serverKey, info="phantom/module-key/v1|"+licenseID) -> 32 bytes.
const keyDerivationScheme = "module-key-hkdf-v1"

// keyDerivationInfoPrefix domain-separates this HKDF use from any other key
// derivation that might one day key off the same server module key.
const keyDerivationInfoPrefix = "phantom/module-key/v1|"

// DeriveLicenseKey returns the 32-byte AES key under which licenseID's content
// bundles are encrypted and which is advertised in that license's signed manifest.
//
// It is HKDF-SHA256 keyed by the server module key with the licenseID bound into
// the HKDF info, so each license gets a distinct, deterministic key: a bundle
// sealed for license A cannot be opened with license B's key. That is the access
// control the old single global module_key never provided (every client shipped
// the same key, so any payer could decrypt any other license's bundle).
//
// An empty licenseID returns the server key unchanged. That preserves the two
// non-per-license paths that legitimately use the raw server key: the shared
// CONTENT_DIR/modules build (no <licenseId> subtree) and the DB/admin manifest
// path. Those paths are out of scope here; this change must not regress them.
func DeriveLicenseKey(serverKey []byte, licenseID string) []byte {
	if licenseID == "" {
		return serverKey
	}
	info := []byte(keyDerivationInfoPrefix + licenseID)
	// No salt: the server module key is already a high-entropy secret, and a
	// fixed (nil) salt keeps derivation deterministic so a cached bundle keeps
	// decrypting across server restarts. Per-license separation comes from info.
	r := hkdf.New(sha256.New, serverKey, nil, info)
	out := make([]byte, 32)
	if _, err := io.ReadFull(r, out); err != nil {
		// HKDF over SHA-256 cannot fail to produce 32 bytes; panic would mean a
		// broken stdlib. Return zeros would be a silent crypto downgrade, so we
		// surface it as a panic that the request handler's recovery turns into a
		// 500 rather than shipping a predictable key.
		panic("content: HKDF derivation failed: " + err.Error())
	}
	return out
}
