package org.phantom.internal.mining.gemstone

import net.minecraft.core.BlockPos
import org.phantom.internal.routes.RoutePointType

enum class GemstoneType(
  val displayName: String,
  val miningTypeName: String,
  val keywords: Set<String>
) {
  RUBY("Ruby", "ruby", setOf("ruby", "red_stained_glass", "red stained glass", "red_glass")),
  AMBER("Amber", "amber", setOf("amber", "orange_stained_glass", "orange stained glass", "orange_glass")),
  SAPPHIRE("Sapphire", "sapphire", setOf("sapphire", "blue_stained_glass", "blue stained glass", "blue_glass")),
  JADE("Jade", "jade", setOf("jade", "lime_stained_glass", "lime stained glass", "green_stained_glass")),
  AMETHYST("Amethyst", "amethyst", setOf("amethyst", "purple_stained_glass", "purple stained glass", "purple_glass")),
  TOPAZ("Topaz", "topaz", setOf("topaz", "yellow_stained_glass", "yellow stained glass", "yellow_glass")),
  JASPER("Jasper", "jasper", setOf("jasper", "magenta_stained_glass", "pink_stained_glass")),
  OPAL("Opal", "opal", setOf("opal", "white_stained_glass", "white stained glass", "white_glass"));
}

data class GemstoneRoutePoint(
  val name: String = "point",
  val standPos: BlockPos,
  val lookPos: BlockPos,
  val gemstoneTypes: Set<GemstoneType> = GemstoneType.entries.toSet(),
  val enabled: Boolean = true,
  val pointType: RoutePointType = RoutePointType.MINE
)

data class LoadedGemstoneRoute(
  val routeName: String,
  val points: List<GemstoneRoutePoint>
) {
  val isValid: Boolean get() = routeName.isNotBlank() && points.isNotEmpty()
}

object GemstoneRouteData {
  fun allEnabled(): List<GemstoneRoutePoint> = emptyList()
  fun routeFor(types: Set<GemstoneType>): List<GemstoneRoutePoint> = allEnabled().filter { point ->
    types.isEmpty() || point.gemstoneTypes.any(types::contains)
  }
}
