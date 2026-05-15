package org.phantom.internal.auth

object AccessManager {
  fun canUseModules(): Boolean {
    // Auth is disabled in this local/dev build. Module availability is controlled
    // by the local module registry and settings, not the remote auth state.
    return true
  }
}
