package org.cobalt.internal.combat

import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.Setting
import org.cobalt.api.module.setting.inGroup

internal class CombatMacroUiSettings(
  val enabled: Setting<*>,
  val toggleKeybind: Setting<*>,
  val info: Setting<*>,
  val targetName: Setting<*>,
  val cryptZombieSlayer: Setting<*>,
  val slayerToggleKeybind: Setting<*>,
  val slayerStatus: Setting<*>,
  val slayerType: Setting<*>,
  val loadoutSlayerType: Setting<*>,
  val slayerTier: Setting<*>,
  val slayerAutoWarp: Setting<*>,
  val slayerBossNotification: Setting<*>,
  val slayerBossEsp: Setting<*>,
  val slayerEspTargets: Setting<*>,
  val slayerEspStyle: Setting<*>,
  val slayerOwnBossOnly: Setting<*>,
  val slayerHighlightMiniBosses: Setting<*>,
  val slayerBossEspColor: Setting<*>,
  val slayerMiniBossEspColor: Setting<*>,
  val slayerHighTierMiniEspColor: Setting<*>,
  val slayerBlazeAttunementColors: Setting<*>,
  val searchRange: Setting<*>,
  val minCps: Setting<*>,
  val maxCps: Setting<*>,
  val attackRange: Setting<*>,
  val chaseStopBuffer: Setting<*>,
  val stayNearStart: Setting<*>,
  val startAreaRadius: Setting<*>,
  val oneTapMode: Setting<*>,
  val combatMode: Setting<*>,
  val sepDefaultSlayerWeapons: Setting<*>,
  val slayerMeleeWeapon: Setting<*>,
  val slayerBowWeapon: Setting<*>,
  val slayerMageWeapon: Setting<*>,
  val sepZombie: Setting<*>,
  val slayerLocation: Setting<*>,
  val zombieRoutePicker: Setting<*>,
  val zombieDynamicCombat: Setting<*>,
  val zombieSpawnWeapon: Setting<*>,
  val zombieDynamicSword: Setting<*>,
  val sepEnderman: Setting<*>,
  val endermanLocation: Setting<*>,
  val endermanVoidWarp: Setting<*>,
  val endermanRoutePicker: Setting<*>,
  val endermanDynamicCombat: Setting<*>,
  val emanSpawnWeapon: Setting<*>,
  val emanSpawnBowWeapon: Setting<*>,
  val emanSpawnSwordWeapon: Setting<*>,
  val emanDynamicSwapRange: Setting<*>,
  val emanHitPhaseStyle: Setting<*>,
  val emanHitPhaseBowWeapon: Setting<*>,
  val emanHitPhaseSwordWeapon: Setting<*>,
  val autoReaperScythe: Setting<*>,
  val emanHitPhaseSneakWhenHitting: Setting<*>,
  val emanBossWeapon: Setting<*>,
  val sepWolf: Setting<*>,
  val wolfLocation: Setting<*>,
  val wolfRoutePicker: Setting<*>,
  val wolfIgnorePups: Setting<*>,
  val sepVampire: Setting<*>,
  val vampireLocation: Setting<*>,
  val vampireRoutePicker: Setting<*>,
  val sepBlaze: Setting<*>,
  val blazeLocation: Setting<*>,
  val blazeRoutePicker: Setting<*>,
  val sepSpider: Setting<*>,
  val spiderLocation: Setting<*>,
  val spiderPrimordialBelt: Setting<*>,
  val swordKeepDistance: Setting<*>,
  val slayerSwordKeepDistance: Setting<*>,
  val bowMinRange: Setting<*>,
  val bowElevationPerBlock: Setting<*>,
  val mageElevationPerBlock: Setting<*>,
  val autoHeal: Setting<*>,
  val autoWandOfAtonement: Setting<*>,
  val alwaysUseWandOfAtonement: Setting<*>,
  val autoZombieSword: Setting<*>,
  val autoHyperion: Setting<*>,
  val autoOverflux: Setting<*>,
  val autoRagnarok: Setting<*>,
  val autoSwordOfBadHealth: Setting<*>,
  val slayerLoadoutBuilder: Setting<*>,
  val loadoutPowerOrbChoice: Setting<*>,
  val healAtHealth: Setting<*>,
  val stuckTicksSetting: Setting<*>,
  val warpOnStuck: Setting<*>,
  val requireLos: Setting<*>,
  val aimTolerance: Setting<*>,
  val minAttackCooldown: Setting<*>,
  val stuckRepathTries: Setting<*>,
  val autoLearnLastKill: Setting<*>,
  val whitelistOnly: Setting<*>,
  val learnedWhitelistText: Setting<*>,
  val lastKillText: Setting<*>,
) {
  val ordered: List<Setting<*>> = listOf(
    enabled,
    toggleKeybind,
    info,
    targetName,
    cryptZombieSlayer,
    slayerToggleKeybind,
    slayerStatus,
    slayerType,
    loadoutSlayerType,
    slayerTier,
    slayerAutoWarp,
    slayerBossNotification,
    slayerBossEsp,
    slayerEspTargets,
    slayerEspStyle,
    slayerOwnBossOnly,
    slayerHighlightMiniBosses,
    slayerBossEspColor,
    slayerMiniBossEspColor,
    slayerHighTierMiniEspColor,
    slayerBlazeAttunementColors,
    searchRange,
    minCps,
    maxCps,
    attackRange,
    chaseStopBuffer,
    stayNearStart,
    startAreaRadius,
    oneTapMode,
    combatMode,
    sepDefaultSlayerWeapons,
    slayerMeleeWeapon,
    slayerBowWeapon,
    slayerMageWeapon,
    sepZombie,
    slayerLocation,
    zombieRoutePicker,
    zombieDynamicCombat,
    zombieSpawnWeapon,
    zombieDynamicSword,
    sepEnderman,
    endermanLocation,
    endermanVoidWarp,
    endermanRoutePicker,
    endermanDynamicCombat,
    emanSpawnWeapon,
    emanSpawnBowWeapon,
    emanSpawnSwordWeapon,
    emanDynamicSwapRange,
    emanHitPhaseStyle,
    emanHitPhaseBowWeapon,
    emanHitPhaseSwordWeapon,
    autoReaperScythe,
    emanHitPhaseSneakWhenHitting,
    emanBossWeapon,
    sepWolf,
    wolfLocation,
    wolfRoutePicker,
    wolfIgnorePups,
    sepVampire,
    vampireLocation,
    vampireRoutePicker,
    sepBlaze,
    blazeLocation,
    blazeRoutePicker,
    sepSpider,
    spiderLocation,
    spiderPrimordialBelt,
    swordKeepDistance,
    slayerSwordKeepDistance,
    bowMinRange,
    bowElevationPerBlock,
    mageElevationPerBlock,
    autoHeal,
    autoWandOfAtonement,
    alwaysUseWandOfAtonement,
    autoZombieSword,
    autoHyperion,
    autoOverflux,
    autoRagnarok,
    autoSwordOfBadHealth,
    slayerLoadoutBuilder,
    loadoutPowerOrbChoice,
    healAtHealth,
    stuckTicksSetting,
    warpOnStuck,
    requireLos,
    aimTolerance,
    minAttackCooldown,
    stuckRepathTries,
    autoLearnLastKill,
    whitelistOnly,
    learnedWhitelistText,
    lastKillText,
  )
}

internal object CombatMacroUi {
  fun register(module: Module, settings: CombatMacroUiSettings) {
    assignSettingGroups(settings)
    module.addSetting(*settings.ordered.toTypedArray())
  }

  private fun assignSettingGroups(settings: CombatMacroUiSettings) = with(settings) {
    group(TAB_COMBAT_GROUP, enabled, toggleKeybind, info, targetName, searchRange, stayNearStart, startAreaRadius)
    group(TAB_COMBAT_GROUP, requireLos, autoLearnLastKill, whitelistOnly, learnedWhitelistText, lastKillText)

    group(TAB_COMBAT_BEHAVIOR_GROUP, combatMode, minCps, maxCps, attackRange, chaseStopBuffer, aimTolerance)
    group(TAB_COMBAT_BEHAVIOR_GROUP, minAttackCooldown, oneTapMode, swordKeepDistance, bowMinRange)
    group(TAB_COMBAT_BEHAVIOR_GROUP, bowElevationPerBlock, mageElevationPerBlock, stuckTicksSetting, warpOnStuck, stuckRepathTries)

    group(HIDDEN_UI_GROUP, cryptZombieSlayer, slayerToggleKeybind, slayerStatus, slayerType)
    group(TAB_SLAYER_GROUP, slayerTier, slayerAutoWarp, slayerSwordKeepDistance, slayerBossNotification)
    group(TAB_SLAYER_GROUP, slayerBossEsp, slayerEspTargets, slayerEspStyle, slayerOwnBossOnly)
    group(TAB_SLAYER_GROUP, slayerHighlightMiniBosses, slayerBossEspColor, slayerMiniBossEspColor)
    group(TAB_SLAYER_GROUP, slayerHighTierMiniEspColor, slayerBlazeAttunementColors)

    group(TAB_LOADOUT_GROUP, loadoutSlayerType, slayerLoadoutBuilder)

    group(TAB_SLAYER_WEAPONS_GROUP, sepDefaultSlayerWeapons, slayerMeleeWeapon, slayerBowWeapon, slayerMageWeapon)
    group(TAB_SLAYER_WEAPONS_GROUP, sepZombie, slayerLocation, zombieRoutePicker, zombieDynamicCombat)
    group(TAB_SLAYER_WEAPONS_GROUP, zombieSpawnWeapon, zombieDynamicSword)
    group(TAB_SLAYER_WEAPONS_GROUP, sepEnderman, endermanLocation, endermanVoidWarp, endermanRoutePicker)
    group(TAB_SLAYER_WEAPONS_GROUP, endermanDynamicCombat, emanSpawnWeapon, emanSpawnBowWeapon, emanSpawnSwordWeapon)
    group(TAB_SLAYER_WEAPONS_GROUP, emanDynamicSwapRange, emanHitPhaseStyle, emanHitPhaseBowWeapon)
    group(TAB_SLAYER_WEAPONS_GROUP, emanHitPhaseSwordWeapon, autoReaperScythe, emanHitPhaseSneakWhenHitting, emanBossWeapon)
    group(TAB_SLAYER_WEAPONS_GROUP, sepWolf, wolfLocation, wolfRoutePicker, wolfIgnorePups)
    group(TAB_SLAYER_WEAPONS_GROUP, sepVampire, vampireLocation, vampireRoutePicker)
    group(TAB_SLAYER_WEAPONS_GROUP, sepBlaze, blazeLocation, blazeRoutePicker)
    group(TAB_SLAYER_WEAPONS_GROUP, sepSpider, spiderLocation, spiderPrimordialBelt)

    group(TAB_AUTO_ITEMS_GROUP, autoHeal, autoWandOfAtonement, alwaysUseWandOfAtonement, autoZombieSword)
    group(TAB_AUTO_ITEMS_GROUP, autoHyperion, autoOverflux, autoRagnarok, autoSwordOfBadHealth, healAtHealth)

    group(HIDDEN_UI_GROUP, loadoutPowerOrbChoice)
  }

  private fun group(name: String, vararg settings: Setting<*>) {
    settings.forEach { it.inGroup(name) }
  }

  private const val TAB_COMBAT_GROUP = "General"
  private const val TAB_COMBAT_BEHAVIOR_GROUP = "Combat"
  private const val TAB_SLAYER_GROUP = "Slayer"
  private const val TAB_LOADOUT_GROUP = "Loadout"
  private const val TAB_SLAYER_WEAPONS_GROUP = "Slayer Weapons"
  private const val TAB_AUTO_ITEMS_GROUP = "Auto Items"
  private const val HIDDEN_UI_GROUP = "__side__"
}
