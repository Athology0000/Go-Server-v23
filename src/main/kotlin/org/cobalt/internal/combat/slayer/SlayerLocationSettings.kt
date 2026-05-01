package org.cobalt.internal.combat.slayer

import org.cobalt.api.module.setting.inGroup
import org.cobalt.api.module.setting.impl.CheckboxSetting

internal object SlayerLocationSettings {
  private const val LOCATION_GROUP = "Location"

  private val warpToLocationSettings = arrayOf(
    warpSetting("Zombie"),
    warpSetting("Wolf"),
    warpSetting("Spider"),
    warpSetting("Enderman"),
    warpSetting("Vampire"),
    warpSetting("Blaze"),
  )

  private val autoSlayerSettings = arrayOf(
    autoSlayerSetting("Zombie"),
    autoSlayerSetting("Wolf"),
    autoSlayerSetting("Spider"),
    autoSlayerSetting("Enderman"),
    autoSlayerSetting("Vampire"),
    autoSlayerSetting("Blaze"),
  )

  private val primordialBeltSettings = arrayOf(
    primordialBeltSetting("Zombie"),
    primordialBeltSetting("Wolf"),
    primordialBeltSetting("Spider"),
    primordialBeltSetting("Enderman"),
    primordialBeltSetting("Vampire"),
    primordialBeltSetting("Blaze"),
  )

  fun warpToLocationSetting(typeIndex: Int): CheckboxSetting =
    warpToLocationSettings.getOrElse(typeIndex) { warpToLocationSettings[0] }

  fun autoSlayerSetting(typeIndex: Int): CheckboxSetting =
    autoSlayerSettings.getOrElse(typeIndex) { autoSlayerSettings[0] }

  fun primordialBeltSetting(typeIndex: Int): CheckboxSetting =
    primordialBeltSettings.getOrElse(typeIndex) { primordialBeltSettings[0] }

  fun shouldWarpToLocation(typeIndex: Int): Boolean =
    warpToLocationSettings.getOrNull(typeIndex)?.value ?: false

  fun shouldUseAutoSlayer(typeIndex: Int): Boolean =
    autoSlayerSettings.getOrNull(typeIndex)?.value ?: false

  fun shouldUsePrimordialBelt(typeIndex: Int): Boolean =
    primordialBeltSettings.getOrNull(typeIndex)?.value ?: false

  private fun warpSetting(typeLabel: String): CheckboxSetting =
    CheckboxSetting(
      "Warp To Location",
      "Warp to the selected $typeLabel Slayer farming location when the macro needs to return.",
      true,
    ).inGroup(LOCATION_GROUP)

  private fun autoSlayerSetting(typeLabel: String): CheckboxSetting =
    CheckboxSetting(
      "Auto Slayer",
      "Use Hypixel Auto Slayer for $typeLabel completions instead of calling Maddox after each successful quest.",
      false,
    ).inGroup(LOCATION_GROUP)

  private fun primordialBeltSetting(typeLabel: String): CheckboxSetting =
    CheckboxSetting(
      "Primordial Belt",
      "Use Primordial Belt once when the $typeLabel Slayer boss is active.",
      false,
    ).inGroup(LOCATION_GROUP)
}
