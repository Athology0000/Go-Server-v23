package content

import "time"

type AddonManifest struct {
	BuildID   string        `json:"build_id"`
	Channel   string        `json:"channel"`
	ExpiresAt time.Time     `json:"expires_at"`
	Addons    []ManifestAddon `json:"addons"`
}

type ManifestAddon struct {
	ID       string `json:"id"`
	URL      string `json:"url"`
	SHA256   string `json:"sha256"`
	Required bool   `json:"required"`
}