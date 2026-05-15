# Route System Rewrite â€” Design Spec
**Date:** 2026-04-15

---

## Overview

A full rewrite of the route system. All route types (mining, commission, combat patrol, gemstone, tunnel) are unified under one data model, one routes screen, and one point-recording flow. The scattered per-macro route panels and the old `PatrolWaypointStore` / `WalkbackBridge` / `UICommissionRoutesPanel` are replaced.

---

## 1. Data Model

### 1.1 Route Types

```kotlin
enum class RouteType {
    ORE_MINER,   // travel route + loop route
    COMMISSION,  // single route, starts from /warpforge
    PATROL,      // travel route + patrol area
    GEMSTONE,    // single warp-and-mine loop
    TUNNEL,      // /warpcamp â†’ mine anchors â†’ /warpcamp
}
```

### 1.2 Point Types

```kotlin
enum class RoutePointType {
    WALK,   // walk to this block
    WARP,   // etherwarp to this block
    MINE,   // mine this vein (carries mineEnd + optional blockId)
    KILL,   // dwell here until mobs are cleared (patrol only)
}
```

Available point types per sub-route:

| Sub-route | WALK | WARP | MINE | KILL |
|---|:---:|:---:|:---:|:---:|
| Ore Miner â€” Travel | âœ“ | âœ“ | | |
| Ore Miner â€” Loop | | âœ“ | âœ“ | |
| Commission | âœ“ | âœ“ | âœ“ | |
| Patrol â€” Travel | âœ“ | âœ“ | | |
| Patrol â€” Area | âœ“ | âœ“ | | âœ“ |
| Gemstone | | âœ“ | âœ“ | |
| Tunnel | âœ“ | âœ“ | âœ“ | |

### 1.3 File Format

**Location:** `config/phantom/routes2/<name>.json`

Dual-sub-route types (Ore Miner, Patrol):
```json
{
  "name": "iron_veins_v2",
  "type": "ORE_MINER",
  "travelRoute": [
    { "type": "WALK", "x": 100, "y": 64, "z": 100 },
    { "type": "WARP", "x": 115, "y": 64, "z": 88 }
  ],
  "loopRoute": [
    { "type": "WARP", "x": 120, "y": 64, "z": 88 },
    { "type": "MINE", "x": 125, "y": 64, "z": 90, "mx": 130, "my": 64, "mz": 92, "bid": "minecraft:iron_ore" }
  ]
}
```

Single-sub-route types (Commission, Gemstone, Tunnel):
```json
{
  "name": "royal_mithril",
  "type": "COMMISSION",
  "points": [
    { "type": "WALK", "x": 5, "y": 70, "z": 10 },
    { "type": "MINE", "x": 20, "y": 72, "z": 18, "mx": 25, "my": 72, "mz": 20 }
  ]
}
```

Both sub-route arrays are unbounded â€” no point count limit.

### 1.4 Migration

On first launch after the update, the system scans `config/phantom/routes/*.json`. Each file is converted to the new format:
- `type` â†’ `ORE_MINER`
- Points mapped: `NORMAL â†’ WALK`, `WARP â†’ WARP`, `MINE â†’ MINE` (mineEnd and blockId preserved)
- Points go into `loopRoute`; `travelRoute` is left as an empty array
- Saved to `config/phantom/routes2/<name>.json`

Old files in `config/phantom/routes/` are left untouched.

---

## 2. Routes Screen UI

### 2.1 Layout

Replaces all existing route-related panels. Opened from the main Phantom UI.

**Top bar:**
- Search input (filters by name)
- Type filter tabs: All | Ore Miner | Commission | Patrol | Gemstone | Tunnel
- **+ New Route** button (top right)

**Route list â€” expandable cards:**

Collapsed state (one row per route):
```
[TYPE BADGE]  route-name               [â–¶ Load]  [â–¼]   (âœ• on hover)
```

Expanded state â€” dual sub-route types (Ore Miner, Patrol):
```
[TYPE BADGE]  route-name               [â–¶ Load]  [â–²]
  â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
  â”‚ ðŸš¶ Travel Route    (8 pts)    [âœ Edit]  [+ Insert]  â”‚
  â”‚ ðŸ”„ Loop Route     (16 pts)    [âœ Edit]  [+ Insert]  â”‚
  â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
```

Expanded state â€” single sub-route types (Commission, Gemstone, Tunnel):
```
[TYPE BADGE]  route-name               [â–¶ Load]  [â–²]
  â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
  â”‚ Route Points       (12 pts)   [âœ Edit]  [+ Insert]  â”‚
  â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
```

### 2.2 Actions

- **â–¶ Load** â€” arms the route for its macro type. Does not start the macro; the macro picks it up when it next runs.
- **âœ Edit** â€” closes the panel, activates in-world edit mode for that specific sub-route.
- **+ Insert** â€” opens the insert overlay on top of the routes screen.
- **+ New Route** â€” opens the type picker modal.
- **âœ• Delete** (hover) â€” removes the route file after confirmation.

### 2.3 New Route Creation Flow

1. Click **+ New Route** â†’ type picker modal appears (5 cards: Ore Miner, Commission, Patrol, Gemstone, Tunnel with descriptions and color badges).
2. Click a type â†’ name input field appears below the cards.
3. Click **Create** â†’ route file created with empty sub-routes â†’ immediately enters Edit mode for the first sub-route (Travel Route for dual types, Points for single types).

### 2.4 Removed Panels

The following existing panels are removed:
- `UICommissionRoutesPanel` â€” commission zone assignment moves to a dropdown in commission macro settings
- `UICombatWalkbackRoutesPanel` â€” slayer route assignment moves to dropdowns in combat macro settings
- `UIRoutePointPicker` â€” replaced by the unified edit mode
- `PatrolPointPopup` â€” replaced by the unified sticky mode HUD

---

## 3. Point Recording & Edit Mode

### 3.1 Entering Edit Mode

Triggered by clicking âœ Edit on any sub-route. The panel closes and the game returns to first person with the edit overlay active.

### 3.2 Sticky Mode HUD

Rendered via NVG, positioned bottom-center of screen.

Contents:
- Route and sub-route label: `iron_veins_v2 â€º Loop Route`
- Mode buttons for the available point types of this sub-route â€” current mode highlighted, click to switch
- Live point count
- **Undo** button â€” removes the last recorded point, saves immediately
- **Done** button â€” saves and exits edit mode, returns to routes screen

### 3.3 Recording Points

- **Right-click any block** â†’ records a point of the current mode instantly (no popup)
- **Shift + right-click** â†’ type popup appears for that one point only (shows only the types valid for this sub-route)
- Points are written to disk immediately on each addition

### 3.4 In-World Rendering During Edit

- Small labeled box at each point position â€” number, type badge, coordinate
- Lines connecting points in order
- Most recently added point pulses briefly to confirm placement

### 3.5 Insert Mode

Triggered by **+ Insert** on the routes screen.

Flow:
1. Overlay panel lists current points with clickable gap slots between each one (e.g., `â† insert between 2 and 3`)
2. Click a gap slot â†’ overlay closes â†’ sticky HUD shows `Inserting after point N`
3. Right-click a block in-world â†’ type popup always appears (no sticky mode for inserts â€” deliberate one-off)
4. Point is inserted at that index; all subsequent points renumber
5. Saved to disk immediately
6. Overlay reopens to allow further insertions
7. **Done** returns to routes screen

### 3.6 Undo

Single-level undo in the sticky HUD. One press removes the last added point and saves immediately. Works in both normal recording and insert mode.

---

## 4. Macro Integration

### 4.1 Route Assignment UI

Each macro's settings panel gets a dropdown (or autocomplete text field) populated from saved routes of the matching type. Replaces all current text-field + separate-picker-panel patterns.

- Commission macro: one dropdown per zone (Royal, Cliffside, Lava, Ramp, Upper) â€” shows only `COMMISSION` routes
- Combat macro: one dropdown per slayer type (Zombie, Wolf, Spider, Enderman, Vampire, Blaze) â€” shows only `PATROL` routes
- Mining macro: dropdown for active ore miner route â€” shows only `ORE_MINER` routes
- Gemstone macro: dropdown â€” shows only `GEMSTONE` routes
- Tunnel macro: dropdown â€” shows only `TUNNEL` routes

### 4.2 Ore Miner Execution

1. Run travel route once via pathfinder (WALK = pathfind to block, WARP = etherwarp)
2. Loop the loop route indefinitely: WARP to each vein point, run mining macro on the MINE block until depleted, move to next WARP point

### 4.3 Commission Execution

1. Issue `/warpforge`
2. Follow route WALK/WARP/MINE points
3. Mine at each MINE point until the commission completes
4. Stop and report completion

### 4.4 Patrol Execution

1. Run travel route once to reach the combat area
2. Loop patrol area: WALK/WARP between points, dwell at KILL zones until mobs are cleared, advance
3. Replaces current walkback + PatrolWaypointStore / KillWaypoint system

### 4.5 Gemstone Execution

1. Loop: WARP to each WARP point, mine the MINE block, advance to next

### 4.6 Tunnel Execution

1. Issue `/warpcamp`
2. Follow WALK/WARP/MINE points into the tunnels, mining each anchor
3. When cold threshold is hit: issue `/warpcamp`, wait for warmup, repeat from step 2

---

## 5. Files Added / Removed

**New files:**
- `internal/routes/RouteStore.kt` â€” load, save, list, migrate routes
- `internal/routes/RouteType.kt` â€” enum + point type availability per sub-route
- `internal/routes/SavedRoute.kt` â€” data classes (SavedRoute, RoutePoint, RoutePointType)
- `internal/routes/RouteEditMode.kt` â€” in-world edit mode controller (sticky HUD, recording, insert, undo)
- `internal/ui/panel/panels/UIRoutesPanel.kt` â€” unified routes screen
- `internal/ui/panel/panels/UINewRouteModal.kt` â€” type picker + name modal

**Removed / replaced:**
- `internal/mining/RoutesModule.kt` â€” execution logic moves to macro modules; storage moves to RouteStore
- `internal/pathfinding/PatrolWaypointStore.kt` â€” replaced by PATROL routes
- `internal/helper/WalkbackBridge.kt` â€” replaced by PATROL route assignment dropdowns
- `internal/ui/panel/panels/UICommissionRoutesPanel.kt`
- `internal/ui/panel/panels/UICombatWalkbackRoutesPanel.kt`
- `internal/ui/panel/panels/UIRoutePointPicker.kt`
- `internal/ui/hud/RoutePointPopup.kt`
- `internal/ui/hud/PatrolPointPopup.kt`
- `internal/ui/hud/WalkbackRoutePickerPopup.kt`
