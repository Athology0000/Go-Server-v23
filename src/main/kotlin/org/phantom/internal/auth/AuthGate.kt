package org.phantom.internal.auth

object AuthGate {
  fun shouldBlockInput(): Boolean {
    return Auth.isGateLocked()
  }

  fun shouldShowOverlay(): Boolean {
    return Auth.isGateLocked()
  }

  fun canUseMenus(): Boolean {
    return !Auth.isGateLocked()
  }
}
