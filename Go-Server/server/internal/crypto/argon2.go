package crypto

import (
	"crypto/rand"
	"crypto/subtle"
	"encoding/base64"
	"fmt"
	"strings"

	"golang.org/x/crypto/argon2"
)

const (
	argonMemory   = 64 * 1024
	argonIter     = 3
	argonParallel = 2
	argonKeyLen   = 32
	argonSaltLen  = 16
)

func HashPassword(password string) (string, error) {
	salt := make([]byte, argonSaltLen)
	if _, err := rand.Read(salt); err != nil {
		return "", err
	}
	hash := argon2.IDKey([]byte(password), salt, argonIter, argonMemory, argonParallel, argonKeyLen)
	encoded := fmt.Sprintf("$argon2id$v=19$m=%d,t=%d,p=%d$%s$%s",
		argonMemory, argonIter, argonParallel,
		base64.RawStdEncoding.EncodeToString(salt),
		base64.RawStdEncoding.EncodeToString(hash),
	)
	return encoded, nil
}

func VerifyPassword(password, encoded string) (bool, error) {
	// Format: $argon2id$v=19$m=<mem>,t=<iter>,p=<par>$<salt>$<hash>
	parts := strings.Split(encoded, "$")
	if len(parts) != 6 || parts[1] != "argon2id" {
		return false, fmt.Errorf("invalid hash format")
	}
	var mem, iter uint32
	var par uint8
	if _, err := fmt.Sscanf(parts[3], "m=%d,t=%d,p=%d", &mem, &iter, &par); err != nil {
		return false, fmt.Errorf("invalid hash params: %w", err)
	}
	salt, err := base64.RawStdEncoding.DecodeString(parts[4])
	if err != nil {
		return false, err
	}
	expectedHash, err := base64.RawStdEncoding.DecodeString(parts[5])
	if err != nil {
		return false, err
	}
	computed := argon2.IDKey([]byte(password), salt, iter, mem, par, uint32(len(expectedHash)))
	return subtle.ConstantTimeCompare(computed, expectedHash) == 1, nil
}

// dummyVerifyHash is a valid argon2id hash computed once at package init.
var dummyVerifyHash string

func init() {
	if h, err := HashPassword("phantom/dummy-verify/target"); err == nil {
		dummyVerifyHash = h
	}
}

// DummyVerifyPassword runs a full argon2id verification against a fixed internal hash, spending the
// same work as a real VerifyPassword call. Authentication paths call it on the account-not-found
// (and account-blocked) branch so the response time does not reveal whether a username/email
// exists — closing the user-enumeration timing oracle that an early return would otherwise create.
func DummyVerifyPassword() {
	if dummyVerifyHash == "" {
		return
	}
	_, _ = VerifyPassword("phantom/dummy-verify/input", dummyVerifyHash)
}
