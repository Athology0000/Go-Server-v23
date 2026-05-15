package org.phantom.internal.combat.slayer

import org.phantom.api.module.setting.Setting
import org.phantom.api.module.setting.inGroup
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.ModeSetting
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.module.setting.impl.TextSetting
import org.phantom.internal.routes.RoutePickerSetting
import org.phantom.internal.routes.RouteType

internal object ZombieSlayerSettings {
  private const val LOCATION_GROUP = "Location"
  private const val WEAPONS_GROUP = "Weapons"

  val location = ModeSetting(
    "Zombie Location",
    "Farming location for Zombie Slayer.",
    0,
    arrayOf("Zombie Graveyard", "Zombie Crypt")
  ).inGroup(LOCATION_GROUP)

  val routePicker = RoutePickerSetting(
    "Zombie Patrol Route",
    "PATROL route for the zombie farming area.",
    RouteType.PATROL,
    "patrol:zombie"
  ).inGroup(LOCATION_GROUP)

  val dynamicCombat = CheckboxSetting(
    "Zombie Auto Swap",
    "Use the spawn weapon while farming, then swap to Dynamic Sword for the boss.",
    true
  ).inGroup(WEAPONS_GROUP)

  val spawnWeapon = TextSetting(
    "Zombie Spawn Weapon",
    "Comma-separated hotbar keywords in priority order for the zombie spawn weapon. Used before the boss spawns.",
    "halberd"
  ).inGroup(WEAPONS_GROUP)

  val dynamicSword = TextSetting(
    "Dynamic Sword",
    "Comma-separated hotbar keywords in priority order for the zombie boss and melee weapon.",
    "falchion, reaper, shredded, sword"
  ).inGroup(WEAPONS_GROUP)

  val pageSettings: Array<Setting<*>> = arrayOf(
    location,
    routePicker,
    dynamicCombat,
    spawnWeapon,
    dynamicSword,
  )
}

internal object WolfSlayerSettings {
  private const val LOCATION_GROUP = "Location"
  private const val BEHAVIOR_GROUP = "Behavior"

  val location = ModeSetting(
    "Wolf Location",
    "Farming location for Wolf Slayer.",
    0,
    arrayOf("Park", "Caves", "Castle")
  ).inGroup(LOCATION_GROUP)

  val routePicker = RoutePickerSetting(
    "Wolf Patrol Route",
    "PATROL route for the wolf farming area.",
    RouteType.PATROL,
    "patrol:wolf"
  ).inGroup(LOCATION_GROUP)

  val ignorePups = CheckboxSetting(
    "Ignore Wolf Pups",
    "During the pups phase, keep tracking the boss instead of retargeting the pups.",
    false
  ).inGroup(BEHAVIOR_GROUP)

  val pageSettings: Array<Setting<*>> = arrayOf(location, routePicker, ignorePups)
}

internal object EndermanSlayerSettings {
  private const val LOCATION_GROUP = "Location"
  private const val WEAPONS_GROUP = "Weapons"
  private const val HIT_PHASE_GROUP = "Hit Phase"
  private const val LASERS_GROUP = "Lasers / Beacons"

  val location = ModeSetting(
    "Enderman Location",
    "Farming location for Enderman Slayer.",
    0,
    arrayOf("The End", "Hideout", "Void Sepulchre")
  ).inGroup(LOCATION_GROUP)

  val voidWarp = CheckboxSetting(
    "Warp To Void",
    "Run /warp void when starting Enderman Slayer at Void Sepulchre.",
    true
  ).inGroup(LOCATION_GROUP)

  val routePicker = RoutePickerSetting(
    "Enderman Patrol Route",
    "PATROL route for the enderman farming area.",
    RouteType.PATROL,
    "patrol:enderman"
  ).inGroup(LOCATION_GROUP)

  val dynamicCombat = CheckboxSetting(
    "Eman Auto Swap",
    "Use bow for spawn farming and hit-shield phases, then swap to melee for boss DPS phases.",
    true
  ).inGroup(WEAPONS_GROUP)

  val spawnWeapon = ModeSetting(
    "Eman Spawn Weapon",
    "Choose the weapon style for Enderman Slayer spawn farming before the boss appears.",
    3,
    arrayOf("Terminator", "Shortbow", "Enderman Slayer Sword", "Dynamic")
  ).inGroup(WEAPONS_GROUP)

  val spawnBowWeapon = TextSetting(
    "Eman Spawn Bow Weapon",
    "Comma-separated hotbar keywords in priority order for Enderman Slayer bow spawn phases.",
    "terminator, juju, shortbow, bow"
  ).inGroup(WEAPONS_GROUP)

  val spawnSwordWeapon = TextSetting(
    "Eman Spawn Sword Weapon",
    "Comma-separated hotbar keywords in priority order for Enderman Slayer melee spawn phases.",
    "atomsplit, vorpal, voidedge, katana"
  ).inGroup(WEAPONS_GROUP)

  val dynamicSwapRange = SliderSetting(
    "Eman Dynamic Swap Range",
    "In Dynamic spawn mode, switch from bow to sword when the target is within this distance.",
    4.5,
    2.0,
    8.0,
    step = 0.1
  ).inGroup(WEAPONS_GROUP)

  val hitPhaseStyle = ModeSetting(
    "Eman Hit Phase Style",
    "Choose the weapon style for Enderman Slayer hit-shield phases.",
    1,
    arrayOf("Shortbow", "Terminator", "End Slayer Sword")
  ).inGroup(HIT_PHASE_GROUP)

  val hitPhaseBowWeapon = TextSetting(
    "Eman Hit Phase Bow Weapon",
    "Comma-separated hotbar keywords in priority order for Enderman Slayer hit-shield bow phases.",
    "terminator"
  ).inGroup(HIT_PHASE_GROUP)

  val hitPhaseSwordWeapon = TextSetting(
    "Eman Hit Phase Sword Weapon",
    "Comma-separated hotbar keywords in priority order for Enderman Slayer hit-shield melee phases.",
    "atomsplit, vorpal, voidedge, katana"
  ).inGroup(HIT_PHASE_GROUP)

  val hitPhaseSneakWhenHitting = CheckboxSetting(
    "Sneak When Hitting",
    "Hold sneak while attacking Enderman Slayer hit-shield phases.",
    false
  ).inGroup(HIT_PHASE_GROUP)

  val laserPhase = CheckboxSetting(
    "Detect Lasers Phase",
    "Detect Voidgloom laser text and use laser-phase movement.",
    true
  ).inGroup(LASERS_GROUP)

  val beaconPhase = CheckboxSetting(
    "Clear Beacons",
    "During laser phase, scan for nearby beacons and AOTV onto them.",
    true
  ).inGroup(LASERS_GROUP)

  val bossWeapon = TextSetting(
    "Eman Boss Weapon",
    "Comma-separated hotbar keywords in priority order for Enderman Slayer boss DPS phases.",
    "atomsplit, vorpal, voidedge, katana"
  ).inGroup(WEAPONS_GROUP)

  val pageSettings: Array<Setting<*>> = arrayOf(
    location,
    voidWarp,
    routePicker,
    dynamicCombat,
    spawnWeapon,
    spawnBowWeapon,
    spawnSwordWeapon,
    dynamicSwapRange,
    hitPhaseStyle,
    hitPhaseBowWeapon,
    hitPhaseSwordWeapon,
    hitPhaseSneakWhenHitting,
    laserPhase,
    beaconPhase,
    bossWeapon,
  )
}

internal object VampireSlayerSettings {
  private const val LOCATION_GROUP = "Location"

  val location = ModeSetting(
    "Vampire Location",
    "Farming location for Vampire Slayer.",
    0,
    arrayOf("Wyld Woods")
  ).inGroup(LOCATION_GROUP)

  val routePicker = RoutePickerSetting(
    "Vampire Patrol Route",
    "PATROL route for the vampire farming area.",
    RouteType.PATROL,
    "patrol:vampire"
  ).inGroup(LOCATION_GROUP)

  val pageSettings: Array<Setting<*>> = arrayOf(location, routePicker)
}

internal object BlazeSlayerSettings {
  private const val LOCATION_GROUP = "Location"

  val location = ModeSetting(
    "Blaze Location",
    "Farming location for Blaze Slayer.",
    0,
    arrayOf("Blazing Fortress")
  ).inGroup(LOCATION_GROUP)

  val routePicker = RoutePickerSetting(
    "Blaze Patrol Route",
    "PATROL route for the blaze farming area.",
    RouteType.PATROL,
    "patrol:blaze"
  ).inGroup(LOCATION_GROUP)

  val pageSettings: Array<Setting<*>> = arrayOf(location, routePicker)
}
