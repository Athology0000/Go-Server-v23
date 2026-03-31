package org.cobalt.internal.ui.hud

import java.awt.Color
import net.minecraft.client.Minecraft
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.MouseEvent
import org.cobalt.api.event.impl.render.NvgEvent
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.internal.mining.RoutesModule
import org.cobalt.internal.ui.components.settings.UICheckboxSetting
import org.cobalt.internal.ui.util.ScrollHandler
import org.cobalt.internal.ui.util.isHoveringOver
import org.cobalt.internal.ui.util.mouseX
import org.cobalt.internal.ui.util.mouseY

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
    scroll.reset()
    visible = true
  }

  fun isVisible(): Boolean = visible

  fun mouseScrolled(horizontalAmount: Double, verticalAmount: Double): Boolean {
    if (!visible) return false
    if (isHoveringOver(panelX, panelY, PANEL_W, PANEL_H)) {
      scroll.handleScroll(verticalAmount)
      return true
    }
    return false
  }

  private fun close() {
    visible = false
    targetSetting = null
    routes = emptyList()
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
    NVGRenderer.text("Walkback Route — $slayerTypeName", panelX + PAD, panelY + 22f, 13f, theme.text)

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
    if (routes.isNotEmpty()) {
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
