package org.cobalt.internal.mining.scatha

import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.internal.mining.failsafe.MiningFailsafeManager
import org.cobalt.internal.mining.supervisor.MiningMacroSupervisor

object ScathaMacroModule : Module("Scatha Macro") {
  override val category = ModuleCategory.MINING

  private val enabled = CheckboxSetting("Enabled", "Dedicated scatha tunnel state shell.", false)
  private val tunnelLength = SliderSetting("Tunnel Length", "Preferred straight tunnel length before turning.", 48.0, 8.0, 160.0, step = 1.0)
  private val turnEvery = SliderSetting("Turn Every", "Ticks between turn decisions.", 120.0, 20.0, 600.0, step = 5.0)

  private var ticksActive = 0
  private var lastDecision = "idle"

  init {
    addSetting(enabled, tunnelLength, turnEvery)
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (!enabled.value) {
      ticksActive = 0
      return
    }

    MiningMacroSupervisor.start(MiningMacroSupervisor.MacroKind.SCATHA, "scatha module enabled")
    val failsafe = MiningFailsafeManager.tick()
    if (failsafe.blocked) {
      lastDecision = "paused: ${failsafe.reason}"
      return
    }

    ticksActive++
    if (ticksActive % turnEvery.value.toInt().coerceAtLeast(1) == 0) {
      lastDecision = "turn decision after ${tunnelLength.value.toInt()} blocks"
    }
  }

  fun status(): String = lastDecision
}
