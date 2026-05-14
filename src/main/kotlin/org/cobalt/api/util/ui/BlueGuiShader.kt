package org.cobalt.api.util.ui

import net.minecraft.client.Minecraft
import org.cobalt.render.rise.UiShaderDrawHelper

object BlueGuiShader {

  private val mc: Minecraft = Minecraft.getInstance()

  @JvmStatic
  @JvmOverloads
  fun draw(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    radius: Float = 10f,
    alpha: Float = 1f,
    intensity: Float = 1f,
  ) {
    drawCustom(
      x = x,
      y = y,
      width = width,
      height = height,
      radius = radius,
      baseColor = 0xCC07142A.toInt(),
      glowColor = 0xB50B52C8.toInt(),
      shineColor = 0xFF4BE7FF.toInt(),
      alpha = alpha,
      intensity = intensity,
    )
  }

  @JvmStatic
  @JvmOverloads
  fun drawCustom(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    radius: Float = 10f,
    baseColor: Int = 0xCC07142A.toInt(),
    glowColor: Int = 0xB50B52C8.toInt(),
    shineColor: Int = 0xFF4BE7FF.toInt(),
    alpha: Float = 1f,
    intensity: Float = 1f,
    timeSeconds: Float = (System.currentTimeMillis() % 1_000_000L) / 1000f,
  ) {
    if (width <= 0f || height <= 0f) return

    val window = mc.window
    val framebuffer = mc.mainRenderTarget
    val framebufferWidth = framebuffer.width
    val framebufferHeight = framebuffer.height
    val screenWidth = window.screenWidth.toFloat().takeIf { it > 0f } ?: return
    val screenHeight = window.screenHeight.toFloat().takeIf { it > 0f } ?: return
    val scaleX = framebufferWidth / screenWidth
    val scaleY = framebufferHeight / screenHeight

    UiShaderDrawHelper.drawBlueCompositeRoundedRect(
      x * scaleX,
      y * scaleY,
      width * scaleX,
      height * scaleY,
      radius * ((scaleX + scaleY) * 0.5f),
      baseColor,
      glowColor,
      shineColor,
      alpha,
      intensity,
      timeSeconds,
      framebufferHeight,
    )
  }
}
