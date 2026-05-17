package org.phantom.internal.diana

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ServerboundUseItemPacket
import net.minecraft.world.entity.LivingEntity
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
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.notification.NotificationManager
import org.phantom.api.pathfinder.jni.NativePathfinder
import org.phantom.api.ui.theme.ThemeManager
import org.phantom.api.util.ChatUtils
import org.phantom.api.util.getSkyblockId
import org.phantom.api.util.ui.NVGRenderer
import org.phantom.internal.helper.ClientGlowEspManager
import org.phantom.internal.pathfinding.OverlayRenderEngine
import org.phantom.internal.pathfinding.OverlayRenderEngine.Color as OREColor
import org.phantom.internal.visual.PetTabListParser
import java.util.UUID
import kotlin.math.floor

/**
 * Diana Helper - shows up to 4 predicted burrow locations as waypoints + directional compass HUD.
 *
 * Particle collection and burrow position detection is handled by DianaParticleTracker.
 * Each burrow block is pinpointed precisely from a single matching CRIT packet.
 */
object DianaHelperModule : Module("Diana Helper") {

    override val category = ModuleCategory.COMBAT

    private val mc = Minecraft.getInstance()

    // -- Settings --------------------------------------------------------------

    val enabled       = CheckboxSetting("Enabled",        "Show Diana burrow waypoints and direction HUD.", false)
    val showWaypoint  = CheckboxSetting("Show Waypoint",  "Render a highlighted box + beam at each detected burrow.", true)
    val showLine      = CheckboxSetting("Show Line",      "Draw a path line from your position to the nearest burrow.", true)
    val showHud       = CheckboxSetting("Show HUD",       "Show the burrow direction compass HUD.", true)
    val waypointColor = ColorSetting(  "Waypoint Color",  "Color of the burrow waypoints.", 0xFFFFD700.toInt())
    val expireSeconds = SliderSetting( "Expire Time",     "Seconds without a confirmed particle before a waypoint disappears.", 30.0, 5.0, 120.0, step = 1.0)
    val highlightRareMobs = CheckboxSetting("Highlight Rare Mobs", "Glow rare Diana mobs like Inquisitor, Sphinx, King Minos, and Manticore.", true)
    val rareMobWaypoints = CheckboxSetting("Rare Mob Waypoints", "Render waypoints and lines to detected rare Diana mobs.", true)
    val shareRareMobs = CheckboxSetting("Share Rare Mobs", "Send rare Diana mob coordinates to party chat when detected.", false)
    val petWarning = CheckboxSetting("Griffin Pet Warning", "Warn when using a Diana spade without a Griffin pet visible in tab.", true)
    val particleFailHint = CheckboxSetting("Particle Fail Hint", "Warn if a spade use does not produce Diana guess particles.", true)
    val creatureTracker = CheckboxSetting("Creature Tracker", "Show Mythological creature counts and rare-mob since counters.", true)
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
            highlightRareMobs,
            rareMobWaypoints,
            shareRareMobs,
            petWarning,
            particleFailHint,
            creatureTracker,
            resetCreatureTracker,
        )
        EventBus.register(this)
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
            syncRareMobs(level, player)
            maybeWarnPet()
            maybeWarnParticleQuality()
        }
    }

    @SubscribeEvent
    fun onPacket(event: PacketEvent.Outgoing) {
        if (!enabled.value) return
        val packet = event.packet as? ServerboundUseItemPacket ?: return
        val player = mc.player ?: return
        if (!isDianaSpade(player.getItemInHand(packet.hand))) return
        lastSpadeUseMs = System.currentTimeMillis()
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

        val records = DianaParticleTracker.getBurrowRecords(level, expireSeconds.value.toLong() * 1000L)

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

        val nearest = burrowPos ?: return

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
        if (showLine.value) {
            val player = mc.player ?: return
            val eye = player.getEyePosition()
            val nodes = NativePathfinder.cachedPathNodes
            if (nodes.size >= 2) {
                for (i in 0 until nodes.size - 1) {
                    val a = nodes[i]; val nb = nodes[i + 1]
                    OverlayRenderEngine.addLine(
                        level,
                        a.x, a.y + 0.05, a.z,
                        nb.x, nb.y + 0.05, nb.z,
                        OREColor(r, g, blue, alpha * 3 / 4), 2f,
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
                    OREColor(r, g, blue, alpha / 2), 1.5f,
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
                OREColor(r, g, blue, alpha * 2 / 3), 2f,
                durationTicks = 3,
                tag = "diana-helper",
                forceRender = true
            )
        }

        renderRareMobWaypoints(level, r, g, blue, alpha)
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
        }
    }

    val trackerHud = hudElement("diana-helper-tracker", "Diana Tracker") {
        anchor = HudAnchor.TOP_RIGHT
        offsetX = -12f
        offsetY = 72f

        width { 178f }
        height {
            if (!creatureTracker.value || creatureCounts.isEmpty()) 26f
            else (42f + creatureCounts.size.coerceAtMost(8) * 13f + trackedRareSinceCount() * 13f)
        }

        render { x, y, _ ->
            if (!creatureTracker.value) return@render

            val rows = creatureCounts.entries.sortedByDescending { it.value }.take(8)
            val rareSince = rareSinceRows()
            val h = 42f + rows.size * 13f + rareSince.size * 13f
            NVGRenderer.rect(x, y, 178f, h, ThemeManager.currentTheme.panel, 8f)
            NVGRenderer.hollowRect(x, y, 178f, h, 1f, ThemeManager.currentTheme.controlBorder, 8f)
            NVGRenderer.text("Mythological Creatures", x + 10f, y + 10f, 11f, ThemeManager.currentTheme.text)

            val total = creatureCounts.values.sum()
            NVGRenderer.text("Total: $total", x + 10f, y + 25f, 9f, ThemeManager.currentTheme.textSecondary)

            var rowY = y + 42f
            for ((name, count) in rows) {
                val label = "$name: $count"
                NVGRenderer.text(label, x + 10f, rowY, 9f, ThemeManager.currentTheme.text)
                rowY += 13f
            }
            for ((name, since) in rareSince) {
                NVGRenderer.text("Since $name: $since", x + 10f, rowY, 9f, ThemeManager.currentTheme.accent)
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
                ClientGlowEspManager.GlowTarget(entity, RARE_MOB_COLOR, RARE_MOB_GLOW_PRIORITY)
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
            OverlayRenderEngine.addLine(
                level,
                eye.x, eye.y, eye.z,
                pos.x, pos.y + 1.0, pos.z,
                OREColor(255, 85, 255, alpha * 3 / 4), 2.5f,
                durationTicks = 3,
                tag = "diana-helper",
                forceRender = true
            )
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

    private const val RARE_MOB_GLOW_SCOPE = "diana_rare_mobs"
    private const val RARE_MOB_GLOW_PRIORITY = 20
    private const val RARE_MOB_COLOR = 0xFFFF55FF.toInt()
    private const val RARE_MOB_EXPIRE_MS = 75_000L
}
