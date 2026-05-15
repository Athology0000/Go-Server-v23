package org.phantom.internal.mining.supervisor

object MiningMacroSupervisor {
  enum class MacroKind {
    NONE,
    MINING_BOT,
    COMMISSION,
    GEMSTONE,
    GLACITE_COMMISSION,
    TUNNELS,
    POWDER,
    SCATHA,
    ORE,
    EXCAVATOR,
    LOBBY_HOPPER,
    PINGLESS_MINING,
  }

  data class Snapshot(
    val active: MacroKind,
    val enabled: Boolean,
    val startedAtMs: Long,
    val lastReason: String,
  ) {
    val runningForMs: Long
      get() = if (!enabled || startedAtMs <= 0L) 0L else System.currentTimeMillis() - startedAtMs
  }

  private var activeKind: MacroKind = MacroKind.NONE
  private var activeSinceMs: Long = 0L
  private var enabled = false
  private var reason = "idle"

  fun start(kind: MacroKind, reason: String = "manual") {
    if (activeKind != kind || !enabled) {
      activeSinceMs = System.currentTimeMillis()
    }
    activeKind = kind
    enabled = kind != MacroKind.NONE
    this.reason = reason
  }

  fun stop(reason: String = "stopped") {
    activeKind = MacroKind.NONE
    activeSinceMs = 0L
    enabled = false
    this.reason = reason
  }

  fun isActive(kind: MacroKind): Boolean {
    return enabled && activeKind == kind
  }

  fun snapshot(): Snapshot {
    return Snapshot(
      active = activeKind,
      enabled = enabled,
      startedAtMs = activeSinceMs,
      lastReason = reason,
    )
  }
}
