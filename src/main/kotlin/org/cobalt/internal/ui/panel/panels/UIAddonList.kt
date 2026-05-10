package org.cobalt.internal.ui.panel.panels

import net.fabricmc.loader.api.FabricLoader
import org.cobalt.api.addon.Addon
import org.cobalt.api.addon.AddonMetadata
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.ModuleManager
import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.internal.mining.CommissionMacroModule
import org.cobalt.api.hud.modules.MiningHudModule
import org.cobalt.internal.combat.CombatMacroModule
import org.cobalt.internal.garden.GardenAnalyzerModule
import org.cobalt.internal.garden.GardenMacroModule
import org.cobalt.internal.loader.AddonLoader
import org.cobalt.internal.mining.AutoLanternModule
import org.cobalt.internal.mining.BlockMinerModule
import org.cobalt.internal.mining.CommissionHudModule
import org.cobalt.internal.mining.FairyModule
import org.cobalt.internal.mining.GemstoneMinerModule
import org.cobalt.internal.mining.MiningMacroModule
import org.cobalt.internal.mining.MiningModule
import org.cobalt.internal.mining.RoutesModule
import org.cobalt.internal.mining.VeinDirectionModule
import org.cobalt.internal.combat.slayer.SlayerMacroModule
import org.cobalt.internal.visual.BlockOutlineModule
import org.cobalt.internal.visual.BlockOverlayModule
import org.cobalt.internal.visual.FreecamModule
import org.cobalt.internal.visual.FullBrightModule
import org.cobalt.internal.visual.HotbarOverlayModule
import org.cobalt.internal.visual.OrbitFreecamModule
import org.cobalt.internal.visual.PetDisplayModule
import org.cobalt.internal.ui.UIComponent
import org.cobalt.internal.ui.components.UIAddonEntry
import org.cobalt.internal.ui.components.UISectionHeader
import org.cobalt.internal.ui.components.UITopbar
import org.cobalt.internal.ui.panel.UIPanel
import org.cobalt.internal.ui.util.GridLayout
import org.cobalt.internal.ui.util.ScrollHandler
import org.cobalt.internal.ui.util.isHoveringOver
import kotlin.math.ceil

internal class UIAddonList : UIPanel(
  x = 0F,
  y = 0F,
  width = 890F,
  height = 600F
) {

  private val topBar = UITopbar("Addons")
  private val allSections = buildAddonSections()
  private var sections = allSections

  private val gridLayout = GridLayout(
    columns = GRID_COLUMNS,
    itemWidth = ENTRY_WIDTH,
    itemHeight = ENTRY_HEIGHT,
    gap = GRID_GAP
  )

  private val scrollHandler = ScrollHandler()

  init {
    rebuildComponents()

    topBar.searchChanged { searchText ->
      val normalizedSearch = searchText.trim().lowercase()

      sections = if (normalizedSearch.isEmpty()) {
        allSections
      } else {
        allSections.mapNotNull { section ->
          val filteredEntries = section.entries.filter { entry ->
            entry.metadata.name.lowercase().contains(normalizedSearch)
          }

          if (filteredEntries.isEmpty()) {
            null
          } else {
            AddonSection(section.title, filteredEntries)
          }
        }
      }

      scrollHandler.reset()
      rebuildComponents()
    }
  }

  override fun render() {
    NVGRenderer.rect(x, y, width, height, ThemeManager.currentTheme.background, 10F)

    topBar
      .updateBounds(x, y)
      .render()

    val startY = y + topBar.height
    val visibleHeight = height - topBar.height

    scrollHandler.setMaxScroll(contentHeight(sections) + CONTENT_PADDING * 2F, visibleHeight)
    NVGRenderer.pushScissor(x, startY, width, visibleHeight)

    val scrollOffset = scrollHandler.getOffset()
    layoutSections(x + CONTENT_PADDING, startY + CONTENT_PADDING - scrollOffset)
    sections.forEach { section ->
      section.header.render()
      section.entries.forEach(UIComponent::render)
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

  private fun buildAddonSections(): List<AddonSection> {
    val addonModules = AddonLoader.getAddons().flatMap { it.second.getModules() }.toSet()
    val builtinModules = ModuleManager.getModules().filter { it !in addonModules }
    val version = FabricLoader.getInstance()
      .getModContainer("cobalt")
      .map { it.metadata.version.friendlyString }
      .orElse("builtin")

    val miningModules = builtinModules.filter { module ->
      module.category == ModuleCategory.MINING ||
        module.javaClass.name.startsWith("org.cobalt.internal.mining.") ||
        module == MiningHudModule ||
        module.name.contains("Mining", ignoreCase = true) ||
        module.name.contains("Miner", ignoreCase = true) ||
        module.name.contains("Gemstone", ignoreCase = true) ||
        module.name.contains("Tunnel", ignoreCase = true) ||
        module.name.contains("Powder", ignoreCase = true) ||
        module.name.contains("Scatha", ignoreCase = true) ||
        module.name.contains("Commission", ignoreCase = true) ||
        module.name.contains("Ore", ignoreCase = true) ||
        module.name.contains("Forge", ignoreCase = true) ||
        module.name.contains("Lantern", ignoreCase = true)
    }

    val combatModules = builtinModules.filter {
      it == CombatMacroModule || it.name.equals("Combat HUD", ignoreCase = true)
    }

    val slayerModules = builtinModules.filterIsInstance<SlayerMacroModule>()

    val visualModules = builtinModules.filter {
      it == FullBrightModule || it == FreecamModule ||
        it == OrbitFreecamModule || it == BlockOverlayModule || it == BlockOutlineModule ||
        it == HotbarOverlayModule || it == PetDisplayModule ||
        it.name.equals("Watermark", ignoreCase = true) ||
        it.name.equals("Inventory HUD", ignoreCase = true)
    }

    val gardenModules = builtinModules.filter {
      it == GardenMacroModule || it == GardenAnalyzerModule
    }

    val coreModules = builtinModules.filter {
      it !in miningModules &&
        it !in combatModules &&
        it !in slayerModules &&
        it !in visualModules &&
        it !in gardenModules
    }

    val sections = mutableListOf<AddonSection>()

    createMiningSection(version, miningModules)?.let(sections::add)
    createSection(
      title = "Garden",
      entries = listOfNotNull(createBuiltinEntry("cobalt-garden", "Garden", version, gardenModules))
    )?.let(sections::add)
    createSection(
      title = "Combat",
      entries = listOfNotNull(createBuiltinEntry("cobalt-combat", "Combat", version, combatModules))
    )?.let(sections::add)
    createSlayerSection(version, slayerModules)?.let(sections::add)
    createSection(
      title = "Visuals",
      entries = listOfNotNull(createBuiltinEntry("cobalt-visuals", "Visuals", version, visualModules))
    )?.let(sections::add)
    createSection(
      title = "Core",
      entries = listOfNotNull(createBuiltinEntry("cobalt-core", "Core", version, coreModules))
    )?.let(sections::add)
    createSection(
      title = "Addons",
      entries = AddonLoader.getAddons().map { UIAddonEntry(it.first, it.second) }
    )?.let(sections::add)

    return sections
  }

  private fun createSlayerSection(version: String, slayerModules: List<SlayerMacroModule>): AddonSection? {
    return createSection(
      title = "Slayers",
      entries = slayerModules.mapNotNull { module ->
        val entryName = module.name.removeSuffix(" Macro")
        val entryId = "cobalt-slayer-${entryName.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')}"
        createBuiltinEntry(entryId, entryName, version, listOf(module))
      }
    )
  }

  private fun createMiningSection(version: String, miningModules: List<Module>): AddonSection? {
    fun moduleName(module: Module): String = module.name.trim()

    fun matchingModules(vararg patterns: String): List<Module> {
      val normalizedPatterns = patterns.map { it.lowercase() }
      return miningModules.filter { module ->
        val name = moduleName(module).lowercase()
        normalizedPatterns.any { pattern -> name.contains(pattern) }
      }
    }

    fun exactModules(vararg names: String): List<Module> {
      val normalizedNames = names.map { it.lowercase() }.toSet()
      return miningModules.filter { module -> moduleName(module).lowercase() in normalizedNames }
    }

    val coreMining = miningModules.filter { module ->
      module == MiningModule ||
        module == MiningHudModule ||
        module == RoutesModule ||
        moduleName(module).equals("Mining", ignoreCase = true) ||
        moduleName(module).equals("Mining Macro", ignoreCase = true) ||
        moduleName(module).equals("Routes", ignoreCase = true) ||
        moduleName(module).equals("Mining Coin Popups", ignoreCase = true) ||
        moduleName(module).equals("Mining Failsafes", ignoreCase = true) ||
        moduleName(module).equals("Ordered Route Debug HUD", ignoreCase = true) ||
        moduleName(module).equals("Strict Mining Block Guard", ignoreCase = true)
    }

    val commissionModules = miningModules.filter { module ->
      moduleName(module).contains("Commission", ignoreCase = true)
    }

    val gemstoneModules = miningModules.filter { module ->
      val name = moduleName(module)
      name.contains("Gemstone", ignoreCase = true) ||
        name.equals("Vein Direction Setter", ignoreCase = true) ||
        name.equals("Routes", ignoreCase = true)
    }

    val tunnelModules = miningModules.filter { module ->
      val name = moduleName(module)
      name.contains("Tunnel", ignoreCase = true) ||
        name.equals("Auto Lantern", ignoreCase = true) ||
        name.equals("Fairy", ignoreCase = true) ||
        name.equals("Vein Direction Setter", ignoreCase = true) ||
        name.equals("Routes", ignoreCase = true)
    }

    val blockMinerModules = exactModules("Block Miner", "Mining")
    val miningBotModules = matchingModules("mining bot")
    val oreModules = matchingModules("ore macro")
    val powderModules = matchingModules("powder")
    val scathaModules = matchingModules("scatha")
    val excavatorModules = matchingModules("excavator")
    val pinglessModules = matchingModules("pingless")
    val utilityModules = matchingModules("forge", "lantern", "fairy", "nofrills", "lobby hopper", "strict mining")

    val covered = (coreMining + blockMinerModules + gemstoneModules + tunnelModules + miningBotModules +
      commissionModules + oreModules + powderModules + scathaModules + excavatorModules +
      pinglessModules + utilityModules).toSet()
    val otherMiningModules = miningModules.filter { it !in covered }

    val finalEntries = listOfNotNull(
      createBuiltinEntry(
        "cobalt-mining-core",
        "Mining Core",
        version,
        coreMining
      ),
      createBuiltinEntry(
        "cobalt-mining-block",
        "Block Miner",
        version,
        blockMinerModules
      ),
      createBuiltinEntry(
        "cobalt-mining-gemstone",
        "Gemstone Miner",
        version,
        gemstoneModules
      ),
      createBuiltinEntry(
        "cobalt-mining-tunnel",
        "Tunnel Miner",
        version,
        tunnelModules
      ),
      createBuiltinEntry(
        "cobalt-mining-bot",
        "Mining Bot",
        version,
        miningBotModules
      ),
      createBuiltinEntry(
        "cobalt-mining-commission",
        "Commission Macros",
        version,
        commissionModules
      ),
      createBuiltinEntry(
        "cobalt-mining-ore",
        "Ore Macro",
        version,
        oreModules
      ),
      createBuiltinEntry(
        "cobalt-mining-powder",
        "Powder Macro",
        version,
        powderModules
      ),
      createBuiltinEntry(
        "cobalt-mining-scatha",
        "Scatha Macro",
        version,
        scathaModules
      ),
      createBuiltinEntry(
        "cobalt-mining-excavator",
        "Excavator Macro",
        version,
        excavatorModules
      ),
      createBuiltinEntry(
        "cobalt-mining-pingless",
        "Pingless Mining",
        version,
        pinglessModules
      ),
      createBuiltinEntry(
        "cobalt-mining-forge",
        "Forge / Utility",
        version,
        utilityModules
      ),
      createBuiltinEntry(
        "cobalt-mining-other",
        "Other Mining",
        version,
        otherMiningModules
      )
    )

    return createSection(
      title = "Mining",
      entries = finalEntries
    )
  }

  private fun createSection(
    title: String,
    entries: List<UIAddonEntry>,
  ): AddonSection? {
    if (entries.isEmpty()) {
      return null
    }

    return AddonSection(title, entries)
  }

  private fun createBuiltinEntry(
    id: String,
    name: String,
    version: String,
    modules: List<Module>,
  ): UIAddonEntry? {
    if (modules.isEmpty()) {
      return null
    }

    return UIAddonEntry(
      AddonMetadata(id, name, version, emptyList(), emptyList()),
      object : Addon() {
        override fun onLoad() {}
        override fun onUnload() {}
        override fun getModules() = modules
      }
    )
  }

  private fun rebuildComponents() {
    components.clear()
    sections.forEach { section ->
      components.add(section.header)
      components.addAll(section.entries)
    }
    components.add(topBar)
  }

  private fun layoutSections(startX: Float, startY: Float) {
    var currentY = startY

    sections.forEachIndexed { index, section ->
      section.header.updateBounds(startX, currentY)
      currentY += section.header.height + HEADER_SPACING

      gridLayout.layout(startX, currentY, section.entries)
      currentY += gridHeight(section.entries.size)

      if (index < sections.lastIndex) {
        currentY += SECTION_SPACING
      }
    }
  }

  private fun contentHeight(sections: List<AddonSection>): Float {
    if (sections.isEmpty()) {
      return 0F
    }

    var totalHeight = 0F

    sections.forEachIndexed { index, section ->
      totalHeight += section.header.height + HEADER_SPACING
      totalHeight += gridHeight(section.entries.size)

      if (index < sections.lastIndex) {
        totalHeight += SECTION_SPACING
      }
    }

    return totalHeight
  }

  private fun gridHeight(itemCount: Int): Float {
    if (itemCount <= 0) {
      return 0F
    }

    val rows = ceil(itemCount.toFloat() / GRID_COLUMNS.toFloat()).toInt()
    return rows * ENTRY_HEIGHT + (rows - 1) * GRID_GAP
  }

  private class AddonSection(
    val title: String,
    val entries: List<UIAddonEntry>,
  ) {
    val header = UISectionHeader(title, CONTENT_WIDTH)
  }

  companion object {
    private const val GRID_COLUMNS = 3
    private const val ENTRY_WIDTH = 270F
    private const val ENTRY_HEIGHT = 70F
    private const val GRID_GAP = 20F
    private const val CONTENT_PADDING = 20F
    private const val HEADER_SPACING = 8F
    private const val SECTION_SPACING = 20F
    private const val CONTENT_WIDTH = 850F
  }

}
