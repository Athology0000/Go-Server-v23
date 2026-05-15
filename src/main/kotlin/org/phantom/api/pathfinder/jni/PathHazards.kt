package org.phantom.api.pathfinder.jni

import kotlin.math.floor
import net.minecraft.core.BlockPos
import net.minecraft.tags.FluidTags
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

internal object PathHazards {

    fun walkPosForNode(node: Vec3): BlockPos {
        return BlockPos(
            floor(node.x).toInt(),
            floor(node.y).toInt(),
            floor(node.z).toInt()
        )
    }

    fun isHarmfulStandPosition(level: Level, feetPos: BlockPos): Boolean {
        return isHazardState(level.getBlockState(feetPos)) ||
            isHazardState(level.getBlockState(feetPos.below()))
    }

    fun isReasonableLandingPosition(level: Level, feetPos: BlockPos): Boolean {
        if (isHarmfulStandPosition(level, feetPos)) return false
        return isPassable(level, feetPos) &&
            isPassable(level, feetPos.above()) &&
            isStandable(level, feetPos.below())
    }

    fun isSafeTeleportSupport(level: Level, supportPos: BlockPos): Boolean {
        if (isHazardState(level.getBlockState(supportPos))) return false
        val feetPos = teleportFeetPos(level, supportPos)
        return isStandable(level, supportPos) &&
            isPassable(level, feetPos) &&
            isPassable(level, feetPos.above()) &&
            !isHarmfulStandPosition(level, feetPos)
    }

    fun teleportFeetPos(level: Level, supportPos: BlockPos): BlockPos {
        return supportPos.above()
    }

    private fun isPassable(level: Level, pos: BlockPos): Boolean {
        val state = level.getBlockState(pos)
        return state.fluidState.isEmpty && state.getCollisionShape(level, pos).isEmpty
    }

    private fun isStandable(level: Level, pos: BlockPos): Boolean {
        val state = level.getBlockState(pos)
        return state.fluidState.isEmpty && !state.getCollisionShape(level, pos).isEmpty
    }

    private fun isHazardState(state: BlockState): Boolean {
        if (state.fluidState.`is`(FluidTags.LAVA)) return true

        return state.`is`(Blocks.LAVA) ||
            state.`is`(Blocks.FIRE) ||
            state.`is`(Blocks.SOUL_FIRE) ||
            state.`is`(Blocks.CAMPFIRE) ||
            state.`is`(Blocks.SOUL_CAMPFIRE) ||
            state.`is`(Blocks.MAGMA_BLOCK) ||
            state.`is`(Blocks.CACTUS) ||
            state.`is`(Blocks.WITHER_ROSE) ||
            state.`is`(Blocks.SWEET_BERRY_BUSH) ||
            state.`is`(Blocks.POWDER_SNOW)
    }
}
