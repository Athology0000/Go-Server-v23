# Pathfinder: Long-Distance Planning & Kill-Waypoint Patrol

**Date:** 2026-03-22
**Status:** Approved

---

## Problem Statement

Two gaps in the current pathfinder:

1. **Distance limit** — `WorldAccessor::callbackBlock` returns `BT_SOLID` for any block outside the 64×32×64 world buffer. Goals beyond ~30 blocks cause A* to exhaust 50k iterations and return FAILED. Complex terrain (height changes, walls, caves) makes straight-line hopping unusable.

2. **Patrol** — No patrol system exists. The slayer macro needs to randomly visit designated kill spots within a defined tunnel/route, without straying outside it.

---

## Feature 1 — Progressive Buffer-Edge A*

### How it works

When the goal is outside the current world buffer, A* cannot reach it directly. Instead of failing, the planner tracks the **best frontier node** — the reachable node that gets closest to the goal — and returns a partial path to it. The executor runs to the frontier, the buffer re-centers on the player, and A* re-plans toward the real goal from the new position. This repeats until the goal enters the buffer, at which point a full path is found.

Each local plan uses real terrain data: AOTV and etherwarp are evaluated at every node expansion, so they are used naturally whenever beneficial (open corridors → AOTV, long straight walls → etherwarp).

### C++ changes

**`AStarResult` struct** — add `isPartial` flag:
```cpp
struct AStarResult {
    bool found    = false;
    bool isPartial = false;   // true = path leads to best frontier, not the goal
    std::vector<Vec3i> nodes;
};
```

**`AStarPlanner::startAsync`** — changes:
1. Before launching the thread (on the main thread, against the `world` reference which is still valid): check `world.inBuffer(goal.x, goal.y, goal.z)`. Capture the result as `bool goalInBuffer` into the thread lambda. Do NOT perform this check inside the lambda.
2. Track `bestFrontierNode` (Vec3i) and `bestFrontierDist` (float, init = `heuristic(start, goal)`) as local variables in the lambda. Initial frontier = none set.
3. During search: when visiting a node, if `!goalInBuffer && heuristic(cur, goal) < bestFrontierDist`, update `bestFrontierNode = cur`, `bestFrontierDist = heuristic(cur, goal)`.
4. On termination (exhausted, not found): if `!goalInBuffer && bestFrontierDist < heuristic(start, goal)` (i.e. frontier advanced beyond start), reconstruct path to `bestFrontierNode`, set `res.isPartial = true`. If frontier never advanced past start, return `found=false, isPartial=false` (FAILED — no progress possible).
5. Raise `MAX_ITER` from 50,000 → 150,000.

**`WorldAccessor`** — expose `inBuffer(x,y,z)` publicly (currently private via `inBuffer`). Add public wrapper:
```cpp
bool inBuffer(int x, int y, int z) const;
```

**`PathExecutor`** — two guards must be updated. Both the `PLANNING` and `REPLANNING` cases currently check `!res.found` and transition to FAILED. They must be changed to treat a partial result as valid:
```cpp
// Before (both PLANNING and REPLANNING handlers):
if (!res.found) { status_ = PathStatus::FAILED; return idleCmd(...); }

// After:
if (!res.found && !res.isPartial) { status_ = PathStatus::FAILED; return idleCmd(...); }
```
Beyond that, the existing REPLANNING loop handles partial paths correctly: partial path executed → `pathNodeIdx_` exhausted → `dist > arrivalRadius_` → REPLANNING → `startPlan` fires from new player position (re-centered buffer) → re-plans toward same `waypoints_[waypointIdx_]`.

**Frontier = start guard** — if no node during search is closer to the goal than the start position (e.g. fully walled-in start), `bestFrontierNode == start`. In this case the planner must return `found=false, isPartial=false` (i.e. FAILED) rather than a trivial single-node partial path, which would cause an infinite REPLAN loop at the same position.

### Behaviour at runtime

| Distance to goal | Behaviour |
|---|---|
| ≤ 30 blocks | Single A* plan, normal execution |
| 30–300 blocks | Chain of partial plans, re-plans on each frontier arrival |
| AOTV available | Expander picks AOTV legs automatically at each local plan |
| Etherwarp available | Expander picks etherwarp legs automatically |
| Wall/height change | Each local plan navigates around real geometry |

---

## Feature 2 — Route Recording + Kill-Waypoint Patrol

### Concepts

| Term | Definition |
|---|---|
| **Route waypoint** | Ordered position defining the patrol corridor (the tunnel path). Recorded by the user walking the tunnel. |
| **Kill waypoint** | A specific mob-spawn spot within the tunnel. Recorded by right-clicking a block. Patrol destinations are chosen from this list only. |
| **Sub-route** | Slice of the route waypoint list from current position to a target kill waypoint. Used as the `setRoute` call so the bot follows the tunnel. |

### Kotlin data

```kotlin
data class RouteWaypoint(val x: Double, val y: Double, val z: Double)
data class KillWaypoint(val x: Double, val y: Double, val z: Double, val label: String = "")
```

Both lists persist to `config/cobalt/addons.json` via the existing `Config` serialization.

### Recording

**Route waypoints** — `ActionSetting("Record Route Point")` in the module UI: records `player.x/y/z` and appends to `routeWaypoints`. A separate `ActionSetting("Clear Route")` clears the list.

**Kill waypoints** — `MouseEvent.RightClick` handler: when patrol mode is enabled and a config flag `recordingKillPoints` is true (toggled via `ActionSetting("Record Kill Points")`), a right-click records the targeted block's position + 1 Y (feet level) as a `KillWaypoint`. A separate `ActionSetting("Clear Kill Points")` clears the list.

### Sub-route computation

```
fun buildSubRoute(from: Vec3, killWp: KillWaypoint): List<RouteWaypoint>
  // Use full 3D Euclidean distance for nearest-waypoint search (handles vertical tunnels)
  j = index of routeWaypoints with minimum distance3D(routeWaypoints[j], from)
  i = index of routeWaypoints with minimum distance3D(routeWaypoints[i], killWp)

  if j == i → return [RouteWaypoint(from), RouteWaypoint(killWp)]

  // Explicit bounds — subList(a,b) requires a <= b in Kotlin
  fwdSlice = if j <= i then routeWaypoints.subList(j, i + 1) else emptyList()
  bwdSlice = if i <= j then routeWaypoints.subList(i, j + 1).reversed() else emptyList()

  slice = when {
    fwdSlice.isEmpty() -> bwdSlice
    bwdSlice.isEmpty() -> fwdSlice
    else -> if fwdSlice.size <= bwdSlice.size then fwdSlice else bwdSlice
  }

  // Prepend player position so path starts exactly where the player stands
  return listOf(RouteWaypoint(from.x, from.y, from.z)) + slice + RouteWaypoint(killWp.x, killWp.y, killWp.z)
```

The sub-route is passed to `NativePathfinder.setRoute(waypoints, loop=false, profile=DEFAULT)`. Each waypoint is ≤ route-recording step size apart (~5–15 blocks), so each hop stays well within the world buffer. Combined with progressive A*, any inter-waypoint leg that exceeds the buffer is handled automatically.

### Patrol state machine

```
IDLE
  │  startPatrol()
  ▼
NAVIGATING_TO_KILL
  │  NativePathfinder.status == ARRIVED
  ▼
AT_KILL
  │  immediately (or after dwell ticks from setting)
  ▼
SELECTING_NEXT  ──────────────────────────────────────────┐
  │  pick random kill waypoint ≠ current                  │
  │  buildSubRoute → NativePathfinder.setRoute(...)       │
  └─────────────────────────────────────────────────────► NAVIGATING_TO_KILL
```

Random selection: `killWaypoints.filter { it != current }.random()`. Guard: if `killWaypoints.size < 2`, `startPatrol()` returns early with a chat message. The same guard applies at each `SELECTING_NEXT` transition.

### Settings added to PathfindingModule

| Setting | Type | Purpose |
|---|---|---|
| `Patrol Mode` | CheckboxSetting | Enable patrol loop |
| `Record Route Point` | ActionSetting | Append current position to route |
| `Clear Route` | ActionSetting | Clear all route waypoints |
| `Record Kill Points` | CheckboxSetting | Toggle right-click kill wp recording |
| `Clear Kill Points` | ActionSetting | Clear all kill waypoints |
| `Kill Dwell Ticks` | SliderSetting (0–100) | Ticks to wait at kill spot before moving |
| Route count / Kill count | InfoSetting | Live count display |

---

## Data Flow Summary

```
User records route  →  routeWaypoints: List<RouteWaypoint>
User records kills  →  killWaypoints:  List<KillWaypoint>

startPatrol()
  → pick random killWaypoints[i]
  → buildSubRoute(player, killWaypoints[i])  →  subRoute: List<Vec3d>
  → NativePathfinder.setRoute(subRoute, loop=false)
  → PatrolState = NAVIGATING_TO_KILL

onTick (PatrolState == NAVIGATING_TO_KILL):
  → NativePathfinder.tick() → cmd → applyToPlayer()
  → if status == ARRIVED → PatrolState = AT_KILL → dwell countdown

onTick (PatrolState == AT_KILL, dwell done):
  → pick next random kill wp ≠ current
  → buildSubRoute → setRoute → PatrolState = NAVIGATING_TO_KILL

C++ (per local A* plan):
  → if goal in buffer  → normal A*
  → if goal out of buffer → frontier A* → partial path
  → PathExecutor re-plans on frontier arrival automatically
```

---

## Files Touched

### C++
| File | Change |
|---|---|
| `natives/include/Types.h` | Add `isPartial` to `AStarResult` |
| `natives/src/engine/AStarPlanner.h` | Declare frontier tracking fields |
| `natives/src/engine/AStarPlanner.cpp` | Frontier tracking, partial return, MAX_ITER 150k |
| `natives/src/engine/WorldAccessor.h` | Make `inBuffer` public |

### Kotlin
| File | Change |
|---|---|
| `api/pathfinder/jni/NativePathfinder.kt` | No changes needed |
| `internal/pathfinding/PathfindingModule.kt` | Add patrol state machine, route/kill wp lists, settings, sub-route builder, right-click handler |

### Config
The existing `Config` system serializes `Setting` values only — it does not auto-serialize `List<data class>`. Route waypoints and kill waypoints require **explicit serialization** in `PathfindingModule`: implement `serializeExtra(): JsonObject` and `deserializeExtra(JsonObject)` hooks (or equivalent pattern used by the Config system) to write/read the two lists as JSON arrays under keys `"routeWaypoints"` and `"killWaypoints"` within the module's config block.

---

## Out of Scope

- Diagonal movement (4-directional expander is sufficient for Skyblock tunnels)
- Multi-floor route branching
- Visual route editor (can be added later)
