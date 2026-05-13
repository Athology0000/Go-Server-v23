package org.cobalt.internal.skyblock

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.ChatEvent
import org.cobalt.api.pathfinder.cache.CachedWorld
import org.cobalt.api.util.ChatUtils
import org.cobalt.api.util.ScoreboardUtils
import org.cobalt.api.util.TabListUtils
import org.cobalt.mixin.client.TabOverlayAccessor
import java.util.Locale

object HypixelManager {

    data class LocationSnapshot(
        val serverName: String = UNKNOWN,
        val serverType: String = UNKNOWN,
        val lobbyName: String = UNKNOWN,
        val mode: String = UNKNOWN,
        val map: String = UNKNOWN,
        val area: String = UNKNOWN,
    ) {
        val placeName: String
            get() = firstKnown(area, map, mode, serverType)
    }

    private var currentLobby: String? = null

    @Volatile var currentServerName: String = UNKNOWN
        private set
    @Volatile var currentServerType: String = UNKNOWN
        private set
    @Volatile var currentLobbyName: String = UNKNOWN
        private set
    @Volatile var currentMap: String = UNKNOWN
        private set
    @Volatile var currentMode: String = UNKNOWN
        private set
    @Volatile var currentArea: String = UNKNOWN
        private set

    fun snapshot(): LocationSnapshot {
        refreshLiveLocation()
        return LocationSnapshot(
            serverName = currentServerName,
            serverType = currentServerType,
            lobbyName = currentLobbyName,
            mode = currentMode,
            map = currentMap,
            area = currentArea,
        )
    }

    fun currentPlaceName(): String = snapshot().placeName

    fun currentAreaName(): String? {
        refreshLiveLocation()
        return currentArea.takeIf(::isKnown) ?: currentMap.takeIf(::isKnown)
    }

    fun isInArea(area: String): Boolean =
        currentAreaName()?.equals(area, ignoreCase = true) == true

    fun init() {
        val api = HypixelModAPI.getInstance()
        api.subscribeToEventPacket(ClientboundLocationPacket::class.java)
        api.createHandler(ClientboundLocationPacket::class.java) { packet ->
            val serverName = packet.serverName
            val serverType = packet.serverType.map { it.name }.orElse("")
            val lobbyName = packet.lobbyName.orElse("")
            val mode = packet.mode.orElse("")
            val map = packet.map.orElse("")

            updateLocation(serverName, serverType, lobbyName, mode, map)
        }

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            onDisconnect()
        }
    }

    @SubscribeEvent
    fun onChat(event: ChatEvent.Receive) {
        val message = ChatFormatting.stripFormatting(event.message ?: return).orEmpty().trim()
        val values = parseLocationJson(message) ?: return
        event.setCancelled(true)
        updateLocation(
            serverName = values["server"].orEmpty(),
            serverType = values["gametype"].orEmpty(),
            lobbyName = values["lobbyname"].orEmpty(),
            mode = values["mode"].orEmpty(),
            map = values["map"].orEmpty(),
        )
    }

    private fun updateLocation(
        serverName: String,
        serverType: String,
        lobbyName: String,
        mode: String,
        map: String,
    ) {
        currentServerName = serverName.ifBlank { UNKNOWN }
        currentServerType = serverType.ifBlank { UNKNOWN }
        currentLobbyName = lobbyName.ifBlank { UNKNOWN }
        currentMap = cleanPlace(map) ?: UNKNOWN
        currentMode = mode.toDisplayName().ifBlank { UNKNOWN }
        currentArea = detectLiveArea() ?: currentMap.takeIf(::isKnown) ?: UNKNOWN

        val cacheName = cleanPlace(map) ?: currentMap.takeIf(::isKnown) ?: currentMode.takeIf(::isKnown) ?: currentServerType
        val rawId = when {
            cacheName.isNotBlank() && isKnown(cacheName) -> cacheName
            serverType.equals("SKYBLOCK", ignoreCase = true) -> if (mode.isNotBlank()) "skyblock_$mode" else "skyblock_generic"
            lobbyName.isNotBlank() -> "lobby_$lobbyName"
            serverType.isNotBlank() -> "type_$serverType"
            else -> "server_$serverName"
        }

        val newLobby = rawId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")

        if (currentLobby != newLobby) {
            if (currentLobby != null) {
                CachedWorld.saveAndClear(currentLobby!!)
            } else {
                CachedWorld.clear()
            }
            val displayName = firstKnown(currentArea, currentMap, currentMode, currentServerType, rawId)
            CachedWorld.setWorldKey(newLobby, displayName)
            CachedWorld.load(newLobby, notify = false)
            currentLobby = newLobby
            sendWorldChangeMessage(displayName, newLobby)
        }
    }

    private fun onDisconnect() {
        val lobby = currentLobby
        if (lobby != null) {
            CachedWorld.saveAndClear(lobby)
            currentLobby = null
        } else {
            CachedWorld.clear()
        }
        CachedWorld.setWorldKey(null)
        currentServerName = UNKNOWN
        currentServerType = UNKNOWN
        currentLobbyName = UNKNOWN
        currentMap = UNKNOWN
        currentMode = UNKNOWN
        currentArea = UNKNOWN
    }

    private fun refreshLiveLocation() {
        val detected = detectLiveArea()
        if (detected != null) {
            currentArea = detected
        }
    }

    private fun detectLiveArea(): String? {
        val mc = Minecraft.getInstance()
        val lines = collectTabLines(mc) + collectScoreboardLines()
        return detectFromLines(lines)
    }

    private fun collectTabLines(mc: Minecraft): List<String> {
        val lines = mutableListOf<String>()
        val overlay = mc.gui.tabList as? TabOverlayAccessor
        overlay?.header?.string?.let { lines += splitCleanLines(it) }
        overlay?.footer?.string?.let { lines += splitCleanLines(it) }
        lines += TabListUtils.rawDisplayNames().flatMap(::splitCleanLines)
        return lines
    }

    private fun collectScoreboardLines(): List<String> {
        val objective = ScoreboardUtils.sidebarObjective()?.displayName?.string.orEmpty()
        return splitCleanLines(objective) +
            ScoreboardUtils.sidebarComponents()
                .map { it.string }
                .flatMap(::splitCleanLines)
    }

    private fun detectFromLines(lines: List<String>): String? {
        for (line in lines) {
            for (regex in LOCATION_LABELS) {
                regex.find(line)?.let { match ->
                    cleanPlace(match.groupValues[1])?.let { return it }
                }
            }

            val symbolIndex = line.indexOf('\u23E3')
            if (symbolIndex >= 0 && symbolIndex + 1 < line.length) {
                cleanPlace(line.substring(symbolIndex + 1))?.let { return it }
            }
        }
        return null
    }

    private fun splitCleanLines(text: String): List<String> =
        ChatFormatting.stripFormatting(text).orEmpty()
            .replace("\r", "\n")
            .split('\n')
            .map { it.replace(Regex("""\s+"""), " ").trim() }
            .filter { it.isNotEmpty() }

    private fun cleanPlace(raw: String): String? {
        val value = raw
            .replace(Regex("""\s+"""), " ")
            .trim(' ', ':', '-', '|')
            .substringBefore("  ")
            .substringBefore(" (")
            .substringBefore(" | ")
            .substringBefore(" - ")
            .trim()
            .ifBlank { return null }
        if (!isKnown(value)) return null
        if (value.length > 64) return null
        return value
    }

    private fun parseLocationJson(message: String): Map<String, String>? {
        if (!message.startsWith("{") || !message.endsWith("}")) return null
        if (!message.contains("\"server\"") && !message.contains("\"gametype\"")) return null
        val values = LOCATION_JSON_FIELD.findAll(message)
            .associate { it.groupValues[1].lowercase(Locale.ROOT) to it.groupValues[2] }
        return values.takeIf { it.isNotEmpty() }
    }

    private fun sendWorldChangeMessage(displayName: String, cacheKey: String) {
        val parts = mutableListOf<String>()
        currentServerType.takeIf(::isKnown)?.let { parts += it.toDisplayName() }
        currentMode.takeIf { isKnown(it) && !it.equals(displayName, ignoreCase = true) }?.let { parts += it }
        currentServerName.takeIf(::isKnown)?.let { parts += it }

        val detail = parts.joinToString(" / ").takeIf { it.isNotBlank() }
        val suffix = if (detail != null) " ($detail)" else ""
        Minecraft.getInstance().execute {
            ChatUtils.sendMessage("World cache: $displayName$suffix - key $cacheKey")
        }
    }

    private fun String.toDisplayName(): String =
        replace('_', ' ')
            .split(' ')
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.lowercase(Locale.ROOT).replaceFirstChar(Char::uppercaseChar)
            }

    private fun isKnown(value: String): Boolean =
        value.isNotBlank() && !value.equals(UNKNOWN, ignoreCase = true)

    private fun firstKnown(vararg values: String): String =
        values.firstOrNull(::isKnown) ?: UNKNOWN

    private const val UNKNOWN = "Unknown"

    private val LOCATION_LABELS = arrayOf(
        Regex("""(?i)\b(?:area|island|location|map|zone)\s*:\s*(.+)$"""),
        Regex("""(?i)^\s*(?:area|island|location|map|zone)\s+(.+)$"""),
    )
    private val LOCATION_JSON_FIELD = Regex(""""([A-Za-z0-9_]+)"\s*:\s*"([^"]*)"""")
}
