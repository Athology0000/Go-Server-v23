/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.animation;

import kotlin.Metadata;
import org.cobalt.internal.ui.animation.Animation;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u000f\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0005\u0010\u0006J'\u0010\u000b\u001a\u00020\u00022\u0006\u0010\u0007\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010\n\u001a\u00020\tH\u0016\u00a2\u0006\u0004\b\u000b\u0010\fJ\u000f\u0010\r\u001a\u00020\u0002H\u0002\u00a2\u0006\u0004\b\r\u0010\u000e\u00a8\u0006\u000f"}, d2={"Lorg/cobalt/internal/ui/animation/BounceAnimation;", "Lorg/cobalt/internal/ui/animation/Animation;", "", "", "duration", "<init>", "(J)V", "start", "end", "", "reverse", "get", "(FFZ)Ljava/lang/Float;", "ease", "()F", "cobalt"})
public final class BounceAnimation
extends Animation<Float> {
    public BounceAnimation(long duration) {
        super(duration);
    }

    @Override
    @NotNull
    public Float get(float start, float end, boolean reverse) {
        if (!this.isAnimating()) {
            return Float.valueOf(reverse ? start : end);
        }
        return Float.valueOf(reverse ? end + (start - end) * this.ease() : start + (end - start) * this.ease());
    }

    private final float ease() {
        float f;
        float x = this.getPercent() / 100.0f;
        if (x < 0.3f) {
            float t = x / 0.3f;
            float easeOut = 1.0f - (float)Math.pow(1.0f - t, 3);
            f = easeOut * 1.05f;
        } else {
            float t = (x - 0.3f) / 0.7f;
            float easeOut = 1.0f - (float)Math.pow(1.0f - t, 2);
            f = 1.05f - 0.05f * easeOut;
        }
        return f;
    }
}

