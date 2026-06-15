package config

import (
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
