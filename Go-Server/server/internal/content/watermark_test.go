package content

import (
	"context"
	"os"
	"os/exec"
	"path/filepath"
	"testing"
	"time"
)

// obfDir resolves the local Phantom obfuscator checkout that provides the watermark-capable
// jar, the watermark-only config, and a sample input jar. Override with PHANTOM_OBF_DIR.
func obfDir() string {
	if d := os.Getenv("PHANTOM_OBF_DIR"); d != "" {
		return d
	}
	return `C:\Users\aeare\Desktop\Full phantom Recode\obfuscator`
}

// newTestWatermarker builds a Watermarker pointed at the real obfuscator artifacts, or skips
// the test when any prerequisite (java on PATH, built jar, config, sample jar) is missing — so
// the suite stays green on machines without the obfuscator checkout.
func newTestWatermarker(t *testing.T) (*Watermarker, []byte) {
	t.Helper()

	java := os.Getenv("WATERMARK_JAVA")
	if java == "" {
		java = "java"
	}
	if _, err := exec.LookPath(java); err != nil {
		t.Skipf("java not on PATH (%v); skipping watermark e2e", err)
	}

	dir := obfDir()
	obfJar := filepath.Join(dir, "build", "libs", "obfuscator.jar")
	config := filepath.Join(dir, "watermark-only.json")
	inputJar := filepath.Join(dir, "input.jar")

	for _, p := range []string{obfJar, config, inputJar} {
		if _, err := os.Stat(p); err != nil {
			t.Skipf("missing obfuscator artifact %s (%v); skipping watermark e2e", p, err)
		}
	}

	raw, err := os.ReadFile(inputJar)
	if err != nil {
		t.Fatalf("read sample jar: %v", err)
	}

	wm := &Watermarker{
		JavaPath:   java,
		ObfJar:     obfJar,
		ConfigPath: config,
		Secret:     "test-wm-secret",
	}
	if !wm.Enabled() {
		t.Fatal("watermarker should report Enabled() with full config")
	}
	return wm, raw
}

// TestWatermarkRoundTrip proves the server can hand back a per-user watermarked jar: it stamps
// the sample jar with an account id and recovers exactly that id via extraction. This is the
// download-page flow the content service runs in ModuleBytes.
func TestWatermarkRoundTrip(t *testing.T) {
	wm, raw := newTestWatermarker(t)

	// The obfuscator JVM cold-starts and runs name obfuscation; give it generous headroom.
	ctx, cancel := context.WithTimeout(context.Background(), 3*time.Minute)
	defer cancel()

	const account = "acct-test-12345"

	stamped, err := wm.Apply(ctx, raw, account)
	if err != nil {
		t.Fatalf("Apply: %v", err)
	}
	if len(stamped) == 0 {
		t.Fatal("Apply returned empty jar")
	}

	got, err := wm.ExtractWatermark(ctx, stamped)
	if err != nil {
		t.Fatalf("ExtractWatermark: %v", err)
	}
	if got != account {
		t.Fatalf("watermark mismatch: got %q, want %q", got, account)
	}
}

// TestWatermarkPerUserDistinct confirms two different accounts get distinguishable jars, each
// carrying its own recoverable mark — the property that makes a leaked jar traceable.
func TestWatermarkPerUserDistinct(t *testing.T) {
	wm, raw := newTestWatermarker(t)

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Minute)
	defer cancel()

	a, err := wm.Apply(ctx, raw, "acct-alice")
	if err != nil {
		t.Fatalf("Apply(alice): %v", err)
	}
	b, err := wm.Apply(ctx, raw, "acct-bob")
	if err != nil {
		t.Fatalf("Apply(bob): %v", err)
	}

	gotA, err := wm.ExtractWatermark(ctx, a)
	if err != nil {
		t.Fatalf("ExtractWatermark(alice): %v", err)
	}
	gotB, err := wm.ExtractWatermark(ctx, b)
	if err != nil {
		t.Fatalf("ExtractWatermark(bob): %v", err)
	}

	if gotA != "acct-alice" {
		t.Fatalf("alice mark: got %q, want acct-alice", gotA)
	}
	if gotB != "acct-bob" {
		t.Fatalf("bob mark: got %q, want acct-bob", gotB)
	}
}

// TestWatermarkWrongSecretRejected verifies extraction fails when the verifier's HMAC secret
// does not match the one used to stamp — so a forged or unsigned mark cannot be passed off.
func TestWatermarkWrongSecretRejected(t *testing.T) {
	wm, raw := newTestWatermarker(t)

	ctx, cancel := context.WithTimeout(context.Background(), 3*time.Minute)
	defer cancel()

	stamped, err := wm.Apply(ctx, raw, "acct-test-12345")
	if err != nil {
		t.Fatalf("Apply: %v", err)
	}

	wrong := &Watermarker{
		JavaPath:   wm.JavaPath,
		ObfJar:     wm.ObfJar,
		ConfigPath: wm.ConfigPath,
		Secret:     "a-different-secret",
	}
	if _, err := wrong.ExtractWatermark(ctx, stamped); err == nil {
		t.Fatal("extraction with wrong secret should fail, but succeeded")
	}
}
