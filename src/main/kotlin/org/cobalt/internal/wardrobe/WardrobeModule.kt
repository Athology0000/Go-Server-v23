package org.cobalt.internal.wardrobe

import org.cobalt.api.module.Module

/**
 * Wardrobe display module for rendering custom UI over AbstractContainerScreen.
 * This is a minimal stub; full implementation is in progress (Task 6).
 */
object WardrobeModule : Module("Wardrobe") {

    // ── Stub fields used by WardrobeRenderer (Task 5) ────────────────────────
    // Will be replaced with full implementations in Task 6.

    /** The currently displayed custom page (1–3). */
    var currentCustomPage: Int = 1

    /**
     * Returns the list of [WardrobeSet]s that should be shown on [currentCustomPage].
     * Stub: always returns an empty list until Task 6 provides full page mapping.
     */
    fun setsOnCurrentCustomPage(): List<WardrobeSet> = emptyList()

    /** Hit-boxes for each slot card, written by the renderer and read by click handling. */
    var slotHitboxes: List<SlotHitbox> = emptyList()

    /** Hit-boxes for the page tab buttons. */
    var tabHitboxes: List<TabHitbox> = emptyList()

    /** Hit-boxes for the Back / Close action buttons. */
    var buttonHitboxes: List<ButtonHitbox> = emptyList()

    // ── Inner data classes ────────────────────────────────────────────────────

    data class SlotHitbox(val setId: Int, val x: Float, val y: Float, val w: Float, val h: Float)

    data class TabHitbox(val page: Int, val x: Float, val y: Float, val w: Float, val h: Float)

    enum class ButtonType { BACK, CLOSE }

    data class ButtonHitbox(val type: ButtonType, val x: Float, val y: Float, val w: Float, val h: Float)

    // ── Render suppression ────────────────────────────────────────────────────

    /**
     * Returns true if vanilla AbstractContainerScreen render should be suppressed
     * in favor of custom rendering.
     */
    fun shouldSuppressVanillaRender(): Boolean {
        // TODO: Implement full logic in Task 6
        return false
    }
}
