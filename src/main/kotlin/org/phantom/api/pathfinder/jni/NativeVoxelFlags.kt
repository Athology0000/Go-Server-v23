package org.phantom.api.pathfinder.jni

object NativeVoxelFlags {
    const val PASSABLE = 1 shl 0
    const val SOLID = 1 shl 1
    const val PASSABLE_FLY = 1 shl 2
    const val BLOCKING_WALL = 1 shl 3
    const val FLUID = 1 shl 4
    const val SLAB_BOTTOM = 1 shl 5
    const val SLAB_TOP = 1 shl 6
    const val FENCE_LIKE = 1 shl 7
    const val STAIRS_BOTTOM = 1 shl 8
    const val CARPET_LIKE = 1 shl 9
    const val ETHER_PASSABLE = 1 shl 10
    const val ETHER_TELEPORT_CLEAR = 1 shl 11
    const val ETHER_FEET_BLOCKER = 1 shl 12
    const val LAVA = 1 shl 13
    const val STAIR_FACING_NORTH = 0 shl 14
    const val STAIR_FACING_SOUTH = 1 shl 14
    const val STAIR_FACING_WEST = 2 shl 14
    const val STAIR_FACING_EAST = 3 shl 14
    const val STAIR_FACING_MASK = 3 shl 14

    // Thin-snow marker (SnowLayerBlock, 1..7 layers). The exact layer count
    // travels on the parallel snow plane, not this word. Aliases a stair-facing
    // bit, which is safe: snow is never STAIRS_BOTTOM and the native side only
    // reads bits 14-15 behind a STAIRS_BOTTOM guard. 8-layer snow = full block
    // (plain SOLID, no SNOW).
    const val SNOW = 1 shl 14
}
