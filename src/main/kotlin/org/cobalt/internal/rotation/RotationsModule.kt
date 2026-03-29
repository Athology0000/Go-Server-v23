package org.cobalt.internal.rotation

import kotlin.random.Random
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.InfoSetting
import org.cobalt.api.module.setting.impl.InfoType
import org.cobalt.api.module.setting.impl.RangeSetting
import org.cobalt.api.module.setting.impl.SliderSetting

object RotationsModule : Module("Rotations") {

  // --- Bezier Tracking (Combat Macro & Routes) ---

  private val headerBezier = InfoSetting("Bezier Shape", "Easing curve shared by combat and route rotations.", InfoType.INFO)

  val bezierCurveIn = SliderSetting(
    "Curve In", "Bezier start control point. Lower = slower start.", 0.20, 0.01, 0.49
  )
  val bezierCurveOut = SliderSetting(
    "Curve Out", "Bezier end control point. Higher = smoother finish.", 0.94, 0.51, 0.99
  )
  val bezierMinScale = SliderSetting(
    "Min Scale", "Minimum movement scale at small deltas (0-1).", 0.18, 0.05, 1.0
  )
  val bezierSnapThreshold = SliderSetting(
    "Snap Threshold", "Degrees below which rotation snaps instantly. Set to 0 to disable.", 0.25, 0.0, 3.0
  )

  // --- Combat Macro ---

  private val headerCombat = InfoSetting("Combat Macro", "Rotation step ranges for the combat macro.", InfoType.INFO)

  val combatYawStep = RangeSetting(
    "Combat Yaw Step", "Max yaw degrees per frame for combat.", Pair(8.0, 12.0), 1.0, 30.0
  )
  val combatPitchStep = RangeSetting(
    "Combat Pitch Step", "Max pitch degrees per frame for combat.", Pair(6.0, 10.0), 1.0, 30.0
  )

  // --- Routes ---

  private val headerRoutes = InfoSetting("Routes", "Rotation step ranges for route walking.", InfoType.INFO)

  val routeYawStep = RangeSetting(
    "Route Yaw Step", "Max yaw degrees per frame for route walking.", Pair(7.0, 11.0), 1.0, 30.0
  )
  val routePitchStep = RangeSetting(
    "Route Pitch Step", "Max pitch degrees per frame for route walking.", Pair(5.0, 9.0), 1.0, 30.0
  )

  // --- Mining Macro ---

  private val headerMining = InfoSetting("Mining Macro", "Head rotation settings for the mining macro.", InfoType.INFO)

  val miningMaxSpeed = RangeSetting(
    "Mining Max Speed", "Max turn speed (deg/sec) while mining.", Pair(80.0, 120.0), 10.0, 300.0
  )
  val miningMaxAccel = RangeSetting(
    "Mining Max Accel", "Max turn acceleration (deg/sec^2) while mining.", Pair(180.0, 260.0), 50.0, 600.0
  )
  val miningSpeedScale = RangeSetting(
    "Mining Speed Scale", "Speed scale multiplier while mining.", Pair(0.7, 1.0), 0.1, 2.0
  )
  val miningAccelScale = RangeSetting(
    "Mining Accel Scale", "Accel scale multiplier while mining.", Pair(0.6, 1.0), 0.1, 2.0
  )
  val miningPitchStep = RangeSetting(
    "Mining Pitch Step", "Max pitch step per frame while mining.", Pair(2.5, 4.5), 0.5, 15.0
  )

  // --- Warp ---

  private val headerWarp = InfoSetting("Warp", "Head rotation settings during etherwarp.", InfoType.INFO)

  val warpSpeedScale = RangeSetting(
    "Warp Speed Scale", "Speed scale multiplier during warp aim.", Pair(0.8, 1.2), 0.1, 2.0
  )
  val warpAccelScale = RangeSetting(
    "Warp Accel Scale", "Accel scale multiplier during warp aim.", Pair(0.7, 1.1), 0.1, 2.0
  )

  init {
    addSetting(
      headerBezier,
      bezierCurveIn, bezierCurveOut, bezierMinScale, bezierSnapThreshold,
      headerCombat,
      combatYawStep, combatPitchStep,
      headerRoutes,
      routeYawStep, routePitchStep,
      headerMining,
      miningMaxSpeed, miningMaxAccel,
      miningSpeedScale, miningAccelScale, miningPitchStep,
      headerWarp,
      warpSpeedScale, warpAccelScale,
    )
  }

  /** Returns a random value sampled uniformly from [range.first, range.second]. */
  fun sample(range: Pair<Double, Double>): Double =
    if (range.first >= range.second) range.first
    else range.first + Random.nextDouble() * (range.second - range.first)
}
