/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.util;

import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.cobalt.internal.ui.UIComponent;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\n\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\b\u0000\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0004\u00a2\u0006\u0004\b\b\u0010\tJ\u0015\u0010\u000b\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u0002\u00a2\u0006\u0004\b\u000b\u0010\fJ+\u0010\u0013\u001a\u00020\u00122\u0006\u0010\r\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u00042\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f\u00a2\u0006\u0004\b\u0013\u0010\u0014R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u0015R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0016R\u0014\u0010\u0006\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0006\u0010\u0016R\u0014\u0010\u0007\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0007\u0010\u0016\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/internal/ui/util/GridLayout;", "", "", "columns", "", "itemWidth", "itemHeight", "gap", "<init>", "(IFFF)V", "itemCount", "contentHeight", "(I)F", "startX", "startY", "", "Lorg/cobalt/internal/ui/UIComponent;", "components", "", "layout", "(FFLjava/util/List;)V", "I", "F", "cobalt"})
@SourceDebugExtension(value={"SMAP\nGridLayout.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GridLayout.kt\norg/cobalt/internal/ui/util/GridLayout\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,36:1\n1924#2,3:37\n*S KotlinDebug\n*F\n+ 1 GridLayout.kt\norg/cobalt/internal/ui/util/GridLayout\n*L\n23#1:37,3\n*E\n"})
public final class GridLayout {
    private final int columns;
    private final float itemWidth;
    private final float itemHeight;
    private final float gap;

    public GridLayout(int columns, float itemWidth, float itemHeight, float gap) {
        this.columns = columns;
        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
        this.gap = gap;
    }

    public final float contentHeight(int itemCount) {
        int rows = (int)Math.ceil((float)itemCount / (float)this.columns);
        return (float)rows * (this.itemHeight + this.gap);
    }

    /*
     * WARNING - void declaration
     */
    public final void layout(float startX, float startY, @NotNull List<? extends UIComponent> components) {
        Intrinsics.checkNotNullParameter(components, (String)"components");
        Iterable $this$forEachIndexed$iv = components;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void component;
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            UIComponent uIComponent = (UIComponent)item$iv;
            int index = n;
            boolean bl = false;
            int col = index % this.columns;
            int row = index / this.columns;
            float x = startX + (float)col * (this.itemWidth + this.gap);
            float y = startY + (float)row * (this.itemHeight + this.gap);
            component.updateBounds(x, y);
        }
    }
}

