package org.cobalt.sample

import org.cobalt.api.runtime.CobaltModuleContext
import org.cobalt.api.runtime.CobaltRuntimeModule

class SampleRuntimeModule : CobaltRuntimeModule {
    override fun onLoad(context: CobaltModuleContext) {
        context.logger.info("Sample runtime module loaded for ${context.minecraftUsername}")
        context.registrar.registerModule("sample", this)
    }

    override fun onUnload() {
        // Clean up event listeners / ticks here.
    }
}
