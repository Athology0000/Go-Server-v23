package org.cobalt.internal.fishing

import java.awt.Color
import java.util.Locale
import kotlin.math.abs
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.FishingHook
import net.minecraft.world.item.FishingRodItem
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.PacketEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.event.impl.render.WorldRenderEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.KeyBindSetting
import org.cobalt.api.module.setting.impl.ModeSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.module.setting.inGroup
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.rotation.strategy.BezierTrackingRotationStrategy
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.InventoryUtils
import org.cobalt.api.util.MouseUtils
import org.cobalt.api.util.helper.KeyBind
import org.cobalt.api.util.helper.Rotation
import org.cobalt.api.util.render.Render3D

object FishingMacroModule : Module("Fishing Macro") {

  override val category = ModuleCategory.FARMING

  private enum class State {
    IDLE,
    CASTING,
    WAITING_BITE,
    REELING,
    RECAST_DELAY,
    SWITCHING_TO_WEAPON,
    KILLING,
    SWITCHING_BACK,
    MISSING_ROD,
    PAUSED,
  }

  private enum class CombatMethod(val label: String) {
    MELEE("Melee Left Click"),
    HYPERION("Hyperion Down Right Click"),
    SOUL_WHIP("Soul Whip / Flay Right Click"),
    HYPERION_DOWN("Hyperion Down Spam"),
  }

  private enum class FishingType(val label: String) {
    NORMAL("Normal"),
    BARN("Barn"),
    TROPHY("Trophy"),
    WORM("Worm"),
    STRIDERSURFER("Stridersurfer"),
  }

  private val mc = Minecraft.getInstance()

  private val enabledSetting = CheckboxSetting(
    "Enabled",
    "Automatically casts, waits for a bite, reels, and recasts your fishing rod.",
    false,
  )

  private val toggleFishingMacroSetting = KeyBindSetting(
    "Toggle Fishing Macro",
    "Key to toggle the fishing macro.",
    KeyBind(-1),
  )

  private val infoSetting = InfoSetting(
    "Fishing Macro",
    "Splash-based fishing loop with optional sea-creature combat after reel.",
    InfoType.INFO,
  )

  private val stateText = TextSetting(
    "State",
    "Current fishing macro state.",
    "Idle",
  )

  private val rodSlotSetting = SliderSetting(
    "Rod Slot",
    "Hotbar slot of the fishing rod (0 = auto-detect, 1-9 = fixed slot).",
    0.0,
    0.0,
    9.0,
    1.0,
  )

  private val fishingTypeSetting = ModeSetting(
    "Fishing Type",
    "Fishing behavior preset.",
    FishingType.NORMAL.ordinal,
    FishingType.entries.map { it.label }.toTypedArray(),
  )

  private val sneakWhileFishingSetting = CheckboxSetting(
    "Sneak While Fishing",
    "Hold sneak while the macro is casting or waiting on the bobber.",
    false,
  )

  private val catchGoldenFishSetting = CheckboxSetting(
    "Catch Golden Fish",
    "Enable golden fish helper controls for trophy fishing.",
    false,
  )

  private val goldenFishPredictionDistanceSetting = SliderSetting(
    "Golden Fish Prediction Distance",
    "Distance between predicted golden fish position and hook.",
    0.8,
    0.1,
    3.0,
    0.1,
  )

  private val minimumTrophyWaitTimeSetting = SliderSetting(
    "Minimum Wait Time",
    "Seconds before catching trophy fish.",
    0.0,
    0.0,
    20.0,
    0.5,
  ).inGroup(TROPHY_GROUP)

  private val reactionDelayTicksSetting = SliderSetting(
    "Reaction Delay",
    "Ticks to wait after the splash before reeling in.",
    3.0,
    0.0,
    12.0,
    1.0,
  )

  private val recastDelayTicksSetting = SliderSetting(
    "Recast Delay",
    "Ticks to wait after reeling before casting again.",
    12.0,
    2.0,
    40.0,
    1.0,
  )

  private val castSettleTicksSetting = SliderSetting(
    "Cast Settle",
    "Minimum ticks to wait after casting before checking for the bobber again.",
    8.0,
    2.0,
    20.0,
    1.0,
  )

  private val hookTimeoutTicksSetting = SliderSetting(
    "Hook Timeout",
    "Maximum ticks to leave the bobber out before reeling and recasting.",
    220.0,
    40.0,
    1200.0,
    5.0,
  )

  private val splashRadiusSetting = SliderSetting(
    "Splash Radius",
    "Maximum distance from your bobber for a splash packet to count as your bite.",
    2.2,
    0.5,
    6.0,
    0.1,
  )

  private val autoSelectRodSetting = CheckboxSetting(
    "Auto Select Rod",
    "Keep your fishing rod selected while the macro is active outside combat states.",
    true,
  )

  private val killSeaCreaturesSetting = CheckboxSetting(
    "Kill Sea Creatures",
    "After reeling, swap to a weapon and attack nearby sea creatures before recasting.",
    false,
  ).inGroup(COMBAT_GROUP)

  private val combatMethodSetting = ModeSetting(
    "Combat Method",
    "How the fishing macro attacks detected sea creatures.",
    CombatMethod.MELEE.ordinal,
    CombatMethod.entries.map { it.label }.toTypedArray(),
  ).inGroup(COMBAT_GROUP)

  private val weaponSlotSetting = SliderSetting(
    "Weapon Slot",
    "Hotbar slot of your weapon (0 = remember the last non-rod slot, 1-9 = fixed slot).",
    0.0,
    0.0,
    9.0,
    1.0,
  ).inGroup(COMBAT_GROUP)

  private val reelSettleTicksSetting = SliderSetting(
    "Reel Settle",
    "Ticks to wait after reeling before deciding whether to enter combat or recast.",
    4.0,
    0.0,
    12.0,
    1.0,
  ).inGroup(COMBAT_GROUP)

  private val weaponSwapDelayTicksSetting = SliderSetting(
    "Weapon Swap Delay",
    "Ticks to wait after switching to or from your weapon slot.",
    5.0,
    0.0,
    20.0,
    1.0,
  ).inGroup(COMBAT_GROUP)

  private val targetSearchRangeSetting = SliderSetting(
    "Target Search Range",
    "Maximum range around your hook or player to look for sea-creature targets.",
    12.0,
    4.0,
    24.0,
    0.5,
  ).inGroup(COMBAT_GROUP)

  private val attackRangeSetting = SliderSetting(
    "Attack Range",
    "Maximum distance to click attacks while fighting a sea creature.",
    4.3,
    2.0,
    6.0,
    0.1,
  ).inGroup(COMBAT_GROUP)

  private val attackIntervalTicksSetting = SliderSetting(
    "Attack Interval",
    "Ticks between left-click attacks while fighting.",
    2.0,
    1.0,
    10.0,
    1.0,
  ).inGroup(COMBAT_GROUP)

  private val killTimeoutTicksSetting = SliderSetting(
    "Kill Timeout",
    "Maximum ticks to stay in the kill phase before swapping back to the rod.",
    80.0,
    20.0,
    400.0,
    5.0,
  ).inGroup(COMBAT_GROUP)

  private val combatRotationSpeedSetting = SliderSetting(
    "Combat Rotation Speed",
    "Maximum yaw step used while turning toward a sea creature.",
    14.0,
    2.0,
    45.0,
    1.0,
  ).inGroup(COMBAT_GROUP)

  private val targetKeywordsSetting = TextSetting(
    "Target Keywords",
    "Comma-separated keywords used to identify sea creatures by name or entity type.",
    DEFAULT_TARGET_KEYWORDS,
  ).inGroup(COMBAT_GROUP)

  private val soulWhipArcSetting = CheckboxSetting(
    "Soul Whip Arc",
    "Render a Soul Whip-style guide arc to your bobber or current sea creature.",
    true,
  ).inGroup(COMBAT_GROUP)

  private val hyperionTargetKeywordsSetting = TextSetting(
    "Hyperion Targets",
    "Sea creature names that should always use Hyperion-style look-down right-click combat.",
    DEFAULT_HYPERION_TARGET_KEYWORDS,
  ).inGroup(COMBAT_GROUP)

  private val hyperionDownClicksSetting = SliderSetting(
    "Hyperion Down Clicks",
    "Total number of right-clicks to fire before switching back to the rod.",
    5.0,
    1.0,
    20.0,
    1.0,
  ).inGroup(COMBAT_GROUP)

  private val hyperionDownMinRangeSetting = SliderSetting(
    "Hyperion Down Min Range",
    "How close the target must be (blocks) before Hyperion Down starts clicking.",
    8.0,
    2.0,
    20.0,
    0.5,
  ).inGroup(COMBAT_GROUP)

  private val lockRotationSetting = CheckboxSetting(
    "Lock Rotation",
    "Lock camera to the cast direction while waiting for a bite, so the macro runs without needing focus.",
    true,
  ).inGroup(ROTATION_LOCK_GROUP)

  private val ungrabMouseSetting = CheckboxSetting(
    "Ungrab Mouse",
    "Free the OS cursor while rotation is locked so you can use other windows.",
    true,
  ).inGroup(ROTATION_LOCK_GROUP)

  private val showCastBoxSetting = CheckboxSetting(
    "Show Cast Box",
    "Render a small box at the water surface you aimed at when casting.",
    true,
  ).inGroup(ROTATION_LOCK_GROUP)

  private val showBobberBoxSetting = CheckboxSetting(
    "Show Bobber Box",
    "Render a small box around the bobber while waiting for a bite.",
    true,
  ).inGroup(ROTATION_LOCK_GROUP)

  private val combatRotationStrategy = BezierTrackingRotationStrategy(
    yawStepSampler = { combatRotationSpeedSetting.value.toFloat().coerceAtLeast(1f) },
    pitchStepSampler = { (combatRotationSpeedSetting.value * 0.78).toFloat().coerceAtLeast(1f) },
    curveIn = 0.18f,
    curveOut = 0.92f,
    minScale = 0.22f,
    snapThreshold = 0.35f,
  )

  private var wasEnabled = false
  private var state = State.IDLE
  private var nextActionTick = 0L
  private var pendingReel = false
  private var lastMissingRodNoticeTick = -1L
  private var lastMissingWeaponNoticeTick = -1L
  private var rememberedWeaponSlot = -1
  private var combatAnchorPos: Vec3? = null
  private var killStartTick = 0L
  private var lastAttackTick = Long.MIN_VALUE
  private var macroSneakHeld = false
  private var castTargetPos: Vec3? = null
  private var hyperionDownClicksRemaining = 0

  init {
    addSetting(
      enabledSetting,
      toggleFishingMacroSetting,
      infoSetting,
      stateText,
      rodSlotSetting,
      fishingTypeSetting,
      sneakWhileFishingSetting,
      catchGoldenFishSetting,
      goldenFishPredictionDistanceSetting,
      minimumTrophyWaitTimeSetting,
      reactionDelayTicksSetting,
      recastDelayTicksSetting,
      castSettleTicksSetting,
      hookTimeoutTicksSetting,
      splashRadiusSetting,
      autoSelectRodSetting,
      killSeaCreaturesSetting,
      combatMethodSetting,
      weaponSlotSetting,
      reelSettleTicksSetting,
      weaponSwapDelayTicksSetting,
      targetSearchRangeSetting,
      attackRangeSetting,
      attackIntervalTicksSetting,
      killTimeoutTicksSetting,
      combatRotationSpeedSetting,
      targetKeywordsSetting,
      soulWhipArcSetting,
      hyperionTargetKeywordsSetting,
      hyperionDownClicksSetting,
      hyperionDownMinRangeSetting,
      lockRotationSetting,
      ungrabMouseSetting,
      showCastBoxSetting,
      showBobberBoxSetting,
    )
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (toggleFishingMacroSetting.value.isPressed()) {
      enabledSetting.value = !enabledSetting.value
    }

    if (!enabledSetting.value) {
      if (wasEnabled) {
        stopMacro()
      }
      releaseMacroSneak()
      wasEnabled = false
      return
    }

    val player = mc.player ?: run { stopMacro(); return }
    val level = mc.level ?: run { stopMacro(); return }

    if (!wasEnabled) {
      rememberedWeaponSlot = player.inventory.selectedSlot
    }
    wasEnabled = true

    if (mc.screen != null) {
      RotationExecutor.stopIfUsing(combatRotationStrategy)
      releaseMacroSneak()
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
      releaseMacroSneak()
      return
    }
    lastMissingRodNoticeTick = -1L

    if (player.inventory.selectedSlot in 0..8 && player.inventory.selectedSlot != rodSlot) {
      rememberedWeaponSlot = player.inventory.selectedSlot
    }

    when (state) {
      State.REELING -> {
        handleReeling(player, rodSlot, level.gameTime)
        return
      }
      State.SWITCHING_TO_WEAPON -> {
        handleSwitchingToWeapon(player, rodSlot, level.gameTime)
        return
      }
      State.KILLING -> {
        handleKilling(player, rodSlot, level.gameTime)
        return
      }
      State.SWITCHING_BACK -> {
        handleSwitchingBack(player, rodSlot, level.gameTime)
        return
      }
      else -> Unit
    }

    if (autoSelectRodSetting.value && player.inventory.selectedSlot != rodSlot) {
      InventoryUtils.holdHotbarSlot(rodSlot)
    }
    updateMacroSneak()
    applyRotationLock()

    val hook = resolveOwnedHook(player)

    if (pendingReel) {
      handlePendingReel(hook, rodSlot, level.gameTime)
      return
    }

    if (hook != null) {
      if (hook.tickCount >= MIN_SPLASH_HOOK_AGE_TICKS && hasHookedFishMarker(hook)) {
        if (!canReelForFishingType(hook)) {
          setState(State.WAITING_BITE)
          return
        }
        combatAnchorPos = hook.position()
        startReeling(rodSlot, level.gameTime)
      } else if (hook.tickCount >= hookTimeoutTicksSetting.value.toInt() && level.gameTime >= nextActionTick) {
        combatAnchorPos = combatAnchorPos ?: hook.position()
        startReeling(rodSlot, level.gameTime)
      } else {
        setState(State.WAITING_BITE)
      }
      return
    }

    if (level.gameTime < nextActionTick) {
      setState(if (state == State.CASTING) State.CASTING else State.RECAST_DELAY)
      return
    }

    castTargetPos = computeCastTarget(player)
    enterRotationLock(player)
    useRod(rodSlot)
    nextActionTick = level.gameTime + castSettleTicksSetting.value.toLong()
    setState(State.CASTING)
  }

  @SubscribeEvent
  fun onPacket(event: PacketEvent.Incoming) {
    if (!enabledSetting.value) return
    if (state != State.WAITING_BITE && state != State.CASTING && state != State.RECAST_DELAY) return

    val packet = event.packet as? ClientboundSoundPacket ?: return
    if (packet.sound.value() != SoundEvents.FISHING_BOBBER_SPLASH) return

    val player = mc.player ?: return
    val hook = resolveOwnedHook(player) ?: return
    if (hook.tickCount < MIN_SPLASH_HOOK_AGE_TICKS) return
    if (!canReelForFishingType(hook)) return

    val dx = packet.x - hook.x
    val dy = packet.y - hook.y
    val dz = packet.z - hook.z
    val radiusSq = splashRadiusSetting.value * splashRadiusSetting.value
    if ((dx * dx) + (dy * dy) + (dz * dz) > radiusSq) return

    queueReel(hook, mc.level?.gameTime ?: return)
  }

  @SubscribeEvent
  fun onRender(event: WorldRenderEvent.Last) {
    if (!enabledSetting.value) return
    renderSoulWhipArc(event)
    renderCastBox(event)
    renderBobberBox(event)
  }

  private fun renderSoulWhipArc(event: WorldRenderEvent.Last) {
    if (!soulWhipArcSetting.value) return
    val player = mc.player ?: return
    val soulWhipArc = state == State.KILLING && combatMethodSetting.value == CombatMethod.SOUL_WHIP.ordinal
    val destination =
      if (soulWhipArc) {
        findSeaCreatureTarget(player)?.position()?.add(0.0, 0.65, 0.0)
      } else {
        resolveOwnedHook(player)?.position()?.add(0.0, 0.12, 0.0)
      } ?: return
    val start =
      if (soulWhipArc) {
        player.position().add(0.0, player.eyeHeight.toDouble() * 0.72, 0.0)
      } else {
        player.position().add(0.0, player.eyeHeight.toDouble() * 0.42, 0.0)
      }
    drawFishingArc(event, start, destination, soulWhipArc)
  }

  private fun renderCastBox(event: WorldRenderEvent.Last) {
    if (!showCastBoxSetting.value) return
    val pos = castTargetPos ?: return
    val box = AABB(
      pos.x - CAST_BOX_HALF, pos.y - CAST_BOX_HALF, pos.z - CAST_BOX_HALF,
      pos.x + CAST_BOX_HALF, pos.y + CAST_BOX_HALF, pos.z + CAST_BOX_HALF,
    )
    Render3D.drawStyledBox(event.context, box, CAST_BOX_COLOR, esp = true)
  }

  private fun renderBobberBox(event: WorldRenderEvent.Last) {
    if (!showBobberBoxSetting.value) return
    if (state != State.WAITING_BITE && state != State.CASTING) return
    val player = mc.player ?: return
    val pos = resolveOwnedHook(player)?.position() ?: return
    val box = AABB(
      pos.x - BOBBER_BOX_HALF, pos.y - BOBBER_BOX_HALF, pos.z - BOBBER_BOX_HALF,
      pos.x + BOBBER_BOX_HALF, pos.y + BOBBER_BOX_HALF, pos.z + BOBBER_BOX_HALF,
    )
    Render3D.drawStyledBox(event.context, box, BOBBER_BOX_COLOR, esp = true)
  }

  private fun queueReel(hook: FishingHook, gameTick: Long) {
    pendingReel = true
    combatAnchorPos = hook.position()
    nextActionTick = gameTick + reactionDelayTicksSetting.value.toLong()
  }

  private fun handlePendingReel(hook: FishingHook?, rodSlot: Int, gameTick: Long) {
    if (hook == null) {
      pendingReel = false
      scheduleRecast(gameTick)
      return
    }

    if (gameTick < nextActionTick) {
      setState(State.WAITING_BITE)
      return
    }

    combatAnchorPos = combatAnchorPos ?: hook.position()
    startReeling(rodSlot, gameTick)
  }

  private fun handleReeling(player: Player, rodSlot: Int, gameTick: Long) {
    if (gameTick < nextActionTick) {
      setState(State.REELING)
      return
    }

    if (tryStartCombat(player, rodSlot, gameTick)) {
      return
    }

    scheduleRecast(gameTick)
  }

  private fun handleSwitchingToWeapon(player: Player, rodSlot: Int, gameTick: Long) {
    val target = findSeaCreatureTarget(player)
    if (target == null) {
      beginSwitchBack(rodSlot, gameTick)
      return
    }

    val weaponSlot = resolveWeaponSlot(player, rodSlot)
    if (weaponSlot !in 0..8) {
      warnMissingWeapon(gameTick)
      beginSwitchBack(rodSlot, gameTick)
      return
    }

    if (player.inventory.selectedSlot != weaponSlot) {
      InventoryUtils.holdHotbarSlot(weaponSlot)
    }

    RotationExecutor.rotateTo(combatAimRotation(player, target, combatMethod(target)), combatRotationStrategy)

    if (gameTick < nextActionTick) {
      setState(State.SWITCHING_TO_WEAPON)
      return
    }

    killStartTick = gameTick
    lastAttackTick = gameTick - attackIntervalTicksSetting.value.toLong()
    if (combatMethod() == CombatMethod.HYPERION_DOWN) {
      hyperionDownClicksRemaining = hyperionDownClicksSetting.value.toInt()
    }
    setState(State.KILLING)
  }

  private fun handleKilling(player: Player, rodSlot: Int, gameTick: Long) {
    if (combatMethod() == CombatMethod.HYPERION_DOWN) {
      tickHyperionDown(player, rodSlot, gameTick)
      return
    }

    val target = findSeaCreatureTarget(player)
    if (target == null || gameTick - killStartTick >= killTimeoutTicksSetting.value.toLong()) {
      beginSwitchBack(rodSlot, gameTick)
      return
    }

    val method = combatMethod(target)
    val rotation = combatAimRotation(player, target, method)
    RotationExecutor.rotateTo(rotation, combatRotationStrategy)

    val attackInterval = attackIntervalTicksSetting.value.toLong().coerceAtLeast(1L)
    if (isAimedAt(rotation) && gameTick - lastAttackTick >= attackInterval) {
      when (method) {
        CombatMethod.MELEE -> {
          val attackRangeSq = attackRangeSetting.value * attackRangeSetting.value
          if (player.distanceToSqr(target) <= attackRangeSq) {
            MouseUtils.leftClick()
            lastAttackTick = gameTick
          }
        }
        CombatMethod.SOUL_WHIP -> {
          val dx = target.x - player.x
          val dz = target.z - player.z
          val verticalDiff = target.y - player.y
          if (dx * dx + dz * dz <= SOUL_WHIP_HORIZONTAL_RANGE_SQ && verticalDiff <= SOUL_WHIP_VERTICAL_RANGE) {
            MouseUtils.rightClick()
            lastAttackTick = gameTick
          }
        }
        CombatMethod.HYPERION,
        CombatMethod.HYPERION_DOWN -> {
          MouseUtils.rightClick()
          lastAttackTick = gameTick
        }
      }
    }

    setState(State.KILLING)
  }

  private fun tickHyperionDown(player: Player, rodSlot: Int, gameTick: Long) {
    val target = findSeaCreatureTarget(player, anchor = null)
    if (target == null || gameTick - killStartTick >= killTimeoutTicksSetting.value.toLong()) {
      beginSwitchBack(rodSlot, gameTick)
      return
    }

    RotationExecutor.rotateTo(Rotation(player.yRot, HYPERION_PITCH), combatRotationStrategy)

    val minRangeSq = hyperionDownMinRangeSetting.value * hyperionDownMinRangeSetting.value
    if (player.distanceToSqr(target) > minRangeSq) {
      setState(State.KILLING)
      return
    }

    if (hyperionDownClicksRemaining > 0 && gameTick > lastAttackTick) {
      MouseUtils.rightClick()
      lastAttackTick = gameTick
      hyperionDownClicksRemaining--
    }

    if (hyperionDownClicksRemaining <= 0) {
      beginSwitchBack(rodSlot, gameTick)
      return
    }

    setState(State.KILLING)
  }

  private fun handleSwitchingBack(player: Player, rodSlot: Int, gameTick: Long) {
    RotationExecutor.stopIfUsing(combatRotationStrategy)
    applyRotationLock()

    if (player.inventory.selectedSlot != rodSlot) {
      InventoryUtils.holdHotbarSlot(rodSlot)
    }

    if (gameTick < nextActionTick) {
      setState(State.SWITCHING_BACK)
      return
    }

    scheduleRecast(gameTick)
  }

  private fun tryStartCombat(player: Player, rodSlot: Int, gameTick: Long): Boolean {
    if (!killSeaCreaturesSetting.value) {
      combatAnchorPos = null
      return false
    }

    val target = findSeaCreatureTarget(player) ?: run {
      combatAnchorPos = null
      return false
    }

    val weaponSlot = resolveWeaponSlot(player, rodSlot)
    if (weaponSlot !in 0..8) {
      warnMissingWeapon(gameTick)
      combatAnchorPos = null
      return false
    }

    InventoryUtils.holdHotbarSlot(weaponSlot)
    RotationExecutor.rotateTo(combatAimRotation(player, target, combatMethod(target)), combatRotationStrategy)
    nextActionTick = gameTick + weaponSwapDelayTicksSetting.value.toLong()
    setState(State.SWITCHING_TO_WEAPON)
    return true
  }

  private fun beginSwitchBack(rodSlot: Int, gameTick: Long) {
    RotationExecutor.stopIfUsing(combatRotationStrategy)
    InventoryUtils.holdHotbarSlot(rodSlot)
    combatAnchorPos = null
    killStartTick = 0L
    lastAttackTick = Long.MIN_VALUE
    hyperionDownClicksRemaining = 0
    nextActionTick = gameTick + weaponSwapDelayTicksSetting.value.toLong()
    setState(State.SWITCHING_BACK)
  }

  private fun startReeling(rodSlot: Int, gameTick: Long) {
    releaseMacroSneak()
    useRod(rodSlot)
    pendingReel = false
    nextActionTick = gameTick + reelSettleTicksSetting.value.toLong()
    setState(State.REELING)
  }

  private fun scheduleRecast(gameTick: Long) {
    RotationExecutor.stopIfUsing(combatRotationStrategy)
    combatAnchorPos = null
    killStartTick = 0L
    lastAttackTick = Long.MIN_VALUE
    nextActionTick = gameTick + recastDelayTicksSetting.value.toLong()
    setState(State.RECAST_DELAY)
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

  private fun resolveWeaponSlot(player: Player, rodSlot: Int): Int {
    val configured = weaponSlotSetting.value.toInt() - 1
    if (configured in 0..8) {
      return if (configured != rodSlot && !player.inventory.getItem(configured).isEmpty) configured else -1
    }

    if (rememberedWeaponSlot in 0..8 && rememberedWeaponSlot != rodSlot && !player.inventory.getItem(rememberedWeaponSlot).isEmpty) {
      return rememberedWeaponSlot
    }

    val selected = player.inventory.selectedSlot
    return if (selected in 0..8 && selected != rodSlot && !player.inventory.getItem(selected).isEmpty) selected else -1
  }

  private fun findSeaCreatureTarget(player: Player, anchor: Vec3? = combatAnchorPos): Mob? {
    val level = mc.level ?: return null
    val keywords = parseTargetKeywords(targetKeywordsSetting.value)
    val searchRangeSq = targetSearchRangeSetting.value * targetSearchRangeSetting.value

    return level.entitiesForRendering()
      .asSequence()
      .mapNotNull { it as? Mob }
      .filter { mob ->
        if (!mob.isAlive || mob.isInvisible) {
          return@filter false
        }

        val playerDistanceSq = player.distanceToSqr(mob)
        val anchorDistanceSq = anchor?.let { mob.position().distanceToSqr(it) } ?: Double.MAX_VALUE
        if (playerDistanceSq > searchRangeSq && anchorDistanceSq > searchRangeSq) {
          return@filter false
        }

        val matchesKeyword = matchesTargetKeyword(mob, keywords)
        matchesKeyword || anchorDistanceSq <= CLOSE_HOOK_FALLBACK_RANGE_SQ
      }
      .minByOrNull { mob ->
        val playerDistanceSq = player.distanceToSqr(mob)
        val anchorDistanceSq = anchor?.let { mob.position().distanceToSqr(it) } ?: playerDistanceSq
        val anchorBias = if (matchesTargetKeyword(mob, keywords)) 0.0 else CLOSE_HOOK_NON_KEYWORD_PENALTY
        (anchorDistanceSq * 0.65) + (playerDistanceSq * 0.35) + anchorBias
      }
  }

  private fun matchesTargetKeyword(mob: Mob, keywords: List<String>): Boolean {
    if (keywords.isEmpty()) return true

    val names = targetMatchNames(mob)
    return keywords.any { keyword -> names.any { name -> name.contains(keyword) } }
  }

  private fun isHyperionTarget(mob: Mob): Boolean {
    val keywords = parseTargetKeywords(hyperionTargetKeywordsSetting.value)
    if (keywords.isEmpty()) return false

    val names = targetMatchNames(mob)
    return keywords.any { keyword -> names.any { name -> name.contains(keyword) } }
  }

  private fun targetMatchNames(mob: Mob): List<String> {
    val names = ArrayList<String>(4)
    names += normalizeText(mob.displayName.string)
    names += normalizeText(mob.type.descriptionId)

    val level = mc.level ?: return names
    level.entitiesForRendering()
      .asSequence()
      .mapNotNull { it as? ArmorStand }
      .filter { stand -> stand.isAlive && stand.position().distanceToSqr(mob.position()) <= TARGET_LABEL_RANGE_SQ }
      .map { stand -> stand.customName?.string ?: stand.name.string }
      .mapTo(names) { normalizeText(it) }

    return names.distinct()
  }

  private fun parseTargetKeywords(raw: String): List<String> {
    return raw.split(',')
      .map { normalizeText(it) }
      .filter { it.isNotBlank() }
      .distinct()
  }

  private fun normalizeText(text: String): String {
    return (ChatFormatting.stripFormatting(text) ?: text)
      .lowercase(Locale.US)
      .trim()
  }

  private fun isAimedAt(rotation: Rotation): Boolean {
    val player = mc.player ?: return false
    val yawDelta = abs(AngleUtils.getRotationDelta(player.yRot, rotation.yaw))
    val pitchDelta = abs(rotation.pitch - player.xRot)
    return yawDelta <= ATTACK_YAW_TOLERANCE && pitchDelta <= ATTACK_PITCH_TOLERANCE
  }

  private fun combatAimRotation(player: Player, target: Mob, method: CombatMethod): Rotation {
    return when (method) {
      CombatMethod.HYPERION,
      CombatMethod.HYPERION_DOWN -> Rotation(player.yRot, HYPERION_PITCH)
      CombatMethod.MELEE,
      CombatMethod.SOUL_WHIP -> AngleUtils.getRotation(target)
    }
  }

  private fun combatMethod(target: Mob? = null): CombatMethod {
    val base = CombatMethod.entries.getOrElse(combatMethodSetting.value) { CombatMethod.MELEE }
    if (base == CombatMethod.HYPERION_DOWN) return base
    if (target != null && isHyperionTarget(target)) return CombatMethod.HYPERION
    return base
  }

  private fun canReelForFishingType(hook: FishingHook): Boolean {
    if (fishingType() != FishingType.TROPHY) return true
    val minimumTicks = (minimumTrophyWaitTimeSetting.value * 20.0).toInt()
    return hook.tickCount >= minimumTicks
  }

  private fun fishingType(): FishingType {
    return FishingType.entries.getOrElse(fishingTypeSetting.value) { FishingType.NORMAL }
  }

  private fun updateMacroSneak() {
    val shouldSneak =
      sneakWhileFishingSetting.value &&
        state != State.REELING &&
        state != State.SWITCHING_TO_WEAPON &&
        state != State.KILLING &&
        state != State.SWITCHING_BACK

    if (shouldSneak) {
      mc.options.keyShift.setDown(true)
      macroSneakHeld = true
    } else {
      releaseMacroSneak()
    }
  }

  private fun releaseMacroSneak() {
    if (!macroSneakHeld) return
    mc.options.keyShift.setDown(false)
    macroSneakHeld = false
  }

  private fun drawFishingArc(event: WorldRenderEvent.Last, start: Vec3, end: Vec3, soulWhipArc: Boolean) {
    val midpoint = start.add(end).scale(0.5).add(0.0, arcHeight(start, end, soulWhipArc), 0.0)
    var previous = start
    val segments = if (soulWhipArc) SOUL_WHIP_ARC_SEGMENTS else BOBBER_ARC_SEGMENTS
    val color = if (soulWhipArc) SOUL_WHIP_ARC_COLOR else BOBBER_ARC_COLOR
    val thickness = if (soulWhipArc) SOUL_WHIP_ARC_THICKNESS else BOBBER_ARC_THICKNESS

    for (step in 1..segments) {
      val t = step.toDouble() / segments
      val point = quadraticBezier(start, midpoint, end, t)
      Render3D.drawLine(event.context, previous, point, color, esp = true, thickness = thickness)
      previous = point
    }
  }

  private fun quadraticBezier(start: Vec3, control: Vec3, end: Vec3, t: Double): Vec3 {
    val inverse = 1.0 - t
    return start.scale(inverse * inverse)
      .add(control.scale(2.0 * inverse * t))
      .add(end.scale(t * t))
  }

  private fun arcHeight(start: Vec3, end: Vec3, soulWhipArc: Boolean): Double {
    val distance = start.distanceTo(end)
    return if (soulWhipArc) {
      (distance * 0.28).coerceIn(1.2, 5.0)
    } else {
      (distance * 0.08).coerceIn(0.15, 1.1)
    }
  }

  private fun warnMissingWeapon(gameTick: Long) {
    if (lastMissingWeaponNoticeTick >= 0L && gameTick - lastMissingWeaponNoticeTick < MISSING_WEAPON_NOTICE_TICKS) {
      return
    }
    ChatUtils.sendMessage("Fishing Macro combat needs a valid non-rod weapon slot.")
    lastMissingWeaponNoticeTick = gameTick
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

  private fun hasHookedFishMarker(hook: FishingHook): Boolean {
    val level = mc.level ?: return false
    return level.entitiesForRendering().any { entity ->
      val stand = entity as? ArmorStand ?: return@any false
      if (!stand.isAlive) return@any false

      val rawName = stand.customName?.string ?: stand.name.string
      val name = ChatFormatting.stripFormatting(rawName)?.trim() ?: rawName.trim()
      name == HOOKED_FISH_MARKER_NAME &&
        stand.position().distanceToSqr(hook.position()) <= HOOKED_FISH_MARKER_RANGE_SQ
    }
  }

  private fun useRod(slot: Int) {
    val player = mc.player ?: return
    if (slot !in 0..8) return
    if (!isFishingRod(player, slot)) return

    InventoryUtils.holdHotbarSlot(slot)
    MouseUtils.rightClick()
  }

  private fun computeCastTarget(player: Player): Vec3? {
    val level = mc.level ?: return null
    val eye = player.eyePosition
    val end = eye.add(player.lookAngle.scale(CAST_RAYCAST_RANGE))
    val result = level.clip(ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, player))
    return if (result.type == HitResult.Type.BLOCK) result.location else null
  }

  private fun enterRotationLock(player: Player) {
    if (!lockRotationSetting.value) return
    FishingRotationLock.lock(player.yRot, player.xRot)
    if (ungrabMouseSetting.value) MouseUtils.ungrabMouse()
  }

  private fun exitRotationLock() {
    FishingRotationLock.unlock()
    MouseUtils.grabMouse()
  }

  private fun applyRotationLock() {
    if (!lockRotationSetting.value) return
    FishingRotationLock.applyLock()
  }

  private fun stopMacro() {
    releaseMacroSneak()
    exitRotationLock()
    castTargetPos = null
    pendingReel = false
    nextActionTick = 0L
    lastMissingRodNoticeTick = -1L
    lastMissingWeaponNoticeTick = -1L
    rememberedWeaponSlot = -1
    combatAnchorPos = null
    killStartTick = 0L
    lastAttackTick = Long.MIN_VALUE
    RotationExecutor.stopIfUsing(combatRotationStrategy)
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
      State.SWITCHING_TO_WEAPON -> "Switch Weapon"
      State.KILLING -> "Killing"
      State.SWITCHING_BACK -> "Switch Rod"
      State.MISSING_ROD -> "Missing Rod"
      State.PAUSED -> "Paused"
    }
  }

  private const val COMBAT_GROUP = "Combat"
  private const val TROPHY_GROUP = "Trophy Fishing"
  private const val ROTATION_LOCK_GROUP = "Rotation Lock"
  private const val MIN_SPLASH_HOOK_AGE_TICKS = 8
  private const val MISSING_ROD_NOTICE_TICKS = 60L
  private const val MISSING_WEAPON_NOTICE_TICKS = 60L
  private const val HOOKED_FISH_MARKER_NAME = "!!!"
  private const val HOOKED_FISH_MARKER_RANGE_SQ = 2.25
  private const val TARGET_LABEL_RANGE_SQ = 9.0
  private const val CLOSE_HOOK_FALLBACK_RANGE_SQ = 16.0
  private const val CLOSE_HOOK_NON_KEYWORD_PENALTY = 5.0
  private const val HYPERION_PITCH = 90.0f
  private const val ATTACK_YAW_TOLERANCE = 14.0f
  private const val ATTACK_PITCH_TOLERANCE = 18.0f
  private const val SOUL_WHIP_ARC_SEGMENTS = 28
  private const val SOUL_WHIP_ARC_THICKNESS = 2.5f
  private val SOUL_WHIP_ARC_COLOR = Color(255, 0, 210, 230)
  private const val BOBBER_ARC_SEGMENTS = 18
  private const val BOBBER_ARC_THICKNESS = 1.15f
  private val BOBBER_ARC_COLOR = Color(235, 235, 255, 185)
  private const val DEFAULT_TARGET_KEYWORDS =
    "sea walker,sea guardian,sea witch,sea archer,rider of the deep,catfish,water hydra," +
      "phantom fisher,deep sea protector,nurse shark,blue shark,tiger shark,great white shark," +
      "thunder,lord jawbus,jawbus,squid,guardian,drowned,phantom,shark," +
      "moogma,magma slug,lava leech"
  private const val DEFAULT_HYPERION_TARGET_KEYWORDS = "moogma,magma slug,lava leech"
  private const val CAST_RAYCAST_RANGE = 30.0
  private const val CAST_BOX_HALF = 0.15
  private const val BOBBER_BOX_HALF = 0.2
  private val CAST_BOX_COLOR = Color(0, 220, 220, 200)
  private val BOBBER_BOX_COLOR = Color(235, 235, 255, 200)
  private const val SOUL_WHIP_HORIZONTAL_RANGE_SQ = 13.0 * 13.0
  private const val SOUL_WHIP_VERTICAL_RANGE = 2.0
}
