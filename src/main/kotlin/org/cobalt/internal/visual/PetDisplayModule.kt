package org.cobalt.internal.visual

import kotlin.math.cos
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
import org.cobalt.api.event.EventBus
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.client.TickEvent
import org.cobalt.api.event.impl.render.GuiRenderContext
import org.cobalt.api.hud.HudAnchor
import org.cobalt.api.hud.HudModuleManager
import org.cobalt.api.hud.hudElement
import org.cobalt.api.module.Module
import org.cobalt.api.module.setting.impl.CheckboxSetting
import org.cobalt.api.ui.theme.ThemeManager
import org.cobalt.api.util.ui.NVGRenderer
import org.cobalt.api.util.ui.helper.Gradient

object PetDisplayModule : Module("Pet Display") {

    private val mc = Minecraft.getInstance()

    // ── Settings ──────────────────────────────────────────────────────────────

    private val enabledSetting      = CheckboxSetting("Enabled",        "Show the pet info HUD.",            true)
    private val glowSetting         = CheckboxSetting("Glow",           "Animated glow border.",             true)
    private val showHeldItemSetting = CheckboxSetting("Show Held Item", "Show the pet's held item line.",    true)

    // ── Layout constants (mirrors Spotify HUD) ────────────────────────────────

    private const val W      = 240f
    private const val ICON   = 52f   // fixed icon square
    private const val CORNER = 10f
    private const val PAD    = 8f
    private const val TEXT_X = PAD + ICON + PAD   // 68f — text column starts here

    // ── Pet item cache ────────────────────────────────────────────────────────

    @Volatile private var cachedPetItem: ItemStack? = null
    @Volatile private var cachedPetName: String? = null

    private val formattingCodeRegex = Regex("""\u00A7[0-9A-FK-ORa-fk-or]""")
    private fun stripFormatting(text: String) = formattingCodeRegex.replace(text, "")
    private fun matchesPet(stack: ItemStack, petName: String): Boolean {
        if (stack.isEmpty) return false
        return stripFormatting(stack.hoverName.string).contains(petName, ignoreCase = true)
    }

    private fun updateCachedPetItem() {
        val pet = PetTabListParser.current
        if (pet == null) {
            cachedPetItem = null
            cachedPetName = null
            return
        }

        val player = mc.player ?: run {
            cachedPetItem = null
            cachedPetName = null
            return
        }

        if (cachedPetName != null && !cachedPetName.equals(pet.name, ignoreCase = true)) {
            cachedPetItem = null
        }
        cachedPetName = pet.name

        // Search player inventory + any open container (catches the pets GUI chest)
        val menu = player.containerMenu
        val found = (0 until player.inventory.containerSize).asSequence()
            .map { player.inventory.getItem(it) }
            .plus(menu.slots.asSequence().map { it.item })
            .firstOrNull { stack -> matchesPet(stack, pet.name) }

        if (found != null) {
            cachedPetItem = found.copy()
            return
        }

        if (cachedPetItem?.let { matchesPet(it, pet.name) } != true) {
            cachedPetItem = null
        }
    }

    // ── HUD ───────────────────────────────────────────────────────────────────

    val petHud = hudElement("pet-display", "Pet Display", "Animated pet info HUD") {
        anchor  = HudAnchor.BOTTOM_RIGHT
        offsetX = 10f
        offsetY = 10f

        fun hudHeight(): Float {
            val hasItem = showHeldItemSetting.value &&
                PetTabListParser.current?.heldItem?.isNotEmpty() == true
            return if (hasItem) 84f else 70f
        }

        width  { W }
        height { hudHeight() }

        render { x, y, _ ->
            if (!enabledSetting.value) return@render

            val now   = System.currentTimeMillis()
            val twoPi = (Math.PI * 2.0).toFloat()
            val h     = hudHeight()
            val c1    = ThemeManager.currentTheme.accent
            val c2    = ThemeManager.currentTheme.accentSecondary
            val data  = PetTabListParser.current

            // ── Glow ──────────────────────────────────────────────────────────
            if (glowSetting.value) {
                val pulse = 0.4f + 0.6f * cos((now % 4000L).toFloat() / 4000f * twoPi)
                val a2 = (0x18 * pulse).toInt().coerceIn(0, 0x28)
                val a1 = (0x2A * pulse).toInt().coerceIn(0, 0x40)
                NVGRenderer.hollowRect(x - 3f,   y - 3f,   W + 6f, h + 6f, 2.5f, (a2 shl 24) or (c1 and 0x00FFFFFF), CORNER + 3f)
                NVGRenderer.hollowRect(x - 1.5f, y - 1.5f, W + 3f, h + 3f, 1.5f, (a1 shl 24) or (c1 and 0x00FFFFFF), CORNER + 1.5f)
            }

            // ── Background ────────────────────────────────────────────────────
            NVGRenderer.rect(x, y, W, h, 0xFF0A0E1A.toInt(), CORNER)
            NVGRenderer.gradientRect(x, y, W, h * 0.5f, 0x14FFFFFF, 0x00000000, Gradient.TopToBottom, CORNER)
            val shiftX = cos((now % 10000L).toFloat() / 10000f * twoPi) * (W * 0.42f)
            NVGRenderer.hollowGradientRectShifted(x, y, W, h, 1.5f, c1, c2, Gradient.LeftToRight, CORNER, shiftX, 0f)

            // ── Icon area ─────────────────────────────────────────────────────
            val iconX = x + PAD
            val iconY = y + PAD
            NVGRenderer.rect(iconX, iconY, ICON, ICON, 0xFF101521.toInt(), 6f)
            NVGRenderer.hollowRect(iconX, iconY, ICON, ICON, 1f, 0x22FFFFFF, 6f)

            if (data == null) {
                val sym  = "✦"
                val symW = NVGRenderer.textWidth(sym, 20f)
                NVGRenderer.text(sym, iconX + ICON / 2f - symW / 2f, iconY + ICON / 2f - 10f, 20f, 0x33FFFFFF)
                NVGRenderer.text("No pet active", x + TEXT_X, y + h / 2f + 5f, 10f, 0x80FFFFFF.toInt())
                return@render
            }

            // Show ✦ placeholder only when we have no cached item to render
            if (cachedPetItem == null) {
                val sym  = "✦"
                val symW = NVGRenderer.textWidth(sym, 20f)
                NVGRenderer.text(sym, iconX + ICON / 2f - symW / 2f, iconY + ICON / 2f - 10f, 20f, c1)
            }

            val textColor = 0xFFFFFFFF.toInt()
            val dimColor  = 0xBBB0C8FF.toInt()

            // ── Pet name + level ──────────────────────────────────────────────
            val lvStr = "Lv ${data.level}"
            val lvW   = NVGRenderer.textWidth(lvStr, 10f)
            NVGRenderer.textShadow(data.name, x + TEXT_X, y + 18f, 12f, textColor)
            NVGRenderer.text(lvStr, x + W - PAD - lvW, y + 18f, 10f, 0xFF7EC8FF.toInt())

            // ── Held item ─────────────────────────────────────────────────────
            if (showHeldItemSetting.value && data.heldItem.isNotEmpty()) {
                NVGRenderer.text(data.heldItem, x + TEXT_X, y + 33f, 10f, dimColor)
            }

            // ── XP bar ───────────────────────────────────────────────────────
            val barX = x + TEXT_X
            val barY = y + h - PAD - 6f
            val barW = W - TEXT_X - PAD
            val barH = 6f
            val barR = barH / 2f

            NVGRenderer.rect(barX, barY, barW, barH, 0xFF1A2040.toInt(), barR)

            if (data.isMaxLevel) {
                NVGRenderer.gradientRect(barX, barY, barW, barH, 0xFFFFD700.toInt(), 0xFFFFA500.toInt(), Gradient.LeftToRight, barR)
                val maxW = NVGRenderer.textWidth("MAX", 8f)
                NVGRenderer.text("MAX", barX + barW / 2f - maxW / 2f, barY - 1f, 8f, 0xFFFFD700.toInt())
            } else if (data.xpRequired > 0) {
                val ratio = (data.xpCurrent.toFloat() / data.xpRequired.toFloat()).coerceIn(0f, 1f)
                if (ratio > 0f) {
                    val fillW = (barW * ratio).coerceAtLeast(0f)
                    if (fillW > barR * 2f) {
                        NVGRenderer.pushScissor(barX, barY, fillW, barH)
                        NVGRenderer.gradientRect(barX, barY, barW, barH, c1, c2, Gradient.LeftToRight, barR)
                        NVGRenderer.popScissor()
                        NVGRenderer.circle(barX + fillW, barY + barH / 2f, barH * 0.7f,  (0x88 shl 24) or (c1 and 0x00FFFFFF))
                        NVGRenderer.circle(barX + fillW, barY + barH / 2f, barH * 0.38f, 0xCCFFFFFF.toInt())
                    }
                }
                val pct  = "${(ratio * 100).toInt()}%"
                val pctW = NVGRenderer.textWidth(pct, 8f)
                NVGRenderer.text(pct, barX + barW / 2f - pctW / 2f, barY - 1f, 8f, dimColor)
            }
        }

        // ── Item icon — rendered after NVG endFrame so it appears on top ──────
        postRender { screenX, screenY, scale ->
            if (!enabledSetting.value) return@postRender
            if (PetTabListParser.current == null) return@postRender
            if (mc.screen != null && !HudModuleManager.isEditorOpen) return@postRender
            val item = cachedPetItem ?: return@postRender

            val graphics = GuiRenderContext.getGraphics() ?: return@postRender
            val window   = mc.window
            val guiScale = window.guiScale.toFloat()
            if (guiScale <= 0f) return@postRender

            // Convert icon area from screen pixels to GUI coordinates
            val iconGuiX    = (screenX + PAD * scale) / guiScale
            val iconGuiY    = (screenY + PAD * scale) / guiScale
            val iconGuiSize = ICON * scale / guiScale
            // Scale item (16x16 GUI px by default) to fill the icon area
            val itemScale   = iconGuiSize / 16f

            val pose = graphics.pose()
            pose.pushMatrix()
            pose.translate(iconGuiX, iconGuiY)
            pose.scale(itemScale, itemScale)
            graphics.renderItem(item, 0, 0)
            pose.popMatrix()
        }
    }

    // ── Init ──────────────────────────────────────────────────────────────────

    init {
        addSetting(enabledSetting, glowSetting, showHeldItemSetting)
        EventBus.register(this)
    }

    // ── Tick ──────────────────────────────────────────────────────────────────

    @SubscribeEvent
    fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.End) {
        if (!enabledSetting.value || !petHud.enabled) return
        PetTabListParser.update()
        updateCachedPetItem()
    }
}
