package org.phantom.internal.diana

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket
import net.minecraft.network.protocol.game.ServerboundUseItemPacket
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.ChatEvent
import org.phantom.api.event.impl.client.PacketEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.event.impl.render.WorldRenderEvent
import org.phantom.api.hud.HudAnchor
import org.phantom.api.hud.hudElement
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.ActionSetting
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.ColorSetting
import org.phantom.api.module.setting.impl.KeyBindSetting
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.module.setting.impl.TextSetting
import org.phantom.api.notification.NotificationManager
import org.phantom.api.pathfinder.jni.NativePathfinder
import org.phantom.api.ui.theme.ThemeManager
import org.phantom.api.util.ChatUtils
import org.phantom.api.util.getSkyblockId
import org.phantom.api.util.helper.KeyBind
import org.phantom.api.util.ui.NVGRenderer
import org.phantom.internal.helper.ClientGlowEspManager
import org.phantom.internal.pathfinding.OverlayRenderEngine
import org.phantom.internal.pathfinding.OverlayRenderEngine.Color as OREColor
import org.phantom.internal.visual.PetTabListParser
import java.util.UUID
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Diana Helper - shows up to 4 predicted burrow locations as waypoints + directional compass HUD.
 *
 * Particle collection and burrow position detection is handled by DianaParticleTracker.
 * Each burrow block is pinpointed precisely from a single matching CRIT packet.
 */
object DianaHelperModule : Module("Diana Helper") {

    override val category = ModuleCategory.DIANA

    private val mc = Minecraft.getInstance()

    // -- Settings --------------------------------------------------------------

    val enabled       = CheckboxSetting("Enabled",        "Show Diana burrow waypoints and direction HUD.", false)
    val showWaypoint  = CheckboxSetting("Show Waypoint",  "Render a highlighted box + beam at each detected burrow.", true)
    val showLine      = CheckboxSetting("Show Line",      "Draw a path line from your position to the nearest burrow.", true)
    val showHud       = CheckboxSetting("Show HUD",       "Show the burrow direction compass HUD.", true)
    val waypointColor = ColorSetting(  "Waypoint Color",  "Color of the burrow waypoints.", 0xFFFFD700.toInt())
    val expireSeconds = SliderSetting( "Expire Time",     "Seconds without a confirmed particle before a waypoint disappears.", 30.0, 5.0, 120.0, step = 1.0)
    val burrowDetection = CheckboxSetting("Burrow Detection", "Detect and render confirmed Diana burrows.", true)
    val highlightRareMobs = CheckboxSetting("Highlight Rare Mobs", "Glow rare Diana mobs like Inquisitor, Sphinx, King Minos, and Manticore.", true)
    val rareMobGlowColor = ColorSetting("Rare Mob Glow Color", "Glow color for rare Diana mobs.", 0xFFFF55FF.toInt())
    val rareMobWaypoints = CheckboxSetting("Rare Mob Waypoints", "Render waypoints and lines to detected rare Diana mobs.", true)
    val shareRareMobs = CheckboxSetting("Share Rare Mobs", "Send rare Diana mob coordinates to party chat when detected.", false)
    val receiveRareMobs = CheckboxSetting("Receive Rare Mobs", "Create rare mob waypoints from party chat coordinates.", true)
    val allChatCoordsAreRare = CheckboxSetting("All Chat Coords Are Rare", "Treat party chat coordinates as rare Diana mobs while this helper is enabled.", false)
    val petWarning = CheckboxSetting("Griffin Pet Warning", "Warn when using a Diana spade without a Griffin pet visible in tab.", true)
    val particleFailHint = CheckboxSetting("Particle Fail Hint", "Warn if a spade use does not produce Diana guess particles.", true)
    val preciseGuess = CheckboxSetting("Precise Spade Guess", "Use SkyHanni-style spade lava trails to guess burrow locations before confirmation.", true)
    val arrowGuess = CheckboxSetting("Arrow Guess", "Use SkyHanni-style arrow particles to guess the next burrow after digging.", true)
    val nearestWarp = CheckboxSetting("Nearest Warp Hint", "Show a suggested hub warp when it saves enough travel distance.", true)
    val allowedWarps = TextSetting("Allowed Warps", "Comma-separated hub warps usable for Diana warp hints.", "wizard,da,castle,crypt,stonks")
    val dontWarpIfClose = CheckboxSetting("Don't Warp If Close", "Do not warp if you are already close to the target burrow.", true)
    val closeWarpDistance = SliderSetting("Close Warp Distance", "Distance under which the warp key is suppressed.", 60.0, 10.0, 120.0, step = 1.0)
    val warpDistanceDifference = SliderSetting("Warp Difference", "Minimum distance saved before suggesting a warp.", 10.0, 0.0, 180.0, step = 1.0)
    val warpDelay = SliderSetting("Warp Delay", "Milliseconds after a guess before the warp key may warp.", 0.0, 0.0, 1000.0, step = 25.0)
    val warpKey = KeyBindSetting("Warp Key", "Warp to the currently suggested Diana hub warp.", KeyBind(-1))
    val useSuggestedWarp = ActionSetting("Use Suggested Warp", "Warp to the currently suggested Diana hub warp.", "Warp", { currentWarp?.displayName ?: "No Warp" }) {
        useSuggestedWarp()
    }
    val creatureTracker = CheckboxSetting("Creature Tracker", "Show Mythological creature counts and rare-mob since counters.", true)
    val profitTracker = CheckboxSetting("Profit Tracker", "Show Diana profit, rates, and timing stats.", true)
    val statsTracker = CheckboxSetting("Stats Tracker", "Show SBO-style since counters for rare mobs and rare drops.", true)
    val magicFindTracker = CheckboxSetting("Magic Find Tracker", "Track highest magic find seen on Diana rare drops.", true)
    val rareDropChatAnnouncer = CheckboxSetting("Rare Drop Chat Announcer", "Print SBO-style chat notices for Diana rare drops.", true)
    val rareDropScreenAnnouncer = CheckboxSetting("Rare Drop Screen Announcer", "Show a title for major Diana rare drops.", false)
    val rareDropPartyAnnouncer = CheckboxSetting("Rare Drop Party Announcer", "Announce major Diana rare drops in party chat.", false)
    val customChimMessage = TextSetting("Custom Chim Message", "Message used for Chimera drops. Supports {mf} and {amount}.", "[SBO] RARE DROP! Chimera! +{mf} Magic Find #{amount}")
    val guessLine = CheckboxSetting("Guess Line", "Draw lines to guessed burrows.", true)
    val burrowLine = CheckboxSetting("Burrow Line", "Draw lines to confirmed burrows.", true)
    val rareMobLine = CheckboxSetting("Rare Mob Line", "Draw lines to rare Diana mobs.", true)
    val lineWidth = SliderSetting("Line Width", "Diana waypoint line width.", 5.0, 1.0, 20.0, step = 1.0)
    val removeGuessDistance = SliderSetting("Remove Guess When Close", "Remove guess waypoints when this close. 0 disables.", 0.0, 0.0, 20.0, step = 1.0)
    val removeRareMobDistance = SliderSetting("Remove Rare Mob When Close", "Remove rare mob waypoints when this close. 0 disables.", 3.0, 0.0, 20.0, step = 1.0)
    val removeRareMobBeamDistance = SliderSetting("Remove Rare Mob Beam Distance", "Hide rare mob beams when this close. 0 disables.", 8.0, 0.0, 20.0, step = 1.0)
    val resetProfitTracker = ActionSetting("Reset Profit Tracker", "Clear the Diana profit/timing session.", "Reset") {
        DianaProfitTracker.reset()
    }
    val resetCreatureTracker = ActionSetting("Reset Creature Tracker", "Clear the Diana creature tracker counters.", "Reset") {
        resetTracker()
    }

    // -- State -----------------------------------------------------------------

    private data class RareMobWaypoint(
        val uuid: UUID,
        val name: String,
        var pos: Vec3,
        val firstSeenMs: Long,
        var lastSeenMs: Long,
        var shared: Boolean = false,
    )

    private val rareMobTargets = linkedMapOf<UUID, RareMobWaypoint>()
    private val creatureCounts = linkedMapOf<String, Int>()
    private val creatureSince = linkedMapOf<String, Int>()
    private var lastSpadeUseMs = 0L
    private var lastParticleFailWarnMs = 0L
    private var lastPetWarnMs = 0L
    private var tickCounter = 0
    private val preciseTrail = ArrayList<Vec3>()
    private var lastPreciseGuess: Vec3? = null
    private val arrowPoints = ArrayList<Vec3>()
    private val recentArrowKeys = linkedSetOf<String>()
    private var currentWarp: WarpPoint? = null
    private var lastWarpMs = 0L
    private val rareDropCounts = linkedMapOf<String, Int>()
    private val rareDropSince = linkedMapOf<String, Int>()
    private val highestMagicFind = linkedMapOf<String, Int>()

    private val MYTHOLOGICAL_SPAWN_PATTERN = Regex(
        """^(?:Oh|Uh oh|Yikes|Oi|Good Grief|Danger|Woah)! You dug out (?:a )?(.+)!$""",
        RegexOption.IGNORE_CASE
    )

    private val DIANA_SPADE_IDS = setOf(
        "ANCESTRAL_SPADE",
        "ANCESTRAL_SPADE_2",
        "DWARVEN_METAL_DETECTOR",
        "DEIFIC_SPADE",
    )

    private val CREATURE_NAMES = listOf(
        "Siamese Lynxes",
        "Minos Hunter",
        "Minotaur",
        "Gaia Construct",
        "Minos Champion",
        "Minos Inquisitor",
        "Cretan Bull",
        "Harpy",
        "Sphinx",
        "King Minos",
        "Manticore",
    )

    private val RARE_MOBS = listOf("Minos Inquisitor", "Sphinx", "King Minos", "Manticore")
    private val RARE_TRACKED_MOBS = listOf("Minos Inquisitor", "Sphinx", "King Minos", "Manticore")
    private val SHARD_WARNING_MOBS = setOf("Cretan Bull", "Harpy", "Minotaur")
    private val RARE_DROP_NAMES = listOf(
        "Chimera",
        "Daedalus Stick",
        "Minos Relic",
        "Manti-core",
        "Fateful Stinger",
        "Brain Food",
        "Shimmering Wool",
        "Mythological Dye",
    )

    /** Nearest known burrow - used by macro and compass HUD. */
    val burrowPos: Vec3?
        get() {
            val level = mc.level ?: return null
            val player = mc.player
            val positions = DianaParticleTracker.getBurrowPositions(level, expireSeconds.value.toLong() * 1000L)
            if (positions.isEmpty()) return null
            if (player == null) return positions.first()
            val px = player.x; val pz = player.z
            return positions.minByOrNull { p ->
                val dx = p.x - px; val dz = p.z - pz; dx * dx + dz * dz
            }
        }

    init {
        for (name in CREATURE_NAMES) creatureSince[name] = 0
        addSetting(
            enabled,
            showWaypoint,
            showLine,
            showHud,
            waypointColor,
            expireSeconds,
            burrowDetection,
            highlightRareMobs,
            rareMobGlowColor,
            rareMobWaypoints,
            shareRareMobs,
            receiveRareMobs,
            allChatCoordsAreRare,
            petWarning,
            particleFailHint,
            preciseGuess,
            arrowGuess,
            nearestWarp,
            allowedWarps,
            dontWarpIfClose,
            closeWarpDistance,
            warpDistanceDifference,
            warpDelay,
            warpKey,
            useSuggestedWarp,
            creatureTracker,
            profitTracker,
            statsTracker,
            magicFindTracker,
            rareDropChatAnnouncer,
            rareDropScreenAnnouncer,
            rareDropPartyAnnouncer,
            customChimMessage,
            guessLine,
            burrowLine,
            rareMobLine,
            lineWidth,
            removeGuessDistance,
            removeRareMobDistance,
            removeRareMobBeamDistance,
            resetProfitTracker,
            resetCreatureTracker,
        )
        EventBus.register(this)
        DianaProfitTracker.ensureInitialized()
    }

    @SubscribeEvent
    fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
        val level = mc.level ?: run {
            ClientGlowEspManager.clear(RARE_MOB_GLOW_SCOPE)
            return
        }
        val player = mc.player ?: return

        tickCounter++
        if (!enabled.value) {
            ClientGlowEspManager.clear(RARE_MOB_GLOW_SCOPE, level)
            return
        }

        if (tickCounter % 5 == 0) {
            pruneCloseGuesses(level, player)
            syncRareMobs(level, player)
            maybeWarnPet()
            maybeWarnParticleQuality()
            updateNearestWarp()
        }
        if (warpKey.value.isPressed()) useSuggestedWarp()
    }

    @SubscribeEvent
    fun onPacket(event: PacketEvent.Outgoing) {
        if (!enabled.value) return
        val packet = event.packet as? ServerboundUseItemPacket ?: return
        val player = mc.player ?: return
        if (!isDianaSpade(player.getItemInHand(packet.hand))) return
        lastSpadeUseMs = System.currentTimeMillis()
        preciseTrail.clear()
        lastPreciseGuess = null
    }

    @SubscribeEvent
    fun onIncomingPacket(event: PacketEvent.Incoming) {
        if (!enabled.value) return
        val packet = event.packet as? ClientboundLevelParticlesPacket ?: return
        if (arrowGuess.value && handleArrowParticle(packet)) return
        if (!preciseGuess.value) return
        if (!isPreciseSpadeParticle(packet)) return

        val now = System.currentTimeMillis()
        if (lastSpadeUseMs == 0L || now - lastSpadeUseMs > 3_000L) return

        val point = Vec3(packet.x, packet.y, packet.z)
        val last = preciseTrail.lastOrNull()
        if (last != null) {
            val dist = last.distanceTo(point)
            if (dist == 0.0 || dist > 3.0) return
        }

        preciseTrail += point
        if (preciseTrail.size > 18) preciseTrail.removeAt(0)

        val guess = solvePreciseGuess() ?: return
        val blockGuess = Vec3(
            guess.x.roundToInt().toDouble(),
            (guess.y - 0.5).roundToInt().toDouble(),
            guess.z.roundToInt().toDouble()
        )
        if (lastPreciseGuess == blockGuess) return
        lastPreciseGuess = blockGuess
        lastWarpMs = now
        DianaParticleTracker.addGuess(blockGuess)
    }

    @SubscribeEvent
    fun onChat(event: ChatEvent.Receive) {
        if (!enabled.value) return
        val raw = event.message ?: return
        val message = ChatFormatting.stripFormatting(raw)?.trim().orEmpty()
        if (message.isEmpty()) return

        val spawnMatch = MYTHOLOGICAL_SPAWN_PATTERN.find(message)
        if (spawnMatch != null) {
            val creature = normalizeCreatureName(spawnMatch.groupValues[1])
            recordCreature(creature)
            lastSpadeUseMs = 0L

            if (creature in SHARD_WARNING_MOBS) {
                alert("Black Hole", "$creature can drop a Black Hole shard.", ChatFormatting.DARK_PURPLE)
            }
            return
        }

        maybeHandleRareDrop(message)
        maybeHandleSharedRareMob(message)

        if (message.startsWith("You dug out a Griffin Burrow!") ||
            message.startsWith("You finished the Griffin burrow chain!")
        ) {
            lastSpadeUseMs = 0L
        } else if (message == "Poof! You have cleared your griffin burrows!") {
            DianaParticleTracker.reset()
            rareMobTargets.clear()
            ChatUtils.sendMessage("Diana burrow data cleared.")
        }
    }

    private fun isPreciseSpadeParticle(packet: ClientboundLevelParticlesPacket): Boolean {
        return packet.particle.type == ParticleTypes.DRIPPING_LAVA &&
            packet.count == 2 &&
            abs(packet.maxSpeed - -0.5f) <= 0.005f
    }

    private fun handleArrowParticle(packet: ClientboundLevelParticlesPacket): Boolean {
        val player = mc.player ?: return false
        if (packet.particle.type != ParticleTypes.DUST) return false
        if (packet.count != 0 || abs(packet.maxSpeed - 1.0f) > 0.005f) return false
        if (player.position().distanceToSqr(packet.x, packet.y, packet.z) > 36.0) return false
        val range = arrowRange(packet.xDist, packet.yDist, packet.zDist) ?: return false

        arrowPoints += Vec3(packet.x, packet.y, packet.z)
        if (arrowPoints.size > 80) arrowPoints.removeAt(0)

        val ray = detectArrowRay() ?: return true
        val key = "${ray.origin.x.roundToInt()}:${ray.origin.y.roundToInt()}:${ray.origin.z.roundToInt()}:${ray.direction.x}:${ray.direction.y}:${ray.direction.z}"
        if (!recentArrowKeys.add(key)) return true
        while (recentArrowKeys.size > 10) recentArrowKeys.remove(recentArrowKeys.first())
        arrowPoints.clear()
        lastWarpMs = System.currentTimeMillis()
        addArrowGuess(ray, range)
        return true
    }

    private data class Ray(val origin: Vec3, val direction: Vec3)

    private fun arrowRange(r: Float, g: Float, b: Float): IntRange? {
        return when {
            close(r, 0f) && close(g, 128f) && close(b, 0f) -> 0..117
            close(r, 255f) && close(g, 255f) && close(b, 0f) -> 112..282
            close(r, 255f) && close(g, 0f) && close(b, 0f) -> 281..600
            else -> null
        }
    }

    private fun detectArrowRay(): Ray? {
        val line = findParticleLine(arrowPoints, 20, 0.12)
        if (line.size < 20) return null
        val candidate1 = line[1]
        val candidate2 = line[line.size - 2]
        val count1 = countNear(arrowPoints, candidate1, 0.12)
        val count2 = countNear(arrowPoints, candidate2, 0.12)
        if (setOf(count1, count2) != setOf(2, 4)) return null

        val tip: Vec3
        val base: Vec3
        if (count1 == 4) {
            tip = line.first()
            base = line.last()
        } else {
            tip = line.last()
            base = line.first()
        }

        val adjustedBase = base.add(0.0, -1.5, 0.0)
        val adjustedTip = tip.add(0.0, -1.5, 0.0)
        val direction = adjustedTip.subtract(adjustedBase).normalize()
        if (!direction.x.isFinite() || !direction.y.isFinite() || !direction.z.isFinite()) return null
        return Ray(adjustedBase, direction)
    }

    private fun findParticleLine(points: List<Vec3>, shaftLength: Int, maxDist: Double): List<Vec3> {
        for (index in points.indices) {
            val line = ArrayList<Vec3>()
            val used = HashSet<Int>()
            line += points[index]
            used += index
            if (extendParticleLine(line, used, points, shaftLength, maxDist)) return line
        }
        return emptyList()
    }

    private fun extendParticleLine(
        line: MutableList<Vec3>,
        used: MutableSet<Int>,
        points: List<Vec3>,
        shaftLength: Int,
        maxDist: Double,
    ): Boolean {
        if (line.size == shaftLength) return true
        var nextIndex = -1
        var minDist = Double.MAX_VALUE
        for (i in points.indices) {
            if (i in used) continue
            val point = points[i]
            val dist = line.last().distanceTo(point)
            if (dist > maxDist) continue
            val second = if (line.size > 1) line[1] else line[0]
            if (!isCollinear(line.first(), second, point)) continue
            if (dist < minDist) {
                minDist = dist
                nextIndex = i
            }
        }
        if (nextIndex < 0) return false
        line += points[nextIndex]
        used += nextIndex
        if (extendParticleLine(line, used, points, shaftLength, maxDist)) return true
        line.removeAt(line.lastIndex)
        used.remove(nextIndex)
        return false
    }

    private fun isCollinear(a: Vec3, b: Vec3, c: Vec3): Boolean {
        val ab = b.subtract(a)
        val ac = c.subtract(a)
        return ab.cross(ac).lengthSqr() < 1.0e-6
    }

    private fun countNear(points: Iterable<Vec3>, origin: Vec3, maxDist: Double): Int {
        val maxDistSq = maxDist * maxDist
        return points.count { it != origin && it.distanceToSqr(origin) <= maxDistSq }
    }

    private fun addArrowGuess(ray: Ray, range: IntRange) {
        val level = mc.level ?: return
        var distance = range.first.coerceAtLeast(1).toDouble()
        while (distance <= range.last) {
            val point = ray.origin.add(ray.direction.scale(distance))
            val pos = BlockPos(point.x.roundToInt(), point.y.roundToInt(), point.z.roundToInt())
            if (isValidBurrowBlock(level, pos)) {
                DianaParticleTracker.addGuess(Vec3(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()))
                return
            }
            distance += 1.0
        }
    }

    private fun isValidBurrowBlock(level: net.minecraft.world.level.Level, pos: BlockPos): Boolean {
        if (!level.isLoaded(pos)) return false
        val block = level.getBlockState(pos).block
        val above = level.getBlockState(pos.above()).block
        return block == Blocks.GRASS_BLOCK && above in ALLOWED_BURROW_ABOVE
    }

    private fun solvePreciseGuess(): Vec3? {
        if (preciseTrail.size < 4) return null
        val degree = 3
        val coeffX = fitPolynomial(preciseTrail.map { it.x }, degree) ?: return null
        val coeffY = fitPolynomial(preciseTrail.map { it.y }, degree) ?: return null
        val coeffZ = fitPolynomial(preciseTrail.map { it.z }, degree) ?: return null

        val derivative = Vec3(coeffX.getOrElse(1) { 0.0 }, coeffY.getOrElse(1) { 0.0 }, coeffZ.getOrElse(1) { 0.0 })
        val len = derivative.length()
        if (!len.isFinite() || len < 1.0e-6) return null
        val t = (3.0 * computePitchWeight(derivative) / len).coerceIn(0.0, 120.0)
        val guess = Vec3(evalPolynomial(coeffX, t), evalPolynomial(coeffY, t), evalPolynomial(coeffZ, t))
        if (!guess.x.isFinite() || !guess.y.isFinite() || !guess.z.isFinite()) return null
        return guess
    }

    private fun fitPolynomial(values: List<Double>, degree: Int): DoubleArray? {
        val n = degree + 1
        val matrix = Array(n) { DoubleArray(n + 1) }
        for (row in 0 until n) {
            for (col in 0 until n) {
                var sum = 0.0
                for (i in values.indices) sum += i.toDouble().pow(row + col)
                matrix[row][col] = sum
            }
            var rhs = 0.0
            for (i in values.indices) rhs += values[i] * i.toDouble().pow(row)
            matrix[row][n] = rhs
        }
        return solveLinearSystem(matrix)
    }

    private fun solveLinearSystem(matrix: Array<DoubleArray>): DoubleArray? {
        val n = matrix.size
        for (pivot in 0 until n) {
            var best = pivot
            for (row in pivot + 1 until n) {
                if (abs(matrix[row][pivot]) > abs(matrix[best][pivot])) best = row
            }
            if (abs(matrix[best][pivot]) < 1.0e-9) return null
            if (best != pivot) {
                val tmp = matrix[pivot]
                matrix[pivot] = matrix[best]
                matrix[best] = tmp
            }

            val scale = matrix[pivot][pivot]
            for (col in pivot until n + 1) matrix[pivot][col] /= scale
            for (row in 0 until n) {
                if (row == pivot) continue
                val factor = matrix[row][pivot]
                for (col in pivot until n + 1) matrix[row][col] -= factor * matrix[pivot][col]
            }
        }
        return DoubleArray(n) { matrix[it][n] }
    }

    private fun evalPolynomial(coefficients: DoubleArray, t: Double): Double {
        var result = 0.0
        for (i in coefficients.indices.reversed()) {
            result = result * t + coefficients[i]
        }
        return result
    }

    private fun computePitchWeight(derivative: Vec3): Double {
        return sqrt(24.0 * sin(getPitchFromDerivative(derivative) - PI) + 25.0)
    }

    private fun getPitchFromDerivative(derivative: Vec3): Double {
        val xzLength = sqrt(derivative.x.pow(2) + derivative.z.pow(2))
        val pitchRadians = -atan2(derivative.y, xzLength)
        var guessPitch = pitchRadians
        var resultPitch = atan2(sin(guessPitch) - 0.75, cos(guessPitch))
        var windowMax = PI / 2.0
        var windowMin = -PI / 2.0
        repeat(100) {
            if (resultPitch < pitchRadians) {
                windowMin = guessPitch
                guessPitch = (windowMin + windowMax) / 2.0
            } else {
                windowMax = guessPitch
                guessPitch = (windowMin + windowMax) / 2.0
            }
            resultPitch = atan2(sin(guessPitch) - 0.75, cos(guessPitch))
            if (resultPitch == pitchRadians) return guessPitch
        }
        return guessPitch
    }

    // -- World Render ----------------------------------------------------------

    @SubscribeEvent
    fun onRender(@Suppress("UNUSED_PARAMETER") event: WorldRenderEvent.Last) {
        if (!enabled.value) return
        val level = mc.level ?: return

        val argb  = waypointColor.value
        val alpha = (argb ushr 24) and 0xFF
        val r     = (argb shr 16)  and 0xFF
        val g     = (argb shr 8)   and 0xFF
        val blue  =  argb          and 0xFF

        OverlayRenderEngine.clearTag("diana-helper")

        val records = if (burrowDetection.value) {
            DianaParticleTracker.getBurrowRecords(level, expireSeconds.value.toLong() * 1000L)
        } else {
            emptyList()
        }

        // Waypoint box + beacon beam for every known burrow
        if (showWaypoint.value) {
            for ((bp, type) in records) {
                val boxColor = type.toColor(alpha)
                OverlayRenderEngine.addBox(
                    level,
                    bp.x - 0.5, bp.y, bp.z - 0.5,
                    bp.x + 0.5, bp.y + 1.0, bp.z + 0.5,
                    fill    = boxColor.withAlpha(alpha / 4),
                    outline = boxColor,
                    durationTicks = 3,
                    tag = "diana-helper",
                    forceRender = true
                )
                OverlayRenderEngine.addLine(
                    level,
                    bp.x, bp.y, bp.z,
                    bp.x, bp.y + 12.0, bp.z,
                    boxColor.withAlpha(alpha * 7 / 10), 3f,
                    durationTicks = 3,
                    tag = "diana-helper",
                    forceRender = true
                )
            }
        }

        val nearest = burrowPos
        if (nearest == null) {
            renderRareMobWaypoints(level, r, g, blue, alpha)
            renderWarpHint(level, alpha)
            return
        }

        // Particle direction line - from activation position toward the nearest burrow
        val activationPos = DianaParticleTracker.getActivationPos()
        if (activationPos != null) {
            OverlayRenderEngine.addLine(
                level,
                activationPos.x, activationPos.y + 0.05, activationPos.z,
                nearest.x, nearest.y + 0.05, nearest.z,
                OREColor(r, g, blue, alpha / 3), 1f,
                durationTicks = 3,
                tag = "diana-helper",
                forceRender = true
            )
        }

        // Path line: cached path nodes -> player eye -> nearest burrow
        if (showLine.value && shouldDrawLineToNearest(records, nearest)) {
            val player = mc.player ?: return
            val eye = player.getEyePosition()
            val nodes = NativePathfinder.cachedPathNodes
            val width = lineWidth.value.toFloat()
            if (nodes.size >= 2) {
                for (i in 0 until nodes.size - 1) {
                    val a = nodes[i]; val nb = nodes[i + 1]
                    OverlayRenderEngine.addLine(
                        level,
                        a.x, a.y + 0.05, a.z,
                        nb.x, nb.y + 0.05, nb.z,
                        OREColor(r, g, blue, alpha * 3 / 4), width,
                        durationTicks = 3,
                        tag = "diana-helper",
                        forceRender = true
                    )
                }
                val last = nodes.last()
                OverlayRenderEngine.addLine(
                    level,
                    last.x, last.y + 0.05, last.z,
                    eye.x, eye.y, eye.z,
                    OREColor(r, g, blue, alpha / 2), (width - 1f).coerceAtLeast(1f),
                    durationTicks = 3,
                    tag = "diana-helper",
                    forceRender = true
                )
            }
            // Eye -> nearest burrow (always - bridges path end to target)
            OverlayRenderEngine.addLine(
                level,
                eye.x, eye.y, eye.z,
                nearest.x, nearest.y + 0.5, nearest.z,
                OREColor(r, g, blue, alpha * 2 / 3), width,
                durationTicks = 3,
                tag = "diana-helper",
                forceRender = true
            )
        }

        renderRareMobWaypoints(level, r, g, blue, alpha)
        renderWarpHint(level, alpha)
    }

    // -- Compass HUD -----------------------------------------------------------

    val compassHud = hudElement("diana-helper-compass", "Diana Compass") {
        anchor  = HudAnchor.BOTTOM_CENTER
        offsetY = -20f

        width  { 90f }
        height { 90f }

        render { x, y, _ ->
            if (!showHud.value) return@render
            val player = mc.player ?: return@render
            val bp = burrowPos ?: return@render

            val argb  = waypointColor.value
            val alpha = (argb ushr 24) and 0xFF
            val rr    = (argb shr 16)  and 0xFF
            val gg    = (argb shr 8)   and 0xFF
            val bb    =  argb          and 0xFF

            val centerX = x + 45f
            val centerY = y + 38f
            val trackRadius = 26f

            NVGRenderer.rect(x, y, 90f, 90f, ThemeManager.currentTheme.panel, 8f)
            NVGRenderer.hollowRect(x, y, 90f, 90f, 1f, ThemeManager.currentTheme.controlBorder, 8f)
            NVGRenderer.hollowRect(
                centerX - trackRadius, centerY - trackRadius,
                trackRadius * 2f, trackRadius * 2f,
                1f, ThemeManager.currentTheme.overlay, trackRadius
            )

            val dx = bp.x - player.x
            val dz = bp.z - player.z
            val targetYawDeg = Math.toDegrees(Math.atan2(-dx, dz))
            val relAngleRad  = Math.toRadians(targetYawDeg - player.yRot)

            val dotX = centerX + (Math.sin(relAngleRad) * trackRadius).toFloat()
            val dotY = centerY - (Math.cos(relAngleRad) * trackRadius).toFloat()

            val dimArgb = ((alpha * 6 / 10) shl 24) or (rr shl 16) or (gg shl 8) or bb
            NVGRenderer.line(centerX, centerY, dotX, dotY, 2f, dimArgb)
            NVGRenderer.circle(dotX, dotY, 5f, argb)

            val dist = player.position().distanceTo(bp)
            val distStr = if (dist >= 100.0) "${dist.toInt()}m" else "${"%.1f".format(dist)}m"
            val textW = NVGRenderer.textWidth(distStr, 11f)
            NVGRenderer.text(distStr, centerX - textW / 2f, y + 74f, 11f, ThemeManager.currentTheme.text)

            val warp = currentWarp
            if (warp != null && nearestWarp.value) {
                val label = "Warp ${warp.displayName}"
                val warpW = NVGRenderer.textWidth(label, 9f)
                NVGRenderer.text(label, centerX - warpW / 2f, y + 62f, 9f, ThemeManager.currentTheme.accent)
            }
        }
    }

    val trackerHud = hudElement("diana-helper-tracker", "Diana Tracker") {
        anchor = HudAnchor.TOP_RIGHT
        offsetX = -12f
        offsetY = 72f

        width { 178f }
        height {
            val creatureRows = if (creatureTracker.value) creatureCounts.size.coerceAtMost(8) + trackedRareSinceCount() else 0
            val profitRows = if (profitTracker.value) 11 else 0
            val sboRows = if (statsTracker.value || magicFindTracker.value) buildSboStatsRows().take(8).size else 0
            (42f + (creatureRows + profitRows + sboRows) * 13f).coerceAtLeast(26f)
        }

        render { x, y, _ ->
            if (!creatureTracker.value && !profitTracker.value && !statsTracker.value && !magicFindTracker.value) return@render

            val rows = creatureCounts.entries.sortedByDescending { it.value }.take(8)
            val rareSince = rareSinceRows()
            val snap = DianaProfitTracker.snapshot()
            val profitRows = if (profitTracker.value) 11 else 0
            val sboRows = if (statsTracker.value || magicFindTracker.value) buildSboStatsRows().take(8) else emptyList()
            val h = 42f + ((if (creatureTracker.value) rows.size + rareSince.size else 0) + profitRows + sboRows.size) * 13f
            NVGRenderer.rect(x, y, 178f, h, ThemeManager.currentTheme.panel, 8f)
            NVGRenderer.hollowRect(x, y, 178f, h, 1f, ThemeManager.currentTheme.controlBorder, 8f)
            NVGRenderer.text("Diana Session", x + 10f, y + 10f, 11f, ThemeManager.currentTheme.text)

            NVGRenderer.text("Runtime: ${formatDuration(snap.runtimeMs)}", x + 10f, y + 25f, 9f, ThemeManager.currentTheme.textSecondary)

            var rowY = y + 42f
            if (profitTracker.value) {
                val timing = listOf(
                    "Profit: ${formatCoins(snap.coins)}",
                    "Profit/hr: ${formatCoins(snap.coinsPerHour)}",
                    "Burrows: ${snap.burrows}  Chains: ${snap.chains}",
                    "Mobs: ${snap.mobs}  Rare: ${snap.rareMobs}",
                    "Detect: ${formatDuration(snap.avgDetectMs)}",
                    "Travel: ${formatDuration(snap.avgTravelMs)}",
                    "Dig: ${formatDuration(snap.avgDigMs)}",
                    "Combat: ${formatDuration(snap.avgCombatMs)}",
                    "Loop: ${formatDuration(snap.avgLoopMs)}",
                    "Drops: ${snap.drops.sumOf { it.second }}",
                    snap.drops.firstOrNull()?.let { "${it.first}: ${it.second}" } ?: "Drops: none",
                )
                for (line in timing) {
                    NVGRenderer.text(line, x + 10f, rowY, 9f, ThemeManager.currentTheme.text)
                    rowY += 13f
                }
            }

            if (creatureTracker.value) {
                for ((name, count) in rows) {
                    NVGRenderer.text("$name: $count", x + 10f, rowY, 9f, ThemeManager.currentTheme.text)
                    rowY += 13f
                }
                for ((name, since) in rareSince) {
                    NVGRenderer.text("Since $name: $since", x + 10f, rowY, 9f, ThemeManager.currentTheme.accent)
                    rowY += 13f
                }
            }

            for (line in sboRows) {
                NVGRenderer.text(line, x + 10f, rowY, 9f, ThemeManager.currentTheme.accent)
                rowY += 13f
            }
        }
    }

    private fun syncRareMobs(level: net.minecraft.client.multiplayer.ClientLevel, player: net.minecraft.world.entity.player.Player) {
        val now = System.currentTimeMillis()
        rareMobTargets.values.removeIf { now - it.lastSeenMs > RARE_MOB_EXPIRE_MS }

        for (entity in level.entitiesForRendering().filterIsInstance<LivingEntity>()) {
            if (!entity.isAlive || entity.uuid == player.uuid) continue
            val name = cleanEntityName(entity)
            val rareName = RARE_MOBS.firstOrNull { name.contains(it, ignoreCase = true) } ?: continue
            val waypoint = rareMobTargets.getOrPut(entity.uuid) {
                RareMobWaypoint(entity.uuid, rareName, entity.position(), now, now)
            }
            waypoint.pos = entity.position()
            waypoint.lastSeenMs = now
            maybeShareRareMob(waypoint)
        }

        if (highlightRareMobs.value) {
            val targets = rareMobTargets.keys.mapNotNull { uuid ->
                val entity = level.getEntity(uuid) as? LivingEntity ?: return@mapNotNull null
                ClientGlowEspManager.GlowTarget(entity, rareMobGlowColor.value, RARE_MOB_GLOW_PRIORITY)
            }
            ClientGlowEspManager.sync(RARE_MOB_GLOW_SCOPE, level, targets)
        } else {
            ClientGlowEspManager.clear(RARE_MOB_GLOW_SCOPE, level)
        }
    }

    private fun renderRareMobWaypoints(level: net.minecraft.world.level.Level, r: Int, g: Int, b: Int, alpha: Int) {
        if (!rareMobWaypoints.value) return
        val player = mc.player ?: return
        val eye = player.getEyePosition()
        val now = System.currentTimeMillis()
        rareMobTargets.values.removeIf { now - it.lastSeenMs > RARE_MOB_EXPIRE_MS }

        for (waypoint in rareMobTargets.values) {
            val pos = waypoint.pos
            val dist = player.position().distanceTo(pos)
            if (removeRareMobDistance.value > 0.0 && dist <= removeRareMobDistance.value) continue
            OverlayRenderEngine.addBox(
                level,
                pos.x - 0.5, pos.y, pos.z - 0.5,
                pos.x + 0.5, pos.y + 2.0, pos.z + 0.5,
                fill = OREColor(255, 85, 255, alpha / 5),
                outline = OREColor(255, 85, 255, alpha),
                durationTicks = 3,
                tag = "diana-helper",
                forceRender = true
            )
            if (rareMobLine.value) {
                OverlayRenderEngine.addLine(
                    level,
                    eye.x, eye.y, eye.z,
                    pos.x, pos.y + 1.0, pos.z,
                    OREColor(255, 85, 255, alpha * 3 / 4), lineWidth.value.toFloat(),
                    durationTicks = 3,
                    tag = "diana-helper",
                    forceRender = true
                )
            }
            if (removeRareMobBeamDistance.value <= 0.0 || dist > removeRareMobBeamDistance.value) {
                OverlayRenderEngine.addLine(
                    level,
                    pos.x, pos.y, pos.z,
                    pos.x, pos.y + 12.0, pos.z,
                    OREColor(255, 85, 255, alpha / 2), 2f,
                    durationTicks = 3,
                    tag = "diana-helper",
                    forceRender = true
                )
            }
        }
    }

    private fun maybeWarnPet() {
        if (!petWarning.value) return
        val player = mc.player ?: return
        if (!isDianaSpade(player.mainHandItem) && !hasDianaSpadeInHotbar()) return

        PetTabListParser.update()
        val petName = PetTabListParser.current?.name.orEmpty()
        if (petName.contains("Griffin", ignoreCase = true)) return

        val now = System.currentTimeMillis()
        if (now - lastPetWarnMs < 30_000L) return
        lastPetWarnMs = now
        alert("Griffin Pet!", "Equip a Griffin pet for Mythological Ritual.", ChatFormatting.RED)
    }

    private fun maybeWarnParticleQuality() {
        if (!particleFailHint.value || lastSpadeUseMs == 0L) return
        val now = System.currentTimeMillis()
        if (now - lastSpadeUseMs < 3_000L) return
        if (DianaParticleTracker.getLastParticleMs() >= lastSpadeUseMs || burrowPos != null) {
            lastSpadeUseMs = 0L
            return
        }
        if (now - lastParticleFailWarnMs < 30_000L) return
        lastParticleFailWarnMs = now
        ChatUtils.sendMessage("Diana: no guess particles found. Try /particlequality extreme, then use the spade again.")
        NotificationManager.queue("Diana Helper", "No guess particles detected.", 3500L)
    }

    private fun recordCreature(name: String) {
        creatureCounts[name] = (creatureCounts[name] ?: 0) + 1
        for (creature in CREATURE_NAMES) {
            creatureSince[creature] = if (creature.equals(name, ignoreCase = true)) 0 else (creatureSince[creature] ?: 0) + 1
        }
    }

    private fun rareSinceRows(): List<Pair<String, Int>> {
        return RARE_TRACKED_MOBS.mapNotNull { name ->
            val count = creatureCounts[name] ?: 0
            val since = creatureSince[name] ?: return@mapNotNull null
            if (count <= 0) null else name to since
        }.sortedBy { it.second }
    }

    private fun trackedRareSinceCount(): Int = rareSinceRows().size

    private fun formatCoins(value: Long): String {
        val absValue = kotlin.math.abs(value)
        return when {
            absValue >= 1_000_000_000L -> String.format("%.2fb", value / 1_000_000_000.0)
            absValue >= 1_000_000L -> String.format("%.2fm", value / 1_000_000.0)
            absValue >= 1_000L -> String.format("%.1fk", value / 1_000.0)
            else -> value.toString()
        }
    }

    private fun formatDuration(ms: Long): String {
        if (ms <= 0L) return "--"
        val totalSeconds = ms / 1000L
        val minutes = totalSeconds / 60L
        val seconds = totalSeconds % 60L
        return if (minutes > 0L) "${minutes}m ${seconds}s" else "${seconds}s"
    }

    private fun updateNearestWarp() {
        if (!nearestWarp.value) {
            currentWarp = null
            return
        }
        val player = mc.player ?: return
        val target = burrowPos ?: run {
            currentWarp = null
            return
        }
        val allowed = allowedWarpCommands()
        val best = HUB_WARPS
            .filter { it.command in allowed }
            .minByOrNull { it.pos.distanceTo(target) + it.extraBlocks } ?: return
        val playerDistance = player.position().distanceTo(target)
        val warpDistance = best.pos.distanceTo(target) + best.extraBlocks
        currentWarp = if (
            (!dontWarpIfClose.value || playerDistance > closeWarpDistance.value) &&
            playerDistance - warpDistance >= warpDistanceDifference.value
        ) best else null
    }

    private fun renderWarpHint(level: net.minecraft.world.level.Level, alpha: Int) {
        val warp = currentWarp ?: return
        if (!nearestWarp.value) return
        OverlayRenderEngine.addBox(
            level,
            warp.pos.x - 0.5, warp.pos.y, warp.pos.z - 0.5,
            warp.pos.x + 0.5, warp.pos.y + 2.0, warp.pos.z + 0.5,
            fill = OREColor(85, 255, 255, alpha / 6),
            outline = OREColor(85, 255, 255, alpha * 3 / 4),
            durationTicks = 3,
            tag = "diana-helper",
            forceRender = true
        )
    }

    private fun close(actual: Float, expected: Float): Boolean = abs(actual - expected) <= 0.005f

    private fun resetTracker() {
        creatureCounts.clear()
        creatureSince.clear()
        for (name in CREATURE_NAMES) creatureSince[name] = 0
        ChatUtils.sendMessage("Diana creature tracker reset.")
    }

    private fun maybeShareRareMob(waypoint: RareMobWaypoint) {
        if (!shareRareMobs.value || waypoint.shared) return
        waypoint.shared = true
        val x = floor(waypoint.pos.x).toInt()
        val y = floor(waypoint.pos.y).toInt()
        val z = floor(waypoint.pos.z).toInt()
        mc.player?.connection?.sendCommand("pc ${waypoint.name} at x:$x y:$y z:$z")
    }

    private fun useSuggestedWarp() {
        val warp = currentWarp ?: return
        if (warpDelay.value > 0.0 && System.currentTimeMillis() - lastWarpMs < warpDelay.value.toLong()) return
        mc.player?.connection?.sendCommand("warp ${warp.command}")
    }

    private fun allowedWarpCommands(): Set<String> {
        val configured = allowedWarps.value.split(',', ';', ' ')
            .map { normalizeWarpName(it) }
            .filter { it.isNotBlank() }
            .toSet()
        return configured.ifEmpty { setOf("wizard", "da", "castle", "crypt", "stonks") }
    }

    private fun normalizeWarpName(raw: String): String {
        val text = raw.trim().lowercase()
        return when (text) {
            "darkauction", "dark_auction", "dark-auction" -> "da"
            else -> text
        }
    }

    private fun shouldDrawLineToNearest(records: List<Pair<Vec3, DianaParticleTracker.BurrowType>>, nearest: Vec3): Boolean {
        val type = records.firstOrNull { it.first.distanceToSqr(nearest) < 1.0 }?.second ?: DianaParticleTracker.BurrowType.GUESS
        return when (type) {
            DianaParticleTracker.BurrowType.GUESS -> guessLine.value
            else -> burrowLine.value
        }
    }

    private fun pruneCloseGuesses(level: net.minecraft.world.level.Level, player: net.minecraft.world.entity.player.Player) {
        val distance = removeGuessDistance.value
        if (distance <= 0.0) return
        DianaParticleTracker.getBurrowRecords(level, expireSeconds.value.toLong() * 1000L)
            .filter { it.second == DianaParticleTracker.BurrowType.GUESS && player.position().distanceTo(it.first) <= distance }
            .forEach { DianaParticleTracker.removeBurrow(floor(it.first.x).toInt(), floor(it.first.z).toInt()) }
    }

    private fun maybeHandleSharedRareMob(message: String) {
        if (!receiveRareMobs.value) return
        val coords = COORD_PATTERN.find(message) ?: return
        val name = RARE_MOBS.firstOrNull { message.contains(it, ignoreCase = true) }
            ?: if (allChatCoordsAreRare.value) "Rare Diana Mob" else return
        val x = coords.groupValues[1].toDoubleOrNull() ?: return
        val y = coords.groupValues[2].toDoubleOrNull() ?: return
        val z = coords.groupValues[3].toDoubleOrNull() ?: return
        val now = System.currentTimeMillis()
        val uuid = UUID.nameUUIDFromBytes("diana-party:$name:$x:$y:$z".toByteArray())
        rareMobTargets[uuid] = RareMobWaypoint(uuid, name, Vec3(x, y, z), now, now, shared = true)
    }

    private fun maybeHandleRareDrop(message: String) {
        val drop = RARE_DROP_NAMES.firstOrNull { message.contains(it, ignoreCase = true) } ?: return
        if (!message.contains("RARE DROP", ignoreCase = true) && drop != "Mythological Dye") return
        val mf = MAGIC_FIND_PATTERN.find(message)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0
        rareDropCounts[drop] = (rareDropCounts[drop] ?: 0) + 1
        for (name in RARE_DROP_NAMES) {
            rareDropSince[name] = if (name.equals(drop, ignoreCase = true)) 0 else (rareDropSince[name] ?: 0) + 1
        }
        if (mf > (highestMagicFind[drop] ?: 0)) highestMagicFind[drop] = mf

        val count = rareDropCounts[drop] ?: 1
        val text = if (drop == "Chimera") {
            customChimMessage.value.replace("{mf}", mf.toString()).replace("{amount}", count.toString())
        } else {
            "[SBO] RARE DROP! $drop!${if (mf > 0) " +$mf Magic Find" else ""} #$count"
        }
        if (rareDropChatAnnouncer.value) ChatUtils.sendMessage(text)
        if (rareDropScreenAnnouncer.value && drop in TITLE_DROPS) {
            mc.gui.setTitle(Component.literal(drop).withStyle(ChatFormatting.LIGHT_PURPLE))
            mc.gui.setSubtitle(Component.literal(if (mf > 0) "+$mf Magic Find" else "Rare Diana drop").withStyle(ChatFormatting.GOLD))
            NotificationManager.queue("Diana Rare Drop", drop, 4500L)
        }
        if (rareDropPartyAnnouncer.value && drop in PARTY_DROPS) {
            mc.player?.connection?.sendCommand("pc $text")
        }
    }

    private fun buildSboStatsRows(): List<String> {
        val rows = ArrayList<String>()
        if (statsTracker.value) {
            for (name in listOf("Minos Inquisitor", "Chimera", "Daedalus Stick", "Minos Relic", "Manti-core", "Brain Food", "Shimmering Wool")) {
                val since = if (name in RARE_MOBS) creatureSince[name] else rareDropSince[name]
                if (since != null) rows += "Since $name: $since"
            }
        }
        if (magicFindTracker.value) {
            highestMagicFind.entries.sortedByDescending { it.value }.take(4).forEach { (drop, mf) ->
                rows += "Best MF $drop: $mf"
            }
        }
        return rows
    }

    private fun alert(title: String, body: String, color: ChatFormatting) {
        mc.gui.setSubtitle(Component.literal(body).withStyle(ChatFormatting.GRAY))
        mc.gui.setTitle(Component.literal(title).withStyle(color))
        ChatUtils.sendMessage("$title $body")
        NotificationManager.queue(title, body, 3500L)
    }

    private fun hasDianaSpadeInHotbar(): Boolean {
        val inventory = mc.player?.inventory ?: return false
        for (slot in 0 until 9) {
            if (isDianaSpade(inventory.getItem(slot))) return true
        }
        return false
    }

    private fun isDianaSpade(stack: net.minecraft.world.item.ItemStack): Boolean {
        if (stack.isEmpty) return false
        val id = stack.getSkyblockId().uppercase()
        if (id in DIANA_SPADE_IDS) return true
        return ChatFormatting.stripFormatting(stack.hoverName.string)
            ?.contains("spade", ignoreCase = true) == true
    }

    private fun cleanEntityName(entity: LivingEntity): String {
        val raw = entity.customName?.string ?: entity.displayName.string
        return ChatFormatting.stripFormatting(raw)?.trim().orEmpty()
    }

    private fun normalizeCreatureName(raw: String): String {
        return raw.trim()
            .replace(Regex("""\s+"""), " ")
            .removePrefix("a ")
            .trim()
    }

    private val ALLOWED_BURROW_ABOVE = setOf(
        Blocks.AIR,
        Blocks.DANDELION,
        Blocks.POPPY,
        Blocks.SHORT_GRASS,
        Blocks.TALL_GRASS,
        Blocks.OAK_LEAVES,
        Blocks.SPRUCE_LEAVES,
        Blocks.BIRCH_LEAVES,
        Blocks.JUNGLE_LEAVES,
        Blocks.ACACIA_LEAVES,
        Blocks.DARK_OAK_LEAVES,
        Blocks.SPRUCE_FENCE,
    )

    private data class WarpPoint(
        val command: String,
        val displayName: String,
        val pos: Vec3,
        val extraBlocks: Int = 0,
    )

    private val HUB_WARPS = listOf(
        WarpPoint("hub", "Hub", Vec3(-3.0, 70.0, -70.0), 0),
        WarpPoint("castle", "Castle", Vec3(-250.0, 130.0, 45.0), 15),
        WarpPoint("crypt", "Crypt", Vec3(-160.0, 61.0, -100.0), 20),
        WarpPoint("da", "Dark Auction", Vec3(91.0, 75.0, 173.0), 15),
        WarpPoint("museum", "Museum", Vec3(-75.0, 76.0, 80.0), 15),
        WarpPoint("wizard", "Wizard", Vec3(42.0, 122.0, 69.0), 20),
        WarpPoint("stonks", "Stonks", Vec3(51.0, 72.0, -52.0), 10),
        WarpPoint("taylor", "Taylor", Vec3(-24.0, 71.0, -39.0), 10),
    )

    private const val RARE_MOB_GLOW_SCOPE = "diana_rare_mobs"
    private const val RARE_MOB_GLOW_PRIORITY = 20
    private const val RARE_MOB_EXPIRE_MS = 75_000L
    private val COORD_PATTERN = Regex("""(?:x[:= ]\s*|@?\s)(-?\d{1,4})(?:[, ]+\s*y[:= ]\s*|[, ]+)(-?\d{1,4})(?:[, ]+\s*z[:= ]\s*|[, ]+)(-?\d{1,4})""", RegexOption.IGNORE_CASE)
    private val MAGIC_FIND_PATTERN = Regex("""\+(\d+)\s*(?:✯|Magic Find)""", RegexOption.IGNORE_CASE)
    private val TITLE_DROPS = setOf("Chimera", "Daedalus Stick", "Minos Relic", "Manti-core", "Fateful Stinger", "Brain Food", "Shimmering Wool", "Mythological Dye")
    private val PARTY_DROPS = TITLE_DROPS
}
