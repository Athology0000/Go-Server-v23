/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.components.tooltips;

import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.cobalt.internal.ui.UIComponent;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\b\u0003\b\u00c0\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\r\u0010\t\u001a\u00020\u0006\u00a2\u0006\u0004\b\t\u0010\u0003R\u001a\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00040\n8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u000b\u0010\f\u00a8\u0006\r"}, d2={"Lorg/cobalt/internal/ui/components/tooltips/TooltipManager;", "", "<init>", "()V", "Lorg/cobalt/internal/ui/UIComponent;", "tooltip", "", "register", "(Lorg/cobalt/internal/ui/UIComponent;)V", "renderAll", "", "tooltips", "Ljava/util/List;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nTooltipManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TooltipManager.kt\norg/cobalt/internal/ui/components/tooltips/TooltipManager\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,18:1\n1915#2,2:19\n*S KotlinDebug\n*F\n+ 1 TooltipManager.kt\norg/cobalt/internal/ui/components/tooltips/TooltipManager\n*L\n14#1:19,2\n*E\n"})
public final class TooltipManager {
    @NotNull
    public static final TooltipManager INSTANCE = new TooltipManager();
    @NotNull
    private static final List<UIComponent> tooltips = new ArrayList();

    private TooltipManager() {
    }

    public final void register(@NotNull UIComponent tooltip) {
        Intrinsics.checkNotNullParameter((Object)tooltip, (String)"tooltip");
        tooltips.add(tooltip);
    }

    public final void renderAll() {
        Iterable $this$forEach$iv = tooltips;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            UIComponent it = (UIComponent)element$iv;
            boolean bl = false;
            it.render();
        }
    }
}

