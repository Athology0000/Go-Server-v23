package org.cobalt.internal.qol

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.net.HttpURLConnection
import java.net.URI
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import net.minecraft.ChatFormatting

internal object NeuRecipeService {

  data class RecipeIngredient(
    val id: String,
    val amount: Int,
  )

  data class RecipeDefinition(
    val outputCount: Int,
    val ingredients: List<RecipeIngredient>,
  )

  data class CachedItem(
    val id: String,
    val displayName: String,
    val recipe: RecipeDefinition?,
  )

  private val cache = ConcurrentHashMap<String, CachedItem>()
  private val inFlight = ConcurrentHashMap.newKeySet<String>()
  private val lastAttemptMs = ConcurrentHashMap<String, Long>()
  private val executor = Executors.newSingleThreadExecutor { runnable ->
    Thread(runnable, "Cobalt-NeuRecipeFetch").apply { isDaemon = true }
  }

  fun getCachedItem(id: String): CachedItem? =
    cache[normalizeId(id)]

  fun requestItem(id: String) {
    val normalizedId = normalizeId(id)
    if (normalizedId.isEmpty()) return
    if (cache.containsKey(normalizedId)) return

    val now = System.currentTimeMillis()
    val lastAttempt = lastAttemptMs[normalizedId] ?: 0L
    if (now - lastAttempt < RETRY_DELAY_MS) return
    if (!inFlight.add(normalizedId)) return

    lastAttemptMs[normalizedId] = now
    executor.execute {
      try {
        fetchItem(normalizedId)?.let { item ->
          cache[normalizedId] = item
          item.recipe?.ingredients?.forEach { ingredient ->
            requestItem(ingredient.id)
          }
        }
      } finally {
        inFlight.remove(normalizedId)
      }
    }
  }

  private fun fetchItem(id: String): CachedItem? {
    val connection = (URI("$ITEM_URL_PREFIX$id.json").toURL().openConnection() as HttpURLConnection).apply {
      requestMethod = "GET"
      connectTimeout = 6_000
      readTimeout = 20_000
      setRequestProperty("User-Agent", "Cobalt")
      useCaches = true
    }

    return try {
      when (val code = connection.responseCode) {
        HttpURLConnection.HTTP_NOT_FOUND -> CachedItem(id, prettyId(id), null)
        in 200..299 -> connection.inputStream.bufferedReader(Charsets.UTF_8).use { reader ->
          parseItem(id, JsonParser.parseReader(reader).asJsonObject)
        }

        else -> throw IllegalStateException("Unexpected response code $code for $id")
      }
    } finally {
      connection.disconnect()
    }
  }

  private fun parseItem(requestedId: String, root: JsonObject): CachedItem {
    val internalId = normalizeId(root.get("internalname")?.asString ?: requestedId)
    val displayName = stripFormatting(root.get("displayname")?.asString).ifBlank { prettyId(internalId) }
    val recipeObject = root.getAsJsonObject("recipe")

    if (recipeObject == null) {
      return CachedItem(internalId, displayName, null)
    }

    val outputCount = recipeObject.get("count")?.asInt?.coerceAtLeast(1) ?: 1
    val mergedIngredients = LinkedHashMap<String, Int>()
    for (slot in RECIPE_SLOTS) {
      val rawIngredient = recipeObject.get(slot)?.asString?.trim().orEmpty()
      if (rawIngredient.isBlank()) continue

      val separator = rawIngredient.lastIndexOf(':')
      val ingredientId = normalizeId(
        if (separator >= 0) rawIngredient.substring(0, separator) else rawIngredient
      )
      if (ingredientId.isBlank()) continue

      val amount = if (separator >= 0) {
        rawIngredient.substring(separator + 1).toIntOrNull()?.coerceAtLeast(1) ?: 1
      } else {
        1
      }

      mergedIngredients[ingredientId] = (mergedIngredients[ingredientId] ?: 0) + amount
    }

    val recipe = if (mergedIngredients.isEmpty()) {
      null
    } else {
      RecipeDefinition(
        outputCount = outputCount,
        ingredients = mergedIngredients.map { (id, amount) -> RecipeIngredient(id, amount) },
      )
    }

    return CachedItem(internalId, displayName, recipe)
  }

  internal fun normalizeId(raw: String?): String =
    raw.orEmpty().trim().uppercase(Locale.US)

  internal fun prettyId(id: String): String {
    val cleaned = normalizeId(id)
      .replace('-', ' ')
      .replace('_', ' ')
      .lowercase(Locale.US)
    if (cleaned.isBlank()) return "Unknown Item"

    return cleaned.split(' ')
      .filter { it.isNotBlank() }
      .joinToString(" ") { token ->
        token.replaceFirstChar { char ->
          if (char.isLowerCase()) char.titlecase(Locale.US) else char.toString()
        }
      }
  }

  private fun stripFormatting(raw: String?): String =
    (ChatFormatting.stripFormatting(raw) ?: raw.orEmpty())
      .replace("Â", "")
      .trim()

  private const val ITEM_URL_PREFIX =
    "https://raw.githubusercontent.com/NotEnoughUpdates/NotEnoughUpdates-REPO/master/items/"
  private const val RETRY_DELAY_MS = 30_000L
  private val RECIPE_SLOTS = arrayOf(
    "A1", "A2", "A3",
    "B1", "B2", "B3",
    "C1", "C2", "C3",
  )
}
