package org.cobalt.internal.combat

import java.util.UUID
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import java.awt.Color
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.scores.DisplaySlot
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.player.Player
import net.minecraft.client.player.LocalPlayer
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.ChatEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.event.impl.render.WorldRenderEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.inGroup
import org.cobalt.api.module.setting.impl.ActionSetting
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.ModeSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.api.notification.NotificationManager
import org.cobalt.api.rotation.EasingType
import org.cobalt.api.rotation.RotationExecutor
import org.cobalt.api.rotation.strategy.BezierTrackingRotationStrategy
import org.cobalt.api.rotation.strategy.TimedEaseStrategy
import org.cobalt.api.util.AngleUtils
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.InventoryUtils
import org.cobalt.api.util.MouseUtils
import org.cobalt.api.util.getLoreLines
import org.cobalt.api.util.helper.Rotation
import org.cobalt.api.util.render.Render3D
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules
import org.cobalt.internal.helper.Config
import org.cobalt.api.pathfinder.jni.MovementProfile
import org.cobalt.api.pathfinder.jni.NativePathfinder
import org.cobalt.api.pathfinder.jni.PathStatus
import org.cobalt.api.util.player.MovementManager
import org.cobalt.internal.pathfinding.PathfindingModule
import org.cobalt.internal.helper.WalkbackBridge
import org.cobalt.internal.rotation.RotationsModule

object CombatMacroModule : Module("Combat Macro") {

  private val mc: Minecraft = Minecraft.getInstance()
  private val rotationStrategy = BezierTrackingRotationStrategy(
    yawStepSampler = { (RotationsModule.sample(RotationsModule.combatYawStep.value) * COMBAT_ROTATION_STEP_SCALE).toFloat() },
    pitchStepSampler = { (RotationsModule.sample(RotationsModule.combatPitchStep.value) * COMBAT_ROTATION_STEP_SCALE).toFloat() },
    curveInProvider = { RotationsModule.bezierCurveIn.value.toFloat() },
    curveOutProvider = { RotationsModule.bezierCurveOut.value.toFloat() },
    minScaleProvider = { RotationsModule.bezierMinScale.value.toFloat() },
    snapThresholdProvider = { RotationsModule.bezierSnapThreshold.value.toFloat() },
  )
  private val builtInBlacklistedNames = setOf(
    "blacksmith",
    "click",
    "knifethrower",
    "toolsmith",
    "gimley",
    "hornum",
    "brynmor",
    "forge foreman",
    "fred",
    "sargwyn",
    "tarwen",
    "pdp2ut6lwf",
    "95j67t0hlr",
    "u07nk5sfh6",
    "12r7qltkkd",
    "lumina",
    "armor stand",
    "gjthi54bdc",
    "fragilis",
    "m68691nsos",
    "gemstone grinder",
    "geo",
    "4ud6865lsx",
    "crystal hollows",
    "gwendolyn",
    "emissary braum",
    "8s3f5ek78w",
    "lqf888h37w",
    "thormyr",
    "emmor",
    "08481rzc37",
    "5h260k1i05",
    "grandan",
    "5m965eru09",
    "emkam",
    "queen mismyla",
    "75576yv62m",
    "v4v8vjd7g8",
    "erren",
    "redros",
    "v7245ab06o",
    "zto57x66p5",
    "tornora",
    "leg1xn0us7",
    "castle guard",
    "i2ztjr35j6",
    "j4bjv2gi6i",
    "465awyh20w",
    "42toq9l273",
    "ticket master",
    "witches stew",
    "alcina",
    "witch",
    "6en215sve0",
    "bylma",
    "4yd3iv3sk7",
    "marigold",
    "gold essence shop",
    "l15l66bsyx",
    "emissary wilson",
    "3nba7e0ea6",
    "rat"
  )

  private val enabled = CheckboxSetting(
    "Enabled",
    "Pathfind to a target and attack until stopped.",
    false
  )

  private val info = InfoSetting(
    "Target",
    "Set a target name (partial match). Leave blank to target nearest mob.",
    InfoType.INFO
  )

  private val targetName = TextSetting(
    "Target Name",
    "Entity name to target (partial match).",
    ""
  )

  private val cryptZombieSlayer = CheckboxSetting(
    "Crypt Zombie Slayer",
    "Crypt Slayer flow: farm zombies/ghouls until Slayer boss is detected in tab, then focus boss.",
    false
  )

  private val slayerStatus = TextSetting(
    "Slayer Status",
    "Current Crypt Slayer state.",
    "Off"
  )

  private val slayerType = ModeSetting(
    "Slayer Type",
    "Which slayer quest type to run.",
    0,
    arrayOf("Zombie", "Wolf", "Spider", "Enderman", "Vampire", "Blaze")
  )

  private val slayerTier = SliderSetting(
    "Slayer Tier",
    "Quest tier to buy (1 = cheapest, 5 = hardest).",
    1.0,
    1.0,
    5.0,
    step = 1.0
  )

  private val slayerAutoWarp = CheckboxSetting(
    "Auto Warp",
    "Warp to the farming area after buying a new quest.",
    true
  )

  private val slayerLocation = ModeSetting(
    "Location",
    "Farming location for zombie slayer.",
    0,
    arrayOf("Zombie Graveyard", "Zombie Crypt")
  )

  private val slayerBossNotification = CheckboxSetting(
    "Boss Spawn Notification",
    "Show a HUD warning when the slayer boss spawns.",
    true
  )

  private val slayerBossEsp = CheckboxSetting(
    "Boss / MiniBoss ESP",
    "Highlight slayer bosses and minibosses while the slayer macro is active.",
    true
  )

  private val slayerEspTargets = ModeSetting(
    "ESP Targets",
    "Choose whether the slayer ESP highlights bosses, minibosses, or both.",
    2,
    arrayOf("Boss", "MiniBoss", "Both")
  )

  private val slayerWalkbackRoute = TextSetting(
    "Walkback Route",
    "Route name to follow back to farming area after boss kill (if Auto Warp is off). Leave blank to skip.",
    ""
  )

  // -- Patrol settings (mirror of Combat Patrol module, settable from slayer tab) ---------------
  private val patrolRouteNameProxy = TextSetting(
    "Patrol Route",
    "Patrol route name. Shared with the Combat Patrol module.",
    CombatPatrolModule.routeName.value
  )
  private val patrolLoadAction = ActionSetting("Load Patrol Route", "Load the patrol route by name.", "Load") {
    CombatPatrolModule.routeName.value = patrolRouteNameProxy.value
    CombatPatrolModule.loadRoute()
  }
  private val patrolSaveAction = ActionSetting("Save Patrol Route", "Save the current patrol route.", "Save") {
    CombatPatrolModule.routeName.value = patrolRouteNameProxy.value
    CombatPatrolModule.saveRoute()
  }
  private val patrolStartAction = ActionSetting("Start Patrol", "Start patrol immediately.", "Start") {
    CombatPatrolModule.routeName.value = patrolRouteNameProxy.value
    CombatPatrolModule.startPatrol()
  }
  private val patrolStopAction = ActionSetting("Stop Patrol", "Stop the running patrol.", "Stop") {
    CombatPatrolModule.stopPatrol()
  }

  private val searchRange = SliderSetting(
    "Search Range",
    "Max distance to search for targets.",
    32.0,
    8.0,
    96.0
  )

  private val minCps = SliderSetting(
    "Min CPS",
    "Minimum clicks per second.",
    6.0,
    1.0,
    20.0
  )

  private val maxCps = SliderSetting(
    "Max CPS",
    "Maximum clicks per second.",
    10.0,
    1.0,
    20.0
  )

  private val attackRange = SliderSetting(
    "Attack Range",
    "Distance to start attacking.",
    3.2,
    2.0,
    6.0
  )

  private val chaseStopBuffer = SliderSetting(
    "Chase Stop Buffer",
    "Stop pathing this far before attack range to reduce strafe jitter.",
    1.1,
    0.0,
    3.0
  )

  private val stayNearStart = CheckboxSetting(
    "Stay Near Start",
    "Keep combat inside the general area where macro was enabled.",
    true
  )

  private val startAreaRadius = SliderSetting(
    "Start Area Radius",
    "Max horizontal distance from start area for combat targets.",
    24.0,
    8.0,
    96.0
  )

  private val autoHeal = CheckboxSetting(
    "Auto Heal",
    "Auto-use a healing item when health is low.",
    true
  )

  private val autoWandOfAtonement = CheckboxSetting(
    "Wand Of Atonement",
    "Use Wand of Atonement for auto-heal. Auto-enables when the item is detected in hotbar.",
    false
  )

  private val autoZombieSword = CheckboxSetting(
    "Zombie Sword",
    "Use Zombie Sword variants for auto-heal. Auto-enables when the item is detected in hotbar.",
    false
  )

  private val autoOverflux = CheckboxSetting(
    "Overflux",
    "Use Overflux when the Slayer boss spawns. Auto-enables when the item is detected in hotbar.",
    false
  )

  private val autoRagnarok = CheckboxSetting(
    "Ragnarok",
    "Use Ragnarok when the Slayer boss spawns. Auto-enables when the item is detected in hotbar.",
    false
  )

  private val oneTapMode = CheckboxSetting(
    "One Tap Mode",
    "Keep moving through mobs - don't stop at each kill. Switch immediately when target dies.",
    false
  )

  private val combatMode = ModeSetting(
    "Combat Mode",
    "Melee: left-click. Bow: right-click ranged with drop aim. Mage: right-click projectile with lead aim.",
    0,
    arrayOf("Melee", "Bow", "Mage")
  )

  private val zombieDynamicCombat = CheckboxSetting(
    "Zombie Auto Swap",
    "Zombie Slayer only: use the spawn weapon while farming, then swap to Dynamic Sword for the boss.",
    true
  )

  private val zombieSpawnWeapon = TextSetting(
    "Zombie Spawn Weapon",
    "Comma-separated hotbar keywords for the zombie spawn weapon. Used before the boss spawns.",
    "halberd"
  )

  private val zombieDynamicSword = TextSetting(
    "Dynamic Sword",
    "Comma-separated hotbar keywords for the zombie boss weapon.",
    "falchion, reaper, shredded, sword"
  )

  private val endermanDynamicCombat = CheckboxSetting(
    "Eman Auto Swap",
    "Enderman Slayer only: use bow for spawn farming and hit-shield phases, then swap to melee for boss DPS phases.",
    true
  )

  private val emanSpawnWeapon = ModeSetting(
    "Eman Spawn Weapon",
    "Choose the weapon style for Enderman Slayer spawn farming before the boss appears.",
    3,
    arrayOf("Terminator", "Shortbow", "Enderman Slayer Sword", "Dynamic")
  )

  private val emanDynamicSwapRange = SliderSetting(
    "Eman Dynamic Swap Range",
    "Enderman Slayer only: in Dynamic spawn mode, switch from bow to sword when the target is within this distance.",
    4.5,
    2.0,
    8.0,
    step = 0.1
  )

  private val emanHitPhaseWeapon = TextSetting(
    "Eman Hit Phase Weapon",
    "Comma-separated hotbar keywords for Enderman Slayer hit-shield phases.",
    "terminator, juju, shortbow, bow"
  )

  private val emanBossWeapon = TextSetting(
    "Eman Boss Weapon",
    "Comma-separated hotbar keywords for Enderman Slayer boss DPS phases.",
    "atomsplit, vorpal, voidedge, katana"
  )

  private val spiderPhaseBowCombat = CheckboxSetting(
    "Spider Hatchling Phase",
    "Spider Slayer only: when the boss becomes invulnerable, step back and use bow mode on nearby hatchlings.",
    true
  )

  private val wolfIgnorePups = CheckboxSetting(
    "Ignore Wolf Pups",
    "Wolf Slayer only: during the pups phase, keep tracking the boss instead of retargeting the pups.",
    false
  )

  private val swordKeepDistance = SliderSetting(
    "Sword Keep Distance",
    "Preferred stand-off distance in melee mode. Lower = closer. 0 = close in fully.",
    1.8,
    0.0,
    6.0,
    step = 0.1
  )

  private val bowMinRange = SliderSetting(
    "Bow Min Range",
    "Preferred stand-off distance in bow mode. 0 = use Attack Range.",
    8.0,
    0.0,
    20.0
  )

  private val bowElevationPerBlock = SliderSetting(
    "Bow Elevation /block",
    "Degrees to aim upward per block of distance to compensate for arrow drop. Tune to your bow.",
    0.15,
    0.0,
    1.0,
    step = 0.01
  )

  private val mageElevationPerBlock = SliderSetting(
    "Mage Elevation /block",
    "Degrees to aim upward per block of distance for slower projectiles. 0 for instant/no-drop weapons.",
    0.0,
    0.0,
    1.0,
    step = 0.01
  )

  private val autoSwordOfBadHealth = CheckboxSetting(
    "Sword Of Bad Health",
    "Use Sword of Bad Health during the Slayer boss fight. Auto-enables when the item is detected in hotbar.",
    false
  )

  private val healAtHealth = SliderSetting(
    "Heal At Health",
    "Use heal item at or below this health.",
    10.0,
    1.0,
    20.0
  )

  private val stuckTicksSetting = SliderSetting(
    "Stuck Ticks",
    "Ticks without movement before warp hub.",
    80.0,
    20.0,
    200.0
  )

  private val warpOnStuck = CheckboxSetting(
    "Warp Hub On Stuck",
    "Warp to hub if stuck while macro is active.",
    true
  )

  private val requireLos = CheckboxSetting(
    "Require LOS",
    "Only attack when you have line of sight.",
    true
  )


  private val aimTolerance = SliderSetting(
    "Aim Tolerance",
    "Max yaw/pitch error before attacking.",
    15.0,
    4.0,
    45.0
  )

  private val minAttackCooldown = SliderSetting(
    "Min Attack Cooldown",
    "Minimum vanilla cooldown needed before an attack.",
    0.2,
    0.0,
    1.0
  )

  private val stuckRepathTries = SliderSetting(
    "Stuck Repath Tries",
    "Repath attempts before optional hub warp.",
    2.0,
    0.0,
    8.0
  )

  private val autoLearnLastKill = CheckboxSetting(
    "Auto Learn Last Kill",
    "Set last killed enemy as target and whitelist entry.",
    true
  )

  private val whitelistOnly = CheckboxSetting(
    "Whitelist Only",
    "Only target entities matching learned whitelist names.",
    true
  )

  private val learnedWhitelistText = TextSetting(
    "Learned Whitelist",
    "Auto-learned target names from kills.",
    "EMPTY"
  )

  private val lastKillText = TextSetting(
    "Last Kill",
    "Most recently learned enemy name.",
    "-"
  )

  val isActive: Boolean get() = enabled.value
  val isRunning: Boolean get() = enabled.value
  val isSlayerHudVisible: Boolean get() = cryptZombieSlayer.value || slayerModeEnabled
  val modeDisplay: String get() = combatModeDisplayName(currentHudCombatMode())
  val slayerDisplay: String get() = if (cryptZombieSlayer.value) slayerStatus.value else "Disabled"
  val slayerQuestLevelDisplay: String
    get() = if (!isSlayerHudVisible) "--" else trackedSlayerQuestLabel()
  val slayerQuestStateDisplay: String
    get() = if (!isSlayerHudVisible) "--" else trackedSlayerQuestHudStageLabel()
  val slayerKillsLeftDisplay: String
    get() = if (!isSlayerHudVisible) "--" else (currentSlayerKillsLeft()?.toString() ?: "--")
  val slayerKillsPerHourDisplay: String
    get() = if (!isSlayerHudVisible) "--" else formatSessionRateDisplay(slayerSessionMobKills)
  val slayerQuestsCompletedDisplay: String
    get() = if (!isSlayerHudVisible) "--" else slayerSessionQuestCompletions.toString()
  val slayerQuestsPerHourDisplay: String
    get() = if (!isSlayerHudVisible) "--" else formatSessionRateDisplay(slayerSessionQuestCompletions)
  val slayerQuestsFailedDisplay: String
    get() = if (!isSlayerHudVisible) "--" else slayerSessionQuestFailures.toString()
  val slayerQuestFailsPerHourDisplay: String
    get() = if (!isSlayerHudVisible) "--" else formatSessionRateDisplay(slayerSessionQuestFailures)
  val statusDisplay: String
    get() =
      when {
        !enabled.value -> "Off"
        cryptZombieSlayer.value -> slayerStatus.value
        currentTargetId != null && startedPath && nativeActive() -> "Pathing To Target"
        currentTargetId != null -> "Engaging Target"
        startedPath && nativeActive() -> "Pathing"
        else -> "Searching"
      }
  val targetDisplay: String
    get() {
      val activeTargetName = resolveCurrentTargetName()
      if (!activeTargetName.isNullOrBlank()) {
        return activeTargetName
      }
      if (cryptZombieSlayer.value) {
        if (slayerBossActive) return "Slayer Boss"
        val activeName = resolveCurrentTargetName()
        if (!activeName.isNullOrBlank()) {
          val norm = normalizeNameForMatch(activeName)
          if (isSlayerPriorityMobName(norm)) return "\u2B50 $activeName"
        }
        return when (slayerType.value) {
          0 -> "Zombie / Ghoul"
          1 -> "Wolf"
          2 -> "Spider"
          3 -> "Voidling"
          4 -> "Vampire"
          5 -> "Blaze"
          else -> "Mob"
        }
      }
      val filter = targetName.value.trim()
      return if (filter.isNotEmpty()) filter else "Nearest Mob"
    }
  val targetHealthDisplay: String
    get() {
      val target = resolveCurrentTarget() ?: return "-- / --"
      return "${formatHudHealth(target.health)} / ${formatHudHealth(target.maxHealth)}"
    }
  val targetHealthRatio: Float
    get() {
      val target = resolveCurrentTarget() ?: return 0f
      val maxHealth = target.maxHealth.coerceAtLeast(1f)
      return (target.health.coerceAtLeast(0f) / maxHealth).coerceIn(0f, 1f)
    }

  private fun currentHudCombatMode(): Int {
    val target = resolveCurrentTarget()
    if (target != null) return effectiveCombatMode(target)
    if (!cryptZombieSlayer.value) return combatMode.value
    return fallbackSlayerCombatModeForHud()
  }

  private fun fallbackSlayerCombatModeForHud(): Int {
    if (shouldUseSpiderPhaseBowCombat() && slayerBossActive && isSpiderBossHatchlingsPhaseActive()) {
      return 1
    }
    if (shouldUseEndermanDynamicCombat()) {
      val boss = resolveNearestSlayerBoss()
      if (boss != null) {
        return if (isEndermanBossHitPhase(boss)) 1 else 0
      }
      return when (emanSpawnWeapon.value) {
        EMAN_SPAWN_WEAPON_ENDERMAN_SWORD -> 0
        else -> 1
      }
    }
    return combatMode.value
  }

  private fun combatModeDisplayName(mode: Int): String =
    when (mode) {
      0 -> "Melee"
      1 -> "Bow"
      2 -> "Mage"
      else -> "Unknown"
    }

  fun startForAutomation(mobName: String) {
    cryptZombieSlayer.value = false
    targetName.value = mobName
    whitelistOnly.value = false
    enabled.value = true
  }

  fun stopForAutomation() {
    enabled.value = false
  }

  private var lastTargetPos: BlockPos? = null
  private var lastMoveX = 0.0
  private var lastMoveY = 0.0
  private var lastMoveZ = 0.0
  private var stuckTicks = 0
  private var nextAttackNs = 0L
  private var startedPath = false
  private var currentTargetId: UUID? = null
  private var closeChaseActive = false
  private var closeChaseTargetId: UUID? = null
  private var losScanStrafeRight = false
  private var losScanLastFlipTick = 0L
  private var lastPathStartTick = 0L
  private var killCandidateId: UUID? = null
  private var killCandidateName: String? = null
  private var killCandidateWasSlayerMob = false
  private var killCandidateWasSlayerBoss = false
  private var killCandidateExpiresTick = 0L
  private var killCandidateAttackTick = 0L
  private val learnedWhitelist = LinkedHashSet<String>()
  private var lastSyncedWhitelistRaw = ""
  private var drillWarnTick = 0L
  private var lastHealUseTick = 0L
  private var stuckRepathCount = 0
  private var startAreaOrigin: BlockPos? = null
  private var pendingHealRelease = false
  private var pendingHealRestoreSlot = -1
  private var pendingOverfluxLookDownTicks = 0
  private var pendingOverfluxRecoverLookTicks = 0
  private var pendingOverfluxRestoreSlot = -1
  private var slayerBossActive = false
  private var slayerSessionStartMs = 0L
  private var slayerTrackedQuestActive = false
  private var slayerTrackedQuestType = -1
  private var slayerTrackedQuestTier = 0
  private var slayerTrackedQuestProgressKills = 0
  private var slayerTrackedQuestTargetKills = 0
  private var slayerTrackedQuestSawMobKill = false
  private var slayerTrackedQuestBossSeen = false
  private var slayerTrackedQuestCompletionPendingRestart = false
  private var slayerSessionMobKills = 0
  private var slayerSessionQuestCompletions = 0
  private var slayerSessionQuestFailures = 0
  private var slayerBossLastSeenTick = 0L
  private var slayerOverfluxUsedThisBoss = false
  private var slayerRagnarokUsedThisBoss = false
  private var slayerLastBadHealthUseTick = -1L
  private var slayerLastOverfluxUseTick = -1L
  private var slayerLastRagnarokUseTick = -1L
  private var slayerLastTabScanTick = -1L
  private var slayerTabCache: List<String> = emptyList()
  private var slayerNeedsQuestRestart = false
  private var slayerQuestReady = false
  private var slayerRagnarokUsedPreBoss = false
  private var slayerAutoDetected = false
  private var slayerDetectStartTick = -1L
  private var slayerNeedsQuestClaim = false
  private var slayerNeedsWalkback = false
  private var slayerWalkbackJustFarm = false  // true = walkback is walk-IN to farm area (skip quest restart)
  private var slayerWalkInDelayUntilTick = -1L  // wait for warp to finish before starting walkin route
  private var slayerDeathRespawnPending = false  // player died; trigger walkback once they respawn
  private var lastKnownLevel: net.minecraft.client.multiplayer.ClientLevel? = null  // server-switch detection
  private var slayerBossLastPos: net.minecraft.world.phys.Vec3? = null
  private var slayerClaimStartTick = -1L
  private var slayerClaimLastClickTick = -1L
  private var slayerLastBatphoneAttemptTick = -1L
  private var slayerLastBatphoneUseTick = -1L
  private var slayerLastGuiActionTick = -1L
  private var slayerWarnNoBatphoneTick = -1L
  private var slayerModeEnabled = false
  private var slayerQuestStateGraceUntilTick = -1L
  private var spiderHatchlingsPhaseActive = false
  private var spiderHatchlingsPhaseUntilTick = -1L
  private var spiderHatchlingsBossId: UUID? = null
  private var spiderHatchlingsBossAnchor: net.minecraft.world.phys.Vec3? = null
  private var wolfPupsPhaseUntilTick = -1L
  /** True once the player has physically been confirmed inside the crypt since the last warp-hub.
   *  The outside-crypt recovery check only fires when this is true, preventing the startup spam
   *  loop where the player warps to hub and the check immediately re-triggers before they arrive. */
  private var slayerEnteredCrypt = false
  private var wandWasInHotbar = false
  private var zombieSwordWasInHotbar = false
  private var overfluxWasInHotbar = false
  private var ragnarokWasInHotbar = false
  private var badHealthWasInHotbar = false
  private val overfluxLookDownStrategy = TimedEaseStrategy(EasingType.LINEAR, EasingType.LINEAR, 220L)

  init {
    assignSettingGroups()
      addSetting(
        enabled,
        info,
        targetName,
        cryptZombieSlayer,
        slayerStatus,
        slayerType,
      slayerTier,
      slayerAutoWarp,
      slayerLocation,
      slayerBossNotification,
      slayerBossEsp,
      slayerEspTargets,
      slayerWalkbackRoute,
      patrolRouteNameProxy,
      patrolLoadAction,
      patrolSaveAction,
      patrolStartAction,
      patrolStopAction,
      searchRange,
      minCps,
      maxCps,
      attackRange,
      chaseStopBuffer,
      stayNearStart,
      startAreaRadius,
      oneTapMode,
      combatMode,
      zombieDynamicCombat,
      zombieSpawnWeapon,
      zombieDynamicSword,
      endermanDynamicCombat,
      emanSpawnWeapon,
      emanDynamicSwapRange,
      emanHitPhaseWeapon,
      emanBossWeapon,
      spiderPhaseBowCombat,
      wolfIgnorePups,
      swordKeepDistance,
      bowMinRange,
      bowElevationPerBlock,
      mageElevationPerBlock,
      autoHeal,
      autoWandOfAtonement,
      autoZombieSword,
      autoOverflux,
      autoRagnarok,
      autoSwordOfBadHealth,
      healAtHealth,
      stuckTicksSetting,
      warpOnStuck,
      requireLos,
      aimTolerance,
      minAttackCooldown,
      stuckRepathTries,
      autoLearnLastKill,
      whitelistOnly,
      learnedWhitelistText,
      lastKillText
    )
    EventBus.register(this)
  }

  private fun assignSettingGroups() {
      enabled.inGroup(TAB_COMBAT_GROUP)
      info.inGroup(TAB_COMBAT_GROUP)
      targetName.inGroup(TAB_COMBAT_GROUP)
      searchRange.inGroup(TAB_COMBAT_GROUP)
      minCps.inGroup(TAB_COMBAT_GROUP)
      maxCps.inGroup(TAB_COMBAT_GROUP)
    attackRange.inGroup(TAB_COMBAT_GROUP)
    chaseStopBuffer.inGroup(TAB_COMBAT_GROUP)
    stayNearStart.inGroup(TAB_COMBAT_GROUP)
    startAreaRadius.inGroup(TAB_COMBAT_GROUP)
    stuckTicksSetting.inGroup(TAB_COMBAT_GROUP)
    warpOnStuck.inGroup(TAB_COMBAT_GROUP)
    requireLos.inGroup(TAB_COMBAT_GROUP)
    aimTolerance.inGroup(TAB_COMBAT_GROUP)
    minAttackCooldown.inGroup(TAB_COMBAT_GROUP)
    stuckRepathTries.inGroup(TAB_COMBAT_GROUP)
    autoLearnLastKill.inGroup(TAB_COMBAT_GROUP)
    whitelistOnly.inGroup(TAB_COMBAT_GROUP)
    learnedWhitelistText.inGroup(TAB_COMBAT_GROUP)
    lastKillText.inGroup(TAB_COMBAT_GROUP)

    cryptZombieSlayer.inGroup(TAB_SLAYER_GROUP)
    slayerStatus.inGroup(TAB_SLAYER_GROUP)
    slayerType.inGroup(TAB_SLAYER_GROUP)
    slayerTier.inGroup(TAB_SLAYER_GROUP)
    slayerAutoWarp.inGroup(TAB_SLAYER_GROUP)
    slayerBossNotification.inGroup(TAB_SLAYER_GROUP)
    slayerBossEsp.inGroup(TAB_SLAYER_GROUP)
    slayerEspTargets.inGroup(TAB_SLAYER_GROUP)

    slayerLocation.inGroup(TAB_SLAYER_SETTINGS_GROUP)
    slayerWalkbackRoute.inGroup(TAB_SLAYER_SETTINGS_GROUP)
    endermanDynamicCombat.inGroup(TAB_SLAYER_SETTINGS_GROUP)
    emanSpawnWeapon.inGroup(TAB_SLAYER_SETTINGS_GROUP)
    emanDynamicSwapRange.inGroup(TAB_SLAYER_SETTINGS_GROUP)
    emanHitPhaseWeapon.inGroup(TAB_SLAYER_SETTINGS_GROUP)
    emanBossWeapon.inGroup(TAB_SLAYER_SETTINGS_GROUP)
    patrolRouteNameProxy.inGroup(TAB_PATROL_GROUP)
    patrolLoadAction.inGroup(TAB_PATROL_GROUP)
    patrolSaveAction.inGroup(TAB_PATROL_GROUP)
    patrolStartAction.inGroup(TAB_PATROL_GROUP)
    patrolStopAction.inGroup(TAB_PATROL_GROUP)

    oneTapMode.inGroup(TAB_COMBAT_GROUP)
    combatMode.inGroup(TAB_COMBAT_GROUP)
    zombieDynamicCombat.inGroup(TAB_COMBAT_GROUP)
    zombieSpawnWeapon.inGroup(TAB_COMBAT_GROUP)
    zombieDynamicSword.inGroup(TAB_COMBAT_GROUP)
    spiderPhaseBowCombat.inGroup(TAB_COMBAT_GROUP)
    wolfIgnorePups.inGroup(TAB_COMBAT_GROUP)
    swordKeepDistance.inGroup(TAB_COMBAT_GROUP)
    bowMinRange.inGroup(TAB_COMBAT_GROUP)
    bowElevationPerBlock.inGroup(TAB_COMBAT_GROUP)
    mageElevationPerBlock.inGroup(TAB_COMBAT_GROUP)
    autoHeal.inGroup(TAB_AUTO_ITEMS_GROUP)
    autoWandOfAtonement.inGroup(TAB_AUTO_ITEMS_GROUP)
    autoZombieSword.inGroup(TAB_AUTO_ITEMS_GROUP)
    autoOverflux.inGroup(TAB_AUTO_ITEMS_GROUP)
    autoRagnarok.inGroup(TAB_AUTO_ITEMS_GROUP)
    autoSwordOfBadHealth.inGroup(TAB_AUTO_ITEMS_GROUP)
    healAtHealth.inGroup(TAB_AUTO_ITEMS_GROUP)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    // Server-switch detection: if level instance changes while macro is active, stop everything.
    val currentLevel = mc.level
    if (currentLevel !== lastKnownLevel) {
      if (lastKnownLevel != null && (enabled.value || slayerModeEnabled)) {
        stopMacro()
        ChatUtils.sendMessage("Combat macro stopped: server change detected.")
      }
      lastKnownLevel = currentLevel
    }

    // Release heal key and restore slot from previous heal use
    if (pendingHealRelease) {
      mc.options.keyUse?.setDown(false)
      pendingHealRelease = false
    }
    if (pendingHealRestoreSlot >= 0) {
      InventoryUtils.holdHotbarSlot(pendingHealRestoreSlot)
      pendingHealRestoreSlot = -1
    }

    val player = mc.player ?: run {
      clearPendingOverfluxPlacement()
      return
    }
    if (pendingOverfluxLookDownTicks > 0) {
      pendingOverfluxLookDownTicks--
      MovementManager.clearForcedMovement()
      RotationExecutor.rotateTo(Rotation(player.yRot, OVERFLUX_PLACE_PITCH), overfluxLookDownStrategy)
      if (pendingOverfluxLookDownTicks <= 0) {
        RotationExecutor.stopRotating()
        mc.options.keyUse?.setDown(true)
        pendingHealRelease = true
        pendingHealRestoreSlot = pendingOverfluxRestoreSlot
        pendingOverfluxRecoverLookTicks = OVERFLUX_RECOVER_LOOK_TICKS
        pendingOverfluxRestoreSlot = -1
      }
      return
    }
    if (pendingOverfluxRecoverLookTicks > 0) {
      pendingOverfluxRecoverLookTicks--
      applyOverfluxRecoverLook(player)
    }
    syncAutoItemToggles(player)
    if (startedPath && !nativeActive()) {
      startedPath = false
      lastTargetPos = null
    }
    if (startedPath && nativeActive() && !CombatPatrolModule.patrolOwnsPathfinder && !slayerNeedsWalkback) {
      val cmd = NativePathfinder.tick()
      if (cmd != null) cmd.applyToPlayer()
      else {
        when (NativePathfinder.status) {
          PathStatus.IDLE, PathStatus.ARRIVED, PathStatus.FAILED -> MovementManager.clearForcedMovement()
          else -> Unit
        }
      }
    }
    syncLearnedWhitelistFromSetting()
    if (cryptZombieSlayer.value && !slayerModeEnabled && enabled.value) {
      slayerModeEnabled = true
      beginSlayerSession()
      beginSlayerQuestDetection(mc.level?.gameTime ?: -1L)
      slayerRagnarokUsedPreBoss = false
      slayerEnteredCrypt = false
      slayerLastBatphoneAttemptTick = -1L
      slayerLastBatphoneUseTick = -1L
      slayerLastGuiActionTick = -1L
      // If not already in the farming area, navigate there via walkback route.
      if (slayerLocation.value == 1) {
        val initPos = mc.player?.blockPosition()
        val alreadyInCrypt = initPos != null && CRYPT_PATROL_WAYPOINTS.any { wp ->
          val dx = wp.x - initPos.x; val dz = wp.z - initPos.z
          dx * dx + dz * dz < CRYPT_PROXIMITY_RANGE_SQ
        }
        if (!alreadyInCrypt) {
          slayerEnteredCrypt = false
          triggerWalkToFarmArea(justFarm = true)
        } else {
          slayerEnteredCrypt = true
        }
      } else if (slayerLocation.value == 0 && slayerWalkbackRoute.value.isNotBlank()
          && CombatPatrolModule.patrolPoints.isNotEmpty()) {
        // Graveyard: check proximity to any patrol point.
        val initPos = mc.player?.blockPosition()
        val nearFarm = initPos == null || CombatPatrolModule.patrolPoints.any { p ->
          val dx = p.x - initPos.x; val dz = p.z - initPos.z
          dx * dx + dz * dz < GRAVEYARD_PROXIMITY_RANGE_SQ
        }
        if (!nearFarm) triggerWalkToFarmArea(justFarm = true)
      }
    }
    if (!cryptZombieSlayer.value) {
      slayerModeEnabled = false
    }
    if (!cryptZombieSlayer.value && !whitelistOnly.value) {
      whitelistOnly.value = true
    }
    if (cryptZombieSlayer.value && whitelistOnly.value) {
      whitelistOnly.value = false
    }
    if (!enabled.value) {
      stopMacro()
      return
    }

    val level = mc.level ?: return
    PathfindingModule.ensureEnabledForAutomation("combat macro")
    updateKillTracking(level)
    if (cryptZombieSlayer.value) {
      if (!slayerAutoDetected) tryAutoDetectSlayerQuest(level)
      updateSlayerBossState(level.gameTime)
      if (slayerNeedsQuestClaim) {
        if (handleClaimSlayerQuest(player, level)) return
      }
      // Legacy hub-warp delay - drained on upgrade; can be removed once all users have new config.
      if (slayerWalkInDelayUntilTick >= 0L) {
        if (level.gameTime < slayerWalkInDelayUntilTick) {
          slayerStatus.value = "Warping to Hub..."
          return
        }
        slayerWalkInDelayUntilTick = -1L
        triggerWalkToFarmArea(justFarm = true)
      }
      if (slayerNeedsWalkback) {
        // Walkback owns the pathfinder - prevent CombatMacroModule from double-ticking it.
        startedPath = false
        lastTargetPos = null
        if (WalkbackBridge.isRunning?.invoke() == true) {
          // Boss spawned during walkback - interrupt immediately and engage.
          if (slayerBossActive) {
            WalkbackBridge.stopWalkback?.invoke()
            slayerNeedsWalkback = false
            startAreaOrigin = null
            slayerStatus.value = "Boss! Engaging..."
            // fall through to combat resolution below
          } else {
            slayerStatus.value = if (slayerWalkbackJustFarm) "Walking to Farm..." else "Walking Back..."
            currentTargetId = null
          }
        } else {
          slayerNeedsWalkback = false
          startAreaOrigin = null
          if (slayerWalkbackJustFarm) {
            slayerWalkbackJustFarm = false
            // Walkback complete - ensure quest detection runs for fresh quest
            beginSlayerQuestDetection(mc.level?.gameTime ?: -1L)
          } else {
            queueSlayerQuestRestart(countFailure = false)
          }
        }
      }
      if (handleMaddoxGui(level.gameTime)) {
        return
      }
      if (tryRestartSlayerQuest(player, level.gameTime)) {
        return
      }
      // Use Ragnarok pre-emptively when quest is ready (boss spawns on next kill)
      if (slayerQuestReady && !slayerBossActive && !slayerRagnarokUsedPreBoss && autoRagnarok.value
        && level.gameTime - slayerLastRagnarokUseTick >= SLAYER_RAGNAROK_COOLDOWN_TICKS) {
        if (useHotbarUtilityItem(player, SLAYER_RAGNAROK_KEYWORDS)) {
          slayerRagnarokUsedPreBoss = true
          slayerRagnarokUsedThisBoss = true
          slayerLastRagnarokUseTick = level.gameTime
        }
      }
    } else {
      slayerStatus.value = "Off"
      clearSlayerBossState(false)
      slayerNeedsQuestRestart = false
    }

    if (player.isDeadOrDying || player.health <= 0f) {
      if (cryptZombieSlayer.value && !slayerDeathRespawnPending) {
        // Slayer: don't stop - queue walkback for when the player respawns.
        slayerDeathRespawnPending = true
        if (CombatPatrolModule.isPatrolRunning) CombatPatrolModule.stopPatrol()
        if (PathfindingModule.isPatrolActive) PathfindingModule.stopPatrol()
        if (slayerNeedsWalkback) WalkbackBridge.stopWalkback?.invoke()
        slayerNeedsWalkback = false
        nativeStop()
        slayerStatus.value = "Died - respawning..."
      } else if (!cryptZombieSlayer.value) {
        stopMacro()
      }
      return
    }
    // Respawn after slayer death: trigger walkback so player returns to farm area.
    if (slayerDeathRespawnPending) {
      slayerDeathRespawnPending = false
      startAreaOrigin = null
      triggerWalkToFarmArea(justFarm = false)
    }

    if (stayNearStart.value && startAreaOrigin == null) {
      val pos = player.blockPosition()
      // For crypt location, only anchor the start area when the player is actually
      // inside the crypt - prevents hub spawn from being used as the anchor point
      // which would block the walk-in route.
      val shouldAnchor = !(cryptZombieSlayer.value && slayerLocation.value == 1) ||
        CRYPT_PATROL_WAYPOINTS.any { wp ->
          val dx = wp.x - pos.x; val dz = wp.z - pos.z
          dx * dx + dz * dz < CRYPT_PROXIMITY_RANGE_SQ
        }
      if (shouldAnchor) startAreaOrigin = pos
    }

    tryAutoHeal(player, level.gameTime)
    // For crypt slayer: track when the player is confirmed inside the crypt, and recover
    // (warp hub + walk-in) only if they previously entered and then unexpectedly left.
    // The slayerEnteredCrypt guard prevents the startup spam loop where the player warps to
    // hub and the outside-crypt check immediately re-fires before they have walked back in.
    if (cryptZombieSlayer.value && slayerLocation.value == 1 && !slayerBossActive
      && slayerWalkInDelayUntilTick < 0L && !slayerNeedsWalkback) {
      val inCrypt = CRYPT_PATROL_WAYPOINTS.any { wp ->
        val dx = wp.x - player.x; val dz = wp.z - player.z
        dx * dx + dz * dz < CRYPT_PROXIMITY_RANGE_SQ
      }
      if (inCrypt) {
        slayerEnteredCrypt = true   // confirmed inside - enable recovery trigger
      } else if (slayerEnteredCrypt) {
        // Was inside the crypt but now isn't - navigate back via walkback route.
        ChatUtils.sendMessage("Combat macro: outside crypt, walking back.")
        startAreaOrigin = null
        slayerEnteredCrypt = false
        triggerWalkToFarmArea(justFarm = false)
        return
      }
      // If !inCrypt && !slayerEnteredCrypt: still making initial walk-in, do nothing here
    }
    // Walkback owns pathfinding - yield before enforceStartArea can override it.
    if (slayerNeedsWalkback && WalkbackBridge.isRunning?.invoke() == true && !slayerBossActive) {
      currentTargetId = null
      return
    }
    // Don't enforce start area when the slayer boss is active - chase it wherever it goes
    if (!(cryptZombieSlayer.value && slayerBossActive) && enforceStartArea(player, level)) {
      return
    }

    val target = if (cryptZombieSlayer.value) resolveSlayerTarget(player, level) else resolveTarget(player)
    if (cryptZombieSlayer.value) {
      tryUseSlayerSupportItems(player, target, level.gameTime)
    }
    if (target == null) {
      resetCloseChase()
      // Boss is active but not yet visible - walk toward its last known position so we can find it.
      if (cryptZombieSlayer.value && slayerBossActive) {
        val bossPos = slayerBossLastPos
        if (bossPos != null) {
          val bossBlockPos = BlockPos(bossPos.x.toInt(), bossPos.y.toInt(), bossPos.z.toInt())
          if (!nativeActive() || lastTargetPos != bossBlockPos) {
            if (level.gameTime - lastPathStartTick >= MIN_PATH_START_INTERVAL_TICKS) {
              lastPathStartTick = level.gameTime
              NativePathfinder.setTarget(bossBlockPos.x + 0.5, bossBlockPos.y.toDouble(), bossBlockPos.z + 0.5)
              val started = true
              if (started) {
                lastTargetPos = bossBlockPos
                startedPath = true
              }
            }
          }
          currentTargetId = null
          return
        }
      }
      // No target - hand off to CombatPatrolModule if patrol points are configured.
      if (CombatPatrolModule.patrolPoints.isNotEmpty() && !CombatPatrolModule.isPatrolRunning) {
        CombatPatrolModule.startPatrol()
      }
      if (CombatPatrolModule.isPatrolRunning) {
        when (CombatPatrolModule.patrolState) {
          CombatPatrolModule.PatrolState.COMBAT_INTERRUPT -> CombatPatrolModule.onCombatResume()
          CombatPatrolModule.PatrolState.AT_KILL_ZONE     -> CombatPatrolModule.onKillZoneCleared()
          else -> { /* NAVIGATING/WARPING - patrol owns movement */ }
        }
        if (startedPath && nativeActive() && !CombatPatrolModule.patrolOwnsPathfinder) nativeStop()
        startedPath = false; lastTargetPos = null; currentTargetId = null
        return
      }

      if (startedPath && nativeActive()) {
        nativeStop()
      }
      startedPath = false
      lastTargetPos = null
      currentTargetId = null
      MovementManager.clearForcedMovement()
      return
    }

    val dist = player.distanceTo(target)
    val pathActiveNow = startedPath && nativeActive()
    val hasLos = player.hasLineOfSight(target)
    val activeCombatMode = effectiveCombatMode(target)
    val engageRange = combatEngageRange(activeCombatMode)
    val keepDistance = combatKeepDistance(activeCombatMode)
    val suppressAttack = cryptZombieSlayer.value && shouldSuppressSlayerAttack(target, level.gameTime)
    val inAttackRange =
      when (activeCombatMode) {
        0 -> dist <= attackRange.value
        else -> hasLos && dist <= engageRange
      }
    val inKeepDistanceZone =
      when (activeCombatMode) {
        0 -> dist <= keepDistance
        else -> hasLos && dist <= keepDistance
      }
    val inCloseChaseRange = dist <= engageRange + chaseStopBuffer.value

    ensurePreferredWeapon(player, target, level.gameTime)
    if (cryptZombieSlayer.value) {
      ensureSlayerWeapon(player, target)
    }

    val aimRotation = if (activeCombatMode != 0) rangedAimRotation(player, target, activeCombatMode) else AngleUtils.getRotation(target)
    val phaseMovementActive =
      if (cryptZombieSlayer.value) {
        handleSlayerPhaseMovement(player, target, dist.toDouble(), hasLos, activeCombatMode, level.gameTime)
      } else {
        false
      }
    if (inCloseChaseRange && (hasLos || inAttackRange)) {
      // Let native path rotation steer the player until it has fully arrived;
      // overriding it early makes the movement drift off-path and circle.
      if (!pathActiveNow || inAttackRange) {
        RotationExecutor.rotateTo(aimRotation, rotationStrategy)
      }
    } else if (!pathActiveNow && !inCloseChaseRange && dist > attackRange.value + chaseStopBuffer.value + ROTATION_STOP_HYSTERESIS) {
      RotationExecutor.stopRotating()
    }

    if (inAttackRange || inKeepDistanceZone) {
      if (!oneTapMode.value && inKeepDistanceZone && startedPath && nativeActive()) {
        nativeStop()
      }
      if (!oneTapMode.value && inKeepDistanceZone) {
        startedPath = false
        lastTargetPos = null
      }
      stuckRepathCount = 0
      if (inAttackRange) {
        losScanLastFlipTick = 0L
        val keepRangedMomentum = activeCombatMode != 0 && oneTapMode.value && pathActiveNow
        if (!keepRangedMomentum && !phaseMovementActive) {
          MovementManager.clearForcedMovement()
        }
        if (!suppressAttack) {
          attemptAttack(player, target, activeCombatMode)
        }
      } else {
        // In range but no LOS - face target and strafe to find a clear angle
        RotationExecutor.rotateTo(aimRotation, rotationStrategy)
        applyLosStrafeScan(level.gameTime)
      }
    } else if (slayerNeedsWalkback) {
      // Walkback owns movement - don't chase, just rotate toward target so we can attack when it comes in range.
      RotationExecutor.rotateTo(aimRotation, rotationStrategy)
    } else {
      // Target spotted - stop kill patrol so combat macro can take over pathfinding.
      if (PathfindingModule.isPatrolActive) PathfindingModule.stopPatrol()
      if (CombatPatrolModule.patrolState == CombatPatrolModule.PatrolState.NAVIGATING ||
          CombatPatrolModule.patrolState == CombatPatrolModule.PatrolState.WARPING) {
        CombatPatrolModule.onCombatInterrupt()
      }
      val targetPos = target.blockPosition()
      val last = lastTargetPos
      val targetMovedFar = last == null || last.distSqr(targetPos) > TARGET_REPATH_DISTANCE_SQ
      val pathDone = !pathActiveNow
      val directChaseZone = activeCombatMode == 0 && shouldUseDirectChase(target, hasLos, dist.toDouble())
      if (directChaseZone) {
        // Once LOS is established and the target is close enough, stop letting the
        // pathfinder keep re-solving around the mob and finish the approach with
        // strict forward-only chase.
        if (pathActiveNow) {
          nativeStop()
        }
        startedPath = false
        lastTargetPos = null
        handleSoftChaseMovement(player, target, dist.toDouble())
      } else if (pathDone || targetMovedFar) {
        if (level.gameTime - lastPathStartTick >= MIN_PATH_START_INTERVAL_TICKS) {
          lastPathStartTick = level.gameTime
          val started = startCombatPathToTarget(level, targetPos, activeCombatMode)
          if (started) {
            lastTargetPos = targetPos
            startedPath = true
          } else {
            startedPath = false
            if (!nativeActive()) {
              lastTargetPos = null
            }
          }
        }
      }
    }

    updateStuck(player, inAttackRange || inKeepDistanceZone, level)
  }

  @SubscribeEvent
  fun onRender(event: WorldRenderEvent.Last) {
    if (!enabled.value) return
    val level = mc.level ?: return

    if (cryptZombieSlayer.value && slayerBossEsp.value) {
      renderSlayerHighlights(event.context, level)
    }

    val targetId = currentTargetId ?: return
    val target = level.entitiesForRendering().firstOrNull { it.uuid == targetId } as? LivingEntity ?: return
    if (!target.isAlive || target.health <= 0f) return

    val color = currentTargetRenderColor()
    Render3D.drawBox(event.context, target.boundingBox.inflate(TARGET_BOX_INFLATE), color, true)
  }

  private fun resolveTarget(player: Player): LivingEntity? {
    val level = mc.level ?: return null
    // Kill zone mode: only target mobs near the current kill point.
    val killPoint = CombatPatrolModule.currentKillPoint
    if (killPoint != null) {
      val kx = killPoint.x + 0.5; val ky = killPoint.y.toDouble(); val kz = killPoint.z + 0.5
      val radiusSq = CombatPatrolModule.killZoneRadiusValue * CombatPatrolModule.killZoneRadiusValue
      val blacklisted = builtInBlacklistedNames
      val filter = targetName.value.trim().lowercase()
      var best: LivingEntity? = null
      var bestDist = Double.POSITIVE_INFINITY
      for (entity in level.entitiesForRendering()) {
        val living = entity as? LivingEntity ?: continue
        if (!isValidTarget(living, player, blacklisted, filter, true)) continue
        val dx = living.x - kx; val dy = living.y - ky; val dz = living.z - kz
        val distSq = dx * dx + dy * dy + dz * dz
        if (distSq > radiusSq) continue
        val playerDist = player.distanceToSqr(living)
        if (playerDist < bestDist) { best = living; bestDist = playerDist }
      }
      if (best != null) currentTargetId = best.uuid
      return best
    }
    val filter = targetName.value.trim().lowercase()
    val blacklisted = builtInBlacklistedNames
    // Patrol running (non-kill-zone): bypass stayNearStart - patrol route defines the area.
    val startOrigin =
      if (CombatPatrolModule.isPatrolRunning || !stayNearStart.value) {
        null
      } else {
        startAreaOrigin ?: player.blockPosition().also { startAreaOrigin = it }
      }
    val startAreaRangeSq = startAreaRadius.value * startAreaRadius.value

    val searchRangeSq = searchRange.value * searchRange.value
    var best: LivingEntity? = null
    var bestDist = Double.POSITIVE_INFINITY
    for (entity in level.entitiesForRendering()) {
      val living = entity as? LivingEntity ?: continue
      if (!isValidTarget(living, player, blacklisted, filter, true)) continue
      if (startOrigin != null) {
        val ox = living.x - (startOrigin.x + 0.5)
        val oz = living.z - (startOrigin.z + 0.5)
        if (ox * ox + oz * oz > startAreaRangeSq) continue
      }
      val dx = living.x - player.x
      val dy = living.y - player.y
      val dz = living.z - player.z
      val distSq = dx * dx + dy * dy + dz * dz
      if (distSq > searchRangeSq) continue
      if (distSq < bestDist) {
        best = living
        bestDist = distSq
      }
    }
    if (best != null) {
      currentTargetId = best.uuid
    }
    return best
  }

  private fun resolveSlayerTarget(player: Player, level: ClientLevel): LivingEntity? {
    val blacklisted = builtInBlacklistedNames
    // Kill zone mode: when patrol is AT_KILL_ZONE, anchor the mob search around the kill
    // point instead of startAreaOrigin. This matches how resolveTarget() works and prevents
    // the slayer from marking the kill zone "cleared" before any mobs there are targeted.
    val killPoint = CombatPatrolModule.currentKillPoint
    val patrolRunning = CombatPatrolModule.isPatrolRunning
    val startOrigin = when {
      // Kill zone: anchor search around the kill point
      killPoint != null -> BlockPos(killPoint.x, killPoint.y, killPoint.z)
      // Patrol navigating: don't constrain by startAreaOrigin - patrol route defines the area
      patrolRunning -> null
      stayNearStart.value -> startAreaOrigin ?: player.blockPosition().also { startAreaOrigin = it }
      else -> null
    }
    val startAreaRangeSq = when {
      killPoint != null -> CombatPatrolModule.killZoneRadiusValue.let { it * it }
      else -> startAreaRadius.value * startAreaRadius.value
    }
    // In kill zone mode the kill-point radius already constrains the search; skip the
    // player-distance searchRange filter so mobs at the kill zone are never missed.
    val searchRangeSq = if (killPoint != null) Double.POSITIVE_INFINITY else searchRange.value * searchRange.value
    var bestBoss: LivingEntity? = null
    var bestBossDist = Double.POSITIVE_INFINITY
    var bestPriorityMob: LivingEntity? = null
    var bestPriorityMobDist = Double.POSITIVE_INFINITY
    var bestFarmMob: LivingEntity? = null
    var bestFarmMobDist = Double.POSITIVE_INFINITY

    for (entity in level.entitiesForRendering()) {
      val living = entity as? LivingEntity ?: continue
      if (!isValidTarget(living, player, blacklisted, "", true, ignoreWhitelist = true)) continue

      val name = normalizedTargetDisplayName(living)
      val isBoss = isSlayerBossName(name)
      val isPriority = isSlayerPriorityMobName(name)
      val isFarmMob = isSlayerFarmMobName(name)
      if (!isBoss && !isPriority && !isFarmMob) continue

      if (startOrigin != null && !isBoss) {
        // Boss ignores start area - chase it wherever it goes
        val ox = living.x - (startOrigin.x + 0.5)
        val oz = living.z - (startOrigin.z + 0.5)
        if (ox * ox + oz * oz > startAreaRangeSq) continue
      }

      val dx = living.x - player.x
      val dy = living.y - player.y
      val dz = living.z - player.z
      val distSq = dx * dx + dy * dy + dz * dz
      if (!isBoss && distSq > searchRangeSq) continue // boss ignores search range

      if (isBoss && distSq < bestBossDist) {
        bestBoss = living
        bestBossDist = distSq
      }
      if (isPriority && distSq < bestPriorityMobDist) {
        bestPriorityMob = living
        bestPriorityMobDist = distSq
      }
      if (isFarmMob && distSq < bestFarmMobDist) {
        bestFarmMob = living
        bestFarmMobDist = distSq
      }
    }

    if (bestBoss != null) {
      onSlayerBossDetected(level.gameTime)
      slayerBossLastPos = bestBoss.position()
    } else if (slayerBossActive && level.gameTime - slayerBossLastSeenTick > SLAYER_BOSS_ENTITY_LOST_TICKS) {
      clearSlayerBossState()
    }

    updateSlayerBossPhaseState(level, bestBoss)
    val phaseTarget = if (slayerBossActive) resolveSlayerPhaseTarget(player, level, bestBoss) else null

    // Priority: boss > priority farm mob (rare/bonus) > regular farm mob
    val selected = when {
      phaseTarget != null -> phaseTarget
      slayerBossActive -> bestBoss
      else -> bestBoss ?: bestPriorityMob ?: bestFarmMob
    }
    if (selected != null) {
      currentTargetId = selected.uuid
    }
    return selected
  }

  private fun updateSlayerBossState(nowTick: Long) {
    val tabLines = readTabListLines(nowTick)
    val scoreboardLines = readScoreboardSidebarLines(mc.level)
    for (line in scoreboardLines) {
      applyQuestDetection(line, "scoreboard", nowTick)
      applyQuestProgressDetection(line, nowTick)
    }
    for (line in tabLines) {
      applyQuestDetection(line, "tab", nowTick)
      applyQuestProgressDetection(line, nowTick)
    }
    val questLines = tabLines + scoreboardLines
    val hasBossTabLine = questLines.any { line -> SLAYER_BOSS_TAB_KEYWORDS.any { keyword -> line.contains(keyword) } }
    val hasClearTabLine = questLines.any { line -> SLAYER_BOSS_CLEAR_KEYWORDS.any { keyword -> line.contains(keyword) } }
    val hasQuestTabLine = questLines.any { line ->
      lineHasActiveSlayerQuestSignal(line)
    }
    val questStateSettling = isSlayerQuestStateSettling(nowTick)

    if (hasBossTabLine) {
      onSlayerBossDetected(nowTick)
      confirmActiveSlayerQuest(nowTick)
      slayerQuestReady = false
    } else {
      if (hasClearTabLine) {
        if (slayerBossActive) {
          clearSlayerBossState()
        }
        if (!questStateSettling) {
          queueSlayerQuestRestart()
        }
      } else if (hasQuestTabLine) {
        confirmActiveSlayerQuest(nowTick)
      }
      if (slayerBossActive && nowTick - slayerBossLastSeenTick > SLAYER_BOSS_TAB_LOST_TICKS) {
        clearSlayerBossState()
      }
    }

    slayerStatus.value =
      when {
        slayerBossActive && isSpiderBossHatchlingsPhaseActive(nowTick) -> "Boss Active - Hatchlings"
        slayerBossActive && isWolfBossPupsPhaseActive(nowTick) ->
          if (wolfIgnorePups.value) "Boss Active - Pups (Ignored)" else "Boss Active - Pups"
        slayerBossActive -> "Boss Active"
        slayerNeedsQuestClaim -> "Claiming Loot"
        slayerQuestReady -> "Quest Ready!"
        slayerNeedsQuestRestart -> "Restarting Quest"
        else -> "Farming ${when (slayerType.value) { 0 -> "Zombies"; 1 -> "Wolves"; 2 -> "Spiders"; 3 -> "Voidlings"; 4 -> "Vampires"; 5 -> "Blazes"; else -> "Mobs" }}"
      }
  }

  private fun onSlayerBossDetected(nowTick: Long) {
    val wasActive = slayerBossActive
    slayerBossActive = true
    slayerTrackedQuestBossSeen = true
    slayerBossLastSeenTick = nowTick
    slayerNeedsQuestRestart = false
    if (!wasActive) {
      slayerOverfluxUsedThisBoss = false
      slayerRagnarokUsedThisBoss = false
      slayerLastBadHealthUseTick = -1L
      slayerLastOverfluxUseTick = -1L
      val bossName = slayerBossDisplayName()
      ChatUtils.sendMessage("Combat macro: $bossName spawned.")
      if (slayerBossNotification.value) {
        NotificationManager.queue("Slayer Boss Spawned", bossName, 3200L)
      }
    }
  }

  private fun clearSlayerBossState(announce: Boolean = true) {
    if (slayerBossActive && announce) {
      ChatUtils.sendMessage("Combat macro: Slayer boss no longer detected.")
    }
    clearPendingOverfluxPlacement()
    clearSpiderHatchlingsPhase()
    wolfPupsPhaseUntilTick = -1L
    slayerBossActive = false
    slayerBossLastSeenTick = 0L
    slayerOverfluxUsedThisBoss = false
    slayerRagnarokUsedThisBoss = false
    slayerRagnarokUsedPreBoss = false
    slayerQuestReady = false
    slayerLastBadHealthUseTick = -1L
    slayerBossLastPos = null
  }

  private fun tryUseSlayerSupportItems(player: Player, target: LivingEntity?, nowTick: Long) {
    if (!slayerBossActive) return
    if (pendingHealRelease || pendingHealRestoreSlot >= 0 || pendingOverfluxLookDownTicks > 0) return

    if (autoOverflux.value && !slayerOverfluxUsedThisBoss && nowTick - slayerLastOverfluxUseTick >= SLAYER_OVERFLUX_COOLDOWN_TICKS) {
      if (useHotbarOverflux(player)) {
        slayerOverfluxUsedThisBoss = true
        slayerLastOverfluxUseTick = nowTick
        return
      }
    }

    if (autoRagnarok.value && !slayerRagnarokUsedThisBoss && nowTick - slayerLastRagnarokUseTick >= SLAYER_RAGNAROK_COOLDOWN_TICKS) {
      if (useHotbarUtilityItem(player, SLAYER_RAGNAROK_KEYWORDS)) {
        slayerRagnarokUsedThisBoss = true
        slayerLastRagnarokUseTick = nowTick
        return
      }
    }

    if (autoSwordOfBadHealth.value && nowTick - slayerLastBadHealthUseTick >= SLAYER_BAD_HEALTH_REUSE_TICKS) {
      if (useHotbarUtilityItem(player, SLAYER_BAD_HEALTH_KEYWORDS)) {
        slayerLastBadHealthUseTick = nowTick
      }
    }
  }

  private fun tryRestartSlayerQuest(player: Player, nowTick: Long): Boolean {
    if (!slayerNeedsQuestRestart || slayerBossActive) return false
    // Don't fire the batphone until quest detection has settled
    if (!slayerAutoDetected) return true
    if (isLikelyMaddoxScreen(nowTick)) return true
    if (mc.screen is AbstractContainerScreen<*>) return false
    if (pendingHealRelease || pendingHealRestoreSlot >= 0) return true
    if (slayerLastBatphoneAttemptTick >= 0L && nowTick - slayerLastBatphoneAttemptTick < SLAYER_BATPHONE_RETRY_TICKS) {
      return true
    }

    var batphoneSlot = findHotbarSlotByKeywords(player, SLAYER_BATPHONE_KEYWORDS)
    if (batphoneSlot !in 0..8) {
      val moved = moveBatphoneToHotbar(player)
      if (!moved) {
        slayerStatus.value = "Need Maddox Batphone"
        if (slayerWarnNoBatphoneTick < 0L || nowTick - slayerWarnNoBatphoneTick >= SLAYER_NO_BATPHONE_WARN_TICKS) {
          slayerWarnNoBatphoneTick = nowTick
          ChatUtils.sendMessage("Combat macro: Maddox Batphone not found in inventory/hotbar.")
        }
        slayerLastBatphoneAttemptTick = nowTick
        return true
      }
      batphoneSlot = findHotbarSlotByKeywords(player, SLAYER_BATPHONE_KEYWORDS)
    }

    if (batphoneSlot !in 0..8) {
      slayerLastBatphoneAttemptTick = nowTick
      return true
    }

    if (useHotbarUtilityItem(player, SLAYER_BATPHONE_KEYWORDS)) {
      slayerLastBatphoneAttemptTick = nowTick
      slayerLastBatphoneUseTick = nowTick
      slayerStatus.value = "Calling Maddox..."
      return true
    }

    return false
  }

  private fun handleMaddoxGui(nowTick: Long): Boolean {
    if (!slayerNeedsQuestRestart) return false
    if (!isLikelyMaddoxScreen(nowTick)) return false
    if (nowTick - slayerLastGuiActionTick < SLAYER_GUI_ACTION_COOLDOWN_TICKS) return true

    val player = mc.player ?: return true
    val menu = player.containerMenu
    val (actionSlot, isFinalPurchase) = findMaddoxMenuActionSlot(menu.slots)
    if (actionSlot >= 0) {
      InventoryUtils.clickSlot(actionSlot)
      slayerLastGuiActionTick = nowTick
      slayerLastBatphoneAttemptTick = nowTick
      if (isFinalPurchase) {
        player.closeContainer()
        beginSlayerQuestDetection(nowTick)
        startTrackedSlayerQuest(slayerType.value, slayerTier.value.toInt())
        slayerRagnarokUsedPreBoss = false
        startAreaOrigin = null
        if (cryptZombieSlayer.value && slayerLocation.value == 1) {
          // Crypt: check if already inside the farming area - if so skip the warp and walk-in.
          val alreadyInCrypt = CRYPT_PATROL_WAYPOINTS.any { wp ->
            val dx = wp.x - player.x; val dz = wp.z - player.z
            dx * dx + dz * dz < CRYPT_PROXIMITY_RANGE_SQ
          }
          if (alreadyInCrypt) {
            slayerEnteredCrypt = true
          } else {
            slayerEnteredCrypt = false
            triggerWalkToFarmArea(justFarm = true)
          }
        } else if (slayerAutoWarp.value) {
          val warp = slayerWarpCommand
          if (warp.isNotEmpty()) player.connection?.sendCommand(warp)
        }
      }
      slayerStatus.value = "Restarting Slayer..."
      return true
    }

    if (slayerLastBatphoneUseTick >= 0L && nowTick - slayerLastBatphoneUseTick >= SLAYER_MADDOX_GUI_TIMEOUT_TICKS) {
      player.closeContainer()
      slayerLastGuiActionTick = nowTick
      slayerLastBatphoneAttemptTick = nowTick
      return true
    }

    return true
  }

  /** Called when the loot claim step finishes (success or timeout). Starts the walkback route
   *  or proceeds directly to quest restart. */
  private fun finishSlayerClaim() {
    slayerNeedsQuestClaim = false
    slayerBossLastPos = null
    val routeName = slayerWalkbackRoute.value.trim()
    if (!slayerAutoWarp.value && routeName.isNotBlank()) {
      if (startedPath && nativeActive()) nativeStop()
      startedPath = false
      lastTargetPos = null
      currentTargetId = null
      stuckTicks = 0
      stuckRepathCount = 0
      RotationExecutor.stopRotating()
      MovementManager.clearForcedMovement()
      val started = WalkbackBridge.startWalkback?.invoke(routeName, 5, true) ?: false
      if (started) {
        slayerNeedsWalkback = true
        return
      }
    }
    queueSlayerQuestRestart(countFailure = false)
  }

  /**
   * Walks to the boss death position, right-clicks the loot bag entity, then transitions
   * to quest restart. Returns true while the claim step is still in progress.
   */
  private fun handleClaimSlayerQuest(player: LocalPlayer, level: ClientLevel): Boolean {
    val nowTick = level.gameTime

    // Timeout - give up and restart the quest anyway
    if (slayerClaimStartTick >= 0L && nowTick - slayerClaimStartTick > SLAYER_CLAIM_TIMEOUT_TICKS) {
      finishSlayerClaim()
      return false
    }

    val deathPos = slayerBossLastPos
    if (deathPos == null) {
      // No tracked position - skip straight to restart
      finishSlayerClaim()
      return false
    }

    // Search for a loot bag ArmorStand near the death position
    val lootEntity = level.entitiesForRendering()
      .filterIsInstance<ArmorStand>()
      .filter { entity ->
        val dx = entity.x - deathPos.x
        val dy = entity.y - deathPos.y
        val dz = entity.z - deathPos.z
        dx * dx + dy * dy + dz * dz < SLAYER_LOOT_SEARCH_RADIUS_SQ
      }
      .filter { entity ->
        val name = normalizeNameForMatch(entity.name.string)
        SLAYER_LOOT_KEYWORDS.any { name.contains(it) }
      }
      .minByOrNull { entity ->
        val dx = entity.x - player.x
        val dy = entity.y - player.y
        val dz = entity.z - player.z
        dx * dx + dy * dy + dz * dz
      }

    if (lootEntity == null) {
      // No loot bag found yet - walk to death pos and keep waiting
      val targetPos = BlockPos(deathPos.x.toInt(), deathPos.y.toInt(), deathPos.z.toInt())
      if (!nativeActive() || lastTargetPos != targetPos) {
        if (nowTick - lastPathStartTick >= MIN_PATH_START_INTERVAL_TICKS) {
          lastPathStartTick = nowTick
          NativePathfinder.setTarget(targetPos.x + 0.5, targetPos.y.toDouble(), targetPos.z + 0.5)
          val started = true
          if (started) {
            lastTargetPos = targetPos
            startedPath = true
          }
        }
      }
      return true
    }

    // Loot entity found - stop pathing, look at it and right-click
    if (startedPath && nativeActive()) {
      nativeStop()
      startedPath = false
    }

    val dx = lootEntity.x - player.x
    val dy = lootEntity.y - player.y
    val dz = lootEntity.z - player.z
    val distSq = dx * dx + dy * dy + dz * dz

    if (distSq > SLAYER_LOOT_CLAIM_RANGE_SQ) {
      val targetPos = lootEntity.blockPosition()
      if (!nativeActive()) {
        if (nowTick - lastPathStartTick >= MIN_PATH_START_INTERVAL_TICKS) {
          lastPathStartTick = nowTick
          NativePathfinder.setTarget(targetPos.x + 0.5, targetPos.y.toDouble(), targetPos.z + 0.5)
          lastTargetPos = targetPos
          startedPath = true
        }
      }
      return true
    }

    val rotation = AngleUtils.getRotation(lootEntity)
    RotationExecutor.rotateTo(rotation, rotationStrategy)

    if (nowTick - slayerClaimLastClickTick >= SLAYER_CLAIM_CLICK_INTERVAL_TICKS) {
      slayerClaimLastClickTick = nowTick
      MouseUtils.rightClick()
    }

    // After a short dwell at the loot bag, assume claimed and move on
    if (nowTick - slayerClaimStartTick > SLAYER_CLAIM_DWELL_TICKS) {
      finishSlayerClaim()
    }

    return true
  }

  private fun isLikelyMaddoxScreen(nowTick: Long): Boolean {
    val screen = mc.screen as? AbstractContainerScreen<*> ?: return false
    val title = normalizeNameForMatch(screen.title.string)
    if (SLAYER_MADDOX_SCREEN_KEYWORDS.any { keyword -> title.contains(keyword) }) return true
    return slayerLastBatphoneUseTick >= 0L && nowTick - slayerLastBatphoneUseTick <= SLAYER_MADDOX_GUI_TIMEOUT_TICKS
  }

  @SubscribeEvent
  fun onChatReceive(event: ChatEvent.Receive) {
    val raw = event.message ?: return
    val msg = ChatFormatting.stripFormatting(raw)?.lowercase(Locale.ROOT)?.trim() ?: return

    if (enabled.value) {
      // Player name mentioned in chat -> play system alert sound.
      val ign = mc.player?.gameProfile?.name?.lowercase(Locale.ROOT) ?: ""
      if (ign.isNotEmpty() && msg.contains(ign)) {
        java.awt.Toolkit.getDefaultToolkit().beep()
      }
      // Evacuated from hub or moved to different server -> walk back to farm area.
      if (msg.contains("evacuated from") || msg.contains("moved to a different server")) {
        triggerWalkToFarmArea(justFarm = false)
        return
      }
    }

    if (!cryptZombieSlayer.value) return

    if (SLAYER_QUEST_READY_CHAT_KEYWORDS.any { msg.contains(it) }) {
      confirmActiveSlayerQuest(mc.level?.gameTime ?: -1L)
      slayerQuestReady = true
      ChatUtils.sendMessage("Combat macro: Quest ready - Ragnarok prepped for boss spawn.")
    }

    if (SLAYER_BOSS_SPAWNED_CHAT_KEYWORDS.any { msg.contains(it) }) {
      val level = mc.level ?: return
      onSlayerBossDetected(level.gameTime)
      confirmActiveSlayerQuest(level.gameTime)
      slayerQuestReady = false
    }

    if (shouldUseSpiderPhaseBowCombat() &&
      msg.contains("broodfather's hatchlings") &&
      msg.contains("before it can be damaged again")
    ) {
      activateSpiderHatchlingsPhase(mc.level?.gameTime ?: -1L)
    }

    if (SLAYER_BOSS_KILLED_CHAT_KEYWORDS.any { msg.contains(it) }) {
      completeTrackedSlayerQuest()
      clearSlayerBossState(false)
      slayerNeedsQuestClaim = true
      slayerClaimStartTick = mc.level?.gameTime ?: -1L
    }
  }

  private fun tryAutoDetectSlayerQuest(level: ClientLevel) {
    val nowTick = level.gameTime
    if (isSlayerQuestStateSettling(nowTick)) return

    // Scan scoreboard sidebar
    for (line in readScoreboardSidebarLines(level)) {
      if (applyQuestDetection(line, "scoreboard", nowTick)) return
    }

    // Scan tab list
    val tabLines = readTabListLines(nowTick)
    for (line in tabLines) {
      if (applyQuestDetection(line, "tab", nowTick)) return
    }

    // Nothing found yet - only conclude "no quest" after the detection window expires
    if (slayerDetectStartTick >= 0L && nowTick - slayerDetectStartTick >= SLAYER_DETECT_WINDOW_TICKS) {
      slayerAutoDetected = true
      queueSlayerQuestRestart() // no quest found in window -> buy one
    }
    // else: still within window - farming continues normally, detection runs next tick
  }

  /** Returns true and updates state if the line matches any active slayer quest. */
  private fun applyQuestDetection(line: String, source: String, nowTick: Long): Boolean {
    val typeIdx = detectSlayerTypeFromLine(line)
    if (typeIdx < 0) return false

    val tier = parseSlayerTierFromLine(line)
    if (slayerType.value != typeIdx) {
      slayerType.value = typeIdx
      ChatUtils.sendMessage("Combat macro: Detected ${slayerType.options[typeIdx]} Slayer from $source.")
    }
    if (tier in 1..5) slayerTier.value = tier.toDouble()
    confirmActiveSlayerQuest(nowTick, typeIdx, tier)
    return true
  }

  private fun beginSlayerQuestDetection(nowTick: Long) {
    slayerNeedsQuestRestart = false
    slayerQuestReady = false
    slayerAutoDetected = false
    slayerDetectStartTick = nowTick
    slayerQuestStateGraceUntilTick =
      if (nowTick >= 0L) nowTick + SLAYER_QUEST_STATE_GRACE_TICKS else -1L
  }

  private fun confirmActiveSlayerQuest(
    nowTick: Long,
    detectedType: Int = slayerType.value,
    detectedTier: Int = slayerTier.value.toInt(),
  ) {
    val resolvedType = detectedType.takeIf { it in slayerType.options.indices } ?: slayerType.value
    val resolvedTier = detectedTier.takeIf { it in 1..5 } ?: slayerTier.value.toInt().coerceIn(1, 5)
    slayerNeedsQuestRestart = false
    slayerAutoDetected = true
    slayerDetectStartTick = -1L
    if (!slayerTrackedQuestActive || slayerTrackedQuestCompletionPendingRestart) {
      startTrackedSlayerQuest(resolvedType, resolvedTier)
    } else {
      slayerTrackedQuestType = resolvedType
      slayerTrackedQuestTier = resolvedTier
    }
    if (nowTick >= 0L) {
      slayerQuestStateGraceUntilTick = nowTick + SLAYER_QUEST_STATE_GRACE_TICKS
    }
  }

  private fun isSlayerQuestStateSettling(nowTick: Long): Boolean {
    return slayerQuestStateGraceUntilTick >= 0L && nowTick < slayerQuestStateGraceUntilTick
  }

  private fun applyQuestProgressDetection(line: String, nowTick: Long): Boolean {
    val progress = parseSlayerQuestProgress(line) ?: return false
    val resolvedTier = progress.tier.takeIf { it in 1..5 } ?: slayerTier.value.toInt().coerceIn(1, 5)
    if (!slayerTrackedQuestActive || slayerTrackedQuestCompletionPendingRestart || slayerTrackedQuestType != progress.typeIndex) {
      startTrackedSlayerQuest(progress.typeIndex, resolvedTier)
    }
    confirmActiveSlayerQuest(nowTick, progress.typeIndex, resolvedTier)
    slayerTrackedQuestTargetKills = progress.target.coerceAtLeast(progress.progress)
    slayerTrackedQuestProgressKills = max(slayerTrackedQuestProgressKills, progress.progress)
    slayerTrackedQuestSawMobKill = slayerTrackedQuestSawMobKill || progress.progress > 0
    return true
  }

  private fun parseSlayerQuestProgress(line: String): SlayerQuestProgress? {
    val match = SLAYER_PROGRESS_PATTERN.find(line) ?: return null
    val progress = match.groupValues[1].toIntOrNull() ?: return null
    val target = match.groupValues[2].toIntOrNull() ?: return null
    if (target <= 0 || progress !in 0..target) return null
    val detectedType = detectSlayerTypeFromLine(line)
    val relevant =
      detectedType >= 0 ||
        line.contains("slayer quest") ||
        SLAYER_GENERIC_QUEST_KEYWORDS.any { keyword -> line.contains(keyword) }
    if (!relevant) return null
    val typeIndex = if (detectedType >= 0) detectedType else slayerType.value
    return SlayerQuestProgress(typeIndex, parseSlayerTierFromLine(line), progress, target)
  }

  private fun detectSlayerTypeFromLine(line: String): Int =
    when {
      line.contains("zombie slayer") || line.contains("revenant horror") || line.contains("atoned horror") || (line.contains("revenant") && line.contains("slayer")) -> 0
      line.contains("wolf slayer") || line.contains("sven packmaster") || (line.contains("sven") && line.contains("slayer")) -> 1
      line.contains("spider slayer") || line.contains("tarantula broodfather") || (line.contains("tarantula") && line.contains("slayer")) -> 2
      line.contains("enderman slayer") || line.contains("voidgloom seraph") || (line.contains("voidgloom") && line.contains("slayer")) -> 3
      line.contains("vampire slayer") || line.contains("riftstalker bloodfiend") || (line.contains("riftstalker") && line.contains("slayer")) -> 4
      line.contains("blaze slayer") || line.contains("inferno demonlord") || (line.contains("inferno") && line.contains("slayer")) -> 5
      else -> -1
    }

  private fun parseSlayerTierFromLine(line: String): Int =
    when {
      line.contains(" v") && !line.contains(" vi") -> 5
      line.contains(" iv") -> 4
      line.contains(" iii") -> 3
      line.contains(" ii") -> 2
      line.contains(" i") -> 1
      else -> -1
    }

  private fun lineHasActiveSlayerQuestSignal(line: String): Boolean {
    return detectSlayerTypeFromLine(line) >= 0 ||
      SLAYER_GENERIC_QUEST_KEYWORDS.any { keyword -> line.contains(keyword) } ||
      parseSlayerQuestProgress(line) != null
  }

  private fun readScoreboardSidebarLines(level: ClientLevel?): List<String> {
    val liveLevel = level ?: return emptyList()
    val scoreboard = liveLevel.scoreboard
    val objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return emptyList()
    return scoreboard.listPlayerScores(objective)
      .mapNotNull { score ->
        val ownerName = score.owner()
        val team = scoreboard.getPlayersTeam(ownerName)
        val raw = if (team != null) team.playerPrefix.string + ownerName + team.playerSuffix.string else ownerName
        ChatFormatting.stripFormatting(raw)?.lowercase(Locale.ROOT)?.trim()
      }
      .filter { it.isNotBlank() }
  }

  private fun moveBatphoneToHotbar(player: Player): Boolean {
    val sourceSlot = findFirstContainerSlotByKeywords(player.containerMenu.slots, SLAYER_BATPHONE_KEYWORDS) ?: return false
    if (sourceSlot in PLAYER_HOTBAR_MENU_SLOT_START..PLAYER_HOTBAR_MENU_SLOT_END) return true

    val targetHotbarSlot = findBestHotbarSwapSlot(player)
    InventoryUtils.swapSlotWithHotbar(sourceSlot, targetHotbarSlot)
    return true
  }

  private fun findFirstContainerSlotByKeywords(
    slots: List<net.minecraft.world.inventory.Slot>,
    keywords: Array<String>
  ): Int? {
    for (slot in slots) {
      if (!slot.hasItem()) continue
      val normalizedName = normalizeNameForMatch(slot.item.hoverName.string)
      if (keywords.any { keyword -> normalizedName.contains(keyword) }) {
        return slot.index
      }
    }
    return null
  }

  private fun findBestHotbarSwapSlot(player: Player): Int {
    val inventory = player.inventory
    for (i in 0..8) {
      if (inventory.getItem(i).isEmpty) {
        return i
      }
    }
    return inventory.selectedSlot.coerceIn(0, 8)
  }

  /**
   * Returns (slotIndex, isFinalPurchase).
   * isFinalPurchase = true  -> slot is "Start Quest" / "Restart" / "Confirm" - quest will be bought.
   * isFinalPurchase = false -> slot is just the revenant-icon navigation step.
   */
  private fun findMaddoxMenuActionSlot(slots: List<net.minecraft.world.inventory.Slot>): Pair<Int, Boolean> {
    var revenantSlot = -1
    var restartSlot = -1
    var confirmSlot = -1

    for (slot in slots) {
      if (!slot.hasItem()) continue
      val stack = slot.item
      val name = normalizeNameForMatch(stack.hoverName.string)
      val lore = stack.getLoreLines().joinToString(" ") { normalizeNameForMatch(it.string) }
      val text = "$name $lore"

      val hasRevenantKeyword = SLAYER_MADDOX_REVENANT_KEYWORDS.any { keyword -> text.contains(keyword) }
      val hasRestartKeyword = SLAYER_MADDOX_RESTART_KEYWORDS.any { keyword -> text.contains(keyword) }
      val hasConfirmKeyword = SLAYER_MADDOX_CONFIRM_KEYWORDS.any { keyword -> text.contains(keyword) }

      if (hasConfirmKeyword && hasRevenantKeyword) return Pair(slot.index, true)
      if (hasRestartKeyword && hasRevenantKeyword) return Pair(slot.index, true)

      if (hasRevenantKeyword && revenantSlot == -1) revenantSlot = slot.index
      if (hasRestartKeyword && restartSlot == -1) restartSlot = slot.index
      if (hasConfirmKeyword && confirmSlot == -1) confirmSlot = slot.index
    }

    return when {
      confirmSlot >= 0 -> Pair(confirmSlot, true)
      restartSlot >= 0 -> Pair(restartSlot, true)
      revenantSlot >= 0 -> Pair(revenantSlot, false) // navigation only, not yet purchased
      else -> Pair(-1, false)
    }
  }

  private fun useHotbarUtilityItem(player: Player, keywords: Array<String>): Boolean {
    if (pendingOverfluxLookDownTicks > 0) return false
    val slot = findHotbarSlotByKeywords(player, keywords)
    if (slot !in 0..8) return false

    val previousSlot = player.inventory.selectedSlot
    if (previousSlot != slot) {
      InventoryUtils.holdHotbarSlot(slot)
    }

    mc.options.keyUse?.setDown(true)
    pendingHealRelease = true
    pendingHealRestoreSlot = if (previousSlot != slot) previousSlot else -1
    return true
  }

  private fun useHotbarOverflux(player: Player): Boolean {
    if (pendingOverfluxLookDownTicks > 0) return false
    val slot = findHotbarSlotByKeywords(player, SLAYER_OVERFLUX_KEYWORDS)
    if (slot !in 0..8) return false

    val previousSlot = player.inventory.selectedSlot
    if (previousSlot != slot) {
      InventoryUtils.holdHotbarSlot(slot)
    }

    pendingOverfluxLookDownTicks = OVERFLUX_LOOK_DOWN_TICKS
    pendingOverfluxRestoreSlot = if (previousSlot != slot) previousSlot else -1
    return true
  }

  private fun clearPendingOverfluxPlacement() {
    if (pendingOverfluxLookDownTicks > 0) {
      RotationExecutor.stopRotating()
    }
    pendingOverfluxLookDownTicks = 0
    pendingOverfluxRecoverLookTicks = 0
    pendingOverfluxRestoreSlot = -1
  }

  private fun applyOverfluxRecoverLook(player: Player) {
    val target = resolveCurrentTarget() ?: if (cryptZombieSlayer.value) resolveNearestSlayerBoss() else null
    if (target != null) {
      val activeCombatMode = effectiveCombatMode(target)
      val rotation =
        if (activeCombatMode != 0) {
          rangedAimRotation(player, target, activeCombatMode)
        } else {
          AngleUtils.getRotation(target)
        }
      RotationExecutor.rotateTo(rotation, rotationStrategy)
      return
    }

    if (cryptZombieSlayer.value) {
      val bossPos = slayerBossLastPos
      if (bossPos != null) {
        RotationExecutor.rotateTo(AngleUtils.getRotation(bossPos), rotationStrategy)
      }
    }
  }

  private fun findHotbarSlotByKeywords(player: Player, keywords: Array<String>): Int {
    val inventory = player.inventory
    for (i in 0..8) {
      val stack = inventory.getItem(i)
      if (stack.isEmpty) continue
      val normalizedName = normalizeNameForMatch(stack.hoverName?.string.orEmpty())
      if (keywords.any { keyword -> normalizedName.contains(keyword) }) {
        return i
      }
    }
    return -1
  }

  private fun isSlayerBossName(normalizedName: String): Boolean {
    return SLAYER_BOSS_ENTITY_KEYWORDS.any { keyword -> normalizedName.contains(keyword) }
  }

  private fun isSlayerPriorityMobName(normalizedName: String): Boolean {
    return SLAYER_PRIORITY_MOB_KEYWORDS.any { keyword -> normalizedName.contains(keyword) }
  }

  private fun isSlayerFarmMobName(normalizedName: String): Boolean {
    return SLAYER_FARM_MOB_KEYWORDS.any { keyword -> normalizedName.contains(keyword) }
  }

  private fun readTabListLines(nowTick: Long): List<String> {
    if (slayerLastTabScanTick >= 0L && nowTick - slayerLastTabScanTick < SLAYER_TAB_SCAN_INTERVAL_TICKS) {
      return slayerTabCache
    }
    slayerLastTabScanTick = nowTick
    val connection = mc.connection ?: run {
      slayerTabCache = emptyList()
      return slayerTabCache
    }
    slayerTabCache =
      try {
        resolveTabEntries(connection)
          .mapNotNull { resolveEntryDisplayName(it) }
          .map { ChatFormatting.stripFormatting(it)?.lowercase(Locale.ROOT)?.trim() ?: "" }
          .filter { it.isNotBlank() }
      } catch (_: Exception) {
        emptyList()
      }
    return slayerTabCache
  }

  private fun resolveTabEntries(connection: Any): List<Any> {
    for (name in listOf("listPlayerEntries", "getListedOnlinePlayers", "getOnlinePlayers")) {
      val method = connection.javaClass.methods.firstOrNull { it.name == name && it.parameterCount == 0 } ?: continue
      val result = runCatching { method.invoke(connection) }.getOrNull() ?: continue
      when (result) {
        is Collection<*> -> return result.filterNotNull()
        is Iterable<*> -> return result.filterNotNull()
      }
    }
    return emptyList()
  }

  private fun resolveEntryDisplayName(entry: Any): String? {
    for (name in listOf("getTabListDisplayName", "tabListDisplayName", "getDisplayName", "displayName")) {
      val method = entry.javaClass.methods.firstOrNull { it.name == name && it.parameterCount == 0 } ?: continue
      val text = coerceText(runCatching { method.invoke(entry) }.getOrNull())
      if (!text.isNullOrBlank()) return text
    }
    for (name in listOf("getProfile", "getGameProfile", "profile")) {
      val method = entry.javaClass.methods.firstOrNull { it.name == name && it.parameterCount == 0 } ?: continue
      val profile = runCatching { method.invoke(entry) }.getOrNull() ?: continue
      val nameMethod = profile.javaClass.methods.firstOrNull { it.name == "getName" && it.parameterCount == 0 } ?: continue
      val profileName = runCatching { nameMethod.invoke(profile) as? String }.getOrNull()
      if (!profileName.isNullOrBlank()) return profileName
    }
    return null
  }

  private fun coerceText(value: Any?): String? {
    if (value == null) return null
    if (value is String) return value
    val m = value.javaClass.methods.firstOrNull { it.name == "getString" && it.parameterCount == 0 }
    val raw = m?.let { runCatching { it.invoke(value) }.getOrNull() }
    return if (raw is String) raw else value.toString()
  }

  private fun isValidTarget(
    living: LivingEntity,
    player: Player,
    blacklisted: Set<String>,
    nameFilter: String,
    requireRange: Boolean,
    ignoreWhitelist: Boolean = false
  ): Boolean {
    if (living is ArmorStand) return false
    if (living == player) return false
    if (!living.isAlive) return false
    if (living is Player && mc.connection?.getPlayerInfo(living.uuid) != null) return false
    val displayName = normalizedTargetDisplayName(living)
    if (blacklisted.contains(displayName)) return false
    if (nameFilter.isNotEmpty() && !displayName.contains(nameFilter)) return false
    if (!ignoreWhitelist && whitelistOnly.value) {
      // Only apply whitelist if there are user-learned entries beyond the default seed.
      // The default "ice walker" entry is always present and must not block all other mobs.
      val effectiveWhitelist = learnedWhitelist.filter { it != DEFAULT_WHITELIST_ENTRY && !blacklisted.contains(it) }
      if (effectiveWhitelist.isNotEmpty()) {
        val whitelistMatch = effectiveWhitelist.any { entry ->
          displayName.contains(entry) || entry.contains(displayName)
        }
        if (!whitelistMatch) return false
      }
    }
    return true
  }

  private fun enforceStartArea(
    player: Player,
    level: ClientLevel
  ): Boolean {
    if (!stayNearStart.value) return false
    // Don't fight the patrol: it defines the operational area, so overriding its
    // pathfinder here would cause false ARRIVED signals and premature point advances.
    if (CombatPatrolModule.isPatrolRunning) return false
    val origin = startAreaOrigin ?: return false
    val radius = startAreaRadius.value + START_AREA_RETURN_BUFFER
    val distSq = horizontalDistSq(player.x, player.z, origin.x + 0.5, origin.z + 0.5)
    if (distSq <= radius * radius) {
      return false
    }

    val pathAlreadyReturning = startedPath && lastTargetPos == origin && nativeActive()
    if (!pathAlreadyReturning && level.gameTime - lastPathStartTick >= MIN_PATH_START_INTERVAL_TICKS) {
      lastPathStartTick = level.gameTime
      NativePathfinder.setTarget(origin.x + 0.5, origin.y.toDouble(), origin.z + 0.5)
      if (true) {
        lastTargetPos = origin
        startedPath = true
      }
    }
    currentTargetId = null
    RotationExecutor.stopRotating()
    return true
  }

  private fun horizontalDistSq(ax: Double, az: Double, bx: Double, bz: Double): Double {
    val dx = ax - bx
    val dz = az - bz
    return dx * dx + dz * dz
  }

  private fun attemptAttack(player: Player, target: LivingEntity, activeCombatMode: Int) {
    when (activeCombatMode) {
      1    -> attemptBowAttack(player, target)
      2    -> attemptMageAttack(player, target)
      else -> attemptMeleeAttack(player, target)
    }
  }

  private fun attemptMeleeAttack(player: Player, target: LivingEntity) {
    if (!isAttackReady(player, target)) return
    val now = System.nanoTime()
    if (now < nextAttackNs) return
    val minRate = min(minCps.value, maxCps.value).coerceAtLeast(0.1)
    val maxRate = max(minCps.value, maxCps.value).coerceAtLeast(minRate)
    val cps = minRate + (Math.random() * (maxRate - minRate))
    val delayNs = (1_000_000_000.0 / cps).toLong()
    nextAttackNs = now + delayNs
    MouseUtils.leftClick()
    registerKillCandidate(target)
  }

  private fun attemptBowAttack(player: Player, target: LivingEntity) {
    if (!isRangedAttackReady(player, target, 1)) return
    val now = System.nanoTime()
    if (now < nextAttackNs) return
    val minRate = min(minCps.value, maxCps.value).coerceAtLeast(0.1)
    val maxRate = max(minCps.value, maxCps.value).coerceAtLeast(minRate)
    val cps = minRate + (Math.random() * (maxRate - minRate))
    nextAttackNs = now + (1_000_000_000.0 / cps).toLong()
    MouseUtils.rightClick()
    registerKillCandidate(target)
    if (oneTapMode.value) currentTargetId = null
  }

  private fun attemptMageAttack(player: Player, target: LivingEntity) {
    if (!isRangedAttackReady(player, target, 2)) return
    val now = System.nanoTime()
    if (now < nextAttackNs) return
    val minRate = min(minCps.value, maxCps.value).coerceAtLeast(0.1)
    val maxRate = max(minCps.value, maxCps.value).coerceAtLeast(minRate)
    val cps = minRate + (Math.random() * (maxRate - minRate))
    nextAttackNs = now + (1_000_000_000.0 / cps).toLong()
    MouseUtils.rightClick()
    registerKillCandidate(target)
    if (oneTapMode.value) currentTargetId = null
  }

  private fun isRangedAttackReady(player: Player, target: LivingEntity, activeCombatMode: Int): Boolean {
    if (!player.hasLineOfSight(target)) return false
    val aimed = rangedAimRotation(player, target, activeCombatMode)
    val yawError = abs(AngleUtils.getRotationDelta(player.yRot, aimed.yaw))
    val pitchError = abs(aimed.pitch - player.xRot)
    val tolerance = aimTolerance.value
    return yawError <= tolerance && pitchError <= tolerance
  }

  /**
   * Returns the aim rotation for ranged modes, including upward pitch correction for
   * projectile drop/lead. Falls back to standard rotation in melee mode.
   */
  private fun rangedAimRotation(
    player: Player,
    target: LivingEntity,
    activeCombatMode: Int = combatMode.value
  ): org.cobalt.api.util.helper.Rotation {
    val base = AngleUtils.getRotation(target)
    val elevPerBlock = when (activeCombatMode) {
      1    -> bowElevationPerBlock.value
      2    -> mageElevationPerBlock.value
      else -> return base
    }
    if (elevPerBlock <= 0.0) return base
    val dist = player.distanceTo(target).toFloat()
    val correction = (elevPerBlock * dist).toFloat().coerceIn(0f, 45f)
    return org.cobalt.api.util.helper.Rotation(base.yaw, base.pitch - correction)
  }

  private fun registerKillCandidate(target: LivingEntity) {
    val level = mc.level ?: return
    val normalizedName = normalizedTargetDisplayName(target)
    killCandidateId = target.uuid
    killCandidateName = resolveTargetDisplayName(target)
    killCandidateWasSlayerBoss = cryptZombieSlayer.value && isSlayerBossName(normalizedName)
    killCandidateWasSlayerMob =
      cryptZombieSlayer.value &&
        !killCandidateWasSlayerBoss &&
        (isSlayerPriorityMobName(normalizedName) || isSlayerFarmMobName(normalizedName))
    killCandidateAttackTick = level.gameTime
    killCandidateExpiresTick = level.gameTime + KILL_CANDIDATE_TTL_TICKS
  }

  private fun updateKillTracking(level: ClientLevel) {
    val candidateId = killCandidateId ?: return
    if (level.gameTime > killCandidateExpiresTick) {
      clearKillCandidate()
      return
    }

    val candidate = level.entitiesForRendering().firstOrNull { it.uuid == candidateId } as? LivingEntity
    if (candidate == null) {
      if (level.gameTime - killCandidateAttackTick >= KILL_DISAPPEAR_CONFIRM_TICKS) {
        onKillCandidateConfirmed(killCandidateName ?: "")
        clearKillCandidate()
      }
      return
    }
    if (candidate.isAlive && candidate.health > 0f) {
      return
    }

    onKillCandidateConfirmed(killCandidateName ?: resolveTargetDisplayName(candidate))
    clearKillCandidate()
  }

  private fun onKillCandidateConfirmed(resolvedName: String) {
    if (killCandidateWasSlayerMob) {
      slayerSessionMobKills++
      if (slayerTrackedQuestActive && !slayerBossActive) {
        slayerTrackedQuestSawMobKill = true
      }
    }
    if (autoLearnLastKill.value) {
      applyLearnedKillTarget(resolvedName)
    }
    if (oneTapMode.value) currentTargetId = null
  }

  private fun clearKillCandidate() {
    killCandidateId = null
    killCandidateName = null
    killCandidateWasSlayerMob = false
    killCandidateWasSlayerBoss = false
    killCandidateExpiresTick = 0L
    killCandidateAttackTick = 0L
  }

  private fun applyLearnedKillTarget(rawName: String) {
    val learnedName = sanitizeTargetName(rawName)
    if (learnedName.isBlank()) return
    var changed = false

    if (targetName.value != learnedName) {
      targetName.value = learnedName
      changed = true
    }
    if (lastKillText.value != learnedName) {
      lastKillText.value = learnedName
      changed = true
    }
    if (learnedWhitelist.add(learnedName.lowercase())) {
      changed = true
    }

    val serializedWhitelist = serializeWhitelist()
    if (learnedWhitelistText.value != serializedWhitelist) {
      learnedWhitelistText.value = serializedWhitelist
      changed = true
    }
    lastSyncedWhitelistRaw = learnedWhitelistText.value

    ChatUtils.sendMessage("Combat macro learned target: $learnedName")
    if (changed) {
      Config.saveModulesConfig()
    }
  }

  private fun sanitizeTargetName(raw: String): String {
    val noFormatting = raw.replace(Regex("\\u00A7."), "").trim()
    val noLevelPrefix = noFormatting.replace(Regex("^\\[[^\\]]+\\]\\s*"), "")
    val noSymbolsPrefix = noLevelPrefix.replace(Regex("^[^A-Za-z0-9]+"), "")
    val noHpSuffix = noSymbolsPrefix.replace(Regex("\\s+[0-9.,]+(?:/[0-9.,]+)?(?:[kKmMbB])?\\s*[\\u2764]?$"), "")
    return noHpSuffix.replace(Regex("\\s+"), " ").trim()
  }

  private fun normalizeNameForMatch(raw: String): String {
    return sanitizeTargetName(raw).lowercase()
  }

  private fun resolveTargetDisplayName(living: LivingEntity): String {
    val baseName = sanitizeTargetName(living.name.string)
    if (living !is Player) return baseName
    val standName = findAttachedArmorStandName(living)
    return if (standName.isNotBlank()) standName else baseName
  }

  private fun normalizedTargetDisplayName(living: LivingEntity): String =
    normalizeNameForMatch(resolveTargetDisplayName(living))

  private fun findAttachedArmorStandName(living: LivingEntity): String {
    val level = mc.level ?: return ""
    val bestStand =
      level.entitiesForRendering()
        .asSequence()
        .filterIsInstance<ArmorStand>()
        .filter { it.isAlive }
        .filter { stand ->
          stand.y >= living.y - 0.5 &&
            stand.y <= living.y + 3.5 &&
            horizontalDistSq(stand.x, stand.z, living.x, living.z) <= ATTACHED_NAMEPLATE_HORIZONTAL_RANGE_SQ
        }
        .map { stand -> stand to sanitizeTargetName(stand.name.string) }
        .filter { (_, name) -> name.isNotBlank() }
        .filter { (_, name) -> !builtInBlacklistedNames.contains(normalizeNameForMatch(name)) }
        .minByOrNull { (stand, _) ->
          horizontalDistSq(stand.x, stand.z, living.x, living.z) + kotlin.math.abs(stand.y - living.y)
        }
        ?: return ""

    return bestStand.second
  }

  private fun resolveCurrentTarget(): LivingEntity? {
    val level = mc.level ?: return null
    val targetId = currentTargetId ?: return null
    val liveTarget = level.entitiesForRendering().firstOrNull { it.uuid == targetId } as? LivingEntity ?: return null
    if (!liveTarget.isAlive || liveTarget.health <= 0f) return null
    return liveTarget
  }

  private fun resolveCurrentTargetName(): String? {
    return resolveCurrentTarget()?.let { target ->
      resolveTargetDisplayName(target).ifBlank { null }
    }
  }

  private fun formatHudHealth(value: Float): String {
    val rounded = (value * 10f).toInt() / 10f
    return if (rounded % 1f == 0f) {
      rounded.toInt().toString()
    } else {
      rounded.toString()
    }
  }

  private fun syncAutoItemToggles(player: Player) {
    var changed = false

    val wandInHotbar = findHotbarSlotByKeywords(player, SLAYER_WAND_OF_ATONEMENT_KEYWORDS) in 0..8
    if (wandInHotbar && !wandWasInHotbar && !autoWandOfAtonement.value) {
      autoWandOfAtonement.value = true
      changed = true
    }
    wandWasInHotbar = wandInHotbar

    val zombieSwordInHotbar = findHotbarSlotByKeywords(player, SLAYER_ZOMBIE_SWORD_KEYWORDS) in 0..8
    if (zombieSwordInHotbar && !zombieSwordWasInHotbar && !autoZombieSword.value) {
      autoZombieSword.value = true
      changed = true
    }
    zombieSwordWasInHotbar = zombieSwordInHotbar

    val overfluxInHotbar = findHotbarSlotByKeywords(player, SLAYER_OVERFLUX_KEYWORDS) in 0..8
    if (overfluxInHotbar && !overfluxWasInHotbar && !autoOverflux.value) {
      autoOverflux.value = true
      changed = true
    }
    overfluxWasInHotbar = overfluxInHotbar

    val ragnarokInHotbar = findHotbarSlotByKeywords(player, SLAYER_RAGNAROK_KEYWORDS) in 0..8
    if (ragnarokInHotbar && !ragnarokWasInHotbar && !autoRagnarok.value) {
      autoRagnarok.value = true
      changed = true
    }
    ragnarokWasInHotbar = ragnarokInHotbar

    val badHealthInHotbar = findHotbarSlotByKeywords(player, SLAYER_BAD_HEALTH_KEYWORDS) in 0..8
    if (badHealthInHotbar && !badHealthWasInHotbar && !autoSwordOfBadHealth.value) {
      autoSwordOfBadHealth.value = true
      changed = true
    }
    badHealthWasInHotbar = badHealthInHotbar

    if (changed) {
      Config.saveModulesConfig()
    }
  }

  private fun syncLearnedWhitelistFromSetting() {
    val raw = learnedWhitelistText.value
    if (raw == lastSyncedWhitelistRaw) return

    learnedWhitelist.clear()
    deserializeWhitelist(raw).forEach { learnedWhitelist.add(it) }
    learnedWhitelist.add(DEFAULT_WHITELIST_ENTRY)

    val normalized = serializeWhitelist()
    lastSyncedWhitelistRaw = normalized
    if (learnedWhitelistText.value != normalized) {
      learnedWhitelistText.value = normalized
    }
  }

  private fun deserializeWhitelist(raw: String): List<String> {
    if (raw.isBlank() || raw.equals("EMPTY", ignoreCase = true)) return emptyList()
    return raw
      .split(",")
      .asSequence()
      .map { it.trim().removeSuffix("...") }
      .filter { it.isNotEmpty() }
      .map(::normalizeNameForMatch)
      .filter { it.isNotBlank() && it != "empty" }
      .distinct()
      .toList()
  }

  private fun serializeWhitelist(): String {
    if (learnedWhitelist.isEmpty()) return "EMPTY"
    return learnedWhitelist.joinToString(", ")
  }

  private fun effectiveCombatMode(target: LivingEntity?): Int {
    if (target == null) return combatMode.value
    if (shouldUseSpiderPhaseBowCombat() && slayerBossActive && isSpiderBossHatchlingsPhaseActive()) {
      val targetName = normalizedTargetDisplayName(target)
      if (isSlayerBossName(targetName) || isSpiderPhaseAddName(targetName)) return 1
    }
    if (shouldUseEndermanDynamicCombat()) {
      val targetName = normalizedTargetDisplayName(target)
      if (!isSlayerBossName(targetName)) return effectiveEndermanSpawnCombatMode(target)
      return if (isEndermanBossHitPhase(target)) 1 else 0
    }
    return combatMode.value
  }

  private fun shouldUseZombieDynamicCombat(): Boolean {
    return cryptZombieSlayer.value && slayerType.value == 0 && zombieDynamicCombat.value
  }

  private fun shouldUseSpiderPhaseBowCombat(): Boolean {
    return cryptZombieSlayer.value && slayerType.value == 2 && spiderPhaseBowCombat.value
  }

  private fun shouldUseWolfPupsLogic(): Boolean {
    return cryptZombieSlayer.value && slayerType.value == 1
  }

  private fun shouldUseEndermanDynamicCombat(): Boolean {
    return cryptZombieSlayer.value && slayerType.value == 3 && endermanDynamicCombat.value
  }

  private fun effectiveEndermanSpawnCombatMode(target: LivingEntity): Int {
    return when (emanSpawnWeapon.value) {
      EMAN_SPAWN_WEAPON_ENDERMAN_SWORD -> 0
      EMAN_SPAWN_WEAPON_DYNAMIC -> if (shouldUseDynamicEndermanSpawnMelee(target)) 0 else 1
      else -> 1
    }
  }

  private fun shouldUseDynamicEndermanSpawnMelee(target: LivingEntity): Boolean {
    if (isSlayerPriorityMobName(normalizedTargetDisplayName(target))) return true
    val player = mc.player ?: return false
    return player.distanceTo(target).toDouble() <= emanDynamicSwapRange.value
  }


  private fun shouldSuppressSlayerAttack(target: LivingEntity, nowTick: Long): Boolean {
    if (!cryptZombieSlayer.value) return false
    val targetName = normalizedTargetDisplayName(target)
    if (!isSlayerBossName(targetName)) return false
    return isSpiderBossHatchlingsPhaseActive(nowTick) || isWolfBossPupsPhaseActive(nowTick)
  }

  private fun handleSlayerPhaseMovement(
    player: Player,
    target: LivingEntity,
    distanceToTarget: Double,
    hasLos: Boolean,
    activeCombatMode: Int,
    nowTick: Long
  ): Boolean {
    if (!cryptZombieSlayer.value) return false
    if (shouldUseEndermanDynamicCombat() && isEndermanBossHitPhase(target)) {
      val targetName = normalizedTargetDisplayName(target)
      if (isSlayerBossName(targetName)) {
        return applyEmanHitsPhaseStandoffMovement(player, target, distanceToTarget)
      }
    }
    if (shouldUseSpiderPhaseBowCombat() && activeCombatMode == 1 && hasLos && isSpiderBossHatchlingsPhaseActive(nowTick)) {
      val targetName = normalizedTargetDisplayName(target)
      if (isSlayerBossName(targetName) || isSpiderPhaseAddName(targetName)) {
        return applySpiderPhaseStepBackMovement(player, target, distanceToTarget)
      }
    }
    if (shouldUseWolfPupsLogic() && wolfIgnorePups.value && isWolfBossPupsPhaseActive(nowTick)) {
      val targetName = normalizedTargetDisplayName(target)
      if (isSlayerBossName(targetName)) {
        MovementManager.clearForcedMovement()
        return true
      }
    }
    return false
  }

  private fun applySpiderPhaseStepBackMovement(
    player: Player,
    target: LivingEntity,
    distanceToTarget: Double
  ): Boolean {
    val desiredDistance = max(SPIDER_PHASE_MIN_BACKSTEP_DISTANCE, combatEngageRange(1) - SPIDER_PHASE_STEPBACK_BUFFER)
    if (distanceToTarget > desiredDistance + SPIDER_PHASE_STEPBACK_HYSTERESIS) return false

    val rotation = rangedAimRotation(player, target, 1)
    RotationExecutor.rotateTo(rotation, rotationStrategy)
    val yawError = abs(AngleUtils.getRotationDelta(player.yRot, rotation.yaw))
    val pitchError = abs(rotation.pitch - player.xRot)
    val aligned = yawError <= SPIDER_PHASE_STEPBACK_YAW_TOLERANCE &&
      pitchError <= SPIDER_PHASE_STEPBACK_PITCH_TOLERANCE

    if (aligned && distanceToTarget < desiredDistance) {
      MovementManager.setForcedMovement(
        forward = false, backward = true,
        left = false, right = false,
        jump = false, shift = false, sprint = false
      )
    } else {
      MovementManager.clearForcedMovement()
    }
    return true
  }

  private fun applyEmanHitsPhaseStandoffMovement(
    player: Player,
    target: LivingEntity,
    distanceToTarget: Double
  ): Boolean {
    val standoffMin = EMAN_HITS_PHASE_MIN_STANDOFF
    val standoffMax = attackRange.value + EMAN_HITS_PHASE_STANDOFF_BUFFER
    if (distanceToTarget > standoffMax + EMAN_HITS_PHASE_STANDOFF_HYSTERESIS) return false
    val rotation = AngleUtils.getRotation(target)
    RotationExecutor.rotateTo(rotation, rotationStrategy)
    val yawError = abs(AngleUtils.getRotationDelta(player.yRot, rotation.yaw))
    val aligned = yawError <= EMAN_HITS_PHASE_YAW_TOLERANCE
    if (aligned && distanceToTarget < standoffMin) {
      MovementManager.setForcedMovement(
        forward = false, backward = true,
        left = false, right = false,
        jump = false, shift = false, sprint = false
      )
    } else {
      MovementManager.clearForcedMovement()
    }
    return true
  }

  private fun updateSlayerBossPhaseState(level: ClientLevel, boss: LivingEntity?) {
    updateSpiderHatchlingsPhaseState(level, boss)
    updateWolfPupsPhaseState(level, boss)
  }

  private fun resolveSlayerPhaseTarget(
    player: Player,
    level: ClientLevel,
    boss: LivingEntity?
  ): LivingEntity? {
    val wantsSpiderAdds = shouldUseSpiderPhaseBowCombat() && isSpiderBossHatchlingsPhaseActive(level.gameTime)
    val wantsWolfAdds = shouldUseWolfPupsLogic() && !wolfIgnorePups.value && isWolfBossPupsPhaseActive(level.gameTime)
    if (!wantsSpiderAdds && !wantsWolfAdds) return null

    val bossPos = boss?.position() ?: slayerBossLastPos ?: return null
    val blacklisted = builtInBlacklistedNames
    var best: LivingEntity? = null
    var bestDistSq = Double.POSITIVE_INFINITY

    for (entity in level.entitiesForRendering()) {
      val living = entity as? LivingEntity ?: continue
      if (!isValidTarget(living, player, blacklisted, "", true, ignoreWhitelist = true)) continue
      val name = normalizedTargetDisplayName(living)
      if (isSlayerBossName(name)) continue

      val matchesPhaseTarget =
        when {
          wantsSpiderAdds -> isSpiderPhaseAddName(name)
          wantsWolfAdds -> isWolfPhaseAddName(name)
          else -> false
        }
      if (!matchesPhaseTarget) continue
      if (horizontalDistSq(living.x, living.z, bossPos.x, bossPos.z) > SLAYER_PHASE_ADD_SEARCH_RADIUS_SQ) continue

      val dx = living.x - player.x
      val dy = living.y - player.y
      val dz = living.z - player.z
      val distSq = dx * dx + dy * dy + dz * dz
      if (distSq < bestDistSq) {
        best = living
        bestDistSq = distSq
      }
    }

    return best
  }

  private fun activateSpiderHatchlingsPhase(nowTick: Long) {
    if (!shouldUseSpiderPhaseBowCombat()) return
    spiderHatchlingsPhaseActive = true
    spiderHatchlingsPhaseUntilTick =
      if (nowTick >= 0L) nowTick + SPIDER_HATCHLING_PHASE_FALLBACK_TICKS else -1L
    val boss = resolveNearestSlayerBoss()
    spiderHatchlingsBossId = boss?.uuid
    spiderHatchlingsBossAnchor = boss?.position() ?: slayerBossLastPos
  }

  private fun clearSpiderHatchlingsPhase() {
    spiderHatchlingsPhaseActive = false
    spiderHatchlingsPhaseUntilTick = -1L
    spiderHatchlingsBossId = null
    spiderHatchlingsBossAnchor = null
  }

  private fun updateSpiderHatchlingsPhaseState(level: ClientLevel, boss: LivingEntity?) {
    if (!shouldUseSpiderPhaseBowCombat()) {
      clearSpiderHatchlingsPhase()
      return
    }
    if (!spiderHatchlingsPhaseActive) return
    if (!slayerBossActive) {
      clearSpiderHatchlingsPhase()
      return
    }

    val trackedBoss = resolveTrackedSpiderPhaseBoss(level, boss)
    if (trackedBoss != null) {
      if (spiderHatchlingsBossId == null) spiderHatchlingsBossId = trackedBoss.uuid
      if (spiderHatchlingsBossAnchor == null) spiderHatchlingsBossAnchor = trackedBoss.position()
      val anchor = spiderHatchlingsBossAnchor
      if (anchor != null && trackedBoss.position().distanceTo(anchor) >= SPIDER_HATCHLING_CLEAR_DISTANCE) {
        clearSpiderHatchlingsPhase()
        return
      }
    }

    if (spiderHatchlingsPhaseUntilTick >= 0L && level.gameTime >= spiderHatchlingsPhaseUntilTick) {
      if (trackedBoss != null && hasNearbySpiderPhaseAdds(level, trackedBoss)) {
        spiderHatchlingsPhaseUntilTick = level.gameTime + SPIDER_HATCHLING_PHASE_REFRESH_TICKS
      } else {
        clearSpiderHatchlingsPhase()
      }
    }
  }

  private fun updateWolfPupsPhaseState(level: ClientLevel, boss: LivingEntity?) {
    if (!shouldUseWolfPupsLogic()) {
      wolfPupsPhaseUntilTick = -1L
      return
    }
    if (!slayerBossActive || boss == null) {
      wolfPupsPhaseUntilTick = -1L
      return
    }

    if (hasNearbyAttachedText(boss, SLAYER_WOLF_PUPS_TEXT_KEYWORDS)) {
      wolfPupsPhaseUntilTick = level.gameTime + SLAYER_WOLF_PUPS_PHASE_TICKS
    } else if (wolfPupsPhaseUntilTick >= 0L && level.gameTime >= wolfPupsPhaseUntilTick) {
      wolfPupsPhaseUntilTick = -1L
    }
  }

  private fun isSpiderBossHatchlingsPhaseActive(nowTick: Long = mc.level?.gameTime ?: -1L): Boolean {
    if (!shouldUseSpiderPhaseBowCombat() || !spiderHatchlingsPhaseActive) return false
    return spiderHatchlingsPhaseUntilTick < 0L || nowTick < spiderHatchlingsPhaseUntilTick
  }

  private fun isWolfBossPupsPhaseActive(nowTick: Long = mc.level?.gameTime ?: -1L): Boolean {
    if (!shouldUseWolfPupsLogic()) return false
    return wolfPupsPhaseUntilTick >= 0L && nowTick < wolfPupsPhaseUntilTick
  }

  private fun resolveNearestSlayerBoss(): LivingEntity? {
    val level = mc.level ?: return null
    val player = mc.player
    return level.entitiesForRendering()
      .asSequence()
      .filterIsInstance<LivingEntity>()
      .filter { it.isAlive && it.health > 0f }
      .filter { entity -> isSlayerBossName(normalizedTargetDisplayName(entity)) }
      .minByOrNull { entity ->
        if (player != null) {
          val dx = entity.x - player.x
          val dy = entity.y - player.y
          val dz = entity.z - player.z
          dx * dx + dy * dy + dz * dz
        } else {
          0.0
        }
      }
  }

  private fun resolveTrackedSpiderPhaseBoss(level: ClientLevel, fallbackBoss: LivingEntity?): LivingEntity? {
    val trackedId = spiderHatchlingsBossId
    if (trackedId != null) {
      val tracked = level.entitiesForRendering().firstOrNull { it.uuid == trackedId } as? LivingEntity
      if (tracked != null && tracked.isAlive && tracked.health > 0f) {
        return tracked
      }
    }
    return fallbackBoss?.takeIf { it.isAlive && it.health > 0f && isSlayerBossName(normalizedTargetDisplayName(it)) }
  }

  private fun hasNearbySpiderPhaseAdds(level: ClientLevel, boss: LivingEntity): Boolean {
    val bossPos = boss.position()
    return level.entitiesForRendering()
      .asSequence()
      .filterIsInstance<LivingEntity>()
      .filter { it.isAlive && it.health > 0f }
      .filter { entity -> !isSlayerBossName(normalizedTargetDisplayName(entity)) }
      .any { entity ->
        val name = normalizedTargetDisplayName(entity)
        isSpiderPhaseAddName(name) &&
          horizontalDistSq(entity.x, entity.z, bossPos.x, bossPos.z) <= SLAYER_PHASE_ADD_SEARCH_RADIUS_SQ
      }
  }

  private fun hasNearbyAttachedText(target: LivingEntity, keywords: Array<String>): Boolean {
    val level = mc.level ?: return false
    return level.entitiesForRendering()
      .asSequence()
      .filterIsInstance<ArmorStand>()
      .filter { it.isAlive }
      .filter { stand ->
        stand.y >= target.y - 0.5 &&
          stand.y <= target.y + 3.5 &&
          horizontalDistSq(stand.x, stand.z, target.x, target.z) <= ATTACHED_NAMEPLATE_HORIZONTAL_RANGE_SQ
      }
      .map { stand -> normalizeNameForMatch(stand.name.string) }
      .any { text -> keywords.any { keyword -> text.contains(keyword) } }
  }

  private fun isSpiderPhaseAddName(normalizedName: String): Boolean {
    return SLAYER_SPIDER_PHASE_ADD_KEYWORDS.any { keyword -> normalizedName.contains(keyword) }
  }

  private fun isWolfPhaseAddName(normalizedName: String): Boolean {
    return SLAYER_WOLF_PHASE_ADD_KEYWORDS.any { keyword -> normalizedName.contains(keyword) }
  }

  private fun isEndermanBossHitPhase(target: LivingEntity): Boolean {
    if (!shouldUseEndermanDynamicCombat()) return false
    if (!isSlayerBossName(normalizedTargetDisplayName(target))) return false
    val level = mc.level ?: return false
    return level.entitiesForRendering()
      .asSequence()
      .filterIsInstance<ArmorStand>()
      .filter { it.isAlive }
      .filter { stand ->
        stand.y >= target.y - 0.5 &&
          stand.y <= target.y + 3.5 &&
          horizontalDistSq(stand.x, stand.z, target.x, target.z) <= ENDERMAN_HITS_HORIZONTAL_RANGE_SQ
      }
      .map { stand -> normalizeNameForMatch(stand.name.string) }
      .any { name -> ENDERMAN_HITS_PATTERN.containsMatchIn(name) }
  }

  private fun keywordSettingValues(raw: String, fallback: Array<String>): Array<String> {
    val parsed = raw
      .split(',', ';', '\n')
      .map(::normalizeNameForMatch)
      .filter { it.isNotBlank() }
      .distinct()
    return if (parsed.isEmpty()) fallback else parsed.toTypedArray()
  }

  private fun preferredEndermanSpawnWeaponKeywords(target: LivingEntity?): Array<String> {
    return when (emanSpawnWeapon.value) {
      EMAN_SPAWN_WEAPON_TERMINATOR -> SLAYER_ENDERMAN_TERMINATOR_WEAPON_KEYWORDS
      EMAN_SPAWN_WEAPON_SHORTBOW -> SLAYER_ENDERMAN_SHORTBOW_WEAPON_KEYWORDS
      EMAN_SPAWN_WEAPON_ENDERMAN_SWORD ->
        keywordSettingValues(emanBossWeapon.value, SLAYER_ENDERMAN_BOSS_WEAPON_DEFAULT_KEYWORDS)
      EMAN_SPAWN_WEAPON_DYNAMIC ->
        if (target != null && shouldUseDynamicEndermanSpawnMelee(target)) {
          keywordSettingValues(emanBossWeapon.value, SLAYER_ENDERMAN_BOSS_WEAPON_DEFAULT_KEYWORDS)
        } else {
          SLAYER_ENDERMAN_DYNAMIC_BOW_WEAPON_KEYWORDS
        }
      else -> SLAYER_ENDERMAN_DYNAMIC_BOW_WEAPON_KEYWORDS
    }
  }

  private fun preferredSlayerWeaponKeywords(activeCombatMode: Int, target: LivingEntity?): Array<String> {
    return when {
      shouldUseZombieDynamicCombat() && slayerBossActive ->
        keywordSettingValues(zombieDynamicSword.value, SLAYER_ZOMBIE_DYNAMIC_SWORD_DEFAULT_KEYWORDS)
      shouldUseZombieDynamicCombat() ->
        keywordSettingValues(zombieSpawnWeapon.value, SLAYER_ZOMBIE_SPAWN_WEAPON_DEFAULT_KEYWORDS)
      shouldUseEndermanDynamicCombat() && target != null &&
        isSlayerBossName(normalizedTargetDisplayName(target)) && isEndermanBossHitPhase(target) ->
        keywordSettingValues(emanHitPhaseWeapon.value, SLAYER_ENDERMAN_HIT_PHASE_WEAPON_DEFAULT_KEYWORDS)
      shouldUseEndermanDynamicCombat() && target != null &&
        isSlayerBossName(normalizedTargetDisplayName(target)) ->
        keywordSettingValues(emanBossWeapon.value, SLAYER_ENDERMAN_BOSS_WEAPON_DEFAULT_KEYWORDS)
      shouldUseEndermanDynamicCombat() ->
        preferredEndermanSpawnWeaponKeywords(target)
      activeCombatMode == 0 -> SLAYER_MELEE_WEAPON_KEYWORDS
      activeCombatMode == 1 -> SLAYER_BOW_WEAPON_KEYWORDS
      activeCombatMode == 2 -> SLAYER_MAGE_WEAPON_KEYWORDS
      else -> SLAYER_ANY_WEAPON_KEYWORDS
    }
  }

  private fun findPreferredSlayerWeaponSlot(player: Player, activeCombatMode: Int, target: LivingEntity?): Int {
    val primaryKeywords = preferredSlayerWeaponKeywords(activeCombatMode, target)
    val primarySlot = findHotbarSlotByKeywords(player, primaryKeywords)
    if (primarySlot in 0..8) return primarySlot
    return findHotbarSlotByKeywords(player, SLAYER_ANY_WEAPON_KEYWORDS)
  }

  private fun isSlayerWeaponForMode(normalizedName: String, activeCombatMode: Int, target: LivingEntity?): Boolean {
    val keywords = preferredSlayerWeaponKeywords(activeCombatMode, target)
    return keywords.any { keyword -> normalizedName.contains(keyword) }
  }

  private fun ensurePreferredWeapon(player: Player, target: LivingEntity, nowTick: Long) {
    val name = normalizedTargetDisplayName(target)
    if (!name.contains("ice walker")) return

    val selected = player.inventory.getItem(player.inventory.selectedSlot)
    val selectedName = selected.hoverName?.string.orEmpty().lowercase()
    if (selectedName.contains("drill")) return

    val drillSlot = InventoryUtils.findItemInHotbar("drill")
    if (drillSlot in 0..8) {
      InventoryUtils.holdHotbarSlot(drillSlot)
      return
    }

    if (nowTick - drillWarnTick >= DRILL_WARN_INTERVAL_TICKS) {
      drillWarnTick = nowTick
      ChatUtils.sendMessage("Combat macro: no drill found in hotbar for Ice Walker target.")
    }
  }

  /** Ensures a weapon is selected when in slayer mode. Switches away from utility items
   *  (batphone, overflux, etc.) to the nearest sword/weapon in the hotbar. */
  private fun ensureSlayerWeapon(player: Player, target: LivingEntity?) {
    if (pendingHealRelease || pendingHealRestoreSlot >= 0) return
    val activeCombatMode = effectiveCombatMode(target)
    val currentSlot = player.inventory.selectedSlot
    val current = player.inventory.getItem(currentSlot)
    val currentName = normalizeNameForMatch(current.hoverName?.string.orEmpty())
    if (!current.isEmpty) {
      if (isSlayerWeaponForMode(currentName, activeCombatMode, target)) return
      val isUtility = SLAYER_NON_WEAPON_KEYWORDS.any { currentName.contains(it) }
      val preferredSlot = findPreferredSlayerWeaponSlot(player, activeCombatMode, target)
      if (preferredSlot in 0..8 && preferredSlot != currentSlot) {
        InventoryUtils.holdHotbarSlot(preferredSlot)
        return
      }
      if (!isUtility) return
    }
    val weaponSlot = findPreferredSlayerWeaponSlot(player, activeCombatMode, target)
    if (weaponSlot in 0..8 && weaponSlot != currentSlot) {
      InventoryUtils.holdHotbarSlot(weaponSlot)
    }
  }

  private fun tryAutoHeal(player: Player, nowTick: Long) {
    if (!autoHeal.value) return
    val effectiveHealth = player.health + player.absorptionAmount
    if (effectiveHealth > healAtHealth.value.toFloat()) return
    if (nowTick - lastHealUseTick < AUTO_HEAL_COOLDOWN_TICKS) return

    val healSlot = findHealHotbarSlot(player)
    if (healSlot !in 0..8) return

    lastHealUseTick = nowTick
    val previousSlot = player.inventory.selectedSlot

    if (previousSlot != healSlot) {
      InventoryUtils.holdHotbarSlot(healSlot)
    }

    mc.options.keyUse?.setDown(true)
    pendingHealRelease = true
    pendingHealRestoreSlot = if (previousSlot != healSlot) previousSlot else -1
  }

  private fun findHealHotbarSlot(player: Player): Int {
    if (autoWandOfAtonement.value) {
      val wandSlot = findHotbarSlotByKeywords(player, SLAYER_WAND_OF_ATONEMENT_KEYWORDS)
      if (wandSlot in 0..8) {
        return wandSlot
      }
    }

    if (autoZombieSword.value) {
      val zombieSwordSlot = findHotbarSlotByKeywords(player, SLAYER_ZOMBIE_SWORD_KEYWORDS)
      if (zombieSwordSlot in 0..8) {
        return zombieSwordSlot
      }
    }

    return -1
  }

  private fun currentTargetRenderColor(): Color {
    val phase = (System.currentTimeMillis() % TARGET_BOX_CYCLE_MS).toDouble() / TARGET_BOX_CYCLE_MS.toDouble()
    val t = 0.5 - 0.5 * cos(phase * Math.PI * 2.0)
    val r = lerpChannel(CYAN_COLOR.red, PINK_COLOR.red, t)
    val g = lerpChannel(CYAN_COLOR.green, PINK_COLOR.green, t)
    val b = lerpChannel(CYAN_COLOR.blue, PINK_COLOR.blue, t)
    return Color(r, g, b, TARGET_BOX_ALPHA)
  }

  private fun lerpChannel(start: Int, end: Int, t: Double): Int {
    return (start + (end - start) * t).toInt().coerceIn(0, 255)
  }

  private fun shouldUseDirectChase(target: LivingEntity, hasLos: Boolean, distanceToTarget: Double): Boolean {
    val targetName = normalizedTargetDisplayName(target)
    val slayerGapBonus =
      when {
        cryptZombieSlayer.value && isSlayerBossName(targetName) -> SLAYER_BOSS_DIRECT_CHASE_GAP_BONUS
        cryptZombieSlayer.value && isSlayerPriorityMobName(targetName) -> SLAYER_MINIBOSS_DIRECT_CHASE_GAP_BONUS
        else -> 0.0
      }
    val handoffGap = SOFT_CHASE_HANDOFF_GAP + slayerGapBonus
    val stickyExitGap = SOFT_CHASE_STICKY_EXIT_GAP + slayerGapBonus
    val losGraceGap = SOFT_CHASE_LOS_GRACE_GAP + min(slayerGapBonus, SOFT_CHASE_MAX_GAP)

    val enterDirectChase = hasLos && distanceToTarget <= attackRange.value + handoffGap
    val stayDirectChase =
      closeChaseActive &&
        closeChaseTargetId == target.uuid &&
        distanceToTarget <= attackRange.value + stickyExitGap &&
        (hasLos || distanceToTarget <= attackRange.value + losGraceGap)

    val active = enterDirectChase || stayDirectChase
    closeChaseActive = active
    closeChaseTargetId = if (active) target.uuid else null
    return active
  }

  private fun resetCloseChase() {
    closeChaseActive = false
    closeChaseTargetId = null
    losScanLastFlipTick = 0L
  }

  private fun renderSlayerHighlights(context: org.cobalt.api.event.impl.render.WorldRenderContext, level: ClientLevel) {
    val espMode = slayerEspTargets.value
    for (entity in level.entitiesForRendering()) {
      val living = entity as? LivingEntity ?: continue
      if (!living.isAlive || living.health <= 0f) continue

      val highlightType = slayerHighlightType(living) ?: continue
      if (!slayerHighlightMatchesMode(highlightType, espMode)) continue

      drawSlayerAura(context, living.boundingBox.inflate(TARGET_BOX_INFLATE), highlightType)
    }
  }

  private fun slayerHighlightType(entity: LivingEntity): SlayerHighlightType? {
    val normalizedName = normalizedTargetDisplayName(entity)
    return when {
      isSlayerBossName(normalizedName) -> SlayerHighlightType.BOSS
      isSlayerPriorityMobName(normalizedName) -> SlayerHighlightType.MINIBOSS
      else -> null
    }
  }

  private fun slayerHighlightMatchesMode(type: SlayerHighlightType, mode: Int): Boolean =
    when (mode) {
      0 -> type == SlayerHighlightType.BOSS
      1 -> type == SlayerHighlightType.MINIBOSS
      else -> true
    }

  private fun drawSlayerAura(
    context: org.cobalt.api.event.impl.render.WorldRenderContext,
    box: net.minecraft.world.phys.AABB,
    type: SlayerHighlightType,
  ) {
    val palette =
      when (type) {
        SlayerHighlightType.BOSS ->
          SlayerEspPalette(
            stroke = Color(255, 114, 186, 235),
            fill = Color(255, 114, 186, 52),
            outerStroke = Color(110, 226, 255, 170),
            outerFill = Color(110, 226, 255, 18)
          )
        SlayerHighlightType.MINIBOSS ->
          SlayerEspPalette(
            stroke = Color(255, 219, 109, 220),
            fill = Color(255, 219, 109, 42),
            outerStroke = Color(255, 158, 86, 150),
            outerFill = Color(255, 158, 86, 14)
          )
      }

    Render3D.drawStyledBox(
      context,
      box.inflate(SLAYER_ESP_OUTER_INFLATE),
      palette.outerStroke,
      palette.outerFill,
      esp = true,
      lineWidth = 3.2f
    )
    Render3D.drawStyledBox(
      context,
      box.inflate(SLAYER_ESP_MID_INFLATE),
      palette.stroke,
      palette.fill,
      esp = true,
      lineWidth = 2.8f
    )
    Render3D.drawStyledBox(
      context,
      box,
      palette.stroke,
      palette.fill,
      esp = true,
      lineWidth = 2.2f
    )
  }

  private fun slayerBossDisplayName(): String =
    when (slayerType.value) {
      0 -> if (slayerTier.value.toInt() >= 5) "Atoned Horror" else "Revenant Horror"
      1 -> "Sven Packmaster"
      2 -> "Tarantula Broodfather"
      3 -> "Voidgloom Seraph"
      4 -> "Riftstalker Bloodfiend"
      5 -> "Inferno Demonlord"
      else -> "Slayer Boss"
    }

  private fun applyLosStrafeScan(nowTick: Long) {
    if (losScanLastFlipTick == 0L) {
      losScanLastFlipTick = nowTick
    }
    if (nowTick - losScanLastFlipTick >= LOS_SCAN_FLIP_TICKS) {
      losScanStrafeRight = !losScanStrafeRight
      losScanLastFlipTick = nowTick
    }
    MovementManager.setForcedMovement(
      forward = false, backward = false,
      left = !losScanStrafeRight, right = losScanStrafeRight,
      jump = false, shift = false, sprint = false
    )
  }

  private fun handleSoftChaseMovement(player: Player, target: LivingEntity, distanceToTarget: Double) {
    val rotation = AngleUtils.getRotation(target)
    RotationExecutor.rotateTo(rotation, rotationStrategy)

    val yawError = abs(AngleUtils.getRotationDelta(player.yRot, rotation.yaw))
    val pitchError = abs(rotation.pitch - player.xRot)
    val aligned = yawError <= SOFT_CHASE_YAW_TOLERANCE && pitchError <= SOFT_CHASE_PITCH_TOLERANCE
    val shouldStepForward = distanceToTarget > combatKeepDistance(0) + SOFT_CHASE_STOP_BUFFER

    if (aligned && shouldStepForward) {
      MovementManager.setForcedMovement(
        forward = true, backward = false,
        left = false, right = false,
        jump = false, shift = false, sprint = true
      )
    } else {
      MovementManager.clearForcedMovement()
    }
  }

  private fun isAttackReady(player: Player, target: LivingEntity): Boolean {
    val cooldown = player.getAttackStrengthScale(0.0f)
    if (cooldown < minAttackCooldown.value.toFloat()) return false
    if (requireLos.value && !player.hasLineOfSight(target)) return false

    val rotation = AngleUtils.getRotation(target)
    val yawError = abs(AngleUtils.getRotationDelta(player.yRot, rotation.yaw))
    val pitchError = abs(rotation.pitch - player.xRot)
    val tolerance = aimTolerance.value
    if (yawError > tolerance || pitchError > tolerance) return false

    return true
  }

  private fun updateStuck(
    player: Player,
    inAttackRange: Boolean,
    level: ClientLevel
  ) {
    if (inAttackRange || !startedPath || !nativeActive()) {
      stuckTicks = 0
      stuckRepathCount = 0
      lastMoveX = player.x
      lastMoveY = player.y
      lastMoveZ = player.z
      return
    }

    val dx = player.x - lastMoveX
    val dy = player.y - lastMoveY
    val dz = player.z - lastMoveZ
    val moved = dx * dx + dy * dy + dz * dz > 0.0008
    if (moved) {
      stuckTicks = 0
      stuckRepathCount = 0
      lastMoveX = player.x
      lastMoveY = player.y
      lastMoveZ = player.z
      return
    }

    stuckTicks++
    if (stuckTicks < stuckTicksSetting.value.toInt()) {
      return
    }
    stuckTicks = 0

    val maxRepaths = stuckRepathTries.value.toInt().coerceAtLeast(0)
    if (stuckRepathCount < maxRepaths) {
      val target =
        currentTargetId?.let { id ->
          level.entitiesForRendering().firstOrNull { it.uuid == id } as? LivingEntity
        }
      if (target != null && target.isAlive && target.health > 0f) {
        nativeStop()
        val targetPos = target.blockPosition()
        val restarted = startCombatPathToTarget(level, targetPos, effectiveCombatMode(target))
        if (restarted) {
          startedPath = true
          lastTargetPos = targetPos
          lastPathStartTick = level.gameTime
          stuckRepathCount++
          return
        }
      }
    }

    stuckRepathCount = 0
    if (warpOnStuck.value) {
      if (cryptZombieSlayer.value) {
        ChatUtils.sendMessage("Combat macro stuck. Triggering walkback.")
        startAreaOrigin = null
        stuckTicks = 0
        slayerEnteredCrypt = false
        triggerWalkToFarmArea(justFarm = false)
      } else {
        nativeStop()
        (player as? LocalPlayer)?.connection?.sendCommand("warp hub")
        ChatUtils.sendMessage("Combat macro stuck. Warping to hub.")
        stopMacro()
      }
    }
  }

  private fun startCombatPathToTarget(
    level: ClientLevel,
    targetPos: BlockPos,
    activeCombatMode: Int = combatMode.value
  ): Boolean {
    val resolved = findNearestWalkableAround(level, targetPos, CHASE_RESOLVE_RADIUS, CHASE_RESOLVE_VERTICAL) ?: targetPos
    val radius = combatPathRadius(activeCombatMode)
    if (!nativeActive()) {
      MovementManager.clearForcedMovement()
      RotationExecutor.stopRotating()
    }
    NativePathfinder.setTargetWithRadius(resolved.x + 0.5, resolved.y.toDouble(), resolved.z + 0.5, radius)
    return true
  }

  private fun combatEngageRange(activeCombatMode: Int = combatMode.value): Double {
    return when (activeCombatMode) {
      1 -> max(attackRange.value, bowMinRange.value.coerceAtLeast(0.0))
      else -> attackRange.value
    }
  }

  private fun combatKeepDistance(activeCombatMode: Int = combatMode.value): Double {
    return when (activeCombatMode) {
      0 -> {
        val configured = swordKeepDistance.value.coerceAtLeast(0.0)
        val desired = if (configured <= 0.0) MELEE_CLOSE_IN_DISTANCE else configured
        min(attackRange.value, desired)
      }
      1 -> combatEngageRange(activeCombatMode)
      else -> attackRange.value
    }
  }

  private fun combatPathRadius(activeCombatMode: Int = combatMode.value): Double {
    return (combatKeepDistance(activeCombatMode) - COMBAT_PATH_RADIUS_BUFFER).coerceAtLeast(COMBAT_MIN_STANDOFF)
  }

  private fun beginSlayerSession() {
    slayerSessionStartMs = System.currentTimeMillis()
    slayerSessionMobKills = 0
    slayerSessionQuestCompletions = 0
    slayerSessionQuestFailures = 0
    clearTrackedSlayerQuestState(clearIdentity = true)
  }

  private fun startTrackedSlayerQuest(typeIdx: Int, tier: Int) {
    slayerTrackedQuestActive = true
    slayerTrackedQuestType = typeIdx.takeIf { it in slayerType.options.indices } ?: slayerType.value
    slayerTrackedQuestTier = tier.takeIf { it in 1..5 } ?: slayerTier.value.toInt().coerceIn(1, 5)
    slayerTrackedQuestProgressKills = 0
    slayerTrackedQuestTargetKills = 0
    slayerTrackedQuestSawMobKill = false
    slayerTrackedQuestBossSeen = false
    slayerTrackedQuestCompletionPendingRestart = false
  }

  private fun clearTrackedSlayerQuestState(clearIdentity: Boolean) {
    slayerTrackedQuestActive = false
    slayerTrackedQuestProgressKills = 0
    slayerTrackedQuestTargetKills = 0
    slayerTrackedQuestSawMobKill = false
    slayerTrackedQuestBossSeen = false
    slayerTrackedQuestCompletionPendingRestart = false
    if (clearIdentity) {
      slayerTrackedQuestType = -1
      slayerTrackedQuestTier = 0
    }
  }

  private fun completeTrackedSlayerQuest() {
    if (slayerTrackedQuestCompletionPendingRestart) return
    slayerSessionQuestCompletions++
    slayerTrackedQuestActive = false
    slayerTrackedQuestSawMobKill = true
    slayerTrackedQuestBossSeen = true
    slayerTrackedQuestCompletionPendingRestart = true
  }

  private fun queueSlayerQuestRestart(countFailure: Boolean = true) {
    if (countFailure) {
      markTrackedSlayerQuestFailedIfNeeded()
    }
    slayerNeedsQuestRestart = true
  }

  private fun markTrackedSlayerQuestFailedIfNeeded() {
    if (!slayerTrackedQuestActive) return
    if (slayerTrackedQuestCompletionPendingRestart || slayerNeedsQuestClaim) return
    slayerSessionQuestFailures++
    clearTrackedSlayerQuestState(clearIdentity = false)
  }

  private fun trackedSlayerQuestLabel(): String {
    val typeIndex =
      when {
        slayerTrackedQuestType in slayerType.options.indices -> slayerTrackedQuestType
        slayerType.value in slayerType.options.indices -> slayerType.value
        else -> return "--"
      }
    val tier =
      when {
        slayerTrackedQuestTier in 1..5 -> slayerTrackedQuestTier
        else -> slayerTier.value.toInt().coerceIn(1, 5)
      }
    return "${slayerType.options[typeIndex]} ${romanTierLabel(tier)}"
  }

  private fun trackedSlayerQuestHudStageLabel(): String =
    when {
      slayerNeedsQuestClaim || slayerTrackedQuestCompletionPendingRestart || slayerBossActive || slayerQuestReady || slayerTrackedQuestBossSeen ->
        "Killing Boss"
      slayerTrackedQuestSawMobKill || slayerTrackedQuestProgressKills > 0 -> "Killing Mobs"
      else -> "Started"
    }

  private fun currentSlayerKillsLeft(): Int? {
    if (slayerNeedsQuestClaim || slayerTrackedQuestCompletionPendingRestart || slayerBossActive || slayerQuestReady || slayerTrackedQuestBossSeen) {
      return 0
    }
    val targetKills = slayerTrackedQuestTargetKills
    if (targetKills <= 0) return null
    return (targetKills - slayerTrackedQuestProgressKills).coerceAtLeast(0)
  }

  private fun formatSessionRateDisplay(count: Int): String {
    val sessionStart = slayerSessionStartMs
    if (sessionStart <= 0L || count <= 0) return "0/h"
    val elapsedMs = (System.currentTimeMillis() - sessionStart).coerceAtLeast(1000L)
    val perHour = count * 3_600_000.0 / elapsedMs.toDouble()
    return if (perHour >= 100.0 || abs(perHour - perHour.toInt()) < 0.05) {
      "${perHour.toInt()}/h"
    } else {
      String.format(Locale.ROOT, "%.1f/h", perHour)
    }
  }

  private fun romanTierLabel(tier: Int): String =
    when (tier.coerceIn(1, 5)) {
      1 -> "I"
      2 -> "II"
      3 -> "III"
      4 -> "IV"
      5 -> "V"
      else -> "I"
    }

  /**
   * Navigate back to the farming area using the configured walkback route.
   * [justFarm] = true  -> just walk in; don't restart the quest after arriving.
   * [justFarm] = false -> buy a new quest once walkback completes.
   */
  private fun triggerWalkToFarmArea(justFarm: Boolean) {
    if (CombatPatrolModule.isPatrolRunning) CombatPatrolModule.stopPatrol()
    if (PathfindingModule.isPatrolActive) PathfindingModule.stopPatrol()
    if (slayerNeedsWalkback) WalkbackBridge.stopWalkback?.invoke()
    nativeStop()
    startedPath = false
    lastTargetPos = null
    currentTargetId = null
    stuckTicks = 0
    stuckRepathCount = 0
    RotationExecutor.stopRotating()
    MovementManager.clearForcedMovement()
    val routeName = when {
      slayerLocation.value == 1 -> CRYPT_WALKBACK_ROUTE_NAME
      slayerWalkbackRoute.value.isNotBlank() -> slayerWalkbackRoute.value.trim()
      else -> ""
    }
    if (routeName.isBlank()) {
      if (!justFarm) queueSlayerQuestRestart(countFailure = false)
      return
    }
    val started = WalkbackBridge.startWalkback?.invoke(routeName, 0, false) ?: false
    if (started) {
      slayerNeedsWalkback = true
      slayerWalkbackJustFarm = justFarm
    } else {
      ChatUtils.sendMessage("Combat macro: walkback route \"$routeName\" not found.")
      if (!justFarm) queueSlayerQuestRestart(countFailure = false)
    }
  }

  private fun findNearestWalkableAround(
    level: ClientLevel,
    origin: BlockPos,
    radius: Int,
    vertical: Int
  ): BlockPos? {
    var best: BlockPos? = null
    var bestDistSq = Double.POSITIVE_INFINITY
    for (dy in -vertical..vertical) {
      for (dx in -radius..radius) {
        for (dz in -radius..radius) {
          val pos = origin.offset(dx, dy, dz)
          if (!MinecraftPathingRules.isWalkable(level, pos)) continue
          val distSq = pos.distSqr(origin).toDouble()
          if (distSq < bestDistSq) {
            bestDistSq = distSq
            best = pos
          }
        }
      }
    }
    return best
  }

  private fun nativeActive(): Boolean =
    NativePathfinder.status.let { it != PathStatus.IDLE && it != PathStatus.ARRIVED && it != PathStatus.FAILED }

  private fun nativeStop() {
    NativePathfinder.stop()
    MovementManager.setMovementLock(false)
  }

  private fun stopMacro() {
    if (PathfindingModule.isPatrolActive) PathfindingModule.stopPatrol()
    if (CombatPatrolModule.isPatrolRunning) CombatPatrolModule.stopPatrol()
    if (startedPath && nativeActive()) {
      nativeStop()
    }
    RotationExecutor.stopRotating()
    lastTargetPos = null
    stuckTicks = 0
    nextAttackNs = 0L
    startedPath = false
    currentTargetId = null
    resetCloseChase()
    lastPathStartTick = 0L
    clearKillCandidate()
    drillWarnTick = 0L
    lastHealUseTick = 0L
    stuckRepathCount = 0
    startAreaOrigin = null
    if (pendingHealRelease) {
      mc.options.keyUse?.setDown(false)
      pendingHealRelease = false
    }
    pendingHealRestoreSlot = -1
    clearPendingOverfluxPlacement()
    slayerStatus.value = "Off"
    clearSlayerBossState(false)
    slayerLastTabScanTick = -1L
    slayerTabCache = emptyList()
    slayerNeedsQuestRestart = false
    slayerQuestReady = false
    slayerRagnarokUsedPreBoss = false
    slayerAutoDetected = false
    slayerDetectStartTick = -1L
    slayerNeedsQuestClaim = false
    if (slayerNeedsWalkback) WalkbackBridge.stopWalkback?.invoke()
    slayerNeedsWalkback = false
    slayerWalkbackJustFarm = false
    slayerDeathRespawnPending = false
    slayerWalkInDelayUntilTick = -1L
    slayerBossLastPos = null
    slayerClaimStartTick = -1L
    slayerClaimLastClickTick = -1L
    slayerLastBatphoneAttemptTick = -1L
    slayerLastBatphoneUseTick = -1L
    slayerLastGuiActionTick = -1L
    slayerWarnNoBatphoneTick = -1L
    slayerModeEnabled = false
    slayerQuestStateGraceUntilTick = -1L
    slayerEnteredCrypt = false
    slayerSessionStartMs = 0L
    slayerSessionMobKills = 0
    slayerSessionQuestCompletions = 0
    slayerSessionQuestFailures = 0
    clearTrackedSlayerQuestState(clearIdentity = true)
  }

  private const val TAB_COMBAT_GROUP = "Combat Macro"
  private const val TAB_SLAYER_GROUP = "Slayer Macro"
  private const val TAB_SLAYER_SETTINGS_GROUP = "Slayer Settings"
  private const val TAB_PATROL_GROUP = "Patrol"
  private const val TAB_AUTO_ITEMS_GROUP = "Auto Items"
  private const val KILL_CANDIDATE_TTL_TICKS = 80L
  private const val KILL_DISAPPEAR_CONFIRM_TICKS = 2L
  private const val DRILL_WARN_INTERVAL_TICKS = 60L
  private const val AUTO_HEAL_COOLDOWN_TICKS = 20L
  private const val SLAYER_TAB_SCAN_INTERVAL_TICKS = 5L
  private const val SLAYER_BOSS_TAB_LOST_TICKS = 30L
  private const val SLAYER_BOSS_ENTITY_LOST_TICKS = 20L
  private const val SLAYER_OVERFLUX_COOLDOWN_TICKS = 600L
  private const val OVERFLUX_LOOK_DOWN_TICKS = 4
  private const val OVERFLUX_RECOVER_LOOK_TICKS = 8
  private const val OVERFLUX_PLACE_PITCH = 80f
  private const val SLAYER_RAGNAROK_COOLDOWN_TICKS = 400L
  private const val SLAYER_BAD_HEALTH_REUSE_TICKS = 80L
  private const val SLAYER_BATPHONE_RETRY_TICKS = 80L
  private const val SLAYER_MADDOX_GUI_TIMEOUT_TICKS = 80L
  private const val SLAYER_GUI_ACTION_COOLDOWN_TICKS = 5L
  private const val SLAYER_NO_BATPHONE_WARN_TICKS = 200L
  private const val SLAYER_DETECT_WINDOW_TICKS = 100L  // 5 s to scan before assuming no quest
  private const val SLAYER_QUEST_STATE_GRACE_TICKS = 40L
  private const val SPIDER_HATCHLING_PHASE_FALLBACK_TICKS = 160L
  private const val SPIDER_HATCHLING_PHASE_REFRESH_TICKS = 40L
  private const val SLAYER_WOLF_PUPS_PHASE_TICKS = 60L
  private const val PLAYER_HOTBAR_MENU_SLOT_START = 36
  private const val PLAYER_HOTBAR_MENU_SLOT_END = 44
  private const val DEFAULT_WHITELIST_ENTRY = "ice walker"
  private const val MIN_PATH_START_INTERVAL_TICKS = 8L
  private const val TARGET_REPATH_DISTANCE_SQ = 9.0   // repath when mob moves ~3+ blocks
  private const val COMBAT_ROTATION_STEP_SCALE = 0.95
  private const val ROTATION_STOP_HYSTERESIS = 2.5     // don't stop rotating until clearly out of range
  private const val COMBAT_MIN_STANDOFF = 0.45
  private const val MELEE_CLOSE_IN_DISTANCE = 1.1
  private const val COMBAT_PATH_RADIUS_BUFFER = 0.18
  private const val SOFT_CHASE_HANDOFF_GAP = 2.5
  private const val SOFT_CHASE_STICKY_EXIT_GAP = 3.2
  private const val SOFT_CHASE_LOS_GRACE_GAP = 0.55
  private const val SOFT_CHASE_MAX_GAP = 0.45
  private const val SOFT_CHASE_STOP_BUFFER = 0.10
  private const val SOFT_CHASE_YAW_TOLERANCE = 7.5
  private const val SOFT_CHASE_PITCH_TOLERANCE = 18.0
  private const val SLAYER_BOSS_DIRECT_CHASE_GAP_BONUS = 0.85
  private const val SLAYER_MINIBOSS_DIRECT_CHASE_GAP_BONUS = 0.45
  private const val SLAYER_PHASE_ADD_SEARCH_RADIUS_SQ = 100.0
  private const val SPIDER_HATCHLING_CLEAR_DISTANCE = 0.9
  private const val SPIDER_PHASE_MIN_BACKSTEP_DISTANCE = 5.0
  private const val SPIDER_PHASE_STEPBACK_BUFFER = 1.35
  private const val SPIDER_PHASE_STEPBACK_HYSTERESIS = 0.35
  private const val SPIDER_PHASE_STEPBACK_YAW_TOLERANCE = 10.0
  private const val LOS_SCAN_FLIP_TICKS = 20L
  private const val EMAN_HITS_PHASE_MIN_STANDOFF = 2.5
  private const val EMAN_HITS_PHASE_STANDOFF_BUFFER = 0.5
  private const val EMAN_HITS_PHASE_STANDOFF_HYSTERESIS = 0.35
  private const val EMAN_HITS_PHASE_YAW_TOLERANCE = 25.0
  private const val SPIDER_PHASE_STEPBACK_PITCH_TOLERANCE = 22.0
  private const val CRYPT_WALKBACK_ROUTE_NAME = "cryptwalkback"
  private const val SLAYER_WALKIN_WARP_DELAY_TICKS = 40L  // ticks to wait after warp hub before starting walkin route
  private const val CRYPT_PROXIMITY_RANGE_SQ = 1600.0    // 40-block radius - if within this of any patrol point, already in crypt
  private const val GRAVEYARD_PROXIMITY_RANGE_SQ = 900.0  // 30-block radius - if within this of any patrol point, already at graveyard
  private const val CHASE_RESOLVE_RADIUS = 3
  private const val CHASE_RESOLVE_VERTICAL = 2
  private const val START_AREA_RETURN_BUFFER = 2.0
  private const val TARGET_BOX_CYCLE_MS = 4000L
  private const val TARGET_BOX_ALPHA = 170
  private const val ATTACHED_NAMEPLATE_HORIZONTAL_RANGE_SQ = 2.25
  private const val ENDERMAN_HITS_HORIZONTAL_RANGE_SQ = 9.0
  private const val TARGET_BOX_INFLATE = 0.08
  private const val SLAYER_ESP_MID_INFLATE = 0.22
  private const val SLAYER_ESP_OUTER_INFLATE = 0.38
  private const val BOW_REFIRE_DELAY_NS = 200_000_000L  // 200 ms between shots
  private val ENDERMAN_HITS_PATTERN = Regex("\\b\\d+\\s+hits?\\b")
  private val SLAYER_PROGRESS_PATTERN = Regex("(\\d{1,5})\\s*/\\s*(\\d{1,5})")
  private data class SlayerQuestProgress(
    val typeIndex: Int,
    val tier: Int,
    val progress: Int,
    val target: Int,
  )
  private val SLAYER_BOSS_ENTITY_KEYWORDS get() = when (slayerType.value) {
    0 -> arrayOf("revenant horror", "atoned horror")
    1 -> arrayOf("sven packmaster")
    2 -> arrayOf("tarantula broodfather")
    3 -> arrayOf("voidgloom seraph")
    4 -> arrayOf("riftstalker bloodfiend")
    5 -> arrayOf("inferno demonlord")
    else -> arrayOf()
  }
  // Priority mobs: rare/high-XP mobs that should be targeted over regular farm mobs.
  // Only applies at the relevant tier (zombie: tier 3+).
  private val SLAYER_PRIORITY_MOB_KEYWORDS get() = when {
    slayerType.value == 0 && slayerTier.value >= 3 -> arrayOf(
      "revenant sycophant", "revenant champion", "deformed revenant",
      "atoned champion", "atoned revenant"
    )
    slayerType.value == 1 && slayerTier.value >= 3 -> arrayOf(
      "pack enforcer", "sven follower", "sven alpha"
    )
    slayerType.value == 2 && slayerTier.value >= 3 -> arrayOf(
      "tarantula vermin", "tarantula beast", "mutant tarantula",
      "primordial jockey", "primordial viscount"
    )
    slayerType.value == 3 && slayerTier.value >= 3 -> arrayOf(
      "voidling devotee", "voidling radical", "voidcrazed maniac"
    )
    slayerType.value == 5 && slayerTier.value >= 3 -> arrayOf(
      "flare demon", "kindleheart demon", "burningsoul demon"
    )
    else -> arrayOf()
  }
  private val SLAYER_FARM_MOB_KEYWORDS get() = when (slayerType.value) {
    0 -> when (slayerLocation.value) {
      0 -> arrayOf("zombie") // Zombie Graveyard: regular zombies only
      1 -> arrayOf("crypt ghoul", "golden ghoul") // Zombie Crypt
      else -> arrayOf("zombie", "crypt ghoul", "golden ghoul")
    }
    1 -> arrayOf("pack wolf", "old wolf", "pit wolf", "zombie wolf", "wolf", "pup", "pups")
    2 -> arrayOf("dasher spider", "voracious spider", "weaver spider", "spider", "hatchling", "hatchlings", "broodling")
    3 -> arrayOf("voidling extremist", "voidling radical", "voidling fanatic", "enderman")
    4 -> arrayOf("bat", "vampiric bat", "bloodfiend")
    5 -> arrayOf("blaze", "smoldering blaze", "emerald slime")
    else -> arrayOf()
  }
  // Only "slay the boss" reliably signals the boss is active.
  // Boss entity names (e.g. "revenant horror") also appear in the quest PROGRESS tab line
  // ("Kill Revenant Horror 50/200"), which would incorrectly set slayerBossActive = true.
  private val SLAYER_BOSS_TAB_KEYWORDS = arrayOf("slay the boss")
  private val SLAYER_BOSS_CLEAR_KEYWORDS = arrayOf("boss slain", "slayer quest complete")
  private val SLAYER_QUEST_TAB_KEYWORDS get() = when (slayerType.value) {
    0 -> arrayOf("slayer quest", "zombie slayer", "revenant horror")
    1 -> arrayOf("slayer quest", "wolf slayer", "sven packmaster")
    2 -> arrayOf("slayer quest", "spider slayer", "tarantula broodfather")
    3 -> arrayOf("slayer quest", "enderman slayer", "voidgloom seraph")
    4 -> arrayOf("slayer quest", "vampire slayer", "riftstalker bloodfiend")
    5 -> arrayOf("slayer quest", "blaze slayer", "inferno demonlord")
    else -> arrayOf("slayer quest")
  }
  // Items that are NOT weapons - switch away from these when preparing to attack
  private val SLAYER_NON_WEAPON_KEYWORDS = arrayOf(
    "batphone", "overflux", "ragnarok", "ragnorak", "wand of atonement",
    "sword of bad health", "drill", "pickaxe", "gauntlet"
  )
  private val SLAYER_MELEE_WEAPON_KEYWORDS = arrayOf(
    "sword", "blade", "scythe", "katana", "saber", "cleaver", "axe", "rapier", "claymore",
    "halberd", "dagger", "falchion"
  )
  private val SLAYER_BOW_WEAPON_KEYWORDS = arrayOf("terminator", "juju", "shortbow", "bow")
  private val SLAYER_MAGE_WEAPON_KEYWORDS = arrayOf("staff", "wand", "sceptre", "scepter", "scythe")
  private val SLAYER_ANY_WEAPON_KEYWORDS = arrayOf(
    *SLAYER_MELEE_WEAPON_KEYWORDS,
    *SLAYER_BOW_WEAPON_KEYWORDS,
    *SLAYER_MAGE_WEAPON_KEYWORDS
  )
  private val SLAYER_ZOMBIE_SPAWN_WEAPON_DEFAULT_KEYWORDS = arrayOf("halberd")
  private val SLAYER_ZOMBIE_DYNAMIC_SWORD_DEFAULT_KEYWORDS = arrayOf("falchion", "reaper", "shredded", "sword")
  private const val EMAN_SPAWN_WEAPON_TERMINATOR = 0
  private const val EMAN_SPAWN_WEAPON_SHORTBOW = 1
  private const val EMAN_SPAWN_WEAPON_ENDERMAN_SWORD = 2
  private const val EMAN_SPAWN_WEAPON_DYNAMIC = 3
  private val SLAYER_ENDERMAN_TERMINATOR_WEAPON_KEYWORDS = arrayOf("terminator")
  private val SLAYER_ENDERMAN_SHORTBOW_WEAPON_KEYWORDS = arrayOf("juju", "shortbow", "bow")
  private val SLAYER_ENDERMAN_DYNAMIC_BOW_WEAPON_KEYWORDS = arrayOf("terminator", "juju", "shortbow", "bow")
  private val SLAYER_ENDERMAN_SPAWN_WEAPON_DEFAULT_KEYWORDS = arrayOf("terminator", "juju", "shortbow", "bow")
  private val SLAYER_ENDERMAN_HIT_PHASE_WEAPON_DEFAULT_KEYWORDS = arrayOf("terminator", "juju", "shortbow", "bow")
  private val SLAYER_ENDERMAN_BOSS_WEAPON_DEFAULT_KEYWORDS = arrayOf("atomsplit", "vorpal", "voidedge", "katana")
  private val SLAYER_SPIDER_PHASE_ADD_KEYWORDS = arrayOf("spider", "hatchling", "hatchlings", "broodling", "vermin")
  private val SLAYER_WOLF_PHASE_ADD_KEYWORDS = arrayOf("wolf", "pup", "pups")
  private val SLAYER_WOLF_PUPS_TEXT_KEYWORDS = arrayOf("calling the pups")
  private val SLAYER_WAND_OF_ATONEMENT_KEYWORDS = arrayOf("wand of atonement")
  private val SLAYER_ZOMBIE_SWORD_KEYWORDS = arrayOf("zombie sword")
  private val SLAYER_OVERFLUX_KEYWORDS = arrayOf("overflux")
  private val SLAYER_RAGNAROK_KEYWORDS = arrayOf("ragnarok", "ragnorak")
  private val SLAYER_BAD_HEALTH_KEYWORDS = arrayOf("sword of bad health")
  private val SLAYER_BATPHONE_KEYWORDS = arrayOf("maddox batphone", "batphone")
  private val SLAYER_MADDOX_SCREEN_KEYWORDS = arrayOf("maddox", "slayer")
  // Generic tab keywords that indicate ANY active slayer quest, regardless of type
  private val SLAYER_GENERIC_QUEST_KEYWORDS = arrayOf("slayer quest", "zombie slayer", "wolf slayer",
    "spider slayer", "enderman slayer", "vampire slayer", "blaze slayer")
  private val SLAYER_MADDOX_REVENANT_KEYWORDS get() = when (slayerType.value) {
    0 -> arrayOf("revenant horror", "zombie slayer", "revenant")
    1 -> arrayOf("sven packmaster", "wolf slayer", "sven")
    2 -> arrayOf("tarantula broodfather", "spider slayer", "tarantula")
    3 -> arrayOf("voidgloom seraph", "enderman slayer", "voidgloom")
    4 -> arrayOf("riftstalker bloodfiend", "vampire slayer", "vampire")
    5 -> arrayOf("inferno demonlord", "blaze slayer", "inferno")
    else -> arrayOf()
  }
  private val SLAYER_MADDOX_RESTART_KEYWORDS = arrayOf("restart", "start quest", "begin quest", "start slayer", "slayer quest", "new quest")
  private val SLAYER_MADDOX_CONFIRM_KEYWORDS = arrayOf("confirm", "click to confirm", "accept", "purchase")
  private val SLAYER_QUEST_READY_CHAT_KEYWORDS = arrayOf(
    "ready to spawn your boss",
    "spawn your boss",
    "right-click to summon",
    "slayer quest is ready",
    "kill a mob to spawn the boss",
    "boss will spawn"
  )
  private val SLAYER_BOSS_SPAWNED_CHAT_KEYWORDS get() = when (slayerType.value) {
    0 -> arrayOf(
      "revenant horror has spawned",
      "your revenant horror spawned",
      "atoned horror has spawned",
      "your atoned horror spawned"
    )
    1 -> arrayOf("sven packmaster has spawned", "your sven packmaster spawned")
    2 -> arrayOf("tarantula broodfather has spawned")
    3 -> arrayOf("voidgloom seraph has spawned")
    4 -> arrayOf("riftstalker bloodfiend has spawned")
    5 -> arrayOf("inferno demonlord has spawned")
    else -> arrayOf()
  }
  private val SLAYER_BOSS_KILLED_CHAT_KEYWORDS = arrayOf(
    "you have slain the boss",
    "your slayer quest has been completed",
    "slayer quest complete",
    "boss slain"
  )
  private val SLAYER_LOOT_KEYWORDS = arrayOf("loot", "bag", "drops")
  private const val SLAYER_LOOT_SEARCH_RADIUS_SQ = 36.0  // 6 block radius
  private const val SLAYER_LOOT_CLAIM_RANGE_SQ = 9.0     // 3 block range to right-click
  private const val SLAYER_CLAIM_TIMEOUT_TICKS = 200L    // 10 s before giving up
  private const val SLAYER_CLAIM_DWELL_TICKS = 60L       // 3 s at loot bag before moving on
  private const val SLAYER_CLAIM_CLICK_INTERVAL_TICKS = 5L
  private val slayerWarpCommand get() = when (slayerType.value) {
    0 -> when (slayerLocation.value) {
      0 -> "warp crypts"  // Zombie Graveyard
      else -> ""             // Zombie Crypt: walkback route handles return
    }
    1 -> "warp hub"
    2 -> "warp spiders_den"
    3 -> "warp end"
    4 -> "warp rift"
    5 -> "warp blazing_fortress"
    else -> ""
  }
  // Reference positions spread across the crypt - used only for "is player inside crypt?" proximity checks.
  private val CRYPT_PATROL_WAYPOINTS = listOf(
    BlockPos(-152, 57, -102), BlockPos(-133, 50, -101), BlockPos(-132, 45, -121),
    BlockPos(-129, 41, -134), BlockPos(-129, 41, -143), BlockPos(-119, 41, -136),
    BlockPos(-106, 46, -129), BlockPos(-102, 48, -119), BlockPos(-89, 46, -104),
    BlockPos(-77, 46, -105),  BlockPos(-65, 50, -120),  BlockPos(-48, 55, -136),
    BlockPos(-48, 57, -141),  BlockPos(-46, 55, -136),  BlockPos(-80, 46, -101),
    BlockPos(-88, 42, -88),   BlockPos(-96, 38, -86),   BlockPos(-106, 38, -87),
    BlockPos(-114, 43, -89),  BlockPos(-133, 50, -105), BlockPos(-147, 56, -100),
    BlockPos(-145, 57, -114), BlockPos(-136, 58, -127), BlockPos(-121, 55, -126),
    BlockPos(-109, 50, -119), BlockPos(-102, 48, -119),
  )

  private val CYAN_COLOR = Color(0, 255, 255)
  private val PINK_COLOR = Color(255, 105, 180)

  private enum class SlayerHighlightType {
    BOSS,
    MINIBOSS
  }

  private data class SlayerEspPalette(
    val stroke: Color,
    val fill: Color,
    val outerStroke: Color,
    val outerFill: Color,
  )
}
