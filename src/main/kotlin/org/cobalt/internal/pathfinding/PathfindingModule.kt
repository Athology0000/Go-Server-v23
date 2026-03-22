package org.cobalt.internal.pathfinding

import java.util.Locale
import net.minecraft.client.Minecraft
import org.cobalt.api.hud.HudAnchor
import org.cobalt.api.hud.hudElement
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.MouseEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.event.impl.render.WorldRenderEvent
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.Vec3
import org.cobalt.internal.pathfinding.OverlayRenderEngine
import org.cobalt.internal.pathfinding.PathSplineRenderer
import org.cobalt.internal.pathfinding.PatrolWaypointStore
import org.cobalt.internal.pathfinding.RouteWaypoint
import org.cobalt.internal.pathfinding.KillWaypoint
import org.cobalt.api.pathfinder.jni.MovementProfile
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.ActionSetting
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.ModeSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.pathfinder.jni.ActionType
import org.cobalt.api.pathfinder.jni.NativePathfinder
import org.cobalt.api.pathfinder.jni.PathStatus
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules
import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.player.MovementManager
import org.cobalt.api.util.ui.NVGRenderer
import kotlin.math.sqrt

object PathfindingModule : Module("Pathfinding") {

  private val mc: Minecraft = Minecraft.getInstance()
  private var moduleOwnsPath = false
  private var cachedSpline: PathSplineRenderer.SplineResult? = null
  private var lastNodesRef: List<Vec3>? = null

  private enum class PatrolState { IDLE, NAVIGATING, AT_KILL }

  private var patrolState: PatrolState = PatrolState.IDLE
  private var currentKillWp: KillWaypoint? = null
  private var dwellTicksRemaining: Int = 0
  // Sub-route waypoints for the current kill navigation leg — visited one at a time.
  private var subRouteWaypoints: List<Triple<Double, Double, Double>> = emptyList()
  private var subRouteIndex: Int = 0

  private val COLOR_PATH_NORMAL = OverlayRenderEngine.Color(0, 200, 255, 220)   // cyan
  private val COLOR_PATH_AOTV   = OverlayRenderEngine.Color(255, 160, 0, 220)   // orange
  private val COLOR_HEAD_RAY    = OverlayRenderEngine.Color(255, 255, 255, 200)  // white

  val enabled = CheckboxSetting(
    "Enabled",
    "Enable pathfinding target selection and commands.",
    false
  )

  private val info = InfoSetting(
    "Target",
    "Use /cobalt setpos or /cobalt setposhere to set the target.",
    InfoType.INFO
  )

  val targetX = TextSetting("Target X", "Target X coordinate.", "0")
  val targetY = TextSetting("Target Y", "Target Y coordinate.", "0")
  val targetZ = TextSetting("Target Z", "Target Z coordinate.", "0")
  val targetBlock = TextSetting("Target Block", "Filled from right-click (informational).", "")

  val cacheHudEnabled = CheckboxSetting(
    "Cache HUD",
    "Show cached chunk map HUD.",
    false
  )

  val cacheHudRadius = SliderSetting(
    "Cache Radius",
    "Chunk radius shown in the cache HUD.",
    4.0,
    1.0,
    12.0
  )

  val cacheHudCellSize = SliderSetting(
    "Cache Cell Size",
    "Cell size for the cache HUD (pixels).",
    8.0,
    4.0,
    16.0
  )

  val cacheHudShowGrid = CheckboxSetting(
    "Cache Grid",
    "Show grid lines on the cache HUD.",
    true
  )

  private val debugFileLogging = CheckboxSetting(
    "Debug File Logs",
    "Write path/rotation debug logs to file.",
    false
  )

  private val aotvSlot = ModeSetting(
    "AOTV Slot",
    "Hotbar slot (1-9) holding your AOTV item.",
    0,
    arrayOf("1","2","3","4","5","6","7","8","9")
  )

  private val startAction = ActionSetting(
    "Start",
    "Start pathfinding to the target coordinates.",
    "Start"
  ) {
    startFromSettings()
  }

  private val stopAction = ActionSetting(
    "Stop",
    "Stop the current path.",
    "Stop"
  ) {
    stopPath()
  }

  private val patrolModeEnabled = CheckboxSetting(
    "Patrol Mode",
    "Randomly patrol between kill waypoints using the recorded route.",
    false
  )

  private val recordRouteAction = ActionSetting(
    "Record Route Point",
    "Appends your current position to the patrol route.",
    "Record"
  ) {
    val player = mc.player ?: return@ActionSetting
    PatrolWaypointStore.routeWaypoints.add(RouteWaypoint(player.x, player.y, player.z))
    PatrolWaypointStore.save()
    ChatUtils.sendMessage("Route point recorded (${PatrolWaypointStore.routeWaypoints.size} total).")
  }

  private val clearRouteAction = ActionSetting(
    "Clear Route",
    "Removes all recorded route waypoints.",
    "Clear"
  ) {
    PatrolWaypointStore.routeWaypoints.clear()
    PatrolWaypointStore.save()
    ChatUtils.sendMessage("Route cleared.")
  }

  private val recordingKillPoints = CheckboxSetting(
    "Record Kill Points",
    "Right-click a block to record a kill waypoint while this is enabled.",
    false
  )

  private val clearKillAction = ActionSetting(
    "Clear Kill Points",
    "Removes all recorded kill waypoints.",
    "Clear"
  ) {
    PatrolWaypointStore.killWaypoints.clear()
    PatrolWaypointStore.save()
    ChatUtils.sendMessage("Kill waypoints cleared.")
  }

  private val killDwellTicks = SliderSetting(
    "Kill Dwell Ticks",
    "Ticks to wait at a kill spot before moving to the next.",
    20.0, 0.0, 100.0
  )

  private val routeCountInfo = InfoSetting(
    "Route Points",
    "Number of recorded route waypoints.",
    InfoType.INFO
  )

  private val killCountInfo = InfoSetting(
    "Kill Points",
    "Number of recorded kill waypoints.",
    InfoType.INFO
  )

  private val startPatrolAction = ActionSetting(
    "Start Patrol",
    "Start patrolling between kill waypoints.",
    "Start Patrol"
  ) {
    startPatrol()
  }

  private val stopPatrolAction = ActionSetting(
    "Stop Patrol",
    "Stop the patrol loop.",
    "Stop Patrol"
  ) {
    stopPatrol()
  }

  val cacheHud = hudElement(
    "path-cache-hud",
    "Cache Map",
    "Shows cached chunks around you."
  ) {
    anchor = HudAnchor.TOP_RIGHT
    offsetX = -12f
    offsetY = 12f

    width {
      val radius = cacheHudRadius.value.toInt().coerceAtLeast(1)
      val cell = cacheHudCellSize.value.toFloat()
      (radius * 2 + 1) * cell + 8f
    }
    height {
      val radius = cacheHudRadius.value.toInt().coerceAtLeast(1)
      val cell = cacheHudCellSize.value.toFloat()
      (radius * 2 + 1) * cell + 8f
    }

    render { screenX, screenY, _ ->
      if (!cacheHudEnabled.value) return@render
      val player = mc.player ?: return@render
      val level = mc.level ?: return@render

      val radius = cacheHudRadius.value.toInt().coerceAtLeast(1)
      val cell = cacheHudCellSize.value.toFloat()
      val size = radius * 2 + 1
      val mapW = size * cell
      val mapH = size * cell

      NVGRenderer.rect(screenX, screenY, mapW + 8f, mapH + 8f, ThemeManager.currentTheme.panel, 6f)

      val originX = screenX + 4f
      val originY = screenY + 4f
      val centerChunkX = player.blockX shr 4
      val centerChunkZ = player.blockZ shr 4

      for (dz in -radius..radius) {
        for (dx in -radius..radius) {
          val chunkX = centerChunkX + dx
          val chunkZ = centerChunkZ + dz
          val cached = MinecraftPathingRules.isChunkCached(level, chunkX, chunkZ)
          val color =
            when {
              dx == 0 && dz == 0 -> ThemeManager.currentTheme.accentSecondary
              cached -> ThemeManager.currentTheme.accent
              else -> ThemeManager.currentTheme.overlay
            }
          val x = originX + (dx + radius) * cell
          val y = originY + (dz + radius) * cell
          NVGRenderer.rect(x, y, cell - 1f, cell - 1f, color)
        }
      }

      if (cacheHudShowGrid.value) {
        NVGRenderer.hollowRect(
          originX - 0.5f,
          originY - 0.5f,
          mapW + 1f,
          mapH + 1f,
          1f,
          ThemeManager.currentTheme.moduleDivider,
          4f
        )
      }
    }
  }

  init {
    addSetting(
      enabled,
      info,
      targetX,
      targetY,
      targetZ,
      targetBlock,
      cacheHudEnabled,
      cacheHudRadius,
      cacheHudCellSize,
      cacheHudShowGrid,
      debugFileLogging,
      aotvSlot,
      startAction,
      stopAction,
      patrolModeEnabled,
      recordRouteAction,
      clearRouteAction,
      recordingKillPoints,
      clearKillAction,
      killDwellTicks,
      routeCountInfo,
      killCountInfo,
      startPatrolAction,
      stopPatrolAction,
    )

    EventBus.register(this)
  }

  @SubscribeEvent
  fun onRightClick(event: MouseEvent.RightClick) {
    if (!recordingKillPoints.value) return
    val mc = Minecraft.getInstance()
    val hit = mc.hitResult ?: return
    if (hit !is net.minecraft.world.phys.BlockHitResult) return
    val pos = hit.blockPos
    val wp = KillWaypoint(pos.x + 0.5, pos.y + 1.0, pos.z + 0.5)
    PatrolWaypointStore.killWaypoints.add(wp)
    PatrolWaypointStore.save()
    ChatUtils.sendMessage("Kill waypoint recorded (${PatrolWaypointStore.killWaypoints.size} total).")
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    org.cobalt.internal.pathfinding.DebugLog.debugFileEnabled = debugFileLogging.value

    // Update live info settings
    routeCountInfo.value = "${PatrolWaypointStore.routeWaypoints.size} points"
    killCountInfo.value  = "${PatrolWaypointStore.killWaypoints.size} points"

    // Patrol state machine — runs independently of moduleOwnsPath
    if (patrolModeEnabled.value && patrolState != PatrolState.IDLE) {
      when (patrolState) {
        PatrolState.NAVIGATING -> {
          val nativeStatus = NativePathfinder.status
          if (nativeStatus == PathStatus.ARRIVED || nativeStatus == PathStatus.FAILED) {
            subRouteIndex++
            if (subRouteIndex < subRouteWaypoints.size) {
              // More intermediate waypoints to visit — navigate to next one.
              val (wx, wy, wz) = subRouteWaypoints[subRouteIndex]
              NativePathfinder.setTarget(wx, wy, wz)
            } else {
              // All sub-route waypoints reached — transition to kill dwell.
              dwellTicksRemaining = killDwellTicks.value.toInt()
              patrolState = PatrolState.AT_KILL
            }
          }
        }
        PatrolState.AT_KILL -> {
          if (dwellTicksRemaining-- <= 0) {
            val kills = PatrolWaypointStore.killWaypoints
            val next = kills.filter { it != currentKillWp }
            if (next.isEmpty()) {
              stopPatrol()
              ChatUtils.sendMessage("No other kill waypoints available.")
            } else {
              navigateToKill(next.random())
            }
          }
        }
        PatrolState.IDLE -> { /* nothing */ }
      }
    }

    if (!enabled.value || !moduleOwnsPath) return

    val cmd = NativePathfinder.tick()
    if (cmd != null) {
      cmd.applyToPlayer()
      if (cmd.activeAction == ActionType.AOTV) {
        val player = mc.player
        if (player != null) {
          val prevSlot = player.inventory.selectedSlot
          player.inventory.selectedSlot = aotvSlot.value
          mc.gameMode?.useItem(player, InteractionHand.MAIN_HAND)
          player.inventory.selectedSlot = prevSlot
        }
      }
    } else {
      val s = NativePathfinder.status
      if (s == PathStatus.IDLE || s == PathStatus.ARRIVED || s == PathStatus.FAILED) {
        moduleOwnsPath = false
        MovementManager.setMovementLock(false)
      }
    }
  }

  @SubscribeEvent
  fun onRender(event: WorldRenderEvent.Last) {
    if (!enabled.value) return
    val player = mc.player ?: return
    val level  = mc.level  ?: return

    // Head-direction ray from eye position along crosshair
    OverlayRenderEngine.clearTag("head-ray")
    val eye  = player.getEyePosition()
    val look = player.getLookAngle()
    OverlayRenderEngine.addLine(
      level,
      eye.x, eye.y, eye.z,
      eye.x + look.x * 5.0, eye.y + look.y * 5.0, eye.z + look.z * 5.0,
      COLOR_HEAD_RAY, 1.5f, durationTicks = 1, tag = "head-ray"
    )

    // Spline path rendering — rebuild only when path nodes change
    val nodes = NativePathfinder.cachedPathNodes
    if (nodes !== lastNodesRef) {
      lastNodesRef = nodes
      cachedSpline = if (nodes.size >= 2) PathSplineRenderer.buildSpline(nodes) else null
      OverlayRenderEngine.clearTag("path-spline")
    }

    val spline = cachedSpline ?: return
    val pts  = spline.points
    val isAv = spline.isAotv
    for (i in 0 until pts.size - 1) {
      val a = pts[i]; val b = pts[i + 1]
      val color = if (isAv[i]) COLOR_PATH_AOTV else COLOR_PATH_NORMAL
      OverlayRenderEngine.addLine(
        level,
        a.x, a.y + 0.05, a.z,
        b.x, b.y + 0.05, b.z,
        color, 2.0f, durationTicks = 3, tag = "path-spline"
      )
    }
  }

  val isPatrolActive: Boolean get() = patrolState != PatrolState.IDLE

  fun ensureEnabledForAutomation(source: String) {
    if (enabled.value) return
    enabled.value = true
    ChatUtils.sendMessage("Pathfinding auto-enabled for $source.")
  }

  fun startFromSettings() {
    if (!enabled.value) {
      ensureEnabledForAutomation("pathfinding")
    }

    val x = parseCoordinate(targetX.value) ?: return invalidTarget("X", targetX.value)
    val y = parseCoordinate(targetY.value) ?: return invalidTarget("Y", targetY.value)
    val z = parseCoordinate(targetZ.value) ?: return invalidTarget("Z", targetZ.value)

    NativePathfinder.setTarget(x, y, z)
    moduleOwnsPath = true
  }

  fun setTargetOnly(x: Double, y: Double, z: Double) {
    setTarget(x, y, z, null)
    ChatUtils.sendMessage("Target set to $x, $y, $z.")
  }

  fun startTo(x: Double, y: Double, z: Double) {
    setTarget(x, y, z, null)
    if (!enabled.value) {
      ensureEnabledForAutomation("pathfinding")
    }
    NativePathfinder.setTarget(x, y, z)
    moduleOwnsPath = true
  }

  fun stopPath() {
    NativePathfinder.stop()
    moduleOwnsPath = false
    MovementManager.setMovementLock(false)
    cachedSpline = null
    lastNodesRef = null
    OverlayRenderEngine.clearTag("path-spline")
  }

  private fun buildSubRoute(
    fromX: Double, fromY: Double, fromZ: Double,
    killWp: KillWaypoint
  ): DoubleArray {
    val route = PatrolWaypointStore.routeWaypoints
    if (route.isEmpty()) {
      // No route — navigate directly (player position + kill target)
      return doubleArrayOf(fromX, fromY, fromZ, killWp.x, killWp.y, killWp.z)
    }

    fun dist3(wp: RouteWaypoint, x: Double, y: Double, z: Double): Double {
      val dx = wp.x - x; val dy = wp.y - y; val dz = wp.z - z
      return sqrt(dx*dx + dy*dy + dz*dz)
    }

    val j = route.indices.minByOrNull { dist3(route[it], fromX, fromY, fromZ) } ?: 0
    val i = route.indices.minByOrNull { dist3(route[it], killWp.x, killWp.y, killWp.z) } ?: 0

    val slice: List<RouteWaypoint> = if (j == i) {
      emptyList()
    } else {
      val fwd = if (j <= i) route.subList(j, i + 1) else emptyList()
      val bwd = if (i <= j) route.subList(i, j + 1).reversed() else emptyList()
      when {
        fwd.isEmpty() -> bwd
        bwd.isEmpty() -> fwd
        else          -> if (fwd.size <= bwd.size) fwd else bwd
      }
    }

    // Build flat double[] for NativePathfinder.setRoute: [x0,y0,z0, x1,y1,z1, ...]
    val points = mutableListOf<Double>()
    // Start at player position
    points += listOf(fromX, fromY, fromZ)
    // Route slice
    slice.forEach { wp -> points += listOf(wp.x, wp.y, wp.z) }
    // Kill waypoint as final destination
    points += listOf(killWp.x, killWp.y, killWp.z)

    return points.toDoubleArray()
  }

  fun startPatrol() {
    if (patrolState != PatrolState.IDLE) stopPatrol()
    if (PatrolWaypointStore.killWaypoints.size < 2) {
      ChatUtils.sendMessage("Need at least 2 kill waypoints to patrol. Use 'Record Kill Points'.")
      return
    }
    if (!enabled.value) ensureEnabledForAutomation("patrol")
    val first = PatrolWaypointStore.killWaypoints.random()
    navigateToKill(first)
  }

  private fun navigateToKill(target: KillWaypoint) {
    val player = mc.player ?: run {
      patrolState = PatrolState.IDLE
      return
    }
    currentKillWp = target
    // Build the sub-route as individual waypoints (skip index 0 which is the player start pos).
    val flat = buildSubRoute(player.x, player.y, player.z, target)
    val wps = mutableListOf<Triple<Double, Double, Double>>()
    var i = 3  // skip first triple (player position)
    while (i + 2 < flat.size) {
      wps.add(Triple(flat[i], flat[i + 1], flat[i + 2]))
      i += 3
    }
    if (i + 2 == flat.size) wps.add(Triple(flat[i], flat[i + 1], flat[i + 2]))
    subRouteWaypoints = wps
    subRouteIndex = 0
    if (wps.isEmpty()) {
      // Degenerate case — already at kill point.
      dwellTicksRemaining = killDwellTicks.value.toInt()
      patrolState = PatrolState.AT_KILL
      return
    }
    val (wx, wy, wz) = wps[0]
    NativePathfinder.setTarget(wx, wy, wz)
    moduleOwnsPath = true
    patrolState = PatrolState.NAVIGATING
    ChatUtils.sendMessage("Patrolling to kill spot (${PatrolWaypointStore.killWaypoints.indexOf(target) + 1}/${PatrolWaypointStore.killWaypoints.size}).")
  }

  fun stopPatrol() {
    patrolState = PatrolState.IDLE
    stopPath()
  }

  private fun nativeActive(): Boolean {
    val s = NativePathfinder.status
    return s != PathStatus.IDLE &&
           s != PathStatus.ARRIVED &&
           s != PathStatus.FAILED
  }

  fun setTargetAtPlayer() {
    val player = mc.player ?: return
    setTarget(player.x, player.y, player.z, "player")
    ChatUtils.sendMessage("Target set to your position.")
  }

  private fun setTarget(x: Double, y: Double, z: Double, blockName: String?) {
    targetX.value = formatCoord(x)
    targetY.value = formatCoord(y)
    targetZ.value = formatCoord(z)
    if (blockName != null) {
      targetBlock.value = blockName
    }
  }

  private fun formatCoord(value: Double): String {
    val intVal = value.toInt()
    return if (value == intVal.toDouble()) {
      intVal.toString()
    } else {
      String.format(Locale.US, "%.3f", value)
    }
  }

  private fun parseCoordinate(value: String): Double? {
    return value.trim().toDoubleOrNull()
  }

  private fun invalidTarget(axis: String, value: String) {
    ChatUtils.sendMessage("Invalid $axis coordinate: \"$value\"")
  }
}
