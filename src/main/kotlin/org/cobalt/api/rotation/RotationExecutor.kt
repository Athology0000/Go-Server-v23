package org.cobalt.api.rotation

import kotlin.math.roundToInt
import net.minecraft.client.Minecraft
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.render.WorldRenderEvent
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.helper.Rotation
import org.cobalt.internal.pathfinding.DebugLog

object RotationExecutor {

  private val mc: Minecraft =
    Minecraft.getInstance()

  private const val DEBUG_TICK_FILE = true
  private const val DEBUG_TICK_INTERVAL = 5L

  private var targetYaw: Float = 0F
  private var targetPitch: Float = 0F

  private var currStrat: IRotationStrategy? = null
  private var isRotating: Boolean = false
  private var currentOwnerName: String? = null
  private var lastConflictWarnTick: Long = Long.MIN_VALUE

  fun rotateTo(
    endRot: Rotation,
    strategy: IRotationStrategy,
  ) {
    if (isRotating && currStrat === strategy) {
      targetYaw = endRot.yaw
      targetPitch = endRot.pitch
      return
    }

    if (isRotating && currStrat !== strategy) {
      warnRotationConflict(strategy)
      return
    }

    targetYaw = endRot.yaw
    targetPitch = endRot.pitch
    currStrat = strategy
    currentOwnerName = strategyName(strategy)

    strategy.onStart()
    isRotating = true
  }

  fun stopRotating() {
    currStrat?.onStop()
    currStrat = null
    isRotating = false
    currentOwnerName = null
  }

  fun stopIfUsing(strategy: IRotationStrategy) {
    if (currStrat === strategy) {
      stopRotating()
    }
  }

  fun isUsing(strategy: IRotationStrategy): Boolean = currStrat === strategy

  fun currentOwner(): String? = if (isRotating) currentOwnerName else null

  fun isRotating(): Boolean {
    return isRotating
  }

  private fun warnRotationConflict(requestedStrategy: IRotationStrategy) {
    val nowTick = mc.level?.gameTime ?: return
    if (nowTick - lastConflictWarnTick < CONFLICT_WARN_INTERVAL_TICKS) return
    lastConflictWarnTick = nowTick

    val active = currentOwnerName ?: strategyName(currStrat)
    val requested = strategyName(requestedStrategy)
    ChatUtils.sendMessage("Rotation failsafe: $requested tried to rotate while $active is active.")
  }

  private fun strategyName(strategy: IRotationStrategy?): String {
    if (strategy == null) return "unknown"
    return strategy::class.simpleName
      ?.takeIf { it.isNotBlank() }
      ?: strategy.javaClass.simpleName.takeIf { it.isNotBlank() }
      ?: strategy.javaClass.name.substringAfterLast('.')
  }

  @SubscribeEvent
  fun onRotate(
    event: WorldRenderEvent.Last,
  ) {
    val player = mc.player ?: return

    if (!isRotating) {
      if (DEBUG_TICK_FILE && DebugLog.debugFileEnabled) {
        mc.level?.let { level ->
          if (level.gameTime % DEBUG_TICK_INTERVAL == 0L) {
            DebugLog.debugTickFile(
              mc,
              "Rotation",
              "idle yaw=${"%.2f".format(player.yRot)} pitch=${"%.2f".format(player.xRot)}",
              level.gameTime
            )
          }
        }
      }
      return
    }

    currStrat?.let {
      val result = it.onRotate(
        player,
        targetYaw,
        targetPitch
      )

      if (result == null) {
        stopRotating()
      } else {
        if (DEBUG_TICK_FILE && DebugLog.debugFileEnabled) {
          mc.level?.let { level ->
            if (level.gameTime % DEBUG_TICK_INTERVAL == 0L) {
              DebugLog.debugTickFile(
                mc,
                "Rotation",
                "yaw=${"%.2f".format(player.yRot)}->${"%.2f".format(result.yaw)} " +
                  "pitch=${"%.2f".format(player.xRot)}->${"%.2f".format(result.pitch)} " +
                  "target=${"%.2f".format(targetYaw)}/${"%.2f".format(targetPitch)}",
                level.gameTime
              )
            }
          }
        }
        player.setYRot(AngleUtils.normalizeAngle(applyGCD(result.yaw, player.yRot)))
        player.setXRot(applyGCD(result.pitch, player.xRot, -90f, 90f).coerceIn(-90f, 90f))
      }
    }
  }

  private fun applyGCD(rotation: Float, prevRotation: Float, min: Float? = null, max: Float? = null): Float {
    if (rotation.isNaN() || prevRotation.isNaN()) {
      return prevRotation
    }
    val sensitivity = mc.options.sensitivity().get()
    if (sensitivity.isNaN()) {
      return prevRotation
    }
    val f = sensitivity * 0.6 + 0.2
    val gcd = f * f * f * 1.2

    val delta = AngleUtils.getRotationDelta(prevRotation, rotation)
    if (delta.isNaN() || gcd == 0.0 || gcd.isNaN()) {
      return prevRotation
    }
    val scaled = delta / gcd
    if (scaled.isNaN() || scaled.isInfinite()) {
      return prevRotation
    }
    val roundedDelta = scaled.roundToInt() * gcd
    var result = prevRotation + roundedDelta

    if (max != null && result > max) {
      result -= gcd
    }
    if (min != null && result < min) {
      result += gcd
    }

    return result.toFloat()
  }

  private const val CONFLICT_WARN_INTERVAL_TICKS = 20L

}
