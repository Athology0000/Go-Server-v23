package org.cobalt.internal.combat

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import net.minecraft.client.player.LocalPlayer
import org.cobalt.api.rotation.IRotationStrategy
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.helper.Rotation
import org.cobalt.internal.rotation.RotationsModule

internal class CombatRotationStrategy : IRotationStrategy {
  private var sampledYawStep = 0f
  private var sampledPitchStep = 0f
  private var lastSampleNs = 0L
  private var lastRotateNs = 0L
  private var lastTargetYaw = Float.NaN
  private var lastTargetPitch = Float.NaN

  // Slow-varying aim drift replaces per-tick random jitter with correlated hand motion.
  private var driftYaw = 0f
  private var driftPitch = 0f
  private var driftYawTarget = 0f
  private var driftPitchTarget = 0f
  private var lastDriftNs = 0L

  override fun onStart() {
    sampledYawStep = 0f
    sampledPitchStep = 0f
    lastSampleNs = 0L
    lastRotateNs = 0L
    lastTargetYaw = Float.NaN
    lastTargetPitch = Float.NaN
    driftYaw = 0f
    driftPitch = 0f
    driftYawTarget = 0f
    driftPitchTarget = 0f
    lastDriftNs = 0L
    refreshSamples(0f, 0f, force = true)
  }

  override fun onStop() {
    lastRotateNs = 0L
    lastTargetYaw = Float.NaN
    lastTargetPitch = Float.NaN
  }

  override fun onRotate(player: LocalPlayer, targetYaw: Float, targetPitch: Float): Rotation {
    refreshSamples(targetYaw, targetPitch)

    val frameScale = currentFrameScale()
    val effectiveCurveIn = RotationsModule.bezierCurveIn.value.toFloat()
    val effectiveCurveOut = RotationsModule.bezierCurveOut.value.toFloat()
    val effectiveMinScale = RotationsModule.bezierMinScale.value.toFloat()
    val effectiveSnap = RotationsModule.bezierSnapThreshold.value.toFloat().coerceAtMost(MAX_SNAP_THRESHOLD)

    val yawDelta = AngleUtils.getRotationDelta(player.yRot, targetYaw)
    val pitchDelta = targetPitch - player.xRot
    val angularScale = stepScaleForDelta(abs(yawDelta), abs(pitchDelta))
    val nextYaw =
      player.yRot + smoothStep(
        yawDelta,
        sampledYawStep * frameScale * angularScale,
        effectiveCurveIn,
        effectiveCurveOut,
        effectiveMinScale,
        effectiveSnap,
      )
    val nextPitch =
      (
        player.xRot + smoothStep(
          pitchDelta,
          sampledPitchStep * frameScale * angularScale,
          effectiveCurveIn,
          effectiveCurveOut,
          effectiveMinScale,
          effectiveSnap,
        )
        ).coerceIn(-90f, 90f)

    updateDrift(System.nanoTime())
    return Rotation(nextYaw + driftYaw, (nextPitch + driftPitch).coerceIn(-90f, 90f))
  }

  private fun updateDrift(nowNs: Long) {
    if (lastDriftNs == 0L || nowNs - lastDriftNs > 550_000_000L) {
      driftYawTarget = (Math.random().toFloat() - 0.5f) * 0.30f
      driftPitchTarget = (Math.random().toFloat() - 0.5f) * 0.40f
      lastDriftNs = nowNs
    }
    driftYaw += (driftYawTarget - driftYaw) * 0.06f
    driftPitch += (driftPitchTarget - driftPitch) * 0.06f
  }

  private fun refreshSamples(targetYaw: Float, targetPitch: Float, force: Boolean = false) {
    val now = System.nanoTime()
    val targetShifted =
      lastTargetYaw.isNaN() ||
        abs(AngleUtils.getRotationDelta(lastTargetYaw, targetYaw)) >= RESEED_YAW ||
        abs(targetPitch - lastTargetPitch) >= RESEED_PITCH
    if (force || lastSampleNs == 0L || now - lastSampleNs >= SAMPLE_NS || targetShifted) {
      sampledYawStep = (RotationsModule.sample(RotationsModule.combatYawStep.value) * STEP_SCALE).toFloat()
      sampledPitchStep = (RotationsModule.sample(RotationsModule.combatPitchStep.value) * STEP_SCALE).toFloat()
      lastSampleNs = now
    }
    lastTargetYaw = targetYaw
    lastTargetPitch = targetPitch
  }

  private fun currentFrameScale(): Float {
    val now = System.nanoTime()
    val dt =
      if (lastRotateNs == 0L) {
        1f / BASE_HZ
      } else {
        ((now - lastRotateNs) / 1_000_000_000.0f).coerceIn(1f / 240f, 0.1f)
      }
    lastRotateNs = now
    return (dt * BASE_HZ).coerceIn(MIN_FRAME_SCALE, MAX_FRAME_SCALE)
  }

  private fun stepScaleForDelta(yawDelta: Float, pitchDelta: Float): Float {
    val maxDelta = max(yawDelta, pitchDelta)
    return 0.42f + (maxDelta / 65f).coerceIn(0f, 1f) * 0.58f
  }

  private fun smoothStep(
    delta: Float,
    maxStep: Float,
    curveIn: Float,
    curveOut: Float,
    minScale: Float,
    snapThreshold: Float,
  ): Float {
    val absDelta = abs(delta)
    if (absDelta < snapThreshold) return delta
    val safeMaxStep = maxStep.coerceAtLeast(0.01f)
    val stepLimit = min(absDelta, safeMaxStep)
    val t = (absDelta / safeMaxStep).coerceIn(0f, 1f)
    val eased = cubicBezier(t, curveIn, curveOut)
    val scale = minScale + (1f - minScale) * eased
    val step = stepLimit * scale
    return if (delta < 0f) -step else step
  }

  private fun cubicBezier(t: Float, p1: Float, p2: Float): Float {
    val inv = 1f - t
    return (3f * inv * inv * t * p1) + (3f * inv * t * t * p2) + (t * t * t)
  }

  private companion object {
    private const val STEP_SCALE = 0.95
    private const val SAMPLE_NS = 180_000_000L
    private const val RESEED_YAW = 16f
    private const val RESEED_PITCH = 10f
    private const val BASE_HZ = 20f
    private const val MIN_FRAME_SCALE = 0.28f
    private const val MAX_FRAME_SCALE = 1.08f
    private const val MAX_SNAP_THRESHOLD = 0.12f
  }
}
