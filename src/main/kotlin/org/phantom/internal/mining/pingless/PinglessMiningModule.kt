package org.phantom.internal.mining.pingless

import org.phantom.api.module.Module
import org.phantom.internal.mining.MiningMacroSupervisor

object PinglessMiningModule : Module("Pingless Mining") {
  private const val OWNER = "pingless-mining"

  var running: Boolean = false
    private set

  var status: String = "Idle"
    private set

  fun start() {
    if (!MiningMacroSupervisor.acquire(OWNER)) {
      status = "Blocked by ${MiningMacroSupervisor.activeOwner ?: "another mining macro"}"
      running = false
      return
    }

    running = true
    status = "Running"
  }

  fun stop() {
    running = false
    status = "Stopped"
    MiningMacroSupervisor.release(OWNER)
  }

  fun tick() {
    if (!running) return

    if (!MiningMacroSupervisor.owns(OWNER)) {
      running = false
      status = "Lost supervisor ownership"
      return
    }

    status = "Running"
  }
}
