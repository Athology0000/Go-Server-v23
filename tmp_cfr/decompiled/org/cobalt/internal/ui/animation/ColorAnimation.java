/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.math.MathKt
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.animation;

import java.awt.Color;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;
import org.cobalt.internal.ui.animation.EaseOutAnimation;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\r\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0007\u0010\bJ%\u0010\r\u001a\u00020\t2\u0006\u0010\u0007\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\u000b\u00a2\u0006\u0004\b\r\u0010\u000eJ%\u0010\r\u001a\u00020\u000f2\u0006\u0010\u0007\u001a\u00020\u000f2\u0006\u0010\n\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\u000b\u00a2\u0006\u0004\b\r\u0010\u0010R\u0014\u0010\u0012\u001a\u00020\u00118\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0013\u00a8\u0006\u0014"}, d2={"Lorg/cobalt/internal/ui/animation/ColorAnimation;", "", "", "duration", "<init>", "(J)V", "", "start", "()V", "Ljava/awt/Color;", "end", "", "reverse", "get", "(Ljava/awt/Color;Ljava/awt/Color;Z)Ljava/awt/Color;", "", "(IIZ)I", "Lorg/cobalt/internal/ui/animation/EaseOutAnimation;", "anim", "Lorg/cobalt/internal/ui/animation/EaseOutAnimation;", "cobalt"})
public final class ColorAnimation {
    @NotNull
    private final EaseOutAnimation anim;

    public ColorAnimation(long duration) {
        this.anim = new EaseOutAnimation(duration);
    }

    public final void start() {
        this.anim.start();
    }

    @NotNull
    public final Color get(@NotNull Color start, @NotNull Color end, boolean reverse) {
        Intrinsics.checkNotNullParameter((Object)start, (String)"start");
        Intrinsics.checkNotNullParameter((Object)end, (String)"end");
        return new Color(this.anim.get(start.getRed(), end.getRed(), reverse).floatValue() / (float)255, this.anim.get(start.getGreen(), end.getGreen(), reverse).floatValue() / (float)255, this.anim.get(start.getBlue(), end.getBlue(), reverse).floatValue() / (float)255, this.anim.get(start.getAlpha(), end.getAlpha(), reverse).floatValue() / (float)255);
    }

    public final int get(int start, int end, boolean reverse) {
        Color startColor = new Color(start, true);
        Color endColor = new Color(end, true);
        int red = RangesKt.coerceIn((int)MathKt.roundToInt((float)this.anim.get(startColor.getRed(), endColor.getRed(), reverse).floatValue()), (int)0, (int)255);
        int green = RangesKt.coerceIn((int)MathKt.roundToInt((float)this.anim.get(startColor.getGreen(), endColor.getGreen(), reverse).floatValue()), (int)0, (int)255);
        int blue = RangesKt.coerceIn((int)MathKt.roundToInt((float)this.anim.get(startColor.getBlue(), endColor.getBlue(), reverse).floatValue()), (int)0, (int)255);
        int alpha = RangesKt.coerceIn((int)MathKt.roundToInt((float)this.anim.get(startColor.getAlpha(), endColor.getAlpha(), reverse).floatValue()), (int)0, (int)255);
        return alpha << 24 | red << 16 | green << 8 | blue;
    }
}

