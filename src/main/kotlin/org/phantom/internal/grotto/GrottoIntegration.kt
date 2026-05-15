package org.phantom.internal.grotto

object GrottoIntegration {

  private var initialized = false

  @JvmStatic
  fun init() {
    if (initialized) return
    initialized = true

    GrottoRouteRenderer.init()
    GrottoCommands.register()

    org.phantom.api.event.EventBus.register(GrottoScanner)
    org.phantom.api.event.EventBus.register(CrystalHollowsDetector)
    org.phantom.api.event.EventBus.register(MansionDetector)
  }

}
