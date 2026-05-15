package org.phantom.internal.mining.ore

import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.module.setting.impl.TextSetting
import org.phantom.internal.mining.MiningNukerController
import org.phantom.internal.mining.failsafe.MiningFailsafeManager
import org.phantom.internal.mining.supervisor.MiningMacroSupervisor

object OreMacroModule : Module("Ore Macro") {
  override val category = ModuleCategory.MINING

  private val enabled = CheckboxSetting("Enabled", "Dedicated ore macro wrapper around custom block matching.", false)
  private val oreMatchers = TextSetting(
    "Ore Matchers",
    "Comma-separated block ids. Example: minecraft:gold_ore, minecraft:iron_ore, minecraft:diamond_ore",
    "minecraft:gold_ore, minecraft:iron_ore, minecraft:diamond_ore, minecraft:emerald_ore",
  )
  private val range = SliderSetting("Range", "Ore scan range.", 4.0, 1.0, 8.0, step = 1.0)
  private val cooldownMs = SliderSetting("Cooldown MS", "Delay between mining bursts.", 100.0, 10.0, 700.0, step = 5.0)
  private val blocksPerTick = SliderSetting("Blocks/Tick", "Blocks to start per burst.", 1.0, 1.0, 8.0, step = 1.0)

  init {
    addSetting(enabled, oreMatchers, range, cooldownMs, blocksPerTick)
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (!enabled.value) return
    MiningMacroSupervisor.start(MiningMacroSupervisor.MacroKind.ORE, "ore module enabled")
    val failsafe = MiningFailsafeManager.tick()
    if (failsafe.blocked) return

    MiningNukerController.tick(
      MiningNukerController.Config(
        range = range.value.toInt(),
        cooldownMs = cooldownMs.value.toInt(),
        blocksPerTick = blocksPerTick.value.toInt(),
        targetMode = MiningNukerController.TargetMode.CUSTOM,
        toolMode = MiningNukerController.ToolMode.STONE,
        customMatchers = parseMatchers(oreMatchers.value),
        powderChestCollector = false,
      )
    )
  }

  private fun parseMatchers(raw: String): List<MiningNukerController.CustomMatcher> {
    return raw
      .split(',', '\n')
      .map { it.trim() }
      .filter { it.isNotEmpty() }
      .map { MiningNukerController.CustomMatcher(blockId = it) }
  }
}
