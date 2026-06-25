package cache

import (
	"testing"
	"time"
)

// TestSlidingWindowAllowed pins the pure sliding-window-counter decision: the previous
// sub-window's count is weighted by the fraction of it still inside the trailing window,
// so a burst that straddles the sub-window boundary is counted against BOTH buckets.
// The "boundary burst is BLOCKED" case is the L1 fix: fixed-window would have allowed it.
func TestSlidingWindowAllowed(t *testing.T) {
	const min = time.Minute
	cases := []struct {
		name          string
		prevCount     int
		currCount     int
		limit         int
		elapsed       time.Duration
		window        time.Duration
		wantAllowed   bool
		wantEstimated int
	}{
		{"fresh window under limit", 0, 4, 10, 0, min, true, 4},
		{"fresh window at limit boundary", 0, 10, 10, 0, min, true, 10},
		{"fresh window over limit", 0, 11, 10, 0, min, false, 11},
		{"boundary burst is BLOCKED", 10, 10, 10, 0, min, false, 20},
		{"half-decayed prev still counts", 10, 0, 10, 30 * time.Second, min, true, 5},
		{"prev fully decayed at window end", 10, 10, 10, min, min, true, 10},
		{"ceil rounds a partial bucket up", 3, 9, 10, 30 * time.Second, min, false, 11},
		{"zero window degenerates safely", 5, 5, 10, 0, 0, true, 5},
	}
	for _, c := range cases {
		t.Run(c.name, func(t *testing.T) {
			gotAllowed, gotEstimated := slidingWindowAllowed(c.prevCount, c.currCount, c.limit, c.elapsed, c.window)
			if gotAllowed != c.wantAllowed || gotEstimated != c.wantEstimated {
				t.Fatalf("slidingWindowAllowed(prev=%d,curr=%d,limit=%d,elapsed=%s,window=%s) = (%v,%d); want (%v,%d)",
					c.prevCount, c.currCount, c.limit, c.elapsed, c.window, gotAllowed, gotEstimated, c.wantAllowed, c.wantEstimated)
			}
		})
	}
}
