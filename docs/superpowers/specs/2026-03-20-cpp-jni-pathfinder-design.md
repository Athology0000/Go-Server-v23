# C++ JNI Pathfinder — Design Spec

**Date:** 2026-03-20
**Scope:** Full rewrite of all pathfinding in Cobalt using a shared C++ native library exposed via JNI
**Platform:** Windows only (single `.dll`, embedded in JAR)

---

## 1. Goal

Replace all per-macro pathfinding logic with a single shared C++ pathfinding engine loaded via JNI. Every macro (pig, garden, mining, combat, slayer, routes) calls the same Kotlin singleton. The engine must be: fast for long-distance paths, responsive (async replanning, never blocks the tick), reliable (stuck detection + recovery), human-like in rotation, and aware of all Skyblock terrain and movement types.

---

## 2. Architecture

```
┌─────────────────────────────────────────────────────┐
│                   Kotlin / JVM side                  │
│                                                      │
│  Macro (any)                                         │
│    │  setRoute(waypoints, loop, profile)             │
│    │  tick() → PathCommand?                          │
│    ▼                                                 │
│  NativePathfinder.kt  ◄──── all macros call this    │
│    │                                                 │
│  NativePathfinderBridge.java  (native declarations)  │
└─────────────────┬───────────────────────────────────┘
                  │  JNI
┌─────────────────▼───────────────────────────────────┐
│             cobalt_pathfinder.dll (C++)              │
│                                                      │
│  PathfinderEngine                                    │
│    ├─ AStarPlanner       (graph search, async thread)│
│    ├─ PathExecutor        (state machine, per-tick)  │
│    ├─ MovementExpander    (all node/edge types)      │
│    ├─ WorldAccessor       (buffer + callback hybrid) │
│    ├─ RotationController  (smooth human-like yaw/pitch│
│    └─ StuckDetector       (position history, recovery│
└─────────────────────────────────────────────────────┘
```

**Per-tick data flow:**
1. Kotlin serializes nearby blocks into a `ByteBuffer` and packages player state (`pos, yaw, pitch, onGround, vel`)
2. Calls `NativePathfinder.tick()` via JNI
3. C++ `PathExecutor` advances the state machine, feeds new data to `WorldAccessor`
4. `AStarPlanner` runs on its own thread — never blocks the tick
5. Returns `PathCommand(forwardKey, jumpKey, sneakKey, sprint, targetYaw, targetPitch, status, activeAction)`
6. Kotlin applies keys and feeds `targetYaw/pitch` to the existing `RotationExecutor`

---

## 3. World Access — Hybrid Buffer + Callback

C++ accesses Minecraft block data via two mechanisms:

- **Buffer (fast path):** Java serializes a region of blocks (e.g. 64×32×64) centred on the player into a `byte[]` before calling `update()`. C++ reads from this buffer for the majority of queries (~95% hit rate).
- **JNI callback (fallback):** For blocks outside the buffer (unusual terrain, long paths), C++ calls back into the JVM via a registered callback. Slower but guarantees correctness on all terrain shapes.

This hybrid ensures the pathfinder never goes blind on weird terrain (ravines, multi-floor structures, irregular tunnels) while keeping per-tick overhead low.

---

## 4. Movement Graph

The A* graph treats each block position as a node. `MovementExpander` generates edges:

| Action | Description | Base Cost |
|---|---|---|
| WALK | Adjacent block, same Y | 1.0 |
| SPRINT | Adjacent block, same Y, open | 0.8 |
| JUMP | Step up 1 block | 1.2 |
| SPRINT_JUMP | Cross 1–4 block gap | distance + 0.5 |
| FALL | Drop N blocks (safe height) | N × 0.9 |
| LADDER | Ascend/descend ladder column | 1.1/block |
| WATER_SWIM | Move through water | 2.0 |
| AOTV | Teleport up to **11 blocks** toward open air | 5.0 + dist×0.05 |
| ETHERWARP | Teleport to looked-at block within **51 blocks** | 4.0 |

**Environment penalties:**
- Lava adjacent: +20 cost
- Water source: +5 cost
- Low ceiling (< 2 blocks): disables SPRINT
- One-wide gap with solid both sides: enables SPRINT_JUMP edge

**Movement profiles** (passed per `setRoute()` call):

| Profile | Behaviour |
|---|---|
| `DEFAULT` | Balanced cost weights |
| `MINING` | Penalise AOTV waste, prefer tunnels |
| `COMBAT` | Low cost for AOTV/Etherwarp, speed-biased |
| `GROUND_ONLY` | Walk/sprint only, no jumps or teleport |

---

## 5. Path Execution State Machine

```
IDLE
  │  setRoute() called
  ▼
PLANNING ── AStarPlanner working async ──► PLANNING_FAILED → IDLE
  │  path ready
  ▼
EXECUTING
  │  ├─ advance along path nodes, issue command each tick
  │  ├─ StuckDetector watches position history (2s window)
  │  │      stuck? → RECOVERING
  │  ├─ block change in WorldAccessor? → REPLANNING
  │  └─ final waypoint reached?
  │        loop=true  → waypoint[0] → PLANNING
  │        loop=false → ARRIVED → IDLE
  ▼
RECOVERING
  │  back up 1–2 blocks, try alternate edge, timeout=3s
  │  resolved → EXECUTING  │  timeout → REPLANNING
  ▼
REPLANNING ── new async plan, keep moving on old path ──► EXECUTING
```

`PathStatus` enum exposed to Kotlin: `IDLE, PLANNING, EXECUTING, RECOVERING, ARRIVED, FAILED`

Macros react to `ARRIVED` (trigger interaction logic) and `FAILED` (retry or notify user).

---

## 6. Human-Like Rotation System

Three layers applied every tick by `RotationController`:

**Layer 1 — Path-aware look-ahead**
Controller targets node+2 or node+3 rather than the immediate next node. Head begins turning before the body arrives at a corner.

**Layer 2 — Bezier curve + GCD correction**
Rotation moves along a bezier curve (fast start, eased finish). GCD correction applied so fractional angles don't form detectable patterns.

**Layer 3 — Micro-variation**
Every 8–15 ticks (randomised), ±0.08°–0.25° noise added to yaw and pitch. During `RECOVERING`, a short head-scan is performed (natural "looking around" behaviour).

**COMBAT profile override:** Faster tracking curve that leads mob movement direction rather than following it.

Output per tick: `targetYaw: Float, targetPitch: Float` — fed into the existing `RotationExecutor`.

---

## 7. Kotlin API

```kotlin
// NativePathfinder.kt — singleton, all macros call this
object NativePathfinder {
    fun setRoute(waypoints: List<Vec3>, loop: Boolean = false,
                 profile: MovementProfile = MovementProfile.DEFAULT)
    fun setTarget(pos: Vec3)   // single-destination shortcut
    fun stop()
    val status: PathStatus
    fun tick(): PathCommand?   // call once per TickEvent.Start
}

data class PathCommand(
    val forward: Boolean,
    val back: Boolean,
    val jump: Boolean,
    val sneak: Boolean,
    val sprint: Boolean,
    val targetYaw: Float,
    val targetPitch: Float,
    val status: PathStatus,
    val activeAction: ActionType
)
```

Macro integration:
```kotlin
// Every macro, same 2 lines replacing all prior pathfinding
NativePathfinder.setRoute(listOf(target), profile = MovementProfile.MINING)

// In TickEvent.Start handler:
NativePathfinder.tick()?.applyToPlayer()
```

`applyToPlayer()` is a Kotlin extension that sends key presses and feeds yaw/pitch to `RotationExecutor`.

---

## 8. JNI Bridge

```java
public class NativePathfinderBridge {
    static { System.load(NativeLoader.extract("natives/windows/cobalt_pathfinder.dll")); }

    public static native long  createEngine();
    public static native void  destroyEngine(long handle);
    public static native void  setRoute(long handle, double[] waypoints,
                                         boolean loop, int profile);
    public static native void  setTarget(long handle, double x, double y, double z);
    public static native int[] update(long handle, byte[] worldBuffer,
                                       int bx, int by, int bz,
                                       double px, double py, double pz,
                                       float yaw, float pitch, boolean onGround);
    public static native void  stop(long handle);
    public static native int   getStatus(long handle);
}
```

---

## 9. Build Setup

**Toolchain:** MSVC (Visual Studio 2022) via CMake, C++17, no external dependencies.

**C++ source layout:**
```
natives/
  CMakeLists.txt
  src/
    pathfinder_jni.cpp
    engine/
      PathfinderEngine.cpp
      AStarPlanner.cpp
      PathExecutor.cpp
      MovementExpander.cpp
      WorldAccessor.cpp
      RotationController.cpp
      StuckDetector.cpp
    include/
      PathfinderEngine.h
      Types.h
      MovementProfile.h
```

**Gradle task** builds the DLL and copies it into `src/main/resources/natives/windows/`. `processResources` depends on `buildNative`. The DLL ships embedded in the JAR — single file, no separate install.

**DLL extraction at runtime:** `NativeLoader.java` extracts `cobalt_pathfinder.dll` from the JAR to `%TEMP%\cobalt\` on first run, skipped on subsequent runs if checksum matches.

---

## 10. Macro Migration Order

| Phase | Files | Validate |
|---|---|---|
| 1 | Infrastructure: `natives/`, `NativePathfinderBridge.java`, `NativeLoader.java`, `NativePathfinder.kt`, `PathCommand.kt`, `MovementProfile.kt`, `PathStatus.kt`, `build.gradle.kts` | DLL loads, engine creates/destroys cleanly |
| 2 | `RoutesModule.kt` | Waypoint routes in tunnels, stuck recovery |
| 3 | `MiningMacroModule.kt`, `CommissionMacroModule.kt` | MINING profile, tunnel nav |
| 4 | `GardenMacroModule.kt`, `FarmingMacroModule.kt` | GROUND_ONLY profile, loop patrol |
| 5 | `PigMacroModule.kt` | Dynamic target, orb approach |
| 6 | `CombatMacroModule.kt` | COMBAT profile, AOTV/Etherwarp edges |
| 7 | Delete `DuskPathfinder.kt`, `AStarPathfinder.kt`, `NodeProcessor.kt`, `PathPlanProfiles.kt` | All macros validated |
