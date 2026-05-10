package org.cobalt.internal.auth

object AuthGate {
  // DEV FIX:
  // Never block title screen, multiplayer, server join, or GUI input.
  fun shouldBlockInput(): Boolean {
    return false
  }

  fun shouldShowOverlay(): Boolean {
    return false
  }

  fun canUseMenus(): Boolean {
    return true
  }
}
