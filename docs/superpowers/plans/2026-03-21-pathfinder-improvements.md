# Pathfinder Improvements Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add tunnel-centering, smooth spline path rendering, head-direction ray, and AOTV auto-fire to the native C++ pathfinder.

**Architecture:** Five targeted changes — (1) clearance cost in C++ MovementExpander, (2) path node exposure through the JNI bridge, (3) Catmull-Rom spline renderer in Kotlin, (4) head-direction ray per frame, (5) AOTV auto-fire (hotbar swap + right-click) when the engine returns an AOTV action.

**Tech Stack:** C++17, JNI, Java 21, Kotlin, Fabric MC 1.21.11, Minecraft Gizmos API via OverlayRenderEngine

---

## File Map

| File | Change |
|------|--------|
| `natives/src/engine/MovementExpander.cpp` | Add `clearanceCost()` helper; apply penalty in `addWalkSprint` |
| `natives/src/engine/PathExecutor.h` | Add `getActivePath()` accessor |
| `natives/src/engine/PathfinderEngine.h` | Add `getPathNodes(out)` declaration |
| `natives/src/engine/PathfinderEngine.cpp` | Implement `getPathNodes()` |
| `natives/src/pathfinder_jni.cpp` | Add `Java_..._getPathNodes` JNI function |
| `src/main/java/org/cobalt/pathfinder/NativePathfinderBridge.java` | Declare `getPathNodes(long handle): FloatArray` |
| `src/main/kotlin/org/cobalt/api/pathfinder/jni/NativePathfinder.kt` | Cache path nodes after tick; expose `cachedPathNodes` |
| `src/main/kotlin/org/cobalt/internal/pathfinding/PathSplineRenderer.kt` | **New** — Catmull-Rom interpolation + AOTV segment detection |
| `src/main/kotlin/org/cobalt/internal/pathfinding/PathfindingModule.kt` | Add `aotvSlot` setting; wire AOTV fire + spline/head-ray rendering |

---

## Task 1: C++ Clearance Cost in MovementExpander

**Files:**
- Modify: `natives/src/engine/MovementExpander.cpp`

Add a `clearanceCost(x, y, z)` helper that counts open cardinal neighbors at the node's Y and returns a penalty. Apply it to all walk/sprint candidate nodes in `addWalkSprint`.

- [ ] **Step 1: Add `clearanceCost` helper after `adjacentCost` (line 17)**

```cpp
float MovementExpander::clearanceCost(int x, int y, int z) const {
    int open = 0;
    for (int i = 0; i < 4; i++) {
        if (world_.isPassable(x + DX4[i], y, z + DZ4[i])) open++;
    }
    // open=4 free, open=3 -> +0.1, open=2 -> +0.3, open=1 -> +0.5
    static const float penalty[5] = {0.5f, 0.5f, 0.3f, 0.1f, 0.0f};
    return penalty[open];
}
```

- [ ] **Step 2: Add declaration to `MovementExpander.h`**

In `natives/src/engine/MovementExpander.h`, add inside the `private:` section alongside `adjacentCost`:
```cpp
float clearanceCost(int x, int y, int z) const;
```

- [ ] **Step 3: Apply clearanceCost in `addWalkSprint`**

In `MovementExpander.cpp`, update the `out.push_back` line inside `addWalkSprint` (currently line 31):
```cpp
// Before:
out.push_back({{nx, from.y, nz}, act, base + adjacentCost({nx, from.y, nz})});
// After:
out.push_back({{nx, from.y, nz}, act, base + adjacentCost({nx, from.y, nz}) + clearanceCost(nx, from.y, nz)});
```

- [ ] **Step 4: Build native DLL**

```bash
./gradlew buildNative copyNativeDll
```
Expected: BUILD SUCCESSFUL, updated DLL in `src/main/resources/natives/windows/`

- [ ] **Step 5: Commit**

```bash
git add natives/src/engine/MovementExpander.h natives/src/engine/MovementExpander.cpp src/main/resources/natives/
git commit -m "feat: add tunnel clearance cost to C++ pathfinder (Task 1)"
```

---

## Task 2: Expose Path Nodes Through JNI

**Files:**
- Modify: `natives/src/engine/PathExecutor.h`
- Modify: `natives/src/engine/PathfinderEngine.h`
- Modify: `natives/src/engine/PathfinderEngine.cpp`
- Modify: `natives/src/pathfinder_jni.cpp`
- Modify: `src/main/java/org/cobalt/pathfinder/NativePathfinderBridge.java`

**Context:** `activePath_` in `PathExecutor` is a `std::vector<Vec3i>`. It is only ever written on the game thread (inside `tick()` → `update()` → JNI). `getPathNodes()` is also called from the game tick thread (at the end of `NativePathfinder.tick()`), so there is no race condition — no mutex needed. The C++ packer adds `+0.5` to X and Z (block-center) but not Y (feet level). Return type is `jfloatArray` — float precision is sufficient for integer coordinates.

- [ ] **Step 1: Add `getActivePath()` accessor to `PathExecutor.h`**

Add this public accessor after `getStatus()` (line 22):
```cpp
const std::vector<Vec3i>& getActivePath() const { return activePath_; }
```

- [ ] **Step 2: Add `getPathNodes()` declaration to `PathfinderEngine.h`**

Add after `getStatus()` declaration (line 19):
```cpp
void getPathNodes(std::vector<Vec3i>& out) const;
```

- [ ] **Step 3: Implement `getPathNodes()` in `PathfinderEngine.cpp`**

Add after `getStatus()` on line 27:
```cpp
void PathfinderEngine::getPathNodes(std::vector<Vec3i>& out) const {
    out = executor_.getActivePath();
}
```

- [ ] **Step 4: Add JNI function to `pathfinder_jni.cpp`**

Add before the closing `} // extern "C"` brace:
```cpp
JNIEXPORT jfloatArray JNICALL
Java_org_cobalt_pathfinder_NativePathfinderBridge_getPathNodes(JNIEnv* env, jclass, jlong handle) {
    std::vector<Vec3i> nodes;
    ((PathfinderEngine*)handle)->getPathNodes(nodes);
    jfloatArray arr = env->NewFloatArray((jsize)(nodes.size() * 3));
    if (nodes.empty()) return arr;
    std::vector<jfloat> data;
    data.reserve(nodes.size() * 3);
    for (const auto& n : nodes) {
        data.push_back((jfloat)(n.x + 0.5));  // block-center X
        data.push_back((jfloat)(n.y));          // feet Y (no offset)
        data.push_back((jfloat)(n.z + 0.5));  // block-center Z
    }
    env->SetFloatArrayRegion(arr, 0, (jsize)data.size(), data.data());
    return arr;
}
```

- [ ] **Step 5: Declare `getPathNodes` in `NativePathfinderBridge.java`**

Add after the `getStatus` declaration (line 38):
```java
/** Returns float[] packed as [x0,y0,z0, x1,y1,z1, ...] with block-center X/Z offsets (+0.5). */
public static native float[] getPathNodes(long handle);
```

- [ ] **Step 6: Build native DLL**

```bash
./gradlew buildNative copyNativeDll
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 7: Commit**

```bash
git add natives/src/engine/PathExecutor.h natives/src/engine/PathfinderEngine.h natives/src/engine/PathfinderEngine.cpp natives/src/pathfinder_jni.cpp src/main/java/org/cobalt/pathfinder/NativePathfinderBridge.java src/main/resources/natives/
git commit -m "feat: expose path nodes via JNI getPathNodes() (Task 2)"
```

---

## Task 3: Cache Path Nodes in NativePathfinder.kt

**Files:**
- Modify: `src/main/kotlin/org/cobalt/api/pathfinder/jni/NativePathfinder.kt`

Cache `cachedPathNodes` whenever the engine transitions into `EXECUTING`. On `REPLANNING`, intentionally keep the old cached nodes (the engine continues walking the old path — the new path arrives on the next `EXECUTING` transition, which triggers a refresh then). On `PLANNING`, neither refresh nor clear (no path exists yet). On `IDLE`/`ARRIVED`/`FAILED`, clear the cache.

- [ ] **Step 1: Add cache fields and `cachedPathNodes` property**

Add after `val isInitialized` line (after line 20):
```kotlin
/** Cached path nodes as Vec3 list; updated on EXECUTING transition. Call only from game tick thread. */
var cachedPathNodes: List<net.minecraft.world.phys.Vec3> = emptyList()
    private set

private var lastTickStatus: PathStatus = PathStatus.IDLE
```

- [ ] **Step 2: Add `refreshPathNodes()` private function**

Add before the closing `}` of the object:
```kotlin
private fun refreshPathNodes() {
    if (handle == 0L) { cachedPathNodes = emptyList(); return }
    val raw = NativePathfinderBridge.getPathNodes(handle)
    val result = ArrayList<net.minecraft.world.phys.Vec3>(raw.size / 3)
    var i = 0
    while (i + 2 < raw.size) {
        result.add(net.minecraft.world.phys.Vec3(raw[i].toDouble(), raw[i + 1].toDouble(), raw[i + 2].toDouble()))
        i += 3
    }
    cachedPathNodes = result
}
```

- [ ] **Step 3: Wire cache refresh at the end of `tick()`**

In `tick()`, after `val parsedStatus = ...` and before the `if (parsedStatus == PathStatus.IDLE || ...)` early-return block, insert:
```kotlin
// Refresh node cache on EXECUTING transition; clear when path ends
when {
    parsedStatus == PathStatus.EXECUTING && lastTickStatus != PathStatus.EXECUTING -> refreshPathNodes()
    parsedStatus == PathStatus.IDLE || parsedStatus == PathStatus.ARRIVED || parsedStatus == PathStatus.FAILED -> {
        if (cachedPathNodes.isNotEmpty()) cachedPathNodes = emptyList()
    }
}
lastTickStatus = parsedStatus
```

- [ ] **Step 4: Build mod JAR**

```bash
./gradlew build
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add src/main/kotlin/org/cobalt/api/pathfinder/jni/NativePathfinder.kt
git commit -m "feat: cache path nodes in NativePathfinder on EXECUTING transition (Task 3)"
```

---

## Task 4: PathSplineRenderer — Catmull-Rom + AOTV Detection

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/pathfinding/PathSplineRenderer.kt`

This object takes a list of `Vec3` path nodes and produces:
1. A `List<Vec3>` of smooth interpolated spline points (Catmull-Rom, 10 steps per segment)
2. A `BooleanArray` of the same length indicating which points are on AOTV segments (XZ distance between source nodes > 2.0 blocks)

AOTV detection: for each pair of consecutive source nodes, if their XZ distance > 2.0 and they share the same Y, the segment is AOTV. This matches the C++ `addAOTV` range (3–11 blocks).

Catmull-Rom phantom endpoints: duplicate first node as P[-1] and last node as P[N].

- [ ] **Step 1: Create `PathSplineRenderer.kt`**

```kotlin
package org.cobalt.internal.pathfinding

import net.minecraft.world.phys.Vec3

object PathSplineRenderer {

    private const val STEPS = 10
    private const val AOTV_XZ_THRESHOLD = 2.0

    data class SplineResult(
        val points: List<Vec3>,
        val isAotv: BooleanArray  // parallel to points; true = this point is on an AOTV segment
    )

    fun buildSpline(nodes: List<Vec3>): SplineResult {
        if (nodes.size < 2) return SplineResult(nodes.toList(), BooleanArray(nodes.size))

        val points = ArrayList<Vec3>((nodes.size - 1) * STEPS + 1)
        val aotv   = ArrayList<Boolean>((nodes.size - 1) * STEPS + 1)

        for (i in 0 until nodes.size - 1) {
            val p0 = nodes[(i - 1).coerceAtLeast(0)]
            val p1 = nodes[i]
            val p2 = nodes[i + 1]
            val p3 = nodes[(i + 2).coerceAtMost(nodes.size - 1)]

            val segIsAotv = xzDist(p1, p2) > AOTV_XZ_THRESHOLD && p1.y == p2.y

            for (step in 0 until STEPS) {
                val t = step.toDouble() / STEPS
                points.add(catmullRom(p0, p1, p2, p3, t))
                aotv.add(segIsAotv)
            }
        }
        // Add final point
        points.add(nodes.last())
        aotv.add(false)

        return SplineResult(points, aotv.toBooleanArray())
    }

    private fun catmullRom(p0: Vec3, p1: Vec3, p2: Vec3, p3: Vec3, t: Double): Vec3 {
        val t2 = t * t
        val t3 = t2 * t
        return Vec3(
            0.5 * (2*p1.x + (-p0.x+p2.x)*t + (2*p0.x-5*p1.x+4*p2.x-p3.x)*t2 + (-p0.x+3*p1.x-3*p2.x+p3.x)*t3),
            0.5 * (2*p1.y + (-p0.y+p2.y)*t + (2*p0.y-5*p1.y+4*p2.y-p3.y)*t2 + (-p0.y+3*p1.y-3*p2.y+p3.y)*t3),
            0.5 * (2*p1.z + (-p0.z+p2.z)*t + (2*p0.z-5*p1.z+4*p2.z-p3.z)*t2 + (-p0.z+3*p1.z-3*p2.z+p3.z)*t3)
        )
    }

    private fun xzDist(a: Vec3, b: Vec3): Double {
        val dx = a.x - b.x; val dz = a.z - b.z
        return kotlin.math.sqrt(dx*dx + dz*dz)
    }
}
```

- [ ] **Step 2: Build mod JAR**

```bash
./gradlew build
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/pathfinding/PathSplineRenderer.kt
git commit -m "feat: add PathSplineRenderer with Catmull-Rom spline and AOTV detection (Task 4)"
```

---

## Task 5: AOTV Auto-Fire in PathfindingModule

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/pathfinding/PathfindingModule.kt`

When `cmd.activeAction == ActionType.AOTV`:
1. Save current held slot
2. Swap to configured AOTV hotbar slot
3. Call `mc.gameMode?.useItem(player, InteractionHand.MAIN_HAND)` to right-click
4. Swap back to previous slot immediately (AOTV teleports instantly)

Add a `ModeSetting` for the AOTV slot (options "1"–"9", value is 0–8 matching `player.inventory.selected`).

- [ ] **Step 1: Add imports to PathfindingModule.kt**

Add these imports at the top with the existing imports:
```kotlin
import net.minecraft.world.InteractionHand
import org.cobalt.api.module.setting.impl.ModeSetting
import org.cobalt.api.pathfinder.jni.ActionType
import org.cobalt.api.pathfinder.jni.NativePathfinder
```

- [ ] **Step 2: Add `aotvSlot` setting after `debugFileLogging`**

```kotlin
private val aotvSlot = ModeSetting(
    "AOTV Slot",
    "Hotbar slot (1-9) holding your AOTV item.",
    0,
    arrayOf("1","2","3","4","5","6","7","8","9")
)
```

Register it in `addSetting(...)` after `debugFileLogging`.

- [ ] **Step 3: Add AOTV fire logic to `onTick()`**

In `onTick()`, replace:
```kotlin
if (cmd != null) {
    cmd.applyToPlayer()
}
```
With:
```kotlin
if (cmd != null) {
    cmd.applyToPlayer()
    if (cmd.activeAction == ActionType.AOTV) {
        val player = mc.player
        if (player != null) {
            val prevSlot = player.inventory.selected
            player.inventory.selected = aotvSlot.value
            mc.gameMode?.useItem(player, InteractionHand.MAIN_HAND)
            player.inventory.selected = prevSlot
        }
    }
}
```

- [ ] **Step 4: Build mod JAR**

```bash
./gradlew build
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/pathfinding/PathfindingModule.kt
git commit -m "feat: AOTV auto-fire with hotbar swap in PathfindingModule (Task 5)"
```

---

## Task 6: Spline + Head-Ray Rendering in PathfindingModule

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/pathfinding/PathfindingModule.kt`

Add two render features to `onRender(WorldRenderEvent.Last)`:

**A. Spline rendering** — on each render frame, rebuild the spline from cached nodes if they changed (track previous list identity). Clear tag `"path-spline"` then add one `addLine()` call per consecutive spline point pair. Normal segments cyan→blue; AOTV segments orange.

**B. Head-direction ray** — each frame, get `player.getLookAngle()` (unit Vec3 in look direction), get `player.getEyePosition(event.partialTick)`, draw a 5-block line from eye in look direction using tag `"head-ray"` with durationTicks=1.

The spline is pre-computed from `NativePathfinder.cachedPathNodes` only when the reference changes — not every frame.

- [ ] **Step 1: Add import for PathSplineRenderer and Vec3**

Add to imports:
```kotlin
import net.minecraft.world.phys.Vec3
import org.cobalt.internal.pathfinding.PathSplineRenderer
```

- [ ] **Step 2: Add spline cache fields to PathfindingModule**

Add after `private var moduleOwnsPath`:
```kotlin
private var cachedSpline: PathSplineRenderer.SplineResult? = null
private var lastNodesRef: List<Vec3>? = null
```

- [ ] **Step 3: Add spline color constants**

Add as private companion constants (or top-level private vals in the object):
```kotlin
private val COLOR_PATH_NORMAL = OverlayRenderEngine.Color(0, 200, 255, 220)   // cyan
private val COLOR_PATH_AOTV   = OverlayRenderEngine.Color(255, 160, 0, 220)   // orange
private val COLOR_HEAD_RAY    = OverlayRenderEngine.Color(255, 255, 255, 200)  // white
```

- [ ] **Step 4: Implement `onRender` handler**

Replace the existing empty `onRender` body with:
```kotlin
@SubscribeEvent
fun onRender(event: WorldRenderEvent.Last) {
    if (!enabled.value) return
    val player = mc.player ?: return
    val level  = mc.level  ?: return

    // --- Head-direction ray ---
    OverlayRenderEngine.clearTag("head-ray")
    val eye  = player.getEyePosition()
    val look = player.getLookAngle()
    OverlayRenderEngine.addLine(
        level,
        eye.x, eye.y, eye.z,
        eye.x + look.x * 5.0, eye.y + look.y * 5.0, eye.z + look.z * 5.0,
        COLOR_HEAD_RAY, 1.5f, durationTicks = 1, tag = "head-ray"
    )

    // --- Spline path rendering ---
    val nodes = NativePathfinder.cachedPathNodes
    if (nodes !== lastNodesRef) {
        lastNodesRef = nodes
        cachedSpline = if (nodes.size >= 2) PathSplineRenderer.buildSpline(nodes) else null
        OverlayRenderEngine.clearTag("path-spline")
    }

    val spline = cachedSpline ?: return
    val pts  = spline.points
    val isAv = spline.isAotv
    for (i in 0 until pts.size - 1) {
        val a = pts[i]; val b = pts[i + 1]
        val color = if (isAv[i]) COLOR_PATH_AOTV else COLOR_PATH_NORMAL
        OverlayRenderEngine.addLine(
            level,
            a.x, a.y + 0.05, a.z,
            b.x, b.y + 0.05, b.z,
            color, 2.0f, durationTicks = 3, tag = "path-spline"
        )
    }
}
```

**Note:** `WorldRenderEvent.Last` only carries a `WorldRenderContext` — no `partialTick` field. Use `player.getEyePosition()` (no partial tick) for the eye origin, which is accurate enough for a 1-tick-expiry line.

- [ ] **Step 5: Clear spline cache when path stops**

In `stopPath()`, after `MovementManager.setMovementLock(false)`:
```kotlin
cachedSpline = null
lastNodesRef = null
OverlayRenderEngine.clearTag("path-spline")
```

- [ ] **Step 6: Build mod JAR**

```bash
./gradlew build
```
Expected: BUILD SUCCESSFUL

- [ ] **Step 7: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/pathfinding/PathfindingModule.kt
git commit -m "feat: spline path rendering and head-direction ray in PathfindingModule (Task 6)"
```

---

## Task 7: Final Build + Smoke Test

- [ ] **Step 1: Full clean build**

```bash
./gradlew clean build
```
Expected: BUILD SUCCESSFUL, no errors

- [ ] **Step 2: In-game verification checklist**

Launch with `./gradlew runClient` and verify:

1. Set a target 30+ blocks away in a flat area — path line renders as smooth cyan spline at feet height
2. Head-direction white ray appears from eye position along crosshair at all times
3. Walk through a 1-block-wide tunnel — path threads through center, not hugging the wall
4. Set a target with a long straight section — AOTV segments render orange; AOTV item fires (hotbar swaps to slot, right-click, swaps back)
5. Stop the path (`Stop` button) — spline clears immediately

- [ ] **Step 3: Final commit if any fixups were needed**

```bash
git add -p
git commit -m "fix: post-smoke-test adjustments to pathfinder improvements"
```
