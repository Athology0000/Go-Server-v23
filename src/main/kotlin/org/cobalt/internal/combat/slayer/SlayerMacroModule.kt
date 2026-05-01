package org.cobalt.internal.combat.slayer

import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.inGroup
import org.cobalt.api.module.setting.impl.ActionSetting
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.KeyBindSetting
import org.cobalt.api.util.helper.KeyBind
import org.cobalt.internal.combat.CombatMacroModule

internal abstract class SlayerMacroModule(
  name: String,
  internal val typeIndex: Int,
  private val typeLabel: String,
) : Module(name) {

  override val category = ModuleCategory.COMBAT

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

  private var lastEnabled: Boolean? = null

  init {
    addSetting(enabled, toggleKeybind, startStopAction, warpToLocation, autoSlayer, primordialBelt)
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
