# Slayer Walkback Route Picker Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the single shared `slayerWalkbackRoute` text input with per-slayer-type walkback route selectors backed by a styled route picker popup, and fix walkback behavior so it triggers on death, area exit, and server change for all slayer types.

**Architecture:** `ActionSetting` gains an optional dynamic label provider. A new `WalkbackRoutePickerPopup` object (modeled on `UICommissionRoutesPanel`) renders a full-screen modal route list and writes the selection into a per-type `TextSetting`. `CombatMacroModule` is updated with 6 backing settings + 6 action buttons, a helper to look up the current-type setting, and extended area-tracking logic.

**Tech Stack:** Kotlin, NanoVG (`NVGRenderer`), Phantom event bus (`@SubscribeEvent`), `RoutesModule.getSavedRouteInfos()`, `ScrollHandler`, `ThemeManager`

---

## File Map

| File | Action |
|------|--------|
| `src/main/kotlin/org/phantom/api/module/setting/impl/ActionSetting.kt` | Modify â€” add optional `buttonLabelProvider` |
| `src/main/kotlin/org/phantom/internal/ui/hud/WalkbackRoutePickerPopup.kt` | **Create** â€” modal route picker popup |
| `src/main/kotlin/org/phantom/internal/combat/CombatMacroModule.kt` | Modify â€” replace shared setting, add per-type settings/buttons, behavior fixes |

---

## Task 1: Extend ActionSetting with dynamic button label

**Files:**
- Modify: `src/main/kotlin/org/phantom/api/module/setting/impl/ActionSetting.kt`

- [ ] **Step 1: Replace file contents**

Replace the entire file with:

```kotlin
package org.phantom.api.module.setting.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.phantom.api.module.setting.Setting

/**
 * Action button setting. Not persisted; used to trigger an action from the UI.
 * If [buttonLabelProvider] is non-null, the button label is computed dynamically each frame.
 */
class ActionSetting(
  name: String,
  description: String,
  staticLabel: String,
  private val onClick: () -> Unit,
  private val buttonLabelProvider: (() -> String)? = null,
) : Setting<Boolean>(name, description, false) {

  private val _staticLabel = staticLabel
  val buttonLabel: String get() = buttonLabelProvider?.invoke() ?: _staticLabel

  override val defaultValue: Boolean = false

  override fun read(element: JsonElement) {
    // No persisted state.
  }

  override fun write(): JsonElement {
    return JsonPrimitive(false)
  }

  fun trigger() {
    onClick()
  }
}
```

- [ ] **Step 2: Verify the build still compiles**

Run: `./gradlew compileKotlin`
Expected: `BUILD SUCCESSFUL` (all existing `ActionSetting(...)` callers use positional args so the param rename from `buttonLabel` to `staticLabel` is transparent)

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/phantom/api/module/setting/impl/ActionSetting.kt
git commit -m "feat: add optional dynamic buttonLabelProvider to ActionSetting"
```

---

## Task 2: Create WalkbackRoutePickerPopup

**Files:**
- Create: `src/main/kotlin/org/phantom/internal/ui/hud/WalkbackRoutePickerPopup.kt`

- [ ] **Step 1: Create the file**

```kotlin
package org.phantom.internal.ui.hud

import java.awt.Color
import net.minecraft.client.Minecraft
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.MouseEvent
import org.phantom.api.event.impl.render.NvgEvent
import org.phantom.api.module.setting.impl.TextSetting
import org.phantom.api.ui.theme.ThemeManager
import org.phantom.api.util.ui.NVGRenderer
import org.phantom.internal.mining.RoutesModule
import org.phantom.internal.ui.components.settings.UICheckboxSetting
import org.phantom.internal.ui.util.ScrollHandler
import org.phantom.internal.ui.util.isHoveringOver
import org.phantom.internal.ui.util.mouseX
import org.phantom.internal.ui.util.mouseY

internal object WalkbackRoutePickerPopup {

  private val mc = Minecraft.getInstance()
  private var visible = false
  private var slayerTypeName = ""
  private var targetSetting: TextSetting? = null
  private var routes = emptyList<RoutesModule.SavedRouteInfo>()
  private val scroll = ScrollHandler()

  private const val PANEL_W = 320f
  private const val PANEL_H = 500f
  private const val PAD = 16f
  private const val ROW_H = 52f
  private const val ROW_GAP = 8f
  private const val CHECKBOX = 16f
  private const val EMPTY_STATE_H = 76f

  private var panelX = 0f
  private var panelY = 0f

  fun open(typeName: String, setting: TextSetting) {
    slayerTypeName = typeName
    targetSetting = setting
    routes = RoutesModule.getSavedRouteInfos()
    scroll.setMaxScroll(0f, 0f) // reset scroll
    visible = true
  }

  fun isVisible(): Boolean = visible

  private fun close() {
    visible = false
    targetSetting = null
  }

  @SubscribeEvent
  fun onRender(@Suppress("UNUSED_PARAMETER") event: NvgEvent) {
    if (!visible) return

    val window = mc.window
    val sw = window.screenWidth.toFloat()
    val sh = window.screenHeight.toFloat()
    panelX = sw / 2f - PANEL_W / 2f
    panelY = sh / 2f - PANEL_H / 2f

    NVGRenderer.beginFrame(sw, sh)

    // Overlay
    NVGRenderer.rect(0f, 0f, sw, sh, Color(0, 0, 0, 120).rgb)

    val theme = ThemeManager.currentTheme

    // Panel background
    NVGRenderer.rect(panelX, panelY, PANEL_W, PANEL_H, theme.background, 10f)
    NVGRenderer.hollowRect(panelX, panelY, PANEL_W, PANEL_H, 1f, theme.controlBorder, 10f)

    // Title
    NVGRenderer.text("Walkback Route â€” $slayerTypeName", panelX + PAD, panelY + 22f, 13f, theme.text)

    // Subtitle
    NVGRenderer.text(
      "Select a route to follow to walk back to the area if killed or teleported away.",
      panelX + PAD,
      panelY + 40f,
      9f,
      theme.textSecondary
    )

    // Top buttons
    renderButton("Refresh", refreshBounds())
    renderButton("Clear", clearBounds())

    // Route list header
    NVGRenderer.text("Saved Routes", panelX + PAD, routeHeaderY() - 2f, 11f, theme.text)

    // Route list (scrollable)
    val contentY = routeListTop()
    val visibleH = panelY + PANEL_H - contentY - PAD
    val totalH = if (routes.isEmpty()) EMPTY_STATE_H else routes.size * (ROW_H + ROW_GAP)
    scroll.setMaxScroll(totalH, visibleH)

    NVGRenderer.pushScissor(panelX + PAD, contentY, PANEL_W - PAD * 2f, visibleH)
    if (routes.isEmpty()) {
      renderEmptyState(contentY)
    } else {
      val offset = scroll.getOffset()
      routes.forEachIndexed { i, route ->
        renderRouteRow(route, contentY + i * (ROW_H + ROW_GAP) - offset)
      }
    }
    NVGRenderer.popScissor()

    NVGRenderer.endFrame()
  }

  @SubscribeEvent
  fun onMouseLeft(event: MouseEvent.LeftClick) {
    if (!visible) return
    event.setCancelled(true)

    val mx = mouseX.toFloat()
    val my = mouseY.toFloat()

    // Refresh button
    val rb = refreshBounds()
    if (mx >= rb[0] && mx <= rb[0] + rb[2] && my >= rb[1] && my <= rb[1] + rb[3]) {
      routes = RoutesModule.getSavedRouteInfos()
      return
    }

    // Clear button
    val cb = clearBounds()
    if (mx >= cb[0] && mx <= cb[0] + cb[2] && my >= cb[1] && my <= cb[1] + cb[3]) {
      targetSetting?.value = ""
      close()
      return
    }

    // Route rows
    if (!routes.isEmpty()) {
      val contentY = routeListTop()
      val offset = scroll.getOffset()
      routes.forEachIndexed { i, route ->
        val rowY = contentY + i * (ROW_H + ROW_GAP) - offset
        if (isHoveringOver(panelX + PAD, rowY, PANEL_W - PAD * 2f, ROW_H)) {
          targetSetting?.value = route.name
          close()
          return
        }
      }
    }

    // Click outside panel closes without change
    if (!isHoveringOver(panelX, panelY, PANEL_W, PANEL_H)) {
      close()
    }
  }

  @SubscribeEvent
  fun onMouseRight(@Suppress("UNUSED_PARAMETER") event: MouseEvent.RightClick) {
    if (!visible) return
    event.setCancelled(true)
    close()
  }

  @SubscribeEvent
  fun onMouseScroll(event: MouseEvent.Scroll) {
    if (!visible) return
    if (isHoveringOver(panelX, panelY, PANEL_W, PANEL_H)) {
      scroll.handleScroll(event.verticalAmount)
    }
  }

  // ---- rendering helpers ----

  private fun renderButton(label: String, bounds: FloatArray) {
    val theme = ThemeManager.currentTheme
    val bx = bounds[0]; val by = bounds[1]; val bw = bounds[2]; val bh = bounds[3]
    val hovering = isHoveringOver(bx, by, bw, bh)
    NVGRenderer.rect(bx, by, bw, bh, if (hovering) theme.selectedOverlay else theme.controlBg, 6f)
    NVGRenderer.hollowRect(bx, by, bw, bh, 1f, theme.controlBorder, 6f)
    val tw = NVGRenderer.textWidth(label, 10f)
    NVGRenderer.text(label, bx + (bw - tw) / 2f, by + 6f, 10f, theme.text)
  }

  private fun renderEmptyState(contentY: Float) {
    val theme = ThemeManager.currentTheme
    val bx = panelX + PAD; val bw = PANEL_W - PAD * 2f
    NVGRenderer.rect(bx, contentY + 4f, bw, EMPTY_STATE_H - 8f, theme.controlBg, 8f)
    NVGRenderer.hollowRect(bx, contentY + 4f, bw, EMPTY_STATE_H - 8f, 1f, theme.controlBorder, 8f)
    NVGRenderer.text("No saved routes found.", bx + 12f, contentY + 20f, 11f, theme.text)
    NVGRenderer.text(
      "Save routes in the Routes module, then press Refresh.",
      bx + 12f,
      contentY + 38f,
      9f,
      theme.textSecondary
    )
  }

  private fun renderRouteRow(route: RoutesModule.SavedRouteInfo, rowY: Float) {
    val listTop = routeListTop()
    val listBottom = panelY + PANEL_H - PAD
    if (rowY + ROW_H < listTop - 4f || rowY > listBottom) return

    val theme = ThemeManager.currentTheme
    val rowX = panelX + PAD
    val rowW = PANEL_W - PAD * 2f
    val selected = targetSetting?.value == route.name
    val hovering = isHoveringOver(rowX, rowY, rowW, ROW_H)

    val bgColor = when {
      selected -> theme.selectedOverlay
      hovering -> theme.overlay
      else -> theme.controlBg
    }
    val borderColor = if (selected) theme.accent else theme.controlBorder
    val nameColor = if (selected) theme.accent else theme.text

    NVGRenderer.rect(rowX, rowY, rowW, ROW_H, bgColor, 8f)
    NVGRenderer.hollowRect(rowX, rowY, rowW, ROW_H, 1f, borderColor, 8f)

    val cbX = rowX + 10f
    val cbY = rowY + 10f
    if (selected) {
      NVGRenderer.rect(cbX, cbY, CHECKBOX, CHECKBOX, theme.accent, 4f)
      NVGRenderer.image(
        UICheckboxSetting.checkmarkIcon,
        cbX + 1f, cbY + 1f,
        CHECKBOX - 2f, CHECKBOX - 2f,
        colorMask = theme.textOnAccent
      )
    } else {
      NVGRenderer.rect(cbX, cbY, CHECKBOX, CHECKBOX, theme.controlBg, 4f)
      NVGRenderer.hollowRect(cbX, cbY, CHECKBOX, CHECKBOX, 1f, theme.controlBorder, 4f)
    }

    val textX = cbX + CHECKBOX + 10f
    val maxW = rowW - (textX - rowX) - 10f
    NVGRenderer.text(ellipsize(route.name, maxW, 11f), textX, rowY + 9f, 11f, nameColor)
    NVGRenderer.text(ellipsize(buildDetails(route), maxW, 9f), textX, rowY + 26f, 9f, theme.textSecondary)
  }

  private fun buildDetails(route: RoutesModule.SavedRouteInfo): String {
    val parts = mutableListOf<String>()
    if (route.mineTypes.isNotEmpty()) {
      parts += route.mineTypes.joinToString(", ")
    } else if (route.hasMinePoints) {
      parts += "Mine anchors"
    } else {
      parts += "Travel only"
    }
    if (route.hasWarpPoints) parts += "warp points"
    parts += "${route.pointCount} pts"
    return parts.joinToString(" | ")
  }

  private fun ellipsize(text: String, maxWidth: Float, size: Float): String {
    if (NVGRenderer.textWidth(text, size) <= maxWidth) return text
    var end = text.length
    while (end > 1) {
      val candidate = text.substring(0, end).trimEnd() + "..."
      if (NVGRenderer.textWidth(candidate, size) <= maxWidth) return candidate
      end--
    }
    return "..."
  }

  // ---- layout ----

  private fun refreshBounds(): FloatArray {
    val bw = 70f; val bh = 24f
    return floatArrayOf(panelX + PANEL_W - PAD - bw * 2f - 8f, panelY + 14f, bw, bh)
  }

  private fun clearBounds(): FloatArray {
    val bw = 70f; val bh = 24f
    return floatArrayOf(panelX + PANEL_W - PAD - bw, panelY + 14f, bw, bh)
  }

  private fun routeHeaderY(): Float = panelY + 68f
  private fun routeListTop(): Float = routeHeaderY() + 16f
}
```

- [ ] **Step 2: Check MouseEvent.Scroll exists**

Run: `grep -r "class Scroll\|object Scroll" src/main/kotlin/org/phantom/api/event/`

If `MouseEvent.Scroll` does not exist, replace the `onMouseScroll` handler with scroll handling inside `onMouseLeft` (no-op: scroll event will be added later). If it does exist, proceed.

- [ ] **Step 3: Verify compilation**

Run: `./gradlew compileKotlin`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/ui/hud/WalkbackRoutePickerPopup.kt
git commit -m "feat: add WalkbackRoutePickerPopup styled like commission routes panel"
```

---

## Task 3: Add per-slayer route settings and action buttons in CombatMacroModule

**Files:**
- Modify: `src/main/kotlin/org/phantom/internal/combat/CombatMacroModule.kt`

- [ ] **Step 1: Add 6 backing TextSettings after the existing slayerWalkbackRoute declaration (line ~374)**

Find:
```kotlin
  private val slayerWalkbackRoute = TextSetting(
    "Walkback Route",
    "Route name to follow back to farming area after boss kill (if Auto Warp is off). Leave blank to skip.",
    ""
  )
```

Replace with:
```kotlin
  // Per-type walkback route backing settings (persisted; not rendered directly â€” shown via action buttons below)
  private val zombieWalkbackRoute   = TextSetting("Zombie Walkback Route",   "", "")
  private val wolfWalkbackRoute     = TextSetting("Wolf Walkback Route",     "", "")
  private val spiderWalkbackRoute   = TextSetting("Spider Walkback Route",   "", "")
  private val endermanWalkbackRoute = TextSetting("Enderman Walkback Route", "", "")
  private val vampireWalkbackRoute  = TextSetting("Vampire Walkback Route",  "", "")
  private val blazeWalkbackRoute    = TextSetting("Blaze Walkback Route",    "", "")

  /** Returns the backing TextSetting for the currently selected slayer type. */
  private fun walkbackRouteForCurrentType(): TextSetting = when (slayerType.value) {
    0 -> zombieWalkbackRoute
    1 -> wolfWalkbackRoute
    2 -> spiderWalkbackRoute
    3 -> endermanWalkbackRoute
    4 -> vampireWalkbackRoute
    5 -> blazeWalkbackRoute
    else -> zombieWalkbackRoute
  }

  // Per-type walkback route action buttons (open route picker popup)
  private val zombieWalkbackAction = ActionSetting(
    "Walkback Route", "Route to follow back to farming area after boss kill.",
    "None", onClick = { WalkbackRoutePickerPopup.open("Zombie", zombieWalkbackRoute) },
    buttonLabelProvider = { zombieWalkbackRoute.value.ifBlank { "None" } }
  )
  private val wolfWalkbackAction = ActionSetting(
    "Walkback Route", "Route to follow back to farming area after boss kill.",
    "None", onClick = { WalkbackRoutePickerPopup.open("Wolf", wolfWalkbackRoute) },
    buttonLabelProvider = { wolfWalkbackRoute.value.ifBlank { "None" } }
  )
  private val spiderWalkbackAction = ActionSetting(
    "Walkback Route", "Route to follow back to farming area after boss kill.",
    "None", onClick = { WalkbackRoutePickerPopup.open("Spider", spiderWalkbackRoute) },
    buttonLabelProvider = { spiderWalkbackRoute.value.ifBlank { "None" } }
  )
  private val endermanWalkbackAction = ActionSetting(
    "Walkback Route", "Route to follow back to farming area after boss kill.",
    "None", onClick = { WalkbackRoutePickerPopup.open("Enderman", endermanWalkbackRoute) },
    buttonLabelProvider = { endermanWalkbackRoute.value.ifBlank { "None" } }
  )
  private val vampireWalkbackAction = ActionSetting(
    "Walkback Route", "Route to follow back to farming area after boss kill.",
    "None", onClick = { WalkbackRoutePickerPopup.open("Vampire", vampireWalkbackRoute) },
    buttonLabelProvider = { vampireWalkbackRoute.value.ifBlank { "None" } }
  )
  private val blazeWalkbackAction = ActionSetting(
    "Walkback Route", "Route to follow back to farming area after boss kill.",
    "None", onClick = { WalkbackRoutePickerPopup.open("Blaze", blazeWalkbackRoute) },
    buttonLabelProvider = { blazeWalkbackRoute.value.ifBlank { "None" } }
  )
```

- [ ] **Step 2: Add import for WalkbackRoutePickerPopup at top of file**

In the imports section near the top of `CombatMacroModule.kt`, add:
```kotlin
import org.phantom.internal.ui.hud.WalkbackRoutePickerPopup
```

- [ ] **Step 3: Update `addSetting(...)` in the `init` block**

Find in `init`:
```kotlin
      slayerWalkbackRoute,
      patrolRouteNameProxy,
```

Replace with:
```kotlin
      zombieWalkbackRoute,
      wolfWalkbackRoute,
      spiderWalkbackRoute,
      endermanWalkbackRoute,
      vampireWalkbackRoute,
      blazeWalkbackRoute,
      zombieWalkbackAction,
      wolfWalkbackAction,
      spiderWalkbackAction,
      endermanWalkbackAction,
      vampireWalkbackAction,
      blazeWalkbackAction,
      patrolRouteNameProxy,
```

- [ ] **Step 4: Register WalkbackRoutePickerPopup in `init`**

Find:
```kotlin
    EventBus.register(this)
  }
```

Replace with:
```kotlin
    EventBus.register(this)
    EventBus.register(WalkbackRoutePickerPopup)
  }
```

- [ ] **Step 5: Update `assignSettingGroups()` â€” remove old group line, add new ones**

Find:
```kotlin
    slayerAutoWarp.inGroup(TAB_SLAYER_GROUP)
    slayerWalkbackRoute.inGroup(TAB_SLAYER_GROUP)
    slayerSwordKeepDistance.inGroup(TAB_SLAYER_GROUP)
```

Replace with:
```kotlin
    slayerAutoWarp.inGroup(TAB_SLAYER_GROUP)
    slayerSwordKeepDistance.inGroup(TAB_SLAYER_GROUP)
```

- [ ] **Step 6: Add per-type action buttons to `assignSettingGroups()` â€” each after its separator**

Find:
```kotlin
    sepZombie.inGroup(TAB_SLAYER_WEAPONS_GROUP)
    slayerLocation.inGroup(TAB_SLAYER_WEAPONS_GROUP)
```

Replace with:
```kotlin
    sepZombie.inGroup(TAB_SLAYER_WEAPONS_GROUP)
    zombieWalkbackAction.inGroup(TAB_SLAYER_WEAPONS_GROUP)
    slayerLocation.inGroup(TAB_SLAYER_WEAPONS_GROUP)
```

Find:
```kotlin
    sepEnderman.inGroup(TAB_SLAYER_WEAPONS_GROUP)
    endermanLocation.inGroup(TAB_SLAYER_WEAPONS_GROUP)
```

Replace with:
```kotlin
    sepEnderman.inGroup(TAB_SLAYER_WEAPONS_GROUP)
    endermanWalkbackAction.inGroup(TAB_SLAYER_WEAPONS_GROUP)
    endermanLocation.inGroup(TAB_SLAYER_WEAPONS_GROUP)
```

Find:
```kotlin
    sepSpider.inGroup(TAB_SLAYER_WEAPONS_GROUP)
    spiderLocation.inGroup(TAB_SLAYER_WEAPONS_GROUP)
```

Replace with:
```kotlin
    sepSpider.inGroup(TAB_SLAYER_WEAPONS_GROUP)
    spiderWalkbackAction.inGroup(TAB_SLAYER_WEAPONS_GROUP)
    spiderLocation.inGroup(TAB_SLAYER_WEAPONS_GROUP)
```

Find:
```kotlin
    sepWolf.inGroup(TAB_SLAYER_WEAPONS_GROUP)
    wolfLocation.inGroup(TAB_SLAYER_WEAPONS_GROUP)
```

Replace with:
```kotlin
    sepWolf.inGroup(TAB_SLAYER_WEAPONS_GROUP)
    wolfWalkbackAction.inGroup(TAB_SLAYER_WEAPONS_GROUP)
    wolfLocation.inGroup(TAB_SLAYER_WEAPONS_GROUP)
```

Find:
```kotlin
    sepVampire.inGroup(TAB_SLAYER_WEAPONS_GROUP)
    vampireLocation.inGroup(TAB_SLAYER_WEAPONS_GROUP)
```

Replace with:
```kotlin
    sepVampire.inGroup(TAB_SLAYER_WEAPONS_GROUP)
    vampireWalkbackAction.inGroup(TAB_SLAYER_WEAPONS_GROUP)
    vampireLocation.inGroup(TAB_SLAYER_WEAPONS_GROUP)
```

Find:
```kotlin
    sepBlaze.inGroup(TAB_SLAYER_WEAPONS_GROUP)
    blazeLocation.inGroup(TAB_SLAYER_WEAPONS_GROUP)
```

Replace with:
```kotlin
    sepBlaze.inGroup(TAB_SLAYER_WEAPONS_GROUP)
    blazeWalkbackAction.inGroup(TAB_SLAYER_WEAPONS_GROUP)
    blazeLocation.inGroup(TAB_SLAYER_WEAPONS_GROUP)
```

- [ ] **Step 7: Verify compilation**

Run: `./gradlew compileKotlin`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 8: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/combat/CombatMacroModule.kt
git commit -m "feat: add per-slayer walkback route settings and picker action buttons"
```

---

## Task 4: Update all slayerWalkbackRoute.value references to use walkbackRouteForCurrentType()

**Files:**
- Modify: `src/main/kotlin/org/phantom/internal/combat/CombatMacroModule.kt`

- [ ] **Step 1: Update `triggerWalkToFarmArea()`**

Find (in `triggerWalkToFarmArea`):
```kotlin
    val routeName = when {
      slayerLocation.value == 1 -> CRYPT_WALKBACK_ROUTE_NAME
      slayerWalkbackRoute.value.isNotBlank() -> slayerWalkbackRoute.value.trim()
      else -> ""
    }
```

Replace with:
```kotlin
    val routeName = when {
      slayerLocation.value == 1 -> CRYPT_WALKBACK_ROUTE_NAME
      walkbackRouteForCurrentType().value.isNotBlank() -> walkbackRouteForCurrentType().value.trim()
      else -> ""
    }
```

- [ ] **Step 2: Update `finishSlayerClaim()`**

Find (in `finishSlayerClaim`):
```kotlin
    val routeName = slayerWalkbackRoute.value.trim()
    if (!slayerAutoWarp.value && routeName.isNotBlank()) {
```

Replace with:
```kotlin
    val routeName = walkbackRouteForCurrentType().value.trim()
    if (!slayerAutoWarp.value && routeName.isNotBlank()) {
```

- [ ] **Step 3: Update the startup proximity check (~line 1163)**

Find:
```kotlin
      } else if (slayerLocation.value == 0 && slayerWalkbackRoute.value.isNotBlank()
          && CombatPatrolModule.patrolPoints.isNotEmpty()) {
```

Replace with:
```kotlin
      } else if (walkbackRouteForCurrentType().value.isNotBlank()
          && CombatPatrolModule.patrolPoints.isNotEmpty()) {
```

- [ ] **Step 4: Verify no remaining references to slayerWalkbackRoute**

Run: `grep -n "slayerWalkbackRoute" src/main/kotlin/org/phantom/internal/combat/CombatMacroModule.kt`
Expected: no output (zero matches)

- [ ] **Step 5: Verify compilation**

Run: `./gradlew compileKotlin`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 6: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/combat/CombatMacroModule.kt
git commit -m "feat: use per-type walkback route settings in all route lookups"
```

---

## Task 5: Fix death-triggered walkback to work for all slayer types

**Files:**
- Modify: `src/main/kotlin/org/phantom/internal/combat/CombatMacroModule.kt`

- [ ] **Step 1: Remove the `cryptZombieSlayer.value` guard on death handling**

Find (~line 1255):
```kotlin
    if (player.isDeadOrDying || player.health <= 0f) {
      if (cryptZombieSlayer.value && !slayerDeathRespawnPending) {
        // Slayer: don't stop - queue walkback for when the player respawns.
        slayerDeathRespawnPending = true
```

Replace with:
```kotlin
    if (player.isDeadOrDying || player.health <= 0f) {
      if (slayerModeEnabled && !slayerDeathRespawnPending) {
        // Slayer: don't stop - queue walkback for when the player respawns.
        slayerDeathRespawnPending = true
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew compileKotlin`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/combat/CombatMacroModule.kt
git commit -m "fix: trigger walkback on death for all slayer types, not just zombie crypt"
```

---

## Task 6: Add farming area entry/exit tracking for all slayer types

**Files:**
- Modify: `src/main/kotlin/org/phantom/internal/combat/CombatMacroModule.kt`

- [ ] **Step 1: Add `enteredFarmingArea` flag near the other slayer state vars (~line 896)**

Find:
```kotlin
  private var slayerEnteredCrypt = false
```

After that line, add:
```kotlin
  /** True once the player has been confirmed near a patrol point since the last macro start or walkback.
   *  Used to detect area-exit (teleport/push) and trigger walkback for non-crypt slayer types. */
  private var enteredFarmingArea = false
```

- [ ] **Step 2: Reset `enteredFarmingArea` in `stopMacro()`**

Find (in `stopMacro`):
```kotlin
    startAreaOrigin = null
```

After that line, add:
```kotlin
    enteredFarmingArea = false
```

- [ ] **Step 3: Reset `enteredFarmingArea` when walkback completes (inside the `slayerNeedsWalkback` block, on the "walkback complete" branch)**

Find (~line 1222):
```kotlin
        } else {
          slayerNeedsWalkback = false
          startAreaOrigin = null
          if (slayerWalkbackJustFarm) {
```

Replace with:
```kotlin
        } else {
          slayerNeedsWalkback = false
          startAreaOrigin = null
          enteredFarmingArea = false
          if (slayerWalkbackJustFarm) {
```

- [ ] **Step 4: Reset `enteredFarmingArea` when the macro starts (in the `slayerModeEnabled` init block)**

Find (~line 1141):
```kotlin
      slayerModeEnabled = true
      beginSlayerSession()
      beginSlayerQuestDetection(mc.level?.gameTime ?: -1L)
      slayerRagnarokUsedPreBoss = false
      slayerEnteredCrypt = false
```

After `slayerEnteredCrypt = false`, add:
```kotlin
      enteredFarmingArea = false
```

- [ ] **Step 5: Add per-tick area entry tracking and exit detection**

This goes right after the existing crypt-specific area tracking block (the block ending at line ~1312 with `return`). Find the end of that block:

```kotlin
      // If !inCrypt && !slayerEnteredCrypt: still making initial walk-in, do nothing here
    }
    // Walkback owns pathfinding - yield before enforceStartArea can override it.
```

Between those two comments, add:

```kotlin
    // For non-crypt slayer types: track area entry/exit using patrol points.
    // Crypt tracking (slayerEnteredCrypt) handles the Crypt case above; this handles everything else.
    if (cryptZombieSlayer.value && slayerLocation.value != 1
      && CombatPatrolModule.patrolPoints.isNotEmpty()
      && walkbackRouteForCurrentType().value.isNotBlank()) {
      val pos = player.blockPosition()
      val nearAnyPoint = CombatPatrolModule.patrolPoints.any { p ->
        val dx = p.x - pos.x; val dz = p.z - pos.z
        dx * dx + dz * dz < GRAVEYARD_PROXIMITY_RANGE_SQ
      }
      if (nearAnyPoint) {
        enteredFarmingArea = true
      } else if (enteredFarmingArea && !slayerBossActive
        && WalkbackBridge.isRunning?.invoke() != true
        && !slayerNeedsWalkback && !slayerDeathRespawnPending) {
        ChatUtils.sendMessage("Combat macro: left farming area, walking back.")
        enteredFarmingArea = false
        startAreaOrigin = null
        triggerWalkToFarmArea(justFarm = false)
        return
      }
    }
```

- [ ] **Step 6: Verify compilation**

Run: `./gradlew compileKotlin`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 7: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/combat/CombatMacroModule.kt
git commit -m "feat: add farming area entry/exit tracking to trigger walkback when teleported away"
```

---

## Task 7: Fix world/server change to queue walkback instead of just stopping

**Files:**
- Modify: `src/main/kotlin/org/phantom/internal/combat/CombatMacroModule.kt`

- [ ] **Step 1: Update the server-switch detection block (~line 1083)**

Find:
```kotlin
    // Server-switch detection: if level instance changes while macro is active, stop everything.
    val currentLevel = mc.level
    if (currentLevel !== lastKnownLevel) {
      if (lastKnownLevel != null && (enabled.value || slayerModeEnabled)) {
        stopMacro()
        ChatUtils.sendMessage("Combat macro stopped: server change detected.")
      }
      lastKnownLevel = currentLevel
    }
```

Replace with:
```kotlin
    // Server-switch detection: if level instance changes while macro is active, stop pathfinding
    // and queue a walkback so the player returns to the farming area on reconnect.
    val currentLevel = mc.level
    if (currentLevel !== lastKnownLevel) {
      if (lastKnownLevel != null && (enabled.value || slayerModeEnabled)) {
        stopMacro()
        enteredFarmingArea = false
        if (slayerModeEnabled && walkbackRouteForCurrentType().value.isNotBlank()) {
          slayerNeedsWalkback = true
          slayerWalkbackJustFarm = false
          ChatUtils.sendMessage("Combat macro: server change detected, will walk back on reconnect.")
        } else {
          ChatUtils.sendMessage("Combat macro stopped: server change detected.")
        }
      }
      lastKnownLevel = currentLevel
    }
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew compileKotlin`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/phantom/internal/combat/CombatMacroModule.kt
git commit -m "fix: queue walkback on server change instead of just stopping macro"
```

---

## Task 8: Verify MouseEvent.Scroll and fix if absent

**Files:**
- Possibly modify: `src/main/kotlin/org/phantom/internal/ui/hud/WalkbackRoutePickerPopup.kt`

- [ ] **Step 1: Check if MouseEvent.Scroll exists**

Run: `grep -rn "class Scroll\|object Scroll\|Scroll(" src/main/kotlin/org/phantom/api/event/`

- [ ] **Step 2a: If Scroll exists**, verify the popup scroll handler is correct â€” no action needed, proceed to Step 3.

- [ ] **Step 2b: If Scroll does NOT exist**, remove the `onMouseScroll` handler from `WalkbackRoutePickerPopup.kt`:

Find and delete the entire block:
```kotlin
  @SubscribeEvent
  fun onMouseScroll(event: MouseEvent.Scroll) {
    if (!visible) return
    if (isHoveringOver(panelX, panelY, PANEL_W, PANEL_H)) {
      scroll.handleScroll(event.verticalAmount)
    }
  }
```

Run: `./gradlew compileKotlin` â†’ Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Full build**

Run: `./gradlew build`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: Commit if any changes were made in this task**

```bash
git add src/main/kotlin/org/phantom/internal/ui/hud/WalkbackRoutePickerPopup.kt
git commit -m "fix: remove missing MouseEvent.Scroll handler from WalkbackRoutePickerPopup"
```

---

## Manual Test Checklist

After a successful `./gradlew build`, load the mod in-game and verify:

1. **Route picker opens** â€” Open the Combat Macro settings panel â†’ Slayer Weapons tab â†’ click the "Walkback Route" action button under any slayer separator. The popup should appear centered on screen with a dark overlay.
2. **Route list populates** â€” Any routes saved in the Routes module appear as rows with checkmarks, name, and details.
3. **Selection persists** â€” Click a route row. The popup closes. Re-open the same picker â€” the selected route has a filled checkmark and accent border. The action button label shows the route name.
4. **Clear works** â€” Click Clear. The popup closes. Button label reverts to "None".
5. **Refresh works** â€” Click Refresh. Route list reloads. Popup stays open.
6. **Close on outside click** â€” Click outside the panel. Popup closes without changing selection.
7. **Per-type isolation** â€” Set a different route for Zombie vs Enderman. Confirm they are saved independently.
8. **Walkback fires on death** (all types) â€” Enable slayer macro for any type other than Zombie Crypt. Die in-game. After respawn, the walkback route should start.
9. **Walkback fires on area exit** â€” Enable slayer macro, walk near a patrol point (entry confirmed), then teleport away. Macro should detect exit and start walkback.
10. **Server change queues walkback** â€” Change servers while macro is active. On reconnect, walkback starts automatically.
