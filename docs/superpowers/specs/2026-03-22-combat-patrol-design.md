# Combat Patrol Module — Design Spec
**Date:** 2026-03-22

## Overview

A new `CombatPatrolModule` in `internal/combat/` that lets the user record named patrol routes with three point types — **Walk**, **Warp**, and **Kill** — and run them in conjunction with `CombatMacroModule`. While patrolling, the combat macro stays active: it kills stray mobs encountered during transit and clears kill-zone mobs when the route reaches a Kill point.

---

## Point Types

| Type | Behavior |
|------|----------|
| **Walk** | Pathfind to the point using `NativePathfinder.setTarget`, advance on arrival |
| **Warp** | AOTV/etherwarp to the point — warp stage state machine copied from `RoutesModule` |
| **Kill** | Pathfind to point, then linger in `killZoneRadius` until no mobs remain (or `killZoneDwellTicks` timeout), then advance |

---

## Patrol States

```
IDLE
NAVIGATING       — walking/warping to a Walk or Kill point
COMBAT_INTERRUPT — mob spotted mid-transit; patrol pauses, CombatMacroModule fights
AT_KILL_ZONE     — arrived at a Kill point; CombatMacroModule fights mobs within killZoneRadius
```

---

## Module: CombatPatrolModule

**File:** `src/main/kotlin/org/cobalt/internal/combat/CombatPatrolModule.kt`

### Data

```kotlin
data class CombatPatrolPoint(val x: Int, val y: Int, val z: Int, val type: CombatPatrolPointType)

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
| Kill Zone Radius | SliderSetting | 16.0 | Mob search radius at Kill points |
| Kill Zone Dwell Ticks | SliderSetting | 40 | Max ticks to wait before advancing even if mobs remain |
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
if (!patrolRunning) return

NAVIGATING:
  tick NativePathfinder → applyToPlayer()
  if ARRIVED/FAILED → advance index, handle next point type
  if mob found by CombatMacroModule → transition to COMBAT_INTERRUPT (patrol stops driving pathfinder)

COMBAT_INTERRUPT:
  do nothing (CombatMacroModule owns NativePathfinder)
  when CombatMacroModule reports no target → re-issue setTarget for current waypoint → NAVIGATING

AT_KILL_ZONE:
  do nothing (CombatMacroModule owns NativePathfinder, uses kill zone radius)
  when no mobs in killZoneRadius for N ticks, OR dwellTicks exceeded → advance index → NAVIGATING
```

### Public API

```kotlin
val isPatrolRunning: Boolean        // user has started the patrol
var patrolState: PatrolState        // IDLE / NAVIGATING / COMBAT_INTERRUPT / AT_KILL_ZONE
val currentKillPoint: CombatPatrolPoint?  // non-null when AT_KILL_ZONE
val killZoneRadiusValue: Double     // used by CombatMacroModule for mob search at kill zones
fun onCombatInterrupt()             // called by CombatMacroModule when it finds a stray mob mid-transit
fun onCombatResume()                // called when no target remains; patrol re-issues waypoint
fun onKillZoneCleared()             // called when kill zone has no mobs; patrol advances
fun stopPatrol(msg: String = "")
```

---

## CombatMacroModule Integration

Three integration points (minimal diffs):

### 1. No-target branch (currently lines 793–800)
```kotlin
// If patrol is running and not at a kill zone, let patrol drive movement.
if (CombatPatrolModule.isPatrolRunning &&
    CombatPatrolModule.patrolState != PatrolState.AT_KILL_ZONE) {
    CombatPatrolModule.onCombatResume()
    startedPath = false; lastTargetPos = null; currentTargetId = null
    return
}

// At a kill zone with no mobs — signal cleared.
if (CombatPatrolModule.patrolState == PatrolState.AT_KILL_ZONE) {
    CombatPatrolModule.onKillZoneCleared()  // advances index after dwell
    startedPath = false; lastTargetPos = null; currentTargetId = null
    return
}
```

### 2. Target-found branch (currently line 834 area)
```kotlin
// Stray mob found mid-transit — interrupt patrol, combat macro takes over.
if (CombatPatrolModule.patrolState == PatrolState.NAVIGATING) {
    CombatPatrolModule.onCombatInterrupt()
}
```

### 3. `stopMacro()`
```kotlin
CombatPatrolModule.stopPatrol()
```

### Target search at Kill points
When `patrolState == AT_KILL_ZONE`, `CombatMacroModule.pickTarget()` uses `killZoneRadiusValue` (from `CombatPatrolModule`) as the search range instead of the normal `searchRange`, and only considers mobs within that radius of `currentKillPoint` position.

---

## Registration

`Cobalt.kt`: Add `CombatPatrolModule` to the module list, registered immediately after `CombatMacroModule`.

---

## Out of Scope

- Per-point kill zone radii (global slider only)
- HUD element for patrol status
- Visual route rendering (can be added later)
