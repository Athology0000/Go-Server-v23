package org.cobalt.internal.helper

/**
 * Decouples the slayer macro (combat package) from RoutesModule (mining package)
 * so that neither package needs to directly import the other.
 *
 * RoutesModule registers its functions here at startup.
 * CombatMacroModule calls them through this bridge.
 */
object WalkbackBridge {
  var startWalkback: ((name: String, fromEnd: Int) -> Boolean)? = null
  var isRunning: (() -> Boolean)? = null
}
