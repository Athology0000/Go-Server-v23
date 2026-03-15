package org.cobalt.api.rotation.strategy

import net.minecraft.client.player.LocalPlayer
import org.cobalt.api.rotation.IRotationStrategy
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.helper.Rotation
import org.cobalt.internal.pathfinding.HeadRotationModule

/**
 * Rotation strategy that uses HeadRotationModule's acceleration/easing model.
 * All samplers are called each frame so they can provide randomised human-like values.
 */
class HeadRotationStrategy(
  private val speedScaleSampler: () -> Float,
  private val accelScaleSampler: () -> Float,
  private val pitchStepSampler: () -> Float,
  private val maxSpeedSampler: () -> Float,
  private val maxAccelSampler: () -> Float,
) : IRotationStrategy {

  override fun onRotate(player: LocalPlayer, targetYaw: Float, targetPitch: Float): Rotation {
    val maxSpeedScale = speedScaleSampler()
    val accelScale    = accelScaleSampler()
    val maxTurnSpeed  = maxSpeedSampler()
    val maxTurnAccel  = maxAccelSampler()
    val maxPitchStep  = pitchStepSampler()

    val yawDelta = AngleUtils.getRotationDelta(player.yRot, targetYaw)
    val yawStep  = HeadRotationModule.computeTurnDelta(
      yawDelta,
      maxSpeedScale = maxSpeedScale,
      accelScale    = accelScale,
      maxTurnSpeed  = maxTurnSpeed,
      maxTurnAccel  = maxTurnAccel,
    )
    val newYaw = AngleUtils.normalizeAngle(player.yRot + yawStep)

    val pitchDelta = targetPitch - player.xRot
    val pitchStep  = HeadRotationModule.computePitchDelta(
      pitchDelta,
      maxSpeedScale = maxSpeedScale,
      accelScale    = accelScale,
      maxPitchSpeed = maxPitchStep * 20f,
      maxPitchAccel = maxPitchStep * 60f,
    )
    val newPitch = (player.xRot + pitchStep).coerceIn(-89.9f, 89.9f)

    return Rotation(newYaw, newPitch)
  }
}
