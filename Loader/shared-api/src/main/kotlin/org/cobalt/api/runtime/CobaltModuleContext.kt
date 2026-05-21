package org.cobalt.api.runtime

data class CobaltModuleContext(
    val userId: String,
    val minecraftUsername: String,
    val moduleId: String,
    val moduleVersion: String,
    val requiredRole: String,
    val registrar: CobaltModuleRegistrar,
    val logger: CobaltRuntimeLogger
)
