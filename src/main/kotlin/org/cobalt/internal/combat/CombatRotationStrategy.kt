package org.cobalt.internal.combat

import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.random.Random
import net.minecraft.client.player.LocalPlayer
import org.cobalt.api.rotation.IRotationStrategy
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.helper.Rotation

internal class CombatRotationStrategy : IRotationStrategy {
  private var initialized = false
  private var currentYaw = 0f
  private var currentPitch = 0f
  private var rawTargetYaw = 0f
  private var rawTargetPitch = 0f
  private var yawVelocity = 0.0
  private var pitchVelocity = 0.0

  private var driftYaw = 0f
  private var driftPitch = 0f
  private var driftYawTarget = 0f
  private var driftPitchTarget = 0f
  private var nextRetargetNs = 0L
  private var lastNs = 0L

  private val rng = Random.Default

  override fun onStart() {
    initialized = false
    yawVelocity = 0.0
    pitchVelocity = 0.0
    resetDrift()
  }

  override fun onStop() {
    initialized = false
    yawVelocity = 0.0
    pitchVelocity = 0.0
    resetDrift()
  }

  override fun onRotate(player: LocalPlayer, targetYaw: Float, targetPitch: Float): Rotation {
    val now = System.nanoTime()

    if (!initialized) {
      currentYaw = player.yRot
      currentPitch = player.xRot
      rawTargetYaw = targetYaw
      rawTargetPitch = targetPitch
      initialized = true
      lastNs = now
      nextRetargetNs = now
      return Rotation(player.yRot, player.xRot)
    }

    rawTargetYaw = targetYaw
    rawTargetPitch = targetPitch.coerceIn(-90f, 90f)

    applyFollowPhysics()

    val dt = ((now - lastNs).toDouble() / 1_000_000_000.0).toFloat().coerceIn(0f, 0.1f)
    lastNs = now
    updateDrift(now, dt, player)

    return Rotation(
      AngleUtils.normalizeAngle(currentYaw + driftYaw),
      (currentPitch + driftPitch).coerceIn(-90f, 90f),
    )
  }

  private fun applyFollowPhysics() {
    currentYaw = AngleUtils.normalizeAngle(currentYaw)

    val yawError = AngleUtils.getRotationDelta(currentYaw, rawTargetYaw).toDouble()
    val pitchError = (rawTargetPitch - currentPitch).toDouble()
    val boostFactor = if (max(abs(yawError), abs(pitchError)) > SHARP_TURN_ERROR) SHARP_TURN_BOOST else 1.0

    yawVelocity = nextVelocity(
      error = yawError,
      velocity = yawVelocity,
      boostFactor = boostFactor,
    )
    currentYaw = AngleUtils.normalizeAngle(currentYaw + yawVelocity.toFloat())

    pitchVelocity = nextVelocity(
      error = pitchError,
      velocity = pitchVelocity,
      boostFactor = boostFactor,
    )
    currentPitch = (currentPitch + pitchVelocity.toFloat()).coerceIn(-90f, 90f)
  }

  private fun nextVelocity(error: Double, velocity: Double, boostFactor: Double): Double {
    if (abs(error) < SETTLE_THRESHOLD && abs(velocity) < SETTLE_VELOCITY) {
      return velocity * 0.25
    }

    val errorMultiplier = (abs(error) / 10.0).coerceIn(0.6, 1.6)
    val kp = BASE_KP * errorMultiplier * boostFactor
    val accelLimit = ACCEL_LIMIT * boostFactor
    val maxVelocity = MAX_VELOCITY * boostFactor
    val accel = (error * kp - velocity * KD).coerceIn(-accelLimit, accelLimit)

    return ((velocity + accel) * FRICTION).coerceIn(-maxVelocity, maxVelocity)
  }

  private fun updateDrift(nowNs: Long, dt: Float, player: LocalPlayer) {
    val trackingError = max(
      abs(AngleUtils.getRotationDelta(player.yRot, rawTargetYaw)),
      abs(rawTargetPitch - player.xRot),
    )
    val nearTargetScale = ((trackingError - DRIFT_MIN_ERROR) / DRIFT_MIN_ERROR).coerceIn(0f, 1f)
    val calmScale = (1f - (trackingError / DRIFT_SUPPRESS_ERROR).coerceIn(0f, 1f)) * nearTargetScale

    if (nextRetargetNs == 0L || nowNs >= nextRetargetNs) {
      driftYawTarget = randomCentered(MAX_YAW_DRIFT * calmScale)
      driftPitchTarget = randomCentered(MAX_PITCH_DRIFT * calmScale)
      nextRetargetNs = nowNs + rng.nextLong(RETARGET_MIN_NS, RETARGET_MAX_NS + 1L)
    }

    val blend = (1.0 - exp(-DRIFT_BLEND_SPEED * dt)).toFloat()
    driftYaw += (driftYawTarget - driftYaw) * blend
    driftPitch += (driftPitchTarget - driftPitch) * blend
  }

  private fun randomCentered(magnitude: Float): Float {
    if (magnitude <= 0f) return 0f
    return (rng.nextFloat() * 2f - 1f) * magnitude
  }

  private fun resetDrift() {
    driftYaw = 0f
    driftPitch = 0f
    driftYawTarget = 0f
    driftPitchTarget = 0f
    nextRetargetNs = 0L
    lastNs = 0L
  }

  private companion object {
    private const val BASE_KP = 0.052
    private const val KD = 0.60
    private const val MAX_VELOCITY = 6.8
    private const val ACCEL_LIMIT = 0.92
    private const val FRICTION = 0.88
    private const val SETTLE_THRESHOLD = 0.15
    private const val SETTLE_VELOCITY = 0.02
    private const val SHARP_TURN_ERROR = 42.0
    private const val SHARP_TURN_BOOST = 1.55

    private const val DRIFT_BLEND_SPEED = 2.7
    private const val DRIFT_SUPPRESS_ERROR = 32f
    private const val DRIFT_MIN_ERROR = 4f
    private const val MAX_YAW_DRIFT = 0.035f
    private const val MAX_PITCH_DRIFT = 0.018f
    private const val RETARGET_MIN_NS = 360_000_000L
    private const val RETARGET_MAX_NS = 720_000_000L
  }
}
