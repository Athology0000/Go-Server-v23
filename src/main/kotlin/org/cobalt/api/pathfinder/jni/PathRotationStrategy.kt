package org.cobalt.api.pathfinder.jni

import net.minecraft.client.player.LocalPlayer
import org.cobalt.api.rotation.IRotationStrategy
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.helper.Rotation
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.random.Random

/**
 * Pathfinding rotation strategy that runs through the central RotationExecutor.
 *
 * Motion model: exp-smoother with independent yaw/pitch rates. Adds two
 * anticipation terms tuned for path-following:
 *   - rising-curvature on yaw: rate is boosted as upcoming pathCurvature grows
 *     (so the camera starts speeding up *before* the corner peaks rather than
 *     reacting after it)
 *   - vertical lookahead delta on pitch: rate is boosted when the active
 *     lookahead point sits well above / below the player's eye, which fixes
 *     the terrain-pitch lag where pitch trailed the path's Y changes
 *
 * Returns `null` from [onRotate] once a soft-release request has converged or
 * its budget expired — that signals RotationExecutor to release the strategy.
 */
object PathRotationStrategy : IRotationStrategy {

    private const val SETTLE_THRESHOLD_DEG = 0.6f
    private const val DRIFT_BLEND_SPEED = 4.0
    private const val MAX_YAW_DRIFT = 0.18f
    private const val MAX_PITCH_DRIFT = 0.10f
    private const val RETARGET_MIN_NS = 260_000_000L
    private const val RETARGET_MAX_NS = 520_000_000L
    private const val HUMANIZATION_SUPPRESS_ERROR = 30f

    private var smoothedYaw: Float = 0f
    private var smoothedPitch: Float = 0f
    private var initialized: Boolean = false

    private var prevCurvature: Double = 0.0
    private var lastFrameNs: Long = 0L

    // Drift state for optional humanization (off when config.humanizationDrift = 0).
    private var driftYaw: Float = 0f
    private var driftPitch: Float = 0f
    private var driftYawTarget: Float = 0f
    private var driftPitchTarget: Float = 0f
    private var nextRetargetNs: Long = 0L
    private val rng = Random.Default

    // Soft-release: external callers (PathCommand, NativePathfinder) ask us to
    // settle on the last target over a few frames instead of cutting. While
    // active, onRotate keeps converging; once the angular error is below
    // SETTLE_THRESHOLD_DEG or the budget runs out, it returns null and the
    // executor releases us.
    @Volatile private var softReleaseFramesRemaining: Int = 0

    /**
     * Ask the strategy to soft-release: keep converging toward the last
     * target for up to [maxFrames] frames, then auto-stop. Mirrors the
     * `releaseWhenSettled` flow on BlockRotationController so the camera
     * lands cleanly instead of clamping mid-flight.
     */
    fun requestSoftRelease(maxFrames: Int = 18) {
        softReleaseFramesRemaining = maxFrames.coerceAtLeast(1)
    }

    override fun onStart() {
        initialized = false
        prevCurvature = 0.0
        lastFrameNs = 0L
        softReleaseFramesRemaining = 0
        resetDrift()
    }

    override fun onStop() {
        initialized = false
        prevCurvature = 0.0
        lastFrameNs = 0L
        softReleaseFramesRemaining = 0
        resetDrift()
    }

    override fun onRotate(player: LocalPlayer, targetYaw: Float, targetPitch: Float): Rotation? {
        val now = System.nanoTime()
        val dt = if (lastFrameNs == 0L) 1.0 / 60.0
        else ((now - lastFrameNs).toDouble() / 1_000_000_000.0).coerceIn(1.0 / 240.0, 0.1)
        lastFrameNs = now

        if (!initialized) {
            smoothedYaw = player.yRot
            smoothedPitch = player.xRot
            initialized = true
        }

        val curvature = PathExecutorState.pathCurvature
        val risingCurvature = (curvature - prevCurvature).coerceAtLeast(0.0)
        prevCurvature = curvature

        val verticalDelta = PathExecutorState.currentLookaheadSplinePoint
            ?.let { (it.y - player.eyeY).toFloat() }
            ?: 0f

        // Per-axis rate. Yaw gets a curvature-derivative boost so it speeds up
        // as upcoming curvature grows; pitch gets a vertical-lookahead boost
        // so it speeds up when the path is climbing/descending.
        val yawRate = (PathRotationConfig.baseYawRate +
            PathRotationConfig.cornerAnticipation * risingCurvature)
            .coerceAtLeast(0.5)
        val pitchRate = (PathRotationConfig.basePitchRate +
            PathRotationConfig.verticalAnticipation * (abs(verticalDelta) / 4f)
                .coerceAtMost(2f))
            .coerceAtLeast(0.5)

        val blendYaw = (1.0 - exp(-yawRate * dt)).coerceIn(0.0, 1.0).toFloat()
        val blendPitch = (1.0 - exp(-pitchRate * dt)).coerceIn(0.0, 1.0).toFloat()

        val yawDelta = AngleUtils.getRotationDelta(smoothedYaw, targetYaw)
        smoothedYaw = AngleUtils.normalizeAngle(smoothedYaw + yawDelta * blendYaw)
        val pitchDelta = targetPitch - smoothedPitch
        smoothedPitch = (smoothedPitch + pitchDelta * blendPitch).coerceIn(-89.9f, 89.9f)

        // Optional humanization drift. Suppressed when tracking error is large.
        val driftScale = PathRotationConfig.humanizationDrift.toFloat()
        if (driftScale > 0f) {
            updateDrift(now, dt.toFloat(), yawDelta, pitchDelta, driftScale)
        }

        // Soft-release: count down; once we've converged below threshold or
        // exhausted the budget, return null to release the executor.
        if (softReleaseFramesRemaining > 0) {
            softReleaseFramesRemaining--
            val settled = abs(yawDelta) < SETTLE_THRESHOLD_DEG &&
                abs(pitchDelta) < SETTLE_THRESHOLD_DEG
            if (settled || softReleaseFramesRemaining == 0) {
                return null
            }
        }

        val outYaw = AngleUtils.normalizeAngle(smoothedYaw + driftYaw)
        val outPitch = (smoothedPitch + driftPitch).coerceIn(-89.9f, 89.9f)
        return Rotation(outYaw, outPitch)
    }

    private fun updateDrift(
        nowNs: Long,
        dt: Float,
        yawError: Float,
        pitchError: Float,
        scale: Float,
    ) {
        val trackingError = max(abs(yawError), abs(pitchError))
        val calmScale = (1f - (trackingError / HUMANIZATION_SUPPRESS_ERROR).coerceIn(0f, 1f)) * scale
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
    }
}
