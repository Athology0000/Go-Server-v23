package org.cobalt.internal.skyblock

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import org.cobalt.api.pathfinder.cache.CachedWorld
import java.util.Locale

object HypixelManager {

    private var currentLobby: String? = null

    @Volatile var currentMap: String = "Unknown"
        private set
    @Volatile var currentMode: String = "Unknown"
        private set

    fun currentPlaceName(): String {
        currentMap.takeIf(::isKnown)?.let { return it }

        val detected = detectLivePlace()
        if (detected != null) {
            currentMap = detected
            return detected
        }

        currentMode.takeIf(::isKnown)?.let { return it }
        return "Unknown"
    }

    fun init() {
        val api = HypixelModAPI.getInstance()
        api.subscribeToEventPacket(ClientboundLocationPacket::class.java)
        api.createHandler(ClientboundLocationPacket::class.java) { packet ->
            val serverName = packet.serverName
            val serverType = packet.serverType.map { it.name }.orElse("")
            val lobbyName = packet.lobbyName.orElse("")
            val mode = packet.mode.orElse("")
            val map = packet.map.orElse("")

            currentMap = map.ifEmpty { "Unknown" }
            currentMode = mode.replace('_', ' ')
                .split(' ')
                .joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }
                .ifEmpty { "Unknown" }

            val rawId = when {
                serverType == "SKYBLOCK" -> if (mode.isNotEmpty()) "skyblock_$mode" else "skyblock_generic"
                map.isNotEmpty() -> "map_$map"
                lobbyName.isNotEmpty() -> "lobby_$lobbyName"
                serverType.isNotEmpty() -> "type_$serverType"
                else -> "server_$serverName"
            }

            val newLobby = rawId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")

            if (currentLobby != newLobby) {
                if (currentLobby != null) {
                    CachedWorld.saveAndClear(currentLobby!!)
                } else {
                    CachedWorld.clear()
                }
                CachedWorld.setWorldKey(newLobby)
                CachedWorld.load(newLobby)
                currentLobby = newLobby
            }
        }

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            onDisconnect()
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
        currentMap = "Unknown"
        currentMode = "Unknown"
    }

    private fun detectLivePlace(): String? {
        val mc = Minecraft.getInstance()
        return detectFromLines(collectTabLines(mc))
            ?: detectFromLines(collectScoreboardLines(mc))
    }

    @Suppress("UNUSED_PARAMETER")
    private fun collectTabLines(mc: Minecraft): List<String> =
        org.cobalt.api.util.TabListUtils.rawDisplayNames().flatMap(::splitCleanLines)

    @Suppress("UNUSED_PARAMETER")
    private fun collectScoreboardLines(mc: Minecraft): List<String> =
        org.cobalt.api.util.ScoreboardUtils.sidebarComponents()
            .map { it.string }
            .flatMap(::splitCleanLines)

    private fun detectFromLines(lines: List<String>): String? {
        for (line in lines) {
            val labelMatch = LOCATION_LABEL.find(line)
            if (labelMatch != null) {
                return cleanPlace(labelMatch.groupValues[1])
            }

            val symbolIndex = line.indexOf('⏣')
            if (symbolIndex >= 0 && symbolIndex + 1 < line.length) {
                return cleanPlace(line.substring(symbolIndex + 1))
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
            .ifBlank { return null }
        if (!isKnown(value)) return null
        return value
    }

    private fun isKnown(value: String): Boolean =
        value.isNotBlank() && !value.equals("Unknown", ignoreCase = true)

    private val LOCATION_LABEL = Regex("""(?i)\b(?:area|island|location|map)\s*:\s*(.+)$""")
}
