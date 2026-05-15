package org.phantom.internal.mining.excavator

import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.TextSetting
import org.phantom.internal.mining.failsafe.MiningFailsafeManager
import org.phantom.internal.mining.supervisor.MiningMacroSupervisor

object ExcavatorMacroModule : Module("Excavator Macro") {
  override val category = ModuleCategory.MINING

  private val enabled = CheckboxSetting("Enabled", "Dedicated excavator/corpse macro state shell.", false)
  private val targetCorpse = TextSetting("Target Corpse", "Preferred corpse or excavation target label.", "any")

  private var ticksActive = 0
  private var lastStatus = "idle"

  init {
    addSetting(enabled, targetCorpse)
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (!enabled.value) {
      ticksActive = 0
      return
    }

    MiningMacroSupervisor.start(MiningMacroSupervisor.MacroKind.EXCAVATOR, "excavator module enabled")
    val failsafe = MiningFailsafeManager.tick()
    if (failsafe.blocked) {
      lastStatus = "paused: ${failsafe.reason}"
      return
    }

    ticksActive++
    lastStatus = "tracking ${targetCorpse.value.trim().ifEmpty { "any" }} for ${ticksActive}t"
  }

  fun status(): String = lastStatus
}
