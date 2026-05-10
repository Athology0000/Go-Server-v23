package org.cobalt.internal.mining.powder

import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.internal.mining.MiningNukerController
import org.cobalt.internal.mining.failsafe.MiningFailsafeManager
import org.cobalt.internal.mining.supervisor.MiningMacroSupervisor

object PowderMacroModule : Module("Powder Macro") {
  override val category = ModuleCategory.MINING

  private val enabled = CheckboxSetting(
    "Enabled",
    "Dedicated powder macro shell. Uses the existing powder chest collector and soft-block nuker path.",
    false,
  )

  private val range = SliderSetting("Range", "Powder scan range.", 4.0, 1.0, 8.0, step = 1.0)
  private val cooldownMs = SliderSetting("Cooldown MS", "Delay between mining bursts.", 120.0, 10.0, 700.0, step = 5.0)
  private val blocksPerTick = SliderSetting("Blocks/Tick", "Blocks to start per burst.", 1.0, 1.0, 8.0, step = 1.0)
  private val collectChests = CheckboxSetting("Chest Collector", "Click powder chests when detected.", true)

  init {
    addSetting(enabled, range, cooldownMs, blocksPerTick, collectChests)
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (!enabled.value) return
    MiningMacroSupervisor.start(MiningMacroSupervisor.MacroKind.POWDER, "powder module enabled")
    val failsafe = MiningFailsafeManager.tick()
    if (failsafe.blocked) return

    MiningNukerController.tick(
      MiningNukerController.Config(
        range = range.value.toInt(),
        cooldownMs = cooldownMs.value.toInt(),
        blocksPerTick = blocksPerTick.value.toInt(),
        targetMode = MiningNukerController.TargetMode.EXPOSED_OR_SOFT,
        toolMode = MiningNukerController.ToolMode.SOFT,
        customMatchers = emptyList(),
        powderChestCollector = collectChests.value,
      )
    )
  }
}
