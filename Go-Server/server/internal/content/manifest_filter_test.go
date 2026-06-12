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
	m, err := BuildStableManifest(context.Background(), dir, "https://example.test", "stable",
		priv, make([]byte, 32), enabled)
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
	got := buildFor(t, dir, []string{"autowalk"})
	want := []string{"phantom", "phantom-autowalk"} // sorted; mining excluded
	if !reflect.DeepEqual(got, want) {
		t.Errorf("modules = %v, want %v", got, want)
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
