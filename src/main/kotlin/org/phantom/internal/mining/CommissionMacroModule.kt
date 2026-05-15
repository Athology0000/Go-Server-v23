package org.phantom.internal.mining

import java.util.*
import kotlin.math.sqrt
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.ChatEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.*
import org.phantom.api.pathfinder.jni.NativePathfinder
import org.phantom.api.pathfinder.jni.PathStatus
import org.phantom.api.pathfinder.minecraft.MinecraftPathingRules
import org.phantom.api.rotation.RotationExecutor
import org.phantom.api.util.*
import org.phantom.api.util.player.MovementManager
import org.phantom.internal.combat.CombatMacroModule
import org.phantom.internal.pathfinding.PathfindingModule

object CommissionMacroModule : Module("Commission Macro") {

  override val category = ModuleCategory.MINING

  private val mc = Minecraft.getInstance()

  private val enabled = CheckboxSetting(
    "Enabled",
    "Completes Dwarven Mines commissions using the RDBT Phantom commission loop.",
    false,
  )

  private val info = InfoSetting(
    "Setup",
    "Requires a drill/pickaxe in hotbar, commissions visible in tab, and Royal Pigeon or emissary access.",
    InfoType.INFO,
  )

  private val avoidanceRadius = SliderSetting(
    "Avoidance Radius",
    "How close players can be to a mining spot before it is considered occupied.",
    10.0,
    0.0,
    30.0,
    1.0,
  )

  private val goblinWeaponSlot = SliderSetting(
    "Weapon Slot (Goblin)",
    "Hotbar slot with weapon for Goblin Slayer.",
    1.0,
    1.0,
    8.0,
    1.0,
  )

  private val statusText = TextSetting("Status", "Current macro state.", "Idle")
  private val commissionText = TextSetting("Commission", "Current commission.", "None")
  private val progressText = TextSetting("Progress", "Current commission progress.", "0%")
  private val toolText = TextSetting("Tool", "Current tool.", "None")

  private enum class State(val label: String) {
    IDLE("Idle"),
    CHOOSING("Choosing Commission"),
    TRAVELING("Traveling to Location"),
    WAITING_GUI_CLOSE("Closing GUI"),
    MINING("Mining"),
    SLAYER("Killing Mobs"),
    SELLING("Selling Items"),
    REFUELING("Refueling Drill"),
    CLAIMING("Claiming Rewards"),
  }

  private data class TabCommission(
    val name: String,
    val progress: Double,
  )

  data class CommissionHudRow(
    val label: String,
    val detail: String,
    val isTargeted: Boolean,
    val percent: Int,
  )

  private var state = State.IDLE
  private var pauseTicks = 0
  private var lastAreaWarpAt = 0L
  private var lastTabReadTick = 0L
  private var commissions: List<TabCommission> = emptyList()
  private var currentCommission: TabCommission? = null
  private var currentTask: CommissionTask? = null
  private var currentWaypoint: BlockPos? = null
  private var currentWaypoints: List<BlockPos> = emptyList()
  private var pathActivated = false
  private var travelPurpose: CommissionTaskType? = null
  private var pathingAvoidanceBreachAt = 0L
  private var lastAvoidanceRepathAt = 0L
  private var completedCommissions = 0
  private var sessionStartMs = 0L
  private var lastCommissionName: String? = null
  private var awaitingTabUpdate = false
  private var pendingUseRelease = false
  private var openAttempts = 0
  private var claimAttempts = 0
  private var detectorAttempts = 0
  private var emissariesUnlocked = true

  private data class DetectedCommission(
    val label: String,
    val progress: Double,
    val rank: Int,
  )

  private data class ParsedCommissionSelection(
    val commissions: List<TabCommission>,
    val selected: TabCommission?,
  )

  private val miningObjectivePrefixes = listOf("mine ", "collect ")
  private val combatObjectivePrefixes = listOf("kill ", "slay ", "defeat ", "punch ", "damage ")
  private val commissionObjectivePrefixes = listOf(
    *miningObjectivePrefixes.toTypedArray(),
    *combatObjectivePrefixes.toTypedArray(),
    "participate ",
    "deposit ",
    "open ",
    "enter ",
    "loot ",
    "obtain ",
    "find ",
  )

  private val miningAreaAliases = linkedMapOf(
    "royal mines" to "Royal Mines",
    "cliffside veins" to "Cliffside Veins",
    "lava springs" to "Lava Springs",
    "rampart's quarry" to "Rampart's Quarry",
    "ramparts quarry" to "Rampart's Quarry",
    "upper mines" to "Upper Mines",
  )

  val statusDisplay: String get() = statusText.value
  val modeDisplay: String get() = "PHANTOMV2"
  val commissionDisplay: String get() = commissionText.value
  val currentZoneDisplay: String get() = detectAreaFromTabList() ?: "Unknown"
  val targetedCommissionName: String? get() = currentCommission?.name
  val targetZoneDisplay: String get() = if (currentTask?.type == CommissionTaskType.SLAYER) "Combat Zone" else "Dwarven Mines"
  val isRunning: Boolean get() = enabled.value

  fun getCommissionRows(): List<CommissionHudRow> =
    commissions.map { commission ->
      val percent = (commission.progress * 100.0).toInt().coerceIn(0, 100)
      CommissionHudRow(
        label = commission.name,
        detail = if (percent >= 100) "DONE" else "$percent%",
        isTargeted = commission.name == currentCommission?.name,
        percent = percent,
      )
    }

  init {
    addSetting(enabled, info, avoidanceRadius, goblinWeaponSlot, statusText, commissionText, progressText, toolText)
    EventBus.register(this)
  }

  internal fun onLevelChange() {
    if (!enabled.value && state == State.IDLE) return
    enabled.value = false
    resetState()
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (pendingUseRelease) {
      mc.options.keyUse.setDown(false)
      pendingUseRelease = false
    }

    if (!enabled.value) {
      if (state != State.IDLE) resetState()
      return
    }

    if (sessionStartMs == 0L) sessionStartMs = System.currentTimeMillis()
    if (pauseTicks > 0) {
      pauseTicks--
      return
    }

    val tick = mc.level?.gameTime ?: 0L
    if (tick - lastTabReadTick >= 20L) {
      updateCommissionsIfChanged(readCommissionsFromTabList())
      lastTabReadTick = tick
    }

    handlePathingAvoidance()

    when (state) {
      State.IDLE -> handleIdle()
      State.CHOOSING -> handleChoosing()
      State.TRAVELING -> handleTraveling()
      State.WAITING_GUI_CLOSE -> handleWaitingGuiClose()
      State.MINING -> handleMining()
      State.SLAYER -> handleSlayer()
      State.SELLING -> handleSelling()
      State.REFUELING -> handleRefueling()
      State.CLAIMING -> handleClaiming()
    }

    syncHudText()
  }

  @SubscribeEvent
  fun onChat(event: ChatEvent.Receive) {
    if (!enabled.value) return
    val msg = ChatFormatting.stripFormatting(event.message ?: "")?.lowercase(Locale.US).orEmpty()
    if (msg.contains("commission complete") || msg.contains("completed a commission")) {
      completedCommissions++
      onCommissionComplete()
    }
    if (state == State.MINING && (msg.contains("inventory full") || msg.contains("your inventory is full"))) {
      onInventoryFull()
    }
    if (state == State.MINING && (msg.contains("drill is out of fuel") || msg.contains("drill has no fuel"))) {
      onDrillEmpty()
    }
    if (msg.contains("you died") || msg.contains("sending to server")) {
      delayedReset(67)
    }
  }

  private fun handleIdle() {
    val player = mc.player
    if (player == null) {
      setStatus("Waiting for player...")
      return
    }
    val tool = findMiningTool(player)
    if (tool == null) {
      ChatUtils.sendMessage("Commission Macro: no drill, gauntlet, or pickaxe found in hotbar.")
      enabled.value = false
      return
    }
    toolText.value = tool.hoverName.string
    setState(State.CHOOSING)
  }

  private fun handleChoosing() {
    val screen = mc.screen as? AbstractContainerScreen<*>
    if (screen != null) {
      val parsed = parseCommissionSelectionFromGui(screen)
      if (parsed.commissions.isNotEmpty()) {
        updateCommissionsIfChanged(parsed.commissions)
        mc.player?.closeContainer()
        detectorAttempts = 0
        setStatus("Detected commissions from GUI.")
        delay(2)
        return
      }
      detectorAttempts++
      if (detectorAttempts >= 30) {
        mc.player?.closeContainer()
        detectorAttempts = 0
        setStatus("No commissions detected.")
        return
      }
      setStatus("Reading commission GUI...")
      return
    }

    val area = detectAreaFromTabList()
    val now = System.currentTimeMillis()
    if (area != null && !area.contains("Dwarven", ignoreCase = true) && !area.contains("Forge", ignoreCase = true)) {
      if (now - lastAreaWarpAt > 10_000L) {
        sendCommand("warpforge")
        lastAreaWarpAt = now
        setStatus("Not in Dwarven Mines, warping...")
      }
      return
    }

    val completed = findCompletedCommission()
    if (completed != null && !awaitingTabUpdate) {
      currentCommission = completed
      onCommissionComplete()
      return
    }
    if (awaitingTabUpdate) return

    val active = getActiveCommissions()
    if (active.isEmpty()) {
      if (tryOpenOldCommissionDetector()) return
      setStatus("No commissions detected.")
      return
    }

    val supportedTasks = getSupportedTasks(active)
    if (supportedTasks.isEmpty()) {
      ChatUtils.sendMessage("Commission Macro: no supported commissions available.")
      enabled.value = false
      return
    }

    val avoidEntities = getAvoidanceEntities()
    val chosen = findAvailableCommission(supportedTasks, avoidEntities)
    if (chosen == null) {
      ChatUtils.sendMessage("Commission Macro: no available spots, finding new lobby.")
      sendCommand("hub")
      delayedReset(80)
      return
    }

    startCommission(chosen.first, chosen.second)
  }

  private fun tryOpenOldCommissionDetector(): Boolean {
    val player = mc.player ?: return false
    val pigeonSlot = InventoryUtils.findItemInHotbar("Royal Pigeon")
    if (pigeonSlot !in 0..8) return false

    if (detectorAttempts >= 6) {
      setStatus("No commissions detected.")
      return false
    }

    if (player.inventory.selectedSlot != pigeonSlot) {
      InventoryUtils.holdHotbarSlot(pigeonSlot)
      detectorAttempts++
      setStatus("Opening Royal Pigeon for commission detection...")
      delay(3)
      return true
    }

    rightClick()
    detectorAttempts++
    setStatus("Opening Royal Pigeon for commission detection...")
    delay(10)
    return true
  }

  private fun handleTraveling() {
    val task = currentTask ?: run {
      setState(State.CHOOSING)
      return
    }
    PathfindingModule.ensureEnabledForAutomation("commission macro")
    val cmd = NativePathfinder.tick()
    if (cmd != null) {
      pathActivated = true
      cmd.applyToPlayer()
    } else {
      MovementManager.clearForcedMovement()
    }

    when (NativePathfinder.status) {
      PathStatus.ARRIVED -> if (pathActivated || hasArrivedAt(currentWaypoint)) onPathComplete(task)
      PathStatus.FAILED -> onPathFail()
      else -> setStatus(State.TRAVELING.label)
    }
  }

  private fun handleWaitingGuiClose() {
    if (mc.screen != null) return
    setState(State.CHOOSING)
  }

  private fun handleMining() {
    if (!MiningMacroModule.isActive) {
      currentTask?.let { startMining(it) }
    }
  }

  private fun handleSlayer() {
    if (!CombatMacroModule.isActive) {
      currentTask?.let { startSlayer(it) }
    }
  }

  private fun handleSelling() {
    // RDBT Phantom has a trades-selling state. Keep the state surface, but return to work if the
    // local trade GUI path is unavailable.
    setStatus("Selling items...")
    setState(State.CHOOSING)
  }

  private fun handleRefueling() {
    ChatUtils.sendMessage("Commission Macro: drill refuel handling is not available in this client yet.")
    enabled.value = false
  }

  private fun handleClaiming() {
    val screen = mc.screen as? AbstractContainerScreen<*>
    if (screen != null) {
      val claimSlot = findClaimSlot(screen)
      if (claimSlot >= 0) {
        if (claimAttempts >= 8) {
          ChatUtils.sendMessage("Commission Macro: claim slot did not clear after $claimAttempts attempts.")
          mc.player?.closeContainer()
          resetCommissionAfterClaim()
          setState(State.CHOOSING)
          return
        }
        InventoryUtils.clickSlot(claimSlot, MouseClickType.LEFT, ClickType.PICKUP)
        claimAttempts++
        delay(10)
        setStatus("Claiming rewards...")
        return
      }
      mc.player?.closeContainer()
      resetCommissionAfterClaim()
      setState(State.WAITING_GUI_CLOSE)
      return
    }

    val player = mc.player ?: return
    val pigeonSlot = InventoryUtils.findItemInHotbar("Royal Pigeon")
    if (pigeonSlot >= 0) {
      if (player.inventory.selectedSlot != pigeonSlot) {
        InventoryUtils.holdHotbarSlot(pigeonSlot)
        delay(3)
      } else {
        rightClick()
        delay(10)
      }
      setStatus("Opening Royal Pigeon...")
      return
    }

    val target = getClosestEmissaryLocation(player)
    if (!hasArrivedAt(target, 4.0)) {
      travelPurpose = null
      startPath(target, 2.5)
      setStatus("Traveling to emissary...")
      return
    }

    val emissary = findNearestEmissary(player)
    if (emissary == null && emissariesUnlocked) {
      emissariesUnlocked = false
      ChatUtils.sendMessage("Commission Macro: emissary not found, reverting to King.")
      startPath(CommissionData.emissaryLocations.first(), 2.5)
      return
    }

    if (emissary != null) faceEntity(emissary) else faceBlock(target)
    rightClick()
    delay(10)
    setStatus("Opening emissary...")
  }

  private fun startCommission(tabCommission: TabCommission, task: CommissionTask) {
    currentCommission = tabCommission
    currentTask = task
    currentWaypoints = resolveWaypoints(task)
    currentWaypoint = getClosestWaypoint(currentWaypoints)
    travelPurpose = task.type
    pathingAvoidanceBreachAt = 0L
    lastAvoidanceRepathAt = 0L

    ChatUtils.sendMessage("Commission Macro: starting ${tabCommission.name} commission.")
    setState(State.TRAVELING)
    startPath(currentWaypoint ?: return, 2.5)
  }

  private fun onPathComplete(task: CommissionTask) {
    pathingAvoidanceBreachAt = 0L
    NativePathfinder.stop()
    MovementManager.clearForcedMovement()
    when (task.type) {
      CommissionTaskType.MINING -> {
        setState(State.MINING)
        startMining(task)
      }
      CommissionTaskType.SLAYER -> {
        setState(State.SLAYER)
        startSlayer(task)
      }
    }
  }

  private fun onPathFail() {
    ChatUtils.sendMessage("Commission Macro: failed to path to ${currentCommission?.name ?: "commission"}, retrying.")
    NativePathfinder.stop()
    MovementManager.clearForcedMovement()
    currentCommission = null
    currentTask = null
    currentWaypoint = null
    currentWaypoints = emptyList()
    setState(State.CHOOSING)
  }

  private fun startMining(task: CommissionTask) {
    if (mc.screen != null) {
      setState(State.WAITING_GUI_CLOSE)
      return
    }
    val player = mc.player ?: return
    val toolSlot = findMiningToolSlot(player)
    if (toolSlot < 0) {
      ChatUtils.sendMessage("Commission Macro: no drill, gauntlet, or pickaxe found.")
      enabled.value = false
      return
    }
    InventoryUtils.holdHotbarSlot(toolSlot)
    MiningMacroModule.startForAutomation(miningTypesFor(task))
  }

  private fun startSlayer(task: CommissionTask) {
    val name = currentCommission?.name ?: task.primaryName
    val target = when {
      name.equals("Goblin Slayer", ignoreCase = true) -> {
        InventoryUtils.holdHotbarSlot((goblinWeaponSlot.value.toInt() - 1).coerceIn(0, 7))
        "Goblin"
      }
      name.equals("Treasure Hoarder Puncher", ignoreCase = true) -> "Treasure"
      else -> "Glacite Walker"
    }
    CombatMacroModule.startForAutomation(target)
  }

  private fun onCommissionComplete() {
    NativePathfinder.stop()
    MovementManager.clearForcedMovement()
    MiningMacroModule.stopForAutomation()
    CombatMacroModule.stopForAutomation()
    lastCommissionName = currentCommission?.name
    awaitingTabUpdate = true
    claimAttempts = 0
    setState(State.CLAIMING)
  }

  private fun onInventoryFull() {
    ChatUtils.sendMessage("Commission Macro: inventory full, pausing mining.")
    MiningMacroModule.stopForAutomation()
    setState(State.SELLING)
  }

  private fun onDrillEmpty() {
    MiningMacroModule.stopForAutomation()
    setState(State.REFUELING)
  }

  private fun delayedReset(delay: Int) {
    resetState()
    pauseTicks = delay
  }

  private fun resetState() {
    NativePathfinder.stop()
    MovementManager.clearForcedMovement()
    MiningMacroModule.stopForAutomation()
    CombatMacroModule.stopForAutomation()
    RotationExecutor.stopRotating()
    state = State.IDLE
    pauseTicks = 0
    commissions = emptyList()
    currentCommission = null
    currentTask = null
    currentWaypoint = null
    currentWaypoints = emptyList()
    pathActivated = false
    travelPurpose = null
    pathingAvoidanceBreachAt = 0L
    lastAvoidanceRepathAt = 0L
    awaitingTabUpdate = false
    pendingUseRelease = false
    openAttempts = 0
    claimAttempts = 0
    detectorAttempts = 0
    sessionStartMs = 0L
    setStatus(State.IDLE.label)
    commissionText.value = "None"
    progressText.value = "0%"
    toolText.value = "None"
  }

  private fun resetCommissionAfterClaim() {
    currentCommission = null
    currentTask = null
    currentWaypoint = null
    currentWaypoints = emptyList()
    awaitingTabUpdate = false
    claimAttempts = 0
    detectorAttempts = 0
  }

  private fun setState(newState: State) {
    if (state == newState) return
    state = newState
    setStatus(newState.label)
  }

  private fun delay(ticks: Int) {
    pauseTicks = ticks.coerceAtLeast(0)
  }

  private fun setStatus(text: String) {
    statusText.value = text
  }

  private fun syncHudText() {
    commissionText.value = currentCommission?.name ?: "None"
    progressText.value = getCommissionProgressDisplay()
    val tool = mc.player?.let(::findMiningTool)
    if (tool != null) toolText.value = truncateToolName(tool.hoverName.string)
  }

  private fun getCommissionProgressDisplay(): String {
    val name = currentCommission?.name ?: return "0%"
    val progress = commissions.firstOrNull { it.name == name }?.progress ?: currentCommission?.progress ?: 0.0
    return if (progress >= 1.0) "DONE" else "${(progress * 100.0).toInt().coerceIn(0, 100)}%"
  }

  private fun truncateToolName(name: String): String =
    if (name.length > 45) name.take(43) + ".." else name

  private fun getActiveCommissions(): List<TabCommission> =
    commissions.filter { it.progress < 1.0 }

  private fun findCompletedCommission(): TabCommission? =
    commissions.firstOrNull { commission ->
      commission.progress >= 1.0 && CommissionData.resolveTask(commission.name) != null
    }

  private fun getSupportedTasks(activeCommissions: List<TabCommission>): List<Pair<TabCommission, CommissionTask>> =
    activeCommissions
      .mapNotNull { tab -> CommissionData.resolveTask(tab.name)?.let { tab to it } }
      .filter { (_, task) -> isSupportedTask(task) }
      .sortedBy { (_, task) -> task.cost }

  private fun isSupportedTask(task: CommissionTask): Boolean {
    if (task.type == CommissionTaskType.SLAYER && task.names.any { it.equals("Goblin Slayer", ignoreCase = true) }) {
      return getGoblinWeapon() != null
    }
    return task.type == CommissionTaskType.MINING || task.type == CommissionTaskType.SLAYER
  }

  private fun findAvailableCommission(
    supportedTasks: List<Pair<TabCommission, CommissionTask>>,
    avoidEntities: List<Entity>,
  ): Pair<TabCommission, CommissionTask>? {
    for ((tab, task) in supportedTasks) {
      val safe = getSafeWaypoints(task, avoidEntities)
      if (safe.isNotEmpty()) return tab to task.copy(waypoints = safe, useAllMiningWaypoints = false)
    }
    return null
  }

  private fun resolveWaypoints(task: CommissionTask): List<BlockPos> =
    if (task.type == CommissionTaskType.MINING) CommissionData.miningWaypointsFor(task)
    else task.waypoints

  private fun getSafeWaypoints(task: CommissionTask, avoidEntities: List<Entity>): List<BlockPos> {
    val waypoints = resolveWaypoints(task)
    val radius = avoidanceRadius.value
    if (radius <= 0.0) return waypoints
    return waypoints.filter { waypoint ->
      avoidEntities.none { entity -> distance(entity.x, entity.y, entity.z, waypoint) < radius }
    }
  }

  private fun getAvoidanceEntities(): List<Entity> {
    val level = mc.level ?: return emptyList()
    val player = mc.player ?: return emptyList()
    if (avoidanceRadius.value <= 0.0) return emptyList()
    return level.players().filter { it.uuid != player.uuid }
  }

  private fun getClosestWaypoint(waypoints: List<BlockPos>): BlockPos? {
    val player = mc.player ?: return waypoints.firstOrNull()
    if (currentTask?.type != CommissionTaskType.MINING) {
      return waypoints.minByOrNull { distance(player.x, player.y, player.z, it) }
    }
    return waypoints.minByOrNull { miningWaypointScore(player, it) }
  }

  private fun miningWaypointScore(player: Player, pos: BlockPos): Double {
    val distance = distance(player.x, player.y, player.z, pos)
    val losPenalty = miningLosPenalty(player, pos)
    return losPenalty + distance
  }

  private fun miningLosPenalty(player: Player, pos: BlockPos): Double {
    val level = mc.level ?: return 200.0
    val eye = player.eyePosition
    val target = Vec3(pos.x + 0.5, pos.y + 1.0, pos.z + 0.5)
    val hit = level.clip(
      ClipContext(
        eye,
        target,
        ClipContext.Block.COLLIDER,
        ClipContext.Fluid.NONE,
        player
      )
    )

    if (hit.type == HitResult.Type.MISS) return 0.0

    val remaining = hit.location.distanceTo(target)
    return when {
      remaining <= 1.5 -> 20.0
      remaining <= 3.0 -> 45.0
      remaining <= 6.0 -> 90.0
      else -> 220.0
    }
  }

  private fun handlePathingAvoidance() {
    val task = currentTask ?: return
    if (state != State.TRAVELING || task.type != CommissionTaskType.MINING || avoidanceRadius.value <= 0.0) {
      pathingAvoidanceBreachAt = 0L
      return
    }
    val waypoint = currentWaypoint ?: return
    val breached = getAvoidanceEntities().any { entity -> distance(entity.x, entity.y, entity.z, waypoint) < avoidanceRadius.value }
    if (!breached) {
      pathingAvoidanceBreachAt = 0L
      return
    }
    val now = System.currentTimeMillis()
    if (pathingAvoidanceBreachAt == 0L) {
      pathingAvoidanceBreachAt = now
      return
    }
    if (now - pathingAvoidanceBreachAt < 5_000L || now - lastAvoidanceRepathAt < 2_000L) return
    val safe = getSafeWaypoints(task, getAvoidanceEntities()).filter { it != waypoint }
    if (safe.isEmpty()) return
    currentWaypoints = safe
    currentWaypoint = getClosestWaypoint(safe)
    pathingAvoidanceBreachAt = 0L
    lastAvoidanceRepathAt = now
    ChatUtils.sendMessage("Commission Macro: avoidance radius breached for 5s, repathing.")
    startPath(currentWaypoint ?: return, 2.5)
  }

  private fun startPath(pos: BlockPos, radius: Double) {
    val level = mc.level
    val resolved = if (level != null) MinecraftPathingRules.resolveTarget(level, pos) ?: pos else pos
    NativePathfinder.stop()
    pathActivated = false
    NativePathfinder.setTargetWithRadius(resolved.x + 0.5, resolved.y.toDouble(), resolved.z + 0.5, radius)
  }

  private fun hasArrivedAt(pos: BlockPos?, radius: Double = 2.5): Boolean {
    val player = mc.player ?: return false
    if (pos == null) return false
    return distance(player.x, player.y, player.z, pos) <= radius
  }

  private fun readCommissionsFromTabList(): List<TabCommission> {
    val lines = readTabLines()
    val found = linkedMapOf<String, TabCommission>()
    for (line in lines) {
      val normalized = normalize(line)
      val parsed = parseCommissionFromTabLine(normalized)
      if (parsed != null) {
        val commission = parsed
        found[commission.name] = commission
        continue
      }
      for (task in CommissionData.commissionData) {
        for (name in task.names) {
          if (!normalized.contains(normalize(name))) continue
          val progress = parseProgress(normalized)
          found[name] = TabCommission(name, progress)
        }
      }
    }
    return found.values.toList()
  }

  private fun parseCommissionFromTabLine(line: String): TabCommission? {
    if (!looksLikeTabCommissionLine(line)) return null
    val label = buildCommissionLabelFromTabLine(line) ?: return null
    val task = CommissionData.resolveTask(label) ?: return null
    return TabCommission(
      name = task.names.firstOrNull { it.equals(label, ignoreCase = true) } ?: task.primaryName,
      progress = parseProgress(line),
    )
  }

  private fun looksLikeTabCommissionLine(line: String): Boolean {
    if (line.isBlank() || line.contains("commission complete", ignoreCase = true)) return false
    val hasProgress =
      Regex("([0-9]{1,3}(?:\\.[0-9]+)?)\\s*%").containsMatchIn(line) ||
        Regex("([0-9,]+)\\s*/\\s*([0-9,]+)").containsMatchIn(line) ||
        line.contains("done", ignoreCase = true) ||
        line.contains("complete", ignoreCase = true)
    if (!hasProgress) return false
    return commissionObjectivePrefixes.any { prefix -> line.startsWith(prefix) || line.contains(" $prefix") } ||
      miningAreaAliases.keys.any { area -> line.contains(area) } ||
      CommissionData.commissionData.any { task -> task.names.any { name -> line.contains(normalize(name)) } }
  }

  private fun buildCommissionLabelFromTabLine(line: String): String? {
    canonicalizeKnownCommissionLabel(line)?.let { return it }
    val lines = listOf(line)

    val miningKeyword = extractMiningKeyword(lines)
    if (miningKeyword != null) return buildMiningCommissionLabel(lines, miningKeyword)

    val combatTarget = extractCombatCommissionTarget(lines)
    if (combatTarget != null) return buildCombatCommissionLabel(lines, combatTarget)

    return null
  }

  private fun updateCommissionsIfChanged(newCommissions: List<TabCommission>) {
    if (commissions == newCommissions) return
    if (newCommissions.isNotEmpty()) detectorAttempts = 0
    if (awaitingTabUpdate && lastCommissionName != null) {
      val stillCompleted = newCommissions.any { it.name == lastCommissionName && it.progress >= 1.0 }
      if (!stillCompleted) awaitingTabUpdate = false
    }
    commissions = newCommissions
  }

  private fun parseProgress(line: String): Double {
    if (line.contains("done") || line.contains("completed") || line.contains("complete")) return 1.0
    Regex("([0-9]{1,3}(?:\\.[0-9]+)?)\\s*%").find(line)?.let { match ->
      return ((match.groupValues[1].toDoubleOrNull() ?: 0.0) / 100.0).coerceIn(0.0, 1.0)
    }
    Regex("([0-9,]+)\\s*/\\s*([0-9,]+)").find(line)?.let { match ->
      val current = match.groupValues[1].replace(",", "").toDoubleOrNull() ?: 0.0
      val max = match.groupValues[2].replace(",", "").toDoubleOrNull() ?: 1.0
      return if (max <= 0.0) 0.0 else (current / max).coerceIn(0.0, 1.0)
    }
    return 0.0
  }

  private fun readTabLines(): List<String> =
    org.phantom.api.util.TabListUtils.displayNames()
      .map { it.trim() }
      .filter { it.isNotBlank() }

  private fun detectAreaFromTabList(): String? {
    val areaNames = listOf("Dwarven Mines", "Crystal Hollows", "Glacite Tunnels", "Forge")
    val lines = readTabLines()
    return areaNames.firstOrNull { area -> lines.any { it.contains(area, ignoreCase = true) } }
  }

  private fun findClaimSlot(screen: AbstractContainerScreen<*>): Int {
    for (slot in getCommissionCandidateSlots(screen)) {
      if (!slot.hasItem()) continue
      val lines = buildGuiTextLines(slot.item)
      if (lines.isEmpty()) continue
      val combined = lines.joinToString("\n")
      if (isClaimableCommissionSlot(lines, combined)) return slot.index
    }
    return -1
  }

  private fun parseCommissionSelectionFromGui(screen: AbstractContainerScreen<*>): ParsedCommissionSelection {
    val detected = mutableListOf<DetectedCommission>()
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

      val label = buildCommissionLabel(item, lines) ?: continue
      val task = CommissionData.resolveTask(label) ?: continue
      detected += DetectedCommission(
        label = task.names.firstOrNull { it.equals(label, ignoreCase = true) } ?: task.primaryName,
        progress = if (max <= 0) 0.0 else (current.toDouble() / max.toDouble()).coerceIn(0.0, 1.0),
        rank = task.cost,
      )
    }

    val commissions = detected
      .distinctBy { "${normalizeComparisonText(it.label)}:${it.progress}" }
      .sortedWith(compareBy<DetectedCommission> { it.rank }.thenBy { normalizeComparisonText(it.label) })
      .map { TabCommission(it.label, it.progress) }

    return ParsedCommissionSelection(commissions, commissions.firstOrNull())
  }

  private fun getCommissionCandidateSlots(screen: AbstractContainerScreen<*>): List<Slot> {
    val containerSlots = screen.menu.slots.filterNot { it.container is Inventory }
    return if (containerSlots.isNotEmpty()) containerSlots else screen.menu.slots
  }

  private fun buildGuiTextLines(item: ItemStack): List<String> {
    val lines = mutableListOf<String>()
    val name = ChatFormatting.stripFormatting(item.hoverName.string)?.lowercase(Locale.US)?.trim().orEmpty()
    if (name.isNotBlank()) lines += name
    lines += item.getLoreLines()
      .mapNotNull { ChatFormatting.stripFormatting(it.string)?.lowercase(Locale.US)?.trim() }
      .filter { it.isNotBlank() }
    return lines
  }

  private fun isClaimableCommissionSlot(lines: List<String>, combined: String): Boolean {
    if (!looksLikeCommissionEntry(lines, combined)) return false
    if (isClaimCommissionText(combined)) return true
    val (current, max) = parseCommissionProgress(combined)
    return max > 0 && current >= max
  }

  private fun looksLikeCommissionEntry(lines: List<String>, combined: String): Boolean {
    val firstLine = lines.firstOrNull().orEmpty()
    if (firstLine.contains("close") || firstLine.contains("back") || firstLine.contains("next page")) return false
    if (combined.contains("royal pigeon")) return false
    val normalizedFirstLine = normalizeComparisonText(firstLine)
    if (canonicalizeKnownCommissionLabel(firstLine) != null) return true
    if (normalizedFirstLine == "goblin raid" || normalizedFirstLine == "star sentry puncher") return true
    if (isClaimCommissionText(combined)) return true

    val hasObjectiveLine = lines.any { line -> commissionObjectivePrefixes.any(line::startsWith) }
    val hasProgressMarker =
      combined.contains(" progress") ||
        Regex("([0-9,]+)\\s*/\\s*([0-9,]+)").containsMatchIn(combined) ||
        Regex("([0-9]{1,3}(?:\\.[0-9]+)?)\\s*%").containsMatchIn(combined)

    return hasObjectiveLine && hasProgressMarker
  }

  private fun isClaimCommissionText(combined: String): Boolean {
    val normalized = normalizeComparisonText(combined)
    return normalized.contains("click to claim") ||
      normalized.contains("click here to claim") ||
      normalized.contains("claim reward") ||
      normalized.contains("claim rewards") ||
      normalized.contains("claim commission") ||
      normalized.contains("commission complete") ||
      normalized.contains("commission completed") ||
      (normalized.contains("claim") && normalized.contains("reward")) ||
      normalized.contains("completed")
  }

  private fun parseCommissionProgress(combined: String): Pair<Int, Int> {
    Regex("([0-9,]+)\\s*/\\s*([0-9,]+)").find(combined)?.let { match ->
      val current = match.groupValues[1].replace(",", "").toIntOrNull() ?: 0
      val max = match.groupValues[2].replace(",", "").toIntOrNull() ?: 100
      return current to max.coerceAtLeast(1)
    }
    Regex("([0-9]{1,3}(?:\\.[0-9]+)?)\\s*%").find(combined)?.let { match ->
      val percent = match.groupValues[1].toDoubleOrNull() ?: 0.0
      return percent.toInt().coerceIn(0, 100) to 100
    }
    if (isClaimCommissionText(combined)) return 100 to 100
    return 0 to 100
  }

  private fun buildCommissionLabel(item: ItemStack, lines: List<String>): String? {
    lines.firstNotNullOfOrNull(::canonicalizeKnownCommissionLabel)?.let { return it }

    val itemName = ChatFormatting.stripFormatting(item.hoverName.string)
      ?.replace(Regex("\\s+"), " ")
      ?.trim()
      .orEmpty()
    if (itemName.isNotBlank() && !itemName.matches(Regex("Commission\\s*#\\d+", RegexOption.IGNORE_CASE))) {
      canonicalizeKnownCommissionLabel(itemName)?.let { return it }
    }

    val miningKeyword = extractMiningKeyword(lines)
    if (miningKeyword != null) return buildMiningCommissionLabel(lines, miningKeyword)

    val combatTarget = extractCombatCommissionTarget(lines)
    if (combatTarget != null) return buildCombatCommissionLabel(lines, combatTarget)

    return null
  }

  private fun canonicalizeKnownCommissionLabel(raw: String): String? {
    val normalized = normalizeComparisonText(raw)
    return CommissionData.commissionData
      .asSequence()
      .flatMap { it.names.asSequence() }
      .firstOrNull { normalizeComparisonText(it) == normalized }
  }

  private fun extractMiningKeyword(lines: List<String>): String? {
    val keywords = linkedMapOf(
      "titanium" to "titanium",
      "mithril" to "mithril",
      "glacite" to "glacite",
      "umber" to "umber",
      "tungsten" to "tungsten",
    )
    for (line in lines) {
      if (miningObjectivePrefixes.none(line::startsWith)) continue
      for ((keyword, value) in keywords) {
        if (line.contains(keyword)) return value
      }
    }
    return null
  }

  private fun buildMiningCommissionLabel(lines: List<String>, miningKeyword: String): String {
    val area = extractMiningAreaLabel(lines)
    val suffix = when (miningKeyword) {
      "titanium" -> "Titanium"
      "mithril" -> "Mithril"
      else -> titleCase(miningKeyword)
    }
    if (area != null) return "$area $suffix"
    return when (suffix) {
      "Titanium" -> "Titanium Miner"
      "Mithril" -> "Mithril Miner"
      else -> suffix
    }
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
    return miningAreaAliases.entries.firstOrNull { (alias, _) -> normalized.contains(alias) }?.value
  }

  private fun extractCombatCommissionTarget(lines: List<String>): String? {
    val objectiveRe = Regex("(?:kill|slay|defeat|punch|damage)\\s+(?:[0-9,]+\\s*/\\s*[0-9,]+\\s+)?(?:[0-9,]+\\s+)?(.+)")
    for (line in lines) {
      val match = objectiveRe.find(line) ?: continue
      val rawTarget = match.groupValues[1]
        .substringBefore(" progress")
        .substringBefore(" in ")
        .replace(Regex("[^a-z0-9' -]"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()
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

  private fun buildCombatCommissionLabel(lines: List<String>, combatTarget: String): String {
    val normalizedTarget = normalizeComparisonText(combatTarget)
    return when {
      normalizedTarget.contains("glacite walker") || normalizedTarget.contains("ice walker") -> "Glacite Walker Slayer"
      normalizedTarget.contains("treasure hoarder") || normalizedTarget.contains("treasure hunter") -> "Treasure Hoarder Puncher"
      lines.any { normalizeComparisonText(it).contains("goblin raid") } -> "Goblin Raid"
      normalizedTarget.contains("goblin") -> "Goblin Slayer"
      else -> titleCase(combatTarget)
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

  private fun getClosestEmissaryLocation(player: Player): BlockPos =
    getAvailableEmissaryLocations().minByOrNull { distance(player.x, player.y, player.z, it) }
      ?: CommissionData.emissaryLocations.first()

  private fun getAvailableEmissaryLocations(): List<BlockPos> =
    if (emissariesUnlocked) CommissionData.emissaryLocations else listOf(CommissionData.emissaryLocations.first())

  private fun findNearestEmissary(player: Player): Entity? {
    val level = mc.level ?: return null
    val radius = 5.0
    return level.getEntities(player, player.boundingBox.inflate(radius)).firstOrNull { entity ->
      val name = entity.name.string
      name.contains("Emissary", ignoreCase = true) || name.contains("King", ignoreCase = true)
    }
  }

  private fun faceEntity(entity: Entity) {
    val target = Vec3(entity.x, entity.eyeY, entity.z)
    applyRotation(target)
  }

  private fun faceBlock(pos: BlockPos) {
    applyRotation(Vec3(pos.x + 0.5, pos.y + 1.5, pos.z + 0.5))
  }

  private fun applyRotation(target: Vec3) {
    val player = mc.player ?: return
    val rot = AngleUtils.getRotation(target)
    player.yRot = rot.yaw
    player.yHeadRot = rot.yaw
    player.yBodyRot = rot.yaw
    player.xRot = rot.pitch
  }

  private fun rightClick() {
    mc.options.keyUse.setDown(false)
    mc.options.keyUse.setDown(true)
    pendingUseRelease = true
    openAttempts++
  }

  private fun sendCommand(command: String) {
    (mc.player as? LocalPlayer)?.connection?.sendCommand(command)
  }

  private fun miningTypesFor(task: CommissionTask): String {
    val name = currentCommission?.name ?: task.primaryName
    return when {
      name.contains("Titanium", ignoreCase = true) ->
        "Mithril (Gray), Mithril (Dark), Mithril (Hot), Titanium"
      else ->
        "Mithril (Gray), Mithril (Dark), Mithril (Hot)"
    }
  }

  private fun findMiningTool(player: Player): ItemStack? {
    val slot = findMiningToolSlot(player)
    return if (slot >= 0) player.inventory.getItem(slot) else null
  }

  private fun findMiningToolSlot(player: Player): Int {
    for (slot in 0..8) {
      val stack = player.inventory.getItem(slot)
      if (stack.isEmpty) continue
      val name = stack.hoverName.string
      if (
        name.contains("Drill", ignoreCase = true) ||
        name.contains("Gauntlet", ignoreCase = true) ||
        name.contains("Pickaxe", ignoreCase = true)
      ) {
        return slot
      }
    }
    return -1
  }

  private fun getGoblinWeapon(): ItemStack? {
    val player = mc.player ?: return null
    val slot = (goblinWeaponSlot.value.toInt() - 1).coerceIn(0, 7)
    val stack = player.inventory.getItem(slot)
    if (stack.isEmpty) return null
    val name = stack.hoverName.string
    if (name.contains("Mithril", ignoreCase = true) || name.contains("Titanium", ignoreCase = true)) return null
    return stack
  }

  private fun distance(x: Double, y: Double, z: Double, pos: BlockPos): Double {
    val dx = x - (pos.x + 0.5)
    val dy = y - pos.y.toDouble()
    val dz = z - (pos.z + 0.5)
    return sqrt(dx * dx + dy * dy + dz * dz)
  }

  private fun normalize(value: String): String =
    ChatFormatting.stripFormatting(value)
      ?.lowercase(Locale.US)
      ?.replace("&", " and ")
      ?.replace(Regex("[^a-z0-9' /%]"), " ")
      ?.replace(Regex("\\s+"), " ")
      ?.trim()
      .orEmpty()
}
