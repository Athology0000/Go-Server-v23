package org.phantom.internal.mining

import java.util.Locale
import kotlin.math.sqrt
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.phys.Vec3
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.ChatEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.InfoSetting
import org.phantom.api.module.setting.impl.InfoType
import org.phantom.api.pathfinder.PathOwner
import org.phantom.api.rotation.EasingType
import org.phantom.api.rotation.RotationExecutor
import org.phantom.api.rotation.strategy.TimedEaseStrategy
import org.phantom.api.util.*
import org.phantom.internal.mining.tunnels.TunnelMinerModule
import org.phantom.internal.mining.tunnels.TunnelOreType
import org.phantom.internal.pathfinding.PathfindingModule

/**
 * Mirrors Phantom's GlaciteCommissionMacro.js exactly.
 *
 * Loop: IDLE â†’ CHOOSING â†’ MINING â†’ CLAIMING â†’ WAITING_GUI_CLOSE â†’ CHOOSING
 *
 * No TRAVELING state â€” TunnelMinerModule handles its own pathfinding to vein edges,
 * exactly like Phantom's TunnelsMiner.
 *
 * Claiming: try Royal Pigeon (3 attempts / 4 s), then path to emissary at [2, 121, 237].
 */
object GlaciteCommissionMacroModule : Module("Glacite Commission Macro") {

    override val category = ModuleCategory.MINING

    private val mc = Minecraft.getInstance()

    private val enabled = CheckboxSetting(
        "Enabled",
        "Completes Glacite tunnel ore commissions using Tunnel Miner.",
        false,
    )

    private val info = InfoSetting(
        "Setup",
        "Requires a drill/pickaxe in hotbar and Glacite commissions visible in /tab.",
        InfoType.INFO,
    )

    private enum class State {
        IDLE, CHOOSING, MINING, CLAIMING, WAITING_GUI_CLOSE
    }

    private data class Commission(val name: String, val progress: Double)

    // Phantom SUPPORTED_ORES order (determines priority when multiple commissions active)
    private val SUPPORTED_ORES = listOf("glacite", "umber", "tungsten", "peridot", "aquamarine", "onyx", "citrine")

    // Phantom EMISSARY_LOCATION = [2, 121, 237]
    private val EMISSARY_POS = BlockPos(2, 121, 237)

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

    // Claiming state (mirrors Phantom pigeonAttempts / firstPigeonAttemptAt)
    private var pigeonAttempts = 0
    private var firstPigeonAttemptAt = 0L
    private var claimAttempts = 0
    private var npcRotationPending = false
    private var claimPathActive = false

    private var pendingUseRelease = false
    private var lastAreaWarpAt = 0L

    init {
        addSetting(enabled, info)
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

        // Phantom reads tab on every step (1-tick frequency)
        updateCommissionsIfChanged(readGlaciteCommissionsFromTab())

        when (state) {
            State.IDLE              -> handleIdle()
            State.CHOOSING          -> handleChoosing()
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
            val stack = player.inventory.getItem(slot)
            !stack.isEmpty && (
                stack.hoverName.string.contains("Drill", ignoreCase = true) ||
                    stack.hoverName.string.contains("Gauntlet", ignoreCase = true) ||
                    stack.hoverName.string.contains("Pickaxe", ignoreCase = true)
                )
        }
        if (!hasTool) {
            ChatUtils.sendMessage("Glacite Commission Macro: no drill, gauntlet, or pickaxe found in hotbar.")
            enabled.value = false
            return
        }
        setState(State.CHOOSING)
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

        startMiningCommissions(supported)
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

        val neededOres = collectOreTypes(supported)
        if (!sameOres(neededOres, activeOreTypes)) {
            startMiningCommissions(supported)
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

        if (claimPathActive) return

        val now = System.currentTimeMillis()
        val pigeonSlot = InventoryUtils.findItemInHotbar("Royal Pigeon")
        val pigeonTimedOut = firstPigeonAttemptAt > 0L && now - firstPigeonAttemptAt > 4_000L

        // Phantom: pigeonAttempts < 3 && !pigeonTimedOut
        if (pigeonSlot >= 0 && pigeonAttempts < 3 && !pigeonTimedOut) {
            val player = mc.player ?: return
            if (player.inventory.selectedSlot != pigeonSlot) {
                InventoryUtils.holdHotbarSlot(pigeonSlot)
                delay(3)
            } else {
                if (firstPigeonAttemptAt == 0L) firstPigeonAttemptAt = now
                pigeonAttempts++
                rightClick()
                delay(10)
            }
            return
        }

        val player = mc.player ?: return
        val dist = distance(player.x, player.y, player.z, EMISSARY_POS)

        if (dist < 4.0) {
            if (!ensureDrillEquippedForClaim()) return
            if (!npcRotationPending) {
                val emissaryTarget = Vec3(
                    EMISSARY_POS.x + 0.5,
                    EMISSARY_POS.y + 2.2,
                    EMISSARY_POS.z + 0.5,
                )
                RotationExecutor.rotateTo(
                    AngleUtils.getRotation(emissaryTarget),
                    TimedEaseStrategy(EasingType.EASE_OUT_SINE, EasingType.EASE_OUT_SINE, 350L),
                )
                npcRotationPending = true
                return
            }

            if (RotationExecutor.isRotating()) return

            npcRotationPending = false
            rightClick()
            delay(10)
            return
        }

        npcRotationPending = false
        claimPathActive = true
        PathfindingModule.ensureEnabledForAutomation("glacite commission")
        PathfindingModule.startTo(
            x = EMISSARY_POS.x + 0.5,
            y = EMISSARY_POS.y.toDouble(),
            z = EMISSARY_POS.z + 0.5,
            owner = PathOwner.COMMISSION,
            source = "GlaciteCommission",
            timeoutTicks = 600,
            arrivalRadius = 3.0,
            onArrive = { claimPathActive = false },
            onFail = {
                ChatUtils.sendMessage("Glacite Commission Macro: failed to reach Glacite emissary.")
                claimPathActive = false
                setState(State.CHOOSING)
                delay(20)
            },
        )
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

    private fun startMiningCommissions(commissions: List<Commission>) {
        val neededOres = collectOreTypes(commissions)
        if (neededOres.isEmpty()) return

        currentCommission = commissions.first()
        activeOreTypes = neededOres
        noSupportedMessageAt = 0L
        resetClaimState()

        if (mc.screen != null) {
            mc.player?.closeContainer()
            setState(State.WAITING_GUI_CLOSE)
            return
        }

        beginTunnelMiner()
    }

    private fun beginTunnelMiner() {
        val ore = activeOreTypes.firstOrNull() ?: return
        TunnelMinerModule.startForAutomation(ore, "glacite commission macro")
        lastTunnelRestartAt = System.currentTimeMillis()
        setState(State.MINING)
    }

    private fun stopTunnelMiner() {
        if (TunnelMinerModule.isActive) TunnelMinerModule.stopForAutomation()
    }

    private fun onCommissionComplete() {
        stopTunnelMiner()
        PathfindingModule.stopPath()
        claimPathActive = false
        awaitingTabUpdate = true
        lastCompletedCommissionName = currentCommission?.name
        resetClaimState()
        setState(State.CLAIMING)
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

    private fun ensureDrillEquippedForClaim(): Boolean {
        val player = mc.player ?: return true
        val toolSlot = (0..8).firstOrNull { slot ->
            val stack = player.inventory.getItem(slot)
            !stack.isEmpty && (
                stack.hoverName.string.contains("Drill", ignoreCase = true) ||
                stack.hoverName.string.contains("Gauntlet", ignoreCase = true) ||
                stack.hoverName.string.contains("Pickaxe", ignoreCase = true)
            )
        } ?: return true
        if (player.inventory.selectedSlot != toolSlot) {
            InventoryUtils.holdHotbarSlot(toolSlot)
            delay(3)
            return false
        }
        return true
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

    private fun distance(x: Double, y: Double, z: Double, pos: BlockPos): Double {
        val dx = x - (pos.x + 0.5)
        val dy = y - pos.y.toDouble()
        val dz = z - (pos.z + 0.5)
        return sqrt(dx * dx + dy * dy + dz * dz)
    }
}
