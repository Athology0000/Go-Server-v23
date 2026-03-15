package org.cobalt.internal.pathfinding

import kotlin.math.abs
import kotlin.math.tanh

object HeadRotationModule {
	private const val MAX_TURN_SPEED_PER_SEC = 100f
	private const val MAX_TURN_ACCEL_PER_SEC2 = 220f
	private const val TURN_HYPERBOLIC_SCALE = 90.0
	private const val MIN_DELTA_EPS = 1.0e-4f
	private const val MAX_DT_SEC = 0.10f
	private const val MIN_DT_SEC = 1f / 240f

	private var yawVelocity = 0f
	private var yawLastTimeNs = 0L
	private var pitchVelocity = 0f
	private var pitchLastTimeNs = 0L

	/** Call when starting a new rotation context (e.g. switching from mining to warp) to avoid velocity bleed. */
	fun resetVelocity() {
		yawVelocity = 0f
		yawLastTimeNs = 0L
		pitchVelocity = 0f
		pitchLastTimeNs = 0L
	}

	fun computeTurnDelta(
		yawDelta: Float,
		maxSpeedScale: Float = 1f,
		accelScale: Float = 1f,
		maxTurnSpeed: Float = MAX_TURN_SPEED_PER_SEC,
		maxTurnAccel: Float = MAX_TURN_ACCEL_PER_SEC2,
	): Float {
		val absDelta = abs(yawDelta)
		if (absDelta < MIN_DELTA_EPS) {
			yawVelocity = 0f
			yawLastTimeNs = System.nanoTime()
			return 0f
		}

		val now = System.nanoTime()
		val dt =
			if (yawLastTimeNs == 0L) {
				1f / 20f
			} else {
				((now - yawLastTimeNs) / 1_000_000_000.0f).coerceIn(MIN_DT_SEC, MAX_DT_SEC)
			}
		yawLastTimeNs = now

		// Hyperbolic easing: near-zero deltas ease slowly, large deltas approach max speed.
		val speedPerSec =
			(maxTurnSpeed * tanh(absDelta / TURN_HYPERBOLIC_SCALE)).toFloat() *
				maxSpeedScale
		val desiredSpeed = if (yawDelta >= 0f) speedPerSec else -speedPerSec
		val maxAccel = maxTurnAccel * accelScale
		val speedDelta = desiredSpeed - yawVelocity
		val maxDelta = maxAccel * dt
		yawVelocity += speedDelta.coerceIn(-maxDelta, maxDelta)

		val turn = yawVelocity * dt
		return if (absDelta < abs(turn)) {
			yawVelocity = 0f
			yawDelta
		} else {
			turn
		}
	}

	/**
	 * Smooth pitch delta with the same acceleration model as yaw.
	 * Uses separate velocity state so it doesn't interfere with yaw smoothing.
	 */
	fun computePitchDelta(
		pitchDelta: Float,
		maxSpeedScale: Float = 1f,
		accelScale: Float = 1f,
		maxPitchSpeed: Float = MAX_TURN_SPEED_PER_SEC,
		maxPitchAccel: Float = MAX_TURN_ACCEL_PER_SEC2,
	): Float {
		val absDelta = abs(pitchDelta)
		if (absDelta < MIN_DELTA_EPS) {
			pitchVelocity = 0f
			pitchLastTimeNs = System.nanoTime()
			return 0f
		}

		val now = System.nanoTime()
		val dt =
			if (pitchLastTimeNs == 0L) {
				1f / 20f
			} else {
				((now - pitchLastTimeNs) / 1_000_000_000.0f).coerceIn(MIN_DT_SEC, MAX_DT_SEC)
			}
		pitchLastTimeNs = now

		val speedPerSec =
			(maxPitchSpeed * tanh(absDelta / TURN_HYPERBOLIC_SCALE)).toFloat() *
				maxSpeedScale
		val desiredSpeed = if (pitchDelta >= 0f) speedPerSec else -speedPerSec
		val maxAccel = maxPitchAccel * accelScale
		val speedDelta = desiredSpeed - pitchVelocity
		val maxDelta = maxAccel * dt
		pitchVelocity += speedDelta.coerceIn(-maxDelta, maxDelta)

		val turn = pitchVelocity * dt
		return if (absDelta < abs(turn)) {
			pitchVelocity = 0f
			pitchDelta
		} else {
			turn
		}
	}
}
