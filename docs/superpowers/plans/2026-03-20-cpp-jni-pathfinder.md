# C++ JNI Pathfinder Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace all per-macro pathfinding with a single C++ JNI engine (`cobalt_pathfinder.dll`) that handles A*, movement, stuck detection, and human-like rotation for all macros (routes, mining, garden, farming, pig, combat/slayer).

**Architecture:** C++ engine owns the full state machine — planning, execution, recovery, rotation. Kotlin calls `NativePathfinder.tick()` once per `TickEvent.Start` and receives `PathCommand` (keys + rotation). All macros replace their own pathfinding with 2 lines: `setRoute(...)` + `tick()?.applyToPlayer()`.

**Tech Stack:** C++17 / MSVC / CMake (Windows only), JNI, Kotlin, Fabric Minecraft 1.21.11

---

## File Map

**New — C++ (natives/)**
- `natives/CMakeLists.txt` — build definition
- `natives/include/Types.h` — all shared structs/enums (Vec3i, PathNode, PathCommand, PathStatus, ActionType, MovementProfile)
- `natives/src/engine/WorldAccessor.h/.cpp` — hybrid buffer + JNI callback block query
- `natives/src/engine/MovementExpander.h/.cpp` — A* neighbor/edge generation for all action types
- `natives/src/engine/AStarPlanner.h/.cpp` — async A* on `std::thread`
- `natives/src/engine/StuckDetector.h/.cpp` — position history, stuck detection + recovery trigger
- `natives/src/engine/RotationController.h/.cpp` — bezier curves, GCD correction, micro-noise
- `natives/src/engine/PathExecutor.h/.cpp` — IDLE/PLANNING/EXECUTING/RECOVERING/REPLANNING/ARRIVED state machine
- `natives/src/engine/PathfinderEngine.h/.cpp` — top-level object; owns all subsystems
- `natives/src/pathfinder_jni.cpp` — JNI entry points

**New — Java**
- `src/main/java/org/cobalt/pathfinder/NativePathfinderBridge.java` — `native` method declarations + DLL loading
- `src/main/java/org/cobalt/pathfinder/NativeLoader.java` — extracts DLL from JAR to `%TEMP%\cobalt\`

**New — Kotlin** (package `org.cobalt.api.pathfinder.jni` — `native` is a Kotlin keyword)
- `src/main/kotlin/org/cobalt/api/pathfinder/jni/PathStatus.kt`
- `src/main/kotlin/org/cobalt/api/pathfinder/jni/ActionType.kt`
- `src/main/kotlin/org/cobalt/api/pathfinder/jni/MovementProfile.kt`
- `src/main/kotlin/org/cobalt/api/pathfinder/jni/PathCommand.kt`
- `src/main/kotlin/org/cobalt/api/pathfinder/jni/PathfinderRotationStrategy.kt` — pass-through strategy (C++ already smoothed)
- `src/main/kotlin/org/cobalt/api/pathfinder/jni/NativePathfinder.kt` — singleton all macros call
- `src/main/kotlin/org/cobalt/api/pathfinder/jni/WorldBufferSerializer.kt` — serializes nearby MC blocks to `byte[]`

**Modified**
- `build.gradle.kts` — add `buildNative` task, `processResources` depends on it
- `src/main/kotlin/org/cobalt/internal/mining/RoutesModule.kt`
- `src/main/kotlin/org/cobalt/internal/mining/MiningMacroModule.kt`
- `src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt`
- `src/main/kotlin/org/cobalt/internal/garden/GardenMacroModule.kt`
- `src/main/kotlin/org/cobalt/internal/farming/FarmingMacroModule.kt`
- `src/main/kotlin/org/cobalt/internal/pig/PigMacroModule.kt`
- `src/main/kotlin/org/cobalt/internal/combat/CombatMacroModule.kt`

**Deleted (Phase 7)**
- `src/main/kotlin/org/cobalt/internal/pathfinding/DuskPathfinder.kt`
- `src/main/kotlin/org/cobalt/internal/pathfinding/PathOverlayRenderer.kt` (dead code after migration)
- `src/main/kotlin/org/cobalt/api/pathfinder/PathExecutor.kt`
- `src/main/kotlin/org/cobalt/internal/pathfinding/PathPlanProfiles.kt`
- `src/main/kotlin/org/cobalt/internal/pathfinding/PathPlanProfile.kt`

---

## World Buffer Format

A region of blocks is serialized into a flat `byte[]` of size `64 × 32 × 64 = 131072`.
- Origin: `(bufOriginX, bufOriginY, bufOriginZ)` = player block pos minus `(32, 16, 32)`
- Index: `(x - bx) + (z - bz) * 64 + (y - by) * 64 * 64`
- Values: `0` = air/passable, `1` = solid, `2` = water, `3` = lava, `4` = ladder

## JNI Command Encoding

`update()` returns `int[10]`:
```
[0] forward  [1] back  [2] jump  [3] sneak  [4] sprint
[5] targetYaw          (Float.intBitsToFloat — raw, no GCD)
[6] targetPitch        (Float.intBitsToFloat — raw, no GCD)
[7] PathStatus ordinal
[8] ActionType ordinal
[9] distanceToTarget   (Float.intBitsToFloat — horizontal dist to current waypoint)
```

**GCD note:** C++ outputs raw angles. `RotationExecutor` on the Kotlin side applies GCD correction once. C++ must NOT apply GCD itself.

---

## Task 1: C++ Project Skeleton

**Files:**
- Create: `natives/CMakeLists.txt`
- Create: `natives/include/Types.h`
- Create stub `.h/.cpp` for each engine file (empty class bodies that compile)

- [ ] **Step 1: Create `natives/include/Types.h`**

```cpp
#pragma once
#include <cstdint>

enum class PathStatus : int {
    IDLE = 0, PLANNING = 1, EXECUTING = 2,
    RECOVERING = 3, REPLANNING = 4, ARRIVED = 5, FAILED = 6
};

enum class ActionType : int {
    WALK = 0, SPRINT = 1, JUMP = 2, SPRINT_JUMP = 3,
    FALL = 4, LADDER = 5, WATER_SWIM = 6, AOTV = 7, ETHERWARP = 8
};

enum class MovementProfile : int {
    DEFAULT = 0, MINING = 1, COMBAT = 2, GROUND_ONLY = 3
};

struct Vec3d { double x, y, z; };
struct Vec3i { int x, y, z; };

struct PathCommand {
    bool forward, back, jump, sneak, sprint;
    float targetYaw, targetPitch;   // raw angles — no GCD applied
    PathStatus status;
    ActionType activeAction;
    float distanceToTarget;         // horizontal dist to current waypoint target
};

// World buffer constants
static constexpr int BUF_W = 64, BUF_H = 32, BUF_D = 64;
static constexpr int BUF_SIZE = BUF_W * BUF_H * BUF_D;
static constexpr int BUF_STRIDE_Z = BUF_W;           // z-stride: skip one row of X
static constexpr int BUF_STRIDE_Y = BUF_W * BUF_D;  // y-stride: skip one XZ plane

// Block type bytes
static constexpr uint8_t BT_AIR    = 0;
static constexpr uint8_t BT_SOLID  = 1;
static constexpr uint8_t BT_WATER  = 2;
static constexpr uint8_t BT_LAVA   = 3;
static constexpr uint8_t BT_LADDER = 4;
```

- [ ] **Step 2: Create `natives/CMakeLists.txt`**

```cmake
cmake_minimum_required(VERSION 3.20)
project(cobalt_pathfinder LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(JNI REQUIRED)

set(SOURCES
    src/pathfinder_jni.cpp
    src/engine/PathfinderEngine.cpp
    src/engine/AStarPlanner.cpp
    src/engine/PathExecutor.cpp
    src/engine/MovementExpander.cpp
    src/engine/WorldAccessor.cpp
    src/engine/RotationController.cpp
    src/engine/StuckDetector.cpp
)

add_library(cobalt_pathfinder SHARED ${SOURCES})

target_include_directories(cobalt_pathfinder PRIVATE
    include
    ${JNI_INCLUDE_DIRS}
)

target_link_libraries(cobalt_pathfinder PRIVATE ${JNI_LIBRARIES})

if(MSVC)
    target_compile_options(cobalt_pathfinder PRIVATE /O2 /W3)
    set_target_properties(cobalt_pathfinder PROPERTIES
        WINDOWS_EXPORT_ALL_SYMBOLS OFF)
endif()
```

- [ ] **Step 3: Create empty stub `.h` + `.cpp` for each engine file**

For each of: `WorldAccessor`, `MovementExpander`, `AStarPlanner`, `StuckDetector`, `RotationController`, `PathExecutor`, `PathfinderEngine` — create a header with an empty class and a `.cpp` that includes it. Also create an empty `pathfinder_jni.cpp`.

Example `natives/src/engine/WorldAccessor.h`:
```cpp
#pragma once
#include "Types.h"
#include <cstdint>
#include <jni.h>

class WorldAccessor {
public:
    void setBuffer(const uint8_t* buf, int bx, int by, int bz);
    void setCallbackEnv(JNIEnv* env, jobject callbackObj);
    uint8_t getBlock(int x, int y, int z) const;
private:
    const uint8_t* buffer_ = nullptr;
    int bx_ = 0, by_ = 0, bz_ = 0;
    JNIEnv* env_ = nullptr;
    jobject cb_ = nullptr;
};
```

- [ ] **Step 4: Configure CMake build directory**

```bash
cd natives
cmake -B build -G "Visual Studio 17 2022" -A x64
```
Expected: CMake configures without errors, finds JNI headers from the JDK.

- [ ] **Step 5: Build to confirm stubs compile**

```bash
cmake --build build --config Release
```
Expected: `cobalt_pathfinder.dll` appears in `natives/build/Release/`.

- [ ] **Step 6: Commit**

```bash
git add natives/
git commit -m "feat: add C++ pathfinder project skeleton (stubs compile)"
```

---

## Task 2: WorldAccessor

**Files:**
- Modify: `natives/src/engine/WorldAccessor.h`
- Modify: `natives/src/engine/WorldAccessor.cpp`

WorldAccessor is the block query layer. It first checks the pre-serialized buffer; if the requested block is outside that region it calls back into the JVM.

- [ ] **Step 1: Implement `WorldAccessor.h`**

```cpp
#pragma once
#include "Types.h"
#include <cstdint>
#include <jni.h>

class WorldAccessor {
public:
    // Called each tick before planning/execution
    void setBuffer(const uint8_t* buf, int bx, int by, int bz);

    uint8_t getBlock(int x, int y, int z) const;
    bool isSolid(int x, int y, int z) const;
    bool isPassable(int x, int y, int z) const;  // air or water
    bool isWalkable(int x, int y, int z) const;  // solid floor + 2 air above
    bool isLadder(int x, int y, int z) const;
    bool isWater(int x, int y, int z) const;
    bool isLava(int x, int y, int z) const;

    // Accessors for buffer snapshot (used by AStarPlanner to copy buffer before threading)
    const uint8_t* bufferData() const { return buffer_; }
    int originX() const { return bx_; }
    int originY() const { return by_; }
    int originZ() const { return bz_; }

private:
    const uint8_t* buffer_ = nullptr;
    int bx_ = 0, by_ = 0, bz_ = 0;

    bool inBuffer(int x, int y, int z) const;
    uint8_t bufferAt(int x, int y, int z) const;
    uint8_t callbackBlock(int x, int y, int z) const;
};
```

- [ ] **Step 2: Implement `WorldAccessor.cpp`**

```cpp
#include "WorldAccessor.h"

void WorldAccessor::setBuffer(const uint8_t* buf, int bx, int by, int bz) {
    buffer_ = buf; bx_ = bx; by_ = by; bz_ = bz;
}


bool WorldAccessor::inBuffer(int x, int y, int z) const {
    return buffer_ &&
        x >= bx_ && x < bx_ + BUF_W &&
        y >= by_ && y < by_ + BUF_H &&
        z >= bz_ && z < bz_ + BUF_D;
}

uint8_t WorldAccessor::bufferAt(int x, int y, int z) const {
    // Use named strides — do not inline BUF_W*BUF_D directly (W==D is not guaranteed)
    int idx = (x - bx_) + (z - bz_) * BUF_STRIDE_Z + (y - by_) * BUF_STRIDE_Y;
    return buffer_[idx];
}

uint8_t WorldAccessor::callbackBlock(int, int, int) const {
    // JNI callbacks from background threads require AttachCurrentThread — not implemented.
    // Buffer is sized to cover all Skyblock pathfinding; treat out-of-range as SOLID.
    return BT_SOLID;
}

uint8_t WorldAccessor::getBlock(int x, int y, int z) const {
    return inBuffer(x, y, z) ? bufferAt(x, y, z) : callbackBlock(x, y, z);
}

bool WorldAccessor::isSolid(int x, int y, int z) const   { return getBlock(x,y,z) == BT_SOLID; }
bool WorldAccessor::isPassable(int x, int y, int z) const { auto b = getBlock(x,y,z); return b == BT_AIR || b == BT_WATER; }
bool WorldAccessor::isLadder(int x, int y, int z) const  { return getBlock(x,y,z) == BT_LADDER; }
bool WorldAccessor::isWater(int x, int y, int z) const   { return getBlock(x,y,z) == BT_WATER; }
bool WorldAccessor::isLava(int x, int y, int z) const    { return getBlock(x,y,z) == BT_LAVA; }

bool WorldAccessor::isWalkable(int x, int y, int z) const {
    return isSolid(x, y, z) && isPassable(x, y+1, z) && isPassable(x, y+2, z);
}
```

- [ ] **Step 3: Build**
```bash
cmake --build natives/build --config Release
```
Expected: compiles clean.

- [ ] **Step 4: Commit**
```bash
git add natives/src/engine/WorldAccessor.h natives/src/engine/WorldAccessor.cpp
git commit -m "feat: implement WorldAccessor (hybrid buffer + JNI callback)"
```

---

## Task 3: MovementExpander

**Files:**
- Modify: `natives/src/engine/MovementExpander.h`
- Modify: `natives/src/engine/MovementExpander.cpp`

Generates A* neighbours from a given block position using the movement actions defined in the design.

- [ ] **Step 1: Implement `MovementExpander.h`**

```cpp
#pragma once
#include "Types.h"
#include "WorldAccessor.h"
#include <vector>

struct PathNode {
    Vec3i pos;
    ActionType action;
    float cost;
};

struct MovementCosts {
    float walk       = 1.0f;
    float sprint     = 0.8f;
    float jump       = 1.2f;
    float sprintJump = 0.5f; // added to distance
    float fall       = 0.9f; // per block
    float ladder     = 1.1f;
    float swim       = 2.0f;
    float aotv       = 5.0f;
    float etherwarp  = 4.0f;
    float lavaAdj    = 20.0f;
    float waterSrc   = 5.0f;
};

class MovementExpander {
public:
    explicit MovementExpander(const WorldAccessor& world,
                               const MovementCosts& costs = {});

    std::vector<PathNode> expand(const Vec3i& from) const;

private:
    const WorldAccessor& world_;
    MovementCosts costs_;

    void addWalkSprint(const Vec3i& from, std::vector<PathNode>& out) const;
    void addJumps(const Vec3i& from, std::vector<PathNode>& out) const;
    void addFall(const Vec3i& from, std::vector<PathNode>& out) const;
    void addLadder(const Vec3i& from, std::vector<PathNode>& out) const;
    void addSwim(const Vec3i& from, std::vector<PathNode>& out) const;
    void addAOTV(const Vec3i& from, std::vector<PathNode>& out) const;
    void addEtherwarp(const Vec3i& from, std::vector<PathNode>& out) const;

    float adjacentCost(const Vec3i& pos) const;
    bool clearanceOk(int x, int y, int z) const; // 2 blocks tall
};
```

- [ ] **Step 2: Implement `MovementExpander.cpp`**

```cpp
#include "MovementExpander.h"
#include <cmath>
#include <algorithm>

static const int DX4[] = {1,-1,0,0};
static const int DZ4[] = {0,0,1,-1};

MovementExpander::MovementExpander(const WorldAccessor& w, const MovementCosts& c)
    : world_(w), costs_(c) {}

float MovementExpander::adjacentCost(const Vec3i& pos) const {
    float c = 0;
    for (int dx=-1; dx<=1; dx++) for (int dz=-1; dz<=1; dz++) {
        if (world_.isLava(pos.x+dx, pos.y, pos.z+dz)) c += costs_.lavaAdj;
        if (world_.isWater(pos.x+dx, pos.y, pos.z+dz)) c += costs_.waterSrc;
    }
    return c;
}

bool MovementExpander::clearanceOk(int x, int y, int z) const {
    return world_.isPassable(x, y, z) && world_.isPassable(x, y+1, z);
}

void MovementExpander::addWalkSprint(const Vec3i& from,
                                      std::vector<PathNode>& out) const {
    for (int i = 0; i < 4; i++) {
        int nx = from.x + DX4[i], nz = from.z + DZ4[i];
        if (!world_.isWalkable(nx, from.y - 1, nz)) continue;
        if (!clearanceOk(nx, from.y, nz)) continue;
        bool lowCeiling = !world_.isPassable(nx, from.y + 2, nz);
        ActionType act = lowCeiling ? ActionType::WALK : ActionType::SPRINT;
        float base = lowCeiling ? costs_.walk : costs_.sprint;
        out.push_back({{nx, from.y, nz}, act, base + adjacentCost({nx, from.y, nz})});
    }
}

void MovementExpander::addJumps(const Vec3i& from,
                                 std::vector<PathNode>& out) const {
    // Step up 1 block
    for (int i = 0; i < 4; i++) {
        int nx = from.x + DX4[i], nz = from.z + DZ4[i];
        int ny = from.y + 1;
        if (!world_.isSolid(nx, ny - 1, nz)) continue;
        if (!clearanceOk(nx, ny, nz)) continue;
        out.push_back({{nx, ny, nz}, ActionType::JUMP, costs_.jump + adjacentCost({nx, ny, nz})});
    }
    // Sprint-jump gap (1–4 blocks)
    for (int i = 0; i < 4; i++) {
        for (int dist = 2; dist <= 4; dist++) {
            int nx = from.x + DX4[i] * dist, nz = from.z + DZ4[i] * dist;
            if (!world_.isWalkable(nx, from.y - 1, nz)) continue;
            if (!clearanceOk(nx, from.y, nz)) continue;
            // Ensure gap between
            bool gapClear = true;
            for (int d = 1; d < dist && gapClear; d++) {
                int mx = from.x + DX4[i]*d, mz = from.z + DZ4[i]*d;
                gapClear = world_.isPassable(mx, from.y, mz);
            }
            if (!gapClear) break;
            float c = (float)dist + costs_.sprintJump + adjacentCost({nx, from.y, nz});
            out.push_back({{nx, from.y, nz}, ActionType::SPRINT_JUMP, c});
        }
    }
}

void MovementExpander::addFall(const Vec3i& from,
                                std::vector<PathNode>& out) const {
    for (int i = 0; i < 4; i++) {
        int nx = from.x + DX4[i], nz = from.z + DZ4[i];
        if (!world_.isPassable(nx, from.y, nz)) continue;
        // Find where we land
        int ny = from.y - 1;
        while (ny > from.y - 24 && world_.isPassable(nx, ny, nz)) ny--;
        if (!world_.isSolid(nx, ny, nz)) continue;
        int landY = ny + 1;
        int drop = from.y - landY;
        if (drop <= 0 || drop > 23) continue;
        float c = drop * costs_.fall + adjacentCost({nx, landY, nz});
        out.push_back({{nx, landY, nz}, ActionType::FALL, c});
    }
}

void MovementExpander::addLadder(const Vec3i& from,
                                  std::vector<PathNode>& out) const {
    if (world_.isLadder(from.x, from.y, from.z)) {
        // Up
        if (world_.isLadder(from.x, from.y+1, from.z))
            out.push_back({{from.x, from.y+1, from.z}, ActionType::LADDER, costs_.ladder});
        // Down
        if (world_.isLadder(from.x, from.y-1, from.z))
            out.push_back({{from.x, from.y-1, from.z}, ActionType::LADDER, costs_.ladder});
    }
}

void MovementExpander::addSwim(const Vec3i& from,
                                std::vector<PathNode>& out) const {
    if (!world_.isWater(from.x, from.y, from.z)) return;
    for (int i = 0; i < 4; i++) {
        int nx = from.x + DX4[i], nz = from.z + DZ4[i];
        if (world_.isWater(nx, from.y, nz) || world_.isPassable(nx, from.y, nz))
            out.push_back({{nx, from.y, nz}, ActionType::WATER_SWIM, costs_.swim});
    }
    // Swim up
    if (world_.isWater(from.x, from.y+1, from.z))
        out.push_back({{from.x, from.y+1, from.z}, ActionType::WATER_SWIM, costs_.swim});
}

void MovementExpander::addAOTV(const Vec3i& from,
                                std::vector<PathNode>& out) const {
    // AOTV: up to 11 blocks in the 4 cardinal directions toward open air
    for (int i = 0; i < 4; i++) {
        for (int dist = 3; dist <= 11; dist++) {
            int nx = from.x + DX4[i]*dist, nz = from.z + DZ4[i]*dist;
            if (!world_.isPassable(nx, from.y, nz)) break;
            if (!world_.isWalkable(nx, from.y-1, nz)) continue;
            float c = costs_.aotv + dist * 0.05f;
            out.push_back({{nx, from.y, nz}, ActionType::AOTV, c});
        }
    }
}

void MovementExpander::addEtherwarp(const Vec3i& from,
                                     std::vector<PathNode>& out) const {
    // Etherwarp: teleport to a solid block within 51 blocks line-of-sight
    for (int i = 0; i < 4; i++) {
        for (int dist = 5; dist <= 51; dist++) {
            int nx = from.x + DX4[i]*dist, nz = from.z + DZ4[i]*dist;
            if (world_.isSolid(nx, from.y, nz)) {
                // land on top of this solid block
                if (clearanceOk(nx, from.y+1, nz))
                    out.push_back({{nx, from.y+1, nz}, ActionType::ETHERWARP, costs_.etherwarp});
                break;
            }
        }
    }
}

std::vector<PathNode> MovementExpander::expand(const Vec3i& from) const {
    std::vector<PathNode> out;
    out.reserve(32);
    addWalkSprint(from, out);
    addJumps(from, out);
    addFall(from, out);
    addLadder(from, out);
    addSwim(from, out);
    addAOTV(from, out);
    addEtherwarp(from, out);
    return out;
}
```

- [ ] **Step 3: Build**
```bash
cmake --build natives/build --config Release
```

- [ ] **Step 4: Commit**
```bash
git add natives/src/engine/MovementExpander.h natives/src/engine/MovementExpander.cpp
git commit -m "feat: implement MovementExpander (all action types)"
```

---

## Task 4: AStarPlanner

**Files:**
- Modify: `natives/src/engine/AStarPlanner.h`
- Modify: `natives/src/engine/AStarPlanner.cpp`

Runs A* on a background `std::thread`. Thread-safe result handoff via `std::atomic` + `std::mutex`.

- [ ] **Step 1: Implement `AStarPlanner.h`**

```cpp
#pragma once
#include "Types.h"
#include "MovementExpander.h"
#include "WorldAccessor.h"
#include <vector>
#include <thread>
#include <mutex>
#include <atomic>

struct AStarResult {
    bool found = false;
    std::vector<Vec3i> nodes;
};

class AStarPlanner {
public:
    ~AStarPlanner();

    // Snapshots the world buffer into the thread closure — no shared reference to WorldAccessor.
    // Safe to call while WorldAccessor is being updated on the main thread.
    void startAsync(Vec3i start, Vec3i goal,
                    const WorldAccessor& world,   // snapshot taken immediately, not stored
                    MovementProfile profile);

    bool isComplete() const { return complete_.load(); }
    bool isRunning()  const { return running_.load(); }
    AStarResult takeResult();  // call once after isComplete()
    void cancel();

private:
    std::thread thread_;
    std::mutex  mutex_;
    std::atomic<bool> complete_{false};
    std::atomic<bool> running_{false};
    std::atomic<bool> cancelled_{false};
    AStarResult result_;

    static float heuristic(const Vec3i& a, const Vec3i& b);
    static MovementCosts costsForProfile(MovementProfile p);
};
```

- [ ] **Step 2: Implement `AStarPlanner.cpp`**

```cpp
#include "AStarPlanner.h"
#include <unordered_map>
#include <queue>
#include <cmath>

AStarPlanner::~AStarPlanner() { cancel(); if (thread_.joinable()) thread_.join(); }

void AStarPlanner::cancel() {
    cancelled_.store(true);
    if (thread_.joinable()) thread_.join();
    cancelled_.store(false);
    running_.store(false);
}

MovementCosts AStarPlanner::costsForProfile(MovementProfile p) {
    MovementCosts c;
    switch (p) {
        case MovementProfile::MINING:
            c.aotv = 20.0f; c.etherwarp = 15.0f; break;
        case MovementProfile::COMBAT:
            c.aotv = 2.0f; c.etherwarp = 1.5f;
            c.sprint = 0.5f; break;
        case MovementProfile::GROUND_ONLY:
            c.aotv = 9999.f; c.etherwarp = 9999.f;
            c.jump = 9999.f; c.sprintJump = 9999.f; break;
        default: break;
    }
    return c;
}

float AStarPlanner::heuristic(const Vec3i& a, const Vec3i& b) {
    float dx = (float)(a.x - b.x), dy = (float)(a.y - b.y), dz = (float)(a.z - b.z);
    return std::sqrt(dx*dx + dy*dy + dz*dz);
}

struct PQNode {
    float f; Vec3i pos;
    bool operator>(const PQNode& o) const { return f > o.f; }
};

struct Vec3iHash {
    size_t operator()(const Vec3i& v) const {
        size_t h = (size_t)(v.x * 1000003) ^ (size_t)(v.y * 999983) ^ (size_t)(v.z * 999979);
        return h;
    }
};
struct Vec3iEq {
    bool operator()(const Vec3i& a, const Vec3i& b) const {
        return a.x==b.x && a.y==b.y && a.z==b.z;
    }
};

void AStarPlanner::startAsync(Vec3i start, Vec3i goal,
                               const WorldAccessor& world,
                               MovementProfile profile) {
    cancel();
    complete_.store(false);
    running_.store(true);
    cancelled_.store(false);

    MovementCosts costs = costsForProfile(profile);

    // Snapshot the entire buffer into the thread closure to avoid data races.
    // WorldAccessor on the main thread may be updated (setBuffer) while this thread runs.
    std::vector<uint8_t> bufSnapshot(world.bufferData(),
                                      world.bufferData() + BUF_SIZE);
    int snapBx = world.originX(), snapBy = world.originY(), snapBz = world.originZ();

    thread_ = std::thread([this, start, goal, costs,
                           bufSnapshot = std::move(bufSnapshot),
                           snapBx, snapBy, snapBz]() mutable {
        // Thread-local WorldAccessor — no shared state with main thread
        WorldAccessor localWorld;
        localWorld.setBuffer(bufSnapshot.data(), snapBx, snapBy, snapBz);
        MovementExpander expander(localWorld, costs);

        using Map = std::unordered_map<Vec3i, float,    Vec3iHash, Vec3iEq>;
        using PMap= std::unordered_map<Vec3i, Vec3i,    Vec3iHash, Vec3iEq>;
        using PQ  = std::priority_queue<PQNode, std::vector<PQNode>, std::greater<PQNode>>;

        Map gScore; PMap parent; PQ open;
        gScore[start] = 0;
        open.push({heuristic(start, goal), start});

        AStarResult res;
        int iters = 0;
        const int MAX_ITER = 50000;

        while (!open.empty() && !cancelled_.load() && iters++ < MAX_ITER) {
            auto [f, cur] = open.top(); open.pop();

            if (cur.x==goal.x && cur.y==goal.y && cur.z==goal.z) {
                // Reconstruct path
                std::vector<Vec3i> path;
                Vec3i c = cur;
                while (!(c.x==start.x && c.y==start.y && c.z==start.z)) {
                    path.push_back(c);
                    c = parent[c];
                }
                path.push_back(start);
                std::reverse(path.begin(), path.end());
                res.found = true;
                res.nodes = std::move(path);
                break;
            }

            float gCur = gScore.count(cur) ? gScore[cur] : 1e9f;
            for (auto& nb : expander.expand(cur)) {
                float ng = gCur + nb.cost;
                if (!gScore.count(nb.pos) || ng < gScore[nb.pos]) {
                    gScore[nb.pos] = ng;
                    parent[nb.pos] = cur;
                    open.push({ng + heuristic(nb.pos, goal), nb.pos});
                }
            }
        }

        {
            std::lock_guard<std::mutex> lk(mutex_);
            result_ = std::move(res);
        }
        running_.store(false);
        complete_.store(true);
    });
}

AStarResult AStarPlanner::takeResult() {
    std::lock_guard<std::mutex> lk(mutex_);
    complete_.store(false);
    return std::move(result_);
}
```

- [ ] **Step 3: Build**
```bash
cmake --build natives/build --config Release
```

- [ ] **Step 4: Commit**
```bash
git add natives/src/engine/AStarPlanner.h natives/src/engine/AStarPlanner.cpp
git commit -m "feat: implement async AStarPlanner"
```

---

## Task 5: StuckDetector

**Files:**
- Modify: `natives/src/engine/StuckDetector.h`
- Modify: `natives/src/engine/StuckDetector.cpp`

Tracks player position history over a rolling window. If total movement is below threshold for `stuckTickLimit` ticks, triggers recovery.

- [ ] **Step 1: Implement `StuckDetector.h`**

```cpp
#pragma once
#include "Types.h"
#include <deque>

class StuckDetector {
public:
    StuckDetector(int stuckTickLimit = 40, double movementEps = 0.05);

    void update(double px, double py, double pz);
    bool isStuck() const;
    void reset();

private:
    int stuckTickLimit_;
    double movementEps_;
    std::deque<Vec3d> history_;
    bool stuck_ = false;
};
```

- [ ] **Step 2: Implement `StuckDetector.cpp`**

```cpp
#include "StuckDetector.h"
#include <cmath>

StuckDetector::StuckDetector(int limit, double eps)
    : stuckTickLimit_(limit), movementEps_(eps) {}

void StuckDetector::update(double px, double py, double pz) {
    history_.push_back({px, py, pz});
    if ((int)history_.size() > stuckTickLimit_) history_.pop_front();

    if ((int)history_.size() < stuckTickLimit_) { stuck_ = false; return; }

    const auto& oldest = history_.front();
    double dx = px - oldest.x, dz = pz - oldest.z;
    double moved = std::sqrt(dx*dx + dz*dz);
    // isStuck triggers as soon as the full window shows no movement
    stuck_ = (moved < movementEps_);
}

bool StuckDetector::isStuck() const { return stuck_; }
void StuckDetector::reset() { history_.clear(); stuck_ = false; }
```

- [ ] **Step 3: Build + Commit**
```bash
cmake --build natives/build --config Release
git add natives/src/engine/StuckDetector.h natives/src/engine/StuckDetector.cpp
git commit -m "feat: implement StuckDetector"
```

---

## Task 6: RotationController

**Files:**
- Modify: `natives/src/engine/RotationController.h`
- Modify: `natives/src/engine/RotationController.cpp`

Generates human-like yaw/pitch each tick via bezier easing, GCD correction, and micro-noise.

- [ ] **Step 1: Implement `RotationController.h`**

```cpp
#pragma once
#include "Types.h"
#include <vector>

class RotationController {
public:
    RotationController();

    // Feed the upcoming path nodes so controller can look ahead
    void setPath(const std::vector<Vec3i>& nodes, int currentIdx);
    void setCombatMode(bool combat);

    // Returns yaw/pitch to apply this tick; currentYaw/Pitch = what player currently has
    void tick(double px, double py, double pz,
              float currentYaw, float currentPitch,
              float& outYaw, float& outPitch);

    void reset();

private:
    std::vector<Vec3i> path_;
    int currentIdx_ = 0;
    bool combatMode_ = false;

    float targetYaw_   = 0;
    float targetPitch_ = 0;
    float smoothYaw_   = 0;
    float smoothPitch_ = 0;
    int   noiseTick_   = 0;
    int   noiseInterval_ = 12;

    float gcdCorrect(float delta) const;
    float angleDiff(float a, float b) const;
    float lookAheadYaw(double px, double py, double pz) const;
    float lookAheadPitch(double px, double py, double pz) const;
    float noise(float scale);

    int noiseSeed_ = 1337;
    float pseudoRand();
};
```

- [ ] **Step 2: Implement `RotationController.cpp`**

```cpp
#include "RotationController.h"
#include <cmath>
#include <algorithm>

static constexpr float PI = 3.14159265f;
static constexpr float GCD_BASE = 0.15f; // approximate mouse sensitivity unit

RotationController::RotationController() {}

void RotationController::setPath(const std::vector<Vec3i>& nodes, int idx) {
    path_ = nodes; currentIdx_ = idx;
}
void RotationController::setCombatMode(bool c) { combatMode_ = c; }
void RotationController::reset() {
    path_.clear(); currentIdx_ = 0; smoothYaw_ = 0; smoothPitch_ = 0;
}

float RotationController::pseudoRand() {
    noiseSeed_ = noiseSeed_ * 1664525 + 1013904223;
    return ((noiseSeed_ & 0x7fff) / (float)0x7fff) * 2.0f - 1.0f;
}

float RotationController::noise(float scale) {
    return pseudoRand() * scale;
}

float RotationController::angleDiff(float a, float b) const {
    float d = a - b;
    while (d >  180) d -= 360;
    while (d < -180) d += 360;
    return d;
}

// No GCD in C++ — RotationExecutor (Kotlin) handles GCD once on output.

float RotationController::lookAheadYaw(double px, double py, double pz) const {
    // Look toward node currentIdx+2 for smoother turns
    int ahead = std::min(currentIdx_ + 2, (int)path_.size() - 1);
    if (ahead < 0 || path_.empty()) return smoothYaw_;
    const auto& t = path_[ahead];
    double dx = t.x + 0.5 - px, dz = t.z + 0.5 - pz;
    float yaw = (float)(std::atan2(-dx, dz) * 180.0 / PI);
    return yaw;
}

float RotationController::lookAheadPitch(double px, double py, double pz) const {
    int ahead = std::min(currentIdx_ + 2, (int)path_.size() - 1);
    if (ahead < 0 || path_.empty()) return 0;
    const auto& t = path_[ahead];
    double dx = t.x + 0.5 - px, dz = t.z + 0.5 - pz;
    double horiz = std::sqrt(dx*dx + dz*dz);
    double dy = (t.y + 0.5) - (py + 1.62); // eye height
    float pitch = (float)(-std::atan2(dy, horiz) * 180.0 / PI);
    return std::max(-45.0f, std::min(45.0f, pitch));
}

void RotationController::tick(double px, double py, double pz,
                               float curYaw, float curPitch,
                               float& outYaw, float& outPitch) {
    if (path_.empty()) { outYaw = curYaw; outPitch = curPitch; return; }

    targetYaw_   = lookAheadYaw(px, py, pz);
    targetPitch_ = lookAheadPitch(px, py, pz);

    // Bezier-like easing (fast start, ease finish)
    float speed = combatMode_ ? 0.35f : 0.18f;
    float yawDiff   = angleDiff(targetYaw_,   smoothYaw_);
    float pitchDiff = targetPitch_ - smoothPitch_;

    smoothYaw_   += yawDiff   * speed;
    smoothPitch_ += pitchDiff * speed;

    // Micro-noise every noiseInterval_ ticks
    if (++noiseTick_ >= noiseInterval_) {
        noiseTick_ = 0;
        noiseInterval_ = 8 + (int)(pseudoRand() * 3.5f + 3.5f); // 8-15
        smoothYaw_   += noise(0.15f);
        smoothPitch_ += noise(0.08f);
    }

    outYaw   = smoothYaw_;
    outPitch = std::max(-45.0f, std::min(45.0f, smoothPitch_));
}
```

- [ ] **Step 3: Build + Commit**
```bash
cmake --build natives/build --config Release
git add natives/src/engine/RotationController.h natives/src/engine/RotationController.cpp
git commit -m "feat: implement RotationController (bezier + GCD + micro-noise)"
```

---

## Task 7: PathExecutor (State Machine)

**Files:**
- Modify: `natives/src/engine/PathExecutor.h`
- Modify: `natives/src/engine/PathExecutor.cpp`

Owns the IDLE → PLANNING → EXECUTING → RECOVERING/REPLANNING → ARRIVED state machine. Called once per tick by `PathfinderEngine`.

- [ ] **Step 1: Implement `PathExecutor.h`**

```cpp
#pragma once
#include "Types.h"
#include "AStarPlanner.h"
#include "StuckDetector.h"
#include "RotationController.h"
#include "WorldAccessor.h"
#include <vector>

class PathExecutor {
public:
    PathExecutor();

    void setRoute(const std::vector<Vec3d>& waypoints, bool loop, MovementProfile profile);
    void setTarget(Vec3d target);
    void stop();

    PathCommand tick(const WorldAccessor& world,
                     double px, double py, double pz,
                     float yaw, float pitch, bool onGround);

    PathStatus getStatus() const { return status_; }

private:
    PathStatus status_ = PathStatus::IDLE;
    MovementProfile profile_ = MovementProfile::DEFAULT;

    std::vector<Vec3d> waypoints_;
    int waypointIdx_ = 0;
    bool loop_ = false;

    std::vector<Vec3i> activePath_;
    int pathNodeIdx_ = 0;

    AStarPlanner planner_;
    StuckDetector stuck_;
    RotationController rotation_;

    int recoverTicks_ = 0;
    static constexpr int RECOVER_TIMEOUT = 60;
    static constexpr double ARRIVE_DIST  = 1.8;
    static constexpr double NODE_DIST    = 0.6;

    void startPlan(const WorldAccessor& world, double px, double py, double pz);
    PathCommand buildCommand(double px, double py, double pz,
                              float yaw, float pitch);
    PathCommand idle() { return {false,false,false,false,false,0,0,PathStatus::IDLE,ActionType::WALK}; }
};
```

- [ ] **Step 2: Implement `PathExecutor.cpp`**

```cpp
#include "PathExecutor.h"
#include <cmath>

PathExecutor::PathExecutor() {}

void PathExecutor::setRoute(const std::vector<Vec3d>& wps, bool loop, MovementProfile p) {
    waypoints_ = wps; loop_ = loop; profile_ = p;
    waypointIdx_ = 0; activePath_.clear(); pathNodeIdx_ = 0;
    planner_.cancel(); stuck_.reset(); rotation_.reset();
    status_ = PathStatus::PLANNING;
}

void PathExecutor::setTarget(Vec3d t) {
    setRoute({t}, false, profile_);
}

void PathExecutor::stop() {
    planner_.cancel(); status_ = PathStatus::IDLE;
    activePath_.clear(); waypoints_.clear();
}

void PathExecutor::startPlan(const WorldAccessor& world, double px, double py, double pz) {
    if (waypointIdx_ >= (int)waypoints_.size()) { status_ = PathStatus::ARRIVED; return; }
    Vec3d goal = waypoints_[waypointIdx_];
    Vec3i startI{(int)px, (int)py, (int)pz};
    Vec3i goalI {(int)goal.x, (int)goal.y, (int)goal.z};
    planner_.startAsync(startI, goalI, world, profile_);
    status_ = PathStatus::PLANNING;
}

PathCommand PathExecutor::buildCommand(double px, double py, double pz,
                                        float yaw, float pitch) {
    if (pathNodeIdx_ >= (int)activePath_.size()) {
        waypointIdx_++;
        if (loop_ && waypointIdx_ >= (int)waypoints_.size()) waypointIdx_ = 0;
        if (waypointIdx_ >= (int)waypoints_.size()) {
            status_ = PathStatus::ARRIVED;
            return {false,false,false,false,false,yaw,pitch,PathStatus::ARRIVED,ActionType::WALK};
        }
        status_ = PathStatus::REPLANNING;
        return {false,false,false,false,false,yaw,pitch,PathStatus::REPLANNING,ActionType::WALK};
    }

    Vec3i target = activePath_[pathNodeIdx_];
    double dx = (target.x + 0.5) - px;
    double dz = (target.z + 0.5) - pz;
    double dy = target.y - py;
    double dist = std::sqrt(dx*dx + dz*dz);

    if (dist < NODE_DIST && std::abs(dy) < 1.5) {
        pathNodeIdx_++;
        rotation_.setPath(activePath_, pathNodeIdx_);
    }

    bool jump   = dy > 0.5;
    bool sprint = dist > 1.5;
    bool forward = true;

    float outYaw, outPitch;
    rotation_.tick(px, py, pz, yaw, pitch, outYaw, outPitch);

    return {forward, false, jump, false, sprint,
            outYaw, outPitch, PathStatus::EXECUTING, ActionType::SPRINT};
}

PathCommand PathExecutor::tick(const WorldAccessor& world,
                                double px, double py, double pz,
                                float yaw, float pitch, bool onGround) {
    stuck_.update(px, py, pz);

    switch (status_) {
    case PathStatus::IDLE:
        return idle();

    case PathStatus::PLANNING:
    case PathStatus::REPLANNING:
        if (planner_.isComplete()) {
            auto res = planner_.takeResult();
            if (!res.found) { status_ = PathStatus::FAILED; return idle(); }
            activePath_ = std::move(res.nodes);
            pathNodeIdx_ = 0;
            rotation_.setPath(activePath_, 0);
            stuck_.reset();
            status_ = PathStatus::EXECUTING;
        }
        // Keep moving on old path while replanning
        if (!activePath_.empty() && status_ == PathStatus::REPLANNING)
            return buildCommand(px, py, pz, yaw, pitch);
        return {false,false,false,false,false,yaw,pitch,status_,ActionType::WALK};

    case PathStatus::EXECUTING:
        if (stuck_.isStuck()) {
            recoverTicks_ = 0;
            status_ = PathStatus::RECOVERING;
        }
        return buildCommand(px, py, pz, yaw, pitch);

    case PathStatus::RECOVERING:
        recoverTicks_++;
        if (recoverTicks_ > RECOVER_TIMEOUT) {
            startPlan(world, px, py, pz);
        } else if (!stuck_.isStuck()) {
            status_ = PathStatus::EXECUTING;
        }
        // Back up
        return {false, true, false, false, false, yaw, pitch, PathStatus::RECOVERING, ActionType::WALK};

    case PathStatus::ARRIVED:
    case PathStatus::FAILED:
        return {false,false,false,false,false,yaw,pitch,status_,ActionType::WALK};
    }
    return idle();
}
```

- [ ] **Step 3: Build + Commit**
```bash
cmake --build natives/build --config Release
git add natives/src/engine/PathExecutor.h natives/src/engine/PathExecutor.cpp
git commit -m "feat: implement PathExecutor state machine"
```

---

## Task 8: PathfinderEngine + JNI Entry Points

**Files:**
- Modify: `natives/src/engine/PathfinderEngine.h`
- Modify: `natives/src/engine/PathfinderEngine.cpp`
- Modify: `natives/src/pathfinder_jni.cpp`

PathfinderEngine is the top-level object. One instance per handle. JNI entry points wrap it.

- [ ] **Step 1: Implement `PathfinderEngine.h`**

```cpp
#pragma once
#include "Types.h"
#include "WorldAccessor.h"
#include "PathExecutor.h"
#include <jni.h>

class PathfinderEngine {
public:
    PathfinderEngine();

    PathCommand update(const uint8_t* worldBuf,
                       int bx, int by, int bz,
                       double px, double py, double pz,
                       float yaw, float pitch, bool onGround);

    void setRoute(const double* waypointData, int count, bool loop, int profile);
    void setTarget(double x, double y, double z);
    void stop();
    PathStatus getStatus() const;

private:
    WorldAccessor world_;
    PathExecutor executor_;
};
```

- [ ] **Step 2: Implement `PathfinderEngine.cpp`**

```cpp
#include "PathfinderEngine.h"

PathfinderEngine::PathfinderEngine() {}

PathCommand PathfinderEngine::update(const uint8_t* buf,
                                      int bx, int by, int bz,
                                      double px, double py, double pz,
                                      float yaw, float pitch, bool onGround) {
    world_.setBuffer(buf, bx, by, bz);
    return executor_.tick(world_, px, py, pz, yaw, pitch, onGround);
}

void PathfinderEngine::setRoute(const double* data, int count, bool loop, int profile) {
    std::vector<Vec3d> wps;
    wps.reserve(count);
    for (int i = 0; i < count*3; i += 3)
        wps.push_back({data[i], data[i+1], data[i+2]});
    executor_.setRoute(wps, loop, (MovementProfile)profile);
}

void PathfinderEngine::setTarget(double x, double y, double z) {
    executor_.setTarget({x, y, z});
}

void PathfinderEngine::stop() { executor_.stop(); }
PathStatus PathfinderEngine::getStatus() const { return executor_.getStatus(); }
```

- [ ] **Step 3: Implement `pathfinder_jni.cpp`**

JNI function naming: `Java_org_cobalt_pathfinder_NativePathfinderBridge_<method>`.

```cpp
#include <jni.h>
#include "engine/PathfinderEngine.h"
#include <cstring>
#include <bit>

extern "C" {

JNIEXPORT jlong JNICALL
Java_org_cobalt_pathfinder_NativePathfinderBridge_createEngine(JNIEnv*, jclass) {
    return (jlong)(new PathfinderEngine());
}

JNIEXPORT void JNICALL
Java_org_cobalt_pathfinder_NativePathfinderBridge_destroyEngine(JNIEnv*, jclass, jlong handle) {
    delete (PathfinderEngine*)handle;
}

JNIEXPORT void JNICALL
Java_org_cobalt_pathfinder_NativePathfinderBridge_setRoute(JNIEnv* env, jclass,
        jlong handle, jdoubleArray waypoints, jboolean loop, jint profile) {
    jsize len = env->GetArrayLength(waypoints);
    jdouble* data = env->GetDoubleArrayElements(waypoints, nullptr);
    ((PathfinderEngine*)handle)->setRoute(data, len/3, loop, profile);
    env->ReleaseDoubleArrayElements(waypoints, data, JNI_ABORT);
}

JNIEXPORT void JNICALL
Java_org_cobalt_pathfinder_NativePathfinderBridge_setTarget(JNIEnv*, jclass,
        jlong handle, jdouble x, jdouble y, jdouble z) {
    ((PathfinderEngine*)handle)->setTarget(x, y, z);
}

JNIEXPORT jintArray JNICALL
Java_org_cobalt_pathfinder_NativePathfinderBridge_update(JNIEnv* env, jclass,
        jlong handle, jbyteArray worldBuf, jint bx, jint by, jint bz,
        jdouble px, jdouble py, jdouble pz,
        jfloat yaw, jfloat pitch, jboolean onGround) {

    jbyte* buf = env->GetByteArrayElements(worldBuf, nullptr);

    PathCommand cmd = ((PathfinderEngine*)handle)->update(
        (const uint8_t*)buf, bx, by, bz,
        px, py, pz, yaw, pitch, onGround);

    env->ReleaseByteArrayElements(worldBuf, buf, JNI_ABORT);

    jintArray arr = env->NewIntArray(10);
    jint out[10] = {
        cmd.forward?1:0, cmd.back?1:0, cmd.jump?1:0,
        cmd.sneak?1:0,   cmd.sprint?1:0,
        std::bit_cast<int>(cmd.targetYaw),
        std::bit_cast<int>(cmd.targetPitch),
        (int)cmd.status, (int)cmd.activeAction,
        std::bit_cast<int>(cmd.distanceToTarget)
    };
    env->SetIntArrayRegion(arr, 0, 10, out);
    return arr;
}

JNIEXPORT void JNICALL
Java_org_cobalt_pathfinder_NativePathfinderBridge_stop(JNIEnv*, jclass, jlong handle) {
    ((PathfinderEngine*)handle)->stop();
}

JNIEXPORT jint JNICALL
Java_org_cobalt_pathfinder_NativePathfinderBridge_getStatus(JNIEnv*, jclass, jlong handle) {
    return (jint)((PathfinderEngine*)handle)->getStatus();
}

} // extern "C"
```

- [ ] **Step 4: Build full DLL**
```bash
cmake --build natives/build --config Release
```
Expected: `cobalt_pathfinder.dll` in `natives/build/Release/` with no linker errors.

- [ ] **Step 5: Commit**
```bash
git add natives/src/engine/PathfinderEngine.h natives/src/engine/PathfinderEngine.cpp natives/src/pathfinder_jni.cpp
git commit -m "feat: implement PathfinderEngine and JNI entry points"
```

---

## Task 9: Java Bridge + NativeLoader

**Files:**
- Create: `src/main/java/org/cobalt/pathfinder/NativeLoader.java`
- Create: `src/main/java/org/cobalt/pathfinder/NativePathfinderBridge.java`

- [ ] **Step 1: Create `NativeLoader.java`**

```java
package org.cobalt.pathfinder;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;

public class NativeLoader {
    public static String extract(String resourcePath) throws IOException {
        Path tempDir = Path.of(System.getProperty("java.io.tmpdir"), "cobalt");
        Files.createDirectories(tempDir);
        String fname = Path.of(resourcePath).getFileName().toString();
        Path dest    = tempDir.resolve(fname);
        Path lock    = tempDir.resolve(fname + ".lock");

        // Exclusive lock guards against two game instances extracting simultaneously
        try (FileChannel fc = FileChannel.open(lock,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             FileLock fl = fc.lock()) {

            try (InputStream in = NativeLoader.class.getResourceAsStream("/" + resourcePath)) {
                if (in == null) throw new IOException("Native resource not found: " + resourcePath);
                byte[] bytes = in.readAllBytes();
                // CRC32 comparison — size alone is insufficient across recompilations of same-size DLLs
                if (Files.exists(dest) && crc32(Files.readAllBytes(dest)) == crc32(bytes)) {
                    return dest.toAbsolutePath().toString();
                }
                Files.write(dest, bytes,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        }
        return dest.toAbsolutePath().toString();
    }

    private static long crc32(byte[] data) {
        java.util.zip.CRC32 crc = new java.util.zip.CRC32();
        crc.update(data);
        return crc.getValue();
    }
}
```

- [ ] **Step 2: Create `NativePathfinderBridge.java`**

```java
package org.cobalt.pathfinder;

public class NativePathfinderBridge {
    static {
        try {
            String path = NativeLoader.extract("natives/windows/cobalt_pathfinder.dll");
            System.load(path);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load cobalt_pathfinder.dll", e);
        }
    }

    public static native long    createEngine();
    public static native void    destroyEngine(long handle);
    public static native void    setRoute(long handle, double[] waypoints, boolean loop, int profile);
    public static native void    setTarget(long handle, double x, double y, double z);
    public static native int[]   update(long handle, byte[] worldBuffer,
                                         int bx, int by, int bz,
                                         double px, double py, double pz,
                                         float yaw, float pitch, boolean onGround);
    public static native void    stop(long handle);
    public static native int     getStatus(long handle);
}
```

- [ ] **Step 3: Commit**
```bash
git add src/main/java/org/cobalt/pathfinder/
git commit -m "feat: add NativePathfinderBridge and NativeLoader (Java JNI bridge)"
```

---

## Task 10: Kotlin API

**Files:**
- Create: `src/main/kotlin/org/cobalt/api/pathfinder/jni/PathStatus.kt`
- Create: `src/main/kotlin/org/cobalt/api/pathfinder/jni/ActionType.kt`
- Create: `src/main/kotlin/org/cobalt/api/pathfinder/jni/MovementProfile.kt`
- Create: `src/main/kotlin/org/cobalt/api/pathfinder/jni/PathCommand.kt`
- Create: `src/main/kotlin/org/cobalt/api/pathfinder/jni/WorldBufferSerializer.kt`
- Create: `src/main/kotlin/org/cobalt/api/pathfinder/jni/NativePathfinder.kt`

- [ ] **Step 1: Create enum files**

`PathStatus.kt`:
```kotlin
package org.cobalt.api.pathfinder.jni
enum class PathStatus { IDLE, PLANNING, EXECUTING, RECOVERING, REPLANNING, ARRIVED, FAILED }
```

`ActionType.kt`:
```kotlin
package org.cobalt.api.pathfinder.jni
enum class ActionType { WALK, SPRINT, JUMP, SPRINT_JUMP, FALL, LADDER, WATER_SWIM, AOTV, ETHERWARP }
```

`MovementProfile.kt`:
```kotlin
package org.cobalt.api.pathfinder.jni
enum class MovementProfile { DEFAULT, MINING, COMBAT, GROUND_ONLY }
```

`PathfinderRotationStrategy.kt` — pass-through: C++ already smoothed the angle, just let RotationExecutor apply GCD:
```kotlin
package org.cobalt.api.pathfinder.jni

import net.minecraft.client.player.LocalPlayer
import org.cobalt.api.rotation.IRotationStrategy
import org.cobalt.api.util.helper.Rotation

object PathfinderRotationStrategy : IRotationStrategy {
    override fun onRotate(player: LocalPlayer, targetYaw: Float, targetPitch: Float): Rotation =
        Rotation(targetYaw, targetPitch)  // return exact target — RotationExecutor applies GCD once
}
```

- [ ] **Step 2: Create `PathCommand.kt`**

```kotlin
package org.cobalt.api.pathfinder.jni

import net.minecraft.client.Minecraft
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.util.helper.Rotation
import org.cobalt.api.util.player.MovementManager

data class PathCommand(
    val forward: Boolean,
    val back: Boolean,
    val jump: Boolean,
    val sneak: Boolean,
    val sprint: Boolean,
    val targetYaw: Float,          // raw — RotationExecutor applies GCD
    val targetPitch: Float,        // raw
    val status: PathStatus,
    val activeAction: ActionType,
    val distanceToTarget: Float,   // horizontal dist to current waypoint — use for interaction range checks
) {
    fun applyToPlayer() {
        // Movement: use setMovementLock + setForcedMovement so Mixin gates work correctly
        MovementManager.setMovementLock(true)
        MovementManager.setForcedMovement(
            forward, back,
            left = false, right = false,
            jump, sneak, sprint
        )
        // Rotation: feed raw angles to RotationExecutor — it applies GCD once via PathfinderRotationStrategy
        RotationExecutor.rotateTo(Rotation(targetYaw, targetPitch), PathfinderRotationStrategy)
    }
}
```

- [ ] **Step 3: Create `WorldBufferSerializer.kt`**

Serializes a 64×32×64 region of MC blocks centered on the player.

```kotlin
package org.cobalt.api.pathfinder.jni

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.tags.FluidTags
import net.minecraft.world.level.block.LadderBlock
import net.minecraft.world.level.block.LiquidBlock

data class WorldBufferResult(val buf: ByteArray, val bx: Int, val by: Int, val bz: Int)

object WorldBufferSerializer {
    private const val W = 64; private const val H = 32; private const val D = 64

    fun serialize(px: Double, py: Double, pz: Double): WorldBufferResult {
        val level = Minecraft.getInstance().level
        val buf = ByteArray(W * H * D)
        val bx = px.toInt() - W / 2
        val by = py.toInt() - H / 2
        val bz = pz.toInt() - D / 2

        if (level != null) {
            val pos = BlockPos.MutableBlockPos()
            for (x in 0 until W) for (y in 0 until H) for (z in 0 until D) {
                pos.set(bx + x, by + y, bz + z)
                val state = level.getBlockState(pos)
                val bt: Byte = when {
                    state.isAir -> 0
                    state.block is LadderBlock -> 4
                    state.fluidState.`is`(FluidTags.LAVA) -> 3
                    state.fluidState.`is`(FluidTags.WATER) -> 2
                    state.isSolid -> 1
                    else -> 0
                }
                buf[x + z * W + y * W * D] = bt
            }
        }
        return WorldBufferResult(buf, bx, by, bz)
    }
}
```

- [ ] **Step 4: Create `NativePathfinder.kt`**

```kotlin
package org.cobalt.api.pathfinder.jni

import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3
import org.cobalt.pathfinder.NativePathfinderBridge
import java.lang.Float.floatToRawIntBits

object NativePathfinder {
    private val handle: Long = NativePathfinderBridge.createEngine()

    fun setRoute(
        waypoints: List<Vec3>,
        loop: Boolean = false,
        profile: MovementProfile = MovementProfile.DEFAULT,
    ) {
        val data = DoubleArray(waypoints.size * 3)
        waypoints.forEachIndexed { i, v -> data[i*3]=v.x; data[i*3+1]=v.y; data[i*3+2]=v.z }
        NativePathfinderBridge.setRoute(handle, data, loop, profile.ordinal)
    }

    fun setTarget(pos: Vec3, profile: MovementProfile = MovementProfile.DEFAULT) {
        NativePathfinderBridge.setRoute(handle, doubleArrayOf(pos.x, pos.y, pos.z), false, profile.ordinal)
    }

    fun stop() = NativePathfinderBridge.stop(handle)

    val status: PathStatus get() = PathStatus.entries[NativePathfinderBridge.getStatus(handle)]

    /** Call once per TickEvent.Start. Returns null when IDLE. */
    fun tick(): PathCommand? {
        val player = Minecraft.getInstance().player ?: return null
        val result = WorldBufferSerializer.serialize(player.x, player.y, player.z)
        val (buf, bx, by, bz) = result
        val r = NativePathfinderBridge.update(
            handle, buf, bx, by, bz,
            player.x, player.y, player.z,
            player.yRot, player.xRot, player.onGround()
        )
        val st = PathStatus.entries[r[7]]
        if (st == PathStatus.IDLE) return null
        return PathCommand(
            forward = r[0] != 0, back = r[1] != 0, jump = r[2] != 0,
            sneak   = r[3] != 0, sprint = r[4] != 0,
            targetYaw        = java.lang.Float.intBitsToFloat(r[5]),
            targetPitch      = java.lang.Float.intBitsToFloat(r[6]),
            status           = st,
            activeAction     = ActionType.entries[r[8]],
            distanceToTarget = java.lang.Float.intBitsToFloat(r[9])
        )
    }
}
```

- [ ] **Step 5: Commit**
```bash
git add src/main/kotlin/org/cobalt/api/pathfinder/jni/
git commit -m "feat: add NativePathfinder Kotlin API and WorldBufferSerializer"
```

---

## Task 11: Gradle Integration + DLL Embedding

**Files:**
- Modify: `build.gradle.kts`
- Create: `src/main/resources/natives/windows/.gitkeep`

- [ ] **Step 1: Add `buildNative` task to `build.gradle.kts`**

Add before the closing `tasks {` block:

```kotlin
tasks.register<Exec>("buildNative") {
    group = "build"
    description = "Builds cobalt_pathfinder.dll and copies it into resources"
    workingDir(project.projectDir.resolve("natives"))
    commandLine("cmake", "--build", "build", "--config", "Release")
    doLast {
        copy {
            from(project.projectDir.resolve("natives/build/Release/cobalt_pathfinder.dll"))
            into(project.projectDir.resolve("src/main/resources/natives/windows/"))
        }
    }
}

tasks.named("processResources") {
    dependsOn("buildNative")
}
```

- [ ] **Step 2: Create placeholder so the directory is tracked by git**
```bash
mkdir -p src/main/resources/natives/windows
touch src/main/resources/natives/windows/.gitkeep
```

- [ ] **Step 3: Run full build to confirm DLL is embedded in JAR**
```bash
./gradlew build
jar tf build/libs/*.jar | grep cobalt_pathfinder
```
Expected: `natives/windows/cobalt_pathfinder.dll` appears in JAR listing.

- [ ] **Step 4: Commit**
```bash
git add build.gradle.kts src/main/resources/natives/windows/.gitkeep
git commit -m "feat: integrate C++ build into Gradle, embed DLL in JAR"
```

---

## Task 12: Migrate RoutesModule

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/mining/RoutesModule.kt`

Read the file first to understand current DuskPathfinder usage, then replace with `NativePathfinder`.

- [ ] **Step 1: Read current RoutesModule**
```
Read: src/main/kotlin/org/cobalt/internal/mining/RoutesModule.kt
```

- [ ] **Step 2: Replace pathfinding calls**

Pattern to replace:
```kotlin
// OLD
DuskPathfinder.moveTo(target)
// NEW
NativePathfinder.setRoute(waypoints, loop = true, profile = MovementProfile.MINING)
```

In the tick handler, add:
```kotlin
NativePathfinder.tick()?.applyToPlayer()
```

- [ ] **Step 3: Build**
```bash
./gradlew build
```

- [ ] **Step 4: Commit**
```bash
git add src/main/kotlin/org/cobalt/internal/mining/RoutesModule.kt
git commit -m "feat: migrate RoutesModule to NativePathfinder"
```

---

## Task 13: Migrate Mining Macros

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/mining/MiningMacroModule.kt`
- Modify: `src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt`

Same pattern as Task 12. Use `MovementProfile.MINING`.

- [ ] **Read both files, replace DuskPathfinder calls, build, commit**
```bash
git add src/main/kotlin/org/cobalt/internal/mining/MiningMacroModule.kt
git add src/main/kotlin/org/cobalt/internal/mining/CommissionMacroModule.kt
git commit -m "feat: migrate mining macros to NativePathfinder"
```

---

## Task 14: Migrate Pig Macro

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/pig/PigMacroModule.kt`

Garden and farming macros do NOT use DuskPathfinder — skip them. Pig macro needs dynamic target updates (pig moves constantly). Use `NativePathfinder.setTarget(pigPos, arrivalRadius)` each tick instead of `setRoute`. Use `cmd.distanceToTarget` to replace manual distance checks for interaction range and deploy range.

- [ ] **Read PigMacroModule:**
```
Read: src/main/kotlin/org/cobalt/internal/pig/PigMacroModule.kt
```

- [ ] **Replace SPRINT_TO_PIG / WALK_PRESSURE DuskPathfinder usage:**

```kotlin
// Each tick where macro needs to approach pig:
val pigPos = pig.position()
NativePathfinder.setTarget(Vec3(pigPos.x, pigPos.y, pigPos.z),
    arrivalRadius = deployRangeSetting.value)
val cmd = NativePathfinder.tick() ?: return
cmd.applyToPlayer()

// Replace manual distance checks:
if (cmd.distanceToTarget <= deployRangeSetting.value) {
    // within range — proceed to deploy
}
```

- [ ] **Build + Commit**
```bash
git add src/main/kotlin/org/cobalt/internal/pig/PigMacroModule.kt
git commit -m "feat: migrate pig macro to NativePathfinder"
```

---

## Task 15: Migrate Combat Macro

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/combat/CombatMacroModule.kt`

Use `MovementProfile.COMBAT`. Call `setTarget(mob.pos)` every tick — the look-ahead rotation naturally leads the moving mob.

- [ ] **Read CombatMacroModule, replace, build, commit**
```bash
git add src/main/kotlin/org/cobalt/internal/combat/CombatMacroModule.kt
git commit -m "feat: migrate combat macro to NativePathfinder"
```

---

## Task 16: Delete Old Pathfinding

Only run after all macros are confirmed working in-game.

- [ ] **Delete old files:**
```bash
git rm src/main/kotlin/org/cobalt/internal/pathfinding/DuskPathfinder.kt
git rm src/main/kotlin/org/cobalt/api/pathfinder/PathExecutor.kt
git rm src/main/kotlin/org/cobalt/internal/pathfinding/PathPlanProfiles.kt
git rm src/main/kotlin/org/cobalt/internal/pathfinding/PathPlanProfile.kt
```

- [ ] **Remove imports referencing deleted files in any remaining code, build:**
```bash
./gradlew build
```

- [ ] **Commit**
```bash
git commit -m "chore: remove old Kotlin pathfinding (DuskPathfinder, PathPlanProfiles, PathExecutor)"
```

---

## In-Game Validation Checklist

After each migration phase, test in Skyblock:
- [ ] RoutesModule: set a multi-waypoint route in a mining tunnel, confirm it navigates without getting stuck
- [ ] Mining macros: run a commission, confirm macro reaches veins without stutter
- [ ] Garden macro: run a plot patrol, confirm GROUND_ONLY keeps macro on farmland
- [ ] Pig macro: spawn pig, confirm macro approaches and stays behind pig
- [ ] Combat: confirm macro chases mob across uneven ground, AOTV fires at 11 blocks
- [ ] All macros: confirm smooth rotation (no snapping), micro-noise visible in hand movement
