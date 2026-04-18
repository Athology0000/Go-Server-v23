/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.ranges.RangesKt
 */
package org.cobalt.internal.ui.util;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.ranges.RangesKt;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\t\b\u0000\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0015\u0010\t\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\t\u0010\nJ\u001d\u0010\r\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\u00022\u0006\u0010\f\u001a\u00020\u0002\u00a2\u0006\u0004\b\r\u0010\u000eJ\r\u0010\u000f\u001a\u00020\u0002\u00a2\u0006\u0004\b\u000f\u0010\u0010J\r\u0010\u0012\u001a\u00020\u0011\u00a2\u0006\u0004\b\u0012\u0010\u0013J\r\u0010\u0014\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0014\u0010\u0010J\r\u0010\u0015\u001a\u00020\b\u00a2\u0006\u0004\b\u0015\u0010\u0016R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u0017R\u0016\u0010\u0018\u001a\u00020\u00028\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0017R\u0016\u0010\u0019\u001a\u00020\u00028\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u0017\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/internal/ui/util/ScrollHandler;", "", "", "scrollSpeed", "<init>", "(F)V", "", "amount", "", "handleScroll", "(D)V", "contentHeight", "visibleHeight", "setMaxScroll", "(FF)V", "getOffset", "()F", "", "isScrollable", "()Z", "getMaxScroll", "reset", "()V", "F", "scroll", "maxScroll", "cobalt"})
public final class ScrollHandler {
    private final float scrollSpeed;
    private float scroll;
    private float maxScroll;

    public ScrollHandler(float scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public /* synthetic */ ScrollHandler(float f, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 1) != 0) {
            f = 20.0f;
        }
        this(f);
    }

    public final void handleScroll(double amount) {
        this.scroll -= (float)(amount * (double)this.scrollSpeed);
        this.scroll = RangesKt.coerceIn((float)this.scroll, (float)0.0f, (float)this.maxScroll);
    }

    public final void setMaxScroll(float contentHeight, float visibleHeight) {
        this.maxScroll = Math.max(0.0f, contentHeight - visibleHeight);
        this.scroll = RangesKt.coerceIn((float)this.scroll, (float)0.0f, (float)this.maxScroll);
    }

    public final float getOffset() {
        return this.scroll;
    }

    public final boolean isScrollable() {
        return this.maxScroll > 0.0f;
    }

    public final float getMaxScroll() {
        return this.maxScroll;
    }

    public final void reset() {
        this.scroll = 0.0f;
    }

    public ScrollHandler() {
        this(0.0f, 1, null);
    }
}

