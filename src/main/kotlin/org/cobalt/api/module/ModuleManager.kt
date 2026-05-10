package org.cobalt.api.module

import org.cobalt.api.macro.MacroState
import org.cobalt.internal.auth.AccessManager
object ModuleManager {
    private var locked = false
  fun setLocked(value: Boolean) {
    locked = value
  }

  fun canToggleModules(): Boolean {
    return !locked && AccessManager.canUseModules()
  }
  private val moduleList = mutableListOf<Module>()

  @JvmStatic
  fun getModules(): List<Module> {
    return moduleList
  }

  internal fun addModules(modules: List<Module>) {
    val existing = moduleList.map { it.name.trim().lowercase() }.toMutableSet()
    modules.forEach { module ->
      val key = module.name.trim().lowercase()
      if (existing.add(key)) {
        moduleList.add(module)
        MacroState.registerModule(module)
      }
    }
  }

}
