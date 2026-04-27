package org.cobalt.internal.auth

enum class AuthState { PENDING, VERIFYING, LOADING, READY, FAILED }

object Auth {
    @Volatile var state: AuthState = AuthState.PENDING
    @Volatile var statusMessage: String = "Waiting…"
    @Volatile var failureReason: String = ""
    @Volatile var modulesLoaded: Int = 0
    @Volatile var modulesTotal: Int = 0

    fun isGateLocked(): Boolean = state != AuthState.READY
}
