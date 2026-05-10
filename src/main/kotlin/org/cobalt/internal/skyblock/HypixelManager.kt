package org.cobalt.internal.skyblock

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import org.cobalt.api.pathfinder.cache.CachedWorld

object HypixelManager {

    private var currentLobby: String? = null

    @Volatile var currentMap: String = "Unknown"
        private set
    @Volatile var currentMode: String = "Unknown"
        private set

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
}
