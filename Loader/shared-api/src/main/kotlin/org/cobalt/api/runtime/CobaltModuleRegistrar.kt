package org.cobalt.api.runtime

interface CobaltModuleRegistrar {
    fun registerModule(name: String, instance: Any)
    fun unregisterModule(name: String)
}
