package org.cobalt.internal.rotation

import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

data class BlockRotationRequest(
    val fromBlock: BlockPos,
    val toBlock: BlockPos,

    val useFromBlockAsStartRotation: Boolean = false,
    val durationTicks: Int = 8,
    val fromFaceHint: AimFaceHint = AimFaceHint.VISIBLE_FACE,
    val toFaceHint: AimFaceHint = AimFaceHint.VISIBLE_FACE,
    val aimOffsetStrength: Double = 0.18,
    val maxDegreesPerTick: Float = 18.0f,
    val easing: RotationEasingType = RotationEasingType.SMOOTHSTEP,
    val fromAimPoint: Vec3? = null,
    val toAimPoint: Vec3? = null,
)
