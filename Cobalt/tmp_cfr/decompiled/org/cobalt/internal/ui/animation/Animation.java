/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.ranges.RangesKt
 */
package org.cobalt.internal.ui.animation;

import kotlin.Metadata;
import kotlin.ranges.RangesKt;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\f\b \u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002B\u000f\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0005\u0010\u0006J\r\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\r\u0010\u000b\u001a\u00020\n\u00a2\u0006\u0004\b\u000b\u0010\fJ\r\u0010\u000e\u001a\u00020\r\u00a2\u0006\u0004\b\u000e\u0010\u000fJ)\u0010\u0012\u001a\u00028\u00002\u0006\u0010\b\u001a\u00028\u00002\u0006\u0010\u0010\u001a\u00028\u00002\b\b\u0002\u0010\u0011\u001a\u00020\rH&\u00a2\u0006\u0004\b\u0012\u0010\u0013R\u0014\u0010\u0004\u001a\u00020\u00038\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0004\u0010\u0014R\u0016\u0010\u0015\u001a\u00020\u00038\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0015\u0010\u0014R\u0016\u0010\u0016\u001a\u00020\r8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0016\u0010\u0017R\u0016\u0010\u0018\u001a\u00020\r8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0017\u00a8\u0006\u0019"}, d2={"Lorg/cobalt/internal/ui/animation/Animation;", "T", "", "", "duration", "<init>", "(J)V", "", "start", "()V", "", "getPercent", "()F", "", "isAnimating", "()Z", "end", "reverse", "get", "(Ljava/lang/Object;Ljava/lang/Object;Z)Ljava/lang/Object;", "J", "startTime", "animating", "Z", "reversed", "cobalt"})
public abstract class Animation<T> {
    private final long duration;
    private long startTime;
    private boolean animating;
    private boolean reversed;

    public Animation(long duration) {
        this.duration = duration;
    }

    public final void start() {
        long currentTime = System.currentTimeMillis();
        if (!this.animating) {
            this.animating = true;
            this.reversed = false;
            this.startTime = currentTime;
            return;
        }
        float percent = RangesKt.coerceIn((float)((float)(currentTime - this.startTime) / (float)this.duration), (float)0.0f, (float)1.0f);
        this.reversed = !this.reversed;
        this.startTime = currentTime - (long)((1.0f - percent) * (float)this.duration);
    }

    public final float getPercent() {
        if (!this.animating) {
            return 100.0f;
        }
        float percent = (float)(System.currentTimeMillis() - this.startTime) / (float)this.duration * 100.0f;
        if (percent >= 100.0f) {
            this.animating = false;
            return 100.0f;
        }
        return RangesKt.coerceAtMost((float)percent, (float)100.0f);
    }

    public final boolean isAnimating() {
        return this.animating;
    }

    public abstract T get(T var1, T var2, boolean var3);

    public static /* synthetic */ Object get$default(Animation animation, Object object, Object object2, boolean bl, int n, Object object3) {
        if (object3 != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: get");
        }
        if ((n & 4) != 0) {
            bl = false;
        }
        return animation.get(object, object2, bl);
    }
}

