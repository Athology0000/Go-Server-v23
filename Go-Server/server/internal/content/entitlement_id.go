package content

import "strings"

// EntitlementID maps a module artifact name to the canonical bare module id
// stored in entitlements.enabled_modules: strip path + extension (via
// NormalizeModuleName), then the build's "phantom-" bundle prefix.
// "phantom-autowalk.enc" -> "autowalk".
func EntitlementID(name string) string {
	return strings.TrimPrefix(NormalizeModuleName(name), "phantom-")
}

// IsCoreModule reports whether the artifact is the always-entitled framework
// core: the master bundle ("phantom.jar"/"phantom.enc") or legacy "phantom-core".
func IsCoreModule(name string) bool {
	id := NormalizeModuleName(name)
	return id == "phantom" || id == "phantom-core"
}

// ModuleAllowed is the single entitlement decision shared by the manifest
// filter and the module download gate, so the two can never disagree. Core is
// always allowed; "*" is a wildcard; otherwise the bare id must be entitled
// (the prefixed artifact name and its ".jar" form stay accepted for rows
// written before the bare-id convention).
func ModuleAllowed(name string, enabledModules []string) bool {
	if IsCoreModule(name) {
		return true
	}
	id := EntitlementID(name)
	full := NormalizeModuleName(name)
	for _, m := range enabledModules {
		switch m {
		case "*", id, full, full + ".jar":
			return true
		}
	}
	return false
}
