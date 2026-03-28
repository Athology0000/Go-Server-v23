# Wardrobe GUI — Design Spec

**Date:** 2026-03-28

## Overview

A custom wardrobe GUI module for Cobalt that replaces the vanilla Hypixel SkyBlock wardrobe inventory screen with a rich overlay. Visually and functionally identical to SkyHanni's `CustomWardrobe`, with one key difference: instead of following vanilla wardrobe pages 1–3, users can configure exactly which wardrobe sets appear on each of three custom pages via module settings.

Additional feature: automatic set labeling based on armor reforge prefix (Mossy→Farming, Mantid→Pest, Jaded→Mining).

## Scope

**In scope:**
- Intercept and replace the vanilla wardrobe container screen
- Parse all 27 wardrobe sets (3 vanilla pages × 9 slots) from inventory packets
- Render a fake 3D player per non-empty set using `InventoryScreen.renderEntityInInventory()`
- 3 configurable custom pages (TextSetting per page, comma-separated slot numbers)
- Slots not assigned to page 1 or 2 auto-fill page 3
- Auto-naming based on reforge prefix (hardcoded: Mossy→Farming, Mantid→Pest, Jaded→Mining)
- Equip a set by clicking it (navigates vanilla pages behind the scenes)
- Hover tooltips for each armor piece (via `GuiGraphics.renderTooltip()`)
- Favorite toggle (heart icon, persisted in config)
- Hide empty sets
- Back and Close buttons (click vanilla slots 48 and 49)

**Out of scope:**
- Drag-and-drop page editor
- Estimated item value display (can be added later)
- "Only favorites" filter toggle
- Global scale / spacing config (use SkyHanni defaults, hardcoded)

## Architecture

### Module: `WardrobeModule`

`object WardrobeModule : Module("Wardrobe GUI")` registered in `Cobalt.onInitializeClient()`.

**Settings:**

```kotlin
val enabled    by CheckboxSetting("Enabled", "Replace vanilla wardrobe with custom GUI", true)
val page1Slots by TextSetting("Page 1 Slots", "Comma-separated slot numbers (1–27)", "1")
val page2Slots by TextSetting("Page 2 Slots", "Comma-separated slot numbers (1–27)", "")
```

Page 3 is always the computed remainder — any non-empty set whose ID is not claimed by page 1 or 2 appears on page 3. No setting needed.

### Wardrobe State: `WardrobeState`

An internal singleton (object) that tracks:

```kotlin
object WardrobeState {
    var isOpen: Boolean                     // currently in a wardrobe inventory
    var currentVanillaPage: Int?            // which vanilla page (1–3) is loaded
    var equippedSlotId: Int?                // slot 1–27 that is currently equipped
    val sets: List<WardrobeSet>             // all 27 sets, populated from packets
    var favorites: Set<Int>                 // persisted set IDs marked as favorite
}

data class WardrobeSet(
    val id: Int,             // 1–27
    val vanillaPage: Int,    // 1–3 (which vanilla page it lives on)
    val inventorySlot: Int,  // slot index within the container to click
    val armor: List<ItemStack?>,  // [helmet, chestplate, leggings, boots], null = empty
    val locked: Boolean,
)
```

`WardrobeSet.isEmpty()` = all armor null.

**Auto-name logic** (computed, not stored):

```kotlin
fun WardrobeSet.displayName(): String {
    val reforge = armor.filterNotNull().firstOrNull()?.let { detectReforge(it) }
    return when (reforge) {
        "Mossy"  -> "Farming"
        "Mantid" -> "Pest"
        "Jaded"  -> "Mining"
        else     -> "Set $id"
    }
}
```

`detectReforge(stack)` reads the item's display name string (stripped of formatting codes) and returns the first word if it is one of `Mossy`, `Mantid`, or `Jaded`, otherwise null.

### Inventory Parsing

Listen to `PacketEvent.Incoming` for `ClientboundContainerSetContentPacket`. When `WardrobeState.isOpen` and the packet's containerId matches the open wardrobe container:

- Vanilla wardrobe layout:
  - Slots 0–8: helmets for sets 1–9 on the current page
  - Slots 9–17: chestplates
  - Slots 18–26: leggings
  - Slots 27–35: boots
  - Slots 36–44: clickable set slots (click these to equip)
  - Slot 48: Back button
  - Slot 49: Close button
- Parse armor from slots 0–35 for the current vanilla page's 9 sets
- Detect equipped slot by checking slot 36–44 item names for "§aEquipped"
- Detect locked by checking for red stained glass pane in slots 36–44

To get all 27 sets, the module automatically cycles vanilla pages on open (clicking slot 53 "next" twice) while the overlay is loading, just as SkyHanni does. A `waitingForData` flag suppresses rendering until all 3 vanilla pages have been scanned.

### Screen Detection

Listen to `PacketEvent.Incoming` for `ClientboundOpenScreenPacket`. If title matches `Wardrobe (\d+/\d+)`:
- Set `WardrobeState.isOpen = true`
- Parse current page number from the regex group

On `GuiRenderEvent` (which fires for the HUD), if `mc.screen` is a wardrobe `AbstractContainerScreen` and the module is enabled, draw the overlay and cancel the vanilla render via a flag checked in the Mixin.

### Rendering

**Mixin:** `WardrobeScreenMixin` on `AbstractContainerScreen`:
- `@Inject` into `render()` at `HEAD` with a cancellable `CallbackInfo`
- If `WardrobeModule.shouldSuppressVanillaRender()` returns true, cancel

**Render pipeline** (in `GuiRenderEvent` handler):

1. Begin NVG frame
2. Draw background panel (rounded rect, `ThemeManager.currentTheme.panel`)
3. For each non-empty set on the current custom page:
   a. Draw slot card background (NVG rounded rect, accent border if equipped)
   b. Call `InventoryScreen.renderEntityInInventory(graphics, x, y, scale, mouseX, mouseY, fakePlayer)` with a `FakePlayer` wearing the set's armor
   c. Draw set label below (NVG text: `displayName()`)
   d. Draw "Equipped" badge if `id == equippedSlotId`
   e. Draw favorite heart (NVG text, clickable hitbox)
4. Draw page tab bar (3 tabs, NVG rects + text)
5. Draw Back and Close buttons (NVG rounded rects)
6. On hover over a slot: collect armor tooltips and render via `GuiGraphics.renderTooltip()` in `postRender`
7. End NVG frame

**FakePlayer creation:** Cobalt does not have a `FakePlayer` class. We create one: a `WardrobeFakePlayer` that extends `AbstractClientPlayer` (or wraps a bare `Player` entity constructed against `mc.level`), never added to the level. One instance per `WardrobeSet`, lazily constructed. Armor is applied via `entity.equipment.set(EquipmentSlot, stack)`. Reused across frames; recreated only when the set's armor changes. This is identical in approach to SkyHanni's `FakePlayer.kt`.

### Click Handling

Track mouse clicks via `MouseEvent` (or read `mc.mouseHandler.xpos()` / `ypos()` in the click handler). Build a hitbox list each render frame; test on click.

Mouse coordinates from `GuiRenderEvent` are not directly available — use `mc.mouseHandler` for current cursor position.

- **Slot click:** if set is on current vanilla page, call `mc.gameMode.handleInventoryMouseClick(containerId, inventorySlot, 0, PICKUP, player)`. If on a different vanilla page, navigate first (click slot 45 "prev" or slot 53 "next"), set `waitingForPageLoad = true`, queue the target slot click for when the next `ContainerSetContent` arrives.
- **Page tab click:** set `currentCustomPage` (local to module), re-render.
- **Favorite click:** toggle `WardrobeState.favorites`, save config.
- **Back click:** `handleInventoryMouseClick(containerId, 48, ...)`, reset state.
- **Close click:** `handleInventoryMouseClick(containerId, 49, ...)`, reset state.

### Config Persistence

Favorites (`Set<Int>`) and page slot assignments are serialized through Cobalt's existing config system (`Config.saveModulesConfig()`). The TextSettings persist automatically. Favorites need a dedicated config field in the module or companion object.

## Data Flow

```
PacketEvent.Incoming (OpenScreen)
  → WardrobeState.isOpen = true, parse page

PacketEvent.Incoming (ContainerSetContent)
  → parse armor for current vanilla page
  → if auto-scanning: advance vanilla page, repeat until all 3 pages scanned

GuiRenderEvent (every frame while wardrobe screen open)
  → WardrobeScreenMixin suppresses vanilla render
  → WardrobeModule.render() draws full custom overlay

Mouse click
  → hit-test against frame's slot hitboxes
  → equip / favorite / navigate
```

## File Layout

```
src/main/kotlin/org/cobalt/internal/wardrobe/
  WardrobeModule.kt          # Module, settings, render entry point, click handling
  WardrobeState.kt           # Parsed state singleton (sets, equipped, favorites)
  WardrobeRenderer.kt        # All NVG + GuiGraphics drawing logic
  WardrobePageConfig.kt      # Page slot assignment parsing from TextSettings
  WardrobeFakePlayer.kt      # Fake player entity for InventoryScreen.renderEntityInInventory()

src/main/java/org/cobalt/mixin/client/
  WardrobeScreenMixin.java   # Suppresses vanilla AbstractContainerScreen render
```

## Key Constraints

- `InventoryScreen.renderEntityInInventory()` must be called outside NVG frame (`endFrame()` before, `beginFrame()` after if mixing NVG and entity rendering in the same pass — same pattern used by `InventoryHudModule` mixing NVG + `GuiGraphics`).
- Vanilla page navigation during the initial scan must happen on the MC main thread (`mc.execute {}`), same pattern as `WardrobeManager.swapTo()`.
- The module must register with `EventBus.register(this)` in its `init {}` block.
