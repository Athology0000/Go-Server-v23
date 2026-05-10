package org.cobalt

import net.fabricmc.api.ClientModInitializer
import net.minecraft.network.protocol.game.ClientboundRespawnPacket
import org.cobalt.api.command.CommandManager
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.PacketEvent
import org.cobalt.api.hud.HudModuleManager
<<<<<<< Updated upstream
import org.cobalt.api.hud.modules.WatermarkModule
import org.cobalt.api.hud.modules.InventoryHudModule
import org.cobalt.api.module.ModuleManager
import org.cobalt.api.notification.NotificationManager
import org.cobalt.api.pathfinder.jni.ChunkSerializer
import org.cobalt.api.pathfinder.jni.NativePathfinder
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.util.TickScheduler
=======
import org.cobalt.api.hud.modules.InventoryHudModule
import org.cobalt.api.hud.modules.MiningHudModule
import org.cobalt.api.hud.modules.WatermarkModule
import org.cobalt.api.module.ModuleManager
import org.cobalt.api.notification.NotificationManager
import org.cobalt.api.pathfinder.jni.ChunkSerializer
import org.cobalt.api.util.WorldScanner
import org.cobalt.api.pathfinder.jni.NativePathfinder
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.util.TickScheduler
import org.cobalt.internal.auth.DevUnlock
import org.cobalt.internal.chat.ChatFilterModule
import org.cobalt.internal.chat.RngDropDisplayModule
>>>>>>> Stashed changes
import org.cobalt.internal.combat.CombatHudModule
import org.cobalt.internal.combat.CombatMacroModule
import org.cobalt.internal.combat.CombatPatrolModule
import org.cobalt.internal.combat.slayer.BlazeSlayerMacroModule
import org.cobalt.internal.combat.slayer.EndermanSlayerMacroModule
import org.cobalt.internal.combat.slayer.SpiderSlayerMacroModule
import org.cobalt.internal.combat.slayer.VampireSlayerMacroModule
import org.cobalt.internal.combat.slayer.WolfSlayerMacroModule
import org.cobalt.internal.combat.slayer.ZombieSlayerMacroModule
import org.cobalt.internal.command.MainCommand
<<<<<<< Updated upstream
=======
import org.cobalt.internal.diana.DianaHelperModule
import org.cobalt.internal.diana.DianaMacroModule
>>>>>>> Stashed changes
import org.cobalt.internal.dungeons.BloodCampHelperModule
import org.cobalt.internal.dungeons.DungeonMapModule
import org.cobalt.internal.dungeons.DungeonRoutesModule
import org.cobalt.internal.dungeons.DungeonsModule
import org.cobalt.internal.dungeons.MobEspModule as DungeonMobEspModule
<<<<<<< Updated upstream
import org.cobalt.internal.etherwarp.EtherwarpHelperModule
import org.cobalt.internal.etherwarp.LeftClickEtherwarpModule
import org.cobalt.internal.etherwarp.SmoothAotvModule
import org.cobalt.internal.helper.Config
import org.cobalt.internal.loader.AddonLoader
import org.cobalt.internal.mining.MiningModule
import org.cobalt.internal.mining.FairyModule
import org.cobalt.internal.mining.RoutesModule
import org.cobalt.internal.mining.MiningMacroModule
import org.cobalt.api.hud.modules.MiningHudModule
import org.cobalt.internal.mining.MiningCoinPopupModule
import org.cobalt.internal.mining.BlockMinerModule
import org.cobalt.internal.mining.GemstoneMinerModule
import org.cobalt.internal.mining.CommissionMacroModule
import org.cobalt.internal.mining.AutoForgeModule
import org.cobalt.internal.mining.VeinDirectionModule
import org.cobalt.internal.mining.AutoLanternModule
import org.cobalt.internal.mining.NoFrillsMiningModule
import org.cobalt.internal.mining.CommissionHudModule
import org.cobalt.internal.pathfinding.PathfindingModule
=======
import org.cobalt.internal.dungeons.gambling.DungeonChestGamblingModule
import org.cobalt.internal.etherwarp.EtherwarpHelperModule
import org.cobalt.internal.etherwarp.LeftClickEtherwarpModule
import org.cobalt.internal.etherwarp.SmoothAotvModule
import org.cobalt.internal.farming.FarmingMacroModule
import org.cobalt.internal.fishing.FishingCastPreviewModule
import org.cobalt.internal.fishing.MageBeamModule
import org.cobalt.internal.fishing.SoulWhipPreviewModule
import org.cobalt.internal.fishing.FishingHotspotModule
import org.cobalt.internal.fishing.FishingMacroModule
import org.cobalt.internal.fishing.FishingQolModule
import org.cobalt.internal.garden.GardenAnalyzerModule
import org.cobalt.internal.garden.GardenHudModule
import org.cobalt.internal.garden.GardenMacroModule
import org.cobalt.internal.garden.PestWarningModule
import org.cobalt.internal.helper.Config
import org.cobalt.internal.loader.AddonLoader
import org.cobalt.internal.mining.AutoForgeModule
import org.cobalt.internal.mining.AutoLanternModule
import org.cobalt.internal.mining.BlockMinerModule
import org.cobalt.internal.mining.CommissionDebugHudModul
import org.cobalt.internal.mining.CommissionHudModule
import org.cobalt.internal.mining.CommissionMacroModule
import org.cobalt.internal.mining.CommissionRouteAssignmentModule
import org.cobalt.internal.mining.FairyModule
import org.cobalt.internal.mining.GemstoneMinerModule
import org.cobalt.internal.mining.MiningCoinPopupModule
import org.cobalt.internal.mining.MiningMacroModule
import org.cobalt.internal.mining.MiningModule
import org.cobalt.internal.mining.NoFrillsMiningModule
import org.cobalt.internal.mining.RoutesModule
import org.cobalt.internal.mining.StrictMiningAttackBlockGuardModule
import org.cobalt.internal.mining.StrictMiningCommand
import org.cobalt.internal.mining.VeinDirectionModule
import org.cobalt.internal.mining.bot.MiningBotDebugHudModule
import org.cobalt.internal.mining.bot.MiningBotModule
import org.cobalt.internal.mining.bot.MiningBotTargetRenderModule
import org.cobalt.internal.mining.excavator.ExcavatorMacroModule
import org.cobalt.internal.mining.failsafe.MiningFailsafeHudModule
import org.cobalt.internal.mining.gemstone.GemstoneDebugHudModule
import org.cobalt.internal.mining.gemstone.GemstoneMinerModule as GemstoneRouteMinerModule
import org.cobalt.internal.mining.lobby.LobbyHopperModule
import org.cobalt.internal.mining.ore.OreMacroModule
import org.cobalt.internal.mining.pingless.PinglessMiningModule
import org.cobalt.internal.mining.powder.PowderMacroModule
import org.cobalt.internal.mining.routes.OrderedMiningRouteCommand
import org.cobalt.internal.mining.routes.OrderedMiningRouteDebugModule
import org.cobalt.internal.mining.scatha.ScathaMacroModule
import org.cobalt.internal.mining.tunnels.TunnelDebugHudModule
import org.cobalt.internal.mining.tunnels.TunnelMinerModule
import org.cobalt.internal.pathfinding.PathDebugHudModule
import org.cobalt.internal.pathfinding.PathfindingModule
import org.cobalt.internal.pathfinding.WorldCacheModule
import org.cobalt.internal.pathfinding.debug.PathPreviewCommand
import org.cobalt.internal.pathfinding.debug.PathPreviewRenderModule
import org.cobalt.internal.pathfinding.debug.PathRouteQuickAssignCommand
import org.cobalt.internal.pig.PigMacroModule
>>>>>>> Stashed changes
import org.cobalt.internal.qol.AutoStashModule
import org.cobalt.internal.qol.CraftHelperModule
import org.cobalt.internal.qol.ItemLockingModule
import org.cobalt.internal.qol.PriceTooltipModule
import org.cobalt.internal.qol.QolModule
import org.cobalt.internal.qol.RsaAutoGfsModule
<<<<<<< Updated upstream
import org.cobalt.internal.stats.MacroTimeTracker
import org.cobalt.internal.visual.BlockOverlayModule
import org.cobalt.internal.visual.BlockOutlineModule
import org.cobalt.internal.visual.CustomScoreboardModule
import org.cobalt.internal.visual.DeadEntityCleanerModule
import org.cobalt.internal.visual.FreecamModule
import org.cobalt.internal.visual.FullBrightModule
import org.cobalt.internal.visual.SkyboxChangerModule
import org.cobalt.internal.rotation.RotationsModule
import org.cobalt.internal.visual.DeployableHudModule
=======
import org.cobalt.internal.routes.RouteEditMode
import org.cobalt.internal.routes.RouteStore
import org.cobalt.internal.rotation.RotationsModule
import org.cobalt.internal.seal.YearOfTheSealModule
import org.cobalt.internal.spotify.SpotifyModule
import org.cobalt.internal.skyblock.HypixelManager
import org.cobalt.internal.skyblock.SkyblockEventManager
import org.cobalt.internal.stats.MacroTimeTracker
import org.cobalt.internal.visual.BlockOutlineModule
import org.cobalt.internal.visual.BlockOverlayModule
import org.cobalt.internal.visual.CustomScoreboardModule
import org.cobalt.internal.visual.DeadEntityCleanerModule
import org.cobalt.internal.visual.DeployableHudModule
import org.cobalt.internal.visual.FreecamModule
import org.cobalt.internal.visual.FullBrightModule
>>>>>>> Stashed changes
import org.cobalt.internal.visual.HotbarOverlayModule
import org.cobalt.internal.visual.MobEspModule as VisualMobEspModule
import org.cobalt.internal.visual.OrbitFreecamModule
import org.cobalt.internal.visual.PetDisplayModule
<<<<<<< Updated upstream
import org.cobalt.internal.visual.RsaPresetWaypointsModule
import org.cobalt.internal.visual.RsaEffectsModule
import org.cobalt.internal.visual.TitleScreenRenderer
import org.cobalt.internal.visual.WitherImpactOverlayModule
import org.cobalt.internal.garden.GardenAnalyzerModule
import org.cobalt.internal.garden.GardenMacroModule
import org.cobalt.internal.garden.PestWarningModule
import org.cobalt.internal.farming.FarmingMacroModule
import org.cobalt.internal.fishing.FishingMacroModule
import org.cobalt.internal.fishing.FishingHotspotModule
import org.cobalt.internal.fishing.FishingQolModule
import org.cobalt.internal.seal.YearOfTheSealModule
import org.cobalt.internal.pig.PigMacroModule
import org.cobalt.internal.spotify.SpotifyModule
import org.cobalt.internal.chat.ChatFilterModule
import org.cobalt.internal.chat.RngDropDisplayModule
import org.cobalt.internal.diana.DianaMacroModule
import org.cobalt.internal.diana.DianaHelperModule
import org.cobalt.internal.wardrobe.WardrobeModule
import org.cobalt.internal.auth.CobaltAuthService
import org.cobalt.internal.auth.CobaltSession
import org.cobalt.internal.garden.GardenHudModule
import org.cobalt.internal.routes.RouteEditMode
import org.cobalt.internal.routes.RouteStore
=======
import org.cobalt.internal.visual.RsaEffectsModule
import org.cobalt.internal.visual.RsaPresetWaypointsModule
import org.cobalt.internal.visual.SkyboxChangerModule
import org.cobalt.internal.visual.TitleScreenRenderer
import org.cobalt.internal.visual.WitherImpactOverlayModule
import org.cobalt.internal.wardrobe.WardrobeModule
>>>>>>> Stashed changes

@Suppress("UNUSED")
object Cobalt : ClientModInitializer {

  private var cobaltSession: CobaltSession = CobaltSession.INVALID

  override fun onInitializeClient() {
<<<<<<< Updated upstream
    cobaltSession = CobaltSession.readAndDelete()
    ModuleManager.addModules(
      listOf(
        WatermarkModule(),
        InventoryHudModule(),
        MiningModule,
        BlockMinerModule,
        GemstoneMinerModule,
        MiningHudModule,
        MiningCoinPopupModule,
        FairyModule,
        RoutesModule,
        MiningMacroModule,
        CommissionMacroModule,
        CommissionHudModule,
        AutoForgeModule,
        VeinDirectionModule,
        AutoLanternModule,
        NoFrillsMiningModule,
        CombatMacroModule,
        ZombieSlayerMacroModule,
        WolfSlayerMacroModule,
        SpiderSlayerMacroModule,
        EndermanSlayerMacroModule,
        VampireSlayerMacroModule,
        BlazeSlayerMacroModule,
        CombatPatrolModule,
        CombatHudModule,
        DungeonsModule,
        DungeonMobEspModule,
        BloodCampHelperModule,
        DungeonMapModule,
        DungeonRoutesModule,
        EtherwarpHelperModule,
        LeftClickEtherwarpModule,
        SmoothAotvModule,
        PathfindingModule,
        FullBrightModule,
        SkyboxChangerModule,
        CustomScoreboardModule,
        DeadEntityCleanerModule,
        FreecamModule,
        DeployableHudModule,
        OrbitFreecamModule,
        BlockOverlayModule,
        BlockOutlineModule,
        VisualMobEspModule,
        WitherImpactOverlayModule,
        QolModule,
        ItemLockingModule,
        PriceTooltipModule,
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
        FarmingMacroModule,
        FishingMacroModule,
        FishingHotspotModule,
        FishingQolModule,
        YearOfTheSealModule,
        PigMacroModule,
        SpotifyModule,
        ChatFilterModule,
        RngDropDisplayModule,
        DianaMacroModule,
        DianaHelperModule,
        WardrobeModule,
        GardenHudModule,
      )
    )

    AddonLoader.getAddons().map { it.second }.forEach {
      it.onLoad()
      ModuleManager.addModules(it.getModules())
    }
=======
    registerModules()
    loadAddons()
    registerCommands()
    registerCoreEventListeners()

    NativePathfinder.init()
    ChunkSerializer.register()
    HypixelManager.init()
    WorldScanner

    Config.loadModulesConfig()
    RouteStore.migrate()
    RouteStore.loadAssignments()
    ItemLockingModule.loadPersistedState()
    WardrobeModule.loadFavorites()
    DevUnlock.apply("remote auth disabled")

    EventBus.register(this)
    println("Dutt Client Initialized")
  }

  private fun registerModules() {
    ModuleManager.addModules(
      listOf(
        WatermarkModule(),
        InventoryHudModule(),

        MiningModule,
        BlockMinerModule,
        GemstoneMinerModule,
        GemstoneRouteMinerModule,
        GemstoneDebugHudModule,
        MiningHudModule,
        MiningCoinPopupModule,
        FairyModule,
        RoutesModule,
        MiningMacroModule,
        CommissionMacroModule,
        CommissionHudModule,
        CommissionDebugHudModul,
        CommissionRouteAssignmentModule,
        AutoForgeModule,
        VeinDirectionModule,
        AutoLanternModule,
        NoFrillsMiningModule,
        TunnelMinerModule,
        TunnelDebugHudModule,
        MiningBotModule,
        MiningBotDebugHudModule,
        MiningBotTargetRenderModule,
        ExcavatorMacroModule,
        OreMacroModule,
        PowderMacroModule,
        ScathaMacroModule,
        PinglessMiningModule,
        MiningFailsafeHudModule,
        LobbyHopperModule,
        OrderedMiningRouteDebugModule,
        StrictMiningAttackBlockGuardModule,

        CombatMacroModule,
        ZombieSlayerMacroModule,
        WolfSlayerMacroModule,
        SpiderSlayerMacroModule,
        EndermanSlayerMacroModule,
        VampireSlayerMacroModule,
        BlazeSlayerMacroModule,
        CombatPatrolModule,
        CombatHudModule,

        DungeonsModule,
        DungeonMobEspModule,
        DungeonChestGamblingModule,
        BloodCampHelperModule,
        DungeonMapModule,
        DungeonRoutesModule,

        EtherwarpHelperModule,
        LeftClickEtherwarpModule,
        SmoothAotvModule,
        PathfindingModule,
        PathDebugHudModule,
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
        VisualMobEspModule,
        WitherImpactOverlayModule,

        QolModule,
        ItemLockingModule,
        PriceTooltipModule,
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
    AddonLoader.getAddons()
      .map { it.second }
      .forEach { addon ->
        addon.onLoad()
        ModuleManager.addModules(addon.getModules())
      }
  }

  private fun registerCommands() {
    CommandManager.register(MainCommand)

    PathPreviewCommand.register()
    PathRouteQuickAssignCommand.register()
    StrictMiningCommand.register()
    OrderedMiningRouteCommand.register()
>>>>>>> Stashed changes

    CommandManager.register(MainCommand)
    CommandManager.dispatchAll()
    MacroTimeTracker.load()

    listOf(
      TickScheduler, MainCommand, NotificationManager,
      RotationExecutor, HudModuleManager, TitleScreenRenderer, MacroTimeTracker,
      RouteEditMode,
    ).forEach { EventBus.register(it) }
    NativePathfinder.init()
    ChunkSerializer.register()
    Config.loadModulesConfig()
    RouteStore.migrate()
    RouteStore.loadAssignments()
    ItemLockingModule.loadPersistedState()
    WardrobeModule.loadFavorites()
    CobaltAuthService.start(cobaltSession)
    EventBus.register(this)
    println("Dutt Client Initialized")
  }

<<<<<<< Updated upstream
=======
  private fun registerCoreEventListeners() {
    listOf(
      TickScheduler,
      MainCommand,
      NotificationManager,
      RotationExecutor,
      HudModuleManager,
      TitleScreenRenderer,
      MacroTimeTracker,
      RouteEditMode,
      SkyblockEventManager,
    ).forEach { EventBus.register(it) }
  }

>>>>>>> Stashed changes
  @SubscribeEvent
  fun onRespawn(event: PacketEvent.Incoming) {
    if (event.packet is ClientboundRespawnPacket) {
      NativePathfinder.onLevelChange()
      ChunkSerializer.invalidate()
      RouteStore.clearAllLoaded()
      RouteEditMode.onLevelChange()
      CommissionMacroModule.onLevelChange()
      RoutesModule.onLevelChange()
      MiningMacroModule.onLevelChange()
      GemstoneMinerModule.onLevelChange()
      YearOfTheSealModule.onLevelChange()
    }
  }
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
}
