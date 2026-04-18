package crypto

import (
	"crypto/ed25519"
	"encoding/base64"
	"encoding/json"
)

func SignManifest(privateKey []byte, manifest any) (string, error) {
	data, err := json.Marshal(manifest)
	if err != nil {
		return "", err
	}
	sig := ed25519.Sign(privateKey, data)
	return base64.StdEncoding.EncodeToString(sig), nil
}

func VerifyManifest(publicKey, data []byte, sigBase64 string) bool {
	sig, err := base64.StdEncoding.DecodeString(sigBase64)
	if err != nil {
		return false
	}
	return ed25519.Verify(publicKey, data, sig)
}
