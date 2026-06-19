package content

import (
	"archive/zip"
	"bytes"
	"io"
	"os"
	"path/filepath"
	"strings"
	"testing"

	ccrypto "github.com/phantom/server/internal/crypto"
)

func makeJar(t *testing.T, entries map[string]string) []byte {
	t.Helper()
	var buf bytes.Buffer
	zw := zip.NewWriter(&buf)
	for name, body := range entries {
		w, err := zw.Create(name)
		if err != nil {
			t.Fatal(err)
		}
		if _, err := w.Write([]byte(body)); err != nil {
			t.Fatal(err)
		}
	}
	if err := zw.Close(); err != nil {
		t.Fatal(err)
	}
	return buf.Bytes()
}

func jarEntry(t *testing.T, jarBytes []byte, name string) (string, bool) {
	t.Helper()
	zr, err := zip.NewReader(bytes.NewReader(jarBytes), int64(len(jarBytes)))
	if err != nil {
		t.Fatal(err)
	}
	for _, f := range zr.File {
		if f.Name == name {
			rc, _ := f.Open()
			b, _ := io.ReadAll(rc)
			rc.Close()
			return string(b), true
		}
	}
	return "", false
}

// WatermarkID + the mark signature must match the obfuscator's buildModules output exactly, so a
// server-generated bundle is byte-compatible with --extract-watermark and the trace ledger. Golden
// values captured from the production dry-run build (license 169d2afc / pepper cd33a668…) and the
// local dry run (secret 077e8e0d… / wmid 46e0db…).
func TestWatermarkIDGolden(t *testing.T) {
	got := WatermarkID("cd33a668eb57bdbeecc7bbc0bc5d41d03d9153463158e8a8410ec737600d675e",
		"169d2afc-92eb-4a0a-a50d-424249dbc5c5")
	if got != "2dfa9a9b9ea3d5a1" {
		t.Fatalf("WatermarkID = %s, want 2dfa9a9b9ea3d5a1 (must match buildModules)", got)
	}
}

func TestWatermarkMarkGolden(t *testing.T) {
	got := watermarkMark("077e8e0d9c0ec4a253194f995dab5f10936f26b562c5d1e75627a70de18b9dc6",
		"46e0db6064093727")
	want := "46e0db6064093727:d6d79ada0cdac088dcb411ff1f2c5c55435f221dd04c89cec9c0ac3ced360955"
	if got != want {
		t.Fatalf("watermarkMark = %s, want %s (HMAC must match the obfuscator)", got, want)
	}
}

func TestWatermarkJarEmbedsBothSites(t *testing.T) {
	jar := makeJar(t, map[string]string{
		"META-INF/MANIFEST.MF":  "Manifest-Version: 1.0\r\n\r\n",
		"xbundle/AutoWalk.class": "fake-class-bytes",
	})
	wmid := "abcdef0123456789"
	out, err := WatermarkJar(jar, wmid, "the-secret")
	if err != nil {
		t.Fatalf("WatermarkJar: %v", err)
	}
	mark := watermarkMark("the-secret", wmid)
	if cb, ok := jarEntry(t, out, "assets/.cbmark"); !ok || cb != mark {
		t.Fatalf("assets/.cbmark = %q (ok=%v), want %q", cb, ok, mark)
	}
	mf, ok := jarEntry(t, out, "META-INF/MANIFEST.MF")
	if !ok || !strings.Contains(mf, "Cobalt-Mark: "+mark) {
		t.Fatalf("manifest missing Cobalt-Mark header: %q", mf)
	}
	if _, ok := jarEntry(t, out, "xbundle/AutoWalk.class"); !ok {
		t.Fatal("original class entry must be preserved")
	}
}

func TestEnsureLicenseBundlesGeneratesPerLicenseEnc(t *testing.T) {
	key := make([]byte, 32)
	for i := range key {
		key[i] = byte(i)
	}
	dir := t.TempDir()
	jarsDir := filepath.Join(dir, "_jars")
	if err := os.MkdirAll(jarsDir, 0o755); err != nil {
		t.Fatal(err)
	}
	// A "core" jar (so IsCoreModule passes downstream) + a module jar.
	for _, name := range []string{"phantom.jar", "phantom-autowalk.jar"} {
		jar := makeJar(t, map[string]string{"META-INF/MANIFEST.MF": "Manifest-Version: 1.0\r\n\r\n"})
		if err := os.WriteFile(filepath.Join(jarsDir, name), jar, 0o644); err != nil {
			t.Fatal(err)
		}
	}

	lic := "lic-XYZ"
	secret, pepper := "the-secret", "the-pepper"
	if err := EnsureLicenseBundles(dir, lic, key, secret, pepper); err != nil {
		t.Fatalf("EnsureLicenseBundles: %v", err)
	}

	encPath := filepath.Join(dir, "modules", lic, "phantom-autowalk.enc")
	enc, err := os.ReadFile(encPath)
	if err != nil {
		t.Fatalf("expected generated bundle: %v", err)
	}
	// Decrypts with the per-license DERIVED key (not the raw server key) to a watermarked jar.
	plain, err := ccrypto.DecryptAESGCM(DeriveLicenseKey(key, lic), enc)
	if err != nil {
		t.Fatalf("generated .enc must decrypt with the derived per-license key: %v", err)
	}
	wmid := WatermarkID(pepper, lic)
	if cb, ok := jarEntry(t, plain, "assets/.cbmark"); !ok || !strings.HasPrefix(cb, wmid+":") {
		t.Fatalf("generated bundle watermark = %q, want prefix %s:", cb, wmid)
	}
	// Ledger maps the wmid to the license.
	led, _ := os.ReadFile(filepath.Join(dir, "watermark-map.json"))
	if !strings.Contains(string(led), wmid+"="+lic) {
		t.Fatalf("ledger missing %s=%s; got %q", wmid, lic, led)
	}
	// Idempotent: a second call regenerates nothing (mtime unchanged).
	info1, _ := os.Stat(encPath)
	if err := EnsureLicenseBundles(dir, lic, key, secret, pepper); err != nil {
		t.Fatal(err)
	}
	info2, _ := os.Stat(encPath)
	if !info1.ModTime().Equal(info2.ModTime()) {
		t.Error("EnsureLicenseBundles must be idempotent (not regenerate existing bundles)")
	}
}
