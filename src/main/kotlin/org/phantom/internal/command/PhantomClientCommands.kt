package org.phantom.internal.commands

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import org.phantom.internal.auth.PhantomAuthDebug
import org.phantom.internal.auth.DevUnlock

object PhantomClientCommands {
  fun register() {
    ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
      dispatcher.register(
        ClientCommandManager.literal("cb")
          .executes {
            PhantomAuthDebug.info("/cb command executed")
            DevUnlock.apply("/cb dev unlock")
            openClickGuiDev()
            1
          }
      )
    }

    PhantomAuthDebug.info("/cb command registered")
  }

  private fun openClickGuiDev() {
    try {
      val minecraftClass = Class.forName("net.minecraft.client.Minecraft")
      val getInstance = minecraftClass.getDeclaredMethod("getInstance")
      val client = getInstance.invoke(null)

      val screenClassNames = listOf(
        "org.phantom.internal.ui.clickgui.PhantomClickGuiScreen",
        "org.phantom.internal.gui.PhantomClickGuiScreen",
        "org.phantom.internal.ui.PhantomClickGuiScreen",
        "org.phantom.ui.PhantomClickGuiScreen",
        "org.phantom.client.gui.PhantomClickGuiScreen",
        "org.phantom.internal.gui.ClickGuiScreen",
        "org.phantom.gui.ClickGuiScreen"
      )

      var screen: Any? = null

      for (name in screenClassNames) {
        try {
          val clazz = Class.forName(name)
          screen = clazz.getDeclaredConstructor().newInstance()
          PhantomAuthDebug.info("opening gui class=$name")
          break
        } catch (_: Throwable) {
        }
      }

      if (screen == null) {
        PhantomAuthDebug.warn("Could not find ClickGui screen class. /cb unlocked modules but did not open GUI.")
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
        PhantomAuthDebug.warn("Could not find Minecraft setScreen method")
      }
    } catch (t: Throwable) {
      PhantomAuthDebug.error("Failed to open /cb GUI", t)
    }
  }
}
