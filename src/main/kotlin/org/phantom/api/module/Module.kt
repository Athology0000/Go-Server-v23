package org.phantom.api.module

import org.phantom.api.hud.HudElement
import org.phantom.api.module.setting.Setting
import org.phantom.api.module.setting.SettingsContainer
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.module.setting.impl.TextSetting
import org.phantom.api.module.setting.inGroup
import org.phantom.internal.auth.Auth

/**
 * Base class for all modules. Extend this to create addon functionality.
 *
 * Modules can contain settings (via [SettingsContainer]) and HUD elements
 * (via the [hudElement][org.phantom.api.hud.hudElement] DSL). Return your modules
 * from [Addon.getModules][org.phantom.api.addon.Addon.getModules].
 *
 * @property name Display name shown in the UI.
 */

abstract class Module(val name: String) : SettingsContainer {

  open val category: ModuleCategory = ModuleCategory.OTHER

  /**
   * True for automation-style modules that should be tracked by MacroState,
   * scheduler overlays, and global failsafes. Existing modules keep the
   * default false until they opt in.
   */
  open val isMacro: Boolean = false

  /**
   * Lets harmless helper modules keep running while global failsafes are armed.
   */
  open val ignoreFailsafes: Boolean = false

  /**
   * Modules with world-sensitive state can opt into being stopped on unload.
   */
  open val autoDisableOnWorldUnload: Boolean = false

  /**
   * Dynamic UI visibility hook. Modules remain registered and runnable; this
   * only controls whether the config module list shows them.
   */
  open fun isVisibleInUi(): Boolean = true

  val isEntitled: Boolean
    get() = Auth.isModuleEntitled(name)

  private val settingsList = mutableListOf<Setting<*>>()
  private val hudElementsList = mutableListOf<HudElement>()
  private val debugSetting = CheckboxSetting(
    "Debug",
    "Show debug output for this module in chat and latest.log.",
    false
  ).inGroup(DEBUG_UI_GROUP)

  override fun addSetting(vararg settings: Setting<*>) {
    settingsList.addAll(listOf(*settings))
  }

  override fun getSettings(): List<Setting<*>> {
    return settingsList + debugSetting
  }

  /** Registers a HUD element on this module. Called automatically by the [hudElement] DSL. */
  fun addHudElement(element: HudElement) {
    hudElementsList.add(element)
  }

  /** Returns all HUD elements registered on this module. */
  fun getHudElements(): List<HudElement> {
    return hudElementsList
  }

  fun isDebugEnabled(): Boolean {
    return debugSetting.value
  }

  fun debug(message: String): Boolean {
    return ModuleDebug.log(this, message)
  }

  inline fun debug(messageBuilder: () -> String): Boolean {
    if (!isDebugEnabled()) return false
    return debug(messageBuilder())
  }

  fun debugWarn(message: String): Boolean {
    return ModuleDebug.warn(this, message)
  }

  fun debugError(message: String): Boolean {
    return ModuleDebug.error(this, message)
  }


  /** Called by future lifecycle-aware toggles when this module starts. */
  open fun onEnable() {}

  /** Called by future lifecycle-aware toggles when this module stops. */
  open fun onDisable() {}

  /** Optional per-client-tick hook for modules that use lifecycle registration. */
  open fun onTick() {}

  /**
   * Convenience setting helpers inspired by Phantom's direct settings API. They keep
   * module code compact while still registering through the existing settings UI.
   */
  protected fun toggleSetting(
    name: String,
    description: String,
    defaultValue: Boolean = false,
    group: String = Setting.DEFAULT_UI_GROUP,
  ): CheckboxSetting {
    return CheckboxSetting(name, description, defaultValue).inGroup(group).also { addSetting(it) }
  }

  protected fun sliderSetting(
    name: String,
    description: String,
    defaultValue: Double,
    min: Double,
    max: Double,
    step: Double = 0.0,
    group: String = Setting.DEFAULT_UI_GROUP,
  ): SliderSetting {
    return SliderSetting(name, description, defaultValue, min, max, step).inGroup(group).also { addSetting(it) }
  }

  protected fun textSetting(
    name: String,
    description: String,
    defaultValue: String = "",
    group: String = Setting.DEFAULT_UI_GROUP,
  ): TextSetting {
    return TextSetting(name, description, defaultValue).inGroup(group).also { addSetting(it) }
  }

  companion object {
    private const val DEBUG_UI_GROUP = "Debug"
  }

}
