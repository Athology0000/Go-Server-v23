package org.cobalt.internal.qol

import com.google.gson.JsonParser
import java.io.File
import net.minecraft.client.Minecraft
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.ActionSetting
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.CommandHotkeySetting
import org.cobalt.api.module.setting.impl.CommandHotkeyValue
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.helper.KeyBind
import org.lwjgl.glfw.GLFW

object QolModule : Module("QoL") {

  private val mc: Minecraft = Minecraft.getInstance()

  private val commandHotkeys = mutableListOf<CommandHotkeySetting>()

  val enabled = CheckboxSetting(
    "Enabled",
    "Enable QoL features.",
    false
  )

  private val commandHotkeysInfo = InfoSetting(
    "Command Hotkeys",
    "Bind keys to run chat commands.",
    InfoType.INFO
  )

  private val addHotkey = ActionSetting(
    "Add Hotkey",
    "Add a new command hotkey.",
    "+"
  ) {
    addCommandHotkey()
  }

  init {
    addSetting(enabled, commandHotkeysInfo, addHotkey)
    val initialCount = loadSavedHotkeyCount()
    repeat(initialCount) {
      addCommandHotkey()
    }

    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    commandHotkeys.forEachIndexed { index, entry ->
      if (!enabled.value && !isDefaultCobaltMenuHotkey(index, entry.value)) return@forEachIndexed
      if (entry.value.keyBind.isPressed()) {
        runCommand(entry.value.command)
      }
    }
  }

  private fun runCommand(raw: String) {
    val player = mc.player ?: return
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) return
    val command = trimmed.removePrefix("/")
    player.connection?.sendCommand(command)
  }

  private fun addCommandHotkey() {
    if (commandHotkeys.isNotEmpty()) {
      val last = commandHotkeys.last().value.command.trim()
      if (last.isEmpty()) {
        ChatUtils.sendMessage("Fill the last command before adding another.")
        return
      }
    }
    val nextIndex = commandHotkeys.size + 1
    val setting = CommandHotkeySetting(
      "Command Hotkey $nextIndex",
      "Keybind + command row $nextIndex.",
      defaultCommandHotkeyValue(nextIndex)
    )
    commandHotkeys.add(setting)
    addSetting(setting)
  }

  private fun defaultCommandHotkeyValue(index: Int): CommandHotkeyValue {
    return if (index == 1) {
      CommandHotkeyValue(KeyBind(GLFW.GLFW_KEY_P), "/cb")
    } else {
      CommandHotkeyValue()
    }
  }

  private fun isDefaultCobaltMenuHotkey(index: Int, value: CommandHotkeyValue): Boolean {
    return index == 0 &&
      value.keyBind.keyCode == GLFW.GLFW_KEY_P &&
      value.command.trim().removePrefix("/").equals("cb", ignoreCase = true)
  }

  private fun loadSavedHotkeyCount(): Int {
    val dir = mc.gameDirectory ?: return 1
    val file = File(dir, "config/cobalt/addons.json")
    if (!file.exists()) return 1
    val text = runCatching { file.readText() }.getOrNull()?.trim().orEmpty()
    if (text.isEmpty()) return 1

    return runCatching {
      val json = JsonParser.parseString(text).asJsonArray
      var maxIndex = 0
      json.forEach { addonEl ->
        val addonObj = addonEl.asJsonObject
        if (addonObj.get("addon")?.asString != "cobalt") return@forEach
        addonObj.getAsJsonArray("modules")?.forEach { moduleEl ->
          val moduleObj = moduleEl.asJsonObject
          if (moduleObj.get("name")?.asString != "QoL") return@forEach
          val settingsObj = moduleObj.getAsJsonObject("settings") ?: return@forEach
          settingsObj.entrySet().forEach { (key, _) ->
            val match = Regex("^Command Hotkey (\\d+)$").find(key) ?: return@forEach
            val idx = match.groupValues[1].toIntOrNull() ?: return@forEach
            if (idx > maxIndex) maxIndex = idx
          }
        }
      }
      maxIndex.coerceAtLeast(1)
    }.getOrDefault(1)
  }
}
