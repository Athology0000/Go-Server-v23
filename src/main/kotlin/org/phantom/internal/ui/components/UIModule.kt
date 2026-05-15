package org.phantom.internal.ui.components

import org.phantom.api.module.Module
import org.phantom.api.module.setting.Setting
import org.phantom.api.ui.theme.ThemeManager
import org.phantom.api.util.ui.NVGRenderer
import org.phantom.api.util.ui.helper.Gradient
import org.phantom.internal.ui.UIComponent
import org.phantom.internal.ui.animation.ColorAnimation
import org.phantom.internal.ui.animation.EaseOutAnimation
import org.phantom.internal.ui.panel.panels.UIModuleList
import org.phantom.internal.ui.util.isHoveringOver

internal class UIModule(
  val module: Module,
  private val panel: UIModuleList,
  private var selected: Boolean,
) : UIComponent(
  x = 0F,
  y = 0F,
  width = 182.5F,
  height = 40F,
) {

  private val colorAnimation = ColorAnimation(150L)
  private val xOffsetAnimation = EaseOutAnimation(200L)

  override fun render() {
    val entitled = module.isEntitled

    val opaqueColor = colorAnimation.get(ThemeManager.currentTheme.transparent, ThemeManager.currentTheme.selectedOverlay, !selected)
    val mainColor = colorAnimation.get(ThemeManager.currentTheme.transparent, ThemeManager.currentTheme.accent, !selected)
    val textColor = if (entitled) {
      colorAnimation.get(ThemeManager.currentTheme.text, ThemeManager.currentTheme.accent, !selected)
    } else {
      ThemeManager.currentTheme.textDisabled
    }
    val xOffset = xOffsetAnimation.get(0F, 10F, !selected)

    if (selected) {
      NVGRenderer.rect(x, y, width, height, opaqueColor, 5F)
      NVGRenderer.hollowRect(x, y, width, height, 1F, mainColor, 5F)
      NVGRenderer.image(
        selectedIcon, x + 10F, y + height / 2 - 7F, 13F, 13F,
        colorMask = mainColor
      )
    }

    if (!entitled) {
      NVGRenderer.image(lockIcon, x + width - 18F, y + height / 2 - 6F, 12F, 12F,
        colorMask = ThemeManager.currentTheme.textDisabled)
    }

    if (entitled && module.name.equals("Fairy Grotto", ignoreCase = true)) {
      NVGRenderer.textGradient(
        module.name,
        x + 20F + xOffset,
        y + height / 2F - 6.5F,
        13F,
        0xFFFF5AC8.toInt(),
        0xFF3FE6FF.toInt(),
        Gradient.LeftToRight
      )
    } else if (entitled && module.name.equals("Full Bright", ignoreCase = true)) {
      val textX = x + 20F + xOffset
      val textY = y + height / 2F - 6.5F
      val glowColor = 0x80FFF0A0.toInt()
      NVGRenderer.text(module.name, textX - 1f, textY, 13F, glowColor)
      NVGRenderer.text(module.name, textX + 1f, textY, 13F, glowColor)
      NVGRenderer.text(module.name, textX, textY - 1f, 13F, glowColor)
      NVGRenderer.text(module.name, textX, textY + 1f, 13F, glowColor)
      NVGRenderer.text(module.name, textX, textY, 13F, 0xFFFFF4C2.toInt())
    } else {
      NVGRenderer.text(
        module.name, x + 20F + xOffset, y + height / 2F - 6.5F, 13F,
        textColor
      )
    }
  }

  override fun mouseClicked(button: Int): Boolean {
    if (!module.isEntitled) return false
    if (isHoveringOver(x, y, width, height) && button == 0) {
      panel.setModule(this)
      return true
    }

    return false
  }

  fun setSelected(selected: Boolean = true) {
    if (this.selected != selected) {
      this.selected = selected
      colorAnimation.start()
      xOffsetAnimation.start()
    }
  }

  fun getSettings(): List<Setting<*>> {
    return module.getSettings()
  }

  companion object {
    private val selectedIcon = NVGRenderer.createImage("/assets/phantom/textures/ui/selected.svg")
    private val lockIcon = NVGRenderer.createImage("/assets/phantom/textures/ui/lock.svg")
  }

}
