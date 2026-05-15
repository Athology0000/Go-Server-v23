# Path Execution Improvements Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make the pathfinder get stuck far less often by using per-node terrain flags to drive jump/sprint/strafe decisions, and recover much faster when a stall does occur.

**Architecture:** All changes are in `NativePathfinder.kt`. The C++ side already computes per-node flags (`FLAG_STEP_UP_NEXT`, `FLAG_LOW_HEADROOM`, `FLAG_TIGHT_CORRIDOR`) and stores them in `cachedKeyNodeFlags`. Task 1 wires these into movement decisions. Tasks 2â€“3 tighten stuck detection and add avoid penalties so replanned paths route around recently-stuck positions.

**Tech Stack:** Kotlin, Fabric Minecraft mod (MC 1.21.11). No unit tests in this project â€” verify by running `./gradlew build` and observing behaviour in-game.

---

### Task 1: Flag-aware action selection and PathCommand construction

**Files:**
- Modify: `src/main/kotlin/org/phantom/api/pathfinder/jni/NativePathfinder.kt`

- [ ] **Step 1: Add flag bit constants**

  Inside the `NativePathfinder` object, add these private constants (mirrored from `natives/src/path_annotations.cpp`):

  ```kotlin
  private const val FLAG_LOW_HEADROOM   = 1 shl 2
  private const val FLAG_STEP_UP_NEXT   = 1 shl 5
  private const val FLAG_TIGHT_CORRIDOR = 1 shl 7
  ```

  Place them with the other numeric constants near the bottom of the object, before the closing `}`.

- [ ] **Step 2: Update `computeActiveAction` to accept node flags and return SPRINT_JUMP**

  Replace the existing `computeActiveAction` function (lines 467â€“476):

  ```kotlin
  private fun computeActiveAction(nodes: List<Vec3>, cursor: Int, aotvAvailable: Boolean, nodeFlags: Int): ActionType {
      // AOTV takes priority â€” long straight leg between key nodes
      if (aotvAvailable && nodes.size >= 2) {
          val current = nodes.getOrNull(cursor)
          val next = nodes.getOrNull(cursor + 1)
          if (current != null && next != null) {
              val dx = next.x - current.x
              val dz = next.z - current.z
              val horizDist = sqrt(dx * dx + dz * dz)
              if (horizDist in AOTV_NODE_DISTANCE_MIN..AOTV_NODE_DISTANCE_MAX) return ActionType.AOTV
          }
      }
      if (nodeFlags and FLAG_STEP_UP_NEXT != 0) return ActionType.SPRINT_JUMP
      return ActionType.WALK
  }
  ```

- [ ] **Step 3: Wire flags into `tick()` â€” read flags, update `computeActiveAction` call and PathCommand construction**

  In `tick()`, find the block starting at:
  ```kotlin
  val aotvSlot = EtherwarpLogic.findEtherwarpHotbarSlot()
  ```

  Replace from that line through the PathCommand construction (ending `forwardOnly = noStrafe`):

  ```kotlin
  val aotvSlot = EtherwarpLogic.findEtherwarpHotbarSlot()
  val computedFlags = if (aotvSlot >= 0) 0x1 or 0x2 else 0
  val availabilityFlags = availabilityFlagsOverride ?: computedFlags

  val flagsAtCursor = cachedKeyNodeFlags.getOrElse(pathNodeCursor) { 0 }
  val lowHeadroom   = flagsAtCursor and FLAG_LOW_HEADROOM   != 0
  val tightCorridor = flagsAtCursor and FLAG_TIGHT_CORRIDOR != 0

  val activeAction = computeActiveAction(nodes, pathNodeCursor, aotvSlot >= 0, flagsAtCursor)

  val cmd = PathCommand(
      forward = true,
      back = false,
      jump = jumpPulse,
      sneak = false,
      sprint = !lowHeadroom && !tightCorridor,
      targetYaw = targetYaw,
      targetPitch = 0f,
      status = state,
      activeAction = activeAction,
      distanceToTarget = distToGoal.toFloat(),
      forwardOnly = noStrafe || tightCorridor
  )
  ```

- [ ] **Step 4: Build**

  ```bash
  ./gradlew build
  ```

  Expected: `BUILD SUCCESSFUL`. Fix any compile errors before continuing.

- [ ] **Step 5: Commit**

  ```bash
  git add src/main/kotlin/org/phantom/api/pathfinder/jni/NativePathfinder.kt
  git commit -m "feat(pathfinder): use per-node flags for jump/sprint/strafe decisions"
  ```

---

### Task 2: Faster stuck detection and vertical stall detection

**Files:**
- Modify: `src/main/kotlin/org/phantom/api/pathfinder/jni/NativePathfinder.kt`

- [ ] **Step 1: Reduce coarse stuck interval and add vertical stall state**

  Find the existing constants block near the bottom of the object:
  ```kotlin
  private const val STUCK_CHECK_INTERVAL = 60
  ```
  Change it to:
  ```kotlin
  private const val STUCK_CHECK_INTERVAL = 20
  ```

  Then add two new constants alongside the other numeric constants:
  ```kotlin
  private const val VERTICAL_STALL_CHECK_INTERVAL = 15
  private const val VERTICAL_STALL_MIN_RISE = 0.2
  ```

- [ ] **Step 2: Add vertical stall state fields**

  Find the existing stuck-state fields:
  ```kotlin
  private var stuckTicks: Int = 0
  private var lastStuckCheckPos: Vec3 = Vec3.ZERO
  private var collisionTicks: Int = 0
  private var lastCollisionCheckPos: Vec3 = Vec3.ZERO
  ```

  Add two new fields immediately after:
  ```kotlin
  private var verticalStallTicks: Int = 0
  private var lastVerticalCheckPos: Vec3 = Vec3.ZERO
  ```

- [ ] **Step 3: Reset vertical stall state in `stop()` and `onLevelChange()`**

  In `stop()`, find the block that resets `stuckTicks` and `collisionTicks`:
  ```kotlin
  stuckTicks = 0
  collisionTicks = 0
  lastStuckCheckPos = Vec3.ZERO
  lastCollisionCheckPos = Vec3.ZERO
  ```
  Add after it:
  ```kotlin
  verticalStallTicks = 0
  lastVerticalCheckPos = Vec3.ZERO
  ```

  Do the same in `onLevelChange()` â€” same location pattern.

- [ ] **Step 4: Add vertical stall check in `tick()`**

  In `tick()`, find the coarse stuck detection block:
  ```kotlin
  // Stuck detection â€” only trigger new replan when EXECUTING (not already REPLANNING)
  stuckTicks++
  if (stuckTicks >= STUCK_CHECK_INTERVAL && state == PathStatus.EXECUTING) {
  ```

  Insert the following block **immediately before** that comment:

  ```kotlin
  // Vertical stall â€” if the upcoming node is a step-up but Y hasn't risen, replan
  if (state == PathStatus.EXECUTING) {
      val upcomingFlags = cachedKeyNodeFlags.getOrElse(pathNodeCursor) { 0 }
      if (upcomingFlags and FLAG_STEP_UP_NEXT != 0) {
          if (lastVerticalCheckPos == Vec3.ZERO) lastVerticalCheckPos = playerPos
          verticalStallTicks++
          if (verticalStallTicks >= VERTICAL_STALL_CHECK_INTERVAL) {
              verticalStallTicks = 0
              val rise = playerPos.y - lastVerticalCheckPos.y
              if (rise < VERTICAL_STALL_MIN_RISE) startReplan(playerPos)
              lastVerticalCheckPos = playerPos
          }
      } else {
          verticalStallTicks = 0
          lastVerticalCheckPos = Vec3.ZERO
      }
  }
  ```

  Note: `playerPos` is already in scope at this point in `tick()` as `Vec3(player.x, player.y, player.z)`.

- [ ] **Step 5: Build**

  ```bash
  ./gradlew build
  ```

  Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 6: Commit**

  ```bash
  git add src/main/kotlin/org/phantom/api/pathfinder/jni/NativePathfinder.kt
  git commit -m "feat(pathfinder): reduce stuck interval to 20t, add vertical stall detector"
  ```

---

### Task 3: Transient avoid penalties on replan

**Files:**
- Modify: `src/main/kotlin/org/phantom/api/pathfinder/jni/NativePathfinder.kt`

- [ ] **Step 1: Add avoid-penalty constants and state**

  Add constants alongside the other numeric constants at the bottom of the object:
  ```kotlin
  private const val STUCK_POSITIONS_MAX  = 4
  private const val AVOID_PENALTY        = 25.0
  private const val AVOID_RADIUS_SQ      = 4
  private const val AVOID_MAX_Y_DIFF     = 2
  private const val CLEAN_EXEC_TICKS     = 30
  ```

  Add state fields alongside the other mutable state fields (after the `lastCollisionCheckPos` field):
  ```kotlin
  private val stuckPositions: ArrayDeque<BlockPos> = ArrayDeque(4)
  private var cleanExecTicks: Int = 0
  ```

  You'll also need to add the `BlockPos` import if not already present:
  ```kotlin
  import net.minecraft.core.BlockPos
  ```
  (It is already imported â€” check the import block to confirm before adding.)

- [ ] **Step 2: Reset new state in `stop()` and `onLevelChange()`**

  In `stop()`, after the `verticalStallTicks = 0` lines added in Task 2:
  ```kotlin
  stuckPositions.clear()
  cleanExecTicks = 0
  ```

  Do the same in `onLevelChange()`.

  Also add a clear in `startSearch()` â€” new navigation target means old stuck positions are irrelevant. Find:
  ```kotlin
  private fun startSearch() {
      cancelSearch()
      state = PathStatus.PLANNING
  ```
  Add after `cancelSearch()`:
  ```kotlin
  stuckPositions.clear()
  cleanExecTicks = 0
  ```

- [ ] **Step 3: Record stuck position in `startReplan()` and reset clean counter**

  Find `startReplan`:
  ```kotlin
  private fun startReplan(playerPos: Vec3) {
      if (state == PathStatus.REPLANNING) return
      state = PathStatus.REPLANNING
      NativePathfinderJNI.cancelSearch()
      searchFuture?.cancel(true)
      searchFuture = null
      val startPos = BlockPos.containing(playerPos)
      submitSearch(
  ```

  Replace it with:
  ```kotlin
  private fun startReplan(playerPos: Vec3) {
      if (state == PathStatus.REPLANNING) return
      state = PathStatus.REPLANNING
      NativePathfinderJNI.cancelSearch()
      searchFuture?.cancel(true)
      searchFuture = null
      cleanExecTicks = 0
      if (stuckPositions.size >= STUCK_POSITIONS_MAX) stuckPositions.removeFirst()
      stuckPositions.addLast(BlockPos.containing(playerPos))
      val startPos = BlockPos.containing(playerPos)
      submitSearch(
          startPos.x, startPos.y, startPos.z,
          goalX, goalY, goalZ,
          routeProfile == MovementProfile.FLY
      )
  }
  ```

- [ ] **Step 4: Add `buildAvoidMeta()` helper and update `submitSearch()`**

  Add the helper just above `submitSearch`:
  ```kotlin
  private fun buildAvoidMeta(): IntArray {
      if (stuckPositions.isEmpty()) return intArrayOf()
      val meta = IntArray(stuckPositions.size * 5)
      var i = 0
      for (pos in stuckPositions) {
          meta[i++] = pos.x
          meta[i++] = pos.y
          meta[i++] = pos.z
          meta[i++] = AVOID_RADIUS_SQ
          meta[i++] = AVOID_MAX_Y_DIFF
      }
      return meta
  }
  ```

  Replace the existing `submitSearch` body:
  ```kotlin
  private fun submitSearch(sx: Int, sy: Int, sz: Int, gx: Int, gy: Int, gz: Int, isFly: Boolean) {
      val avoidMeta = buildAvoidMeta()
      val avoidPenalty = DoubleArray(stuckPositions.size) { AVOID_PENALTY }
      searchFuture = searchExecutor.submit {
          try {
              val result = NativePathfinderJNI.findPath(
                  startPoints = intArrayOf(sx, sy, sz),
                  endPoints = intArrayOf(gx, gy, gz),
                  isFly = isFly,
                  maxIterations = MAX_ITERATIONS,
                  heuristicWeight = HEURISTIC_WEIGHT,
                  nonPrimaryStartPenalty = 0.0,
                  moveOrderOffset = 0,
                  avoidMeta = avoidMeta,
                  avoidPenalty = avoidPenalty
              )
              if (result != null && result.keyPath.size >= 3) {
                  searchResult = result
              } else {
                  searchFailed = true
              }
          } catch (_: Exception) {
              searchFailed = true
          }
      }
  }
  ```

- [ ] **Step 5: Increment `cleanExecTicks` in `tick()` and clear stuck positions when threshold reached**

  In `tick()`, find the coarse stuck detection block:
  ```kotlin
  stuckTicks++
  if (stuckTicks >= STUCK_CHECK_INTERVAL && state == PathStatus.EXECUTING) {
      stuckTicks = 0
      val moved = playerPos.distanceTo(lastStuckCheckPos)
      if (moved < STUCK_THRESHOLD) startReplan(playerPos)
      lastStuckCheckPos = playerPos
  }
  ```

  Add the following block **immediately after** it:
  ```kotlin
  if (state == PathStatus.EXECUTING && stuckPositions.isNotEmpty()) {
      cleanExecTicks++
      if (cleanExecTicks >= CLEAN_EXEC_TICKS) {
          stuckPositions.clear()
          cleanExecTicks = 0
      }
  }
  ```

- [ ] **Step 6: Build**

  ```bash
  ./gradlew build
  ```

  Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 7: Commit**

  ```bash
  git add src/main/kotlin/org/phantom/api/pathfinder/jni/NativePathfinder.kt
  git commit -m "feat(pathfinder): add transient avoid penalties on replan to route around stuck positions"
  ```
