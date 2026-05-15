package org.phantom.internal.combat.slayer

internal object ZombieSlayerMacroModule : SlayerMacroModule("Zombie Slayer Macro", 0, "Zombie") {
  init {
    addSetting(*ZombieSlayerSettings.pageSettings)
  }
}
