package content

import (
	"context"
	"crypto/sha256"
	"encoding/base64"
	"encoding/hex"
	"errors"
	"fmt"
	"net/url"
	"os"
	"path/filepath"
	"sort"
	"strings"
	"time"

	ccrypto "github.com/phantom/server/internal/crypto"
	"github.com/phantom/server/internal/db"
)

type signedManifestPayload struct {
	BuildID          string              `json:"build_id"`
	Channel          string              `json:"channel"`
	MinLoaderVersion string              `json:"minimum_loader_version"`
	ModuleKey        string              `json:"module_key,omitempty"`
	Modules          []db.ManifestModule `json:"modules"`
	NativeComponents []db.ManifestNative `json:"native_components"`
	// expires_at_millis + epoch are INSIDE the signature so a MITM can neither extend a manifest's
	// expiry nor roll a client back to an older bundle (a lower epoch) without breaking the Ed25519
	// signature. No omitempty: a zero must still be emitted so the signed bytes reproduce identically
	// in the Java + C++ verifiers. revoked is deliberately NOT signed — its signed value is always
	// false (the server never re-signs on revoke), so revocation is enforced server-side and by the
	// heartbeat lease instead.
	ExpiresAtMillis int64 `json:"expires_at_millis"`
	Epoch           int64 `json:"epoch"`
}

// signedPayloadOf projects a ContentManifest onto the exact subset that is Ed25519-signed.
func signedPayloadOf(m *db.ContentManifest) signedManifestPayload {
	return signedManifestPayload{
		BuildID:          m.BuildID,
		Channel:          m.Channel,
		MinLoaderVersion: m.MinLoaderVersion,
		ModuleKey:        m.ModuleKey,
		Modules:          m.Modules,
		NativeComponents: m.NativeComponents,
		ExpiresAtMillis:  m.ExpiresAtMillis,
		Epoch:            m.Epoch,
	}
}

// SignManifest signs the canonical signed-payload subset of m. It is the single source of truth
// for the signed byte contract so the stable-filesystem and admin manifest paths can never drift
// to different signed bytes (which would make one of them fail verification on the client).
func SignManifest(signingKey []byte, m *db.ContentManifest) (string, error) {
	return ccrypto.SignManifest(signingKey, signedPayloadOf(m))
}

// effectiveModulesDir returns the per-license content subtree (CONTENT_DIR/modules/<licenseId>) when
// it exists, otherwise the shared CONTENT_DIR/modules. Per-license bundles are watermarked uniquely,
// so a client served from its own subtree gets bundles (and manifest hashes) distinct from everyone
// else's; a license with no subtree falls back to the shared build.
func effectiveModulesDir(contentDir, licenseID string) string {
	shared := filepath.Join(contentDir, "modules")
	if licenseID == "" {
		return shared
	}
	perLicense := filepath.Join(shared, licenseID)
	if info, err := os.Stat(perLicense); err == nil && info.IsDir() {
		return perLicense
	}
	return shared
}

// moduleDeps maps a bare module id to the bare ids it depends on (from module_metadata); it stamps
// each manifest module's DependsOn so the signed manifest carries the dependency for client
// enforcement. Pass nil to omit (modules then serialize byte-identically to the pre-deps format).
func BuildStableManifest(_ context.Context, contentDir, licenseID, baseURL, channel string, signingKey, moduleKey []byte, enabledModules []string, moduleDeps map[string][]string) (*db.ContentManifest, error) {
	modulesDir := effectiveModulesDir(contentDir, licenseID)
	entries, err := os.ReadDir(modulesDir)
	if err != nil {
		return nil, err
	}

	baseURL = strings.TrimRight(baseURL, "/")
	if channel == "" {
		channel = "stable"
	}

	modules := make([]db.ManifestModule, 0)
	var maxModuleMtimeMillis int64
	for _, entry := range entries {
		if entry.IsDir() || !isPhantomModuleArtifact(entry.Name()) {
			continue
		}

		// Entitlement filter: a bundle the account cannot download must not
		// appear in its manifest, or the loader 403s mid-load and locks out.
		if !ModuleAllowed(entry.Name(), enabledModules) {
			continue
		}

		// Newest served-artifact mtime becomes the manifest epoch (rollback ordering key).
		if info, infoErr := entry.Info(); infoErr == nil {
			if mm := info.ModTime().UnixMilli(); mm > maxModuleMtimeMillis {
				maxModuleMtimeMillis = mm
			}
		}

		jarPath := filepath.Join(modulesDir, entry.Name())
		raw, err := os.ReadFile(jarPath)
		if err != nil {
			return nil, err
		}

		// The loader verifies sha256 over the bytes it downloads — the artifact
		// exactly as served — and only then decrypts it in the native. So the
		// manifest hash must cover the stored bytes (ciphertext for .enc), not the
		// decrypted plaintext. We still confirm each .enc decrypts with the
		// configured module key, so a wrong MODULE_ENCRYPTION_KEY fails loudly here
		// instead of shipping a bundle every client would reject.
		if strings.EqualFold(filepath.Ext(jarPath), ".enc") {
			if _, decErr := ccrypto.DecryptAESGCM(moduleKey, raw); decErr != nil {
				return nil, fmt.Errorf("module %s does not decrypt with the configured module key: %w", entry.Name(), decErr)
			}
		}

		hash := sha256Hex(raw)
		moduleName := strings.TrimSuffix(entry.Name(), filepath.Ext(entry.Name()))
		modules = append(modules, db.ManifestModule{
			Name:      moduleName,
			URL:       baseURL + "/content/module/" + url.PathEscape(moduleName),
			SHA256:    hash,
			Required:  IsCoreModule(entry.Name()),
			DependsOn: moduleDeps[EntitlementID(entry.Name())],
		})
	}

	sort.SliceStable(modules, func(i, j int) bool {
		return modules[i].Name < modules[j].Name
	})

	for i := range modules {
		modules[i].InitOrder = i
	}

	// In this deployment model the framework core ships INSIDE the client jar, so there is no
	// server-delivered core bundle — modules resolve their framework superclasses from the jar
	// (parent classloader). We therefore no longer require a Required/core module; we only fail an
	// empty content dir (zero modules), so a genuine misdeploy still surfaces instead of signing a
	// manifest that delivers nothing. (Entitlement filtering can narrow per account, but the
	// unfiltered filesystem always has ≥1 module when correctly deployed.)
	if len(modules) == 0 {
		return nil, ErrNotFound
	}

	natives, err := buildNativeManifest(contentDir, baseURL)
	if err != nil {
		return nil, err
	}

	// One expiry instant, signed as epoch-millis AND served as the time.Time, so the client verifies
	// the exact integer it reconstructs rather than re-deriving it from an RFC3339 string.
	expiresAt := time.Now().Add(15 * time.Minute)
	manifest := &db.ContentManifest{
		ID:               "stable",
		BuildID:          "stable-filesystem",
		Channel:          channel,
		MinLoaderVersion: "1",
		ModuleKey:        base64.StdEncoding.EncodeToString(moduleKey),
		Modules:          modules,
		NativeComponents: natives,
		ExpiresAt:        expiresAt,
		ExpiresAtMillis:  expiresAt.UnixMilli(),
		// Monotonic per-channel ordering key: the newest content mtime. Stable for unchanged
		// content (re-serve → same epoch ≥ client high-water-mark → accepted), and it rises as
		// content is redeployed. Serving an older bundle yields a lower epoch the native rejects.
		Epoch:     maxModuleMtimeMillis,
		Revoked:   false,
		CreatedAt: time.Now(),
	}

	signature, err := SignManifest(signingKey, manifest)
	if err != nil {
		return nil, err
	}
	manifest.Signature = signature
	return manifest, nil
}

func buildNativeManifest(contentDir, baseURL string) ([]db.ManifestNative, error) {
	nativeDir := filepath.Join(contentDir, "native")
	entries, err := os.ReadDir(nativeDir)
	if err != nil {
		if errors.Is(err, os.ErrNotExist) {
			return []db.ManifestNative{}, nil
		}
		return nil, err
	}

	natives := make([]db.ManifestNative, 0)
	for _, entry := range entries {
		if entry.IsDir() || !isNativeArtifact(entry.Name()) {
			continue
		}

		path := filepath.Join(nativeDir, entry.Name())
		bytes, err := os.ReadFile(path)
		if err != nil {
			return nil, err
		}

		natives = append(natives, db.ManifestNative{
			Name:     entry.Name(),
			URL:      baseURL + "/content/native/" + url.PathEscape(entry.Name()),
			SHA256:   sha256Hex(bytes),
			Required: true,
		})
	}

	sort.SliceStable(natives, func(i, j int) bool {
		return natives[i].Name < natives[j].Name
	})

	return natives, nil
}

func NormalizeModuleName(name string) string {
	safeName := filepath.Base(strings.TrimSpace(name))
	return strings.TrimSuffix(safeName, filepath.Ext(safeName))
}

func moduleFilePath(modulesDir, name string) (string, error) {
	moduleName := NormalizeModuleName(name)
	if moduleName == "" || moduleName == "." {
		return "", ErrNotFound
	}

	path := filepath.Join(modulesDir, moduleName+".jar")
	if _, err := os.Stat(path); err == nil {
		return path, nil
	} else if !errors.Is(err, os.ErrNotExist) {
		return "", err
	}

	encPath := filepath.Join(modulesDir, moduleName+".enc")
	if _, encErr := os.Stat(encPath); encErr == nil {
		return encPath, nil
	} else if !errors.Is(encErr, os.ErrNotExist) {
		return "", encErr
	}

	return "", ErrNotFound
}

func isModuleArtifact(name string) bool {
	ext := strings.ToLower(filepath.Ext(name))
	return ext == ".jar" || ext == ".enc"
}

func isPhantomModuleArtifact(name string) bool {
	return strings.HasPrefix(strings.ToLower(name), "phantom") && isModuleArtifact(name)
}

func isNativeArtifact(name string) bool {
	ext := strings.ToLower(filepath.Ext(name))
	return ext == ".dll" || ext == ".so" || ext == ".dylib"
}

func sha256Hex(data []byte) string {
	sum := sha256.Sum256(data)
	return hex.EncodeToString(sum[:])
}
