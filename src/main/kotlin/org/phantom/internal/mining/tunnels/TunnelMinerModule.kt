package org.phantom.internal.mining.tunnels

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.event.impl.render.WorldRenderEvent
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.InfoSetting
import org.phantom.api.module.setting.impl.InfoType
import org.phantom.api.module.setting.impl.ModeSetting
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.pathfinder.jni.NativePathfinder
import org.phantom.api.pathfinder.jni.PathStatus
import org.phantom.api.util.player.MovementManager
import org.phantom.internal.mining.MiningModule
import org.phantom.internal.pathfinding.OverlayRenderEngine
import org.phantom.internal.pathfinding.PathfindingModule
import org.phantom.internal.skyblock.DwarvenSidebarLocationParser
import org.phantom.internal.skyblock.HypixelManager
import kotlin.math.min

object TunnelMinerModule : Module("Tunnel Miner") {
  override val category = ModuleCategory.MINING

  private const val OVERLAY_TAG = "tunnel-miner"
  private const val RESCAN_TICKS = 10

  private val mc = Minecraft.getInstance()

  private val enabled = CheckboxSetting(
    "Enabled",
    "Pathfind to the closest selected tunnel vein stand point.",
    false
  )

  private val oreMode = ModeSetting(
    "Ore",
    "Ore type to target when Tunnel Miner is enabled manually.",
    0,
    arrayOf("All", "Glacite", "Tungsten", "Umber", "Peridot", "Aquamarine", "Onyx", "Citrine")
  )

  private val dynamicScan = CheckboxSetting(
    "Dynamic Scan",
    "Find nearby veins directly from loaded world blocks.",
    true
  )

  private val recordedData = CheckboxSetting(
    "Recorded Data",
    "Use bundled recorded tunnel vein data when it matches loaded blocks.",
    true
  )

  private val routePoints = CheckboxSetting(
    "Route Points",
    "Use saved tunnel route points before free scanning.",
    true
  )

  private val scanRadius = SliderSetting(
    "Scan Radius",
    "Radius used for dynamic vein discovery around the player.",
    22.0,
    8.0,
    48.0,
    1.0
  )

  private val standPointLimit = SliderSetting(
    "Stand Point Limit",
    "Maximum stand points shown for the selected vein.",
    24.0,
    4.0,
    64.0,
    1.0
  )

  private val arrivalRadius = SliderSetting(
    "Arrival Radius",
    "How close the native pathfinder must get to the selected stand point.",
    1.35,
    0.5,
    2.5,
    0.05
  )

  private val status = InfoSetting(
    "Status",
    "Idle.",
    InfoType.INFO
  )

  override fun isVisibleInUi(): Boolean =
    isInGlaciteTunnels()

  var isActive: Boolean = false
    private set

  private var automationSource: String? = null
  private var selectedOres: Set<TunnelOreType> = emptySet()
  private var currentTarget: TunnelVeinScanner.TunnelTarget? = null
  private var currentStandPoints: List<TunnelVeinScanner.TunnelTarget> = emptyList()
  private var pathTarget: BlockPos? = null
  private var lastScanTick = Long.MIN_VALUE
  private var ownsPathfinder = false
  private var wasRunning = false

  init {
    addSetting(
      enabled,
      oreMode,
      dynamicScan,
      recordedData,
      routePoints,
      scanRadius,
      standPointLimit,
      arrivalRadius,
      status,
    )

    EventBus.register(this)
  }

  override fun onDisable() {
    enabled.value = false
    stopForAutomation()
  }

  fun startForAutomation(ore: TunnelOreType, source: String) {
    startForAutomation(setOf(ore), source)
  }

  fun startForAutomation(ores: Set<TunnelOreType>, source: String) {
    val wasActive = isActive
    selectedOres = ores
    automationSource = source
    isActive = true
    status.value = "Automation from $source: ${oreLabel(ores)}."
    if (!wasActive) {
      requestStartupScrape()
      wasRunning = true
    }
  }

  fun stopForAutomation() {
    stopPath()
    selectedOres = emptySet()
    automationSource = null
    isActive = false
    wasRunning = false
    currentTarget = null
    currentStandPoints = emptyList()
    pathTarget = null
    status.value = "Idle."
    OverlayRenderEngine.clearTag(OVERLAY_TAG)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.End) {
    val shouldRun = enabled.value || automationSource != null
    if (shouldRun && !wasRunning) {
      requestStartupScrape()
    }
    wasRunning = shouldRun
    isActive = shouldRun

    if (!shouldRun) {
      if (ownsPathfinder || currentTarget != null) {
        stopPath()
        currentTarget = null
        currentStandPoints = emptyList()
        pathTarget = null
        OverlayRenderEngine.clearTag(OVERLAY_TAG)
      }
      status.value = "Idle."
      return
    }

    val level = mc.level ?: return
    mc.player ?: return

    if (level.gameTime - lastScanTick >= RESCAN_TICKS || currentTarget == null) {
      lastScanTick = level.gameTime
      selectTarget()
    }

    val target = currentTarget ?: return
    val stand = target.standPos
    val nativeStatus = NativePathfinder.status
    if (nativeStatus == PathStatus.FAILED && pathTarget == stand) {
      TunnelVeinScanner.markBadStand(stand)
      currentTarget = null
      currentStandPoints = emptyList()
      pathTarget = null
      ownsPathfinder = false
      selectTarget()
      return
    }

    val needsPath = pathTarget != stand || nativeStatus == PathStatus.IDLE

    if (needsPath) {
      startPathTo(stand)
    }

    if (ownsPathfinder) {
      NativePathfinder.tick()?.applyToPlayer()
    }
  }

  @SubscribeEvent
  fun onRender(@Suppress("UNUSED_PARAMETER") event: WorldRenderEvent.Last) {
    val level = mc.level ?: return
    val target = currentTarget

    OverlayRenderEngine.clearTag(OVERLAY_TAG)
    if (!(enabled.value || automationSource != null) || target == null) return

    val veinFill = OverlayRenderEngine.Color(80, 180, 255, 48)
    val veinOutline = OverlayRenderEngine.Color(80, 180, 255, 220)
    val selectedFill = OverlayRenderEngine.Color(255, 220, 80, 76)
    val selectedOutline = OverlayRenderEngine.Color(255, 220, 80, 245)
    val standFill = OverlayRenderEngine.Color(80, 255, 135, 82)
    val standOutline = OverlayRenderEngine.Color(80, 255, 135, 235)
    val selectedStandFill = OverlayRenderEngine.Color(255, 100, 80, 92)
    val selectedStandOutline = OverlayRenderEngine.Color(255, 100, 80, 245)

    for (block in target.veinBlocks.take(96)) {
      val isEdge = block == target.edgeBlock
      OverlayRenderEngine.addBox(
        level,
        block.x - 0.003,
        block.y - 0.003,
        block.z - 0.003,
        block.x + 1.003,
        block.y + 1.003,
        block.z + 1.003,
        if (isEdge) selectedFill else veinFill,
        if (isEdge) selectedOutline else veinOutline,
        if (isEdge) 2.5f else 1.5f,
        durationTicks = 3,
        tag = OVERLAY_TAG,
        forceRender = true
      )
    }

    for (standTarget in currentStandPoints.take(standPointLimit.value.toInt().coerceAtLeast(1))) {
      val stand = standTarget.standPos
      val selected = stand == target.standPos
      OverlayRenderEngine.addBox(
        level,
        stand.x + 0.18,
        stand.y + 0.02,
        stand.z + 0.18,
        stand.x + 0.82,
        stand.y + 0.12,
        stand.z + 0.82,
        if (selected) selectedStandFill else standFill,
        if (selected) selectedStandOutline else standOutline,
        if (selected) 2.4f else 1.5f,
        durationTicks = 3,
        tag = OVERLAY_TAG,
        forceRender = true
      )

      val edge = standTarget.edgeBlock
      OverlayRenderEngine.addLine(
        level,
        stand.x + 0.5,
        stand.y + 0.18,
        stand.z + 0.5,
        edge.x + 0.5,
        edge.y + 0.5,
        edge.z + 0.5,
        if (selected) selectedStandOutline else standOutline,
        lineWidth = if (selected) 2.1f else 1.2f,
        durationTicks = 3,
        tag = OVERLAY_TAG,
        forceRender = true
      )
    }
  }

  private fun selectTarget() {
    val targets = TunnelVeinScanner.scan(
      selectedOres = effectiveOres(),
      maxTargets = 120,
      dynamicScanRadius = scanRadius.value.toInt().coerceAtLeast(1),
      includeRecordedData = recordedData.value,
      includeDynamicScan = dynamicScan.value,
      includeRouteStore = routePoints.value,
      preferOrderedRoutes = routePoints.value,
    )

    if (targets.isEmpty()) {
      currentTarget = null
      currentStandPoints = emptyList()
      pathTarget = null
      status.value = "No selected tunnel veins found."
      return
    }

    val next = targets.minByOrNull { it.score } ?: return
    val sameVein = targets
      .filter { it.sameVeinAs(next) }
      .distinctBy { it.standPos }
      .sortedBy { it.score }

    currentTarget = next
    currentStandPoints = sameVein

    status.value = "${next.ore.displayName} vein: ${next.veinBlocks.size} blocks, ${min(sameVein.size, standPointLimit.value.toInt())} stand points."
  }

  private fun startPathTo(stand: BlockPos) {
    PathfindingModule.ensureEnabledForAutomation("tunnel miner")
    NativePathfinder.stop()
    NativePathfinder.availabilityFlagsOverride = 0
    NativePathfinder.noTunnelCenter = true
    MovementManager.setMovementLock(false)
    NativePathfinder.setTargetWithRadius(
      stand.x + 0.5,
      stand.y.toDouble(),
      stand.z + 0.5,
      arrivalRadius.value
    )
    pathTarget = stand
    ownsPathfinder = true
  }

  private fun requestStartupScrape() {
    MiningModule.requestStatsAndHotmScrape("tunnel miner")
  }

  private fun stopPath() {
    if (!ownsPathfinder) return
    NativePathfinder.stop()
    MovementManager.setMovementLock(false)
    ownsPathfinder = false
  }

  private fun effectiveOres(): Set<TunnelOreType> {
    if (automationSource != null && selectedOres.isNotEmpty()) return selectedOres

    return when (oreMode.value.coerceIn(0, oreMode.options.lastIndex)) {
      1 -> setOf(TunnelOreType.GLACITE)
      2 -> setOf(TunnelOreType.TUNGSTEN)
      3 -> setOf(TunnelOreType.UMBER)
      4 -> setOf(TunnelOreType.PERIDOT)
      5 -> setOf(TunnelOreType.AQUAMARINE)
      6 -> setOf(TunnelOreType.ONYX)
      7 -> setOf(TunnelOreType.CITRINE)
      else -> emptySet()
    }
  }

  private fun TunnelVeinScanner.TunnelTarget.sameVeinAs(other: TunnelVeinScanner.TunnelTarget): Boolean {
    if (source != other.source) return false
    if (ore != other.ore) return false
    if (veinIndex != other.veinIndex) return false
    if (routeName != other.routeName) return false
    if (routeIndex != other.routeIndex) return false
    return true
  }

  private fun oreLabel(ores: Set<TunnelOreType>): String =
    ores.ifEmpty { TunnelOreType.entries.toSet() }
      .joinToString(", ") { it.displayName }

  private fun isInGlaciteTunnels(): Boolean {
    DwarvenSidebarLocationParser.currentSidebarLocation()?.let { location ->
      if (location.equals("Glacite Tunnels", ignoreCase = true)) return true
    }

    val snapshot = HypixelManager.snapshot()
    if (
      snapshot.area.equals("Glacite Tunnels", ignoreCase = true) ||
      snapshot.map.equals("Glacite Tunnels", ignoreCase = true) ||
      snapshot.placeName.equals("Glacite Tunnels", ignoreCase = true)
    ) {
      return true
    }

    return mc.player?.blockY?.let { it > 187 } == true
  }
}
