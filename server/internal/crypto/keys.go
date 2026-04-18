package crypto

import (
	"crypto/rand"
	"crypto/sha256"
	"encoding/base32"
	"encoding/hex"
	"strings"
)

var base32Enc = base32.NewEncoding("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567").WithPadding(base32.NoPadding)

func GenerateLicenseKey() (raw string, hash string, err error) {
	b := make([]byte, 20)
	if _, err = rand.Read(b); err != nil {
		return
	}
	encoded := base32Enc.EncodeToString(b)
	parts := []string{"COBALT"}
	for i := 0; i < len(encoded); i += 4 {
		end := i + 4
		if end > len(encoded) {
			end = len(encoded)
		}
		parts = append(parts, encoded[i:end])
	}
	raw = strings.Join(parts, "-")
	h := sha256.Sum256([]byte(raw))
	hash = hex.EncodeToString(h[:])
	return
}

func HashLicenseKey(raw string) string {
	normalized := strings.ToUpper(strings.TrimSpace(raw))
	h := sha256.Sum256([]byte(normalized))
	return hex.EncodeToString(h[:])
}
