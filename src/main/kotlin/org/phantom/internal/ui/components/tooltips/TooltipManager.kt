package org.phantom.internal.ui.components.tooltips

import org.phantom.internal.ui.UIComponent

internal object TooltipManager {

  private val tooltips = mutableListOf<UIComponent>()

  fun register(tooltip: UIComponent) {
    tooltips.add(tooltip)
  }

  fun renderAll() {
    tooltips.forEach { it.render() }
  }

}
