package org.cobalt.internal.combat

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
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

  private var aimYawOffset = 0f
  private var aimPitchOffset = 0f
  private var aimYawOffsetTarget = 0f
  private var aimPitchOffsetTarget = 0f
  private var lastAimOffsetNs = 0L
  private var correctionYaw = 0f
  private var correctionPitch = 0f
  private var correctionUntilNs = 0L
  private var overshootYaw = 0f
  private var overshootPitch = 0f

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
    aimYawOffset = 0f
    aimPitchOffset = 0f
    aimYawOffsetTarget = 0f
    aimPitchOffsetTarget = 0f
    lastAimOffsetNs = 0L
    correctionYaw = 0f
    correctionPitch = 0f
    correctionUntilNs = 0L
    overshootYaw = 0f
    overshootPitch = 0f
    refreshSamples(0f, 0f, force = true)
  }

  override fun onStop() {
    lastRotateNs = 0L
    lastTargetYaw = Float.NaN
    lastTargetPitch = Float.NaN
  }

  override fun onRotate(player: LocalPlayer, targetYaw: Float, targetPitch: Float): Rotation {
    val baseYawDelta = AngleUtils.getRotationDelta(player.yRot, targetYaw)
    val basePitchDelta = targetPitch - player.xRot
    val targetShifted = refreshSamples(targetYaw, targetPitch)

    val frameScale = currentFrameScale()
    val effectiveCurveIn = RotationsModule.bezierCurveIn.value.toFloat()
    val effectiveCurveOut = RotationsModule.bezierCurveOut.value.toFloat()
    val effectiveMinScale = RotationsModule.bezierMinScale.value.toFloat()
    val effectiveSnap = RotationsModule.bezierSnapThreshold.value.toFloat().coerceAtMost(MAX_SNAP_THRESHOLD)

    val nowNs = System.nanoTime()
    if (targetShifted) seedOvershoot(baseYawDelta, basePitchDelta)
    updateAimOffsets(nowNs, baseYawDelta, basePitchDelta)
    updateDrift(nowNs)

    val adjustedTargetYaw = targetYaw + aimYawOffset + correctionYaw + overshootYaw
    val adjustedTargetPitch = (targetPitch + aimPitchOffset + correctionPitch + overshootPitch).coerceIn(-90f, 90f)
    val yawDelta = AngleUtils.getRotationDelta(player.yRot, adjustedTargetYaw)
    val pitchDelta = adjustedTargetPitch - player.xRot
    val angularScale = stepScaleForDelta(abs(yawDelta), abs(pitchDelta))
    val humanCadence = cadenceScale(nowNs, abs(yawDelta), abs(pitchDelta))
    val nextYaw =
      player.yRot + smoothStep(
        yawDelta,
        sampledYawStep * frameScale * angularScale * humanCadence,
        effectiveCurveIn,
        effectiveCurveOut,
        effectiveMinScale,
        effectiveSnap,
      )
    val nextPitch =
      (
        player.xRot + smoothStep(
          pitchDelta,
          sampledPitchStep * frameScale * angularScale * humanCadence,
          effectiveCurveIn,
          effectiveCurveOut,
          effectiveMinScale,
          effectiveSnap,
        )
        ).coerceIn(-90f, 90f)

    return Rotation(nextYaw + driftYaw, (nextPitch + driftPitch).coerceIn(-90f, 90f))
  }

  private fun updateAimOffsets(nowNs: Long, yawDelta: Float, pitchDelta: Float) {
    if (lastAimOffsetNs == 0L || nowNs - lastAimOffsetNs > nextAimOffsetIntervalNs(yawDelta, pitchDelta)) {
      val distanceScale = (max(abs(yawDelta), abs(pitchDelta)) / 40f).coerceIn(0.35f, 1f)
      aimYawOffsetTarget = randomSigned(0.36f * distanceScale)
      aimPitchOffsetTarget = randomSigned(0.28f * distanceScale)
      lastAimOffsetNs = nowNs

      if (max(abs(yawDelta), abs(pitchDelta)) > 4f && Math.random() < 0.22) {
        correctionYaw = randomSigned(0.20f * distanceScale)
        correctionPitch = randomSigned(0.16f * distanceScale)
        correctionUntilNs = nowNs + (120_000_000L + (Math.random() * 140_000_000L).toLong())
      }
    }

    aimYawOffset += (aimYawOffsetTarget - aimYawOffset) * 0.045f
    aimPitchOffset += (aimPitchOffsetTarget - aimPitchOffset) * 0.05f

    if (correctionUntilNs > 0L && nowNs >= correctionUntilNs) {
      correctionYaw *= 0.68f
      correctionPitch *= 0.68f
      if (abs(correctionYaw) < 0.015f && abs(correctionPitch) < 0.015f) {
        correctionYaw = 0f
        correctionPitch = 0f
        correctionUntilNs = 0L
      }
    }

    overshootYaw *= 0.82f
    overshootPitch *= 0.80f
    if (abs(overshootYaw) < 0.01f) overshootYaw = 0f
    if (abs(overshootPitch) < 0.01f) overshootPitch = 0f
  }

  private fun seedOvershoot(yawDelta: Float, pitchDelta: Float) {
    val maxDelta = max(abs(yawDelta), abs(pitchDelta))
    if (maxDelta < 9f) return
    val overshootScale = (maxDelta / 55f).coerceIn(0.12f, 0.75f)
    overshootYaw = -kotlin.math.sign(yawDelta) * randomRange(0.10f, 0.36f) * overshootScale
    overshootPitch = -kotlin.math.sign(pitchDelta) * randomRange(0.06f, 0.24f) * overshootScale
  }

  private fun updateDrift(nowNs: Long) {
    if (lastDriftNs == 0L || nowNs - lastDriftNs > 550_000_000L) {
      driftYawTarget = randomSigned(0.15f)
      driftPitchTarget = randomSigned(0.20f)
      lastDriftNs = nowNs
    }
    driftYaw += (driftYawTarget - driftYaw) * 0.06f
    driftPitch += (driftPitchTarget - driftPitch) * 0.06f
  }

  private fun refreshSamples(targetYaw: Float, targetPitch: Float, force: Boolean = false): Boolean {
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
    return targetShifted
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

  private fun cadenceScale(nowNs: Long, yawDelta: Float, pitchDelta: Float): Float {
    val maxDelta = max(yawDelta, pitchDelta)
    val phase = (nowNs % 1_700_000_000L).toDouble() / 1_700_000_000.0
    val wave = sin(phase * Math.PI * 2.0).toFloat()
    val correctionBoost = if (maxDelta > 7f) 0.06f else 0f
    return (0.93f + wave * 0.055f + correctionBoost).coerceIn(0.84f, 1.08f)
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

  private fun nextAimOffsetIntervalNs(yawDelta: Float, pitchDelta: Float): Long {
    val urgency = (max(abs(yawDelta), abs(pitchDelta)) / 45f).coerceIn(0f, 1f)
    val base = 520_000_000L - (urgency * 180_000_000L).toLong()
    return base + (Math.random() * 220_000_000L).toLong()
  }

  private fun randomSigned(maxMagnitude: Float): Float =
    (Math.random().toFloat() * 2f - 1f) * maxMagnitude

  private fun randomRange(minValue: Float, maxValue: Float): Float =
    minValue + Math.random().toFloat() * (maxValue - minValue)

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
