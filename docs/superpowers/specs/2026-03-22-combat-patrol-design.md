# Combat Patrol Module — Design Spec
**Date:** 2026-03-22

## Overview

A new `CombatPatrolModule` in `internal/combat/` that lets the user record named patrol routes with three point types — **Walk**, **Warp**, and **Kill** — and run them in conjunction with `CombatMacroModule`. While patrolling, the combat macro stays active: it kills stray mobs encountered during transit and clears kill-zone mobs when the route reaches a Kill point.

---

## Point Types

| Type | Behavior |
|------|----------|
| **Walk** | Pathfind to the point using `NativePathfinder.setTarget`, advance on arrival |
| **Warp** | AOTV/etherwarp to the point — full warp state machine (see §Warp Sub-Machine) |
| **Kill** | Pathfind to point, then linger until all mobs in `killZoneRadius` are dead (N-tick debounce), then advance |

---

## Patrol States

```
IDLE
NAVIGATING        — walking to a Walk or Kill point, CombatPatrolModule owns NativePathfinder
WARPING           — executing AOTV warp sub-machine, frame-event driven, CombatPatrolModule owns pathfinder
COMBAT_INTERRUPT  — stray mob spotted mid-transit; patrol suspended, CombatMacroModule owns NativePathfinder
AT_KILL_ZONE      — arrived at a Kill point; CombatMacroModule owns NativePathfinder, fights in zone
```

**NativePathfinder ownership rule (exclusive):**
- `NAVIGATING` / `WARPING`: `CombatPatrolModule` calls `NativePathfinder.tick()?.applyToPlayer()` each tick. `CombatMacroModule` must NOT call it. Accomplished by `CombatPatrolModule` setting a shared `patrolOwnsPathfinder: Boolean` flag; `CombatMacroModule` skips its top-of-tick `tick()` call when this is true.
- `COMBAT_INTERRUPT` / `AT_KILL_ZONE` / `IDLE`: `CombatMacroModule` owns NativePathfinder normally.

---

## Module: CombatPatrolModule

**File:** `src/main/kotlin/org/cobalt/internal/combat/CombatPatrolModule.kt`

### Data

```kotlin
data class CombatPatrolPoint(val x: Int, val y: Int, val z: Int, val type: CombatPatrolPointType)
// Distance checks use x + 0.5, y.toDouble(), z + 0.5 (block centre, not corner)

enum class CombatPatrolPointType(val id: String) {
    WALK("walk"), WARP("warp"), KILL("kill")
}
```

### Settings

| Setting | Type | Default | Notes |
|---------|------|---------|-------|
| Route Name | TextSetting | "default" | Used for save/load |
| Points | InfoSetting | — | Live count display |
| Status | InfoSetting | — | IDLE / NAVIGATING / etc. |
| Record on Right Click | CheckboxSetting | false | Appends point at right-clicked block |
| Loop Route | CheckboxSetting | true | Wraps index back to 0 at end |
| Start From Nearest | CheckboxSetting | true | Begins at closest point to player |
| Point Type | ModeSetting | 0 | Walk / Warp / Kill |
| Kill Zone Radius | SliderSetting | 16.0 | Mob search radius at Kill points (distance from block centre) |
| Kill Zone Dwell Ticks | SliderSetting | 60 | Zero-mob ticks required before advancing past a Kill point |
| AOTV Slot | ModeSetting | 0 | Hotbar slot 1–9 for warp (mirrors RoutesModule) |
| Add Point | ActionSetting | — | Record player's current position |
| Remove Last | ActionSetting | — | Remove last point |
| Clear Route | ActionSetting | — | Clear all points |
| Save Route | ActionSetting | — | Save to `config/cobalt/combat_patrol/<name>.json` |
| Load Route | ActionSetting | — | Load from file |
| Start Patrol | ActionSetting | — | Start from nearest (if enabled) or index 0 |
| Stop Patrol | ActionSetting | — | Stop and reset state |

### Persistence

Routes saved to `config/cobalt/combat_patrol/<name>.json`:
```json
{
  "points": [
    { "x": 100, "y": 64, "z": 200, "type": "walk" },
    { "x": 120, "y": 64, "z": 210, "type": "kill" },
    { "x": 140, "y": 64, "z": 195, "type": "warp" }
  ]
}
```

### Tick Logic (TickEvent.Start)

```
// Guards
if (!enabled.value):
    if patrolRunning: stopPatrol("Patrol disabled.")
    return
if patrolPoints.isEmpty():
    if patrolRunning: stopPatrol("No patrol points.")
    return
if !patrolRunning: return

NAVIGATING:
    NativePathfinder.tick()?.applyToPlayer()          // owns pathfinder
    status = NativePathfinder.status
    if status == ARRIVED || status == FAILED:
        advance index (wrap if loop, else stop if end)
        navigate to next point (setTarget or startWarp)
    // Note: COMBAT_INTERRUPT transition is triggered externally by onCombatInterrupt()

WARPING:
    // Warp sub-machine runs in onRender (WorldRenderEvent.Last), not here.
    // TickEvent only updates warpStageElapsedMs and checks for timeout.
    if warpElapsedMs > WARP_TOTAL_TIMEOUT_MS:
        // Warp failed — advance past warp point rather than hanging.
        cancelWarp()
        advance index
        navigate to next point

COMBAT_INTERRUPT:
    // CombatMacroModule owns pathfinder. Do nothing.
    // Transition back to NAVIGATING is triggered by onCombatResume().

AT_KILL_ZONE:
    // CombatMacroModule owns pathfinder. Do nothing.
    // killZoneClearTicks is managed here:
    if noMobsInZone (signalled by onKillZoneCleared()):
        killZoneClearTicks++
        if killZoneClearTicks >= killZoneDwellTicks.value:
            advance index
            navigate to next point
    else:
        killZoneClearTicks = 0    // reset debounce if mob reappears
```

### Warp Sub-Machine

Mirrors `RoutesModule` warp logic exactly. Required additional state variables:
```kotlin
private var warpStage: Int = 0          // 0=idle, 1=align, 2=sneak+use, 3=post-warp
private var warpTarget: CombatPatrolPoint? = null
private var warpStageElapsedMs: Double = 0.0
private var warpStageLastNs: Long = 0L
private var warpLookLastNs: Long = 0L
private var warpCooldownUntil: Long = 0L
private var warpRestoreSlot: Int = -1
```

Requires a `WorldRenderEvent.Last` handler (`onRender`) in addition to `TickEvent.Start`. The warp is entirely frame-driven in `onRender`; `onTick` only handles the timeout fallback.

Warp failure behavior: if the warp stage does not complete within `WARP_TOTAL_TIMEOUT_MS` (copied from `RoutesModule`), `cancelWarp()` is called, the warp point is skipped, and the patrol advances to the next point. The patrol does NOT stop on warp failure.

`startWarp(point: CombatPatrolPoint)`:
- Validates AOTV is in the configured hotbar slot (else log and advance)
- Sets `patrolState = WARPING`, initialises warp state vars
- Sets `patrolOwnsPathfinder = true`

`cancelWarp()`:
- Resets all warp state vars
- Restores hotbar slot

### Public API

```kotlin
val isPatrolRunning: Boolean                        // user has started the patrol
var patrolOwnsPathfinder: Boolean                   // true → CombatMacroModule skips its tick() call
val patrolState: PatrolState
val currentKillPoint: CombatPatrolPoint?            // non-null when AT_KILL_ZONE
val killZoneRadiusValue: Double                     // used by CombatMacroModule for mob search radius at kill zones

// Called by CombatMacroModule — all are edge-triggered (guard against repeated calls in same state)
fun onCombatInterrupt()   // stray mob found during NAVIGATING/WARPING
fun onCombatResume()      // no target remains; resume patrol from current waypoint
fun onKillZoneCleared()   // no mobs in kill zone this tick
fun stopPatrol(msg: String = "")
```

**`onCombatInterrupt()` contract:**
- Only acts if `patrolState == NAVIGATING || WARPING`
- Calls `NativePathfinder.stop()` synchronously (prevents one-tick path overrun)
- Sets `patrolOwnsPathfinder = false`
- Sets `patrolState = COMBAT_INTERRUPT`

**`onCombatResume()` contract:**
- Only acts if `patrolState == COMBAT_INTERRUPT` (edge-triggered, not level-triggered)
- Re-issues `NativePathfinder.setTarget(...)` for `patrolPoints[routeIndex]`
- Sets `patrolOwnsPathfinder = true`
- Sets `patrolState = NAVIGATING`

**`onKillZoneCleared()` contract:**
- Only acts if `patrolState == AT_KILL_ZONE`
- Increments `killZoneClearTicks` (defined above in tick logic)
- Does NOT advance index itself — advancement is handled in `onTick` when counter threshold is met

---

## CombatMacroModule Integration

### 0. Top-of-tick NativePathfinder.tick() guard (line ~587)
```kotlin
if (startedPath && nativeActive() && !CombatPatrolModule.patrolOwnsPathfinder) {
    NativePathfinder.tick()?.applyToPlayer()
}
```

### 1. No-target branch (currently lines 793–800)
```kotlin
if (CombatPatrolModule.isPatrolRunning) {
    when (CombatPatrolModule.patrolState) {
        PatrolState.COMBAT_INTERRUPT -> CombatPatrolModule.onCombatResume()
        PatrolState.AT_KILL_ZONE     -> CombatPatrolModule.onKillZoneCleared()
        else -> { /* NAVIGATING/WARPING — patrol owns movement, do nothing */ }
    }
    startedPath = false; lastTargetPos = null; currentTargetId = null
    return
}
```

### 2. Target-found branch (currently line 834 area)
```kotlin
// Interrupt patrol if it is actively navigating between points.
if (CombatPatrolModule.patrolState == PatrolState.NAVIGATING ||
    CombatPatrolModule.patrolState == PatrolState.WARPING) {
    CombatPatrolModule.onCombatInterrupt()
}
```

### 3. `stopMacro()`
```kotlin
if (CombatPatrolModule.isPatrolRunning) CombatPatrolModule.stopPatrol()
```

### Kill zone target search
When `CombatPatrolModule.patrolState == AT_KILL_ZONE`, `CombatMacroModule.pickTarget()` uses `CombatPatrolModule.killZoneRadiusValue` as the search radius and only considers mobs within that radius of `currentKillPoint` block centre (`x + 0.5`, `y.toDouble()`, `z + 0.5`). Normal `searchRange` and `stayNearStart` constraints are suspended at kill zones.

---

## Registration

`Cobalt.kt`: Add `CombatPatrolModule` to the module list immediately after `CombatMacroModule`.

---

## Out of Scope

- Per-point kill zone radii (global slider only)
- HUD element for patrol status
- Visual route rendering (can be added later)
