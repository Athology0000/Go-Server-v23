package org.cobalt.internal.auth

enum class AuthState {
  PENDING,
  VERIFYING,
  LOADING,
  READY,
  FAILED
}

object Auth {
  @Volatile var state: AuthState = AuthState.READY
  @Volatile var statusMessage: String = "Ready (auth disabled)"
  @Volatile var failureReason: String = ""

  @Volatile var modulesLoaded: Int = 0
  @Volatile var modulesTotal: Int = 0

  @Volatile var alias: String = ""
  @Volatile var accountId: String = ""

  @Volatile var minecraftBound: Boolean = false
  @Volatile var minecraftUsername: String = ""
  @Volatile var minecraftUuid: String = ""

  // null = entitlement not yet received.
  // "*" means all modules.
  @Volatile var entitledModules: Set<String>? = null

  fun isGateLocked(): Boolean {
    return state != AuthState.READY
  }

  fun isModuleEntitled(idOrName: String): Boolean {
    val entitled = entitledModules ?: return true
    return "*" in entitled || idOrName in entitled
  }

  fun reset() {
    state = AuthState.READY
    statusMessage = "Ready (auth disabled)"
    failureReason = ""

    modulesLoaded = 0
    modulesTotal = 0

    alias = ""
    accountId = ""

    minecraftBound = false
    minecraftUsername = ""
    minecraftUuid = ""

    entitledModules = null
  }
}
