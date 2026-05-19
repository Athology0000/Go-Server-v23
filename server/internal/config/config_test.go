package config

import "testing"

func TestNormalizeCORSOriginsStripsPathsAndDedupes(t *testing.T) {
	got, err := normalizeCORSOrigins("CORS_ALLOW_ORIGINS", " https://go-server-v23.vercel.app/login,https://go-server-v23.vercel.app, http://localhost:5173/dashboard?tab=users ")
	if err != nil {
		t.Fatalf("normalizeCORSOrigins returned error: %v", err)
	}

	want := "https://go-server-v23.vercel.app,http://localhost:5173"
	if got != want {
		t.Fatalf("normalizeCORSOrigins = %q, want %q", got, want)
	}
}

func TestNormalizeCORSOriginsRejectsMalformedOrigin(t *testing.T) {
	if _, err := normalizeCORSOrigins("CORS_ALLOW_ORIGINS", "go-server-v23.vercel.app/login"); err == nil {
		t.Fatal("normalizeCORSOrigins returned nil error for malformed origin")
	}
}
