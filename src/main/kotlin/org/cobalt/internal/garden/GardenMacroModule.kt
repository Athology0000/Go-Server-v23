package org.cobalt.internal.garden

import net.minecraft.client.Minecraft
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.ChatEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.hud.HudAnchor
import org.cobalt.api.hud.hudElement
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.inGroup
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.internal.garden.managers.BookCombineManager
import org.cobalt.internal.garden.managers.BoosterCookieManager
import org.cobalt.internal.garden.managers.CropFeverManager
import org.cobalt.internal.garden.managers.DynamicRestManager
import org.cobalt.internal.garden.managers.EquipmentManager
import org.cobalt.internal.garden.managers.GearManager
import org.cobalt.internal.garden.managers.GeorgeManager
import org.cobalt.internal.garden.managers.JunkManager
import org.cobalt.internal.garden.managers.PestAotvManager
import org.cobalt.internal.garden.managers.PestBonusManager
import org.cobalt.internal.garden.managers.PestCleaningSequencer
import org.cobalt.internal.garden.managers.PestManager
import org.cobalt.internal.garden.managers.PestPrepSwapManager
import org.cobalt.internal.garden.managers.PestReturnManager
import org.cobalt.internal.garden.managers.PetXpTracker
import org.cobalt.internal.garden.managers.ProfitManager
import org.cobalt.internal.garden.managers.RecoveryManager
import org.cobalt.internal.garden.managers.RestartManager
import org.cobalt.internal.garden.managers.RodManager
import org.cobalt.internal.garden.managers.VisitorManager
import org.cobalt.internal.garden.managers.WardrobeManager

object GardenMacroModule : Module("Garden Macro") {

    private val mc = Minecraft.getInstance()

    // ── State ─────────────────────────────────────────────────────────────────
    @Volatile var state = GardenState.OFF
        private set
    @Volatile var sessionStartTime = System.currentTimeMillis()
    @Volatile var autosellingManager: String? = null
    @Volatile private var wasConnected = true

    fun setState(newState: GardenState) {
        mc.execute { state = newState }
    }

    // ── Settings ──────────────────────────────────────────────────────────────

    // General
    private val enabledSetting = CheckboxSetting("Enabled", "Run the garden macro.", false)

    // Scripts
    private val farmScriptSetting    = TextSetting("Farm Script",    "Taunahi script name for farming (e.g. netherwart:1).",      "netherwart:1").inGroup("Scripts")
    private val pestScriptSetting    = TextSetting("Pest Script",    "Taunahi script name for pest cleaning.",                    "misc:pestCleaner").inGroup("Scripts")
    private val returnScriptSetting  = TextSetting("Return Script",  "Taunahi script name to run after pest clean.",              "misc:visitor").inGroup("Scripts")
    private val visitorScriptSetting = TextSetting("Visitor Script", "Taunahi script name for visitors.",                        "misc:visitor").inGroup("Scripts")

    // Pest
    private val pestThresholdSetting = SliderSetting("Pest Threshold", "Alive count to trigger cleaning.", 4.0, 1.0, 8.0, step = 1.0).inGroup("Pest")
    private val aotvEnabledSetting   = CheckboxSetting("AOTV to Roof",  "Teleport to roof before pest clean.", false).inGroup("Pest")
    private val aotvPlotsSetting     = TextSetting("AOTV Plots",     "Comma-separated plot names.",       "").inGroup("Pest")
    private val prepSwapSetting      = CheckboxSetting("Prep Swap",     "Swap gear before threshold hit.",   false).inGroup("Pest")
    private val roofPitchSetting     = SliderSetting("Roof Pitch",     "Camera pitch for roof teleport.",   -80.0, -90.0, 90.0).inGroup("Pest")

    // Visitor
    private val autoVisitorSetting = CheckboxSetting("Auto Visitor", "Handle visitor offers automatically.", false).inGroup("Visitor")

    // Wardrobe
    private val autoWardrobeSetting   = CheckboxSetting("Auto Wardrobe",   "Swap wardrobe slots automatically.",   false).inGroup("Wardrobe")
    private val farmingSlotSetting    = SliderSetting("Farming Slot",   "Wardrobe slot for farming.",          1.0, 1.0, 18.0, step = 1.0).inGroup("Wardrobe")
    private val pestSlotSetting       = SliderSetting("Pest Slot",      "Wardrobe slot for pest cleaning.",    2.0, 1.0, 18.0, step = 1.0).inGroup("Wardrobe")
    private val visitorSlotSetting    = SliderSetting("Visitor Slot",   "Wardrobe slot for visitors.",         3.0, 1.0, 18.0, step = 1.0).inGroup("Wardrobe")

    // Equipment
    private val farmingArmorSetting = TextSetting("Farming Armor", "Armor set name for farming.",   "").inGroup("Equipment")
    private val pestArmorSetting    = TextSetting("Pest Armor",    "Armor set name for pest.",      "").inGroup("Equipment")
    private val visitorArmorSetting = TextSetting("Visitor Armor", "Armor set name for visitors.",  "").inGroup("Equipment")
    private val swapDelaySetting    = SliderSetting("Swap Delay", "Ms between equipment swaps.", 300.0, 0.0, 2000.0).inGroup("Equipment")

    // Economy
    private val bazaarRefreshSetting = SliderSetting("Bazaar Refresh", "Seconds between Bazaar updates.", 120.0, 30.0, 600.0).inGroup("Economy")
    private val georgeRaritySetting  = TextSetting("George Rarity", "Rarities to sell (LEGENDARY,MYTHIC).", "LEGENDARY").inGroup("Economy")
    private val bookLevelSetting     = SliderSetting("Book Level", "Enchant level to combine books at.", 5.0, 1.0, 10.0, step = 1.0).inGroup("Economy")
    private val junkItemsSetting     = TextSetting("Junk Items", "Comma-separated item names to drop.", "").inGroup("Economy")
    private val cookieItemSetting    = TextSetting("Cookie Item", "Item name to use booster cookie on.", "").inGroup("Economy")

    // Rest
    private val workDurationSetting  = SliderSetting("Work Duration",  "Minutes to farm before resting.",   60.0, 1.0, 240.0).inGroup("Rest")
    private val workOffsetSetting    = SliderSetting("Work Offset",    "Random offset for work duration.",   5.0, 0.0, 30.0).inGroup("Rest")
    private val breakDurationSetting = SliderSetting("Break Duration", "Minutes to rest before resuming.",  10.0, 1.0, 60.0).inGroup("Rest")
    private val breakOffsetSetting   = SliderSetting("Break Offset",   "Random offset for break duration.",  2.0, 0.0, 15.0).inGroup("Rest")

    // Advanced
    private val hideFilteredChatSetting = CheckboxSetting("Hide Chat Spam", "Filter bot-related chat messages.", false).inGroup("Advanced")
    private val maxRecoverySetting      = SliderSetting("Max Recovery", "Max auto-reconnect attempts.",     15.0, 1.0, 30.0, step = 1.0).inGroup("Advanced")
    private val reconnectMinSetting     = SliderSetting("Reconnect Min", "Min seconds before reconnecting.", 30.0, 5.0, 120.0).inGroup("Advanced")
    private val reconnectMaxSetting     = SliderSetting("Reconnect Max", "Max seconds before reconnecting.", 60.0, 5.0, 120.0).inGroup("Advanced")

    // ── HUD ───────────────────────────────────────────────────────────────────
    private val hudWidth = 200f

    private val gardenHudElement = hudElement("garden-macro-hud", "Garden Macro HUD", "Macro status and profit tracking") {
        anchor  = HudAnchor.TOP_LEFT
        offsetX = 10f
        offsetY = 10f

        val showProfitSetting = setting(CheckboxSetting("Show Profit", "Show profit section.", true))
        val showRestSetting   = setting(CheckboxSetting("Show Rest",   "Show rest countdown.", true))

        fun computeHeight(): Float {
            var h = 48f
            if (showRestSetting.value) h += 36f
            if (showProfitSetting.value) h += 62f
            return h
        }

        width  { hudWidth }
        height { computeHeight() }

        render { x, y, _ ->
            if (!enabledSetting.value && state == GardenState.OFF) return@render
            GardenHud.render(x, y, hudWidth, computeHeight(), state, showProfitSetting.value, showRestSetting.value, sessionStartTime)
        }
    }

    // ── init ──────────────────────────────────────────────────────────────────
    init {
        addSetting(
            enabledSetting,
            farmScriptSetting, pestScriptSetting, returnScriptSetting, visitorScriptSetting,
            pestThresholdSetting, aotvEnabledSetting, aotvPlotsSetting, prepSwapSetting, roofPitchSetting,
            autoVisitorSetting,
            autoWardrobeSetting, farmingSlotSetting, pestSlotSetting, visitorSlotSetting,
            farmingArmorSetting, pestArmorSetting, visitorArmorSetting, swapDelaySetting,
            bazaarRefreshSetting, georgeRaritySetting, bookLevelSetting, junkItemsSetting, cookieItemSetting,
            workDurationSetting, workOffsetSetting, breakDurationSetting, breakOffsetSetting,
            hideFilteredChatSetting, maxRecoverySetting, reconnectMinSetting, reconnectMaxSetting
        )

        EventBus.register(this)
    }

    // ── GardenConfig sync ─────────────────────────────────────────────────────
    private fun syncConfig() {
        GardenConfig.pestThreshold       = pestThresholdSetting.value.toInt()
        GardenConfig.aotvEnabled         = aotvEnabledSetting.value
        GardenConfig.roofPitch           = roofPitchSetting.value
        GardenConfig.autoWardrobeEnabled = autoWardrobeSetting.value
        GardenConfig.farmingWardrobeSlot = farmingSlotSetting.value.toInt()
        GardenConfig.pestWardrobeSlot    = pestSlotSetting.value.toInt()
        GardenConfig.visitorWardrobeSlot = visitorSlotSetting.value.toInt()
        GardenConfig.farmingArmor        = farmingArmorSetting.value
        GardenConfig.pestArmor           = pestArmorSetting.value
        GardenConfig.visitorArmor        = visitorArmorSetting.value
        GardenConfig.swapDelayMs         = swapDelaySetting.value.toLong()
        GardenConfig.farmScript          = farmScriptSetting.value
        GardenConfig.pestScript          = pestScriptSetting.value
        GardenConfig.returnScript        = returnScriptSetting.value
        GardenConfig.visitorScript       = visitorScriptSetting.value
        GardenConfig.georgeRarity        = georgeRaritySetting.value
        GardenConfig.bookCombineLevel    = bookLevelSetting.value.toInt()
        GardenConfig.junkItems           = junkItemsSetting.value
        GardenConfig.cookieItem          = cookieItemSetting.value
        GardenConfig.workDurationMins    = workDurationSetting.value.toLong()
        GardenConfig.workOffsetMins      = workOffsetSetting.value.toLong()
        GardenConfig.breakDurationMins   = breakDurationSetting.value.toLong()
        GardenConfig.breakOffsetMins     = breakOffsetSetting.value.toLong()
        GardenConfig.maxRecoveryAttempts = maxRecoverySetting.value.toInt()
        GardenConfig.reconnectDelayMin   = reconnectMinSetting.value.toLong()
        GardenConfig.reconnectDelayMax   = reconnectMaxSetting.value.toLong()
        GardenConfig.bazaarRefreshSecs   = bazaarRefreshSetting.value.toLong()
    }

    // ── Events ────────────────────────────────────────────────────────────────

    @SubscribeEvent
    fun onTick(event: TickEvent.End) {
        val enabled = enabledSetting.value

        // Start/stop on enabled toggle
        if (enabled && state == GardenState.OFF) {
            startMacro()
            return
        }
        if (!enabled && state != GardenState.OFF) {
            stopMacro()
            return
        }
        if (!enabled) return

        syncConfig()

        // Disconnect detection
        val connected = mc.connection != null
        if (!connected && wasConnected && state != GardenState.RECOVERING && state != GardenState.RESTING) {
            setState(GardenState.RECOVERING)
            RecoveryManager.onDisconnect {
                setState(GardenState.FARMING)
                ScriptBridge.startFarming(GardenConfig.farmScript)
            }
        }
        wasConnected = connected

        if (!connected) return

        // Route to managers
        CropFeverManager.update()
        PestBonusManager.update()
        RestartManager.update { stopMacro() }
        ProfitManager.refreshBazaarIfNeeded()
        PetXpTracker.update()

        when (state) {
            GardenState.FARMING -> tickFarming()
            GardenState.RESTING -> { /* DynamicRestManager manages the timer */ }
            else -> { /* Other states managed by their worker tasks */ }
        }
    }

    @SubscribeEvent
    fun onChat(event: ChatEvent.Receive) {
        val msg = event.message?.replace(Regex("§[0-9a-fk-or]"), "") ?: return

        // Chat spam filter
        if (hideFilteredChatSetting.value) {
            val lower = msg.lowercase()
            if (lower.contains("pet killed") || lower.contains("macro started") || lower.contains("script started")) {
                event.setCancelled(true)
                return
            }
        }

        CropFeverManager.onChatMessage(msg)
        RestartManager.onChatMessage(msg) { stopMacro() }
        ProfitManager.onChatMessage(msg)
        VisitorManager.onChatMessage(msg)
    }

    private fun tickFarming() {
        // Rest check
        if (DynamicRestManager.shouldRest()) {
            setState(GardenState.RESTING)
            DynamicRestManager.startRest {
                setState(GardenState.FARMING)
                ScriptBridge.startFarming(GardenConfig.farmScript)
            }
            return
        }

        // Visitor check
        if (autoVisitorSetting.value && VisitorManager.shouldHandle() && !CropFeverManager.shouldDelay()) {
            setState(GardenState.VISITING)
            VisitorManager.startVisitorSequence { setState(GardenState.FARMING) }
            return
        }

        // AUTOSELLING — priority: George > BookCombine > Junk
        if (autosellingManager == null) {
            if (GeorgeManager.shouldSell()) {
                autosellingManager = "george"
                setState(GardenState.AUTOSELLING)
                GeorgeManager.startSell { autosellingManager = null; setState(GardenState.FARMING) }
                return
            }
            if (BookCombineManager.shouldCombine()) {
                autosellingManager = "book"
                setState(GardenState.AUTOSELLING)
                BookCombineManager.startCombine { autosellingManager = null; setState(GardenState.FARMING) }
                return
            }
            if (JunkManager.shouldDrop()) {
                autosellingManager = "junk"
                setState(GardenState.AUTOSELLING)
                JunkManager.startDrop { autosellingManager = null; setState(GardenState.FARMING) }
                return
            }
        }

        // Pest prep-swap check
        if (prepSwapSetting.value && !PestPrepSwapManager.swapDone) {
            val count = PestManager.lastAliveCount
            if (PestPrepSwapManager.shouldPrepSwap(count, GardenConfig.pestThreshold)) {
                PestPrepSwapManager.markDone()
                GardenWorkerThread.submit("prep-swap") { GearManager.swapForPest() }
            }
        }

        // Pest cleaning trigger
        if (!CropFeverManager.shouldDelay() && PestManager.update(GardenConfig.pestThreshold)) {
            setState(GardenState.CLEANING)
            PestCleaningSequencer.startSequence { setState(GardenState.FARMING) }
        }
    }

    private fun startMacro() {
        syncConfig()
        sessionStartTime = System.currentTimeMillis()
        state = GardenState.FARMING
        // Reset all managers
        listOf<() -> Unit>(
            PestManager::reset, PestCleaningSequencer::reset, PestAotvManager::reset,
            PestPrepSwapManager::reset, PestReturnManager::reset, PestBonusManager::reset,
            CropFeverManager::reset, VisitorManager::reset, WardrobeManager::reset,
            EquipmentManager::reset, GearManager::reset, RodManager::reset,
            GeorgeManager::reset, BookCombineManager::reset, JunkManager::reset,
            BoosterCookieManager::reset, ProfitManager::reset, PetXpTracker::reset,
            DynamicRestManager::reset, RecoveryManager::reset, RestartManager::reset,
        ).forEach { it() }
        autosellingManager = null
        wasConnected = true
        ScriptBridge.startFarming(GardenConfig.farmScript)
    }

    private fun stopMacro() {
        GardenWorkerThread.shutdown()
        ScriptBridge.stopScript()
        state = GardenState.OFF
    }
}
