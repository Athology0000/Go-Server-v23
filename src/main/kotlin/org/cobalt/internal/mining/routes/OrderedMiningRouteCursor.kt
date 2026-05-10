package org.cobalt.internal.mining.routes

import net.minecraft.core.BlockPos

object OrderedMiningRouteCursor {
  enum class RouteKind {
    TUNNEL,
    GEMSTONE,
  }

  data class CursorState(
    val kind: RouteKind,
    val routeName: String,
    val nextIndex: Int,
    val failedIndices: Set<Int> = emptySet(),
  )

  private val states = mutableMapOf<String, MutableState>()

  fun state(kind: RouteKind, routeName: String): CursorState? {
    val state = states[key(kind, routeName)] ?: return null
    return state.toPublic(kind, routeName)
  }

  fun chooseNearestIndex(
    kind: RouteKind,
    routeName: String,
    points: List<BlockPos>,
    playerPos: BlockPos,
  ): Int {
    val index = points
      .indices
      .minByOrNull { points[it].distManhattan(playerPos) }
      ?: 0
    states[key(kind, routeName)] = MutableState(nextIndex = index)
    return index
  }

  fun nextIndex(
    kind: RouteKind,
    routeName: String,
    routeSize: Int,
    allowFailed: Boolean = true,
  ): Int {
    if (routeSize <= 0) return -1
    val state = states.getOrPut(key(kind, routeName)) { MutableState() }
    repeat(routeSize) { offset ->
      val index = (state.nextIndex + offset).floorMod(routeSize)
      if (allowFailed || !state.failedIndices.contains(index)) return index
    }
    return state.nextIndex.floorMod(routeSize)
  }

  fun markCompleted(kind: RouteKind, routeName: String, routeSize: Int, index: Int) {
    if (routeSize <= 0) return
    val state = states.getOrPut(key(kind, routeName)) { MutableState() }
    state.failedIndices.remove(index)
    state.nextIndex = (index + 1).floorMod(routeSize)
  }

  fun markFailed(kind: RouteKind, routeName: String, routeSize: Int, index: Int) {
    if (routeSize <= 0) return
    val state = states.getOrPut(key(kind, routeName)) { MutableState() }
    state.failedIndices.add(index.floorMod(routeSize))
    state.nextIndex = (index + 1).floorMod(routeSize)
  }

  fun reset(kind: RouteKind? = null, routeName: String? = null) {
    states.keys
      .filter { key ->
        (kind == null || key.startsWith("${kind.name}:")) &&
          (routeName == null || key.endsWith(":$routeName"))
      }
      .forEach(states::remove)
  }

  private fun key(kind: RouteKind, routeName: String): String = "${kind.name}:$routeName"

  private fun Int.floorMod(mod: Int): Int = Math.floorMod(this, mod)

  private data class MutableState(
    var nextIndex: Int = 0,
    val failedIndices: MutableSet<Int> = mutableSetOf(),
  ) {
    fun toPublic(kind: RouteKind, routeName: String): CursorState =
      CursorState(kind, routeName, nextIndex, failedIndices.toSet())
  }
}
