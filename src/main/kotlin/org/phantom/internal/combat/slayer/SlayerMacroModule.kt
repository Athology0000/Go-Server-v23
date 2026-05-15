package org.phantom.internal.combat.slayer

import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.inGroup
import org.phantom.api.module.setting.impl.ActionSetting
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.InfoSetting
import org.phantom.api.module.setting.impl.InfoType
import org.phantom.api.module.setting.impl.KeyBindSetting
import org.phantom.api.util.helper.KeyBind
import org.phantom.internal.combat.CombatMacroModule

internal abstract class SlayerMacroModule(
  name: String,
  internal val typeIndex: Int,
  private val typeLabel: String,
) : Module(name) {

  override val category = ModuleCategory.COMBAT
  protected val definition: SlayerDefinition? = SlayerDefinitions.forType(typeIndex)

  private val enabled = CheckboxSetting(
    "Enabled",
    "Start or stop the $typeLabel Slayer macro.",
    false,
  ).inGroup(GENERAL_GROUP)

  private val toggleKeybind = KeyBindSetting(
    "Toggle Keybind",
    "Key to start or stop the $typeLabel Slayer macro.",
    KeyBind(-1),
  ).inGroup(GENERAL_GROUP)

  private val startStopAction = ActionSetting(
    "Start / Stop",
    "Start or stop the $typeLabel Slayer macro.",
    "Start",
    buttonLabelProvider = {
      if (CombatMacroModule.isSlayerMacroActiveFor(typeIndex)) "Stop" else "Start"
    },
    onClick = { requestToggle() },
  ).inGroup(GENERAL_GROUP)

  private val warpToLocation = SlayerLocationSettings.warpToLocationSetting(typeIndex)
  private val autoSlayer = SlayerLocationSettings.autoSlayerSetting(typeIndex)
  private val primordialBelt = SlayerLocationSettings.primordialBeltSetting(typeIndex)
  private val phaseDefinitions = InfoSetting(
    "Phase Definitions",
    definition?.phases
      ?.joinToString(" | ") { phase -> phase.displayName }
      ?: "Spawn Farming | Boss Damage",
    InfoType.INFO,
  ).inGroup(GENERAL_GROUP)

  private var lastEnabled: Boolean? = null

  init {
    addSetting(enabled, toggleKeybind, startStopAction, warpToLocation, autoSlayer, primordialBelt, phaseDefinitions)
    SlayerMacroModuleRegistry.register(this)
  }

  internal fun handleTick() {
    if (toggleKeybind.value.isPressed()) {
      requestToggle()
      return
    }

    val activeInEngine = CombatMacroModule.isSlayerMacroActiveFor(typeIndex)
    val previous = lastEnabled
    val requested = enabled.value

    if (previous == null) {
      if (requested && !activeInEngine) {
        SlayerMacroModuleRegistry.start(this)
      } else {
        setEnabledFromEngine(activeInEngine)
      }
      return
    }

    if (requested != previous) {
      if (requested) {
        SlayerMacroModuleRegistry.start(this)
      } else {
        SlayerMacroModuleRegistry.stop(this)
      }
      return
    }

    if (requested != activeInEngine) {
      setEnabledFromEngine(activeInEngine)
    }
  }

  internal fun setEnabledFromEngine(active: Boolean) {
    enabled.value = active
    lastEnabled = active
  }

  private fun requestToggle() {
    if (CombatMacroModule.isSlayerMacroActiveFor(typeIndex)) {
      SlayerMacroModuleRegistry.stop(this)
    } else {
      SlayerMacroModuleRegistry.start(this)
    }
  }

  private companion object {
    private const val GENERAL_GROUP = "General"
  }
}

private object SlayerMacroModuleRegistry {
  private val modules = mutableListOf<SlayerMacroModule>()

  fun register(module: SlayerMacroModule) {
    modules += module
    EventBus.register(SlayerMacroModuleController)
  }

  fun start(module: SlayerMacroModule) {
    CombatMacroModule.startSlayerMacro(module.typeIndex)
    modules.forEach { it.setEnabledFromEngine(it === module) }
  }

  fun stop(module: SlayerMacroModule) {
    if (CombatMacroModule.isSlayerMacroActiveFor(module.typeIndex)) {
      CombatMacroModule.stopSlayerMacro(module.typeIndex)
    }
    module.setEnabledFromEngine(false)
  }

  fun tickModules() {
    modules.forEach { it.handleTick() }
  }
}

private object SlayerMacroModuleController {
  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    SlayerMacroModuleRegistry.tickModules()
  }
}
