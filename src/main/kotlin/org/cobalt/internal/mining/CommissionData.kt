package org.cobalt.internal.mining

import net.minecraft.core.BlockPos

enum class CommissionTaskType {
  MINING,
  SLAYER,
}

data class CommissionTask(
  val names: List<String>,
  val type: CommissionTaskType,
  val cost: Int,
  val waypoints: List<BlockPos>,
  val useAllMiningWaypoints: Boolean = false,
) {
  val primaryName: String get() = names.firstOrNull().orEmpty()
}

data class MobConfig(
  val names: List<String>,
)

object CommissionData {
  val emissaryLocations = listOf(
    BlockPos(129, 195, 196), // King. Keep first, matching RDBT V5.
    BlockPos(42, 134, 22),
    BlockPos(171, 149, 31),
    BlockPos(-73, 152, -11),
    BlockPos(-133, 173, -51),
    BlockPos(-38, 199, -132),
    BlockPos(58, 197, -9),
  )

  val trashItems = listOf("Mithril", "Titanium", "Rune", "Glacite", "Goblin", "Cobblestone", "Stone")

  val mobConfigs = mapOf(
    "goblin" to MobConfig(listOf("Goblin", "Weakling", "Knifethrower", "Fireslinger")),
    "icewalker" to MobConfig(listOf("Ice Walker", "Glacite Walker")),
    "treasure" to MobConfig(listOf("Treasuer Hunter", "Treasure Hunter")),
  )

  val commissionData = listOf(
    CommissionTask(
      names = listOf("Royal Mines Mithril", "Royal Mines Titanium", "Royal Mines Mithril Miner"),
      type = CommissionTaskType.MINING,
      cost = 5,
      waypoints = listOf(
        BlockPos(141, 151, 24),
        BlockPos(173, 149, 70),
        BlockPos(166, 148, 90),
      ),
    ),
    CommissionTask(
      names = listOf("Cliffside Veins Mithril", "Cliffside Veins Titanium", "Cliffside Veins Mithril Miner"),
      type = CommissionTaskType.MINING,
      cost = 10,
      waypoints = listOf(
        BlockPos(46, 134, 11),
        BlockPos(25, 128, 27),
        BlockPos(10, 127, 37),
      ),
    ),
    CommissionTask(
      names = listOf("Upper Mines Mithril", "Upper Mines Titanium", "Upper Mines Mithril Miner"),
      type = CommissionTaskType.MINING,
      cost = 15,
      waypoints = listOf(
        BlockPos(-113, 166, -75),
        BlockPos(-125, 170, -76),
        BlockPos(-78, 187, -74),
      ),
    ),
    CommissionTask(
      names = listOf("Rampart's Quarry Mithril", "Rampart's Quarry Titanium", "Rampart's Quarry Mithril Miner"),
      type = CommissionTaskType.MINING,
      cost = 15,
      waypoints = listOf(
        BlockPos(-88, 146, 29),
        BlockPos(-87, 146, -14),
        BlockPos(-118, 149, -31),
      ),
    ),
    CommissionTask(
      names = listOf("Lava Springs Mithril", "Lava Springs Titanium", "Lava Springs Mithril Miner"),
      type = CommissionTaskType.MINING,
      cost = 20,
      waypoints = listOf(
        BlockPos(50, 197, -26),
        BlockPos(42, 197, -20),
      ),
    ),
    CommissionTask(
      names = listOf("Titanium Miner", "Mithril Miner"),
      type = CommissionTaskType.MINING,
      cost = 12,
      waypoints = emptyList(),
      useAllMiningWaypoints = true,
    ),
    CommissionTask(
      names = listOf("Goblin Slayer"),
      type = CommissionTaskType.SLAYER,
      cost = 30,
      waypoints = listOf(BlockPos(-130, 145, 147)),
    ),
    CommissionTask(
      names = listOf("Glacite Walker Slayer", "Mines Slayer"),
      type = CommissionTaskType.SLAYER,
      cost = 25,
      waypoints = listOf(BlockPos(0, 127, 157)),
    ),
    CommissionTask(
      names = listOf("Treasure Hoarder Puncher"),
      type = CommissionTaskType.SLAYER,
      cost = 25,
      waypoints = listOf(BlockPos(-117, 204, -56)),
    ),
  )

  fun resolveTask(name: String): CommissionTask? =
    commissionData.firstOrNull { task -> task.names.any { it.equals(name, ignoreCase = true) } }

  fun miningWaypoints(): List<BlockPos> =
    commissionData
      .filter { it.type == CommissionTaskType.MINING && !it.useAllMiningWaypoints }
      .flatMap { it.waypoints }
}
