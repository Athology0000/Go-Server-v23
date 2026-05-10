package org.cobalt.internal.pathfinding.debug

import com.mojang.brigadier.arguments.StringArgumentType
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import org.cobalt.api.util.ChatUtils
import org.cobalt.internal.routes.RouteStore

object PathRouteQuickAssignCommand {

  fun register() {
    ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->

      dispatcher.register(
        literal("pathroute")
          .then(
            literal("assign")
              .then(
                argument("slotKey", StringArgumentType.word())
                  .then(
                    argument("routeName", StringArgumentType.greedyString())
                      .executes { ctx ->
                        val slotKey = StringArgumentType.getString(ctx, "slotKey")
                        val routeName = StringArgumentType.getString(ctx, "routeName")

                        assign(slotKey, routeName)
                        1
                      }
                  )
              )
          )
          .then(
            literal("gemstone")
              .then(
                argument("routeName", StringArgumentType.greedyString())
                  .executes { ctx ->
                    val routeName = StringArgumentType.getString(ctx, "routeName")
                    assign("gemstone-route", routeName)
                    1
                  }
              )
          )
          .then(
            literal("tunnel")
              .then(
                argument("routeName", StringArgumentType.greedyString())
                  .executes { ctx ->
                    val routeName = StringArgumentType.getString(ctx, "routeName")
                    assign("tunnel-route", routeName)
                    1
                  }
              )
          )
          .then(
            literal("tungsten")
              .then(
                argument("routeName", StringArgumentType.greedyString())
                  .executes { ctx ->
                    val routeName = StringArgumentType.getString(ctx, "routeName")
                    assign("glacite-commission-area:tungsten", routeName)
                    1
                  }
              )
          )
          .then(
            literal("umber")
              .then(
                argument("routeName", StringArgumentType.greedyString())
                  .executes { ctx ->
                    val routeName = StringArgumentType.getString(ctx, "routeName")
                    assign("glacite-commission-area:umber", routeName)
                    1
                  }
              )
          )
          .then(
            literal("glacite")
              .then(
                argument("routeName", StringArgumentType.greedyString())
                  .executes { ctx ->
                    val routeName = StringArgumentType.getString(ctx, "routeName")
                    assign("glacite-commission-area:glacite", routeName)
                    1
                  }
              )
          )
          .then(
            literal("glaciteslayer")
              .then(
                argument("routeName", StringArgumentType.greedyString())
                  .executes { ctx ->
                    val routeName = StringArgumentType.getString(ctx, "routeName")
                    assign("glacite-commission-area:slayer", routeName)
                    1
                  }
              )
          )
          .then(
            literal("glacitemining")
              .then(
                argument("routeName", StringArgumentType.greedyString())
                  .executes { ctx ->
                    val routeName = StringArgumentType.getString(ctx, "routeName")
                    assign("glacite-commission-type:mining", routeName)
                    1
                  }
              )
          )
          .then(
            literal("glaciteregion")
              .then(
                argument("routeName", StringArgumentType.greedyString())
                  .executes { ctx ->
                    val routeName = StringArgumentType.getString(ctx, "routeName")
                    assign("commission-region:glacite", routeName)
                    1
                  }
              )
          )
          .then(
            literal("check")
              .then(
                argument("slotKey", StringArgumentType.word())
                  .executes { ctx ->
                    val slotKey = StringArgumentType.getString(ctx, "slotKey")
                    check(slotKey)
                    1
                  }
              )
          )
          .then(
            literal("clear")
              .then(
                argument("slotKey", StringArgumentType.word())
                  .executes { ctx ->
                    val slotKey = StringArgumentType.getString(ctx, "slotKey")
                    clear(slotKey)
                    1
                  }
              )
          )
          .then(
            literal("keys")
              .executes {
                printKeys()
                1
              }
          )
      )
    }
  }

  private fun assign(
    slotKey: String,
    routeName: String
  ) {
    val cleanRoute = routeName.trim()
    val cleanKey = slotKey.trim().lowercase()

    if (cleanKey.isBlank()) {
      ChatUtils.sendMessage("PathRoute: slot key is empty.")
      return
    }

    if (cleanRoute.isBlank()) {
      ChatUtils.sendMessage("PathRoute: route name is empty.")
      return
    }

    val exists = RouteStore.loadAll().any {
      it.name.equals(cleanRoute, ignoreCase = true)
    }

    if (!exists) {
      ChatUtils.sendMessage("PathRoute: route \"$cleanRoute\" was not found.")
      return
    }

    RouteStore.setSlotRoute(cleanKey, cleanRoute)
    ChatUtils.sendMessage("PathRoute: assigned $cleanKey → $cleanRoute")
  }

  private fun check(slotKey: String) {
    val cleanKey = slotKey.trim().lowercase()
    val assigned = RouteStore.getSlotRoute(cleanKey)

    if (assigned.isNullOrBlank()) {
      ChatUtils.sendMessage("PathRoute: $cleanKey → none")
    } else {
      ChatUtils.sendMessage("PathRoute: $cleanKey → $assigned")
    }
  }

  private fun clear(slotKey: String) {
    val cleanKey = slotKey.trim().lowercase()
    RouteStore.clearSlotRoute(cleanKey)
    ChatUtils.sendMessage("PathRoute: cleared $cleanKey")
  }

  private fun printKeys() {
    ChatUtils.sendMessage("PathRoute keys:")
    ChatUtils.sendMessage("- gemstone-route")
    ChatUtils.sendMessage("- tunnel-route")
    ChatUtils.sendMessage("- commission-region:glacite")
    ChatUtils.sendMessage("- glacite-commission-type:mining")
    ChatUtils.sendMessage("- glacite-commission-area:tungsten")
    ChatUtils.sendMessage("- glacite-commission-area:umber")
    ChatUtils.sendMessage("- glacite-commission-area:glacite")
    ChatUtils.sendMessage("- glacite-commission-area:slayer")
    ChatUtils.sendMessage("- dwarven-commission-type:mining")
    ChatUtils.sendMessage("- dwarven-commission-area:royal-mines")
  }
}