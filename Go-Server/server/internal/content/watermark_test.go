package content

import (
	"bytes"
	"context"
	"os"
	"os/exec"
	"path/filepath"
	"testing"
	"time"
)

// TestStampJarFailClosed proves the security fix: when the watermarker is enabled but stamping a
// .jar fails (here, a bogus java path makes Apply error), ModuleBytes' helper returns an error
// instead of the raw bytes. The handler maps any untyped error to 500, so the download fails
// rather than handing out an un-watermarked, untraceable jar. Needs no DB or JVM.
func TestStampJarFailClosed(t *testing.T) {
	svc := &Service{}
	svc.SetWatermarker(&Watermarker{
		JavaPath:   filepath.Join(t.TempDir(), "no-such-java-executable"),
		ObfJar:     "obfuscator.jar",
		ConfigPath: "watermark-only.json",
		Secret:     "secret",
	})

	got, err := svc.stampJar(context.Background(), []byte("PK\x03\x04 fake jar"), "/content/modules/mod.jar", "acct-1")
	if err == nil {
		t.Fatal("watermark failure on a .jar must be fatal (fail-closed), but stampJar returned nil error")
	}
	if got != nil {
		t.Fatalf("fail-closed must return no bytes, got %d bytes", len(got))
	}
}

// TestStampJarPassThrough confirms the cases that must remain byte-exact: a disabled watermarker
// (nil) leaves any artifact untouched, and even with an enabled watermarker a non-.jar artifact
// (e.g. an encrypted .enc bundle) is never re-stamped.
func TestStampJarPassThrough(t *testing.T) {
	raw := []byte("byte-exact-payload")

	// Disabled watermarker: a .jar passes through unchanged.
	disabled := &Service{}
	got, err := disabled.stampJar(context.Background(), raw, "/content/modules/mod.jar", "acct")
	if err != nil {
		t.Fatalf("disabled watermarker should not error: %v", err)
	}
	if !bytes.Equal(got, raw) {
		t.Fatal("disabled watermarker altered the jar bytes")
	}

	// Enabled watermarker, but a non-.jar artifact must not be touched (so a stamping path that
	// would fail is never even invoked).
	enabled := &Service{}
	enabled.SetWatermarker(&Watermarker{
		JavaPath:   filepath.Join(t.TempDir(), "no-such-java-executable"),
		ObfJar:     "obfuscator.jar",
		ConfigPath: "watermark-only.json",
		Secret:     "secret",
	})
	got, err = enabled.stampJar(context.Background(), raw, "/content/modules/mod.enc", "acct")
	if err != nil {
		t.Fatalf(".enc artifact should pass through without watermarking: %v", err)
	}
	if !bytes.Equal(got, raw) {
		t.Fatal(".enc artifact was modified — encrypted bundles must stay byte-exact")
	}
}

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
