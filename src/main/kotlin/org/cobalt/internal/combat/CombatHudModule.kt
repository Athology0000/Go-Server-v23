package org.cobalt.internal.combat

import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import org.cobalt.api.hud.HudAnchor
import org.cobalt.api.hud.hudElement
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.api.util.ui.helper.Gradient

object CombatHudModule : Module("Combat HUD") {

  private val textSize = 13f
  private val lineHeight = textSize + 5f
  private val padding = 8f
  private val barHeight = 12f
  private val barLabelGap = 8f
  private val barTopGap = 10f
  private val borderRadius = 8f
  private val borderThickness = 1.5f
  private val minBarWidth = 210f
  private val labelColor get() = (ThemeManager.currentTheme.text and 0x00FFFFFF) or 0x99000000.toInt()

  private fun rows() = listOf(
    "Status" to CombatMacroModule.statusDisplay,
    "Mode" to CombatMacroModule.modeDisplay,
    "Target" to CombatMacroModule.targetDisplay,
    "Slayer" to CombatMacroModule.slayerDisplay,
  )

  val combatHud = hudElement("combat-hud", "Combat HUD", "Tracks combat macro status") {
    anchor = HudAnchor.TOP_LEFT
    offsetX = 10f
    offsetY = 140f

    val background = setting(CheckboxSetting("Background", "Show panel background", true))
    val onlyWhenActive = setting(CheckboxSetting("Only When Active", "Hide when macro is off", false))

    width {
      val rowsWidth = rows().maxOfOrNull { (label, value) ->
        NVGRenderer.textWidth("$label: $value", textSize)
      } ?: 120f
      max(rowsWidth, minBarWidth) + padding * 2
    }

    height {
      lineHeight * rows().size + barTopGap + lineHeight + barLabelGap + barHeight + padding * 2
    }

    render { x, y, _ ->
      if (onlyWhenActive.value && !CombatMacroModule.isRunning) return@render

      val rows = rows()
      val rowsWidth = rows.maxOfOrNull { (label, value) ->
        NVGRenderer.textWidth("$label: $value", textSize)
      } ?: 120f
      val panelW = max(rowsWidth, minBarWidth) + padding * 2
      val panelH = lineHeight * rows.size + barTopGap + lineHeight + barLabelGap + barHeight + padding * 2

      if (background.value) {
        NVGRenderer.rect(x, y, panelW, panelH, GLASS_BASE, borderRadius)
        NVGRenderer.gradientRect(
          x,
          y,
          panelW,
          panelH,
          GLASS_TINT_TOP,
          GLASS_TINT_BOTTOM,
          Gradient.TopToBottom,
          borderRadius
        )
        NVGRenderer.hollowRect(x + 1f, y + 1f, panelW - 2f, panelH - 2f, 1f, GLASS_INNER_BORDER, borderRadius - 1f)
      }

      val angle = (System.currentTimeMillis() % 12000L).toFloat() / 12000f * (Math.PI * 2.0).toFloat()
      val shiftX = cos(angle) * (panelW * 0.45f)
      val shiftY = sin(angle) * (panelH * 0.45f)
      NVGRenderer.hollowGradientRectShifted(
        x,
        y,
        panelW,
        panelH,
        borderThickness,
        OUTLINE_START,
        OUTLINE_END,
        Gradient.LeftToRight,
        borderRadius,
        shiftX,
        shiftY
      )

      rows.forEachIndexed { i, (label, value) ->
        val ty = y + padding + i * lineHeight
        val labelStr = "$label: "
        NVGRenderer.text(labelStr, x + padding, ty, textSize, labelColor)
        val labelW = NVGRenderer.textWidth(labelStr, textSize)
        NVGRenderer.text(value, x + padding + labelW, ty, textSize, ThemeManager.currentTheme.accent)
      }

      val hpLabelY = y + padding + rows.size * lineHeight + barTopGap
      val hpLabel = "Target HP"
      val hpValue = CombatMacroModule.targetHealthDisplay
      NVGRenderer.text("$hpLabel: ", x + padding, hpLabelY, textSize, labelColor)
      val hpLabelWidth = NVGRenderer.textWidth("$hpLabel: ", textSize)
      NVGRenderer.text(hpValue, x + padding + hpLabelWidth, hpLabelY, textSize, ThemeManager.currentTheme.accent)

      val barX = x + padding
      val barY = hpLabelY + lineHeight + barLabelGap
      val barW = panelW - padding * 2
      val barRatio = CombatMacroModule.targetHealthRatio
      val fillW = when {
        barRatio <= 0f -> 0f
        else -> (barW * barRatio).coerceAtLeast(barHeight).coerceAtMost(barW)
      }

      NVGRenderer.rect(barX, barY, barW, barHeight, BAR_TRACK, barHeight / 2f)
      NVGRenderer.gradientRect(
        barX + 1f,
        barY + 1f,
        barW - 2f,
        barHeight * 0.5f,
        BAR_GLOSS_TOP,
        BAR_GLOSS_BOTTOM,
        Gradient.TopToBottom,
        max(1f, barHeight / 2f - 1f)
      )
      NVGRenderer.hollowRect(barX, barY, barW, barHeight, 1f, BAR_BORDER, barHeight / 2f)
      if (fillW > 0f) {
        NVGRenderer.gradientRect(
          barX,
          barY,
          fillW,
          barHeight,
          lerpColor(HP_LOW_START, HP_HIGH_START, barRatio),
          lerpColor(HP_LOW_END, HP_HIGH_END, barRatio),
          Gradient.LeftToRight,
          barHeight / 2f
        )
        if (fillW > 2f) {
          NVGRenderer.gradientRect(
            barX + 1f,
            barY + 1f,
            fillW - 2f,
            barHeight * 0.45f,
            HP_FILL_GLOSS_TOP,
            HP_FILL_GLOSS_BOTTOM,
            Gradient.TopToBottom,
            max(1f, barHeight / 2f - 1f)
          )
        }
        NVGRenderer.hollowRect(
          barX,
          barY,
          fillW,
          barHeight,
          1f,
          lerpColor(HP_EDGE_LOW, HP_EDGE_HIGH, barRatio),
          barHeight / 2f
        )
      }

      val hpTextW = NVGRenderer.textWidth(hpValue, 11f)
      val hpTextX = barX + (barW - hpTextW) / 2f
      NVGRenderer.text(hpValue, hpTextX, barY - 0.5f, 11f, 0xFFF4F7FB.toInt())
    }
  }

  private fun lerpColor(start: Int, end: Int, t: Float): Int {
    val clamped = t.coerceIn(0f, 1f)
    val a = ((start ushr 24) and 0xFF) + ((((end ushr 24) and 0xFF) - ((start ushr 24) and 0xFF)) * clamped).toInt()
    val r = ((start ushr 16) and 0xFF) + ((((end ushr 16) and 0xFF) - ((start ushr 16) and 0xFF)) * clamped).toInt()
    val g = ((start ushr 8) and 0xFF) + ((((end ushr 8) and 0xFF) - ((start ushr 8) and 0xFF)) * clamped).toInt()
    val b = (start and 0xFF) + (((end and 0xFF) - (start and 0xFF)) * clamped).toInt()
    return (a shl 24) or (r shl 16) or (g shl 8) or b
  }

  private const val GLASS_BASE = 0x50101010.toInt()
  private const val GLASS_TINT_TOP = 0x183A6D8C
  private const val GLASS_TINT_BOTTOM = 0x08101822
private const val GLASS_INNER_BORDER = 0x26FFFFFF
  private const val BAR_TRACK = 0x7A121820
  private const val BAR_GLOSS_TOP = 0x24FFFFFF
  private const val BAR_GLOSS_BOTTOM = 0x00000000
  private const val BAR_BORDER = 0x24D9F1FF
  private const val OUTLINE_START = 0xFF2DE2FF.toInt()
  private const val OUTLINE_END = 0xFFFF6ACD.toInt()
  private const val HP_LOW_START = 0xFFFF5A6B.toInt()
  private const val HP_LOW_END = 0xFFFFA545.toInt()
  private const val HP_HIGH_START = 0xFF3BE38D.toInt()
  private const val HP_HIGH_END = 0xFF46E6F4.toInt()
  private const val HP_FILL_GLOSS_TOP = 0x34FFFFFF
  private const val HP_FILL_GLOSS_BOTTOM = 0x00000000
  private const val HP_EDGE_LOW = 0xFFFFA06A.toInt()
  private const val HP_EDGE_HIGH = 0xFF8DF7FF.toInt()
}
