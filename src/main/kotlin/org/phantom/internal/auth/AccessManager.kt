package org.phantom.internal.auth

object AccessManager {
  fun canUseModules(): Boolean {
    return Auth.state == AuthState.READY
  }
}
