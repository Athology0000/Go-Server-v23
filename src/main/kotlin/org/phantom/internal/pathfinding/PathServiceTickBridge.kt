package org.phantom.internal.pathfinding

import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.pathfinder.PathService

/**
 * Ticks the active path request even when the Pathfinding UI module is disabled
 * or when a macro owns the path. This mirrors RDBT Phantom's always-on PathExecutor.
 */
object PathServiceTickBridge {
  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (PathService.isActive) {
      PathService.tick()
    }
  }
}
