package org.phantom.internal.diana

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.protocol.game.ServerboundUseItemPacket
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.ChatEvent
import org.phantom.api.event.impl.client.PacketEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.util.getSkyblockId
import org.phantom.internal.qol.SkyblockPriceService
import java.util.Locale
import kotlin.math.roundToLong

object DianaProfitTracker {
  data class Snapshot(
    val runtimeMs: Long,
    val coins: Long,
    val coinsPerHour: Long,
    val burrows: Int,
    val chains: Int,
    val mobs: Int,
    val rareMobs: Int,
    val drops: List<Pair<String, Int>>,
    val avgDetectMs: Long,
    val avgTravelMs: Long,
    val avgDigMs: Long,
    val avgCombatMs: Long,
    val avgLoopMs: Long,
  )

  private val mc = Minecraft.getInstance()

  @Volatile private var sessionStartMs = 0L
  @Volatile private var sessionCoins = 0L
  @Volatile private var burrows = 0
  @Volatile private var chains = 0
  @Volatile private var mobs = 0
  @Volatile private var rareMobs = 0

  private val drops = linkedMapOf<String, Int>()
  private val timingSamples = linkedMapOf<String, MutableList<Long>>()

  private var current = Cycle()
  private var lastDetectedKey: String? = null
  private var lastPriceRefreshMs = 0L

  init { EventBus.register(this) }

  fun ensureInitialized() {}

  fun reset() {
    sessionStartMs = System.currentTimeMillis()
    sessionCoins = 0L
    burrows = 0
    chains = 0
    mobs = 0
    rareMobs = 0
    drops.clear()
    timingSamples.clear()
    current = Cycle()
    lastDetectedKey = null
    refreshPricesIfNeeded(force = true)
  }

  fun snapshot(): Snapshot {
    ensureStarted()
    val runtime = runtimeMs()
    val cph = if (runtime < 1_000L) 0L else (sessionCoins * 3_600_000.0 / runtime.toDouble()).roundToLong()
    return Snapshot(
      runtimeMs = runtime,
      coins = sessionCoins,
      coinsPerHour = cph,
      burrows = burrows,
      chains = chains,
      mobs = mobs,
      rareMobs = rareMobs,
      drops = drops.entries.map { it.key to it.value },
      avgDetectMs = avg("detect"),
      avgTravelMs = avg("travel"),
      avgDigMs = avg("dig"),
      avgCombatMs = avg("combat"),
      avgLoopMs = avg("loop"),
    )
  }

  fun onMacroTarget(type: DianaParticleTracker.BurrowType) {
    ensureStarted()
    current.targetType = type
    current.targetMs = now()
  }

  fun onMacroArrived() {
    val t = now()
    if (current.arriveMs > 0L) return
    current.arriveMs = t
    current.targetMs.takeIf { it > 0L }?.let { sample("travel", t - it) }
  }

  fun onMacroDug() {
    val t = now()
    if (current.digMs > 0L) return
    current.digMs = t
    current.arriveMs.takeIf { it > 0L }?.let { sample("dig", t - it) }
  }

  fun onMacroMobFound() {
    val t = now()
    if (current.mobFoundMs > 0L) return
    current.mobFoundMs = t
    current.digMs.takeIf { it > 0L }?.let { sample("mobSpawn", t - it) }
  }

  fun onMacroKill() {
    val t = now()
    current.mobFoundMs.takeIf { it > 0L }?.let { sample("combat", t - it) }
    finishLoop(t)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (DianaMacroModule.isActive || DianaHelperModule.enabled.value) {
      ensureStarted()
      refreshPricesIfNeeded()
    }

    val level = mc.level ?: return
    val record = DianaParticleTracker.getBurrowRecord(level) ?: return
    val key = "${record.first.x.toInt()}:${record.first.y.toInt()}:${record.first.z.toInt()}:${record.second}"
    if (key == lastDetectedKey) return
    lastDetectedKey = key
    val t = now()
    current.detectMs = t
    current.spadeMs.takeIf { it > 0L }?.let { sample("detect", t - it) }
  }

  @SubscribeEvent
  fun onPacket(event: PacketEvent.Outgoing) {
    val packet = event.packet as? ServerboundUseItemPacket ?: return
    val player = mc.player ?: return
    if (!isDianaSpade(player.getItemInHand(packet.hand))) return
    ensureStarted()
    current = Cycle(spadeMs = now())
  }

  @SubscribeEvent
  fun onChat(event: ChatEvent.Receive) {
    val message = ChatFormatting.stripFormatting(event.message ?: return)?.trim().orEmpty()
    if (message.isEmpty()) return

    MYTHOLOGICAL_SPAWN_PATTERN.find(message)?.let { match ->
      mobs++
      val creature = normalizeName(match.groupValues[1])
      if (RARE_MOBS.any { creature.contains(it, ignoreCase = true) }) rareMobs++
      return
    }

    BURROW_DUG_PATTERN.find(message)?.let { match ->
      burrows++
      val currentCount = match.groupValues[1].toIntOrNull() ?: 0
      val maxCount = match.groupValues[2].toIntOrNull() ?: 0
      if (currentCount > 0 && currentCount == maxCount) chains++
      onMacroDug()
      finishLoop(now())
      return
    }

    COINS_PATTERN.find(message)?.let { match ->
      addCoins(parseCoins(match.groupValues[1]))
      return
    }

    DROP_PATTERN.find(message)?.let { match ->
      val name = normalizeName(match.groupValues[1])
      if (name.contains("coins", ignoreCase = true)) {
        COINS_IN_TEXT_PATTERN.find(name)?.let { addCoins(parseCoins(it.groupValues[1])) }
      } else {
        addDrop(name)
      }
    }
  }

  private fun addDrop(name: String) {
    drops[name] = (drops[name] ?: 0) + 1
    val id = DROP_IDS[name.lowercase(Locale.US)]
    val value = id?.let(::priceFor) ?: FALLBACK_DROP_PRICES[name.lowercase(Locale.US)] ?: 0.0
    if (value > 0.0) addCoins(value.roundToLong())
  }

  private fun priceFor(id: String): Double? {
    SkyblockPriceService.getBazaarQuote(id)?.let { quote ->
      if (quote.sellPrice > 0.0) return quote.sellPrice
      quote.buyPrice?.takeIf { it > 0.0 }?.let { return it }
    }
    SkyblockPriceService.getLowestBin(id)?.let { if (it > 0.0) return it }
    SkyblockPriceService.getNpcSellPrice(id)?.let { if (it > 0.0) return it }
    return null
  }

  private fun addCoins(amount: Long) {
    ensureStarted()
    if (amount > 0L) sessionCoins += amount
  }

  private fun finishLoop(t: Long) {
    if (current.finished) return
    current.finished = true
    current.spadeMs.takeIf { it > 0L }?.let { sample("loop", t - it) }
    current = Cycle()
  }

  private fun sample(name: String, durationMs: Long) {
    if (durationMs <= 0L || durationMs > 10 * 60_000L) return
    val list = timingSamples.getOrPut(name) { ArrayList() }
    list += durationMs
    if (list.size > 80) list.removeAt(0)
  }

  private fun avg(name: String): Long {
    val list = timingSamples[name].orEmpty()
    if (list.isEmpty()) return 0L
    return (list.sum().toDouble() / list.size.toDouble()).roundToLong()
  }

  private fun ensureStarted() {
    if (sessionStartMs == 0L) reset()
  }

  private fun runtimeMs(): Long {
    val start = sessionStartMs
    return if (start == 0L) 0L else (now() - start).coerceAtLeast(0L)
  }

  private fun refreshPricesIfNeeded(force: Boolean = false) {
    val t = now()
    if (!force && t - lastPriceRefreshMs < 120_000L) return
    lastPriceRefreshMs = t
    SkyblockPriceService.refreshIfNeeded(300L)
  }

  private fun isDianaSpade(stack: net.minecraft.world.item.ItemStack): Boolean {
    if (stack.isEmpty) return false
    val id = stack.getSkyblockId().uppercase(Locale.US)
    return id in DIANA_SPADE_IDS || ChatFormatting.stripFormatting(stack.hoverName.string)
      ?.contains("spade", ignoreCase = true) == true
  }

  private fun normalizeName(raw: String): String =
    raw.replace(Regex("""\s+"""), " ").trim().removePrefix("a ").trim()

  private fun parseCoins(raw: String): Long =
    raw.replace(",", "").replace(".", "").trim().toLongOrNull() ?: 0L

  private fun now(): Long = System.currentTimeMillis()

  private data class Cycle(
    var spadeMs: Long = 0L,
    var detectMs: Long = 0L,
    var targetMs: Long = 0L,
    var arriveMs: Long = 0L,
    var digMs: Long = 0L,
    var mobFoundMs: Long = 0L,
    var targetType: DianaParticleTracker.BurrowType = DianaParticleTracker.BurrowType.UNKNOWN,
    var finished: Boolean = false,
  )

  private val MYTHOLOGICAL_SPAWN_PATTERN = Regex(
    """^(?:Oh|Uh oh|Yikes|Oi|Good Grief|Danger|Woah)! You dug out (?:a )?(.+)!$""",
    RegexOption.IGNORE_CASE
  )
  private val BURROW_DUG_PATTERN = Regex("""^You (?:dug out a Griffin Burrow!|finished the Griffin burrow chain!) \((\d+)/(\d+)\)""")
  private val COINS_PATTERN = Regex("""You dug out (?:a )?([\d,.]+) coins?!""", RegexOption.IGNORE_CASE)
  private val COINS_IN_TEXT_PATTERN = Regex("""([\d,.]+)\s*coins?""", RegexOption.IGNORE_CASE)
  private val DROP_PATTERN = Regex("""^(?:RARE DROP!|Wow!) You dug out(?: a)? (.+)!$""", RegexOption.IGNORE_CASE)

  private val DIANA_SPADE_IDS = setOf("ANCESTRAL_SPADE", "ANCESTRAL_SPADE_2", "DEIFIC_SPADE", "DWARVEN_METAL_DETECTOR")
  private val RARE_MOBS = setOf("Minos Inquisitor", "Sphinx", "King Minos", "Manticore")
  private val DROP_IDS = mapOf(
    "griffin feather" to "GRIFFIN_FEATHER",
    "crown of greed" to "CROWN_OF_GREED",
    "washed-up souvenir" to "WASHED_UP_SOUVENIR",
    "minos relic" to "MINOS_RELIC",
    "antique remedies" to "ANTIQUE_REMEDIES",
    "crochet tiger plushie" to "CROCHET_TIGER_PLUSHIE",
    "dwarf turtle shelmet" to "DWARF_TURTLE_SHELMET",
    "daedalus stick" to "DAEDALUS_STICK",
    "chimera i" to "ENCHANTMENT_CHIMERA_1",
    "chimera 1" to "ENCHANTMENT_CHIMERA_1",
    "mythos fragment" to "MYTHOS_FRAGMENT",
  )
  private val FALLBACK_DROP_PRICES = mapOf(
    "griffin feather" to 50_000.0,
    "mythos fragment" to 35_000.0,
  )
}
