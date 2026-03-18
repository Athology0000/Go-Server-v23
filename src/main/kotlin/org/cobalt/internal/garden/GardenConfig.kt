package org.cobalt.internal.garden

/** Shared config values populated by GardenMacroModule at startup. */
object GardenConfig {
    @Volatile var pestThreshold: Int = 4
    @Volatile var aotvEnabled: Boolean = false
    @Volatile var roofPitch: Double = -80.0
    @Volatile var autoWardrobeEnabled: Boolean = false
    @Volatile var farmingWardrobeSlot: Int = 1
    @Volatile var pestWardrobeSlot: Int = 2
    @Volatile var visitorWardrobeSlot: Int = 3
    @Volatile var farmingArmor: String = ""
    @Volatile var pestArmor: String = ""
    @Volatile var visitorArmor: String = ""
    @Volatile var swapDelayMs: Long = 300L
    @Volatile var farmScript: String = "farm"
    @Volatile var pestScript: String = "pest"
    @Volatile var returnScript: String = "return"
    @Volatile var visitorScript: String = "visitor"
    @Volatile var georgeRarity: String = "LEGENDARY"
    @Volatile var bookCombineLevel: Int = 5
    @Volatile var junkItems: String = ""
    @Volatile var cookieItem: String = ""
    @Volatile var workDurationMins: Long = 60L
    @Volatile var workOffsetMins: Long = 5L
    @Volatile var breakDurationMins: Long = 10L
    @Volatile var breakOffsetMins: Long = 2L
    @Volatile var maxRecoveryAttempts: Int = 15
    @Volatile var reconnectDelayMin: Long = 30L
    @Volatile var reconnectDelayMax: Long = 60L
    @Volatile var bazaarRefreshSecs: Long = 120L
}
