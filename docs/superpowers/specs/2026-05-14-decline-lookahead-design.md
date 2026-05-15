# Walkable-Decline Lookahead Design

**Date:** 2026-05-14
**Scope:** `src/main/kotlin/org/phantom/api/pathfinder/jni/PathExecutorState.kt`
**Type:** Targeted behavioral fix (no API changes)

## Problem

When the executor descends a gradual slope built from alternating block / slab / block (or any descending terrain that a player can hold W across without jumping), the rotation lookahead point collapses to ~1.25 blocks ahead of the player instead of staying on the far keynode. This makes the camera duck toward the player's feet on long downhills, which looks unnatural and produces sloppy steering through descending corridors.

## Root Cause

`PathExecutorState.hasDropAhead(spline, distance=3.5, threshold=0.45)` returns `true` if **any** sample within 3.5 blocks ahead of the player drops at least 0.45 blocks vertically below the start sample. Blockâ†’slabâ†’block descents drop 0.5 blocks per horizontal block, so the very first scan sample (0.75 blocks ahead) already crosses the threshold and the function returns `true`.

`update()` then forces:

```kotlin
dropAhead || safety.ledgeRisk || safety.corridorUnsafe -> PRECISION_LOOKAHEAD
```

inside `safetyLookaheadCap` (PathExecutorState.kt:506), clamping `effectiveLookahead` to `PRECISION_LOOKAHEAD = 1.25`. The aim collapses to a point ~1 block ahead.

The intent of `dropAhead` was to slow the executor at one-shot ledges, not on sustained walkable declines.

## Design

### Classifier: distinguish walkable decline from cliff

Replace `hasDropAhead`'s boolean return with a richer classification used at the single existing call site. The classifier produces one of three states:

| State | Meaning |
|-------|---------|
| `NONE` | No drop within scan window |
| `WALKABLE_DECLINE` | Drop is distributed across the scan window with no single steep step |
| `CLIFF` | Single large drop (a ledge to step off) |

**Walkable decline rule (must satisfy all):**
- Total vertical drop across the scan window â‰¥ `DROP_HEIGHT_THRESHOLD` (0.45) â€” i.e. the old "drop ahead" still fires
- AND for every adjacent sample pair, `prev.y - next.y â‰¤ MAX_STEP_PER_SAMPLE` (â‰ˆ 0.55 â€” blockâ†’slab is 0.5 so this fits with a small margin)
- AND drop per horizontal block over the window â‰¤ `MAX_WALKABLE_SLOPE_RATIO` (0.6) â€” generous enough for block-slab-block (~0.5), rejects 1-block-per-1-block stairs (1.0)

Anything else that crosses the threshold is `CLIFF`.

### Lookahead behavior change

In `update()` (the spline path), when computing `safetyLookaheadCap`:

```kotlin
safetyLookaheadCap = when {
    descentState == CLIFF || safety.ledgeRisk || safety.corridorUnsafe -> PRECISION_LOOKAHEAD
    // descentState == WALKABLE_DECLINE falls through to the adaptive lookahead.
    safety.turnAhead || pathCurvature >= TURN_BRAKE_CURVATURE -> { ... unchanged ... }
    else -> adaptiveLookahead
}
```

`getAdaptiveLookaheadDistance(eye, spline, dropAhead)` currently shrinks the smoothed lookahead to `PRECISION_LOOKAHEAD` when `dropAhead` is true (line 1010, 1018). It must receive `false` for the walkable-decline case so the smoothed lookahead can stay near `lookaheadDistanceFar`.

### What does NOT change

- **Movement layer:** sneak, sprint, jump, and stuck-detection logic are untouched. The pre-emptive sprint brake (curvature) and sneak hysteresis already correctly behave on slopes.
- **`assessSplineSafety`:** already bails out of edge/corridor scans when the spline itself descends past `FOOTPRINT_DROP_DEPTH = 1.25`. This change keeps that intact.
- **JNI/native pathfinder:** no changes.
- **Public settings (`lookaheadDistanceFar`, `lookaheadShrinkStrength`, `lookaheadVelocityBoost`):** no changes; the user's existing tuning still applies.

### New constants

In the constants block (PathExecutorState.kt:170-242):

```kotlin
private const val MAX_STEP_PER_SAMPLE = 0.55       // blockâ†’slab = 0.5; tolerates float noise
private const val MAX_WALKABLE_SLOPE_RATIO = 0.6   // vertical drop / horizontal distance
```

## Files Touched

- `src/main/kotlin/org/phantom/api/pathfinder/jni/PathExecutorState.kt`
  - Add `DescentState` enum (private)
  - Add `classifyDescent()` private method
  - Remove `hasDropAhead` (only one call site, at line 450)
  - Modify `update()` site to consume the classifier â€” `dropAhead` boolean is replaced by `descentState`, and the existing `dropAhead` parameter passed to `getAdaptiveLookaheadDistance` becomes `descentState == CLIFF`

## Testing

Manual in-game:
1. **Block-slab-block descending corridor** â€” verify the lookahead spline marker (visible via the existing debug rendering) stays near the far keynode instead of collapsing to the player's feet. Camera should track the end of the corridor smoothly.
2. **Single 2-block cliff drop** â€” verify precision still engages (rotation pulls in, sneak engages as before).
3. **45Â° "staircase" of full blocks** â€” should classify as `CLIFF` (drop ratio = 1.0 > 0.6) and behave as today.
4. **Flat terrain** â€” no behavior change.

No unit tests; the project has none.

## Risks

- Misclassifying a sustained 1-block-per-block stair as `WALKABLE_DECLINE` would let the lookahead stay far while the player needed precision. The 0.6 slope-ratio bound forbids this.
- A path that goes up a slab then off a cliff within the 3.5-block scan could in theory present a low average slope. The per-sample `MAX_STEP_PER_SAMPLE` guard catches this â€” a single 1+ block step forces `CLIFF`.
