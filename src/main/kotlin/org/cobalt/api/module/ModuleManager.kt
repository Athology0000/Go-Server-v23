package org.cobalt.api.module
import org.cobalt.internal.auth.AccessManager
object ModuleManager {
    private var locked = true
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
    moduleList.addAll(modules)
  }

}
