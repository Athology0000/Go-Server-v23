package forge

import (
	"bytes"
	"testing"
)

// a stand-in "dll" body; the marker logic is content-agnostic.
func sampleDLL() []byte {
	b := make([]byte, 4096)
	for i := range b {
		b[i] = byte(i * 7)
	}
	return b
}

func TestMarkDLLRoundTrip(t *testing.T) {
	dll := sampleDLL()
	const id = "build-abc123"
	const secret = "forge-secret"

	marked := MarkDLL(dll, id, secret)

	got, ok := ExtractDLLMarker(marked, secret)
	if !ok {
		t.Fatal("expected a valid marker, got none")
	}
	if got != id {
		t.Fatalf("build id = %q, want %q", got, id)
	}
}

func TestMarkDLLPreservesOriginalBytes(t *testing.T) {
	dll := sampleDLL()
	marked := MarkDLL(dll, "build-x", "s")
	if !bytes.Equal(marked[:len(dll)], dll) {
		t.Fatal("original dll bytes must be left intact (marker is strictly appended)")
	}
	if len(marked) <= len(dll) {
		t.Fatal("marked dll should be larger than the original")
	}
}

func TestExtractWrongSecretRejected(t *testing.T) {
	dll := sampleDLL()
	marked := MarkDLL(dll, "build-x", "right-secret")

	if _, ok := ExtractDLLMarker(marked, "wrong-secret"); ok {
		t.Fatal("extraction with the wrong secret must fail")
	}
}

func TestExtractTamperedBodyRejected(t *testing.T) {
	dll := sampleDLL()
	marked := MarkDLL(dll, "build-x", "s")
	// flip a byte in the dll body — HMAC covers the body, so this must fail verification.
	marked[10] ^= 0xFF

	if _, ok := ExtractDLLMarker(marked, "s"); ok {
		t.Fatal("a tampered dll body must fail marker verification")
	}
}

func TestExtractUnmarkedRejected(t *testing.T) {
	if _, ok := ExtractDLLMarker(sampleDLL(), "s"); ok {
		t.Fatal("an unmarked dll must not yield a marker")
	}
	if _, ok := ExtractDLLMarker(nil, "s"); ok {
		t.Fatal("nil must not yield a marker")
	}
	if _, ok := ExtractDLLMarker([]byte{1, 2, 3}, "s"); ok {
		t.Fatal("a tiny buffer must not yield a marker")
	}
}

func TestMarkDLLEmptyBuildID(t *testing.T) {
	// empty id is still a deterministic, verifiable marker (caller is expected to pass a
	// real id; this just shouldn't panic or misparse).
	marked := MarkDLL(sampleDLL(), "", "s")
	got, ok := ExtractDLLMarker(marked, "s")
	if !ok || got != "" {
		t.Fatalf("empty-id round trip failed: ok=%v got=%q", ok, got)
	}
}
