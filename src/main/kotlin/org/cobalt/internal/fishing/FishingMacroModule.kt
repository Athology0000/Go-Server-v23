package org.cobalt.internal.fishing

import net.minecraft.client.Minecraft
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.FishingHook
import net.minecraft.world.item.FishingRodItem
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.PacketEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.InventoryUtils
import org.cobalt.mixin.client.MinecraftAccessor

object FishingMacroModule : Module("Fishing Macro") {

  private enum class State {
    IDLE,
    CASTING,
    WAITING_BITE,
    REELING,
    RECAST_DELAY,
    MISSING_ROD,
    PAUSED,
  }

  private val mc = Minecraft.getInstance()

  private val enabledSetting = CheckboxSetting(
    "Enabled",
    "Automatically casts, waits for a bite, reels, and recasts your fishing rod.",
    false
  )

  private val infoSetting = InfoSetting(
    "Fishing Macro",
    "First pass autonomous fishing: hotbar rod selection, splash detection, timeout recovery, and recast loop.",
    InfoType.INFO
  )

  private val stateText = TextSetting(
    "State",
    "Current fishing macro state.",
    "Idle"
  )

  private val rodSlotSetting = SliderSetting(
    "Rod Slot",
    "Hotbar slot of the fishing rod (0 = auto-detect, 1-9 = fixed slot).",
    0.0,
    0.0,
    9.0,
    1.0
  )

  private val reactionDelayTicksSetting = SliderSetting(
    "Reaction Delay",
    "Ticks to wait after the splash before reeling in.",
    3.0,
    0.0,
    12.0,
    1.0
  )

  private val recastDelayTicksSetting = SliderSetting(
    "Recast Delay",
    "Ticks to wait after reeling before casting again.",
    12.0,
    2.0,
    40.0,
    1.0
  )

  private val castSettleTicksSetting = SliderSetting(
    "Cast Settle",
    "Minimum ticks to wait after casting before checking for the bobber again.",
    8.0,
    2.0,
    20.0,
    1.0
  )

  private val hookTimeoutTicksSetting = SliderSetting(
    "Hook Timeout",
    "Maximum ticks to leave the bobber out before reeling and recasting.",
    220.0,
    40.0,
    1200.0,
    5.0
  )

  private val splashRadiusSetting = SliderSetting(
    "Splash Radius",
    "Maximum distance from your bobber for a splash packet to count as your bite.",
    2.2,
    0.5,
    6.0,
    0.1
  )

  private val autoSelectRodSetting = CheckboxSetting(
    "Auto Select Rod",
    "Keep your fishing rod selected while the macro is active.",
    true
  )

  private var wasEnabled = false
  private var state = State.IDLE
  private var nextActionTick = 0L
  private var pendingReel = false
  private var lastMissingRodNoticeTick = -1L

  init {
    addSetting(
      enabledSetting,
      infoSetting,
      stateText,
      rodSlotSetting,
      reactionDelayTicksSetting,
      recastDelayTicksSetting,
      castSettleTicksSetting,
      hookTimeoutTicksSetting,
      splashRadiusSetting,
      autoSelectRodSetting,
    )
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (!enabledSetting.value) {
      if (wasEnabled) {
        stopMacro()
      }
      wasEnabled = false
      return
    }
    wasEnabled = true

    val player = mc.player ?: run { stopMacro(); return }
    val level = mc.level ?: run { stopMacro(); return }

    if (mc.screen != null) {
      setState(State.PAUSED)
      return
    }

    val rodSlot = resolveRodSlot(player)
    if (rodSlot !in 0..8) {
      setState(State.MISSING_ROD)
      if (lastMissingRodNoticeTick < 0L || level.gameTime - lastMissingRodNoticeTick >= MISSING_ROD_NOTICE_TICKS) {
        ChatUtils.sendMessage("Fishing Macro requires a fishing rod in your hotbar.")
        lastMissingRodNoticeTick = level.gameTime
      }
      return
    }
    lastMissingRodNoticeTick = -1L

    if (autoSelectRodSetting.value && player.inventory.selectedSlot != rodSlot) {
      InventoryUtils.holdHotbarSlot(rodSlot)
    }

    val hook = resolveOwnedHook(player)

    if (pendingReel) {
      if (hook == null) {
        pendingReel = false
        nextActionTick = level.gameTime + recastDelayTicksSetting.value.toLong()
        setState(State.RECAST_DELAY)
        return
      }
      if (level.gameTime >= nextActionTick) {
        useRod(rodSlot)
        pendingReel = false
        nextActionTick = level.gameTime + recastDelayTicksSetting.value.toLong()
        setState(State.REELING)
      } else {
        setState(State.WAITING_BITE)
      }
      return
    }

    if (hook != null) {
      if (hook.tickCount >= hookTimeoutTicksSetting.value.toInt() && level.gameTime >= nextActionTick) {
        useRod(rodSlot)
        nextActionTick = level.gameTime + recastDelayTicksSetting.value.toLong()
        setState(State.REELING)
      } else {
        setState(State.WAITING_BITE)
      }
      return
    }

    if (level.gameTime < nextActionTick) {
      setState(if (state == State.CASTING) State.CASTING else State.RECAST_DELAY)
      return
    }

    useRod(rodSlot)
    nextActionTick = level.gameTime + castSettleTicksSetting.value.toLong()
    setState(State.CASTING)
  }

  @SubscribeEvent
  fun onPacket(event: PacketEvent.Incoming) {
    if (!enabledSetting.value) return

    val packet = event.packet as? ClientboundSoundPacket ?: return
    if (packet.sound.value() != SoundEvents.FISHING_BOBBER_SPLASH) return

    val player = mc.player ?: return
    val level = mc.level ?: return
    val hook = resolveOwnedHook(player) ?: return
    if (hook.tickCount < MIN_SPLASH_HOOK_AGE_TICKS) return

    val dx = packet.x - hook.x
    val dy = packet.y - hook.y
    val dz = packet.z - hook.z
    val radiusSq = splashRadiusSetting.value * splashRadiusSetting.value
    if ((dx * dx) + (dy * dy) + (dz * dz) > radiusSq) return

    pendingReel = true
    nextActionTick = level.gameTime + reactionDelayTicksSetting.value.toLong()
  }

  private fun resolveRodSlot(player: Player): Int {
    val configured = rodSlotSetting.value.toInt() - 1
    if (configured in 0..8) {
      return if (isFishingRod(player, configured)) configured else -1
    }

    for (slot in 0..8) {
      if (isFishingRod(player, slot)) {
        return slot
      }
    }
    return -1
  }

  private fun isFishingRod(player: Player, slot: Int): Boolean {
    if (slot !in 0..8) return false
    val stack = player.inventory.getItem(slot)
    return !stack.isEmpty && stack.item is FishingRodItem
  }

  private fun resolveOwnedHook(player: Player): FishingHook? {
    val hook = player.fishing ?: return null
    if (!hook.isAlive) return null
    return if (hook.playerOwner?.uuid == player.uuid) hook else null
  }

  private fun useRod(slot: Int) {
    val player = mc.player ?: return
    if (slot !in 0..8) return
    if (!isFishingRod(player, slot)) return

    InventoryUtils.holdHotbarSlot(slot)
    (mc as? MinecraftAccessor)?.rightClick()
  }

  private fun stopMacro() {
    pendingReel = false
    nextActionTick = 0L
    lastMissingRodNoticeTick = -1L
    setState(State.IDLE)
  }

  private fun setState(next: State) {
    state = next
    stateText.value = when (next) {
      State.IDLE -> "Idle"
      State.CASTING -> "Casting"
      State.WAITING_BITE -> "Waiting Bite"
      State.REELING -> "Reeling"
      State.RECAST_DELAY -> "Recast Delay"
      State.MISSING_ROD -> "Missing Rod"
      State.PAUSED -> "Paused"
    }
  }

  private const val MIN_SPLASH_HOOK_AGE_TICKS = 8
  private const val MISSING_ROD_NOTICE_TICKS = 60L
}
