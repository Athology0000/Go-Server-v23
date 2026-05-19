// server/internal/config/config.go
package config

import (
	"encoding/base64"
	"fmt"
	"net/url"
	"os"
	"strconv"
	"strings"

	"github.com/joho/godotenv"
)

type Config struct {
	MasterKey             []byte // 32 bytes, AES-256
	ServerPepper          []byte // 32 bytes, HMAC key
	ManifestSigningKey    []byte // Ed25519 private key (64 bytes)
	DBURL                 string
	RedisURL              string
	AdminAPISecret        string
	PublicPort            string
	AdminPort             string
	ContentDir            string
	BaseURL               string
	StrictSessionIP         bool
	AppEnv                  string
	AllowPublicRegistration bool
	ExposeAdminOnPublic    bool
	PublicCORSAllowOrigins string
	AdminCORSAllowOrigins  string
	BodyLimit              int
	SessionTTLHours        int
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

	publicOrigins, err := normalizeCORSOrigins("CORS_ALLOW_ORIGINS", getEnvOr("CORS_ALLOW_ORIGINS", "http://localhost:3001,http://localhost:3002,http://127.0.0.1:3001,http://127.0.0.1:3002,http://localhost:5173,http://localhost:5174"))
	if err != nil {
		return nil, err
	}
	adminOrigins, err := normalizeCORSOrigins("ADMIN_CORS_ALLOW_ORIGINS", getEnvOr("ADMIN_CORS_ALLOW_ORIGINS", publicOrigins))
	if err != nil {
		return nil, err
	}

	cfg := &Config{
		MasterKey:             masterKey,
		ServerPepper:          pepper,
		ManifestSigningKey:    signingKey,
		DBURL:                 requireEnv("DB_URL"),
		RedisURL:              requireEnv("REDIS_URL"),
		AdminAPISecret:        requireEnv("ADMIN_API_SECRET"),
		PublicPort:            getEnvOr("PUBLIC_PORT", "8080"),
		AdminPort:             getEnvOr("ADMIN_PORT", "8081"),
		ContentDir:            getEnvOr("CONTENT_DIR", "./content"),
		BaseURL:               getEnvOr("BASE_URL", "http://localhost:8080"),
		StrictSessionIP:         getEnvOr("STRICT_SESSION_IP", "true") == "true",
		AppEnv:                  strings.ToLower(getEnvOr("APP_ENV", "development")),
		AllowPublicRegistration: getEnvOr("ALLOW_PUBLIC_REGISTRATION", "") == "true",
		ExposeAdminOnPublic:    getEnvOr("EXPOSE_ADMIN_ON_PUBLIC", "false") == "true",
		PublicCORSAllowOrigins:  publicOrigins,
		AdminCORSAllowOrigins:   adminOrigins,
		SessionTTLHours:         getEnvIntOr("SESSION_TTL_HOURS", 1),
		BodyLimit:               getEnvIntOr("BODY_LIMIT_BYTES", 10*1024*1024),
	}

	if cfg.AppEnv != "production" && os.Getenv("ALLOW_PUBLIC_REGISTRATION") == "" {
		cfg.AllowPublicRegistration = true
	}

	if cfg.AppEnv == "production" && !strings.HasPrefix(cfg.BaseURL, "https://") {
		return nil, fmt.Errorf("BASE_URL must use https:// in production")
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

func getEnvIntOr(key string, def int) int {
	if v := os.Getenv(key); v != "" {
		if i, err := strconv.Atoi(v); err == nil {
			return i
		}
	}
	return def
}

func normalizeCORSOrigins(key, raw string) (string, error) {
	parts := strings.Split(raw, ",")
	origins := make([]string, 0, len(parts))
	seen := make(map[string]struct{}, len(parts))

	for _, part := range parts {
		origin := strings.TrimSpace(part)
		if origin == "" {
			continue
		}
		if origin == "*" {
			if _, ok := seen[origin]; !ok {
				origins = append(origins, origin)
				seen[origin] = struct{}{}
			}
			continue
		}

		u, err := url.Parse(origin)
		if err != nil || u.Scheme == "" || u.Host == "" {
			return "", fmt.Errorf("%s: invalid CORS origin %q", key, origin)
		}

		normalized := (&url.URL{Scheme: u.Scheme, Host: u.Host}).String()
		if _, ok := seen[normalized]; ok {
			continue
		}
		origins = append(origins, normalized)
		seen[normalized] = struct{}{}
	}

	if len(origins) == 0 {
		return "", fmt.Errorf("%s: no valid CORS origins configured", key)
	}

	return strings.Join(origins, ","), nil
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
