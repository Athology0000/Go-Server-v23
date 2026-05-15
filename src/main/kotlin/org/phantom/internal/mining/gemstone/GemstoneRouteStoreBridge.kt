package org.phantom.internal.mining.gemstone

import org.phantom.internal.routes.RoutePoint
import org.phantom.internal.routes.RouteStore
import org.phantom.internal.routes.RouteType

/**
 * Loads the active RouteType.GEMSTONE route from RouteStore and converts it to
 * the compact format consumed by GemstoneMinerModule.
 *
 * Route priority:
 * 1. routeName parameter (from GemstoneMinerModule's Route Name setting)
 * 2. /pathroute gemstone <name> assignment (slot key: gemstone-route)
 * 3. RouteStore loaded route for RouteType.GEMSTONE
 * 4. First saved RouteType.GEMSTONE route on disk
 */
object GemstoneRouteStoreBridge {

  private const val GEMSTONE_SLOT_KEY = "gemstone-route"

  fun loadActiveGemstoneRoute(
    fallbackTypes: Set<GemstoneType> = GemstoneType.entries.toSet(),
    routeName: String? = null
  ): LoadedGemstoneRoute? {
    val routes = RouteStore.listByType(RouteType.GEMSTONE)
    if (routes.isEmpty()) return null

    val requestedName = routeName?.takeIf { it.isNotEmpty() }
      ?: RouteStore.getSlotRoute(GEMSTONE_SLOT_KEY)
      ?: RouteStore.getLoadedName(RouteType.GEMSTONE)

    val route = when {
      !requestedName.isNullOrBlank() ->
        routes.firstOrNull { it.name.equals(requestedName, ignoreCase = true) }
      else -> null
    } ?: RouteStore.getLoaded(RouteType.GEMSTONE)
      ?: routes.firstOrNull()
      ?: return null

    val points = route.points.mapIndexed { index, point ->
      point.toGemstonePoint(index, fallbackTypes)
    }

    return if (points.isEmpty()) null else LoadedGemstoneRoute(route.name, points)
  }

  fun routeNames(): List<String> = RouteStore.listByType(RouteType.GEMSTONE).map { it.name }

  private fun RoutePoint.toGemstonePoint(
    index: Int,
    fallbackTypes: Set<GemstoneType>
  ): GemstoneRoutePoint {
    return GemstoneRoutePoint(
      name = "${type.id}#$index",
      standPos = pos,
      lookPos = mineEnd ?: pos,
      gemstoneTypes = typesFor(blockId, fallbackTypes),
      enabled = true,
      pointType = type
    )
  }

  private fun typesFor(
    blockId: String?,
    fallbackTypes: Set<GemstoneType>
  ): Set<GemstoneType> {
    val text = blockId.orEmpty().lowercase()
    val fromBlockId = GemstoneType.entries.filter { type ->
      type.keywords.any { keyword -> text.contains(keyword.lowercase()) }
    }.toSet()
    if (fromBlockId.isNotEmpty()) return fromBlockId
    if (fallbackTypes.isNotEmpty()) return fallbackTypes
    return GemstoneType.entries.toSet()
  }
}
