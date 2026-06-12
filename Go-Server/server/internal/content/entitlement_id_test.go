package content

import "testing"

func TestEntitlementID(t *testing.T) {
	cases := []struct{ in, want string }{
		{"phantom-autowalk.enc", "autowalk"},
		{"phantom-autowalk.jar", "autowalk"},
		{"phantom-autowalk", "autowalk"},
		{"autowalk", "autowalk"},
		{"phantom.enc", "phantom"},
		{"phantom-core", "core"}, // core is matched via IsCoreModule BEFORE this is consulted
	}
	for _, c := range cases {
		if got := EntitlementID(c.in); got != c.want {
			t.Errorf("EntitlementID(%q) = %q, want %q", c.in, got, c.want)
		}
	}
}

func TestIsCoreModule(t *testing.T) {
	for _, name := range []string{"phantom.enc", "phantom.jar", "phantom", "phantom-core", "phantom-core.jar"} {
		if !IsCoreModule(name) {
			t.Errorf("IsCoreModule(%q) = false, want true", name)
		}
	}
	for _, name := range []string{"phantom-autowalk.enc", "autowalk"} {
		if IsCoreModule(name) {
			t.Errorf("IsCoreModule(%q) = true, want false", name)
		}
	}
}

func TestModuleAllowed(t *testing.T) {
	cases := []struct {
		name    string
		modules []string
		want    bool
	}{
		{"phantom.enc", nil, true},                                       // core always
		{"phantom-core", nil, true},                                      // legacy core always
		{"phantom-autowalk.enc", []string{"*"}, true},                    // wildcard
		{"phantom-autowalk.enc", []string{"autowalk"}, true},             // canonical bare id
		{"phantom-autowalk.enc", []string{"phantom-autowalk"}, true},     // legacy full name
		{"phantom-autowalk.enc", []string{"phantom-autowalk.jar"}, true}, // legacy .jar form
		{"phantom-autowalk", []string{"phantom-autowalk.jar"}, true},     // legacy .jar row, extensionless request
		{"phantom-autowalk.enc", []string{"mining"}, false},
		{"phantom-autowalk.enc", nil, false},
	}
	for _, c := range cases {
		if got := ModuleAllowed(c.name, c.modules); got != c.want {
			t.Errorf("ModuleAllowed(%q, %v) = %v, want %v", c.name, c.modules, got, c.want)
		}
	}
}
