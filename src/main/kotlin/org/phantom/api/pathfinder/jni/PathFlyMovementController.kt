package org.phantom.api.pathfinder.jni

import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.phys.Vec3
import org.phantom.api.util.AngleUtils
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot

internal data class FlyMovementUpdate(
    val command: PathCommand?,
    val arrived: Boolean,
)

internal object PathFlyMovementController {
    private var currentIndex = 0
    private var decelerating = false
    private var decelTicks = 0
    private var forcedFlying = false

    fun reset() {
        currentIndex = 0
        decelerating = false
        decelTicks = 0
        releaseForcedFlight()
    }

    fun update(
        player: LocalPlayer,
        path: List<Vec3>,
        finalTarget: Vec3,
        status: PathStatus,
        distanceToTarget: Float,
    ): FlyMovementUpdate {
        if (path.isEmpty()) return FlyMovementUpdate(null, arrived = false)
        ensureFlying(player)

        if (decelerating) {
            decelTicks++
            val arrived = shouldFinishDeceleration(player, finalTarget) || decelTicks >= MAX_DECEL_TICKS
            return FlyMovementUpdate(
                command = if (arrived) null else stopCommand(status, distanceToTarget),
                arrived = arrived,
            )
        }

        if (willArriveAtDestinationAfterStopping(player, finalTarget) ||
            player.position().distanceToSqr(finalTarget) < 0.09
        ) {
            decelerating = true
            decelTicks = 0
            return FlyMovementUpdate(stopCommand(status, distanceToTarget), arrived = false)
        }

        val current = player.position()
        val motion = player.deltaMovement
        val speedXZ = hypot(motion.x, motion.z)
        val targetInfo = getMovementTarget(current, path, speedXZ)
            ?: return FlyMovementUpdate(null, arrived = false)

        val movementTarget = targetInfo.movementTarget
        val verticalTarget = targetInfo.verticalTarget
        val desiredYaw = yawToTarget(movementTarget.x - player.x, movementTarget.z - player.z)
        val keys = movementKeysForYaw(player.yRot, desiredYaw, horizontalDistanceSq(player.position(), movementTarget))

        val yError = verticalTarget.y - player.y
        val verticalDeadzone = if (speedXZ > 0.12) VERTICAL_DEADZONE_MOVING else VERTICAL_DEADZONE
        val lift = yError > verticalDeadzone || shouldBoostFromGround(player, movementTarget)
        val descend = yError < -verticalDeadzone

        return FlyMovementUpdate(
            command = PathCommand(
                forward = keys.forward,
                back = keys.back,
                left = keys.left,
                right = keys.right,
                jump = lift,
                sneak = descend,
                sprint = true,
                targetYaw = desiredYaw,
                targetPitch = 0f,
                status = status,
                activeAction = ActionType.FLY,
                distanceToTarget = distanceToTarget,
            ),
            arrived = false,
        )
    }

    private fun ensureFlying(player: LocalPlayer) {
        val abilities = player.abilities
        if (!abilities.flying) {
            forcedFlying = true
            abilities.flying = true
            player.onUpdateAbilities()
        }
    }

    private fun releaseForcedFlight() {
        val player = net.minecraft.client.Minecraft.getInstance().player ?: return
        val abilities = player.abilities
        if (forcedFlying && abilities.flying) {
            abilities.flying = false
            player.onUpdateAbilities()
        }
        forcedFlying = false
    }

    private fun stopCommand(status: PathStatus, distanceToTarget: Float): PathCommand =
        PathCommand(
            forward = false,
            back = false,
            left = false,
            right = false,
            jump = false,
            sneak = false,
            sprint = false,
            targetYaw = 0f,
            targetPitch = 0f,
            status = status,
            activeAction = ActionType.FLY,
            distanceToTarget = distanceToTarget,
        )

    private fun willArriveAtDestinationAfterStopping(player: LocalPlayer, targetPos: Vec3): Boolean {
        val predicted = predictStoppingPosition(player, PREDICT_TICKS)
        return predicted.distanceToSqr(targetPos) <= STOPPING_DISTANCE_THRESHOLD * STOPPING_DISTANCE_THRESHOLD
    }

    private fun shouldFinishDeceleration(player: LocalPlayer, finalTarget: Vec3): Boolean {
        val motion = player.deltaMovement
        val slowEnough =
            abs(motion.x) <= MOTION_STOP_THRESHOLD_XZ &&
                abs(motion.z) <= MOTION_STOP_THRESHOLD_XZ &&
                abs(motion.y) <= MOTION_STOP_THRESHOLD_Y
        if (!slowEnough) return false
        return player.position().distanceToSqr(finalTarget) <=
            STOPPING_DISTANCE_THRESHOLD * STOPPING_DISTANCE_THRESHOLD * 2.25
    }

    private fun predictStoppingPosition(player: LocalPlayer, ticks: Int): Vec3 {
        var px = player.x
        var py = player.y
        var pz = player.z
        var velocity = player.deltaMovement

        for (i in 0 until ticks) {
            px += velocity.x
            py += velocity.y
            pz += velocity.z
            velocity = if (player.abilities.flying) {
                Vec3(velocity.x * HORIZONTAL_DRAG, velocity.y * FLYING_VERTICAL_DRAG, velocity.z * HORIZONTAL_DRAG)
            } else {
                Vec3(velocity.x * HORIZONTAL_DRAG, (velocity.y + GRAVITY) * FALLING_VERTICAL_DRAG, velocity.z * HORIZONTAL_DRAG)
            }
            if (abs(velocity.x) < EPSILON && abs(velocity.z) < EPSILON && abs(velocity.y) < EPSILON) {
                break
            }
        }

        return Vec3(px, py, pz)
    }

    private fun getMovementTarget(current: Vec3, path: List<Vec3>, speedXZ: Double): FlyTargetInfo? {
        if (path.isEmpty()) return null
        if (path.size == 1) return FlyTargetInfo(path[0], path[0])

        var closestIndex = currentIndex
        var closestDistSq = Double.POSITIVE_INFINITY
        val searchStart = maxOf(0, currentIndex - 10)
        val searchEnd = minOf(path.lastIndex, currentIndex + 30)
        for (i in searchStart..searchEnd) {
            val dist = horizontalDistanceSq(path[i], current)
            if (dist < closestDistSq) {
                closestDistSq = dist
                closestIndex = i
            }
        }

        currentIndex = closestIndex
        val dynamicLookahead = (MOVE_TARGET_LOOKAHEAD + speedXZ * 8.0).toInt()
        val moveIndex = minOf(path.lastIndex, currentIndex + dynamicLookahead)
        val verticalLookahead = minOf(path.lastIndex, currentIndex + maxOf(1, (1.0 + speedXZ * 2.0).toInt()))
        return FlyTargetInfo(path[moveIndex], path[verticalLookahead])
    }

    private fun movementKeysForYaw(playerYaw: Float, desiredYaw: Float, distSqXZ: Double): FlyKeys {
        val yawDelta = AngleUtils.getRotationDelta(playerYaw, desiredYaw)
        val absYawDelta = abs(yawDelta)
        if (distSqXZ < 0.15) return FlyKeys(forward = true)

        val forward = absYawDelta < 75f
        val back = absYawDelta > 145f
        val useStrafe = distSqXZ > LATERAL_DEADZONE * LATERAL_DEADZONE * 2.25 &&
            absYawDelta > 25f &&
            absYawDelta < 155f
        val right = useStrafe && yawDelta > 0f && absYawDelta > 45f
        val left = useStrafe && yawDelta < 0f && absYawDelta > 45f
        return FlyKeys(forward = forward, back = back, left = left, right = right)
    }

    private fun yawToTarget(dx: Double, dz: Double): Float =
        -Math.toDegrees(atan2(dx, dz)).toFloat()

    private fun horizontalDistanceSq(a: Vec3, b: Vec3): Double {
        val dx = a.x - b.x
        val dz = a.z - b.z
        return dx * dx + dz * dz
    }

    private fun shouldBoostFromGround(player: LocalPlayer, next: Vec3): Boolean =
        player.onGround() && next.y - player.y > 0.5

    private data class FlyTargetInfo(val movementTarget: Vec3, val verticalTarget: Vec3)
    private data class FlyKeys(
        val forward: Boolean = false,
        val back: Boolean = false,
        val left: Boolean = false,
        val right: Boolean = false,
    )

    private const val PREDICT_TICKS = 30
    private const val STOPPING_DISTANCE_THRESHOLD = 0.85
    private const val MOTION_STOP_THRESHOLD_XZ = 0.05
    private const val MOTION_STOP_THRESHOLD_Y = 0.02
    private const val MAX_DECEL_TICKS = 60
    private const val MOVE_TARGET_LOOKAHEAD = 6
    private const val LATERAL_DEADZONE = 0.55
    private const val VERTICAL_DEADZONE = 0.55
    private const val VERTICAL_DEADZONE_MOVING = 0.75
    private const val HORIZONTAL_DRAG = 0.91
    private const val FLYING_VERTICAL_DRAG = 0.6
    private const val FALLING_VERTICAL_DRAG = 0.98
    private const val GRAVITY = -0.08
    private const val EPSILON = 0.01
}
