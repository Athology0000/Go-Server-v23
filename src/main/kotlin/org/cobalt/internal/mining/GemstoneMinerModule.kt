package org.cobalt.internal.mining

import java.util.Locale
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.InventoryUtils
import org.cobalt.api.util.MouseUtils
import org.cobalt.internal.routes.RoutePickerSetting
import org.cobalt.internal.routes.RouteType

object GemstoneMinerModule : Module("Gemstone Miner") {

  override val category = ModuleCategory.MINING

  private val mc: Minecraft = Minecraft.getInstance()

  private val enabled = CheckboxSetting(
    "Enabled",
    "Run a GEMSTONE route that warps to each point, mines the nearby gemstone cluster, then moves to the next point.",
    false
  )

  private val info = InfoSetting(
    "Route Format",
    "Build a GEMSTONE route with WARP points for etherwarps and MINE points at each gemstone cluster center.",
    InfoType.INFO
  )

  private val routePicker = RoutePickerSetting(
    "Gemstone Route",
    "GEMSTONE route used by the gemstone miner.",
    RouteType.GEMSTONE,
    "mining:gemstone",
  )

  private val startFromNearest = CheckboxSetting(
    "Start From Nearest",
    "Start from the route point nearest to your current position.",
    true
  )

  private val loopRoute = CheckboxSetting(
    "Loop Route",
    "Restart the route from the first point when the route completes.",
    true
  )

  private val useRodSwap = CheckboxSetting(
    "Use Rod Swap",
    "Cast a rod at configured transitions so AutoPet rod-swap setups can change pets.",
    false
  )

  private val rodKeyword = TextSetting(
    "Rod Keyword",
    "Hotbar item name match used for rod swap.",
    "rod"
  )

  private val rodSwapOnStart = CheckboxSetting(
    "Rod Swap On Start",
    "Cast the rod before the route begins.",
    true
  )

  private val rodSwapOnMineStart = CheckboxSetting(
    "Rod Swap On Mine Start",
    "Cast the rod when the route hands off into a mine point.",
    true
  )

  private val rodSwapOnMineEnd = CheckboxSetting(
    "Rod Swap On Mine End",
    "Cast the rod after a mine point finishes and the route returns to travel.",
    false
  )

  private val rodSwapDelayMs = SliderSetting(
    "Rod Swap Delay",
    "Delay between selecting the rod, casting it, and restoring the previous slot.",
    125.0,
    0.0,
    1000.0,
    step = 5.0,
  )

  private val useMiningAbility = CheckboxSetting(
    "Use Mining Ability",
    "Try to fire the mining tool ability at the start of each mine point and keep Auto Speed Boost enabled while the miner runs.",
    false
  )

  private var wasEnabled = false
  private var ownsActiveRoute = false
  private var lastMinePhase = false
  private var autoSpeedBoostRestoreValue: Boolean? = null
  private var lastAbilityAttemptMs = 0L
  private var lastRodWarnTick = -1L
  private var lastToolWarnTick = -1L

  private var rodSwapStage = RodSwapStage.IDLE
  private var rodSwapDeadlineMs = 0L
  private var rodSwapRestoreSlot = -1
  private var startRouteAfterRodSwap = false
  private var useAbilityAfterRodSwap = false

  init {
    addSetting(
      enabled,
      info,
      routePicker,
      startFromNearest,
      loopRoute,
      useRodSwap,
      rodKeyword,
      rodSwapOnStart,
      rodSwapOnMineStart,
      rodSwapOnMineEnd,
      rodSwapDelayMs,
      useMiningAbility,
    )

    val generalGroup = "General"
    val routeGroup = "Route"
    val petsGroup = "Pets"
    val miningGroup = "Mining"

    enabled.uiGroup = generalGroup
    info.uiGroup = generalGroup

    routePicker.uiGroup = routeGroup
    startFromNearest.uiGroup = routeGroup
    loopRoute.uiGroup = routeGroup

    useRodSwap.uiGroup = petsGroup
    rodKeyword.uiGroup = petsGroup
    rodSwapOnStart.uiGroup = petsGroup
    rodSwapOnMineStart.uiGroup = petsGroup
    rodSwapOnMineEnd.uiGroup = petsGroup
    rodSwapDelayMs.uiGroup = petsGroup

    useMiningAbility.uiGroup = miningGroup

    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    processRodSwap()

    if (!enabled.value) {
      if (wasEnabled) {
        stopGemstoneMiner("Gemstone miner stopped.")
      }
      return
    }

    syncMiningAbilityBridge()

    if (!wasEnabled) {
      wasEnabled = true
      lastMinePhase = false
      val routeName = routePicker.value.trim()
      if (routeName.isEmpty()) {
        ChatUtils.sendMessage("Gemstone miner: no GEMSTONE route selected.")
        stopGemstoneMiner()
        return
      }

      if (useRodSwap.value && rodSwapOnStart.value) {
        if (!scheduleRodSwap(startRouteAfter = true, useAbilityAfter = false)) {
          startSelectedRoute()
        }
      } else {
        startSelectedRoute()
      }
      return
    }

    val waitingForRouteStart = startRouteAfterRodSwap || rodSwapStage != RodSwapStage.IDLE
    if (ownsActiveRoute && !RoutesModule.isRunning && !waitingForRouteStart) {
      ChatUtils.sendMessage("Gemstone miner: route stopped.")
      stopGemstoneMiner()
      return
    }

    val minePhase = ownsActiveRoute && RoutesModule.isRunning && RoutesModule.routeOwnsMining
    if (minePhase && !lastMinePhase) {
      onMinePhaseStart()
    } else if (!minePhase && lastMinePhase) {
      onMinePhaseEnd()
    }
    lastMinePhase = minePhase
  }

  private fun onMinePhaseStart() {
    if (useRodSwap.value && rodSwapOnMineStart.value) {
      if (!scheduleRodSwap(startRouteAfter = false, useAbilityAfter = useMiningAbility.value)) {
        if (useMiningAbility.value) {
          tryUseMiningAbility()
        }
      }
      return
    }

    if (useMiningAbility.value) {
      tryUseMiningAbility()
    }
  }

  private fun onMinePhaseEnd() {
    if (!useRodSwap.value || !rodSwapOnMineEnd.value) return
    scheduleRodSwap(startRouteAfter = false, useAbilityAfter = false)
  }

  private fun startSelectedRoute() {
    val routeName = routePicker.value.trim()
    if (routeName.isEmpty()) {
      ChatUtils.sendMessage("Gemstone miner: no GEMSTONE route selected.")
      stopGemstoneMiner()
      return
    }

    if (RoutesModule.isRunning) {
      ChatUtils.sendMessage("Gemstone miner: another route is already running.")
      stopGemstoneMiner()
      return
    }

    val started =
      RoutesModule.loadAndStartAutomationRoute(
        routeName,
        startNearest = startFromNearest.value,
        loop = loopRoute.value,
        automationSource = "gemstone miner",
      )

    if (!started) {
      ChatUtils.sendMessage("Gemstone miner: failed to start route \"$routeName\".")
      stopGemstoneMiner()
      return
    }

    ownsActiveRoute = true
    lastMinePhase = false
  }

  private fun syncMiningAbilityBridge() {
    if (autoSpeedBoostRestoreValue == null) {
      autoSpeedBoostRestoreValue = MiningModule.autoActivateSpeedBoost.value
    }
    MiningModule.autoActivateSpeedBoost.value = useMiningAbility.value
  }

  private fun restoreMiningAbilityBridge() {
    autoSpeedBoostRestoreValue?.let { MiningModule.autoActivateSpeedBoost.value = it }
    autoSpeedBoostRestoreValue = null
  }

  private fun tryUseMiningAbility() {
    val player = mc.player ?: return
    if (mc.screen != null) return

    val now = System.currentTimeMillis()
    if (now - lastAbilityAttemptMs < ABILITY_ATTEMPT_COOLDOWN_MS) {
      return
    }

    if (!ensureMiningToolSelected(player)) {
      warnMissingMiningTool()
      return
    }

    MouseUtils.rightClick()
    lastAbilityAttemptMs = now
  }

  private fun ensureMiningToolSelected(player: Player): Boolean {
    val currentSlot = player.inventory.selectedSlot
    if (isMiningToolName(player.inventory.getItem(currentSlot).hoverName.string)) {
      return true
    }

    for (slot in 0..8) {
      val name = player.inventory.getItem(slot).hoverName.string
      if (isMiningToolName(name)) {
        InventoryUtils.holdHotbarSlot(slot)
        return true
      }
    }

    return false
  }

  private fun isMiningToolName(name: String): Boolean {
    val lower = name.lowercase(Locale.ROOT)
    return MINING_TOOL_KEYWORDS.any(lower::contains)
  }

  private fun scheduleRodSwap(startRouteAfter: Boolean, useAbilityAfter: Boolean): Boolean {
    val player = mc.player ?: return false
    if (mc.screen != null) return false

    if (rodSwapStage != RodSwapStage.IDLE) {
      startRouteAfterRodSwap = startRouteAfterRodSwap || startRouteAfter
      useAbilityAfterRodSwap = useAbilityAfterRodSwap || useAbilityAfter
      return true
    }

    val keyword = rodKeyword.value.trim().ifEmpty { "rod" }
    val rodSlot = InventoryUtils.findItemInHotbar(keyword)
    if (rodSlot !in 0..8) {
      warnMissingRod()
      return false
    }

    startRouteAfterRodSwap = startRouteAfter
    useAbilityAfterRodSwap = useAbilityAfter
    rodSwapRestoreSlot = player.inventory.selectedSlot
    InventoryUtils.holdHotbarSlot(rodSlot)
    rodSwapStage = RodSwapStage.WAIT_USE
    rodSwapDeadlineMs = System.currentTimeMillis() + rodSwapDelayMs.value.toLong().coerceAtLeast(0L)
    return true
  }

  private fun processRodSwap() {
    if (rodSwapStage == RodSwapStage.IDLE) return
    if (mc.screen != null) return
    val now = System.currentTimeMillis()

    when (rodSwapStage) {
      RodSwapStage.WAIT_USE -> {
        if (now < rodSwapDeadlineMs) return
        MouseUtils.rightClick()
        rodSwapStage = RodSwapStage.WAIT_RESTORE
        rodSwapDeadlineMs = now + rodSwapDelayMs.value.toLong().coerceAtLeast(0L)
      }

      RodSwapStage.WAIT_RESTORE -> {
        if (now < rodSwapDeadlineMs) return
        if (rodSwapRestoreSlot in 0..8) {
          InventoryUtils.holdHotbarSlot(rodSwapRestoreSlot)
        }
        rodSwapRestoreSlot = -1
        rodSwapStage = RodSwapStage.IDLE

        val shouldUseAbility = useAbilityAfterRodSwap
        val shouldStartRoute = startRouteAfterRodSwap
        useAbilityAfterRodSwap = false
        startRouteAfterRodSwap = false

        if (shouldUseAbility) {
          tryUseMiningAbility()
        }
        if (shouldStartRoute) {
          startSelectedRoute()
        }
      }

      RodSwapStage.IDLE -> Unit
    }
  }

  private fun warnMissingRod() {
    val level = mc.level ?: return
    if (lastRodWarnTick >= 0L && level.gameTime - lastRodWarnTick < 40L) return
    lastRodWarnTick = level.gameTime
    ChatUtils.sendMessage("Gemstone miner: no rod matching \"${rodKeyword.value.trim().ifEmpty { "rod" }}\" found in hotbar.")
  }

  private fun warnMissingMiningTool() {
    val level = mc.level ?: return
    if (lastToolWarnTick >= 0L && level.gameTime - lastToolWarnTick < 40L) return
    lastToolWarnTick = level.gameTime
    ChatUtils.sendMessage("Gemstone miner: no drill, gauntlet, or pickaxe found in hotbar for mining ability.")
  }

  private fun stopGemstoneMiner(reason: String = "") {
    cancelRodSwap()
    if (ownsActiveRoute) {
      RoutesModule.stopForAutomation(reason)
    }
    ownsActiveRoute = false
    lastMinePhase = false
    restoreMiningAbilityBridge()
    enabled.value = false
    wasEnabled = false
  }

  private fun cancelRodSwap() {
    rodSwapStage = RodSwapStage.IDLE
    rodSwapDeadlineMs = 0L
    rodSwapRestoreSlot = -1
    startRouteAfterRodSwap = false
    useAbilityAfterRodSwap = false
  }

  internal fun onLevelChange() {
    cancelRodSwap()
    ownsActiveRoute = false
    lastMinePhase = false
    restoreMiningAbilityBridge()
    enabled.value = false
    wasEnabled = false
  }

  private enum class RodSwapStage {
    IDLE,
    WAIT_USE,
    WAIT_RESTORE,
  }

  private const val ABILITY_ATTEMPT_COOLDOWN_MS = 3500L
  private val MINING_TOOL_KEYWORDS = listOf("drill", "gauntlet", "pickaxe")
}
