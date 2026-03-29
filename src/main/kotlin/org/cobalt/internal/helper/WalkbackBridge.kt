package org.cobalt.internal.helper

/**
 * Decouples the slayer macro (combat package) from RoutesModule (mining package)
 * so that neither package needs to directly import the other.
 *
 * RoutesModule registers its functions here at startup.
 * CombatMacroModule calls them through this bridge.
 */
object WalkbackBridge {
  var startWalkback: ((name: String, fromEnd: Int, reverse: Boolean) -> Boolean)? = null
  var stopWalkback: (() -> Unit)? = null
  var isRunning: (() -> Boolean)? = null
  /** Returns the first waypoint of the walkback route (i.e., the last point of the stored route,
   *  since walkback reverses it). Null if the route cannot be loaded. */
  var getRouteEndPos: ((name: String) -> net.minecraft.core.BlockPos?)? = null
}
