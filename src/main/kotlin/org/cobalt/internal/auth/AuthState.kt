package org.cobalt.internal.auth

enum class AuthState { PENDING, VERIFYING, LOADING, READY, FAILED }

object Auth {
    @Volatile var state: AuthState = AuthState.PENDING
    @Volatile var statusMessage: String = "Waiting…"
    @Volatile var failureReason: String = ""
    @Volatile var modulesLoaded: Int = 0
    @Volatile var modulesTotal: Int = 0

    // null = entitlement not yet received (don't block). After auth resolves,
    // set<String> containing module names the server granted. "*" means all.
    @Volatile var entitledModules: Set<String>? = null

    fun isGateLocked(): Boolean = state != AuthState.READY

    fun isModuleEntitled(name: String): Boolean {
        val entitled = entitledModules ?: return true  // auth hasn't resolved yet — don't block
        return "*" in entitled || name in entitled
    }
}
