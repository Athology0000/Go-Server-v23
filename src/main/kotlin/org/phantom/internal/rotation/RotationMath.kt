package org.phantom.internal.rotation

import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import kotlin.math.atan2
import kotlin.math.sqrt

object RotationMath {

    data class Rotation(
        val yaw: Float,
        val pitch: Float
    )

    fun rotationToPoint(playerEyePos: Vec3, target: Vec3): Rotation {
        val dx = target.x - playerEyePos.x
        val dy = target.y - playerEyePos.y
        val dz = target.z - playerEyePos.z

        val horizontalDistance = sqrt(dx * dx + dz * dz)

        val yaw = Math.toDegrees(atan2(dz, dx)).toFloat() - 90f
        val pitch = -Math.toDegrees(atan2(dy, horizontalDistance)).toFloat()

        return Rotation(
            yaw = wrapDegrees(yaw),
            pitch = pitch.coerceIn(-90f, 90f)
        )
    }

    fun eyePos(player: Player): Vec3 {
        return Vec3(
            player.x,
            player.eyeY,
            player.z
        )
    }

    fun lerpAngleDegrees(start: Float, end: Float, progress: Double): Float {
        val delta = wrapDegrees(end - start)
        return wrapDegrees(start + (delta * progress).toFloat())
    }

    fun lerpFloat(start: Float, end: Float, progress: Double): Float {
        return start + ((end - start) * progress).toFloat()
    }

    fun wrapDegrees(value: Float): Float {
        var angle = value
        while (angle <= -180f) angle += 360f
        while (angle > 180f) angle -= 360f
        return angle
    }

    fun clampStep(current: Float, target: Float, maxStep: Float): Float {
        val diff = wrapDegrees(target - current)
        val clamped = diff.coerceIn(-maxStep, maxStep)
        return wrapDegrees(current + clamped)
    }

    fun clampPitchStep(current: Float, target: Float, maxStep: Float): Float {
        val diff = target - current
        val clamped = diff.coerceIn(-maxStep, maxStep)
        return (current + clamped).coerceIn(-90f, 90f)
    }
}
