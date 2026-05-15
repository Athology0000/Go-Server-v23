package org.phantom.internal.rotation

import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.render.WorldRenderEvent

object PhantomRotation {
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
