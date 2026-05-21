package org.phantom.api.module

import org.phantom.api.macro.MacroState
import org.phantom.internal.auth.AccessManager
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

  internal fun removeModules(modules: List<Module>) {
    val removedNames = modules.map { it.name.trim().lowercase() }.toSet()
    moduleList.removeAll { it.name.trim().lowercase() in removedNames }
  }

}
