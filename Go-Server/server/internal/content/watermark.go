package content

import (
	"context"
	"fmt"
	"os"
	"os/exec"
	"path/filepath"
	"strings"
)

// Watermarker stamps a per-user, HMAC-signed identifier into a .jar by invoking the
// watermark-capable obfuscator's watermark stage. The heavy obfuscation is done once to
// produce the base jar; this only embeds the per-download mark, so a leaked client jar can
// be traced back to the account that downloaded it (extractable via --extract-watermark).
type Watermarker struct {
	JavaPath   string // java executable, e.g. "java"
	ObfJar     string // path to the obfuscator jar (must support --watermark-id/--extract-watermark)
	ConfigPath string // watermark-only obfuscator config json
	Secret     string // HMAC secret used to sign the embedded mark
}

// Enabled reports whether the watermarker is fully configured.
func (w *Watermarker) Enabled() bool {
	return w != nil && w.ObfJar != "" && w.ConfigPath != "" && w.Secret != ""
}

func (w *Watermarker) java() string {
	if w.JavaPath != "" {
		return w.JavaPath
	}
	return "java"
}

// Apply returns jarBytes watermarked with watermarkID. The obfuscator runs on a private temp
// dir (deleted on return); the caller decides whether to fail the download or serve the
// un-watermarked bytes on error.
func (w *Watermarker) Apply(ctx context.Context, jarBytes []byte, watermarkID string) ([]byte, error) {
	if !w.Enabled() {
		return nil, fmt.Errorf("watermarker not configured")
	}
	if watermarkID == "" {
		return nil, fmt.Errorf("empty watermark id")
	}

	dir, err := os.MkdirTemp("", "wm-")
	if err != nil {
		return nil, err
	}
	defer os.RemoveAll(dir)

	inJar := filepath.Join(dir, "in.jar")
	outJar := filepath.Join(dir, "out.jar")
	inDll := filepath.Join(dir, "in.dll")
	outDll := filepath.Join(dir, "out.dll")
	if err := os.WriteFile(inJar, jarBytes, 0o600); err != nil {
		return nil, err
	}
	// The obfuscator CLI takes positional in/out DLLs even when native mode is disabled.
	if err := os.WriteFile(inDll, []byte{0}, 0o600); err != nil {
		return nil, err
	}

	cmd := exec.CommandContext(ctx, w.java(), "-jar", w.ObfJar,
		"--config", w.ConfigPath,
		"--watermark-id", watermarkID,
		"--watermark-secret", w.Secret,
		inJar, inDll, outJar, outDll,
	)
	if out, err := cmd.CombinedOutput(); err != nil {
		return nil, fmt.Errorf("watermark obfuscator failed: %w: %s", err, strings.TrimSpace(string(out)))
	}

	return os.ReadFile(outJar)
}

// ExtractWatermark recovers the embedded id from a watermarked jar (verifying the HMAC with
// Secret). Returns the id, or an error if no valid watermark is present. Used for verification.
func (w *Watermarker) ExtractWatermark(ctx context.Context, jarBytes []byte) (string, error) {
	dir, err := os.MkdirTemp("", "wm-extract-")
	if err != nil {
		return "", err
	}
	defer os.RemoveAll(dir)

	jarPath := filepath.Join(dir, "check.jar")
	if err := os.WriteFile(jarPath, jarBytes, 0o600); err != nil {
		return "", err
	}

	cmd := exec.CommandContext(ctx, w.java(), "-jar", w.ObfJar, "--extract-watermark", jarPath, w.Secret)
	out, err := cmd.CombinedOutput()
	if err != nil {
		return "", fmt.Errorf("no valid watermark: %w: %s", err, strings.TrimSpace(string(out)))
	}
	return strings.TrimSpace(string(out)), nil
}
