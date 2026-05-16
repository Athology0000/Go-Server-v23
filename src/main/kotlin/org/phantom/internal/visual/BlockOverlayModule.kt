package org.phantom.internal.visual

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
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.ui.theme.ThemeManager
import org.phantom.internal.pathfinding.OverlayRenderEngine

object BlockOverlayModule : Module("Block Overlay") {

  private const val TAG = "block-overlay"

  private val enabled = CheckboxSetting(
    "Enabled",
    "Fill the targeted block with a translucent overlay.",
    false
  )

  private val overlayColor = ColorSetting(
    "Overlay Color",
    "Block overlay color (RGBA).",
    0xFFFFFFFF.toInt()
  )

  private val overlayOpacity = SliderSetting(
    "Opacity",
    "Fill opacity (0-100%).",
    0.2,
    0.0,
    1.0
  )

  private val useTheme = CheckboxSetting(
    "Use Theme",
    "Use the active Phantom theme accent color for the overlay.",
    false
  )

  private val faceOnly = CheckboxSetting(
    "Face Only",
    "Only fill the targeted face instead of the whole block.",
    false
  )

  init {
    addSetting(
      enabled,
      overlayColor,
      overlayOpacity,
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
    val fillAlpha = (baseColor.a * overlayOpacity.value).roundToInt().coerceIn(0, 255)
    val fillColor = baseColor.withAlpha(fillAlpha)
    if (faceOnly.value) {
      TargetFaceOverlay.addFill(
        level,
        hit.direction,
        minX,
        minY,
        minZ,
        maxX,
        maxY,
        maxZ,
        fillColor,
        2,
        TAG
      )
      return
    }

    OverlayRenderEngine.addBox(
      level,
      minX,
      minY,
      minZ,
      maxX,
      maxY,
      maxZ,
      fillColor,
      null,
      1.0f,
      2,
      TAG
    )

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

    return overlayColor.value
  }
}
