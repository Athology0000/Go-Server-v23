# Design: Port v5 Pathfinder to Cobalt

**Date:** 2026-04-23

## Overview

Replace the current tick-based, byte-buffer pathfinder (`src/engine/`) with the v5 open-source pathfinder (`natives/v5/`). The v5 engine provides: physics-accurate A* with walk and fly runtimes, path simplification, avoid zones, multi-start/goal, path annotations/signatures, and an RRT*-style etherwarp angle search. The port is a rename-only operation on the C++ side; all algorithm logic is copied verbatim.

---

## C++ / DLL Layer

### What is deleted
- `natives/src/engine/` — all files (AStarPlanner, MovementExpander, WorldAccessor, PathExecutor, PathfinderEngine, RotationController, StuckDetector)
- `natives/src/pathfinder_jni.cpp`

### What replaces it
- All 22 files from `natives/v5/` are copied into `natives/src/`
- In `jni_bridge.cpp`, every JNI function symbol is renamed:
  - `Java_com_v5_swift_nativepath_NativePathfinderJNI_X` → `Java_org_cobalt_pathfinder_NativePathfinderJNI_X`
- `natives/CMakeLists.txt` updated to compile the new source set

### What is not changed
All v5 algorithm files, data structures, and logic are copied without modification.

---

## Kotlin / JNI Bridge Layer

### New files

**`src/main/kotlin/org/cobalt/api/pathfinder/jni/NativePathfinderJNI.kt`**
Singleton `object` with `external` methods matching v5's JNI API exactly:
- `initNative(): Boolean`
- `setWorld(worldKey: String, minY: Int, maxY: Int)`
- `clearWorld()`
- `upsertChunk(chunkX: Int, chunkZ: Int, minY: Int, maxY: Int, sectionMask: Long, sectionFlags: ShortArray)`
- `applyBlockUpdates(updates: IntArray)` — flat [x, y, z, flags] quads
- `cancelSearch()`
- `findPath(startPoints: IntArray, endPoints: IntArray, isFly: Boolean, maxIterations: Int, heuristicWeight: Double, nonPrimaryStartPenalty: Double, moveOrderOffset: Int, avoidMeta: IntArray, avoidPenalty: DoubleArray): NativePathResult?`
- `findEtherwarpPath(goalX: Int, goalY: Int, goalZ: Int, startEyeX: Double, startEyeY: Double, startEyeZ: Double, maxIterations: Int, threadCount: Int, yawStep: Double, pitchStep: Double, newNodeCost: Double, heuristicWeight: Double, rayLength: Double, rewireEpsilon: Double, eyeHeight: Double): NativeEtherwarpResult?`

**`src/main/kotlin/org/cobalt/api/pathfinder/jni/NativePathResult.kt`**
```kotlin
data class NativePathResult(
    val path: IntArray,           // flat x,y,z triples — all nodes
    val keyPath: IntArray,        // flat x,y,z triples — simplified key nodes
    val timeMs: Long,
    val nodesExplored: Int,
    val nanosecondsPerNode: Double,
    val selectedStartIndex: Int,
    val pathFlags: IntArray,
    val keyNodeFlags: IntArray,
    val keyNodeMetrics: IntArray,
    val signature: String
)
```

**`src/main/kotlin/org/cobalt/api/pathfinder/jni/NativeEtherwarpResult.kt`**
```kotlin
data class NativeEtherwarpResult(
    val path: IntArray,      // flat x,y,z triples
    val angles: FloatArray,  // yaw/pitch pairs per node
    val timeMs: Long,
    val nodesExplored: Int,
    val nanosecondsPerNode: Double
)
```

### Files rewritten

**`WorldBufferSerializer.kt` → `ChunkSerializer.kt`**
- Subscribes to chunk load/unload and `BlockChangeEvent`
- On chunk load: computes `uint16_t` voxel flags per block using v5's `VoxelFlags` bitmask, calls `NativePathfinderJNI.upsertChunk()`
- On `BlockChangeEvent`: batches updates as flat int quads (x, y, z, flags), calls `NativePathfinderJNI.applyBlockUpdates()`
- On world join: calls `NativePathfinderJNI.setWorld(worldKey, minY, maxY)`
- On world leave / dimension change: calls `NativePathfinderJNI.clearWorld()`

**Voxel flag mapping (block state → `uint16_t`):**
| Block type | Flags set |
|---|---|
| Air / fully passable | `VF_PASSABLE \| VF_PASSABLE_FLY \| VF_ETHER_PASSABLE \| VF_ETHER_TELEPORT_CLEAR` |
| Solid full cube | `VF_SOLID \| VF_BLOCKING_WALL \| VF_ETHER_FEET_BLOCKER` |
| Water / lava | `VF_FLUID \| VF_PASSABLE_FLY` |
| Ladder | `VF_PASSABLE \| VF_PASSABLE_FLY` (special traversal handled by runtime) |
| Bottom slab | `VF_SLAB_BOTTOM \| VF_SOLID` |
| Top slab | `VF_SLAB_TOP \| VF_SOLID \| VF_BLOCKING_WALL` |
| Fence / wall | `VF_FENCE_LIKE \| VF_SOLID \| VF_BLOCKING_WALL` |
| Carpet / thin | `VF_CARPET_LIKE \| VF_PASSABLE \| VF_PASSABLE_FLY` |

**`NativePathfinder.kt`**
- Rewritten to call `NativePathfinderJNI.findPath()` on a background thread
- Drives executor from `keyPoints` (flat int array → Vec3 list)
- `setTarget`, `setTargetWithRadius`, `setRoute`, `stop`, `onLevelChange` signatures preserved for callers

### Files deleted
- `NativePathfinderBridge.java` — replaced by `NativePathfinderJNI.kt`

### Files updated (callers)
- `PathfindingModule.kt` — remove buffer serialization, wire to rewritten `NativePathfinder`
- `EtherwarpLogic.kt` — call `NativePathfinderJNI.findEtherwarpPath()` for angle search; apply returned `angles` FloatArray via `RotationExecutor`
- `RoutesModule.kt` — update any direct references to old bridge/serializer
- `Cobalt.kt` — register `ChunkSerializer` instead of `WorldBufferSerializer`

---

## Data Flow

### World state
```
World join       → setWorld(worldKey, minY, maxY)
Chunk load       → upsertChunk(chunkX, chunkZ, minY, maxY, sectionMask, sectionFlags)
BlockChangeEvent → applyBlockUpdates([x,y,z,flags, ...])
World leave      → clearWorld()
```

### Pathfinding
```
Caller sets start + goal(s) as flat IntArray (x,y,z triples)
NativePathfinderJNI.findPath() → runs on background thread
  └── v5 takes internal WorldSnapshot (atomic, no lock during search)
Result keyPoints → PathfindingModule drives movement
```

### Etherwarp
```
EtherwarpLogic calls findEtherwarpPath(goal, eyePos, searchParams)
Result angles: FloatArray (yaw/pitch pairs) → RotationExecutor
```

---

## What is not changing
- `RotationExecutor` and rotation strategies
- `MovementManager` and mixin input interception
- `PathfindingModule` UI/settings (module name, settings, HUD)
- `EtherwarpHelperModule` settings and trigger logic
- `RoutesModule` route definitions
- The `PathCommand` concept (movement commands still applied per tick from executor)
