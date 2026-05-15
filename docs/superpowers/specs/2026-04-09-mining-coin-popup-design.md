# Mining Coin Popup Effect Design

**Date:** 2026-04-09

---

## Summary

When the mining macro is active and a block is mined, a `+X coins` label spawns at the bottom of the screen and floats upward, fading out over ~2 seconds. One popup per block break. Toggled by a setting. Bazaar prices are fetched fresh every client start.

---

## Components

### 1. `MiningProfitTracker` â€” two small changes

**Bazaar fetch on client start:**
Call `refreshBazaarIfNeeded()` at the end of the `init` block. Since `lastBazaarRefresh = 0L` at startup, this always fires a fetch immediately on first client load (not just when a macro session starts).

**Expose prices for popup lookup:**
Add a public accessor so `MiningCoinPopupModule` can read prices without coupling to `MiningProfitTracker`'s internals:
```kotlin
fun getPriceForKey(key: String): Double? = bazaarPrices[key]
```

### 2. `MiningCoinPopupModule` â€” new file

**Location:** `src/main/kotlin/org/phantom/internal/mining/MiningCoinPopupModule.kt`

**Settings:**
- `Enabled` `CheckboxSetting` (default `false` â€” opt-in visual effect)
- `Font Size` `SliderSetting` (default `14.0`, range `12.0â€“20.0`)

**Block-type â†’ bazaar key mapping** (static map in companion/object):

| Block Type | Bazaar key (lowercase, `_`â†’` ` of Hypixel product ID) |
|---|---|
| Mithril (Gray / Dark / Hot) | `mithril ore` |
| Titanium | `titanium ore` |
| Ruby Gemstone | `flawed ruby gem` |
| Amber Gemstone | `flawed amber gem` |
| Amethyst Gemstone | `flawed amethyst gem` |
| Jade Gemstone | `flawed jade gem` |
| Sapphire Gemstone | `flawed sapphire gem` |
| Opal Gemstone | `flawed opal gem` |
| Topaz Gemstone | `flawed topaz gem` |
| Jasper Gemstone | `flawed jasper gem` |
| Onyx Gemstone | `flawed onyx gem` |
| Aquamarine Gemstone | `flawed aquamarine gem` |
| Citrine Gemstone | `flawed citrine gem` |
| Peridot Gemstone | `flawed peridot gem` |
| Umber | `umber` |
| Tungsten | `tungsten` |
| Glacite | `glacite` |

**Popup data class:**
```kotlin
private data class CoinPopup(
    val label: String,       // e.g. "+1.2K"
    val startX: Float,       // fixed X for lifetime
    var y: Float,            // current Y, decreases each frame
    val startMs: Long,       // System.currentTimeMillis() at spawn
)
```
Lifetime: 2000ms. Alpha interpolates linearly from `0xFF` â†’ `0x00` over lifetime.

**`BlockChangeEvent` handler:**
1. Guard: `enabled.value && MiningMacroModule.isActive`
2. `oldBlock` must be in `MiningBlockRegistry.BLOCK_ID_TO_TYPE` (i.e. a known ore), `newBlock` must be air (`minecraft:air`)
3. Look up block type â†’ bazaar key â†’ `MiningProfitTracker.getPriceForKey(key)` â€” skip if null or â‰¤ 0
4. Format value as `+X`, `+X.XK`, or `+X.XM`
5. Spawn `CoinPopup` at:
   - X: `screenWidth / 2 + Random.nextFloat(-60f, 60f)`
   - Y: `screenHeight * 0.8f`
6. Add to `popups` list

**`NvgEvent` handler:**
1. Guard: `enabled.value`
2. Advance each popup: `y -= 40f * deltaSeconds` (40 px/sec upward)
3. Compute alpha: `(1f - elapsed/2000f).coerceIn(0f,1f)` â†’ `(alpha * 0xFF).toInt() shl 24 or 0x4CFF72`
4. Remove expired popups (elapsed â‰¥ 2000ms)
5. Render remaining with `NVGRenderer.text(popup.label, popup.x, popup.y, fontSize, color)`

`deltaSeconds` is derived from tick timing: store `lastNvgMs: Long`, compute `(now - lastNvgMs) / 1000f`, update `lastNvgMs = now`.

**Color:** green `0xFF4CFF72` with alpha applied to the top byte.

### 3. `Phantom.kt`
Add `MiningCoinPopupModule` to the `ModuleManager.addModules(...)` list alongside the other mining modules.

---

## Files Changed

| File | Action |
|------|--------|
| `MiningProfitTracker.kt` | Add `refreshBazaarIfNeeded()` in `init`, add `getPriceForKey()` |
| `MiningCoinPopupModule.kt` | **New** â€” popup spawn + render logic |
| `Phantom.kt` | Register `MiningCoinPopupModule` |
