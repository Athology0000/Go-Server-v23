package org.phantom.internal.skyblock

import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.ChatEvent
import org.phantom.api.failsafe.FailsafeManager
import org.phantom.api.macro.MacroState

/** Centralizes common Hypixel/SkyBlock chat events so modules do not all parse chat separately. */
object SkyblockEventManager {
  enum class EventType {
    DEATH,
    WARP,
    SERVER_CHANGE,
    LIMBO,
    FULL_INVENTORY,
    ABILITY_COOLDOWN,
    ABILITY_READY,
    EMPTY_DRILL,
    COOKIE_REQUIRED,
  }

  private val listeners = mutableMapOf<EventType, MutableList<() -> Unit>>()

  fun subscribe(type: EventType, callback: () -> Unit) {
    listeners.computeIfAbsent(type) { mutableListOf() }.add(callback)
  }

  fun emit(type: EventType) {
    when (type) {
      EventType.DEATH,
      EventType.WARP,
      EventType.SERVER_CHANGE,
      EventType.LIMBO -> FailsafeManager.disableAllFor(1_000L)
      else -> Unit
    }

    if (type == EventType.LIMBO) {
      MacroState.stopAll(MacroState.ToggleContext.FAILSAFE, "sent to limbo")
    }

    listeners[type]?.forEach { it.invoke() }
  }

  @SubscribeEvent
  fun onChat(event: ChatEvent.Receive) {
    val message = event.message?.lowercase() ?: return

    when {
      "you died" in message || "you were killed" in message -> emit(EventType.DEATH)
      "sending to server" in message || "warping" in message -> emit(EventType.WARP)
      "sending you to" in message || "transferring" in message -> emit(EventType.SERVER_CHANGE)
      "limbo" in message -> emit(EventType.LIMBO)
      "your inventory is full" in message -> emit(EventType.FULL_INVENTORY)
      "ability is on cooldown" in message -> emit(EventType.ABILITY_COOLDOWN)
      "not enough fuel" in message || "drill" in message && "empty" in message -> emit(EventType.EMPTY_DRILL)
      "booster cookie" in message -> emit(EventType.COOKIE_REQUIRED)
    }
  }
}
