# Pathfinder Improvements Design

**Date:** 2026-03-21
**Status:** Approved

---

## Summary

Five improvements to the C++ native pathfinder system:

1. Expose full path nodes via JNI for client-side rendering
2. Render a smooth Catmull-Rom spline at feet height showing the planned route
3. Render a head-direction ray from the player's eye along the crosshair
4. Auto-detect 20+ block straight runs and fire AOTV (hotbar swap + right-click), with distinct visual segment coloring
5. Add clearance cost in C++ MovementExpander so paths thread through the middle of tunnels and doorways

---

## Section 1: JNI Path Node Exposure

### What changes
- `NativePathfinderBridge.java` â€” new native method `getPathNodes()` returning `float[]`
- `pathfinder_jni.cpp` â€” implements `getPathNodes()` by iterating `PathExecutor`'s current node list and packing into `[x0,y0,z0, x1,y1,z1, ...]`
- `NativePathfinder.kt` â€” new `cachedPathNodes: List<Vec3d>` field; re-fetched only when `PathStatus` transitions to `EXECUTING` or `REPLANNING`

### Constraints
- JNI call fires only on status transition, not every tick
- Returns empty array if no path is planned
- Nodes are block-center coordinates (Vec3d with 0.5 offsets)

---

## Section 2: Spline Rendering (Feet Path)

### What changes
- New `PathSplineRenderer` object in `PathfindingModule.kt` (or a companion file)
- On path node cache update: runs Catmull-Rom spline interpolation, generating ~10 interpolated `Vec3d` points per segment
- Pre-computed spline points cached as `List<Vec3d>` â€” no per-frame recomputation
- On `WorldRenderEvent.Last`: draws lines between consecutive spline points using `OverlayRenderEngine.addLine()`

### Visual spec
- Y offset: `+0.05` above ground (feet level)
- Normal segments: cyan â†’ blue gradient
- AOTV segments: orange/yellow, drawn on top of normal path
- Short expiry (1â€“2 ticks) so lines auto-clear when path stops
- Spline rebuilt only when cached path nodes change

---

## Section 3: Head Direction Ray

### What changes
- `PathfindingModule.kt` `WorldRenderEvent.Last` handler adds a head-direction line each frame

### Visual spec
- Origin: `player.eyePosition` (head height)
- Direction: `player.getRotationVector()` normalized
- Length: 5 blocks
- Color: white or bright green â€” distinct from path line
- Expiry: 1 tick (updates every frame, never lingers)
- Active whenever PathfindingModule is enabled, regardless of path state

---

## Section 4: AOTV Straight-Segment Detection & Auto-Fire

### Detection algorithm
After caching path nodes, run a sliding-window direction-cosine check:
- Window size: 5 consecutive nodes
- Compute direction vector for each consecutive pair
- If cumulative straight run â‰¥ 20 blocks and direction deviation stays within ~15Â°, flag as AOTV segment
- Store flagged index ranges alongside cached spline for rendering

### Auto-fire behavior
In `PathCommand.applyToPlayer()`, when `actionType == ActionType.AOTV`:
1. Record currently held hotbar slot
2. Swap to configured AOTV hotbar slot via `player.inventory.selectedSlot`
3. Simulate right-click via `options.useKey` / `KeyBinding.setKeyPressed`
4. Swap back to previous slot after AOTV fires
- Fires once per AOTV node, not held
- AOTV hotbar slot is a `ModeSetting("AOTV Slot", ..., 0, arrayOf("1","2","3","4","5","6","7","8","9"))` in PathfindingModule

### Visual spec
- AOTV-flagged segments rendered in orange/yellow over normal path line

---

## Section 5: Tunnel/Doorway Centering (C++ Clearance Cost)

### What changes
- `MovementExpander.cpp` â€” additional clearance penalty added to `g` cost for each expanded node

### Algorithm
For each candidate neighbor node, sample the 4 horizontal cardinal neighbors at the same Y in the world buffer:
- Count open (non-solid) blocks among those 4
- Clearance 4 (wide open): +0.0 penalty
- Clearance 3: +0.1 penalty
- Clearance 2: +0.3 penalty
- Clearance 1 (tight tunnel): +0.5 penalty

### Properties
- Paths through centers of corridors are preferred without blocking tight traversal
- No change to the A* heuristic â€” only `g` cost is modified, so correctness is preserved
- Wide open areas: no behavior change
- 1-block gaps: still traversable, just higher cost than open alternatives
- Results in human-like path centering and reduced stuck risk in tunnels/doorways

---

## Files Affected

| File | Change |
|------|--------|
| `natives/src/engine/MovementExpander.cpp` | Add clearance cost per node |
| `natives/src/pathfinder_jni.cpp` | Implement `getPathNodes()` JNI method |
| `src/main/java/org/phantom/pathfinder/NativePathfinderBridge.java` | Declare `getPathNodes()` native method |
| `src/main/kotlin/org/phantom/api/pathfinder/jni/NativePathfinder.kt` | Cache path nodes on status transition |
| `src/main/kotlin/org/phantom/internal/pathfinding/PathfindingModule.kt` | Add spline renderer, head ray, AOTV detection, AOTV slot setting |
| `src/main/kotlin/org/phantom/api/pathfinder/jni/PathCommand.kt` | AOTV hotbar swap + right-click in `applyToPlayer()` |

---

## Non-Goals

- No changes to the A* heuristic function
- No changes to how path planning is triggered
- No new JNI calls on every tick (only on status change)
- No modifications to rotation system (C++ bezier rotation unchanged)
