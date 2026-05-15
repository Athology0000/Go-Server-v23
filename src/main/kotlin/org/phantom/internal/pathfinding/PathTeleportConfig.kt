package org.phantom.internal.pathfinding

object PathTeleportConfig {
  var v5EtherwarpEnabled: Boolean = true
  var v5AotvEnabled: Boolean = true
  var minTeleportGain: Double = 6.0
  var finalNoTeleportRadius: Double = 4.0
  var maxLookAheadNodes: Int = 24
  var teleportCooldownTicks: Int = 1
  var teleportStraightnessTolerance: Double = 1.8
}
