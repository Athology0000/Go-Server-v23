# Combat Patrol Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a `CombatPatrolModule` with Walk/Warp/Kill point types that integrates with `CombatMacroModule` — patrol routes when idle, fight stray mobs mid-transit, and clear kill zones at Kill points.

**Architecture:** A standalone Kotlin `object` module in `internal/combat/` that owns `NativePathfinder` during NAVIGATING/WARPING states and yields ownership to `CombatMacroModule` during COMBAT_INTERRUPT/AT_KILL_ZONE. Warp logic is copied from `RoutesModule`'s frame-driven state machine. `CombatMacroModule` is modified in four places to check patrol state.

**Tech Stack:** Kotlin, Fabric Minecraft 1.21.11, `NativePathfinder` JNI bridge, `EventBus` / `@SubscribeEvent`, Cobalt module/setting API, Gson for persistence.

**Spec:** `docs/superpowers/specs/2026-03-22-combat-patrol-design.md`

---

## File Map

| Action | File | Purpose |
|--------|------|---------|
| **Create** | `src/main/kotlin/org/cobalt/internal/combat/CombatPatrolModule.kt` | New module — all patrol logic |
| **Modify** | `src/main/kotlin/org/cobalt/internal/combat/CombatMacroModule.kt` | 4 integration points |
| **Modify** | `src/main/kotlin/org/cobalt/Cobalt.kt` | Register new module |

No unit tests exist in this project — verification is by in-game behaviour and build success (`./gradlew build`).

---

## Task 1: Data model + persistence

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/combat/CombatPatrolModule.kt` (skeleton only)

- [ ] **Step 1: Create the file with data model, enum, and persistence helpers**

```kotlin
package org.cobalt.internal.combat

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import net.minecraft.client.Minecraft
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.*
import org.cobalt.api.util.ChatUtils

// ──────────────────────────────────────────────────────────────────────────────
// Data model
// ──────────────────────────────────────────────────────────────────────────────

enum class CombatPatrolPointType(val id: String) {
    WALK("walk"), WARP("warp"), KILL("kill");
    companion object {
        fun fromId(id: String?) = entries.firstOrNull { it.id == id } ?: WALK
    }
}

data class CombatPatrolPoint(val x: Int, val y: Int, val z: Int, val type: CombatPatrolPointType)

// ──────────────────────────────────────────────────────────────────────────────
// Module skeleton
// ──────────────────────────────────────────────────────────────────────────────

object CombatPatrolModule : Module("Combat Patrol") {

    private val mc: Minecraft = Minecraft.getInstance()
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val patrolDir: File by lazy {
        File(mc.gameDirectory, "config/cobalt/combat_patrol")
    }

    // ── route data ────────────────────────────────────────────────────────────
    internal val patrolPoints = mutableListOf<CombatPatrolPoint>()

    // ── settings ──────────────────────────────────────────────────────────────
    private val routeName = TextSetting("Route Name", "Name used for save/load.", "default")
    private val pointsInfo = InfoSetting("Points", "Number of recorded points.", InfoType.INFO)
    private val statusInfo = InfoSetting("Status", "Current patrol state.", InfoType.INFO)
    private val recordOnRightClick = CheckboxSetting("Record on Right Click", "Append a point when you right-click a block.", false)
    val loopRoute = CheckboxSetting("Loop Route", "Wrap back to first point at end.", true)
    val startFromNearest = CheckboxSetting("Start From Nearest", "Begin at the closest point to your position.", true)
    val pointType = ModeSetting("Point Type", "Type used when adding points.", 0, arrayOf("Walk", "Warp", "Kill"))
    val killZoneRadius = SliderSetting("Kill Zone Radius", "Mob search radius around a Kill point (blocks).", 16.0, 4.0, 64.0)
    val killZoneDwellTicks = SliderSetting("Kill Zone Dwell Ticks", "Zero-mob ticks required before advancing past a Kill point.", 60.0, 10.0, 200.0, step = 1.0)
    val aotvSlot = ModeSetting("AOTV Slot", "Hotbar slot (1–9) holding your AOTV item.", 0, arrayOf("1","2","3","4","5","6","7","8","9"))

    private val addPointAction = ActionSetting("Add Point", "Record your current position.", "Add") { addPointFromPlayer() }
    private val removeLastAction = ActionSetting("Remove Last", "Remove the last recorded point.", "Remove") {
        if (patrolPoints.isNotEmpty()) { patrolPoints.removeAt(patrolPoints.lastIndex); updateInfo() }
    }
    private val clearRouteAction = ActionSetting("Clear Route", "Remove all patrol points.", "Clear") {
        patrolPoints.clear(); updateInfo()
    }
    private val saveRouteAction = ActionSetting("Save Route", "Save route to disk.", "Save") { saveRoute() }
    private val loadRouteAction = ActionSetting("Load Route", "Load route from disk.", "Load") { loadRoute() }
    private val startPatrolAction = ActionSetting("Start Patrol", "Start the patrol.", "Start") { startPatrol() }
    private val stopPatrolAction = ActionSetting("Stop Patrol", "Stop the patrol.", "Stop") { stopPatrol() }

    init {
        addSetting(
            routeName, pointsInfo, statusInfo,
            recordOnRightClick, loopRoute, startFromNearest, pointType,
            killZoneRadius, killZoneDwellTicks, aotvSlot,
            addPointAction, removeLastAction, clearRouteAction,
            saveRouteAction, loadRouteAction,
            startPatrolAction, stopPatrolAction,
        )
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private fun currentPointType() = when (pointType.value) {
        1 -> CombatPatrolPointType.WARP
        2 -> CombatPatrolPointType.KILL
        else -> CombatPatrolPointType.WALK
    }

    private fun addPointFromPlayer() {
        val player = mc.player ?: return
        val pos = player.blockPosition()
        patrolPoints.add(CombatPatrolPoint(pos.x, pos.y, pos.z, currentPointType()))
        ChatUtils.sendMessage("Patrol point added (${patrolPoints.size} total, type=${currentPointType().id}).")
        updateInfo()
    }

    private fun isValidName(name: String): Boolean {
        if (name == "." || name == "..") return false
        if (name.endsWith(".") || name.endsWith(" ")) return false
        return name.none { it in charArrayOf('\\', '/', ':', '*', '?', '"', '<', '>', '|') }
    }

    private fun routeFile(name: String) = File(patrolDir, "$name.json")

    private fun saveRoute() {
        val name = routeName.value.trim()
        if (name.isEmpty()) { ChatUtils.sendMessage("Route name is empty."); return }
        if (!isValidName(name)) { ChatUtils.sendMessage("Invalid route name characters."); return }
        if (!patrolDir.exists()) patrolDir.mkdirs()
        val root = JsonObject()
        val arr = JsonArray()
        patrolPoints.forEach { p ->
            val o = JsonObject()
            o.addProperty("x", p.x); o.addProperty("y", p.y); o.addProperty("z", p.z)
            o.addProperty("type", p.type.id)
            arr.add(o)
        }
        root.add("points", arr)
        routeFile(name).writeText(gson.toJson(root))
        ChatUtils.sendMessage("Saved patrol route \"$name\" (${patrolPoints.size} points).")
    }

    private fun loadRoute() {
        val name = routeName.value.trim()
        if (name.isEmpty()) { ChatUtils.sendMessage("Route name is empty."); return }
        if (!isValidName(name)) { ChatUtils.sendMessage("Invalid route name characters."); return }
        val file = routeFile(name)
        if (!file.exists()) { ChatUtils.sendMessage("Route \"$name\" not found."); return }
        val text = runCatching { file.readText() }.getOrNull()?.trim().orEmpty()
        if (text.isEmpty()) { ChatUtils.sendMessage("Route file is empty."); return }
        val parsed = runCatching { JsonParser.parseString(text) }.getOrNull() ?: run {
            ChatUtils.sendMessage("Route file is invalid JSON."); return
        }
        val arr = parsed.asJsonObject?.getAsJsonArray("points") ?: run {
            ChatUtils.sendMessage("Route file has no \"points\" array."); return
        }
        val loaded = mutableListOf<CombatPatrolPoint>()
        arr.forEach { el ->
            val o = el.asJsonObject
            val x = o["x"]?.asInt ?: return@forEach
            val y = o["y"]?.asInt ?: return@forEach
            val z = o["z"]?.asInt ?: return@forEach
            val t = CombatPatrolPointType.fromId(o["type"]?.asString)
            loaded.add(CombatPatrolPoint(x, y, z, t))
        }
        patrolPoints.clear()
        patrolPoints.addAll(loaded)
        updateInfo()
        ChatUtils.sendMessage("Loaded patrol route \"$name\" (${patrolPoints.size} points).")
    }

    private fun updateInfo() {
        pointsInfo.value = "${patrolPoints.size} points"
    }

    fun startPatrol() { /* filled in Task 2 */ }
    fun stopPatrol(msg: String = "") { /* filled in Task 2 */ }
}
```

- [ ] **Step 2: Build to verify it compiles**

```bash
./gradlew build
```
Expected: BUILD SUCCESSFUL (no errors in `CombatPatrolModule.kt`)

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/combat/CombatPatrolModule.kt
git commit -m "feat(combat-patrol): data model, settings skeleton, persistence"
```

---

## Task 2: Patrol state machine (Walk points only — no Warp, no Kill yet)

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/combat/CombatPatrolModule.kt`

This task adds the core IDLE → NAVIGATING → ARRIVED → advance loop for WALK points, plus the public API (`patrolOwnsPathfinder`, `onCombatInterrupt`, `onCombatResume`, `onKillZoneCleared`).

- [ ] **Step 1: Add state machine variables and EventBus imports at the top of the object**

After the `// ── route data ───` block, add:

```kotlin
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.MouseEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.event.impl.render.WorldRenderEvent
import org.cobalt.api.pathfinder.jni.NativePathfinder
import org.cobalt.api.pathfinder.jni.PathStatus
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules
import org.cobalt.internal.pathfinding.PathfindingModule
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.BlockHitResult
```

Add these state fields inside the object (after `patrolPoints`):

```kotlin
enum class PatrolState { IDLE, NAVIGATING, WARPING, COMBAT_INTERRUPT, AT_KILL_ZONE }

var patrolState: PatrolState = PatrolState.IDLE
    private set
var patrolOwnsPathfinder: Boolean = false
    private set

private var patrolRunning = false
private var routeIndex = 0
private var killZoneClearTicks = 0
private var lastPathStartGameTime = 0L
```

- [ ] **Step 2: Register EventBus in `init {}` and add tick handler**

Add to the `init {}` block:
```kotlin
EventBus.register(this)
```

Add the tick handler after `init`:

```kotlin
@SubscribeEvent
fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    updateInfo()
    if (!enabled.value) {
        if (patrolRunning) stopPatrol("Patrol module disabled.")
        return
    }
    if (patrolPoints.isEmpty()) {
        if (patrolRunning) stopPatrol("No patrol points.")
        return
    }
    if (!patrolRunning) return

    val level = mc.level ?: return

    when (patrolState) {
        PatrolState.NAVIGATING -> {
            NativePathfinder.tick()?.applyToPlayer()
            val s = NativePathfinder.status
            if (s == PathStatus.ARRIVED || s == PathStatus.FAILED) {
                advanceAndNavigate()
            }
        }
        PatrolState.WARPING -> {
            // Warp drives itself via onRender. Only handle timeout here (Task 4).
        }
        PatrolState.AT_KILL_ZONE -> {
            // killZoneClearTicks is incremented by onKillZoneCleared() calls from CombatMacroModule.
            // If counter reaches threshold, clear zone and move on.
            if (killZoneClearTicks >= killZoneDwellTicks.value.toInt()) {
                killZoneClearTicks = 0
                advanceAndNavigate()
            }
        }
        PatrolState.COMBAT_INTERRUPT, PatrolState.IDLE -> { /* CombatMacroModule owns pathfinder */ }
    }
    statusInfo.value = patrolState.name
}
```

- [ ] **Step 3: Add `startPatrol`, `stopPatrol`, `advanceAndNavigate`, `navigateTo`, `findNearestIndex`**

Replace the placeholder `startPatrol()` and `stopPatrol()` stubs:

```kotlin
fun startPatrol() {
    if (patrolPoints.isEmpty()) { ChatUtils.sendMessage("No patrol points. Add some first."); return }
    PathfindingModule.ensureEnabledForAutomation("combat-patrol")
    routeIndex = if (startFromNearest.value) findNearestIndex() else 0
    patrolRunning = true
    killZoneClearTicks = 0
    navigateTo(patrolPoints[routeIndex])
    ChatUtils.sendMessage("Combat patrol started at point ${routeIndex + 1}/${patrolPoints.size}.")
}

fun stopPatrol(msg: String = "") {
    if (patrolRunning && msg.isNotEmpty()) ChatUtils.sendMessage(msg)
    patrolRunning = false
    patrolOwnsPathfinder = false
    patrolState = PatrolState.IDLE
    killZoneClearTicks = 0
    NativePathfinder.stop()
    statusInfo.value = "IDLE"
}

val isPatrolRunning: Boolean get() = patrolRunning

private fun advanceAndNavigate() {
    routeIndex++
    if (routeIndex >= patrolPoints.size) {
        if (loopRoute.value) {
            routeIndex = 0
        } else {
            stopPatrol("Patrol complete.")
            return
        }
    }
    navigateTo(patrolPoints[routeIndex])
}

private fun navigateTo(point: CombatPatrolPoint) {
    when (point.type) {
        CombatPatrolPointType.WALK, CombatPatrolPointType.KILL -> {
            val level = mc.level
            val resolved = if (level != null) {
                MinecraftPathingRules.resolveTarget(level, BlockPos(point.x, point.y, point.z))
                    ?: BlockPos(point.x, point.y, point.z)
            } else BlockPos(point.x, point.y, point.z)
            NativePathfinder.setTarget(resolved.x + 0.5, resolved.y.toDouble(), resolved.z + 0.5)
            lastPathStartGameTime = mc.level?.gameTime ?: 0L
            patrolOwnsPathfinder = true
            patrolState = PatrolState.NAVIGATING
        }
        CombatPatrolPointType.WARP -> {
            // Warp logic added in Task 4. For now fall back to walk.
            NativePathfinder.setTarget(point.x + 0.5, point.y.toDouble(), point.z + 0.5)
            patrolOwnsPathfinder = true
            patrolState = PatrolState.NAVIGATING
        }
    }
}

private fun findNearestIndex(): Int {
    val player = mc.player ?: return 0
    val px = player.x; val py = player.y; val pz = player.z
    return patrolPoints.indices.minByOrNull { i ->
        val p = patrolPoints[i]
        val dx = p.x + 0.5 - px; val dy = p.y - py; val dz = p.z + 0.5 - pz
        dx * dx + dy * dy + dz * dz
    } ?: 0
}
```

- [ ] **Step 4: Add public API methods (`onCombatInterrupt`, `onCombatResume`, `onKillZoneCleared`)**

```kotlin
/** Called by CombatMacroModule when a stray mob is found while patrol is navigating. */
fun onCombatInterrupt() {
    if (patrolState != PatrolState.NAVIGATING && patrolState != PatrolState.WARPING) return
    NativePathfinder.stop()          // synchronous stop — prevents one-tick overrun
    patrolOwnsPathfinder = false
    patrolState = PatrolState.COMBAT_INTERRUPT
}

/** Called by CombatMacroModule when no target remains (edge-triggered: only acts on COMBAT_INTERRUPT). */
fun onCombatResume() {
    if (patrolState != PatrolState.COMBAT_INTERRUPT) return
    // Re-issue the current waypoint target.
    val point = patrolPoints.getOrNull(routeIndex) ?: run { stopPatrol("Route index out of bounds."); return }
    navigateTo(point)
}

/** Called by CombatMacroModule each tick there are no mobs in the kill zone (AT_KILL_ZONE state). */
fun onKillZoneCleared() {
    if (patrolState != PatrolState.AT_KILL_ZONE) return
    killZoneClearTicks++
    // Actual advancement happens in onTick when the counter reaches the threshold.
}

val currentKillPoint: CombatPatrolPoint?
    get() = if (patrolState == PatrolState.AT_KILL_ZONE) patrolPoints.getOrNull(routeIndex) else null

val killZoneRadiusValue: Double get() = killZoneRadius.value
```

- [ ] **Step 5: Handle AT_KILL_ZONE arrival**

When `navigateTo` targets a KILL point and the pathfinder arrives, instead of immediately advancing we should enter `AT_KILL_ZONE`. Modify the `NAVIGATING` tick handler:

```kotlin
PatrolState.NAVIGATING -> {
    NativePathfinder.tick()?.applyToPlayer()
    val s = NativePathfinder.status
    if (s == PathStatus.ARRIVED || s == PathStatus.FAILED) {
        val current = patrolPoints.getOrNull(routeIndex)
        if (current?.type == CombatPatrolPointType.KILL) {
            // Yield pathfinder to CombatMacroModule for kill zone clearing.
            NativePathfinder.stop()
            patrolOwnsPathfinder = false
            patrolState = PatrolState.AT_KILL_ZONE
            killZoneClearTicks = 0
        } else {
            advanceAndNavigate()
        }
    }
}
```

- [ ] **Step 6: Right-click recording**

```kotlin
@SubscribeEvent
fun onRightClick(@Suppress("UNUSED_PARAMETER") event: MouseEvent.RightClick) {
    if (!enabled.value || !recordOnRightClick.value) return
    val hit = mc.hitResult
    if (hit is BlockHitResult && hit.type == HitResult.Type.BLOCK) {
        val pos = hit.blockPos
        patrolPoints.add(CombatPatrolPoint(pos.x, pos.y, pos.z, currentPointType()))
        ChatUtils.sendMessage("Patrol point recorded at ${pos.x} ${pos.y} ${pos.z} (${currentPointType().id}).")
        updateInfo()
    }
}
```

- [ ] **Step 7: Build**

```bash
./gradlew build
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 8: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/combat/CombatPatrolModule.kt
git commit -m "feat(combat-patrol): WALK/KILL state machine, public API, right-click recording"
```

---

## Task 3: CombatMacroModule integration (4 touch points)

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/combat/CombatMacroModule.kt` (lines ~587, ~793, ~834, ~2026)

- [ ] **Step 1: Add import at top of CombatMacroModule**

Find the imports block (around line 48 where `PatrolWaypointStore` is imported) and add:

```kotlin
import org.cobalt.internal.combat.CombatPatrolModule
```

- [ ] **Step 2: Guard the top-of-tick `NativePathfinder.tick()` call (line ~587)**

Change:
```kotlin
if (startedPath && nativeActive()) {
    NativePathfinder.tick()?.applyToPlayer()
}
```
To:
```kotlin
if (startedPath && nativeActive() && !CombatPatrolModule.patrolOwnsPathfinder) {
    NativePathfinder.tick()?.applyToPlayer()
}
```

- [ ] **Step 3: Replace the "no target" idle block (lines ~793–800)**

Find this block (it is the last `return` inside the `if (target == null)` path for non-slayer / general combat):
```kotlin
      if (startedPath && nativeActive()) {
        nativeStop()
      }
      startedPath = false
      lastTargetPos = null
      currentTargetId = null
      return
    }
```

Replace with:
```kotlin
      if (CombatPatrolModule.isPatrolRunning) {
        when (CombatPatrolModule.patrolState) {
          CombatPatrolModule.PatrolState.COMBAT_INTERRUPT -> CombatPatrolModule.onCombatResume()
          CombatPatrolModule.PatrolState.AT_KILL_ZONE     -> CombatPatrolModule.onKillZoneCleared()
          else -> { /* NAVIGATING/WARPING — patrol owns movement */ }
        }
        if (startedPath && nativeActive()) nativeStop()
        startedPath = false; lastTargetPos = null; currentTargetId = null
        return
      }
      if (startedPath && nativeActive()) {
        nativeStop()
      }
      startedPath = false
      lastTargetPos = null
      currentTargetId = null
      return
    }
```

- [ ] **Step 4: Interrupt patrol when target found (line ~834)**

Find the `else` branch that handles chasing a target (after `// Target spotted — stop kill patrol...`):
```kotlin
    } else {
      // Target spotted — stop kill patrol so combat macro can take over pathfinding.
      if (PathfindingModule.isPatrolActive) PathfindingModule.stopPatrol()
```
After that line, add:
```kotlin
      if (CombatPatrolModule.patrolState == CombatPatrolModule.PatrolState.NAVIGATING ||
          CombatPatrolModule.patrolState == CombatPatrolModule.PatrolState.WARPING) {
        CombatPatrolModule.onCombatInterrupt()
      }
```

- [ ] **Step 5: Stop patrol in `stopMacro()` (line ~2026)**

Find `stopMacro()` and after `if (PathfindingModule.isPatrolActive) PathfindingModule.stopPatrol()` add:
```kotlin
    if (CombatPatrolModule.isPatrolRunning) CombatPatrolModule.stopPatrol()
```

- [ ] **Step 6: Kill zone target filtering in `resolveTarget()`**

In `resolveTarget()` (line ~870), the function currently filters by `searchRangeSq` and `startAreaOrigin`. When patrol is at a kill zone, we need to search within `killZoneRadiusValue` of the kill point instead.

Find the top of `resolveTarget` and add a kill-zone fast path **before** the existing filter loop:

```kotlin
  private fun resolveTarget(player: Player): LivingEntity? {
    val level = mc.level ?: return null

    // Kill zone mode: only target mobs near the current kill point.
    val killPoint = CombatPatrolModule.currentKillPoint
    if (killPoint != null) {
      val kx = killPoint.x + 0.5; val ky = killPoint.y.toDouble(); val kz = killPoint.z + 0.5
      val radiusSq = CombatPatrolModule.killZoneRadiusValue * CombatPatrolModule.killZoneRadiusValue
      val blacklisted = builtInBlacklistedNames
      val filter = targetName.value.trim().lowercase()
      var best: LivingEntity? = null
      var bestDist = Double.POSITIVE_INFINITY
      for (entity in level.entitiesForRendering()) {
        val living = entity as? LivingEntity ?: continue
        if (!isValidTarget(living, player, blacklisted, filter, true)) continue
        val dx = living.x - kx; val dy = living.y - ky; val dz = living.z - kz
        val distSq = dx * dx + dy * dy + dz * dz
        if (distSq > radiusSq) continue
        val playerDist = player.distanceToSqr(living)
        if (playerDist < bestDist) { best = living; bestDist = playerDist }
      }
      if (best != null) currentTargetId = best.uuid
      return best
    }

    // Normal combat search (existing code unchanged below) …
```

- [ ] **Step 7: Build**

```bash
./gradlew build
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 8: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/combat/CombatMacroModule.kt
git commit -m "feat(combat-patrol): integrate CombatPatrolModule into CombatMacroModule"
```

---

## Task 4: Register module in Cobalt.kt

**Files:**
- Modify: `src/main/kotlin/org/cobalt/Cobalt.kt`

- [ ] **Step 1: Add import**

```kotlin
import org.cobalt.internal.combat.CombatPatrolModule
```

- [ ] **Step 2: Register module**

Find the `ModuleManager.register(` list. After `CombatMacroModule,` add:
```kotlin
        CombatPatrolModule,
```

- [ ] **Step 3: Build and verify no crash on startup**

```bash
./gradlew build
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add src/main/kotlin/org/cobalt/Cobalt.kt
git commit -m "feat(combat-patrol): register CombatPatrolModule"
```

---

## Task 5: Warp point type

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/combat/CombatPatrolModule.kt`

The warp sub-machine is copied from `RoutesModule`. Read `RoutesModule.kt` lines 102–108 (warp state vars), 437–445 (`onFrame` driving `handleWarp`), 1779–1808 (`startWarp`), 1810–1900 (`handleWarp` full body) and the constants at lines 2185–2193 before starting this task.

- [ ] **Step 1: Add warp imports and state variables**

Add these imports to the top of the file alongside the existing imports from Task 2:

```kotlin
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.multiplayer.ClientLevel
import org.cobalt.api.util.AngleUtils
```

Add state variables and constants inside the object:

```kotlin
// ── warp sub-machine state ─────────────────────────────────────────────────
private var warpStage = 0
private var warpTargetPoint: CombatPatrolPoint? = null
private var warpStageElapsedMs = 0.0
private var warpStageLastNs = 0L
private var warpLookLastNs = 0L
private var warpCooldownUntil = 0L
private var warpRestoreSlot = -1

private const val WARP_AIM_TOLERANCE = 6.0
private const val WARP_LOOK_YAW_SPEED_DPS = 360.0
private const val WARP_LOOK_PITCH_SPEED_DPS = 300.0
private const val WARP_ALIGN_MS = 170.0
private const val WARP_SNEAK_MS = 85.0
private const val WARP_POST_MS = 70.0
private const val WARP_STAGE1_TIMEOUT_MS = 240.0
private const val WARP_RETRY_COOLDOWN_TICKS = 4L
private const val WARP_TOTAL_TIMEOUT_MS = WARP_ALIGN_MS + WARP_STAGE1_TIMEOUT_MS + WARP_SNEAK_MS + WARP_POST_MS + 200.0
```

- [ ] **Step 2: Add `WorldRenderEvent.Last` handler that drives the warp**

```kotlin
@SubscribeEvent
fun onRender(@Suppress("UNUSED_PARAMETER") event: WorldRenderEvent.Last) {
    if (!patrolRunning || patrolState != PatrolState.WARPING) return
    if (mc.screen != null) return
    val player = mc.player ?: return
    val level = mc.level ?: return
    val target = warpTargetPoint ?: run { cancelWarp(); return }
    handleWarp(player, level, target)
}
```

- [ ] **Step 3: Handle warp timeout in `onTick` WARPING branch**

Replace the placeholder comment in `onTick` WARPING case:
```kotlin
PatrolState.WARPING -> {
    // elapsed time is updated in handleWarp (onRender). Check total timeout here.
    if (warpStageElapsedMs > WARP_TOTAL_TIMEOUT_MS) {
        ChatUtils.sendMessage("Warp timed out at point ${routeIndex + 1} — skipping.")
        cancelWarp()
        advanceAndNavigate()
    }
}
```

- [ ] **Step 4: Implement `startWarpPoint`, `cancelWarp`, `handleWarp`**

Copy the logic from `RoutesModule` — the only difference is targets are `CombatPatrolPoint` not `BlockPos` and we use `aotvSlot.value` for the hotbar slot. Key reference: `RoutesModule.startWarp`, `RoutesModule.handleWarp`, `RoutesModule.resetWarp`.

```kotlin
private fun startWarpPoint(point: CombatPatrolPoint): Boolean {
    val slot = aotvSlot.value
    // Validate AOTV item in hotbar slot (check inventory)
    val inv = mc.player?.inventory ?: return false
    val item = inv.getItem(slot)
    if (item.isEmpty) {
        ChatUtils.sendMessage("No item in AOTV slot ${slot + 1} — skipping warp point.")
        return false
    }
    warpTargetPoint = point
    warpStage = 0
    warpStageElapsedMs = 0.0
    warpStageLastNs = 0L
    warpLookLastNs = 0L
    warpRestoreSlot = mc.player?.inventory?.selected ?: 0
    patrolOwnsPathfinder = true
    patrolState = PatrolState.WARPING
    return true
}

private fun cancelWarp() {
    mc.options.keyUse?.setDown(false)
    mc.options.keyShift?.setDown(false)
    if (warpRestoreSlot >= 0) {
        mc.player?.inventory?.selected = warpRestoreSlot
    }
    warpTargetPoint = null
    warpStage = 0
    warpStageElapsedMs = 0.0
    warpStageLastNs = 0L
    warpLookLastNs = 0L
    warpRestoreSlot = -1
    patrolOwnsPathfinder = false
}

private fun handleWarp(
    player: net.minecraft.client.player.LocalPlayer,
    level: net.minecraft.client.multiplayer.ClientLevel,
    target: CombatPatrolPoint
) {
    // Advance frame timer
    val now = System.nanoTime()
    if (warpStageLastNs != 0L) {
        warpStageElapsedMs += (now - warpStageLastNs) / 1_000_000.0
    }
    warpStageLastNs = now

    val targetPos = BlockPos(target.x, target.y, target.z)
    val aimPos = net.minecraft.world.phys.Vec3(target.x + 0.5, target.y + 1.6, target.z + 0.5)

    // Switch to AOTV slot
    mc.player?.inventory?.selected = aotvSlot.value

    when (warpStage) {
        0 -> {
            // Stage 0: align head toward target
            val rot = org.cobalt.api.util.AngleUtils.getRotation(aimPos, player)
            val yawErr = kotlin.math.abs(org.cobalt.api.util.AngleUtils.getRotationDelta(player.yRot, rot.yaw))
            val pitchErr = kotlin.math.abs(rot.pitch - player.xRot)
            applyWarpLook(player, aimPos)
            if (yawErr <= WARP_AIM_TOLERANCE && pitchErr <= WARP_AIM_TOLERANCE
                || warpStageElapsedMs >= WARP_ALIGN_MS
            ) {
                mc.options.keyShift?.setDown(true)
                warpStage = 1
                warpStageElapsedMs = 0.0
                warpStageLastNs = now
            }
        }
        1 -> {
            // Stage 1: hold sneak, wait until can warp
            applyWarpLook(player, aimPos)
            mc.options.keyShift?.setDown(true)
            val canWarp = level.getBlockState(targetPos).isAir ||
                !level.getBlockState(targetPos.above()).blocksMotion()
            if (!canWarp && warpStageElapsedMs >= WARP_STAGE1_TIMEOUT_MS) {
                mc.options.keyShift?.setDown(false)
                warpCooldownUntil = level.gameTime + WARP_RETRY_COOLDOWN_TICKS
                cancelWarp()
                advanceAndNavigate()
                return
            }
            if (warpStageElapsedMs >= WARP_SNEAK_MS) {
                mc.options.keyUse?.setDown(true)
                warpStage = 2
                warpStageElapsedMs = 0.0
                warpStageLastNs = now
            }
        }
        2 -> {
            // Stage 2: post-warp — hold briefly then complete
            mc.options.keyShift?.setDown(true)
            if (warpStageElapsedMs >= WARP_POST_MS) {
                mc.options.keyUse?.setDown(false)
                mc.options.keyShift?.setDown(false)
                cancelWarp()
                // Check if at kill point
                val point = patrolPoints.getOrNull(routeIndex)
                if (point?.type == CombatPatrolPointType.KILL) {
                    NativePathfinder.stop()
                    patrolOwnsPathfinder = false
                    patrolState = PatrolState.AT_KILL_ZONE
                    killZoneClearTicks = 0
                } else {
                    advanceAndNavigate()
                }
            }
        }
    }
}

private fun applyWarpLook(
    player: net.minecraft.client.player.LocalPlayer,
    aimPos: net.minecraft.world.phys.Vec3
) {
    val now = System.nanoTime()
    val dtSec = if (warpLookLastNs == 0L) (1.0 / 20.0)
    else ((now - warpLookLastNs) / 1_000_000_000.0).coerceIn(1.0 / 240.0, 0.08)
    warpLookLastNs = now

    val target = org.cobalt.api.util.AngleUtils.getRotation(aimPos, player)
    val maxYaw = WARP_LOOK_YAW_SPEED_DPS * dtSec
    val maxPitch = WARP_LOOK_PITCH_SPEED_DPS * dtSec
    val yawDelta = org.cobalt.api.util.AngleUtils.getRotationDelta(player.yRot, target.yaw).toDouble()
    val pitchDelta = (target.pitch - player.xRot).toDouble()
    player.yRot += yawDelta.coerceIn(-maxYaw, maxYaw).toFloat()
    player.xRot += pitchDelta.coerceIn(-maxPitch, maxPitch).toFloat()
}
```

- [ ] **Step 5: Update `navigateTo` to call `startWarpPoint` for WARP type**

Replace the WARP fallback in `navigateTo`:
```kotlin
CombatPatrolPointType.WARP -> {
    if (!startWarpPoint(point)) {
        // AOTV not found — fall back to walking
        NativePathfinder.setTarget(point.x + 0.5, point.y.toDouble(), point.z + 0.5)
        patrolOwnsPathfinder = true
        patrolState = PatrolState.NAVIGATING
    }
}
```

- [ ] **Step 6: Build**

```bash
./gradlew build
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 7: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/combat/CombatPatrolModule.kt
git commit -m "feat(combat-patrol): WARP point type — etherwarp state machine"
```

---

## Task 6: Final build verification and cleanup

- [ ] **Step 1: Full clean build**

```bash
./gradlew clean build
```
Expected: BUILD SUCCESSFUL, JAR in `build/libs/`

- [ ] **Step 2: Verify module appears in client**

Run `./gradlew runClient`, open Cobalt UI — confirm "Combat Patrol" module is present with all settings visible.

- [ ] **Step 3: Smoke test Walk points**

1. Enable Combat Patrol, enable Combat Macro
2. Add 3–4 Walk points in a patrol area
3. Start Patrol — confirm player walks from point to point in order
4. Confirm when a mob appears the patrol pauses and combat macro attacks
5. Confirm after kill patrol resumes from the same waypoint

- [ ] **Step 4: Smoke test Kill zone**

1. Add a Kill point in a mob-dense area
2. Start Patrol — confirm player arrives at Kill point, enters AT_KILL_ZONE, fights mobs in zone
3. After mobs die, confirm `killZoneDwellTicks` worth of ticks pass then patrol moves to next point

- [ ] **Step 5: Final commit**

```bash
git add src/main/kotlin/org/cobalt/internal/combat/CombatPatrolModule.kt \
        src/main/kotlin/org/cobalt/internal/combat/CombatMacroModule.kt \
        src/main/kotlin/org/cobalt/Cobalt.kt
git commit -m "feat(combat-patrol): complete implementation — Walk, Warp, Kill patrol routes"
```
