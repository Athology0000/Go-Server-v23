package org.phantom.internal.mining.tunnels

import net.minecraft.core.BlockPos
import org.phantom.internal.routes.RoutePointType

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
  private val veinsByOre: Map<TunnelOreType, List<TunnelVein>> by lazy {
    TunnelOreType.entries.associateWith { ore -> toTunnelVeins(ore, dataFor(ore)) }
  }

  fun getVeins(selectedOres: Set<TunnelOreType>): List<TunnelVein> {
    val ores = selectedOres.ifEmpty { TunnelOreType.entries.toSet() }

    return buildList {
      for (ore in TunnelOreType.entries) {
        if (ore !in ores) continue
        addAll(veinsByOre[ore].orEmpty())
      }
    }
  }

  private fun dataFor(ore: TunnelOreType): Array<Array<IntArray>> =
    when (ore) {
      TunnelOreType.GLACITE -> GlaciteData.glacite
      TunnelOreType.TUNGSTEN -> GlaciteData.tungsten
      TunnelOreType.UMBER -> GlaciteData.umber
      TunnelOreType.PERIDOT -> GlaciteData.peridot
      TunnelOreType.AQUAMARINE -> GlaciteData.aquamarine
      TunnelOreType.ONYX -> GlaciteData.onyx
      TunnelOreType.CITRINE -> GlaciteData.citrine
    }

  private fun toTunnelVeins(
    ore: TunnelOreType,
    rawVeins: Array<Array<IntArray>>
  ): List<TunnelVein> =
    rawVeins.mapIndexed { index, rawBlocks ->
      TunnelVein(
        ore = ore,
        index = index,
        blocks = rawBlocks.mapNotNull { coords -> coords.toBlockPosOrNull() }
      )
    }.filter { it.blocks.isNotEmpty() }

  private fun IntArray.toBlockPosOrNull(): BlockPos? {
    if (size < 3) return null
    return BlockPos(this[0], this[1], this[2])
  }
}
