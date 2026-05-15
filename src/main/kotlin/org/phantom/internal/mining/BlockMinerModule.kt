package org.phantom.internal.mining

import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.ModeSetting
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.module.setting.impl.TextSetting

object BlockMinerModule : Module("Block Miner") {

  override val category = ModuleCategory.MINING

  private val enabled = CheckboxSetting(
    "Enabled",
    "Mine nearby blocks using the recovered block miner.",
    false
  )

  private val targetMode = ModeSetting(
    "Target Mode",
    "Choose how the block miner selects blocks.",
    0,
    arrayOf("Detected Block", "Custom Matchers", "Exposed Only", "Exposed Or Soft")
  )

  private val customMatchers = TextSetting(
    "Custom Matchers",
    "Comma/newline-separated block ids or raw ids. Used when Target Mode is Custom Matchers.",
    ""
  )

  private val toolMode = ModeSetting(
    "Tool Mode",
    "Tool family required before the block miner fires.",
    0,
    arrayOf("Stone", "Soft", "Custom")
  )

  private val range = SliderSetting(
    "Range",
    "Horizontal and vertical range used by the block miner.",
    4.0,
    1.0,
    8.0,
    step = 1.0,
  )

  private val cooldownMs = SliderSetting(
    "Cooldown MS",
    "Delay between mining bursts.",
    100.0,
    10.0,
    500.0,
    step = 5.0,
  )

  private val blocksPerTick = SliderSetting(
    "Blocks/Tick",
    "Maximum nearby blocks to start breaking each burst.",
    1.0,
    1.0,
    8.0,
    step = 1.0,
  )

  private val powderChestCollector = CheckboxSetting(
    "Powder Chest Aura",
    "Right-click nearby powder chests while the block miner is active.",
    false
  )

  private var migratedLegacyConfig = false

  init {
    addSetting(
      enabled,
      targetMode,
      customMatchers,
      toolMode,
      range,
      cooldownMs,
      blocksPerTick,
      powderChestCollector,
    )
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (!migratedLegacyConfig) {
      importLegacyNukerSettingsIfNeeded()
      migratedLegacyConfig = true
    }

    MiningModule.nukerEnabled.value = enabled.value
    MiningModule.powderChestCollector.value = powderChestCollector.value
    MiningModule.nukerRange.value = range.value
    MiningModule.nukerCooldownMs.value = cooldownMs.value
    MiningModule.nukerBlocksPerTick.value = blocksPerTick.value
    MiningModule.nukerToolMode.value = toolMode.value

    when (targetMode.value) {
      TARGET_DETECTED_BLOCK -> {
        MiningModule.nukerTargetMode.value = NUKER_CUSTOM
        MiningModule.nukerCustomMatchers.value = MiningModule.getDetectedBlockId()?.takeIf { it.isNotBlank() }.orEmpty()
      }

      TARGET_CUSTOM_MATCHERS -> {
        MiningModule.nukerTargetMode.value = NUKER_CUSTOM
        MiningModule.nukerCustomMatchers.value = customMatchers.value
      }

      TARGET_EXPOSED_ONLY -> {
        MiningModule.nukerTargetMode.value = NUKER_EXPOSED_ONLY
      }

      TARGET_EXPOSED_OR_SOFT -> {
        MiningModule.nukerTargetMode.value = NUKER_EXPOSED_OR_SOFT
      }
    }
  }

  private fun importLegacyNukerSettingsIfNeeded() {
    val wrapperStillDefault =
      enabled.value == enabled.defaultValue &&
        targetMode.value == targetMode.defaultValue &&
        customMatchers.value == customMatchers.defaultValue &&
        toolMode.value == toolMode.defaultValue &&
        range.value == range.defaultValue &&
        cooldownMs.value == cooldownMs.defaultValue &&
        blocksPerTick.value == blocksPerTick.defaultValue &&
        powderChestCollector.value == powderChestCollector.defaultValue

    if (!wrapperStillDefault) {
      return
    }

    val legacyChanged =
      MiningModule.nukerEnabled.value != MiningModule.nukerEnabled.defaultValue ||
        MiningModule.powderChestCollector.value != MiningModule.powderChestCollector.defaultValue ||
        MiningModule.nukerRange.value != MiningModule.nukerRange.defaultValue ||
        MiningModule.nukerCooldownMs.value != MiningModule.nukerCooldownMs.defaultValue ||
        MiningModule.nukerBlocksPerTick.value != MiningModule.nukerBlocksPerTick.defaultValue ||
        MiningModule.nukerTargetMode.value != MiningModule.nukerTargetMode.defaultValue ||
        MiningModule.nukerToolMode.value != MiningModule.nukerToolMode.defaultValue ||
        MiningModule.nukerCustomMatchers.value != MiningModule.nukerCustomMatchers.defaultValue

    if (!legacyChanged) {
      return
    }

    enabled.value = MiningModule.nukerEnabled.value
    powderChestCollector.value = MiningModule.powderChestCollector.value
    range.value = MiningModule.nukerRange.value
    cooldownMs.value = MiningModule.nukerCooldownMs.value
    blocksPerTick.value = MiningModule.nukerBlocksPerTick.value
    toolMode.value = MiningModule.nukerToolMode.value
    customMatchers.value = MiningModule.nukerCustomMatchers.value
    targetMode.value =
      when (MiningModule.nukerTargetMode.value) {
        NUKER_EXPOSED_OR_SOFT -> TARGET_EXPOSED_OR_SOFT
        NUKER_CUSTOM -> TARGET_CUSTOM_MATCHERS
        else -> TARGET_EXPOSED_ONLY
      }
  }

  private const val TARGET_DETECTED_BLOCK = 0
  private const val TARGET_CUSTOM_MATCHERS = 1
  private const val TARGET_EXPOSED_ONLY = 2
  private const val TARGET_EXPOSED_OR_SOFT = 3

  private const val NUKER_EXPOSED_ONLY = 0
  private const val NUKER_EXPOSED_OR_SOFT = 1
  private const val NUKER_CUSTOM = 2
}
