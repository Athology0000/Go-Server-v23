package org.cobalt.internal.pathfinding

import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.pathfinder.PathService

/**
 * Ticks the active path request even when the Pathfinding UI module is disabled
 * or when a macro owns the path. This mirrors RDBT V5's always-on PathExecutor.
 */
object PathServiceTickBridge {
  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (PathService.isActive) {
      PathService.tick()
    }
  }
}
