package org.cobalt.api.pathfinder

data class PathRequest(
  val x: Double,
  val y: Double,
  val z: Double,
  val owner: PathOwner,
  val source: String,
  val timeoutTicks: Int = 6000,
  val arrivalRadius: Double = 1.8,
  val onArrive: () -> Unit = {},
  val onFail: (PathFailReason) -> Unit = {}
)