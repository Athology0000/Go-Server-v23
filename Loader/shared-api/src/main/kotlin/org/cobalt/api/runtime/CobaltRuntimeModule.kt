package org.cobalt.api.runtime

interface CobaltRuntimeModule {
    fun onLoad(context: CobaltModuleContext)
    fun onUnload() {}
}
