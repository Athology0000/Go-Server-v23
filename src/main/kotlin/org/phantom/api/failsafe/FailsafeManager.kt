package org.phantom.api.failsafe

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos

/**
 * Global failsafe framework. MiningFailsafeManager can stay as a compatibility
 * wrapper, but new macros should register focused failsafes here instead of
 * growing one mining-only class forever.
 */
object FailsafeManager {
  enum class Severity {
    INFO,
    WARNING,
    CRITICAL,
  }

  data class FailsafeState(
    val blocked: Boolean,
    val severity: Severity,
    val reason: String,
    val source: String,
    val lastPlayerPos: BlockPos?,
    val stationaryTicks: Int,
  )

  private val mc: Minecraft = Minecraft.getInstance()
  private val failsafes = mutableListOf<Failsafe>()

  private var lastPlayerPos: BlockPos? = null
  private var stationaryTicks: Int = 0
  private var blocked: Boolean = false
  private var lastReason: String = "ok"
  private var lastSource: String = "core"
  private var lastSeverity: Severity = Severity.INFO

  @Synchronized
  fun register(failsafe: Failsafe) {
    if (failsafes.none { it.name.equals(failsafe.name, ignoreCase = true) }) {
      failsafes.add(failsafe)
    }
  }

  @Synchronized
  fun unregister(failsafe: Failsafe) {
    failsafes.removeIf { it === failsafe || it.name.equals(failsafe.name, ignoreCase = true) }
  }

  @Synchronized
  fun tick(requireInWorld: Boolean = true, pauseOnScreen: Boolean = true): FailsafeState {
    val player = mc.player
    val level = mc.level

    if (requireInWorld && (player == null || level == null)) {
      return block("not in world", Severity.CRITICAL, "core")
    }

    if (pauseOnScreen && mc.screen != null) {
      return block("screen open", Severity.WARNING, "core")
    }

    val pos = player?.blockPosition()
    if (pos != null) {
      stationaryTicks = if (pos == lastPlayerPos) stationaryTicks + 1 else 0
      lastPlayerPos = pos
    }

    blocked = false
    lastReason = "ok"
    lastSeverity = Severity.INFO
    lastSource = "core"

    failsafes.forEach { failsafe ->
      if (failsafe.shouldCheck()) failsafe.tick()
    }

    return snapshot()
  }

  @Synchronized
  fun block(
    reason: String,
    severity: Severity = Severity.WARNING,
    source: String = "external",
  ): FailsafeState {
    blocked = true
    lastReason = reason
    lastSeverity = severity
    lastSource = source
    return snapshot()
  }

  fun shouldPause(): Boolean = blocked

  fun disableAllFor(durationMs: Long) {
    failsafes.forEach { if (it is BaseFailsafe) it.temporarilyDisable(durationMs) }
  }

  @Synchronized
  fun resetAll() {
    failsafes.forEach { it.reset() }
    blocked = false
    lastReason = "ok"
    lastSeverity = Severity.INFO
    lastSource = "core"
    stationaryTicks = 0
    lastPlayerPos = null
  }

  @Synchronized
  fun snapshot(): FailsafeState {
    return FailsafeState(
      blocked = blocked,
      severity = lastSeverity,
      reason = lastReason,
      source = lastSource,
      lastPlayerPos = lastPlayerPos,
      stationaryTicks = stationaryTicks,
    )
  }
}
