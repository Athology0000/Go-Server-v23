/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.StringsKt
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1703
 *  net.minecraft.class_1713
 *  net.minecraft.class_1799
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_636
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.util;

import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_636;
import net.minecraft.class_746;
import org.cobalt.api.util.ItemUtilsKt;
import org.cobalt.api.util.MouseClickType;
import org.cobalt.internal.qol.ItemLockingModule;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J+\u0010\u000b\u001a\u00020\n2\u0006\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\t\u001a\u00020\bH\u0007\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0017\u0010\r\u001a\u00020\n2\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\r\u0010\u000eJ\u001f\u0010\u0010\u001a\u00020\n2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0017\u0010\u0014\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u0012H\u0007\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u0017\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00020\u0012H\u0007\u00a2\u0006\u0004\b\u0017\u0010\u0015J\u0017\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u0012H\u0007\u00a2\u0006\u0004\b\u0018\u0010\u0015J\u0017\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00020\u0012H\u0007\u00a2\u0006\u0004\b\u0019\u0010\u0015R\u0014\u0010\u001b\u001a\u00020\u001a8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u001cR\u001b\u0010!\u001a\t\u0018\u00010\u001d\u00a2\u0006\u0002\b\u001e8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u001f\u0010 R\u001b\u0010%\u001a\t\u0018\u00010\"\u00a2\u0006\u0002\b\u001e8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b#\u0010$\u00a8\u0006&"}, d2={"Lorg/cobalt/api/util/InventoryUtils;", "", "<init>", "()V", "", "slot", "Lorg/cobalt/api/util/MouseClickType;", "click", "Lnet/minecraft/class_1713;", "action", "", "clickSlot", "(ILorg/cobalt/api/util/MouseClickType;Lnet/minecraft/class_1713;)V", "holdHotbarSlot", "(I)V", "hotbarSlot", "swapSlotWithHotbar", "(II)V", "", "name", "findItemInHotbar", "(Ljava/lang/String;)I", "lore", "findItemInHotbarWithLore", "findItemInInventory", "findItemInInventoryWithLore", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lnet/minecraft/class_746;", "Lorg/jspecify/annotations/Nullable;", "getPlayer", "()Lnet/minecraft/class_746;", "player", "Lnet/minecraft/class_636;", "getInteractionManager", "()Lnet/minecraft/class_636;", "interactionManager", "cobalt"})
public final class InventoryUtils {
    @NotNull
    public static final InventoryUtils INSTANCE = new InventoryUtils();
    @NotNull
    private static final class_310 mc;

    private InventoryUtils() {
    }

    private final class_746 getPlayer() {
        return InventoryUtils.mc.field_1724;
    }

    private final class_636 getInteractionManager() {
        return InventoryUtils.mc.field_1761;
    }

    @JvmStatic
    public static final void clickSlot(int slot, @NotNull MouseClickType click, @NotNull class_1713 action) {
        block1: {
            Intrinsics.checkNotNullParameter((Object)((Object)click), (String)"click");
            Intrinsics.checkNotNullParameter((Object)action, (String)"action");
            class_746 class_7462 = INSTANCE.getPlayer();
            if (class_7462 == null) {
                return;
            }
            class_746 player = class_7462;
            class_1703 class_17032 = player.field_7512;
            Intrinsics.checkNotNullExpressionValue((Object)class_17032, (String)"containerMenu");
            class_1703 handler = class_17032;
            class_636 class_6362 = INSTANCE.getInteractionManager();
            if (class_6362 == null) break block1;
            class_6362.method_2906(handler.field_7763, slot, click.ordinal(), action, (class_1657)player);
        }
    }

    public static /* synthetic */ void clickSlot$default(int n, MouseClickType mouseClickType, class_1713 class_17132, int n2, Object object) {
        if ((n2 & 2) != 0) {
            mouseClickType = MouseClickType.LEFT;
        }
        if ((n2 & 4) != 0) {
            class_17132 = class_1713.field_7790;
        }
        InventoryUtils.clickSlot(n, mouseClickType, class_17132);
    }

    @JvmStatic
    public static final void holdHotbarSlot(int slot) {
        block1: {
            if (!(0 <= slot ? slot < 9 : false)) {
                return;
            }
            class_746 class_7462 = INSTANCE.getPlayer();
            if (class_7462 == null || (class_7462 = class_7462.method_31548()) == null) break block1;
            class_7462.method_61496(slot);
        }
    }

    @JvmStatic
    public static final void swapSlotWithHotbar(int slot, int hotbarSlot) {
        block3: {
            if (!(0 <= hotbarSlot ? hotbarSlot < 9 : false)) {
                return;
            }
            if (ItemLockingModule.INSTANCE.isBlockedHotbarSlot(hotbarSlot)) {
                return;
            }
            class_746 class_7462 = INSTANCE.getPlayer();
            if (class_7462 == null) {
                return;
            }
            class_746 player = class_7462;
            class_1703 class_17032 = player.field_7512;
            Intrinsics.checkNotNullExpressionValue((Object)class_17032, (String)"containerMenu");
            class_1703 handler = class_17032;
            class_636 class_6362 = INSTANCE.getInteractionManager();
            if (class_6362 == null) break block3;
            class_6362.method_2906(handler.field_7763, slot, hotbarSlot, class_1713.field_7791, (class_1657)player);
        }
    }

    @JvmStatic
    public static final int findItemInHotbar(@NotNull String name) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        class_746 class_7462 = INSTANCE.getPlayer();
        if (class_7462 == null) {
            return -1;
        }
        class_746 player = class_7462;
        class_1661 class_16612 = player.method_31548();
        Intrinsics.checkNotNullExpressionValue((Object)class_16612, (String)"getInventory(...)");
        class_1661 inventory = class_16612;
        for (int i = 0; i < 9; ++i) {
            String displayName;
            class_1799 stack;
            Intrinsics.checkNotNullExpressionValue((Object)inventory.method_5438(i), (String)"getItem(...)");
            if (stack.method_7960()) continue;
            Intrinsics.checkNotNullExpressionValue((Object)stack.method_7964().getString(), (String)"getString(...)");
            if (!StringsKt.contains((CharSequence)displayName, (CharSequence)name, (boolean)true)) continue;
            return i;
        }
        return -1;
    }

    @JvmStatic
    public static final int findItemInHotbarWithLore(@NotNull String lore) {
        Intrinsics.checkNotNullParameter((Object)lore, (String)"lore");
        class_746 class_7462 = INSTANCE.getPlayer();
        if (class_7462 == null) {
            return -1;
        }
        class_746 player = class_7462;
        class_1661 class_16612 = player.method_31548();
        Intrinsics.checkNotNullExpressionValue((Object)class_16612, (String)"getInventory(...)");
        class_1661 inventory = class_16612;
        for (int i = 0; i < 9; ++i) {
            class_1799 stack;
            Intrinsics.checkNotNullExpressionValue((Object)inventory.method_5438(i), (String)"getItem(...)");
            if (stack.method_7960()) continue;
            for (class_2561 line : ItemUtilsKt.getLoreLines(stack)) {
                String string = line.getString();
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
                if (!StringsKt.contains((CharSequence)string, (CharSequence)lore, (boolean)true)) continue;
                return i;
            }
        }
        return -1;
    }

    @JvmStatic
    public static final int findItemInInventory(@NotNull String name) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        class_746 class_7462 = INSTANCE.getPlayer();
        if (class_7462 == null) {
            return -1;
        }
        class_746 player = class_7462;
        class_1661 class_16612 = player.method_31548();
        Intrinsics.checkNotNullExpressionValue((Object)class_16612, (String)"getInventory(...)");
        class_1661 inventory = class_16612;
        int n = inventory.method_5439();
        for (int i = 0; i < n; ++i) {
            class_1799 stack;
            Intrinsics.checkNotNullExpressionValue((Object)inventory.method_5438(i), (String)"getItem(...)");
            if (stack.method_7960()) continue;
            String string = stack.method_7964().getString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            if (!StringsKt.contains((CharSequence)string, (CharSequence)name, (boolean)true)) continue;
            return i;
        }
        return -1;
    }

    @JvmStatic
    public static final int findItemInInventoryWithLore(@NotNull String lore) {
        Intrinsics.checkNotNullParameter((Object)lore, (String)"lore");
        class_746 class_7462 = INSTANCE.getPlayer();
        if (class_7462 == null) {
            return -1;
        }
        class_746 player = class_7462;
        class_1661 class_16612 = player.method_31548();
        Intrinsics.checkNotNullExpressionValue((Object)class_16612, (String)"getInventory(...)");
        class_1661 inventory = class_16612;
        int n = inventory.method_5439();
        for (int i = 0; i < n; ++i) {
            class_1799 stack;
            Intrinsics.checkNotNullExpressionValue((Object)inventory.method_5438(i), (String)"getItem(...)");
            if (stack.method_7960()) continue;
            for (class_2561 line : ItemUtilsKt.getLoreLines(stack)) {
                String string = line.getString();
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
                if (!StringsKt.contains((CharSequence)string, (CharSequence)lore, (boolean)true)) continue;
                return i;
            }
        }
        return -1;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
    }
}

