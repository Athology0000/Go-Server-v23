package org.phantom.api.util.player

enum class MovementOwner(val priority: Int) {
  NONE(0),
  PATHFINDER(10),
  COMBAT(15),
  RECOVERY(20),
  FAILSAFE(100),
}
