/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.pathfinding;

import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0010\n\u0002\u0010\u0006\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001'B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001f\u0010\b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\b\u0010\tJ\r\u0010\u000b\u001a\u00020\n\u00a2\u0006\u0004\b\u000b\u0010\u0003JG\u0010\u0012\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u00042\b\b\u0002\u0010\r\u001a\u00020\u00042\b\b\u0002\u0010\u000e\u001a\u00020\u00042\b\b\u0002\u0010\u000f\u001a\u00020\u00042\b\b\u0002\u0010\u0010\u001a\u00020\u00042\b\b\u0002\u0010\u0011\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0012\u0010\u0013JG\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00042\b\b\u0002\u0010\r\u001a\u00020\u00042\b\b\u0002\u0010\u000e\u001a\u00020\u00042\b\b\u0002\u0010\u0015\u001a\u00020\u00042\b\b\u0002\u0010\u0016\u001a\u00020\u00042\b\b\u0002\u0010\u0011\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0017\u0010\u0013R\u0014\u0010\u0018\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0019R\u0014\u0010\u001a\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u0019R\u0014\u0010\u001c\u001a\u00020\u001b8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u001dR\u0014\u0010\u001e\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u0019R\u0014\u0010\u001f\u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001f\u0010\u0019R\u0014\u0010 \u001a\u00020\u00048\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b \u0010\u0019R\u0016\u0010!\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b!\u0010\u0019R\u0016\u0010#\u001a\u00020\"8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b#\u0010$R\u0016\u0010%\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b%\u0010\u0019R\u0016\u0010&\u001a\u00020\"8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b&\u0010$\u00a8\u0006("}, d2={"Lorg/cobalt/internal/pathfinding/HeadRotationModule;", "", "<init>", "()V", "", "absDelta", "Lorg/cobalt/internal/pathfinding/HeadRotationModule$EaseMode;", "mode", "easeCurve", "(FLorg/cobalt/internal/pathfinding/HeadRotationModule$EaseMode;)F", "", "resetVelocity", "yawDelta", "maxSpeedScale", "accelScale", "maxTurnSpeed", "maxTurnAccel", "easeMode", "computeTurnDelta", "(FFFFFLorg/cobalt/internal/pathfinding/HeadRotationModule$EaseMode;)F", "pitchDelta", "maxPitchSpeed", "maxPitchAccel", "computePitchDelta", "MAX_TURN_SPEED_PER_SEC", "F", "MAX_TURN_ACCEL_PER_SEC2", "", "TURN_HYPERBOLIC_SCALE", "D", "MIN_DELTA_EPS", "MAX_DT_SEC", "MIN_DT_SEC", "yawVelocity", "", "yawLastTimeNs", "J", "pitchVelocity", "pitchLastTimeNs", "EaseMode", "cobalt"})
@SourceDebugExtension(value={"SMAP\nHeadRotationModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HeadRotationModule.kt\norg/cobalt/internal/pathfinding/HeadRotationModule\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,148:1\n1#2:149\n*E\n"})
public final class HeadRotationModule {
    @NotNull
    public static final HeadRotationModule INSTANCE = new HeadRotationModule();
    private static final float MAX_TURN_SPEED_PER_SEC = 100.0f;
    private static final float MAX_TURN_ACCEL_PER_SEC2 = 220.0f;
    private static final double TURN_HYPERBOLIC_SCALE = 90.0;
    private static final float MIN_DELTA_EPS = 1.0E-4f;
    private static final float MAX_DT_SEC = 0.1f;
    private static final float MIN_DT_SEC = 0.004166667f;
    private static float yawVelocity;
    private static long yawLastTimeNs;
    private static float pitchVelocity;
    private static long pitchLastTimeNs;

    private HeadRotationModule() {
    }

    private final float easeCurve(float absDelta, EaseMode mode) {
        float t = RangesKt.coerceAtLeast((float)(absDelta / 90.0f), (float)0.0f);
        return switch (WhenMappings.$EnumSwitchMapping$0[mode.ordinal()]) {
            case 1 -> (float)Math.tanh(t);
            case 2 -> RangesKt.coerceAtMost((float)t, (float)1.0f);
            case 3 -> {
                float tc = RangesKt.coerceAtMost((float)t, (float)1.0f);
                yield (float)Math.sin((double)tc * 1.5707963267948966);
            }
            case 4 -> {
                float tc = RangesKt.coerceAtMost((float)t, (float)1.0f);
                yield (float)((1.0 - Math.cos((double)tc * Math.PI)) * 0.5);
            }
            case 5 -> {
                void it;
                float tc = RangesKt.coerceAtMost((float)t, (float)1.0f);
                if (tc < 0.5f) {
                    yield 4.0f * tc * tc * tc;
                }
                float var5_7 = -2.0f * tc + 2.0f;
                float var7_8 = 1.0f;
                boolean $i$a$-let-HeadRotationModule$easeCurve$1 = false;
                void var8_10 = it * it * it;
                yield var7_8 - var8_10 / 2.0f;
            }
            default -> throw new NoWhenBranchMatchedException();
        };
    }

    public final void resetVelocity() {
        yawVelocity = 0.0f;
        yawLastTimeNs = 0L;
        pitchVelocity = 0.0f;
        pitchLastTimeNs = 0L;
    }

    public final float computeTurnDelta(float yawDelta, float maxSpeedScale, float accelScale, float maxTurnSpeed, float maxTurnAccel, @NotNull EaseMode easeMode) {
        float f;
        float maxAccel;
        float maxDelta;
        float speedDelta;
        float turn;
        Intrinsics.checkNotNullParameter((Object)((Object)easeMode), (String)"easeMode");
        float absDelta = Math.abs(yawDelta);
        if (absDelta < 1.0E-4f) {
            yawVelocity = 0.0f;
            yawLastTimeNs = System.nanoTime();
            return 0.0f;
        }
        long now = System.nanoTime();
        float dt = yawLastTimeNs == 0L ? 0.05f : RangesKt.coerceIn((float)((float)(now - yawLastTimeNs) / 1.0E9f), (float)0.004166667f, (float)0.1f);
        yawLastTimeNs = now;
        float speedPerSec = maxTurnSpeed * this.easeCurve(absDelta, easeMode) * maxSpeedScale;
        float desiredSpeed = yawDelta >= 0.0f ? speedPerSec : -speedPerSec;
        if (absDelta < Math.abs(turn = (yawVelocity += RangesKt.coerceIn((float)(speedDelta = desiredSpeed - yawVelocity), (float)(-(maxDelta = (maxAccel = maxTurnAccel * accelScale) * dt)), (float)maxDelta)) * dt)) {
            yawVelocity = 0.0f;
            f = yawDelta;
        } else {
            f = turn;
        }
        return f;
    }

    public static /* synthetic */ float computeTurnDelta$default(HeadRotationModule headRotationModule, float f, float f2, float f3, float f4, float f5, EaseMode easeMode, int n, Object object) {
        if ((n & 2) != 0) {
            f2 = 1.0f;
        }
        if ((n & 4) != 0) {
            f3 = 1.0f;
        }
        if ((n & 8) != 0) {
            f4 = 100.0f;
        }
        if ((n & 0x10) != 0) {
            f5 = 220.0f;
        }
        if ((n & 0x20) != 0) {
            easeMode = EaseMode.TANH;
        }
        return headRotationModule.computeTurnDelta(f, f2, f3, f4, f5, easeMode);
    }

    public final float computePitchDelta(float pitchDelta, float maxSpeedScale, float accelScale, float maxPitchSpeed, float maxPitchAccel, @NotNull EaseMode easeMode) {
        float f;
        float maxAccel;
        float maxDelta;
        float speedDelta;
        float turn;
        Intrinsics.checkNotNullParameter((Object)((Object)easeMode), (String)"easeMode");
        float absDelta = Math.abs(pitchDelta);
        if (absDelta < 1.0E-4f) {
            pitchVelocity = 0.0f;
            pitchLastTimeNs = System.nanoTime();
            return 0.0f;
        }
        long now = System.nanoTime();
        float dt = pitchLastTimeNs == 0L ? 0.05f : RangesKt.coerceIn((float)((float)(now - pitchLastTimeNs) / 1.0E9f), (float)0.004166667f, (float)0.1f);
        pitchLastTimeNs = now;
        float speedPerSec = maxPitchSpeed * this.easeCurve(absDelta, easeMode) * maxSpeedScale;
        float desiredSpeed = pitchDelta >= 0.0f ? speedPerSec : -speedPerSec;
        if (absDelta < Math.abs(turn = (pitchVelocity += RangesKt.coerceIn((float)(speedDelta = desiredSpeed - pitchVelocity), (float)(-(maxDelta = (maxAccel = maxPitchAccel * accelScale) * dt)), (float)maxDelta)) * dt)) {
            pitchVelocity = 0.0f;
            f = pitchDelta;
        } else {
            f = turn;
        }
        return f;
    }

    public static /* synthetic */ float computePitchDelta$default(HeadRotationModule headRotationModule, float f, float f2, float f3, float f4, float f5, EaseMode easeMode, int n, Object object) {
        if ((n & 2) != 0) {
            f2 = 1.0f;
        }
        if ((n & 4) != 0) {
            f3 = 1.0f;
        }
        if ((n & 8) != 0) {
            f4 = 100.0f;
        }
        if ((n & 0x10) != 0) {
            f5 = 220.0f;
        }
        if ((n & 0x20) != 0) {
            easeMode = EaseMode.TANH;
        }
        return headRotationModule.computePitchDelta(f, f2, f3, f4, f5, easeMode);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\b\u00a8\u0006\t"}, d2={"Lorg/cobalt/internal/pathfinding/HeadRotationModule$EaseMode;", "", "<init>", "(Ljava/lang/String;I)V", "TANH", "SINE_OUT", "SINE_IN_OUT", "CUBIC_IN_OUT", "LINEAR", "cobalt"})
    public static final class EaseMode
    extends Enum<EaseMode> {
        public static final /* enum */ EaseMode TANH = new EaseMode();
        public static final /* enum */ EaseMode SINE_OUT = new EaseMode();
        public static final /* enum */ EaseMode SINE_IN_OUT = new EaseMode();
        public static final /* enum */ EaseMode CUBIC_IN_OUT = new EaseMode();
        public static final /* enum */ EaseMode LINEAR = new EaseMode();
        private static final /* synthetic */ EaseMode[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static EaseMode[] values() {
            return (EaseMode[])$VALUES.clone();
        }

        public static EaseMode valueOf(String value) {
            return Enum.valueOf(EaseMode.class, value);
        }

        @NotNull
        public static EnumEntries<EaseMode> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = easeModeArray = new EaseMode[]{EaseMode.TANH, EaseMode.SINE_OUT, EaseMode.SINE_IN_OUT, EaseMode.CUBIC_IN_OUT, EaseMode.LINEAR};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[EaseMode.values().length];
            try {
                nArray[EaseMode.TANH.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[EaseMode.LINEAR.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[EaseMode.SINE_OUT.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[EaseMode.SINE_IN_OUT.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[EaseMode.CUBIC_IN_OUT.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

