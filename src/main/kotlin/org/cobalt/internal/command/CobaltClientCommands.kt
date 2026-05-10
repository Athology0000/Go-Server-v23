package org.cobalt.internal.commands

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import org.cobalt.internal.auth.CobaltAuthDebug
import org.cobalt.internal.auth.DevUnlock

object CobaltClientCommands {
  fun register() {
    ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
      dispatcher.register(
        ClientCommandManager.literal("cb")
          .executes {
            CobaltAuthDebug.info("/cb command executed")
            DevUnlock.apply("/cb dev unlock")
            openClickGuiDev()
            1
          }
      )
    }

    CobaltAuthDebug.info("/cb command registered")
  }

  private fun openClickGuiDev() {
    try {
      val minecraftClass = Class.forName("net.minecraft.client.Minecraft")
      val getInstance = minecraftClass.getDeclaredMethod("getInstance")
      val client = getInstance.invoke(null)

      val screenClassNames = listOf(
        "org.cobalt.internal.ui.clickgui.CobaltClickGuiScreen",
        "org.cobalt.internal.gui.CobaltClickGuiScreen",
        "org.cobalt.internal.ui.CobaltClickGuiScreen",
        "org.cobalt.ui.CobaltClickGuiScreen",
        "org.cobalt.client.gui.CobaltClickGuiScreen",
        "org.cobalt.internal.gui.ClickGuiScreen",
        "org.cobalt.gui.ClickGuiScreen"
      )

      var screen: Any? = null

      for (name in screenClassNames) {
        try {
          val clazz = Class.forName(name)
          screen = clazz.getDeclaredConstructor().newInstance()
          CobaltAuthDebug.info("opening gui class=$name")
          break
        } catch (_: Throwable) {
        }
      }

      if (screen == null) {
        CobaltAuthDebug.warn("Could not find ClickGui screen class. /cb unlocked modules but did not open GUI.")
        return
      }

      val executeMethod = minecraftClass.methods.firstOrNull {
        it.name == "execute" && it.parameterTypes.size == 1
      }

      val setScreenMethod = minecraftClass.methods.firstOrNull {
        it.name == "setScreen" && it.parameterTypes.size == 1
      }

      if (executeMethod != null && setScreenMethod != null) {
        executeMethod.invoke(
          client,
          Runnable {
            setScreenMethod.invoke(client, screen)
          }
        )
      } else if (setScreenMethod != null) {
        setScreenMethod.invoke(client, screen)
      } else {
        CobaltAuthDebug.warn("Could not find Minecraft setScreen method")
      }
    } catch (t: Throwable) {
      CobaltAuthDebug.error("Failed to open /cb GUI", t)
    }
  }
}
