package org.phantom.internal.ui.panel.panels

import org.phantom.api.addon.Addon
import org.phantom.api.addon.AddonMetadata
import org.phantom.api.module.setting.Setting
import org.phantom.api.module.setting.impl.*
import org.phantom.api.ui.theme.ThemeManager
import org.phantom.api.util.ui.NVGRenderer
import org.phantom.internal.combat.SlayerLoadoutSetting
import org.phantom.internal.mining.BlockMinerModule
import org.phantom.internal.mining.GemstoneMinerModule
import org.phantom.internal.mining.MiningMacroModule
import org.phantom.internal.mining.MiningModule
import org.phantom.internal.routes.RoutePickerSetting
import org.phantom.api.module.ModuleCategory
import org.phantom.internal.ui.UIComponent
import org.phantom.internal.ui.components.UIBackButton
import org.phantom.internal.ui.components.UICategoryHeader
import org.phantom.internal.ui.components.UIModule
import org.phantom.internal.ui.components.UITopbar
import org.phantom.internal.ui.components.settings.*
import org.phantom.internal.ui.panel.UIPanel
import org.phantom.internal.ui.screen.UIConfig
import org.phantom.internal.ui.util.GridLayout
import org.phantom.internal.ui.util.ScrollHandler
import org.phantom.internal.ui.util.isHoveringOver

internal class UIModuleList(
  private val metadata: AddonMetadata,
  addon: Addon,
) : UIPanel(
  x = 0F,
  y = 0F,
  width = 890F,
  height = 600F
) {

  private data class SettingsTabHitbox(
    val name: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
  )

  private val topBar = UITopbar("Modules")
  private val backButton = UIBackButton()

  private val allModules: List<UIModule> = addon.getModules()
    .map { module -> UIModule(module, this, false) }

  private val allItems: List<UIComponent> = buildCategoryItems(allModules)

  private var modules: List<UIComponent> = allItems
  private val modulesScroll = ScrollHandler()

  private var module = allModules.first()
  private var settingsTabs = emptyList<String>()
  private var selectedSettingsTab = Setting.DEFAULT_UI_GROUP
  private var tabHitboxes = emptyList<SettingsTabHitbox>()

  /**
   * Settings list (right side)
   */
  private var settings = emptyList<UIComponent>()

  private val settingsScroll = ScrollHandler()
  private val settingsLayout = GridLayout(
    columns = 1,
    itemWidth = 627.5F,
    itemHeight = 60F,
    gap = 10F
  )

  init {
    components.addAll(allItems)
    components.add(backButton)
    components.add(topBar)

    refreshTabs(resetSelection = true)
    rebuildSettings(resetScroll = false)
    updateAuxPanel(allModules.first().module)

    topBar.searchChanged { searchText ->
      modules = if (searchText.isEmpty()) {
        allItems
      } else {
        val searchLower = searchText.lowercase()
        allModules.filter { uiModule ->
          uiModule.module.name.lowercase().contains(searchLower) ||
            uiModule.module.getSettings().any { setting ->
              setting.name.lowercase().contains(searchLower) ||
                setting.description.lowercase().contains(searchLower)
            }
        }
      }

      val firstModule = modules.filterIsInstance<UIModule>().firstOrNull()
      if (firstModule != null) {
        setModule(firstModule)
      } else {
        components.removeAll(settings)
        settings = emptyList()
        settingsTabs = emptyList()
        tabHitboxes = emptyList()
        UIConfig.clearAuxPanel()
      }
    }
  }

  override fun render() {
    NVGRenderer.rect(x, y, width, height, ThemeManager.currentTheme.background, 10F)
    NVGRenderer.line(
      x + width / 4F,
      y + topBar.height / 2 + height * 1F / 8F,
      x + width / 4F,
      y + topBar.height / 2 + height * 7F / 8F,
      1F, ThemeManager.currentTheme.moduleDivider
    )

    topBar
      .updateBounds(x, y)
      .render()

    backButton
      .updateBounds(x + 20F, y + topBar.height + 20F)
      .render()

    NVGRenderer.text(
      metadata.name,
      x + backButton.width + 35F,
      y + topBar.height + 27.5F,
      15F, ThemeManager.currentTheme.text
    )

    val startY = y + topBar.height + backButton.height + 40F
    val visibleHeight = height - (topBar.height + backButton.height + 40F)

    modulesScroll.setMaxScroll(modulesItemsHeight(modules) + 20F, visibleHeight)
    NVGRenderer.pushScissor(x, startY, width / 4F, visibleHeight)

    val modulesScrollOffset = modulesScroll.getOffset()
    layoutModuleItems(x + 20F, startY - modulesScrollOffset, modules)
    modules.forEach(UIComponent::render)

    NVGRenderer.popScissor()

    val settingsStartX = x + width / 4F + 20F
    val settingsRegionWidth = width * 3F / 4F - 40F
    val showTabs = shouldShowTabs()

    if (showTabs) {
      renderSettingsTabs(settingsStartX, startY - TAB_BAR_HEIGHT - TAB_BAR_GAP, settingsRegionWidth)
    } else {
      tabHitboxes = emptyList()
    }

    val settingsViewY = if (showTabs) startY else startY - 35F
    val settingsViewHeight = if (showTabs) visibleHeight else visibleHeight + 35F

    settingsScroll.setMaxScroll(settingsContentHeight() - 15F, settingsViewHeight)
    NVGRenderer.pushScissor(settingsStartX, settingsViewY, settingsRegionWidth, settingsViewHeight)

    val settingsScrollOffset = settingsScroll.getOffset()
    layoutSettings(settingsStartX, settingsViewY - settingsScrollOffset)
    settings.forEach(UIComponent::render)

    NVGRenderer.popScissor()

    settings.forEach { setting ->
      when (setting) {
        is UIModeSetting -> setting.renderDropdown()
        is UIColorSetting -> setting.drawColorPicker()
        is UIRoutePickerSetting -> setting.renderDropdown()
      }
    }
  }

  override fun mouseClicked(button: Int): Boolean {
    if (button == 0 && shouldShowTabs()) {
      val hit = tabHitboxes.firstOrNull { tab ->
        isHoveringOver(tab.x, tab.y, tab.width, tab.height)
      }
      if (hit != null && hit.name != selectedSettingsTab) {
        selectedSettingsTab = hit.name
        rebuildSettings()
        return true
      }
    }

    return super.mouseClicked(button)
  }

  override fun mouseScrolled(horizontalAmount: Double, verticalAmount: Double): Boolean {
    settings.forEach {
      if (it.mouseScrolled(horizontalAmount, verticalAmount))
        return true
    }

    if (isHoveringOver(x, y, width / 4F, height)) {
      modulesScroll.handleScroll(verticalAmount)
      return true
    }

    if (isHoveringOver(x + width / 4F, y, width * 3F / 4F, height)) {
      settingsScroll.handleScroll(verticalAmount)
      return true
    }

    return false
  }

  fun setModule(module: UIModule) {
    val moduleChanged = this.module != module

    allModules.forEach {
      when {
        it == module -> module.setSelected()
        else -> it.setSelected(false)
      }
    }

    this.module = module
    refreshTabs(resetSelection = moduleChanged)
    rebuildSettings()
    updateAuxPanel(module.module)
  }

  private fun updateAuxPanel(module: org.phantom.api.module.Module) {
    if (module === MiningModule || module === MiningMacroModule || module === BlockMinerModule || module === GemstoneMinerModule) {
      UIConfig.setAuxPanel(UIMiningStatsPanel())
    } else {
      UIConfig.clearAuxPanel()
    }
  }

  private fun refreshTabs(resetSelection: Boolean) {
    val groups = module.getSettings()
      .filter { it.uiGroup != SIDE_GROUP }
      .map { setting -> setting.uiGroup.ifBlank { Setting.DEFAULT_UI_GROUP } }
      .distinct()

    settingsTabs = if (groups.isEmpty()) listOf(Setting.DEFAULT_UI_GROUP) else groups
    if (resetSelection || selectedSettingsTab !in settingsTabs) {
      selectedSettingsTab = settingsTabs.first()
    }
    tabHitboxes = emptyList()
  }

  private fun shouldShowTabs(): Boolean {
    return settingsTabs.size > 1 && topBar.getSearchText().isEmpty()
  }

  private fun rebuildSettings(resetScroll: Boolean = true) {
    components.removeAll(settings)
    settings = filteredSettings().map(::toComponent)
    components.addAll(settings)
    if (resetScroll) {
      settingsScroll.reset()
    }
  }

  private fun filteredSettings(): List<Setting<*>> {
    val searchLower = topBar.getSearchText().trim().lowercase()
    return module.getSettings().filter { setting ->
      if (setting.uiGroup == SIDE_GROUP) return@filter false
      val matchesSearch = searchLower.isEmpty() ||
        setting.name.lowercase().contains(searchLower) ||
        setting.description.lowercase().contains(searchLower)
      if (!matchesSearch) {
        return@filter false
      }

      if (searchLower.isNotEmpty() || settingsTabs.size <= 1) {
        return@filter true
      }

      val group = setting.uiGroup.ifBlank { Setting.DEFAULT_UI_GROUP }
      group == selectedSettingsTab
    }
  }

  private fun toComponent(setting: Setting<*>): UIComponent {
    return when (setting) {
      is ActionSetting -> UIActionSetting(setting)
      is CommandHotkeySetting -> UICommandHotkeySetting(setting)
      is CheckboxSetting -> UICheckboxSetting(setting)
      is ColorSetting -> UIColorSetting(setting)
      is InfoSetting -> UIInfoSetting(setting)
      is KeyBindSetting -> UIKeyBindSetting(setting)
      is SlayerLoadoutSetting -> UISlayerLoadoutSetting(setting)
      is ModeSetting -> UIModeSetting(setting)
      is RangeSetting -> UIRangeSetting(setting)
      is RoutePickerSetting -> UIRoutePickerSetting(setting)
      is SliderSetting -> UISliderSetting(setting)
      else -> UITextSetting(setting as TextSetting)
    }
  }

  private fun settingsContentHeight(): Float {
    if (settings.isEmpty()) return 0F
    var total = 0F
    settings.forEachIndexed { index, component ->
      total += component.height
      if (index < settings.lastIndex) total += SETTINGS_ITEM_GAP
    }
    return total
  }

  private fun layoutSettings(startX: Float, startY: Float) {
    var currentY = startY
    settings.forEach { component ->
      component.updateBounds(startX, currentY)
      currentY += component.height + SETTINGS_ITEM_GAP
    }
  }

  private fun renderSettingsTabs(startX: Float, y: Float, maxWidth: Float) {
    val hitboxes = ArrayList<SettingsTabHitbox>(settingsTabs.size)
    var cursorX = startX
    val maxX = startX + maxWidth

    for (tab in settingsTabs) {
      val tabWidth = NVGRenderer.textWidth(tab, TAB_TEXT_SIZE) + TAB_HORIZONTAL_PADDING * 2F
      if (cursorX + tabWidth > maxX) {
        break
      }

      val selected = tab == selectedSettingsTab
      val hovering = isHoveringOver(cursorX, y, tabWidth, TAB_BAR_HEIGHT)
      val bgColor = when {
        selected -> ThemeManager.currentTheme.selectedOverlay
        hovering -> ThemeManager.currentTheme.overlay
        else -> ThemeManager.currentTheme.controlBg
      }
      val borderColor = if (selected) ThemeManager.currentTheme.accent else ThemeManager.currentTheme.controlBorder
      val textColor = if (selected) ThemeManager.currentTheme.accent else ThemeManager.currentTheme.text

      NVGRenderer.rect(cursorX, y, tabWidth, TAB_BAR_HEIGHT, bgColor, 5F)
      NVGRenderer.hollowRect(cursorX, y, tabWidth, TAB_BAR_HEIGHT, 1F, borderColor, 5F)
      NVGRenderer.text(tab, cursorX + TAB_HORIZONTAL_PADDING, y + TAB_TEXT_Y, TAB_TEXT_SIZE, textColor)

      hitboxes.add(SettingsTabHitbox(tab, cursorX, y, tabWidth, TAB_BAR_HEIGHT))
      cursorX += tabWidth + TAB_GAP
    }

    tabHitboxes = hitboxes
  }

  private fun buildCategoryItems(uiModules: List<UIModule>): List<UIComponent> {
    val result = mutableListOf<UIComponent>()
    for (cat in ModuleCategory.entries) {
      val inCategory = uiModules.filter { it.module.category == cat }
      if (inCategory.isNotEmpty()) {
        result.add(UICategoryHeader(cat))
        result.addAll(inCategory)
      }
    }
    return result
  }

  private fun modulesItemsHeight(items: List<UIComponent>): Float {
    if (items.isEmpty()) return 0F
    return items.sumOf { it.height.toDouble() }.toFloat() + (items.size - 1) * MODULES_ITEM_GAP
  }

  private fun layoutModuleItems(startX: Float, startY: Float, items: List<UIComponent>) {
    var currentY = startY
    items.forEach { item ->
      item.updateBounds(startX, currentY)
      currentY += item.height + MODULES_ITEM_GAP
    }
  }

  companion object {
    const val SIDE_GROUP = "__side__"
    private const val MODULES_ITEM_GAP = 5F
    private const val TAB_BAR_HEIGHT = 24F
    private const val TAB_HORIZONTAL_PADDING = 10F
    private const val TAB_GAP = 8F
    private const val TAB_TEXT_SIZE = 12F
    private const val TAB_TEXT_Y = 5F
    private const val TAB_BAR_GAP = 8F
    private const val SETTINGS_ITEM_GAP = 10F
  }
}
