package config

import "strings"

// resolveAuthDebugLogs reports whether the verbose per-request auth success-path
// diagnostics ([auth.verify_session]/[auth.finish]) should be emitted. They are
// off in production by default (so they neither flood prod stdout nor the
// network-reachable /admin/server-logs buffer) and on elsewhere. An explicit
// AUTH_DEBUG_LOGS value — "true"/"1" or "false"/"0" — overrides in either
// direction, so the detail can be turned on in prod to debug an incident without
// redeploying with a changed APP_ENV.
func resolveAuthDebugLogs(appEnv, override string) bool {
	switch strings.ToLower(strings.TrimSpace(override)) {
	case "true", "1":
		return true
	case "false", "0":
		return false
	}
	return appEnv != "production"
}
