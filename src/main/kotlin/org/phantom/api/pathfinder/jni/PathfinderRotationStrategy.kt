package org.phantom.api.pathfinder.jni

import net.minecraft.client.player.LocalPlayer
import org.phantom.api.rotation.IRotationStrategy
import org.phantom.api.util.AngleUtils
import org.phantom.api.util.helper.Rotation
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.random.Random

/**
 * PD-controller rotation strategy â€” direct port of Phantom PathRotations.applyHumanizedPhysics().
 *
 *   error * dynamicKp âˆ’ velocity * dynamicKd â†’ accel â†’ velocity *= 0.92 â†’ angle
 *
 * Includes Phantom's initialTurnBoostFactor (2Ã— KP/accel on first sharp turn of each path).
 * PD state lives in PathExecutorState so it survives RotationExecutor restarts mid-path.
 * Humanization drift is retained for anti-bot appearance.
 */
object PathfinderRotationStrategy : IRotationStrategy {

    // â”€â”€ PD constants (Phantom walker values) â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private const val BASE_KP = 0.042
    private const val KD = 0.62
    private const val MAX_VELOCITY = 5.6
    private const val ACCEL_LIMIT = 0.78
    private const val FRICTION = 0.88
    private const val SETTLE_THRESHOLD = 0.15
    private const val SETTLE_VELOCITY = 0.02

    // â”€â”€ Humanization â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private const val DRIFT_BLEND_SPEED = 2.5
    private const val HUMANIZATION_SUPPRESS_ERROR = 32f
    private const val HUMANIZATION_MIN_ERROR = 4f
    private const val MAX_YAW_DRIFT = 0.03f
    private const val MAX_PITCH_DRIFT = 0.015f
    private const val RETARGET_MIN_NS = 450_000_000L
    private const val RETARGET_MAX_NS = 900_000_000L

    private var driftYaw: Float = 0f
    private var driftPitch: Float = 0f
    private var driftYawTarget: Float = 0f
    private var driftPitchTarget: Float = 0f
    private var nextRetargetNs: Long = 0L
    private var lastNs: Long = 0L
    private val rng = Random.Default

    // â”€â”€ Lifecycle â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    override fun onStart() {
        PathExecutorState.initialized = false
        resetHumanization()
    }

    override fun onStop() {
        PathExecutorState.initialized = false
        resetHumanization()
    }

    // â”€â”€ Core rotation (per render frame) â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    override fun onRotate(player: LocalPlayer, targetYaw: Float, targetPitch: Float): Rotation {
        val state = PathExecutorState
        val now = System.nanoTime()

        if (!state.initialized) {
            state.currentYaw = player.yRot
            state.currentPitch = player.xRot
            state.rawTargetYaw = targetYaw
            state.rawTargetPitch = targetPitch
            state.yawVelocity = 0.0
            state.pitchVelocity = 0.0
            state.initialized = true
            lastNs = now
            nextRetargetNs = now
            return Rotation(player.yRot, player.xRot)
        }

        state.rawTargetYaw = targetYaw
        state.rawTargetPitch = targetPitch

        applyPDPhysics()

        val dt = ((now - lastNs).toDouble() / 1_000_000_000.0).toFloat().coerceIn(0f, 0.1f)
        lastNs = now
        updateHumanization(now, dt, player, targetYaw, targetPitch)

        return Rotation(
            AngleUtils.normalizeAngle(state.currentYaw + driftYaw),
            (state.currentPitch + driftPitch).coerceIn(-89.9f, 89.9f)
        )
    }

    // â”€â”€ PD physics â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Direct port of Phantom applyHumanizedPhysics().
     * initialTurnBoostFactor doubles KP, accelLimit, maxVelocity on large initial turns.
     * isStraight increases KD by 1.3Ã— to damp overshoot on straight segments.
     */
    private fun applyPDPhysics() {
        val state = PathExecutorState
        val isStraight = state.pathCurvature < 0.2

        // â”€ Yaw â”€
        state.currentYaw = AngleUtils.normalizeAngle(state.currentYaw)
        val yawError = AngleUtils.getRotationDelta(state.currentYaw, state.rawTargetYaw).toDouble()
        val absYawError = abs(yawError)

        // Phantom: decrement initialTurnBoostTicks; zero early when turn is nearly complete
        if (state.initialTurnBoostTicks > 0) {
            val currentYawError = abs(AngleUtils.getRotationDelta(state.currentYaw, state.rawTargetYaw))
            if (currentYawError <= maxOf(10f, 1.2f * 2f)) {
                state.initialTurnBoostTicks = 0
            } else {
                state.initialTurnBoostTicks--
            }
        }

        val boostFactor = state.initialTurnBoostFactor(yawError.toFloat())

        if (absYawError < SETTLE_THRESHOLD && abs(state.yawVelocity) < SETTLE_VELOCITY) {
            state.yawVelocity *= 0.25
        } else {
            val errorMult = (absYawError / 10.0).coerceIn(0.6, 1.5)
            val dynamicKp = BASE_KP * errorMult * boostFactor
            val dynamicKd = if (isStraight) KD * 1.3 else KD
            val accelLimit = ACCEL_LIMIT * boostFactor
            val maxVelocity = MAX_VELOCITY * boostFactor
            var accel = yawError * dynamicKp - state.yawVelocity * dynamicKd
            accel = accel.coerceIn(-accelLimit, accelLimit)
            state.yawVelocity += accel
            state.yawVelocity *= FRICTION
            state.yawVelocity = state.yawVelocity.coerceIn(-maxVelocity, maxVelocity)
            state.currentYaw = AngleUtils.normalizeAngle(state.currentYaw + state.yawVelocity.toFloat())
        }

        // â”€ Pitch â”€
        val pitchError = (state.rawTargetPitch - state.currentPitch).toDouble()
        val absPitchError = abs(pitchError)

        if (absPitchError < SETTLE_THRESHOLD && abs(state.pitchVelocity) < SETTLE_VELOCITY) {
            state.pitchVelocity *= 0.25
        } else {
            val errorMult = (absPitchError / 10.0).coerceIn(0.6, 1.5)
            val dynamicKp = BASE_KP * errorMult * boostFactor
            var accel = pitchError * dynamicKp - state.pitchVelocity * KD
            accel = accel.coerceIn(-ACCEL_LIMIT * boostFactor, ACCEL_LIMIT * boostFactor)
            state.pitchVelocity += accel
            state.pitchVelocity *= FRICTION
            state.pitchVelocity = state.pitchVelocity.coerceIn(-MAX_VELOCITY * boostFactor, MAX_VELOCITY * boostFactor)
            state.currentPitch += state.pitchVelocity.toFloat()
        }
        state.currentPitch = state.currentPitch.coerceIn(-90f, 90f)
    }

    // â”€â”€ Humanization â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private fun updateHumanization(
        nowNs: Long, dt: Float,
        player: LocalPlayer, targetYaw: Float, targetPitch: Float
    ) {
        val trackingError = max(
            abs(AngleUtils.getRotationDelta(player.yRot, targetYaw)),
            abs(targetPitch - player.xRot)
        )
        val nearTargetScale = ((trackingError - HUMANIZATION_MIN_ERROR) / HUMANIZATION_MIN_ERROR).coerceIn(0f, 1f)
        val calmScale = (1f - (trackingError / HUMANIZATION_SUPPRESS_ERROR).coerceIn(0f, 1f)) * nearTargetScale
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

    private fun resetHumanization() {
        driftYaw = 0f; driftPitch = 0f
        driftYawTarget = 0f; driftPitchTarget = 0f
        nextRetargetNs = 0L; lastNs = 0L
    }
}
