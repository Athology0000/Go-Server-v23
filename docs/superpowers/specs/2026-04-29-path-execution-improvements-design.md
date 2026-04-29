# Path Execution Improvements Design

**Date:** 2026-04-29
**Status:** Approved

## Problem

The pathfinder gets stuck too frequently on complex terrain (height changes, tight corridors, step-ups). Two root causes:

1. **Blind movement** — `computeActiveAction` never returns `JUMP`/`SPRINT_JUMP`, so the fast-exit in `shouldAssistJump` never fires. Jump assist always relies on the slower lookahead scan, which triggers too late on approaching step-ups. Sprint and strafe are applied uniformly regardless of headroom or corridor width.

2. **Slow recovery** — the coarse stuck check fires every 60 ticks (3 s). The collision replan only fires on `horizontalCollision`, missing vertical stalls (player walking into a step face without triggering horizontal collision reliably). When replanning, no avoid penalty is added, so the new path hits the same obstacle.

## Approach

### A: Flag-aware action selection (`NativePathfinder.kt`, `PathCommand.kt`)

The C++ side already encodes per-node flags into `cachedKeyNodeFlags` (parallel to `cachedPathNodes`). These are currently unused in execution. Wire them in:

**`computeActiveAction` changes:**
- Read `cachedKeyNodeFlags[cursor]`
- If `FLAG_STEP_UP_NEXT` (bit 5) is set → return `ActionType.SPRINT_JUMP`
- AOTV distance check runs first and takes priority as before
- `ActionType.JUMP` and `ActionType.SPRINT_JUMP` are already handled by `shouldAssistJump`'s fast-exit (`if activeAction == SPRINT_JUMP return true`), so this change causes jump to fire a tick earlier — before the player walks into the step face rather than after

**`NativePathfinder.tick()` PathCommand construction:**
- Read flags at `pathNodeCursor` before building the command
- If `FLAG_LOW_HEADROOM` (bit 2) or `FLAG_TIGHT_CORRIDOR` (bit 7) → `sprint = false`
- If `FLAG_TIGHT_CORRIDOR` → `forwardOnly = true` (suppresses strafe via `usesStrafeAdjustment()`)

Flag bit constants are defined in `path_annotations.cpp` and need to be mirrored in `NativePathfinder.kt` as private constants.

### B: Faster recovery (`NativePathfinder.kt`)

**1. Reduce coarse stuck interval**
- `STUCK_CHECK_INTERVAL`: 60 → 20 ticks (1 s)
- Threshold unchanged at 0.5 blocks

**2. Vertical stall detection**
- New check every 15 ticks when `state == EXECUTING`
- If the nearest upcoming node has `FLAG_STEP_UP_NEXT` and player Y has not increased ≥ 0.2 blocks since the last vertical check → trigger replan
- Tracks `lastVerticalCheckPos: Vec3` and `verticalStallTicks: Int` (reset to 0 on each vertical check and on `stop()`/`onLevelChange()`)
- Does not fire during `REPLANNING` to avoid cascading replans

**3. Transient avoid penalties on replan**
- `stuckPositions: ArrayDeque<BlockPos>` (max 4 entries, oldest dropped when full)
- When `startReplan` fires, add `BlockPos.containing(playerPos)` to `stuckPositions`
- Clear `stuckPositions` when a new path executes cleanly for 30 ticks without triggering a replan
- Pass to `submitSearch` as `avoidMeta` / `avoidPenalty`: each stuck position encodes as a quintuple `[x, y, z, radiusSq=4, maxYDiff=2]` in `avoidMeta` and a corresponding `25.0` in `avoidPenalty`
- `stuckPositions` reset to empty in `stop()` and `onLevelChange()`

## File Changes

| File | Changes |
|------|---------|
| `NativePathfinder.kt` | Flag constants, updated `computeActiveAction`, flag reads in `tick()`, stuck interval, vertical stall detector, avoid penalty on replan |
| `PathCommand.kt` | No changes needed — existing flag fields (`forwardOnly`, `sprint`) already drive the right behavior |

## What Does Not Change

- `PathfinderRotationStrategy.kt` — rotation smoothing is unrelated
- C++ pathfinder — `avoidMeta`/`avoidPenalty` interface already exists
- `shouldAssistJump` lookahead logic — kept as fallback for cases where flags are absent
- `COLLISION_REPLAN_TICKS` / `COLLISION_STUCK_THRESHOLD` — collision replan is complementary, not replaced
