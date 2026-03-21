package org.cobalt.internal.mining

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.player.Player
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.ChatEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.InventoryUtils
import org.cobalt.api.util.getLoreLines
import org.cobalt.internal.combat.CombatMacroModule
import org.cobalt.api.pathfinder.jni.NativePathfinder
import org.cobalt.api.util.player.MovementManager
import org.cobalt.internal.pathfinding.PathPlanProfiles

object CommissionMacroModule : Module("Commission Macro") {

  private val mc = Minecraft.getInstance()

  // ---- Settings ----

  private val enabled = CheckboxSetting(
    "Enabled",
    "Complete commissions in a loop using the Royal Pigeon item.",
    false
  )

  private val info = InfoSetting(
    "How It Works",
    "Select Royal Pigeon in hotbar → right-click to open GUI → scrape commission book lore → check zone → walk/warpforge → mine until complete → return to Royal Pigeon → claim → repeat.",
    InfoType.INFO
  )

  private val statusText  = TextSetting("Status",     "Current macro state.",  "Idle")
  private val commText    = TextSetting("Commission", "Detected commission.",   "None")
  private val areaText    = TextSetting("Area",       "Detected map area.",     "Unknown")

  // ---- Commission model ----

  private enum class CommissionType { MINING, COMBAT }

  private data class Commission(
    val type:    CommissionType,
    val target:  String,   // block-type name (mining) or mob name (combat)
    val current: Int,
    val max:     Int,
  ) {
    val isComplete get() = max > 0 && current >= max
  }

  // ---- State machine ----

  private enum class State {
    IDLE,
    OPEN_PIGEON,        // select + right-click Royal Pigeon item; wait for GUI
    READ_GUI,           // GUI open: scrape book lore for commissions
    CLAIM_GUI,          // GUI open: click the claim button on a complete commission
    CHECK_ZONE,         // decide if we can walk or need /warpforge
    WARP_TO_ZONE,       // /warpforge sent; waiting for teleport
    MINING,             // MiningMacroModule (or CombatMacroModule) is active
    RETURN_PIGEON,      // commission done; open Royal Pigeon to claim
  }

  private var state     = State.IDLE
  private var stateTick = 0L

  private var commission:       Commission? = null
  private var pendingUseRelease = false
  private var pendingSlotRestore = -1
  private var openAttempts      = 0
  private var claimAttempts     = 0
  private var readAttempts      = 0

  // ---- Known areas and ore→zone mapping ----

  private val AREA_NAMES = listOf(
    "Dwarven Mines", "Crystal Hollows", "Glacite Tunnels",
    "Deep Caverns", "Spider's Den", "The End", "Crimson Isle"
  )

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

  // ---- Public HUD ----

  val statusDisplay:     String get() = statusText.value
  val commissionDisplay: String get() = commText.value
  val currentZoneDisplay: String get() = areaText.value
  val targetZoneDisplay: String get() {
    val c = commission ?: return "Unknown"
    return if (c.type == CommissionType.MINING) ORE_TO_ZONE[c.target] ?: "Unknown" else "Combat Zone"
  }
  val isRunning: Boolean get() = enabled.value

  init {
    addSetting(enabled, info, statusText, commText, areaText)
    EventBus.register(this)
  }

  // ---- Tick ----

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    // Deferred key releases
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
      State.IDLE          -> transition(State.OPEN_PIGEON)
      State.OPEN_PIGEON   -> handleOpenPigeon(player)
      State.READ_GUI      -> handleReadGui()
      State.CLAIM_GUI     -> handleClaimGui()
      State.CHECK_ZONE    -> handleCheckZone(player)
      State.WARP_TO_ZONE  -> { if (stateTick >= 80) transition(State.MINING) }
      State.MINING        -> handleMining()
      State.RETURN_PIGEON -> handleReturnPigeon(player)
    }
  }

  // ---- Chat ----

  @SubscribeEvent
  fun onChat(event: ChatEvent.Receive) {
    if (!enabled.value) return
    val msg = event.message?.lowercase() ?: return
    if (state == State.MINING && msg.contains("commission complete")) {
      setStatus("Commission complete! Returning to Royal Pigeon...")
      stopWorkModule()
      transition(State.RETURN_PIGEON)
    }
  }

  // ---- State: OPEN_PIGEON ----
  // Select Royal Pigeon in hotbar and right-click to open the GUI.

  private fun handleOpenPigeon(player: Player) {
    // If GUI already opened by a previous right-click, move on
    if (mc.screen is AbstractContainerScreen<*>) {
      openAttempts = 0
      transition(State.READ_GUI)
      return
    }

    if (stateTick % 10 != 0L) return

    val pigeonSlot = findPigeonHotbarSlot(player)
    if (pigeonSlot < 0) {
      setStatus("Royal Pigeon not found in hotbar!")
      if (stateTick > 100) { ChatUtils.sendMessage("Commission Macro: Royal Pigeon not in hotbar. Disabling."); enabled.value = false }
      return
    }

    if (openAttempts >= 6) {
      ChatUtils.sendMessage("Commission Macro: Could not open Royal Pigeon GUI. Disabling.")
      enabled.value = false
      return
    }

    val restoreSlot = findPreferredRestoreSlot(player, pigeonSlot)
    pressHotbarSlot(pigeonSlot)
    mc.options.keyUse?.setDown(true)
    pendingUseRelease = true
    if (restoreSlot in 0..8 && restoreSlot != pigeonSlot) pendingSlotRestore = restoreSlot
    openAttempts++
    setStatus("Opening Royal Pigeon... (attempt $openAttempts)")
  }

  // ---- State: READ_GUI ----
  // Scrape book item lore from the open Royal Pigeon GUI.

  private fun handleReadGui() {
    val screen = mc.screen as? AbstractContainerScreen<*>
    if (screen == null) {
      // GUI closed unexpectedly; retry open
      transition(State.OPEN_PIGEON)
      return
    }

    if (stateTick < 5) return  // wait for slots to populate

    // Only check every 5 ticks to give the server time to send all slot data
    if (stateTick % 5 != 0L) return

    // Look for any commission book that is already complete → claim it first
    val claimSlot = findClaimSlot(screen)
    if (claimSlot >= 0) {
      setStatus("Found claimable commission — claiming...")
      transition(State.CLAIM_GUI)
      return
    }

    // Parse an active (incomplete) commission from the books
    val parsed = parseCommissionFromGui(screen)
    if (parsed != null) {
      commission = parsed
      commText.value = "${parsed.type.name.lowercase().replaceFirstChar { it.uppercase() }}: ${parsed.target} (${parsed.current}/${parsed.max})"
      mc.player?.closeContainer()
      openAttempts = 0
      setStatus("Commission: ${parsed.target}")
      transition(State.CHECK_ZONE)
      return
    }

    readAttempts++
    if (readAttempts >= 30) {
      mc.player?.closeContainer()
      ChatUtils.sendMessage("Commission Macro: Could not read any commission from Royal Pigeon GUI. Disabling.")
      enabled.value = false
    }
  }

  // ---- State: CLAIM_GUI ----
  // Click the claim button for the completed commission book.

  private fun handleClaimGui() {
    val screen = mc.screen as? AbstractContainerScreen<*>
    if (screen == null) {
      // GUI closed — restart loop for next commission
      commission = null
      commText.value = "None"
      openAttempts  = 0
      claimAttempts = 0
      readAttempts  = 0
      transition(State.OPEN_PIGEON)
      return
    }

    if (stateTick < 3) return

    val claimSlot = findClaimSlot(screen)
    if (claimSlot >= 0) {
      InventoryUtils.clickSlot(claimSlot)
      claimAttempts++
      setStatus("Claiming commission (slot $claimSlot, attempt $claimAttempts)...")
      if (claimAttempts >= 3) {
        // Claimed — close and start the loop again
        mc.player?.closeContainer()
        commission    = null
        commText.value = "None"
        openAttempts  = 0
        claimAttempts = 0
        readAttempts  = 0
        setStatus("Claimed! Restarting...")
        transition(State.OPEN_PIGEON)
      }
      return
    }

    // No claimable slot visible any more — GUI may have updated
    mc.player?.closeContainer()
    commission    = null
    commText.value = "None"
    openAttempts  = 0
    claimAttempts = 0
    readAttempts  = 0
    transition(State.OPEN_PIGEON)
  }

  // ---- State: CHECK_ZONE ----
  // If already in the right zone, start mining immediately.
  // Otherwise /warpforge and wait before mining.

  private fun handleCheckZone(player: Player) {
    val c = commission ?: run { transition(State.OPEN_PIGEON); return }

    val currentArea = detectAreaFromTabList()
    if (currentArea != null) areaText.value = currentArea

    val targetZone = if (c.type == CommissionType.MINING) ORE_TO_ZONE[c.target] else null

    val needsWarp = targetZone != null && currentArea != null && !currentArea.contains(targetZone, ignoreCase = true)

    if (needsWarp) {
      setStatus("Wrong zone ($currentArea → $targetZone). Warping to forge...")
      ;(mc.player as? LocalPlayer)?.connection?.sendCommand("warpforge")
      transition(State.WARP_TO_ZONE)
    } else {
      transition(State.MINING)
    }
  }

  // ---- State: MINING ----

  private fun handleMining() {
    val c = commission ?: run { transition(State.OPEN_PIGEON); return }

    if (!workModuleIsActive(c)) {
      // Module isn't running yet — start it
      startWorkModule(c)
      setStatus("Mining: ${c.target}")
    }
  }

  // ---- State: RETURN_PIGEON ----
  // Mining is done; open Royal Pigeon to claim.

  private fun handleReturnPigeon(player: Player) {
    if (mc.screen is AbstractContainerScreen<*>) {
      // GUI is open — let READ_GUI handle it (will find the claim button)
      openAttempts = 0
      readAttempts = 0
      transition(State.READ_GUI)
      return
    }

    if (stateTick % 10 != 0L) return

    val pigeonSlot = findPigeonHotbarSlot(player)
    if (pigeonSlot < 0) {
      if (stateTick > 100) { ChatUtils.sendMessage("Commission Macro: Royal Pigeon not found when returning to claim. Disabling."); enabled.value = false }
      return
    }

    if (openAttempts >= 6) {
      ChatUtils.sendMessage("Commission Macro: Could not re-open Royal Pigeon for claiming. Disabling.")
      enabled.value = false
      return
    }

    val restoreSlot = findPreferredRestoreSlot(player, pigeonSlot)
    pressHotbarSlot(pigeonSlot)
    mc.options.keyUse?.setDown(true)
    pendingUseRelease = true
    if (restoreSlot in 0..8 && restoreSlot != pigeonSlot) pendingSlotRestore = restoreSlot
    openAttempts++
    setStatus("Re-opening Royal Pigeon to claim... (attempt $openAttempts)")
  }

  // ---- Helpers ----

  private fun findPigeonHotbarSlot(player: Player): Int {
    for (i in 0..8) {
      val stack = player.inventory.getItem(i)
      if (stack.isEmpty) continue
      val name = ChatFormatting.stripFormatting(stack.hoverName.string)?.lowercase() ?: continue
      if (name.contains("royal pigeon")) return i
    }
    return -1
  }

  private fun findPreferredRestoreSlot(player: Player, pigeonSlot: Int): Int {
    val drillSlot = findHotbarSlotContaining(player, "drill")
    if (drillSlot in 0..8 && drillSlot != pigeonSlot) {
      return drillSlot
    }

    val previousSlot = player.inventory.selectedSlot
    return if (previousSlot in 0..8 && previousSlot != pigeonSlot) previousSlot else -1
  }

  private fun findHotbarSlotContaining(player: Player, keyword: String): Int {
    for (i in 0..8) {
      val stack = player.inventory.getItem(i)
      if (stack.isEmpty) continue
      val name = ChatFormatting.stripFormatting(stack.hoverName.string)?.lowercase() ?: continue
      if (name.contains(keyword)) {
        return i
      }
    }
    return -1
  }

  private fun pressHotbarSlot(slot: Int) {
    if (slot !in 0..8) return
    mc.player?.inventory?.selectedSlot = slot
  }

  /** Parse the first active (incomplete) commission from all book items in the open GUI. */
  private fun parseCommissionFromGui(screen: AbstractContainerScreen<*>): Commission? {
    for (slot in screen.menu.slots) {
      if (!slot.hasItem()) continue
      val item = slot.item
      val lines = buildGuiTextLines(item)
      if (lines.isEmpty()) continue
      val combined = lines.joinToString("\n")
      if (!looksLikeCommissionEntry(lines, combined)) continue
      if (isClaimCommissionText(combined)) continue

      val (current, max) = parseCommissionProgress(combined)
      if (max > 0 && current >= max) continue

      val miningTarget = extractMiningCommissionTarget(combined)
      if (miningTarget != null) {
        return Commission(CommissionType.MINING, miningTarget, current, max)
      }

      val combatTarget = extractCombatCommissionTarget(lines)
      if (combatTarget != null) {
        return Commission(CommissionType.COMBAT, combatTarget, current, max)
      }
    }
    return null
  }

  /** Find a GUI slot whose item lore contains a "claim" prompt for a completed commission. */
  private fun findClaimSlot(screen: AbstractContainerScreen<*>): Int {
    for (slot in screen.menu.slots) {
      if (!slot.hasItem()) continue
      val item = slot.item
      val lines = buildGuiTextLines(item)
      if (lines.isEmpty()) continue
      val combined = lines.joinToString("\n")
      if (looksLikeCommissionEntry(lines, combined) && isClaimCommissionText(combined)) {
        return slot.index
      }
    }
    return -1
  }

  private fun buildGuiTextLines(item: net.minecraft.world.item.ItemStack): List<String> {
    val lines = mutableListOf<String>()
    val name = ChatFormatting.stripFormatting(item.hoverName.string)?.lowercase()?.trim().orEmpty()
    if (name.isNotBlank()) {
      lines += name
    }
    lines += item.getLoreLines()
      .mapNotNull { ChatFormatting.stripFormatting(it.string)?.lowercase()?.trim() }
      .filter { it.isNotBlank() }
    return lines
  }

  private fun looksLikeCommissionEntry(lines: List<String>, combined: String): Boolean {
    val firstLine = lines.firstOrNull().orEmpty()
    if (firstLine.contains("close") || firstLine.contains("back") || firstLine.contains("next page")) return false
    if (combined.contains("royal pigeon")) return false

    return combined.contains("mine ") ||
      combined.contains("kill ") ||
      combined.contains("slay ") ||
      combined.contains("defeat ") ||
      ORE_TO_TYPE.keys.any { combined.contains(it) }
  }

  private fun isClaimCommissionText(combined: String): Boolean {
    return combined.contains("click to claim") ||
      combined.contains("claim reward") ||
      combined.contains("claim rewards") ||
      combined.contains("claim commission") ||
      combined.contains("commission complete") ||
      combined.contains("commission completed") ||
      combined.contains("completed!")
  }

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

    if (isClaimCommissionText(combined)) {
      return 100 to 100
    }

    // Royal Pigeon sometimes separates objective text from progress formatting.
    return 0 to 100
  }

  private fun extractMiningCommissionTarget(combined: String): String? {
    for ((keyword, blockType) in ORE_TO_TYPE) {
      if (combined.contains(keyword)) {
        return blockType
      }
    }
    return null
  }

  private fun extractCombatCommissionTarget(lines: List<String>): String? {
    val objectiveRe = Regex("(?:kill|slay|defeat)\\s+(?:[0-9,]+\\s*/\\s*[0-9,]+\\s+)?(?:[0-9,]+\\s+)?(.+)")

    for (line in lines) {
      val match = objectiveRe.find(line) ?: continue
      val rawTarget = match.groupValues[1]
        .substringBefore(" progress")
        .substringBefore(" in ")
        .replace(Regex("[^a-z0-9' -]"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()
      if (rawTarget.isBlank()) continue

      return rawTarget
        .removeSuffix(" mobs")
        .removeSuffix(" mob")
        .let { target ->
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
      CommissionType.MINING -> MiningMacroModule.startForAutomation(c.target)
      CommissionType.COMBAT -> CombatMacroModule.startForAutomation(c.target)
    }
  }

  private fun stopWorkModule() {
    when (commission?.type) {
      CommissionType.MINING -> MiningMacroModule.stopForAutomation()
      CommissionType.COMBAT -> CombatMacroModule.stopForAutomation()
      null -> {}
    }
  }

  private fun workModuleIsActive(c: Commission): Boolean = when (c.type) {
    CommissionType.MINING -> MiningMacroModule.isActive
    CommissionType.COMBAT -> CombatMacroModule.isActive
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
      val text   = coerceText(runCatching { method.invoke(entry) }.getOrNull())
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

  private fun resetMacro() {
    stopWorkModule()
    NativePathfinder.stop()
    MovementManager.setMovementLock(false)
    RotationExecutor.stopRotating()
    if (pendingUseRelease) { mc.options.keyUse?.setDown(false); pendingUseRelease = false }
    state          = State.IDLE
    stateTick      = 0
    commission     = null
    openAttempts   = 0
    claimAttempts  = 0
    readAttempts   = 0
    pendingSlotRestore = -1
    setStatus("Idle")
    commText.value = "None"
    areaText.value = "Unknown"
  }
}
