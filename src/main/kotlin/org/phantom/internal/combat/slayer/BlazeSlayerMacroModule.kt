package org.phantom.internal.combat.slayer

internal object BlazeSlayerMacroModule : SlayerMacroModule("Blaze Slayer Macro", 5, "Blaze") {
  init {
    addSetting(*BlazeSlayerSettings.pageSettings)
  }
}
