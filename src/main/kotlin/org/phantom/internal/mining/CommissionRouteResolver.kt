package org.phantom.internal.mining

import net.minecraft.core.BlockPos
import org.phantom.api.pathfinder.scoring.WaypointScorer
import org.phantom.internal.routes.*

object CommissionRouteResolver {

  enum class CommissionRouteMode(
    val displayName: String
  ) {
    CUSTOM_THEN_AUTO("Custom then Auto"),
    AUTO_THEN_CUSTOM("Auto then Custom"),
    CUSTOM_ONLY("Custom Only"),
    AUTO_ONLY("Auto Only")
  }

  enum class RouteSource {
    SLOT_ASSIGNMENT,
    LOADED_COMMISSION_ROUTE,
    NAMED_ROUTE_MATCH,
    AUTO_TASK_WAYPOINTS,
    NONE
  }

  data class CommissionRouteChoice(
    val routeName: String?,
    val source: RouteSource,
    val mode: CommissionRouteMode,
    val region: CommissionRegion,
    val travelPoints: List<BlockPos>,
    val workPoints: List<BlockPos>,
    val selectedTarget: BlockPos?,
    val shouldRunRouteModule: Boolean
  ) {
    val hasCustomRoute: Boolean
      get() = routeName != null &&
        source != RouteSource.AUTO_TASK_WAYPOINTS &&
        source != RouteSource.NONE

    val hasAutoRoute: Boolean
      get() = source == RouteSource.AUTO_TASK_WAYPOINTS

    val hasAnyRoute: Boolean
      get() = selectedTarget != null || travelPoints.isNotEmpty() || workPoints.isNotEmpty()

    val allPoints: List<BlockPos>
      get() = travelPoints + workPoints

    fun summary(): String {
      return when {
        hasCustomRoute -> "Custom ${region.displayName} route: $routeName [$source]"
        hasAutoRoute -> "Auto ${region.displayName} route: ${selectedTarget?.let { "${it.x}, ${it.y}, ${it.z}" } ?: "none"}"
        else -> "No route"
      }
    }
  }

  fun resolveForCommission(
    commissionName: String,
    task: CommissionTask,
    fallbackWaypoints: List<BlockPos>,
    mode: CommissionRouteMode = CommissionRouteMode.CUSTOM_THEN_AUTO,
    avoidPlayers: Boolean = true,
    avoidDangerEntities: Boolean = false
  ): CommissionRouteChoice {
    decayFailures()

    return when (mode) {
      CommissionRouteMode.CUSTOM_THEN_AUTO -> {
        resolveCustom(
          commissionName = commissionName,
          task = task,
          fallbackWaypoints = fallbackWaypoints,
          mode = mode,
          avoidPlayers = avoidPlayers,
          avoidDangerEntities = avoidDangerEntities
        ) ?: resolveAuto(
          task = task,
          fallbackWaypoints = fallbackWaypoints,
          mode = mode,
          avoidPlayers = avoidPlayers,
          avoidDangerEntities = avoidDangerEntities
        )
      }

      CommissionRouteMode.AUTO_THEN_CUSTOM -> {
        val auto = resolveAuto(
          task = task,
          fallbackWaypoints = fallbackWaypoints,
          mode = mode,
          avoidPlayers = avoidPlayers,
          avoidDangerEntities = avoidDangerEntities
        )

        if (auto.selectedTarget != null) {
          auto
        } else {
          resolveCustom(
            commissionName = commissionName,
            task = task,
            fallbackWaypoints = fallbackWaypoints,
            mode = mode,
            avoidPlayers = avoidPlayers,
            avoidDangerEntities = avoidDangerEntities
          ) ?: auto
        }
      }

      CommissionRouteMode.CUSTOM_ONLY -> {
        resolveCustom(
          commissionName = commissionName,
          task = task,
          fallbackWaypoints = fallbackWaypoints,
          mode = mode,
          avoidPlayers = avoidPlayers,
          avoidDangerEntities = avoidDangerEntities
        ) ?: emptyChoice(task.regionCompat, mode)
      }

      CommissionRouteMode.AUTO_ONLY -> {
        resolveAuto(
          task = task,
          fallbackWaypoints = fallbackWaypoints,
          mode = mode,
          avoidPlayers = avoidPlayers,
          avoidDangerEntities = avoidDangerEntities
        )
      }
    }
  }

  fun markFailed(pos: BlockPos?) {
    if (pos == null) return
    WaypointScorer.markFailed(pos)
  }

  fun decayFailures() {
    WaypointScorer.decayFailures()
  }

  fun resetFailures() {
    WaypointScorer.reset()
  }

  fun routeNameForCommission(
    commissionName: String,
    task: CommissionTask
  ): String? {
    val normalizedCommission = normalizeKey(commissionName)
    val normalizedTask = normalizeKey(task.primaryName)

    return RouteStore.getSlotRoute(slotKey("commission", normalizedCommission))
      ?: RouteStore.getSlotRoute(slotKey("${task.regionCompat.slotKey}-commission", normalizedCommission))
      ?: RouteStore.getSlotRoute(slotKey("commission", normalizedTask))
      ?: RouteStore.getSlotRoute(slotKey("${task.regionCompat.slotKey}-commission", normalizedTask))
      ?: RouteStore.getSlotRoute(areaSlotKey(task))
      ?: RouteStore.getSlotRoute(typeSlotKey(task))
      ?: RouteStore.getSlotRoute(regionSlotKey(task.regionCompat))
      ?: findNamedRoute(commissionName, task)?.name
  }

  private fun resolveCustom(
    commissionName: String,
    task: CommissionTask,
    fallbackWaypoints: List<BlockPos>,
    mode: CommissionRouteMode,
    avoidPlayers: Boolean,
    avoidDangerEntities: Boolean
  ): CommissionRouteChoice? {
    val normalizedCommission = normalizeKey(commissionName)
    val normalizedTask = normalizeKey(task.primaryName)

    val routeFromSlot =
      loadAssignedRoute(slotKey("commission", normalizedCommission))
        ?: loadAssignedRoute(slotKey("${task.regionCompat.slotKey}-commission", normalizedCommission))
        ?: loadAssignedRoute(slotKey("commission", normalizedTask))
        ?: loadAssignedRoute(slotKey("${task.regionCompat.slotKey}-commission", normalizedTask))
        ?: loadAssignedRoute(areaSlotKey(task))
        ?: loadAssignedRoute(typeSlotKey(task))
        ?: loadAssignedRoute(regionSlotKey(task.regionCompat))

    if (routeFromSlot != null) {
      return buildCustomChoice(
        route = routeFromSlot,
        source = RouteSource.SLOT_ASSIGNMENT,
        mode = mode,
        region = task.regionCompat,
        fallbackWaypoints = fallbackWaypoints,
        avoidPlayers = avoidPlayers,
        avoidDangerEntities = avoidDangerEntities
      )
    }

    val loadedCommissionRoute = getLoadedCommissionRoute()

    if (loadedCommissionRoute != null) {
      return buildCustomChoice(
        route = loadedCommissionRoute,
        source = RouteSource.LOADED_COMMISSION_ROUTE,
        mode = mode,
        region = task.regionCompat,
        fallbackWaypoints = fallbackWaypoints,
        avoidPlayers = avoidPlayers,
        avoidDangerEntities = avoidDangerEntities
      )
    }

    val namedMatch = findNamedRoute(commissionName, task)

    if (namedMatch != null) {
      return buildCustomChoice(
        route = namedMatch,
        source = RouteSource.NAMED_ROUTE_MATCH,
        mode = mode,
        region = task.regionCompat,
        fallbackWaypoints = fallbackWaypoints,
        avoidPlayers = avoidPlayers,
        avoidDangerEntities = avoidDangerEntities
      )
    }

    return null
  }

  private fun resolveAuto(
    task: CommissionTask,
    fallbackWaypoints: List<BlockPos>,
    mode: CommissionRouteMode,
    avoidPlayers: Boolean,
    avoidDangerEntities: Boolean
  ): CommissionRouteChoice {
    val selected = selectBestWaypoint(
      waypoints = fallbackWaypoints,
      avoidPlayers = avoidPlayers,
      avoidDangerEntities = avoidDangerEntities
    )

    return CommissionRouteChoice(
      routeName = null,
      source = RouteSource.AUTO_TASK_WAYPOINTS,
      mode = mode,
      region = task.regionCompat,
      travelPoints = emptyList(),
      workPoints = fallbackWaypoints,
      selectedTarget = selected,
      shouldRunRouteModule = false
    )
  }

  private fun emptyChoice(
    region: CommissionRegion,
    mode: CommissionRouteMode
  ): CommissionRouteChoice {
    return CommissionRouteChoice(
      routeName = null,
      source = RouteSource.NONE,
      mode = mode,
      region = region,
      travelPoints = emptyList(),
      workPoints = emptyList(),
      selectedTarget = null,
      shouldRunRouteModule = false
    )
  }

  private fun buildCustomChoice(
    route: SavedRoute,
    source: RouteSource,
    mode: CommissionRouteMode,
    region: CommissionRegion,
    fallbackWaypoints: List<BlockPos>,
    avoidPlayers: Boolean,
    avoidDangerEntities: Boolean
  ): CommissionRouteChoice {
    val travelPoints = extractTravelPoints(route)
    val workPoints = extractWorkPoints(route)

    val candidates = when {
      workPoints.isNotEmpty() -> workPoints
      travelPoints.isNotEmpty() -> travelPoints
      else -> fallbackWaypoints
    }

    val selected = selectBestWaypoint(
      waypoints = candidates,
      avoidPlayers = avoidPlayers,
      avoidDangerEntities = avoidDangerEntities
    )

    return CommissionRouteChoice(
      routeName = route.name,
      source = source,
      mode = mode,
      region = region,
      travelPoints = travelPoints,
      workPoints = workPoints,
      selectedTarget = selected,
      shouldRunRouteModule = travelPoints.size + workPoints.size >= 2
    )
  }

  private fun selectBestWaypoint(
    waypoints: List<BlockPos>,
    avoidPlayers: Boolean,
    avoidDangerEntities: Boolean
  ): BlockPos? {
    if (waypoints.isEmpty()) return null

    val players = if (avoidPlayers) {
      WaypointScorer.collectNearbyPlayers()
    } else {
      emptyList()
    }

    val dangerEntities = if (avoidDangerEntities) {
      WaypointScorer.collectDangerEntities()
    } else {
      emptyList()
    }

    return WaypointScorer.bestWaypoint(
      waypoints = waypoints,
      avoidPoints = emptyList(),
      dangerEntities = dangerEntities,
      crowdingPlayers = players
    )
  }

  private fun loadAssignedRoute(slotKey: String): SavedRoute? {
    val routeName = RouteStore.getSlotRoute(slotKey)
      ?.trim()
      ?.takeIf { it.isNotEmpty() }
      ?: return null

    return RouteStore.loadAll().firstOrNull { route ->
      route.name.equals(routeName, ignoreCase = true)
    }
  }

  private fun getLoadedCommissionRoute(): SavedRoute? {
    val commissionType = findRouteType("COMMISSION") ?: return null
    return RouteStore.getLoaded(commissionType)
  }

  private fun findNamedRoute(
    commissionName: String,
    task: CommissionTask
  ): SavedRoute? {
    val wantedNames = buildList {
      add(commissionName)
      add(task.primaryName)
      addAll(task.names)
      add(task.regionCompat.displayName)
      add(task.regionCompat.slotKey)
      add(areaName(task))
      add(typeName(task))
    }
      .map(::normalizeName)
      .filter { it.isNotBlank() }
      .distinct()

    return RouteStore.loadAll().firstOrNull { route ->
      val routeName = normalizeName(route.name)

      wantedNames.any { wanted ->
        routeName == wanted ||
          routeName.contains(wanted) ||
          wanted.contains(routeName)
      }
    }
  }

  private fun extractTravelPoints(route: SavedRoute): List<BlockPos> {
    val source = when {
      route.travelRoute.isNotEmpty() -> route.travelRoute
      route.points.isNotEmpty() -> route.points
      else -> emptyList()
    }

    return source
      .filter { point ->
        point.type == RoutePointType.WALK ||
          point.type == RoutePointType.WARP
      }
      .map { it.pos }
      .distinct()
  }

  private fun extractWorkPoints(route: SavedRoute): List<BlockPos> {
    val source = when {
      route.loopOrArea.isNotEmpty() -> route.loopOrArea
      route.points.isNotEmpty() -> route.points
      else -> emptyList()
    }

    return source
      .filter { point ->
        point.type == RoutePointType.MINE ||
          point.type == RoutePointType.VEIN ||
          point.type == RoutePointType.KILL ||
          point.type == RoutePointType.WALK
      }
      .map { it.pos }
      .distinct()
  }

  private val RoutePoint.pos: BlockPos
    get() = BlockPos(x, y, z)

  private fun findRouteType(name: String): RouteType? {
    return RouteType.entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
  }

  private fun slotKey(prefix: String, key: String): String {
    return "$prefix:$key"
  }

  private fun regionSlotKey(region: CommissionRegion): String {
    return "commission-region:${region.slotKey}"
  }

  private fun areaSlotKey(task: CommissionTask): String {
    return slotKey("${task.regionCompat.slotKey}-commission-area", normalizeKey(areaName(task)))
  }

  private fun typeSlotKey(task: CommissionTask): String {
    return slotKey("${task.regionCompat.slotKey}-commission-type", normalizeKey(typeName(task)))
  }

  private fun areaName(task: CommissionTask): String {
    val name = task.primaryName.lowercase()

    return when (task.regionCompat) {
      CommissionRegion.DWARVEN -> when {
        "royal mines" in name -> "royal-mines"
        "cliffside veins" in name -> "cliffside-veins"
        "upper mines" in name -> "upper-mines"
        "rampart" in name -> "ramparts-quarry"
        "lava springs" in name -> "lava-springs"
        "goblin" in name -> "goblin"
        "glacite" in name || "ice walker" in name -> "glacite"
        "treasure" in name -> "treasure"
        else -> "general"
      }

      CommissionRegion.GLACITE -> when {
        "tungsten" in name -> "tungsten"
        "umber" in name -> "umber"
        "powder" in name -> "powder"
        "slayer" in name -> "slayer"
        "glacite" in name -> "glacite"
        else -> "general"
      }
    }
  }

  private fun typeName(task: CommissionTask): String {
    return when (task.type) {
      CommissionTaskType.MINING -> "mining"
      CommissionTaskType.SLAYER -> "slayer"
      else -> "general"
    }
  }

  private fun normalizeKey(value: String): String {
    return normalizeName(value)
      .replace(" ", "-")
      .replace(Regex("-+"), "-")
      .trim('-')
  }

  private fun normalizeName(value: String): String {
    return value
      .lowercase()
      .replace("&", " and ")
      .replace(Regex("[^a-z0-9 ]"), " ")
      .replace(Regex("\\s+"), " ")
      .trim()
  }
}

private val CommissionTask.regionCompat: CommissionRegion
  get() = when {
    names.any { name ->
      val value = name.lowercase()
      value.contains("glacite tunnel") ||
        value.contains("tungsten") ||
        value.contains("umber") ||
        (value.contains("glacite") && !value.contains("walker"))
    } -> CommissionRegion.GLACITE

    else -> CommissionRegion.DWARVEN
  }
