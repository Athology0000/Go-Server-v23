package org.cobalt.loader.runtime

import org.cobalt.api.runtime.CobaltModuleRegistrar
import org.cobalt.loader.util.LoaderLog
import java.util.concurrent.ConcurrentHashMap

object SimpleRuntimeRegistrar : CobaltModuleRegistrar {
    private val modules = ConcurrentHashMap<String, Any>()

    override fun registerModule(name: String, instance: Any) {
        require(name.isNotBlank()) { "Module name cannot be blank" }
        modules[name] = instance
        LoaderLog.info("Registered runtime module: $name")
    }

    override fun unregisterModule(name: String) {
        modules.remove(name)
        LoaderLog.info("Unregistered runtime module: $name")
    }

    fun allRegistered(): Map<String, Any> = modules.toMap()
}
