package org.cobalt.internal.mining.tunnels

import net.minecraft.core.BlockPos
import org.cobalt.internal.mining.MiningBlockRegistry
import org.cobalt.internal.routes.RoutePointType

enum class TunnelOreType(
  val displayName: String,
  val miningTypeName: String,
  val keywords: Set<String>
) {
  GLACITE("Glacite", "glacite", setOf("glacite", "packed_ice", "blue_ice")),
  TUNGSTEN("Tungsten", "tungsten", tunnelOreKeywords("Tungsten", "tungsten")),
  UMBER("Umber", "umber", tunnelOreKeywords("Umber", "umber")),
  PERIDOT("Peridot", "peridot", setOf("peridot", "green")),
  AQUAMARINE("Aquamarine", "aquamarine", setOf("aquamarine", "cyan")),
  ONYX("Onyx", "onyx", setOf("onyx", "black")),
  CITRINE("Citrine", "citrine", setOf("citrine", "yellow"));
}

private fun tunnelOreKeywords(type: String, vararg aliases: String): Set<String> =
  linkedSetOf<String>().apply {
    addAll(aliases.map { it.lowercase() })
    MiningBlockRegistry.familyForType(type)?.blockIds.orEmpty().forEach { blockId ->
      add(blockId.lowercase())
      add(blockId.substringAfter(':').lowercase())
    }
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
