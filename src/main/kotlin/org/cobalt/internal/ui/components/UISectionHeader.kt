package org.cobalt.internal.ui.components

import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.internal.ui.UIComponent

internal class UISectionHeader(
  private val label: String,
  width: Float,
) : UIComponent(
  x = 0F,
  y = 0F,
  width = width,
  height = 28F,
) {

  override fun render() {
    val text = label.uppercase()
    val textColor = ThemeManager.currentTheme.moduleDivider
    val textWidth = NVGRenderer.textWidth(text, TEXT_SIZE)

    NVGRenderer.text(text, x + 8F, y + height / 2F - TEXT_SIZE / 2F, TEXT_SIZE, textColor)

    val lineStartX = x + 8F + textWidth + 8F
    val lineY = y + height / 2F
    NVGRenderer.line(lineStartX, lineY, x + width - 8F, lineY, 1F, textColor)
  }

  companion object {
    private const val TEXT_SIZE = 10F
  }
}
