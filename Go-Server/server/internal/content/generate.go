package content

import (
	"archive/zip"
	"bytes"
	"crypto/hmac"
	"crypto/sha256"
	"encoding/hex"
	"io"
	"os"
	"path/filepath"
	"strings"

	ccrypto "github.com/phantom/server/internal/crypto"
)

// Server-side per-client generation: the server holds the (obfuscated) plaintext module jars and, on a
// license's first request, watermarks each per-license, AES-encrypts it, and caches the .enc under
// CONTENT_DIR/modules/<licenseId>/ — so SP-D's per-license manifest + routing then serve it. No build,
// git, or redeploy per customer; no JVM (the watermark is a pure-Go reimplementation of the
// obfuscator's WatermarkStage, byte-compatible with --extract-watermark and the trace ledger).

// WatermarkID derives the 16-hex per-license watermark id. Matches buildModules' wmId():
// first16hex(HMAC-SHA256(wmPepper, "wm:"+licenseId)).
func WatermarkID(wmPepper, licenseID string) string {
	mac := hmac.New(sha256.New, []byte(wmPepper))
	mac.Write([]byte("wm:" + licenseID))
	return hex.EncodeToString(mac.Sum(nil))[:16]
}

// watermarkMark is the "<wmid>:<sig>" string embedded at both sites, where sig = hex HMAC-SHA256 of the
// wmid under the (string) wm secret — identical to the obfuscator's WatermarkStage.
func watermarkMark(wmSecret, wmid string) string {
	mac := hmac.New(sha256.New, []byte(wmSecret))
	mac.Write([]byte(wmid))
	return wmid + ":" + hex.EncodeToString(mac.Sum(nil))
}

// WatermarkJar embeds the Cobalt-Mark into a jar at the two sites the obfuscator uses — the
// META-INF/MANIFEST.MF "Cobalt-Mark" header and the assets/.cbmark resource — and returns the new jar.
func WatermarkJar(jarBytes []byte, wmid, wmSecret string) ([]byte, error) {
	mark := watermarkMark(wmSecret, wmid)
	zr, err := zip.NewReader(bytes.NewReader(jarBytes), int64(len(jarBytes)))
	if err != nil {
		return nil, err
	}
	var buf bytes.Buffer
	zw := zip.NewWriter(&buf)
	wroteManifest := false
	for _, f := range zr.File {
		if f.Name == "assets/.cbmark" {
			continue // rewritten below
		}
		rc, err := f.Open()
		if err != nil {
			return nil, err
		}
		data, err := io.ReadAll(rc)
		rc.Close()
		if err != nil {
			return nil, err
		}
		if f.Name == "META-INF/MANIFEST.MF" {
			data = addManifestHeader(data, "Cobalt-Mark", mark)
			wroteManifest = true
		}
		w, err := zw.Create(f.Name)
		if err != nil {
			return nil, err
		}
		if _, err := w.Write(data); err != nil {
			return nil, err
		}
	}
	if !wroteManifest {
		w, err := zw.Create("META-INF/MANIFEST.MF")
		if err != nil {
			return nil, err
		}
		if _, err := w.Write([]byte("Manifest-Version: 1.0\r\nCobalt-Mark: " + mark + "\r\n\r\n")); err != nil {
			return nil, err
		}
	}
	cb, err := zw.Create("assets/.cbmark")
	if err != nil {
		return nil, err
	}
	if _, err := cb.Write([]byte(mark)); err != nil {
		return nil, err
	}
	if err := zw.Close(); err != nil {
		return nil, err
	}
	return buf.Bytes(), nil
}

// addManifestHeader appends "Key: Value" to a jar manifest, before its trailing blank line.
func addManifestHeader(manifest []byte, key, value string) []byte {
	body := strings.TrimRight(string(manifest), "\r\n ")
	return []byte(body + "\r\n" + key + ": " + value + "\r\n\r\n")
}

// EnsureLicenseBundles generates any missing per-license .enc bundles for licenseID from the shared
// jars in CONTENT_DIR/_jars, appending each (wmid -> licenseId) to the watermark-map ledger. Idempotent:
// already-generated bundles are left untouched (so it is cheap to call on every manifest build).
//
// Each bundle is encrypted under the license's DERIVED key (DeriveLicenseKey(moduleKey, licenseID)),
// not the raw server key — so a bundle sealed for one license cannot be opened with another's key. The
// matching key is what BuildStableManifest advertises in that license's signed manifest, so the client
// (which reads module_key from the manifest at runtime) needs no change.
//
// Cache safety across the key-scheme change: a .keyscheme marker is written into each license subtree.
// If it is absent (a subtree generated under the OLD single global key, before this change) or its
// scheme does not match keyDerivationScheme, the stale subtree's .enc bundles are wiped and
// regenerated under the derived key rather than served undecryptable.
func EnsureLicenseBundles(contentDir, licenseID string, moduleKey []byte, wmSecret, wmPepper string) error {
	if licenseID == "" || wmSecret == "" {
		return nil // not configured for per-license generation; serve whatever is already on disk
	}
	jarsDir := filepath.Join(contentDir, "_jars")
	entries, err := os.ReadDir(jarsDir)
	if err != nil {
		if os.IsNotExist(err) {
			return nil // no raw jars shipped; nothing to generate
		}
		return err
	}
	licDir := filepath.Join(contentDir, "modules", licenseID)
	if err := os.MkdirAll(licDir, 0o755); err != nil {
		return err
	}
	// If this subtree predates the per-license key scheme (or used an older one), purge its stale
	// .enc bundles so they are regenerated below under the derived key.
	if err := evictStaleScheme(licDir); err != nil {
		return err
	}
	licenseKey := DeriveLicenseKey(moduleKey, licenseID)
	wid := WatermarkID(wmPepper, licenseID)
	generatedAny := false
	for _, e := range entries {
		if e.IsDir() || !strings.HasSuffix(strings.ToLower(e.Name()), ".jar") {
			continue
		}
		module := strings.TrimSuffix(e.Name(), filepath.Ext(e.Name()))
		encPath := filepath.Join(licDir, module+".enc")
		if _, statErr := os.Stat(encPath); statErr == nil {
			continue // already generated
		}
		raw, err := os.ReadFile(filepath.Join(jarsDir, e.Name()))
		if err != nil {
			return err
		}
		wm, err := WatermarkJar(raw, wid, wmSecret)
		if err != nil {
			return err
		}
		enc, err := ccrypto.EncryptAESGCM(licenseKey, wm)
		if err != nil {
			return err
		}
		if err := os.WriteFile(encPath, enc, 0o644); err != nil {
			return err
		}
		generatedAny = true
	}
	if generatedAny {
		if err := appendLedger(contentDir, wid, licenseID); err != nil {
			return err
		}
	}
	// Stamp the scheme so a future EnsureLicenseBundles treats this subtree as
	// current and does not re-purge it. Written even when nothing was generated so
	// an already-current subtree (e.g. wiped then immediately re-run) is marked.
	if err := writeSchemeMarker(licDir); err != nil {
		return err
	}
	return nil
}

// schemeMarkerName is the per-license-subtree file recording which key-derivation
// scheme its .enc bundles were sealed under.
const schemeMarkerName = ".keyscheme"

// evictStaleScheme removes the .enc bundles in licDir when its scheme marker is
// missing or does not match the current keyDerivationScheme — i.e. the subtree was
// generated under the old single global key (no marker) or a superseded scheme. The
// bundles are then regenerated under the derived key by the caller. Non-.enc files
// and the marker itself are left alone.
func evictStaleScheme(licDir string) error {
	markerPath := filepath.Join(licDir, schemeMarkerName)
	if data, err := os.ReadFile(markerPath); err == nil {
		if strings.TrimSpace(string(data)) == keyDerivationScheme {
			return nil // current scheme; nothing to evict
		}
	} else if !os.IsNotExist(err) {
		return err
	}
	entries, err := os.ReadDir(licDir)
	if err != nil {
		if os.IsNotExist(err) {
			return nil
		}
		return err
	}
	for _, e := range entries {
		if e.IsDir() || !strings.HasSuffix(strings.ToLower(e.Name()), ".enc") {
			continue
		}
		if err := os.Remove(filepath.Join(licDir, e.Name())); err != nil {
			return err
		}
	}
	return nil
}

// writeSchemeMarker records the current key-derivation scheme for licDir.
func writeSchemeMarker(licDir string) error {
	return os.WriteFile(filepath.Join(licDir, schemeMarkerName), []byte(keyDerivationScheme+"\n"), 0o644)
}

// appendLedger records "wmid=licenseId" in CONTENT_DIR/watermark-map.json if not already present, so
// the trace endpoint can map a recovered watermark back to its license.
func appendLedger(contentDir, wid, licenseID string) error {
	path := filepath.Join(contentDir, "watermark-map.json")
	line := wid + "=" + licenseID
	if existing, err := os.ReadFile(path); err == nil {
		if strings.Contains(string(existing), line) {
			return nil
		}
	}
	f, err := os.OpenFile(path, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0o644)
	if err != nil {
		return err
	}
	defer f.Close()
	_, err = f.WriteString(line + "\n")
	return err
}
