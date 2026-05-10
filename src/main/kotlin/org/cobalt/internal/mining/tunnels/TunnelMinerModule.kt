package org.cobalt.internal.mining.tunnels

import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory

object TunnelMinerModule : Module("Tunnel Miner") {
  override val category = ModuleCategory.MINING

  var isActive: Boolean = false
    private set

  private var automationSource: String? = null
  private var selectedOres: Set<TunnelOreType> = emptySet()

  fun startForAutomation(ore: TunnelOreType, source: String) {
    startForAutomation(setOf(ore), source)
  }

  fun startForAutomation(ores: Set<TunnelOreType>, source: String) {
    selectedOres = ores
    automationSource = source
    isActive = true
  }

  fun stopForAutomation() {
    selectedOres = emptySet()
    automationSource = null
    isActive = false
  }
}
