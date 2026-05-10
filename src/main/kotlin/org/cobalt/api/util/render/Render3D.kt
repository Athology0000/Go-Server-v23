package org.cobalt.api.util.render

import java.awt.Color
import com.mojang.blaze3d.opengl.GlStateManager
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.gizmos.GizmoStyle
import net.minecraft.gizmos.Gizmos
import net.minecraft.util.ARGB
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.cobalt.api.event.impl.render.WorldRenderContext
import kotlin.math.cos
import kotlin.math.sin

object Render3D {

  @JvmStatic
  fun drawBox(context: WorldRenderContext, box: AABB, color: Color, esp: Boolean = false) {
    drawStyledBox(
      context = context,
      box = box,
      strokeColor = color,
      fillColor = Color(color.red, color.green, color.blue, 150),
      esp = esp,
      lineWidth = 2.5f
    )
  }

  @JvmStatic
  fun drawStyledBox(
    context: WorldRenderContext,
    box: AABB,
    strokeColor: Color,
    fillColor: Color? = null,
    esp: Boolean = false,
    lineWidth: Float = 2.5f,
  ) {
    if (!FrustumUtils.isVisible(context.frustum, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)) {
      return
    }

    val stroke = ARGB.color(strokeColor.alpha, strokeColor.red, strokeColor.green, strokeColor.blue)
    val fill = fillColor?.let { ARGB.color(it.alpha, it.red, it.green, it.blue) } ?: ARGB.color(0, 0, 0, 0)

    val style = GizmoStyle.strokeAndFill(stroke, lineWidth, fill)
    val props = Gizmos.cuboid(box, style)

    if (esp) {
      props.setAlwaysOnTop()
    }
  }

  @JvmStatic
  fun drawLine(
    context: WorldRenderContext,
    start: Vec3,
    end: Vec3,
    color: Color,
    esp: Boolean = false,
    thickness: Float = 1f,
  ) {
    if (!FrustumUtils.isVisible(
        context.frustum,
        minOf(start.x, end.x), minOf(start.y, end.y), minOf(start.z, end.z),
        maxOf(start.x, end.x), maxOf(start.y, end.y), maxOf(start.z, end.z)
      )
    ) return

    val argbColor = ARGB.color(color.alpha, color.red, color.green, color.blue)
    val props = Gizmos.line(start, end, argbColor, thickness)

    if (esp) {
      props.setAlwaysOnTop()
    }
  }

  /**
   * Renders a billboard text label in world space, visible through blocks.
   * The label always faces the camera.
   */
  @JvmStatic
  fun drawWorldLabel(
    context: WorldRenderContext,
    worldPos: Vec3,
    text: String,
    color: Color,
    textScale: Float = 1f,
  ) {
    val mc = Minecraft.getInstance()
    val camera = context.camera
    val cam = camera.position()
    val matrices = context.matrixStack ?: PoseStack()
    val font = mc.font
    val buffer = mc.renderBuffers().bufferSource()
    val textWidth = font.width(text).toFloat()
    val scale = 0.025f * textScale.coerceAtLeast(0.1f)

    try {
      GlStateManager._enableBlend()
      GlStateManager._blendFuncSeparate(770, 771, 1, 771)
      GlStateManager._disableDepthTest()
      GlStateManager._depthMask(false)

      matrices.pushPose()
      matrices.translate(worldPos.x - cam.x, worldPos.y - cam.y, worldPos.z - cam.z)
      matrices.mulPose(camera.rotation())
      matrices.scale(-scale, -scale, scale)

      font.drawInBatch(
        text,
        -textWidth / 2f,
        0f,
        color.rgb,
        false,
        matrices.last().pose(),
        buffer,
        Font.DisplayMode.SEE_THROUGH,
        0,
        LightTexture.FULL_BRIGHT,
      )

      matrices.popPose()
      buffer.endBatch()
    } finally {
      GlStateManager._depthMask(true)
      GlStateManager._enableDepthTest()
      GlStateManager._disableBlend()
    }
  }

  @JvmStatic
  fun drawCircleOutline(
    context: WorldRenderContext,
    center: Vec3,
    radius: Float,
    color: Color,
    esp: Boolean = false,
    thickness: Float = 2f,
  ) {
    if (radius <= 0f) return
    if (!FrustumUtils.isVisible(
        context.frustum,
        center.x - radius, center.y - 0.05, center.z - radius,
        center.x + radius, center.y + 0.05, center.z + radius
      )
    ) return

    val argbColor = ARGB.color(color.alpha, color.red, color.green, color.blue)
    for (i in 0 until CIRCLE_OUTLINE_SEGMENTS) {
      val rad = Math.toRadians(i * CIRCLE_OUTLINE_STEP_DEGREES)
      val nextRad = Math.toRadians((i + 1) * CIRCLE_OUTLINE_STEP_DEGREES)
      val start = center.add(radius * cos(rad), SURFACE_RENDER_OFFSET, radius * sin(rad))
      val end = center.add(radius * cos(nextRad), SURFACE_RENDER_OFFSET, radius * sin(nextRad))
      val props = Gizmos.line(start, end, argbColor, thickness)

      if (esp) {
        props.setAlwaysOnTop()
      }
    }
  }

  @JvmStatic
  fun drawCircleSurface(
    context: WorldRenderContext,
    center: Vec3,
    radius: Float,
    color: Color,
  ) {
    if (radius <= 0f) return
    if (!FrustumUtils.isVisible(
        context.frustum,
        center.x - radius, center.y - 0.05, center.z - radius,
        center.x + radius, center.y + 0.05, center.z + radius
      )
    ) return

    val matrices = context.matrixStack ?: PoseStack()
    val cameraPos = context.camera.position()
    val buffer = context.consumers.getBuffer(RenderTypes.debugQuads())
    val argb = ARGB.color(color.alpha, color.red, color.green, color.blue)

    matrices.pushPose()
    matrices.translate(center.x - cameraPos.x, center.y + SURFACE_RENDER_OFFSET, center.z - cameraPos.z)
    val pose = matrices.last().pose()

    for (i in 0 until CIRCLE_OUTLINE_SEGMENTS) {
      val rad = Math.toRadians(i * CIRCLE_OUTLINE_STEP_DEGREES)
      val nextRad = Math.toRadians((i + 1) * CIRCLE_OUTLINE_STEP_DEGREES)
      val x1 = (radius * cos(rad)).toFloat()
      val z1 = (radius * sin(rad)).toFloat()
      val x2 = (radius * cos(nextRad)).toFloat()
      val z2 = (radius * sin(nextRad)).toFloat()

      // Degenerate quad (v0=v3=center) → one filled triangle per segment
      buffer.addVertex(pose, 0f, 0f, 0f).setColor(argb)
      buffer.addVertex(pose, x1, 0f, z1).setColor(argb)
      buffer.addVertex(pose, x2, 0f, z2).setColor(argb)
      buffer.addVertex(pose, 0f, 0f, 0f).setColor(argb)
    }

    matrices.popPose()
  }

  @JvmStatic
  fun drawCylinderSurface(
    context: WorldRenderContext,
    center: Vec3,
    radius: Float,
    height: Float,
    color: Color,
  ) {
    if (radius <= 0f || height <= 0f) return
    if (!FrustumUtils.isVisible(
        context.frustum,
        center.x - radius, center.y, center.z - radius,
        center.x + radius, center.y + height, center.z + radius
      )
    ) return

    val matrices = context.matrixStack ?: PoseStack()
    val cameraPos = context.camera.position()
    val buffer = context.consumers.getBuffer(RenderTypes.debugQuads())
    val argb = ARGB.color(color.alpha, color.red, color.green, color.blue)

    matrices.pushPose()
    matrices.translate(center.x - cameraPos.x, center.y + SURFACE_RENDER_OFFSET, center.z - cameraPos.z)
    val pose = matrices.last().pose()

    for (i in 0 until CYLINDER_SEGMENTS) {
      val rad = Math.toRadians(i.toDouble())
      val nextRad = Math.toRadians((i + 1).toDouble())
      val x1 = (radius * cos(rad)).toFloat()
      val z1 = (radius * sin(rad)).toFloat()
      val x2 = (radius * cos(nextRad)).toFloat()
      val z2 = (radius * sin(nextRad)).toFloat()

      buffer.addVertex(pose, x1, 0f, z1).setColor(argb)
      buffer.addVertex(pose, x2, 0f, z2).setColor(argb)
      buffer.addVertex(pose, x2, height, z2).setColor(argb)
      buffer.addVertex(pose, x1, height, z1).setColor(argb)

      buffer.addVertex(pose, x2, 0f, z2).setColor(argb)
      buffer.addVertex(pose, x1, 0f, z1).setColor(argb)
      buffer.addVertex(pose, x1, height, z1).setColor(argb)
      buffer.addVertex(pose, x2, height, z2).setColor(argb)
    }

    matrices.popPose()
  }

  private const val CYLINDER_SEGMENTS = 360
  private const val CIRCLE_OUTLINE_SEGMENTS = 96
  private const val CIRCLE_OUTLINE_STEP_DEGREES = 360.0 / CIRCLE_OUTLINE_SEGMENTS
  private const val SURFACE_RENDER_OFFSET = 0.01
}
