package org.phantom.internal.mining.failsafe

import net.minecraft.core.BlockPos
import org.phantom.api.failsafe.FailsafeManager

/**
 * Backwards-compatible facade for older mining code.
 *
 * New failsafes should register with org.phantom.api.failsafe.FailsafeManager.
 */
object MiningFailsafeManager {
  enum class Severity {
    INFO,
    WARNING,
    CRITICAL,
  }

  data class FailsafeState(
    val blocked: Boolean,
    val severity: Severity,
    val reason: String,
    val lastPlayerPos: BlockPos?,
    val stationaryTicks: Int,
  )

  fun tick(requireInWorld: Boolean = true, pauseOnScreen: Boolean = true): FailsafeState {
    return FailsafeManager.tick(requireInWorld, pauseOnScreen).toMiningState()
  }

  fun block(reason: String, severity: Severity = Severity.WARNING): FailsafeState {
    return FailsafeManager.block(reason, severity.toGlobal(), "mining").toMiningState()
  }

  fun shouldPause(): Boolean = FailsafeManager.shouldPause()

  fun snapshot(): FailsafeState = FailsafeManager.snapshot().toMiningState()

  private fun FailsafeManager.FailsafeState.toMiningState(): FailsafeState {
    return FailsafeState(
      blocked = blocked,
      severity = severity.toMining(),
      reason = reason,
      lastPlayerPos = lastPlayerPos,
      stationaryTicks = stationaryTicks,
    )
  }

  private fun Severity.toGlobal(): FailsafeManager.Severity {
    return when (this) {
      Severity.INFO -> FailsafeManager.Severity.INFO
      Severity.WARNING -> FailsafeManager.Severity.WARNING
      Severity.CRITICAL -> FailsafeManager.Severity.CRITICAL
    }
  }

  private fun FailsafeManager.Severity.toMining(): Severity {
    return when (this) {
      FailsafeManager.Severity.INFO -> Severity.INFO
      FailsafeManager.Severity.WARNING -> Severity.WARNING
      FailsafeManager.Severity.CRITICAL -> Severity.CRITICAL
    }
  }
}
