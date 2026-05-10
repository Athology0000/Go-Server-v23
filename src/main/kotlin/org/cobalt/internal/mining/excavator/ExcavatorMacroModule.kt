package org.cobalt.internal.mining.excavator

import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.internal.mining.failsafe.MiningFailsafeManager
import org.cobalt.internal.mining.supervisor.MiningMacroSupervisor

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
