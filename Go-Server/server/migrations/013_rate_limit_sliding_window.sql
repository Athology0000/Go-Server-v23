-- Sliding-window-counter rate limiting (L1): the fixed-window limiter anchored its window to
-- the first request, so a caller could fire `limit` requests at the end of one window and
-- `limit` more at the start of the next — up to a 2x burst across the boundary. Track the
-- current sub-window's start plus the current/previous bucket counts so a straddling burst is
-- counted against both buckets. `count` is retained (still written = curr_count) so the NOT NULL
-- column stays satisfied and the expires_at sweeper (= window_start + 2*window) is unchanged.
ALTER TABLE rate_limits ADD COLUMN IF NOT EXISTS window_start TIMESTAMPTZ NOT NULL DEFAULT now();
ALTER TABLE rate_limits ADD COLUMN IF NOT EXISTS curr_count   INTEGER     NOT NULL DEFAULT 0;
ALTER TABLE rate_limits ADD COLUMN IF NOT EXISTS prev_count   INTEGER     NOT NULL DEFAULT 0;
