# Pathfinder Long-Distance & Kill-Waypoint Patrol — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Enable pathfinding over hundreds of blocks through complex terrain via progressive frontier A*, and add a slayer-macro patrol system that randomly visits designated kill spots along a recorded tunnel route.

**Architecture:** C++ side gains frontier-tracking in `AStarPlanner` — when a goal is outside the world buffer it returns a partial path to the best reachable node and lets `PathExecutor`'s existing REPLANNING loop re-plan from there. Kotlin side gains a `PatrolWaypointStore` for persistence and patrol state-machine logic inside `PathfindingModule`.

**Tech Stack:** C++17 (MSVC/CMake), Kotlin, Fabric Minecraft 1.21.11, Gson for JSON persistence.

**Spec:** `docs/superpowers/specs/2026-03-22-pathfinder-long-distance-patrol-design.md`

---

## File Map

| File | Action | Responsibility |
|---|---|---|
| `natives/src/engine/AStarPlanner.h` | Modify | Add `isPartial` to `AStarResult`; no new fields on class |
| `natives/src/engine/AStarPlanner.cpp` | Modify | Frontier tracking, partial return, MAX_ITER 150k |
| `natives/src/engine/WorldAccessor.h` | Modify | Make `inBuffer` public |
| `natives/src/engine/PathExecutor.cpp` | Modify | Guard both PLANNING/REPLANNING FAILED transitions against isPartial |
| `src/main/kotlin/org/cobalt/internal/pathfinding/PatrolWaypointStore.kt` | Create | Serialize/deserialize routeWaypoints + killWaypoints to separate JSON file |
| `src/main/kotlin/org/cobalt/internal/pathfinding/PathfindingModule.kt` | Modify | Patrol state machine, route/kill waypoint lists, settings, buildSubRoute, right-click handler |

---

## Task 1: Make `inBuffer` public in WorldAccessor

`AStarPlanner::startAsync` needs to call `world.inBuffer(goal)` before launching its thread (checked on the main thread where `world` is still valid). Currently `inBuffer` is private.

**Files:**
- Modify: `natives/src/engine/WorldAccessor.h`

- [ ] **Step 1: Move `inBuffer` declaration from private to public**

Open `natives/src/engine/WorldAccessor.h`. The current layout is:
```cpp
public:
    void setBuffer(...);
    uint8_t getBlock(...) const;
    bool isSolid(...) const;
    bool isPassable(...) const;
    bool isWalkable(...) const;
    bool isLadder(...) const;
    bool isWater(...) const;
    bool isLava(...) const;
    const uint8_t* bufferData() const { return buffer_; }
    int originX() const { return bx_; }
    int originY() const { return by_; }
    int originZ() const { return bz_; }

private:
    const uint8_t* buffer_ = nullptr;
    int bx_ = 0, by_ = 0, bz_ = 0;
    bool inBuffer(int x, int y, int z) const;   // ← move this line up
    uint8_t bufferAt(int x, int y, int z) const;
    uint8_t callbackBlock(int x, int y, int z) const;
```

Move the `inBuffer` declaration into the `public:` section (add it after `isLava`). Leave `bufferAt` and `callbackBlock` private. Result:
```cpp
public:
    // ... existing public methods ...
    bool isLava(int x, int y, int z) const;
    bool inBuffer(int x, int y, int z) const;   // ← now public

private:
    const uint8_t* buffer_ = nullptr;
    int bx_ = 0, by_ = 0, bz_ = 0;
    uint8_t bufferAt(int x, int y, int z) const;
    uint8_t callbackBlock(int x, int y, int z) const;
```

- [ ] **Step 2: Verify build**

```bash
cmake --build natives/build --config Release 2>&1 | tail -10
```
Expected: no errors, `cobalt_pathfinder.dll` rebuilt.

- [ ] **Step 3: Commit**

```bash
git add natives/src/engine/WorldAccessor.h
git commit -m "feat: make WorldAccessor::inBuffer public for frontier A*"
```

---

## Task 2: Add `isPartial` to `AStarResult`

> **Note:** The spec's Files Touched table incorrectly lists `natives/include/Types.h` for this change. `AStarResult` is actually defined in `natives/src/engine/AStarPlanner.h` (lines 10–13). Edit only `AStarPlanner.h`.

**Files:**
- Modify: `natives/src/engine/AStarPlanner.h`

- [ ] **Step 1: Add `isPartial` field to `AStarResult`**

Current struct in `AStarPlanner.h`:
```cpp
struct AStarResult {
    bool found = false;
    std::vector<Vec3i> nodes;
};
```

Change to:
```cpp
struct AStarResult {
    bool found     = false;
    bool isPartial = false;   // true = path leads to best frontier node, not the real goal
    std::vector<Vec3i> nodes;
};
```

- [ ] **Step 2: Verify build**

```bash
cmake --build natives/build --config Release 2>&1 | tail -10
```
Expected: no errors.

- [ ] **Step 3: Commit**

```bash
git add natives/src/engine/AStarPlanner.h
git commit -m "feat: add isPartial flag to AStarResult"
```

---

## Task 3: Implement frontier A* in `AStarPlanner::startAsync`

This is the core C++ change. When the goal is outside the world buffer, A* tracks the best reachable node (closest to goal), and on exhaustion returns a partial path to it instead of failing.

**Files:**
- Modify: `natives/src/engine/AStarPlanner.cpp`

- [ ] **Step 1: Raise MAX_ITER and add goalInBuffer check before thread launch**

In `AStarPlanner::startAsync`, `MAX_ITER` is declared inside the thread lambda (line 92 of `AStarPlanner.cpp`). Change its value in place — do not move it:
```cpp
const int MAX_ITER = 150000;
```

Then add a `goalInBuffer` check **before** the `thread_ = std::thread(...)` call (while still on the main thread, where `world` is valid):
```cpp
bool goalInBuffer = world.inBuffer(goal.x, goal.y, goal.z);
```
Capture `goalInBuffer` into the thread lambda's capture list: `[this, start, goal, costs, goalInBuffer, bufSnapshot = std::move(bufSnapshot), snapBx, snapBy, snapBz]`.

- [ ] **Step 2: Add frontier tracking variables inside the lambda**

Inside the thread lambda, after the `AStarResult res;` and `int iters = 0;` declarations, add:
```cpp
Vec3i bestFrontierNode = start;
float bestFrontierDist = heuristic(start, goal);   // initial = distance from start
bool  frontierAdvanced = false;
```

- [ ] **Step 3: Update frontier during node visits**

Inside the while loop, after `auto [f, cur] = open.top(); open.pop();`, add:
```cpp
if (!goalInBuffer) {
    float d = heuristic(cur, goal);
    if (d < bestFrontierDist) {
        bestFrontierDist  = d;
        bestFrontierNode  = cur;
        frontierAdvanced  = true;
    }
}
```

- [ ] **Step 4: Add partial path reconstruction on loop exit**

After the while loop ends (before the `std::lock_guard` block), add:
```cpp
if (!res.found && !goalInBuffer && frontierAdvanced) {
    // Reconstruct path to best frontier node
    std::vector<Vec3i> path;
    Vec3i c = bestFrontierNode;
    while (!(c.x == start.x && c.y == start.y && c.z == start.z)) {
        path.push_back(c);
        auto it = parent.find(c);
        if (it == parent.end()) { path.clear(); break; }
        c = it->second;
    }
    if (!path.empty()) {
        path.push_back(start);
        std::reverse(path.begin(), path.end());
        res.found     = false;
        res.isPartial = true;
        res.nodes     = std::move(path);
    }
    // If path reconstruction failed (no parent chain), leave res.found=false, isPartial=false → FAILED
}
```

- [ ] **Step 5: Verify build**

```bash
cmake --build natives/build --config Release 2>&1 | tail -10
```
Expected: no errors, DLL rebuilt.

- [ ] **Step 6: Commit**

```bash
git add natives/src/engine/AStarPlanner.cpp
git commit -m "feat: frontier A* — partial path to best reachable node when goal outside buffer"
```

---

## Task 4: Update `PathExecutor` FAILED guards to respect `isPartial`

Both the `PLANNING` and `REPLANNING` handlers in `PathExecutor::tick` currently unconditionally transition to FAILED when `!res.found`. A partial result must not trigger FAILED — the partial path should be executed and REPLANNING will re-plan from the frontier.

**Files:**
- Modify: `natives/src/engine/PathExecutor.cpp`

- [ ] **Step 1: Update the PLANNING handler**

Find (around line 92):
```cpp
if (!res.found) { status_ = PathStatus::FAILED; return idleCmd(yaw, pitch); }
```
Change to:
```cpp
if (!res.found && !res.isPartial) { status_ = PathStatus::FAILED; return idleCmd(yaw, pitch); }
```

- [ ] **Step 2: Update the REPLANNING handler**

Find the identical guard in the `REPLANNING` case (around line 106):
```cpp
if (!res.found) { status_ = PathStatus::FAILED; return idleCmd(yaw, pitch); }
```
Change to:
```cpp
if (!res.found && !res.isPartial) { status_ = PathStatus::FAILED; return idleCmd(yaw, pitch); }
```

- [ ] **Step 3: Verify build**

```bash
cmake --build natives/build --config Release 2>&1 | tail -10
```
Expected: no errors.

- [ ] **Step 4: Copy DLL to resources**

```bash
copy /Y natives\build\Release\cobalt_pathfinder.dll src\main\resources\natives\windows\
```

- [ ] **Step 5: Commit**

```bash
git add natives/src/engine/PathExecutor.cpp src/main/resources/natives/windows/cobalt_pathfinder.dll
git commit -m "feat: treat isPartial A* result as valid — enables progressive re-planning for long paths"
```

---

## Task 5: Create `PatrolWaypointStore` for persistence

The Config system only serializes `Setting` values. Route waypoints and kill waypoints are `List<data class>` and need a separate JSON file. This class owns that file.

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/pathfinding/PatrolWaypointStore.kt`

- [ ] **Step 1: Create the file**

```kotlin
package org.cobalt.internal.pathfinding

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import java.io.File
import net.minecraft.client.Minecraft

data class RouteWaypoint(val x: Double, val y: Double, val z: Double)
data class KillWaypoint(val x: Double, val y: Double, val z: Double, val label: String = "")

internal object PatrolWaypointStore {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val file: File by lazy {
        File(Minecraft.getInstance().gameDirectory, "config/cobalt/patrol_waypoints.json")
    }

    var routeWaypoints: MutableList<RouteWaypoint> = mutableListOf()
    var killWaypoints:  MutableList<KillWaypoint>  = mutableListOf()

    fun load() {
        routeWaypoints.clear()   // idempotent — prevent double-append on reload
        killWaypoints.clear()
        if (!file.exists()) return
        val text = file.bufferedReader().use { it.readText() }
        if (text.isBlank()) return
        runCatching {
            val root = JsonParser.parseString(text).asJsonObject
            root.getAsJsonArray("routeWaypoints")?.forEach { el ->
                val o = el.asJsonObject
                routeWaypoints.add(RouteWaypoint(
                    o["x"].asDouble, o["y"].asDouble, o["z"].asDouble
                ))
            }
            root.getAsJsonArray("killWaypoints")?.forEach { el ->
                val o = el.asJsonObject
                killWaypoints.add(KillWaypoint(
                    o["x"].asDouble, o["y"].asDouble, o["z"].asDouble,
                    o["label"]?.asString ?: ""
                ))
            }
        }
    }

    fun save() {
        file.parentFile?.mkdirs()
        val root = com.google.gson.JsonObject()

        val rArr = JsonArray()
        routeWaypoints.forEach { wp ->
            val o = com.google.gson.JsonObject()
            o.addProperty("x", wp.x); o.addProperty("y", wp.y); o.addProperty("z", wp.z)
            rArr.add(o)
        }
        root.add("routeWaypoints", rArr)

        val kArr = JsonArray()
        killWaypoints.forEach { wp ->
            val o = com.google.gson.JsonObject()
            o.addProperty("x", wp.x); o.addProperty("y", wp.y); o.addProperty("z", wp.z)
            o.addProperty("label", wp.label)
            kArr.add(o)
        }
        root.add("killWaypoints", kArr)

        file.bufferedWriter().use { it.write(gson.toJson(root)) }
    }
}
```

- [ ] **Step 2: Call `load()` from `Cobalt.onInitializeClient()`**

Open `src/main/kotlin/org/cobalt/Cobalt.kt`. Find the section that calls `Config.loadModulesConfig()` (or equivalent startup). Add after it:
```kotlin
PatrolWaypointStore.load()
```

- [ ] **Step 3: Build**

```bash
./gradlew build 2>&1 | tail -5
```
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/pathfinding/PatrolWaypointStore.kt src/main/kotlin/org/cobalt/Cobalt.kt
git commit -m "feat: PatrolWaypointStore — persist route and kill waypoints to patrol_waypoints.json"
```

---

## Task 6: Add patrol state machine and settings to `PathfindingModule`

This is the largest Kotlin change. Read the full current file before editing (`src/main/kotlin/org/cobalt/internal/pathfinding/PathfindingModule.kt`).

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/pathfinding/PathfindingModule.kt`

- [ ] **Step 1: Add imports and patrol state enum at top of file**

Add these imports alongside the existing ones:
```kotlin
import org.cobalt.internal.pathfinding.PatrolWaypointStore
import org.cobalt.internal.pathfinding.RouteWaypoint
import org.cobalt.internal.pathfinding.KillWaypoint
import org.cobalt.api.pathfinder.jni.MovementProfile
import kotlin.math.sqrt
```

Add a private enum inside the object (before the settings block):
```kotlin
private enum class PatrolState { IDLE, NAVIGATING, AT_KILL }
```

- [ ] **Step 2: Add patrol state fields**

Inside the object, alongside the existing `moduleOwnsPath` field:
```kotlin
private var patrolState: PatrolState = PatrolState.IDLE
private var currentKillWp: KillWaypoint? = null
private var dwellTicksRemaining: Int = 0
```
(No `recordingKillMode` field — that is now the `CheckboxSetting` named `recordingKillPoints`.)

- [ ] **Step 3: Add patrol settings**

In the `init { addSetting(...) }` block, add the following settings (declare them as `private val` properties alongside the existing settings before `init`):

```kotlin
private val patrolModeEnabled = CheckboxSetting(
    "Patrol Mode",
    "Randomly patrol between kill waypoints using the recorded route.",
    false
)

private val recordRouteAction = ActionSetting(
    "Record Route Point",
    "Appends your current position to the patrol route.",
    "Record"
) {
    val player = mc.player ?: return@ActionSetting
    PatrolWaypointStore.routeWaypoints.add(RouteWaypoint(player.x, player.y, player.z))
    PatrolWaypointStore.save()
    ChatUtils.sendMessage("Route point recorded (${PatrolWaypointStore.routeWaypoints.size} total).")
}

private val clearRouteAction = ActionSetting(
    "Clear Route",
    "Removes all recorded route waypoints.",
    "Clear"
) {
    PatrolWaypointStore.routeWaypoints.clear()
    PatrolWaypointStore.save()
    ChatUtils.sendMessage("Route cleared.")
}

private val recordingKillPoints = CheckboxSetting(
    "Record Kill Points",
    "Right-click a block to record a kill waypoint while this is enabled.",
    false
)

private val clearKillAction = ActionSetting(
    "Clear Kill Points",
    "Removes all recorded kill waypoints.",
    "Clear"
) {
    PatrolWaypointStore.killWaypoints.clear()
    PatrolWaypointStore.save()
    ChatUtils.sendMessage("Kill waypoints cleared.")
}

private val killDwellTicks = SliderSetting(
    "Kill Dwell Ticks",
    "Ticks to wait at a kill spot before moving to the next.",
    20.0, 0.0, 200.0
)

private val routeCountInfo = InfoSetting(
    "Route Points",
    "Number of recorded route waypoints.",
    InfoType.INFO
)

private val killCountInfo = InfoSetting(
    "Kill Points",
    "Number of recorded kill waypoints.",
    InfoType.INFO
)
```

Add all of them to `addSetting(...)` in `init`. Place them after the existing `stopAction`.

- [ ] **Step 4: Add `buildSubRoute` function**

Add this private function to the object:

```kotlin
private fun buildSubRoute(
    fromX: Double, fromY: Double, fromZ: Double,
    killWp: KillWaypoint
): DoubleArray {
    val route = PatrolWaypointStore.routeWaypoints
    if (route.isEmpty()) {
        // No route — navigate directly (player position + kill target)
        return doubleArrayOf(fromX, fromY, fromZ, killWp.x, killWp.y, killWp.z)
    }

    fun dist3(wp: RouteWaypoint, x: Double, y: Double, z: Double): Double {
        val dx = wp.x - x; val dy = wp.y - y; val dz = wp.z - z
        return sqrt(dx*dx + dy*dy + dz*dz)
    }

    val j = route.indices.minByOrNull { dist3(route[it], fromX, fromY, fromZ) } ?: 0
    val i = route.indices.minByOrNull { dist3(route[it], killWp.x, killWp.y, killWp.z) } ?: 0

    val slice: List<RouteWaypoint> = if (j == i) {
        emptyList()
    } else {
        val fwd = if (j <= i) route.subList(j, i + 1) else emptyList()
        val bwd = if (i <= j) route.subList(i, j + 1).reversed() else emptyList()
        when {
            fwd.isEmpty() -> bwd
            bwd.isEmpty() -> fwd
            else          -> if (fwd.size <= bwd.size) fwd else bwd
        }
    }

    // Build flat double[] for NativePathfinder.setRoute: [x0,y0,z0, x1,y1,z1, ...]
    val points = mutableListOf<Double>()
    // Start at player position
    points += listOf(fromX, fromY, fromZ)
    // Route slice
    slice.forEach { wp -> points += listOf(wp.x, wp.y, wp.z) }
    // Kill waypoint as final destination
    points += listOf(killWp.x, killWp.y, killWp.z)

    return points.toDoubleArray()
}
```

- [ ] **Step 5: Add `startPatrol` and `stopPatrol` functions**

```kotlin
fun startPatrol() {
    if (PatrolWaypointStore.killWaypoints.size < 2) {
        ChatUtils.sendMessage("Need at least 2 kill waypoints to patrol. Use 'Record Kill Points'.")
        return
    }
    if (!enabled.value) ensureEnabledForAutomation("patrol")
    val first = PatrolWaypointStore.killWaypoints.random()
    navigateToKill(first)
}

private fun navigateToKill(target: KillWaypoint) {
    val player = mc.player ?: return
    currentKillWp = target
    val waypoints = buildSubRoute(player.x, player.y, player.z, target)
    NativePathfinder.setRoute(waypoints, loop = false, profile = MovementProfile.DEFAULT)
    moduleOwnsPath = true
    patrolState = PatrolState.NAVIGATING
    ChatUtils.sendMessage("Patrolling to kill spot (${PatrolWaypointStore.killWaypoints.indexOf(target) + 1}/${PatrolWaypointStore.killWaypoints.size}).")
}

fun stopPatrol() {
    patrolState = PatrolState.IDLE
    stopPath()
}
```

- [ ] **Step 6: Extend `onTick` to drive the patrol state machine**

The current `onTick` has an early-return guard `if (!enabled.value || !moduleOwnsPath) return`. The patrol state machine must be checked regardless of `moduleOwnsPath`. Replace the current `onTick` body with:

```kotlin
@SubscribeEvent
fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    org.cobalt.internal.pathfinding.DebugLog.debugFileEnabled = debugFileLogging.value

    // Update live info settings
    routeCountInfo.value = "${PatrolWaypointStore.routeWaypoints.size} points"
    killCountInfo.value  = "${PatrolWaypointStore.killWaypoints.size} points"

    // Patrol state machine — runs independently of moduleOwnsPath
    if (patrolModeEnabled.value && patrolState != PatrolState.IDLE) {
        when (patrolState) {
            PatrolState.NAVIGATING -> {
                val nativeStatus = NativePathfinder.status
                if (nativeStatus == PathStatus.ARRIVED || nativeStatus == PathStatus.FAILED) {
                    dwellTicksRemaining = killDwellTicks.value.toInt()
                    patrolState = PatrolState.AT_KILL
                }
            }
            PatrolState.AT_KILL -> {
                if (dwellTicksRemaining-- <= 0) {
                    val kills = PatrolWaypointStore.killWaypoints
                    val next = kills.filter { it != currentKillWp }
                    if (next.isEmpty()) {
                        stopPatrol()
                        ChatUtils.sendMessage("No other kill waypoints available.")
                    } else {
                        navigateToKill(next.random())
                    }
                }
            }
            PatrolState.IDLE -> { /* nothing */ }
        }
    }

    if (!enabled.value || !moduleOwnsPath) return

    val cmd = NativePathfinder.tick()
    if (cmd != null) {
        cmd.applyToPlayer()
        if (cmd.activeAction == ActionType.AOTV) {
            val player = mc.player
            if (player != null) {
                val prevSlot = player.inventory.selectedSlot
                player.inventory.selectedSlot = aotvSlot.value
                mc.gameMode?.useItem(player, InteractionHand.MAIN_HAND)
                player.inventory.selectedSlot = prevSlot
            }
        }
    } else {
        val s = NativePathfinder.status
        if (s == PathStatus.IDLE || s == PathStatus.ARRIVED || s == PathStatus.FAILED) {
            moduleOwnsPath = false
            MovementManager.setMovementLock(false)
        }
    }
}
```

- [ ] **Step 7: Update `onRightClick` to record kill waypoints**

Replace the existing no-op `onRightClick`:
```kotlin
@SubscribeEvent
fun onRightClick(event: MouseEvent.RightClick) {
    if (!enabled.value || !recordingKillPoints.value) return
    val mc = Minecraft.getInstance()
    val hit = mc.hitResult ?: return
    if (hit !is net.minecraft.world.phys.BlockHitResult) return
    val pos = hit.blockPos
    val wp = KillWaypoint(pos.x + 0.5, pos.y + 1.0, pos.z + 0.5)
    PatrolWaypointStore.killWaypoints.add(wp)
    PatrolWaypointStore.save()
    ChatUtils.sendMessage("Kill waypoint recorded (${PatrolWaypointStore.killWaypoints.size} total).")
}
```

- [ ] **Step 8: Add a `startPatrolAction` setting and wire it up**

Add alongside the other ActionSettings:
```kotlin
private val startPatrolAction = ActionSetting(
    "Start Patrol",
    "Start patrolling between kill waypoints.",
    "Start Patrol"
) {
    startPatrol()
}

private val stopPatrolAction = ActionSetting(
    "Stop Patrol",
    "Stop the patrol loop.",
    "Stop Patrol"
) {
    stopPatrol()
}
```

Add both to `addSetting(...)`.

- [ ] **Step 9: Build**

```bash
./gradlew build 2>&1 | tail -5
```
Expected: `BUILD SUCCESSFUL`. Fix any compilation errors before proceeding.

- [ ] **Step 10: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/pathfinding/PathfindingModule.kt
git commit -m "feat: kill-waypoint patrol — route-guided random patrol with right-click kill point recording"
```

---

## Task 7: Deploy and verify

- [ ] **Step 1: Deploy to Prism**

```bash
./gradlew deployMod 2>&1 | tail -5
```
Expected: `BUILD SUCCESSFUL`, `cobalt-1.0.1.jar` copied to Prism mods folder.

- [ ] **Step 2: In-game verification checklist**

Launch the game and verify:

**Long-distance pathfinding:**
1. Use `/cobalt setpos` to set a target 100+ blocks away.
2. Click Start in the Pathfinding module.
3. Observe: bot begins moving toward target, re-plans every ~25 blocks, navigates around obstacles. Path spline updates each re-plan.
4. Confirm arrival or near-arrival (within `arrivalRadius`).

**Route recording:**
1. Walk through your patrol tunnel.
2. Click "Record Route Point" every ~10 blocks.
3. Verify route count InfoSetting increments.

**Kill waypoint recording:**
1. Click "Record Kill Points" to enter recording mode (chat confirms).
2. Right-click mob spawn blocks in the tunnel.
3. Verify kill count InfoSetting increments.
4. `/cobalt setpos` not needed — kill points are recorded separately.

**Patrol:**
1. Ensure ≥ 2 kill waypoints and route waypoints are recorded.
2. Enable Patrol Mode and click "Start Patrol".
3. Observe: bot navigates through the tunnel route to a random kill spot, dwells, then moves to a different kill spot. Never takes the same route two consecutive times (usually).
4. Click "Stop Patrol" — bot stops and movement unlocks.

- [ ] **Step 3: Final commit (if any last-minute fixes)**

```bash
git add -p
git commit -m "fix: <describe any in-game fix>"
```

---

## Notes

- **No unit tests** — this project has none. Verification is in-game only.
- **Patrol auto-save** — `PatrolWaypointStore.save()` is called inline from each `ActionSetting` lambda (record/clear). Waypoints are saved immediately on each action rather than waiting for the UI save button. This is intentional.
- **Disabling Patrol Mode mid-navigation** — unchecking `Patrol Mode` while `patrolState == NAVIGATING` stops the patrol from advancing but lets the current leg finish (bot walks to the frontier and stops). Use "Stop Patrol" button for an immediate clean stop.
- **Config system** — route/kill waypoints use a separate file (`config/cobalt/patrol_waypoints.json`), not `addons.json`, because the Config system only serializes `Setting` values.
- **InfoSetting.value** — if `InfoSetting` does not have a mutable `value` property, use a different approach to display counts (e.g., a read-only label in the UI that you update via a different mechanism, or just omit the live count and add it later).
- **`MovementProfile` import** — verify the correct package path: `org.cobalt.api.pathfinder.jni.MovementProfile`.
- **`NativePathfinder.setRoute` signature** — takes `DoubleArray`, `Boolean`, `MovementProfile`. Confirm at `src/main/kotlin/org/cobalt/api/pathfinder/jni/NativePathfinder.kt:54`.
