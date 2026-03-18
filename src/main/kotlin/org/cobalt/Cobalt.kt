package org.cobalt

import net.fabricmc.api.ClientModInitializer
import org.cobalt.api.command.CommandManager
import org.cobalt.api.event.EventBus
import org.cobalt.api.hud.HudModuleManager
import org.cobalt.api.hud.modules.WatermarkModule
import org.cobalt.api.hud.modules.InventoryHudModule
import org.cobalt.api.module.ModuleManager
import org.cobalt.api.notification.NotificationManager
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.util.TickScheduler
import org.cobalt.internal.combat.CombatHudModule
import org.cobalt.internal.combat.CombatMacroModule
import org.cobalt.internal.command.MainCommand
import org.cobalt.internal.dungeons.DungeonsModule
import org.cobalt.internal.etherwarp.EtherwarpHelperModule
import org.cobalt.internal.etherwarp.LeftClickEtherwarpModule
import org.cobalt.internal.helper.Config
import org.cobalt.internal.loader.AddonLoader
import org.cobalt.internal.mining.MiningModule
import org.cobalt.internal.mining.FairyModule
import org.cobalt.internal.mining.RoutesModule
import org.cobalt.internal.mining.MiningMacroModule
import org.cobalt.internal.mining.CommissionHudModule
import org.cobalt.internal.mining.CommissionMacroModule
import org.cobalt.internal.mining.VeinDirectionModule
import org.cobalt.internal.mining.AutoLanternModule
import org.cobalt.internal.pathfinding.PathfindingModule
import org.cobalt.internal.qol.QolModule
import org.cobalt.internal.visual.BlockOverlayModule
import org.cobalt.internal.visual.BlockOutlineModule
import org.cobalt.internal.visual.DarkModeModule
import org.cobalt.internal.visual.FreecamModule
import org.cobalt.internal.visual.FullBrightModule
import org.cobalt.internal.rotation.RotationsModule
import org.cobalt.internal.visual.HotbarOverlayModule
import org.cobalt.internal.visual.OrbitFreecamModule
import org.cobalt.internal.visual.PetDisplayModule
import org.cobalt.internal.visual.TitleScreenRenderer

@Suppress("UNUSED")
object Cobalt : ClientModInitializer {


  override fun onInitializeClient() {
    ModuleManager.addModules(
      listOf(
        WatermarkModule(),
        InventoryHudModule(),
        MiningModule,
        FairyModule,
        RoutesModule,
        MiningMacroModule,
        CommissionMacroModule,
        CommissionHudModule,
        VeinDirectionModule,
        AutoLanternModule,
        CombatMacroModule,
        CombatHudModule,
        DungeonsModule,
        EtherwarpHelperModule,
        LeftClickEtherwarpModule,
        PathfindingModule,
        FullBrightModule,
        DarkModeModule,
        FreecamModule,
        OrbitFreecamModule,
        BlockOverlayModule,
        BlockOutlineModule,
        QolModule,
        RotationsModule,
        HotbarOverlayModule,
        PetDisplayModule
      )
    )

    AddonLoader.getAddons().map { it.second }.forEach {
      it.onLoad()
      ModuleManager.addModules(it.getModules())
    }

    CommandManager.register(MainCommand)
    CommandManager.dispatchAll()

    listOf(
      TickScheduler, MainCommand, NotificationManager,
      RotationExecutor, HudModuleManager, TitleScreenRenderer,
    ).forEach { EventBus.register(it) }
    Config.loadModulesConfig()
    EventBus.register(this)
    println("Dutt Client Initialized")
  }

}
