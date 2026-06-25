package config

import (
	"encoding/base64"
	"testing"
)

// AUDIT_RETENTION_DAYS defaults to 90 when unset, honors an explicit positive value, and
// clamps a 0/negative value back to 90 (a 0-day retention would reap recent audit rows).
func TestAuditRetentionDays(t *testing.T) {
	k32 := base64.StdEncoding.EncodeToString(make([]byte, 32))
	base := func() {
		t.Setenv("MASTER_KEY", k32)
		t.Setenv("SERVER_PEPPER", k32)
		t.Setenv("MANIFEST_SIGNING_KEY", k32)
		t.Setenv("MODULE_ENCRYPTION_KEY", k32)
		t.Setenv("DB_URL", "postgres://x")
		t.Setenv("ADMIN_API_SECRET", "secret")
		t.Setenv("APP_ENV", "development")
		t.Setenv("BASE_URL", "http://localhost:8080")
	}

	t.Run("unset -> default 90", func(t *testing.T) {
		base()
		t.Setenv("AUDIT_RETENTION_DAYS", "")
		cfg, err := Load()
		if err != nil {
			t.Fatalf("load: %v", err)
		}
		if cfg.AuditRetentionDays != 90 {
			t.Errorf("default AuditRetentionDays = %d, want 90", cfg.AuditRetentionDays)
		}
	})

	t.Run("explicit positive honored", func(t *testing.T) {
		base()
		t.Setenv("AUDIT_RETENTION_DAYS", "30")
		cfg, err := Load()
		if err != nil {
			t.Fatalf("load: %v", err)
		}
		if cfg.AuditRetentionDays != 30 {
			t.Errorf("AuditRetentionDays = %d, want 30", cfg.AuditRetentionDays)
		}
	})

	t.Run("zero or negative clamps to 90", func(t *testing.T) {
		for _, v := range []string{"0", "-5"} {
			base()
			t.Setenv("AUDIT_RETENTION_DAYS", v)
			cfg, err := Load()
			if err != nil {
				t.Fatalf("load: %v", err)
			}
			if cfg.AuditRetentionDays != 90 {
				t.Errorf("AUDIT_RETENTION_DAYS=%s -> AuditRetentionDays=%d, want clamp to 90", v, cfg.AuditRetentionDays)
			}
		}
	})
}
