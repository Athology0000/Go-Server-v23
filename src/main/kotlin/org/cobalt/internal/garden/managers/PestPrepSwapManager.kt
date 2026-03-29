package org.cobalt.internal.garden.managers

object PestPrepSwapManager {

    @Volatile var swapDone = false
    /** System.currentTimeMillis() when we became eligible to run the prep-swap
     *  (i.e., the post-clean cooldown expired). 0 means not yet eligible. */
    @Volatile private var activeSince = 0L

    fun reset() {
        swapDone = false
        activeSince = 0L
    }

    /** Call each tick once cleaningCooldownUntil has passed, so the fallback timer starts. */
    fun markActive() {
        if (activeSince == 0L) activeSince = System.currentTimeMillis()
    }

    fun shouldPrepSwap(cooldownSeconds: Int): Boolean {
        if (swapDone) return false
        // Tab-list cooldown detected: fire when 1-60 s remain before spawn
        if (cooldownSeconds in 1..60) return true
        // Fallback: if the tab list never yields a cooldown (regex mismatch / format unknown),
        // fire 30 s after we became eligible so pest gear is on well before spawns.
        if (cooldownSeconds == 0 && activeSince > 0L &&
            System.currentTimeMillis() - activeSince >= 30_000L) return true
        return false
    }

    fun markDone() { swapDone = true }
}
