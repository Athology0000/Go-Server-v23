/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.StringsKt
 *  net.minecraft.class_124
 *  net.minecraft.class_1297
 *  net.minecraft.class_1542
 *  net.minecraft.class_1799
 *  net.minecraft.class_638
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.dungeons;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_1799;
import net.minecraft.class_638;
import org.cobalt.api.util.SkyblockItemUtilsKt;
import org.cobalt.internal.dungeons.map.GridComponent;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=2, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\u001a\u0017\u0010\u0003\u001a\u00020\u00022\u0006\u0010\u0001\u001a\u00020\u0000H\u0000\u00a2\u0006\u0004\b\u0003\u0010\u0004\u001a\u001d\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\u0006\u001a\u00020\u0005H\u0000\u00a2\u0006\u0004\b\t\u0010\n\u001a\u0017\u0010\r\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\b\r\u0010\u000e\u00a8\u0006\u000f"}, d2={"Lnet/minecraft/class_1799;", "stack", "", "isWitherKeyItem", "(Lnet/minecraft/class_1799;)Z", "Lnet/minecraft/class_638;", "level", "", "", "findWitherKeyRoomIndices", "(Lnet/minecraft/class_638;)Ljava/util/Set;", "", "text", "stripDungeonFormatting", "(Ljava/lang/String;)Ljava/lang/String;", "cobalt"})
public final class DungeonKeyUtilsKt {
    public static final boolean isWitherKeyItem(@NotNull class_1799 stack) {
        Intrinsics.checkNotNullParameter((Object)stack, (String)"stack");
        if (stack.method_7960()) {
            return false;
        }
        String skyblockId = SkyblockItemUtilsKt.getSkyblockId(stack);
        if (StringsKt.equals((String)skyblockId, (String)"WITHER_KEY", (boolean)true)) {
            return true;
        }
        String string = stack.method_7964().getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        String string2 = DungeonKeyUtilsKt.stripDungeonFormatting(string);
        Locale locale = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
        String string3 = string2.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
        String displayName = string3;
        return StringsKt.contains$default((CharSequence)displayName, (CharSequence)"wither key", (boolean)false, (int)2, null);
    }

    @NotNull
    public static final Set<Integer> findWitherKeyRoomIndices(@NotNull class_638 level2) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        LinkedHashSet indices = new LinkedHashSet();
        for (Object t : level2.method_18112()) {
            GridComponent component;
            class_1542 itemEntity;
            Intrinsics.checkNotNullExpressionValue(t, (String)"next(...)");
            class_1297 entity = (class_1297)t;
            class_1542 class_15422 = entity instanceof class_1542 ? (class_1542)entity : null;
            if (class_15422 == null || !(itemEntity = class_15422).method_5805()) continue;
            class_1799 class_17992 = itemEntity.method_6983();
            Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getItem(...)");
            if (!DungeonKeyUtilsKt.isWitherKeyItem(class_17992) || !(component = GridComponent.Companion.fromWorld(itemEntity.method_23317(), itemEntity.method_23321())).isValid()) continue;
            if (component.isRoom()) {
                int roomIndex = component.roomIndex();
                boolean bl = 0 <= roomIndex ? roomIndex < 36 : false;
                if (!bl) continue;
                ((Collection)indices).add(roomIndex);
                continue;
            }
            if (!component.isDoor()) continue;
            for (GridComponent neighbor : component.neighboringRooms()) {
                int roomIndex = neighbor.roomIndex();
                boolean bl = 0 <= roomIndex ? roomIndex < 36 : false;
                if (!bl) continue;
                ((Collection)indices).add(roomIndex);
            }
        }
        return indices;
    }

    private static final String stripDungeonFormatting(String text) {
        String string = class_124.method_539((String)text);
        if (string == null) {
            string = text;
        }
        return string;
    }
}

