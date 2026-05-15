package org.phantom.api.pathfinder

enum class PathFailReason {
  OWNER_PREEMPTED,
  NO_PLAYER,
  NO_WORLD,
  TIMEOUT,
  PATHFINDER_FAILED,
  UNKNOWN,
  USER_CANCELLED,
  WORLD_CHANGED,
}
