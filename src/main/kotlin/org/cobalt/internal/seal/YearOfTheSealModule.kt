package org.cobalt.internal.seal

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.monster.Slime
import net.minecraft.world.phys.Vec3
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.KeyBindSetting
import org.cobalt.api.module.setting.impl.SliderSetting
import org.cobalt.api.pathfinder.jni.NativePathfinder
import org.cobalt.api.util.helper.KeyBind
import org.cobalt.api.util.player.MovementManager

object YearOfTheSealModule : Module("Year of the Seal") {

    private val mc = Minecraft.getInstance()

    private val enabledSetting = CheckboxSetting(
        "Enabled",
        "Automatically pathfinds to the predicted beach ball landing position.",
        false
    )

    private val toggleKeybind = KeyBindSetting(
        "Toggle Key",
        "Keybind to enable/disable the Year of the Seal macro.",
        KeyBind(-1)
    )

    private val minBounces = SliderSetting(
        "Min Bounce Quality",
        "Minimum observed bounces before pathfinding starts. Higher = more data, more accurate prediction.",
        2.0, 1.0, 10.0, 1.0
    )

    /** Tracks position history and predicts the landing position for one beach ball entity. */
    private class BallPredictor {
        val data = mutableListOf<Vec3>()    // all positions recorded since spawn
        var startIndex = 0                  // slice start within data (reset each bounce)
        var minY = Double.MAX_VALUE         // lowest Y seen (ground level)
        var bounceCounter = 0
        var lastBounceMs = 0L
        var positiveYDelta = true           // whether last Y delta was upward (true = start neutral so first rise doesn't count as bounce)
        var lastPos = Vec3(0.0, 0.0, 0.0)
        var predTick = 0                    // counter mod 3 for throttled prediction updates
        var landingPos: Vec3? = null        // current best landing prediction

        fun update(pos: Vec3) {
            data.add(pos)
            if (pos.y < minY) minY = pos.y

            // Skip bounce logic on first tick — lastPos is uninitialized
            if (data.size == 1) { lastPos = pos; return }

            // Bounce detection — skip if movement too small or debounce active
            val dist = pos.distanceTo(lastPos)
            if (dist >= 0.3) {
                val dy = pos.y - lastPos.y
                val nowMs = System.currentTimeMillis()
                if (dy > 0.0 && !positiveYDelta && (nowMs - lastBounceMs) > 800L) {
                    // Ball just started going up — it bounced
                    bounceCounter++
                    lastBounceMs = nowMs
                    startIndex = data.lastIndex   // reset prediction slice to this bounce
                }
                positiveYDelta = dy > 0.0
                lastPos = pos

                // Throttle prediction to every 3 meaningful position updates
                predTick++
                if (predTick >= 3) {
                    predTick = 0
                    landingPos = runPrediction()
                }
            }
        }

        private fun runPrediction(): Vec3? {
            val slice = data.subList(startIndex, data.size).toList()
            val candidates = listOfNotNull(
                smallPoly(slice),
                averagePoly(slice),
                spreadPoly(slice)
            )
            if (candidates.isEmpty()) return null
            // Average X and Z of all valid model outputs
            val avgX = candidates.sumOf { it.x } / candidates.size
            val avgZ = candidates.sumOf { it.z } / candidates.size
            return Vec3(avgX, minY, avgZ)
        }

        // Fit y = a*t^2 + b*t + c through three (t, y) points.
        // Returns Triple(a, b, c) or null if inputs are degenerate.
        private fun fitQuadratic(
            t1: Int, y1: Double,
            t2: Int, y2: Double,
            t3: Int, y3: Double
        ): Triple<Double, Double, Double>? {
            val d1 = (t2 - t1).toDouble()
            val d2 = (t3 - t1).toDouble()
            if (d1 == 0.0 || d2 == 0.0 || d1 == d2) return null
            val sq1 = t1.toDouble() * t1
            val sq2 = t2.toDouble() * t2
            val sq3 = t3.toDouble() * t3
            val denom = (sq2 - sq1) * d2 - (sq3 - sq1) * d1
            if (denom == 0.0) return null
            val a = ((y2 - y1) * d2 - (y3 - y1) * d1) / denom
            val b = ((y2 - y1) - a * (sq2 - sq1)) / d1
            val c = y1 - b * t1 - a * sq1
            return Triple(a, b, c)
        }

        // Extrapolate from t=fromT forward until poly(t) <= targetY, up to maxSteps ticks.
        // Returns the landing Vec3 (XZ from linear drift + Y at targetY), or null if never reached.
        private fun extrapolate(
            fromT: Int,
            startXZ: Vec3,
            dx: Double, dz: Double,
            a: Double, b: Double, c: Double,
            targetY: Double,
            maxSteps: Int = 300
        ): Vec3? {
            var x = startXZ.x
            var z = startXZ.z
            for (t in (fromT + 1)..(fromT + maxSteps)) {
                x += dx
                z += dz
                val y = a * t * t + b * t + c
                if (y <= targetY) return Vec3(x, targetY, z)
            }
            return null
        }

        // Average XZ drift per tick across the current bounce segment.
        private fun segmentDrift(slice: List<Vec3>): Pair<Double, Double> {
            if (slice.size < 2) return 0.0 to 0.0
            val n = (slice.size - 1).toDouble()
            return (slice.last().x - slice.first().x) / n to
                   (slice.last().z - slice.first().z) / n
        }

        // SmallPoly: 3 most recent points. Min 3 points in slice.
        private fun smallPoly(slice: List<Vec3>): Vec3? {
            if (slice.size < 3) return null
            val n = slice.size - 1
            val (dx, dz) = segmentDrift(slice)
            val (a, b, c) = fitQuadratic(
                n,     slice[n].y,
                n - 1, slice[n - 1].y,
                n - 2, slice[n - 2].y
            ) ?: return null
            return extrapolate(n, slice[n], dx, dz, a, b, c, minY)
        }

        // AveragePoly: 2-point averaged windows at t-1, t-3, t-5. Min 7 points in slice.
        private fun averagePoly(slice: List<Vec3>): Vec3? {
            if (slice.size < 7) return null
            val n = slice.size - 1
            val y1 = (slice[n - 1].y + slice[n - 2].y) / 2.0
            val y2 = (slice[n - 3].y + slice[n - 4].y) / 2.0
            val y3 = (slice[n - 5].y + slice[n - 6].y) / 2.0
            val (dx, dz) = segmentDrift(slice)
            val (a, b, c) = fitQuadratic(n - 1, y1, n - 3, y2, n - 5, y3) ?: return null
            return extrapolate(n - 1, slice[n - 1], dx, dz, a, b, c, minY)
        }

        // SpreadPoly: spread across full bounce segment. Min 5 points in slice.
        private fun spreadPoly(slice: List<Vec3>): Vec3? {
            if (slice.size < 5) return null
            val n = slice.size - 1
            val mid = (n + 1) / 2
            val (dx, dz) = segmentDrift(slice)
            val (a, b, c) = fitQuadratic(
                n - 1,  slice[n - 1].y,
                mid,    slice[mid].y,
                1,      slice[1].y
            ) ?: return null
            return extrapolate(n - 1, slice[n - 1], dx, dz, a, b, c, minY)
        }
    }

    // Ball trackers keyed by entity ID
    private val predictors = mutableMapOf<Int, BallPredictor>()
    // Last target issued to NativePathfinder (to avoid replanning on every tick)
    private var lastIssuedTarget: Vec3? = null

    init {
        addSetting(enabledSetting, toggleKeybind, minBounces)
        EventBus.register(this)
    }

    @SubscribeEvent
    fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
        if (toggleKeybind.value.isPressed()) {
            enabledSetting.value = !enabledSetting.value
        }
        if (!enabledSetting.value) {
            if (predictors.isNotEmpty()) stopAll()
            return
        }
        val level = mc.level ?: return

        // Discover new beach ball slimes (invisible, 1024 max HP)
        level.entitiesForRendering().forEach { entity ->
            if (entity is Slime && entity.isInvisible && entity.maxHealth == 1024f && entity.size == 4) {
                predictors.putIfAbsent(entity.id, BallPredictor())
            }
        }

        // Update existing predictors, prune removed entities
        val iter = predictors.iterator()
        while (iter.hasNext()) {
            val (id, predictor) = iter.next()
            val entity = level.getEntity(id)
            if (entity == null || entity.isRemoved) {
                iter.remove()
                continue
            }
            predictor.update(Vec3(entity.x, entity.y, entity.z))
        }

        // Pathfinding — implemented in Task 5
    }

    private fun stopAll() {
        NativePathfinder.stop()
        MovementManager.clearForcedMovement()
        predictors.clear()
        lastIssuedTarget = null
    }
}
