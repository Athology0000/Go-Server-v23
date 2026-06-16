package content

import (
	"os"
	"path/filepath"
	"strings"
)

// LookupWatermark maps a recovered watermark id (the first-16-hex HMAC stamped into a leaked bundle)
// back to the license it was built for, using the build's watermark-map ledger written by
// buildModules: CONTENT_DIR/watermark-map.json, one "wmid=licenseId" line per (license, bundle) build.
func LookupWatermark(contentDir, wmid string) (string, error) {
	wmid = strings.TrimSpace(wmid)
	if wmid == "" {
		return "", ErrNotFound
	}
	raw, err := os.ReadFile(filepath.Join(contentDir, "watermark-map.json"))
	if err != nil {
		return "", ErrNotFound
	}
	for _, line := range strings.Split(string(raw), "\n") {
		line = strings.TrimSpace(line)
		if line == "" || strings.HasPrefix(line, "#") {
			continue
		}
		idx := strings.Index(line, "=")
		if idx < 0 {
			continue
		}
		if strings.TrimSpace(line[:idx]) == wmid {
			return strings.TrimSpace(line[idx+1:]), nil
		}
	}
	return "", ErrNotFound
}
