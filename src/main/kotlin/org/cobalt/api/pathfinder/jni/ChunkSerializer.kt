package org.cobalt.api.pathfinder.jni

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.tags.FluidTags
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Half
import net.minecraft.world.level.chunk.LevelChunk
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.BlockChangeEvent

object ChunkSerializer {

    private const val VF_PASSABLE: Int             = 0x0001
    private const val VF_SOLID: Int                = 0x0002
    private const val VF_PASSABLE_FLY: Int         = 0x0004
    private const val VF_BLOCKING_WALL: Int        = 0x0008
    private const val VF_FLUID: Int                = 0x0010
    private const val VF_SLAB_BOTTOM: Int          = 0x0020
    private const val VF_SLAB_TOP: Int             = 0x0040
    private const val VF_FENCE_LIKE: Int           = 0x0080
    private const val VF_STAIRS_BOTTOM: Int        = 0x0100
    private const val VF_CARPET_LIKE: Int          = 0x0200
    private const val VF_ETHER_PASSABLE: Int       = 0x0400
    private const val VF_ETHER_TELEPORT_CLEAR: Int = 0x0800
    private const val VF_ETHER_FEET_BLOCKER: Int   = 0x1000

    private val AIR_FLAGS: Short = (VF_PASSABLE or VF_PASSABLE_FLY or VF_ETHER_PASSABLE or VF_ETHER_TELEPORT_CLEAR).toShort()

    private val heightCache = HashMap<BlockState, Double>(512)
    private val mpos = BlockPos.MutableBlockPos()

    // Track current world key so setWorld is only called on dimension change
    @Volatile private var currentWorldKey: String? = null

    fun register() {
        ClientChunkEvents.CHUNK_LOAD.register { world, chunk ->
            onChunkLoad(world, chunk)
        }
        EventBus.register(this)
    }

    @SubscribeEvent
    fun onBlockChange(event: BlockChangeEvent) {
        val level = Minecraft.getInstance().level ?: return
        mpos.set(event.pos.x, event.pos.y, event.pos.z)
        val flags = classifyState(event.newBlock, level).toInt() and 0xFFFF
        NativePathfinderJNI.applyBlockUpdates(intArrayOf(event.pos.x, event.pos.y, event.pos.z, flags))
    }

    fun onChunkLoad(world: ClientLevel, chunk: LevelChunk) {
        val minY = world.minBuildHeight
        val maxY = world.maxBuildHeight
        val chunkX = chunk.pos.x
        val chunkZ = chunk.pos.z
        val worldKey = world.dimension().location().toString()

        if (worldKey != currentWorldKey) {
            NativePathfinderJNI.setWorld(worldKey, minY, maxY)
            currentWorldKey = worldKey
        }

        val sections = chunk.sections
        val flagsList = ArrayList<Short>(sections.size * 4096)
        var sectionMask = 0L

        for (i in sections.indices) {
            val section = sections[i]
            if (section.hasOnlyAir()) continue

            sectionMask = sectionMask or (1L shl i)
            val sectionMinY = minY + i * 16

            for (ly in 0 until 16) {
                val worldY = sectionMinY + ly
                for (lz in 0 until 16) {
                    val worldZ = chunkZ * 16 + lz
                    for (lx in 0 until 16) {
                        val worldX = chunkX * 16 + lx
                        val state = section.getBlockState(lx, ly, lz)
                        mpos.set(worldX, worldY, worldZ)
                        flagsList.add(classifyState(state, world))
                    }
                }
            }
        }

        if (sectionMask == 0L) return

        val sectionFlagsArray = ShortArray(flagsList.size) { flagsList[it] }
        NativePathfinderJNI.upsertChunk(chunkX, chunkZ, minY, maxY, sectionMask, sectionFlagsArray)
    }

    fun invalidate() {
        NativePathfinderJNI.clearWorld()
        heightCache.clear()
        currentWorldKey = null
    }

    private fun classifyState(state: BlockState, world: ClientLevel): Short {
        if (state.isAir) return AIR_FLAGS

        val fluid = state.fluidState
        if (!fluid.isEmpty) {
            return (VF_FLUID or VF_PASSABLE_FLY).toShort()
        }

        if (state.`is`(BlockTags.CLIMBABLE)) {
            return (VF_PASSABLE or VF_PASSABLE_FLY).toShort()
        }

        if (state.block is StairBlock) {
            return classifyStair(state)
        }

        val maxHeight = heightCache.getOrPut(state) {
            val shape = state.getCollisionShape(world, mpos)
            if (shape.isEmpty) 0.0 else shape.bounds().maxY
        }

        return when {
            maxHeight < 0.1  -> (VF_PASSABLE or VF_PASSABLE_FLY or VF_CARPET_LIKE or VF_ETHER_PASSABLE or VF_ETHER_TELEPORT_CLEAR).toShort()
            maxHeight < 0.6  -> (VF_SLAB_BOTTOM or VF_SOLID).toShort()
            maxHeight < 1.1  -> (VF_SOLID or VF_BLOCKING_WALL or VF_ETHER_FEET_BLOCKER).toShort()
            else             -> (VF_SOLID or VF_BLOCKING_WALL or VF_FENCE_LIKE or VF_ETHER_FEET_BLOCKER).toShort()
        }
    }

    private fun classifyStair(state: BlockState): Short {
        if (state.getValue(StairBlock.HALF) != Half.BOTTOM) {
            return (VF_SOLID or VF_BLOCKING_WALL or VF_SLAB_TOP or VF_ETHER_FEET_BLOCKER).toShort()
        }
        return (VF_STAIRS_BOTTOM or VF_SOLID).toShort()
    }
}
