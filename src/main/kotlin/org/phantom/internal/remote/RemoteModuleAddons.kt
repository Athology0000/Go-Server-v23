package org.phantom.internal.remote

import org.phantom.api.addon.Addon
import org.phantom.api.hud.modules.MiningHudModule
import org.phantom.api.module.Module
import org.phantom.internal.BuiltinModules
import org.phantom.internal.diana.DianaHelperModule
import org.phantom.internal.diana.DianaMacroModule
import org.phantom.internal.mining.AutoForgeModule
import org.phantom.internal.mining.AutoLanternModule
import org.phantom.internal.mining.BlockMinerModule
import org.phantom.internal.mining.CommissionHudModule
import org.phantom.internal.mining.CommissionMacroModule
import org.phantom.internal.mining.GemstoneMinerModule
import org.phantom.internal.mining.MiningCoinPopupModule
import org.phantom.internal.mining.MiningMacroModule
import org.phantom.internal.mining.MiningModule
import org.phantom.internal.mining.NoFrillsMiningModule
import org.phantom.internal.mining.RoutesModule
import org.phantom.internal.mining.VeinDirectionModule
import org.phantom.internal.mining.VeinScannerModule
import org.phantom.internal.mining.WorldVeinCacherModule
import org.phantom.internal.mining.excavator.ExcavatorMacroModule
import org.phantom.internal.mining.failsafe.MiningFailsafeHudModule
import org.phantom.internal.mining.lobby.LobbyHopperModule
import org.phantom.internal.mining.ore.OreMacroModule
import org.phantom.internal.mining.pingless.PinglessMiningModule
import org.phantom.internal.mining.powder.PowderMacroModule
import org.phantom.internal.mining.scatha.ScathaMacroModule
import org.phantom.internal.mining.tunnels.TunnelMinerModule
import org.phantom.internal.qol.ItemLockingModule
import org.phantom.internal.wardrobe.WardrobeModule

class PhantomRemoteAddon : Addon() {
  override fun onLoad() {
    ItemLockingModule.loadPersistedState()
    WardrobeModule.loadFavorites()
  }

  override fun onUnload() {}

  override fun getModules(): List<Module> = BuiltinModules.all()
}

class PhantomMiningAddon : Addon() {
  override fun onLoad() {}

  override fun onUnload() {}

  override fun getModules(): List<Module> = listOf(
    MiningModule,
    MiningMacroModule,
    MiningHudModule,
    MiningCoinPopupModule,
    RoutesModule,
    VeinScannerModule,
    BlockMinerModule,
    GemstoneMinerModule,
    CommissionMacroModule,
    CommissionHudModule,
    AutoForgeModule,
    AutoLanternModule,
    VeinDirectionModule,
    NoFrillsMiningModule,
    MiningFailsafeHudModule,
    PinglessMiningModule,
    ExcavatorMacroModule,
    OreMacroModule,
    PowderMacroModule,
    ScathaMacroModule,
    LobbyHopperModule,
    TunnelMinerModule,
    WorldVeinCacherModule,
  ).distinctBy { it.name.trim().lowercase() }
}

class PhantomDianaAddon : Addon() {
  override fun onLoad() {}

  override fun onUnload() {}

  override fun getModules(): List<Module> = listOf(
    DianaMacroModule,
    DianaHelperModule,
  ).distinctBy { it.name.trim().lowercase() }
}
