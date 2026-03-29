package org.cobalt.api.pathfinder.jni

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.helper.Rotation
import org.cobalt.api.util.player.MovementManager
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import net.minecraft.world.phys.Vec3

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
        val player = Minecraft.getInstance().player
        val targetRotation = player?.let(::resolveGuidedRotation) ?: Rotation(targetYaw, targetPitch)
        val shouldJump = jump || shouldAssistJump()
        val shouldMoveForward = forward && shouldAllowForward(player, targetRotation)
        val shouldMoveBack = back && shouldAllowBackward(player, targetRotation)
        MovementManager.setMovementLock(true)
        MovementManager.setForcedMovement(
            shouldMoveForward, shouldMoveBack,
            left = false, right = false,
            shouldJump, shift = sneak, sprint
        )
        RotationExecutor.rotateTo(targetRotation, PathfinderRotationStrategy)
    }

    private fun shouldAssistJump(): Boolean {
        // Only jump when the C++ planner explicitly generated a JUMP action.
        // The proactive lookahead (rise >= threshold) was removed because it caused
        // spurious jumps on slabs and step-up WALK edges — Minecraft auto-steps those.
        return activeAction == ActionType.JUMP || activeAction == ActionType.SPRINT_JUMP
    }

    private fun shouldAllowForward(player: net.minecraft.client.player.LocalPlayer?, targetRotation: Rotation): Boolean {
        if (player == null) return true
        if (!usesGroundMovement()) return true
        val yawError = abs(AngleUtils.getRotationDelta(player.yRot, targetRotation.yaw))
        val maxYaw = if (distanceToTarget <= CLOSE_ALIGN_DISTANCE) FORWARD_ALIGNMENT_YAW_CLOSE else FORWARD_ALIGNMENT_YAW
        return yawError <= maxYaw
    }

    private fun shouldAllowBackward(player: net.minecraft.client.player.LocalPlayer?, targetRotation: Rotation): Boolean {
        if (player == null) return true
        if (!usesGroundMovement()) return true
        val yawError = abs(AngleUtils.getRotationDelta(player.yRot, targetRotation.yaw))
        return yawError <= BACKWARD_ALIGNMENT_YAW
    }

    private fun resolveGuidedRotation(player: net.minecraft.client.player.LocalPlayer): Rotation {
        if (!usesGroundMovement()) return Rotation(targetYaw, targetPitch)
        val guide = resolveGuidePoint(player) ?: return Rotation(targetYaw, targetPitch)
        return AngleUtils.getRotation(player.eyePosition, guide)
    }

    private fun resolveGuidePoint(player: net.minecraft.client.player.LocalPlayer): Vec3? {
        val nodes = NativePathfinder.cachedPathNodes
        if (nodes.isEmpty()) return null

        val nearestIndex = nearestNodeIndex(player, nodes)
        if (nearestIndex < 0) return null

        val guideIndex = min(nodes.lastIndex, nearestIndex + LOOKAHEAD_NODE_COUNT)
        val baseNode = nodes[guideIndex]
        val direction = nodeDirection(nodes, guideIndex)
        val centered = centerTunnelNode(baseNode, direction)
        val guideY = max(baseNode.y + GUIDE_LOOK_HEIGHT, player.eyePosition.y)
        return Vec3(centered.x, guideY, centered.z)
    }

    private fun nearestNodeIndex(
        player: net.minecraft.client.player.LocalPlayer,
        nodes: List<Vec3>
    ): Int {
        var nearestIndex = -1
        var nearestDistSq = Double.POSITIVE_INFINITY
        val px = player.x
        val pz = player.z
        for (index in nodes.indices) {
            val node = nodes[index]
            val dx = node.x - px
            val dz = node.z - pz
            val distSq = dx * dx + dz * dz
            if (distSq < nearestDistSq) {
                nearestDistSq = distSq
                nearestIndex = index
            }
        }
        return nearestIndex
    }

    private fun firstUpcomingTraversalNode(
        player: net.minecraft.client.player.LocalPlayer,
        nodes: List<Vec3>,
        nearestIndex: Int
    ): Int? {
        val px = player.x
        val pz = player.z
        val nearest = nodes[nearestIndex]
        val nearestDx = nearest.x - px
        val nearestDz = nearest.z - pz
        val nearestHorizontalDistSq = nearestDx * nearestDx + nearestDz * nearestDz
        val startIndex =
            if (nearestHorizontalDistSq <= AUTO_JUMP_CURRENT_NODE_DISTANCE_SQ && nearestIndex < nodes.lastIndex) {
                nearestIndex + 1
            } else {
                nearestIndex
            }

        val endIndex = min(nodes.lastIndex, startIndex + AUTO_JUMP_NODE_LOOKAHEAD)
        for (index in startIndex..endIndex) {
            val node = nodes[index]
            val dx = node.x - px
            val dz = node.z - pz
            val horizontalDistSq = dx * dx + dz * dz
            if (horizontalDistSq < AUTO_JUMP_MIN_NODE_DISTANCE_SQ) continue
            if (horizontalDistSq > AUTO_JUMP_MAX_NODE_DISTANCE_SQ) return null
            return index
        }
        return null
    }

    private fun nodeDirection(nodes: List<Vec3>, index: Int): Vec3 {
        val prev = nodes[max(0, index - 1)]
        val next = nodes[min(nodes.lastIndex, index + 1)]
        return Vec3(next.x - prev.x, next.y - prev.y, next.z - prev.z)
    }

    private fun centerTunnelNode(baseNode: Vec3, direction: Vec3): Vec3 {
        val level = Minecraft.getInstance().level ?: return baseNode
        val basePos = BlockPos.containing(baseNode.x, baseNode.y, baseNode.z)
        val absDx = abs(direction.x)
        val absDz = abs(direction.z)

        if (absDx < MIN_DIRECTION_MAG && absDz < MIN_DIRECTION_MAG) return baseNode

        return when {
            absDz >= absDx * STRAIGHT_AXIS_RATIO -> {
                val minX = scanContiguousWalkable(level, basePos, -1, 0)
                val maxX = scanContiguousWalkable(level, basePos, 1, 0)
                val centerX = (minX + maxX + 1) / 2.0
                Vec3(centerX, basePos.y.toDouble(), basePos.z + 0.5)
            }
            absDx >= absDz * STRAIGHT_AXIS_RATIO -> {
                val minZ = scanContiguousWalkable(level, basePos, 0, -1)
                val maxZ = scanContiguousWalkable(level, basePos, 0, 1)
                val centerZ = (minZ + maxZ + 1) / 2.0
                Vec3(basePos.x + 0.5, basePos.y.toDouble(), centerZ)
            }
            else -> baseNode
        }
    }

    private fun scanContiguousWalkable(
        level: net.minecraft.world.level.Level,
        origin: BlockPos,
        stepX: Int,
        stepZ: Int
    ): Int {
        var current = origin
        for (@Suppress("UNUSED_VARIABLE") step in 1..CENTER_SCAN_RADIUS) {
            val next = current.offset(stepX, 0, stepZ)
            if (!MinecraftPathingRules.isWalkable(level, next)) break
            current = next
        }
        return if (stepX != 0) current.x else current.z
    }

    private fun usesGroundMovement(): Boolean =
        activeAction == ActionType.WALK ||
            activeAction == ActionType.SPRINT ||
            activeAction == ActionType.JUMP ||
            activeAction == ActionType.SPRINT_JUMP

    companion object {
        private const val AUTO_JUMP_MIN_RISE = 0.45
        private const val AUTO_JUMP_MAX_RISE = 1.25
        private const val AUTO_JUMP_CURRENT_NODE_DISTANCE_SQ = 0.25
        private const val AUTO_JUMP_MIN_NODE_DISTANCE_SQ = 0.04
        private const val AUTO_JUMP_MAX_NODE_DISTANCE_SQ = 2.25
        private const val AUTO_JUMP_NODE_LOOKAHEAD = 2
        private const val FORWARD_ALIGNMENT_YAW = 18f
        private const val FORWARD_ALIGNMENT_YAW_CLOSE = 28f
        private const val BACKWARD_ALIGNMENT_YAW = 24f
        private const val CLOSE_ALIGN_DISTANCE = 1.6f
        private const val LOOKAHEAD_NODE_COUNT = 2
        private const val CENTER_SCAN_RADIUS = 3
        private const val STRAIGHT_AXIS_RATIO = 1.5
        private const val MIN_DIRECTION_MAG = 0.05
        private const val GUIDE_LOOK_HEIGHT = 0.55
    }
}
