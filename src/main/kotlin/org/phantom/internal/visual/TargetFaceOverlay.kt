package org.phantom.internal.visual

import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.phantom.internal.pathfinding.OverlayRenderEngine

internal object TargetFaceOverlay {
  private const val FACE_PAD = 0.003
  private const val FACE_THICKNESS = 0.012

  fun addFill(
    level: Level,
    direction: Direction,
    minX: Double,
    minY: Double,
    minZ: Double,
    maxX: Double,
    maxY: Double,
    maxZ: Double,
    fill: OverlayRenderEngine.Color,
    durationTicks: Int,
    tag: String,
  ) {
    val faceBox = faceBox(direction, minX, minY, minZ, maxX, maxY, maxZ)
    OverlayRenderEngine.addBox(
      level,
      faceBox[0],
      faceBox[1],
      faceBox[2],
      faceBox[3],
      faceBox[4],
      faceBox[5],
      fill,
      null,
      1.0f,
      durationTicks,
      tag,
      forceRender = true,
    )
  }

  fun addOutline(
    level: Level,
    direction: Direction,
    minX: Double,
    minY: Double,
    minZ: Double,
    maxX: Double,
    maxY: Double,
    maxZ: Double,
    outline: OverlayRenderEngine.Color,
    lineWidth: Float,
    durationTicks: Int,
    tag: String,
  ) {
    val corners = faceCorners(direction, minX, minY, minZ, maxX, maxY, maxZ)
    for (index in corners.indices) {
      val start = corners[index]
      val end = corners[(index + 1) % corners.size]
      OverlayRenderEngine.addLine(
        level,
        start.x,
        start.y,
        start.z,
        end.x,
        end.y,
        end.z,
        outline,
        lineWidth,
        durationTicks,
        tag,
        forceRender = true,
      )
    }
  }

  private fun faceBox(
    direction: Direction,
    minX: Double,
    minY: Double,
    minZ: Double,
    maxX: Double,
    maxY: Double,
    maxZ: Double,
  ): DoubleArray {
    return when (direction) {
      Direction.DOWN -> doubleArrayOf(minX, minY - FACE_THICKNESS, minZ, maxX, minY, maxZ)
      Direction.UP -> doubleArrayOf(minX, maxY, minZ, maxX, maxY + FACE_THICKNESS, maxZ)
      Direction.NORTH -> doubleArrayOf(minX, minY, minZ - FACE_THICKNESS, maxX, maxY, minZ)
      Direction.SOUTH -> doubleArrayOf(minX, minY, maxZ, maxX, maxY, maxZ + FACE_THICKNESS)
      Direction.WEST -> doubleArrayOf(minX - FACE_THICKNESS, minY, minZ, minX, maxY, maxZ)
      Direction.EAST -> doubleArrayOf(maxX, minY, minZ, maxX + FACE_THICKNESS, maxY, maxZ)
    }
  }

  private fun faceCorners(
    direction: Direction,
    minX: Double,
    minY: Double,
    minZ: Double,
    maxX: Double,
    maxY: Double,
    maxZ: Double,
  ): List<Vec3> {
    return when (direction) {
      Direction.DOWN -> listOf(
        Vec3(minX, minY - FACE_PAD, minZ),
        Vec3(maxX, minY - FACE_PAD, minZ),
        Vec3(maxX, minY - FACE_PAD, maxZ),
        Vec3(minX, minY - FACE_PAD, maxZ),
      )
      Direction.UP -> listOf(
        Vec3(minX, maxY + FACE_PAD, minZ),
        Vec3(maxX, maxY + FACE_PAD, minZ),
        Vec3(maxX, maxY + FACE_PAD, maxZ),
        Vec3(minX, maxY + FACE_PAD, maxZ),
      )
      Direction.NORTH -> listOf(
        Vec3(minX, minY, minZ - FACE_PAD),
        Vec3(maxX, minY, minZ - FACE_PAD),
        Vec3(maxX, maxY, minZ - FACE_PAD),
        Vec3(minX, maxY, minZ - FACE_PAD),
      )
      Direction.SOUTH -> listOf(
        Vec3(minX, minY, maxZ + FACE_PAD),
        Vec3(maxX, minY, maxZ + FACE_PAD),
        Vec3(maxX, maxY, maxZ + FACE_PAD),
        Vec3(minX, maxY, maxZ + FACE_PAD),
      )
      Direction.WEST -> listOf(
        Vec3(minX - FACE_PAD, minY, minZ),
        Vec3(minX - FACE_PAD, minY, maxZ),
        Vec3(minX - FACE_PAD, maxY, maxZ),
        Vec3(minX - FACE_PAD, maxY, minZ),
      )
      Direction.EAST -> listOf(
        Vec3(maxX + FACE_PAD, minY, minZ),
        Vec3(maxX + FACE_PAD, minY, maxZ),
        Vec3(maxX + FACE_PAD, maxY, maxZ),
        Vec3(maxX + FACE_PAD, maxY, minZ),
      )
    }
  }
}
