package org.phantom.internal.combat.slayer

internal object WolfSlayerMacroModule : SlayerMacroModule("Wolf Slayer Macro", 1, "Wolf") {
  init {
    addSetting(*WolfSlayerSettings.pageSettings)
  }
}
