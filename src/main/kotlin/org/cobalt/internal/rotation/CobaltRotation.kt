package org.cobalt.internal.rotation

import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.render.WorldRenderEvent

object CobaltRotation {
    val blockController = BlockRotationController()

    /** Frame-driven: produces smooth sweeping motion at the actual render FPS. */
    @SubscribeEvent
    fun onFrame(@Suppress("UNUSED_PARAMETER") event: WorldRenderEvent.Last) {
        blockController.updateFrame()
    }

    fun tick() {
        blockController.tick()
    }
}
