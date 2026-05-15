package org.phantom.internal.mining.failsafe

import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting

object MiningFailsafeHudModule : Module("Mining Failsafes") {
  override val category = ModuleCategory.MINING

  private val enabled = CheckboxSetting(
    "Enabled",
    "Track mining failsafe state for the parity modules.",
    false,
  )

  init {
    addSetting(enabled)
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (!enabled.value) return
    MiningFailsafeManager.tick()
  }
}
