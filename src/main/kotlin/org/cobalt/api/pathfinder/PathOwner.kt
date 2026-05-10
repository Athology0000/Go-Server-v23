package org.cobalt.api.pathfinder

enum class PathOwner(val priority: Int) {
  NONE(0),
  USER(10),
  COMMISSION(20),
  FAILSAFE(100),
}
