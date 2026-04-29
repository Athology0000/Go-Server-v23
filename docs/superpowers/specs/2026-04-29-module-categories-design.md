# Module Categories Design

**Date:** 2026-04-29
**Status:** Approved

## Problem

The module list in `UIModuleList` is a flat unsorted list. As the number of modules grows (especially after per-slayer modules are added), there is no visual grouping. The user wants section separator headers in the left-side module list — e.g. a "Combat" header above Combat Macro and each slayer module.

## Approach: Category enum on Module (Approach B)

A `ModuleCategory` enum declares all categories in display order (enum ordinal). Each `Module` declares its own category via `open val category`. `UIModuleList` renders a `UICategoryHeader` separator before each non-empty group.

## Design

### `ModuleCategory` enum — `api/module/ModuleCategory.kt`

New file. Enum ordinal controls display order.

```kotlin
enum class ModuleCategory(val displayName: String) {
    COMBAT("Combat"),
    MINING("Mining"),
    VISUAL("Visual"),
    QOL("QoL"),
    OTHER("Other")
}
```

### `Module` base class — `api/module/Module.kt`

Add one field with a default so all existing modules continue to compile:

```kotlin
open val category: ModuleCategory = ModuleCategory.OTHER
```

Each module that needs a non-OTHER category overrides it:

```kotlin
override val category = ModuleCategory.COMBAT
```

### `UICategoryHeader` component — `internal/ui/components/UICategoryHeader.kt`

A lightweight `UIComponent` (height = 28f). Renders the category `displayName` as text using `ThemeManager.currentTheme.textMuted` (or equivalent dimmed color). Non-clickable — `mouseClicked` always returns false.

### `UIModuleList` changes

**`allItems: List<UIComponent>`** — built once at construction by grouping `allModules` by category in enum-ordinal order, inserting a `UICategoryHeader` before each non-empty group. Categories with zero modules are omitted.

**`modules: List<UIComponent>`** — what is actually displayed. Starts as `allItems`. On search, set to the flat matching `UIModule` list only (no headers), matching current behaviour.

**Layout** — `GridLayout` assumed uniform 40px height and is no longer suitable. Replace with a manual layout loop:

```
currentY = startY - scrollOffset
for item in modules:
    item.updateBounds(x + 20, currentY)
    currentY += item.height + gap
```

**Scroll content height** — replace `modulesLayout.contentHeight(modules.size)` with a sum of all item heights plus gaps.

**Click routing** — `UICategoryHeader` ignores clicks. `UIModule` click handling is unchanged.

## File Changes

| File | Change |
|------|--------|
| `api/module/ModuleCategory.kt` | New — enum with displayName |
| `api/module/Module.kt` | Add `open val category: ModuleCategory = ModuleCategory.OTHER` |
| `internal/ui/components/UICategoryHeader.kt` | New — 28px non-clickable label component |
| `internal/ui/panel/panels/UIModuleList.kt` | Build `allItems` with headers, variable-height layout, search hides headers |

## What Does Not Change

- `UIModule` component — no changes
- `GridLayout` — still used for the settings side
- Module registration in `Cobalt.kt` — flat list unchanged; categories come from each module's `category` field
- Addon-provided modules default to `OTHER` automatically
