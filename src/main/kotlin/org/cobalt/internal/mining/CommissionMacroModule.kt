package org.cobalt.internal.mining

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.player.Player
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.ChatEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.ModeSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.rotation.EasingType
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.rotation.strategy.TimedEaseStrategy
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.InventoryUtils
import org.cobalt.api.util.TickScheduler
import org.cobalt.api.util.getLoreLines
import org.cobalt.internal.combat.CombatMacroModule
import org.cobalt.internal.pathfinding.DuskPathfinder
import org.cobalt.internal.pathfinding.PathPlanProfiles

object CommissionMacroModule : Module("Commission Macro") {

  private val mc = Minecraft.getInstance()
  private val npcRotationStrategy = TimedEaseStrategy(
    yawEasing = EasingType.EASE_OUT_EXPO,
    pitchEasing = EasingType.EASE_OUT_EXPO,
    duration = 300L
  )

  // ---- Settings ----

  private val enabled = CheckboxSetting(
    "Enabled",
    "Complete commissions in a loop: detect → travel to vein/mob → work → /warpforge → claim → repeat.",
    false
  )

  private val info = InfoSetting(
    "How It Works",
    "1. Reads area and nearby vein from world. 2. Reads commission from tab list (or finds Royal Pigeon/Emissary). 3. Pathfinds to vein or mob. 4. Mines or fights. 5. /warpforge to claim. Repeat.",
    InfoType.INFO
  )

  private val veinScanRadius = SliderSetting(
    "Vein Scan Radius",
    "Block radius to scan for nearby commission ore when checking location.",
    32.0, 8.0, 64.0
  )

  private val warpTarget = ModeSetting(
    "Return Warp",
    "Where to warp back after claiming commissions.",
    0,
    arrayOf("Forge", "Camp")
  )

  private val statusText = TextSetting("Status", "Current macro state.", "Idle")
  private val commissionText = TextSetting("Commission", "Detected commission.", "None")
  private val areaText = TextSetting("Area", "Detected map area.", "Unknown")

  // ---- Commission model ----

  private enum class CommissionType { MINING, COMBAT }

  private data class Commission(
    val type: CommissionType,
    val target: String,   // block type name (mining) or mob name (combat)
    val current: Int,
    val max: Int,
  ) {
    val isComplete get() = max > 0 && current >= max
  }

  // ---- State machine ----

  private enum class State {
    IDLE,
    CHECK_LOCATION,     // detect area + scan for nearby vein
    CHECK_COMMISSION,   // parse tab list; if missing, find Royal Pigeon / Emissary and interact
    TRAVELING,          // pathfind to nearest vein block (mining) or nearest mob (combat)
    WORKING,            // MiningMacroModule or CombatMacroModule is active
    WARP_FORGE,         // /warpforge sent, waiting for teleport
    AT_FORGE,           // find commission NPC, interact, claim
  }

  private var state = State.IDLE
  private var stateTick = 0L
  private var commission: Commission? = null

  // Location check results
  private var detectedArea: String? = null
  private var nearestVeinBlock: BlockPos? = null

  // NPC interaction
  private var npcInteractAttempts = 0
  private var guiClaimAttempts = 0
  private var pendingUseRelease = false

  // ---- Known areas (from tab list) ----

  private val AREA_NAMES = listOf(
    "Dwarven Mines", "Crystal Hollows", "Glacite Tunnels",
    "Deep Caverns", "Spider's Den", "The End", "Crimson Isle"
  )

  // ---- Commission NPCs ----

  private val COMMISSION_NPC_KEYWORDS = listOf(
    "royal pigeon", "king yolkar", "emissary", "commission"
  )

  // ---- Ore keyword → MiningBlockRegistry type ----

  private val ORE_TO_TYPE = linkedMapOf(
    "titanium" to "Titanium",
    "mithril" to "Mithril (Gray)",
    "ruby" to "Ruby Gemstone",
    "amber" to "Amber Gemstone",
    "amethyst" to "Amethyst Gemstone",
    "jade" to "Jade Gemstone",
    "sapphire" to "Sapphire Gemstone",
    "opal" to "Opal Gemstone",
    "topaz" to "Topaz Gemstone",
    "jasper" to "Jasper Gemstone",
    "onyx" to "Onyx Gemstone",
    "aquamarine" to "Aquamarine Gemstone",
    "citrine" to "Citrine Gemstone",
    "peridot" to "Peridot Gemstone",
    "umber" to "Umber",
    "tungsten" to "Tungsten",
    "glacite" to "Glacite",
    "sulphur" to "Sulphur",
    "coal" to "Pure Coal",
    "iron" to "Pure Iron",
    "gold" to "Pure Gold",
    "lapis" to "Pure Lapis",
    "redstone" to "Pure Redstone",
    "emerald" to "Pure Emerald",
    "diamond" to "Pure Diamond",
    "quartz" to "Pure Quartz",
  )

  private val ORE_TO_ZONE = mapOf(
    "Titanium" to "Dwarven Mines",
    "Mithril (Gray)" to "Dwarven Mines",
    "Pure Coal" to "Dwarven Mines",
    "Pure Iron" to "Dwarven Mines",
    "Pure Gold" to "Dwarven Mines",
    "Pure Lapis" to "Dwarven Mines",
    "Pure Redstone" to "Dwarven Mines",
    "Pure Emerald" to "Dwarven Mines",
    "Pure Diamond" to "Dwarven Mines",
    "Pure Quartz" to "Dwarven Mines",
    "Ruby Gemstone" to "Crystal Hollows",
    "Amber Gemstone" to "Crystal Hollows",
    "Amethyst Gemstone" to "Crystal Hollows",
    "Jade Gemstone" to "Crystal Hollows",
    "Sapphire Gemstone" to "Crystal Hollows",
    "Opal Gemstone" to "Crystal Hollows",
    "Topaz Gemstone" to "Crystal Hollows",
    "Jasper Gemstone" to "Crystal Hollows",
    "Umber" to "Glacite Tunnels",
    "Tungsten" to "Glacite Tunnels",
    "Glacite" to "Glacite Tunnels",
    "Sulphur" to "Glacite Tunnels",
    "Onyx Gemstone" to "Glacite Tunnels",
    "Aquamarine Gemstone" to "Glacite Tunnels",
    "Citrine Gemstone" to "Glacite Tunnels",
    "Peridot Gemstone" to "Glacite Tunnels",
  )

  // ---- Public HUD state ----

  val statusDisplay: String get() = statusText.value
  val commissionDisplay: String get() = commissionText.value
  val currentZoneDisplay: String get() = areaText.value
  val targetZoneDisplay: String get() {
    val c = commission ?: return "Unknown"
    return when (c.type) {
      CommissionType.MINING -> ORE_TO_ZONE[c.target] ?: "Unknown"
      CommissionType.COMBAT -> "Combat Zone"
    }
  }
  val isRunning: Boolean get() = enabled.value

  init {
    addSetting(enabled, info, veinScanRadius, warpTarget, statusText, commissionText, areaText)
    EventBus.register(this)
  }

  // ---- Tick ----

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (pendingUseRelease) {
      mc.options.keyUse?.setDown(false)
      pendingUseRelease = false
    }

    if (!enabled.value) {
      if (state != State.IDLE) resetMacro()
      return
    }

    val player = mc.player ?: return
    stateTick++

    when (state) {
      State.IDLE -> transition(State.CHECK_LOCATION)

      State.CHECK_LOCATION -> handleCheckLocation(player)

      State.CHECK_COMMISSION -> handleCheckCommission(player)

      State.TRAVELING -> handleTraveling(player)

      State.WORKING -> {
        val c = commission ?: run { transition(State.CHECK_LOCATION); return }
        val running = when (c.type) {
          CommissionType.MINING -> MiningMacroModule.isActive
          CommissionType.COMBAT -> CombatMacroModule.isActive
        }
        if (!running) {
          ChatUtils.sendMessage("Commission Macro: Work module stopped — rescanning.")
          transition(State.CHECK_LOCATION)
        }
      }

      State.WARP_FORGE -> {
        // Wait for teleport, then head to the NPC to claim
        if (stateTick >= 60) {
          npcInteractAttempts = 0
          guiClaimAttempts = 0
          transition(State.AT_FORGE)
        }
      }

      State.AT_FORGE -> handleAtForge(player)
    }
  }

  // ---- Chat ----

  @SubscribeEvent
  fun onChat(event: ChatEvent.Receive) {
    if (!enabled.value || state != State.WORKING) return
    val msg = event.message?.lowercase() ?: return
    if (msg.contains("commission complete")) {
      onCommissionComplete()
    }
  }

  // ---- State: CHECK_LOCATION ----

  private fun handleCheckLocation(player: Player) {
    // Detect what area we're in from the tab list
    val area = detectAreaFromTabList()
    if (area != null) {
      detectedArea = area
      areaText.value = area
    }

    // Scan nearby blocks for any known ore — gives us a rough "near vein" signal
    val c = commission
    nearestVeinBlock = if (c?.type == CommissionType.MINING) {
      findNearestVeinBlock(player, c.target)
    } else {
      null
    }

    val nearVein = nearestVeinBlock != null
    setStatus("Location: ${detectedArea ?: "Unknown"} | Near vein: $nearVein")
    transition(State.CHECK_COMMISSION)
  }

  // ---- State: CHECK_COMMISSION ----

  private fun handleCheckCommission(player: Player) {
    if (stateTick < 3) return

    // Primary: tab list
    val detected = parseCommissionFromTabList()
    if (detected != null) {
      applyCommission(detected)
      return
    }

    // Secondary: find Royal Pigeon / Emissary NPC and interact with them
    val npc = findNearbyCommissionNpc(player)
    if (npc == null) {
      if (stateTick % 20 == 0L) setStatus("Looking for commission... (tab empty, no NPC nearby)")
      if (stateTick > 120) {
        ChatUtils.sendMessage("Commission Macro: No commission found in tab list and no commission NPC nearby.")
        enabled.value = false
      }
      return
    }

    // Walk to NPC if needed
    val distSq = player.distanceToSqr(npc.x, npc.y, npc.z)
    if (distSq > 9.0) {
      if (!DuskPathfinder.isActive() && stateTick % 20 == 0L) {
        DuskPathfinder.start(mc, BlockPos.containing(npc.x, npc.y, npc.z), PathPlanProfiles.DEFAULT_ID)
        setStatus("Walking to ${npc.name.string}...")
      }
      return
    }

    DuskPathfinder.stop(mc, "At commission NPC.")

    // Open NPC menu every 10 ticks to read commission
    if (mc.screen is AbstractContainerScreen<*>) {
      // Try to read commission info from open GUI
      val fromGui = parseCommissionFromOpenGui()
      if (fromGui != null) {
        mc.player?.closeContainer()
        applyCommission(fromGui)
        return
      }
      // GUI open but no commission readable — close and retry
      mc.player?.closeContainer()
      return
    }

    if (stateTick % 10 == 0L && npcInteractAttempts < 5) {
      RotationExecutor.rotateTo(AngleUtils.getRotation(npc), npcRotationStrategy)
      TickScheduler.schedule(3L) {
        mc.options.keyUse?.setDown(true)
        pendingUseRelease = true
        npcInteractAttempts++
      }
    } else if (npcInteractAttempts >= 5) {
      ChatUtils.sendMessage("Commission Macro: Could not read commission from NPC.")
      enabled.value = false
    }
  }

  private fun applyCommission(c: Commission) {
    commission = c
    commissionText.value = "${c.type.name.lowercase().replaceFirstChar { it.uppercase() }}: ${c.target} (${c.current}/${c.max})"
    npcInteractAttempts = 0

    if (c.isComplete) {
      ChatUtils.sendMessage("Commission Macro: Commission already complete — going to forge.")
      onCommissionComplete()
      return
    }

    ChatUtils.sendMessage("Commission Macro: ${c.type.name} commission — ${c.target}")
    transition(State.TRAVELING)
  }

  // ---- State: TRAVELING ----

  private fun handleTraveling(player: Player) {
    val c = commission ?: run { transition(State.CHECK_LOCATION); return }

    when (c.type) {
      CommissionType.MINING -> {
        // Find or refresh nearest vein block every 40 ticks
        if (stateTick % 40 == 0L || nearestVeinBlock == null) {
          nearestVeinBlock = findNearestVeinBlock(player, c.target)
        }

        val target = nearestVeinBlock
        if (target == null) {
          // No vein found nearby — start mining macro and let it scan
          setStatus("No vein found nearby, starting miner scan...")
          MiningMacroModule.startForAutomation(c.target)
          transition(State.WORKING)
          return
        }

        val distSq = player.distanceToSqr(target.x + 0.5, target.y + 0.5, target.z + 0.5)
        if (distSq <= 100.0) { // within 10 blocks — close enough to start mining
          DuskPathfinder.stop(mc, "Near vein, starting miner.")
          MiningMacroModule.startForAutomation(c.target)
          setStatus("Mining: ${c.target}")
          transition(State.WORKING)
          return
        }

        // Pathfind to the vein
        if (!DuskPathfinder.isActive() && stateTick % 20 == 0L) {
          DuskPathfinder.start(mc, target, PathPlanProfiles.DEFAULT_ID)
          setStatus("Traveling to ${c.target} vein (${distSq.toInt()} dist²)...")
        }
      }

      CommissionType.COMBAT -> {
        // CombatMacroModule handles its own target-finding and pathfinding
        CombatMacroModule.startForAutomation(c.target)
        setStatus("Fighting: ${c.target}")
        transition(State.WORKING)
      }
    }
  }

  // ---- State: WARP_FORGE → AT_FORGE ----

  private fun onCommissionComplete() {
    when (commission?.type) {
      CommissionType.MINING -> MiningMacroModule.stopForAutomation()
      CommissionType.COMBAT -> CombatMacroModule.stopForAutomation()
      null -> {}
    }
    DuskPathfinder.stop(mc, "Commission complete.")
    RotationExecutor.stopRotating()
    ChatUtils.sendMessage("Commission Macro: Complete! Sending /warpforge.")
    setStatus("Warping to forge...")
    ;(mc.player as? LocalPlayer)?.connection?.sendCommand("warpforge")
    transition(State.WARP_FORGE)
  }

  private fun handleAtForge(player: Player) {
    // If container GUI is open — claim
    if (mc.screen is AbstractContainerScreen<*>) {
      if (stateTick < 3) return
      if (tryClaimInGui()) {
        setStatus("Claimed! Warping back...")
        TickScheduler.schedule(20L) {
          if (!enabled.value) return@schedule
          mc.player?.closeContainer()
          val returnCmd = if (warpTarget.value == 0) "warp forge" else "warp camp"
          (mc.player as? LocalPlayer)?.connection?.sendCommand(returnCmd)
          TickScheduler.schedule(60L) {
            if (enabled.value) {
              commission = null
              nearestVeinBlock = null
              transition(State.CHECK_LOCATION)
            }
          }
        }
        return
      }
      guiClaimAttempts++
      if (guiClaimAttempts >= 5) {
        mc.player?.closeContainer()
        guiClaimAttempts = 0
        npcInteractAttempts++
      }
      if (npcInteractAttempts >= 4) {
        ChatUtils.sendMessage("Commission Macro: Could not claim at forge. Disabling.")
        enabled.value = false
      }
      return
    }

    // Find commission NPC (Royal Pigeon / King Yolkar / Emissary)
    val npc = findNearbyCommissionNpc(player)
    if (npc == null) {
      if (stateTick % 20 == 0L) setStatus("Looking for commission NPC at forge...")
      if (stateTick > 200) {
        ChatUtils.sendMessage("Commission Macro: No commission NPC found at forge. Disabling.")
        enabled.value = false
      }
      return
    }

    val distSq = player.distanceToSqr(npc.x, npc.y, npc.z)
    if (distSq > 9.0) {
      if (!DuskPathfinder.isActive() && stateTick % 20 == 0L) {
        DuskPathfinder.start(mc, BlockPos.containing(npc.x, npc.y, npc.z), PathPlanProfiles.DEFAULT_ID)
        setStatus("Walking to ${npc.name.string}...")
      }
      return
    }

    DuskPathfinder.stop(mc, "At NPC.")

    if (stateTick % 10 == 0L && npcInteractAttempts < 5) {
      RotationExecutor.rotateTo(AngleUtils.getRotation(npc), npcRotationStrategy)
      TickScheduler.schedule(3L) {
        mc.options.keyUse?.setDown(true)
        pendingUseRelease = true
        npcInteractAttempts++
      }
    } else if (npcInteractAttempts >= 5 && stateTick > 60) {
      ChatUtils.sendMessage("Commission Macro: NPC not responding at forge. Disabling.")
      enabled.value = false
    }
  }

  // ---- Vein scanning ----

  /** Scan nearby blocks (within veinScanRadius) for the nearest ore of the commission type. */
  private fun findNearestVeinBlock(player: Player, blockType: String): BlockPos? {
    val level = mc.level ?: return null
    val targetIds = MiningBlockRegistry.idsForType(blockType)
    if (targetIds.isEmpty()) return null

    val origin = player.blockPosition()
    val r = veinScanRadius.value.toInt()
    val v = 8
    var best: BlockPos? = null
    var bestDist = Double.MAX_VALUE

    for (dy in -v..v) {
      for (dx in -r..r) {
        for (dz in -r..r) {
          val pos = origin.offset(dx, dy, dz)
          val id = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).block).toString()
          if (id !in targetIds) continue
          val d = pos.distSqr(origin)
          if (d < bestDist) {
            bestDist = d
            best = pos
          }
        }
      }
    }
    return best
  }

  // ---- Tab list parsing ----

  private fun parseCommissionFromTabList(): Commission? {
    val lines = readTabListLines()
    val progressRe = Regex("([0-9,]+)/([0-9,]+)")

    for (line in lines) {
      // Mining: "mine X/Y mithril" or "X/Y mithril" with known ore keyword
      val hasMineVerb = line.contains("mine ")
      val hasOre = ORE_TO_TYPE.keys.any { line.contains(it) }
      if ((hasMineVerb || hasOre) && line.contains("/")) {
        val prog = progressRe.find(line)
        val current = prog?.groupValues?.get(1)?.replace(",", "")?.toIntOrNull() ?: 0
        val max = prog?.groupValues?.get(2)?.replace(",", "")?.toIntOrNull() ?: 1
        for ((keyword, blockType) in ORE_TO_TYPE) {
          if (line.contains(keyword)) {
            return Commission(CommissionType.MINING, blockType, current, max)
          }
        }
      }

      // Combat: "kill X/Y <mob name>"
      if (line.contains("kill ") && line.contains("/")) {
        val m = Regex("kill\\s+([0-9,]+)/([0-9,]+)\\s+(.+)").find(line) ?: continue
        val current = m.groupValues[1].replace(",", "").toIntOrNull() ?: 0
        val max = m.groupValues[2].replace(",", "").toIntOrNull() ?: 1
        val mob = m.groupValues[3].trim()
        if (mob.isNotBlank()) return Commission(CommissionType.COMBAT, mob, current, max)
      }
    }
    return null
  }

  private fun detectAreaFromTabList(): String? {
    val lines = readTabListLines()
    for (line in lines) {
      for (area in AREA_NAMES) {
        if (line.contains(area, ignoreCase = true)) return area
      }
    }
    return null
  }

  private fun readTabListLines(): List<String> {
    val connection = mc.connection ?: return emptyList()
    return try {
      resolveTabEntries(connection)
        .mapNotNull { resolveEntryDisplayName(it) }
        .map { ChatFormatting.stripFormatting(it)?.lowercase()?.trim() ?: "" }
        .filter { it.isNotBlank() }
    } catch (_: Exception) {
      emptyList()
    }
  }

  // ---- NPC scanning ----

  private fun findNearbyCommissionNpc(player: Player): LivingEntity? {
    val level = mc.level ?: return null
    var best: LivingEntity? = null
    var bestDist = 48f
    for (entity in level.entitiesForRendering()) {
      if (entity !is LivingEntity || entity is ArmorStand || entity == player) continue
      val name = ChatFormatting.stripFormatting(entity.name.string)?.lowercase()?.trim() ?: continue
      if (COMMISSION_NPC_KEYWORDS.none { name.contains(it) }) continue
      val dist = player.distanceTo(entity)
      if (dist < bestDist) {
        best = entity
        bestDist = dist
      }
    }
    return best
  }

  // ---- GUI parsing (commission from NPC menu) ----

  private fun parseCommissionFromOpenGui(): Commission? {
    val menu = mc.player?.containerMenu ?: return null
    val progressRe = Regex("([0-9,]+)/([0-9,]+)")

    for (slot in menu.slots) {
      if (!slot.hasItem()) continue
      val item = slot.item
      val lines = mutableListOf(item.hoverName.string.lowercase())
      lines += item.getLoreLines().map { it.string.lowercase() }

      for (line in lines) {
        // Mining
        val hasOre = ORE_TO_TYPE.keys.any { line.contains(it) }
        if ((line.contains("mine ") || hasOre) && line.contains("/")) {
          val prog = progressRe.find(line)
          val current = prog?.groupValues?.get(1)?.replace(",", "")?.toIntOrNull() ?: 0
          val max = prog?.groupValues?.get(2)?.replace(",", "")?.toIntOrNull() ?: 1
          for ((keyword, blockType) in ORE_TO_TYPE) {
            if (line.contains(keyword)) {
              return Commission(CommissionType.MINING, blockType, current, max)
            }
          }
        }
        // Combat
        if (line.contains("kill ") && line.contains("/")) {
          val m = Regex("kill\\s+([0-9,]+)/([0-9,]+)\\s+(.+)").find(line) ?: continue
          val current = m.groupValues[1].replace(",", "").toIntOrNull() ?: 0
          val max = m.groupValues[2].replace(",", "").toIntOrNull() ?: 1
          val mob = m.groupValues[3].trim()
          if (mob.isNotBlank()) return Commission(CommissionType.COMBAT, mob, current, max)
        }
      }
    }
    return null
  }

  // ---- GUI claim ----

  private fun tryClaimInGui(): Boolean {
    val menu = mc.player?.containerMenu ?: return false
    for (slot in menu.slots) {
      if (!slot.hasItem()) continue
      val item = slot.item
      val name = item.hoverName.string.lowercase()
      val lore = item.getLoreLines().joinToString("\n") { it.string.lowercase() }
      if (name.contains("claim") ||
        lore.contains("click to claim") ||
        lore.contains("claim reward") ||
        lore.contains("claim commission")
      ) {
        InventoryUtils.clickSlot(slot.index)
        return true
      }
    }
    return false
  }

  // ---- Tab list reflection (same pattern as AutoLanternModule) ----

  private fun resolveTabEntries(connection: Any): List<Any> {
    for (name in listOf("listPlayerEntries", "getListedOnlinePlayers", "getOnlinePlayers")) {
      val method = connection.javaClass.methods.firstOrNull { it.name == name && it.parameterCount == 0 } ?: continue
      val result = runCatching { method.invoke(connection) }.getOrNull() ?: continue
      when (result) {
        is Collection<*> -> return result.filterNotNull()
        is Iterable<*> -> return result.filterNotNull()
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
      val method = entry.javaClass.methods.firstOrNull { it.name == name && it.parameterCount == 0 } ?: continue
      val profile = runCatching { method.invoke(entry) }.getOrNull() ?: continue
      val nameMethod = profile.javaClass.methods.firstOrNull { it.name == "getName" && it.parameterCount == 0 } ?: continue
      val profileName = runCatching { nameMethod.invoke(profile) as? String }.getOrNull()
      if (!profileName.isNullOrBlank()) return profileName
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

  // ---- Utility ----

  private fun transition(newState: State) {
    state = newState
    stateTick = 0
  }

  private fun setStatus(msg: String) {
    statusText.value = msg
  }

  private fun resetMacro() {
    when (commission?.type) {
      CommissionType.MINING -> MiningMacroModule.stopForAutomation()
      CommissionType.COMBAT -> CombatMacroModule.stopForAutomation()
      null -> {}
    }
    DuskPathfinder.stop(mc, "Commission macro reset.")
    RotationExecutor.stopRotating()
    if (pendingUseRelease) {
      mc.options.keyUse?.setDown(false)
      pendingUseRelease = false
    }
    state = State.IDLE
    stateTick = 0
    commission = null
    detectedArea = null
    nearestVeinBlock = null
    npcInteractAttempts = 0
    guiClaimAttempts = 0
    setStatus("Idle")
    commissionText.value = "None"
    areaText.value = "Unknown"
  }
}
