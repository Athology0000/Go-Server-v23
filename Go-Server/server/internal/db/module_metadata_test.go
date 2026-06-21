package db

import (
	"errors"
	"strings"
	"testing"
)

// Valid bare entitlement ids (the enabled_modules / depends_on namespace) are accepted.
func TestValidateModuleMetadata_AcceptsValidBareIds(t *testing.T) {
	if err := ValidateModuleMetadata("commission", []string{"combat", "mining"}); err != nil {
		t.Fatalf("valid metadata rejected: %v", err)
	}
	for _, name := range []string{"a", "auto-walk", "kuudra_2", "x0", strings.Repeat("z", 64)} {
		if err := ValidateModuleMetadata(name, nil); err != nil {
			t.Errorf("valid module_name %q rejected: %v", name, err)
		}
	}
}

// Any id outside ^[a-z0-9_-]{1,64}$ is rejected with ErrInvalidModuleMetadata. The control-byte cases
// are the ones that previously bricked the channel: Go json.Marshal escapes 0x08/0x0c as \b/\f, which
// the client canonicalizers must match exactly; constraining ids here removes that divergence surface.
func TestValidateModuleMetadata_RejectsBadIds(t *testing.T) {
	cases := []struct {
		desc string
		name string
		deps []string
	}{
		{"backspace in dep (0x08 -> escaper brick)", "commission", []string{"comb\x08at"}},
		{"formfeed in dep (0x0c -> escaper brick)", "commission", []string{"min\x0cing"}},
		{"newline in dep", "commission", []string{"a\nb"}},
		{"tab in dep", "commission", []string{"a\tb"}},
		{"html char in dep", "commission", []string{"a<b"}},
		{"ampersand in dep", "commission", []string{"a&b"}},
		{"quote in dep", "commission", []string{"a\"b"}},
		{"backslash in dep", "commission", []string{"a\\b"}},
		{"unicode in dep", "commission", []string{"café"}},
		{"uppercase in dep", "commission", []string{"Combat"}},
		{"space in dep", "commission", []string{"comb at"}},
		{"empty dep", "commission", []string{""}},
		{"dot in dep", "commission", []string{"a.b"}},
		{"bad module_name", "comm ission", []string{"combat"}},
		{"empty module_name", "", []string{"combat"}},
		{"self dependency", "commission", []string{"commission"}},
		{"duplicate dep", "commission", []string{"combat", "combat"}},
	}
	for _, tc := range cases {
		err := ValidateModuleMetadata(tc.name, tc.deps)
		if err == nil {
			t.Errorf("%s: expected rejection, got nil", tc.desc)
			continue
		}
		if !errors.Is(err, ErrInvalidModuleMetadata) {
			t.Errorf("%s: expected ErrInvalidModuleMetadata, got %v", tc.desc, err)
		}
	}
}

func TestValidateModuleMetadata_RejectsOverlongId(t *testing.T) {
	long := strings.Repeat("a", 65)
	if err := ValidateModuleMetadata(long, nil); !errors.Is(err, ErrInvalidModuleMetadata) {
		t.Errorf("overlong module_name should be rejected, got %v", err)
	}
	if err := ValidateModuleMetadata("ok", []string{long}); !errors.Is(err, ErrInvalidModuleMetadata) {
		t.Errorf("overlong dep should be rejected, got %v", err)
	}
}
