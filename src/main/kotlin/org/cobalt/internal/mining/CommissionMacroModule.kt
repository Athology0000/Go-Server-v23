package org.cobalt.internal.mining

import java.util.Locale
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.ChatEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.pathfinder.jni.PathStatus
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.InventoryUtils
import org.cobalt.api.util.getLoreLines
import org.cobalt.internal.combat.CombatMacroModule
import org.cobalt.internal.etherwarp.EtherwarpLogic
import org.cobalt.api.pathfinder.jni.NativePathfinder
import org.cobalt.api.util.player.MovementManager
import org.cobalt.internal.pathfinding.PathfindingModule
import org.cobalt.internal.ui.panel.panels.UIModuleList

object CommissionMacroModule : Module("Commission Macro") {

  private val mc = Minecraft.getInstance()

  // ---- Settings ----

  private val enabled = CheckboxSetting(
    "Enabled",
    "Complete commissions in a loop. Mode is auto-detected from AOTV and Royal Pigeon availability.",
    false
  )

  private val info = InfoSetting(
    "How It Works",
    "Modes: no AOTV/no pigeon = walk to emissary and walk route points, AOTV/no pigeon = warp dwarves -> Eliza -> warp forge -> route -> warp dwarves, AOTV + pigeon = use Royal Pigeon for claim/read and route with AOTV. Routes without AOTV still walk warp points.",
    InfoType.INFO
  )

  private val statusText  = TextSetting("Status",     "Current macro state.",  "Idle")
  private val modeText    = TextSetting("Mode",       "Auto-detected commission mode.", "Unknown")
  private val commText    = TextSetting("Commission", "Detected commission.",   "None")
  private val areaText    = TextSetting("Area",       "Detected map area.",     "Unknown")
  private val elizaXText  = TextSetting("Eliza X",    "Block X coordinate for Emissary Eliza.", "")
  private val elizaYText  = TextSetting("Eliza Y",    "Block Y coordinate for Emissary Eliza.", "")
  private val elizaZText  = TextSetting("Eliza Z",    "Block Z coordinate for Emissary Eliza.", "")
  private val selectedRoutesText = TextSetting(
    "Commission Routes",
    "Legacy fallback pool of saved route names for commission route matching.",
    ""
  ).apply {
    uiGroup = UIModuleList.SIDE_GROUP
  }
  private val royalRouteText = TextSetting(
    "Royal Route",
    "Saved route used for Royal Mines commissions.",
    ""
  ).apply {
    uiGroup = UIModuleList.SIDE_GROUP
  }
  private val cliffsideRouteText = TextSetting(
    "Cliffside Route",
    "Saved route used for Cliffside Veins commissions.",
    ""
  ).apply {
    uiGroup = UIModuleList.SIDE_GROUP
  }
  private val lavaRouteText = TextSetting(
    "Lava Route",
    "Saved route used for Lava Springs commissions.",
    ""
  ).apply {
    uiGroup = UIModuleList.SIDE_GROUP
  }
  private val rampRouteText = TextSetting(
    "Ramp Route",
    "Saved route used for Rampart's Quarry commissions.",
    ""
  ).apply {
    uiGroup = UIModuleList.SIDE_GROUP
  }
  private val upperRouteText = TextSetting(
    "Upper Route",
    "Saved route used for Upper Mines commissions.",
    ""
  ).apply {
    uiGroup = UIModuleList.SIDE_GROUP
  }

  // ---- Commission model ----

  private enum class CommissionType { MINING, COMBAT }

  private data class Commission(
    val label:   String,
    val type:    CommissionType,
    val target:  String,   // primary block-type name (mining) or mob name (combat)
    val mineTypes: String, // comma-separated block types to mine (may include variants)
    val current: Int,
    val max:     Int,
  ) {
    val isComplete get() = max > 0 && current >= max
  }

  data class CommissionHudRow(
    val label: String,
    val detail: String,
    val isTargeted: Boolean,
    val percent: Int,
  )

  private data class ParsedCommissionSelection(
    val commissions: List<Commission>,
    val selected: Commission?,
  )

  private enum class CommissionMode(
    val label: String,
    val usesWarps: Boolean,
    val usesPigeon: Boolean,
  ) {
    WALK_ONLY("Walk Only", false, false),
    WALK_PIGEON("Walk + Pigeon", false, true),
    AOTV_ELIZA("AOTV + Emissary", true, false),
    AOTV_PIGEON("AOTV + Pigeon", true, true),
  }

  enum class CommissionRouteZone(
    val label: String,
    val areaLabel: String,
  ) {
    ROYAL("Royal", "Royal Mines"),
    CLIFFSIDE("Cliffside", "Cliffside Veins"),
    LAVA("Lava", "Lava Springs"),
    RAMP("Ramp", "Rampart's Quarry"),
    UPPER("Upper", "Upper Mines"),
  }

  private data class EmissaryTarget(
    val walkPos: BlockPos,
    val interactionEntity: Entity?,
    val label: String,
  )

  // ---- State machine ----

  private enum class State {
    IDLE,
    OPEN_PIGEON,        // select + right-click Royal Pigeon item; wait for GUI
    WARP_TO_DWARVES,    // /warp dwarves sent; waiting for teleport
    WALK_TO_EMISSARY,   // pathfind to Emissary Eliza
    OPEN_EMISSARY,      // face + right-click Emissary Eliza; wait for GUI
    READ_GUI,           // GUI open: scrape book lore for commissions
    CLAIM_GUI,          // GUI open: click the claim button on a complete commission
    WARP_TO_FORGE,      // /warp forge sent; waiting for teleport before route start
    MINING,             // MiningMacroModule (or CombatMacroModule) is active
    RETURN_TO_DWARVES,  // commission done; warp back to Eliza to claim
  }

  private var state     = State.IDLE
  private var stateTick = 0L

  private var currentMode:       CommissionMode = CommissionMode.WALK_ONLY
  private var commission:        Commission? = null
  private var activeCommissions: List<Commission> = emptyList()
  private var activeMiningRouteName: String? = null
  private var pendingUseRelease  = false
  private var pendingSlotRestore = -1
  private var openAttempts       = 0
  private var claimAttempts      = 0
  private var readAttempts       = 0
  private var lastEmissaryPathTarget: BlockPos? = null

  // ---- Known areas and ore->zone mapping ----

  private val AREA_NAMES = listOf(
    "Dwarven Mines", "Crystal Hollows", "Glacite Tunnels",
    "Deep Caverns", "Spider's Den", "The End", "Crimson Isle"
  )

  // Maps ore keyword (in lore) to primary type label used for zone lookup
  private val ORE_TO_TYPE = linkedMapOf(
    "titanium"    to "Titanium",
    "mithril"     to "Mithril (Gray)",
    "ruby"        to "Ruby Gemstone",
    "amber"       to "Amber Gemstone",
    "amethyst"    to "Amethyst Gemstone",
    "jade"        to "Jade Gemstone",
    "sapphire"    to "Sapphire Gemstone",
    "opal"        to "Opal Gemstone",
    "topaz"       to "Topaz Gemstone",
    "jasper"      to "Jasper Gemstone",
    "onyx"        to "Onyx Gemstone",
    "aquamarine"  to "Aquamarine Gemstone",
    "citrine"     to "Citrine Gemstone",
    "peridot"     to "Peridot Gemstone",
    "umber"       to "Umber",
    "tungsten"    to "Tungsten",
    "glacite"     to "Glacite",
    "sulphur"     to "Sulphur",
    "coal"        to "Pure Coal",
    "iron"        to "Pure Iron",
    "gold"        to "Pure Gold",
    "lapis"       to "Pure Lapis",
    "redstone"    to "Pure Redstone",
    "emerald"     to "Pure Emerald",
    "diamond"     to "Pure Diamond",
    "quartz"      to "Pure Quartz",
  )

  // All block types to mine for a given ore keyword (expands mithril to all variants)
  private val ORE_TO_MINE_TYPES = linkedMapOf(
    "mithril"     to "Mithril (Gray), Mithril (Dark), Mithril (Hot)",
    "titanium"    to "Titanium",
    "ruby"        to "Ruby Gemstone",
    "amber"       to "Amber Gemstone",
    "amethyst"    to "Amethyst Gemstone",
    "jade"        to "Jade Gemstone",
    "sapphire"    to "Sapphire Gemstone",
    "opal"        to "Opal Gemstone",
    "topaz"       to "Topaz Gemstone",
    "jasper"      to "Jasper Gemstone",
    "onyx"        to "Onyx Gemstone",
    "aquamarine"  to "Aquamarine Gemstone",
    "citrine"     to "Citrine Gemstone",
    "peridot"     to "Peridot Gemstone",
    "umber"       to "Umber",
    "tungsten"    to "Tungsten",
    "glacite"     to "Glacite",
    "sulphur"     to "Sulphur",
    "coal"        to "Pure Coal",
    "iron"        to "Pure Iron",
    "gold"        to "Pure Gold",
    "lapis"       to "Pure Lapis",
    "redstone"    to "Pure Redstone",
    "emerald"     to "Pure Emerald",
    "diamond"     to "Pure Diamond",
    "quartz"      to "Pure Quartz",
  )

  private val ORE_TO_ZONE = mapOf(
    "Titanium"            to "Dwarven Mines",
    "Mithril (Gray)"      to "Dwarven Mines",
    "Pure Coal"           to "Dwarven Mines",
    "Pure Iron"           to "Dwarven Mines",
    "Pure Gold"           to "Dwarven Mines",
    "Pure Lapis"          to "Dwarven Mines",
    "Pure Redstone"       to "Dwarven Mines",
    "Pure Emerald"        to "Dwarven Mines",
    "Pure Diamond"        to "Dwarven Mines",
    "Pure Quartz"         to "Dwarven Mines",
    "Ruby Gemstone"       to "Crystal Hollows",
    "Amber Gemstone"      to "Crystal Hollows",
    "Amethyst Gemstone"   to "Crystal Hollows",
    "Jade Gemstone"       to "Crystal Hollows",
    "Sapphire Gemstone"   to "Crystal Hollows",
    "Opal Gemstone"       to "Crystal Hollows",
    "Topaz Gemstone"      to "Crystal Hollows",
    "Jasper Gemstone"     to "Crystal Hollows",
    "Umber"               to "Glacite Tunnels",
    "Tungsten"            to "Glacite Tunnels",
    "Glacite"             to "Glacite Tunnels",
    "Sulphur"             to "Glacite Tunnels",
    "Onyx Gemstone"       to "Glacite Tunnels",
    "Aquamarine Gemstone" to "Glacite Tunnels",
    "Citrine Gemstone"    to "Glacite Tunnels",
    "Peridot Gemstone"    to "Glacite Tunnels",
  )

  // Lower index = higher priority
  private val COMMISSION_PRIORITY = listOf(
    "Royal Mines Titanium",
    "Cliffside Veins Titanium",
    "Lava Springs Titanium",
    "Rampart's Quarry Titanium",
    "Upper Mines Titanium",
    "Royal Mines Mithril",
    "Cliffside Veins Mithril",
    "Lava Springs Mithril",
    "Rampart's Quarry Mithril",
    "Upper Mines Mithril",
    "Glacite Walker Slayer",
    "Goblin Slayer",
    "Goblin Raid",
    "Star Sentry Puncher",
    "Titanium Miner",
    "Mithril Miner",
  )

  private val COMMISSION_OBJECTIVE_PREFIXES = listOf("mine ", "kill ", "slay ", "defeat ", "punch ")

  private val COMMISSION_PRIORITY_INDEX =
    COMMISSION_PRIORITY.withIndex().associate { (index, label) -> normalizeComparisonText(label) to index }

  private val MINING_AREA_ALIASES = linkedMapOf(
    "royal mines" to "Royal Mines",
    "cliffside veins" to "Cliffside Veins",
    "lava springs" to "Lava Springs",
    "rampart's quarry" to "Rampart's Quarry",
    "ramparts quarry" to "Rampart's Quarry",
    "upper mines" to "Upper Mines",
  )

  private val COMMISSION_ROUTE_ZONE_ALIASES = linkedMapOf(
    CommissionRouteZone.ROYAL to listOf("royal mines", "royal"),
    CommissionRouteZone.CLIFFSIDE to listOf("cliffside veins", "cliffside"),
    CommissionRouteZone.LAVA to listOf("lava springs", "lava"),
    CommissionRouteZone.RAMP to listOf("rampart's quarry", "ramparts quarry", "ramp"),
    CommissionRouteZone.UPPER to listOf("upper mines", "upper"),
  )

  // ---- Public HUD ----

  val statusDisplay:      String  get() = statusText.value
  val modeDisplay:        String  get() = modeText.value
  val commissionDisplay:  String  get() = commText.value
  val currentZoneDisplay: String  get() = areaText.value
  val targetedCommissionName: String? get() = commission?.label
  fun getCommissionRows(): List<CommissionHudRow> =
    activeCommissions.map { active ->
      val percent =
        if (active.max > 0) ((active.current.toDouble() / active.max.toDouble()) * 100.0).toInt().coerceIn(0, 100)
        else 0
      CommissionHudRow(
        label      = active.label,
        detail     = "$percent%",
        isTargeted = active == commission,
        percent    = percent,
      )
    }

  val targetZoneDisplay: String get() {
    val c = commission ?: return "Unknown"
    return if (c.type == CommissionType.MINING) ORE_TO_ZONE[c.target] ?: "Unknown" else "Combat Zone"
  }
  val isRunning: Boolean get() = enabled.value

  fun getAvailableRouteInfos(): List<RoutesModule.SavedRouteInfo> = RoutesModule.getSavedRouteInfos()

  fun getRouteZones(): List<CommissionRouteZone> = CommissionRouteZone.values().toList()

  fun getAssignedRouteName(zone: CommissionRouteZone): String? =
    routeSettingForZone(zone).value.trim().takeIf { it.isNotEmpty() }

  fun isRouteAssigned(zone: CommissionRouteZone, name: String): Boolean {
    val assigned = getAssignedRouteName(zone) ?: return false
    return assigned.equals(name.trim(), ignoreCase = false)
  }

  fun assignRoute(zone: CommissionRouteZone, name: String?) {
    routeSettingForZone(zone).value = name?.trim().orEmpty()
  }

  fun clearRouteAssignments() {
    getRouteZones().forEach { zone -> assignRoute(zone, null) }
  }

  fun getSelectedRouteNames(): List<String> = parseSelectedRouteNames(selectedRoutesText.value)

  fun isRouteSelected(name: String): Boolean = getSelectedRouteNames().any { it.equals(name.trim(), ignoreCase = false) }

  fun toggleRouteSelection(name: String) {
    setRouteSelected(name, !isRouteSelected(name))
  }

  fun setRouteSelected(name: String, selected: Boolean) {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return
    val updated = getSelectedRouteNames().toMutableList()
    updated.removeAll { it.equals(trimmed, ignoreCase = false) }
    if (selected) {
      updated.add(trimmed)
    }
    selectedRoutesText.value = updated.joinToString("|")
  }

  fun clearRouteSelections() {
    selectedRoutesText.value = ""
  }

  init {
    addSetting(
      enabled,
      info,
      statusText,
      modeText,
      commText,
      areaText,
      elizaXText,
      elizaYText,
      elizaZText,
      selectedRoutesText,
      royalRouteText,
      cliffsideRouteText,
      lavaRouteText,
      rampRouteText,
      upperRouteText,
    )
    EventBus.register(this)
  }

  // ---- Tick ----

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (pendingUseRelease) {
      mc.options.keyUse?.setDown(false)
      pendingUseRelease = false
    }
    if (pendingSlotRestore >= 0) {
      pressHotbarSlot(pendingSlotRestore)
      pendingSlotRestore = -1
    }

    if (!enabled.value) {
      if (state != State.IDLE) resetMacro()
      return
    }

    val player = mc.player ?: return
    stateTick++

    when (state) {
      State.IDLE            -> {
        val mode = refreshMode(player)
        if (!ensureCommissionSourceAvailable(player, mode)) return
        ChatUtils.sendMessage("Commission Macro: starting in ${mode.label} mode.")
        transitionToLoopStart(mode)
      }
      State.OPEN_PIGEON     -> handleOpenPigeon(player)
      State.WARP_TO_DWARVES -> handleWarpToDwarves()
      State.WALK_TO_EMISSARY -> handleWalkToEmissary(player)
      State.OPEN_EMISSARY   -> handleOpenEmissary(player)
      State.READ_GUI        -> handleReadGui()
      State.CLAIM_GUI       -> handleClaimGui()
      State.WARP_TO_FORGE   -> handleWarpToForge()
      State.MINING          -> handleMining()
      State.RETURN_TO_DWARVES -> handleReturnToDwarves()
    }
  }

  // ---- Chat ----

  @SubscribeEvent
  fun onChat(event: ChatEvent.Receive) {
    if (!enabled.value) return
    val msg = event.message?.lowercase() ?: return

    if (state == State.MINING) {
      val isComplete = msg.contains("commission complete") ||
        msg.contains("completed a commission") ||
        msg.contains("you've completed") ||
        msg.contains("commission completed") ||
        (msg.contains("commission") && msg.contains("complete"))
      if (isComplete) {
        val player = mc.player
        val mode = refreshMode(player)
        val destination =
          when {
            mode.usesPigeon -> "Royal Pigeon"
            mode.usesWarps -> "Emissary Eliza"
            else -> "emissary"
          }
        setStatus("Commission complete! Returning to $destination...")
        ChatUtils.sendMessage("Commission Macro: commission complete, returning to $destination.")
        stopWorkModule()
        transitionToTurnIn(mode)
      }
    }
  }

  // ---- State: OPEN_PIGEON ----

  private fun handleOpenPigeon(player: Player) {
    val mode = refreshMode(player)
    if (!mode.usesPigeon) {
      transitionToCommissionSource(mode, alreadyAtSource = false)
      return
    }

    if (mc.screen is AbstractContainerScreen<*>) {
      openAttempts = 0
      transition(State.READ_GUI)
      return
    }

    if (stateTick % 10L != 0L) return

    val pigeonSlot = findPigeonHotbarSlot(player)
    if (pigeonSlot !in 0..8) {
      transitionToCommissionSource(mode, alreadyAtSource = false)
      return
    }

    if (openAttempts >= 6) {
      ChatUtils.sendMessage("Commission Macro: could not open Royal Pigeon GUI. Disabling.")
      enabled.value = false
      return
    }

    val restoreSlot = player.inventory.selectedSlot
    if (restoreSlot in 0..8 && restoreSlot != pigeonSlot) {
      pendingSlotRestore = restoreSlot
    }
    pressHotbarSlot(pigeonSlot)
    mc.options.keyUse?.setDown(false)
    mc.options.keyUse?.setDown(true)
    pendingUseRelease = true
    openAttempts++
    setStatus("Opening Royal Pigeon... (attempt $openAttempts)")
  }

  // ---- State: WARP_TO_DWARVES ----

  private fun handleWarpToDwarves() {
    if (stateTick == 1L) {
      (mc.player as? LocalPlayer)?.connection?.sendCommand("warp dwarves")
      areaText.value = "Dwarven Mines"
      setStatus("Warping to dwarves...")
    }
    if (stateTick >= 100L) {
      openAttempts = 0
      readAttempts = 0
      transition(State.WALK_TO_EMISSARY)
    }
  }

  // ---- State: WALK_TO_EMISSARY ----

  private fun handleWalkToEmissary(player: Player) {
    if (mc.screen is AbstractContainerScreen<*>) {
      stopEmissaryNavigation()
      openAttempts = 0
      transition(State.READ_GUI)
      return
    }

    val target = resolveEmissaryTarget(player, refreshMode(player))
    if (target == null) {
      disableForMissingEmissaryTarget()
      return
    }

    val emissary = target.interactionEntity
    if (emissary != null && player.distanceToSqr(emissary) <= 12.25) {
      stopEmissaryNavigation()
      transition(State.OPEN_EMISSARY)
      return
    }

    val walkTarget = target.walkPos
    if (lastEmissaryPathTarget != walkTarget || !nativePathActive() || stateTick % 40L == 1L) {
      PathfindingModule.ensureEnabledForAutomation("commission macro")
      PathfindingModule.startTo(walkTarget.x + 0.5, walkTarget.y.toDouble(), walkTarget.z + 0.5)
      lastEmissaryPathTarget = walkTarget
    }
    setStatus("Walking to ${target.label}...")
  }

  // ---- State: OPEN_EMISSARY ----

  private fun handleOpenEmissary(player: Player) {
    if (mc.screen is AbstractContainerScreen<*>) {
      openAttempts = 0
      transition(State.READ_GUI)
      return
    }

    val target = resolveEmissaryTarget(player, refreshMode(player))
    if (target == null) {
      disableForMissingEmissaryTarget()
      return
    }

    val emissary = target.interactionEntity
    if (emissary == null) {
      if (player.blockPosition().distSqr(target.walkPos) <= 25.0 && stateTick > 80L) {
        ChatUtils.sendMessage("Commission Macro: reached emissary target but could not find ${target.label}. Disabling.")
        enabled.value = false
        return
      }
      transition(State.WALK_TO_EMISSARY)
      return
    }
    if (player.distanceToSqr(emissary) > 12.25) {
      transition(State.WALK_TO_EMISSARY)
      return
    }

    stopEmissaryNavigation()
    faceEntity(emissary)

    if (stateTick == 1L) {
      setStatus("Facing ${target.label}...")
      return
    }
    if (stateTick % 4L != 0L) return
    if (openAttempts >= 8) {
      ChatUtils.sendMessage("Commission Macro: could not open ${target.label} after 8 attempts. Disabling.")
      enabled.value = false
      return
    }

    mc.options.keyUse?.setDown(false)
    mc.options.keyUse?.setDown(true)
    pendingUseRelease = true
    openAttempts++
    setStatus("Opening ${target.label}... (attempt $openAttempts)")
  }

  // ---- State: WARP_TO_FORGE ----

  private fun handleWarpToForge() {
    if (stateTick == 1L) {
      (mc.player as? LocalPlayer)?.connection?.sendCommand("warp forge")
      areaText.value = "Forge"
      setStatus("Warping to forge for route...")
    }
    if (stateTick >= 100L) {
      transition(State.MINING)
    }
  }

  // ---- State: READ_GUI ----

  private fun handleReadGui() {
    val player = mc.player
    val mode = refreshMode(player)
    val screen = mc.screen as? AbstractContainerScreen<*>
    if (screen == null) {
      transitionToCommissionSource(mode, alreadyAtSource = false)
      return
    }

    if (stateTick < 5) return
    if (stateTick % 5 != 0L) return

    val claimSlot = findClaimSlot(screen)
    if (claimSlot >= 0) {
      setStatus("Found claimable commission - claiming...")
      transition(State.CLAIM_GUI)
      return
    }

    val parsed = parseCommissionSelectionFromGui(screen)
    activeCommissions = parsed.commissions
    val selected = parsed.selected

    if (selected != null) {
      commission = selected
      commText.value = "${selected.type.name.lowercase().replaceFirstChar { it.uppercase() }}: ${selected.label} (${selected.current}/${selected.max})"

      // Print all available commissions to chat
      if (activeCommissions.isNotEmpty()) {
        ChatUtils.sendMessage("Commission Macro: available commissions:")
        activeCommissions.forEachIndexed { i, c ->
          val pct = if (c.max > 0) "${c.current}/${c.max}" else "?"
          val marker = if (c == selected) " \u25C4 selected" else ""
          ChatUtils.sendMessage("  ${i + 1}. ${c.label} ($pct)$marker")
        }
      }

      mc.player?.closeContainer()
      openAttempts = 0
      setStatus("Commission: ${selected.label}")
      if (mode.usesWarps) {
        transition(State.WARP_TO_FORGE)
      } else {
        transition(State.MINING)
      }
      return
    }

    readAttempts++
    if (readAttempts >= 30) {
      mc.player?.closeContainer()
      val source = if (mode.usesPigeon) "Royal Pigeon" else "emissary GUI"
      ChatUtils.sendMessage("Commission Macro: could not read any commission from $source. Disabling.")
      enabled.value = false
    }
  }

  // ---- State: CLAIM_GUI ----

  private fun handleClaimGui() {
    val player = mc.player
    val mode = refreshMode(player)
    val screen = mc.screen as? AbstractContainerScreen<*>
    if (screen == null) {
      resetCommissionState()
      transitionToCommissionSource(mode, alreadyAtSource = false)
      return
    }

    if (stateTick < 3) return

    val claimSlot = findClaimSlot(screen)
    if (claimSlot >= 0) {
      InventoryUtils.clickSlot(claimSlot)
      claimAttempts++
      setStatus("Claiming commission (slot $claimSlot, attempt $claimAttempts)...")
      if (claimAttempts >= 3) {
        mc.player?.closeContainer()
        ChatUtils.sendMessage("Commission Macro: claimed! Restarting loop...")
        resetCommissionState()
        transitionToCommissionSource(mode, alreadyAtSource = false)
      }
      return
    }

    mc.player?.closeContainer()
    resetCommissionState()
    transitionToCommissionSource(mode, alreadyAtSource = false)
  }

  // ---- State: MINING ----

  private fun handleMining() {
    val player = mc.player
    val mode = refreshMode(player)
    val c = commission ?: run {
      transitionToCommissionSource(mode, alreadyAtSource = false)
      return
    }

    if (!workModuleIsActive(c)) {
      stopEmissaryNavigation()
      startWorkModule(c)
      val action = if (c.type == CommissionType.MINING) "Mining" else "Combat"
      setStatus("$action: ${c.label}")
      ChatUtils.sendMessage("Commission Macro: starting ${action.lowercase()} - ${c.label}")
    }
  }

  // ---- State: RETURN_TO_DWARVES ----

  private fun handleReturnToDwarves() {
    val mode = refreshMode(mc.player)
    if (!mode.usesWarps || mode.usesPigeon) {
      transitionToTurnIn(mode)
      return
    }
    if (stateTick == 1L) {
      (mc.player as? LocalPlayer)?.connection?.sendCommand("warp dwarves")
      areaText.value = "Dwarven Mines"
      setStatus("Returning to Emissary Eliza...")
    }
    if (stateTick >= 100L) {
      openAttempts = 0
      readAttempts = 0
      claimAttempts = 0
      transition(State.WALK_TO_EMISSARY)
    }
  }

  // ---- Helpers ----

  private fun refreshMode(player: Player?): CommissionMode {
    val resolved =
      when {
        player != null && findAotvHotbarSlot(player) in 0..8 && findPigeonHotbarSlot(player) in 0..8 -> CommissionMode.AOTV_PIGEON
        player != null && findAotvHotbarSlot(player) in 0..8 -> CommissionMode.AOTV_ELIZA
        player != null && findPigeonHotbarSlot(player) in 0..8 -> CommissionMode.WALK_PIGEON
        else -> CommissionMode.WALK_ONLY
      }
    currentMode = resolved
    modeText.value = resolved.label
    return resolved
  }

  private fun ensureCommissionSourceAvailable(player: Player, mode: CommissionMode): Boolean {
    if (mode.usesPigeon) return true
    if (resolveEmissaryTarget(player, mode) != null) return true
    disableForMissingEmissaryTarget()
    return false
  }

  private fun transitionToLoopStart(mode: CommissionMode) {
    when {
      mode.usesPigeon -> {
        setStatus("Opening Royal Pigeon...")
        transition(State.OPEN_PIGEON)
      }
      mode.usesWarps -> {
        setStatus("Warping to dwarves...")
        transition(State.WARP_TO_DWARVES)
      }
      else -> {
        setStatus("Walking to emissary...")
        transition(State.WALK_TO_EMISSARY)
      }
    }
  }

  private fun transitionToTurnIn(mode: CommissionMode) {
    openAttempts = 0
    readAttempts = 0
    claimAttempts = 0
    when {
      mode.usesPigeon -> {
        setStatus("Opening Royal Pigeon...")
        transition(State.OPEN_PIGEON)
      }
      mode.usesWarps -> {
        setStatus("Returning to Emissary Eliza...")
        transition(State.RETURN_TO_DWARVES)
      }
      else -> {
        setStatus("Walking to emissary...")
        transition(State.WALK_TO_EMISSARY)
      }
    }
  }

  private fun transitionToCommissionSource(mode: CommissionMode, alreadyAtSource: Boolean) {
    openAttempts = 0
    readAttempts = 0
    claimAttempts = 0
    when {
      mode.usesPigeon -> {
        setStatus("Opening Royal Pigeon...")
        transition(State.OPEN_PIGEON)
      }
      alreadyAtSource -> {
        transition(State.OPEN_EMISSARY)
      }
      else -> {
        transition(State.WALK_TO_EMISSARY)
      }
    }
  }

  private fun getElizaTargetPos(): BlockPos? {
    val x = elizaXText.value.trim().toDoubleOrNull() ?: return null
    val y = elizaYText.value.trim().toDoubleOrNull() ?: return null
    val z = elizaZText.value.trim().toDoubleOrNull() ?: return null
    return BlockPos.containing(x, y, z)
  }

  private fun disableForMissingEmissaryTarget() {
    setStatus("Missing emissary target")
    ChatUtils.sendMessage("Commission Macro: configure Eliza X/Y/Z or stand near a loaded emissary before starting. Disabling.")
    enabled.value = false
  }

  private fun resolveEmissaryTarget(player: Player, mode: CommissionMode): EmissaryTarget? {
    if (!mode.usesWarps) {
      val nearestLoaded = findNearestLoadedEmissaryInteractionEntity(player)
      if (nearestLoaded != null) {
        return EmissaryTarget(findWalkTargetNear(nearestLoaded.blockPosition()), nearestLoaded, formatEmissaryLabel(nearestLoaded))
      }
    }

    val elizaPos = getElizaTargetPos() ?: return null
    return EmissaryTarget(findWalkTargetNear(elizaPos), findEmissaryInteractionEntity(elizaPos), "Emissary Eliza")
  }

  private fun findEmissaryInteractionEntity(targetPos: BlockPos): Entity? {
    val level = mc.level ?: return null
    val player = mc.player ?: return null
    val anchor = level.entitiesForRendering()
      .asSequence()
      .filter { it != player }
      .filter { it.distanceToSqr(targetPos.x + 0.5, targetPos.y.toDouble(), targetPos.z + 0.5) <= 64.0 }
      .filter { entityNameMatchesEliza(it) }
      .sortedWith(compareBy<Entity>({ if (it is ArmorStand) 1 else 0 }, { player.distanceToSqr(it) }))
      .firstOrNull() ?: return null
    if (anchor !is ArmorStand) return anchor

    return level.entitiesForRendering()
      .asSequence()
      .filter { it != player && it !is ArmorStand && it.distanceToSqr(anchor) <= 16.0 }
      .sortedWith(compareBy<Entity>({ if (it is Player) 0 else 1 }, { anchor.distanceToSqr(it) }))
      .firstOrNull() ?: anchor
  }

  private fun findNearestLoadedEmissaryInteractionEntity(player: Player): Entity? {
    val level = mc.level ?: return null
    val anchors = level.entitiesForRendering()
      .asSequence()
      .filter { it != player }
      .filter { entityNameMatchesAnyEmissary(it) }
      .sortedBy { player.distanceToSqr(it) }
      .toList()
    if (anchors.isEmpty()) return null

    val anchor = anchors.first()
    if (anchor !is ArmorStand) return anchor

    return level.entitiesForRendering()
      .asSequence()
      .filter { it != player && it !is ArmorStand && it.distanceToSqr(anchor) <= 16.0 }
      .sortedWith(compareBy<Entity>({ if (it is Player) 0 else 1 }, { anchor.distanceToSqr(it) }))
      .firstOrNull() ?: anchor
  }

  private fun entityNameMatchesEliza(entity: Entity): Boolean {
    val name = normalizeName(entity.name.string)
    return name.contains("emissary eliza") || name == "eliza"
  }

  private fun entityNameMatchesAnyEmissary(entity: Entity): Boolean {
    val name = normalizeName(entity.name.string)
    return name.contains("emissary") || name == "eliza"
  }

  private fun formatEmissaryLabel(entity: Entity): String {
    val normalized = normalizeName(entity.name.string)
    return normalized
      .removePrefix("emissary ")
      .ifBlank { "emissary" }
      .let(::titleCase)
      .let { if (it.startsWith("Emissary ")) it else "Emissary $it" }
  }

  private fun findAotvHotbarSlot(player: Player): Int {
    for (i in 0..8) {
      val stack = player.inventory.getItem(i)
      if (stack.isEmpty) continue
      if (!EtherwarpLogic.isEtherwarpStack(stack)) continue

      val name = ChatFormatting.stripFormatting(stack.hoverName.string)?.lowercase(Locale.US) ?: continue
      if (name.contains("aspect of the void") || name.contains("warped aspect of the void")) return i
    }
    return -1
  }

  private fun findPigeonHotbarSlot(player: Player): Int {
    for (i in 0..8) {
      val stack = player.inventory.getItem(i)
      if (stack.isEmpty) continue
      val name = ChatFormatting.stripFormatting(stack.hoverName.string)?.lowercase(Locale.US) ?: continue
      if (name.contains("royal pigeon")) return i
    }
    return -1
  }

  private fun pressHotbarSlot(slot: Int) {
    if (slot !in 0..8) return
    mc.player?.inventory?.selectedSlot = slot
  }

  private fun normalizeName(raw: String): String {
    return ChatFormatting.stripFormatting(raw)
      ?.lowercase(Locale.US)
      ?.replace(Regex("\\s+"), " ")
      ?.trim()
      .orEmpty()
  }

  private fun findWalkTargetNear(base: BlockPos): BlockPos {
    val level = mc.level ?: return base
    if (MinecraftPathingRules.isWalkable(level, base)) return base

    for (radius in 1..3) {
      for (dy in -2..2) {
        for (dx in -radius..radius) {
          for (dz in -radius..radius) {
            val candidate = base.offset(dx, dy, dz)
            if (MinecraftPathingRules.isWalkable(level, candidate)) return candidate
          }
        }
      }
    }

    return base
  }

  private fun faceEntity(entity: Entity) {
    val player = mc.player ?: return
    val rotation = AngleUtils.getRotation(entity)
    player.setYRot(rotation.yaw)
    player.setXRot(rotation.pitch)
    player.yHeadRot = rotation.yaw
  }

  private fun nativePathActive(): Boolean =
    NativePathfinder.status.let { it != PathStatus.IDLE && it != PathStatus.ARRIVED && it != PathStatus.FAILED }

  private fun stopEmissaryNavigation() {
    if (nativePathActive()) {
      PathfindingModule.stopPath()
    }
    lastEmissaryPathTarget = null
  }

  /** Parse active commissions from the open GUI, prioritising exact known commission names first. */
  private fun parseCommissionSelectionFromGui(screen: AbstractContainerScreen<*>): ParsedCommissionSelection {
    val commissions = mutableListOf<Commission>()
    for (slot in getCommissionCandidateSlots(screen)) {
      if (!slot.hasItem()) continue
      val item = slot.item
      val lines = buildGuiTextLines(item)
      if (lines.isEmpty()) continue
      val combined = lines.joinToString("\n")
      if (!looksLikeCommissionEntry(lines, combined)) continue
      if (isClaimCommissionText(combined)) continue

      val (current, max) = parseCommissionProgress(combined)
      if (max > 0 && current >= max) continue

      val miningKeyword = extractMiningKeyword(combined)
      val miningTarget  = miningKeyword?.let { ORE_TO_TYPE[it] }
      val mineTypes     = miningKeyword?.let { ORE_TO_MINE_TYPES[it] }
      val combatTarget  = extractCombatCommissionTarget(lines)
      val label         = buildCommissionLabel(item, lines, miningTarget, combatTarget)

      if (miningTarget != null && mineTypes != null) {
        commissions += Commission(label, CommissionType.MINING, miningTarget, mineTypes, current, max)
        continue
      }
      if (combatTarget != null) {
        commissions += Commission(label, CommissionType.COMBAT, combatTarget, combatTarget, current, max)
      }
    }

    // The commission GUI can expose the same commission in multiple slots.
    // Collapse duplicates before selecting a target or printing the list.
    val unique = commissions.distinctBy { commission ->
      listOf(
        commission.type.name,
        commission.label.lowercase(Locale.US),
        commission.target.lowercase(Locale.US),
        commission.mineTypes.lowercase(Locale.US),
        commission.current.toString(),
        commission.max.toString(),
      ).joinToString("|")
    }

    val sorted = unique.sortedWith(
      compareBy<Commission>(
        { commissionSelectionRank(it) },
        { if (it.type == CommissionType.MINING) 0 else 1 },
        { normalizeComparisonText(it.label) }
      )
    )

    val selected = sorted.firstOrNull()
    return ParsedCommissionSelection(sorted, selected)
  }

  private fun findClaimSlot(screen: AbstractContainerScreen<*>): Int {
    for (slot in getCommissionCandidateSlots(screen)) {
      if (!slot.hasItem()) continue
      val item = slot.item
      val lines = buildGuiTextLines(item)
      if (lines.isEmpty()) continue
      val combined = lines.joinToString("\n")
      if (looksLikeCommissionEntry(lines, combined) && isClaimCommissionText(combined)) return slot.index
    }
    return -1
  }

  private fun getCommissionCandidateSlots(screen: AbstractContainerScreen<*>): List<net.minecraft.world.inventory.Slot> {
    val containerSlots = screen.menu.slots.filterNot { it.container is Inventory }
    return if (containerSlots.isNotEmpty()) containerSlots else screen.menu.slots
  }

  private fun buildGuiTextLines(item: net.minecraft.world.item.ItemStack): List<String> {
    val lines = mutableListOf<String>()
    val name = ChatFormatting.stripFormatting(item.hoverName.string)?.lowercase()?.trim().orEmpty()
    if (name.isNotBlank()) lines += name
    lines += item.getLoreLines()
      .mapNotNull { ChatFormatting.stripFormatting(it.string)?.lowercase()?.trim() }
      .filter { it.isNotBlank() }
    return lines
  }

  private fun buildCommissionLabel(
    item: net.minecraft.world.item.ItemStack,
    lines: List<String>,
    miningTarget: String?,
    combatTarget: String?,
  ): String {
    val itemName = ChatFormatting.stripFormatting(item.hoverName.string)
      ?.replace(Regex("\\s+"), " ")?.trim().orEmpty()
    if (itemName.isNotBlank() && !itemName.matches(Regex("Commission\\s*#\\d+", RegexOption.IGNORE_CASE))) {
      canonicalizeKnownCommissionLabel(itemName)?.let { return it }
      return itemName
    }
    if (miningTarget != null) return buildMiningCommissionLabel(lines, miningTarget)
    if (combatTarget != null) return buildCombatCommissionLabel(lines, combatTarget)
    return itemName.ifBlank { "Unknown Commission" }
  }

  private fun buildMiningCommissionLabel(lines: List<String>, miningTarget: String): String {
    val area = extractMiningAreaLabel(lines)
    val suffix = miningCommissionSuffix(miningTarget)
    if (area != null) return "$area $suffix"
    return when (suffix) {
      "Titanium" -> "Titanium Miner"
      "Mithril" -> "Mithril Miner"
      else -> suffix
    }
  }

  private fun buildCombatCommissionLabel(lines: List<String>, combatTarget: String): String {
    val normalizedTarget = normalizeComparisonText(combatTarget)
    return when {
      normalizedTarget.contains("glacite walker") -> "Glacite Walker Slayer"
      normalizedTarget.contains("star sentry") -> "Star Sentry Puncher"
      lines.any { normalizeComparisonText(it).contains("goblin raid") } -> "Goblin Raid"
      normalizedTarget.contains("goblin") -> "Goblin Slayer"
      else -> {
        val objectiveLine = lines.firstOrNull {
          COMMISSION_OBJECTIVE_PREFIXES.any(it::startsWith)
        }.orEmpty()
        val area = objectiveLine.substringAfter(" in ", "").substringBefore(" progress")
          .replace(Regex("[^a-z0-9' -]"), " ").replace(Regex("\\s+"), " ").trim()
        val targetLabel = titleCase(combatTarget)
        if (area.isNotBlank() && area != objectiveLine) "${titleCase(area)} $targetLabel" else targetLabel
      }
    }
  }

  private fun canonicalizeKnownCommissionLabel(raw: String): String? {
    val normalized = normalizeComparisonText(raw)
    return COMMISSION_PRIORITY.firstOrNull { normalizeComparisonText(it) == normalized }
  }

  private fun extractMiningAreaLabel(lines: List<String>): String? {
    val objectiveLine = lines.firstOrNull { it.startsWith("mine ") }.orEmpty()
    val inlineArea = objectiveLine.substringAfter(" in ", "").substringBefore(" progress").trim()
    canonicalizeMiningArea(inlineArea)?.let { return it }

    for (line in lines) {
      canonicalizeMiningArea(line)?.let { return it }
    }
    return null
  }

  private fun canonicalizeMiningArea(raw: String): String? {
    val normalized = normalizeComparisonText(raw)
    if (normalized.isBlank()) return null
    return MINING_AREA_ALIASES.entries.firstOrNull { (alias, _) -> normalized.contains(alias) }?.value
  }

  private fun miningCommissionSuffix(miningTarget: String): String {
    val normalized = MiningBlockRegistry.normalizeType(miningTarget)
    return when {
      normalized == "Titanium" -> "Titanium"
      normalized.startsWith("Mithril") -> "Mithril"
      else -> normalized
    }
  }

  private fun commissionSelectionRank(commission: Commission): Int {
    val normalizedLabel = normalizeComparisonText(commission.label)
    COMMISSION_PRIORITY_INDEX[normalizedLabel]?.let { return it }

    return when (commission.type) {
      CommissionType.MINING -> when (miningCommissionSuffix(commission.target)) {
        "Titanium" -> COMMISSION_PRIORITY.size + 10
        "Mithril" -> COMMISSION_PRIORITY.size + 11
        "Glacite" -> COMMISSION_PRIORITY.size + 20
        "Umber" -> COMMISSION_PRIORITY.size + 21
        "Tungsten" -> COMMISSION_PRIORITY.size + 22
        else -> COMMISSION_PRIORITY.size + 40
      }
      CommissionType.COMBAT -> when {
        normalizedLabel.contains("glacite walker") -> COMMISSION_PRIORITY.size + 30
        normalizedLabel.contains("goblin") -> COMMISSION_PRIORITY.size + 31
        else -> COMMISSION_PRIORITY.size + 50
      }
    }
  }

  private fun titleCase(value: String): String =
    value.split(Regex("\\s+")).filter { it.isNotBlank() }.joinToString(" ") { word ->
      word.lowercase(Locale.US).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }
    }

  private fun normalizeComparisonText(value: String): String {
    return ChatFormatting.stripFormatting(value)
      ?.lowercase(Locale.US)
      ?.replace("&", " and ")
      ?.replace(Regex("[^a-z0-9' ]"), " ")
      ?.replace(Regex("\\s+"), " ")
      ?.trim()
      .orEmpty()
  }

  private fun looksLikeCommissionEntry(lines: List<String>, combined: String): Boolean {
    val firstLine = lines.firstOrNull().orEmpty()
    if (firstLine.contains("close") || firstLine.contains("back") || firstLine.contains("next page")) return false
    if (combined.contains("royal pigeon")) return false
    val normalizedFirstLine = normalizeComparisonText(firstLine)
    if (canonicalizeKnownCommissionLabel(firstLine) != null) return true
    if (normalizedFirstLine == "goblin raid" || normalizedFirstLine == "star sentry puncher") return true

    val hasObjectiveLine = lines.any { line -> COMMISSION_OBJECTIVE_PREFIXES.any(line::startsWith) }
    val hasProgressMarker =
      combined.contains(" progress") ||
        Regex("([0-9,]+)\\s*/\\s*([0-9,]+)").containsMatchIn(combined) ||
        Regex("([0-9]{1,3}(?:\\.[0-9]+)?)\\s*%").containsMatchIn(combined)

    return hasObjectiveLine && hasProgressMarker
  }

  private fun isClaimCommissionText(combined: String): Boolean =
    combined.contains("click to claim") || combined.contains("claim reward") ||
      combined.contains("claim rewards") || combined.contains("claim commission") ||
      combined.contains("commission complete") || combined.contains("commission completed") ||
      combined.contains("completed!")

  private fun parseCommissionProgress(combined: String): Pair<Int, Int> {
    val ratioMatch = Regex("([0-9,]+)\\s*/\\s*([0-9,]+)").find(combined)
    if (ratioMatch != null) {
      val current = ratioMatch.groupValues[1].replace(",", "").toIntOrNull() ?: 0
      val max = ratioMatch.groupValues[2].replace(",", "").toIntOrNull() ?: 100
      return current to max.coerceAtLeast(1)
    }
    val percentMatch = Regex("([0-9]{1,3}(?:\\.[0-9]+)?)\\s*%").find(combined)
    if (percentMatch != null) {
      val percent = percentMatch.groupValues[1].toDoubleOrNull() ?: 0.0
      return percent.toInt().coerceIn(0, 100) to 100
    }
    if (isClaimCommissionText(combined)) return 100 to 100
    return 0 to 100
  }

  private fun extractMiningKeyword(combined: String): String? {
    for (keyword in ORE_TO_TYPE.keys) {
      if (combined.contains(keyword)) return keyword
    }
    return null
  }

  private fun extractCombatCommissionTarget(lines: List<String>): String? {
    val objectiveRe = Regex("(?:kill|slay|defeat|punch)\\s+(?:[0-9,]+\\s*/\\s*[0-9,]+\\s+)?(?:[0-9,]+\\s+)?(.+)")
    for (line in lines) {
      val match = objectiveRe.find(line) ?: continue
      val rawTarget = match.groupValues[1]
        .substringBefore(" progress").substringBefore(" in ")
        .replace(Regex("[^a-z0-9' -]"), " ").replace(Regex("\\s+"), " ").trim()
      if (rawTarget.isBlank()) continue
      return rawTarget.removeSuffix(" mobs").removeSuffix(" mob").let { target ->
        when {
          target.endsWith("ies") && target.length > 3 -> target.dropLast(3) + "y"
          target.endsWith("s") && !target.endsWith("ss") && target.length > 3 -> target.dropLast(1)
          else -> target
        }
      }
    }
    return null
  }

  private fun startWorkModule(c: Commission) {
    when (c.type) {
      CommissionType.MINING -> {
        val routeName = findMatchingRouteName(c)
        if (routeName != null && RoutesModule.loadAndStartAutomationRoute(routeName, startNearest = false)) {
          activeMiningRouteName = routeName
          ChatUtils.sendMessage("Commission Macro: using route \"$routeName\" for ${c.label}.")
          return
        }
        if (routeName != null) {
          ChatUtils.sendMessage("Commission Macro: route \"$routeName\" could not be started. Falling back to mining macro.")
        } else {
          ChatUtils.sendMessage("Commission Macro: no route assigned or matched for ${c.label}. Falling back to mining macro.")
        }
        activeMiningRouteName = null
        MiningMacroModule.startForAutomation(c.mineTypes)
      }
      CommissionType.COMBAT -> {
        activeMiningRouteName = null
        CombatMacroModule.startForAutomation(c.target)
      }
    }
  }

  private fun stopWorkModule() {
    when (commission?.type) {
      CommissionType.MINING -> {
        if (activeMiningRouteName != null && RoutesModule.isRunning) {
          RoutesModule.stopForAutomation()
        }
        activeMiningRouteName = null
        MiningMacroModule.stopForAutomation()
      }
      CommissionType.COMBAT -> CombatMacroModule.stopForAutomation()
      null -> {}
    }
  }

  private fun workModuleIsActive(c: Commission): Boolean = when (c.type) {
    CommissionType.MINING -> (activeMiningRouteName != null && RoutesModule.isRunning) || MiningMacroModule.isActive
    CommissionType.COMBAT -> CombatMacroModule.isActive
  }

  private fun findMatchingRouteName(c: Commission): String? {
    val availableRoutes = RoutesModule.getSavedRouteInfos()
    resolveAssignedRouteName(c, availableRoutes)?.let { return it }

    val selectedNames = getSelectedRouteNames()
    if (selectedNames.isEmpty()) return null

    val availableRoutesByName = availableRoutes.associateBy { it.name }
    val selectedRoutes = selectedNames.mapNotNull { name -> findRouteInfoByName(availableRoutesByName, name) }
    if (selectedRoutes.isEmpty()) return null

    val requiredTypes = c.mineTypes
      .split(',')
      .map { it.trim() }
      .filter { it.isNotEmpty() }
      .map(MiningBlockRegistry::normalizeType)
      .toSet()
    val preferredTypes = preferredRouteMineTypes(c, requiredTypes)
    val namedZoneRoutes =
      resolveCommissionRouteZone(c.label)?.let { zone ->
        selectedRoutes.filter { routeMatchesZone(it.name, zone) }
      }.orEmpty()
    val targetZone = ORE_TO_ZONE[c.target]
    val areaRoutes =
      if (targetZone != null) {
        selectedRoutes.filter { route -> route.mineTypes.any { type -> ORE_TO_ZONE[type] == targetZone } }
      } else {
        emptyList()
      }

    return selectRouteByCommissionName(namedZoneRoutes, c)
      ?: selectRouteByType(namedZoneRoutes, preferredTypes)
      ?: namedZoneRoutes.firstOrNull()?.name
      ?: selectRouteByCommissionName(areaRoutes, c)
      ?: selectRouteByCommissionName(selectedRoutes, c)
      ?: selectRouteByType(areaRoutes, preferredTypes)
      ?: areaRoutes.firstOrNull()?.name
      ?: selectRouteByType(selectedRoutes, preferredTypes)
      ?: selectedRoutes.firstOrNull()?.name
  }

  private fun selectRouteByCommissionName(
    routes: List<RoutesModule.SavedRouteInfo>,
    commission: Commission,
  ): String? {
    val best =
      routes
        .map { route -> route to scoreRouteName(route.name, commission) }
        .filter { (_, score) -> score > 0 }
        .maxByOrNull { (_, score) -> score }
        ?: return null
    return best.first.name
  }

  private fun scoreRouteName(routeName: String, commission: Commission): Int {
    val normalizedRouteName = normalizeComparisonText(routeName)
    val normalizedLabel = normalizeComparisonText(commission.label)
    var score = 0

    if (normalizedRouteName.contains(normalizedLabel)) {
      score += 100
    }

    extractCommissionAreaKey(commission.label)?.let { areaKey ->
      if (normalizedRouteName.contains(areaKey)) {
        score += 40
      }
    }

    when (miningCommissionSuffix(commission.target)) {
      "Titanium" -> if (normalizedRouteName.contains("titanium")) score += 20
      "Mithril" -> if (normalizedRouteName.contains("mithril")) score += 20
      else -> {
        val targetKey = normalizeComparisonText(commission.target)
        if (targetKey.isNotBlank() && normalizedRouteName.contains(targetKey)) {
          score += 20
        }
      }
    }

    return score
  }

  private fun selectRouteByType(
    routes: List<RoutesModule.SavedRouteInfo>,
    preferredTypes: Set<String>,
  ): String? {
    return routes.firstOrNull { route -> route.mineTypes.any { it in preferredTypes } }?.name
  }

  private fun preferredRouteMineTypes(c: Commission, requiredTypes: Set<String>): Set<String> {
    val dwarvenMithrilTypes = setOf("Mithril (Gray)", "Mithril (Dark)", "Mithril (Hot)")
    return when (c.target) {
      "Titanium" -> linkedSetOf<String>().apply {
        addAll(dwarvenMithrilTypes)
        add("Titanium")
      }
      "Mithril (Gray)", "Mithril (Dark)", "Mithril (Hot)" -> dwarvenMithrilTypes
      else -> requiredTypes
    }
  }

  private fun extractCommissionAreaKey(label: String): String? {
    val normalized = normalizeComparisonText(label)
    return MINING_AREA_ALIASES.keys.firstOrNull { normalized.contains(it) }
  }

  private fun resolveCommissionRouteZone(label: String): CommissionRouteZone? {
    val normalized = normalizeComparisonText(label)
    return COMMISSION_ROUTE_ZONE_ALIASES.entries.firstOrNull { (_, aliases) ->
      aliases.any { alias -> normalized.contains(alias) }
    }?.key
  }

  private fun resolveAssignedRouteName(
    commission: Commission,
    availableRoutes: List<RoutesModule.SavedRouteInfo>,
  ): String? {
    if (commission.type != CommissionType.MINING) return null
    val zone = resolveCommissionRouteZone(commission.label) ?: return null
    val assignedName = getAssignedRouteName(zone) ?: return null
    return findRouteInfoByName(availableRoutes.associateBy { it.name }, assignedName)?.name
  }

  private fun routeMatchesZone(routeName: String, zone: CommissionRouteZone): Boolean {
    val normalized = normalizeComparisonText(routeName)
    return COMMISSION_ROUTE_ZONE_ALIASES[zone].orEmpty().any { alias -> normalized.contains(alias) }
  }

  private fun findRouteInfoByName(
    routesByName: Map<String, RoutesModule.SavedRouteInfo>,
    name: String,
  ): RoutesModule.SavedRouteInfo? {
    routesByName[name]?.let { return it }
    return routesByName.entries.firstOrNull { (routeName, _) ->
      routeName.equals(name, ignoreCase = true)
    }?.value
  }

  private fun routeSettingForZone(zone: CommissionRouteZone): TextSetting {
    return when (zone) {
      CommissionRouteZone.ROYAL -> royalRouteText
      CommissionRouteZone.CLIFFSIDE -> cliffsideRouteText
      CommissionRouteZone.LAVA -> lavaRouteText
      CommissionRouteZone.RAMP -> rampRouteText
      CommissionRouteZone.UPPER -> upperRouteText
    }
  }

  private fun parseSelectedRouteNames(raw: String): List<String> {
    return raw
      .split('|')
      .map { it.trim() }
      .filter { it.isNotEmpty() }
      .distinct()
  }

  // ---- Tab list area detection ----

  private fun detectAreaFromTabList(): String? {
    val connection = mc.connection ?: return null
    val lines = try {
      resolveTabEntries(connection)
        .mapNotNull { resolveEntryDisplayName(it) }
        .map { ChatFormatting.stripFormatting(it)?.lowercase()?.trim() ?: "" }
        .filter { it.isNotBlank() }
    } catch (_: Exception) { emptyList() }
    for (line in lines) {
      for (area in AREA_NAMES) {
        if (line.contains(area, ignoreCase = true)) return area
      }
    }
    return null
  }

  private fun resolveTabEntries(connection: Any): List<Any> {
    for (name in listOf("listPlayerEntries", "getListedOnlinePlayers", "getOnlinePlayers")) {
      val method = connection.javaClass.methods.firstOrNull { it.name == name && it.parameterCount == 0 } ?: continue
      val result = runCatching { method.invoke(connection) }.getOrNull() ?: continue
      when (result) {
        is Collection<*> -> return result.filterNotNull()
        is Iterable<*>   -> return result.filterNotNull()
      }
    }
    return emptyList()
  }

  private fun resolveEntryDisplayName(entry: Any): String? {
    for (name in listOf("getTabListDisplayName", "tabListDisplayName", "getDisplayName", "displayName")) {
      val method = entry.javaClass.methods.firstOrNull { it.name == name && it.parameterCount == 0 } ?: continue
      val text = coerceText(runCatching { method.invoke(entry) }.getOrNull())
      if (!text.isNullOrBlank()) return text
    }
    for (name in listOf("getProfile", "getGameProfile", "profile")) {
      val method  = entry.javaClass.methods.firstOrNull { it.name == name && it.parameterCount == 0 } ?: continue
      val profile = runCatching { method.invoke(entry) }.getOrNull() ?: continue
      val nm      = profile.javaClass.methods.firstOrNull { it.name == "getName" && it.parameterCount == 0 } ?: continue
      val n       = runCatching { nm.invoke(profile) as? String }.getOrNull()
      if (!n.isNullOrBlank()) return n
    }
    return null
  }

  private fun coerceText(value: Any?): String? {
    if (value == null) return null
    if (value is String) return value
    val m = value.javaClass.methods.firstOrNull { it.name == "getString" && it.parameterCount == 0 }
    val raw = m?.let { runCatching { it.invoke(value) }.getOrNull() }
    return if (raw is String) raw else value.toString()
  }

  // ---- Lifecycle ----

  private fun transition(newState: State) {
    state     = newState
    stateTick = 0
  }

  private fun setStatus(msg: String) {
    statusText.value = msg
  }

  private fun resetCommissionState() {
    commission        = null
    activeCommissions = emptyList()
    activeMiningRouteName = null
    commText.value    = "None"
    openAttempts      = 0
    claimAttempts     = 0
    readAttempts      = 0
  }

  private fun resetMacro() {
    stopWorkModule()
    stopEmissaryNavigation()
    NativePathfinder.stop()
    MovementManager.setMovementLock(false)
    if (pendingUseRelease) { mc.options.keyUse?.setDown(false); pendingUseRelease = false }
    state               = State.IDLE
    stateTick           = 0
    resetCommissionState()
    setStatus("Idle")
    areaText.value = "Unknown"
  }
}
