package content

import (
	"crypto/ed25519"
	"encoding/json"
	"errors"
	"testing"
	"time"

	ccrypto "github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/db"
	"github.com/phantom/server/internal/entitlement"
)

// manifestForChannel builds a signed DB-style manifest carrying modules from two
// tiers (a free one and a premium one), as the admin path would persist it.
func manifestForChannel(t *testing.T, channel string, signingKey ed25519.PrivateKey) *db.ContentManifest {
	t.Helper()
	expires := time.Now().Add(15 * time.Minute)
	m := &db.ContentManifest{
		ID:               "11111111-1111-1111-1111-111111111111",
		BuildID:          "build-" + channel,
		Channel:          channel,
		MinLoaderVersion: "1",
		ModuleKey:        "bW9kdWxlS2V5", // base64("moduleKey")
		Modules: []db.ManifestModule{
			{Name: "phantom", URL: "https://example.test/content/module/phantom", SHA256: "aaa", Required: true, InitOrder: 0},
			{Name: "phantom-autowalk", URL: "https://example.test/content/module/phantom-autowalk", SHA256: "bbb", Required: false, InitOrder: 1},
			{Name: "phantom-mining", URL: "https://example.test/content/module/phantom-mining", SHA256: "ccc", Required: false, InitOrder: 2},
		},
		NativeComponents: []db.ManifestNative{
			{Name: "phantom-autowalk.dll", URL: "https://example.test/content/native/phantom-autowalk.dll", SHA256: "ddd", Required: true},
			{Name: "phantom-mining.dll", URL: "https://example.test/content/native/phantom-mining.dll", SHA256: "eee", Required: true},
		},
		ExpiresAt:       expires,
		ExpiresAtMillis: expires.UnixMilli(),
		Epoch:           expires.UnixMilli(),
	}
	sig, err := SignManifest(signingKey, m)
	if err != nil {
		t.Fatalf("sign fixture: %v", err)
	}
	m.Signature = sig
	return m
}

// A caller entitled only to a DIFFERENT channel must be rejected outright: a manifest
// for another channel/tier leaks that channel's module names, URLs, hashes and key.
func TestScopeManifest_RejectsCrossChannel(t *testing.T) {
	priv := fixedSigningKey()
	m := manifestForChannel(t, "premium", priv)
	ent := &entitlement.Result{
		Authorized:     true,
		ContentChannel: "stable", // different channel than the manifest
		EnabledModules: []string{"*"},
	}
	_, err := scopeManifestForEntitlement(m, ent, priv)
	if !errors.Is(err, ErrNotEntitled) {
		t.Fatalf("cross-channel request err = %v, want ErrNotEntitled", err)
	}
}

// Same channel but a narrower tier: unentitled modules/natives are dropped and the
// returned manifest is re-signed so its signature still verifies over the filtered bytes.
func TestScopeManifest_FiltersAndResigns(t *testing.T) {
	priv := fixedSigningKey()
	m := manifestForChannel(t, "stable", priv)
	originalSig := m.Signature
	ent := &entitlement.Result{
		Authorized:     true,
		ContentChannel: "stable",
		EnabledModules: []string{"autowalk"}, // entitled to autowalk only, not mining
	}

	got, err := scopeManifestForEntitlement(m, ent, priv)
	if err != nil {
		t.Fatalf("scopeManifestForEntitlement: %v", err)
	}

	// mining must be gone from both modules and native components; core + autowalk remain.
	gotModules := map[string]bool{}
	for _, mod := range got.Modules {
		gotModules[mod.Name] = true
	}
	if !gotModules["phantom"] || !gotModules["phantom-autowalk"] {
		t.Errorf("entitled modules missing: %v", got.Modules)
	}
	if gotModules["phantom-mining"] {
		t.Errorf("unentitled module phantom-mining leaked: %v", got.Modules)
	}
	for _, n := range got.NativeComponents {
		if n.Name == "phantom-mining.dll" {
			t.Errorf("unentitled native phantom-mining.dll leaked: %v", got.NativeComponents)
		}
	}

	// Filtering changed the payload, so the signature MUST have been recomputed...
	if got.Signature == originalSig {
		t.Errorf("signature was not re-signed after filtering")
	}
	// ...and it must verify over exactly the canonical signed bytes of the filtered manifest.
	signed, err := json.Marshal(signedPayloadOf(got))
	if err != nil {
		t.Fatal(err)
	}
	pub := priv.Public().(ed25519.PublicKey)
	if !ccrypto.VerifyManifest(pub, signed, got.Signature) {
		t.Error("re-signed manifest signature does not verify over its filtered payload bytes")
	}
}

// InitOrder must be re-densified after filtering so the loader's ordered load has no gaps.
func TestScopeManifest_ReindexesInitOrder(t *testing.T) {
	priv := fixedSigningKey()
	m := manifestForChannel(t, "stable", priv)
	ent := &entitlement.Result{
		Authorized:     true,
		ContentChannel: "stable",
		EnabledModules: []string{"mining"}, // drops autowalk (which sat between core and mining)
	}
	got, err := scopeManifestForEntitlement(m, ent, priv)
	if err != nil {
		t.Fatalf("scopeManifestForEntitlement: %v", err)
	}
	for i, mod := range got.Modules {
		if mod.InitOrder != i {
			t.Errorf("InitOrder[%d] = %d, want %d (gaps after filtering)", i, mod.InitOrder, i)
		}
	}
}
