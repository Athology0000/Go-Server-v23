package org.cobalt.internal.mining.lobby

import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.internal.mining.failsafe.MiningFailsafeManager
import org.cobalt.internal.mining.supervisor.MiningMacroSupervisor

object LobbyHopperModule : Module("Lobby Hopper") {
  override val category = ModuleCategory.MINING

  private val enabled = CheckboxSetting("Enabled", "Tracks when a mining macro should rotate lobbies. Does not send commands by itself.", false)
  private val minStaySeconds = SliderSetting("Min Stay", "Minimum seconds before a hop is allowed.", 300.0, 30.0, 3600.0, step = 10.0)
  private val hopOnFailsafe = CheckboxSetting("Hop On Failsafe", "Request a hop when a critical failsafe is active.", true)

  private var enteredLobbyAtMs = 0L
  private var hopRequested = false
  private var hopReason = "none"

  init {
    addSetting(enabled, minStaySeconds, hopOnFailsafe)
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (!enabled.value) {
      enteredLobbyAtMs = 0L
      hopRequested = false
      return
    }

    MiningMacroSupervisor.start(MiningMacroSupervisor.MacroKind.LOBBY_HOPPER, "lobby hopper enabled")
    if (enteredLobbyAtMs == 0L) enteredLobbyAtMs = System.currentTimeMillis()

    val state = MiningFailsafeManager.snapshot()
    if (hopOnFailsafe.value && state.blocked && state.severity == MiningFailsafeManager.Severity.CRITICAL) {
      requestHop("critical failsafe: ${state.reason}")
    }
  }

  fun canHopNow(): Boolean {
    if (!enabled.value || enteredLobbyAtMs == 0L) return false
    val ageSeconds = (System.currentTimeMillis() - enteredLobbyAtMs) / 1000.0
    return ageSeconds >= minStaySeconds.value
  }

  fun requestHop(reason: String) {
    hopRequested = true
    hopReason = reason
  }

  fun consumeHopRequest(): String? {
    if (!hopRequested || !canHopNow()) return null
    hopRequested = false
    return hopReason
  }
}
