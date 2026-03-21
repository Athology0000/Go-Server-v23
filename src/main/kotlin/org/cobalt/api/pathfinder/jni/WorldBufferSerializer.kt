package org.cobalt.api.pathfinder.jni

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.tags.FluidTags
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LadderBlock
import net.minecraft.world.level.block.VineBlock

/**
 * Serializes a 64×32×64 block region centred on the player into a flat byte[].
 *
 * Buffer layout (matches Types.h constants):
 *   index(x,y,z) = (y-by)*BUF_STRIDE_Y + (z-bz)*BUF_STRIDE_Z + (x-bx)
 *   BUF_W=64, BUF_H=32, BUF_D=64
 *   BUF_STRIDE_Z=64, BUF_STRIDE_Y=64*64=4096
 *
 * Block type byte values (must match WorldAccessor.h):
 *   0 = AIR, 1 = SOLID, 2 = WATER, 3 = LAVA, 4 = LADDER
 */
object WorldBufferSerializer {

    private const val BUF_W = 64
    private const val BUF_H = 32
    private const val BUF_D = 64
    private const val BUF_STRIDE_Z = BUF_W
    private const val BUF_STRIDE_Y = BUF_W * BUF_D

    private const val BT_AIR: Byte    = 0
    private const val BT_SOLID: Byte  = 1
    private const val BT_WATER: Byte  = 2
    private const val BT_LAVA: Byte   = 3
    private const val BT_LADDER: Byte = 4

    fun serialize(mc: Minecraft): WorldBufferResult? {
        val level = mc.level ?: return null
        val player = mc.player ?: return null

        val px = player.blockX
        val py = player.blockY
        val pz = player.blockZ

        // Origin: place player 32 blocks in from the horizontal edges, 8 blocks up from the bottom
        val bx = px - BUF_W / 2
        val by = py - 8
        val bz = pz - BUF_D / 2

        val buf = ByteArray(BUF_W * BUF_H * BUF_D)
        val mutablePos = BlockPos.MutableBlockPos()

        for (dy in 0 until BUF_H) {
            val worldY = by + dy
            val baseY = dy * BUF_STRIDE_Y
            for (dz in 0 until BUF_D) {
                val worldZ = bz + dz
                val baseYZ = baseY + dz * BUF_STRIDE_Z
                for (dx in 0 until BUF_W) {
                    mutablePos.set(bx + dx, worldY, worldZ)
                    val state = level.getBlockState(mutablePos)
                    val bt: Byte = when {
                        state.isAir -> BT_AIR
                        !state.fluidState.isEmpty && state.fluidState.`is`(FluidTags.WATER) -> BT_WATER
                        !state.fluidState.isEmpty && state.fluidState.`is`(FluidTags.LAVA) -> BT_LAVA
                        state.block is LadderBlock || state.block is VineBlock -> BT_LADDER
                        state.block == Blocks.VINE -> BT_LADDER
                        else -> BT_SOLID
                    }
                    buf[baseYZ + dx] = bt
                }
            }
        }

        return WorldBufferResult(buf, bx, by, bz)
    }
}
