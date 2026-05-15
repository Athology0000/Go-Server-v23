package org.phantom.internal.auth

import org.phantom.api.module.ModuleManager

object DevUnlock {
  fun apply(reason: String = "dev unlock") {
    PhantomAuthDebug.warn("Applying dev unlock: $reason")

    Auth.failureReason = ""
    Auth.alias = Auth.alias.ifBlank { "Iamaperson2004" }
    Auth.accountId = Auth.accountId.ifBlank { "bypass-local" }
    Auth.minecraftBound = true
    Auth.minecraftUsername = Auth.minecraftUsername.ifBlank {
      MinecraftIdentity.currentUsername().orEmpty()
    }
    Auth.minecraftUuid = ""
    Auth.entitledModules = setOf("*")
    Auth.modulesLoaded = 0
    Auth.modulesTotal = 0
    Auth.state = AuthState.READY
    Auth.statusMessage = "Ready ($reason)"

    ModuleManager.setLocked(false)
  }

  fun canUseModule(moduleName: String): Boolean {
    return true
  }

  fun canToggleModules(): Boolean {
    return true
  }
}
