package org.phantom.api.hud

import org.phantom.api.ui.theme.ThemeManager
import org.phantom.api.ui.theme.ThemeSurface
import org.phantom.api.util.ui.NVGRenderer
import org.phantom.api.util.ui.helper.Gradient

object HudGlassStyle {
  const val RADIUS = 6f
  const val PADDING_X = 10f
  const val PADDING_Y = 8f
  const val TITLE_SIZE = 13f
  const val BODY_SIZE = 13f
  const val SECTION_SIZE = 12f
  const val LINE_HEIGHT = 17f

  private const val PANEL = 0x9A07101D.toInt()
  private const val PANEL_TOP = 0x260F2A48
  private const val PANEL_BOTTOM = 0x18000000
  private const val BORDER = 0x64325076
  private const val INNER_BORDER = 0x1EFFFFFF
  private const val SHADOW = 0x4C000000
  private const val TITLE = 0xFF8FA8D6.toInt()
  private const val LABEL = 0xFFC9D7F2.toInt()
  private const val VALUE = 0xFFF4F7FF.toInt()
  private const val MUTED = 0xFF8190AE.toInt()

  fun textColor(): Int = VALUE
  fun labelColor(): Int = LABEL
  fun mutedColor(): Int = MUTED
  fun accentColor(): Int = ThemeManager.currentTheme.accent

  fun drawPanel(x: Float, y: Float, w: Float, h: Float, radius: Float = RADIUS) {
    NVGRenderer.rect(x + 1f, y + 2f, w, h, SHADOW, radius + 1f)
    NVGRenderer.rect(x, y, w, h, PANEL, radius)
    NVGRenderer.gradientRect(x, y, w, h * 0.52f, PANEL_TOP, 0x00000000, Gradient.TopToBottom, radius)
    NVGRenderer.gradientRect(x, y + h * 0.46f, w, h * 0.54f, 0x00000000, PANEL_BOTTOM, Gradient.TopToBottom, radius)
    NVGRenderer.hollowRect(x, y, w, h, 1f, BORDER, radius)
    NVGRenderer.hollowRect(x + 1f, y + 1f, w - 2f, h - 2f, 1f, INNER_BORDER, (radius - 1f).coerceAtLeast(0f))
  }

  fun drawHeader(title: String, x: Float, y: Float, w: Float) {
    NVGRenderer.text(title, x + PADDING_X, y + 6f, TITLE_SIZE, TITLE)
    NVGRenderer.line(x + PADDING_X, y + 24f, x + w - PADDING_X, y + 24f, 1f, ThemeSurface.withAlpha(BORDER, 0x44))
  }

  fun drawSectionTitle(title: String, x: Float, y: Float) {
    NVGRenderer.text(title, x, y, SECTION_SIZE, TITLE)
  }

  fun drawRow(label: String, value: String, x: Float, y: Float, valueColor: Int = VALUE) {
    val labelText = "$label: "
    NVGRenderer.text(labelText, x, y, BODY_SIZE, LABEL)
    NVGRenderer.text(value, x + NVGRenderer.textWidth(labelText, BODY_SIZE), y, BODY_SIZE, valueColor)
  }

}
