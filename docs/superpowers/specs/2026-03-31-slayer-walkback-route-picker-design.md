# Slayer Walkback Route Picker — Design Spec

**Date:** 2026-03-31

## Overview

Add a per-slayer-type walkback route selector to each slayer section in the `TAB_SLAYER_WEAPONS_GROUP`. Replace the single shared `slayerWalkbackRoute` TextSetting with 6 individual settings (one per type), each exposed via an ActionSetting button that opens a styled route picker popup. Also fix the walkback behavior so it fires correctly when the player dies, leaves the farming area, or is teleported to another world/server.

---

## 1. Settings Changes

### Remove
- `slayerWalkbackRoute` (TextSetting in `TAB_SLAYER_GROUP`) — removed entirely.

### Add (per slayer type)
Six hidden `TextSetting` instances, registered in `settings` list and persisted via config but not rendered directly by the UI (they serve as the backing store):

```
zombieWalkbackRoute   TextSetting("Zombie Walkback Route",   "", "")
endermanWalkbackRoute TextSetting("Enderman Walkback Route", "", "")
spiderWalkbackRoute   TextSetting("Spider Walkback Route",   "", "")
wolfWalkbackRoute     TextSetting("Wolf Walkback Route",     "", "")
vampireWalkbackRoute  TextSetting("Vampire Walkback Route",  "", "")
blazeWalkbackRoute    TextSetting("Blaze Walkback Route",    "", "")
```

Six `ActionSetting` buttons, each placed directly after the corresponding separator in `TAB_SLAYER_WEAPONS_GROUP` (e.g. after `sepZombie`, before `slayerLocation`):

- **name:** `"Walkback Route"`
- **description:** `"Route to follow back to farming area after boss kill."`
- **buttonLabel:** dynamic — shows current route name or `"None"` if blank. Requires modifying `ActionSetting` to accept an optional `buttonLabelProvider: (() -> String)? = null` parameter; when non-null, `buttonLabel` returns `buttonLabelProvider.invoke()`. `UIActionSetting` reads `setting.buttonLabel` each render frame, so no UI change needed.
- **onClick:** opens `WalkbackRoutePickerPopup.open(slayerTypeName, backingTextSetting)`

Helper function `walkbackRouteForCurrentType(): TextSetting` — returns the correct backing `TextSetting` based on `slayerType.value` (0=Zombie, 1=Wolf, 2=Spider, 3=Enderman, 4=Vampire, 5=Blaze).

---

## 2. WalkbackRoutePickerPopup (new file)

**Location:** `src/main/kotlin/org/cobalt/internal/ui/hud/WalkbackRoutePickerPopup.kt`

### Appearance
Matches the visual style of `UICommissionRoutesPanel`:

- Panel: 320×500px, centered on screen, rounded corners, `theme.background` fill
- **Title:** `"Walkback Route — <Slayer Type>"` (13px, `theme.text`)
- **Subtitle:** `"Select a route to follow to walk back to the area if killed or teleported away."` (9px, `theme.textSecondary`, wraps if needed)
- **Top-right buttons:** `Refresh` | `Clear`
  - `Refresh` reloads `RoutesModule.getSavedRouteInfos()`
  - `Clear` sets the backing `TextSetting` to `""` and closes the popup
- **Route rows** (same structure as `UICommissionRoutesPanel.renderRouteRow`):
  - Checkbox (filled accent + checkmark SVG if selected, hollow border if not)
  - Route name (accent color if selected, `theme.text` otherwise)
  - Details line: warp points flag, point count (same `buildRouteDetails` logic)
  - Accent border on selected row
  - Hover overlay on non-selected rows
- **Scrollable list** via `ScrollHandler`; scissor-clipped
- **Empty state:** `"No saved routes found."` + `"Save routes in the Routes module, then press Refresh."`
- **Semi-transparent dark overlay** behind panel (0,0,0,120 alpha) covering full screen

### Interaction
- Click a route row → sets `backingTextSetting.value = route.name`, closes popup
- Click `Refresh` → reloads route list, does not close
- Click `Clear` → sets `backingTextSetting.value = ""`, closes popup
- Click outside panel → closes without change
- Right-click anywhere → closes without change
- Mouse scroll over panel → scrolls list

### Notes on cursor/movement locking
Unlike `PatrolPointPopup`, this popup opens from within the settings UI where the cursor is already free. No `lockPlayer` or `MouseUtils.ungrabMouse` calls are needed.

### API
```kotlin
object WalkbackRoutePickerPopup {
    fun open(slayerTypeName: String, setting: TextSetting)
    fun isVisible(): Boolean
    fun render()
    fun mouseClicked(button: Int): Boolean
    fun mouseScrolled(h: Double, v: Double): Boolean
}
```

`WalkbackRoutePickerPopup` is an `object` with `@SubscribeEvent` handlers for `NvgEvent`, `MouseEvent.LeftClick`, and `MouseEvent.RightClick`. It is registered via `EventBus.register(WalkbackRoutePickerPopup)` inside `CombatMacroModule`'s init block (same pattern as `PatrolPointPopup` in `CombatPatrolModule`).

---

## 3. Behavior Changes

### 3a. Area Entry Tracking
- New flag: `private var enteredFarmingArea = false`
- Reset to `false` when the macro starts or when a walkback route completes.
- Set to `true` when the player comes within `GRAVEYARD_PROXIMITY_RANGE_SQ` (30 blocks) of any point in `CombatPatrolModule.patrolPoints`.
- Only relevant when patrol points are non-empty. If no patrol points are configured, skip tracking.

### 3b. Area Exit Detection
Each tick, while:
- Macro is active
- `enteredFarmingArea == true`
- No boss is active (`!slayerBossActive`)
- Not already running a walkback (`WalkbackBridge.isRunning() != true`)
- Not already dead/respawning

Check: player is farther than `GRAVEYARD_PROXIMITY_RANGE_SQ` from **all** patrol points.
If true → call `triggerWalkToFarmArea(justFarm = false)`, reset `enteredFarmingArea = false`.

The Crypt-specific `slayerEnteredCrypt` tracking is unchanged.

### 3c. Death → Walkback for All Slayer Types
Currently death-triggered walkback is guarded by `cryptZombieSlayer.value`. Remove that guard so all slayer types queue a walkback on respawn.

### 3d. World/Server Change Detection
`lastKnownLevel` is already compared each tick to detect server switches. When a level change is detected and the macro is active:
- Reset `enteredFarmingArea = false`
- Queue walkback (set `slayerNeedsWalkback = true`) so the player returns to the farm area on reconnect.

---

## 4. Runtime Route Lookup

Replace all references to `slayerWalkbackRoute.value` with calls to `walkbackRouteForCurrentType().value`.

This affects:
- `triggerWalkToFarmArea()`
- `finishSlayerClaim()`
- The startup proximity check at macro start (lines ~1163–1164)

---

## 5. Files Changed

| File | Change |
|------|--------|
| `CombatMacroModule.kt` | Remove `slayerWalkbackRoute`; add 6 backing TextSettings + 6 ActionSettings; register `WalkbackRoutePickerPopup`; add area tracking flags + logic; update all route lookups; fix death guard; fix world-change walkback |
| `WalkbackRoutePickerPopup.kt` | **New file** — modal route picker popup styled like UICommissionRoutesPanel |
| `ActionSetting.kt` | Add optional `buttonLabelProvider: (() -> String)? = null`; make `buttonLabel` a computed property |

No changes to `RoutesModule`, `WalkbackBridge`, `UIActionSetting`, or the UI panel system.
