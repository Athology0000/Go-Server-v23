package org.cobalt.api.pathfinder.jni

import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Half
import net.minecraft.world.phys.Vec3
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.util.helper.Rotation
import org.cobalt.api.util.player.MovementManager
import kotlin.math.abs
import kotlin.math.sqrt

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

        // Decrement jump suppression each tick (V5 PathJumps tick callback)
        if (PathExecutorState.jumpSuppressTicks > 0) PathExecutorState.jumpSuppressTicks--

        val shouldJump = jump || (player != null && detectJump(player))
        val movement = resolveMovementInputs(player, targetRotation, effectiveMovementYaw)

        // Old DUSk/V5 execution core: take the movement lock and write raw forced inputs.
        // Do not let the newer owner layer reject PATHFINDER while mining/combat wrappers are active.
        MovementManager.setMovementLock(true)
        MovementManager.setForcedMovement(
            movement.forward,
            movement.backward,
            left = movement.left,
            right = movement.right,
            jump = shouldJump,
            shift = sneak,
            sprint = movement.sprint
        )
        player?.setSprinting(movement.sprint)
        if (applyRotation) {
            MovementManager.setLookLock(true)
            RotationExecutor.rotateTo(targetRotation, PathfinderRotationStrategy)
        } else if (RotationExecutor.isUsing(PathfinderRotationStrategy)) {
            MovementManager.setLookLock(false)
            RotationExecutor.stopIfUsing(PathfinderRotationStrategy)
        }
    }

    // ── Rotation ──────────────────────────────────────────────────────────

    /**
     * For ground movement, returns the rawTargetYaw/Pitch that PathExecutorState.update()
     * computed this tick (adaptive lookahead + visibility check).
     * AOTV is still walking movement in V5: the path rotation stays guided, and
     * the item use only fires when that aim is already lined up with the path.
     */
    private fun resolveGuidedRotation(player: net.minecraft.client.player.LocalPlayer): Rotation {
        if (!usesGroundMovement()) return Rotation(targetYaw, targetPitch)
        return Rotation(PathExecutorState.rawTargetYaw, PathExecutorState.rawTargetPitch)
    }

    // ── Movement inputs ───────────────────────────────────────────────────

    private fun resolveMovementInputs(
        player: net.minecraft.client.player.LocalPlayer?,
        @Suppress("UNUSED_PARAMETER")
        targetRotation: Rotation,
        @Suppress("UNUSED_PARAMETER")
        movementYawOverride: Float? = null
    ): MovementInputs {
        if (player == null || !usesGroundMovement()) {
            val shouldSprint = sprint && forward && !back
            return MovementInputs(forward, back, left, right, shouldSprint)
        }

        // V5 PathMovement does not solve strafe vectors while walking; it holds W
        // and lets PathRotations steer. The old mixed solver could orbit when its
        // sparse guide node disagreed with the rotation look point.
        return MovementInputs(
            forward = forward,
            backward = back,
            left = false,
            right = false,
            sprint = sprint && forward && !back && !player.horizontalCollision
        )
    }

    private fun detectJump(player: LocalPlayer): Boolean {
        val state = PathExecutorState
        blockCache.clear()

        /**
         * Port of V5 PathJumps.detectJump(). Exact V5 check order:
         *   suppress ticks → FLAG_LOW_HEADROOM → fluid → not-on-ground guard →
         *   low ceiling → FLAG_STEP_UP_NEXT + preemptive climb → gap → snow → obstacle
         */
        if (state.jumpSuppressTicks > 0) return false

        val mc = Minecraft.getInstance()
        val level = mc.level ?: return false
        val nodes = NativePathfinder.cachedPathNodes
        if (nodes.isEmpty()) return false

        val cursor = NativePathfinder.pathNodeCursor
        val nearIdx = cursor.coerceIn(0, nodes.lastIndex)

        // 1. FLAG_LOW_HEADROOM from native node flags → suppress jump (V5 nextFlags & FLAG_LOW_HEADROOM)
        val nextFlags = NativePathfinder.cachedKeyNodeFlags.getOrElse(
            minOf(nearIdx + 1, nodes.lastIndex)
        ) { 0 }
        if (nextFlags and FLAG_LOW_HEADROOM != 0) {
            state.jumpSuppressTicks = 4
            return false
        }

        // 2. Fluid — V5 checkFluidJump: continuously hold jump in water or lava
        if (player.isInWater || player.isInLava) return true

        // 3. Not on ground — release jump unless recovering (V5 returns early here)
        if (!player.onGround()) return false

        // 4. Low ceiling — solid collision at feet+2 or feet+3 (V5 hasLowCeiling)
        val fx = player.blockX; val fy = player.blockY; val fz = player.blockZ
        if (hasLowCeiling(level, fx, fy, fz)) {
            state.jumpSuppressTicks = 4
            return false
        }

        val playerFloorY = Math.floor(player.y - 0.001).toInt()

        // 5. FLAG_STEP_UP_NEXT guard + preemptive climb (V5: only fires when flag set)
        if (nextFlags and FLAG_STEP_UP_NEXT != 0) {
            if (checkPreemptiveClimbJump(player, nodes, nearIdx, playerFloorY)) return true
        }

        // 6. Gap jump (V5 checkGapJump)
        if (checkGapJump(player, nodes, nearIdx, playerFloorY)) return true

        // 7. Snow jump (V5 checkSnowJump)
        if (checkSnowJump(level, nodes, nearIdx, playerFloorY)) return true

        // 8. Obstacle jump (V5 checkObstacleJump)
        if (checkObstacleJump(level, player, nodes, nearIdx, playerFloorY)) return true

        return false
    }

    /** V5 hasLowCeiling: checks feet+2 and feet+3 for non-stair solid collision. */
    private fun hasLowCeiling(level: Level, x: Int, y: Int, z: Int): Boolean {
        for (offset in 2..3) {
            val pos = BlockPos(x, y + offset, z)
            val bs = cachedBlock(level, pos)
            if (bs.block is StairBlock && bs.getValue(StairBlock.HALF) == Half.BOTTOM) continue
            if (!bs.getCollisionShape(level, pos).isEmpty) return true
        }
        return false
    }

    /**
     * V5 checkPreemptiveClimbJump:
     * next node must be strictly higher than current node,
     * player must be within PREEMPTIVE_JUMP_DISTANCE (1.65) horizontally,
     * and rise from playerFloorY must be 1 or 2 blocks.
     */
    private fun checkPreemptiveClimbJump(
        player: net.minecraft.client.player.LocalPlayer,
        nodes: List<Vec3>,
        nearIdx: Int,
        playerFloorY: Int
    ): Boolean {
        if (nearIdx + 1 > nodes.lastIndex) return false
        val curr = nodes[nearIdx]; val next = nodes[nearIdx + 1]
        if (next.y <= curr.y) return false        // V5: nextNode.y <= currentNode.y → return false
        val rise = Math.round(next.y).toInt() - playerFloorY
        if (rise !in 1..2) return false
        val tdx = next.x + 0.5 - player.x; val tdz = next.z + 0.5 - player.z
        if (tdx * tdx + tdz * tdz > PREEMPTIVE_JUMP_DIST_SQ) return false
        return jumpTrue(JUMP_SUPPRESS_TICKS)
    }

    /**
     * V5 checkGapJump:
     * scans for a path that drops below baseY then recovers ≥ baseY within 7 nodes,
     * with horizontal gap ≤ sqrt(20) ≈ 4.5 blocks, triggers when player is within
     * 1.35 blocks of the last node before the drop.
     */
    private fun checkGapJump(
        player: net.minecraft.client.player.LocalPlayer,
        nodes: List<Vec3>,
        nearIdx: Int,
        @Suppress("UNUSED_PARAMETER") playerFloorY: Int
    ): Boolean {
        val baseY = Math.round(nodes[nearIdx].y).toInt()
        // V5: player must be at roughly baseY+1 height
        if (abs(player.y - (baseY + 1)) > 1.8) return false
        val gapEnd = minOf(nodes.lastIndex, nearIdx + 7)
        var dropIdx = -1; var recoveryIdx = -1
        for (i in (nearIdx + 1)..gapEnd) {
            val ny = Math.round(nodes[i].y).toInt()
            when {
                ny < baseY && dropIdx == -1 -> dropIdx = i
                dropIdx != -1 && ny >= baseY -> { recoveryIdx = i; break }
                ny > baseY && dropIdx == -1 -> return false
            }
        }
        if (dropIdx == -1 || recoveryIdx == -1) return false
        val edgeNode = nodes[dropIdx - 1]; val recovNode = nodes[recoveryIdx]
        val gdx = recovNode.x - edgeNode.x; val gdz = recovNode.z - edgeNode.z
        val gapDistSq = gdx * gdx + gdz * gdz
        if (gapDistSq <= 0.0 || gapDistSq > 20.0) return false
        val pdx = player.x - (edgeNode.x + 0.5); val pdz = player.z - (edgeNode.z + 0.5)
        if (pdx * pdx + pdz * pdz > GAP_TRIGGER_DIST_SQ) return false
        return jumpTrue(JUMP_SUPPRESS_TICKS)
    }

    /**
     * V5 checkSnowJump: upcoming node is a snow block with ≥ 7 layers and
     * effective height diff > 0.75.
     */
    private fun checkSnowJump(
        level: Level,
        nodes: List<Vec3>,
        nearIdx: Int,
        playerFloorY: Int
    ): Boolean {
        val nextIdx = (nearIdx + 1).coerceAtMost(nodes.lastIndex)
        val n = nodes[nextIdx]

        val floorPos = BlockPos(
            Math.floor(n.x).toInt(),
            Math.round(n.y).toInt(),
            Math.floor(n.z).toInt()
        )

        val bs = cachedBlock(level, floorPos)
        if (!bs.block.descriptionId.contains("snow")) return false

        val shape = bs.getCollisionShape(level, floorPos)
        if (shape.isEmpty) return false
        val topY = shape.bounds().maxY   // 0..1 range
        val heightAboveFloor = (Math.round(n.y).toInt() + topY) - (playerFloorY + 1)
        if (topY < 0.875 || heightAboveFloor <= 0.75) return false
        return jumpTrue(JUMP_SUPPRESS_TICKS)
    }

    /**
     * V5 checkObstacleJump: lookahead blocks higher than STEP_HEIGHT (0.6)
     * that cannot be walked up as stairs/slabs → jump.
     * Uses solid blocks at the floor Y of upcoming nodes (V5 drawPathAndPlayerLookAhead).
     */
    private fun checkObstacleJump(
        level: Level,
        player: LocalPlayer,
        nodes: List<Vec3>,
        nearIdx: Int,
        playerFloorY: Int
    ): Boolean {
        val standingPos = BlockPos(
            Math.floor(player.x).toInt(),
            playerFloorY,
            Math.floor(player.z).toInt()
        )

        val standingBlock = cachedBlock(level, standingPos).block
        val standingOnPartial = standingBlock is StairBlock || standingBlock is SlabBlock
        val stepLimit = if (standingOnPartial) 1.05 else STEP_HEIGHT

        var needsJump = false
        var canWalkInstead = false
        val lookEnd = minOf(nodes.lastIndex, nearIdx + LOOKAHEAD_NODES)
        for (i in (nearIdx + 1)..lookEnd) {
            val n = nodes[i]
            val floorY = Math.round(n.y).toInt()
            val floorPos = BlockPos(
                Math.floor(n.x).toInt(),
                floorY,
                Math.floor(n.z).toInt()
            )

            val bs = cachedBlock(level, floorPos)
            val block = bs.block
            val name = block.descriptionId

            if (name.contains("snow")) continue
            if (bs.getCollisionShape(level, floorPos).isEmpty) continue

            if (floorY - playerFloorY > stepLimit) needsJump = true

            when {
                name.contains("slab") -> canWalkInstead = true
                name.contains("stair") -> {
                    // Only bottom stairs are walkable ramps; top-half stairs need a jump.
                    if (block is StairBlock) {
                        try {
                            if (bs.getValue(StairBlock.HALF) != Half.BOTTOM) continue
                            val facing = bs.getValue(StairBlock.FACING)
                            val dx = n.x + 0.5 - player.x
                            val dz = n.z + 0.5 - player.z
                            val approach = if (abs(dx) > abs(dz)) {
                                if (dx > 0) Direction.WEST else Direction.EAST
                            } else {
                                if (dz > 0) Direction.NORTH else Direction.SOUTH
                            }
                            if (approach == facing.opposite) canWalkInstead = true
                        } catch (_: Exception) {
                        }
                    } else {
                        canWalkInstead = true
                    }
                }
            }
        }
        if (!needsJump || canWalkInstead) return false
        return jumpTrue(JUMP_SUPPRESS_TICKS)
    }

    private fun jumpTrue(suppressTicks: Int): Boolean {
        PathExecutorState.jumpSuppressTicks = suppressTicks
        return true
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private fun usesGroundMovement(): Boolean =
        activeAction == ActionType.WALK ||
            activeAction == ActionType.SPRINT ||
            activeAction == ActionType.JUMP ||
            activeAction == ActionType.SPRINT_JUMP ||
            activeAction == ActionType.AOTV

    companion object {
        // Per-node flag bits (mirrored from NativePathfinder)
        private const val FLAG_LOW_HEADROOM   = 1 shl 2
        private const val FLAG_STEP_UP_NEXT   = 1 shl 5

        // Jump detection (V5 values)
        private const val STEP_HEIGHT = 0.6
        private const val LOOKAHEAD_NODES = 3           // V5 LOOKAHEAD_NODES
        private const val PREEMPTIVE_JUMP_DISTANCE = 1.65
        private const val PREEMPTIVE_JUMP_DIST_SQ = PREEMPTIVE_JUMP_DISTANCE * PREEMPTIVE_JUMP_DISTANCE
        private const val GAP_TRIGGER_DIST_SQ = 1.35 * 1.35
        private const val JUMP_SUPPRESS_TICKS = 3

        // Per-tick block state cache shared across all jump checks in one detectJump call
        private val blockCache = HashMap<Long, BlockState>(32)

        private fun cachedBlock(level: Level, pos: BlockPos): BlockState {
            val key = pos.asLong()
            return blockCache.getOrPut(key) { level.getBlockState(pos) }
        }
    }
}

private data class MovementInputs(
    val forward: Boolean,
    val backward: Boolean,
    val left: Boolean,
    val right: Boolean,
    val sprint: Boolean,
)
