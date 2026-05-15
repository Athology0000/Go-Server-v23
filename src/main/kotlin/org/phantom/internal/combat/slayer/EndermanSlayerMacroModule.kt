package org.phantom.internal.combat.slayer

internal object EndermanSlayerMacroModule : SlayerMacroModule("Enderman Slayer Macro", 3, "Enderman") {
  init {
    addSetting(*EndermanSlayerSettings.pageSettings)
  }
}
