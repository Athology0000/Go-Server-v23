package org.cobalt.api.hud.modules

import kotlin.math.cos
import org.cobalt.api.hud.HudAnchor
import org.cobalt.api.hud.hudElement
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.ColorSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.api.util.ui.helper.Gradient

class WatermarkModule : Module("Watermark") {

  private val textSize = 18f
  private val pad      = 10f
  private val corner   = 10f

  val watermark = hudElement("watermark", "Watermark", "Displays Dutt Client branding") {
    anchor  = HudAnchor.TOP_LEFT
    offsetX = 10f
    offsetY = 10f

    val text       = setting(TextSetting("Text",       "Display text",      "Dutt Client"))
    val color      = setting(ColorSetting("Color",     "Text color",        ThemeManager.currentTheme.accent))
    val shadow     = setting(CheckboxSetting("Shadow",     "Show text shadow",     true))
    val background = setting(CheckboxSetting("Background", "Show Spotify-style background", true))

    width  { NVGRenderer.textWidth(text.value, textSize) + (if (background.value) pad * 2f else 0f) }
    height { textSize + (if (background.value) pad * 2f else 0f) }

    render { screenX, screenY, _ ->
      if (background.value) {
        val bgW   = NVGRenderer.textWidth(text.value, textSize) + pad * 2f
        val bgH   = textSize + pad * 2f
        val now   = System.currentTimeMillis()
        val twoPi = (Math.PI * 2).toFloat()

        // Spotify-style background
        NVGRenderer.rect(screenX, screenY, bgW, bgH, 0xFF0A0E1A.toInt(), corner)
        NVGRenderer.gradientRect(screenX, screenY, bgW, bgH * 0.5f, 0x14FFFFFF, 0x00000000, Gradient.TopToBottom, corner)

        // Animated gradient border
        val angle  = (now % 10000L).toFloat() / 10000f * twoPi
        val shiftX = cos(angle) * (bgW * 0.42f)
        NVGRenderer.hollowGradientRectShifted(
          screenX, screenY, bgW, bgH, 1.5f,
          ThemeManager.currentTheme.accent, ThemeManager.currentTheme.accentSecondary,
          Gradient.LeftToRight, corner, shiftX, 0f
        )
      }

      val textX = screenX + (if (background.value) pad else 0f)
      val textY = screenY + (if (background.value) pad else 0f)
      if (shadow.value) {
        NVGRenderer.textShadow(text.value, textX, textY, textSize, color.value)
      } else {
        NVGRenderer.text(text.value, textX, textY, textSize, color.value)
      }
    }
  }
}
