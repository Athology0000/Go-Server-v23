package org.phantom

import net.minecraft.network.protocol.game.ClientboundRespawnPacket
import org.phantom.api.command.CommandManager
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.PacketEvent
import org.phantom.api.hud.HudModuleManager
import org.phantom.api.notification.NotificationManager
import org.phantom.api.pathfinder.jni.ChunkSerializer
import org.phantom.api.pathfinder.jni.NativePathfinder
import org.phantom.api.rotation.RotationExecutor
import org.phantom.api.util.TickScheduler
import org.phantom.api.util.WorldScanner
import org.phantom.internal.command.MainCommand
import org.phantom.internal.helper.Config
import org.phantom.internal.pathfinding.PathServiceTickBridge
import org.phantom.internal.pathfinding.debug.PathRouteQuickAssignCommand
import org.phantom.internal.rotation.BlockRotationDebugRenderer
import org.phantom.internal.rotation.PhantomRotation
import org.phantom.internal.routes.RouteEditMode
import org.phantom.internal.routes.RouteStore
import org.phantom.internal.skyblock.HypixelManager
import org.phantom.internal.skyblock.SkyblockEventManager
import org.phantom.internal.stats.MacroTimeTracker
import org.phantom.internal.visual.TitleScreenRenderer

/**
 * Public-layer infrastructure bring-up.
 *
 * Runs everything the Phantom client needs before protected modules load —
 * commands, core event listeners, the native pathfinder, Skyblock tracking,
 * and the config/route stores — but NOT authentication or module registration.
 *
 * The loader calls [init] after its runtime guard and before stage-2 auth; the
 * dev entrypoint [Phantom] also calls it directly. [init] is idempotent so the
 * two callers cannot double-initialize.
 */
object PhantomPublicInit {
  @Volatile
  private var initialized = false

  fun init() {
    if (initialized) return
    initialized = true

    registerCommands()
    registerCoreEventListeners()

    NativePathfinder.init()
    ChunkSerializer.register()
    HypixelManager.init()
    WorldScanner

    Config.loadModulesConfig()
    RouteStore.migrate()
    RouteStore.loadAssignments()

    EventBus.register(this)
  }

  private fun registerCommands() {
    CommandManager.register(MainCommand)
    PathRouteQuickAssignCommand.register()
    CommandManager.dispatchAll()
    MacroTimeTracker.load()
  }

  private fun registerCoreEventListeners() {
    listOf(
      TickScheduler,
      MainCommand,
      NotificationManager,
      RotationExecutor,
      PhantomRotation,
      BlockRotationDebugRenderer,
      HudModuleManager,
      TitleScreenRenderer,
      MacroTimeTracker,
      RouteEditMode,
      HypixelManager,
      SkyblockEventManager,
      PathServiceTickBridge,
    ).forEach { EventBus.register(it) }
  }

  @SubscribeEvent
  fun onRespawn(event: PacketEvent.Incoming) {
    if (event.packet is ClientboundRespawnPacket) {
      NativePathfinder.onLevelChange()
      ChunkSerializer.invalidate()
      RouteStore.clearAllLoaded()
      RouteEditMode.onLevelChange()
      notifyRemoteModuleLevelChange("org.phantom.internal.mining.CommissionMacroModule")
      notifyRemoteModuleLevelChange("org.phantom.internal.mining.RoutesModule")
      notifyRemoteModuleLevelChange("org.phantom.internal.mining.MiningMacroModule")
      notifyRemoteModuleLevelChange("org.phantom.internal.mining.GemstoneMinerModule")
      notifyRemoteModuleLevelChange("org.phantom.internal.seal.YearOfTheSealModule")
    }
  }

  private fun notifyRemoteModuleLevelChange(className: String) {
    runCatching {
      val clazz = Class.forName(className)
      val instance = clazz.getField("INSTANCE").get(null)
      clazz.getMethod("onLevelChange").invoke(instance)
    }
  }
}
