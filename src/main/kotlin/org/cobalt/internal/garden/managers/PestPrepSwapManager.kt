package org.cobalt.internal.garden.managers

object PestPrepSwapManager {

    @Volatile var swapDone = false

    fun reset() { swapDone = false }

    fun shouldPrepSwap(aliveCount: Int, threshold: Int): Boolean {
        if (swapDone) return false
        return aliveCount >= (threshold - 1).coerceAtLeast(1)
    }

    fun markDone() { swapDone = true }
}
