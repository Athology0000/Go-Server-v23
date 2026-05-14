package org.cobalt.internal

import org.cobalt.api.hud.modules.InventoryHudModule
import org.cobalt.api.hud.modules.MiningHudModule
import org.cobalt.api.hud.modules.WatermarkModule
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleManager
import org.cobalt.internal.chat.ChatFilterModule
import org.cobalt.internal.chat.RngDropDisplayModule
import org.cobalt.internal.combat.CombatHudModule
import org.cobalt.internal.combat.CombatMacroModule
import org.cobalt.internal.combat.CombatPatrolModule
import org.cobalt.internal.combat.slayer.BlazeSlayerMacroModule
import org.cobalt.internal.combat.slayer.CocoonAlertModule
import org.cobalt.internal.combat.slayer.EndermanSlayerMacroModule
import org.cobalt.internal.combat.slayer.MinibossAlertModule
import org.cobalt.internal.combat.slayer.SpiderSlayerMacroModule
import org.cobalt.internal.combat.slayer.VampireSlayerMacroModule
import org.cobalt.internal.combat.slayer.WolfSlayerMacroModule
import org.cobalt.internal.combat.slayer.ZombieSlayerMacroModule
import org.cobalt.internal.crimson.AutoDojoModule
import org.cobalt.internal.diana.DianaHelperModule
import org.cobalt.internal.diana.DianaMacroModule
import org.cobalt.internal.dungeons.BloodCampHelperModule
import org.cobalt.internal.dungeons.DungeonMapModule
import org.cobalt.internal.dungeons.DungeonRoutesModule
import org.cobalt.internal.dungeons.DungeonsModule
import org.cobalt.internal.dungeons.MobEspModule as DungeonMobEspModule
import org.cobalt.internal.etherwarp.EtherwarpHelperModule
import org.cobalt.internal.etherwarp.LeftClickEtherwarpModule
import org.cobalt.internal.etherwarp.SmoothAotvModule
import org.cobalt.internal.farming.FarmingMacroModule
import org.cobalt.internal.fishing.FishingHotspotModule
import org.cobalt.internal.fishing.FishingMacroModule
import org.cobalt.internal.fishing.FishingQolModule
import org.cobalt.internal.garden.GardenAnalyzerModule
import org.cobalt.internal.garden.GardenHudModule
import org.cobalt.internal.garden.GardenMacroModule
import org.cobalt.internal.garden.PestWarningModule
import org.cobalt.internal.grotto.FairyGrottoModule
import org.cobalt.internal.mining.AutoForgeModule
import org.cobalt.internal.mining.AutoLanternModule
import org.cobalt.internal.mining.BlockMinerModule
import org.cobalt.internal.mining.CommissionHudModule
import org.cobalt.internal.mining.CommissionMacroModule
import org.cobalt.internal.mining.GemstoneMinerModule
import org.cobalt.internal.mining.MiningCoinPopupModule
import org.cobalt.internal.mining.MiningMacroModule
import org.cobalt.internal.mining.MiningModule
import org.cobalt.internal.mining.NoFrillsMiningModule
import org.cobalt.internal.mining.RoutesModule
import org.cobalt.internal.mining.VeinScannerModule
import org.cobalt.internal.mining.VeinDirectionModule
import org.cobalt.internal.mining.excavator.ExcavatorMacroModule
import org.cobalt.internal.mining.failsafe.MiningFailsafeHudModule
import org.cobalt.internal.mining.lobby.LobbyHopperModule
import org.cobalt.internal.mining.ore.OreMacroModule
import org.cobalt.internal.mining.pingless.PinglessMiningModule
import org.cobalt.internal.mining.powder.PowderMacroModule
import org.cobalt.internal.mining.scatha.ScathaMacroModule
import org.cobalt.internal.pathfinding.PathfindingModule
import org.cobalt.internal.pathfinding.debug.PathPreviewRenderModule
import org.cobalt.internal.pig.PigMacroModule
import org.cobalt.internal.qol.AutoStashModule
import org.cobalt.internal.qol.ColoredEnchantsModule
import org.cobalt.internal.qol.CraftHelperModule
import org.cobalt.internal.qol.ItemLockingModule
import org.cobalt.internal.qol.LagDetectorModule
import org.cobalt.internal.qol.MissingEnchantsModule
import org.cobalt.internal.qol.PriceTooltipModule
import org.cobalt.internal.qol.QolModule
import org.cobalt.internal.qol.RsaAutoGfsModule
import org.cobalt.internal.rotation.RotationsModule
import org.cobalt.internal.seal.YearOfTheSealModule
import org.cobalt.internal.spotify.SpotifyModule
import org.cobalt.internal.visual.BlockOutlineModule
import org.cobalt.internal.visual.BlockOverlayModule
import org.cobalt.internal.visual.ArrowHitboxesModule
import org.cobalt.internal.visual.CustomScoreboardModule
import org.cobalt.internal.visual.DeadEntityCleanerModule
import org.cobalt.internal.visual.DeployableHudModule
import org.cobalt.internal.visual.FreecamModule
import org.cobalt.internal.visual.FullBrightModule
import org.cobalt.internal.visual.HotbarOverlayModule
import org.cobalt.internal.visual.MobEspModule as VisualMobEspModule
import org.cobalt.internal.visual.OrbitFreecamModule
import org.cobalt.internal.visual.PetDisplayModule
import org.cobalt.internal.visual.RsaEffectsModule
import org.cobalt.internal.visual.RsaPresetWaypointsModule
import org.cobalt.internal.visual.SkyboxChangerModule
import org.cobalt.internal.visual.WitherImpactOverlayModule
import org.cobalt.internal.wardrobe.WardrobeModule

/**
 * Single source of truth for built-in Cobalt modules.
 *
 * Your uploaded Cobalt build had the module classes, but Cobalt.kt never added
 * them into ModuleManager. That is why /cb could open while the module pages,
 * including Slayer, looked empty or incomplete.
 */
internal object BuiltinModules {
  private var registered = false

  fun register() {
    if (registered) return
    registered = true

    val modules = all()
    applyDefaultHudVisibility(modules)
    ModuleManager.addModules(modules)
  }

  private fun applyDefaultHudVisibility(modules: List<Module>) {
    modules
      .flatMap { it.getHudElements() }
      .forEach { hudElement ->
        hudElement.enabled = hudElement.id == DEFAULT_ENABLED_HUD_ID
      }
  }

  fun all(): List<Module> = listOf(
    // HUD / core UI
    WatermarkModule(),
    InventoryHudModule(),
    MiningHudModule,

    // Pathfinding
    PathfindingModule,
    PathPreviewRenderModule,
    RotationsModule,

    // Slayer modules
    ZombieSlayerMacroModule,
    SpiderSlayerMacroModule,
    WolfSlayerMacroModule,
    EndermanSlayerMacroModule,
    BlazeSlayerMacroModule,
    VampireSlayerMacroModule,
    CocoonAlertModule,
    MinibossAlertModule,

    // Combat
    CombatHudModule,
    CombatMacroModule,
    CombatPatrolModule,

    // Mining base + route systems
    MiningModule,
    MiningMacroModule,
    MiningCoinPopupModule,
    RoutesModule,
    VeinScannerModule,
    BlockMinerModule,
    GemstoneMinerModule,
    CommissionMacroModule,
    CommissionHudModule,
    AutoForgeModule,
    AutoLanternModule,
    FairyGrottoModule,
    VeinDirectionModule,
    NoFrillsMiningModule,
    MiningFailsafeHudModule,
    PinglessMiningModule,
    ExcavatorMacroModule,
    OreMacroModule,
    PowderMacroModule,
    ScathaMacroModule,
    LobbyHopperModule,

    // Garden / farming / fishing / misc macros
    GardenMacroModule,
    GardenAnalyzerModule,
    GardenHudModule,
    PestWarningModule,
    FarmingMacroModule,
    FishingMacroModule,
    FishingHotspotModule,
    FishingQolModule,
    PigMacroModule,
    DianaMacroModule,
    DianaHelperModule,
    YearOfTheSealModule,

    // Dungeons / helper modules
    DungeonsModule,
    DungeonMapModule,
    DungeonRoutesModule,
    BloodCampHelperModule,
    DungeonMobEspModule,
    AutoDojoModule,

    // Etherwarp / movement helpers
    EtherwarpHelperModule,
    LeftClickEtherwarpModule,
    SmoothAotvModule,

    // QoL
    QolModule,
    AutoStashModule,
    CraftHelperModule,
    ItemLockingModule,
    PriceTooltipModule,
    ColoredEnchantsModule,
    MissingEnchantsModule,
    LagDetectorModule,
    RsaAutoGfsModule,
    WardrobeModule,
    SpotifyModule,

    // Visuals
    FullBrightModule,
    FreecamModule,
    OrbitFreecamModule,
    BlockOverlayModule,
    BlockOutlineModule,
    ArrowHitboxesModule,
    CustomScoreboardModule,
    HotbarOverlayModule,
    PetDisplayModule,
    VisualMobEspModule,
    DeadEntityCleanerModule,
    DeployableHudModule,
    RsaEffectsModule,
    RsaPresetWaypointsModule,
    SkyboxChangerModule,
    WitherImpactOverlayModule,

    // Chat / drop display
    ChatFilterModule,
    RngDropDisplayModule,
  ).distinctBy { it.name.trim().lowercase() }

  private const val DEFAULT_ENABLED_HUD_ID = "watermark"
}
