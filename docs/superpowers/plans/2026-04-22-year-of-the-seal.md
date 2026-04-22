# Year of the Seal Macro Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build `YearOfTheSealModule` — a standalone Cobalt module that detects the Year of the Seal beach ball, predicts its landing position using 3-model polynomial regression (ported from SkyHanni), and pathfinds to the landing spot automatically.

**Architecture:** One file (`YearOfTheSealModule.kt`) containing the module object and an inner `BallPredictor` class that owns all tracking/prediction state per ball entity. Three polynomial model objects (SmallPoly, AveragePoly, SpreadPoly) live as sealed subclasses inside `BallPredictor`. The module scans for invisible 1024-HP slime entities each tick, delegates to predictors, and feeds the stable landing position to `NativePathfinder`.

**Tech Stack:** Kotlin, Fabric 1.21.1, Mojang mappings, Cobalt module/event system, `NativePathfinder` JNI bridge, `net.minecraft.world.entity.monster.Slime`, `net.minecraft.world.phys.Vec3`

---

## File Map

| Action | Path |
|--------|------|
| Create | `src/main/kotlin/org/cobalt/internal/seal/YearOfTheSealModule.kt` |
| Modify | `src/main/kotlin/org/cobalt/Cobalt.kt` |

---

### Task 1: Module skeleton, settings, and registration

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/seal/YearOfTheSealModule.kt`
- Modify: `src/main/kotlin/org/cobalt/Cobalt.kt`

- [ ] **Step 1: Create the module file**

Create `src/main/kotlin/org/cobalt/internal/seal/YearOfTheSealModule.kt` with this content:

```kotlin
package org.cobalt.internal.seal

import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.KeyBindSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.pathfinder.jni.NativePathfinder
import org.cobalt.api.pathfinder.jni.PathStatus
import org.cobalt.api.util.player.MovementManager
import net.minecraft.world.entity.monster.Slime

object YearOfTheSealModule : Module("Year of the Seal") {

    private val mc = Minecraft.getInstance()

    private val enabledSetting = CheckboxSetting(
        "Enabled",
        "Automatically pathfinds to the predicted beach ball landing position.",
        false
    )

    private val toggleKeybind = KeyBindSetting(
        "Toggle Key",
        "Keybind to enable/disable the Year of the Seal macro.",
        -1
    )

    private val minBounces = SliderSetting(
        "Min Bounce Quality",
        "Minimum observed bounces before pathfinding starts. Higher = more data, more accurate prediction.",
        2.0, 1.0, 10.0, 1.0
    )

    // Ball trackers keyed by entity ID
    private val predictors = mutableMapOf<Int, BallPredictor>()
    // Last target issued to NativePathfinder (to avoid replanning on every tick)
    private var lastIssuedTarget: Vec3? = null

    init {
        addSetting(enabledSetting, toggleKeybind, minBounces)
        EventBus.register(this)
    }

    @SubscribeEvent
    fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
        if (toggleKeybind.value.isPressed()) {
            enabledSetting.value = !enabledSetting.value
        }
        // stub — detection and pathfinding added in later tasks
    }
}
```

- [ ] **Step 2: Register the module in `Cobalt.kt`**

Open `src/main/kotlin/org/cobalt/Cobalt.kt`. Add the import:
```kotlin
import org.cobalt.internal.seal.YearOfTheSealModule
```

Inside `ModuleManager.addModules(listOf(...))`, add `YearOfTheSealModule` after `FishingMacroModule`:
```kotlin
FishingMacroModule,
YearOfTheSealModule,
```

- [ ] **Step 3: Verify it compiles**

Run:
```
./gradlew build
```
Expected: `BUILD SUCCESSFUL`. Fix any compile errors before continuing.

- [ ] **Step 4: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/seal/YearOfTheSealModule.kt \
        src/main/kotlin/org/cobalt/Cobalt.kt
git commit -m "feat(seal): module skeleton with settings and registration"
```

---

### Task 2: Ball entity detection

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/seal/YearOfTheSealModule.kt`

- [ ] **Step 1: Add the `BallPredictor` stub class**

Inside `YearOfTheSealModule.kt`, before the `object` declaration, add:

```kotlin
/** Tracks position history and predicts the landing position for one beach ball entity. */
class BallPredictor {
    val data = mutableListOf<Vec3>()    // all positions recorded since spawn
    var startIndex = 0                  // slice start within data (reset each bounce)
    var minY = Double.MAX_VALUE         // lowest Y seen (ground level)
    var bounceCounter = 0
    var lastBounceMs = 0L
    var positiveYDelta = false          // whether last Y delta was upward
    var lastPos = Vec3(0.0, 0.0, 0.0)
    var predTick = 0                    // counter mod 3 for throttled prediction updates
    var landingPos: Vec3? = null        // current best landing prediction

    fun update(pos: Vec3) {
        // implemented in Task 3
        data.add(pos)
    }
}
```

- [ ] **Step 2: Add entity scanning in `onTick`**

Replace the `onTick` body with:

```kotlin
@SubscribeEvent
fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (toggleKeybind.value.isPressed()) {
        enabledSetting.value = !enabledSetting.value
    }
    if (!enabledSetting.value) {
        if (predictors.isNotEmpty()) stopAll()
        return
    }
    val level = mc.level ?: return

    // Discover new beach ball slimes (invisible, 1024 max HP)
    level.entitiesForRendering().forEach { entity ->
        if (entity is Slime && entity.isInvisible && entity.maxHealth == 1024f) {
            predictors.putIfAbsent(entity.id, BallPredictor())
        }
    }

    // Update existing predictors, prune removed entities
    val iter = predictors.iterator()
    while (iter.hasNext()) {
        val (id, predictor) = iter.next()
        val entity = level.getEntity(id)
        if (entity == null || entity.isRemoved) {
            iter.remove()
            continue
        }
        predictor.update(Vec3(entity.x, entity.y, entity.z))
    }

    // Pathfinding — implemented in Task 5
}

private fun stopAll() {
    NativePathfinder.stop()
    MovementManager.clearForcedMovement()
    predictors.clear()
    lastIssuedTarget = null
}
```

- [ ] **Step 3: Verify it compiles**

```
./gradlew build
```
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/seal/YearOfTheSealModule.kt
git commit -m "feat(seal): ball entity detection and predictor lifecycle"
```

---

### Task 3: Position tracking and bounce detection

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/seal/YearOfTheSealModule.kt`

- [ ] **Step 1: Replace the `BallPredictor.update` stub with full implementation**

Replace the `fun update(pos: Vec3)` body in `BallPredictor`:

```kotlin
fun update(pos: Vec3) {
    data.add(pos)
    if (pos.y < minY) minY = pos.y

    // Skip bounce logic on first tick — lastPos is uninitialized
    if (data.size == 1) { lastPos = pos; return }

    // Bounce detection — skip if movement too small or debounce active
    val dist = pos.distanceTo(lastPos)
    if (dist >= 0.3) {
        val dy = pos.y - lastPos.y
        val nowMs = System.currentTimeMillis()
        if (dy > 0.0 && !positiveYDelta && (nowMs - lastBounceMs) > 800L) {
            // Ball just started going up — it bounced
            bounceCounter++
            lastBounceMs = nowMs
            startIndex = data.lastIndex   // reset prediction slice to this bounce
        }
        positiveYDelta = dy > 0.0
        lastPos = pos
    }

    // Throttle prediction to every 3 ticks
    predTick++
    if (predTick >= 3) {
        predTick = 0
        landingPos = runPrediction()
    }
}

private fun runPrediction(): Vec3? {
    // implemented in Task 4
    return null
}
```

- [ ] **Step 2: Verify it compiles**

```
./gradlew build
```
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/seal/YearOfTheSealModule.kt
git commit -m "feat(seal): position tracking and bounce detection"
```

---

### Task 4: Polynomial regression models and landing prediction

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/seal/YearOfTheSealModule.kt`

- [ ] **Step 1: Add the shared quadratic solver and model base**

Inside `BallPredictor` (after the `update` / `runPrediction` functions), add:

```kotlin
// Fit y = a*t^2 + b*t + c through three (t, y) points.
// Returns Triple(a, b, c) or null if inputs are degenerate.
private fun fitQuadratic(
    t1: Int, y1: Double,
    t2: Int, y2: Double,
    t3: Int, y3: Double
): Triple<Double, Double, Double>? {
    val d1 = (t2 - t1).toDouble()
    val d2 = (t3 - t1).toDouble()
    if (d1 == 0.0 || d2 == 0.0 || d1 == d2) return null
    val sq1 = t1.toDouble() * t1
    val sq2 = t2.toDouble() * t2
    val sq3 = t3.toDouble() * t3
    val denom = (sq3 - sq1) * d1 + (sq2 - sq1) * (t1 - t3)
    if (denom == 0.0) return null
    val a = ((y3 - y1) * d1 + (y2 - y1) * (t1 - t3)) / denom
    val b = ((y2 - y1) - a * (sq2 - sq1)) / d1
    val c = y1 - b * t1 - a * sq1
    return Triple(a, b, c)
}

// Extrapolate from t=fromT forward until poly(t) <= targetY, up to maxSteps ticks.
// Returns the landing Vec3 (XZ from linear drift + Y at targetY), or null if never reached.
private fun extrapolate(
    fromT: Int,
    startXZ: Vec3,
    dx: Double, dz: Double,
    a: Double, b: Double, c: Double,
    targetY: Double,
    maxSteps: Int = 300
): Vec3? {
    var x = startXZ.x
    var z = startXZ.z
    for (t in (fromT + 1)..(fromT + maxSteps)) {
        x += dx
        z += dz
        val y = a * t * t + b * t + c
        if (y <= targetY) return Vec3(x, targetY, z)
    }
    return null
}
```

- [ ] **Step 2: Add the three model functions**

Still inside `BallPredictor`, add:

```kotlin
// Average XZ drift per tick across the current bounce segment.
private fun segmentDrift(slice: List<Vec3>): Pair<Double, Double> {
    if (slice.size < 2) return 0.0 to 0.0
    val n = (slice.size - 1).toDouble()
    return (slice.last().x - slice.first().x) / n to
           (slice.last().z - slice.first().z) / n
}

// SmallPoly: 3 most recent points. Min 3 points in slice.
private fun smallPoly(slice: List<Vec3>): Vec3? {
    if (slice.size < 3) return null
    val n = slice.size - 1  // last index in slice
    val (dx, dz) = segmentDrift(slice)
    val (a, b, c) = fitQuadratic(
        n,     slice[n].y,
        n - 1, slice[n - 1].y,
        n - 2, slice[n - 2].y
    ) ?: return null
    return extrapolate(n, slice[n], dx, dz, a, b, c, minY)
}

// AveragePoly: 2-point averaged windows at t-1, t-3, t-5. Min 7 points in slice.
private fun averagePoly(slice: List<Vec3>): Vec3? {
    if (slice.size < 7) return null
    val n = slice.size - 1
    val y1 = (slice[n - 1].y + slice[n - 2].y) / 2.0
    val y2 = (slice[n - 3].y + slice[n - 4].y) / 2.0
    val y3 = (slice[n - 5].y + slice[n - 6].y) / 2.0
    val (dx, dz) = segmentDrift(slice)
    val (a, b, c) = fitQuadratic(n - 1, y1, n - 3, y2, n - 5, y3) ?: return null
    return extrapolate(n - 1, slice[n - 1], dx, dz, a, b, c, minY)
}

// SpreadPoly: spread across full bounce segment. Min 5 points in slice.
private fun spreadPoly(slice: List<Vec3>): Vec3? {
    if (slice.size < 5) return null
    val n = slice.size - 1
    val mid = (n + 1) / 2
    val (dx, dz) = segmentDrift(slice)
    val (a, b, c) = fitQuadratic(
        n - 1,  slice[n - 1].y,
        mid,    slice[mid].y,
        1,      slice[1].y
    ) ?: return null
    return extrapolate(n - 1, slice[n - 1], dx, dz, a, b, c, minY)
}
```

- [ ] **Step 3: Implement `runPrediction`**

Replace the `runPrediction` stub:

```kotlin
private fun runPrediction(): Vec3? {
    val slice = data.subList(startIndex, data.size).toList()
    val candidates = listOfNotNull(
        smallPoly(slice),
        averagePoly(slice),
        spreadPoly(slice)
    ).filter { v ->
        // Accept only predictions landing within 1 block of observed ground
        kotlin.math.abs(v.y - minY) <= 1.0
    }
    if (candidates.isEmpty()) return null
    // Average X and Z of all valid model outputs
    val avgX = candidates.sumOf { it.x } / candidates.size
    val avgZ = candidates.sumOf { it.z } / candidates.size
    return Vec3(avgX, minY, avgZ)
}
```

- [ ] **Step 4: Verify it compiles**

```
./gradlew build
```
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/seal/YearOfTheSealModule.kt
git commit -m "feat(seal): 3-model polynomial regression landing prediction"
```

---

### Task 5: Pathfinding integration and cleanup

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/seal/YearOfTheSealModule.kt`

- [ ] **Step 1: Wire pathfinding into `onTick`**

At the end of the `onTick` body (after the predictor update loop), add:

```kotlin
// Find the first predictor with enough bounces and a valid prediction
val target = predictors.values
    .firstOrNull { it.bounceCounter >= minBounces.value.toInt() && it.landingPos != null }
    ?.landingPos

if (target == null) {
    // No valid prediction yet — release pathfinder if we held it
    if (lastIssuedTarget != null) {
        NativePathfinder.stop()
        MovementManager.clearForcedMovement()
        lastIssuedTarget = null
    }
    return
}

// Only replan if target shifted by more than 0.5 blocks
val prev = lastIssuedTarget
if (prev == null || target.distanceTo(prev) > 0.5) {
    NativePathfinder.setTarget(target.x, target.y, target.z)
    lastIssuedTarget = target
}

// Tick the pathfinder and apply movement
val cmd = NativePathfinder.tick()
if (cmd != null) {
    cmd.applyToPlayer()
} else {
    when (NativePathfinder.status) {
        PathStatus.IDLE, PathStatus.ARRIVED, PathStatus.FAILED ->
            MovementManager.clearForcedMovement()
        else -> Unit
    }
}
```

- [ ] **Step 2: Clear state when module is disabled**

The `stopAll()` function is already called when `!enabledSetting.value`. Verify the full `onTick` reads correctly and that `stopAll()` is called before the early return:

```kotlin
if (!enabledSetting.value) {
    if (predictors.isNotEmpty() || lastIssuedTarget != null) stopAll()
    return
}
```

- [ ] **Step 3: Stop pathfinder on level change**

Add this event handler inside `YearOfTheSealModule`:

```kotlin
@SubscribeEvent
fun onRespawn(event: org.cobalt.api.event.impl.client.PacketEvent.Incoming) {
    if (event.packet is net.minecraft.network.protocol.game.ClientboundRespawnPacket) {
        stopAll()
    }
}
```

- [ ] **Step 4: Verify it compiles**

```
./gradlew build
```
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Manual smoke test**

Run:
```
./gradlew runClient
```

With the module enabled, enter a SkyBlock hub during the Year of the Seal event. Verify:
- Module appears in the Cobalt module list under "Year of the Seal"
- Toggle keybind enables/disables without crash
- While a beach ball is rolling, check that after 2+ bounces the player starts walking toward the landing spot
- Player stops moving when the ball despawns or module is toggled off

- [ ] **Step 6: Final commit**

```bash
git add src/main/kotlin/org/cobalt/internal/seal/YearOfTheSealModule.kt
git commit -m "feat(seal): pathfinding integration and cleanup on disable/respawn"
```
