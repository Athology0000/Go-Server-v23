# Hybrid Etherwarp Detour Shortcut Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** When `goto` walking A* returns a path that is a large detour relative to the straight-line distance, and an etherwarp item is available, plan a dedicated multi-hop etherwarp chain to the destination and execute it instead of walking the detour.

**Architecture:** Keep the existing walking A* exactly as-is. In the background search task, after the walking result returns, measure detour ratio (walk length vs. straight-line). If it qualifies, run the already-existing-but-orphaned native `findEtherwarpPath` chain planner toward the destination support block. If a chain is found, hand it to `tick()` which runs a new dedicated etherwarp-chain execution mode (aim at each hop, sneak+use to fire, advance per hop, then fall back to a normal short walking search for any residual approach). Any failure at any point cleanly falls back to the normal walking path.

**Tech Stack:** Kotlin (Fabric mod, MC 1.21.11), existing C++ JNI pathfinder (`phantom_pathfinder.dll`, no rebuild required — `findEtherwarpPath` already exported and working).

**Testing note:** This project has no unit-test framework (per `CLAUDE.md`: "There are no unit tests in this project."). Per the writing-plans skill's instruction-priority rule, the user's project reality overrides the TDD template. Verification for each task is: (a) `./gradlew build` compiles, and (b) the explicit in-game manual test described in the task. Do not fabricate a test harness.

---

## File Structure

- `src/main/kotlin/org/phantom/internal/pathfinding/PathTeleportConfig.kt` — add three tunables for the hybrid trigger. Single responsibility: teleport config holder (unchanged role).
- `src/main/kotlin/org/phantom/internal/etherwarp/EtherwarpLogic.kt` — add `findEtherwarpChain(...)` returning the full native chain result (the existing `findEtherwarpAngles` truncates to the first hop and cannot be reused). Single responsibility: etherwarp domain logic (unchanged role).
- `src/main/kotlin/org/phantom/api/pathfinder/jni/NativePathfinder.kt` — all integration: capture eye/range at submit, detour detection + chain search in the background task, new chain-execution state + `tickEtherwarpChain()`, and clean fallback. This is where the path lifecycle already lives.

No native (C++) changes. No new files.

---

## Background facts the implementer must know

- `goto` → `PathfindingModule.setTargetOnly` → `PathfindingModule.setTarget` (just stores text fields) **and** `NativePathfinder.setTarget(x,y,z)` → `setTargetWithRadius` → `startSearch()`.
- `startSearch()` runs on the client thread (it calls `Minecraft.getInstance().player` and `DebugLog.status` synchronously). It calls `submitSearch(...)` which does `searchExecutor.submit { ... }` (single background thread named `phantom-pathfinder`).
- The background task currently calls `NativePathfinderJNI.findPath(...)`, and on success sets `searchResult`; on failure sets `searchFailed`. `tick()` (client tick) consumes those: `applySearchResult(result)` or transitions to `FAILED`.
- `NativePathfinderJNI.findEtherwarpPath(...)` returns `NativeEtherwarpResult(path: IntArray /* flat x,y,z landing blocks, hop order */, angles: FloatArray /* flat yaw,pitch per point */, ...)` or `null`. It reads the persistent native `g_worldState` (no buffer arg) and shares `g_cancelSearch` with `findPath`, so it MUST be called sequentially after `findPath` in the same background task — never concurrently.
- The native chain planner requires the goal to be a valid etherwarp **landing/support block** (a solid block with two passable blocks above). The player "feet" goal is `(goalX, goalY, goalZ)`; the support block to aim at is `(goalX, goalY - 1, goalZ)` (mirrors `selectV5TeleportCandidate`'s `aimBlock = node.y - 1`). If the support block is not landable, the native call returns `null` → we keep the walking path. This is the safe fallback and requires no extra handling.
- Firing an etherwarp is already implemented: `fireTeleport(isEtherwarp = true, aotvSlot)` (private in `NativePathfinder`) holds the hotbar slot, presses sneak+use for 1 tick, restores slot after 4 ticks. It is gated by `EtherwarpLogic.tryConsumeTeleportUseThisTick()` (one teleport per game tick globally).
- `ActionType.ETHERWARP` already exists and is what `PathCommand.activeAction` uses for teleport aiming elsewhere.
- Eye position / range must be read on the client thread (not the background thread). Capture them in `startSearch()`/`startReplan()` and thread them through `submitSearch`.

---

### Task 1: Add hybrid-trigger tunables

**Files:**
- Modify: `src/main/kotlin/org/phantom/internal/pathfinding/PathTeleportConfig.kt`

- [ ] **Step 1: Add the three tunables**

Replace the whole object body so it reads exactly:

```kotlin
package org.phantom.internal.pathfinding

object PathTeleportConfig {
  var v5EtherwarpEnabled: Boolean = true
  var v5AotvEnabled: Boolean = true
  var minTeleportGain: Double = 6.0
  var finalNoTeleportRadius: Double = 4.0
  var maxLookAheadNodes: Int = 24
  var teleportCooldownTicks: Int = 1
  var teleportStraightnessTolerance: Double = 1.8

  // Hybrid etherwarp-chain shortcut (goto only). When a walking path is a big
  // detour relative to the straight-line distance to the goal, try a dedicated
  // multi-hop etherwarp chain instead.
  var chainShortcutEnabled: Boolean = true
  // Minimum straight-line distance (blocks) from start to goal before a chain
  // is even considered. Keeps short hops on the normal walker and prevents the
  // post-chain residual approach from re-triggering a chain.
  var chainMinStraightLine: Double = 14.0
  // Walk-path length must exceed straightLine * this ratio to count as a detour.
  var chainDetourRatio: Double = 1.6
}
```

- [ ] **Step 2: Verify build**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/pathfinding/PathTeleportConfig.kt
git commit -m "feat: add hybrid etherwarp-chain shortcut tunables"
```

---

### Task 2: Add `findEtherwarpChain` to EtherwarpLogic

**Files:**
- Modify: `src/main/kotlin/org/phantom/internal/etherwarp/EtherwarpLogic.kt` (add a function next to `findEtherwarpAngles`, around line 177)

Rationale: `findEtherwarpAngles` returns only the first hop's `(yaw,pitch)` and cannot express a multi-hop chain. We need the full `NativeEtherwarpResult`. Parameters are passed explicitly (no `mc.player` access) so it is safe to call from the background search thread.

- [ ] **Step 1: Add the function**

Insert immediately after the closing `}` of `findEtherwarpAngles` (after current line 177, before `fun findEtherwarpHotbarSlot()`):

```kotlin
  /**
   * Full multi-hop etherwarp chain to [supportGoal] (the SOLID support block to
   * land on top of, i.e. destination-feet minus one Y). Explicit eye origin so
   * this is safe to call off the client thread. Returns the native result
   * (path = flat x,y,z landing blocks in hop order; angles = flat yaw,pitch per
   * point) or null if no chain exists / goal is not etherwarp-landable.
   */
  fun findEtherwarpChain(
    supportGoal: BlockPos,
    eyeX: Double,
    eyeY: Double,
    eyeZ: Double,
    rayLength: Double,
  ): org.phantom.api.pathfinder.jni.NativeEtherwarpResult? {
    val result = org.phantom.api.pathfinder.jni.NativePathfinderJNI.findEtherwarpPath(
      goalX = supportGoal.x, goalY = supportGoal.y, goalZ = supportGoal.z,
      startEyeX = eyeX, startEyeY = eyeY, startEyeZ = eyeZ,
      maxIterations = 50_000,
      threadCount = 1,
      yawStep = 0.5,
      pitchStep = 0.5,
      newNodeCost = 1.0,
      heuristicWeight = 1.0,
      rayLength = rayLength,
      rewireEpsilon = 0.01,
      eyeHeight = 1.62,
    ) ?: return null
    // Need at least one landing point (3 ints) and its angle pair (2 floats).
    if (result.path.size < 3 || result.angles.size < 2) return null
    return result
  }
```

- [ ] **Step 2: Verify build**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/etherwarp/EtherwarpLogic.kt
git commit -m "feat: add findEtherwarpChain returning full native chain result"
```

---

### Task 3: Add chain-plan state and capture eye/range at submit

**Files:**
- Modify: `src/main/kotlin/org/phantom/api/pathfinder/jni/NativePathfinder.kt`

This task only adds state + plumbing. No behavior change yet (chain is searched but not executed until Task 4/5).

- [ ] **Step 1: Add the chain-plan data class and volatile result slot**

In `NativePathfinder.kt`, find the existing block (around lines 111-114):

```kotlin
    @Volatile private var searchResult: NativePathResult? = null
    @Volatile private var searchFailed: Boolean = false
    @Volatile private var lastSearchSummary: String = ""
    private var searchFuture: Future<*>? = null
```

Replace it with:

```kotlin
    @Volatile private var searchResult: NativePathResult? = null
    @Volatile private var searchFailed: Boolean = false
    @Volatile private var lastSearchSummary: String = ""
    private var searchFuture: Future<*>? = null

    private data class EtherwarpChainPlan(
        val points: List<Vec3>,
        val angles: FloatArray,
    )

    @Volatile private var etherwarpChainResult: EtherwarpChainPlan? = null

    // Chain-execution mode state (consumed in tick()).
    private var usingEtherwarpChain: Boolean = false
    private var chainPoints: List<Vec3> = emptyList()
    private var chainAngles: FloatArray = FloatArray(0)
    private var chainCursor: Int = 0
    private var chainHopWaitTicks: Int = 0
    private var chainStartPos: Vec3 = Vec3.ZERO

    // Captured on the client thread at submit time, read on the bg thread.
    private var pendingEyeX: Double = 0.0
    private var pendingEyeY: Double = 0.0
    private var pendingEyeZ: Double = 0.0
    private var pendingEwRange: Double = 0.0
    private var pendingEwAvailable: Boolean = false
    private var pendingChainEligible: Boolean = false
```

- [ ] **Step 2: Add chain tuning constants**

Find the constant block; immediately after this existing line (around line 208):

```kotlin
    private const val TELEPORT_NEED_LOOKAHEAD_NODES = 7
```

add:

```kotlin
    // Hybrid etherwarp-chain shortcut.
    private const val CHAIN_HOP_ALIGN_YAW = 3.0f
    private const val CHAIN_HOP_ALIGN_PITCH = 3.0f
    // After firing a hop, how many ticks to wait for the teleport to resolve
    // before deciding it failed and abandoning the chain to the walker.
    private const val CHAIN_HOP_WAIT_TICKS = 12
    // A hop is "reached" when the player is within this 3D distance of the
    // expected landing cell centre.
    private const val CHAIN_HOP_REACHED_DIST = 1.6
```

- [ ] **Step 3: Capture eye/range/eligibility in `startSearch()`**

Find `startSearch(isFly: Boolean = ...)` (around line 749). Locate, near its end, this existing block:

```kotlin
        val startY = player.blockY
        val goalYAdjusted = goalY

        submitSearch(
            starts = buildYPaddedCandidates(player.blockX, startY, player.blockZ, isFly),
            goals = buildYPaddedCandidates(goalX, goalYAdjusted, goalZ, isFly),
            isFly = isFly
        )
        return true
```

Replace it with:

```kotlin
        val startY = player.blockY
        val goalYAdjusted = goalY

        captureChainInputs(player, isFly)

        submitSearch(
            starts = buildYPaddedCandidates(player.blockX, startY, player.blockZ, isFly),
            goals = buildYPaddedCandidates(goalX, goalYAdjusted, goalZ, isFly),
            isFly = isFly
        )
        return true
```

- [ ] **Step 4: Capture eye/range/eligibility in `startReplan()`**

Find `startReplan(playerPos: Vec3, crawlRecovery: Boolean = false)` (around line 821). Locate this existing tail:

```kotlin
        val startPos = BlockPos.containing(playerPos)
        val isFly = routeProfile == MovementProfile.FLY
        submitSearch(
            starts = buildYPaddedCandidates(startPos.x, startPos.y, startPos.z, isFly),
            goals = buildYPaddedCandidates(goalX, goalY, goalZ, isFly),
            isFly = isFly
        )
    }
```

Replace it with:

```kotlin
        val startPos = BlockPos.containing(playerPos)
        val isFly = routeProfile == MovementProfile.FLY
        Minecraft.getInstance().player?.let { captureChainInputs(it, isFly) }
            ?: run { pendingChainEligible = false }
        submitSearch(
            starts = buildYPaddedCandidates(startPos.x, startPos.y, startPos.z, isFly),
            goals = buildYPaddedCandidates(goalX, goalY, goalZ, isFly),
            isFly = isFly
        )
    }
```

- [ ] **Step 5: Add the `captureChainInputs` helper**

Add this private function immediately above `private fun startRecovery(` (around line 809):

```kotlin
    // Reads client-thread-only state (player eye, etherwarp item/range) once at
    // submit time so the background task can decide a chain shortcut safely.
    private fun captureChainInputs(player: LocalPlayer, isFly: Boolean) {
        val eye = player.eyePosition
        pendingEyeX = eye.x
        pendingEyeY = eye.y
        pendingEyeZ = eye.z
        pendingEwRange = EtherwarpLogic.getEtherwarpRange().toDouble().coerceIn(8.0, 61.0)
        pendingEwAvailable = EtherwarpLogic.findEtherwarpHotbarSlot() >= 0
        pendingChainEligible =
            PathTeleportConfig.chainShortcutEnabled &&
            PathTeleportConfig.v5EtherwarpEnabled &&
            !isFly &&
            routeWaypoints.isEmpty() &&
            pendingEwAvailable
    }
```

(`LocalPlayer` and `EtherwarpLogic` are already imported in this file — confirm the imports at the top: `import net.minecraft.client.player.LocalPlayer` and `import org.phantom.internal.etherwarp.EtherwarpLogic` are present near lines 4 and 15.)

- [ ] **Step 6: Verify build**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL. (No behavior change yet — fields are set but unused.)

- [ ] **Step 7: Commit**

```bash
git add src/main/kotlin/org/phantom/api/pathfinder/jni/NativePathfinder.kt
git commit -m "feat: add etherwarp-chain plan state and submit-time input capture"
```

---

### Task 4: Detour detection + chain search in the background task

**Files:**
- Modify: `src/main/kotlin/org/phantom/api/pathfinder/jni/NativePathfinder.kt` (the `submitSearch` background closure, around lines 723-746)

- [ ] **Step 1: Compute detour and run the chain search after walking search succeeds**

Find this existing block inside `submitSearch` (around lines 723-746):

```kotlin
        searchFuture = searchExecutor.submit {
            try {
                val result = NativePathfinderJNI.findPath(
                    startPoints = flatStarts,
                    endPoints = flatGoals,
                    isFly = isFly,
                    maxIterations = MAX_ITERATIONS,
                    heuristicWeight = HEURISTIC_WEIGHT,
                    nonPrimaryStartPenalty = NON_PRIMARY_START_PENALTY,
                    moveOrderOffset = moveOrderOffset,
                    avoidMeta = avoidMeta,
                    avoidPenalty = avoidPenalty
                )
                if (result != null && result.keyPath.size >= 3) {
                    searchResult = result
                } else {
                    lastError = "findPath returned no result"
                    searchFailed = true
                }
            } catch (e: Exception) {
                lastError = e.message ?: "Native path search failed"
                searchFailed = true
            }
        }
```

Replace it with:

```kotlin
        val eyeX = pendingEyeX
        val eyeY = pendingEyeY
        val eyeZ = pendingEyeZ
        val ewRange = pendingEwRange
        val chainEligible = pendingChainEligible
        val gX = goalX
        val gY = goalY
        val gZ = goalZ
        searchFuture = searchExecutor.submit {
            try {
                val result = NativePathfinderJNI.findPath(
                    startPoints = flatStarts,
                    endPoints = flatGoals,
                    isFly = isFly,
                    maxIterations = MAX_ITERATIONS,
                    heuristicWeight = HEURISTIC_WEIGHT,
                    nonPrimaryStartPenalty = NON_PRIMARY_START_PENALTY,
                    moveOrderOffset = moveOrderOffset,
                    avoidMeta = avoidMeta,
                    avoidPenalty = avoidPenalty
                )
                if (result != null && result.keyPath.size >= 3) {
                    val chain = maybePlanEtherwarpChain(
                        result, chainEligible, eyeX, eyeY, eyeZ, ewRange, gX, gY, gZ
                    )
                    if (chain != null) {
                        etherwarpChainResult = chain
                    }
                    searchResult = result
                } else {
                    lastError = "findPath returned no result"
                    searchFailed = true
                }
            } catch (e: Exception) {
                lastError = e.message ?: "Native path search failed"
                searchFailed = true
            }
        }
```

- [ ] **Step 2: Add `maybePlanEtherwarpChain` + path-length helper**

Add these two private functions immediately above `private fun startSearch(` (around line 749):

```kotlin
    // Runs ONLY on the background search thread, sequentially AFTER findPath
    // (they share the native cancel flag and world state). Returns a chain plan
    // when the walking result is a big detour and a chain to the destination
    // support block exists; otherwise null (keep the walking path).
    private fun maybePlanEtherwarpChain(
        walkResult: NativePathResult,
        chainEligible: Boolean,
        eyeX: Double,
        eyeY: Double,
        eyeZ: Double,
        ewRange: Double,
        gX: Int,
        gY: Int,
        gZ: Int,
    ): EtherwarpChainPlan? {
        if (!chainEligible) return null

        val straightLine = run {
            val dx = (gX + 0.5) - eyeX
            val dy = gY.toDouble() - (eyeY - 1.62)
            val dz = (gZ + 0.5) - eyeZ
            sqrt(dx * dx + dy * dy + dz * dz)
        }
        if (straightLine < PathTeleportConfig.chainMinStraightLine) return null

        val walkLen = flatPathLength(walkResult.path)
        if (walkLen < straightLine * PathTeleportConfig.chainDetourRatio) return null

        // The native chain goal is the SOLID support block under the feet goal.
        val supportGoal = BlockPos(gX, gY - 1, gZ)
        val chain = EtherwarpLogic.findEtherwarpChain(
            supportGoal, eyeX, eyeY, eyeZ, ewRange
        ) ?: return null

        val pts = ArrayList<Vec3>(chain.path.size / 3)
        var i = 0
        while (i + 2 < chain.path.size) {
            pts.add(Vec3(chain.path[i].toDouble(), chain.path[i + 1].toDouble(), chain.path[i + 2].toDouble()))
            i += 3
        }
        if (pts.isEmpty()) return null

        DebugLog.status(
            Minecraft.getInstance(),
            "Pathfinding",
            "Pathfinder: detour ${formatBlocks(walkLen)} vs straight ${formatBlocks(straightLine)} " +
                "-> etherwarp chain (${pts.size} hops)"
        )
        return EtherwarpChainPlan(pts, chain.angles.copyOf())
    }

    private fun flatPathLength(flat: IntArray): Double {
        if (flat.size < 6) return 0.0
        var total = 0.0
        var i = 3
        var px = flat[0].toDouble()
        var py = flat[1].toDouble()
        var pz = flat[2].toDouble()
        while (i + 2 < flat.size) {
            val x = flat[i].toDouble()
            val y = flat[i + 1].toDouble()
            val z = flat[i + 2].toDouble()
            val dx = x - px
            val dy = y - py
            val dz = z - pz
            total += sqrt(dx * dx + dy * dy + dz * dz)
            px = x; py = y; pz = z
            i += 3
        }
        return total
    }
```

(`sqrt`, `BlockPos`, `Vec3`, `DebugLog`, `Minecraft`, `EtherwarpLogic`, `PathTeleportConfig` are all already imported/used in this file.)

- [ ] **Step 3: Verify build**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL. (Chain may now be searched and stored in `etherwarpChainResult`, but nothing consumes it yet — still pure walking behavior in-game.)

- [ ] **Step 4: Commit**

```bash
git add src/main/kotlin/org/phantom/api/pathfinder/jni/NativePathfinder.kt
git commit -m "feat: detect detours and plan an etherwarp chain in the bg search"
```

---

### Task 5: Execute the chain in tick(), with clean fallback

**Files:**
- Modify: `src/main/kotlin/org/phantom/api/pathfinder/jni/NativePathfinder.kt` (`tick()` consumption + result handling + `resetPathState`/`stop`/`onLevelChange`)

- [ ] **Step 1: Consume `etherwarpChainResult` and enter chain mode**

In `tick()`, find this existing block (around lines 362-369):

```kotlin
        } else {
            val result = searchResult
            if (result != null) {
                searchResult = null
                searchFuture = null
                applySearchResult(result)
            }
        }
```

Replace it with:

```kotlin
        } else {
            val chain = etherwarpChainResult
            if (chain != null) {
                etherwarpChainResult = null
                searchResult = null
                searchFuture = null
                applyEtherwarpChain(chain)
            } else {
                val result = searchResult
                if (result != null) {
                    searchResult = null
                    searchFuture = null
                    applySearchResult(result)
                }
            }
        }
```

- [ ] **Step 2: Run chain execution early in the EXECUTING flow**

In `tick()`, find this existing block (around lines 394-400):

```kotlin
        val nodes = cachedPathNodes
        if (nodes.isEmpty()) {
            state = PathStatus.FAILED
            releaseGuidedControl()
            lastTickStatus = state
            return null
        }
```

Insert immediately ABOVE it:

```kotlin
        if (usingEtherwarpChain) {
            val chainCmd = tickEtherwarpChain(player)
            lastTickStatus = state
            return chainCmd
        }

```

- [ ] **Step 3: Add `applyEtherwarpChain` and `tickEtherwarpChain`**

Add these private functions immediately above `private fun applySearchResult(` (around line 843):

```kotlin
    private fun applyEtherwarpChain(chain: EtherwarpChainPlan) {
        usingEtherwarpChain = true
        chainPoints = chain.points
        chainAngles = chain.angles
        chainCursor = 0
        chainHopWaitTicks = 0
        // Show the chain as the path so debug renderers have something sane.
        cachedFullPathNodes = chain.points
        cachedPathNodes = chain.points
        cachedPathFlags = IntArray(0)
        cachedKeyNodeFlags = IntArray(0)
        cachedKeyNodeMetrics = IntArray(0)
        lastPathSignature = "etherwarp-chain"
        pathNodeCursor = 0
        teleportFiredNodeKey = -1L
        state = PathStatus.EXECUTING
        val mc = Minecraft.getInstance()
        chainStartPos = mc.player?.position() ?: Vec3.ZERO
        lastStuckCheckPos = chainStartPos
        DebugLog.status(
            mc, "Pathfinding",
            "Pathfinder: executing etherwarp chain, ${chain.points.size} hops"
        )
    }

    // Aim at the current hop's solved angles; when aligned, fire one etherwarp
    // and advance. After the last hop, drop chain mode and run a normal walking
    // search for the residual approach. Any stall abandons the chain to the
    // walker so we never get stuck mid-chain.
    private fun tickEtherwarpChain(player: LocalPlayer): PathCommand? {
        if (chainCursor >= chainPoints.size) {
            abandonChainToWalk()
            return null
        }

        val target = chainPoints[chainCursor]
        val targetCenter = Vec3(target.x + 0.5, target.y, target.z + 0.5)

        // Did we land on (close to) the current hop? Advance.
        val pPos = player.position()
        val reached = pPos.distanceTo(targetCenter) <= CHAIN_HOP_REACHED_DIST
        if (reached) {
            chainCursor++
            chainHopWaitTicks = 0
            teleportFiredNodeKey = -1L
            if (chainCursor >= chainPoints.size) {
                // Final landing reached; let the normal walker handle the
                // short residual approach (e.g. "walk 10 blocks").
                abandonChainToWalk()
                return null
            }
            return null
        }

        val aotvSlot = EtherwarpLogic.findEtherwarpHotbarSlot()
        if (aotvSlot < 0) {
            // Lost the etherwarp item mid-chain — fall back to walking.
            abandonChainToWalk()
            return null
        }

        val ai = chainCursor * 2
        val targetYaw = chainAngles.getOrElse(ai) { Float.NaN }
        val targetPitch = chainAngles.getOrElse(ai + 1) { Float.NaN }
        if (targetYaw.isNaN() || targetPitch.isNaN()) {
            abandonChainToWalk()
            return null
        }

        if (teleportCooldownTicks > 0) teleportCooldownTicks--

        val yawErr = abs(AngleUtils.getRotationDelta(player.yRot, targetYaw))
        val pitchErr = abs(player.xRot - targetPitch)
        val aligned = yawErr < CHAIN_HOP_ALIGN_YAW && pitchErr < CHAIN_HOP_ALIGN_PITCH

        val hopKey = (target.x.toLong() and 0x1FFFFF) or
            ((target.z.toLong() and 0x1FFFFF) shl 21)

        if (aligned && teleportCooldownTicks <= 0 && hopKey != teleportFiredNodeKey) {
            teleportFiredNodeKey = hopKey
            fireTeleport(isEtherwarp = true, aotvSlot = aotvSlot)
            teleportCooldownTicks = PathTeleportConfig.teleportCooldownTicks.coerceAtLeast(1)
            chainHopWaitTicks = 0
        } else if (hopKey == teleportFiredNodeKey) {
            // We already fired for this hop; wait for the teleport to resolve.
            chainHopWaitTicks++
            if (chainHopWaitTicks >= CHAIN_HOP_WAIT_TICKS) {
                // Teleport didn't land us near the hop in time → abandon.
                abandonChainToWalk()
                return null
            }
        }

        return PathCommand(
            forward = false,
            back = false,
            jump = false,
            sneak = true,
            sprint = false,
            targetYaw = targetYaw,
            targetPitch = targetPitch,
            status = state,
            activeAction = ActionType.ETHERWARP,
            distanceToTarget = pPos.distanceTo(targetCenter).toFloat()
        )
    }

    // Leave chain mode and re-plan a normal walking path to the same goal from
    // the player's current position (handles residual approach AND mid-chain
    // failure recovery — both want "just walk from here").
    private fun abandonChainToWalk() {
        usingEtherwarpChain = false
        chainPoints = emptyList()
        chainAngles = FloatArray(0)
        chainCursor = 0
        chainHopWaitTicks = 0
        etherwarpChainResult = null
        pendingChainEligible = false
        val player = Minecraft.getInstance().player
        if (player == null) {
            state = PathStatus.FAILED
            releaseGuidedControl()
            return
        }
        startReplan(player.position())
    }
```

Note: `abandonChainToWalk` sets `pendingChainEligible = false`, but `startReplan` calls `captureChainInputs` which recomputes it. That is intentional and correct: from the post-chain landing the straight-line distance to the goal is now short (< `chainMinStraightLine`) OR no longer a detour, so `maybePlanEtherwarpChain` returns null and the walker runs. The reset of the flag is just a safe default in case `player` was momentarily null.

- [ ] **Step 4: Clear chain state on reset/stop/level change**

Find `resetPathState()` (around line 306). Locate this existing line inside it:

```kotlin
        routeWpIndex = 0
```

Insert immediately after it:

```kotlin
        usingEtherwarpChain = false
        chainPoints = emptyList()
        chainAngles = FloatArray(0)
        chainCursor = 0
        chainHopWaitTicks = 0
        etherwarpChainResult = null
        pendingChainEligible = false
```

(`resetPathState()` is already called by `stop()` and `onLevelChange()`, so those paths are covered. `cancelSearch()` does not need it — it only flips state to IDLE and the next search recaptures inputs.)

- [ ] **Step 5: Verify build**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 6: In-game manual test (the reported scenario)**

1. `./gradlew deployMod`, launch the game, hold an etherwarp item (e.g. AOTV/Etherwarp sword).
2. Stand where the original bug occurred: a destination on a ledge ~30 blocks away whose only walking route loops to the back of the glacite tunnels and back.
3. Run `goto` to the ledge coordinate.
4. Expected: chat shows `Pathfinder: detour ... -> etherwarp chain (N hops)`, the player aims and etherwarps across the gap toward the ledge instead of walking the loop, then walks the short residual to the exact point.
5. Negative test: `goto` a nearby reachable spot (< 14 blocks, or a straight corridor). Expected: NO chain message; normal walking (unchanged behavior).
6. Failure-safety test: `goto` a ledge target while NOT holding an etherwarp item. Expected: normal walking detour (no chain attempted, no errors).

- [ ] **Step 7: Commit**

```bash
git add src/main/kotlin/org/phantom/api/pathfinder/jni/NativePathfinder.kt
git commit -m "feat: execute etherwarp chain shortcut with clean walker fallback"
```

---

## Self-Review

**Spec coverage:**
- "Run walking A* as today" → unchanged; `findPath` still called first (Task 4 Step 1).
- "If resulting path is a big detour (path length >> straight-line) and etherwarp available" → `maybePlanEtherwarpChain` ratio + min-distance + `chainEligible` gates (Task 4 Step 2; Task 3 Step 5).
- "invoke findEtherwarpPath toward the goal" → `EtherwarpLogic.findEtherwarpChain` → `NativePathfinderJNI.findEtherwarpPath` against the support block (Task 2; Task 4 Step 2).
- "prefer it over the detour" → `tick()` consumes `etherwarpChainResult` before `searchResult` and enters chain mode (Task 5 Steps 1-3).
- Residual "walk 10 blocks" + mid-chain failure → `abandonChainToWalk()` → `startReplan` (Task 5 Step 3).
- No infinite re-trigger → `chainMinStraightLine` guard + post-landing short distance (Task 5 Step 3 note).
- Thread-safety: chain search runs sequentially after `findPath` on the same single bg thread; client-only reads captured at submit (Task 3 Steps 3-5; Task 4 Step 1).

**Placeholder scan:** No TBD/“handle edge cases” — all steps contain full code.

**Type consistency:** `EtherwarpChainPlan(points: List<Vec3>, angles: FloatArray)` defined Task 3, used identically in Tasks 4-5. `findEtherwarpChain(supportGoal, eyeX, eyeY, eyeZ, rayLength): NativeEtherwarpResult?` defined Task 2, called with exactly those args in Task 4. `captureChainInputs(player, isFly)` defined Task 3 Step 5, called in Steps 3-4. `maybePlanEtherwarpChain(...)` 9-arg signature matches its single call site. `abandonChainToWalk()` / `tickEtherwarpChain(player)` / `applyEtherwarpChain(chain)` names consistent across Task 5.

---

## Risks / things to watch during execution

- **Eye-height accuracy for hops.** We pass `eyeHeight = 1.62` and start origin = `player.eyePosition` (mirrors the proven `findEtherwarpAngles`). If hops consistently land short/long, revisit the eye-height constant — but do not change it speculatively.
- **`AngleUtils.getRotationDelta` signature.** Confirm it is `(current: Float, target: Float): Float` as used by the existing teleport-alignment code in `tick()` (it is used identically there ~line 589). Reuse that exact call shape.
- **Rotation actually being driven to the chain angles.** The existing executor turns the player toward `PathCommand.targetYaw/targetPitch` when `activeAction == ETHERWARP` (same as `selectV5TeleportCandidate`). If the player does not rotate in chain mode, check how `PathfindingModule` consumes `PathCommand` (`PathfindingModule.kt:620 val cmd = NativePathfinder.tick()`) and ensure ETHERWARP rotation is honored when `forward = false` — adjust the consumer there if needed (do not widen scope beyond rotation honoring).
- **Cooldown field reuse.** `teleportCooldownTicks` and `teleportFiredNodeKey` are shared with the walking overlay; chain mode resets them on entry (`applyEtherwarpChain`) and never runs concurrently with the overlay (overlay code path is skipped via the early return in Task 5 Step 2), so reuse is safe.
