package org.phantom.internal.pathfinding

object PathTeleportConfig {
  var v5EtherwarpEnabled: Boolean = true
  var v5AotvEnabled: Boolean = true
  var minTeleportGain: Double = 6.0
  var finalNoTeleportRadius: Double = 4.0
  var maxLookAheadNodes: Int = 24
  var teleportCooldownTicks: Int = 1
  var teleportStraightnessTolerance: Double = 1.8

  // Hybrid etherwarp-chain shortcut (goto only). When a walking path is a big
  // detour relative to the straight-line distance to the goal, try a dedicated
  // multi-hop etherwarp chain instead.
  var chainShortcutEnabled: Boolean = true
  // Minimum straight-line distance (blocks) from start to goal before a chain
  // is even considered. Keeps short hops on the normal walker and prevents the
  // post-chain residual approach from re-triggering a chain.
  var chainMinStraightLine: Double = 14.0
  // Walk-path length must exceed straightLine * this ratio to count as a detour.
  var chainDetourRatio: Double = 1.6
}
