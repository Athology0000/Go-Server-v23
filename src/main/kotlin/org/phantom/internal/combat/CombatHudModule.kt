package org.phantom.internal.combat

import kotlin.math.cos
import kotlin.math.max
import org.phantom.api.hud.HudAnchor
import org.phantom.api.hud.hudElement
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.ui.theme.ThemeGradient
import org.phantom.api.ui.theme.ThemeManager
import org.phantom.api.ui.theme.ThemeSurface
import org.phantom.api.util.ui.NVGRenderer
import org.phantom.api.util.ui.helper.Gradient

object CombatHudModule : Module("Combat HUD") {

  override val category = ModuleCategory.COMBAT

  private val textSize     = 13f
  private val lineHeight   = textSize + 5f
  private val padding      = 10f
  private val barHeight    = 12f
  private val barLabelGap  = 8f
  private val barTopGap    = 10f
  private val corner       = 10f
  private val minBarWidth  = 210f

  private val labelColor   get() = (ThemeManager.currentTheme.text and 0x00FFFFFF) or 0x99000000.toInt()
  private fun rows(): List<Pair<String, String>> {
    val rows = mutableListOf(
      "Status" to CombatMacroModule.statusDisplay,
      "Mode" to CombatMacroModule.modeDisplay,
      "Target" to CombatMacroModule.targetDisplay,
      "Slayer" to CombatMacroModule.slayerDisplay,
    )
    if (CombatMacroModule.isSlayerHudVisible) {
      rows += listOf(
        "Quest" to CombatMacroModule.slayerQuestLevelDisplay,
        "Quest State" to CombatMacroModule.slayerQuestStateDisplay,
        "Kills Left" to CombatMacroModule.slayerKillsLeftDisplay,
        "Kills/hr" to CombatMacroModule.slayerKillsPerHourDisplay,
        "Quests Done" to CombatMacroModule.slayerQuestsCompletedDisplay,
        "Quests/hr" to CombatMacroModule.slayerQuestsPerHourDisplay,
        "Quests Failed" to CombatMacroModule.slayerQuestsFailedDisplay,
        "Fails/hr" to CombatMacroModule.slayerQuestFailsPerHourDisplay,
      )
    }
    return rows
  }

  val combatHud = hudElement("combat-hud", "Combat HUD", "Tracks combat macro status") {
    anchor  = HudAnchor.TOP_LEFT
    offsetX = 10f
    offsetY = 140f

    val onlyWhenActive = setting(CheckboxSetting("Only When Active", "Hide when macro is off", true))

    width {
      max(rows().maxOfOrNull { (l, v) -> NVGRenderer.textWidth("$l: $v", textSize) } ?: 120f, minBarWidth) + padding * 2
    }

    height {
      lineHeight * rows().size + barTopGap + lineHeight + barLabelGap + barHeight + padding * 2
    }

    render { x, y, _ ->
      if (onlyWhenActive.value && !CombatMacroModule.isRunning) return@render

      val rows   = rows()
      val panelW = max(rows.maxOfOrNull { (l, v) -> NVGRenderer.textWidth("$l: $v", textSize) } ?: 120f, minBarWidth) + padding * 2
      val panelH = lineHeight * rows.size + barTopGap + lineHeight + barLabelGap + barHeight + padding * 2
      val now    = System.currentTimeMillis()
      val twoPi  = (Math.PI * 2).toFloat()

      // Spotify-style background
      NVGRenderer.rect(x, y, panelW, panelH, ThemeSurface.panelSolid(), corner)
      NVGRenderer.gradientRect(x, y, panelW, panelH * 0.5f, ThemeSurface.overlay(), 0x00000000, Gradient.TopToBottom, corner)

      // Animated gradient border
      val angle  = (now % 10000L).toFloat() / 10000f * twoPi
      val shiftX = cos(angle) * (panelW * 0.42f)
      val (borderColor1, borderColor2) = ThemeGradient.colors()
      NVGRenderer.hollowGradientRectShifted(x, y, panelW, panelH, 1.5f, borderColor1, borderColor2, Gradient.LeftToRight, corner, shiftX, 0f)

      rows.forEachIndexed { i, (label, value) ->
        val ty       = y + padding + i * lineHeight
        val labelStr = "$label: "
        NVGRenderer.text(labelStr, x + padding, ty, textSize, labelColor)
        val labelW = NVGRenderer.textWidth(labelStr, textSize)
        NVGRenderer.text(value, x + padding + labelW, ty, textSize, ThemeManager.currentTheme.accent)
      }

      // HP label + bar
      val hpLabelY = y + padding + rows.size * lineHeight + barTopGap
      val hpLabel  = "Target HP"
      val hpValue  = CombatMacroModule.targetHealthDisplay
      NVGRenderer.text("$hpLabel: ", x + padding, hpLabelY, textSize, labelColor)
      val hpLabelW = NVGRenderer.textWidth("$hpLabel: ", textSize)
      NVGRenderer.text(hpValue, x + padding + hpLabelW, hpLabelY, textSize, ThemeManager.currentTheme.accent)

      val barX     = x + padding
      val barY     = hpLabelY + lineHeight + barLabelGap
      val barW     = panelW - padding * 2
      val barRatio = CombatMacroModule.targetHealthRatio
      val fillW    = if (barRatio <= 0f) 0f else (barW * barRatio).coerceAtLeast(barHeight).coerceAtMost(barW)

      NVGRenderer.rect(barX, barY, barW, barHeight, ThemeSurface.track(), barHeight / 2f)
      NVGRenderer.hollowRect(barX, barY, barW, barHeight, 1f, 0x22FFFFFF, barHeight / 2f)

      if (fillW > 0f) {
        val hpC1 = ThemeGradient.sample(position = ThemeGradient.phase() + barRatio * 0.15f)
        val hpC2 = ThemeGradient.sample(position = ThemeGradient.phase() + 0.5f + barRatio * 0.15f)
        NVGRenderer.gradientRect(barX, barY, fillW, barHeight, hpC1, hpC2, Gradient.LeftToRight, barHeight / 2f)
      }

      val hpTextW = NVGRenderer.textWidth(hpValue, 11f)
      NVGRenderer.text(hpValue, barX + (barW - hpTextW) / 2f, barY - 0.5f, 11f, 0xFFF4F7FB.toInt())
    }
  }

}
