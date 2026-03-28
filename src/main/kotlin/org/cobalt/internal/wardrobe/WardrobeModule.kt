package org.cobalt.internal.wardrobe

import org.cobalt.api.module.Module

/**
 * Wardrobe display module for rendering custom UI over AbstractContainerScreen.
 * This is a minimal stub; full implementation is in progress.
 */
object WardrobeModule : Module("Wardrobe") {

    /**
     * Returns true if vanilla AbstractContainerScreen render should be suppressed
     * in favor of custom rendering.
     */
    fun shouldSuppressVanillaRender(): Boolean {
        // TODO: Implement full logic in Task 6
        return false
    }
}
