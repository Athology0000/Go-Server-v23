# Mining: Anchor Radius + Step-Closer Fix & Profit Tracker

**Date:** 2026-04-09

---

## 1. Anchor Radius & Step-Closer Fix

### Problem

Two bugs prevent the macro from stepping closer to out-of-range blocks:

1. **`target == null` branch bug** — when all vein blocks lack LOS, the code finds the nearest block via `selectNearestBlock` but only approaches it if it's within `shortStepLimit` (~5 blocks). If farther, it falls to `else -> MovementManager.clearForcedMovement()` — doing nothing, even when pathfinding is enabled.

2. **`NEARBY_STEP_MAX_DRIFT = 3.25` too small** — this hardcoded constant is used in both `hasDriftedFromVeinStart` and `canStepToNearbyTarget`. At 3.25 blocks the player is considered "drifted" before it can even reach many targets, causing a constant return-to-anchor loop.

### Design

**New setting on `MiningMacroModule`:**
- `Anchor Radius` `SliderSetting`, default `14.0`, range `4.0–48.0`
- Same defaults as `AutoLanternModule`'s "Reuse Distance" — the idea being the macro stays within the lantern coverage area

**Replace `NEARBY_STEP_MAX_DRIFT`:**
- Remove the constant; use `anchorRadius.value` everywhere it appeared
- `hasDriftedFromVeinStart`: player drift threshold → `anchorRadius.value`
- `canStepToNearbyTarget`: player drift threshold → `anchorRadius.value`; max target-from-anchor distance → `anchorRadius.value + mineRange.value`

**Fix `target == null` else-branch** (`MiningMacroModule.kt` ~line 616):
```
} else {
  if (usePathfinding.value && canStepToNearbyTarget(player, nearest)) {
    moveToward(level, player, nearest)
  } else {
    MovementManager.clearForcedMovement()
  }
}
```

**Routes exception:**
- Add `val routeOwnsMining: Boolean get() = routeOwnsMiningMacro` to `RoutesModule`
- In `hasDriftedFromVeinStart`: if `RoutesModule.isRunning && RoutesModule.routeOwnsMining`, return `false` — routes dictates movement, anchor constraint does not apply

---

## 2. Profit Tracker in Mining HUD

### Design

**New file: `MiningProfitTracker.kt`**

Modeled after `ProfitManager` from garden:
- `sessionStartTime: Long` — set on `resetSession()`
- `sessionCoins: Long` — accumulated coin value from loot
- `bazaarPrices: MutableMap<String, Double>` — refreshed from Hypixel API
- `resetSession()` — zeroes coins, records start time
- `onChatMessage(message: String)` — parses `+N ItemName` patterns, looks up bazaar price, calls `addCoins()`
- `coinsPerHour(): Long` — `sessionCoins / elapsedHours`
- `runtimeMs(): Long` — `now - sessionStartTime`
- `refreshBazaarIfNeeded()` — same Hypixel bazaar endpoint as `ProfitManager`
- Registered with `EventBus` for `ChatEvent.Receive`

**Reset triggers:**
- `MiningMacroModule.startForAutomation()` and on `wasEnabled` flip → call `MiningProfitTracker.resetSession()`
- `CommissionMacroModule` on start → call `MiningProfitTracker.resetSession()`

**HUD changes in `MiningHudModule`:**

New section added at the bottom of the existing panel, gated by a `Show Profit` `CheckboxSetting` (default `true`):

```
─────────────────
MACRO
Active: Mining Macro          (or "Commission Macro", "Routes", "—")
Coins/hr: 45.2K
Runtime: 01:23:45
```

- `panelHeight()` and `panelWidth()` updated to account for this section
- Active macro label resolved from `MiningMacroModule.isActive`, `CommissionMacroModule.isRunning`, `RoutesModule.isRunning` — first match wins
- Coin formatting: same `formatProfit` style as `GardenHud` (K/M suffixes)
- Runtime formatting: `HH:MM:SS` when >= 1 hour, `MM:SS` otherwise

---

## Files Changed

| File | Change |
|------|--------|
| `MiningMacroModule.kt` | Add `anchorRadius` setting, replace `NEARBY_STEP_MAX_DRIFT`, fix else-branch, call `MiningProfitTracker.resetSession()` on start |
| `RoutesModule.kt` | Expose `routeOwnsMining: Boolean` getter |
| `MiningHudModule.kt` | Add profit section, `Show Profit` setting, update height/width |
| `CommissionMacroModule.kt` | Call `MiningProfitTracker.resetSession()` on start |
| `MiningProfitTracker.kt` | **New file** — session tracking, bazaar fetch, chat parsing |
| `Cobalt.kt` | Register `MiningProfitTracker` if it needs EventBus registration |
