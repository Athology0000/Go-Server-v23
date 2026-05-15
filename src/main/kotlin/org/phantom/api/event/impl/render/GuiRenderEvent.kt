package org.phantom.api.event.impl.render

import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import org.phantom.api.event.Event

class GuiRenderEvent(
  val graphics: GuiGraphics,
  val delta: DeltaTracker
) : Event()

