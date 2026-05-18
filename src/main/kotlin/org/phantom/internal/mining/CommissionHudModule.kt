package org.phantom.internal.mining

import org.phantom.api.hud.HudAnchor
import org.phantom.api.hud.HudGlassStyle
import org.phantom.api.hud.hudElement
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.util.ui.NVGRenderer

object CommissionHudModule : Module("Commission HUD") {

  override val category = ModuleCategory.MINING

  private val textSize = HudGlassStyle.BODY_SIZE
  private val lineHeight = HudGlassStyle.LINE_HEIGHT
  private val paddingX = HudGlassStyle.PADDING_X
  private val paddingY = HudGlassStyle.PADDING_Y
  private val panelGap = 5f
  private val titlePanelHeight = 31f
  private val headerHeight = 29f

  private data class GlassSection(
    val title: String,
    val rows: List<Pair<String, String>>,
  )

  private fun rows(): List<Pair<String, String>> {
    return if (isGlaciteMode()) {
      listOf(
        "Commissions Completed" to "-",
        "Commission" to GlaciteCommissionMacroModule.commissionDisplay,
        "Area"       to GlaciteCommissionMacroModule.currentZoneDisplay,
        "State"      to GlaciteCommissionMacroModule.statusDisplay,
      )
    } else {
      listOf(
        "Commissions Completed" to "-",
        "Commission" to CommissionMacroModule.commissionDisplay,
        "Area"       to CommissionMacroModule.currentZoneDisplay,
        "State"      to CommissionMacroModule.statusDisplay,
      )
    }
  }

  private val debugRow = CheckboxSetting("Debug", "Show parsed-commission debug line (gradient)", false)

  private fun debugText(): String? =
    if (debugRow.value && !isGlaciteMode()) CommissionMacroModule.commissionDebugDisplay else null

  private fun isActiveCommissionMacro(): Boolean =
    if (isGlaciteMode()) GlaciteCommissionMacroModule.isRunning else CommissionMacroModule.isRunning

  private fun isGlaciteMode(): Boolean =
    MiningMacroModule.currentMiningArea() == MiningArea.GLACITE

  private fun title(): String =
    if (isGlaciteMode()) "Glacite Commission Macro" else "Dwarven Commission Macro"

  private fun sections(): List<GlassSection> {
    val miningRows = MiningModule.buildOverlayRows()
      .take(4)
      .mapNotNull { row ->
        val separator = row.indexOf(':')
        if (separator < 0) null else row.substring(0, separator).trim() to row.substring(separator + 1).trim()
      }

    return listOf(
      GlassSection("Commission Info", rows()),
      GlassSection("Mining Info", miningRows.ifEmpty { listOf("Mining" to "Idle") }),
      GlassSection(
        "Macro Info",
        listOf(
          "Mode" to if (isGlaciteMode()) GlaciteCommissionMacroModule.modeDisplay else CommissionMacroModule.modeDisplay,
          "Running" to if (isActiveCommissionMacro()) "Yes" else "No",
        ),
      ),
    )
  }

  private fun sectionHeight(section: GlassSection): Float =
    headerHeight + paddingY + section.rows.size * lineHeight

  private fun panelWidth(debug: String?): Float {
    val titleWidth = NVGRenderer.textWidth(title(), HudGlassStyle.TITLE_SIZE)
    val sectionWidth = sections().maxOfOrNull { section ->
      maxOf(
        NVGRenderer.textWidth(section.title, HudGlassStyle.SECTION_SIZE),
        section.rows.maxOfOrNull { (label, value) -> NVGRenderer.textWidth("$label: $value", textSize) } ?: 0f,
      )
    } ?: 120f
    val debugWidth = debug?.let { NVGRenderer.textWidth(it, textSize) } ?: 0f
    return maxOf(titleWidth, sectionWidth, debugWidth) + paddingX * 2f
  }

  private fun panelHeight(debug: String?): Float {
    val sectionsHeight = sections().sumOf { sectionHeight(it).toDouble() }.toFloat()
    val debugHeight = if (debug != null) panelGap + paddingY * 2f + lineHeight else 0f
    return titlePanelHeight + panelGap * sections().size + sectionsHeight + debugHeight
  }

  val commissionHud = hudElement("commission-hud", "Commission HUD", "Tracks commission macro status") {
    anchor  = HudAnchor.TOP_LEFT
    offsetX = 10f
    offsetY = 80f
    blurBackground = true
    blurStrength = 16.0

    val onlyWhenActive = setting(CheckboxSetting("Only When Active", "Hide when macro is off", true))
    setting(debugRow)

    width {
      if (onlyWhenActive.value && !isActiveCommissionMacro()) 0f else panelWidth(debugText())
    }

    height {
      if (onlyWhenActive.value && !isActiveCommissionMacro()) 0f else panelHeight(debugText())
    }

    render { x, y, _ ->
      if (onlyWhenActive.value && !isActiveCommissionMacro()) return@render

      val dbg = debugText()
      val panelW = panelWidth(dbg)
      var panelY = y

      HudGlassStyle.drawPanel(x, panelY, panelW, titlePanelHeight)
      NVGRenderer.text(title(), x + paddingX, panelY + 8f, HudGlassStyle.TITLE_SIZE, HudGlassStyle.mutedColor())
      panelY += titlePanelHeight + panelGap

      sections().forEach { section ->
        val panelH = sectionHeight(section)
        HudGlassStyle.drawPanel(x, panelY, panelW, panelH)
        HudGlassStyle.drawHeader(section.title, x, panelY, panelW)
        section.rows.forEachIndexed { i, (label, value) ->
          HudGlassStyle.drawRow(
            label,
            value,
            x + paddingX,
            panelY + headerHeight + i * lineHeight,
            if (label == "Commission") HudGlassStyle.accentColor() else HudGlassStyle.textColor(),
          )
        }
        panelY += panelH + panelGap
      }

      if (dbg != null) {
        val panelH = paddingY * 2f + lineHeight
        HudGlassStyle.drawPanel(x, panelY, panelW, panelH)
        NVGRenderer.text(dbg, x + paddingX, panelY + paddingY, textSize, HudGlassStyle.accentColor())
      }
    }
  }
}
