# Garden Pest HUD â€” Design Spec

**Date:** 2026-03-28

## Overview

A HUD element that displays real-time pest status for the Hypixel SkyBlock Garden. Shows pest cooldown countdown, alive pest count, infested plots, last-spawn timestamp, and farming bonus state. Visible in the Garden regardless of whether the Garden Macro is running.

Reference UI (the HUD to replicate):
- Plot: X/Y
- Curr Pest: X/Y
- Last pest spawned: X ago
- Pest Cooldown: Xs
- Farming Bonus: active/inactive
- X Pests Expire On Y+ (out of scope â€” data source unknown)

## Scope

**In scope:**
- `hudElement()` on a new `GardenHudModule` (or added to `GardenMacroModule` if appropriate)
- Real-time pest cooldown countdown (tick-driven, not just the raw tab-list value)
- Alive pest count and infested plot count/list from `PestTabListParser`
- "Last pest spawned" timestamp â€” track when `lastAliveCount` increases
- Internal cleaning cooldown remaining (from `PestManager.cleaningCooldownUntil`)
- Farming bonus indicator
- Only visible when the player is in the Garden (detected via scoreboard/tab list/island check)

**Out of scope:**
- Per-plot pest type breakdown
- Pest kill tracking / history
- Integration with macro start/stop

## Architecture

### Module: `GardenHudModule`

`object GardenHudModule : Module("Garden HUD")` â€” separate module so the HUD works without enabling the macro. Registered in `Phantom.onInitializeClient()`.

Subscribes to `TickEvent.Start` to poll `PestTabListParser` and update state. On each tick:
1. Call `PestTabListParser.parse()` â†’ `TabListData`
2. Update `lastAliveCount`, `cooldownSeconds`, `infestedPlots`, `bonusActive`
3. If `data.alivePests > previousAliveCount` â†’ record `lastSpawnedAt = System.currentTimeMillis()`
4. Derive a local `cooldownDeadline: Long` from `data.cooldownSeconds`: each tick when `cooldownSeconds > 0`, set `cooldownDeadline = now + cooldownSeconds * 1000`. Between ticks the HUD counts down in real time from this deadline.

### HUD Element

```kotlin
private const val ROW_HEIGHT = 14f
private const val FONT_SIZE  = 11f
private const val PADDING    = 8f

val pestHud = hudElement("garden-pest-hud", "Garden Pest HUD") {
    anchor = HudAnchor.TOP_LEFT
    width  { 180f }              // fixed width wide enough for all label+value pairs
    height { PADDING * 2 + ROW_HEIGHT * VISIBLE_ROW_COUNT }
    render { x, y, _ ->
        // draw rows of text with NVGRenderer.text(...)
    }
}
```

`VISIBLE_ROW_COUNT` is the number of non-zero rows shown (some rows hide when value is 0/inactive).

**Rows rendered (NVG text, left-aligned):**

| Label | Value | Color |
|---|---|---|
| Pest Cooldown | `Xs` countdown, `--` if 0 | Yellow if >0, green if 0 |
| Curr Pest | `X / threshold` | White |
| Infested Plots | `X plots` or list | White |
| Last Spawned | `Xs ago` or `never` | Gray |
| Farming Bonus | `Active` / `Inactive` | Green / Red |
| Internal CD | `Xs` remaining, hidden if 0 | Orange |

The countdown display for `Pest Cooldown` is computed from `cooldownDeadline - System.currentTimeMillis()` each frame (not each tick) so it counts down smoothly without waiting for the next tab-list parse.

### Garden Detection

Only render the HUD when in the Garden island. Detection: check tab list or scoreboard text contains `"Garden"` â€” same approach used by `PestTabListParser` (it already parses the tab list header/footer). Add a simple `isInGarden(): Boolean` helper that checks `TabOverlayAccessor.header` for the word "Garden".

### State Tracking (in `GardenHudModule`)

```kotlin
private var lastAliveCount     = 0
private var cooldownDeadline   = 0L   // System.currentTimeMillis() + remaining ms
private var lastSpawnedAt      = 0L   // epoch ms when last pest spawned
private var infestedPlots      = emptyList<String>()
private var bonusActive        = false
private var internalCdUntil    = 0L   // mirrors PestManager.cleaningCooldownUntil
```

`internalCdUntil` reads from `PestManager.cleaningCooldownUntil` each tick (already volatile).

### No New Data Sources Needed

All data comes from existing systems:
- `PestTabListParser.parse()` â€” alive count, tab-list cooldown, infested plots, bonus
- `PestManager.cleaningCooldownUntil` â€” internal post-clean cooldown
- Local timestamp tracking for "last spawned"

## File Layout

```
src/main/kotlin/org/phantom/internal/garden/
  GardenHudModule.kt     # New module: HUD element + tick-driven state updates
```

No new managers or mixins needed.

## Key Constraints

- `PestTabListParser.parse()` reads MC GUI state â€” must be called on the MC main thread (inside `TickEvent.Start` handler, which already runs on the game thread).
- The `hudElement` `render {}` block runs inside an NVG frame â€” no `GuiGraphics` calls there.
- The HUD should be a no-op (skip rendering entirely) when `isInGarden()` returns false.
