package org.cobalt.internal.garden.managers

object PestManager {

    @Volatile var lastAliveCount = 0
    @Volatile var cleaningCooldownUntil = 0L
    @Volatile var prepSwapDoneForCycle = false

    fun reset() {
        lastAliveCount = 0
        cleaningCooldownUntil = 0L
        prepSwapDoneForCycle = false
    }

    fun update(threshold: Int): Boolean {
        val data = PestTabListParser.parse()
        lastAliveCount = data.alivePests
        if (System.currentTimeMillis() < cleaningCooldownUntil) return false
        return data.alivePests >= threshold
    }

    fun startCooldown(durationMs: Long) {
        cleaningCooldownUntil = System.currentTimeMillis() + durationMs
        prepSwapDoneForCycle = false
    }
}
