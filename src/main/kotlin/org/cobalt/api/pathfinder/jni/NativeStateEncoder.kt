package org.cobalt.api.pathfinder.jni

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.AbstractSkullBlock
import net.minecraft.world.level.block.BannerBlock
import net.minecraft.world.level.block.BaseRailBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.BushBlock
import net.minecraft.world.level.block.ButtonBlock
import net.minecraft.world.level.block.CarpetBlock
import net.minecraft.world.level.block.ComparatorBlock
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.FenceBlock
import net.minecraft.world.level.block.FenceGateBlock
import net.minecraft.world.level.block.FlowerPotBlock
import net.minecraft.world.level.block.LadderBlock
import net.minecraft.world.level.block.LeverBlock
import net.minecraft.world.level.block.PressurePlateBlock
import net.minecraft.world.level.block.RedStoneWireBlock
import net.minecraft.world.level.block.SignBlock
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.SnowLayerBlock
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.TorchBlock
import net.minecraft.world.level.block.TrapDoorBlock
import net.minecraft.world.level.block.TripWireBlock
import net.minecraft.world.level.block.TripWireHookBlock
import net.minecraft.world.level.block.VineBlock
import net.minecraft.world.level.block.WallBannerBlock
import net.minecraft.world.level.block.WallBlock
import net.minecraft.world.level.block.WallSignBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Half
import net.minecraft.world.level.block.state.properties.SlabType
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.tags.FluidTags

object NativeStateEncoder {

    private const val DEFAULT_EMPTY_FLAGS =
        NativeVoxelFlags.PASSABLE or
            NativeVoxelFlags.PASSABLE_FLY or
            NativeVoxelFlags.ETHER_PASSABLE or
            NativeVoxelFlags.ETHER_TELEPORT_CLEAR

    private val ZERO = BlockPos.ZERO
    private val SHAPE_CONTEXT = CollisionContext.empty()

    private val stateCache = HashMap<BlockState, Short>(512)

    @JvmStatic
    fun flagsForStateId(stateId: Int): Short {
        if (stateId < 0 || stateId >= Block.BLOCK_STATE_REGISTRY.size()) {
            return DEFAULT_EMPTY_FLAGS.toShort()
        }
        val state = Block.BLOCK_STATE_REGISTRY.byId(stateId) ?: return DEFAULT_EMPTY_FLAGS.toShort()
        return flagsShortForState(state)
    }

    @JvmStatic
    fun flagsForState(state: BlockState): Int = flagsShortForState(state).toInt() and 0xFFFF

    @JvmStatic
    fun flagsShortForState(state: BlockState): Short {
        stateCache[state]?.let { return it }
        val flags = computeFlags(state).toShort()
        stateCache[state] = flags
        return flags
    }

    @JvmStatic
    fun flagsShortForStateId(stateId: Int): Short = flagsForStateId(stateId)

    private fun computeFlags(state: BlockState): Int {
        if (state.isAir) return DEFAULT_EMPTY_FLAGS

        var flags = 0
        val block = state.block
        val level = Minecraft.getInstance().level
        val collisionShape = if (level != null) {
            state.getCollisionShape(level, ZERO, SHAPE_CONTEXT)
        } else {
            null
        }

        if (!state.fluidState.isEmpty) {
            flags = flags or NativeVoxelFlags.FLUID
            if (state.fluidState.`is`(FluidTags.LAVA)) {
                flags = flags or NativeVoxelFlags.LAVA
            }
        }

        if (block is CarpetBlock) {
            return flags or NativeVoxelFlags.PASSABLE or NativeVoxelFlags.PASSABLE_FLY or NativeVoxelFlags.CARPET_LIKE
        }

        if (block is AbstractSkullBlock) {
            return flags or
                NativeVoxelFlags.PASSABLE or
                NativeVoxelFlags.PASSABLE_FLY or
                NativeVoxelFlags.ETHER_PASSABLE or
                NativeVoxelFlags.ETHER_FEET_BLOCKER
        }

        val isPassThrough = block is SlabBlock ||
            block is StairBlock ||
            block is DoorBlock ||
            block is TrapDoorBlock ||
            block is TorchBlock ||
            block is SignBlock ||
            block is WallSignBlock ||
            block is BushBlock ||
            block is BaseRailBlock ||
            block is VineBlock ||
            block is LadderBlock ||
            block is SnowLayerBlock ||
            block is PressurePlateBlock ||
            block is ButtonBlock ||
            block is RedStoneWireBlock ||
            block is LeverBlock ||
            block is BannerBlock ||
            block is WallBannerBlock ||
            block is TripWireBlock ||
            block is TripWireHookBlock

        val isFlyPassable = block is LadderBlock ||
            block is VineBlock ||
            block is BaseRailBlock ||
            block is SignBlock ||
            block is WallSignBlock ||
            block is BannerBlock ||
            block is WallBannerBlock ||
            block is TripWireBlock ||
            block is TripWireHookBlock ||
            block is LeverBlock ||
            block is ButtonBlock ||
            block is TorchBlock ||
            block is RedStoneWireBlock ||
            block is PressurePlateBlock

        if (isFlyPassable) {
            flags = flags or NativeVoxelFlags.PASSABLE_FLY
        }

        if (block is FenceBlock || block is FenceGateBlock || block is WallBlock) {
            flags = flags or NativeVoxelFlags.SOLID or NativeVoxelFlags.BLOCKING_WALL or NativeVoxelFlags.FENCE_LIKE
        }

        when (block) {
            is SlabBlock -> {
                flags = flags or NativeVoxelFlags.SOLID
                when (state.getValue(SlabBlock.TYPE)) {
                    SlabType.BOTTOM -> flags = flags or NativeVoxelFlags.SLAB_BOTTOM
                    SlabType.TOP -> flags = flags or NativeVoxelFlags.SLAB_TOP or NativeVoxelFlags.BLOCKING_WALL
                    SlabType.DOUBLE -> flags = flags or NativeVoxelFlags.BLOCKING_WALL
                    else -> flags = flags or NativeVoxelFlags.BLOCKING_WALL
                }
            }

            is StairBlock -> {
                flags = flags or NativeVoxelFlags.SOLID
                if (state.getValue(StairBlock.HALF) == Half.BOTTOM) {
                    flags = flags or NativeVoxelFlags.STAIRS_BOTTOM or stairFacingFlag(state.getValue(StairBlock.FACING))
                } else {
                    flags = flags or NativeVoxelFlags.BLOCKING_WALL
                }
            }

            else -> {
                if (collisionShape == null || collisionShape.isEmpty()) {
                    flags = flags or NativeVoxelFlags.PASSABLE
                } else {
                    flags = flags or NativeVoxelFlags.SOLID
                    val box = collisionShape.bounds()
                    if (box.maxY - box.minY >= 0.5 && !isPassThrough) {
                        flags = flags or NativeVoxelFlags.BLOCKING_WALL
                    }
                }
            }
        }

        if ((flags and NativeVoxelFlags.PASSABLE) != 0 || (flags and NativeVoxelFlags.CARPET_LIKE) != 0) {
            flags = flags or NativeVoxelFlags.PASSABLE_FLY
        }

        val etherPassable = when {
            block is ComparatorBlock -> true
            block is FlowerPotBlock -> true
            block is LadderBlock -> true
            block is SignBlock || block is WallSignBlock -> false
            else -> collisionShape == null || collisionShape.isEmpty()
        }
        val etherwarpFeetBlocker = block is ComparatorBlock ||
            block is FlowerPotBlock ||
            block is LadderBlock ||
            block is VineBlock

        val teleportSpaceClear =
            (etherPassable || block is SignBlock || block is WallSignBlock) && !etherwarpFeetBlocker

        if (etherPassable) flags = flags or NativeVoxelFlags.ETHER_PASSABLE
        if (teleportSpaceClear) flags = flags or NativeVoxelFlags.ETHER_TELEPORT_CLEAR
        if (etherwarpFeetBlocker) flags = flags or NativeVoxelFlags.ETHER_FEET_BLOCKER

        return flags
    }

    private fun stairFacingFlag(facing: Direction): Int =
        when (facing) {
            Direction.NORTH -> NativeVoxelFlags.STAIR_FACING_NORTH
            Direction.SOUTH -> NativeVoxelFlags.STAIR_FACING_SOUTH
            Direction.WEST -> NativeVoxelFlags.STAIR_FACING_WEST
            Direction.EAST -> NativeVoxelFlags.STAIR_FACING_EAST
            else -> NativeVoxelFlags.STAIR_FACING_NORTH
        }
}
