package forge

import (
	"context"
	"crypto/rand"
	"crypto/sha256"
	"encoding/hex"
	"fmt"
	"os"
	"os/exec"
	"path/filepath"
	"strings"
)

// Forge runs the production obfuscation + build-watermark pipeline on a delivered payload.
// The jar is obfuscated and watermarked by the obfuscator (aggressive passes + watermark
// stage); the locally-built .dll is bound to the build with a signed marker (see marker.go).
// The obfuscator runs on a private temp dir; finished artifacts land in StagingDir/<buildID>/
// awaiting superadmin approval before promotion.
type Forge struct {
	JavaPath   string // java executable, e.g. "java"
	ObfJar     string // path to the obfuscator jar (aggressive passes + watermark)
	ConfigPath string // forge-prod obfuscator config (proven MC-loadable pass set)
	Secret     string // HMAC secret for the jar watermark + dll marker
	StagingDir string // root for staged builds (NOT route-reachable)
}

// BuildResult describes a staged, unapproved build.
type BuildResult struct {
	BuildID   string
	Module    string
	JarPath   string
	JarSHA256 string
	DLLPath   string
	DLLSHA256 string
}

// Enabled reports whether the forge is fully configured.
func (f *Forge) Enabled() bool {
	return f != nil && f.ObfJar != "" && f.ConfigPath != "" && f.Secret != "" && f.StagingDir != ""
}

func (f *Forge) java() string {
	if f.JavaPath != "" {
		return f.JavaPath
	}
	return "java"
}

// NewBuildID returns a fresh, unique build id (also used as the jar watermark id and the dll
// marker id, so a leaked artifact traces back to this build).
func NewBuildID() (string, error) {
	b := make([]byte, 8)
	if _, err := rand.Read(b); err != nil {
		return "", err
	}
	return "build-" + hex.EncodeToString(b), nil
}

// Build obfuscates+watermarks jarBytes, marks dllBytes, and stages both under
// StagingDir/<buildID>/. It does not touch the live content dir — promotion is a separate,
// approval-gated step. buildID must be non-empty (use NewBuildID).
func (f *Forge) Build(ctx context.Context, module, buildID string, jarBytes, dllBytes []byte) (*BuildResult, error) {
	if !f.Enabled() {
		return nil, fmt.Errorf("forge not configured")
	}
	if strings.TrimSpace(buildID) == "" {
		return nil, fmt.Errorf("empty build id")
	}
	safeMod := safeModuleName(module)
	if safeMod == "" {
		return nil, fmt.Errorf("invalid module name %q", module)
	}
	if len(jarBytes) == 0 {
		return nil, fmt.Errorf("empty jar payload")
	}
	if len(dllBytes) == 0 {
		return nil, fmt.Errorf("empty dll payload")
	}

	work, err := os.MkdirTemp("", "forge-")
	if err != nil {
		return nil, err
	}
	defer os.RemoveAll(work)

	inJar := filepath.Join(work, "in.jar")
	outJar := filepath.Join(work, "out.jar")
	inDll := filepath.Join(work, "in.dll")
	outDll := filepath.Join(work, "out.dll")
	if err := os.WriteFile(inJar, jarBytes, 0o600); err != nil {
		return nil, err
	}
	// The obfuscator CLI takes a positional in/out .dll even when native mode is disabled. We
	// feed the delivered dll and ignore the obfuscator's out.dll — the shipped dll is the
	// delivered one after our own marker step below.
	if err := os.WriteFile(inDll, dllBytes, 0o600); err != nil {
		return nil, err
	}

	cmd := exec.CommandContext(ctx, f.java(), "-jar", f.ObfJar,
		"--config", f.ConfigPath,
		"--watermark-id", buildID,
		"--watermark-secret", f.Secret,
		inJar, inDll, outJar, outDll,
	)
	if out, err := cmd.CombinedOutput(); err != nil {
		return nil, fmt.Errorf("obfuscator failed: %w: %s", err, strings.TrimSpace(string(out)))
	}

	obfJar, err := os.ReadFile(outJar)
	if err != nil {
		return nil, fmt.Errorf("read obfuscated jar: %w", err)
	}

	// Bind the delivered dll to this build (embed marker; its hash is signed at promotion).
	markedDll := MarkDLL(dllBytes, buildID, f.Secret)

	// Stage under a fresh per-build dir.
	stageDir := filepath.Join(f.StagingDir, buildID)
	if err := os.MkdirAll(stageDir, 0o755); err != nil {
		return nil, err
	}
	jarPath := filepath.Join(stageDir, safeMod+".jar")
	dllPath := filepath.Join(stageDir, safeMod+".dll")
	if err := os.WriteFile(jarPath, obfJar, 0o644); err != nil {
		return nil, err
	}
	if err := os.WriteFile(dllPath, markedDll, 0o644); err != nil {
		return nil, err
	}

	return &BuildResult{
		BuildID:   buildID,
		Module:    safeMod,
		JarPath:   jarPath,
		JarSHA256: sha256Hex(obfJar),
		DLLPath:   dllPath,
		DLLSHA256: sha256Hex(markedDll),
	}, nil
}

// safeModuleName strips any path components and a trailing extension, leaving a filesystem-
// and route-safe module id (e.g. "phantom-autowalk").
func safeModuleName(name string) string {
	base := filepath.Base(strings.TrimSpace(name))
	if base == "." || base == string(filepath.Separator) {
		return ""
	}
	base = strings.TrimSuffix(base, filepath.Ext(base))
	if base == "" || base == ".." {
		return ""
	}
	return base
}

func sha256Hex(b []byte) string {
	sum := sha256.Sum256(b)
	return hex.EncodeToString(sum[:])
}
