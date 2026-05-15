package org.phantom.internal.combat.slayer

import org.phantom.api.module.setting.Setting
import org.phantom.api.module.setting.inGroup
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.InfoSetting
import org.phantom.api.module.setting.impl.InfoType
import org.phantom.api.module.setting.impl.ModeSetting
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.module.setting.impl.TextSetting
import org.phantom.internal.routes.RoutePickerSetting
import org.phantom.internal.routes.RouteType

internal object SpiderSlayerSettings {
  private const val LOCATION_GROUP = "Location"
  private const val WEAPONS_GROUP = "Weapons"
  private const val HATCHLINGS_GROUP = "Hatchlings Phase"
  private const val MOBILITY_GROUP = "Mobility"
  private const val HIDDEN_GROUP = "__side__"

  val location = ModeSetting(
    "Spider Location",
    "Farming location for Spider Slayer.",
    0,
    arrayOf("Upper Spiders", "Arachne Spider", "Crimson Spider")
  ).inGroup(LOCATION_GROUP)

  val routePicker = RoutePickerSetting(
    "Spider Patrol Route",
    "PATROL route for the spider farming area.",
    RouteType.PATROL,
    "patrol:spider"
  ).inGroup(LOCATION_GROUP)

  val weaponsHeader = InfoSetting("Weapons", "", InfoType.SEPARATOR).inGroup(WEAPONS_GROUP)

  val meleeWeapon = TextSetting(
    "Melee Weapon",
    "Comma-separated hotbar keywords in priority order for Spider Slayer melee phases.",
    "sting, foil, sword, blade, scythe, katana, saber, cleaver, axe, rapier, claymore, halberd, dagger, falchion, hyperion, astraea, scylla, valkyrie, necron blade"
  ).inGroup(WEAPONS_GROUP)

  val bowWeapon = TextSetting(
    "Bow Weapon",
    "Comma-separated hotbar keywords in priority order for Spider Slayer bow phases.",
    "terminator, juju, shortbow, bow"
  ).inGroup(WEAPONS_GROUP)

  val autoDetectSword = CheckboxSetting(
    "Auto Detect Sword",
    "Ignore the configured sword keywords and use the left-most sword in the hotbar.",
    false
  ).inGroup(HIDDEN_GROUP)

  val autoDetectBow = CheckboxSetting(
    "Auto Detect Bow",
    "Ignore the configured bow keywords and use the left-most bow in the hotbar.",
    false
  ).inGroup(HIDDEN_GROUP)

  val hatchlingsHeader = InfoSetting("Hatchlings Phase", "", InfoType.SEPARATOR).inGroup(HATCHLINGS_GROUP)

  val hatchlingsPhase = CheckboxSetting(
    "Hatchlings Phase",
    "When the boss becomes invulnerable, step back and use bow mode on nearby hatchlings.",
    true
  ).inGroup(HATCHLINGS_GROUP)

  val hyperionBeforeShooting = CheckboxSetting(
    "Hyperion Before Shooting Hatchlings",
    "Right-click Hyperion or another Wither blade at the ground before shooting hatchlings.",
    false
  ).inGroup(HATCHLINGS_GROUP)

  val mobilityHeader = InfoSetting("Mobility", "", InfoType.SEPARATOR).inGroup(MOBILITY_GROUP)

  val etherwarpEnabled = CheckboxSetting(
    "Etherwarp to Spiders",
    "Etherwarp to spiders that are too far to walk to quickly. Requires an AOTV or etherwarp item in hotbar.",
    false
  ).inGroup(MOBILITY_GROUP)

  val etherwarpMinDist = SliderSetting(
    "Etherwarp Min Distance",
    "Minimum distance in blocks before etherwarp is used instead of walking.",
    12.0, 5.0, 30.0
  ).inGroup(MOBILITY_GROUP)

  val instantTransmission = CheckboxSetting(
    "Instant Transmission",
    "Attack spiders from up to 15 blocks when holding an Instant Transmission weapon to trigger the teleport.",
    false
  ).inGroup(MOBILITY_GROUP)

  val pageSettings: Array<Setting<*>> = arrayOf(
    location,
    routePicker,
    weaponsHeader,
    meleeWeapon,
    bowWeapon,
    autoDetectSword,
    autoDetectBow,
    hatchlingsHeader,
    hatchlingsPhase,
    hyperionBeforeShooting,
    mobilityHeader,
    etherwarpEnabled,
    etherwarpMinDist,
    instantTransmission,
  )
}
