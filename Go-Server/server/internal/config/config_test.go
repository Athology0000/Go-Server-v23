package config

import (
	"bytes"
	"encoding/base64"
	"testing"
)

// HWID TOFU defaults ON in production, OFF elsewhere, and an explicit env value overrides.
func TestHwidTofuDefaultByEnv(t *testing.T) {
	k32 := base64.StdEncoding.EncodeToString(make([]byte, 32))
	base := func() {
		t.Setenv("MASTER_KEY", k32)
		t.Setenv("SERVER_PEPPER", k32)
		t.Setenv("MANIFEST_SIGNING_KEY", k32)
		t.Setenv("MODULE_ENCRYPTION_KEY", k32)
		t.Setenv("DB_URL", "postgres://x")
		t.Setenv("ADMIN_API_SECRET", "secret")
	}

	t.Run("production unset -> on", func(t *testing.T) {
		base()
		t.Setenv("APP_ENV", "production")
		t.Setenv("BASE_URL", "https://example.com")
		t.Setenv("HWID_TOFU_ENABLED", "")
		cfg, err := Load()
		if err != nil {
			t.Fatalf("load: %v", err)
		}
		if !cfg.HwidTofuEnabled {
			t.Error("expected HWID TOFU default-on in production")
		}
	})

	t.Run("production explicit false -> off", func(t *testing.T) {
		base()
		t.Setenv("APP_ENV", "production")
		t.Setenv("BASE_URL", "https://example.com")
		t.Setenv("HWID_TOFU_ENABLED", "false")
		cfg, err := Load()
		if err != nil {
			t.Fatalf("load: %v", err)
		}
		if cfg.HwidTofuEnabled {
			t.Error("explicit HWID_TOFU_ENABLED=false must override the production default")
		}
	})

	t.Run("development unset -> off", func(t *testing.T) {
		base()
		t.Setenv("APP_ENV", "development")
		t.Setenv("BASE_URL", "http://localhost:8080")
		t.Setenv("HWID_TOFU_ENABLED", "")
		cfg, err := Load()
		if err != nil {
			t.Fatalf("load: %v", err)
		}
		if cfg.HwidTofuEnabled {
			t.Error("expected HWID TOFU off outside production")
		}
	})
}

// MODULE_ENCRYPTION_KEY is required in production (boot must fail when it is
// missing or invalid) but may fall back to a generated key outside production.
func TestModuleEncryptionKeyRequiredInProduction(t *testing.T) {
	k32 := base64.StdEncoding.EncodeToString(make([]byte, 32))
	base := func() {
		t.Setenv("MASTER_KEY", k32)
		t.Setenv("SERVER_PEPPER", k32)
		t.Setenv("MANIFEST_SIGNING_KEY", k32)
		t.Setenv("DB_URL", "postgres://x")
		t.Setenv("ADMIN_API_SECRET", "secret")
	}

	t.Run("production missing -> error", func(t *testing.T) {
		base()
		t.Setenv("APP_ENV", "production")
		t.Setenv("BASE_URL", "https://example.com")
		t.Setenv("MODULE_ENCRYPTION_KEY", "")
		if _, err := Load(); err == nil {
			t.Fatal("expected Load to fail in production without MODULE_ENCRYPTION_KEY")
		}
	})

	t.Run("production invalid -> error", func(t *testing.T) {
		base()
		t.Setenv("APP_ENV", "production")
		t.Setenv("BASE_URL", "https://example.com")
		t.Setenv("MODULE_ENCRYPTION_KEY", "not-base64!!")
		if _, err := Load(); err == nil {
			t.Fatal("expected Load to fail in production with an invalid MODULE_ENCRYPTION_KEY")
		}
	})

	t.Run("production valid -> uses provided key", func(t *testing.T) {
		base()
		t.Setenv("APP_ENV", "production")
		t.Setenv("BASE_URL", "https://example.com")
		key := make([]byte, 32)
		key[0] = 0x42
		t.Setenv("MODULE_ENCRYPTION_KEY", base64.StdEncoding.EncodeToString(key))
		cfg, err := Load()
		if err != nil {
			t.Fatalf("load: %v", err)
		}
		if !bytes.Equal(cfg.ModuleEncryptionKey, key) {
			t.Error("expected the configured MODULE_ENCRYPTION_KEY to be used verbatim")
		}
	})

	t.Run("development missing -> generated fallback", func(t *testing.T) {
		base()
		t.Setenv("APP_ENV", "development")
		t.Setenv("BASE_URL", "http://localhost:8080")
		t.Setenv("MODULE_ENCRYPTION_KEY", "")
		cfg, err := Load()
		if err != nil {
			t.Fatalf("load: %v", err)
		}
		if len(cfg.ModuleEncryptionKey) != 32 {
			t.Fatalf("expected a 32-byte generated key in dev, got %d bytes", len(cfg.ModuleEncryptionKey))
		}
		if allZero(cfg.ModuleEncryptionKey) {
			t.Error("dev fallback key must be randomly generated, not all-zero/static")
		}
	})
}

// Public registration must be default-deny in every environment (fail closed): it only turns on
// with an explicit ALLOW_PUBLIC_REGISTRATION=true. Previously it auto-enabled outside production,
// so a prod deploy that forgot/misspelled APP_ENV silently opened registration.
func TestPublicRegistrationDefaultDeny(t *testing.T) {
	k32 := base64.StdEncoding.EncodeToString(make([]byte, 32))
	base := func() {
		t.Setenv("MASTER_KEY", k32)
		t.Setenv("SERVER_PEPPER", k32)
		t.Setenv("MANIFEST_SIGNING_KEY", k32)
		t.Setenv("MODULE_ENCRYPTION_KEY", k32)
		t.Setenv("DB_URL", "postgres://x")
		t.Setenv("ADMIN_API_SECRET", "secret")
		t.Setenv("ALLOW_PUBLIC_REGISTRATION", "")
	}

	cases := []struct{ name, appEnv, baseURL string }{
		{"development unset -> off", "development", "http://localhost:8080"},
		{"unrecognized env unset -> off", "prdo", "http://localhost:8080"},
		{"empty env unset -> off", "", "http://localhost:8080"},
	}
	for _, tc := range cases {
		t.Run(tc.name, func(t *testing.T) {
			base()
			t.Setenv("APP_ENV", tc.appEnv)
			t.Setenv("BASE_URL", tc.baseURL)
			cfg, err := Load()
			if err != nil {
				t.Fatalf("load: %v", err)
			}
			if cfg.AllowPublicRegistration {
				t.Error("public registration must be default-deny when ALLOW_PUBLIC_REGISTRATION is unset")
			}
		})
	}

	t.Run("explicit true -> on", func(t *testing.T) {
		base()
		t.Setenv("APP_ENV", "development")
		t.Setenv("BASE_URL", "http://localhost:8080")
		t.Setenv("ALLOW_PUBLIC_REGISTRATION", "true")
		cfg, err := Load()
		if err != nil {
			t.Fatalf("load: %v", err)
		}
		if !cfg.AllowPublicRegistration {
			t.Error("explicit ALLOW_PUBLIC_REGISTRATION=true must enable registration")
		}
	})
}

func allZero(b []byte) bool {
	for _, x := range b {
		if x != 0 {
			return false
		}
	}
	return true
}
