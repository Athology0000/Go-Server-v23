package org.phantom.internal.mining

import java.util.Locale
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.ChatEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.InfoSetting
import org.phantom.api.module.setting.impl.InfoType
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.rotation.RotationExecutor
import org.phantom.api.util.*
import org.phantom.internal.mining.tunnels.TunnelMinerModule
import org.phantom.internal.mining.tunnels.TunnelOreType
import org.phantom.internal.pathfinding.PathfindingModule
import org.phantom.internal.skyblock.DwarvenSidebarLocationParser
import org.phantom.internal.skyblock.HypixelManager

/**
 * Mirrors Phantom's GlaciteCommissionMacro.js exactly.
 *
 * Loop: IDLE â†’ CHOOSING â†’ MINING â†’ CLAIMING â†’ WAITING_GUI_CLOSE â†’ CHOOSING
 *
 * No TRAVELING state â€” TunnelMinerModule handles its own pathfinding to vein edges,
 * exactly like Phantom's TunnelsMiner.
 *
 * Claiming requires Royal Pigeon. The macro mines one tab-list commission,
 * opens Royal Pigeon, claims, then selects the next commission from tab.
 */
object GlaciteCommissionMacroModule : Module("Glacite Commission Macro") {

    override val category = ModuleCategory.MINING

    override fun isVisibleInUi(): Boolean =
        isInGlaciteTunnels()

    private val mc = Minecraft.getInstance()

    private val enabled = CheckboxSetting(
        "Enabled",
        "Completes Glacite tunnel ore commissions using Tunnel Miner.",
        false,
    )

    private val info = InfoSetting(
        "Setup",
        "Requires a drill/pickaxe and Royal Pigeon in hotbar, with Glacite commissions visible in /tab.",
        InfoType.INFO,
    )

    private val swapPickobulusForGlacite = CheckboxSetting(
        "Glacite Pickobulus",
        "Before mining a Glacite commission, open HOTM and select Pickobulus.",
        true,
    )

    private val coldThreshold = SliderSetting(
        "Cold Threshold",
        "Pause commission mining at this cold value. Set to 0 to disable.",
        90.0,
        0.0,
        100.0,
        1.0,
    )

    private enum class State {
        IDLE, CHOOSING, HOTM_PICKOBULUS, MINING, CLAIMING, WAITING_GUI_CLOSE
    }

    private data class Commission(val name: String, val progress: Double)

    // Phantom SUPPORTED_ORES order (determines priority when multiple commissions active)
    private val SUPPORTED_ORES = listOf("glacite", "umber", "tungsten", "peridot", "aquamarine", "onyx", "citrine")

    private var state = State.IDLE
    private var pauseTicks = 0
    private var commissions: List<Commission> = emptyList()
    private var currentCommission: Commission? = null
    private var activeOreTypes: List<TunnelOreType> = emptyList()

    private var awaitingTabUpdate = false
    private var ignoreTabUpdatesUntil = 0L
    private var lastCommissionSyncSource: String? = null
    private var lastCompletedCommissionName: String? = null

    private var lastTunnelRestartAt = 0L
    private var noSupportedMessageAt = 0L

    private val COLD_PATTERNS = listOf(
        Regex("""(?i)\bcold\s*[:\-]?\s*([0-9]{1,4})"""),
        Regex("""(?i)([0-9]{1,4})\s*(?:❄|cold)\b"""),
    )

    // Claiming state (mirrors Phantom pigeonAttempts / firstPigeonAttemptAt)
    private var pigeonAttempts = 0
    private var firstPigeonAttemptAt = 0L
    private var claimAttempts = 0
    private var npcRotationPending = false
    private var claimPathActive = false
    private var ownsMiningMacro = false
    private var pickobulusOpenTick = 0L
    private var pickobulusClickAttempts = 0
    private var pickobulusPreparedCommissionName: String? = null
    private var coldPaused = false
    private var lastColdWarningAt = 0L
    private var lastColdWarpAt = 0L

    private var pendingUseRelease = false
    private var lastAreaWarpAt = 0L

    val statusDisplay: String get() = state.name.toDisplayLabel()
    val modeDisplay: String get() = "GLACITE"
    val commissionDisplay: String
        get() {
            val commission = currentCommission ?: getSupportedActiveCommissions().firstOrNull()
            return commission?.let { "${it.name} ${formatProgressDisplay(it.progress)}" } ?: "None"
        }
    val currentZoneDisplay: String get() = detectAreaFromTab() ?: "Unknown"
    val isRunning: Boolean get() = enabled.value

    init {
        addSetting(enabled, swapPickobulusForGlacite, coldThreshold, info)
        EventBus.register(this)
    }

    // â”€â”€ Tick â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @SubscribeEvent
    fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
        if (pendingUseRelease) {
            mc.options.keyUse.setDown(false)
            pendingUseRelease = false
        }

        if (!enabled.value) {
            if (state != State.IDLE) resetState()
            return
        }

        if (pauseTicks > 0) {
            pauseTicks--
            return
        }

        if (handleColdThreshold()) return

        // Phantom reads tab on every step (1-tick frequency)
        updateCommissionsIfChanged(readGlaciteCommissionsFromTab())

        when (state) {
            State.IDLE              -> handleIdle()
            State.CHOOSING          -> handleChoosing()
            State.HOTM_PICKOBULUS   -> handleHotmPickobulus()
            State.MINING            -> handleMining()
            State.CLAIMING          -> handleClaiming()
            State.WAITING_GUI_CLOSE -> handleWaitingGuiClose()
        }
    }

    @SubscribeEvent
    fun onChat(event: ChatEvent.Receive) {
        if (!enabled.value) return
        val msg = ChatFormatting.stripFormatting(event.message ?: "")?.lowercase(Locale.US).orEmpty()

        if (msg.contains("commission complete") || msg.contains("completed a commission")) {
            onCommissionComplete()
        }

        if (msg.contains("you died") || msg.contains("sending to server")) {
            delayedReset(67)
        }
    }

    // â”€â”€ States â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private fun handleIdle() {
        val player = mc.player ?: return
        val hasTool = (0..8).any { slot ->
            isMiningTool(player.inventory.getItem(slot))
        }
        if (!hasTool) {
            ChatUtils.sendMessage("Glacite Commission Macro: no drill, gauntlet, or pickaxe found in hotbar.")
            enabled.value = false
            return
        }

        if (!hasRoyalPigeon()) {
            ChatUtils.sendMessage("Glacite Commission Macro: Royal Pigeon is required in your hotbar.")
            enabled.value = false
            return
        }

        setState(State.CHOOSING)
    }

    private fun handleColdThreshold(): Boolean {
        val threshold = coldThreshold.value.toInt()
        if (threshold <= 0) {
            coldPaused = false
            return false
        }

        if (state == State.IDLE || state == State.CLAIMING || state == State.WAITING_GUI_CLOSE) {
            return false
        }

        val cold = currentCold() ?: return false
        val resumeBelow = (threshold - 5).coerceAtLeast(0)

        if (coldPaused) {
            if (cold <= resumeBelow) {
                coldPaused = false
                ChatUtils.sendMessage("Glacite Commission Macro: cold is $cold, resuming.")
                return false
            }

            stopTunnelMiner()
            PathfindingModule.stopPath()
            delay(20)
            return true
        }

        if (cold >= threshold) {
            coldPaused = true
            stopTunnelMiner()
            PathfindingModule.stopPath()
            mc.player?.closeContainer()

            val now = System.currentTimeMillis()
            if (now - lastColdWarpAt > 10_000L) {
                lastColdWarpAt = now
                sendCommand("warp camp")
            }

            if (now - lastColdWarningAt > 5_000L) {
                lastColdWarningAt = now
                ChatUtils.sendMessage("Glacite Commission Macro: cold $cold reached threshold $threshold, warping to camp.")
            }

            delay(20)
            return true
        }

        return false
    }

    private fun handleChoosing() {
        val now = System.currentTimeMillis()
        val area = detectAreaFromTab()
        val validAreas = listOf("Glacite Tunnels", "Fossil Research Center", "Dwarven Base Camp")

        if (area == null || validAreas.none { area.contains(it, ignoreCase = true) }) {
            if (now - lastAreaWarpAt > 10_000L) {
                ChatUtils.sendMessage("Glacite Commission Macro: not in Glacite area, warping to camp...")
                sendCommand("warp camp")
                lastAreaWarpAt = now
            }
            return
        }
        lastAreaWarpAt = 0L

        if (shouldWaitForLastCompleted()) return
        if (awaitingTabUpdate) return

        val completed = findCompletedSupportedCommission()
        if (completed != null) {
            currentCommission = completed
            onCommissionComplete()
            return
        }

        val supported = getSupportedActiveCommissions()
        if (supported.isEmpty()) {
            notifyNoSupportedCommissions()
            delay(10)
            return
        }

        startMiningCommission(supported.first())
    }

    private fun handleMining() {
        if (shouldWaitForLastCompleted()) return
        if (awaitingTabUpdate) return

        val completed = findCompletedSupportedCommission()
        if (completed != null) {
            currentCommission = completed
            onCommissionComplete()
            return
        }

        val supported = getSupportedActiveCommissions()
        if (supported.isEmpty()) {
            stopTunnelMiner()
            activeOreTypes = emptyList()
            currentCommission = null
            setState(State.CHOOSING)
            return
        }

        val current = currentCommission
        val stillCurrent = current?.let { active ->
            supported.firstOrNull { it.name == active.name }
        }

        if (stillCurrent == null) {
            startMiningCommission(supported.first())
            return
        }

        currentCommission = stillCurrent
        val neededOres = collectOreTypes(listOf(stillCurrent))
        if (!sameOres(neededOres, activeOreTypes)) {
            startMiningCommission(stillCurrent)
            return
        }

        // Phantom: if (!Pathfinder.isPathing() && !MiningBot.enabled && now - lastTunnelRestartAt >= 5000)
        //       tunnelsMiner.restart()
        val now = System.currentTimeMillis()
        if (!TunnelMinerModule.isActive && now - lastTunnelRestartAt >= 5_000L) {
            beginTunnelMiner()
        }
    }

    private fun handleClaiming() {
        val screen = mc.screen as? AbstractContainerScreen<*>
        if (screen != null) {
            claimCompletedCommissions(screen)
            return
        }

        val now = System.currentTimeMillis()
        val pigeonSlot = InventoryUtils.findItemInHotbar("Royal Pigeon")

        if (pigeonSlot < 0) {
            ChatUtils.sendMessage("Glacite Commission Macro: Royal Pigeon is required to claim commissions.")
            enabled.value = false
            return
        }

        val player = mc.player ?: return
        val pigeonTimedOut = firstPigeonAttemptAt > 0L && now - firstPigeonAttemptAt > 6_000L

        if (pigeonAttempts >= 6 || pigeonTimedOut) {
            ChatUtils.sendMessage("Glacite Commission Macro: failed to open Royal Pigeon.")
            enabled.value = false
            return
        }

        if (player.inventory.selectedSlot != pigeonSlot) {
            InventoryUtils.holdHotbarSlot(pigeonSlot)
            delay(3)
        } else {
            if (firstPigeonAttemptAt == 0L) firstPigeonAttemptAt = now
            pigeonAttempts++
            rightClick()
            delay(10)
        }
    }

    private fun handleHotmPickobulus() {
        val commission = currentCommission ?: run {
            setState(State.CHOOSING)
            return
        }

        val screen = mc.screen as? AbstractContainerScreen<*>
        val nowTick = mc.level?.gameTime ?: 0L

        if (screen == null) {
            if (pickobulusOpenTick == 0L || nowTick - pickobulusOpenTick > 40L) {
                pickobulusOpenTick = nowTick
                sendCommand("hotm")
            }
            return
        }

        val title = screen.title.string.lowercase(Locale.US)
        if (!title.contains("heart") && !title.contains("hotm")) {
            mc.player?.closeContainer()
            delay(5)
            return
        }

        val slot = findPickobulusSlot(screen)
        if (slot == null) {
            if (nowTick - pickobulusOpenTick > 100L) {
                ChatUtils.sendMessage("Glacite Commission Macro: Pickobulus not found in HOTM.")
                mc.player?.closeContainer()
                pickobulusPreparedCommissionName = commission.name
                beginTunnelMiner()
            }
            return
        }

        if (isPickobulusSelected(slot.item) || pickobulusClickAttempts >= 2) {
            mc.player?.closeContainer()
            pickobulusPreparedCommissionName = commission.name
            pickobulusClickAttempts = 0
            delay(5)
            beginTunnelMiner()
            return
        }

        InventoryUtils.clickSlot(slot.index, MouseClickType.LEFT, ClickType.PICKUP)
        pickobulusClickAttempts++
        delay(8)
    }

    private fun handleWaitingGuiClose() {
        if (mc.screen != null) return
        refreshDrillSlot()
        if (activeOreTypes.isNotEmpty()) {
            beginTunnelMiner()
        } else {
            setState(State.CHOOSING)
        }
    }

    // â”€â”€ Commission mining â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private fun startMiningCommission(commission: Commission) {
        val neededOres = collectOreTypes(listOf(commission))
        if (neededOres.isEmpty()) return

        currentCommission = commission
        activeOreTypes = neededOres
        noSupportedMessageAt = 0L
        resetClaimState()

        if (mc.screen != null) {
            mc.player?.closeContainer()
            setState(State.WAITING_GUI_CLOSE)
            return
        }

        if (shouldPreparePickobulus(commission, neededOres)) {
            startPickobulusPrep(commission)
            return
        }

        beginTunnelMiner()
    }

    private fun beginTunnelMiner() {
        val ore = activeOreTypes.firstOrNull() ?: return
        if (ore == TunnelOreType.GLACITE && swapPickobulusForGlacite.value) {
            MiningMacroModule.usePickobulus.value = true
            MiningMacroModule.useMiningSpeedBoost.value = false
        }
        TunnelMinerModule.startForAutomation(ore, "glacite commission macro")
        MiningMacroModule.startForAutomation(miningMacroTypeName(ore))
        ownsMiningMacro = true
        lastTunnelRestartAt = System.currentTimeMillis()
        setState(State.MINING)
    }

    private fun stopTunnelMiner() {
        if (TunnelMinerModule.isActive) TunnelMinerModule.stopForAutomation()
        if (ownsMiningMacro && MiningMacroModule.isActive) {
            MiningMacroModule.stopForAutomation()
        }
        ownsMiningMacro = false
    }

    private fun shouldPreparePickobulus(
        commission: Commission,
        ores: List<TunnelOreType>,
    ): Boolean =
        swapPickobulusForGlacite.value &&
            ores.firstOrNull() == TunnelOreType.GLACITE &&
            pickobulusPreparedCommissionName != commission.name

    private fun startPickobulusPrep(commission: Commission) {
        pickobulusOpenTick = 0L
        pickobulusClickAttempts = 0
        pickobulusPreparedCommissionName = null
        currentCommission = commission
        setState(State.HOTM_PICKOBULUS)
    }

    private fun findPickobulusSlot(screen: AbstractContainerScreen<*>): Slot? =
        screen.menu.slots.firstOrNull { slot ->
            if (!slot.hasItem()) return@firstOrNull false
            val stack = slot.item
            val name = ChatFormatting.stripFormatting(stack.hoverName.string).orEmpty()
            if (name.contains("Pickobulus", ignoreCase = true)) return@firstOrNull true
            stack.getLoreLines().any { line ->
                ChatFormatting.stripFormatting(line.string)
                    ?.contains("Pickobulus", ignoreCase = true) == true
            }
        }

    private fun isPickobulusSelected(stack: ItemStack): Boolean {
        val lines = buildList {
            add(ChatFormatting.stripFormatting(stack.hoverName.string).orEmpty())
            addAll(stack.getLoreLines().mapNotNull { ChatFormatting.stripFormatting(it.string) })
        }

        return lines.any { line ->
            val lower = line.lowercase(Locale.US)
            lower.contains("selected") ||
                lower.contains("currently active") ||
                lower.contains("already selected")
        }
    }

    private fun onCommissionComplete() {
        stopTunnelMiner()
        PathfindingModule.stopPath()
        claimPathActive = false
        awaitingTabUpdate = true
        lastCompletedCommissionName = currentCommission?.name
        resetClaimState()
        setState(State.CLAIMING)
        delay(10)
    }

    private fun claimCompletedCommissions(screen: AbstractContainerScreen<*>) {
        val slots = screen.menu.slots.filterNot { it.container is Inventory }

        for (slot in slots) {
            if (!slot.hasItem()) continue
            val lore = slot.item.getLoreLines()
                .mapNotNull { ChatFormatting.stripFormatting(it.string)?.lowercase(Locale.US) }
            val combined = lore.joinToString("\n")

            if (combined.contains("completed") || combined.contains("click to claim")) {
                if (claimAttempts >= 8) {
                    mc.player?.closeContainer()
                    resetCommissionAfterClaim()
                    setState(State.WAITING_GUI_CLOSE)
                    return
                }
                InventoryUtils.clickSlot(slot.index, MouseClickType.LEFT, ClickType.PICKUP)
                claimAttempts++
                delay(10)
                return
            }
        }

        // No completed slots left â€” done claiming; sync commission list from GUI (Phantom updateCommissionsFromGui)
        updateCommissionsFromGui(screen)
        mc.player?.closeContainer()
        resetCommissionAfterClaim()
        setState(State.WAITING_GUI_CLOSE)
    }

    // â”€â”€ Commission detection â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private fun extractOreTypesFromName(name: String): List<String> {
        val lower = name.lowercase(Locale.US)
        if (lower.contains("powder")) return emptyList()
        return SUPPORTED_ORES.filter { lower.contains(it) }
    }

    private fun isSupportedCommissionName(name: String): Boolean =
        extractOreTypesFromName(name).isNotEmpty()

    private fun collectOreTypes(commissions: List<Commission>): List<TunnelOreType> {
        val oreNames = mutableSetOf<String>()
        for (commission in commissions) {
            oreNames += extractOreTypesFromName(commission.name)
        }
        return SUPPORTED_ORES
            .filter { it in oreNames }
            .mapNotNull { name -> TunnelOreType.entries.firstOrNull { it.miningTypeName == name } }
    }

    private fun miningMacroTypeName(ore: TunnelOreType): String =
        when (ore) {
            TunnelOreType.PERIDOT -> "Peridot Gemstone"
            TunnelOreType.AQUAMARINE -> "Aquamarine Gemstone"
            TunnelOreType.ONYX -> "Onyx Gemstone"
            TunnelOreType.CITRINE -> "Citrine Gemstone"
            else -> ore.displayName
        }

    private fun findCompletedSupportedCommission(): Commission? =
        commissions.firstOrNull { it.progress >= 1.0 && isSupportedCommissionName(it.name) }

    private fun getSupportedActiveCommissions(): List<Commission> =
        commissions.filter { it.progress < 1.0 && isSupportedCommissionName(it.name) }

    private fun shouldWaitForLastCompleted(): Boolean {
        val name = lastCompletedCommissionName ?: return false
        val stale = commissions.firstOrNull { it.name == name && it.progress > 0.0 }
        if (stale?.progress == 1.0) return true
        lastCompletedCommissionName = null
        return false
    }

    private fun notifyNoSupportedCommissions() {
        val now = System.currentTimeMillis()
        if (now - noSupportedMessageAt < 5_000L) return
        noSupportedMessageAt = now
        ChatUtils.sendMessage("Glacite Commission Macro: no supported Glacite mining commissions detected.")
    }

    // â”€â”€ Tab reading â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Read glacite commission names + progress from the tab list.
     *
     * A tab line is considered a commission entry if it contains one of the
     * supported ore keywords (and not "powder"). Progress is stripped from the
     * end; the remaining text becomes the commission name.
     */
    private fun readGlaciteCommissionsFromTab(): List<Commission> {
        val lines = readTabLines()
        val found = linkedMapOf<String, Commission>()

        for (line in lines) {
            val lower = line.lowercase(Locale.US)
            if (lower.contains("powder")) continue
            if (SUPPORTED_ORES.none { lower.contains(it) }) continue

            val progress = parseProgress(lower)

            // Strip trailing progress indicators to get the bare commission name
            val name = line
                .replace(Regex("\\s*[0-9,]+\\s*/\\s*[0-9,]+\\s*$"), "")
                .replace(Regex("\\s*[0-9]{1,3}(?:\\.[0-9]+)?\\s*%\\s*$"), "")
                .trim()

            if (name.isBlank() || name.any { it.isDigit() }) continue

            found[name] = Commission(name, progress)
        }

        return found.values.toList()
    }

    private fun detectAreaFromTab(): String? {
        val areas = listOf("Glacite Tunnels", "Fossil Research Center", "Dwarven Base Camp", "Dwarven Mines")
        val lines = readTabLines()
        return areas.firstOrNull { area -> lines.any { it.contains(area, ignoreCase = true) } }
    }

    private fun currentCold(): Int? {
        val lines = buildList {
            addAll(ScoreboardUtils.sidebarLines())
            addAll(readTabLines())
        }

        for (line in lines) {
            parseColdValue(line)?.let { return it }
        }

        return null
    }

    private fun parseColdValue(raw: String): Int? {
        val cleaned = ChatFormatting.stripFormatting(raw).orEmpty()
            .replace(",", "")
            .trim()
        if (cleaned.isBlank()) return null

        COLD_PATTERNS.forEach { pattern ->
            pattern.find(cleaned)?.groupValues?.getOrNull(1)?.toIntOrNull()?.let { value ->
                return value.coerceIn(0, 1000)
            }
        }

        return null
    }

    private fun updateCommissionsIfChanged(newCommissions: List<Commission>) {
        if (commissions == newCommissions) return

        val now = System.currentTimeMillis()

        // Respect GUI-sourced sync window
        if (ignoreTabUpdatesUntil > 0L && now < ignoreTabUpdatesUntil && lastCommissionSyncSource == "GUI") return

        if (ignoreTabUpdatesUntil > 0L && now < ignoreTabUpdatesUntil && lastCompletedCommissionName != null) {
            val stale = newCommissions.firstOrNull { it.name == lastCompletedCommissionName && it.progress >= 1.0 }
            if (stale != null) return
            ignoreTabUpdatesUntil = 0L
        } else if (ignoreTabUpdatesUntil > 0L && now >= ignoreTabUpdatesUntil) {
            ignoreTabUpdatesUntil = 0L
        }

        commissions = newCommissions
        lastCommissionSyncSource = "TAB"

        if (awaitingTabUpdate) {
            if (lastCompletedCommissionName != null) {
                val still = newCommissions.firstOrNull { it.name == lastCompletedCommissionName }
                if (still == null || still.progress < 1.0) awaitingTabUpdate = false
            } else {
                if (newCommissions.none { it.progress >= 1.0 && isSupportedCommissionName(it.name) }) {
                    awaitingTabUpdate = false
                }
            }
        }
    }

    private fun parseProgress(line: String): Double {
        if (line.contains("done") || line.contains("completed") || line.contains("complete")) return 1.0

        Regex("([0-9]{1,3}(?:\\.[0-9]+)?)\\s*%").find(line)?.let { m ->
            return ((m.groupValues[1].toDoubleOrNull() ?: 0.0) / 100.0).coerceIn(0.0, 1.0)
        }

        Regex("([0-9,]+)\\s*/\\s*([0-9,]+)").find(line)?.let { m ->
            val current = m.groupValues[1].replace(",", "").toDoubleOrNull() ?: 0.0
            val max = m.groupValues[2].replace(",", "").toDoubleOrNull() ?: 1.0
            return if (max <= 0.0) 0.0 else (current / max).coerceIn(0.0, 1.0)
        }

        return 0.0
    }

    private fun formatProgressDisplay(progress: Double): String =
        "${(progress * 100.0).toInt().coerceIn(0, 100)}%"

    private fun String.toDisplayLabel(): String =
        lowercase(Locale.US)
            .split('_')
            .joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() } }

    private fun readTabLines(): List<String> {
        val connection = mc.connection ?: return emptyList()
        return try {
            resolveTabEntries(connection)
                .mapNotNull { resolveEntryDisplayName(it) }
                .map { ChatFormatting.stripFormatting(it)?.trim() ?: it.trim() }
                .filter { it.isNotBlank() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun resolveTabEntries(connection: Any): List<Any> {
        for (name in listOf("listPlayerEntries", "getListedOnlinePlayers", "getOnlinePlayers")) {
            val method = connection.javaClass.methods.firstOrNull { it.name == name && it.parameterCount == 0 }
                ?: continue
            val result = runCatching { method.invoke(connection) }.getOrNull() ?: continue
            when (result) {
                is Collection<*> -> return result.filterNotNull()
                is Iterable<*>   -> return result.filterNotNull()
            }
        }
        return emptyList()
    }

    private fun resolveEntryDisplayName(entry: Any): String? {
        for (name in listOf("getTabListDisplayName", "tabListDisplayName", "getDisplayName", "displayName")) {
            val method = entry.javaClass.methods.firstOrNull { it.name == name && it.parameterCount == 0 } ?: continue
            val text = coerceText(runCatching { method.invoke(entry) }.getOrNull())
            if (!text.isNullOrBlank()) return text
        }
        for (name in listOf("getProfile", "getGameProfile", "profile")) {
            val method = entry.javaClass.methods.firstOrNull { it.name == name && it.parameterCount == 0 } ?: continue
            val profile = runCatching { method.invoke(entry) }.getOrNull() ?: continue
            val nm = profile.javaClass.methods.firstOrNull { it.name == "getName" && it.parameterCount == 0 } ?: continue
            val value = runCatching { nm.invoke(profile) as? String }.getOrNull()
            if (!value.isNullOrBlank()) return value
        }
        return null
    }

    private fun coerceText(value: Any?): String? {
        if (value == null) return null
        if (value is String) return value
        val method = value.javaClass.methods.firstOrNull { it.name == "getString" && it.parameterCount == 0 }
        val raw = method?.let { runCatching { it.invoke(value) }.getOrNull() }
        return if (raw is String) raw else value.toString()
    }

    // â”€â”€ State management â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private fun setState(newState: State) {
        if (state == newState) return
        state = newState
    }

    private fun resetState() {
        stopTunnelMiner()
        PathfindingModule.stopPath()
        RotationExecutor.stopRotating()

        state = State.IDLE
        pauseTicks = 0
        commissions = emptyList()
        currentCommission = null
        activeOreTypes = emptyList()
        awaitingTabUpdate = false
        ignoreTabUpdatesUntil = 0L
        lastCommissionSyncSource = null
        lastCompletedCommissionName = null
        lastTunnelRestartAt = 0L
        noSupportedMessageAt = 0L
        claimPathActive = false
        ownsMiningMacro = false
        pickobulusPreparedCommissionName = null
        pickobulusOpenTick = 0L
        pickobulusClickAttempts = 0
        coldPaused = false
        lastColdWarningAt = 0L
        lastColdWarpAt = 0L
        pendingUseRelease = false
        lastAreaWarpAt = 0L
        resetClaimState()
    }

    private fun resetClaimState() {
        pigeonAttempts = 0
        firstPigeonAttemptAt = 0L
        claimAttempts = 0
        npcRotationPending = false
    }

    private fun resetCommissionAfterClaim() {
        currentCommission = null
        activeOreTypes = emptyList()
        awaitingTabUpdate = false
        claimPathActive = false
        ignoreTabUpdatesUntil = 0L
        pickobulusPreparedCommissionName = null
        pickobulusOpenTick = 0L
        pickobulusClickAttempts = 0
        coldPaused = false
        lastColdWarpAt = 0L
        resetClaimState()
    }

    private fun delayedReset(ticks: Int) {
        resetState()
        pauseTicks = ticks
    }

    private fun delay(ticks: Int) {
        pauseTicks = ticks.coerceAtLeast(0)
    }

    private fun sameOres(a: List<TunnelOreType>, b: List<TunnelOreType>): Boolean {
        if (a.size != b.size) return false
        return a.zip(b).all { (x, y) -> x == y }
    }

    private fun hasRoyalPigeon(): Boolean =
        InventoryUtils.findItemInHotbar("Royal Pigeon") >= 0

    private fun isMiningTool(stack: ItemStack): Boolean {
        if (stack.isEmpty) return false
        val name = stack.hoverName.string
        return name.contains("Drill", ignoreCase = true) ||
            name.contains("Gauntlet", ignoreCase = true) ||
            name.contains("Pickaxe", ignoreCase = true)
    }

    private fun isInGlaciteTunnels(): Boolean {
        DwarvenSidebarLocationParser.currentSidebarLocation()?.let { location ->
            if (location.equals("Glacite Tunnels", ignoreCase = true)) return true
        }

        val snapshot = HypixelManager.snapshot()
        if (
            snapshot.area.equals("Glacite Tunnels", ignoreCase = true) ||
            snapshot.map.equals("Glacite Tunnels", ignoreCase = true) ||
            snapshot.placeName.equals("Glacite Tunnels", ignoreCase = true)
        ) {
            return true
        }

        return mc.player?.blockY?.let { it > 187 } == true
    }

    private fun updateCommissionsFromGui(screen: AbstractContainerScreen<*>) {
        val newCommissions = mutableListOf<Commission>()
        val slots = screen.menu.slots.filterNot { it.container is Inventory }

        for (slot in slots) {
            if (!slot.hasItem()) continue
            val allLines = buildList {
                val name = ChatFormatting.stripFormatting(slot.item.hoverName.string)?.trim().orEmpty()
                if (name.isNotBlank()) add(name.lowercase(Locale.US))
                addAll(slot.item.getLoreLines()
                    .mapNotNull { ChatFormatting.stripFormatting(it.string)?.lowercase(Locale.US)?.trim() }
                    .filter { it.isNotBlank() })
            }
            val combined = allLines.joinToString("\n")
            if (combined.contains("powder")) continue
            val oreName = SUPPORTED_ORES.firstOrNull { combined.contains(it) } ?: continue
            val nameLine = allLines.firstOrNull { line ->
                line.contains(oreName) && !line.contains("powder")
            } ?: continue
            val name = nameLine
                .replace(Regex("\\s*[0-9,]+\\s*/\\s*[0-9,]+\\s*$"), "")
                .replace(Regex("\\s*[0-9]{1,3}(?:\\.[0-9]+)?\\s*%\\s*$"), "")
                .trim()
                .split(" ")
                .joinToString(" ") { w -> w.replaceFirstChar { c -> if (c.isLowerCase()) c.uppercaseChar() else c } }
            if (name.isBlank() || !isSupportedCommissionName(name)) continue
            newCommissions += Commission(name, parseProgress(combined))
        }

        if (newCommissions.isEmpty()) return

        commissions = newCommissions
        awaitingTabUpdate = false
        lastCommissionSyncSource = "GUI"
        ignoreTabUpdatesUntil = System.currentTimeMillis() + 5_000L

        val currentName = currentCommission?.name
        val matching = if (currentName != null) newCommissions.firstOrNull { it.name == currentName } else null
        if (matching == null || matching.progress >= 1.0) currentCommission = null
    }

    private fun refreshDrillSlot() {
        val player = mc.player ?: return
        for (slot in 0..8) {
            val stack = player.inventory.getItem(slot)
            if (stack.isEmpty) continue
            val name = stack.hoverName.string
            if (name.contains("Drill", ignoreCase = true) ||
                name.contains("Gauntlet", ignoreCase = true) ||
                name.contains("Pickaxe", ignoreCase = true)
            ) {
                InventoryUtils.holdHotbarSlot(slot)
                break
            }
        }
    }

    private fun rightClick() {
        mc.options.keyUse.setDown(false)
        mc.options.keyUse.setDown(true)
        pendingUseRelease = true
    }

    private fun sendCommand(command: String) {
        (mc.player as? LocalPlayer)?.connection?.sendCommand(command)
    }
}
