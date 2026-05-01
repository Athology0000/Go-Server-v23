package org.cobalt.internal.combat.slayer

import org.cobalt.api.module.setting.Setting
import org.cobalt.api.module.setting.inGroup
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.internal.routes.RoutePickerSetting
import org.cobalt.internal.routes.RouteType

internal object SpiderSlayerSettings {
  private const val LOCATION_GROUP = "Location"
  private const val WEAPONS_GROUP = "Weapons"
  private const val HATCHLINGS_GROUP = "Hatchlings Phase"
  private const val HIDDEN_GROUP = "__side__"

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

  val pageSettings: Array<Setting<*>> = arrayOf(
    routePicker,
    weaponsHeader,
    meleeWeapon,
    bowWeapon,
    autoDetectSword,
    autoDetectBow,
    hatchlingsHeader,
    hatchlingsPhase,
    hyperionBeforeShooting,
  )
}
