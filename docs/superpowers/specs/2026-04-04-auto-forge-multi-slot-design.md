# Auto Forge — Multi-Slot Design

**Date:** 2026-04-04  
**Status:** Approved

## Overview

Extend `AutoForgeModule` to support up to 7 independently configured forge slots. Each slot has its own material assignment. The module watches the tablist for ready slots, then warps to a randomly chosen forge NPC route, claims ready slots, and re-queues each slot's configured material — all in one visit.

---

## Settings

### Existing (kept)
- `Enabled` — checkbox
- `Status` — read-only text display
- `NPC Name` — partial name of the forge NPC to interact with (default `"forger"`)
- `Auto Claim Ready` — claim ready slots before starting new ones
- `Close On Done` — close forge GUI after finishing

### Replaced
- `Route Name` (single text) → **5 route text settings**: `Route 1`–`Route 5`. Each tick a run is triggered, one of the non-blank routes is chosen at random.
- `Material` (global text) → **per-slot material** (see Slot Config below)

### New — Slot Config (7 groups, Slot 1–7)
Each group has:
- `Slot N Enabled` — checkbox (default false)
- `Slot N Material` — text, material keyword to forge in this slot (e.g. `"refined mithril"`, `"titanium"`)

---

## Tablist Parsing

New object `ForgeTablistParser`:

- Called each tick from `AutoForgeModule.onTick` while in `IDLE` state
- Reads `Minecraft.getInstance().gui.tabList` (player list entries)
- Matches lines containing `"forge"` + a digit + a status keyword:
  - `"ready"` / `"ready to collect"` / `"claim"` → slot is ready
  - `"empty"` → slot is empty
  - time pattern (e.g. `"3h 45m"`, `"45m"`) → slot is busy
- Returns `Map<Int, ForgeSlotStatus>` where key is slot number (1–7) and value is `READY`, `EMPTY`, or `BUSY`

Trigger condition: any slot N where `status == READY` AND `slotConfigs[N].enabled` AND `slotConfigs[N].material.isNotBlank()`.

---

## State Machine

States (updated):

```
IDLE → WAIT_ROUTE → OPEN_NPC → SELECT_SLOT → SELECT_RECIPE → CONFIRM_RECIPE → VERIFY_STARTED → COMPLETE
```

### IDLE
- Each tick: run `ForgeTablistParser`. If any configured+enabled slot is ready → pick a random non-blank route from Route 1–5 → start it → transition to `WAIT_ROUTE`.

### WAIT_ROUTE
- Same as existing: wait for `RoutesModule.isRunning` to go false → transition to `OPEN_NPC`.

### OPEN_NPC
- Same as existing: find NPC by `NPC Name`, walk to it, interact.

### SELECT_SLOT
- Iterate slot configs 1–7 in order. For each enabled config:
  - Find the Nth forge GUI slot by position (Nth non-navigation slot in `getForgeCandidateSlots`).
  - If **ready to claim** AND `Auto Claim Ready` is enabled → click it, mark `claimedThisRun[N] = true`, continue loop.
  - If **empty** (or just claimed) → click it → set `currentSlotConfigIndex = N` → transition to `SELECT_RECIPE`.
  - If **busy** → skip.
- After all 7 processed with no pending recipe → transition to `COMPLETE`.

### SELECT_RECIPE / CONFIRM_RECIPE / VERIFY_STARTED
- Same logic as existing, but material query comes from `slotConfigs[currentSlotConfigIndex].material` instead of the global `materialText` setting.
- After `VERIFY_STARTED` confirms success → transition back to `SELECT_SLOT` (forge GUI remains open; no NPC re-interaction needed) to continue iterating remaining slot configs.

### COMPLETE
- Close forge GUI if `Close On Done` is enabled → module goes idle (does not disable; stays enabled waiting for next tablist trigger).

---

## Runtime State

New runtime variables:
- `currentSlotConfigIndex: Int` — which slot config (1–7) is currently being processed
- `claimedThisRun: BooleanArray(7)` — tracks which slots were claimed this visit (reset each run)
- `processedSlots: BooleanArray(7)` — tracks which slots were fully handled this visit

Removed:
- `claimedReadyThisRun: Boolean` (replaced by per-slot tracking)

---

## Forge GUI Slot Mapping

The forge GUI presents forge slots as items in the container. `getForgeCandidateSlots` (existing) returns non-player-inventory slots in container order. Among these, forge-relevant slots are those whose lore/name contains one of: `"empty forge slot"`, `"time remaining"`, `"ready to collect"`, `"click to forge"`, `"claim item"`. Slot config N maps to the Nth forge-relevant item in this ordered list (1-indexed).

---

## Error Handling

- If a configured route name is blank or fails to start → skip it, try another random route.
- If all 5 route names are blank → disable with message "Configure at least one forge route."
- If no slot configs are enabled → disable with message "Enable at least one forge slot."
- Existing timeouts and error paths (NPC not found, screen mismatch, recipe not found) are unchanged.
- `COMPLETE` does not disable the module — it idles and waits for the next tablist-ready trigger.
