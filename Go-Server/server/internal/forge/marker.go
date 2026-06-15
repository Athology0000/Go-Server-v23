// Package forge turns a delivered raw .jar + locally-built .dll into a production-ready,
// watermarked, approval-gated build. This file implements the .dll build-marker: a signed
// trailer appended to the native library so a leaked .dll traces back to its build.
package forge

import (
	"crypto/hmac"
	"crypto/sha256"
	"encoding/binary"
)

// markerMagic identifies a Phantom dll build-marker trailer. Versioned so the format can
// evolve without misreading old markers.
var markerMagic = []byte("PHANTMK1")

const (
	markerHMACLen   = sha256.Size // 32
	markerLenField  = 4           // uint32 total trailer length, at the very end
	markerIDLenSize = 2           // uint16 build-id length
	// fixed overhead = magic(8) + idLen(2) + hmac(32) + lenField(4)
	markerFixedOverhead = len("PHANTMK1") + markerIDLenSize + markerHMACLen + markerLenField
)

// MarkDLL returns dll with a signed build-marker trailer appended. The original bytes are
// left byte-for-byte intact (the marker is strictly appended); PE loaders map a DLL from its
// header-declared image size and ignore trailing bytes, so the library still loads.
//
// Layout of the appended trailer:
//
//	magic(8) || buildIDLen(uint16 BE) || buildID || HMAC-SHA256(secret, dll||buildID)(32) || totalLen(uint32 BE)
func MarkDLL(dll []byte, buildID, secret string) []byte {
	id := []byte(buildID)
	mac := markerHMAC(dll, id, secret)

	total := markerFixedOverhead + len(id)
	out := make([]byte, 0, len(dll)+total)
	out = append(out, dll...)
	out = append(out, markerMagic...)
	out = binary.BigEndian.AppendUint16(out, uint16(len(id)))
	out = append(out, id...)
	out = append(out, mac...)
	out = binary.BigEndian.AppendUint32(out, uint32(total))
	return out
}

// ExtractDLLMarker recovers the build id from a marked dll, verifying the HMAC with secret.
// ok is false (and buildID empty) when no valid, correctly-signed marker is present —
// including the wrong-secret case, so a forged or unsigned marker cannot be passed off.
func ExtractDLLMarker(dll []byte, secret string) (buildID string, ok bool) {
	if len(dll) < markerFixedOverhead {
		return "", false
	}
	total := int(binary.BigEndian.Uint32(dll[len(dll)-markerLenField:]))
	if total < markerFixedOverhead || total > len(dll) {
		return "", false
	}
	boundary := len(dll) - total
	body := dll[:boundary]
	trailer := dll[boundary:]

	if !hmac.Equal(trailer[:len(markerMagic)], markerMagic) {
		return "", false
	}
	pos := len(markerMagic)
	idLen := int(binary.BigEndian.Uint16(trailer[pos : pos+markerIDLenSize]))
	pos += markerIDLenSize
	// idLen must leave exactly room for id + hmac + lenField.
	if idLen < 0 || pos+idLen+markerHMACLen+markerLenField != total {
		return "", false
	}
	id := trailer[pos : pos+idLen]
	pos += idLen
	gotMAC := trailer[pos : pos+markerHMACLen]

	wantMAC := markerHMAC(body, id, secret)
	if !hmac.Equal(gotMAC, wantMAC) {
		return "", false
	}
	return string(id), true
}

func markerHMAC(body, id []byte, secret string) []byte {
	h := hmac.New(sha256.New, []byte(secret))
	h.Write(body)
	h.Write(id)
	return h.Sum(nil)
}
