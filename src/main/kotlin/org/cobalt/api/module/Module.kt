package org.cobalt.api.module

import org.cobalt.api.hud.HudElement
import org.cobalt.api.module.setting.Setting
import org.cobalt.api.module.setting.SettingsContainer
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.inGroup

/**
 * Base class for all modules. Extend this to create addon functionality.
 *
 * Modules can contain settings (via [SettingsContainer]) and HUD elements
 * (via the [hudElement][org.cobalt.api.hud.hudElement] DSL). Return your modules
 * from [Addon.getModules][org.cobalt.api.addon.Addon.getModules].
 *
 * @property name Display name shown in the UI.
 */
abstract class Module(val name: String) : SettingsContainer {

  open val category: ModuleCategory = ModuleCategory.OTHER

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

  companion object {
    private const val DEBUG_UI_GROUP = "Debug"
  }

}
