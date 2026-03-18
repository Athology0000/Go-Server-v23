# Garden Macro — Design Spec
**Date:** 2026-03-18
**Status:** Approved

---

## Overview

Port all 22 feature managers from ihanuat into Cobalt as a single `GardenMacroModule`, using Cobalt's existing UI systems (NanoVG, Module/Setting DSL, hudElement() DSL). External script commands (ez-macrostart, etc.) are used for now via a thin `ScriptBridge` abstraction — designed so swapping to standalone only requires changes to `ScriptBridge`, not individual managers.

**Excluded from port:** ihanuat's `RotationManager` — Cobalt's existing `RotationExecutor` covers this entirely.

---

## Architecture

### Pattern: Module + Manager

`GardenMacroModule` is a Cobalt `object` extending `Module("Garden Macro")`. It owns:
- All settings (organized with `InfoSetting` section headers)
- The state machine (`GardenState`)
- The combined HUD element (inline `hudElement()` block, rendering delegated to `GardenHud`)
- EventBus registration and event routing to managers

Each ihanuat manager becomes a Kotlin `object` singleton in `internal/garden/managers/`. Managers are **stateful singletons**: they hold `@Volatile var` state fields and expose a `reset()` that clears all state. Managers do **not** register with `EventBus`. They are driven entirely by `GardenMacroModule` calling `update(mc)` (from `TickEvent.End`) and `handleScreen(mc, screen)` (from `TickEvent.End`, using `mc.screen`).

---

## File Structure

```
src/main/kotlin/org/cobalt/internal/garden/
  GardenMacroModule.kt     # Central module, settings, state machine, event routing
  GardenState.kt           # State enum
  GardenWorkerThread.kt    # Serial async task queue
  ScriptBridge.kt          # Abstraction over external script commands
  GardenHud.kt             # NVG rendering logic, called from hudElement() render block
  managers/
    PestManager.kt
    PestTabListParser.kt
    PestCleaningSequencer.kt
    PestAotvManager.kt
    PestPrepSwapManager.kt
    PestReturnManager.kt
    PestBonusManager.kt
    CropFeverManager.kt
    VisitorManager.kt
    WardrobeManager.kt
    EquipmentManager.kt
    GearManager.kt
    RodManager.kt
    GeorgeManager.kt
    BookCombineManager.kt
    JunkManager.kt
    BoosterCookieManager.kt
    ProfitManager.kt
    PetXpTracker.kt
    DynamicRestManager.kt
    RecoveryManager.kt
    RestartManager.kt

src/main/java/org/cobalt/mixin/client/
  GardenInventoryAccessor.java       # @Accessor for inventory slots
  GardenTabOverlayAccessor.java      # @Accessor for tab list entries
```

**Mixin registration:** All classes placed under `org.cobalt.mixin` are auto-discovered by `MixinAutoDiscover`. No manual edits to `cobalt.mixins.json`.

**Mouse suppression:** No new mixin needed. Set `MovementManager.isLookLocked = true` from `GardenMacroModule` during rotation sequences. The existing `MouseHandlerMixin` already cancels `turnPlayer()` when this flag is true.

**Disconnect detection:** No new mixin needed. `TickEvent.End` checks `mc.connection == null` when state is active. This is the sole mechanism.

**Chat spam filtering:** No new mixin needed. `GardenMacroModule` handles `ChatEvent.Receive` (already fired by the existing `ConnectionMixin` for every incoming chat packet). When `hideFilteredChat.value` is true, matching messages are cancelled before display — no bytecode injection required.

---

## State Machine

```
OFF
 └─► FARMING        (script running, tick loop active)
      ├─► CLEANING     (pest threshold hit → stop script → gear swap → pest script → return)
      ├─► VISITING     (visitor detected → visitor script → return)
      ├─► AUTOSELLING  (George/book/junk trigger → sell/drop → return to FARMING)
      ├─► RESTING      (rest timer expired → /setspawn → disconnect → reconnect after break)
      └─► RECOVERING   (unexpected disconnect detected → warp garden → restart script)
```

**Start/stop:** The module uses a `CheckboxSetting("Enabled", ...)` (same pattern as `CombatMacroModule`). `TickEvent.End` checks `enabled.value` before running any logic. When `enabled` transitions false→true, all managers are reset and the farming sequence starts.

**State transitions:** Managed exclusively by `GardenMacroModule.setState(GardenState)`. Since `GardenWorkerThread` tasks run off the main thread, `setState()` always posts via `Minecraft.getInstance().execute {}` before updating state.

**AUTOSELLING coordination:** `GeorgeManager`, `BookCombineManager`, and `JunkManager` are checked **in that priority order** within `TickEvent.End`. `GardenMacroModule` holds a single `@Volatile var autosellingManager: String? = null`. The first manager whose trigger condition is true sets this field to its name and calls `setState(AUTOSELLING)`. Subsequent managers skip if `autosellingManager != null`. After completing, the manager clears the field and calls `setState(FARMING)`. This serializes all three managers through a single gating variable with no race condition.

**Disconnect detection:** In `TickEvent.End`, when state is FARMING/CLEANING/VISITING/AUTOSELLING, check `mc.connection == null`. If true, call `setState(RECOVERING)`. No additional mixin required; `GardenClientConnectionMixin` is a fallback that fires on the incoming `ClientboundDisconnectPacket` for faster detection.

---

## Settings

Section headers use `InfoSetting("Section Name", "", InfoType.INFO)` — the same pattern used in `CombatMacroModule`. All settings are registered in `init {}` via `addSetting(...)`.

| Section | Setting | Type | Default |
|---|---|---|---|
| **General** | Enabled | `CheckboxSetting("Enabled", "Run the garden macro.", false)` | false |
| | Farm script name | `TextSetting("Farm Script", "Script name to run for farming.", "farm")` | `"farm"` |
| | Pest script name | `TextSetting("Pest Script", "Script name to run for pest cleaning.", "pest")` | `"pest"` |
| | Return script name | `TextSetting("Return Script", "Script name to run after pest.", "return")` | `"return"` |
| | Visitor script name | `TextSetting("Visitor Script", "Script name to run for visitors.", "visitor")` | `"visitor"` |
| **Pest** | Pest threshold | `SliderSetting("Pest Threshold", "Alive pest count to trigger cleaning.", 4.0, 1.0, 8.0)` | 4 |
| | AOTV enabled | `CheckboxSetting("AOTV to Roof", "Teleport to roof before pest clean.", false)` | false |
| | AOTV plot whitelist | `TextSetting("AOTV Plots", "Comma-separated plot names for AOTV.", "")` | `""` |
| | Prep-swap enabled | `CheckboxSetting("Prep Swap", "Swap gear when pest count nears threshold.", false)` | false |
| | Roof pitch angle | `SliderSetting("Roof Pitch", "Camera pitch when teleporting to roof.", -80.0, -90.0, 90.0)` | -80 |
| **Visitor** | Auto-accept visitors | `CheckboxSetting("Auto Visitor", "Automatically handle visitor offers.", false)` | false |
| **Wardrobe** | Farming slot | `SliderSetting("Farming Slot", "Wardrobe slot for farming loadout.", 1.0, 1.0, 18.0)` | 1 |
| | Pest slot | `SliderSetting("Pest Slot", "Wardrobe slot for pest loadout.", 2.0, 1.0, 18.0)` | 2 |
| | Visitor slot | `SliderSetting("Visitor Slot", "Wardrobe slot for visitor loadout.", 3.0, 1.0, 18.0)` | 3 |
| | Auto-swap wardrobe | `CheckboxSetting("Auto Wardrobe", "Swap wardrobe slots automatically.", false)` | false |
| **Equipment** | Farming armor | `TextSetting("Farming Armor", "Armor set name for farming.", "")` | `""` |
| | Pest armor | `TextSetting("Pest Armor", "Armor set name for pest cleaning.", "")` | `""` |
| | Visitor armor | `TextSetting("Visitor Armor", "Armor set name for visitors.", "")` | `""` |
| | Swap delay (ms) | `SliderSetting("Swap Delay", "Delay between equipment swaps in ms.", 300.0, 0.0, 2000.0)` | 300 |
| **Economy** | Bazaar refresh interval | `SliderSetting("Bazaar Refresh", "Seconds between Bazaar price updates.", 120.0, 30.0, 600.0)` | 120 |
| | George sell threshold | `SliderSetting("George Threshold", "Sell pets to George above this profit.", 100000.0, 0.0, 10000000.0)` | 100000 |
| | George pet rarity | `TextSetting("George Rarity", "Comma-separated rarities to sell (e.g. LEGENDARY).", "LEGENDARY")` | `"LEGENDARY"` |
| | Book combine level | `SliderSetting("Book Level", "Combine books at this enchantment level.", 5.0, 1.0, 10.0)` | 5 |
| | Junk items | `TextSetting("Junk Items", "Comma-separated item names to auto-drop.", "")` | `""` |
| | Booster cookie item | `TextSetting("Cookie Item", "Item name to use booster cookies on.", "")` | `""` |
| **Rest** | Work duration (min) | `SliderSetting("Work Duration", "Minutes to farm before resting.", 60.0, 1.0, 240.0)` | 60 |
| | Work offset (min) | `SliderSetting("Work Offset", "Random offset added to work duration.", 5.0, 0.0, 30.0)` | 5 |
| | Break duration (min) | `SliderSetting("Break Duration", "Minutes to rest before resuming.", 10.0, 1.0, 60.0)` | 10 |
| | Break offset (min) | `SliderSetting("Break Offset", "Random offset added to break duration.", 2.0, 0.0, 15.0)` | 2 |
| **Advanced** | Hide filtered chat | `CheckboxSetting("Hide Chat Spam", "Filter pet kill and script messages.", false)` | false |
| | Max recovery attempts | `SliderSetting("Max Recovery", "Max auto-reconnect attempts.", 15.0, 1.0, 30.0)` | 15 |
| | Reconnect delay min | `SliderSetting("Reconnect Min", "Min seconds before reconnecting.", 30.0, 5.0, 120.0)` | 30 |
| | Reconnect delay max | `SliderSetting("Reconnect Max", "Max seconds before reconnecting.", 60.0, 5.0, 120.0)` | 60 |
| | Debug logging | `CheckboxSetting("Debug", "Print debug messages to chat.", false)` | false |

---

## Combined HUD Panel

Defined inline via `hudElement("garden-hud", "Garden HUD")` on `GardenMacroModule`. The render lambda calls `GardenHud.render(x, y, scale, ...)`, keeping the DSL block clean.

```
┌─────────────────────────────┐
│  GARDEN MACRO  [FARMING]    │  ← title + state badge (color-coded)
│  Runtime: 02:34:17          │  ← session uptime
│  Next Rest: 18:42  [====--] │  ← countdown + filled progress bar
├─────────────────────────────┤
│  Profit                     │
│  Session:  +1,234,567g      │
│  Daily:    +8,912,345g      │
│  Lifetime: +42,000,000g     │
└─────────────────────────────┘
```

**State badge colors:** FARMING=green, CLEANING=yellow, VISITING=cyan, AUTOSELLING=orange, RESTING=gray, RECOVERING=red, OFF=dark gray.

Uses `ThemeManager.currentTheme` for panel/text/overlay base colors.

HUD settings (inside hudElement block via `setting()`):
- `showProfit = setting(CheckboxSetting("Show Profit", "", true))`
- `showRest = setting(CheckboxSetting("Show Rest", "", true))`
- `scale = setting(SliderSetting("Scale", "", 1.0, 0.5, 2.0))`

---

## Event Routing

`GardenMacroModule` calls `EventBus.register(this)` in `init {}`. Handlers:

| Event | Action |
|---|---|
| `TickEvent.End` | If `enabled.value`: check disconnect (`mc.connection == null`), call all manager `update(mc)` in order, call `handleScreen(mc, mc.screen)` for GUI managers, check rest timer |
| `ChatEvent.Receive` | Route to `RestartManager`, `VisitorManager`, `ProfitManager`, `RecoveryManager` |

**Note:** There is no `GuiRenderEvent` screen reference. GUI screen handling happens in `TickEvent.End` using `mc.screen`.

---

## GardenWorkerThread

A serial queue backed by a single daemon `Thread`. API:

```kotlin
object GardenWorkerThread {
    fun submit(name: String, block: () -> Unit)
    fun shutdown()          // interrupts current task, drains queue, joins thread
    private fun ensureRunning()  // called by submit(); creates new thread if not alive
}
```

- `submit()` calls `ensureRunning()` before adding to queue — safe to call after re-enable.
- `shutdown()` sets an interrupt flag, clears the queue, and joins the thread.
- Blocking `Thread.sleep()` is permitted inside tasks.
- Minecraft API calls inside tasks must use `Minecraft.getInstance().execute {}`.

---

## ScriptBridge

All external commands go through `ScriptBridge` using `mc.player.connection.sendCommand(cmd)` (no leading slash). No manager calls this directly.

```kotlin
object ScriptBridge {
    private val mc get() = Minecraft.getInstance()
    fun startFarming(script: String) = mc.player?.connection?.sendCommand("ez-macrostart $script")
    fun startPestCleaning(script: String) = mc.player?.connection?.sendCommand("ez-macrostart $script")
    fun startVisitorScript(script: String) = mc.player?.connection?.sendCommand("ez-macrostart $script")
    fun stopScript() = mc.player?.connection?.sendCommand("ez-macrostop")
    fun setSpawn() = mc.player?.connection?.sendCommand("setspawn")
    fun warpGarden() = mc.player?.connection?.sendCommand("warp garden")
}
```

**Future standalone:** Replace each method body with internal pathfinding/automation. No other files change.

---

## Mixin Specifications

### GardenInventoryAccessor.java
**Location:** `org.cobalt.mixin.client`
**Target:** `net.minecraft.world.entity.player.Inventory`
**Type:** `@Mixin` + `@Accessor`
**Exposes:** `getItems()` returning the `NonNullList<ItemStack>` items field (Mojang mapped: `items`).

### GardenTabOverlayAccessor.java
**Location:** `org.cobalt.mixin.client`
**Target:** `net.minecraft.client.gui.components.PlayerTabOverlay`
**Type:** `@Mixin` + `@Accessor`
**Exposes:**
- `getHeader()` → `Component` (Mojang mapped field: `header`)
- `getFooter()` → `Component` (Mojang mapped field: `footer`)
- `getPlayerInfoMap()` → `Map<UUID, PlayerInfo>` (Mojang mapped field: `playerInfo`)

`PestTabListParser` checks all three: Hypixel may embed pest counts in the footer text, header text, or player-row display names depending on the server version. The parser strips formatting codes and regex-matches for pest count patterns across all three sources.

---

## ProfitManager — Bazaar HTTP

Network calls run inside `GardenWorkerThread` tasks (not on the main thread). The result is applied back via `Minecraft.getInstance().execute {}`:

```kotlin
GardenWorkerThread.submit("bazaar-refresh") {
    val json = URL("https://api.hypixel.net/skyblock/bazaar").readText()
    val prices = parseBazaarJson(json)
    Minecraft.getInstance().execute { applyPrices(prices) }
}
```

No additional HTTP libraries needed — Kotlin's `URL.readText()` is sufficient.

---

## Registration

`GardenMacroModule` is added to `Cobalt.onInitializeClient()` alongside other internal modules. The four new mixin classes in `org.cobalt.mixin.client` are auto-discovered by `MixinAutoDiscover`.

---

## Future: Standalone Mode

Replace each `ScriptBridge` method body with internal pathfinding/automation logic. No changes to `GardenMacroModule` or any manager.

---

## Out of Scope

- Cloth Config UI — replaced by Cobalt's NVG settings panel
- `ConfigScreenFactory` / `DynamicRestScreen` — replaced by Cobalt's existing UI + HUD
- ihanuat's `RotationManager` — Cobalt's `RotationExecutor` handles this
- `MacroConfig` persistence — handled by Cobalt's `Config.saveModulesConfig()`
