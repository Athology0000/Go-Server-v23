package org.cobalt.internal.mining.tunnels

import net.minecraft.core.BlockPos
import org.cobalt.internal.routes.RoutePoint
import org.cobalt.internal.routes.RoutePointType
import org.cobalt.internal.routes.RouteStore
import org.cobalt.internal.routes.RouteType

/**
 * Converts Cobalt saved Tunnel routes into the compact route format consumed by TunnelMiner.
 *
 * Route input priority:
 * 1. explicitRouteName passed by TunnelMiner's Route Name setting
 * 2. /pathroute tunnel <routeName> assignment, slot key: tunnel-route
 * 3. RouteStore loaded route for RouteType.TUNNEL from the Routes UI
 * 4. first saved RouteType.TUNNEL route on disk
 */
object TunnelRouteStoreBridge {

  private const val TUNNEL_SLOT_KEY = "tunnel-route"

  fun loadActiveTunnelRoute(
    fallbackOres: Set<TunnelOreType> = TunnelOreType.entries.toSet(),
    explicitRouteName: String? = null
  ): LoadedTunnelRoute? {
    val routes = RouteStore.listByType(RouteType.TUNNEL)
    if (routes.isEmpty()) return null

    val requestedName = explicitRouteName
      ?.trim()
      ?.takeIf { it.isNotEmpty() }
      ?: RouteStore.getSlotRoute(TUNNEL_SLOT_KEY)
      ?: RouteStore.getLoadedName(RouteType.TUNNEL)

    val route = when {
      !requestedName.isNullOrBlank() -> routes.firstOrNull { it.name.equals(requestedName, ignoreCase = true) }
      else -> null
    } ?: RouteStore.getLoaded(RouteType.TUNNEL)
      ?: routes.firstOrNull()
      ?: return null

    val points = route.points.mapIndexedNotNull { index, point ->
      point.toTunnelPoint(index, fallbackOres)
    }

    return LoadedTunnelRoute(
      routeName = route.name,
      points = points
    )
  }

  fun routeNames(): List<String> = RouteStore.listByType(RouteType.TUNNEL).map { it.name }

  private fun RoutePoint.toTunnelPoint(
    index: Int,
    fallbackOres: Set<TunnelOreType>
  ): TunnelRoutePoint? {
    val ores = oresFor(blockId, fallbackOres)
    val stand = pos
    val mine = mineEnd ?: pos

    return TunnelRoutePoint(
      name = "${type.id}#$index",
      standPos = stand,
      veinPos = mine,
      oreTypes = ores,
      enabled = true,
      pointType = type,
      blockId = blockId
    )
  }

  private fun oresFor(
    blockId: String?,
    fallbackOres: Set<TunnelOreType>
  ): Set<TunnelOreType> {
    val text = blockId.orEmpty().lowercase()
    val fromBlockId = TunnelOreType.entries.filter { ore ->
      ore.keywords.any { keyword -> text.contains(keyword.lowercase()) }
    }.toSet()

    if (fromBlockId.isNotEmpty()) return fromBlockId
    if (fallbackOres.isNotEmpty()) return fallbackOres
    return TunnelOreType.entries.toSet()
  }
}
