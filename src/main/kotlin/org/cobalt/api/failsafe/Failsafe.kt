package org.cobalt.api.failsafe

interface Failsafe {
  val name: String
  val enabled: Boolean
    get() = true

  fun shouldCheck(): Boolean = enabled
  fun tick() {}
  fun reset() {}
}
