package org.cobalt.internal.mining

import org.cobalt.api.hud.HudAnchor
import org.cobalt.api.hud.hudElement
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.util.ui.NVGRenderer

object CommissionHudModule : Module("Commission HUD") {

  private val textSize = 13f
  private val lineHeight = textSize + 5f
  private val padding = 8f
  private val labelColor get() = (ThemeManager.currentTheme.text and 0x00FFFFFF.toInt()) or 0x99000000.toInt()

  private fun rows() = listOf(
    "Status" to CommissionMacroModule.statusDisplay,
    "Commission" to CommissionMacroModule.commissionDisplay,
    "Zone" to CommissionMacroModule.currentZoneDisplay,
    "Target" to CommissionMacroModule.targetZoneDisplay,
  )

  val commissionHud = hudElement("commission-hud", "Commission HUD", "Tracks commission macro status") {
    anchor = HudAnchor.TOP_LEFT
    offsetX = 10f
    offsetY = 80f

    val background = setting(CheckboxSetting("Background", "Show panel background", true))
    val onlyWhenActive = setting(CheckboxSetting("Only When Active", "Hide when macro is off", false))

    width {
      val r = rows()
      val maxW = r.maxOfOrNull { (label, value) ->
        NVGRenderer.textWidth("$label: $value", textSize)
      } ?: 120f
      maxW + padding * 2
    }

    height { lineHeight * rows().size + padding * 2 }

    render { x, y, _ ->
      if (onlyWhenActive.value && !CommissionMacroModule.isRunning) return@render

      val r = rows()
      val maxW = r.maxOfOrNull { (label, value) ->
        NVGRenderer.textWidth("$label: $value", textSize)
      } ?: 120f
      val panelW = maxW + padding * 2
      val panelH = lineHeight * r.size + padding * 2

      if (background.value) {
        NVGRenderer.rect(x, y, panelW, panelH, ThemeManager.currentTheme.panel, 6f)
      }

      r.forEachIndexed { i, (label, value) ->
        val ty = y + padding + i * lineHeight
        val labelStr = "$label: "
        NVGRenderer.text(labelStr, x + padding, ty, textSize, labelColor)
        val labelW = NVGRenderer.textWidth(labelStr, textSize)
        NVGRenderer.text(value, x + padding + labelW, ty, textSize, ThemeManager.currentTheme.accent)
      }
    }
  }
}
