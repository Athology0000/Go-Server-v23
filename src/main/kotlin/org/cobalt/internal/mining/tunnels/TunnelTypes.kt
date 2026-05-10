package org.cobalt.internal.mining.tunnels

import net.minecraft.core.BlockPos
import org.cobalt.internal.routes.RoutePointType

enum class TunnelOreType(
  val displayName: String,
  val miningTypeName: String,
  val keywords: Set<String>
) {
  GLACITE("Glacite", "glacite", setOf("glacite", "packed_ice", "blue_ice")),
  TUNGSTEN("Tungsten", "tungsten", setOf("tungsten")),
  UMBER("Umber", "umber", setOf("umber")),
  PERIDOT("Peridot", "peridot", setOf("peridot", "green")),
  AQUAMARINE("Aquamarine", "aquamarine", setOf("aquamarine", "cyan")),
  ONYX("Onyx", "onyx", setOf("onyx", "black")),
  CITRINE("Citrine", "citrine", setOf("citrine", "yellow"));
}

data class TunnelRoutePoint(
  val name: String = "point",
  val standPos: BlockPos,
  val veinPos: BlockPos,
  val oreTypes: Set<TunnelOreType> = TunnelOreType.entries.toSet(),
  val enabled: Boolean = true,
  val pointType: RoutePointType = RoutePointType.MINE,
  val blockId: String? = null
)

data class LoadedTunnelRoute(
  val routeName: String,
  val points: List<TunnelRoutePoint>
) {
  val isValid: Boolean get() = routeName.isNotBlank() && points.isNotEmpty()
}

data class TunnelVein(
  val ore: TunnelOreType,
  val index: Int,
  val blocks: List<BlockPos>
)

object TunnelVeinData {
  fun getVeins(selectedOres: Set<TunnelOreType>): List<TunnelVein> = emptyList()
}
