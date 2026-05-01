package org.cobalt.api.pathfinder.jni

import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.phys.Vec3
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.helper.Rotation
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * V5 walker runtime state shared by PathfinderRotationStrategy and PathCommand.
 * The important invariant is that path progress only moves forward; picking the
 * globally nearest node each tick can make the walker turn back and orbit.
 */
object PathExecutorState {

    var rawTargetYaw: Float = 0f
    var rawTargetPitch: Float = 0f

    var currentYaw: Float = 0f
    var currentPitch: Float = 0f
    var yawVelocity: Double = 0.0
    var pitchVelocity: Double = 0.0
    var initialized: Boolean = false

    var pathCurvature: Double = 0.0
    var currentPathPosition: Double = 0.0
        private set
    var currentTargetPoint: Vec3? = null
        private set

    var initialTurnBoostTicks: Int = 0
    var jumpSuppressTicks: Int = 0

    fun reset() {
        rawTargetYaw = 0f
        rawTargetPitch = 0f
        currentYaw = 0f
        currentPitch = 0f
        yawVelocity = 0.0
        pitchVelocity = 0.0
        initialized = false
        initialTurnBoostTicks = 10
        pathCurvature = 0.0
        currentPathPosition = 0.0
        currentTargetPoint = null
        jumpSuppressTicks = 0
    }

    fun updateTarget(desiredYaw: Float, desiredPitch: Float, remaining: Double) {
        val isStraight = pathCurvature < STRAIGHT_CURVATURE
        val finishFactor = if (remaining < 3.0) maxOf(0.1, remaining / 3.0).toFloat() else 1f

        val yawDelta = AngleUtils.getRotationDelta(rawTargetYaw, desiredYaw)
        val boostFactor = initialTurnBoostFactor(yawDelta)
        val dynamicSmooth = ((if (isStraight) SMOOTH_FACTOR * 0.5f else SMOOTH_FACTOR) / finishFactor * boostFactor).coerceAtMost(1f)
        val dynamicYawDeadzone = (if (isStraight) YAW_DEADZONE * 1.5f else YAW_DEADZONE) * finishFactor

        if (abs(yawDelta) > dynamicYawDeadzone) {
            rawTargetYaw = AngleUtils.normalizeAngle(rawTargetYaw + yawDelta * dynamicSmooth)
        }
        val pitchDelta = desiredPitch - rawTargetPitch
        if (abs(pitchDelta) > PITCH_DEADZONE * finishFactor) {
            rawTargetPitch += pitchDelta * dynamicSmooth
        }
    }

    fun update(player: LocalPlayer, nodes: List<Vec3>) {
        if (nodes.isEmpty()) {
            rawTargetYaw = player.yRot
            rawTargetPitch = player.xRot
            pathCurvature = 0.0
            currentPathPosition = 0.0
            currentTargetPoint = null
            return
        }

        val eye = player.eyePosition
        val projected = projectPathPositionHorizontal(player.x, player.z, currentPathPosition, nodes)
        if (projected.isFinite() && projected > currentPathPosition) {
            val steadyAdvance = minOf(currentPathPosition + PREDICTION_MAX_ADVANCE_GROUND, projected)
            val catchUpAdvance = projected - LOOKAHEAD_NODES + MIN_TARGET_AHEAD_OF_PLAYER
            currentPathPosition = maxOf(steadyAdvance, catchUpAdvance, currentPathPosition)
                .coerceAtMost(nodes.lastIndex.toDouble())
        }

        val nearIdx = currentPathPosition.toInt().coerceIn(0, nodes.lastIndex)
        updateCurvature(nodes, nearIdx)

        val lookTargetPosition = if (projected.isFinite()) {
            maxOf(
                currentPathPosition + LOOKAHEAD_NODES,
                projected + MIN_TARGET_AHEAD_OF_PLAYER
            )
        } else {
            currentPathPosition + LOOKAHEAD_NODES
        }.coerceIn(0.0, nodes.lastIndex.toDouble())

        val targetPoint = clampLookDistance(eye, pointAt(nodes, lookTargetPosition))
        currentTargetPoint = targetPoint
        val rotation = rotationTo(eye, targetPoint)
        val remaining = (nodes.lastIndex - currentPathPosition).coerceAtLeast(0.0)

        if (!initialized) {
            rawTargetYaw = rotation.yaw
            rawTargetPitch = rotation.pitch
        }
        updateTarget(rotation.yaw, rotation.pitch, remaining)
    }

    fun updateCurvature(nodes: List<Vec3>, nearIdx: Int, range: Int = 4) {
        if (nearIdx + 2 > nodes.lastIndex) {
            pathCurvature = 0.0
            return
        }
        val a = nodes[nearIdx]
        val b = nodes[minOf(nearIdx + 1, nodes.lastIndex)]
        val bdx = b.x - a.x
        val bdz = b.z - a.z
        val baseMag = sqrt(bdx * bdx + bdz * bdz)
        if (baseMag < 0.3) {
            pathCurvature = 0.0
            return
        }

        var maxAngle = 0.0
        for (k in 2..range) {
            val fi = minOf(nearIdx + k, nodes.lastIndex - 1)
            if (fi >= nodes.lastIndex) break
            val fa = nodes[fi]
            val fb = nodes[minOf(fi + 1, nodes.lastIndex)]
            val fdx = fb.x - fa.x
            val fdz = fb.z - fa.z
            val fMag = sqrt(fdx * fdx + fdz * fdz)
            if (fMag < 0.3) continue
            val dot = (bdx * fdx + bdz * fdz) / (baseMag * fMag)
            val angle = acos(dot.coerceIn(-1.0, 1.0))
            if (angle > maxAngle) maxAngle = angle
        }
        pathCurvature = maxAngle
    }

    fun initialTurnBoostFactor(yawError: Float): Float =
        if (initialTurnBoostTicks > 0 && abs(yawError) >= maxOf(35f, YAW_DEADZONE * 4)) 2f else 1f

    private fun projectPathPositionHorizontal(x: Double, z: Double, hint: Double, nodes: List<Vec3>): Double {
        if (nodes.size < 2) return 0.0
        val lastSegment = nodes.size - 2
        val base = hint.toInt().coerceIn(0, lastSegment)
        val start = maxOf(0, base - 2)
        val end = minOf(lastSegment, base + 28)
        var bestT = hint.coerceIn(0.0, nodes.lastIndex.toDouble())
        var bestDistSq = Double.POSITIVE_INFINITY

        for (i in start..end) {
            val a = nodes[i]
            val b = nodes[i + 1]
            val ax = a.x + 0.5
            val az = a.z + 0.5
            val bx = b.x + 0.5
            val bz = b.z + 0.5
            val abx = bx - ax
            val abz = bz - az
            val lenSq = abx * abx + abz * abz
            val frac = if (lenSq <= 1e-8) 0.0 else (((x - ax) * abx + (z - az) * abz) / lenSq).coerceIn(0.0, 1.0)
            val px = ax + abx * frac
            val pz = az + abz * frac
            val dx = x - px
            val dz = z - pz
            val distSq = dx * dx + dz * dz
            if (distSq < bestDistSq) {
                bestDistSq = distSq
                bestT = i + frac
            }
        }

        return bestT
    }

    private fun pointAt(nodes: List<Vec3>, indexFloat: Double): Vec3 {
        val safe = indexFloat.coerceIn(0.0, nodes.lastIndex.toDouble())
        val idx = safe.toInt().coerceIn(0, nodes.lastIndex)
        val frac = safe - idx
        val a = nodes[idx]
        val b = nodes[minOf(idx + 1, nodes.lastIndex)]
        return Vec3(
            a.x + 0.5 + (b.x - a.x) * frac,
            a.y + PLAYER_LOOK_HEIGHT + (b.y - a.y) * frac,
            a.z + 0.5 + (b.z - a.z) * frac
        )
    }

    private fun clampLookDistance(eye: Vec3, target: Vec3): Vec3 {
        val dx = target.x - eye.x
        val dy = target.y - eye.y
        val dz = target.z - eye.z
        val dist = sqrt(dx * dx + dy * dy + dz * dz)
        if (dist <= MAX_LOOK_DISTANCE || dist <= 1e-6) return target
        val scale = MAX_LOOK_DISTANCE / dist
        return Vec3(eye.x + dx * scale, eye.y + dy * scale, eye.z + dz * scale)
    }

    private fun rotationTo(from: Vec3, to: Vec3): Rotation {
        val dx = to.x - from.x
        val dy = to.y - from.y
        val dz = to.z - from.z
        val horizontal = sqrt(dx * dx + dz * dz)
        return Rotation(
            Math.toDegrees(atan2(-dx, dz)).toFloat(),
            -Math.toDegrees(atan2(dy, horizontal)).toFloat()
        )
    }

    private const val YAW_DEADZONE = 1.2f
    private const val PITCH_DEADZONE = 1.8f
    private const val SMOOTH_FACTOR = 0.1f
    private const val STRAIGHT_CURVATURE = 0.15
    private const val PLAYER_LOOK_HEIGHT = 1.62
    private const val LOOKAHEAD_NODES = 2.5
    private const val MIN_TARGET_AHEAD_OF_PLAYER = 0.75
    private const val MAX_LOOK_DISTANCE = 0.8
    private const val PREDICTION_MAX_ADVANCE_GROUND = 0.9
}
