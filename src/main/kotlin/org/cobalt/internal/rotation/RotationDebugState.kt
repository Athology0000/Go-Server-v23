package org.cobalt.internal.rotation

import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

data class RotationDebugState(
    val active: Boolean = false,
    val fromBlock: BlockPos? = null,
    val toBlock: BlockPos? = null,
    val fromPoint: Vec3? = null,
    val toPoint: Vec3? = null,
    val startYaw: Float = 0f,
    val startPitch: Float = 0f,
    val targetYaw: Float = 0f,
    val targetPitch: Float = 0f,
    val currentYaw: Float = 0f,
    val currentPitch: Float = 0f,
    val tick: Int = 0,
    val durationTicks: Int = 0,
    val progress: Double = 0.0,
    val easedProgress: Double = 0.0,
    val finished: Boolean = false,
    val precisionPoint: Vec3? = null,
    val nextFallbackBlock: BlockPos? = null
)
