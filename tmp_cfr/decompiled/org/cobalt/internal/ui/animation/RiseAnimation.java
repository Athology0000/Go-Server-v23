/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.animation;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import org.cobalt.internal.ui.animation.RiseEasing;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\f\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J%\u0010\f\u001a\u00020\u000b2\u0006\u0010\u0006\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\t\u00a2\u0006\u0004\b\f\u0010\rJ\u0015\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\u0006\u001a\u00020\u0002\u00a2\u0006\u0004\b\u000e\u0010\u0005J\r\u0010\u000f\u001a\u00020\u0002\u00a2\u0006\u0004\b\u000f\u0010\u0010J\r\u0010\u0012\u001a\u00020\u0011\u00a2\u0006\u0004\b\u0012\u0010\u0013R\u0016\u0010\u0014\u001a\u00020\u00028\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0014\u0010\u0015R\u0016\u0010\u0016\u001a\u00020\u00028\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0016\u0010\u0015R\u0016\u0010\u0017\u001a\u00020\u00028\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0017\u0010\u0015R\u0016\u0010\u0018\u001a\u00020\u00078\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0019R\u0016\u0010\b\u001a\u00020\u00078\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\b\u0010\u0019R\u0016\u0010\n\u001a\u00020\t8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\n\u0010\u001aR\u0016\u0010\u001b\u001a\u00020\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u001c\u00a8\u0006\u001d"}, d2={"Lorg/cobalt/internal/ui/animation/RiseAnimation;", "", "", "initialValue", "<init>", "(D)V", "target", "", "durationMillis", "Lorg/cobalt/internal/ui/animation/RiseEasing;", "easing", "", "run", "(DJLorg/cobalt/internal/ui/animation/RiseEasing;)V", "snap", "getValue", "()D", "", "isFinished", "()Z", "from", "D", "to", "value", "startTime", "J", "Lorg/cobalt/internal/ui/animation/RiseEasing;", "finished", "Z", "cobalt"})
public final class RiseAnimation {
    private double from;
    private double to;
    private double value;
    private long startTime;
    private long durationMillis;
    @NotNull
    private RiseEasing easing;
    private boolean finished;

    public RiseAnimation(double initialValue) {
        this.from = initialValue;
        this.to = initialValue;
        this.value = initialValue;
        this.durationMillis = 1L;
        this.easing = RiseEasing.LINEAR;
        this.finished = true;
    }

    public /* synthetic */ RiseAnimation(double d, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 1) != 0) {
            d = 0.0;
        }
        this(d);
    }

    public final void run(double target, long durationMillis, @NotNull RiseEasing easing) {
        Intrinsics.checkNotNullParameter((Object)((Object)easing), (String)"easing");
        double current = this.getValue();
        if (!this.finished && this.to == target && this.durationMillis == durationMillis && this.easing == easing) {
            return;
        }
        this.from = current;
        this.to = target;
        this.durationMillis = RangesKt.coerceAtLeast((long)durationMillis, (long)1L);
        this.easing = easing;
        this.startTime = System.currentTimeMillis();
        this.finished = false;
    }

    public final void snap(double target) {
        this.from = target;
        this.to = target;
        this.value = target;
        this.finished = true;
    }

    public final double getValue() {
        if (this.finished) {
            return this.value;
        }
        long elapsed = RangesKt.coerceAtLeast((long)(System.currentTimeMillis() - this.startTime), (long)0L);
        double progress = RangesKt.coerceIn((double)((double)elapsed / (double)this.durationMillis), (double)0.0, (double)1.0);
        this.value = this.from + (this.to - this.from) * this.easing.apply(progress);
        if (progress >= 1.0) {
            this.value = this.to;
            this.finished = true;
        }
        return this.value;
    }

    public final boolean isFinished() {
        this.getValue();
        return this.finished;
    }

    public RiseAnimation() {
        this(0.0, 1, null);
    }
}

