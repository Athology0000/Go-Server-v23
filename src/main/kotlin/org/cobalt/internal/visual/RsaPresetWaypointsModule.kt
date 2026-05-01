package org.cobalt.internal.visual

import java.awt.Color
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.ChatEvent
import org.cobalt.api.event.impl.render.WorldRenderEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.inGroup
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.render.Render3D

object RsaPresetWaypointsModule : Module("RSA Preset Waypoints") {

  override val category = ModuleCategory.VISUAL

  private val enabled = CheckboxSetting(
    "Enabled",
    "Render the preset RSA waypoint set.",
    false,
  )

  private val renderDistance = SliderSetting(
    "Render Distance",
    "Maximum distance for preset waypoint rendering.",
    900.0,
    64.0,
    2000.0,
  )

  private val abiPhones = CheckboxSetting("AbiPhones", "Show Abiphone waypoint locations.", false).inGroup(CRIMSON_GROUP)
  private val dean = CheckboxSetting("Dean", "Show Dean waypoint.", false).inGroup(CRIMSON_GROUP)
  private val dailies = CheckboxSetting("Dailies", "Show Crimson Isle daily quest waypoints.", false).inGroup(CRIMSON_GROUP)
  private val keys = CheckboxSetting("Kuudra Keys", "Show Kuudra key waypoints.", false).inGroup(CRIMSON_GROUP)
  private val blacksmiths = CheckboxSetting("BlackSmiths", "Show faction blacksmith waypoints.", false).inGroup(CRIMSON_GROUP)
  private val minionShops = CheckboxSetting("Minion Shops", "Show faction minion shop waypoints.", false).inGroup(CRIMSON_GROUP)
  private val miniBosses = CheckboxSetting("MiniBosses", "Show Crimson Isle miniboss waypoints.", false).inGroup(CRIMSON_GROUP)
  private val duels = CheckboxSetting("Duels", "Show faction duel waypoints.", false).inGroup(CRIMSON_GROUP)
  private val dojo = CheckboxSetting("Dojo", "Show Dojo waypoint.", false).inGroup(CRIMSON_GROUP)
  private val chickenCoop = CheckboxSetting("Chicken Coop", "Show Chicken Coop waypoint.", false).inGroup(CRIMSON_GROUP)
  private val matriarch = CheckboxSetting("Matriarch", "Show Matriarch waypoint.", false).inGroup(CRIMSON_GROUP)
  private val baar = CheckboxSetting("Baar Npc (+250 rep)", "Show Baar and print item helper messages.", false).inGroup(CRIMSON_GROUP)

  init {
    addSetting(
      enabled,
      renderDistance,
      blacksmiths,
      minionShops,
      chickenCoop,
      abiPhones,
      dean,
      dailies,
      keys,
      dojo,
      matriarch,
      miniBosses,
      duels,
      baar,
    )
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onRender(event: WorldRenderEvent.Last) {
    val player = Minecraft.getInstance().player ?: return
    if (!enabled.value) return

    val waypoints = buildList {
      if (abiPhones.value) {
        add(Waypoint("Abiphone Mage", Vec3(-78.5, 107.0, -791.5), Color.MAGENTA))
        add(Waypoint("Abiphone Hub", Vec3(66.5, 72.0, -63.5), Color.MAGENTA))
      }
      if (baar.value) add(Waypoint("Baar Beer", Vec3(-637.5, 123.0, -792.0), Color.RED))
      if (dean.value) add(Waypoint("Dean", Vec3(-16.5, 123.0, -882.5), Color.PINK))
      if (dailies.value) {
        add(Waypoint("Daily Mage", Vec3(-124.5, 92.0, -754.5), Color.YELLOW))
        add(Waypoint("Daily Bers", Vec3(-579.5, 100.0, -687.5), Color.YELLOW))
      }
      if (keys.value) {
        add(Waypoint("Kuudra Keys Mage", Vec3(-132.5, 89.0, -721.5), ORANGE))
        add(Waypoint("Kuudra Keys Bers", Vec3(-581.5, 99.0, -711.5), ORANGE))
      }
      if (blacksmiths.value) {
        add(Waypoint("Bers Blacksmith", Vec3(-548.5, 98.0, -707.5), Color.GRAY))
        add(Waypoint("Mage Blacksmith", Vec3(-81.5, 92.0, -734.5), Color.GRAY))
      }
      if (minionShops.value) {
        add(Waypoint("Bers Minion Shop", Vec3(-645.5, 101.0, -825.5), Color.GREEN))
        add(Waypoint("Mage Minion Shop", Vec3(-45.5, 107.0, -779.5), Color.GREEN))
      }
      if (miniBosses.value) {
        add(Waypoint("Bers MiniBoss", Vec3(-535.5, 117.0, -904.5), Color.RED))
        add(Waypoint("Mage MiniBoss", Vec3(-180.5, 105.0, -859.5), Color.RED))
        add(Waypoint("Ashfang", Vec3(-485.5, 135.0, -1016.5), Color.RED))
        add(Waypoint("Magma Boss", Vec3(-367.5, 63.0, -792.5), Color.RED))
        add(Waypoint("Blade Soul", Vec3(-296.5, 82.0, -517.5), Color.RED))
      }
      if (duels.value) {
        add(Waypoint("Bers Duels", Vec3(-597.5, 113.0, -638.5), Color.BLUE))
        add(Waypoint("Mage Duels", Vec3(149.5, 106.0, -852.5), Color.BLUE))
      }
      if (dojo.value) add(Waypoint("Dojo", Vec3(-235.5, 108.0, -597.5), Color.CYAN))
      if (chickenCoop.value) add(Waypoint("Chicken Coop", Vec3(-32.5, 93.0, -816.5), Color.WHITE))
      if (matriarch.value) add(Waypoint("Matriarch", Vec3(-531.5, 40.0, -889.5), ORANGE))
    }

    val maxDistanceSq = renderDistance.value * renderDistance.value
    for (waypoint in waypoints) {
      if (player.position().distanceToSqr(waypoint.pos) <= maxDistanceSq) {
        drawWaypoint(event, waypoint)
      }
    }
  }

  @SubscribeEvent
  fun onChat(event: ChatEvent.Receive) {
    if (!enabled.value || !baar.value) return
    val message = ChatFormatting.stripFormatting(event.message ?: return) ?: return
    val response = baarResponses.firstOrNull { message.contains(it.trigger) }?.response ?: return
    ChatUtils.sendMessage(response)
  }

  private fun drawWaypoint(event: WorldRenderEvent.Last, waypoint: Waypoint) {
    val lineColor = Color(waypoint.color.red, waypoint.color.green, waypoint.color.blue, 180)
    Render3D.drawLine(event.context, waypoint.pos, waypoint.pos.add(0.0, 18.0, 0.0), lineColor, esp = true, thickness = 2f)
    Render3D.drawCircleOutline(event.context, waypoint.pos, 0.5f, waypoint.color, esp = true, thickness = 2f)
    Render3D.drawWorldLabel(event.context, waypoint.pos.add(0.0, 2.0, 0.0), waypoint.name, waypoint.color, 0.8f)
  }

  private data class Waypoint(val name: String, val pos: Vec3, val color: Color)

  private data class BaarResponse(val trigger: String, val response: String)

  private val baarResponses = listOf(
    BaarResponse("Hello there, adventurer!", "Hello BAAR!!"),
    BaarResponse(
      "To start out I'll need some generic gold to experiment on, could you get me a stack?",
      "Baar: 64x Gold ingots.",
    ),
    BaarResponse(
      "Next I need some flat gold to test how reflective gold is, could you try forging gold into 5 gold plates?",
      "Baar: 5x Golden Plates. (/bz Golden plate)",
    ),
    BaarResponse(
      "I heard there exist golden boots that helps you swim faster in water. I would like to test their magical properties, could you bring me them?",
      "Baar: Divers Boots. (/ahs Diver's Boots)",
    ),
    BaarResponse(
      "It seems like the first piece you brought me is only 25% of the magical power, could you get me the chestplate?",
      "Baar: Divers Shirt. (/ahs Diver's Shirt)",
    ),
    BaarResponse(
      "Now I need a lot of compacted gold, it has to be extremely dense. A half stack should do.",
      "Baar: 32x Enchanted Gold Block. (/bz Enchanted Gold Block)",
    ),
    BaarResponse(
      "There is a fine-grained gold substance somewhere in the Hub, I'll need 5 of that.",
      "Baar: 5x Golden Powder. (/bz Golden Powder)",
    ),
    BaarResponse(
      "Next I'm going to need a vegetable that is made out of solid gold. I want to experiment with how gold interacts with organics, maybe you can find some, like a half stack?",
      "Baar: 32x Enchanted Golden Carrot (/bz Enchanted Golden Carrot)",
    ),
    BaarResponse(
      "I just need one last thing, there's an extremely dangerous scientist who sells an assortment of items, he has a special rounded type of gold. Try to convince him to sell you it.",
      "Baar: 1x Golden Ball (/bz Golden Ball)",
    ),
    BaarResponse("As promised, here is your reward.", "yw for the help :)"),
  )

  private val ORANGE = Color(255, 170, 0)
  private const val CRIMSON_GROUP = "Crimson Isle"
}
