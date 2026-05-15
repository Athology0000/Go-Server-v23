package org.phantom.api.addon

import org.phantom.api.module.Module

/**
 * Base class for Phantom addons. Implement this to provide modules, HUD elements, and commands.
 *
 * Define your addon entry in `phantom.addon.json` and return your modules from [getModules].
 */
abstract class Addon {

  /** Called when the addon is loaded during game startup. */
  abstract fun onLoad()

  /** Called when the addon is unloaded. */
  abstract fun onUnload()

  /** Returns the list of [Module]s this addon provides. Override to register your modules. */
  open fun getModules(): List<Module> =
    emptyList()

}
