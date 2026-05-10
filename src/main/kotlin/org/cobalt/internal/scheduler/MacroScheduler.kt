package org.cobalt.internal.scheduler

import org.cobalt.api.macro.MacroState

/** Lightweight V5-style scheduler state shell. Wire this to GUI controls/macros incrementally. */
object MacroScheduler {
  enum class State {
    IDLE,
    RUNNING,
    RESTING,
    RETURNING,
  }

  @Volatile
  var state: State = State.IDLE
    private set

  private val trackedMacros = linkedSetOf<String>()

  @Volatile
  var timerEndMs: Long = 0L
    private set

  fun track(moduleName: String) {
    trackedMacros.add(moduleName)
  }

  fun untrack(moduleName: String) {
    trackedMacros.remove(moduleName)
  }

  fun trackedMacroNames(): List<String> = trackedMacros.toList()

  fun beginSession(durationMs: Long = 0L) {
    state = State.RUNNING
    timerEndMs = if (durationMs > 0L) System.currentTimeMillis() + durationMs else 0L
  }

  fun beginRest(durationMs: Long) {
    state = State.RESTING
    timerEndMs = System.currentTimeMillis() + durationMs.coerceAtLeast(0L)
    MacroState.stopAll(MacroState.ToggleContext.SCHEDULER, "scheduler rest")
  }

  fun beginReturn() {
    state = State.RETURNING
  }

  fun endSession() {
    state = State.IDLE
    timerEndMs = 0L
    MacroState.stopAll(MacroState.ToggleContext.SCHEDULER, "scheduler stopped")
  }

  fun isTimerFinished(nowMs: Long = System.currentTimeMillis()): Boolean {
    return timerEndMs > 0L && nowMs >= timerEndMs
  }
}
