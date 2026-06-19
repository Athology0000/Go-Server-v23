package entitlement

import (
	"sort"
	"testing"
	"time"
)

func TestIsFullAccessTier(t *testing.T) {
	cases := map[string]bool{
		"pro":        true,
		"lifetime":   true,
		"PRO":        true,
		" Lifetime ": true,
		"basic":      false,
		"trial":      false,
		"":           false,
		"premium":    false,
	}
	for tier, want := range cases {
		if got := isFullAccessTier(tier); got != want {
			t.Errorf("isFullAccessTier(%q) = %v, want %v", tier, got, want)
		}
	}
}

func TestApplyOverride(t *testing.T) {
	tests := []struct {
		name              string
		base, add, remove []string
		want              []string
	}{
		{"add and remove", []string{"a", "b"}, []string{"c"}, []string{"b"}, []string{"a", "c"}},
		{"duplicate add dedups", []string{"a"}, []string{"a", "b"}, nil, []string{"a", "b"}},
		{"remove nonexistent is noop", []string{"a"}, nil, []string{"x"}, []string{"a"}},
		{"remove all yields empty", []string{"a", "b"}, nil, []string{"a", "b"}, []string{}},
		{"empty base with add", nil, []string{"a"}, nil, []string{"a"}},
		{"all nil yields empty", nil, nil, nil, []string{}},
		{"remove wins over add conflict", []string{"a"}, []string{"b"}, []string{"b"}, []string{"a"}},
	}
	for _, tc := range tests {
		t.Run(tc.name, func(t *testing.T) {
			got := applyOverride(tc.base, tc.add, tc.remove)
			sort.Strings(got)
			want := append([]string(nil), tc.want...)
			sort.Strings(want)
			if !equalStrings(got, want) {
				t.Errorf("applyOverride(%v, %v, %v) = %v, want %v", tc.base, tc.add, tc.remove, got, want)
			}
		})
	}
}

func TestFullAccessResult(t *testing.T) {
	exp := time.Now().Add(24 * time.Hour)
	r := fullAccessResult("pro", "beta", &exp)
	if !r.Authorized {
		t.Fatal("full access must be authorized")
	}
	for _, lst := range [][]string{r.EnabledModules, r.EnabledFeatures, r.NativeComponents} {
		if len(lst) != 1 || lst[0] != "*" {
			t.Fatalf("full access lists must be [\"*\"], got %v", lst)
		}
	}
	if r.PlanTier != "pro" {
		t.Errorf("plan tier = %q, want pro", r.PlanTier)
	}
	if r.ContentChannel != "beta" {
		t.Errorf("content channel = %q, want beta", r.ContentChannel)
	}
	if r.EntitlementExpiresAt == nil || !r.EntitlementExpiresAt.Equal(exp) {
		t.Error("expiry must be passed through unchanged")
	}
}

func TestFullAccessResultDefaultsAndTrimsChannel(t *testing.T) {
	if r := fullAccessResult("lifetime", "", nil); r.ContentChannel != "stable" {
		t.Errorf("empty channel must default to stable, got %q", r.ContentChannel)
	}
	if r := fullAccessResult("lifetime", "  spaced  ", nil); r.ContentChannel != "spaced" {
		t.Errorf("channel must be trimmed, got %q", r.ContentChannel)
	}
}

func TestExpandDependencies(t *testing.T) {
	deps := map[string][]string{
		"commission": {"combat", "mining"},
		"combat":     {"rotation"}, // transitive chain
		"loopA":      {"loopB"},    // cycle guard
		"loopB":      {"loopA"},
	}
	cases := []struct {
		name string
		in   []string
		want []string
	}{
		{"single dependent pulls deps", []string{"commission"}, []string{"commission", "combat", "rotation", "mining"}},
		{"transitive chain", []string{"combat"}, []string{"combat", "rotation"}},
		{"unknown module is a no-op", []string{"autowalk"}, []string{"autowalk"}},
		{"cycle terminates", []string{"loopA"}, []string{"loopA", "loopB"}},
		{"wildcard unchanged", []string{"*"}, []string{"*"}},
		{"dedup across inputs", []string{"commission", "combat"}, []string{"commission", "combat", "rotation", "mining"}},
	}
	for _, tc := range cases {
		t.Run(tc.name, func(t *testing.T) {
			got := ExpandDependencies(deps, tc.in)
			if !equalStrings(got, tc.want) {
				t.Errorf("ExpandDependencies(%v) = %v, want %v", tc.in, got, tc.want)
			}
		})
	}
}

func equalStrings(a, b []string) bool {
	if len(a) != len(b) {
		return false
	}
	for i := range a {
		if a[i] != b[i] {
			return false
		}
	}
	return true
}
