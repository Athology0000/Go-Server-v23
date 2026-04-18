/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.math.MathKt
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.animation;

import java.awt.Color;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u000e\b\u0000\u0018\u00002\u00020\u0001B\u001b\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\r\u0010\t\u001a\u00020\b\u00a2\u0006\u0004\b\t\u0010\nJ\r\u0010\u000b\u001a\u00020\b\u00a2\u0006\u0004\b\u000b\u0010\nJ\r\u0010\r\u001a\u00020\f\u00a2\u0006\u0004\b\r\u0010\u000eJ\r\u0010\u000f\u001a\u00020\u0004\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0015\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013J/\u0010\u0018\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u00142\u0006\u0010\u0011\u001a\u00020\u00042\b\b\u0002\u0010\u0017\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0019J/\u0010\u0018\u001a\u00020\u001a2\u0006\u0010\u0015\u001a\u00020\u001a2\u0006\u0010\u0016\u001a\u00020\u001a2\u0006\u0010\u0011\u001a\u00020\u00042\b\b\u0002\u0010\u0017\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0018\u0010\u001bJ\u0017\u0010\u001d\u001a\u00020\u00042\u0006\u0010\u001c\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u001d\u0010\u0013J'\u0010 \u001a\u00020\u001a2\u0006\u0010\t\u001a\u00020\u001a2\u0006\u0010\u001e\u001a\u00020\u001a2\u0006\u0010\u001f\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b \u0010!R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\"R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010#R\u0014\u0010$\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b$\u0010#R\u0016\u0010%\u001a\u00020\u00028\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b%\u0010\"R\u0016\u0010&\u001a\u00020\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b&\u0010'\u00a8\u0006("}, d2={"Lorg/cobalt/internal/ui/animation/GlowShimmerAnimation;", "", "", "cycleMs", "", "shimmerWidth", "<init>", "(JF)V", "", "start", "()V", "stop", "", "isRunning", "()Z", "progress", "()F", "normalizedX", "strengthAt", "(F)F", "Ljava/awt/Color;", "base", "shimmer", "amount", "colorAt", "(Ljava/awt/Color;Ljava/awt/Color;FF)Ljava/awt/Color;", "", "(IIFF)I", "x", "smoothstep", "end", "t", "lerp", "(IIF)I", "J", "F", "width", "startTime", "running", "Z", "cobalt"})
public final class GlowShimmerAnimation {
    private final long cycleMs;
    private final float shimmerWidth;
    private final float width;
    private long startTime;
    private boolean running;

    public GlowShimmerAnimation(long cycleMs, float shimmerWidth) {
        this.cycleMs = cycleMs;
        this.shimmerWidth = shimmerWidth;
        this.width = RangesKt.coerceAtLeast((float)this.shimmerWidth, (float)0.001f);
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }

    public /* synthetic */ GlowShimmerAnimation(long l, float f, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 1) != 0) {
            l = 1400L;
        }
        if ((n & 2) != 0) {
            f = 0.22f;
        }
        this(l, f);
    }

    public final void start() {
        this.running = true;
        this.startTime = System.currentTimeMillis();
    }

    public final void stop() {
        this.running = false;
    }

    public final boolean isRunning() {
        return this.running;
    }

    public final float progress() {
        if (!this.running || this.cycleMs <= 0L) {
            return 0.0f;
        }
        long elapsed = System.currentTimeMillis() - this.startTime;
        long mod = (elapsed % this.cycleMs + this.cycleMs) % this.cycleMs;
        return RangesKt.coerceIn((float)((float)mod / (float)this.cycleMs), (float)0.0f, (float)1.0f);
    }

    public final float strengthAt(float normalizedX) {
        if (!this.running) {
            return 0.0f;
        }
        float x = RangesKt.coerceIn((float)normalizedX, (float)0.0f, (float)1.0f);
        float center = this.progress();
        float directDist = Math.abs(x - center);
        float wrappedDist = Math.min(directDist, 1.0f - directDist);
        float local = RangesKt.coerceIn((float)((this.width - wrappedDist) / this.width), (float)0.0f, (float)1.0f);
        return this.smoothstep(local);
    }

    @NotNull
    public final Color colorAt(@NotNull Color base, @NotNull Color shimmer, float normalizedX, float amount) {
        Intrinsics.checkNotNullParameter((Object)base, (String)"base");
        Intrinsics.checkNotNullParameter((Object)shimmer, (String)"shimmer");
        float t = RangesKt.coerceIn((float)(this.strengthAt(normalizedX) * amount), (float)0.0f, (float)1.0f);
        return new Color(this.lerp(base.getRed(), shimmer.getRed(), t), this.lerp(base.getGreen(), shimmer.getGreen(), t), this.lerp(base.getBlue(), shimmer.getBlue(), t), this.lerp(base.getAlpha(), shimmer.getAlpha(), t));
    }

    public static /* synthetic */ Color colorAt$default(GlowShimmerAnimation glowShimmerAnimation, Color color, Color color2, float f, float f2, int n, Object object) {
        if ((n & 8) != 0) {
            f2 = 1.0f;
        }
        return glowShimmerAnimation.colorAt(color, color2, f, f2);
    }

    public final int colorAt(int base, int shimmer, float normalizedX, float amount) {
        Color color = this.colorAt(new Color(base, true), new Color(shimmer, true), normalizedX, amount);
        return color.getAlpha() << 24 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
    }

    public static /* synthetic */ int colorAt$default(GlowShimmerAnimation glowShimmerAnimation, int n, int n2, float f, float f2, int n3, Object object) {
        if ((n3 & 8) != 0) {
            f2 = 1.0f;
        }
        return glowShimmerAnimation.colorAt(n, n2, f, f2);
    }

    private final float smoothstep(float x) {
        return x * x * (3.0f - 2.0f * x);
    }

    private final int lerp(int start, int end, float t) {
        return RangesKt.coerceIn((int)MathKt.roundToInt((float)((float)start + (float)(end - start) * t)), (int)0, (int)255);
    }

    public GlowShimmerAnimation() {
        this(0L, 0.0f, 3, null);
    }
}

