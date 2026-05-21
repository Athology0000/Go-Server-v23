package org.phantom

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.phantom.api.hud.modules.InventoryHudModule
import org.phantom.api.hud.modules.MiningHudModule
import org.phantom.api.hud.modules.WatermarkModule
import org.phantom.api.module.ModuleManager
import org.phantom.internal.auth.PhantomAuthService
import org.phantom.internal.auth.PhantomSession
import org.phantom.internal.chat.ChatFilterModule
import org.phantom.internal.chat.RngDropDisplayModule
import org.phantom.internal.combat.CombatHudModule
import org.phantom.internal.combat.CombatMacroModule
import org.phantom.internal.combat.CombatPatrolModule
import org.phantom.internal.combat.slayer.*
import org.phantom.internal.diana.DianaHelperModule
import org.phantom.internal.diana.DianaMacroModule
import org.phantom.internal.dungeons.BloodCampHelperModule
import org.phantom.internal.dungeons.DungeonMapModule
import org.phantom.internal.dungeons.DungeonRoutesModule
import org.phantom.internal.dungeons.DungeonsModule
import org.phantom.internal.dungeons.MobEspModule as DungeonMobEspModule
import org.phantom.internal.dungeons.autoroutes.AutoRoutesModule
import org.phantom.internal.dungeons.gambling.DungeonChestGamblingModule
import org.phantom.internal.etherwarp.EtherwarpHelperModule
import org.phantom.internal.etherwarp.LeftClickEtherwarpModule
import org.phantom.internal.etherwarp.SmoothAotvModule
import org.phantom.internal.farming.FarmingMacroModule
import org.phantom.internal.fishing.*
import org.phantom.internal.garden.GardenAnalyzerModule
import org.phantom.internal.garden.GardenHudModule
import org.phantom.internal.garden.GardenMacroModule
import org.phantom.internal.garden.PestWarningModule
import org.phantom.internal.grotto.FairyGrottoModule
import org.phantom.internal.loader.AddonLoader
import org.phantom.internal.mining.*
import org.phantom.internal.mining.excavator.ExcavatorMacroModule
import org.phantom.internal.mining.failsafe.MiningFailsafeHudModule
import org.phantom.internal.mining.lobby.LobbyHopperModule
import org.phantom.internal.mining.ore.OreMacroModule
import org.phantom.internal.mining.pingless.PinglessMiningModule
import org.phantom.internal.mining.powder.PowderMacroModule
import org.phantom.internal.mining.scatha.ScathaMacroModule
import org.phantom.internal.mining.tunnels.TunnelMinerModule
import org.phantom.internal.pathfinding.PathfindingModule
import org.phantom.internal.pathfinding.WorldCacheModule
import org.phantom.internal.pathfinding.debug.PathPreviewRenderModule
import org.phantom.internal.pig.PigMacroModule
import org.phantom.internal.qol.*
import org.phantom.internal.rotation.RotationsModule
import org.phantom.internal.seal.YearOfTheSealModule
import org.phantom.internal.spotify.SpotifyModule
import org.phantom.internal.visual.*
import org.phantom.internal.visual.MobEspModule as VisualMobEspModule
import org.phantom.internal.wardrobe.WardrobeModule

@Suppress("UNUSED")
object Phantom : ClientModInitializer {

  override fun onInitializeClient() {
    PhantomAuthService.start(PhantomSession.readAndDelete())

    if (shouldRegisterEmbeddedModules()) {
      registerModules()
    }

    loadAddons()
    PhantomPublicInit.init()

    println("Phantom Client Initialized")
  }

  private fun shouldRegisterEmbeddedModules(): Boolean {
    if (FabricLoader.getInstance().isDevelopmentEnvironment) return true

    return (System.getProperty("phantom.embeddedModules")
      ?.trim()
      ?.lowercase()
      in setOf("1", "true", "yes", "on")
      )
  }

  private fun registerModules() {
    ModuleManager.addModules(
      listOf(
        WatermarkModule(),
        InventoryHudModule(),

        MiningModule,
        BlockMinerModule,
        GemstoneMinerModule,
        MiningHudModule,
        MiningCoinPopupModule,
        FairyGrottoModule,
        RoutesModule,
        MiningMacroModule,
        TunnelMinerModule,
        WorldVeinCacherModule,
        CommissionMacroModule,
        CommissionHudModule,
        AutoForgeModule,
        VeinDirectionModule,
        AutoLanternModule,
        NoFrillsMiningModule,
        ExcavatorMacroModule,
        OreMacroModule,
        PowderMacroModule,
        ScathaMacroModule,
        PinglessMiningModule,
        MiningFailsafeHudModule,
        LobbyHopperModule,

        CombatMacroModule,
        ZombieSlayerMacroModule,
        WolfSlayerMacroModule,
        SpiderSlayerMacroModule,
        EndermanSlayerMacroModule,
        VampireSlayerMacroModule,
        BlazeSlayerMacroModule,
        CocoonAlertModule,
        MinibossAlertModule,
        CombatPatrolModule,
        CombatHudModule,

        DungeonsModule,
        DungeonMobEspModule,
        DungeonChestGamblingModule,
        BloodCampHelperModule,
        DungeonMapModule,
        DungeonRoutesModule,
        AutoRoutesModule,

        EtherwarpHelperModule,
        LeftClickEtherwarpModule,
        SmoothAotvModule,
        PathfindingModule,
        PathPreviewRenderModule,
        WorldCacheModule,

        FullBrightModule,
        SkyboxChangerModule,
        CustomScoreboardModule,
        DeadEntityCleanerModule,
        FreecamModule,
        DeployableHudModule,
        OrbitFreecamModule,
        BlockOverlayModule,
        BlockOutlineModule,
        ArrowHitboxesModule,
        VisualMobEspModule,
        WitherImpactOverlayModule,

        QolModule,
        ItemLockingModule,
        PriceTooltipModule,
        ColoredEnchantsModule,
        MissingEnchantsModule,
        LagDetectorModule,
        TimerModule,
        CraftHelperModule,
        AutoStashModule,
        RsaAutoGfsModule,
        RotationsModule,
        HotbarOverlayModule,
        PetDisplayModule,
        RsaEffectsModule,
        RsaPresetWaypointsModule,

        GardenAnalyzerModule,
        GardenMacroModule,
        PestWarningModule,
        GardenHudModule,
        FarmingMacroModule,
        FishingMacroModule,
        FishingHotspotModule,
        FishingCastPreviewModule,
        SoulWhipPreviewModule,
        MageBeamModule,
        FishingQolModule,
        YearOfTheSealModule,
        PigMacroModule,
        SpotifyModule,
        ChatFilterModule,
        RngDropDisplayModule,
        DianaMacroModule,
        DianaHelperModule,
        WardrobeModule,
      ).distinctBy { it.name.trim().lowercase() }
    )
  }

  private fun loadAddons() {
    AddonLoader.activateLoadedAddons()
  }
}
