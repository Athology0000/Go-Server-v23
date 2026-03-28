# Wardrobe GUI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the vanilla Hypixel SkyBlock wardrobe screen with a custom NVG overlay that renders 3D player previews, supports 3 configurable custom pages (TextSettings), and auto-labels sets by reforge (Mossy→Farming, Mantid→Pest, Jaded→Mining).

**Architecture:** A `WardrobeModule` (Kotlin object) intercepts the wardrobe `AbstractContainerScreen` via a Mixin that cancels vanilla rendering, parses all 27 sets from inventory packets (auto-scanning all 3 vanilla pages on open), then renders a full custom overlay using NVG for panels/text and `InventoryScreen.renderEntityInInventory()` for 3D player previews.

**Tech Stack:** Kotlin, NanoVG (NVGRenderer), Minecraft 1.21.11 GuiGraphics, Fabric Mixin, Cobalt event system (PacketEvent, GuiRenderEvent, MouseEvent).

---

## File Map

| File | Action | Purpose |
|---|---|---|
| `src/main/kotlin/org/cobalt/internal/wardrobe/WardrobeState.kt` | Create | Data model: WardrobeSet, WardrobeState singleton, auto-naming |
| `src/main/kotlin/org/cobalt/internal/wardrobe/WardrobePageConfig.kt` | Create | Parse TextSetting CSV → custom page assignments |
| `src/main/kotlin/org/cobalt/internal/wardrobe/WardrobeFakePlayer.kt` | Create | Fake AbstractClientPlayer for entity rendering |
| `src/main/kotlin/org/cobalt/internal/wardrobe/WardrobeRenderer.kt` | Create | All NVG + GuiGraphics drawing logic, builds hitbox lists |
| `src/main/kotlin/org/cobalt/internal/wardrobe/WardrobeModule.kt` | Create | Module, settings, packet parsing, click handling |
| `src/main/java/org/cobalt/mixin/client/WardrobeScreenMixin.java` | Create | Cancel vanilla AbstractContainerScreen render when overlay active |
| `src/main/kotlin/org/cobalt/Cobalt.kt` | Modify | Register WardrobeModule in ModuleManager |

---

### Task 1: Data model — WardrobeState

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/wardrobe/WardrobeState.kt`

- [ ] **Step 1: Create WardrobeState.kt**

```kotlin
package org.cobalt.internal.wardrobe

import net.minecraft.world.item.ItemStack

data class WardrobeSet(
    val id: Int,                        // 1–27 (global slot id across all 3 vanilla pages)
    val vanillaPage: Int,               // 1–3
    val inventorySlot: Int,             // slot index (36–44) in the open container to click for equip
    val armor: List<ItemStack?>,        // [helmet, chestplate, leggings, boots], null = empty slot
    val locked: Boolean,
) {
    fun isEmpty() = armor.all { it == null || it.isEmpty }
}

object WardrobeState {
    var isOpen = false
    var currentVanillaPage: Int? = null
    var equippedSlotId: Int? = null
    val sets: MutableList<WardrobeSet> = MutableList(27) { i ->
        WardrobeSet(
            id = i + 1,
            vanillaPage = i / 9 + 1,
            inventorySlot = 36 + i % 9,
            armor = listOf(null, null, null, null),
            locked = false,
        )
    }
    val favorites: MutableSet<Int> = mutableSetOf()

    fun reset() {
        isOpen = false
        currentVanillaPage = null
        equippedSlotId = null
        for (i in sets.indices) {
            sets[i] = sets[i].copy(armor = listOf(null, null, null, null), locked = false)
        }
    }

    fun updatePage(
        page: Int,
        armorBySetId: Map<Int, List<ItemStack?>>,
        equippedId: Int?,
        lockedIds: Set<Int>,
    ) {
        val startId = (page - 1) * 9 + 1
        for (id in startId until startId + 9) {
            val idx = id - 1
            sets[idx] = sets[idx].copy(
                armor = armorBySetId[id] ?: listOf(null, null, null, null),
                locked = id in lockedIds,
            )
        }
        if (equippedId != null) equippedSlotId = equippedId
    }
}

// Auto-naming: first non-null armor piece's reforge prefix → friendly label
private val REFORGE_LABELS = mapOf("Mossy" to "Farming", "Mantid" to "Pest", "Jaded" to "Mining")

fun WardrobeSet.displayName(): String {
    val reforge = armor.filterNotNull().firstOrNull { !it.isEmpty }
        ?.hoverName?.string
        ?.replace(Regex("\u00A7[0-9a-fk-or]"), "")
        ?.trim()?.split(" ")?.firstOrNull()
    return REFORGE_LABELS[reforge] ?: "Set $id"
}
```

- [ ] **Step 2: Verify build**

```bash
./gradlew build
```
Expected: BUILD SUCCESSFUL (or only pre-existing errors — none from the new file).

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/wardrobe/WardrobeState.kt
git commit -m "feat: add WardrobeState data model and auto-naming"
```

---

### Task 2: Page config parser

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/wardrobe/WardrobePageConfig.kt`

- [ ] **Step 1: Create WardrobePageConfig.kt**

```kotlin
package org.cobalt.internal.wardrobe

object WardrobePageConfig {

    /** Parse a CSV string of slot IDs (1–27) into a set of valid IDs. */
    fun parseSlots(text: String): Set<Int> =
        text.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it in 1..27 }
            .toSet()

    /**
     * Returns a map of setId (1–27) → custom page number (1, 2, or 3).
     * Sets not claimed by page 1 or 2 auto-assign to page 3.
     */
    fun resolvePages(page1Text: String, page2Text: String): Map<Int, Int> {
        val page1 = parseSlots(page1Text)
        val page2 = parseSlots(page2Text)
        return (1..27).associateWith { id ->
            when (id) {
                in page1 -> 1
                in page2 -> 2
                else     -> 3
            }
        }
    }
}
```

- [ ] **Step 2: Verify build**

```bash
./gradlew build
```

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/wardrobe/WardrobePageConfig.kt
git commit -m "feat: add WardrobePageConfig CSV parser"
```

---

### Task 3: Fake player for 3D renders

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/wardrobe/WardrobeFakePlayer.kt`

- [ ] **Step 1: Create WardrobeFakePlayer.kt**

```kotlin
package org.cobalt.internal.wardrobe

import com.mojang.authlib.GameProfile
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemStack
import java.util.UUID

/**
 * A fake AbstractClientPlayer used only for InventoryScreen.renderEntityInInventory().
 * Never added to the world — purely for rendering armor previews.
 */
class WardrobeFakePlayer(level: ClientLevel) : AbstractClientPlayer(
    level,
    GameProfile(UUID.randomUUID(), "WardrobePreview"),
) {
    fun applyArmor(armor: List<ItemStack?>) {
        setItemSlot(EquipmentSlot.HEAD,  armor.getOrNull(0)?.takeIf { !it.isEmpty } ?: ItemStack.EMPTY)
        setItemSlot(EquipmentSlot.CHEST, armor.getOrNull(1)?.takeIf { !it.isEmpty } ?: ItemStack.EMPTY)
        setItemSlot(EquipmentSlot.LEGS,  armor.getOrNull(2)?.takeIf { !it.isEmpty } ?: ItemStack.EMPTY)
        setItemSlot(EquipmentSlot.FEET,  armor.getOrNull(3)?.takeIf { !it.isEmpty } ?: ItemStack.EMPTY)
    }
}

/**
 * Per-set cache of WardrobeFakePlayer instances. Recreated only when the armor changes.
 * Key = setId; value = player + fingerprint of last applied armor item IDs.
 */
object WardrobeFakePlayerCache {
    private data class Entry(val player: WardrobeFakePlayer, val fingerprint: String)
    private val cache = mutableMapOf<Int, Entry>()

    fun get(setId: Int, armor: List<ItemStack?>, level: ClientLevel): WardrobeFakePlayer {
        val fp = armor.joinToString("|") { it?.item?.toString() ?: "null" }
        val entry = cache[setId]
        if (entry != null && entry.fingerprint == fp) return entry.player
        val player = WardrobeFakePlayer(level)
        player.applyArmor(armor)
        cache[setId] = Entry(player, fp)
        return player
    }

    fun clear() = cache.clear()
}
```

- [ ] **Step 2: Verify build**

```bash
./gradlew build
```
If `AbstractClientPlayer` has compilation issues (e.g. abstract members requiring override), check in your IDE which members need implementing and add stub overrides returning default values.

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/wardrobe/WardrobeFakePlayer.kt
git commit -m "feat: add WardrobeFakePlayer and cache for armor previews"
```

---

### Task 4: Mixin — suppress vanilla container render

**Files:**
- Create: `src/main/java/org/cobalt/mixin/client/WardrobeScreenMixin.java`

No mixin JSON edit needed — `MixinAutoDiscover` scans the `org.cobalt.mixin` package automatically.

- [ ] **Step 1: Create WardrobeScreenMixin.java**

```java
package org.cobalt.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.cobalt.internal.wardrobe.WardrobeModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class WardrobeScreenMixin<T extends AbstractContainerMenu> {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cobalt$suppressWardrobeRender(
            GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (WardrobeModule.INSTANCE.shouldSuppressVanillaRender()) {
            ci.cancel();
        }
    }
}
```

- [ ] **Step 2: Verify build**

```bash
./gradlew build
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/org/cobalt/mixin/client/WardrobeScreenMixin.java
git commit -m "feat: add WardrobeScreenMixin to suppress vanilla wardrobe render"
```

---

### Task 5: WardrobeRenderer

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/wardrobe/WardrobeRenderer.kt`

The renderer uses **three passes** to interleave NVG and GuiGraphics:
1. NVG pass — panel background, slot card backgrounds, page tabs, buttons
2. GuiGraphics pass — `InventoryScreen.renderEntityInInventory()` for each slot
3. NVG pass — text overlays (set names, equipped badge, favorite heart)
4. GuiGraphics pass — armor tooltip on hover

All layout is in **screen pixels** (matching `NVGRenderer.beginFrame`). Convert to GUI coords (`/ guiScale`) when calling GuiGraphics APIs.

- [ ] **Step 1: Create WardrobeRenderer.kt**

```kotlin
package org.cobalt.internal.wardrobe

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.util.ui.NVGRenderer
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.atan

object WardrobeRenderer {

    // Layout constants in screen pixels
    private const val SLOT_W       = 66f
    private const val SLOT_H       = 120f
    private const val SLOT_GAP     = 8f
    private const val MAX_COLS     = 9
    private const val PADDING      = 16f
    private const val TAB_H        = 26f
    private const val TAB_W        = 72f
    private const val TAB_GAP      = 6f
    private const val BTN_H        = 26f
    private const val BTN_W        = 80f
    private const val BTN_GAP      = 8f
    private const val PLAYER_SCALE = 40
    private const val SECTION_GAP  = 8f

    private data class Rect(val x: Float, val y: Float, val w: Float, val h: Float) {
        fun contains(px: Float, py: Float) = px in x..(x + w) && py in y..(y + h)
    }

    fun render(graphics: GuiGraphics, module: WardrobeModule) {
        val mc = Minecraft.getInstance()
        val level = mc.level ?: return
        val guiScale = mc.window.guiScale.toFloat()
        val sw = mc.window.screenWidth.toFloat()
        val sh = mc.window.screenHeight.toFloat()
        val mx = mc.mouseHandler.xpos().toFloat()
        val my = mc.mouseHandler.ypos().toFloat()

        val sets = module.setsOnCurrentCustomPage()
        val cols = sets.size.coerceAtMost(MAX_COLS)
        val rows = if (sets.isEmpty()) 1 else (sets.size + MAX_COLS - 1) / MAX_COLS

        val gridW = cols * SLOT_W + (cols - 1).coerceAtLeast(0) * SLOT_GAP
        val tabsW = 3 * TAB_W + 2 * TAB_GAP
        val btnsW = 2 * BTN_W + BTN_GAP
        val contentW = maxOf(gridW, tabsW, btnsW) + PADDING * 2
        val contentH = PADDING + TAB_H + SECTION_GAP +
            rows * SLOT_H + (rows - 1).coerceAtLeast(0) * SLOT_GAP +
            SECTION_GAP + BTN_H + PADDING

        val ox = (sw - contentW) / 2f
        val oy = (sh - contentH) / 2f

        // Compute slot rects (screen pixels)
        val slotRects = sets.mapIndexed { i, set ->
            val col = i % MAX_COLS
            val row = i / MAX_COLS
            val x = ox + PADDING + col * (SLOT_W + SLOT_GAP)
            val y = oy + PADDING + TAB_H + SECTION_GAP + row * (SLOT_H + SLOT_GAP)
            set.id to Rect(x, y, SLOT_W, SLOT_H)
        }.toMap()

        val hoveredId = slotRects.entries.firstOrNull { (_, r) -> r.contains(mx, my) }?.key

        // ── Pass 1: NVG backgrounds ───────────────────────────────────────────
        NVGRenderer.beginFrame(sw, sh)
        val theme = ThemeManager.currentTheme

        // Panel
        NVGRenderer.rect(ox, oy, contentW, contentH, theme.panel, 12f)

        // Page tabs
        val newTabHitboxes = mutableListOf<WardrobeModule.TabHitbox>()
        for (p in 1..3) {
            val tx = ox + PADDING + (p - 1) * (TAB_W + TAB_GAP)
            val ty = oy + PADDING
            val selected = p == module.currentCustomPage
            val bg = if (selected) theme.selectedOverlay else theme.controlBg
            val border = if (selected) theme.accent else theme.controlBorder
            NVGRenderer.rect(tx, ty, TAB_W, TAB_H, bg, 6f)
            NVGRenderer.hollowRect(tx, ty, TAB_W, TAB_H, 1f, border, 6f)
            NVGRenderer.text("Page $p", tx + TAB_W / 2f, ty + TAB_H / 2f - 5f, 11f,
                if (selected) theme.accent else theme.text)
            newTabHitboxes.add(WardrobeModule.TabHitbox(p, tx, ty, TAB_W, TAB_H))
        }
        module.tabHitboxes = newTabHitboxes

        // Slot card backgrounds
        val newSlotHitboxes = mutableListOf<WardrobeModule.SlotHitbox>()
        slotRects.forEach { (id, r) ->
            val isEquipped = WardrobeState.equippedSlotId == id
            val isFav = WardrobeState.favorites.contains(id)
            val isHovered = hoveredId == id
            val cardBg = when {
                isEquipped -> theme.selectedOverlay
                isHovered  -> theme.overlay
                else       -> theme.controlBg
            }
            val border = when {
                isEquipped -> theme.accent
                isFav      -> 0xFFFFD700.toInt()
                else       -> theme.controlBorder
            }
            NVGRenderer.rect(r.x, r.y, r.w, r.h, cardBg, 8f)
            NVGRenderer.hollowRect(r.x, r.y, r.w, r.h, 1.5f, border, 8f)
            newSlotHitboxes.add(WardrobeModule.SlotHitbox(id, r.x, r.y, r.w, r.h))
        }
        module.slotHitboxes = newSlotHitboxes

        // Back / Close buttons
        val btnY = oy + contentH - PADDING - BTN_H
        val btnX = ox + PADDING
        NVGRenderer.rect(btnX, btnY, BTN_W, BTN_H, theme.controlBg, 6f)
        NVGRenderer.hollowRect(btnX, btnY, BTN_W, BTN_H, 1f, theme.controlBorder, 6f)
        NVGRenderer.text("◀ Back", btnX + BTN_W / 2f, btnY + BTN_H / 2f - 5f, 11f, theme.text)
        val closeBtnX = btnX + BTN_W + BTN_GAP
        NVGRenderer.rect(closeBtnX, btnY, BTN_W, BTN_H, theme.controlBg, 6f)
        NVGRenderer.hollowRect(closeBtnX, btnY, BTN_W, BTN_H, 1f, theme.controlBorder, 6f)
        NVGRenderer.text("✕ Close", closeBtnX + BTN_W / 2f, btnY + BTN_H / 2f - 5f, 11f, 0xFFFF6666.toInt())
        module.buttonHitboxes = listOf(
            WardrobeModule.ButtonHitbox(WardrobeModule.ButtonType.BACK,  btnX,      btnY, BTN_W, BTN_H),
            WardrobeModule.ButtonHitbox(WardrobeModule.ButtonType.CLOSE, closeBtnX, btnY, BTN_W, BTN_H),
        )

        NVGRenderer.endFrame()

        // ── Pass 2: GuiGraphics — fake player renders ─────────────────────────
        slotRects.forEach { (id, r) ->
            val set = WardrobeState.sets[id - 1]
            if (set.isEmpty()) return@forEach
            val fp = WardrobeFakePlayerCache.get(id, set.armor, level)

            // InventoryScreen.renderEntityInInventory coords are in GUI units
            val gx1 = r.x / guiScale
            val gy1 = r.y / guiScale
            val gx2 = (r.x + r.w) / guiScale
            val gy2 = (r.y + r.h) / guiScale
            val gcx = (gx1 + gx2) / 2f
            val gcy = (gy1 + gy2) / 2f

            val mouseOffX = (mx / guiScale - gcx).toFloat()
            val mouseOffY = (my / guiScale - gcy - SLOT_H / guiScale * 0.3f).toFloat()
            val rotY = atan(mouseOffX / 40.0).toFloat()
            val rotX = atan(mouseOffY / 40.0).toFloat()

            val rotate = Quaternionf().rotateZ(Math.PI.toFloat())
                .rotateY(rotY)
            val toRotate = Quaternionf().rotateX(rotX)

            try {
                InventoryScreen.renderEntityInInventory(
                    graphics,
                    gx1, gy1, gx2, gy2,
                    PLAYER_SCALE,
                    Vector3f(0f, fp.eyeHeight / 2f, 0f),
                    rotate, toRotate,
                    fp,
                )
            } catch (_: Exception) {
                // Guard against rendering errors on entities not fully initialized
            }
        }

        // ── Pass 3: NVG text overlays ─────────────────────────────────────────
        NVGRenderer.beginFrame(sw, sh)
        slotRects.forEach { (id, r) ->
            val set = WardrobeState.sets[id - 1]

            // Set label
            NVGRenderer.text(
                set.displayName(),
                r.x + r.w / 2f, r.y + r.h - 18f,
                10f, ThemeManager.currentTheme.text,
            )

            // Equipped badge
            if (WardrobeState.equippedSlotId == id) {
                val bw = 52f; val bh = 14f
                val bx = r.x + (r.w - bw) / 2f; val by = r.y + 4f
                NVGRenderer.rect(bx, by, bw, bh, ThemeManager.currentTheme.accent, 4f)
                NVGRenderer.text("Equipped", bx + bw / 2f, by + 1f, 9f, 0xFFFFFFFF.toInt())
            }

            // Favorite heart
            val heartColor = if (WardrobeState.favorites.contains(id)) 0xFFFF4444.toInt() else 0xFF555555.toInt()
            NVGRenderer.text("♥", r.x + r.w - 14f, r.y + 6f, 13f, heartColor)
        }

        // If empty page, show hint
        if (sets.isEmpty()) {
            NVGRenderer.text(
                "No sets on this page",
                sw / 2f, sh / 2f - 10f,
                13f, ThemeManager.currentTheme.text,
            )
        }

        NVGRenderer.endFrame()

        // ── Pass 4: armor tooltip on hover ───────────────────────────────────
        hoveredId?.let { id ->
            val set = WardrobeState.sets[id - 1]
            val rect = slotRects[id] ?: return@let
            val tooltipX = ((rect.x + rect.w + 4f) / guiScale).toInt()
            val tooltipY = (rect.y / guiScale).toInt()
            set.armor.filterNotNull().filter { !it.isEmpty }.forEach { stack ->
                val tooltip = stack.getTooltipLines(
                    net.minecraft.world.item.Item.TooltipContext.EMPTY,
                    mc.player,
                    if (mc.options.advancedItemTooltips) net.minecraft.world.item.TooltipFlag.Default.ADVANCED
                    else net.minecraft.world.item.TooltipFlag.Default.NORMAL,
                )
                graphics.renderTooltip(mc.font, tooltip, tooltipX, tooltipY)
            }
        }
    }
}
```

> **Note on `InventoryScreen.renderEntityInInventory` signature:** The method signature changed in MC 1.21.x. Verify the exact overload in your IDE. The 5-argument bounding-box overload `(GuiGraphics, float, float, float, float, int, Vector3f, LivingEntity)` or the center-point overload `(GuiGraphics, int, int, int, Vector3f, Quaternionf, Quaternionf?, LivingEntity)` may apply — use whichever compiles. Adjust parameters accordingly.

- [ ] **Step 2: Verify build**

```bash
./gradlew build
```
Fix any compilation errors (likely around the `InventoryScreen.renderEntityInInventory` signature — check in IDE for the exact 1.21.11 overload and adjust the call).

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/wardrobe/WardrobeRenderer.kt
git commit -m "feat: add WardrobeRenderer with NVG panel and fake player renders"
```

---

### Task 6: WardrobeModule — screen detection, packet parsing, click handling

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/wardrobe/WardrobeModule.kt`

- [ ] **Step 1: Create WardrobeModule.kt**

```kotlin
package org.cobalt.internal.wardrobe

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.item.Items
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.MouseEvent
import org.cobalt.api.event.impl.client.PacketEvent
import org.cobalt.api.event.impl.render.GuiRenderEvent
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.module.setting.impl.TextSetting
import org.cobalt.internal.helper.Config

object WardrobeModule : Module("Wardrobe GUI") {

    private val mc = Minecraft.getInstance()

    val enabled    by CheckboxSetting("Enabled",     "Replace vanilla wardrobe with custom GUI", true)
    val page1Slots by TextSetting("Page 1 Slots", "Comma-separated set numbers (1–27) for page 1", "1")
    val page2Slots by TextSetting("Page 2 Slots", "Comma-separated set numbers (1–27) for page 2", "")
    // Hidden setting — persists favorites as comma-separated IDs. Never shown in UI.
    // Keep a direct reference so we can write back to .value when favorites change.
    private val _favSetting = TextSetting("Favorites Data", "", "").inGroup("__side__")
    @Suppress("unused") private val favoritesData by _favSetting

    var currentCustomPage = 1
        private set

    private enum class ScanState { IDLE, SCANNING, DONE }
    private var scanState = ScanState.IDLE
    private var scanPagesReceived = 0
    private var openContainerId = -1
    private var pendingEquipSetId: Int? = null

    // Hitboxes built each frame by WardrobeRenderer
    data class SlotHitbox(val setId: Int, val x: Float, val y: Float, val w: Float, val h: Float)
    data class TabHitbox(val page: Int, val x: Float, val y: Float, val w: Float, val h: Float)
    data class ButtonHitbox(val type: ButtonType, val x: Float, val y: Float, val w: Float, val h: Float)
    enum class ButtonType { BACK, CLOSE }

    var slotHitboxes: List<SlotHitbox> = emptyList()
    var tabHitboxes: List<TabHitbox> = emptyList()
    var buttonHitboxes: List<ButtonHitbox> = emptyList()

    init {
        EventBus.register(this)
    }

    /** Call this from Cobalt.kt after Config.loadModulesConfig() to hydrate WardrobeState.favorites. */
    fun loadFavorites() {
        WardrobeState.favorites.clear()
        _favSetting.value.split(",").mapNotNull { it.trim().toIntOrNull() }
            .forEach { WardrobeState.favorites.add(it) }
    }

    /** Called by WardrobeScreenMixin to decide whether to cancel vanilla rendering. */
    fun shouldSuppressVanillaRender(): Boolean =
        enabled && WardrobeState.isOpen && scanState == ScanState.DONE

    fun setsOnCurrentCustomPage(): List<WardrobeSet> {
        val pageMap = WardrobePageConfig.resolvePages(page1Slots, page2Slots)
        return WardrobeState.sets
            .filter { !it.isEmpty() && !it.locked }
            .filter { pageMap[it.id] == currentCustomPage }
    }

    // ── Packet handling ───────────────────────────────────────────────────────

    @SubscribeEvent
    fun onPacket(event: PacketEvent.Incoming) {
        when (val pkt = event.packet) {
            is ClientboundOpenScreenPacket  -> handleOpenScreen(pkt)
            is ClientboundContainerSetContentPacket -> {
                if (WardrobeState.isOpen) handleContainerContent(pkt)
            }
        }
    }

    private val WARDROBE_TITLE_REGEX = Regex("""Wardrobe \((\d+)/\d+\)""")

    private fun handleOpenScreen(pkt: ClientboundOpenScreenPacket) {
        val title = pkt.title.string.replace(Regex("\u00A7[0-9a-fk-or]"), "")
        val match = WARDROBE_TITLE_REGEX.find(title)
        if (match == null) {
            if (WardrobeState.isOpen) closeWardrobe()
            return
        }
        val page = match.groupValues[1].toInt()
        WardrobeState.isOpen = true
        WardrobeState.currentVanillaPage = page
        openContainerId = pkt.containerId

        if (scanState == ScanState.IDLE) {
            scanState = ScanState.SCANNING
            scanPagesReceived = 0
            currentCustomPage = 1
        }
    }

    private fun handleContainerContent(pkt: ClientboundContainerSetContentPacket) {
        if (pkt.containerId != openContainerId) return
        val page = WardrobeState.currentVanillaPage ?: return
        val items = pkt.items

        // Inventory layout for each vanilla page:
        //   0–8   helmets, 9–17 chestplates, 18–26 leggings, 27–35 boots  (for sets on this page)
        //   36–44 set selector slots (click to equip; name contains "Equipped" if active;
        //          red stained glass pane if locked)
        val armorBySetId = mutableMapOf<Int, List<net.minecraft.world.item.ItemStack?>>()
        var equippedId: Int? = null
        val lockedIds = mutableSetOf<Int>()

        for (slotIndex in 0 until 9) {
            val setId = (page - 1) * 9 + slotIndex + 1

            fun slot(base: Int) = items.getOrNull(base + slotIndex)?.takeIf { !it.isEmpty }

            armorBySetId[setId] = listOf(slot(0), slot(9), slot(18), slot(27))

            val selectorItem = items.getOrNull(36 + slotIndex)
            if (selectorItem != null && !selectorItem.isEmpty) {
                // Check equipped: look for "Equipped" in lore
                val lore = selectorItem.get(net.minecraft.core.component.DataComponents.LORE)
                    ?.lines?.joinToString(" ") { it.string.replace(Regex("\u00A7[0-9a-fk-or]"), "") } ?: ""
                if (lore.contains("Equipped", ignoreCase = true) ||
                    selectorItem.hoverName.string.contains("Equipped", ignoreCase = true)) {
                    equippedId = setId
                }
                // Locked = red stained glass pane
                if (selectorItem.`is`(Items.RED_STAINED_GLASS_PANE)) {
                    lockedIds.add(setId)
                }
            }
        }

        WardrobeState.updatePage(page, armorBySetId, equippedId, lockedIds)
        scanPagesReceived++

        if (scanState == ScanState.SCANNING && scanPagesReceived < 3) {
            // Navigate to next vanilla page via slot 53 (next button)
            mc.execute {
                val screen = mc.screen as? AbstractContainerScreen<*> ?: return@execute
                mc.gameMode?.handleInventoryMouseClick(
                    screen.menu.containerId, 53, 0, ClickType.PICKUP, mc.player!!,
                )
            }
        } else if (scanState == ScanState.SCANNING && scanPagesReceived >= 3) {
            scanState = ScanState.DONE
        }

        // Equip pending set if we just navigated to its page
        val pending = pendingEquipSetId
        if (pending != null) {
            val set = WardrobeState.sets.getOrNull(pending - 1)
            if (set != null && set.vanillaPage == page) {
                pendingEquipSetId = null
                mc.execute { clickVanillaSlot(set.inventorySlot) }
            }
        }
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    @SubscribeEvent
    fun onGuiRender(event: GuiRenderEvent) {
        if (!shouldSuppressVanillaRender()) return
        WardrobeRenderer.render(event.graphics, this)
    }

    // ── Click handling ────────────────────────────────────────────────────────

    @SubscribeEvent
    fun onLeftClick(event: MouseEvent.LeftClick) {
        if (event.action != MouseEvent.PRESS) return
        if (!shouldSuppressVanillaRender()) return

        val mx = mc.mouseHandler.xpos().toFloat()
        val my = mc.mouseHandler.ypos().toFloat()

        fun inBounds(x: Float, y: Float, w: Float, h: Float) =
            mx >= x && mx <= x + w && my >= y && my <= y + h

        // Slot clicks (includes favorite heart region — handled by area proximity)
        slotHitboxes.firstOrNull { inBounds(it.x, it.y, it.w, it.h) }?.let { hit ->
            // Detect if click is on the heart (top-right 20×20 of the card)
            val heartX = hit.x + hit.w - 20f
            val heartY = hit.y
            if (inBounds(heartX, heartY, 20f, 20f)) {
                clickFavorite(hit.setId)
            } else {
                clickSet(hit.setId)
            }
            return
        }

        // Tab clicks
        tabHitboxes.firstOrNull { inBounds(it.x, it.y, it.w, it.h) }?.let { hit ->
            currentCustomPage = hit.page
            return
        }

        // Button clicks
        buttonHitboxes.firstOrNull { inBounds(it.x, it.y, it.w, it.h) }?.let { hit ->
            when (hit.type) {
                ButtonType.BACK  -> clickVanillaSlot(48)
                ButtonType.CLOSE -> clickVanillaSlot(49)
            }
            return
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun clickSet(setId: Int) {
        val set = WardrobeState.sets.getOrNull(setId - 1) ?: return
        if (set.isEmpty() || set.locked) return
        val currentVanillaPage = WardrobeState.currentVanillaPage ?: return

        if (set.vanillaPage == currentVanillaPage) {
            clickVanillaSlot(set.inventorySlot)
        } else {
            // Navigate first, then equip via pendingEquipSetId
            pendingEquipSetId = setId
            val pageDiff = set.vanillaPage - currentVanillaPage
            repeat(kotlin.math.abs(pageDiff)) {
                clickVanillaSlot(if (pageDiff > 0) 53 else 45)
            }
        }
    }

    private fun clickFavorite(setId: Int) {
        if (!WardrobeState.favorites.add(setId)) WardrobeState.favorites.remove(setId)
        _favSetting.value = WardrobeState.favorites.joinToString(",")
        Config.saveModulesConfig()
    }

    private fun clickVanillaSlot(slot: Int) {
        val screen = mc.screen as? AbstractContainerScreen<*> ?: return
        mc.gameMode?.handleInventoryMouseClick(
            screen.menu.containerId, slot, 0, ClickType.PICKUP, mc.player!!,
        )
    }

    private fun closeWardrobe() {
        WardrobeState.reset()
        WardrobeFakePlayerCache.clear()
        scanState = ScanState.IDLE
        scanPagesReceived = 0
        openContainerId = -1
        pendingEquipSetId = null
    }
}
```

- [ ] **Step 2: Verify build**

```bash
./gradlew build
```

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/wardrobe/WardrobeModule.kt
git commit -m "feat: add WardrobeModule with packet parsing and click handling"
```

---

### Task 7: Register WardrobeModule in Cobalt.kt

**Files:**
- Modify: `src/main/kotlin/org/cobalt/Cobalt.kt`

- [ ] **Step 1: Add import**

Add to the import block in `Cobalt.kt`:
```kotlin
import org.cobalt.internal.wardrobe.WardrobeModule
```

- [ ] **Step 2: Register the module**

In `onInitializeClient()`, inside the `ModuleManager.addModules(listOf(...))` call, add `WardrobeModule` after `DianaHelperModule`:

```kotlin
        DianaHelperModule,
        WardrobeModule,
```

- [ ] **Step 3: Load favorites after config load**

Still in `onInitializeClient()`, after `Config.loadModulesConfig()`:

```kotlin
    Config.loadModulesConfig()
    WardrobeModule.loadFavorites()   // hydrate WardrobeState.favorites from persisted setting
```

- [ ] **Step 5: Verify build**

```bash
./gradlew build
```
Expected: BUILD SUCCESSFUL.

- [ ] **Step 6: Commit**

```bash
git add src/main/kotlin/org/cobalt/Cobalt.kt
git commit -m "feat: register WardrobeModule and load persisted favorites"
```

---

### Task 8: Manual verification checklist

- [ ] Run client: `./gradlew runClient`
- [ ] `/wardrobe` command in Hypixel SkyBlock Garden or lobby → vanilla wardrobe opens → custom overlay replaces it
- [ ] All 3 custom page tabs visible; clicking tabs switches displayed sets
- [ ] 3D player renders show correct armor per set
- [ ] Equipped set has accent border and "Equipped" badge
- [ ] Hovering a set shows armor tooltip
- [ ] Clicking a non-current-vanilla-page set navigates vanilla pages silently and equips
- [ ] Favorite heart toggles gold border
- [ ] Back and Close buttons work
- [ ] Set auto-named "Farming" when all pieces start with "Mossy", "Pest" for "Mantid", "Mining" for "Jaded"
- [ ] Setting Page 1 Slots to `"1,4,5"` → sets 1, 4, 5 appear on page 1; rest on page 3

---
