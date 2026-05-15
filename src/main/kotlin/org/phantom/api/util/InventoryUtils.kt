package org.phantom.api.util

import net.minecraft.client.Minecraft
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.item.ItemStack
import org.phantom.internal.qol.ItemLockingModule

object InventoryUtils {

  private val mc: Minecraft =
    Minecraft.getInstance()

  private val player
    get() = mc.player

  private val interactionManager
    get() = mc.gameMode

  @JvmStatic
  fun clickSlot(
    slot: Int,
    click: MouseClickType = MouseClickType.LEFT,
    action: ClickType = ClickType.PICKUP,
  ) {
    val player = player ?: return
    val handler = player.containerMenu

    interactionManager?.handleInventoryMouseClick(
      handler.containerId,
      slot,
      click.ordinal,
      action,
      player
    )
  }

  @JvmStatic
  fun holdHotbarSlot(slot: Int) {
    if (slot !in 0..8) return
    player?.inventory?.selectedSlot = slot
  }

  @JvmStatic
  fun swapSlotWithHotbar(slot: Int, hotbarSlot: Int) {
    if (hotbarSlot !in 0..8) return
    if (ItemLockingModule.isBlockedHotbarSlot(hotbarSlot)) return
    val player = player ?: return
    val handler = player.containerMenu

    interactionManager?.handleInventoryMouseClick(
      handler.containerId,
      slot,
      hotbarSlot,
      ClickType.SWAP,
      player
    )
  }

  @JvmStatic
  fun findItemInHotbar(name: String): Int {
    val player = player ?: return -1
    val inventory = player.inventory

    for (i in 0..8) {
      val stack = inventory.getItem(i)
      if (stack.isEmpty) continue

      val displayName =
        stack.hoverName.string

      if (displayName.contains(name, ignoreCase = true)) {
        return i
      }
    }

    return -1
  }

  @JvmStatic
  fun findItemInHotbarWithLore(lore: String): Int {
    val player = player ?: return -1
    val inventory = player.inventory

    for (i in 0..8) {
      val stack = inventory.getItem(i)
      if (stack.isEmpty) continue

      for (line in stack.getLoreLines()) {
        if (line.string.contains(lore, ignoreCase = true)) {
          return i
        }
      }
    }

    return -1
  }

  @JvmStatic
  fun findItemInInventory(name: String): Int {
    val player = player ?: return -1
    val inventory = player.inventory

    for (i in 0 until inventory.containerSize) {
      val stack = inventory.getItem(i)
      if (stack.isEmpty) continue

      if (stack.hoverName.string.contains(name, ignoreCase = true)) {
        return i
      }
    }

    return -1
  }

  @JvmStatic
  fun findItemInInventoryWithLore(lore: String): Int {
    val player = player ?: return -1
    val inventory = player.inventory

    for (i in 0 until inventory.containerSize) {
      val stack = inventory.getItem(i)
      if (stack.isEmpty) continue

      for (line in stack.getLoreLines()) {
        if (line.string.contains(lore, ignoreCase = true)) {
          return i
        }
      }
    }

    return -1
  }

  /** Current hotbar slot (0..8), or -1 when no player is loaded. */
  @JvmStatic
  fun selectedSlot(): Int = player?.inventory?.selectedSlot ?: -1

  /** The [ItemStack] in the currently selected hotbar slot, or [ItemStack.EMPTY]. */
  @JvmStatic
  fun selectedItem(): ItemStack = player?.let { it.inventory.getItem(it.inventory.selectedSlot) } ?: ItemStack.EMPTY

  /** Item at [slot] in the main inventory (hotbar 0..8 + storage 9..35), or [ItemStack.EMPTY]. */
  @JvmStatic
  fun itemAt(slot: Int): ItemStack {
    val inv = player?.inventory ?: return ItemStack.EMPTY
    if (slot !in 0 until inv.containerSize) return ItemStack.EMPTY
    return inv.getItem(slot)
  }

  /**
   * Generic hotbar finder. Returns the first slot 0..8 where [predicate] holds
   * for the (non-empty) stack, or -1. Generalises the name/lore-specific
   * helpers above; new callers should prefer this over inlining a loop.
   */
  inline fun findHotbarSlotMatching(predicate: (ItemStack) -> Boolean): Int {
    val p = Minecraft.getInstance().player ?: return -1
    val inv = p.inventory
    for (i in 0..8) {
      val stack = inv.getItem(i)
      if (stack.isEmpty) continue
      if (predicate(stack)) return i
    }
    return -1
  }

  /**
   * Holds [slot] for the duration of [block], then restores the previously
   * selected slot. Centralises the "remember â†’ swap â†’ run â†’ restore" pattern
   * spread across CombatMacroModule, FishingMacroModule, DungeonsModule, etc.
   * Returns the block's value, or null if there's no player.
   */
  inline fun <T> withHotbarSlot(slot: Int, block: () -> T): T? {
    val p = Minecraft.getInstance().player ?: return null
    if (slot !in 0..8) return block()
    val previous = p.inventory.selectedSlot
    if (previous == slot) return block()
    p.inventory.selectedSlot = slot
    return try {
      block()
    } finally {
      p.inventory.selectedSlot = previous.coerceIn(0, 8)
    }
  }
}

enum class MouseClickType {
  LEFT,
  RIGHT,
  MIDDLE
}
