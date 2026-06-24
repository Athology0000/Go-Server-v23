package content

import "testing"

// The native download gate must authorize a request the SAME way the signed
// manifest filters natives — through ModuleAllowed against the caller's
// enabled_modules — so what a client is told it may have and what it can
// actually download cannot diverge. Critically the rule is fail-closed: an
// entitlement with no enabled modules (e.g. the seeded "trial" tier, whose
// native_components AND enabled_modules are both []) must NOT be able to
// download any native. The previous gate treated an empty native_components
// list as allow-all, letting a trial/restricted tier pull every native
// (phantom_auth.dll, the pathfinder, ...). This pins the corrected contract.
func TestNativeDownloadAllowed(t *testing.T) {
	cases := []struct {
		name           string
		native         string
		enabledModules []string
		want           bool
	}{
		{"no modules (trial) denies a native — empty=allow-all bypass closed", "phantom_auth.dll", nil, false},
		{"empty slice denies", "phantom_pathfinder.dll", []string{}, false},
		{"wildcard tier allows any native", "phantom_auth.dll", []string{"*"}, true},
		{"unrelated module entitlement denies", "phantom_auth.dll", []string{"mining"}, false},
		{"path components are stripped before the check", "../../phantom_auth.dll", []string{"*"}, true},
	}
	for _, c := range cases {
		t.Run(c.name, func(t *testing.T) {
			if got := nativeDownloadAllowed(c.native, c.enabledModules); got != c.want {
				t.Fatalf("nativeDownloadAllowed(%q, %v) = %v, want %v", c.native, c.enabledModules, got, c.want)
			}
		})
	}
}
