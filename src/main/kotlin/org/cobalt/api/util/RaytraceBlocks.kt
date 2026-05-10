package org.cobalt.api.util

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import kotlin.math.*

data class VoxelHit(val blockPos: BlockPos, val intersection: Vector3)

fun traverseVoxels(
    start: Vector3,
    end: Vector3,
    blockCheckFunc: ((BlockPos) -> Boolean)? = null,
    returnWhenTrue: Boolean = false,
    stopWhenNotAir: Boolean = false,
    returnIntersection: Boolean = false
): List<BlockPos> {
    val direction = end - start
    val stepX = sign(direction.x).toInt()
    val stepY = sign(direction.y).toInt()
    val stepZ = sign(direction.z).toInt()

    val tDeltaX = if (direction.x == 0.0) Double.MAX_VALUE else abs(1.0 / direction.x)
    val tDeltaY = if (direction.y == 0.0) Double.MAX_VALUE else abs(1.0 / direction.y)
    val tDeltaZ = if (direction.z == 0.0) Double.MAX_VALUE else abs(1.0 / direction.z)

    var tMaxX = if (tDeltaX == Double.MAX_VALUE) Double.MAX_VALUE else {
        val cur = floor(start.x)
        (if (stepX > 0) cur + 1 - start.x else start.x - cur) * tDeltaX
    }
    var tMaxY = if (tDeltaY == Double.MAX_VALUE) Double.MAX_VALUE else {
        val cur = floor(start.y)
        (if (stepY > 0) cur + 1 - start.y else start.y - cur) * tDeltaY
    }
    var tMaxZ = if (tDeltaZ == Double.MAX_VALUE) Double.MAX_VALUE else {
        val cur = floor(start.z)
        (if (stepZ > 0) cur + 1 - start.z else start.z - cur) * tDeltaZ
    }

    var cx = floor(start.x).toInt()
    var cy = floor(start.y).toInt()
    var cz = floor(start.z).toInt()
    val endX = floor(end.x).toInt()
    val endY = floor(end.y).toInt()
    val endZ = floor(end.z).toInt()

    val maxIters = (abs(direction.x) + abs(direction.y) + abs(direction.z)).toInt() + 10
    val path = ArrayList<BlockPos>(maxIters.coerceAtMost(256))

    val world = Minecraft.getInstance().level
    var iters = 0

    while (iters < maxIters && iters < 1000) {
        iters++
        val pos = BlockPos(cx, cy, cz)

        if (world != null && stopWhenNotAir && !world.getBlockState(pos).isAir) {
            if (returnWhenTrue) return listOf(pos)
            return listOf(pos)
        }

        if (blockCheckFunc != null && blockCheckFunc(pos)) {
            if (returnWhenTrue) return listOf(pos)
        }

        path.add(pos)

        if (cx == endX && cy == endY && cz == endZ) break

        when {
            tMaxX < tMaxY && tMaxX < tMaxZ -> { cx += stepX; tMaxX += tDeltaX }
            tMaxY < tMaxZ -> { cy += stepY; tMaxY += tDeltaY }
            else -> { cz += stepZ; tMaxZ += tDeltaZ }
        }
    }

    return path
}

fun raytraceBlocks(
    startPos: Vector3? = null,
    directionVector: Vector3? = null,
    distance: Double = 60.0,
    blockCheckFunc: ((BlockPos) -> Boolean)? = null,
    returnWhenTrue: Boolean = false,
    stopWhenNotAir: Boolean = true
): List<BlockPos> {
    val mc = Minecraft.getInstance()
    val player = mc.player ?: return if (returnWhenTrue) emptyList() else emptyList()

    val origin = startPos ?: run {
        val eye = player.eyePosition
        Vector3(eye.x, eye.y, eye.z)
    }

    val dir = directionVector ?: run {
        val look = player.getViewVector(1.0f)
        Vector3(look.x, look.y, look.z)
    }

    val normalized = dir.normalize()
    val endPos = origin + normalized * distance

    return traverseVoxels(origin, endPos, blockCheckFunc, returnWhenTrue, stopWhenNotAir)
}

fun raytraceBlocksFirst(
    startPos: Vector3? = null,
    directionVector: Vector3? = null,
    distance: Double = 60.0,
    blockCheckFunc: ((BlockPos) -> Boolean)? = null,
    stopWhenNotAir: Boolean = true
): BlockPos? = raytraceBlocks(startPos, directionVector, distance, blockCheckFunc, true, stopWhenNotAir).firstOrNull()
