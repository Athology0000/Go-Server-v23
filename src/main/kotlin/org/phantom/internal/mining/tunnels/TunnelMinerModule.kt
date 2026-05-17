package org.phantom.internal.mining.tunnels

import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.internal.mining.MiningArea
import org.phantom.internal.mining.MiningMacroModule

object TunnelMinerModule : Module("Tunnel Miner") {
  override val category = ModuleCategory.MINING

  override fun isVisibleInUi(): Boolean =
    MiningMacroModule.currentMiningArea() == MiningArea.GLACITE

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
