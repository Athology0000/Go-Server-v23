package org.cobalt.internal.ui.components

import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.internal.ui.UIComponent

internal class UICategoryHeader(
    val category: ModuleCategory,
) : UIComponent(
    x = 0F,
    y = 0F,
    width = 182.5F,
    height = 28F,
) {

    override fun render() {
        val label = category.displayName.uppercase()
        val textColor = ThemeManager.currentTheme.moduleDivider
        val textWidth = NVGRenderer.textWidth(label, TEXT_SIZE)

        NVGRenderer.text(label, x + 8F, y + height / 2F - TEXT_SIZE / 2F, TEXT_SIZE, textColor)

        val lineStartX = x + 8F + textWidth + 8F
        val lineY = y + height / 2F
        NVGRenderer.line(lineStartX, lineY, x + width - 8F, lineY, 1F, textColor)
    }

    override fun mouseClicked(button: Int): Boolean = false

    companion object {
        private const val TEXT_SIZE = 10F
    }
}
