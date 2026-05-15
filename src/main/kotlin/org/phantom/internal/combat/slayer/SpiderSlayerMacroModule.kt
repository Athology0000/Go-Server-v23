package org.phantom.internal.combat.slayer

internal object SpiderSlayerMacroModule : SlayerMacroModule("Spider Slayer Macro", 2, "Spider") {
  init {
    addSetting(*SpiderSlayerSettings.pageSettings)
  }
}
