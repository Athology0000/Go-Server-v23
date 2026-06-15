package forge

import (
	"context"
	"os"
	"os/exec"
	"path/filepath"
	"strings"
	"testing"
	"time"
)

// newTestForge builds a Forge pointed at the real obfuscator artifacts, or skips when any
// prerequisite (java on PATH, obfuscator jar, forge-prod config, sample jar) is missing.
func newTestForge(t *testing.T) (*Forge, []byte, []byte) {
	t.Helper()

	java := os.Getenv("FORGE_JAVA")
	if java == "" {
		java = "java"
	}
	if _, err := exec.LookPath(java); err != nil {
		t.Skipf("java not on PATH (%v); skipping forge build e2e", err)
	}

	obfDir := os.Getenv("PHANTOM_OBF_DIR")
	if obfDir == "" {
		obfDir = `C:\Users\aeare\Desktop\Full phantom Recode\obfuscator`
	}
	obfJar := filepath.Join(obfDir, "build", "libs", "obfuscator.jar")
	sampleJar := filepath.Join(obfDir, "input.jar")

	config := os.Getenv("FORGE_TEST_CONFIG")
	if config == "" {
		// package dir is internal/forge; the config ships at server/configs/forge-prod.json
		abs, err := filepath.Abs(filepath.Join("..", "..", "configs", "forge-prod.json"))
		if err != nil {
			t.Fatal(err)
		}
		config = abs
	}

	for _, p := range []string{obfJar, sampleJar, config} {
		if _, err := os.Stat(p); err != nil {
			t.Skipf("missing forge artifact %s (%v); skipping forge build e2e", p, err)
		}
	}

	jarBytes, err := os.ReadFile(sampleJar)
	if err != nil {
		t.Fatalf("read sample jar: %v", err)
	}

	f := &Forge{
		JavaPath:   java,
		ObfJar:     obfJar,
		ConfigPath: config,
		Secret:     "forge-test-secret",
		StagingDir: t.TempDir(),
	}
	if !f.Enabled() {
		t.Fatal("forge should report Enabled() with full config")
	}
	return f, jarBytes, sampleDLL()
}

// TestForgeBuildProducesWatermarkedJarAndMarkedDLL is the increment-1 proof: a delivered
// jar+dll comes out as an obfuscated, build-watermarked jar plus a marker-bound dll, staged
// with matching checksums — all carrying the same build id.
func TestForgeBuildProducesWatermarkedJarAndMarkedDLL(t *testing.T) {
	f, jarBytes, dllBytes := newTestForge(t)

	ctx, cancel := context.WithTimeout(context.Background(), 4*time.Minute)
	defer cancel()

	buildID, err := NewBuildID()
	if err != nil {
		t.Fatalf("NewBuildID: %v", err)
	}

	res, err := f.Build(ctx, "phantom-forgetest.jar", buildID, jarBytes, dllBytes)
	if err != nil {
		t.Fatalf("Build: %v", err)
	}

	// module name is sanitized (extension stripped).
	if res.Module != "phantom-forgetest" {
		t.Errorf("module = %q, want phantom-forgetest", res.Module)
	}

	// staged files exist and their on-disk sha256 matches the reported checksum.
	stagedJar := mustRead(t, res.JarPath)
	stagedDll := mustRead(t, res.DLLPath)
	if sha256Hex(stagedJar) != res.JarSHA256 {
		t.Error("staged jar sha256 mismatch")
	}
	if sha256Hex(stagedDll) != res.DLLSHA256 {
		t.Error("staged dll sha256 mismatch")
	}

	// the obfuscated jar must differ from the delivered payload (passes + watermark ran).
	if len(stagedJar) == len(jarBytes) && string(stagedJar) == string(jarBytes) {
		t.Error("staged jar is byte-identical to the delivered jar — obfuscation did not run")
	}

	// the dll carries this build's marker.
	gotDllID, ok := ExtractDLLMarker(stagedDll, f.Secret)
	if !ok || gotDllID != buildID {
		t.Fatalf("dll marker = (%q, ok=%v), want %q", gotDllID, ok, buildID)
	}

	// the jar carries this build's watermark (verified via the obfuscator's extractor).
	gotJarID := extractJarWatermark(t, ctx, f, res.JarPath)
	if gotJarID != buildID {
		t.Fatalf("jar watermark = %q, want %q", gotJarID, buildID)
	}
}

func TestForgeBuildRejectsEmptyInputs(t *testing.T) {
	f := &Forge{ObfJar: "x", ConfigPath: "y", Secret: "z", StagingDir: t.TempDir()}
	ctx := context.Background()
	if _, err := f.Build(ctx, "m", "build-1", nil, []byte{1}); err == nil {
		t.Error("expected error on empty jar")
	}
	if _, err := f.Build(ctx, "m", "build-1", []byte{1}, nil); err == nil {
		t.Error("expected error on empty dll")
	}
	if _, err := f.Build(ctx, "m", "", []byte{1}, []byte{1}); err == nil {
		t.Error("expected error on empty build id")
	}
}

func extractJarWatermark(t *testing.T, ctx context.Context, f *Forge, jarPath string) string {
	t.Helper()
	out, err := exec.CommandContext(ctx, f.java(), "-jar", f.ObfJar,
		"--extract-watermark", jarPath, f.Secret).CombinedOutput()
	if err != nil {
		t.Fatalf("extract jar watermark: %v: %s", err, strings.TrimSpace(string(out)))
	}
	return strings.TrimSpace(string(out))
}

func mustRead(t *testing.T, path string) []byte {
	t.Helper()
	b, err := os.ReadFile(path)
	if err != nil {
		t.Fatalf("read %s: %v", path, err)
	}
	return b
}
