package org.phantom.internal.rotation

import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

data class BlockAimPoint(
    val block: BlockPos,
    val point: Vec3,
    val faceHint: AimFaceHint = AimFaceHint.CENTER
)

enum class AimFaceHint {
    CENTER,
    NORTH_FACE,
    SOUTH_FACE,
    EAST_FACE,
    WEST_FACE,
    TOP_FACE,
    BOTTOM_FACE,
    VISIBLE_FACE
}
