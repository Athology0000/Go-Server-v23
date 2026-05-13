package org.cobalt.api.pathfinder.jni

import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import org.cobalt.api.pathfinder.jni.executor.JumpDetector
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.helper.Rotation
import org.cobalt.api.util.player.MovementManager
import kotlin.math.abs

data class PathCommand(
    val forward: Boolean,
    val back: Boolean,
    val left: Boolean = false,
    val right: Boolean = false,
    val jump: Boolean,
    val sneak: Boolean,
    val sprint: Boolean,
    val targetYaw: Float,
    val targetPitch: Float,
    val status: PathStatus,
    val activeAction: ActionType,
    val distanceToTarget: Float,
) {
    fun applyToPlayer(applyRotation: Boolean = true, movementYawOverride: Float? = null) {
        val player = Minecraft.getInstance().player
        val targetRotation = player?.let(::resolveGuidedRotation) ?: Rotation(targetYaw, targetPitch)
        val effectiveMovementYaw =
            movementYawOverride ?: if (applyRotation) player?.yRot else null

        // Decrement jump suppression each tick (V5 PathJumps tick callback).
        if (PathExecutorState.jumpSuppressTicks > 0) PathExecutorState.jumpSuppressTicks--

        val shouldJump = jump || (player != null && JumpDetector.detectJump(player))
        val movement = resolveMovementInputs(player, targetRotation, effectiveMovementYaw)

        // Old DUSk/V5 execution core: take the movement lock and write raw forced inputs.
        // Do not let the newer owner layer reject PATHFINDER while mining/combat wrappers are active.
        // Sneak engage hysteresis: require N ticks of sustained "want sneak"
        // before actually engaging. Releases instantly when the signal stops,
        // so the player doesn't stay sneaked when the path demands a drop.
        val wantSneak = sneak || PathExecutorState.shouldUsePrecisionSneak
        if (wantSneak) {
            PathExecutorState.sneakRequestTicks = (PathExecutorState.sneakRequestTicks + 1).coerceAtMost(100)
        } else {
            PathExecutorState.sneakRequestTicks = 0
        }
        val effectiveSneak = wantSneak && PathExecutorState.sneakRequestTicks >= PathExecutorState.sneakEngageTicks

        // Precision profile: when sneak is engaged for safety, optionally also
        // hold sprint ("ninja sneak"). Sneak still dominates the actual move
        // speed in MC, but the sprint key being pressed maintains sprint state
        // (FOV bonus, sprint-attack flag, jump-boost on transitions). This is
        // the "slow mode = sprint-crouching" the user requested.
        val precisionSprintBoost = effectiveSneak && PathExecutorState.precisionUsesSprint && usesGroundMovement()
        val finalSprintKey = movement.sprint || precisionSprintBoost

        MovementManager.setMovementLock(true)
        MovementManager.setForcedMovement(
            movement.forward,
            movement.backward,
            left = movement.left,
            right = movement.right,
            jump = shouldJump,
            shift = effectiveSneak,
            sprint = finalSprintKey,
        )
        player?.setSprinting(finalSprintKey)
        if (applyRotation) {
            MovementManager.setLookLock(true)
            // Stop the legacy PD/humanization strategy if it was running from a prior
            // path tick — PathRotationStrategy now owns pathfinding rotation.
            RotationExecutor.stopIfUsing(PathfinderRotationStrategy)
            drivePathRotation(player)
        } else {
            MovementManager.setLookLock(false)
            RotationExecutor.stopIfUsing(PathfinderRotationStrategy)
            // Soft-release: let the smoother finish converging to the last
            // path aim over a few frames instead of cutting it dead, which
            // produced a visible "clamp" at the end of the path.
            if (PathExecutorState.pathRotationOwned) {
                PathRotationStrategy.requestSoftRelease(maxFrames = 20)
                PathExecutorState.pathRotationOwned = false
                PathExecutorState.pathRotationLastTarget = null
            }
        }
    }

    /**
     * For ground movement, returns the rawTargetYaw/Pitch that PathExecutorState.update()
     * computed this tick (adaptive lookahead + visibility check). AOTV is still walking
     * movement in V5: the path rotation stays guided and the item use only fires when
     * that aim is already lined up with the path.
     */
    private fun resolveGuidedRotation(@Suppress("UNUSED_PARAMETER") player: LocalPlayer): Rotation {
        if (!usesGroundMovement()) return Rotation(targetYaw, targetPitch)
        return Rotation(PathExecutorState.rawTargetYaw, PathExecutorState.rawTargetPitch)
    }

    private fun resolveMovementInputs(
        player: LocalPlayer?,
        @Suppress("UNUSED_PARAMETER") targetRotation: Rotation,
        @Suppress("UNUSED_PARAMETER") movementYawOverride: Float? = null,
    ): MovementInputs {
        if (player == null || !usesGroundMovement()) {
            val shouldSprint = sprint && forward && !back
            return MovementInputs(forward, back, left, right, shouldSprint)
        }

        // V5 PathMovement does not solve strafe vectors while walking; it holds W
        // and lets PathRotations steer. The old mixed solver could orbit when its
        // sparse guide node disagreed with the rotation look point.
        //
        // Pre-emptive sprint brake: drop sprint when an upcoming turn (pathCurvature
        // computed from nodes ahead) exceeds the configured threshold.
        val effectiveSprintBrakeCurvature = PathExecutorState.sprintBrakeCurvature.coerceAtLeast(1.2)
        val curvatureBrake = PathExecutorState.pathCurvature >= effectiveSprintBrakeCurvature

        // Yaw-alignment gate on forward: if the player's current facing is way
        // off from the desired travel direction (rotation still catching up after
        // teleport / sharp replan), don't press W — let the rotation align first
        // so we don't walk sideways into a wall.
        val yawError = AngleUtils.getRotationDelta(player.yRot, PathExecutorState.rawTargetYaw)
        val alignedForward = abs(yawError).toDouble() <= PathExecutorState.forwardYawTolerance
        val finalForward = forward && alignedForward

        // Sprint engage hysteresis: instant release, but require N stable ticks
        // before re-engaging. Eliminates tick-to-tick flicker from horizontal
        // collision / curvature crossing the brake threshold.
        val rawWantSprint = sprint && finalForward && !back && !player.horizontalCollision && !curvatureBrake
        if (rawWantSprint) {
            PathExecutorState.sprintReadyTicks = (PathExecutorState.sprintReadyTicks + 1).coerceAtMost(100)
        } else {
            PathExecutorState.sprintReadyTicks = 0
        }
        val finalSprint = rawWantSprint && PathExecutorState.sprintReadyTicks >= PathExecutorState.sprintEngageTicks

        return MovementInputs(
            forward = finalForward,
            backward = back,
            left = false,
            right = false,
            sprint = finalSprint,
        )
    }

    /**
     * Pathfinding rotation: feeds the live lookahead aim into PathRotationStrategy
     * via the central RotationExecutor. The strategy's exp-smoother runs each
     * render frame with decoupled yaw/pitch rates and anticipation terms; all
     * tuning lives on PathRotationConfig (driven by PathfindingModule sliders).
     *
     * The yaw/pitch used here are the values the native executor already
     * solved this tick (rawTargetYaw/Pitch via [resolveGuidedRotation], or the
     * raw PathCommand fields when air-walking / AOTV-ing).
     */
    private fun drivePathRotation(player: LocalPlayer?) {
        if (player == null) return

        val aim = resolveGuidedRotation(player)
        RotationExecutor.rotateTo(Rotation(aim.yaw, aim.pitch), PathRotationStrategy)

        PathExecutorState.pathRotationOwned = true
        if (PathExecutorState.pathRotationLastTarget == null) {
            PathExecutorState.pathRotationLastTarget = player.blockPosition()
        }
    }

    private fun usesGroundMovement(): Boolean =
        activeAction == ActionType.WALK ||
            activeAction == ActionType.SPRINT ||
            activeAction == ActionType.JUMP ||
            activeAction == ActionType.SPRINT_JUMP ||
            activeAction == ActionType.AOTV
}

private data class MovementInputs(
    val forward: Boolean,
    val backward: Boolean,
    val left: Boolean,
    val right: Boolean,
    val sprint: Boolean,
)
