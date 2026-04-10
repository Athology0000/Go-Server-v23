package org.cobalt.internal.mining

import com.google.gson.JsonParser
import net.minecraft.client.Minecraft
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.ChatEvent
import java.net.URL

object MiningProfitTracker {

    @Volatile var sessionCoins = 0L
        private set
    @Volatile var sessionStartTime = 0L
        private set

    private val bazaarPrices = mutableMapOf<String, Double>()
    @Volatile private var lastBazaarRefresh = 0L
    private val BAZAAR_REFRESH_INTERVAL_MS = 5 * 60 * 1000L  // 5 minutes

    private val itemPickupRegex = Regex("""\+\s*(\d[\d,]*)\s+(.+)""")

    init {
        EventBus.register(this)
    }

    fun ensureInitialized() { /* touching this object causes the init block to run */ }

    fun resetSession() {
        sessionCoins = 0L
        sessionStartTime = System.currentTimeMillis()
        refreshBazaarIfNeeded()
    }

    fun runtimeMs(): Long = if (sessionStartTime == 0L) 0L
        else (System.currentTimeMillis() - sessionStartTime).coerceAtLeast(0L)

    fun coinsPerHour(): Long {
        val elapsedHours = runtimeMs() / 3_600_000.0
        if (elapsedHours < 0.001) return 0L
        return (sessionCoins / elapsedHours).toLong()
    }

    @SubscribeEvent
    fun onChat(event: ChatEvent.Receive) {
        val message = event.message ?: return
        val m = itemPickupRegex.find(message) ?: return
        val countStr = m.groupValues[1].replace(",", "")
        val count = countStr.toLongOrNull() ?: return
        val item = m.groupValues[2].trim().lowercase()
        val price = bazaarPrices[item] ?: return
        sessionCoins += (count * price).toLong()
    }

    private fun refreshBazaarIfNeeded() {
        val now = System.currentTimeMillis()
        if (now - lastBazaarRefresh < BAZAAR_REFRESH_INTERVAL_MS) return
        lastBazaarRefresh = now
        Thread({
            try {
                val json = URL("https://api.hypixel.net/v2/skyblock/bazaar").readText()
                val root = JsonParser.parseString(json).asJsonObject
                val prods = root.getAsJsonObject("products") ?: return@Thread
                val prices = mutableMapOf<String, Double>()
                for ((name, data) in prods.entrySet()) {
                    val buy = data.asJsonObject
                        .getAsJsonObject("quick_status")
                        ?.get("buyPrice")?.asDouble ?: continue
                    prices[name.lowercase().replace("_", " ")] = buy
                }
                Minecraft.getInstance().execute {
                    bazaarPrices.clear()
                    bazaarPrices.putAll(prices)
                }
            } catch (_: Exception) { /* network failure — keep cached prices */ }
        }, "mining-bazaar-refresh").also { it.isDaemon = true }.start()
    }
}
