package config

import "testing"

// resolveAuthDebugLogs decides whether the verbose per-request
// [auth.verify_session]/[auth.finish] success-path diagnostics are emitted.
// Default: OFF in production (so they don't flood prod stdout or the
// network-reachable /admin/server-logs buffer), ON elsewhere. An explicit
// AUTH_DEBUG_LOGS override wins in either direction, so an operator can turn the
// detail on in prod to debug an incident without redeploying with a changed
// APP_ENV (which would alter other behavior).
func TestResolveAuthDebugLogs(t *testing.T) {
	cases := []struct {
		appEnv   string
		override string
		want     bool
	}{
		{"production", "", false},
		{"development", "", true},
		{"staging", "", true},
		{"test", "", true},
		{"", "", true}, // unknown/empty env is non-production -> on
		{"production", "true", true},
		{"production", "1", true},
		{"production", "false", false},
		{"production", "0", false},
		{"development", "false", false},
		{"development", "0", false},
	}
	for _, c := range cases {
		t.Run(c.appEnv+"/"+c.override, func(t *testing.T) {
			if got := resolveAuthDebugLogs(c.appEnv, c.override); got != c.want {
				t.Fatalf("resolveAuthDebugLogs(%q, %q) = %v, want %v", c.appEnv, c.override, got, c.want)
			}
		})
	}
}
