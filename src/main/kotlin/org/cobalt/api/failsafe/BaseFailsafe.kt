package org.cobalt.api.failsafe

import org.cobalt.api.macro.MacroState
import kotlin.math.max

abstract class BaseFailsafe : Failsafe {
  private var disabledUntilMs: Long = 0L

  fun temporarilyDisable(durationMs: Long) {
    disabledUntilMs = max(disabledUntilMs, System.currentTimeMillis() + durationMs.coerceAtLeast(0L))
  }

  fun isTemporarilyDisabled(): Boolean {
    return System.currentTimeMillis() < disabledUntilMs
  }

  override fun shouldCheck(): Boolean {
    return enabled && !isTemporarilyDisabled() && MacroState.isFailsafeMacroRunning()
  }
}
