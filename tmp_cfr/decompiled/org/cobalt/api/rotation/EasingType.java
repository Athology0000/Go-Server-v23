/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.rotation;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b%\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u001d\b\u0002\u0012\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J%\u0010\n\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\u0003\u00a2\u0006\u0004\b\n\u0010\u000bR#\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\f\u001a\u0004\b\r\u0010\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015j\u0002\b\u0016j\u0002\b\u0017j\u0002\b\u0018j\u0002\b\u0019j\u0002\b\u001aj\u0002\b\u001bj\u0002\b\u001cj\u0002\b\u001dj\u0002\b\u001ej\u0002\b\u001fj\u0002\b j\u0002\b!j\u0002\b\"j\u0002\b#j\u0002\b$j\u0002\b%j\u0002\b&j\u0002\b'\u00a8\u0006("}, d2={"Lorg/cobalt/api/rotation/EasingType;", "", "Lkotlin/Function1;", "", "ease", "<init>", "(Ljava/lang/String;ILkotlin/jvm/functions/Function1;)V", "from", "to", "progress", "apply", "(FFF)F", "Lkotlin/jvm/functions/Function1;", "getEase", "()Lkotlin/jvm/functions/Function1;", "LINEAR", "EASE_IN_SINE", "EASE_OUT_SINE", "EASE_IN_OUT_SINE", "EASE_IN_QUAD", "EASE_OUT_QUAD", "EASE_IN_OUT_QUAD", "EASE_IN_CUBIC", "EASE_OUT_CUBIC", "EASE_IN_OUT_CUBIC", "EASE_IN_QUART", "EASE_OUT_QUART", "EASE_IN_OUT_QUART", "EASE_IN_QUINT", "EASE_OUT_QUINT", "EASE_IN_OUT_QUINT", "EASE_IN_EXPO", "EASE_OUT_EXPO", "EASE_IN_OUT_EXPO", "EASE_IN_CIRC", "EASE_OUT_CIRC", "EASE_IN_OUT_CIRC", "EASE_IN_BACK", "EASE_OUT_BACK", "EASE_IN_OUT_BACK", "cobalt"})
@SourceDebugExtension(value={"SMAP\nEasingType.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EasingType.kt\norg/cobalt/api/rotation/EasingType\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,86:1\n1#2:87\n*E\n"})
public final class EasingType
extends Enum<EasingType> {
    @NotNull
    private final Function1<Float, Float> ease;
    public static final /* enum */ EasingType LINEAR = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$0));
    public static final /* enum */ EasingType EASE_IN_SINE = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$1));
    public static final /* enum */ EasingType EASE_OUT_SINE = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$2));
    public static final /* enum */ EasingType EASE_IN_OUT_SINE = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$3));
    public static final /* enum */ EasingType EASE_IN_QUAD = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$4));
    public static final /* enum */ EasingType EASE_OUT_QUAD = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$5));
    public static final /* enum */ EasingType EASE_IN_OUT_QUAD = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$6));
    public static final /* enum */ EasingType EASE_IN_CUBIC = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$7));
    public static final /* enum */ EasingType EASE_OUT_CUBIC = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$8));
    public static final /* enum */ EasingType EASE_IN_OUT_CUBIC = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$9));
    public static final /* enum */ EasingType EASE_IN_QUART = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$10));
    public static final /* enum */ EasingType EASE_OUT_QUART = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$11));
    public static final /* enum */ EasingType EASE_IN_OUT_QUART = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$12));
    public static final /* enum */ EasingType EASE_IN_QUINT = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$13));
    public static final /* enum */ EasingType EASE_OUT_QUINT = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$14));
    public static final /* enum */ EasingType EASE_IN_OUT_QUINT = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$15));
    public static final /* enum */ EasingType EASE_IN_EXPO = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$16));
    public static final /* enum */ EasingType EASE_OUT_EXPO = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$17));
    public static final /* enum */ EasingType EASE_IN_OUT_EXPO = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$18));
    public static final /* enum */ EasingType EASE_IN_CIRC = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$19));
    public static final /* enum */ EasingType EASE_OUT_CIRC = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$20));
    public static final /* enum */ EasingType EASE_IN_OUT_CIRC = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$21));
    public static final /* enum */ EasingType EASE_IN_BACK = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$22));
    public static final /* enum */ EasingType EASE_OUT_BACK = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$23));
    public static final /* enum */ EasingType EASE_IN_OUT_BACK = new EasingType((Function1<? super Float, Float>)((Function1)EasingType::_init_$lambda$24));
    private static final /* synthetic */ EasingType[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    private EasingType(Function1<? super Float, Float> ease) {
        this.ease = ease;
    }

    @NotNull
    public final Function1<Float, Float> getEase() {
        return this.ease;
    }

    public final float apply(float from, float to, float progress) {
        float t = RangesKt.coerceIn((float)progress, (float)0.0f, (float)1.0f);
        return from + (to - from) * ((Number)this.ease.invoke((Object)Float.valueOf(t))).floatValue();
    }

    public static EasingType[] values() {
        return (EasingType[])$VALUES.clone();
    }

    public static EasingType valueOf(String value) {
        return Enum.valueOf(EasingType.class, value);
    }

    @NotNull
    public static EnumEntries<EasingType> getEntries() {
        return $ENTRIES;
    }

    private static final float _init_$lambda$0(float it) {
        return it;
    }

    private static final float _init_$lambda$1(float t) {
        return (float)(1.0 - Math.cos((double)t * Math.PI / (double)2));
    }

    private static final float _init_$lambda$2(float t) {
        return (float)Math.sin((double)t * Math.PI / (double)2);
    }

    private static final float _init_$lambda$3(float t) {
        return (float)((double)-0.5f * (Math.cos(Math.PI * (double)t) - 1.0));
    }

    private static final float _init_$lambda$4(float t) {
        return t * t;
    }

    private static final float _init_$lambda$5(float t) {
        return t * ((float)2 - t);
    }

    private static final float _init_$lambda$6(float t) {
        return t < 0.5f ? (float)2 * t * t : (float)-1 + ((float)4 - (float)2 * t) * t;
    }

    private static final float _init_$lambda$7(float t) {
        return t * t * t;
    }

    private static final float _init_$lambda$8(float t) {
        float it = t - 1.0f;
        boolean bl = false;
        return it * it * it + 1.0f;
    }

    private static final float _init_$lambda$9(float t) {
        return t < 0.5f ? (float)4 * t * t * t : (t - 1.0f) * ((float)2 * t - (float)2) * ((float)2 * t - (float)2) + 1.0f;
    }

    private static final float _init_$lambda$10(float t) {
        return t * t * t * t;
    }

    private static final float _init_$lambda$11(float t) {
        float it = t - 1.0f;
        float f = 1.0f;
        boolean bl = false;
        float f2 = it * it * it * it;
        return f - f2;
    }

    /*
     * WARNING - void declaration
     */
    private static final float _init_$lambda$12(float t) {
        float f;
        if (t < 0.5f) {
            f = (float)8 * t * t * t * t;
        } else {
            void it;
            float f2 = t - 1.0f;
            float f3 = 8;
            float f4 = 1.0f;
            boolean bl = false;
            void var5_5 = it * it * it * it;
            f = f4 - f3 * var5_5;
        }
        return f;
    }

    private static final float _init_$lambda$13(float t) {
        return t * t * t * t * t;
    }

    private static final float _init_$lambda$14(float t) {
        float it = t - 1.0f;
        float f = 1.0f;
        boolean bl = false;
        float f2 = it * it * it * it * it;
        return f + f2;
    }

    /*
     * WARNING - void declaration
     */
    private static final float _init_$lambda$15(float t) {
        float f;
        if (t < 0.5f) {
            f = (float)16 * t * t * t * t * t;
        } else {
            void it;
            float f2 = t - 1.0f;
            float f3 = 16;
            float f4 = 1.0f;
            boolean bl = false;
            void var5_5 = it * it * it * it * it;
            f = f4 + f3 * var5_5;
        }
        return f;
    }

    private static final float _init_$lambda$16(float t) {
        return t == 0.0f ? 0.0f : (float)Math.pow(2.0, 10.0 * (double)(t - 1.0f));
    }

    private static final float _init_$lambda$17(float t) {
        return t == 1.0f ? 1.0f : 1.0f - (float)Math.pow(2.0, -10.0 * (double)t);
    }

    private static final float _init_$lambda$18(float t) {
        return t == 0.0f ? 0.0f : (t == 1.0f ? 1.0f : (t < 0.5f ? (float)(Math.pow(2.0, 20.0 * (double)t - 10.0) / (double)2) : (float)((double)2 - Math.pow(2.0, -20.0 * (double)t + 10.0) / (double)2)));
    }

    private static final float _init_$lambda$19(float t) {
        return 1.0f - (float)Math.sqrt(1.0f - t * t);
    }

    private static final float _init_$lambda$20(float t) {
        float it = t - 1.0f;
        float f = 1.0f;
        boolean bl = false;
        float f2 = it * it;
        return (float)Math.sqrt(f - f2);
    }

    /*
     * WARNING - void declaration
     */
    private static final float _init_$lambda$21(float t) {
        float f;
        if (t < 0.5f) {
            void it;
            float f2 = (float)2 * t;
            float f3 = 1.0f;
            float f4 = 1.0f;
            boolean bl = false;
            void var5_9 = it * it;
            f = (f4 - (float)Math.sqrt(f3 - var5_9)) / (float)2;
        } else {
            float it = (float)-2 * t + (float)2;
            float f5 = 1.0f;
            boolean bl = false;
            float f6 = it * it;
            f = ((float)Math.sqrt(f5 - f6) + 1.0f) / (float)2;
        }
        return f;
    }

    private static final float _init_$lambda$22(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        return c3 * t * t * t - c1 * t * t;
    }

    private static final float _init_$lambda$23(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        float x = t - 1.0f;
        return 1.0f + c3 * x * x * x + c1 * x * x;
    }

    private static final float _init_$lambda$24(float t) {
        float f;
        float c1 = 1.70158f;
        float c2 = c1 * 1.525f;
        if (t < 0.5f) {
            float k = (float)2 * t;
            f = k * k * ((c2 + 1.0f) * k - c2) / (float)2;
        } else {
            float k = (float)2 * t - (float)2;
            f = (k * k * ((c2 + 1.0f) * k + c2) + (float)2) / (float)2;
        }
        return f;
    }

    static {
        $VALUES = easingTypeArray = new EasingType[]{EasingType.LINEAR, EasingType.EASE_IN_SINE, EasingType.EASE_OUT_SINE, EasingType.EASE_IN_OUT_SINE, EasingType.EASE_IN_QUAD, EasingType.EASE_OUT_QUAD, EasingType.EASE_IN_OUT_QUAD, EasingType.EASE_IN_CUBIC, EasingType.EASE_OUT_CUBIC, EasingType.EASE_IN_OUT_CUBIC, EasingType.EASE_IN_QUART, EasingType.EASE_OUT_QUART, EasingType.EASE_IN_OUT_QUART, EasingType.EASE_IN_QUINT, EasingType.EASE_OUT_QUINT, EasingType.EASE_IN_OUT_QUINT, EasingType.EASE_IN_EXPO, EasingType.EASE_OUT_EXPO, EasingType.EASE_IN_OUT_EXPO, EasingType.EASE_IN_CIRC, EasingType.EASE_OUT_CIRC, EasingType.EASE_IN_OUT_CIRC, EasingType.EASE_IN_BACK, EasingType.EASE_OUT_BACK, EasingType.EASE_IN_OUT_BACK};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}

