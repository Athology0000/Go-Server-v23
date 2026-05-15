package org.phantom.api.macro

import org.phantom.api.module.Module
import java.util.concurrent.ConcurrentHashMap

/**
 * Global macro runtime state inspired by Phantom's MacroState.js.
 *
 * This intentionally does not replace each module's existing "Enabled" setting.
 * Existing macros can opt in by calling [onModuleEnabled] and [onModuleDisabled]
 * from their own start/stop paths, or by overriding Module.isMacro once the
 * project moves to lifecycle-aware module toggles.
 */
object MacroState {
  enum class ToggleContext {
    USER,
    KEYBIND,
    SCHEDULER,
    FAILSAFE,
    WORLD_UNLOAD,
    INTERNAL,
  }

  data class DisableMeta(
    val context: ToggleContext,
    val reason: String,
    val timestampMs: Long = System.currentTimeMillis(),
  )

  private val modules = ConcurrentHashMap<String, Module>()
  private val enabledMacros = linkedSetOf<String>()
  private val lastDisableMeta = ConcurrentHashMap<String, DisableMeta>()

  @Volatile
  var running: Boolean = false
    private set

  @Volatile
  var activeMacro: String? = null
    private set

  @Volatile
  var lastActiveMacro: String? = null
    private set

  @Synchronized
  fun registerModule(module: Module) {
    modules[module.name.normalizedKey()] = module
  }

  @Synchronized
  fun onModuleEnabled(
    moduleName: String,
    context: ToggleContext = ToggleContext.USER,
  ) {
    val module = modules[moduleName.normalizedKey()]
    if (module != null && !module.isMacro) return

    enabledMacros.add(moduleName)
    running = enabledMacros.isNotEmpty()
    activeMacro = moduleName
    lastActiveMacro = moduleName
    lastDisableMeta.remove(moduleName)
  }

  @Synchronized
  fun onModuleDisabled(
    moduleName: String,
    context: ToggleContext = ToggleContext.USER,
    reason: String = context.name.lowercase(),
  ) {
    enabledMacros.remove(moduleName)
    lastDisableMeta[moduleName] = DisableMeta(context, reason)
    running = enabledMacros.isNotEmpty()
    activeMacro = enabledMacros.lastOrNull()
  }

  @Synchronized
  fun stopAll(
    context: ToggleContext = ToggleContext.INTERNAL,
    reason: String = context.name.lowercase(),
  ) {
    enabledMacros.toList().forEach { onModuleDisabled(it, context, reason) }
  }

  @Synchronized
  fun enabledMacroNames(): List<String> = enabledMacros.toList()

  fun lastDisableFor(moduleName: String): DisableMeta? {
    return lastDisableMeta[moduleName]
  }

  fun isFailsafeMacroRunning(): Boolean {
    return enabledMacros.any { name ->
      val module = modules[name.normalizedKey()]
      module == null || (module.isMacro && !module.ignoreFailsafes)
    }
  }

  private fun String.normalizedKey(): String = trim().lowercase()
}
