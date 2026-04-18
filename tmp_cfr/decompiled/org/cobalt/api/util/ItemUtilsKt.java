/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_1799
 *  net.minecraft.class_2561
 *  net.minecraft.class_9290
 *  net.minecraft.class_9334
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.util;

import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_9290;
import net.minecraft.class_9334;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=2, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001*\u00020\u0000\u00a2\u0006\u0004\b\u0003\u0010\u0004\u00a8\u0006\u0005"}, d2={"Lnet/minecraft/class_1799;", "", "Lnet/minecraft/class_2561;", "getLoreLines", "(Lnet/minecraft/class_1799;)Ljava/util/List;", "cobalt"})
public final class ItemUtilsKt {
    @NotNull
    public static final List<class_2561> getLoreLines(@NotNull class_1799 $this$getLoreLines) {
        Intrinsics.checkNotNullParameter((Object)$this$getLoreLines, (String)"<this>");
        class_9290 class_92902 = (class_9290)$this$getLoreLines.method_58694(class_9334.field_49632);
        if (class_92902 == null) {
            return CollectionsKt.emptyList();
        }
        class_9290 lore = class_92902;
        List list = lore.comp_2400();
        Intrinsics.checkNotNullExpressionValue((Object)list, (String)"lines(...)");
        return list;
    }
}

