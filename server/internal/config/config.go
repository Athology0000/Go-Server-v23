// server/internal/config/config.go
package config

import (
	"encoding/base64"
	"fmt"
	"os"

	"github.com/joho/godotenv"
)

type Config struct {
	MasterKey          []byte // 32 bytes, AES-256
	ServerPepper       []byte // 32 bytes, HMAC key
	ManifestSigningKey []byte // Ed25519 private key (64 bytes)
	DBURL              string
	RedisURL           string
	AdminAPISecret     string
	PublicPort         string
	AdminPort          string
	ContentDir         string
	BaseURL            string
	StrictSessionIP    bool
}

func Load() (*Config, error) {
	_ = godotenv.Load()

	masterKey, err := decodeBase64Env("MASTER_KEY", 32)
	if err != nil {
		return nil, err
	}
	pepper, err := decodeBase64Env("SERVER_PEPPER", 32)
	if err != nil {
		return nil, err
	}
	signingKey, err := decodeBase64Env("MANIFEST_SIGNING_KEY", 64)
	if err != nil {
		return nil, err
	}

	cfg := &Config{
		MasterKey:          masterKey,
		ServerPepper:       pepper,
		ManifestSigningKey: signingKey,
		DBURL:              requireEnv("DB_URL"),
		RedisURL:           requireEnv("REDIS_URL"),
		AdminAPISecret:     requireEnv("ADMIN_API_SECRET"),
		PublicPort:         getEnvOr("PUBLIC_PORT", "8080"),
		AdminPort:          getEnvOr("ADMIN_PORT", "8081"),
		ContentDir:         getEnvOr("CONTENT_DIR", "./content"),
		BaseURL:            getEnvOr("BASE_URL", "http://localhost:8080"),
		StrictSessionIP:    getEnvOr("STRICT_SESSION_IP", "true") == "true",
	}
	return cfg, nil
}

func requireEnv(key string) string {
	v := os.Getenv(key)
	if v == "" {
		panic(fmt.Sprintf("required env var %s is not set", key))
	}
	return v
}

func getEnvOr(key, def string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return def
}

func decodeBase64Env(key string, expectedLen int) ([]byte, error) {
	raw := requireEnv(key)
	b, err := base64.StdEncoding.DecodeString(raw)
	if err != nil {
		return nil, fmt.Errorf("%s: invalid base64: %w", key, err)
	}
	if len(b) != expectedLen {
		return nil, fmt.Errorf("%s: expected %d bytes, got %d", key, expectedLen, len(b))
	}
	return b, nil
}
