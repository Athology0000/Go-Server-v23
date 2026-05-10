package org.cobalt.api.pathfinder

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

/**
 * Global old-DUSk-style executor bridge.
 *
 * Path execution should not depend on the GUI module being enabled. Any caller can
 * request a path through PathService, and this bridge keeps NativePathfinder.tick()
 * applying movement every client tick until arrival/failure/cancel.
 */
object PathServiceTickBridge {
  private var registered = false

  fun register() {
    if (registered) return
    registered = true

    ClientTickEvents.END_CLIENT_TICK.register {
      PathService.tick()
    }
  }
}
