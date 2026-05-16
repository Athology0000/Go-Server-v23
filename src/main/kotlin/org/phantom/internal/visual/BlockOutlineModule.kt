package org.phantom.internal.visual

import kotlin.math.max
import kotlin.math.roundToInt
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.shapes.CollisionContext
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.render.WorldRenderEvent
import org.phantom.api.module.Module
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.ColorSetting
import org.phantom.api.ui.theme.ThemeManager
import org.phantom.internal.pathfinding.OverlayRenderEngine

object BlockOutlineModule : Module("Block Outline") {

  private const val TAG = "block-outline"
  private const val OUTLINE_RADIUS = 4.0f
  private const val EDGE_THRESHOLD = 0.3

  private val enabled = CheckboxSetting(
    "Enabled",
    "Show a custom outline on the targeted block.",
    false
  )

  private val outlineColor = ColorSetting(
    "Outline Color",
    "Block outline color (RGBA).",
    0xFFFFFFFF.toInt()
  )

  private val outlineEnabled = CheckboxSetting(
    "Outline",
    "Render the outline around the block.",
    true
  )

  private val useTheme = CheckboxSetting(
    "Use Theme",
    "Use the active Phantom theme accent color for the outline.",
    false
  )

  private val faceOnly = CheckboxSetting(
    "Face Only",
    "Only outline the targeted face instead of the whole block.",
    false
  )

  init {
    addSetting(
      enabled,
      outlineColor,
      outlineEnabled,
      useTheme,
      faceOnly,
    )
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onRender(event: WorldRenderEvent.Last) {
    val mc = Minecraft.getInstance()
    val level = mc.level ?: run {
      OverlayRenderEngine.clearTag(TAG)
      return
    }

    if (!enabled.value) {
      OverlayRenderEngine.clearTag(TAG)
      return
    }

    val hit = mc.hitResult
    if (hit !is BlockHitResult || hit.type != HitResult.Type.BLOCK) {
      OverlayRenderEngine.clearTag(TAG)
      return
    }

    val blockPos = hit.blockPos
    val blockState = level.getBlockState(blockPos)
    if (blockState.isAir || !level.worldBorder.isWithinBounds(blockPos)) {
      OverlayRenderEngine.clearTag(TAG)
      return
    }

    val player = mc.player ?: run {
      OverlayRenderEngine.clearTag(TAG)
      return
    }
    val shape = blockState.getShape(level, blockPos, CollisionContext.of(player))
    if (shape.isEmpty) {
      OverlayRenderEngine.clearTag(TAG)
      return
    }

    OverlayRenderEngine.clearTag(TAG)

    val bounds = shape.bounds()
    val pad = 0.002
    val minX = blockPos.x + bounds.minX - pad
    val minY = blockPos.y + bounds.minY - pad
    val minZ = blockPos.z + bounds.minZ - pad
    val maxX = blockPos.x + bounds.maxX + pad
    val maxY = blockPos.y + bounds.maxY + pad
    val maxZ = blockPos.z + bounds.maxZ + pad

    val argb = resolveColor()
    val baseColor = toOverlayColor(argb)
    val radius = OUTLINE_RADIUS
    val minAlpha = (baseColor.a * EDGE_THRESHOLD).roundToInt().coerceIn(0, 255)
    if (!outlineEnabled.value) {
      return
    }

    val passes = max(1, (radius / 3.5f).roundToInt())
    val baseWidth = 1.2f
    for (i in 0 until passes) {
      val t = if (passes == 1) 0f else i.toFloat() / (passes - 1).toFloat()
      val width = baseWidth + radius * (0.35f + 0.65f * t)
      val alpha = (baseColor.a + (minAlpha - baseColor.a) * t).roundToInt().coerceIn(0, 255)
      val color = baseColor.withAlpha(alpha)
      if (faceOnly.value) {
        TargetFaceOverlay.addOutline(
          level,
          hit.direction,
          minX,
          minY,
          minZ,
          maxX,
          maxY,
          maxZ,
          color,
          width,
          2,
          TAG
        )
        continue
      }

      OverlayRenderEngine.addBox(
        level,
        minX,
        minY,
        minZ,
        maxX,
        maxY,
        maxZ,
        null,
        color,
        width,
        2,
        TAG
      )
    }

  }

  private fun toOverlayColor(argb: Int): OverlayRenderEngine.Color {
    val a = (argb ushr 24) and 0xFF
    val r = (argb ushr 16) and 0xFF
    val g = (argb ushr 8) and 0xFF
    val b = argb and 0xFF
    return OverlayRenderEngine.Color(r, g, b, a)
  }

  private fun resolveColor(): Int {
    if (useTheme.value) {
      return ThemeManager.currentTheme.accent
    }

    return outlineColor.value
  }
}
