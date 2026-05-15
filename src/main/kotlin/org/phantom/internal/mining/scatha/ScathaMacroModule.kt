package org.phantom.internal.mining.scatha

import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.internal.mining.failsafe.MiningFailsafeManager
import org.phantom.internal.mining.supervisor.MiningMacroSupervisor

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
