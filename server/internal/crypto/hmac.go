package crypto

import (
	"crypto/hmac"
	"crypto/sha256"
	"crypto/subtle"
	"encoding/hex"
)

func HMACHash(key, data []byte) string {
	mac := hmac.New(sha256.New, key)
	mac.Write(data)
	return hex.EncodeToString(mac.Sum(nil))
}

func HMACVerify(key, data []byte, providedHex string) bool {
	expected := HMACHash(key, data)
	provided := []byte(providedHex)
	expectedBytes := []byte(expected)
	if len(provided) != len(expectedBytes) {
		return false
	}
	return subtle.ConstantTimeCompare(provided, expectedBytes) == 1
}
