# Mining Macro Targeting Fixes Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Fix three targeting bugs in `MiningMacroModule`: mining fires before the crosshair reaches the target block, target selection uses block-center angles instead of visible-face angles, and precision aim point / speed scale never activates.

**Architecture:** All three changes are isolated edits inside `MiningMacroModule.kt`. Fix 1 replaces the `canClick` mining gate with a per-tick crosshair check against all vein blocks. Fix 2 refactors the `bestInRange` selection loop to use the actual visible-face point angle. Fix 3 adds a `hitResult.location` fallback in `resolveMiningAimPoint` so the precision rotation scale fires from the first tick the crosshair lands on a block.

**Tech Stack:** Kotlin, Fabric Minecraft mod (MC 1.21.1), Mojang mappings. Build: `./gradlew build`. No unit test framework — compile success + in-game verification.

---

## File Map

| File | What changes |
|------|-------------|
| `src/main/kotlin/org/cobalt/internal/mining/MiningMacroModule.kt` | All three fixes — ~25 lines changed/added |

---

### Task 1: Add `angularDistanceTo(Player, Vec3)` overload

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/mining/MiningMacroModule.kt:1755`

The existing `angularDistanceTo(player, BlockPos)` is at line 1745 and hard-codes `+ 0.5` to get block center. Add an overload that takes an arbitrary `Vec3` so Fix 2 can pass the actual visible face point.

- [ ] **Insert the Vec3 overload immediately after the existing `angularDistanceTo` function (after line 1755):**

```kotlin
  private fun angularDistanceTo(player: Player, point: Vec3): Float {
    val dx = point.x - player.x
    val dy = point.y - player.eyeY
    val dz = point.z - player.z
    val targetYaw = Math.toDegrees(kotlin.math.atan2(-dx, dz)).toFloat()
    val horizDist = sqrt(dx * dx + dz * dz)
    val targetPitch = Math.toDegrees(kotlin.math.atan2(-dy, horizDist)).toFloat()
    val yawDelta = abs(AngleUtils.getRotationDelta(player.yRot, targetYaw))
    val pitchDelta = abs(targetPitch - player.xRot)
    return sqrt(yawDelta * yawDelta + pitchDelta * pitchDelta)
  }
```

- [ ] **Build to confirm compilation:**

```
./gradlew build
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Commit:**

```bash
git add src/main/kotlin/org/cobalt/internal/mining/MiningMacroModule.kt
git commit -m "feat: add angularDistanceTo(Vec3) overload for face-point angle computation"
```

---

### Task 2: Fix `selectMineTarget` to use visible-face angle (Fix 2)

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/mining/MiningMacroModule.kt:965-978`

The `bestInRange` loop currently calls `hasLineOfSight` (which internally calls `findVisibleAimPoint` and discards the result) then calls `angularDistanceTo` to the block center — two issues: redundant work, and wrong angle. Replace with a single `findVisibleAimPoint` call whose result is both the LOS gate and the aim point for angle computation.

- [ ] **Replace lines 965–978 with:**

```kotlin
    val rangeSq = mineRange.value * mineRange.value
    val eye = player.eyePosition

    // For in-range blocks, prefer the one whose visible face is closest to the
    // player's current aim. Using the actual face point (not block center) gives
    // the real rotation cost and avoids picking blocks that require more turn than
    // their center angle implies.
    var bestInRange: BlockPos? = null
    var bestAngle = Float.POSITIVE_INFINITY
    for (pos in vein.blocks) {
      if (!isMineableTarget(level, player, pos, vein.targetIds)) continue
      if (distanceToBlockSq(player, pos) > rangeSq) continue
      val visiblePoint: Vec3? = if (REQUIRE_MINE_LOS) findVisibleAimPoint(level, player, eye, pos) else null
      if (REQUIRE_MINE_LOS && visiblePoint == null) continue
      val aimPoint = visiblePoint ?: Vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
      val ang = angularDistanceTo(player, aimPoint)
      if (ang < bestAngle) { bestAngle = ang; bestInRange = pos }
    }
    if (bestInRange != null) return bestInRange
```

Note: the `val eye = player.eyePosition` line must be added — it doesn't exist in this scope yet. `Vec3` is already imported at the top of the file.

- [ ] **Build:**

```
./gradlew build
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Commit:**

```bash
git add src/main/kotlin/org/cobalt/internal/mining/MiningMacroModule.kt
git commit -m "fix: use visible face angle in bestInRange target selection, not block center"
```

---

### Task 3: Crosshair-first mining gate (Fix 1)

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/mining/MiningMacroModule.kt:580-612`

Replace the `if (inRange)` block's `canClick = hasLos || isCrosshairOnTarget(target)` logic. The new logic: check if `mc.hitResult` is on **any** in-range vein block each tick. If yes, mine that block (pivoting `currentTarget`). If no, release attack and keep rotating. This means the camera sweeps naturally and mines whatever mithril it lands on rather than firing dig packets at bedrock.

- [ ] **Replace the entire `if (inRange) { ... }` block (lines 580–612) with:**

```kotlin
    if (inRange) {
      // Crosshair-first mining: mine whatever valid vein block the crosshair is on.
      // This prevents dig packets firing at bedrock while the camera is still rotating.
      val hitPos = (mc.hitResult as? BlockHitResult)
        ?.takeIf { it.type == HitResult.Type.BLOCK }?.blockPos
      val crosshairVeinBlock = hitPos?.takeIf { pos ->
        vein.blocks.contains(pos) &&
          isMineableTarget(level, player, pos, vein.targetIds) &&
          distanceToBlockSq(player, pos) <= mineRange.value * mineRange.value
      }

      if (crosshairVeinBlock != null) {
        currentTarget = crosshairVeinBlock
        maybeRefreshLantern(level, player)
        if (startedPath && nativeActive()) {
          nativeStop()
        }
        startedPath = false
        lastPathTarget = null
        resetApproachTracking()
        MovementManager.clearForcedMovement()
        startMining(player, crosshairVeinBlock)
      } else {
        // Not on a vein block yet — release attack and keep rotating toward selected target.
        if (miningActive) {
          mc.options.keyAttack?.setDown(false)
          miningActive = false
        }
        val aim = resolveMiningAimPoint(player, target)
        val precisionRotScale =
          if (aim.usesPrecisionPoint) (precisionPointRotationSpeed.value / 100.0).coerceAtLeast(0.1)
          else 1.0
        frameRotSnapThreshold = if (aim.usesPrecisionPoint) 2.4f else 5.5f
        frameRotTarget     = aim.point
        frameRotSpeedScale = (RotationsModule.sample(RotationsModule.miningSpeedScale.value) * precisionRotScale).toFloat()
        frameRotAccelScale = (RotationsModule.sample(RotationsModule.miningAccelScale.value) * precisionRotScale).toFloat()
        frameRotPitchStep  = (RotationsModule.sample(RotationsModule.miningPitchStep.value) * precisionRotScale).toFloat()
        frameRotMaxSpeed   = (RotationsModule.sample(RotationsModule.miningMaxSpeed.value) * precisionRotScale).toFloat()
        frameRotMaxAccel   = (RotationsModule.sample(RotationsModule.miningMaxAccel.value) * precisionRotScale).toFloat()
      }
    }
```

- [ ] **Build:**

```
./gradlew build
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Commit:**

```bash
git add src/main/kotlin/org/cobalt/internal/mining/MiningMacroModule.kt
git commit -m "fix: crosshair-first mining — only attack when crosshair is on a vein block"
```

---

### Task 4: Hit-result fallback in `resolveMiningAimPoint` (Fix 3)

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/mining/MiningMacroModule.kt:1929-1945`

When `precisionActive` is true but the tracker has no samples yet (confidence < 2), `resolveMiningAimPoint` currently falls straight through to the generic `findVisibleAimPoint`. Add a fallback between the tracker check and the generic fallback: if the crosshair is currently on the target block, use `hitResult.location` (the exact sub-block face coordinate) as the precision aim point. This fires immediately on tick 1 of mining so `precisionRotScale` and `frameRotSnapThreshold = 2.4f` apply from the start.

- [ ] **Replace `resolveMiningAimPoint` (lines 1929–1945) with:**

```kotlin
  private fun resolveMiningAimPoint(player: Player, target: BlockPos, checkPrecisionChance: Boolean = false): AimTarget {
    val level = mc.level
    val eye = player.eyePosition
    val usePrecision = if (checkPrecisionChance) shouldUsePrecisionPoint(target)
                       else MiningModule.precisionActive.value
    if (level != null && usePrecision) {
      MiningPrecisionTracker.getPrecisionPointFor(target)?.let { point ->
        if (canSeeAimPoint(level, player, eye, point, target)) {
          return AimTarget(point, true)
        }
      }
      // Tracker has no data yet — use the exact face hit point from the crosshair raycast.
      // This fires immediately when the crosshair lands on the block so the precision
      // rotation scale applies from the very first tick of mining.
      val hit = mc.hitResult
      if (hit is BlockHitResult && hit.type == HitResult.Type.BLOCK && hit.blockPos == target) {
        val loc = hit.location
        if (canSeeAimPoint(level, player, eye, loc, target)) {
          return AimTarget(loc, true)
        }
      }
    }
    if (level != null) {
      findVisibleAimPoint(level, player, eye, target)?.let { return AimTarget(it, false) }
    }
    return AimTarget(Vec3(target.x + 0.5, target.y + 0.5, target.z + 0.5), false)
  }
```

- [ ] **Build:**

```
./gradlew build
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Commit:**

```bash
git add src/main/kotlin/org/cobalt/internal/mining/MiningMacroModule.kt
git commit -m "fix: use hitResult.location as precision aim fallback before tracker accumulates samples"
```

---

### Task 5: In-game verification

- [ ] **Run the client:**

```
./gradlew runClient
```

- [ ] **Enable Mining Macro, go to a mithril vein in the Crystal Hollows / Dwarven Mines, observe:**
  - Camera sweeps smoothly and mines the first mithril block the crosshair lands on (no bedrock dig attempts)
  - Target selection rotates toward the nearest visible-face block, not through bedrock
  - If `Precision Active` is checked in Mining module, the snap threshold and rotation speed apply from tick 1

- [ ] **If `Precision Active` is on, confirm `Mining HUD` shows "Precision: Ready" quickly after the crosshair lands on a block** (tracker still needs particles for its own data, but the hit-result fallback means rotation behaviour changes immediately)
