package org.cobalt.internal.fishing

import java.util.Locale
import kotlin.math.abs
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.FishingHook
import net.minecraft.world.item.FishingRodItem
import net.minecraft.world.phys.Vec3
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.PacketEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.ModuleCategory
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.module.setting.inGroup
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.rotation.strategy.BezierTrackingRotationStrategy
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.InventoryUtils
import org.cobalt.api.util.MouseUtils
import org.cobalt.api.util.helper.Rotation

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

  private val mc = Minecraft.getInstance()

  private val enabledSetting = CheckboxSetting(
    "Enabled",
    "Automatically casts, waits for a bite, reels, and recasts your fishing rod.",
    false,
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
      killSeaCreaturesSetting,
      weaponSlotSetting,
      reelSettleTicksSetting,
      weaponSwapDelayTicksSetting,
      targetSearchRangeSetting,
      attackRangeSetting,
      attackIntervalTicksSetting,
      killTimeoutTicksSetting,
      combatRotationSpeedSetting,
      targetKeywordsSetting,
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

    val player = mc.player ?: run { stopMacro(); return }
    val level = mc.level ?: run { stopMacro(); return }

    if (!wasEnabled) {
      rememberedWeaponSlot = player.inventory.selectedSlot
    }
    wasEnabled = true

    if (mc.screen != null) {
      RotationExecutor.stopIfUsing(combatRotationStrategy)
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

    val hook = resolveOwnedHook(player)

    if (pendingReel) {
      handlePendingReel(hook, rodSlot, level.gameTime)
      return
    }

    if (hook != null) {
      if (hook.tickCount >= hookTimeoutTicksSetting.value.toInt() && level.gameTime >= nextActionTick) {
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

    useRod(rodSlot)
    nextActionTick = level.gameTime + castSettleTicksSetting.value.toLong()
    setState(State.CASTING)
  }

  @SubscribeEvent
  fun onPacket(event: PacketEvent.Incoming) {
    if (!enabledSetting.value) return
    if (state != State.WAITING_BITE) return

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
    combatAnchorPos = hook.position()
    nextActionTick = level.gameTime + reactionDelayTicksSetting.value.toLong()
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

    RotationExecutor.rotateTo(AngleUtils.getRotation(target), combatRotationStrategy)

    if (gameTick < nextActionTick) {
      setState(State.SWITCHING_TO_WEAPON)
      return
    }

    killStartTick = gameTick
    lastAttackTick = gameTick - attackIntervalTicksSetting.value.toLong()
    setState(State.KILLING)
  }

  private fun handleKilling(player: Player, rodSlot: Int, gameTick: Long) {
    val target = findSeaCreatureTarget(player)
    if (target == null || gameTick - killStartTick >= killTimeoutTicksSetting.value.toLong()) {
      beginSwitchBack(rodSlot, gameTick)
      return
    }

    val rotation = AngleUtils.getRotation(target)
    RotationExecutor.rotateTo(rotation, combatRotationStrategy)

    val attackRangeSq = attackRangeSetting.value * attackRangeSetting.value
    val attackInterval = attackIntervalTicksSetting.value.toLong().coerceAtLeast(1L)
    if (
      player.distanceToSqr(target) <= attackRangeSq &&
      isAimedAt(rotation) &&
      gameTick - lastAttackTick >= attackInterval
    ) {
      MouseUtils.leftClick()
      lastAttackTick = gameTick
    }

    setState(State.KILLING)
  }

  private fun handleSwitchingBack(player: Player, rodSlot: Int, gameTick: Long) {
    RotationExecutor.stopIfUsing(combatRotationStrategy)

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
    RotationExecutor.rotateTo(AngleUtils.getRotation(target), combatRotationStrategy)
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
    nextActionTick = gameTick + weaponSwapDelayTicksSetting.value.toLong()
    setState(State.SWITCHING_BACK)
  }

  private fun startReeling(rodSlot: Int, gameTick: Long) {
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

  private fun findSeaCreatureTarget(player: Player): Mob? {
    val level = mc.level ?: return null
    val keywords = parseTargetKeywords(targetKeywordsSetting.value)
    val anchor = combatAnchorPos
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

    val displayName = normalizeText(mob.displayName.string)
    val typeName = normalizeText(mob.type.descriptionId)
    return keywords.any { keyword -> displayName.contains(keyword) || typeName.contains(keyword) }
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

  private fun useRod(slot: Int) {
    val player = mc.player ?: return
    if (slot !in 0..8) return
    if (!isFishingRod(player, slot)) return

    InventoryUtils.holdHotbarSlot(slot)
    MouseUtils.rightClick()
  }

  private fun stopMacro() {
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
  private const val MIN_SPLASH_HOOK_AGE_TICKS = 8
  private const val MISSING_ROD_NOTICE_TICKS = 60L
  private const val MISSING_WEAPON_NOTICE_TICKS = 60L
  private const val CLOSE_HOOK_FALLBACK_RANGE_SQ = 16.0
  private const val CLOSE_HOOK_NON_KEYWORD_PENALTY = 5.0
  private const val ATTACK_YAW_TOLERANCE = 14.0f
  private const val ATTACK_PITCH_TOLERANCE = 18.0f
  private const val DEFAULT_TARGET_KEYWORDS =
    "sea walker,sea guardian,sea witch,sea archer,rider of the deep,catfish,water hydra," +
      "phantom fisher,deep sea protector,nurse shark,blue shark,tiger shark,great white shark," +
      "thunder,lord jawbus,jawbus,squid,guardian,drowned,phantom,shark"
}
