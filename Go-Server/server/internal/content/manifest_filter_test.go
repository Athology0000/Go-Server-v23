package content

import (
	"context"
	"crypto/ed25519"
	"crypto/rand"
	"os"
	"path/filepath"
	"reflect"
	"testing"
)

func contentDirWith(t *testing.T, names ...string) string {
	t.Helper()
	dir := t.TempDir()
	modDir := filepath.Join(dir, "modules")
	if err := os.MkdirAll(modDir, 0o755); err != nil {
		t.Fatal(err)
	}
	for _, name := range names {
		if err := os.WriteFile(filepath.Join(modDir, name), []byte("jar:"+name), 0o644); err != nil {
			t.Fatal(err)
		}
	}
	return dir
}

func buildFor(t *testing.T, dir string, enabled []string) []string {
	t.Helper()
	_, priv, err := ed25519.GenerateKey(rand.Reader)
	if err != nil {
		t.Fatal(err)
	}
	m, err := BuildStableManifest(context.Background(), dir, "", "https://example.test", "stable",
		priv, make([]byte, 32), enabled, nil)
	if err != nil {
		t.Fatal(err)
	}
	names := make([]string, 0, len(m.Modules))
	for _, mod := range m.Modules {
		names = append(names, mod.Name)
	}
	return names
}

func TestManifestFiltersToEntitledModules(t *testing.T) {
	dir := contentDirWith(t, "phantom.jar", "phantom-autowalk.jar", "phantom-mining.jar")
	_, priv, err := ed25519.GenerateKey(rand.Reader)
	if err != nil {
		t.Fatal(err)
	}
	m, err := BuildStableManifest(context.Background(), dir, "", "https://example.test", "stable",
		priv, make([]byte, 32), []string{"autowalk"}, nil)
	if err != nil {
		t.Fatal(err)
	}
	names := make([]string, 0, len(m.Modules))
	for _, mod := range m.Modules {
		names = append(names, mod.Name)
	}
	want := []string{"phantom", "phantom-autowalk"} // sorted; mining excluded
	if !reflect.DeepEqual(names, want) {
		t.Errorf("modules = %v, want %v", names, want)
	}
	for i, mod := range m.Modules {
		if mod.InitOrder != i {
			t.Errorf("InitOrder[%d] = %d, want %d", i, mod.InitOrder, i)
		}
		wantRequired := mod.Name == "phantom"
		if mod.Required != wantRequired {
			t.Errorf("Required[%s] = %v, want %v", mod.Name, mod.Required, wantRequired)
		}
	}
}

// End-to-end: a non-nil moduleDeps map stamps DependsOn onto the right manifest module, keyed by the
// bare id (EntitlementID strips "phantom-"). Other modules carry no deps (preserving signed-byte parity).
func TestManifestStampsDependsOnFromMap(t *testing.T) {
	dir := contentDirWith(t, "phantom.jar", "phantom-commission.jar", "phantom-combat.jar", "phantom-mining.jar")
	_, priv, err := ed25519.GenerateKey(rand.Reader)
	if err != nil {
		t.Fatal(err)
	}
	deps := map[string][]string{"commission": {"combat", "mining"}}
	m, err := BuildStableManifest(context.Background(), dir, "", "https://example.test", "stable",
		priv, make([]byte, 32), []string{"*"}, deps)
	if err != nil {
		t.Fatal(err)
	}
	found := false
	for _, mod := range m.Modules {
		if mod.Name == "phantom-commission" {
			found = true
			if !reflect.DeepEqual(mod.DependsOn, []string{"combat", "mining"}) {
				t.Errorf("phantom-commission DependsOn = %v, want [combat mining]", mod.DependsOn)
			}
		} else if len(mod.DependsOn) != 0 {
			t.Errorf("module %s should carry no deps, got %v", mod.Name, mod.DependsOn)
		}
	}
	if !found {
		t.Fatal("phantom-commission not present in manifest")
	}
}

func TestManifestWithoutCoreBundleSucceeds(t *testing.T) {
	// The framework core ships INSIDE the client jar in this deployment model, so a content dir
	// with only non-core module bundles is a valid deployment and must build a manifest (modules
	// resolve their framework superclasses from the jar's parent classloader), not error.
	dir := contentDirWith(t, "phantom-autowalk.jar", "phantom-combat.jar") // no phantom.jar core
	got := buildFor(t, dir, []string{"autowalk", "combat"})
	want := []string{"phantom-autowalk", "phantom-combat"} // sorted
	if !reflect.DeepEqual(got, want) {
		t.Errorf("modules = %v, want %v", got, want)
	}
}

func TestManifestEmptyContentDirErrors(t *testing.T) {
	// A content dir with zero module artifacts is a genuine misdeploy and must still error rather
	// than signing a manifest that delivers nothing.
	dir := contentDirWith(t) // modules/ exists but is empty
	_, priv, err := ed25519.GenerateKey(rand.Reader)
	if err != nil {
		t.Fatal(err)
	}
	_, err = BuildStableManifest(context.Background(), dir, "", "https://example.test", "stable",
		priv, make([]byte, 32), []string{"*"}, nil)
	if err == nil {
		t.Fatal("expected an error for an empty content dir")
	}
}

func TestManifestWildcardIncludesEverything(t *testing.T) {
	dir := contentDirWith(t, "phantom.jar", "phantom-autowalk.jar", "phantom-mining.jar")
	got := buildFor(t, dir, []string{"*"})
	want := []string{"phantom", "phantom-autowalk", "phantom-mining"}
	if !reflect.DeepEqual(got, want) {
		t.Errorf("modules = %v, want %v", got, want)
	}
}

func TestManifestCoreOnlyIsValid(t *testing.T) {
	dir := contentDirWith(t, "phantom.jar", "phantom-autowalk.jar")
	got := buildFor(t, dir, nil) // entitled to nothing -> core only, NOT an error
	want := []string{"phantom"}
	if !reflect.DeepEqual(got, want) {
		t.Errorf("modules = %v, want %v", got, want)
	}
}
