package org.phantom.internal.combat.slayer

import java.util.Locale

internal data class SlayerQuestProgress(
  val typeIndex: Int,
  val tier: Int,
  val progress: Int,
  val target: Int,
)

internal object SlayerQuestSignals {
  val bossTabKeywords = arrayOf("slay the boss")
  val bossClearKeywords = arrayOf("boss slain", "slayer quest complete")
  val maddoxRestartKeywords = arrayOf("restart", "start quest", "begin quest", "start slayer", "slayer quest", "new quest")
  val maddoxConfirmKeywords = arrayOf("confirm", "click to confirm", "accept", "purchase")
  val purchaseFailedKeywords = arrayOf(
    "cannot afford to start this quest",
    "can't afford to start this quest",
    "not enough coins to start this quest",
    "you cannot afford this slayer quest",
  )
  val questReadyChatKeywords = arrayOf(
    "ready to spawn your boss",
    "spawn your boss",
    "right-click to summon",
    "slayer quest is ready",
    "kill a mob to spawn the boss",
    "boss will spawn",
  )
  val bossKilledChatKeywords = arrayOf(
    "you have slain the boss",
    "your slayer quest has been completed",
    "slayer quest complete",
    "boss slain",
  )

  private val progressPattern = Regex("(\\d{1,5})\\s*/\\s*(\\d{1,5})")
  private val genericQuestKeywords = arrayOf(
    "slayer quest",
    "zombie slayer",
    "wolf slayer",
    "spider slayer",
    "enderman slayer",
    "vampire slayer",
    "blaze slayer",
  )

  fun maddoxQuestKeywords(typeIndex: Int): Array<String> =
    when (typeIndex) {
      0 -> arrayOf("revenant horror", "atoned horror", "zombie slayer", "revenant")
      1 -> arrayOf("sven packmaster", "wolf slayer", "sven")
      2 -> arrayOf("tarantula broodfather", "spider slayer", "tarantula")
      3 -> arrayOf("voidgloom seraph", "enderman slayer", "voidgloom")
      4 -> arrayOf("riftstalker bloodfiend", "vampire slayer", "vampire")
      5 -> arrayOf("inferno demonlord", "blaze slayer", "inferno")
      else -> emptyArray()
    }

  fun bossSpawnedChatKeywords(typeIndex: Int): Array<String> =
    when (typeIndex) {
      0 -> arrayOf(
        "revenant horror has spawned",
        "your revenant horror spawned",
        "atoned horror has spawned",
        "your atoned horror spawned",
      )
      1 -> arrayOf("sven packmaster has spawned", "your sven packmaster spawned")
      2 -> arrayOf("tarantula broodfather has spawned")
      3 -> arrayOf("voidgloom seraph has spawned")
      4 -> arrayOf("riftstalker bloodfiend has spawned")
      5 -> arrayOf("inferno demonlord has spawned")
      else -> emptyArray()
    }

  fun detectType(line: String): Int =
    when {
      line.contains("zombie slayer") || line.contains("revenant horror") ||
        line.contains("atoned horror") || (line.contains("revenant") && line.contains("slayer")) -> 0
      line.contains("wolf slayer") || line.contains("sven packmaster") ||
        (line.contains("sven") && line.contains("slayer")) -> 1
      line.contains("spider slayer") || line.contains("tarantula broodfather") ||
        (line.contains("tarantula") && line.contains("slayer")) -> 2
      line.contains("enderman slayer") || line.contains("voidgloom seraph") ||
        (line.contains("voidgloom") && line.contains("slayer")) -> 3
      line.contains("vampire slayer") || line.contains("riftstalker bloodfiend") ||
        (line.contains("riftstalker") && line.contains("slayer")) -> 4
      line.contains("blaze slayer") || line.contains("inferno demonlord") ||
        (line.contains("inferno") && line.contains("slayer")) -> 5
      else -> -1
    }

  fun parseTierFromLine(line: String): Int =
    when {
      line.contains(" v") && !line.contains(" vi") -> 5
      line.contains(" iv") -> 4
      line.contains(" iii") -> 3
      line.contains(" ii") -> 2
      line.contains(" i") -> 1
      else -> -1
    }

  fun parseProgress(
    line: String,
    currentType: Int,
    trackedType: Int,
    trackedTier: Int,
    hasTrackedQuestContext: Boolean,
  ): SlayerQuestProgress? {
    val match = progressPattern.find(line) ?: return null
    val progress = match.groupValues[1].toIntOrNull() ?: return null
    val target = match.groupValues[2].toIntOrNull() ?: return null
    if (target <= 0 || progress !in 0..target) return null

    val detectedType = detectType(line)
    val relevant =
      detectedType >= 0 ||
        line.contains("slayer quest") ||
        genericQuestKeywords.any { keyword -> line.contains(keyword) } ||
        hasTrackedQuestContext
    if (!relevant) return null

    val typeIndex =
      when {
        detectedType >= 0 -> detectedType
        hasTrackedQuestContext -> trackedType
        else -> currentType
      }
    val resolvedTier =
      parseTierFromLine(line).takeIf { it in 1..5 }
        ?: trackedTier.takeIf { hasTrackedQuestContext && it in 1..5 }
        ?: -1
    return SlayerQuestProgress(typeIndex, resolvedTier, progress, target)
  }

  fun parseTierFromMenuText(text: String, typeIndex: Int): Int {
    val normalized = text.lowercase(Locale.ROOT)
    for (questName in maddoxQuestKeywords(typeIndex)) {
      val start = normalized.indexOf(questName)
      if (start < 0) continue
      val suffixTokens =
        normalized.substring(start + questName.length)
          .split(Regex("[^a-z0-9]+"))
          .filter { it.isNotBlank() }
      val tier = suffixTokens.firstOrNull()?.let(::tierFromToken) ?: -1
      if (tier in 1..5) return tier
    }
    return -1
  }

  fun hasActiveQuestSignal(
    line: String,
    currentType: Int,
    trackedType: Int,
    trackedTier: Int,
    hasTrackedQuestContext: Boolean,
  ): Boolean =
    detectType(line) >= 0 ||
      genericQuestKeywords.any { keyword -> line.contains(keyword) } ||
      parseProgress(line, currentType, trackedType, trackedTier, hasTrackedQuestContext) != null

  private fun tierFromToken(token: String): Int =
    when (token) {
      "5", "v" -> 5
      "4", "iv" -> 4
      "3", "iii" -> 3
      "2", "ii" -> 2
      "1", "i" -> 1
      else -> -1
    }
}
