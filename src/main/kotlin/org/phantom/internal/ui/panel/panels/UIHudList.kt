package org.phantom.internal.ui.panel.panels

import java.awt.Color
import org.phantom.api.hud.HudElement
import org.phantom.api.hud.HudModuleManager
import org.phantom.api.module.ModuleManager
import org.phantom.api.module.setting.impl.*
import org.phantom.api.ui.theme.ThemeManager
import org.phantom.api.util.ui.NVGRenderer
import org.phantom.internal.ui.UIComponent
import org.phantom.internal.ui.components.UITopbar
import org.phantom.internal.ui.components.settings.*
import org.phantom.internal.ui.panel.UIPanel
import org.phantom.internal.ui.screen.UIHudEditor
import org.phantom.internal.ui.util.ScrollHandler
import org.phantom.internal.ui.util.isHoveringOver

internal class UIHudList : UIPanel(
  x = 0F,
  y = 0F,
  width = 890F,
  height = 600F
) {

  private val topBar = UITopbar("HUD Modules")
  private var allEntries = HudModuleManager.getElements().map { HudElementEntry(it) }
  private var entries = allEntries

  private val scrollHandler = ScrollHandler()

  private val editButton = ActionButton(
    label = "Edit HUD",
    width = 170F,
    height = 40F,
    background = { ThemeManager.currentTheme.accent },
    textColor = { ThemeManager.currentTheme.textOnAccent }
  ) {
    UIHudEditor().openUI()
  }

  private val resetButton = ActionButton(
    label = "Reset All",
    width = 140F,
    height = 40F,
    background = { ThemeManager.currentTheme.controlBg },
    textColor = { ThemeManager.currentTheme.text }
  ) {
    HudModuleManager.resetAllPositions()
  }

  fun refreshEntries() {
    allEntries = HudModuleManager.getElements().map { HudElementEntry(it) }
    entries = allEntries
    components.removeAll { it is HudElementEntry }
    components.addAll(0, allEntries)
  }

  init {
    components.addAll(allEntries)
    components.add(editButton)
    components.add(resetButton)
    components.add(topBar)

    topBar.searchChanged { searchText ->
      entries = if (searchText.isEmpty()) {
        allEntries
      } else {
        val searchLower = searchText.lowercase()
        allEntries.filter {
          it.module.name.lowercase().contains(searchLower) ||
            it.module.description.lowercase().contains(searchLower)
        }
      }
    }
  }

  override fun render() {
    NVGRenderer.rect(x, y, width, height, ThemeManager.currentTheme.background, 10F)

    topBar
      .updateBounds(x, y)
      .render()

    val buttonsY = y + topBar.height + 15F
    val buttonGap = 12F
    val totalButtonsWidth = editButton.width + resetButton.width + buttonGap
    val buttonsStartX = x + width / 2F - totalButtonsWidth / 2F

    editButton
      .updateBounds(buttonsStartX, buttonsY)
      .render()

    resetButton
      .updateBounds(buttonsStartX + editButton.width + buttonGap, buttonsY)
      .render()

    val listStartY = buttonsY + editButton.height + 20F
    val visibleHeight = height - (listStartY - y)
    val entryGap = 10F
    val contentHeight = if (entries.isEmpty()) 0F else {
      entries.sumOf { it.height.toDouble() }.toFloat() + (entries.size - 1) * entryGap
    }

    scrollHandler.setMaxScroll(contentHeight + 20F, visibleHeight)
    NVGRenderer.pushScissor(x, listStartY, width, visibleHeight)

    val scrollOffset = scrollHandler.getOffset()
    entries.forEachIndexed { index, entry ->
      val entryY = listStartY + 10F + index * (entry.height + entryGap) - scrollOffset
      entry.updateBounds(x, entryY)
      entry.render()
    }

    NVGRenderer.popScissor()
  }

  override fun mouseScrolled(horizontalAmount: Double, verticalAmount: Double): Boolean {
    if (isHoveringOver(x, y, width, height)) {
      scrollHandler.handleScroll(verticalAmount)
      return true
    }

    return false
  }

  override fun mouseClicked(button: Int): Boolean {
    println("[UIHudList] panel mouseClicked button=$button panel=($x,$y,$width,$height)")

    if (editButton.mouseClicked(button)) {
      println("[UIHudList] edit button clicked")
      return true
    }

    if (resetButton.mouseClicked(button)) {
      println("[UIHudList] reset button clicked")
      return true
    }

    val buttonsY = y + topBar.height + 15F
    val listStartY = buttonsY + editButton.height + 20F
    val visibleHeight = height - (listStartY - y)

    val insideList = isHoveringOver(x, listStartY, width, visibleHeight)
    println("[UIHudList] insideList=$insideList listBounds=($x,$listStartY,$width,$visibleHeight) entries=${entries.size}")

    if (!insideList) {
      return false
    }

    val entryGap = 10F
    val scrollOffset = scrollHandler.getOffset()
    println("[UIHudList] scrollOffset=$scrollOffset")

    entries.forEachIndexed { index, entry ->
      val entryY = listStartY + 10F + index * (entry.height + entryGap) - scrollOffset
      entry.updateBounds(x, entryY)

      println(
        "[UIHudList] forwarding click to entry index=$index name=${entry.module.name} " +
          "bounds=(${entry.x},${entry.y},${entry.width},${entry.height})"
      )

      if (entry.mouseClicked(button)) {
        println("[UIHudList] entry handled click index=$index name=${entry.module.name}")
        return true
      }
    }

    println("[UIHudList] no entry handled click")
    return false
  }

  private class ActionButton(
    private val label: String,
    width: Float,
    height: Float,
    private val background: () -> Int,
    private val textColor: () -> Int,
    private val onClick: () -> Unit,
  ) : UIComponent(0F, 0F, width, height) {

    override fun render() {
      val hovering = isHoveringOver(x, y, width, height)
      val baseColor = background()
      val color = if (hovering) Color(baseColor).darker().rgb else baseColor

      NVGRenderer.rect(x, y, width, height, color, 8F)
      NVGRenderer.text(
        label,
        x + width / 2F - NVGRenderer.textWidth(label, 14F) / 2F,
        y + height / 2F - 7F,
        14F,
        textColor()
      )
    }

    override fun mouseClicked(button: Int): Boolean {
      val hovering = isHoveringOver(x, y, width, height)

      if (button == 0 && hovering) {
        println("[UIHudList] ActionButton '$label' clicked")
        onClick()
        return true
      }

      return false
    }
  }

  private class HudElementEntry(
    val module: HudElement,
  ) : UIComponent(0F, 0F, 890F, 60F) {

    private var expanded = false
    private val baseHeight = 60F

    private val settings = module.getSettings().map {
      when (it) {
        is ActionSetting -> UIActionSetting(it)
        is CheckboxSetting -> UICheckboxSetting(it)
        is ColorSetting -> UIColorSetting(it)
        is InfoSetting -> UIInfoSetting(it)
        is KeyBindSetting -> UIKeyBindSetting(it)
        is ModeSetting -> UIModeSetting(it)
        is RangeSetting -> UIRangeSetting(it)
        is SliderSetting -> UISliderSetting(it)
        else -> UITextSetting(it as TextSetting)
      }
    }

    private fun computeHeight(): Float {
      return if (expanded && settings.isNotEmpty()) {
        baseHeight + settings.size * 70F
      } else {
        baseHeight
      }
    }

    override fun render() {
      height = computeHeight()
      val theme = ThemeManager.currentTheme
      NVGRenderer.rect(x, y, width, height, theme.controlBg, 8F)

      NVGRenderer.text(
        module.name,
        x + 20F,
        y + 18F,
        14F,
        theme.text
      )

      NVGRenderer.text(
        module.description,
        x + 20F,
        y + 36F,
        12F,
        theme.textSecondary
      )

      val toggleWidth = 40F
      val toggleHeight = 22F
      val toggleX = x + width - 20F - toggleWidth
      val toggleY = y + height / 2F - toggleHeight / 2F
      val toggleColor = if (module.enabled) theme.accent else theme.controlBg

      NVGRenderer.rect(toggleX, toggleY, toggleWidth, toggleHeight, toggleColor, toggleHeight / 2F)

      val knobRadius = 9F
      val knobX = if (module.enabled) {
        toggleX + toggleWidth - 11F
      } else {
        toggleX + 11F
      }

      val knobY = toggleY + toggleHeight / 2F
      NVGRenderer.circle(knobX, knobY, knobRadius, theme.textOnAccent)

      NVGRenderer.line(x, y + height, x + width, y + height, 1F, theme.moduleDivider)

      if (expanded && settings.isNotEmpty()) {
        var settingY = y + baseHeight + 10F
        settings.forEach { setting ->
          setting.updateBounds(x + 20F, settingY)
          setting.width = width - 40F
          setting.height = 60F
          setting.render()
          settingY += 70F
        }
      }
    }

    override fun mouseClicked(button: Int): Boolean {
      println(
        "[UIHudList] HudElementEntry mouseClicked name=${module.name} button=$button " +
          "entryBounds=($x,$y,$width,$height)"
      )

      if (button == 0) {
        val toggleWidth = 40F
        val toggleHeight = 22F
        val toggleX = x + width - 20F - toggleWidth
        val toggleY = y + height / 2F - toggleHeight / 2F

        val toggleHovered = isHoveringOver(toggleX, toggleY, toggleWidth, toggleHeight)
        val rowHovered = isHoveringOver(x, y, width, baseHeight)
        val canToggle = ModuleManager.canToggleModules()

        println(
          "[UIHudList] name=${module.name} toggleBounds=($toggleX,$toggleY,$toggleWidth,$toggleHeight) " +
            "toggleHovered=$toggleHovered rowHovered=$rowHovered canToggle=$canToggle enabled=${module.enabled}"
        )

        if (toggleHovered) {
          if (!canToggle) {
            println("[UIHudList] BLOCKED toggle for ${module.name}: ModuleManager.canToggleModules() returned false")
            return true
          }

          module.enabled = !module.enabled
          println("[UIHudList] TOGGLED ${module.name} enabled=${module.enabled}")
          return true
        }

        if (rowHovered) {
          expanded = !expanded
          println("[UIHudList] EXPANDED ${module.name} expanded=$expanded")
          return true
        }
      }

      if (expanded && button == 0) {
        for (setting in settings) {
          if (setting.mouseClicked(button)) {
            println("[UIHudList] setting handled click for ${module.name}")
            return true
          }
        }
      }

      return false
    }
  }
}
