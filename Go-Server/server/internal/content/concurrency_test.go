package content

import (
	"bytes"
	"os"
	"path/filepath"
	"sync"
	"sync/atomic"
	"testing"

	ccrypto "github.com/phantom/server/internal/crypto"
)

// TestEnsureLicenseBundlesConcurrentSingleConsistentOutput hammers EnsureLicenseBundles for ONE
// license from many goroutines (the first-sight burst: a manifest build, a module download, and
// /auth/finish can all regen the same license at once). On the unfixed code this fails two ways:
//   - evictStaleScheme ReadDir->Remove races a sibling goroutine's just-written .enc -> os.Remove
//     ENOENT surfaces as a non-nil error; and
//   - the in-place os.WriteFile lets a concurrent reader observe a torn .enc. Because AES-GCM uses a
//     fresh random nonce per call, each goroutine writes DIFFERENT ciphertext, so a torn/overwritten
//     file fails GCM auth (undecryptable) — the SHA the manifest signed then desyncs from the served
//     bytes and the client fail-closes ("session locked").
//
// With per-license singleflight + atomic temp/rename it must: never error, never expose a torn read,
// and converge to a single decryptable bundle.
func TestEnsureLicenseBundlesConcurrentSingleConsistentOutput(t *testing.T) {
	key := make([]byte, 32)
	for i := range key {
		key[i] = byte(i)
	}
	dir := t.TempDir()
	jarsDir := filepath.Join(dir, "_jars")
	if err := os.MkdirAll(jarsDir, 0o755); err != nil {
		t.Fatal(err)
	}
	jar := makeJar(t, map[string]string{"META-INF/MANIFEST.MF": "Manifest-Version: 1.0\r\n\r\n"})
	if err := os.WriteFile(filepath.Join(jarsDir, "phantom-autowalk.jar"), jar, 0o644); err != nil {
		t.Fatal(err)
	}

	const lic = "lic-RACE"
	const secret, pepper = "secret", "pepper"
	derived := DeriveLicenseKey(key, lic)
	encPath := filepath.Join(dir, "modules", lic, "phantom-autowalk.enc")

	const writers = 32
	var wg sync.WaitGroup
	errs := make(chan error, writers)
	stop := make(chan struct{})

	// Concurrent reader: whenever the .enc exists and is non-empty it MUST decrypt. A torn in-place
	// write (differing nonces across goroutines) yields undecryptable bytes.
	var tornObserved int32
	var rwg sync.WaitGroup
	rwg.Add(1)
	go func() {
		defer rwg.Done()
		for {
			select {
			case <-stop:
				return
			default:
			}
			b, err := os.ReadFile(encPath)
			if err != nil || len(b) == 0 {
				continue // not yet published / transient miss before first publication
			}
			if _, derr := ccrypto.DecryptAESGCM(derived, b); derr != nil {
				atomic.StoreInt32(&tornObserved, 1)
			}
		}
	}()

	for i := 0; i < writers; i++ {
		wg.Add(1)
		go func() {
			defer wg.Done()
			errs <- EnsureLicenseBundles(dir, lic, key, secret, pepper)
		}()
	}
	wg.Wait()
	close(errs)
	close(stop)
	rwg.Wait()

	for err := range errs {
		if err != nil {
			t.Errorf("concurrent EnsureLicenseBundles returned error (eviction/write race): %v", err)
		}
	}
	if atomic.LoadInt32(&tornObserved) == 1 {
		t.Error("reader observed a torn/undecryptable .enc mid-write (non-atomic publication)")
	}

	// Converged output must exist and decrypt, and an idempotent re-run must leave it byte-identical.
	final, err := os.ReadFile(encPath)
	if err != nil {
		t.Fatalf("final bundle missing: %v", err)
	}
	if _, err := ccrypto.DecryptAESGCM(derived, final); err != nil {
		t.Fatalf("final bundle does not decrypt: %v", err)
	}
	if err := EnsureLicenseBundles(dir, lic, key, secret, pepper); err != nil {
		t.Fatalf("idempotent re-run errored: %v", err)
	}
	again, err := os.ReadFile(encPath)
	if err != nil {
		t.Fatalf("bundle missing after idempotent re-run: %v", err)
	}
	if !bytes.Equal(final, again) {
		t.Error("idempotent re-run changed the converged bundle bytes")
	}
}
