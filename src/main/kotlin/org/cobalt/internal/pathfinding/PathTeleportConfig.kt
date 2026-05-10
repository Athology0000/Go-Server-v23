package org.cobalt.internal.pathfinding

object PathTeleportConfig {
  var v5EtherwarpEnabled: Boolean = true
  var v5AotvEnabled: Boolean = true
  var minTeleportGain: Double = 12.0
  var finalNoTeleportRadius: Double = 18.0
  var maxLookAheadNodes: Int = 18
  var teleportCooldownTicks: Int = 10
  var teleportStraightnessTolerance: Double = 2.25
}
