package org.cobalt.internal.combat.slayer

internal enum class SlayerKind(
  val typeIndex: Int,
  val displayName: String,
) {
  ZOMBIE(0, "Zombie"),
  WOLF(1, "Wolf"),
  SPIDER(2, "Spider"),
  ENDERMAN(3, "Enderman"),
  VAMPIRE(4, "Vampire"),
  BLAZE(5, "Blaze");

  companion object {
    fun fromTypeIndex(typeIndex: Int): SlayerKind? =
      entries.firstOrNull { it.typeIndex == typeIndex }
  }
}

internal enum class SlayerPhaseKind(
  val displayName: String,
  val description: String,
) {
  SPAWN_FARMING("Spawn Farming", "Killing normal mobs to spawn the Slayer boss."),
  BOSS_DAMAGE("Boss Damage", "Normal boss damage phase."),
  SPIDER_HATCHLINGS("Hatchlings", "Tarantula boss invulnerability phase with hatchling targets."),
  WOLF_PUPS("Pups", "Sven boss pup phase."),
  ENDERMAN_HIT_SHIELD("Hit Shield", "Voidgloom hit shield phase."),
  ENDERMAN_LASERS("Lasers", "Voidgloom laser dodge phase."),
  ENDERMAN_BEACONS("Beacons", "Voidgloom beacon-clearing phase."),
  BLAZE_ATTUNEMENT("Attunement", "Inferno Demonlord attunement phase."),
}

internal data class SlayerDefinition(
  val kind: SlayerKind,
  val bossKeywords: Array<String>,
  val priorityKeywordsByMinTier: Map<Int, Array<String>> = emptyMap(),
  val highTierPriorityKeywordsByMinTier: Map<Int, Array<String>> = emptyMap(),
  val farmKeywordsForLocation: (Int) -> Array<String>,
  val phases: Set<SlayerPhaseKind> = setOf(SlayerPhaseKind.SPAWN_FARMING, SlayerPhaseKind.BOSS_DAMAGE),
  val bossDisplayNameForTier: (Int) -> String = { bossKeywords.firstOrNull()?.titleCaseWords() ?: "${kind.displayName} Boss" },
) {
  fun priorityKeywords(tier: Int): Array<String> =
    keywordsForTier(tier, priorityKeywordsByMinTier)

  fun highTierPriorityKeywords(tier: Int): Array<String> =
    keywordsForTier(tier, highTierPriorityKeywordsByMinTier)

  fun farmKeywords(locationIndex: Int): Array<String> =
    farmKeywordsForLocation(locationIndex)

  fun bossDisplayName(tier: Int): String =
    bossDisplayNameForTier(tier)

  private fun keywordsForTier(tier: Int, tiers: Map<Int, Array<String>>): Array<String> =
    tiers
      .filterKeys { tier >= it }
      .maxByOrNull { it.key }
      ?.value
      ?: emptyArray()
}

internal object SlayerDefinitions {
  const val ENDERMAN_END_FARM_MOB_KEYWORD = "enderman"
  const val ENDERMAN_HIDEOUT_FARM_MOB_KEYWORD = "zealot bruiser"
  const val ENDERMAN_VOID_FARM_MOB_KEYWORD = "voidling"

  val all: List<SlayerDefinition> = listOf(
    SlayerDefinition(
      kind = SlayerKind.ZOMBIE,
      bossKeywords = arrayOf("revenant horror", "atoned horror"),
      priorityKeywordsByMinTier = mapOf(
        3 to arrayOf("revenant sycophant", "revenant champion", "deformed revenant", "atoned champion", "atoned revenant")
      ),
      highTierPriorityKeywordsByMinTier = mapOf(
        3 to arrayOf("revenant champion", "deformed revenant", "atoned champion", "atoned revenant")
      ),
      farmKeywordsForLocation = { location ->
        when (location) {
          0 -> arrayOf("zombie")
          1 -> arrayOf("crypt ghoul", "golden ghoul")
          else -> arrayOf("zombie", "crypt ghoul", "golden ghoul")
        }
      },
      bossDisplayNameForTier = { tier -> if (tier >= 5) "Atoned Horror" else "Revenant Horror" },
    ),
    SlayerDefinition(
      kind = SlayerKind.WOLF,
      bossKeywords = arrayOf("sven packmaster"),
      priorityKeywordsByMinTier = mapOf(3 to arrayOf("pack enforcer", "sven follower", "sven alpha")),
      highTierPriorityKeywordsByMinTier = mapOf(3 to arrayOf("sven alpha")),
      farmKeywordsForLocation = { arrayOf("pack wolf", "old wolf", "pit wolf", "zombie wolf", "wolf", "pup", "pups") },
      phases = setOf(SlayerPhaseKind.SPAWN_FARMING, SlayerPhaseKind.BOSS_DAMAGE, SlayerPhaseKind.WOLF_PUPS),
    ),
    SlayerDefinition(
      kind = SlayerKind.SPIDER,
      bossKeywords = arrayOf("tarantula broodfather", "conjoined brood"),
      priorityKeywordsByMinTier = mapOf(
        3 to arrayOf("tarantula vermin", "tarantula beast", "mutant tarantula", "primordial jockey", "primordial viscount")
      ),
      highTierPriorityKeywordsByMinTier = mapOf(
        3 to arrayOf("mutant tarantula", "primordial jockey", "primordial viscount")
      ),
      farmKeywordsForLocation = { arrayOf("dasher spider", "voracious spider", "weaver spider", "spider", "hatchling", "hatchlings", "broodling") },
      phases = setOf(SlayerPhaseKind.SPAWN_FARMING, SlayerPhaseKind.BOSS_DAMAGE, SlayerPhaseKind.SPIDER_HATCHLINGS),
    ),
    SlayerDefinition(
      kind = SlayerKind.ENDERMAN,
      bossKeywords = arrayOf("voidgloom seraph"),
      priorityKeywordsByMinTier = mapOf(3 to arrayOf("voidling devotee", "voidling radical", "voidcrazed maniac")),
      highTierPriorityKeywordsByMinTier = mapOf(3 to arrayOf("voidling radical", "voidcrazed maniac")),
      farmKeywordsForLocation = { location ->
        when (location) {
          0 -> arrayOf(ENDERMAN_END_FARM_MOB_KEYWORD)
          1 -> arrayOf(ENDERMAN_HIDEOUT_FARM_MOB_KEYWORD)
          2 -> arrayOf(ENDERMAN_VOID_FARM_MOB_KEYWORD)
          else -> arrayOf(ENDERMAN_END_FARM_MOB_KEYWORD)
        }
      },
      phases = setOf(
        SlayerPhaseKind.SPAWN_FARMING,
        SlayerPhaseKind.BOSS_DAMAGE,
        SlayerPhaseKind.ENDERMAN_HIT_SHIELD,
        SlayerPhaseKind.ENDERMAN_LASERS,
        SlayerPhaseKind.ENDERMAN_BEACONS,
      ),
    ),
    SlayerDefinition(
      kind = SlayerKind.VAMPIRE,
      bossKeywords = arrayOf("riftstalker bloodfiend"),
      farmKeywordsForLocation = { arrayOf("bat", "vampiric bat", "bloodfiend") },
    ),
    SlayerDefinition(
      kind = SlayerKind.BLAZE,
      bossKeywords = arrayOf("inferno demonlord"),
      priorityKeywordsByMinTier = mapOf(3 to arrayOf("flare demon", "kindleheart demon", "burningsoul demon")),
      highTierPriorityKeywordsByMinTier = mapOf(3 to arrayOf("kindleheart demon", "burningsoul demon")),
      farmKeywordsForLocation = { arrayOf("blaze", "smoldering blaze", "emerald slime") },
      phases = setOf(SlayerPhaseKind.SPAWN_FARMING, SlayerPhaseKind.BOSS_DAMAGE, SlayerPhaseKind.BLAZE_ATTUNEMENT),
    ),
  )

  fun forType(typeIndex: Int): SlayerDefinition? =
    all.firstOrNull { it.kind.typeIndex == typeIndex }
}

private fun String.titleCaseWords(): String =
  split(' ')
    .filter { it.isNotBlank() }
    .joinToString(" ") { word ->
      word.replaceFirstChar { ch -> ch.uppercaseChar() }
    }
