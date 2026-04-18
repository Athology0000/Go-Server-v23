/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.animation;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function1;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0002\b!\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u001d\b\u0002\u0012\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0015\u0010\b\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\u0004\b\b\u0010\tR \u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0004\u0010\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015j\u0002\b\u0016j\u0002\b\u0017j\u0002\b\u0018j\u0002\b\u0019j\u0002\b\u001aj\u0002\b\u001bj\u0002\b\u001cj\u0002\b\u001dj\u0002\b\u001ej\u0002\b\u001fj\u0002\b j\u0002\b!j\u0002\b\"j\u0002\b#\u00a8\u0006$"}, d2={"Lorg/cobalt/internal/ui/animation/RiseEasing;", "", "Lkotlin/Function1;", "", "easing", "<init>", "(Ljava/lang/String;ILkotlin/jvm/functions/Function1;)V", "progress", "apply", "(D)D", "Lkotlin/jvm/functions/Function1;", "LINEAR", "EASE_IN_QUAD", "EASE_OUT_QUAD", "EASE_IN_OUT_QUAD", "EASE_IN_CUBIC", "EASE_OUT_CUBIC", "EASE_IN_OUT_CUBIC", "EASE_IN_QUART", "EASE_OUT_QUART", "EASE_IN_OUT_QUART", "EASE_IN_QUINT", "EASE_OUT_QUINT", "EASE_IN_OUT_QUINT", "EASE_IN_SINE", "EASE_OUT_SINE", "EASE_IN_OUT_SINE", "EASE_IN_EXPO", "EASE_OUT_EXPO", "EASE_IN_OUT_EXPO", "EASE_IN_CIRC", "EASE_OUT_CIRC", "EASE_IN_OUT_CIRC", "SIGMOID", "EASE_OUT_ELASTIC", "EASE_IN_BACK", "cobalt"})
public final class RiseEasing
extends Enum<RiseEasing> {
    @NotNull
    private final Function1<Double, Double> easing;
    public static final /* enum */ RiseEasing LINEAR = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$0));
    public static final /* enum */ RiseEasing EASE_IN_QUAD = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$1));
    public static final /* enum */ RiseEasing EASE_OUT_QUAD = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$2));
    public static final /* enum */ RiseEasing EASE_IN_OUT_QUAD = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$3));
    public static final /* enum */ RiseEasing EASE_IN_CUBIC = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$4));
    public static final /* enum */ RiseEasing EASE_OUT_CUBIC = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$5));
    public static final /* enum */ RiseEasing EASE_IN_OUT_CUBIC = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$6));
    public static final /* enum */ RiseEasing EASE_IN_QUART = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$7));
    public static final /* enum */ RiseEasing EASE_OUT_QUART = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$8));
    public static final /* enum */ RiseEasing EASE_IN_OUT_QUART = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$9));
    public static final /* enum */ RiseEasing EASE_IN_QUINT = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$10));
    public static final /* enum */ RiseEasing EASE_OUT_QUINT = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$11));
    public static final /* enum */ RiseEasing EASE_IN_OUT_QUINT = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$12));
    public static final /* enum */ RiseEasing EASE_IN_SINE = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$13));
    public static final /* enum */ RiseEasing EASE_OUT_SINE = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$14));
    public static final /* enum */ RiseEasing EASE_IN_OUT_SINE = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$15));
    public static final /* enum */ RiseEasing EASE_IN_EXPO = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$16));
    public static final /* enum */ RiseEasing EASE_OUT_EXPO = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$17));
    public static final /* enum */ RiseEasing EASE_IN_OUT_EXPO = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$18));
    public static final /* enum */ RiseEasing EASE_IN_CIRC = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$19));
    public static final /* enum */ RiseEasing EASE_OUT_CIRC = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$20));
    public static final /* enum */ RiseEasing EASE_IN_OUT_CIRC = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$21));
    public static final /* enum */ RiseEasing SIGMOID = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$22));
    public static final /* enum */ RiseEasing EASE_OUT_ELASTIC = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$23));
    public static final /* enum */ RiseEasing EASE_IN_BACK = new RiseEasing((Function1<? super Double, Double>)((Function1)RiseEasing::_init_$lambda$24));
    private static final /* synthetic */ RiseEasing[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    private RiseEasing(Function1<? super Double, Double> easing) {
        this.easing = easing;
    }

    public final double apply(double progress) {
        return ((Number)this.easing.invoke((Object)RangesKt.coerceIn((double)progress, (double)0.0, (double)1.0))).doubleValue();
    }

    public static RiseEasing[] values() {
        return (RiseEasing[])$VALUES.clone();
    }

    public static RiseEasing valueOf(String value) {
        return Enum.valueOf(RiseEasing.class, value);
    }

    @NotNull
    public static EnumEntries<RiseEasing> getEntries() {
        return $ENTRIES;
    }

    private static final double _init_$lambda$0(double it) {
        return it;
    }

    private static final double _init_$lambda$1(double it) {
        return it * it;
    }

    private static final double _init_$lambda$2(double it) {
        return 1.0 - (1.0 - it) * (1.0 - it);
    }

    private static final double _init_$lambda$3(double t) {
        return t < 0.5 ? 2.0 * t * t : 1.0 - Math.pow(-2.0 * t + 2.0, 2.0) / 2.0;
    }

    private static final double _init_$lambda$4(double it) {
        return it * it * it;
    }

    private static final double _init_$lambda$5(double it) {
        return 1.0 - Math.pow(1.0 - it, 3.0);
    }

    private static final double _init_$lambda$6(double t) {
        return t < 0.5 ? 4.0 * t * t * t : 1.0 - Math.pow(-2.0 * t + 2.0, 3.0) / 2.0;
    }

    private static final double _init_$lambda$7(double it) {
        return Math.pow(it, 4.0);
    }

    private static final double _init_$lambda$8(double it) {
        return 1.0 - Math.pow(1.0 - it, 4.0);
    }

    private static final double _init_$lambda$9(double t) {
        return t < 0.5 ? 8.0 * Math.pow(t, 4.0) : 1.0 - Math.pow(-2.0 * t + 2.0, 4.0) / 2.0;
    }

    private static final double _init_$lambda$10(double it) {
        return Math.pow(it, 5.0);
    }

    private static final double _init_$lambda$11(double it) {
        return 1.0 - Math.pow(1.0 - it, 5.0);
    }

    private static final double _init_$lambda$12(double t) {
        return t < 0.5 ? 16.0 * Math.pow(t, 5.0) : 1.0 - Math.pow(-2.0 * t + 2.0, 5.0) / 2.0;
    }

    private static final double _init_$lambda$13(double it) {
        return 1.0 - Math.cos(it * Math.PI / 2.0);
    }

    private static final double _init_$lambda$14(double it) {
        return Math.sin(it * Math.PI / 2.0);
    }

    private static final double _init_$lambda$15(double it) {
        return -(Math.cos(Math.PI * it) - 1.0) / 2.0;
    }

    private static final double _init_$lambda$16(double t) {
        return t == 0.0 ? 0.0 : Math.pow(2.0, 10.0 * t - 10.0);
    }

    private static final double _init_$lambda$17(double t) {
        return t == 1.0 ? 1.0 : 1.0 - Math.pow(2.0, -10.0 * t);
    }

    private static final double _init_$lambda$18(double t) {
        return t == 0.0 ? 0.0 : (t == 1.0 ? 1.0 : (t < 0.5 ? Math.pow(2.0, 20.0 * t - 10.0) / 2.0 : (2.0 - Math.pow(2.0, -20.0 * t + 10.0)) / 2.0));
    }

    private static final double _init_$lambda$19(double it) {
        return 1.0 - Math.sqrt(1.0 - it * it);
    }

    private static final double _init_$lambda$20(double it) {
        return Math.sqrt(1.0 - (it - 1.0) * (it - 1.0));
    }

    private static final double _init_$lambda$21(double t) {
        return t < 0.5 ? (1.0 - Math.sqrt(1.0 - Math.pow(2.0 * t, 2.0))) / 2.0 : (Math.sqrt(1.0 - Math.pow(-2.0 * t + 2.0, 2.0)) + 1.0) / 2.0;
    }

    private static final double _init_$lambda$22(double t) {
        return 1.0 / (1.0 + Math.exp(-12.0 * (t - 0.5)));
    }

    private static final double _init_$lambda$23(double t) {
        return t == 0.0 || t == 1.0 ? t : Math.pow(2.0, -10.0 * t) * Math.sin((t * 10.0 - 0.75) * 2.0943951023931953) + 1.0;
    }

    private static final double _init_$lambda$24(double t) {
        double c1 = 1.70158;
        double c3 = c1 + 1.0;
        return c3 * t * t * t - c1 * t * t;
    }

    static {
        $VALUES = riseEasingArray = new RiseEasing[]{RiseEasing.LINEAR, RiseEasing.EASE_IN_QUAD, RiseEasing.EASE_OUT_QUAD, RiseEasing.EASE_IN_OUT_QUAD, RiseEasing.EASE_IN_CUBIC, RiseEasing.EASE_OUT_CUBIC, RiseEasing.EASE_IN_OUT_CUBIC, RiseEasing.EASE_IN_QUART, RiseEasing.EASE_OUT_QUART, RiseEasing.EASE_IN_OUT_QUART, RiseEasing.EASE_IN_QUINT, RiseEasing.EASE_OUT_QUINT, RiseEasing.EASE_IN_OUT_QUINT, RiseEasing.EASE_IN_SINE, RiseEasing.EASE_OUT_SINE, RiseEasing.EASE_IN_OUT_SINE, RiseEasing.EASE_IN_EXPO, RiseEasing.EASE_OUT_EXPO, RiseEasing.EASE_IN_OUT_EXPO, RiseEasing.EASE_IN_CIRC, RiseEasing.EASE_OUT_CIRC, RiseEasing.EASE_IN_OUT_CIRC, RiseEasing.SIGMOID, RiseEasing.EASE_OUT_ELASTIC, RiseEasing.EASE_IN_BACK};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}

