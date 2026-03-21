package org.cobalt.internal.mining

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.awt.Color
import java.io.File
import kotlin.math.abs
import kotlin.math.max
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.AABB
import net.minecraft.core.Direction
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.MouseEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.event.impl.render.WorldRenderEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.ActionSetting
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.ModeSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.rotation.strategy.BezierTrackingRotationStrategy
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.InventoryUtils
import org.cobalt.api.util.render.Render3D
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules
import org.cobalt.api.pathfinder.jni.MovementProfile
import org.cobalt.api.pathfinder.jni.NativePathfinder
import org.cobalt.api.pathfinder.jni.PathStatus
import org.cobalt.api.util.player.MovementManager
import org.cobalt.internal.etherwarp.EtherwarpLogic
import org.cobalt.internal.pathfinding.PathfindingModule
import org.cobalt.internal.rotation.RotationsModule

object RoutesModule : Module("Routes") {

  enum class RoutePointType(val id: String) {
    NORMAL("normal"),
    WARP("warp"),
    MINE("mine");

    companion object {
      fun fromId(id: String?): RoutePointType {
        return when (id?.lowercase()) {
          "warp" -> WARP
          "mine" -> MINE
          else -> NORMAL
        }
      }
    }
  }

  data class RoutePoint(val pos: BlockPos, val type: RoutePointType, val mineEnd: BlockPos? = null)

  private enum class RouteAction {
    NONE,
    WALK,
    WARP,
    MINE
  }

  private val mc: Minecraft = Minecraft.getInstance()
  private val gson = GsonBuilder().setPrettyPrinting().create()
  private val routesDirectory = File(mc.gameDirectory, "config/cobalt/routes")
  private val legacyRoutesFile = File(mc.gameDirectory, "config/cobalt/routes.json")

  private val routePoints = mutableListOf<RoutePoint>()
  private var routeIndex = 0
  private var routeRunning = false
  private var awaitingArrival = false
  private var walkCompletePointOnArrival = true
  private var walkRetryCount = 0
  private var lastTarget: BlockPos? = null
  private var lastResolvedTarget: BlockPos? = null
  private var action = RouteAction.NONE
  private var activePoint: RoutePoint? = null
  private var pendingClickPos: BlockPos? = null
  private var pendingMineStart: BlockPos? = null
  private var awaitingMineSecond = false

  private val rotationStrategy = BezierTrackingRotationStrategy(
    yawStepSampler = { RotationsModule.sample(RotationsModule.routeYawStep.value).toFloat() },
    pitchStepSampler = { RotationsModule.sample(RotationsModule.routePitchStep.value).toFloat() },
    curveInProvider = { RotationsModule.bezierCurveIn.value.toFloat() },
    curveOutProvider = { RotationsModule.bezierCurveOut.value.toFloat() },
    minScaleProvider = { RotationsModule.bezierMinScale.value.toFloat() },
    snapThresholdProvider = { RotationsModule.bezierSnapThreshold.value.toFloat() },
  )

  private var warpStage = 0
  private var warpTarget: BlockPos? = null
  private var warpStageElapsedMs = 0.0
  private var warpStageLastNs = 0L
  private var warpLookLastNs = 0L
  private var warpCooldownUntil = 0L
  private var warpRestoreSlot = -1
  private var warpCompletePointOnArrival = true
  private var warpResumeAction = RouteAction.NONE
  private var lastSuccessfulWarpTarget: BlockPos? = null
  private var lastSuccessfulWarpTick = -1L
  private var screenPauseNoticeTick = 0L

  private var mineBlocks: MutableSet<BlockPos> = LinkedHashSet()
  private var mineOrderedBlocks: List<BlockPos> = emptyList()
  private var mineBlockId: String? = null
  private var mineTarget: BlockPos? = null
  private var minePathTarget: BlockPos? = null
  private var minePointStart: BlockPos? = null
  private var minePointEnd: BlockPos? = null
  private var mineTravelWaypoints: List<BlockPos> = emptyList()
  private var chainedMineEndIndex = -1
  private var lastPathStartTick = 0L
  private var miningActive = false
  private var mineDrillWarnTick = 0L

  // Auto-detected mining loop: two route points with the same block position.
  // loopStartIndex = first occurrence, loopEndIndex = second occurrence.
  // All points between them are the mining area. Points before loopStartIndex
  // are the one-time travel route.
  private var loopStartIndex = -1
  private var loopEndIndex = -1

  val enabled = CheckboxSetting(
    "Enabled",
    "Enable route tools.",
    false
  )

  private val info = InfoSetting(
    "Route Builder",
    "Add points, save/load, and run routes with the pathfinder.",
    InfoType.INFO
  )

  val routeName = TextSetting(
    "Route Name",
    "Name used for save/load.",
    "default"
  )

  val coordX = TextSetting(
    "Add X",
    "X coordinate to add as a route point.",
    ""
  )
  val coordY = TextSetting(
    "Add Y",
    "Y coordinate to add as a route point.",
    ""
  )
  val coordZ = TextSetting(
    "Add Z",
    "Z coordinate to add as a route point.",
    ""
  )

  val pointsText = TextSetting(
    "Points",
    "Number of points in the current route.",
    "0"
  )

  val statusText = TextSetting(
    "Status",
    "Current route status.",
    "Idle"
  )

  val renderRoute = CheckboxSetting(
    "Render Route",
    "Render the route in-world.",
    true
  )

  val recordOnRightClick = CheckboxSetting(
    "Record on Right Click",
    "Add a route point when you right-click a block.",
    false
  )

  private val loopRoute = CheckboxSetting(
    "Loop Route",
    "Restart from the first point when the route reaches the end.",
    true
  )

  private val startFromNearest = CheckboxSetting(
    "Start From Nearest",
    "When starting a route, begin at the checkpoint closest to your current position.",
    false
  )

  private val pointType = ModeSetting(
    "Point Type",
    "Type used when adding points.",
    0,
    arrayOf("Normal", "Warp", "Mine")
  )

  private val veinOccupancyRadius = SliderSetting(
    "Vein Occupancy Radius",
    "Radius around a vein considered occupied by other players.",
    6.0,
    1.0,
    16.0
  )

  private val openPickerAction = ActionSetting(
    "Point Picker",
    "Open a picker to choose the next point type.",
    "Open"
  ) {
    org.cobalt.internal.ui.hud.RoutePointPopup.open()
  }

  private val addPointAction = ActionSetting(
    "Add Point",
    "Add a point at your position.",
    "Add"
  ) {
    addPointFromPlayer()
  }

  private val addCoordPointAction = ActionSetting(
    "Add Coord Point",
    "Add a point using the Add X/Y/Z fields.",
    "Add"
  ) {
    addPointFromCoords()
  }

  private val removePointAction = ActionSetting(
    "Remove Last",
    "Remove the last route point.",
    "Remove"
  ) {
    if (routePoints.isNotEmpty()) {
      routePoints.removeAt(routePoints.lastIndex)
      updateStatus()
    }
  }

  private val clearRouteAction = ActionSetting(
    "Clear Route",
    "Remove all route points.",
    "Clear"
  ) {
    routePoints.clear()
    routeIndex = 0
    routeRunning = false
    resetRuntimeState()
    nativeStop("Route cleared.")
    updateStatus()
  }

  private val saveRouteAction = ActionSetting(
    "Save Route",
    "Save route points under the current name.",
    "Save"
  ) {
    saveRoute()
  }

  private val loadRouteAction = ActionSetting(
    "Load Route",
    "Load route points by name.",
    "Load"
  ) {
    loadRoute()
  }

  private val startRouteAction = ActionSetting(
    "Start Route",
    "Run the current route with the pathfinder.",
    "Start"
  ) {
    startRoute()
  }

  private val startClosestVeinAction = ActionSetting(
    "Path Closest Vein",
    "Pathfind to the nearest unoccupied mine point.",
    "Start"
  ) {
    pathToClosestVein()
  }

  private val stopRouteAction = ActionSetting(
    "Stop Route",
    "Stop running the route.",
    "Stop"
  ) {
    stopRoute("Stopped.")
  }

  init {
    addSetting(
      enabled,
      info,
      routeName,
      coordX,
      coordY,
      coordZ,
      pointsText,
      statusText,
      renderRoute,
      recordOnRightClick,
      loopRoute,
      startFromNearest,
      pointType,
      veinOccupancyRadius,
      openPickerAction,
      addPointAction,
      addCoordPointAction,
      removePointAction,
      clearRouteAction,
      saveRouteAction,
      loadRouteAction,
      startRouteAction,
      startClosestVeinAction,
      stopRouteAction,
    )

    EventBus.register(this)
    EventBus.register(org.cobalt.internal.ui.hud.RoutePointPopup)
    org.cobalt.internal.helper.WalkbackBridge.startWalkback = ::loadAndStartWalkback
    org.cobalt.internal.helper.WalkbackBridge.isRunning = { routeRunning }
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (!enabled.value) {
      if (routeRunning) {
        stopRoute("Routes disabled.")
      }
      return
    }

    updateStatus()

    if (!routeRunning) return

    val player = mc.player ?: return
    val level = mc.level ?: return
    if (routePoints.isEmpty()) {
      stopRoute("Route has no points.")
      return
    }

    if (mc.screen != null) {
      mc.options.keyUse?.setDown(false)
      mc.options.keyShift?.setDown(false)
      stopMiningKeys()
      RotationExecutor.stopRotating()
      if (level.gameTime >= screenPauseNoticeTick) {
        ChatUtils.sendMessage("Route paused while a menu is open.")
        screenPauseNoticeTick = level.gameTime + 40L
      }
      return
    }
    screenPauseNoticeTick = 0L

    if (warpTarget != null || warpStage > 0) {
      return
    }

    if (action == RouteAction.MINE) {
      handleMine(player, level)
      return
    }

    if (action == RouteAction.WALK) {
      if (nativeActive()) {
        NativePathfinder.tick()?.applyToPlayer()
        return
      }
      if (awaitingArrival) {
        val target = lastTarget
        if (target != null && hasArrived(player, target)) {
          if (walkCompletePointOnArrival) {
            completePoint()
          } else {
            action = RouteAction.NONE
            awaitingArrival = false
            lastTarget = null
            lastResolvedTarget = null
            walkCompletePointOnArrival = true
          }
        } else {
          if (walkCompletePointOnArrival) {
            if (attemptRouteVisibleEtherwarpRecovery(level, player)) {
              awaitingArrival = false
              lastTarget = null
              lastResolvedTarget = null
              walkCompletePointOnArrival = true
              walkRetryCount = 0
              return
            }
            // Before giving up, retry with an A* repath from current position
            val retryDest = lastResolvedTarget ?: lastTarget?.let { resolveApproxTarget(it) }
            if (retryDest != null && walkRetryCount < MAX_WALK_RETRIES) {
              NativePathfinder.setTarget(retryDest.x + 0.5, retryDest.y.toDouble(), retryDest.z + 0.5)
              walkRetryCount++
              lastResolvedTarget = retryDest
              return
            }
            walkRetryCount = 0
            stopRoute("Route failed: could not reach point ${routeIndex + 1}.")
          } else {
            action = RouteAction.NONE
            awaitingArrival = false
            lastTarget = null
            lastResolvedTarget = null
            walkCompletePointOnArrival = true
          }
        }
      }
    }

    if (action == RouteAction.NONE) {
      startNextPoint(player, level)
    }
  }

  @SubscribeEvent
  fun onFrame(@Suppress("UNUSED_PARAMETER") event: WorldRenderEvent.Last) {
    if (!enabled.value || !routeRunning) return
    if (mc.screen != null) return
    val player = mc.player ?: return
    val level = mc.level ?: return
    if (warpTarget != null || warpStage > 0) {
      handleWarp(player, level)
    }
  }

  @SubscribeEvent
  fun onRightClick(@Suppress("UNUSED_PARAMETER") event: MouseEvent.RightClick) {
    if (!enabled.value || !recordOnRightClick.value) return
    val hit = mc.hitResult
    if (hit is BlockHitResult && hit.type == HitResult.Type.BLOCK) {
      val clicked = hit.blockPos
      if (awaitingMineSecond) {
        val start = pendingMineStart
        if (start != null) {
          addRoutePoint(RoutePoint(start, RoutePointType.MINE, clicked))
          ChatUtils.sendMessage("Mine point added (start -> end).")
        }
        pendingMineStart = null
        awaitingMineSecond = false
        return
      }
      pendingClickPos = clicked
      org.cobalt.internal.ui.hud.RoutePointPopup.open()
    }
  }

  @SubscribeEvent
  fun onRender(event: WorldRenderEvent.Last) {
    if (!enabled.value || !renderRoute.value) return
    if (routePoints.isEmpty()) return
    val level = mc.level ?: return
    val segments = max(1, routePoints.size - 1)
    if (routePoints.size >= 2) {
      for (i in 0 until routePoints.size - 1) {
        val a = routePoints[i].pos
        val b = routePoints[i + 1].pos
        val color = gradientColor(i / (segments - 1.0))
        val start = Vec3(a.x + 0.5, a.y + 0.2, a.z + 0.5)
        val end = Vec3(b.x + 0.5, b.y + 0.2, b.z + 0.5)
        Render3D.drawLine(event.context, start, end, color, true, 2.0f)
      }
    }
    for (i in routePoints.indices) {
      val point = routePoints[i]
      val p = point.pos
      val color = pointTypeColor(point.type, i / (segments - 1.0))
      if (point.type == RoutePointType.MINE) {
        val end = point.mineEnd
        if (end != null) {
          val startVec = Vec3(p.x + 0.5, p.y + 0.4, p.z + 0.5)
          val endVec = Vec3(end.x + 0.5, end.y + 0.4, end.z + 0.5)
          Render3D.drawLine(event.context, startVec, endVec, color, true, 2.5f)
          val endBox = AABB(
            end.x.toDouble(),
            end.y.toDouble(),
            end.z.toDouble(),
            end.x + 1.0,
            end.y + 1.0,
            end.z + 1.0
          )
          Render3D.drawBox(event.context, endBox, color, true)
          highlightVein(level, p, end, color, event.context)
        }
      }
      val box = AABB(
        p.x.toDouble(),
        p.y.toDouble(),
        p.z.toDouble(),
        p.x + 1.0,
        p.y + 1.0,
        p.z + 1.0
      )
      Render3D.drawBox(event.context, box, color, true)
    }
  }

  private fun addPointFromPlayer() {
    val player = mc.player ?: return
    addRoutePoint(RoutePoint(player.blockPosition(), currentPointType()))
  }

  private fun addPointFromCoords() {
    val x = coordX.value.trim().toIntOrNull()
    val y = coordY.value.trim().toIntOrNull()
    val z = coordZ.value.trim().toIntOrNull()
    if (x == null || y == null || z == null) {
      ChatUtils.sendMessage("Invalid coordinates. Use integers for Add X/Y/Z.")
      return
    }
    addRoutePoint(RoutePoint(BlockPos(x, y, z), currentPointType()))
  }

  private fun addRoutePoint(point: RoutePoint) {
    routePoints.add(point)
    updateStatus()

    if (point.type == RoutePointType.NORMAL) {
      PathfindingModule.startTo(
        point.pos.x.toDouble(),
        point.pos.y.toDouble(),
        point.pos.z.toDouble()
      )
    }
  }

  val isRunning: Boolean get() = routeRunning

  private fun findNearestPointIndex(): Int {
    val origin = mc.player?.blockPosition() ?: return 0
    return routePoints.indices.minByOrNull { i ->
      routePoints[i].pos.distSqr(origin).toDouble()
    } ?: 0
  }

  private fun startRoute() {
    if (routePoints.isEmpty()) {
      ChatUtils.sendMessage("Route has no points.")
      return
    }
    val hasWarpPoints = routePoints.any { it.type == RoutePointType.WARP }
    if (hasWarpPoints && EtherwarpLogic.findEtherwarpHotbarSlot() !in 0..8) {
      ChatUtils.sendMessage("Route has warp points but no EtherWarp item found in hotbar. Aborting.")
      return
    }
    if (!enabled.value) {
      enabled.value = true
    }
    if (mc.screen != null) {
      mc.setScreen(null)
    }
    PathfindingModule.ensureEnabledForAutomation("routes")
    routeIndex = if (startFromNearest.value) findNearestPointIndex() else 0
    routeRunning = true
    resetRuntimeState()
    detectMiningLoop()
    updateStatus()
    val startMsg = if (startFromNearest.value) "from checkpoint ${routeIndex + 1}" else ""
    if (loopStartIndex >= 0) {
      ChatUtils.sendMessage(
        "Route started${ if (startMsg.isNotEmpty()) " $startMsg" else "" }. Mining loop: " +
          "travel 0-${loopStartIndex}, loop ${loopStartIndex}-${loopEndIndex}."
      )
    } else {
      ChatUtils.sendMessage("Route started${ if (startMsg.isNotEmpty()) " $startMsg" else "" }.")
    }
  }

  /**
   * Loads route [name] from disk and starts it.
   * If [fromEndOffset] > 0, starts that many checkpoints before the last point.
   * Otherwise starts at the checkpoint nearest to the player.
   * Returns true if the route was loaded and started successfully.
   */
  fun loadAndStartWalkback(name: String, fromEndOffset: Int = 0): Boolean {
    // Don't restart if a walkback is already in progress.
    if (routeRunning) return false
    val trimmedName = name.trim()
    if (trimmedName.isEmpty() || !isValidRouteName(trimmedName)) return false
    val routeFile = routeFileForName(trimmedName)
    val loaded = when {
      routeFile.exists() -> readRouteFile(routeFile)
      else -> readLegacyRoute(trimmedName)
    } ?: return false
    if (loaded.isEmpty()) return false

    routePoints.clear()
    routePoints.addAll(loaded)
    routeIndex = if (fromEndOffset > 0) {
      (loaded.size - fromEndOffset).coerceIn(0, loaded.size - 1)
    } else {
      findNearestPointIndex()
    }
    loopRoute.value = false  // walkback runs once — don't loop
    routeRunning = true
    resetRuntimeState()
    detectMiningLoop()
    if (!enabled.value) enabled.value = true
    PathfindingModule.ensureEnabledForAutomation("routes")

    // If all remaining points are NORMAL, feed them directly to bypass A* entirely.
    // This makes the pathfinder follow the recorded path geometry rather than
    // computing its own route between waypoints.
    val remaining = routePoints.subList(routeIndex, routePoints.size)
    if (remaining.isNotEmpty() && remaining.all { it.type == RoutePointType.NORMAL }) {
      val waypoints = remaining.map { it.pos }
      // startFromBeginning=true: findNearestPointIndex() already identified the correct
      // starting waypoint via 3D distance — don't let startDirect re-search by XZ and
      // jump ahead to a later node that may be through a wall (e.g. the crypt exit).
      val flat = DoubleArray(waypoints.size * 3) { i ->
        val bp = waypoints[i / 3]
        when (i % 3) { 0 -> bp.x + 0.5; 1 -> bp.y.toDouble(); else -> bp.z + 0.5 }
      }
      NativePathfinder.setRoute(flat, loop = false, MovementProfile.DEFAULT)
      action = RouteAction.WALK
      awaitingArrival = true
      walkCompletePointOnArrival = true
      lastTarget = routePoints.last().pos
      lastResolvedTarget = lastTarget
      activePoint = null
      routeIndex = routePoints.lastIndex  // completePoint will push past end → stopRoute
      updateStatus()
      ChatUtils.sendMessage("Walkback \"$trimmedName\" started (direct, ${waypoints.size} nodes).")
      return true
    }

    updateStatus()
    ChatUtils.sendMessage("Walkback \"$trimmedName\" started at checkpoint ${routeIndex + 1}/${loaded.size}.")
    return true
  }

  private fun pathToClosestVein() {
    val player = mc.player ?: return
    val level = mc.level ?: return
    if (routePoints.isEmpty()) {
      ChatUtils.sendMessage("Route has no points.")
      return
    }

    if (routeRunning) {
      stopRoute("Switching to closest vein.")
    }

    val candidates = routePoints.filter { it.type == RoutePointType.MINE && it.mineEnd != null }
    if (candidates.isEmpty()) {
      ChatUtils.sendMessage("No mine points available.")
      return
    }

    var bestPoint: RoutePoint? = null
    var bestTarget: BlockPos? = null
    var bestDistSq = Double.POSITIVE_INFINITY
    val origin = player.blockPosition()

    for (point in candidates) {
      if (isVeinOccupied(level, point, player)) continue
      val target = resolveApproxTarget(point.pos) ?: continue
      val distSq = target.distSqr(origin).toDouble()
      if (distSq < bestDistSq) {
        bestDistSq = distSq
        bestPoint = point
        bestTarget = target
      }
    }

    if (bestPoint == null || bestTarget == null) {
      ChatUtils.sendMessage("No unoccupied mine points found.")
      return
    }

    PathfindingModule.ensureEnabledForAutomation("routes")

    NativePathfinder.setTarget(bestTarget.x + 0.5, bestTarget.y.toDouble(), bestTarget.z + 0.5)
    lastPathStartTick = level.gameTime

    ChatUtils.sendMessage(
      "Pathing to vein at ${bestPoint.pos.x} ${bestPoint.pos.y} ${bestPoint.pos.z}."
    )
  }

  private fun nativeActive(): Boolean =
    NativePathfinder.status.let { it != PathStatus.IDLE && it != PathStatus.ARRIVED && it != PathStatus.FAILED }

  private fun nativeStop(reason: String?) {
    NativePathfinder.stop()
    MovementManager.setMovementLock(false)
    if (reason != null) ChatUtils.sendMessage(reason)
  }

  private fun stopRoute(reason: String) {
    val wasRunning = routeRunning
    routeRunning = false
    resetRuntimeState()
    nativeStop(null)
    updateStatus()
    if (wasRunning && reason.isNotBlank()) {
      ChatUtils.sendMessage(reason)
    }
  }

  private fun updateStatus() {
    pointsText.value = routePoints.size.toString()
    statusText.value = if (!routeRunning) {
      "Idle"
    } else {
      if (routePoints.isEmpty()) {
        "Running 0/0"
      } else {
        val current = (routeIndex + 1).coerceAtMost(routePoints.size)
        "Running $current/${routePoints.size}"
      }
    }
  }

  private fun saveRoute() {
    val name = routeName.value.trim()
    if (name.isEmpty()) {
      ChatUtils.sendMessage("Route name is empty.")
      return
    }
    if (!isValidRouteName(name)) {
      ChatUtils.sendMessage("Route name contains invalid filename characters.")
      return
    }
    if (!routesDirectory.exists()) {
      routesDirectory.mkdirs()
    }
    val routeFile = routeFileForName(name)
    writeRouteFile(routeFile, routePoints)
    ChatUtils.sendMessage("Saved route \"$name\" (${routePoints.size} points) to ${routeFile.name}.")
  }

  private fun loadRoute() {
    val name = routeName.value.trim()
    if (name.isEmpty()) {
      ChatUtils.sendMessage("Route name is empty.")
      return
    }
    if (!isValidRouteName(name)) {
      ChatUtils.sendMessage("Route name contains invalid filename characters.")
      return
    }

    val routeFile = routeFileForName(name)
    val loaded = when {
      routeFile.exists() -> readRouteFile(routeFile) ?: run {
        ChatUtils.sendMessage("Route file \"${routeFile.name}\" is invalid.")
        return
      }
      else -> readLegacyRoute(name) ?: run {
        ChatUtils.sendMessage("Route \"$name\" not found.")
        return
      }
    }

    routePoints.clear()
    routePoints.addAll(loaded)
    routeIndex = 0
    routeRunning = false
    resetRuntimeState()
    detectMiningLoop()
    updateStatus()
    val loopMsg = if (loopStartIndex >= 0) " Loop: pts ${loopStartIndex}-${loopEndIndex}." else ""
    ChatUtils.sendMessage("Loaded route \"$name\" (${routePoints.size} points).$loopMsg")
  }

  private fun isValidRouteName(name: String): Boolean {
    if (name == "." || name == "..") return false
    if (name.endsWith(".") || name.endsWith(" ")) return false
    val invalidChars = charArrayOf('\\', '/', ':', '*', '?', '"', '<', '>', '|')
    return name.none { it in invalidChars }
  }

  private fun routeFileForName(name: String): File {
    return File(routesDirectory, "$name.json")
  }

  private fun writeRouteFile(routeFile: File, points: List<RoutePoint>) {
    val root = JsonObject()
    root.add("points", serializeRoutePoints(points))
    routeFile.writeText(gson.toJson(root))
  }

  private fun serializeRoutePoints(points: List<RoutePoint>): JsonArray {
    val pointsArray = JsonArray()
    points.forEach { point ->
      val obj = JsonObject()
      obj.addProperty("x", point.pos.x)
      obj.addProperty("y", point.pos.y)
      obj.addProperty("z", point.pos.z)
      obj.addProperty("type", point.type.id)
      point.mineEnd?.let { end ->
        obj.addProperty("mx", end.x)
        obj.addProperty("my", end.y)
        obj.addProperty("mz", end.z)
      }
      pointsArray.add(obj)
    }
    return pointsArray
  }

  private fun readRouteFile(routeFile: File): List<RoutePoint>? {
    val text = runCatching { routeFile.readText() }.getOrNull()?.trim().orEmpty()
    if (text.isEmpty()) return emptyList()
    val parsed = runCatching { JsonParser.parseString(text) }.getOrNull() ?: return null
    val pointsArray = when {
      parsed.isJsonArray -> parsed.asJsonArray
      parsed.isJsonObject -> parsed.asJsonObject.getAsJsonArray("points")
      else -> null
    } ?: return null
    return parseRoutePoints(pointsArray)
  }

  private fun readLegacyRoute(name: String): List<RoutePoint>? {
    val root = readLegacyRoutesJson()
    val routesObj = root.getAsJsonObject("routes") ?: return null
    val pointsArray = routesObj.getAsJsonArray(name) ?: return null
    return parseRoutePoints(pointsArray)
  }

  private fun parseRoutePoints(pointsArray: JsonArray): List<RoutePoint> {
    val loaded = mutableListOf<RoutePoint>()
    pointsArray.forEach { el ->
      val obj = el.asJsonObject
      val x = obj.get("x")?.asInt ?: return@forEach
      val y = obj.get("y")?.asInt ?: return@forEach
      val z = obj.get("z")?.asInt ?: return@forEach
      val type = RoutePointType.fromId(obj.get("type")?.asString)
      val mx = obj.get("mx")?.asInt
      val my = obj.get("my")?.asInt
      val mz = obj.get("mz")?.asInt
      val mineEnd = if (mx != null && my != null && mz != null) BlockPos(mx, my, mz) else null
      loaded.add(RoutePoint(BlockPos(x, y, z), type, mineEnd))
    }
    return loaded
  }

  private fun readLegacyRoutesJson(): JsonObject {
    if (!legacyRoutesFile.exists()) return JsonObject()
    val text = runCatching { legacyRoutesFile.readText() }.getOrNull()?.trim().orEmpty()
    if (text.isEmpty()) return JsonObject()
    return runCatching { JsonParser.parseString(text).asJsonObject }.getOrDefault(JsonObject())
  }

  private fun resolveApproxTarget(target: BlockPos): BlockPos? {
    val level = mc.level ?: return null
    MinecraftPathingRules.resolveTarget(level, target)?.let { return it }
    return findNearestWalkable(level, target, APPROX_SCAN_RADIUS, APPROX_SCAN_VERTICAL)
  }

  private fun findNearestWalkable(
    level: net.minecraft.world.level.Level,
    origin: BlockPos,
    radius: Int,
    vertical: Int
  ): BlockPos? {
    var best: BlockPos? = null
    var bestDistSq = Double.POSITIVE_INFINITY
    for (dy in -vertical..vertical) {
      for (dx in -radius..radius) {
        for (dz in -radius..radius) {
          val pos = origin.offset(dx, dy, dz)
          if (!MinecraftPathingRules.isWalkable(level, pos)) continue
          val distSq = pos.distSqr(origin).toDouble()
          if (distSq < bestDistSq) {
            bestDistSq = distSq
            best = pos
          }
        }
      }
    }
    return best
  }

  private fun gradientColor(t: Double): Color {
    val clamped = t.coerceIn(0.0, 1.0)
    val r = (0 + (255 - 0) * clamped).toInt()
    val g = (255 + (0 - 255) * clamped).toInt()
    val b = 255
    return Color(r, g, b, 255)
  }

  private fun pointTypeColor(type: RoutePointType, t: Double): Color {
    return when (type) {
      RoutePointType.WARP -> Color(175, 120, 255, 255)
      RoutePointType.MINE -> Color(80, 255, 140, 255)
      RoutePointType.NORMAL -> gradientColor(t)
    }
  }

  private fun highlightVein(
    level: net.minecraft.world.level.Level,
    start: BlockPos,
    end: BlockPos,
    color: Color,
    context: org.cobalt.api.event.impl.render.WorldRenderContext
  ) {
    val state = level.getBlockState(start)
    if (state.isAir) return
    val block = state.block

    val minX = minOf(start.x, end.x)
    val maxX = maxOf(start.x, end.x)
    val minY = minOf(start.y, end.y)
    val maxY = maxOf(start.y, end.y)
    val minZ = minOf(start.z, end.z)
    val maxZ = maxOf(start.z, end.z)

    val queue = ArrayDeque<BlockPos>()
    val visited = HashSet<Long>()
    queue.add(start)
    visited.add(start.asLong())

    var rendered = 0
    val maxBlocks = 256

    while (queue.isNotEmpty() && rendered < maxBlocks) {
      val pos = queue.removeFirst()
      if (pos.x !in minX..maxX || pos.y !in minY..maxY || pos.z !in minZ..maxZ) {
        continue
      }
      val curState = level.getBlockState(pos)
      if (curState.isAir || curState.block != block) {
        continue
      }

      if (isExposed(level, pos)) {
        val box = AABB(
          pos.x.toDouble(),
          pos.y.toDouble(),
          pos.z.toDouble(),
          pos.x + 1.0,
          pos.y + 1.0,
          pos.z + 1.0
        )
        Render3D.drawBox(context, box, color, true)
        rendered++
      }

      for (dir in Direction.values()) {
        val next = pos.relative(dir)
        val key = next.asLong()
        if (visited.add(key)) {
          queue.add(next)
        }
      }
    }
  }

  private fun isExposed(level: net.minecraft.world.level.Level, pos: BlockPos): Boolean {
    for (dir in Direction.values()) {
      val adj = pos.relative(dir)
      if (MinecraftPathingRules.isPassable(level, adj)) {
        return true
      }
    }
    return false
  }

  private fun currentPointType(): RoutePointType {
    return when (pointType.value) {
      1 -> RoutePointType.WARP
      2 -> RoutePointType.MINE
      else -> RoutePointType.NORMAL
    }
  }

  fun setPointType(type: RoutePointType) {
    pointType.value = when (type) {
      RoutePointType.WARP -> 1
      RoutePointType.MINE -> 2
      else -> 0
    }
  }

  fun applyPickedType(type: RoutePointType) {
    setPointType(type)
    val clicked = pendingClickPos
    if (clicked == null) {
      return
    }
    if (type == RoutePointType.MINE) {
      pendingMineStart = clicked
      awaitingMineSecond = true
      pendingClickPos = null
      ChatUtils.sendMessage("Mine point: select the end block.")
      return
    }
    addRoutePoint(RoutePoint(clicked, type))
    pendingClickPos = null
  }

  fun cancelPendingPick() {
    pendingClickPos = null
    pendingMineStart = null
    awaitingMineSecond = false
  }

  private fun isVeinOccupied(
    level: net.minecraft.world.level.Level,
    point: RoutePoint,
    player: net.minecraft.world.entity.player.Player
  ): Boolean {
    val end = point.mineEnd ?: point.pos
    val radius = veinOccupancyRadius.value
    val minX = minOf(point.pos.x, end.x).toDouble() - radius
    val minY = minOf(point.pos.y, end.y).toDouble() - radius
    val minZ = minOf(point.pos.z, end.z).toDouble() - radius
    val maxX = maxOf(point.pos.x, end.x).toDouble() + 1.0 + radius
    val maxY = maxOf(point.pos.y, end.y).toDouble() + 1.0 + radius
    val maxZ = maxOf(point.pos.z, end.z).toDouble() + 1.0 + radius
    val aabb = AABB(minX, minY, minZ, maxX, maxY, maxZ)

    for (other in level.players()) {
      if (other == player) continue
      if (other.isSpectator) continue
      if (aabb.intersects(other.boundingBox)) {
        return true
      }
    }
    return false
  }

  private fun startNextPoint(player: Player, level: net.minecraft.world.level.Level) {
    if (!advanceRouteIndexForLoop()) {
      stopRoute("Route complete.")
      return
    }

    val point = resolvePointForExecution(routeIndex)
    activePoint = point

    when (point.type) {
      RoutePointType.WARP -> {
        val warpPoint = resolveWarpPoint(level, point.pos) ?: run {
          stopRoute("Route failed: invalid warp point ${routeIndex + 1}.")
          return
        }
        if (isStandingOnWarpTarget(player, warpPoint)) {
          completePoint()
          return
        }
        if (startWarp(warpPoint)) {
          action = RouteAction.WARP
          return
        }
        if (hasArrived(player, warpPoint)) {
          completePoint()
          return
        }
        if (
          level.gameTime < warpCooldownUntil &&
          (EtherwarpLogic.holdingEtherwarpItem() || EtherwarpLogic.findEtherwarpHotbarSlot() in 0..8)
        ) {
          return
        }
        if (!attemptRouteVisibleEtherwarpRecovery(level, player)) {
          stopRoute("Route failed: no visible route etherwarp target.")
        }
        return
      }
      RoutePointType.MINE -> {
        startMine(level, point)
      }
      else -> {
        // Batch all consecutive NORMAL points (including single ones) into startDirect so
        // the pathfinder follows recorded route geometry instead of A* shortcuts through walls.
        var batchEnd = routeIndex
        while (batchEnd + 1 < routePoints.size && routePoints[batchEnd + 1].type == RoutePointType.NORMAL) {
          batchEnd++
        }
        val waypoints = routePoints.subList(routeIndex, batchEnd + 1).map { it.pos }
        val flat = DoubleArray(waypoints.size * 3) { i ->
          val bp = waypoints[i / 3]
          when (i % 3) { 0 -> bp.x + 0.5; 1 -> bp.y.toDouble(); else -> bp.z + 0.5 }
        }
        NativePathfinder.setRoute(flat, loop = false, MovementProfile.DEFAULT)
        action = RouteAction.WALK
        awaitingArrival = true
        walkCompletePointOnArrival = true
        lastTarget = waypoints.last()
        lastResolvedTarget = lastTarget
        activePoint = null
        routeIndex = batchEnd  // completePoint() → routeIndex++ → next non-NORMAL
        updateStatus()
        return
      }
    }
  }

  private fun resolvePointForExecution(pointIndex: Int): RoutePoint {
    val base = routePoints[pointIndex]

    if (chainedMineEndIndex != -1 && pointIndex >= chainedMineEndIndex) {
      chainedMineEndIndex = -1
    }

    if (chainedMineEndIndex > pointIndex) {
      val nextPos = routePoints.getOrNull(pointIndex + 1)?.pos
      if (nextPos != null) {
        return RoutePoint(base.pos, RoutePointType.MINE, nextPos)
      }
      chainedMineEndIndex = -1
      return base
    }

    if (base.type != RoutePointType.MINE) {
      return base
    }

    val end = base.mineEnd ?: return base
    val matchingEndIndex = findMatchingMineEndPointIndex(pointIndex, end)
    if (matchingEndIndex != null && matchingEndIndex > pointIndex + 1) {
      chainedMineEndIndex = matchingEndIndex
      val nextPos = routePoints.getOrNull(pointIndex + 1)?.pos
      if (nextPos != null) {
        return RoutePoint(base.pos, RoutePointType.MINE, nextPos)
      }
      chainedMineEndIndex = -1
    }

    return base
  }

  private fun startWalk(target: BlockPos, completePointOnArrival: Boolean = true) {
    PathfindingModule.ensureEnabledForAutomation("routes")
    val resolved = resolveApproxTarget(target)
    if (resolved == null) {
      stopRoute("Route failed: no walkable target near point ${routeIndex + 1}.")
      return
    }
    NativePathfinder.setTarget(resolved.x + 0.5, resolved.y.toDouble(), resolved.z + 0.5)
    mc.level?.let { level -> lastPathStartTick = level.gameTime }
    action = RouteAction.WALK
    awaitingArrival = true
    walkCompletePointOnArrival = completePointOnArrival
    lastTarget = target
    lastResolvedTarget = resolved
  }

  private fun startMine(level: net.minecraft.world.level.Level, point: RoutePoint) {
    val end = point.mineEnd ?: run {
      startWalk(point.pos)
      return
    }

    minePointStart = point.pos
    minePointEnd = end
    mineTravelWaypoints = resolveMineTravelWaypoints(routeIndex, point.pos, end)

    val vein = buildMineVein(level, mineTravelWaypoints)
    if (vein == null || vein.blocks.isEmpty()) {
      ChatUtils.sendMessage("Mine point empty; skipping.")
      completePoint()
      return
    }

    mineBlocks = vein.blocks
    mineOrderedBlocks = buildMineOrder(vein.blocks, mineTravelWaypoints)
    mineBlockId = vein.blockId
    mineTarget = null
    minePathTarget = null
    action = RouteAction.MINE
    // Hold sneak for the entire mine phase to prevent falling off vein ledges.
    mc.options.keyShift?.setDown(true)
    // Face mine1 so the player looks into the vein from the start
    val mine1Center = Vec3(point.pos.x + 0.5, point.pos.y + 0.5, point.pos.z + 0.5)
    RotationExecutor.rotateTo(AngleUtils.getRotation(mine1Center), rotationStrategy)
  }

  private fun resolveMineTravelWaypoints(
    pointIndex: Int,
    start: BlockPos,
    end: BlockPos
  ): List<BlockPos> {
    val waypoints = mutableListOf<BlockPos>()
    waypoints.add(start)

    val matchingEndIndex = findMatchingMineEndPointIndex(pointIndex, end)
    if (matchingEndIndex != null && matchingEndIndex > pointIndex + 1) {
      for (i in (pointIndex + 1) until matchingEndIndex) {
        val pos = routePoints[i].pos
        if (waypoints.last() != pos) {
          waypoints.add(pos)
        }
      }
    }

    if (waypoints.last() != end) {
      waypoints.add(end)
    }
    return waypoints
  }

  private fun findMatchingMineEndPointIndex(pointIndex: Int, end: BlockPos): Int? {
    for (i in (pointIndex + 1) until routePoints.size) {
      if (routePoints[i].pos == end) {
        return i
      }
    }
    return null
  }

  private fun handleMine(player: Player, level: net.minecraft.world.level.Level) {
    pruneMineBlocks(level)
    if (mineBlocks.isEmpty()) {
      val waypoints =
        if (mineTravelWaypoints.isNotEmpty()) {
          mineTravelWaypoints
        } else {
          val start = minePointStart
          val end = minePointEnd
          if (start != null && end != null) {
            listOf(start, end)
          } else {
            emptyList()
          }
        }

      if (waypoints.isNotEmpty()) {
        val vein = buildMineVein(level, waypoints)
        if (vein != null && vein.blocks.isNotEmpty()) {
          mineBlocks = vein.blocks
          mineBlockId = vein.blockId
          mineOrderedBlocks = buildMineOrder(vein.blocks, waypoints)
        } else {
          finishMine("Vein complete.")
          return
        }
      } else {
        finishMine("Vein complete.")
        return
      }
    }

    val target = selectMineTarget(level, player, mineBlocks)
    if (target != null) {
      mineTarget = target
      // Keep pathfinder running so the player continues moving while mining.
      // The drill fires while moving through the vein rather than stopping per block.
      startMining(target, level.gameTime)
      return
    }

    stopMiningKeys()
    RotationExecutor.stopRotating()

    val nextPending = selectNextPendingMineBlock(mineBlocks)
    if (nextPending != null) {
      if (!attemptMineEtherwarpRecovery(level, player, nextPending)) {
        moveToward(level, player, nextPending)
      }
      return
    } else {
      finishMine("No mine targets.")
    }
  }

  private fun finishMine(reason: String) {
    ChatUtils.sendMessage(reason)
    stopMiningKeys()
    mc.options.keyShift?.setDown(false)
    RotationExecutor.stopRotating()
    resetMineState()
    completePoint()
  }

  private fun completePoint() {
    val completedPoint = activePoint
    action = RouteAction.NONE
    awaitingArrival = false
    walkCompletePointOnArrival = true
    lastTarget = null
    lastResolvedTarget = null
    activePoint = null
    walkRetryCount = 0
    routeIndex++
    if (chainedMineEndIndex != -1 && routeIndex >= chainedMineEndIndex) {
      chainedMineEndIndex = -1
    }
    stopMiningKeys()
    if (completedPoint?.type == RoutePointType.MINE) {
      mc.options.keyShift?.setDown(false)
    }
    RotationExecutor.stopRotating()
    resetMineState()

    if (!advanceRouteIndexForLoop()) {
      stopRoute("Route complete.")
      return
    }

    if (tryStartMineToMineTransition(completedPoint)) {
      updateStatus()
      return
    }
    if (!routeRunning) {
      return
    }

    if (shouldKeepEtherwarpForNextPoint()) {
      ensureEtherwarpHotbarSelected()
    } else {
      restoreEtherwarpSlot()
    }
    resetWarp()
    updateStatus()
  }

  private fun advanceRouteIndexForLoop(): Boolean {
    if (routeIndex < routePoints.size) return true
    if (!loopRoute.value || routePoints.isEmpty()) return false
    // Loop is on: restart from the very beginning (point 0 = warp to forge/camp).
    routeIndex = 0
    chainedMineEndIndex = -1
    return true
  }

  /**
   * Scans route points for two entries with the same block position (at least one
   * point apart). The first match defines the mining loop: points before
   * [loopStartIndex] are the one-time travel route; points from [loopStartIndex]
   * to [loopEndIndex] (inclusive) are the repeating mining loop.
   */
  private fun detectMiningLoop() {
    // Auto-loop detection removed: intersection points in a route no longer create
    // implicit loops. Looping is controlled exclusively by the Loop Route setting,
    // which always restarts from index 0 (the forge/camp warp at the route start).
    loopStartIndex = -1
    loopEndIndex = -1
  }

  private fun tryStartMineToMineTransition(completedPoint: RoutePoint?): Boolean {
    if (completedPoint?.type != RoutePointType.MINE) return false
    if (routeIndex !in routePoints.indices) return false

    val level = mc.level ?: return false
    val nextPoint = resolvePointForExecution(routeIndex)
    if (nextPoint.type != RoutePointType.MINE) return false

    val warpTarget = resolveWarpPoint(level, nextPoint.pos)
    if (warpTarget != null && startWarp(
        warpTarget,
        completePointOnArrival = false,
        resumeAction = RouteAction.NONE
      )
    ) {
      return true
    }

    startWalk(nextPoint.pos, completePointOnArrival = false)
    return routeRunning && action == RouteAction.WALK
  }

  private fun resetRuntimeState() {
    action = RouteAction.NONE
    awaitingArrival = false
    walkCompletePointOnArrival = true
    lastTarget = null
    lastResolvedTarget = null
    activePoint = null
    walkRetryCount = 0
    chainedMineEndIndex = -1
    loopStartIndex = -1
    loopEndIndex = -1
    stopMiningKeys()
    RotationExecutor.stopRotating()
    mc.options.keyUse?.setDown(false)
    mc.options.keyShift?.setDown(false)
    restoreEtherwarpSlot()
    resetWarp()
    resetMineState()
    lastPathStartTick = 0L
    screenPauseNoticeTick = 0L
    lastSuccessfulWarpTarget = null
    lastSuccessfulWarpTick = -1L
  }

  private fun resetMineState() {
    mineBlocks.clear()
    mineOrderedBlocks = emptyList()
    mineBlockId = null
    mineTarget = null
    minePathTarget = null
    minePointStart = null
    minePointEnd = null
    mineTravelWaypoints = emptyList()
    miningActive = false
    mineDrillWarnTick = 0L
  }

  private fun startMining(target: BlockPos, nowTick: Long) {
    ensureMineDrillSelected(nowTick)
    val aim = Vec3(target.x + 0.5, target.y + 0.5, target.z + 0.5)
    RotationExecutor.rotateTo(AngleUtils.getRotation(aim), rotationStrategy)
    mc.options.keyAttack?.setDown(true)
    miningActive = true
  }

  private fun ensureMineDrillSelected(nowTick: Long) {
    val player = mc.player ?: return
    val selected = player.inventory.getItem(player.inventory.selectedSlot)
    val selectedName = selected.hoverName?.string.orEmpty().lowercase()
    if (selectedName.contains("drill")) return

    val drillSlot = InventoryUtils.findItemInHotbar("drill")
    if (drillSlot in 0..8) {
      InventoryUtils.holdHotbarSlot(drillSlot)
      return
    }

    if (nowTick - mineDrillWarnTick >= MINE_DRILL_WARN_INTERVAL_TICKS) {
      mineDrillWarnTick = nowTick
      ChatUtils.sendMessage("Routes mine: no drill found in hotbar.")
    }
  }

  private fun stopMiningKeys() {
    if (miningActive) {
      mc.options.keyAttack?.setDown(false)
      miningActive = false
    }
  }

  private fun selectMineTarget(
    level: net.minecraft.world.level.Level,
    player: Player,
    blocks: Set<BlockPos>
  ): BlockPos? {
    val ordered = if (mineOrderedBlocks.isNotEmpty()) mineOrderedBlocks else blocks.toList()
    if (ordered.isEmpty()) return null
    val rangeSq = MINE_RANGE * MINE_RANGE
    // Always scan from mine1 end so the vein is mined progressively toward mine2
    for (pos in ordered) {
      if (!blocks.contains(pos)) continue
      if (distanceToBlockSq(player, pos) > rangeSq) continue
      if (MINE_REQUIRE_LOS && !hasMineLineOfSight(level, player, pos)) continue
      return pos
    }
    return null
  }

  private fun selectNextPendingMineBlock(blocks: Set<BlockPos>): BlockPos? {
    val ordered = if (mineOrderedBlocks.isNotEmpty()) mineOrderedBlocks else blocks.toList()
    // Return the first remaining block in mine1→mine2 order (for etherwarp targeting)
    for (pos in ordered) {
      if (blocks.contains(pos)) return pos
    }
    return null
  }

  private fun buildMineOrder(
    blocks: Set<BlockPos>,
    travelWaypoints: List<BlockPos>
  ): List<BlockPos> {
    if (blocks.isEmpty()) return emptyList()

    val waypoints = mutableListOf<BlockPos>()
    for (pos in travelWaypoints) {
      if (waypoints.isEmpty() || waypoints.last() != pos) {
        waypoints.add(pos)
      }
    }
    if (waypoints.isEmpty()) {
      return blocks.toList()
    }
    if (waypoints.size == 1) {
      val only = waypoints.first()
      return blocks
        .sortedBy { pos -> pos.distSqr(only).toDouble() }
    }

    val centers = waypoints.map { pos ->
      Vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
    }
    val prefixDistances = DoubleArray(centers.size)
    val segmentLengths = DoubleArray(centers.size - 1)
    for (i in 0 until centers.size - 1) {
      val a = centers[i]
      val b = centers[i + 1]
      val segDx = b.x - a.x
      val segDy = b.y - a.y
      val segDz = b.z - a.z
      val len = kotlin.math.sqrt(segDx * segDx + segDy * segDy + segDz * segDz)
      segmentLengths[i] = len
      prefixDistances[i + 1] = prefixDistances[i] + len
    }
    val totalLength = prefixDistances.last().coerceAtLeast(1.0e-6)
    val startCenter = centers.first()

    val ranked = blocks.map { pos ->
      val px = pos.x + 0.5
      val py = pos.y + 0.5
      val pz = pos.z + 0.5

      var bestLateralDistSq = Double.POSITIVE_INFINITY
      var bestProgressDistance = 0.0

      for (i in 0 until centers.size - 1) {
        val a = centers[i]
        val b = centers[i + 1]
        val segDx = b.x - a.x
        val segDy = b.y - a.y
        val segDz = b.z - a.z
        val lenSq = segDx * segDx + segDy * segDy + segDz * segDz
        if (lenSq <= 1.0e-6) continue

        val vx = px - a.x
        val vy = py - a.y
        val vz = pz - a.z
        val t = ((vx * segDx + vy * segDy + vz * segDz) / lenSq).coerceIn(0.0, 1.0)
        val cx = a.x + segDx * t
        val cy = a.y + segDy * t
        val cz = a.z + segDz * t
        val lx = px - cx
        val ly = py - cy
        val lz = pz - cz
        val lateralDistSq = lx * lx + ly * ly + lz * lz
        val progressDistance = prefixDistances[i] + segmentLengths[i] * t

        if (lateralDistSq < bestLateralDistSq) {
          bestLateralDistSq = lateralDistSq
          bestProgressDistance = progressDistance
        }
      }

      val progress = (bestProgressDistance / totalLength).coerceIn(0.0, 1.0)
      val startDx = px - startCenter.x
      val startDy = py - startCenter.y
      val startDz = pz - startCenter.z
      val startDistSq = startDx * startDx + startDy * startDy + startDz * startDz
      MineOrderEntry(pos, progress, bestLateralDistSq, startDistSq)
    }

    return ranked
      .sortedWith(
        compareBy<MineOrderEntry>(
          { it.progress },
          { it.lateralDistSq },
          { it.startDistSq }
        )
      )
      .map { it.pos }
  }

  private fun moveToward(
    level: net.minecraft.world.level.Level,
    player: Player,
    target: BlockPos
  ) {
    val approach = findApproach(level, player, target) ?: return
    PathfindingModule.ensureEnabledForAutomation("routes")
    val distSq = minePathTarget?.distSqr(approach)?.toDouble() ?: Double.POSITIVE_INFINITY
    if (!nativeActive() || distSq > 1.0) {
      if (level.gameTime - lastPathStartTick < 8L) {
        return
      }
      lastPathStartTick = level.gameTime
      NativePathfinder.setTarget(approach.x + 0.5, approach.y.toDouble(), approach.z + 0.5)
      minePathTarget = approach
    }
    NativePathfinder.tick()?.applyToPlayer()
  }

  private fun findApproach(
    level: net.minecraft.world.level.Level,
    player: Player,
    target: BlockPos
  ): BlockPos? {
    var best: BlockPos? = null
    var bestScore = Double.POSITIVE_INFINITY
    val origin = player.blockPosition()
    val offsets = arrayOf(
      intArrayOf(1, 0, 0), intArrayOf(-1, 0, 0), intArrayOf(0, 0, 1), intArrayOf(0, 0, -1),
      intArrayOf(1, -1, 0), intArrayOf(-1, -1, 0), intArrayOf(0, -1, 1), intArrayOf(0, -1, -1),
      intArrayOf(1, 1, 0), intArrayOf(-1, 1, 0), intArrayOf(0, 1, 1), intArrayOf(0, 1, -1),
      intArrayOf(1, 0, 1), intArrayOf(1, 0, -1), intArrayOf(-1, 0, 1), intArrayOf(-1, 0, -1),
      intArrayOf(0, 2, 0), intArrayOf(0, -2, 0),
    )
    for (off in offsets) {
      val pos = target.offset(off[0], off[1], off[2])
      if (!MinecraftPathingRules.isWalkable(level, pos)) continue
      val playerDistSq = origin.distSqr(pos).toDouble()
      val targetDistSq = pos.distSqr(target).toDouble()
      val score = playerDistSq + targetDistSq * 0.5
      if (score < bestScore) {
        bestScore = score
        best = pos
      }
    }
    if (best != null) return best
    return MinecraftPathingRules.resolveTarget(level, target)
  }

  private fun pruneMineBlocks(level: net.minecraft.world.level.Level) {
    val id = mineBlockId ?: return
    val iterator = mineBlocks.iterator()
    while (iterator.hasNext()) {
      val pos = iterator.next()
      if (blockIdAt(level, pos) != id) {
        iterator.remove()
      }
    }
  }

  private fun buildMineVein(
    level: net.minecraft.world.level.Level,
    waypoints: List<BlockPos>
  ): MineVein? {
    if (waypoints.isEmpty()) return null
    val start = waypoints.first()
    val minX = waypoints.minOf { it.x }
    val maxX = waypoints.maxOf { it.x }
    val minY = waypoints.minOf { it.y }
    val maxY = waypoints.maxOf { it.y }
    val minZ = waypoints.minOf { it.z }
    val maxZ = waypoints.maxOf { it.z }

    val seed = findMineSeed(level, start, minX, maxX, minY, maxY, minZ, maxZ) ?: return null
    val blockId = blockIdAt(level, seed)

    val blocks = LinkedHashSet<BlockPos>()
    val queue = ArrayDeque<BlockPos>()
    queue.add(seed)
    blocks.add(seed)

    while (queue.isNotEmpty() && blocks.size < MINE_MAX_BLOCKS) {
      val pos = queue.removeFirst()
      for (dir in Direction.values()) {
        val next = pos.relative(dir)
        if (abs(next.x - seed.x) > MINE_VEIN_SCAN_RADIUS) continue
        if (abs(next.y - seed.y) > MINE_VEIN_SCAN_RADIUS) continue
        if (abs(next.z - seed.z) > MINE_VEIN_SCAN_RADIUS) continue
        if (blocks.contains(next)) continue
        if (blockIdAt(level, next) != blockId) continue
        blocks.add(next)
        queue.add(next)
        if (blocks.size >= MINE_MAX_BLOCKS) break
      }
    }

    return MineVein(blockId, blocks)
  }

  private fun findMineSeed(
    level: net.minecraft.world.level.Level,
    start: BlockPos,
    minX: Int,
    maxX: Int,
    minY: Int,
    maxY: Int,
    minZ: Int,
    maxZ: Int
  ): BlockPos? {
    if (!level.getBlockState(start).isAir) {
      return start
    }
    for (y in minY..maxY) {
      for (x in minX..maxX) {
        for (z in minZ..maxZ) {
          val pos = BlockPos(x, y, z)
          if (!level.getBlockState(pos).isAir) {
            return pos
          }
        }
      }
    }
    return null
  }

  private fun hasLineOfSight(
    level: net.minecraft.world.level.Level,
    player: Player,
    target: BlockPos
  ): Boolean {
    val center = Vec3(target.x + 0.5, target.y + 0.5, target.z + 0.5)
    return hasLineOfSight(level, player, target, center)
  }

  private fun hasLineOfSight(
    level: net.minecraft.world.level.Level,
    player: Player,
    target: BlockPos,
    point: Vec3
  ): Boolean {
    val eye = player.eyePosition
    val hit = level.clip(
      net.minecraft.world.level.ClipContext(
        eye,
        point,
        net.minecraft.world.level.ClipContext.Block.OUTLINE,
        net.minecraft.world.level.ClipContext.Fluid.NONE,
        player
      )
    )
    return hit.type == net.minecraft.world.phys.HitResult.Type.BLOCK &&
      hit.blockPos == target
  }

  private fun hasMineLineOfSight(
    level: net.minecraft.world.level.Level,
    player: Player,
    target: BlockPos
  ): Boolean {
    val center = Vec3(target.x + 0.5, target.y + 0.5, target.z + 0.5)
    val points = listOf(
      center,
      Vec3(center.x + 0.24, center.y, center.z),
      Vec3(center.x - 0.24, center.y, center.z),
      Vec3(center.x, center.y + 0.24, center.z),
      Vec3(center.x, center.y - 0.24, center.z),
      Vec3(center.x, center.y, center.z + 0.24),
      Vec3(center.x, center.y, center.z - 0.24),
    )
    for (point in points) {
      if (hasLineOfSight(level, player, target, point)) {
        return true
      }
    }
    return false
  }

  private fun blockIdAt(level: net.minecraft.world.level.Level, pos: BlockPos): String {
    val state = level.getBlockState(pos)
    return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.block).toString()
  }

  private fun distanceToBlockSq(player: Player, pos: BlockPos): Double {
    val dx = (pos.x + 0.5) - player.x
    val dy = (pos.y + 0.5) - player.y
    val dz = (pos.z + 0.5) - player.z
    return dx * dx + dy * dy + dz * dz
  }

  private fun hasArrived(player: Player, target: BlockPos): Boolean {
    val distSq = player.blockPosition().distSqr(target).toDouble()
    return distSq <= ARRIVAL_DISTANCE_SQ
  }

  private fun applyWarpHeadRotation(player: Player, target: Vec3): Pair<Double, Double> {
    val targetRotation = AngleUtils.getRotation(target)
    val now = System.nanoTime()
    val dtSec =
      if (warpLookLastNs == 0L) {
        1.0 / 60.0
      } else {
        ((now - warpLookLastNs) / 1_000_000_000.0).coerceIn(1.0 / 240.0, 0.08)
      }
    warpLookLastNs = now

    val maxYawStep = WARP_LOOK_YAW_SPEED_DPS * dtSec
    val maxPitchStep = WARP_LOOK_PITCH_SPEED_DPS * dtSec

    val yawDelta = AngleUtils.getRotationDelta(player.yRot, targetRotation.yaw).toDouble()
    val pitchDelta = (targetRotation.pitch - player.xRot).toDouble()

    val yawStep = yawDelta.coerceIn(-maxYawStep, maxYawStep).toFloat()
    val pitchStep = pitchDelta.coerceIn(-maxPitchStep, maxPitchStep).toFloat()

    player.yRot = AngleUtils.normalizeAngle(player.yRot + yawStep)
    player.yHeadRot = player.yRot
    player.yBodyRot = player.yRot
    player.xRot = (player.xRot + pitchStep).coerceIn(-89.9f, 89.9f)

    val yawError = kotlin.math.abs(AngleUtils.getRotationDelta(player.yRot, targetRotation.yaw)).toDouble()
    val pitchError = kotlin.math.abs(targetRotation.pitch - player.xRot).toDouble()
    return yawError to pitchError
  }

  private fun resolveWarpAimPoint(
    level: net.minecraft.world.level.Level,
    player: Player,
    target: BlockPos
  ): Vec3 {
    val center = Vec3(target.x + 0.5, target.y + 0.5, target.z + 0.5)
    val eye = player.eyePosition
    val towardX = (eye.x - center.x).coerceIn(-0.28, 0.28)
    val towardZ = (eye.z - center.z).coerceIn(-0.28, 0.28)

    val candidates = listOf(
      center,
      Vec3(center.x, center.y + 0.26, center.z),
      Vec3(center.x, center.y - 0.20, center.z),
      Vec3(center.x + towardX, center.y, center.z),
      Vec3(center.x, center.y, center.z + towardZ),
      Vec3(center.x + towardX, center.y + 0.18, center.z + towardZ),
      Vec3(center.x + towardX, center.y - 0.12, center.z + towardZ),
      Vec3(center.x + 0.24, center.y, center.z),
      Vec3(center.x - 0.24, center.y, center.z),
      Vec3(center.x, center.y, center.z + 0.24),
      Vec3(center.x, center.y, center.z - 0.24),
    )

    for (candidate in candidates) {
      if (hasLineOfSight(level, player, target, candidate)) {
        return candidate
      }
    }
    return center
  }

  private fun startWarp(
    target: BlockPos,
    completePointOnArrival: Boolean = true,
    resumeAction: RouteAction = RouteAction.NONE
  ): Boolean {
    val player = mc.player ?: return false
    val level = mc.level ?: return false
    if (level.gameTime < warpCooldownUntil) return false
    if (wasJustWarpedToTarget(level, player, target)) return false
    if (!ensureEtherwarpHotbarSelected()) return false
    if (!EtherwarpLogic.holdingEtherwarpItem()) return false
    if (!ensureEtherwarpHotbarSelected()) return false

    if (nativeActive()) {
      nativeStop(null)
    }
    RotationExecutor.stopRotating()
    mc.options.keyUse?.setDown(false)
    mc.options.keyShift?.setDown(false)

    warpTarget = target
    warpStage = 0
    warpStageElapsedMs = 0.0
    warpStageLastNs = 0L
    warpLookLastNs = 0L
    warpCompletePointOnArrival = completePointOnArrival
    warpResumeAction = resumeAction
    action = RouteAction.WARP
    return true
  }

  private fun handleWarp(
    player: Player,
    level: net.minecraft.world.level.Level
  ) {
    val target = warpTarget ?: run {
      resetWarp()
      return
    }

    val warpAimPoint = resolveWarpAimPoint(level, player, target)
    advanceWarpFrameTime()

    when (warpStage) {
      0 -> {
        val (yawError, pitchError) = applyWarpHeadRotation(player, warpAimPoint)
        if (
          (yawError <= WARP_AIM_TOLERANCE && pitchError <= WARP_AIM_TOLERANCE) ||
          warpStageElapsedMs >= WARP_ALIGN_MS
        ) {
          mc.options.keyShift?.setDown(true)
          warpStage = 1
          resetWarpStageTimer()
          return
        }
      }
      1 -> {
        applyWarpHeadRotation(player, warpAimPoint)
        mc.options.keyShift?.setDown(true)
        if (!canWarpToTarget(level, player, target, warpAimPoint)) {
          if (warpStageElapsedMs >= WARP_STAGE1_TIMEOUT_MS) {
            mc.options.keyShift?.setDown(false)
            warpCooldownUntil = level.gameTime + WARP_RETRY_COOLDOWN_TICKS
            val resumeAction = warpResumeAction
            resetWarp()
            action = resumeAction
            return
          }
          return
        }
        if (warpStageElapsedMs >= WARP_SNEAK_MS) {
          val shiftKeyHeld = mc.options.keyShift?.isDown == true
          val playerIsShifting = player.isShiftKeyDown
          if (!shiftKeyHeld || !playerIsShifting) {
            return
          }
          mc.options.keyUse?.setDown(true)
          warpStage = 2
          resetWarpStageTimer()
          return
        }
      }
      else -> {
        mc.options.keyUse?.setDown(false)
        val landed = hasArrived(player, target)
        val postWarpAim = if (landed) resolveNextRouteLookPoint(level) else null
        val frameAim =
          if (postWarpAim != null) {
            val t = (warpStageElapsedMs / WARP_POST_MS).coerceIn(0.0, 1.0)
            blendVec3(warpAimPoint, postWarpAim, t)
          } else {
            warpAimPoint
          }
        applyWarpHeadRotation(player, frameAim)

        mc.options.keyShift?.setDown(true)
        if (warpStageElapsedMs >= WARP_POST_MS) {
          mc.options.keyShift?.setDown(false)
          warpCooldownUntil = level.gameTime + WARP_COOLDOWN_TICKS
          val arrived = hasArrived(player, target)
          if (arrived) {
            lastSuccessfulWarpTarget = target
            lastSuccessfulWarpTick = level.gameTime
          }
          val completePointOnArrival = warpCompletePointOnArrival
          val resumeAction = warpResumeAction
          resetWarp()
          if (arrived) {
            if (completePointOnArrival) {
              completePoint()
            } else {
              action = resumeAction
            }
          } else {
            action = resumeAction
          }
          return
        }
      }
    }
  }

  private fun advanceWarpFrameTime() {
    val now = System.nanoTime()
    val dtMs =
      if (warpStageLastNs == 0L) {
        0.0
      } else {
        ((now - warpStageLastNs) / 1_000_000.0).coerceIn(0.0, 80.0)
      }
    warpStageLastNs = now
    warpStageElapsedMs += dtMs
  }

  private fun resetWarpStageTimer() {
    warpStageElapsedMs = 0.0
    warpStageLastNs = System.nanoTime()
  }

  private fun resolveNextRouteLookPoint(level: net.minecraft.world.level.Level): Vec3? {
    val nextIndex = routeIndex + 1
    if (nextIndex !in routePoints.indices) return null
    val nextPoint = routePoints[nextIndex]
    val lookBlock =
      if (nextPoint.type == RoutePointType.WARP) {
        resolveWarpPoint(level, nextPoint.pos) ?: nextPoint.pos
      } else {
        nextPoint.pos
      }
    return Vec3(lookBlock.x + 0.5, lookBlock.y + 0.6, lookBlock.z + 0.5)
  }

  private fun attemptRouteVisibleEtherwarpRecovery(
    level: net.minecraft.world.level.Level,
    player: Player
  ): Boolean {
    if (routePoints.isEmpty()) return false
    if (level.gameTime < warpCooldownUntil) return false

    val target = findClosestVisibleRouteWarpTarget(level, player) ?: return false
    return startWarp(target)
  }

  private fun findClosestVisibleRouteWarpTarget(
    level: net.minecraft.world.level.Level,
    player: Player
  ): BlockPos? {
    var best: BlockPos? = null
    var bestDistSq = Double.POSITIVE_INFINITY
    val eye = player.eyePosition
    val startIndex = routeIndex.coerceIn(0, routePoints.lastIndex)

    for (i in startIndex until routePoints.size) {
      val point = routePoints[i]
      val candidate =
        when (point.type) {
          RoutePointType.WARP -> resolveWarpPoint(level, point.pos)
          else -> candidateWarpBlock(level, point.pos)
        } ?: continue

      if (isStandingOnWarpTarget(player, candidate)) continue
      if (wasJustWarpedToTarget(level, player, candidate)) continue
      if (!canWarpToTarget(level, player, candidate)) continue

      val center = Vec3(candidate.x + 0.5, candidate.y + 0.5, candidate.z + 0.5)
      val distSq = eye.distanceToSqr(center)
      if (distSq < bestDistSq) {
        bestDistSq = distSq
        best = candidate
      }
    }
    return best
  }

  private fun attemptMineEtherwarpRecovery(
    level: net.minecraft.world.level.Level,
    player: Player,
    target: BlockPos
  ): Boolean {
    if (level.gameTime < warpCooldownUntil) return true

    val mineTarget = findClosestVisibleMineWarpTarget(level, player, target)
    if (mineTarget != null && startWarp(
        mineTarget,
        completePointOnArrival = false,
        resumeAction = RouteAction.MINE
      )
    ) {
      return true
    }

    return false
  }

  private fun findClosestVisibleMineWarpTarget(
    level: net.minecraft.world.level.Level,
    player: Player,
    focus: BlockPos
  ): BlockPos? {
    if (mineBlocks.isEmpty()) return null

    val candidates = LinkedHashSet<BlockPos>()

    // Scan adjacent walkable tunnel spots around the focus ore block
    fun addAdjacentWalkableLandings(oreBlock: BlockPos) {
      for (dir in Direction.values()) {
        val adj = oreBlock.relative(dir)
        if (!MinecraftPathingRules.isWalkable(level, adj)) continue
        val floor = adj.below()
        if (level.getBlockState(floor).isAir) continue
        if (!MinecraftPathingRules.isPassable(level, adj)) continue
        if (!MinecraftPathingRules.isPassable(level, adj.above())) continue
        candidates.add(floor)
      }
    }

    addAdjacentWalkableLandings(focus)
    candidateWarpBlock(level, focus)?.let { candidates.add(it) }

    val ordered = if (mineOrderedBlocks.isNotEmpty()) mineOrderedBlocks else mineBlocks.toList()
    for (block in ordered) {
      if (!mineBlocks.contains(block)) continue
      addAdjacentWalkableLandings(block)
      if (candidates.size >= MINE_WARP_MAX_CANDIDATES) break
    }

    val eye = player.eyePosition
    var best: BlockPos? = null
    var bestScore = Double.POSITIVE_INFINITY
    for (candidate in candidates) {
      if (isStandingOnWarpTarget(player, candidate)) continue
      if (wasJustWarpedToTarget(level, player, candidate)) continue
      if (!canWarpToTarget(level, player, candidate)) continue
      val center = Vec3(candidate.x + 0.5, candidate.y + 0.5, candidate.z + 0.5)
      val eyeDistSq = eye.distanceToSqr(center)
      val focusDistSq = candidate.distSqr(focus).toDouble()
      val score = focusDistSq * 1.6 + eyeDistSq
      if (score < bestScore) {
        bestScore = score
        best = candidate
      }
    }
    return best
  }

  private fun blendVec3(from: Vec3, to: Vec3, t: Double): Vec3 {
    val clamped = t.coerceIn(0.0, 1.0)
    return Vec3(
      from.x + (to.x - from.x) * clamped,
      from.y + (to.y - from.y) * clamped,
      from.z + (to.z - from.z) * clamped
    )
  }

  private fun resolveWarpPoint(level: net.minecraft.world.level.Level, rawPoint: BlockPos): BlockPos? {
    val direct = candidateWarpBlock(level, rawPoint)
    if (direct != null && isWarpBlockViable(level, direct)) {
      return direct
    }

    var best: BlockPos? = null
    var bestDistSq = Double.POSITIVE_INFINITY
    for (dy in -WARP_RESOLVE_VERTICAL..WARP_RESOLVE_VERTICAL) {
      for (dx in -WARP_RESOLVE_RADIUS..WARP_RESOLVE_RADIUS) {
        for (dz in -WARP_RESOLVE_RADIUS..WARP_RESOLVE_RADIUS) {
          val probe = rawPoint.offset(dx, dy, dz)
          val candidate = candidateWarpBlock(level, probe) ?: continue
          if (!isWarpBlockViable(level, candidate)) continue
          val distSq = candidate.distSqr(rawPoint).toDouble()
          if (distSq < bestDistSq) {
            bestDistSq = distSq
            best = candidate
          }
        }
      }
    }
    return best
  }

  private fun candidateWarpBlock(level: net.minecraft.world.level.Level, pos: BlockPos): BlockPos? {
    return if (MinecraftPathingRules.isWalkable(level, pos)) {
      val support = pos.below()
      if (level.getBlockState(support).isAir) null else support
    } else {
      if (level.getBlockState(pos).isAir) null else pos
    }
  }

  private fun isWarpBlockViable(level: net.minecraft.world.level.Level, block: BlockPos): Boolean {
    if (level.getBlockState(block).isAir) return false
    if (!MinecraftPathingRules.isPassable(level, block.above())) return false
    if (!MinecraftPathingRules.isPassable(level, block.above(2))) return false
    return true
  }

  private fun canWarpToTarget(
    level: net.minecraft.world.level.Level,
    player: Player,
    target: BlockPos,
    aimPoint: Vec3? = null
  ): Boolean {
    if (!isWarpBlockViable(level, target)) return false
    if (!EtherwarpLogic.canEtherwarp()) return false
    val eye = player.eyePosition
    val point = aimPoint ?: resolveWarpAimPoint(level, player, target)
    val range = EtherwarpLogic.getEtherwarpRange().toDouble() + 0.5
    if (eye.distanceToSqr(point) > range * range) return false
    // Use etherwarp's own raycast — standard line-of-sight misses blocks that
    // etherwarp passes through (carpets, vines, flowers, etc.)
    val result = EtherwarpLogic.getEtherwarpResultSneaking()
    return result.succeeded && result.pos == target
  }

  private fun isStandingOnWarpTarget(player: Player, target: BlockPos): Boolean {
    return player.blockPosition().below() == target
  }

  private fun wasJustWarpedToTarget(
    level: net.minecraft.world.level.Level,
    player: Player,
    target: BlockPos
  ): Boolean {
    val lastTarget = lastSuccessfulWarpTarget ?: return false
    if (lastSuccessfulWarpTick < 0L) return false
    if (lastTarget != target) return false
    if (!isStandingOnWarpTarget(player, target)) return false
    return level.gameTime - lastSuccessfulWarpTick <= WARP_REPEAT_BLOCK_SUPPRESS_TICKS
  }

  private fun resetWarp() {
    mc.options.keyUse?.setDown(false)
    mc.options.keyShift?.setDown(false)
    RotationExecutor.stopRotating()
    warpStage = 0
    warpTarget = null
    warpStageElapsedMs = 0.0
    warpStageLastNs = 0L
    warpLookLastNs = 0L
    warpCompletePointOnArrival = true
    warpResumeAction = RouteAction.NONE
  }

  private fun ensureEtherwarpHotbarSelected(): Boolean {
    val player = mc.player ?: return false
    val currentSlot = player.inventory.selectedSlot
    val currentStack = player.inventory.getItem(currentSlot)
    if (EtherwarpLogic.isEtherwarpStack(currentStack)) {
      return true
    }
    if (EtherwarpLogic.isEtherwarpStack(player.offhandItem)) {
      return true
    }
    val slot = EtherwarpLogic.findEtherwarpHotbarSlot()
    if (slot in 0..8) {
      if (warpRestoreSlot == -1) {
        warpRestoreSlot = currentSlot
      }
      InventoryUtils.holdHotbarSlot(slot)
      return true
    }
    return false
  }

  private fun restoreEtherwarpSlot() {
    if (warpRestoreSlot in 0..8) {
      InventoryUtils.holdHotbarSlot(warpRestoreSlot)
    }
    warpRestoreSlot = -1
  }

  private fun shouldKeepEtherwarpForNextPoint(): Boolean {
    if (routeIndex !in routePoints.indices) return false
    if (chainedMineEndIndex > routeIndex) return false
    return routePoints[routeIndex].type == RoutePointType.WARP
  }

  private const val MAX_WALK_RETRIES = 3
  private const val ARRIVAL_DISTANCE_SQ = 6.0 * 6.0
  private const val APPROX_SCAN_RADIUS = 6
  private const val APPROX_SCAN_VERTICAL = 4
  private const val MINE_RANGE = 4.5
  private const val MINE_REQUIRE_LOS = true
  private const val MINE_MAX_BLOCKS = 768
  private const val MINE_VEIN_SCAN_RADIUS = 18
  private const val MINE_WARP_MAX_CANDIDATES = 220
  private const val MINE_DRILL_WARN_INTERVAL_TICKS = 60L
  private const val WARP_AIM_TOLERANCE = 6.0
  private const val WARP_LOOK_YAW_SPEED_DPS = 360.0
  private const val WARP_LOOK_PITCH_SPEED_DPS = 300.0
  private const val WARP_ALIGN_MS = 170.0
  private const val WARP_SNEAK_MS = 85.0
  private const val WARP_POST_MS = 70.0
  private const val WARP_COOLDOWN_TICKS = 1L
  private const val WARP_STAGE1_TIMEOUT_MS = 240.0
  private const val WARP_RETRY_COOLDOWN_TICKS = 4L
  private const val WARP_REPEAT_BLOCK_SUPPRESS_TICKS = 10L
  private const val WARP_RESOLVE_RADIUS = 2
  private const val WARP_RESOLVE_VERTICAL = 2

  private data class MineVein(
    val blockId: String,
    val blocks: MutableSet<BlockPos>
  )

  private data class MineOrderEntry(
    val pos: BlockPos,
    val progress: Double,
    val lateralDistSq: Double,
    val startDistSq: Double
  )
}
