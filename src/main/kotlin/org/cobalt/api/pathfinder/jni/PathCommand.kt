package org.cobalt.api.pathfinder.jni

import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.util.helper.Rotation
import org.cobalt.api.util.player.MovementManager

data class PathCommand(
    val forward: Boolean,
    val back: Boolean,
    val jump: Boolean,
    val sneak: Boolean,
    val sprint: Boolean,
    val targetYaw: Float,
    val targetPitch: Float,
    val status: PathStatus,
    val activeAction: ActionType,
    val distanceToTarget: Float,
) {
    fun applyToPlayer() {
        MovementManager.setMovementLock(true)
        MovementManager.setForcedMovement(
            forward, back,
            left = false, right = false,
            jump, shift = sneak, sprint
        )
        RotationExecutor.rotateTo(Rotation(targetYaw, targetPitch), PathfinderRotationStrategy)
    }
}
