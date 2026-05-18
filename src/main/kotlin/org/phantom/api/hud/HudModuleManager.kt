package org.phantom.api.hud

import net.minecraft.client.Minecraft
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.render.NvgEvent
import org.phantom.api.module.ModuleManager
import org.phantom.api.util.ui.NVGRenderer
import org.phantom.render.HudGlassBlurRenderer

object HudModuleManager {

  private val mc: Minecraft = Minecraft.getInstance()

  @Volatile
  var isEditorOpen: Boolean = false

  fun getElements(): List<HudElement> =
    ModuleManager.getModules().flatMap { it.getHudElements() }

  fun resetAllPositions() {
    getElements().forEach { it.resetPosition() }
  }

  @Suppress("unused")
  @SubscribeEvent
  fun onRender(event: NvgEvent) {
    if (mc.screen != null && !isEditorOpen) return

    val window = mc.window
    val screenWidth = window.screenWidth.toFloat()
    val screenHeight = window.screenHeight.toFloat()
    val enabledElements = getElements().filter { it.enabled }
    val blurredElements = enabledElements.filter {
      it.usesManagedBlurBackground() && it.isBlurBackgroundEnabled()
    }
    val maxBlurStrength = blurredElements.maxOfOrNull { it.getBlurStrength().toDouble() }?.toFloat() ?: 0f
    val blurFramePrepared = blurredElements.isNotEmpty() && HudGlassBlurRenderer.beginFrame(maxBlurStrength)

    try {
      enabledElements.forEach { element ->
        val (screenX, screenY) = element.getScreenPosition(screenWidth, screenHeight)
        renderElementBlur(element, screenX, screenY, blurFramePrepared)
        element.renderPre(screenX, screenY, element.scale)
      }
    } finally {
      if (blurFramePrepared) {
        HudGlassBlurRenderer.endFrame()
      }
    }

    NVGRenderer.beginFrame(screenWidth, screenHeight)

    enabledElements.forEach { element ->
      val (screenX, screenY) = element.getScreenPosition(screenWidth, screenHeight)

      NVGRenderer.push()
      NVGRenderer.translate(screenX, screenY)
      NVGRenderer.scale(element.scale, element.scale)
      element.render(0f, 0f, element.scale)
      NVGRenderer.pop()
    }

    NVGRenderer.endFrame()
    enabledElements.forEach { element ->
      val (screenX, screenY) = element.getScreenPosition(screenWidth, screenHeight)
      element.renderPost(screenX, screenY, element.scale)
    }
  }

  private fun renderElementBlur(element: HudElement, screenX: Float, screenY: Float, framePrepared: Boolean) {
    if (!element.usesManagedBlurBackground()) return
    if (!element.isBlurBackgroundEnabled()) return
    if (!framePrepared) return
    if (element.getScaledWidth() <= 0f || element.getScaledHeight() <= 0f) return

    val padding = 2f * element.scale
    HudGlassBlurRenderer.renderBlurRect(
      screenX - padding,
      screenY - padding,
      element.getScaledWidth() + padding * 2f,
      element.getScaledHeight() + padding * 2f,
      10f * element.scale,
      element.getBlurStrength(),
    )
  }
}
