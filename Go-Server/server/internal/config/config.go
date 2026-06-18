// server/internal/config/config.go
package config

import (
	"crypto/ed25519"
	"crypto/rand"
	"encoding/base64"
	"fmt"
	"log"
	"net/url"
	"os"
	"strconv"
	"strings"
	"time"

	"github.com/joho/godotenv"
)

type Config struct {
	MasterKey               []byte // 32 bytes, AES-256
	ServerPepper            []byte // 32 bytes, HMAC key
	ManifestSigningKey      []byte // Ed25519 private key derived from MANIFEST_SIGNING_KEY seed material
	ModuleEncryptionKey     []byte // 32 bytes, AES-256; required in production, generated dev-only fallback otherwise
	DBURL                   string
	AdminAPISecret          string
	PublicPort              string
	AdminPort               string
	ContentDir              string
	BaseURL                 string
	StrictSessionIP         bool
	HwidTofuEnabled         bool
	AppEnv                  string
	AllowPublicRegistration bool
	PublicCORSAllowOrigins  string
	AdminCORSAllowOrigins   string
	BodyLimit               int
	SessionTTLHours         int
	HeartbeatLivenessWindow time.Duration
	MigrationsDir           string

	// Per-user watermarking of .jar downloads. Optional and gated: when WATERMARK_ENABLED is
	// true AND the obfuscator jar + config + secret are set, each .jar module download is
	// stamped with the requesting account id (HMAC-signed) so a leaked jar traces to a user.
	WatermarkEnabled    bool
	WatermarkJavaPath   string // java executable (default "java")
	WatermarkObfJar     string // path to the watermark-capable obfuscator jar
	WatermarkConfigPath string // path to the watermark-only obfuscator config json
	WatermarkSecret     string // HMAC secret used to sign/verify the embedded watermark

	// Server-side per-license generation (pure Go, no JVM): when both are set, the content service
	// watermarks each license's bundles from CONTENT_DIR/_jars and AES-encrypts them on first
	// request. These mirror the build-side MODULE_WM_SECRET/MODULE_WM_PEPPER, held server-side so the
	// server can stamp + trace per license without a build/redeploy per customer.
	ModuleWmSecret string
	ModuleWmPepper string
}

func Load() (*Config, error) {
	_ = godotenv.Load()

	appEnv := strings.ToLower(getEnvOr("APP_ENV", "development"))

	masterKey, err := decodeBase64Env("MASTER_KEY", 32)
	if err != nil {
		return nil, err
	}
	pepper, err := decodeBase64Env("SERVER_PEPPER", 32)
	if err != nil {
		return nil, err
	}
	signingKey, err := decodeManifestSigningKeyEnv()
	if err != nil {
		return nil, err
	}
	moduleKey, err := loadModuleEncryptionKey(appEnv)
	if err != nil {
		return nil, err
	}

	publicOrigins := sanitizeOrigins(getEnvOr("CORS_ALLOW_ORIGINS", "http://localhost:3001,http://localhost:3002,http://127.0.0.1:3001,http://127.0.0.1:3002,http://localhost:5173,http://localhost:5174"))
	adminOrigins := sanitizeOrigins(getEnvOr("ADMIN_CORS_ALLOW_ORIGINS", publicOrigins))

	cfg := &Config{
		MasterKey:           masterKey,
		ServerPepper:        pepper,
		ManifestSigningKey:  signingKey,
		ModuleEncryptionKey: moduleKey,
		DBURL:               requireEnv("DB_URL"),
		AdminAPISecret:      requireEnv("ADMIN_API_SECRET"),
		PublicPort:          getEnvOr("PUBLIC_PORT", "8080"),
		AdminPort:           getEnvOr("ADMIN_PORT", "8081"),
		ContentDir:          getEnvOr("CONTENT_DIR", "./content"),
		BaseURL:             getEnvOr("BASE_URL", "http://localhost:8080"),
		// Default to false: clients behind carrier-grade NAT or Railway/Cloudflare
		// edges often present rotating egress IPs across consecutive requests, so
		// strict IP-binding produces false 401s on otherwise-valid sessions. Set
		// STRICT_SESSION_IP=true explicitly to re-enable the check.
		StrictSessionIP: getEnvOr("STRICT_SESSION_IP", "false") == "true",
		// HWID trust-on-first-use at /auth/verify-session. OFF by default — HWID enforcement was
		// deliberately removed; this re-enables it (pin-on-first-sight, compare after) only when set.
		HwidTofuEnabled:         getEnvOr("HWID_TOFU_ENABLED", "false") == "true",
		AppEnv:                  appEnv,
		AllowPublicRegistration: getEnvOr("ALLOW_PUBLIC_REGISTRATION", "") == "true",
		PublicCORSAllowOrigins:  publicOrigins,
		AdminCORSAllowOrigins:   adminOrigins,
		// Absolute session cap (NOT rolling). Heartbeats no longer extend this;
		// liveness is governed by HeartbeatLivenessWindow. Default 12h so a normal
		// play session is never kicked mid-game.
		SessionTTLHours:         getEnvIntOr("SESSION_TTL_HOURS", 12),
		HeartbeatLivenessWindow: time.Duration(getEnvIntOr("HEARTBEAT_LIVENESS_WINDOW_SECONDS", 900)) * time.Second,
		BodyLimit:               getEnvIntOr("BODY_LIMIT_BYTES", 10*1024*1024),
		MigrationsDir:           getEnvOr("MIGRATIONS_DIR", "./migrations"),

		WatermarkEnabled:    getEnvOr("WATERMARK_ENABLED", "false") == "true",
		WatermarkJavaPath:   getEnvOr("WATERMARK_JAVA", "java"),
		WatermarkObfJar:     getEnvOr("WATERMARK_OBFUSCATOR_JAR", ""),
		WatermarkConfigPath: getEnvOr("WATERMARK_CONFIG", ""),
		WatermarkSecret:     getEnvOr("WATERMARK_SECRET", ""),
		ModuleWmSecret:      getEnvOr("MODULE_WM_SECRET", ""),
		ModuleWmPepper:      getEnvOr("MODULE_WM_PEPPER", ""),
	}

	if cfg.AppEnv != "production" && os.Getenv("ALLOW_PUBLIC_REGISTRATION") == "" {
		cfg.AllowPublicRegistration = true
	}

	// HWID trust-on-first-use defaults ON in production (device hardware pinning) and OFF
	// elsewhere so dev/test/staging enroll freely. An explicit HWID_TOFU_ENABLED= overrides in
	// any environment. Recovery from a hardware change is an admin device reset + re-enroll
	// (see docs/deploy/railway-runbook.md).
	if cfg.AppEnv == "production" && os.Getenv("HWID_TOFU_ENABLED") == "" {
		cfg.HwidTofuEnabled = true
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

// sanitizeOrigins normalizes a comma-separated CORS origin list to bare
// scheme://host[:port] entries. A stray path, query, fragment, or trailing
// slash on any origin makes gofiber's cors.New panic at boot, so they are
// stripped defensively here — a misconfigured env var should never crash
// the server.
func sanitizeOrigins(raw string) string {
	parts := strings.Split(raw, ",")
	out := make([]string, 0, len(parts))
	for _, part := range parts {
		origin := strings.TrimSpace(part)
		if origin == "" {
			continue
		}
		if origin == "*" {
			out = append(out, origin)
			continue
		}
		if u, err := url.Parse(origin); err == nil && u.Scheme != "" && u.Host != "" {
			out = append(out, u.Scheme+"://"+u.Host)
			continue
		}
		out = append(out, strings.TrimRight(origin, "/"))
	}
	return strings.Join(out, ",")
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

func decodeManifestSigningKeyEnv() ([]byte, error) {
	raw := requireEnv("MANIFEST_SIGNING_KEY")
	b, err := base64.StdEncoding.DecodeString(raw)
	if err != nil {
		return nil, fmt.Errorf("MANIFEST_SIGNING_KEY: invalid base64: %w", err)
	}
	if len(b) != ed25519.SeedSize && len(b) != ed25519.PrivateKeySize {
		return nil, fmt.Errorf("MANIFEST_SIGNING_KEY: expected %d-byte seed or %d-byte private key, got %d", ed25519.SeedSize, ed25519.PrivateKeySize, len(b))
	}

	// Older generated configs used 64 random bytes, which is not a valid
	// Ed25519 private key. Treat the first 32 bytes as seed material and
	// derive a valid seed+public-key private key every time.
	return ed25519.NewKeyFromSeed(b[:ed25519.SeedSize]), nil
}

// loadModuleEncryptionKey resolves the AES-256 key used to encrypt module
// bundles (*.enc) and embed module_key into signed manifests. It is REQUIRED in
// production: a missing or invalid MODULE_ENCRYPTION_KEY is a hard boot failure
// there, exactly like MASTER_KEY/SERVER_PEPPER. Outside production (dev/test/
// staging) a missing key falls back to a freshly generated random key so local
// workflows are not blocked — never a committed constant — and the fallback is
// announced loudly so it is never mistaken for a configured key.
func loadModuleEncryptionKey(appEnv string) ([]byte, error) {
	raw := os.Getenv("MODULE_ENCRYPTION_KEY")
	if raw == "" {
		if appEnv == "production" {
			return nil, fmt.Errorf("MODULE_ENCRYPTION_KEY is required in production: set a base64-encoded 32-byte key (e.g. `openssl rand -base64 32`)")
		}
		key := make([]byte, 32)
		if _, err := rand.Read(key); err != nil {
			return nil, fmt.Errorf("MODULE_ENCRYPTION_KEY: failed to generate dev fallback key: %w", err)
		}
		log.Printf("WARNING: MODULE_ENCRYPTION_KEY is not set; generated an ephemeral random module-encryption key for non-production (APP_ENV=%q). Module bundles encrypted now will NOT be decryptable after restart. Set MODULE_ENCRYPTION_KEY for stable behaviour.", appEnv)
		return key, nil
	}

	b, err := base64.StdEncoding.DecodeString(raw)
	if err != nil {
		return nil, fmt.Errorf("MODULE_ENCRYPTION_KEY: invalid base64: %w", err)
	}
	if len(b) != 32 {
		return nil, fmt.Errorf("MODULE_ENCRYPTION_KEY: expected 32 bytes, got %d", len(b))
	}
	return b, nil
}
