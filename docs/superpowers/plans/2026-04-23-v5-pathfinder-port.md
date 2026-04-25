# V5 Pathfinder Port Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the tick-buffer–based native pathfinder engine with the v5 open-source pathfinder, giving Cobalt physics-accurate A*, fly mode, avoid zones, multi-start/goal, path simplification, signatures, and a dedicated etherwarp RRT* search.

**Architecture:** C++ side is a rename-only copy of `natives/v5/` into `natives/src/` with JNI symbol prefix swapped. Kotlin side gets a new `NativePathfinderJNI` singleton, two result data classes, a chunk-push `ChunkSerializer` replacing `WorldBufferSerializer`, and a rewritten `NativePathfinder` that owns the async search + path execution state machine.

**Tech Stack:** C++20 / MSVC, JNI, Kotlin, Fabric MC 1.21.x, Fabric API (`ClientChunkEvents`, `ClientPlayConnectionEvents`)

---

## File Map

### Create (C++)
- `natives/src/jni_bridge.cpp` — v5's jni_bridge.cpp with renamed JNI symbols
- `natives/src/common.hpp` — v5 types, VoxelFlags, ActionCosts
- `natives/src/world_state.hpp` / `world_state.cpp` — chunk map, WorldSnapshot
- `natives/src/pathfinder.hpp` / `pathfinder.cpp` — findPath entry point
- `natives/src/pathfinder_heap.hpp` — min-heap
- `natives/src/pathfinder_runtime.hpp` / `_common.inl` / `_walk.inl` / `_fly.inl` — A* runtime
- `natives/src/path_annotations.hpp` / `.cpp` — node flag encoding
- `natives/src/path_directional_scan.hpp` / `.cpp`
- `natives/src/path_signature.hpp` / `.cpp`
- `natives/src/path_simplifier.hpp` / `.cpp`
- `natives/src/path_voxel_checks.hpp`
- `natives/src/world_voxel_cursor.hpp`
- `natives/src/etherwarp_raymarch.hpp` / `.cpp`
- `natives/src/etherwarp_search.hpp` / `.cpp`

### Modify (C++)
- `natives/CMakeLists.txt` — new SOURCES list

### Delete (C++)
- `natives/src/pathfinder_jni.cpp`
- `natives/src/engine/AStarPlanner.h` / `.cpp`
- `natives/src/engine/MovementExpander.h` / `.cpp`
- `natives/src/engine/WorldAccessor.h` / `.cpp`
- `natives/src/engine/PathExecutor.h` / `.cpp`
- `natives/src/engine/PathfinderEngine.h` / `.cpp`
- `natives/src/engine/RotationController.h` / `.cpp`
- `natives/src/engine/StuckDetector.h` / `.cpp`

### Create (Kotlin)
- `src/main/kotlin/org/cobalt/api/pathfinder/jni/NativePathResult.kt`
- `src/main/kotlin/org/cobalt/api/pathfinder/jni/NativeEtherwarpResult.kt`
- `src/main/kotlin/org/cobalt/api/pathfinder/jni/NativePathfinderJNI.kt`
- `src/main/kotlin/org/cobalt/api/pathfinder/jni/ChunkSerializer.kt`

### Rewrite (Kotlin)
- `src/main/kotlin/org/cobalt/api/pathfinder/jni/NativePathfinder.kt`

### Delete (Kotlin)
- `src/main/kotlin/org/cobalt/api/pathfinder/jni/WorldBufferSerializer.kt`
- `src/main/java/org/cobalt/pathfinder/NativePathfinderBridge.java`

### Update (Kotlin)
- `src/main/kotlin/org/cobalt/internal/etherwarp/EtherwarpLogic.kt`
- `src/main/kotlin/org/cobalt/internal/pathfinding/PathfindingModule.kt`
- `src/main/kotlin/org/cobalt/internal/mining/RoutesModule.kt`

---

## Task 1: Update CMakeLists.txt

**Files:**
- Modify: `natives/CMakeLists.txt`

- [ ] **Step 1: Replace SOURCES list**

Replace the entire `set(SOURCES ...)` block with:

```cmake
set(SOURCES
    src/jni_bridge.cpp
    src/world_state.cpp
    src/pathfinder.cpp
    src/path_annotations.cpp
    src/path_directional_scan.cpp
    src/path_signature.cpp
    src/path_simplifier.cpp
    src/etherwarp_raymarch.cpp
    src/etherwarp_search.cpp
)
```

Also remove `include` from `target_include_directories` (v5 uses no separate include dir; headers live alongside sources in `src/`). Final file:

```cmake
cmake_minimum_required(VERSION 3.20)
project(cobalt_pathfinder LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(JNI REQUIRED)

set(SOURCES
    src/jni_bridge.cpp
    src/world_state.cpp
    src/pathfinder.cpp
    src/path_annotations.cpp
    src/path_directional_scan.cpp
    src/path_signature.cpp
    src/path_simplifier.cpp
    src/etherwarp_raymarch.cpp
    src/etherwarp_search.cpp
)

add_library(cobalt_pathfinder SHARED ${SOURCES})

target_include_directories(cobalt_pathfinder PRIVATE
    src
    ${JNI_INCLUDE_DIRS}
)

target_link_libraries(cobalt_pathfinder PRIVATE ${JNI_LIBRARIES})

if(MSVC)
    target_compile_options(cobalt_pathfinder PRIVATE /O2 /W3)
    set_target_properties(cobalt_pathfinder PROPERTIES
        WINDOWS_EXPORT_ALL_SYMBOLS OFF)
endif()
```

- [ ] **Step 2: Commit**

```bash
git add natives/CMakeLists.txt
git commit -m "build(natives): replace engine source list with v5 files"
```

---

## Task 2: Copy v5 header and source files into natives/src/

**Files:**
- Create: all v5 files in `natives/src/`

- [ ] **Step 1: Copy all v5 files**

Run from repo root:

```bash
cp natives/v5/common.hpp              natives/src/common.hpp
cp natives/v5/world_state.hpp         natives/src/world_state.hpp
cp natives/v5/world_state.cpp         natives/src/world_state.cpp
cp natives/v5/pathfinder.hpp          natives/src/pathfinder.hpp
cp natives/v5/pathfinder.cpp          natives/src/pathfinder.cpp
cp natives/v5/pathfinder_heap.hpp     natives/src/pathfinder_heap.hpp
cp natives/v5/pathfinder_runtime.hpp  natives/src/pathfinder_runtime.hpp
cp natives/v5/pathfinder_runtime_common.inl  natives/src/pathfinder_runtime_common.inl
cp natives/v5/pathfinder_runtime_walk.inl    natives/src/pathfinder_runtime_walk.inl
cp natives/v5/pathfinder_runtime_fly.inl     natives/src/pathfinder_runtime_fly.inl
cp natives/v5/path_annotations.hpp    natives/src/path_annotations.hpp
cp natives/v5/path_annotations.cpp    natives/src/path_annotations.cpp
cp natives/v5/path_directional_scan.hpp  natives/src/path_directional_scan.hpp
cp natives/v5/path_directional_scan.cpp  natives/src/path_directional_scan.cpp
cp natives/v5/path_signature.hpp      natives/src/path_signature.hpp
cp natives/v5/path_signature.cpp      natives/src/path_signature.cpp
cp natives/v5/path_simplifier.hpp     natives/src/path_simplifier.hpp
cp natives/v5/path_simplifier.cpp     natives/src/path_simplifier.cpp
cp natives/v5/path_voxel_checks.hpp   natives/src/path_voxel_checks.hpp
cp natives/v5/world_voxel_cursor.hpp  natives/src/world_voxel_cursor.hpp
cp natives/v5/etherwarp_raymarch.hpp  natives/src/etherwarp_raymarch.hpp
cp natives/v5/etherwarp_raymarch.cpp  natives/src/etherwarp_raymarch.cpp
cp natives/v5/etherwarp_search.hpp    natives/src/etherwarp_search.hpp
cp natives/v5/etherwarp_search.cpp    natives/src/etherwarp_search.cpp
```

- [ ] **Step 2: Commit**

```bash
git add natives/src/
git commit -m "feat(natives): copy v5 pathfinder engine source files"
```

---

## Task 3: Create jni_bridge.cpp with renamed JNI symbols

**Files:**
- Create: `natives/src/jni_bridge.cpp`
- This is a copy of `natives/v5/jni_bridge.cpp` with the class name prefix changed.

- [ ] **Step 1: Copy jni_bridge.cpp**

```bash
cp natives/v5/jni_bridge.cpp natives/src/jni_bridge.cpp
```

- [ ] **Step 2: Rename all JNI symbol prefixes**

In `natives/src/jni_bridge.cpp`, replace every occurrence of:
```
Java_com_v5_swift_nativepath_NativePathfinderJNI_
```
with:
```
Java_org_cobalt_pathfinder_NativePathfinderJNI_
```

There are 8 functions to rename:
- `Java_..._initNative`
- `Java_..._setWorld`
- `Java_..._clearWorld`
- `Java_..._upsertChunk`
- `Java_..._applyBlockUpdates`
- `Java_..._findPath`
- `Java_..._findEtherwarpPath`
- `Java_..._cancelSearch`

Also update the two `env->FindClass(...)` calls to use the Cobalt result class paths:
- `"com/v5/swift/nativepath/NativePathResult"` → `"org/cobalt/pathfinder/jni/NativePathResult"`
- `"com/v5/swift/nativepath/NativeEtherwarpResult"` → `"org/cobalt/pathfinder/jni/NativeEtherwarpResult"`

- [ ] **Step 3: Commit**

```bash
git add natives/src/jni_bridge.cpp
git commit -m "feat(natives): add jni_bridge.cpp with Cobalt JNI symbol names"
```

---

## Task 4: Delete old engine files

**Files:**
- Delete: `natives/src/pathfinder_jni.cpp` and all `natives/src/engine/` files

- [ ] **Step 1: Delete old files**

```bash
rm natives/src/pathfinder_jni.cpp
rm -rf natives/src/engine/
```

- [ ] **Step 2: Commit**

```bash
git add -u natives/src/
git commit -m "chore(natives): remove old tick-buffer engine"
```

---

## Task 5: Build the DLL and verify

**Files:**
- Run: `./gradlew buildNative` (or `cmake --build natives/build --config Release`)

- [ ] **Step 1: Build**

```bash
./gradlew buildNative
```

Expected: build succeeds, no compile errors. If include paths fail, verify `target_include_directories` in CMakeLists.txt points to `src/` (where all headers now live).

- [ ] **Step 2: Copy DLL into resources**

```bash
./gradlew copyNativeDll
```

Expected: `src/main/resources/natives/windows/cobalt_pathfinder.dll` is updated.

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/natives/windows/cobalt_pathfinder.dll
git commit -m "feat(natives): rebuild DLL with v5 pathfinder engine"
```

---

## Task 6: Create NativePathResult.kt

**Files:**
- Create: `src/main/kotlin/org/cobalt/api/pathfinder/jni/NativePathResult.kt`

- [ ] **Step 1: Create the file**

```kotlin
package org.cobalt.api.pathfinder.jni

data class NativePathResult(
    val path: IntArray,
    val keyPath: IntArray,
    val timeMs: Long,
    val nodesExplored: Int,
    val nanosecondsPerNode: Double,
    val selectedStartIndex: Int,
    val pathFlags: IntArray,
    val keyNodeFlags: IntArray,
    val keyNodeMetrics: IntArray,
    val signature: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NativePathResult) return false
        return path.contentEquals(other.path) &&
            keyPath.contentEquals(other.keyPath) &&
            timeMs == other.timeMs &&
            nodesExplored == other.nodesExplored &&
            nanosecondsPerNode == other.nanosecondsPerNode &&
            selectedStartIndex == other.selectedStartIndex &&
            pathFlags.contentEquals(other.pathFlags) &&
            keyNodeFlags.contentEquals(other.keyNodeFlags) &&
            keyNodeMetrics.contentEquals(other.keyNodeMetrics) &&
            signature == other.signature
    }

    override fun hashCode(): Int {
        var result = path.contentHashCode()
        result = 31 * result + keyPath.contentHashCode()
        result = 31 * result + timeMs.hashCode()
        result = 31 * result + nodesExplored
        result = 31 * result + nanosecondsPerNode.hashCode()
        result = 31 * result + selectedStartIndex
        result = 31 * result + pathFlags.contentHashCode()
        result = 31 * result + keyNodeFlags.contentHashCode()
        result = 31 * result + keyNodeMetrics.contentHashCode()
        result = 31 * result + signature.hashCode()
        return result
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/kotlin/org/cobalt/api/pathfinder/jni/NativePathResult.kt
git commit -m "feat(jni): add NativePathResult data class"
```

---

## Task 7: Create NativeEtherwarpResult.kt

**Files:**
- Create: `src/main/kotlin/org/cobalt/api/pathfinder/jni/NativeEtherwarpResult.kt`

- [ ] **Step 1: Create the file**

```kotlin
package org.cobalt.api.pathfinder.jni

data class NativeEtherwarpResult(
    val path: IntArray,
    val angles: FloatArray,
    val timeMs: Long,
    val nodesExplored: Int,
    val nanosecondsPerNode: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NativeEtherwarpResult) return false
        return path.contentEquals(other.path) &&
            angles.contentEquals(other.angles) &&
            timeMs == other.timeMs &&
            nodesExplored == other.nodesExplored &&
            nanosecondsPerNode == other.nanosecondsPerNode
    }

    override fun hashCode(): Int {
        var result = path.contentHashCode()
        result = 31 * result + angles.contentHashCode()
        result = 31 * result + timeMs.hashCode()
        result = 31 * result + nodesExplored
        result = 31 * result + nanosecondsPerNode.hashCode()
        return result
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/kotlin/org/cobalt/api/pathfinder/jni/NativeEtherwarpResult.kt
git commit -m "feat(jni): add NativeEtherwarpResult data class"
```

---

## Task 8: Create NativePathfinderJNI.kt

The Kotlin singleton that loads the DLL and exposes every v5 JNI function. The DLL is loaded the same way as the old bridge (via `NativeLoader.extract`).

**Files:**
- Create: `src/main/kotlin/org/cobalt/api/pathfinder/jni/NativePathfinderJNI.kt`

- [ ] **Step 1: Create the file**

```kotlin
package org.cobalt.api.pathfinder.jni

import org.cobalt.pathfinder.NativeLoader

object NativePathfinderJNI {

    init {
        val path = NativeLoader.extract("natives/windows/cobalt_pathfinder.dll")
        System.load(path)
    }

    @JvmStatic external fun initNative(): Boolean

    @JvmStatic external fun setWorld(worldKey: String, minY: Int, maxY: Int)
    @JvmStatic external fun clearWorld()

    /**
     * sectionMask: bit i set = section i has data in sectionFlags.
     * sectionFlags: packed uint16_t voxel flags, 4096 shorts per section,
     *   index within section = ((y & 15) shl 8) or ((z & 15) shl 4) or (x & 15).
     */
    @JvmStatic external fun upsertChunk(
        chunkX: Int, chunkZ: Int,
        minY: Int, maxY: Int,
        sectionMask: Long,
        sectionFlags: ShortArray
    )

    /** updates: flat [x, y, z, flags(uint16 as int)] quads */
    @JvmStatic external fun applyBlockUpdates(updates: IntArray)

    @JvmStatic external fun cancelSearch()

    /**
     * startPoints / endPoints: flat [x,y,z, x,y,z, ...] int triples.
     * avoidMeta: flat [x,y,z,radiusSq,maxYDiff, ...] int quintuples.
     * avoidPenalty: one double per avoid zone.
     * Returns null if no path found.
     */
    @JvmStatic external fun findPath(
        startPoints: IntArray,
        endPoints: IntArray,
        isFly: Boolean,
        maxIterations: Int,
        heuristicWeight: Double,
        nonPrimaryStartPenalty: Double,
        moveOrderOffset: Int,
        avoidMeta: IntArray,
        avoidPenalty: DoubleArray
    ): NativePathResult?

    /**
     * Returns null if no etherwarp path found.
     * angles in result: flat [yaw0, pitch0, yaw1, pitch1, ...] float pairs.
     */
    @JvmStatic external fun findEtherwarpPath(
        goalX: Int, goalY: Int, goalZ: Int,
        startEyeX: Double, startEyeY: Double, startEyeZ: Double,
        maxIterations: Int,
        threadCount: Int,
        yawStep: Double,
        pitchStep: Double,
        newNodeCost: Double,
        heuristicWeight: Double,
        rayLength: Double,
        rewireEpsilon: Double,
        eyeHeight: Double
    ): NativeEtherwarpResult?
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/kotlin/org/cobalt/api/pathfinder/jni/NativePathfinderJNI.kt
git commit -m "feat(jni): add NativePathfinderJNI singleton"
```

---

## Task 9: Create ChunkSerializer.kt

Replaces `WorldBufferSerializer`. Subscribes to Fabric chunk-load and block-change events and pushes voxel-flag data to the DLL.

**Files:**
- Create: `src/main/kotlin/org/cobalt/api/pathfinder/jni/ChunkSerializer.kt`

Block→flag mapping (constants match `VoxelFlags` in `common.hpp`):
| Constant | Value |
|---|---|
| VF_PASSABLE | 0x0001 |
| VF_SOLID | 0x0002 |
| VF_PASSABLE_FLY | 0x0004 |
| VF_BLOCKING_WALL | 0x0008 |
| VF_FLUID | 0x0010 |
| VF_SLAB_BOTTOM | 0x0020 |
| VF_SLAB_TOP | 0x0040 |
| VF_FENCE_LIKE | 0x0080 |
| VF_STAIRS_BOTTOM | 0x0100 |
| VF_CARPET_LIKE | 0x0200 |
| VF_ETHER_PASSABLE | 0x0400 |
| VF_ETHER_TELEPORT_CLEAR | 0x0800 |
| VF_ETHER_FEET_BLOCKER | 0x1000 |
| AIR_DEFAULT | VF_PASSABLE or VF_PASSABLE_FLY or VF_ETHER_PASSABLE or VF_ETHER_TELEPORT_CLEAR = 0x0C05 |

- [ ] **Step 1: Create the file**

```kotlin
package org.cobalt.api.pathfinder.jni

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.tags.FluidTags
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.block.state.properties.Half

object ChunkSerializer {

    private const val VF_PASSABLE: Int           = 0x0001
    private const val VF_SOLID: Int              = 0x0002
    private const val VF_PASSABLE_FLY: Int       = 0x0004
    private const val VF_BLOCKING_WALL: Int      = 0x0008
    private const val VF_FLUID: Int              = 0x0010
    private const val VF_SLAB_BOTTOM: Int        = 0x0020
    private const val VF_SLAB_TOP: Int           = 0x0040
    private const val VF_FENCE_LIKE: Int         = 0x0080
    private const val VF_STAIRS_BOTTOM: Int      = 0x0100
    private const val VF_CARPET_LIKE: Int        = 0x0200
    private const val VF_ETHER_PASSABLE: Int     = 0x0400
    private const val VF_ETHER_TELEPORT_CLEAR: Int = 0x0800
    private const val VF_ETHER_FEET_BLOCKER: Int = 0x1000
    private const val AIR_FLAGS: Short = (VF_PASSABLE or VF_PASSABLE_FLY or VF_ETHER_PASSABLE or VF_ETHER_TELEPORT_CLEAR).toShort()

    private val heightCache = HashMap<BlockState, Double>(512)
    private val mpos = BlockPos.MutableBlockPos()

    fun register() {
        ClientChunkEvents.CHUNK_LOAD.register { world, chunk ->
            onChunkLoad(world, chunk)
        }
        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            // world is not yet ready at JOIN; rely on chunk-load events to populate
        }
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            NativePathfinderJNI.clearWorld()
            heightCache.clear()
        }
    }

    fun onChunkLoad(world: ClientLevel, chunk: LevelChunk) {
        val minY = world.minBuildHeight
        val maxY = world.maxBuildHeight
        val chunkX = chunk.pos.x
        val chunkZ = chunk.pos.z

        val worldKey = world.dimension().location().toString()
        NativePathfinderJNI.setWorld(worldKey, minY, maxY)

        val sections = chunk.sections
        val flagsList = mutableListOf<Short>()
        var sectionMask = 0L

        for (i in sections.indices) {
            val section = sections[i]
            if (section.hasOnlyAir()) continue

            sectionMask = sectionMask or (1L shl i)
            val sectionMinY = minY + i * 16

            for (ly in 0 until 16) {
                val worldY = sectionMinY + ly
                for (lz in 0 until 16) {
                    for (lx in 0 until 16) {
                        val state = section.getBlockState(lx, ly, lz)
                        val worldX = chunkX * 16 + lx
                        val worldZ = chunkZ * 16 + lz
                        mpos.set(worldX, worldY, worldZ)
                        flagsList.add(classifyState(state, world))
                    }
                }
            }
        }

        if (sectionMask == 0L) return

        NativePathfinderJNI.upsertChunk(
            chunkX, chunkZ,
            minY, maxY,
            sectionMask,
            flagsList.toShortArray()
        )
    }

    fun applyBlockUpdate(x: Int, y: Int, z: Int, state: BlockState, world: ClientLevel) {
        mpos.set(x, y, z)
        val flags = classifyState(state, world).toInt() and 0xFFFF
        NativePathfinderJNI.applyBlockUpdates(intArrayOf(x, y, z, flags))
    }

    fun invalidate() {
        NativePathfinderJNI.clearWorld()
        heightCache.clear()
    }

    private fun classifyState(state: BlockState, world: ClientLevel): Short {
        if (state.isAir) return AIR_FLAGS

        val fluid = state.fluidState
        if (!fluid.isEmpty) {
            return when {
                fluid.`is`(FluidTags.LAVA) -> (VF_FLUID or VF_PASSABLE_FLY).toShort()
                else -> (VF_FLUID or VF_PASSABLE_FLY).toShort()
            }
        }

        if (state.`is`(BlockTags.CLIMBABLE)) {
            return (VF_PASSABLE or VF_PASSABLE_FLY).toShort()
        }

        if (state.block is StairBlock) {
            return classifyStair(state)
        }

        val maxHeight = heightCache.getOrPut(state) {
            val shape = state.getCollisionShape(world, mpos)
            if (shape.isEmpty) 0.0 else shape.bounds().maxY
        }

        return when {
            maxHeight < 0.1 -> (VF_PASSABLE or VF_PASSABLE_FLY or VF_CARPET_LIKE or VF_ETHER_PASSABLE or VF_ETHER_TELEPORT_CLEAR).toShort()
            maxHeight < 0.6 -> (VF_SLAB_BOTTOM or VF_SOLID).toShort()
            maxHeight < 1.1 -> (VF_SOLID or VF_BLOCKING_WALL or VF_ETHER_FEET_BLOCKER).toShort()
            else -> (VF_SOLID or VF_BLOCKING_WALL or VF_FENCE_LIKE or VF_ETHER_FEET_BLOCKER).toShort()
        }
    }

    private fun classifyStair(state: BlockState): Short {
        if (state.getValue(StairBlock.HALF) != Half.BOTTOM) {
            return (VF_SOLID or VF_BLOCKING_WALL or VF_ETHER_FEET_BLOCKER).toShort()
        }
        return (VF_STAIRS_BOTTOM or VF_SOLID).toShort()
    }
}
```

- [ ] **Step 2: Wire ChunkSerializer into BlockChangeEvent**

In `src/main/kotlin/org/cobalt/internal/pathfinding/PathfindingModule.kt`, find the existing `BlockChangeEvent` handler. Add a call to `ChunkSerializer.applyBlockUpdate(...)` inside it. The event provides `event.pos` (BlockPos) and `event.newState` (BlockState). Example:

```kotlin
@SubscribeEvent
fun onBlockChange(event: BlockChangeEvent) {
    val level = mc.level ?: return
    ChunkSerializer.applyBlockUpdate(event.pos.x, event.pos.y, event.pos.z, event.newState, level)
    // ... existing handler code unchanged
}
```

If `PathfindingModule` has no `BlockChangeEvent` handler, add one in `ChunkSerializer` itself by registering with `EventBus` in `ChunkSerializer.register()`:

```kotlin
EventBus.register(this)

@SubscribeEvent
fun onBlockChange(event: BlockChangeEvent) {
    val level = net.minecraft.client.Minecraft.getInstance().level ?: return
    applyBlockUpdate(event.pos.x, event.pos.y, event.pos.z, event.newState, level)
}
```

- [ ] **Step 3: Register ChunkSerializer in Cobalt.kt**

Find `Cobalt.onInitializeClient()` (or wherever `WorldBufferSerializer` is initialized). Add:

```kotlin
ChunkSerializer.register()
```

- [ ] **Step 4: Commit**

```bash
git add src/main/kotlin/org/cobalt/api/pathfinder/jni/ChunkSerializer.kt
git commit -m "feat(jni): add ChunkSerializer — chunk-push world state for v5 engine"
```

---

## Task 10: Rewrite NativePathfinder.kt

Replaces the tick-buffer engine wrapper with an async pathfinder + Kotlin path-execution state machine. Public API (method names and signatures) is preserved so that `PathfindingModule`, `RoutesModule`, etc. compile unchanged.

**Files:**
- Modify: `src/main/kotlin/org/cobalt/api/pathfinder/jni/NativePathfinder.kt`

The state machine:
- `IDLE` → no target set
- `PLANNING` → `findPath` running on a background thread
- `EXECUTING` → following cached path nodes
- `REPLANNING` → new `findPath` running while still following old path
- `ARRIVED` → within arrival radius of final goal
- `FAILED` → `findPath` returned null

- [ ] **Step 1: Rewrite the file**

```kotlin
package org.cobalt.api.pathfinder.jni

import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.InventoryUtils
import org.cobalt.api.util.TickScheduler
import org.cobalt.api.util.player.MovementManager
import org.cobalt.internal.etherwarp.EtherwarpLogic
import org.cobalt.internal.pathfinding.DebugLog
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

object NativePathfinder {

    val isInitialized: Boolean get() = true

    var cachedPathNodes: List<Vec3> = emptyList()
        private set
    var pathNodeCursor: Int = 0
        internal set

    var availabilityFlagsOverride: Int? = null

    private var state: PathStatus = PathStatus.IDLE
    private var lastTickStatus: PathStatus = PathStatus.IDLE

    private var goalX: Int = 0
    private var goalY: Int = 0
    private var goalZ: Int = 0
    private var arrivalRadius: Double = 1.8
    private var isRoutePath: Boolean = false

    private var routeWaypoints: DoubleArray = doubleArrayOf()
    private var routeLoop: Boolean = false
    private var routeProfile: MovementProfile = MovementProfile.DEFAULT
    private var routeArrivalRadius: Double = 1.8
    private var routeWpIndex: Int = 0

    @Volatile private var searchResult: NativePathResult? = null
    @Volatile private var searchFailed: Boolean = false
    private var searchThread: Thread? = null

    private var prevJump: Boolean = false
    private var teleportFiredNodeKey: Long = -1L
    private var teleportRestoreSlot: Int = -1

    private var stuckTicks: Int = 0
    private var lastStuckCheckPos: Vec3 = Vec3.ZERO

    private const val STUCK_CHECK_INTERVAL = 60
    private const val STUCK_THRESHOLD = 0.5
    private const val REPLAN_OFF_PATH_DIST = 4.0
    private const val TELEPORT_YAW_THRESHOLD = 8f
    private const val TELEPORT_PITCH_THRESHOLD = 8f
    private const val MAX_ITERATIONS = 500_000
    private const val HEURISTIC_WEIGHT = 1.05

    fun init() { /* DLL loaded by NativePathfinderJNI singleton init */ }
    fun destroy() { cancelSearch(); releaseGuidedControl() }

    fun setTarget(x: Double, y: Double, z: Double) {
        setTargetWithRadius(x, y, z, 1.8)
    }

    fun setTargetWithRadius(x: Double, y: Double, z: Double, radius: Double) {
        isRoutePath = false
        goalX = x.toInt(); goalY = y.toInt(); goalZ = z.toInt()
        arrivalRadius = radius
        startSearch()
    }

    fun setRoute(waypoints: DoubleArray, loop: Boolean, profile: MovementProfile) {
        setRouteWithRadius(waypoints, loop, profile, 1.8)
    }

    fun setRouteWithRadius(waypoints: DoubleArray, loop: Boolean, profile: MovementProfile, radius: Double) {
        isRoutePath = true
        routeWaypoints = waypoints
        routeLoop = loop
        routeProfile = profile
        routeArrivalRadius = radius
        routeWpIndex = 0
        if (waypoints.size >= 3) {
            goalX = waypoints[waypoints.size - 3].toInt()
            goalY = waypoints[waypoints.size - 2].toInt()
            goalZ = waypoints[waypoints.size - 1].toInt()
        }
        startSearch()
    }

    fun stop() {
        cancelSearch()
        state = PathStatus.IDLE
        cachedPathNodes = emptyList()
        pathNodeCursor = 0
        prevJump = false
        teleportFiredNodeKey = -1L
        availabilityFlagsOverride = null
        routeWpIndex = 0
        stuckTicks = 0
        releaseGuidedControl()
    }

    fun onLevelChange() {
        cancelSearch()
        ChunkSerializer.invalidate()
        state = PathStatus.IDLE
        cachedPathNodes = emptyList()
        pathNodeCursor = 0
        prevJump = false
        teleportFiredNodeKey = -1L
        availabilityFlagsOverride = null
        routeWpIndex = 0
        stuckTicks = 0
        releaseGuidedControl()
    }

    val status: PathStatus get() = state

    fun tick(): PathCommand? {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: run { releaseGuidedControl(); return null }

        // Consume completed search result
        if (searchFailed) {
            searchFailed = false
            searchThread = null
            state = PathStatus.FAILED
        } else {
            val result = searchResult
            if (result != null) {
                searchResult = null
                searchThread = null
                applySearchResult(result)
            }
        }

        when (state) {
            PathStatus.IDLE, PathStatus.FAILED, PathStatus.ARRIVED -> {
                if (state != lastTickStatus) {
                    prevJump = false
                    cachedPathNodes = emptyList()
                    pathNodeCursor = 0
                    releaseGuidedControl()
                }
                lastTickStatus = state
                return null
            }
            PathStatus.PLANNING -> {
                lastTickStatus = state
                return null
            }
            else -> {}
        }

        // EXECUTING or REPLANNING
        val nodes = cachedPathNodes
        if (nodes.isEmpty()) {
            state = PathStatus.FAILED
            releaseGuidedControl()
            lastTickStatus = state
            return null
        }

        val playerPos = Vec3(player.x, player.y, player.z)

        // Advance cursor past nodes we've passed
        while (pathNodeCursor < nodes.size - 1) {
            val wp = nodes[pathNodeCursor]
            val dx = wp.x - player.x
            val dz = wp.z - player.z
            if (sqrt(dx * dx + dz * dz) < 0.8) pathNodeCursor++
            else break
        }

        // Check arrival at final goal
        val finalNode = nodes.last()
        val dxFinal = finalNode.x - player.x
        val dyFinal = finalNode.y - player.y
        val dzFinal = finalNode.z - player.z
        val distFinal = sqrt(dxFinal * dxFinal + dyFinal * dyFinal + dzFinal * dzFinal)
        if (distFinal <= arrivalRadius) {
            // Route: advance to next waypoint
            if (isRoutePath && routeWaypoints.size >= 3) {
                routeWpIndex++
                val totalWp = routeWaypoints.size / 3
                if (routeWpIndex < totalWp) {
                    val bi = routeWpIndex * 3
                    goalX = routeWaypoints[bi].toInt()
                    goalY = routeWaypoints[bi + 1].toInt()
                    goalZ = routeWaypoints[bi + 2].toInt()
                    startSearch()
                    lastTickStatus = state
                    return null
                } else if (routeLoop) {
                    routeWpIndex = 0
                    val bi = 0
                    goalX = routeWaypoints[bi].toInt()
                    goalY = routeWaypoints[bi + 1].toInt()
                    goalZ = routeWaypoints[bi + 2].toInt()
                    startSearch()
                    lastTickStatus = state
                    return null
                }
            }
            state = PathStatus.ARRIVED
            cachedPathNodes = emptyList()
            pathNodeCursor = 0
            releaseGuidedControl()
            lastTickStatus = state
            return null
        }

        // Stuck detection
        stuckTicks++
        if (stuckTicks >= STUCK_CHECK_INTERVAL) {
            stuckTicks = 0
            val moved = playerPos.distanceTo(lastStuckCheckPos)
            if (moved < STUCK_THRESHOLD && state == PathStatus.EXECUTING) {
                startReplan(playerPos)
            }
            lastStuckCheckPos = playerPos
        }

        // Off-path detection
        val curNode = nodes[pathNodeCursor]
        val dxCur = curNode.x - player.x
        val dzCur = curNode.z - player.z
        if (sqrt(dxCur * dxCur + dzCur * dzCur) > REPLAN_OFF_PATH_DIST && state == PathStatus.EXECUTING) {
            startReplan(playerPos)
        }

        // Compute movement command toward current waypoint
        val target = nodes[pathNodeCursor]
        val dx = target.x - player.x
        val dz = target.z - player.z
        val dy = target.y - player.y

        val targetYaw = Math.toDegrees(atan2(-dx, dz)).toFloat()
        val targetPitch = 0f

        val jumpRaw = player.onGround() && dy > 0.5 && sqrt(dx * dx + dz * dz) < 2.5
        if (jumpRaw && player.onGround()) prevJump = false
        val jumpPulse = jumpRaw && !prevJump
        prevJump = jumpRaw

        val aotvSlot = EtherwarpLogic.findEtherwarpHotbarSlot()
        val computedFlags = if (aotvSlot >= 0) 0x1 or 0x2 else 0
        val availabilityFlags = availabilityFlagsOverride ?: computedFlags

        val cmd = PathCommand(
            forward = true,
            back = false,
            jump = jumpPulse,
            sneak = false,
            sprint = true,
            targetYaw = targetYaw,
            targetPitch = targetPitch,
            status = state,
            activeAction = ActionType.WALK,
            distanceToTarget = distFinal.toFloat()
        )

        // AOTV / Etherwarp fire (unchanged from original)
        if (state == PathStatus.EXECUTING &&
            (cmd.activeAction == ActionType.AOTV || cmd.activeAction == ActionType.ETHERWARP)) {
            val yawErr = abs(AngleUtils.getRotationDelta(player.yRot, cmd.targetYaw))
            val pitchErr = abs(player.xRot - cmd.targetPitch)
            val aligned = yawErr < TELEPORT_YAW_THRESHOLD &&
                (cmd.activeAction == ActionType.AOTV || pitchErr < TELEPORT_PITCH_THRESHOLD)
            val nodeKey = cachedPathNodes.getOrNull(pathNodeCursor)
                ?.let { n -> (n.x.toLong() and 0x1FFFFF) or ((n.z.toLong() and 0x1FFFFF) shl 21) }
                ?: -1L
            if (aligned && nodeKey != teleportFiredNodeKey && aotvSlot >= 0) {
                teleportFiredNodeKey = nodeKey
                fireTeleport(cmd.activeAction == ActionType.ETHERWARP, aotvSlot)
            }
        } else if (cmd.activeAction != ActionType.AOTV && cmd.activeAction != ActionType.ETHERWARP) {
            teleportFiredNodeKey = -1L
        }

        if (state != lastTickStatus && state == PathStatus.EXECUTING) {
            DebugLog.debug(mc, "NativePathfinder", "executing path, ${nodes.size} key nodes")
        }
        lastTickStatus = state
        return cmd
    }

    private fun startSearch() {
        cancelSearch()
        searchFailed = false
        searchResult = null
        state = PathStatus.PLANNING
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return
        val startX = player.blockX; val startY = player.blockY; val startZ = player.blockZ
        val gx = goalX; val gy = goalY; val gz = goalZ
        val isFly = routeProfile == MovementProfile.FLY

        searchThread = Thread {
            try {
                val result = NativePathfinderJNI.findPath(
                    startPoints = intArrayOf(startX, startY, startZ),
                    endPoints = intArrayOf(gx, gy, gz),
                    isFly = isFly,
                    maxIterations = MAX_ITERATIONS,
                    heuristicWeight = HEURISTIC_WEIGHT,
                    nonPrimaryStartPenalty = 0.0,
                    moveOrderOffset = 0,
                    avoidMeta = intArrayOf(),
                    avoidPenalty = doubleArrayOf()
                )
                if (result != null && result.keyPath.size >= 3) {
                    searchResult = result
                } else {
                    searchFailed = true
                }
            } catch (_: Exception) {
                searchFailed = true
            }
        }.also { it.isDaemon = true; it.name = "cobalt-pathfinder"; it.start() }
    }

    private fun startReplan(playerPos: Vec3) {
        if (state == PathStatus.REPLANNING) return
        state = PathStatus.REPLANNING
        cancelSearch(keepState = true)
        val gx = goalX; val gy = goalY; val gz = goalZ
        val sx = playerPos.x.toInt(); val sy = playerPos.y.toInt(); val sz = playerPos.z.toInt()
        val isFly = routeProfile == MovementProfile.FLY

        searchThread = Thread {
            try {
                val result = NativePathfinderJNI.findPath(
                    startPoints = intArrayOf(sx, sy, sz),
                    endPoints = intArrayOf(gx, gy, gz),
                    isFly = isFly,
                    maxIterations = MAX_ITERATIONS,
                    heuristicWeight = HEURISTIC_WEIGHT,
                    nonPrimaryStartPenalty = 0.0,
                    moveOrderOffset = 0,
                    avoidMeta = intArrayOf(),
                    avoidPenalty = doubleArrayOf()
                )
                if (result != null && result.keyPath.size >= 3) {
                    searchResult = result
                } else {
                    searchFailed = true
                }
            } catch (_: Exception) {
                searchFailed = true
            }
        }.also { it.isDaemon = true; it.name = "cobalt-pathfinder-replan"; it.start() }
    }

    private fun applySearchResult(result: NativePathResult) {
        val flat = result.keyPath
        val nodes = ArrayList<Vec3>(flat.size / 3)
        var i = 0
        while (i + 2 < flat.size) {
            nodes.add(Vec3(flat[i] + 0.5, flat[i + 1].toDouble(), flat[i + 2] + 0.5))
            i += 3
        }
        cachedPathNodes = nodes
        pathNodeCursor = 0
        stuckTicks = 0
        state = PathStatus.EXECUTING
    }

    private fun cancelSearch(keepState: Boolean = false) {
        NativePathfinderJNI.cancelSearch()
        searchThread?.interrupt()
        searchThread = null
        searchResult = null
        searchFailed = false
        if (!keepState) state = PathStatus.IDLE
    }

    private fun fireTeleport(isEtherwarp: Boolean, aotvSlot: Int) {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return
        teleportRestoreSlot = player.inventory.selectedSlot
        InventoryUtils.holdHotbarSlot(aotvSlot)
        if (isEtherwarp) mc.options.keyShift?.setDown(true)
        mc.options.keyUse?.setDown(true)
        TickScheduler.schedule(1L) {
            mc.options.keyUse?.setDown(false)
            if (isEtherwarp) mc.options.keyShift?.setDown(false)
        }
        TickScheduler.schedule(4L) {
            val restore = teleportRestoreSlot
            if (restore in 0..8) InventoryUtils.holdHotbarSlot(restore)
            teleportRestoreSlot = -1
        }
    }

    private fun releaseGuidedControl() {
        MovementManager.setLookLock(false)
        RotationExecutor.stopIfUsing(PathfinderRotationStrategy)
    }
}
```

- [ ] **Step 2: Add FLY to MovementProfile enum**

Open `src/main/kotlin/org/cobalt/api/pathfinder/jni/MovementProfile.kt` (or wherever the enum is defined). Add `FLY` as a new entry:

```kotlin
enum class MovementProfile {
    DEFAULT, MINING, COMBAT, GROUND_ONLY, FLY
}
```

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/api/pathfinder/jni/NativePathfinder.kt
git add src/main/kotlin/org/cobalt/api/pathfinder/jni/MovementProfile.kt
git commit -m "feat(pathfinder): rewrite NativePathfinder for v5 async findPath + Kotlin executor"
```

---

## Task 11: Update EtherwarpLogic.kt

Wire `findEtherwarpPath` so that when etherwarp needs a rotation search, it calls the v5 native search instead of doing it in Kotlin.

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/etherwarp/EtherwarpLogic.kt`

- [ ] **Step 1: Add findEtherwarpAngles() function**

Find the location in `EtherwarpLogic.kt` where the current angle search / rotation lookup happens. Add the following function (or replace the existing angle-search call):

```kotlin
fun findEtherwarpAngles(
    goalX: Int, goalY: Int, goalZ: Int,
    eyeX: Double, eyeY: Double, eyeZ: Double
): Pair<Float, Float>? {
    val result = NativePathfinderJNI.findEtherwarpPath(
        goalX = goalX, goalY = goalY, goalZ = goalZ,
        startEyeX = eyeX, startEyeY = eyeY, startEyeZ = eyeZ,
        maxIterations = 50_000,
        threadCount = 1,
        yawStep = 0.5,
        pitchStep = 0.5,
        newNodeCost = 1.0,
        heuristicWeight = 1.0,
        rayLength = 60.0,
        rewireEpsilon = 0.01,
        eyeHeight = 1.62
    ) ?: return null

    if (result.angles.size < 2) return null
    return Pair(result.angles[0], result.angles[1])
}
```

- [ ] **Step 2: Replace the existing Kotlin-side angle search call site**

Find the place in `EtherwarpLogic` where yaw/pitch toward an etherwarp target is computed. Replace it with a call to `findEtherwarpAngles(...)`. Pass the target block coordinates and player eye position. If the result is non-null, use `result.first` as yaw and `result.second` as pitch for `RotationExecutor`.

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/etherwarp/EtherwarpLogic.kt
git commit -m "feat(etherwarp): use v5 native RRT* angle search for etherwarp targeting"
```

---

## Task 12: Delete WorldBufferSerializer.kt and NativePathfinderBridge.java

**Files:**
- Delete: `src/main/kotlin/org/cobalt/api/pathfinder/jni/WorldBufferSerializer.kt`
- Delete: `src/main/java/org/cobalt/pathfinder/NativePathfinderBridge.java`

- [ ] **Step 1: Check for remaining references**

```bash
grep -r "WorldBufferSerializer" src/
grep -r "NativePathfinderBridge" src/
```

Expected: zero results. If any remain, update those callers to use `ChunkSerializer` or `NativePathfinderJNI` respectively before deleting.

- [ ] **Step 2: Delete files**

```bash
rm src/main/kotlin/org/cobalt/api/pathfinder/jni/WorldBufferSerializer.kt
rm src/main/java/org/cobalt/pathfinder/NativePathfinderBridge.java
```

- [ ] **Step 3: Commit**

```bash
git add -u src/
git commit -m "chore: remove WorldBufferSerializer and NativePathfinderBridge"
```

---

## Task 13: Build mod and verify

**Files:**
- Run: `./gradlew build`

- [ ] **Step 1: Build**

```bash
./gradlew build
```

Expected: BUILD SUCCESSFUL, no compile errors. Common failure modes:
- `NativePathfinderJNI` class not found at runtime → verify `NativeLoader.extract` path matches the DLL resource path (`natives/windows/cobalt_pathfinder.dll`)
- `NativePathResult` class not found in JNI → verify `env->FindClass("org/cobalt/pathfinder/jni/NativePathResult")` in `jni_bridge.cpp` matches the Kotlin package
- `MovementProfile.FLY` missing → verify the enum was updated in Task 10

- [ ] **Step 2: If build fails — check JNI class path**

The JNI bridge looks up `"org/cobalt/pathfinder/jni/NativePathResult"` and `"org/cobalt/pathfinder/jni/NativeEtherwarpResult"` at runtime. The Kotlin data classes live in package `org.cobalt.api.pathfinder.jni`. JNI uses slashes and the full class path including `api`:

Update `jni_bridge.cpp` if needed:
- `"org/cobalt/pathfinder/jni/NativePathResult"` → `"org/cobalt/api/pathfinder/jni/NativePathResult"`
- `"org/cobalt/pathfinder/jni/NativeEtherwarpResult"` → `"org/cobalt/api/pathfinder/jni/NativeEtherwarpResult"`

Rebuild DLL and copy after fixing:
```bash
./gradlew buildNative && ./gradlew copyNativeDll
```

- [ ] **Step 3: Commit final build**

```bash
git add src/main/resources/natives/windows/cobalt_pathfinder.dll
git commit -m "feat: v5 pathfinder port complete — build passing"
```
