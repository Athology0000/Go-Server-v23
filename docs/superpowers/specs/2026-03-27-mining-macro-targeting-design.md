# Mining Macro Targeting — Design Spec
**Date:** 2026-03-27

## Problem Summary

Three bugs in `MiningMacroModule` targeting:

1. **Mining fires before crosshair reaches target.** `canClick = hasLos || isCrosshairOnTarget(target)` triggers `startMining` as soon as a line-of-sight path *exists*, not when the crosshair is actually on the block. At low rotation speeds the crosshair is still sweeping over bedrock when attack goes down, sending dig packets to the wrong block.

2. **Target selection uses block center angle, not visible face angle.** `angularDistanceTo` computes angle to `(pos + 0.5, pos + 0.5, pos + 0.5)`. The actual aim point returned by `resolveMiningAimPoint` → `findVisibleAimPoint` is offset toward the eye. A block whose center is at 8° may require 14° of real rotation; a block whose center is at 12° may only need 9°. Selection picks the wrong block.

3. **Precision point never used; speed scale never fires.** `resolveMiningAimPoint` only returns `usesPrecisionPoint = true` once `MiningPrecisionTracker` has `confidence ≥ 2`. Because Bug 2 causes target switching, the tracker never accumulates enough samples. `precisionRotScale` stays 1.0 and the `precisionPointRotationSpeed` setting has no effect.

## Design

### Fix 1 — Crosshair-first mining (replaces `canClick` logic)

**Where:** `onTick`, inside the `if (inRange)` branch.

Replace:
```
val hasLos = hasLineOfSight(...)
val canClick = hasLos || isCrosshairOnTarget(target)
if (canClick) { startMining(player, target) }
else { rotate toward aim point }
```

With:
```
val hitPos = (mc.hitResult as? BlockHitResult)
    ?.takeIf { it.type == BLOCK }?.blockPos

val crosshairVeinBlock = hitPos?.takeIf { pos ->
    vein.blocks.contains(pos)
    && isMineableTarget(level, player, pos, vein.targetIds)
    && distanceToBlockSq(player, pos) <= mineRange²
}

if (crosshairVeinBlock != null) {
    currentTarget = crosshairVeinBlock   // pivot to whatever we're looking at
    maybeRefreshLantern(level, player)
    clearPathState()
    startMining(player, crosshairVeinBlock)
} else {
    stopAttackKey()
    rotateToward(resolveMiningAimPoint(player, target))
}
```

- Attack key is held **only** when the crosshair is confirmed on a valid vein block.
- As the camera sweeps, the first mithril block it lands on gets mined — no more bedrock packets.
- `currentTarget` pivots to the swept block; the overall target selection keeps rotating toward the vein.

`hasLineOfSight` is no longer used as a mining gate (it is still used in `selectMineTarget` for deciding which block to rotate toward).

### Fix 2 — Visible-face angular distance in `selectMineTarget`

**Where:** `selectMineTarget`, the `bestInRange` loop.

Current loop calls `hasLineOfSight` (which internally calls `findVisibleAimPoint` and discards the point) then calls `angularDistanceTo` to the block center. Two problems: double work, and the center angle is wrong.

Change:
```kotlin
// Before the loop
val eye = player.eyePosition

// Inside the loop — single call, cache the result
val visiblePoint: Vec3? = if (REQUIRE_MINE_LOS)
    findVisibleAimPoint(level, player, eye, pos) else null
if (REQUIRE_MINE_LOS && visiblePoint == null) continue

val aimPoint = visiblePoint ?: Vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
val ang = angularDistanceTo(player, aimPoint)   // angle to actual face, not center
if (ang < bestAngle) { bestAngle = ang; bestInRange = pos }
```

Add a `angularDistanceTo(player: Player, point: Vec3): Float` overload that accepts an arbitrary `Vec3` instead of computing center from `BlockPos`.

This removes the redundant `hasLineOfSight` call (replaced by inline `findVisibleAimPoint`) and uses the real rotation cost for selection.

### Fix 3 — Hit-result fallback in `resolveMiningAimPoint`

**Where:** `resolveMiningAimPoint`.

After the tracker check (which may return null if confidence < 2), before the generic `findVisibleAimPoint` fallback, add:

```kotlin
// Fallback: use the exact face hit point from the current crosshair raycast
if (level != null && usePrecision) {
    val hit = mc.hitResult
    if (hit is BlockHitResult && hit.type == BLOCK && hit.blockPos == target) {
        val loc = hit.location
        if (canSeeAimPoint(level, player, eye, loc, target)) {
            return AimTarget(loc, usesPrecisionPoint = true)
        }
    }
}
```

- `hitResult.location` is the exact sub-block face coordinate Minecraft computed for the crosshair raycast — a precise point on the block surface.
- This fires as soon as the crosshair lands on the block (before any particles), so `precisionRotScale` applies from the very first tick of mining.
- `usesPrecisionPoint = true` is only set when `precisionActive` is true, keeping the gate consistent.

## Affected Files

| File | Change |
|------|--------|
| `MiningMacroModule.kt` | Fix 1: replace `canClick` block; Fix 2: refactor `bestInRange` loop + add `angularDistanceTo(Vec3)` overload; Fix 3: add hit-result fallback in `resolveMiningAimPoint` |

No other files change.

## Non-Goals

- Rotation path avoidance (don't try to steer around bedrock during sweeping — Fix 1 makes this irrelevant).
- Changes to the precision particle tracker logic.
- Changes to out-of-range warp/walk behavior.
