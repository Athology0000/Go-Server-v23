/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.module.setting.impl;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0005\u0004\u0005\u0006\u0007\bB\t\b\u0004\u00a2\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\u0005\t\n\u000b\f\r\u00a8\u0006\u000e"}, d2={"Lorg/cobalt/api/module/setting/impl/ColorMode;", "", "<init>", "()V", "Static", "Rainbow", "SyncedRainbow", "ThemeColor", "TweakedTheme", "Lorg/cobalt/api/module/setting/impl/ColorMode$Rainbow;", "Lorg/cobalt/api/module/setting/impl/ColorMode$Static;", "Lorg/cobalt/api/module/setting/impl/ColorMode$SyncedRainbow;", "Lorg/cobalt/api/module/setting/impl/ColorMode$ThemeColor;", "Lorg/cobalt/api/module/setting/impl/ColorMode$TweakedTheme;", "cobalt"})
public abstract sealed class ColorMode {
    private ColorMode() {
    }

    public /* synthetic */ ColorMode(DefaultConstructorMarker $constructor_marker) {
        this();
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\r\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\b\u0086\b\u0018\u00002\u00020\u0001B/\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\nJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\nJ\u0010\u0010\r\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\nJ8\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u001b\u0010\u0013\u001a\u00020\u00122\b\u0010\u0011\u001a\u0004\u0018\u00010\u0010H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0016\u001a\u00020\u0015H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0019\u001a\u00020\u0018H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0019\u0010\u001aR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001b\u001a\u0004\b\u001c\u0010\nR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001b\u001a\u0004\b\u001d\u0010\nR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001b\u001a\u0004\b\u001e\u0010\nR\u0017\u0010\u0006\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001b\u001a\u0004\b\u001f\u0010\n\u00a8\u0006 "}, d2={"Lorg/cobalt/api/module/setting/impl/ColorMode$Rainbow;", "Lorg/cobalt/api/module/setting/impl/ColorMode;", "", "speed", "saturation", "brightness", "opacity", "<init>", "(FFFF)V", "component1", "()F", "component2", "component3", "component4", "copy", "(FFFF)Lorg/cobalt/api/module/setting/impl/ColorMode$Rainbow;", "", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "F", "getSpeed", "getSaturation", "getBrightness", "getOpacity", "cobalt"})
    public static final class Rainbow
    extends ColorMode {
        private final float speed;
        private final float saturation;
        private final float brightness;
        private final float opacity;

        public Rainbow(float speed, float saturation, float brightness, float opacity) {
            super(null);
            this.speed = speed;
            this.saturation = saturation;
            this.brightness = brightness;
            this.opacity = opacity;
        }

        public /* synthetic */ Rainbow(float f, float f2, float f3, float f4, int n, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n & 1) != 0) {
                f = 1.0f;
            }
            if ((n & 2) != 0) {
                f2 = 1.0f;
            }
            if ((n & 4) != 0) {
                f3 = 1.0f;
            }
            if ((n & 8) != 0) {
                f4 = 1.0f;
            }
            this(f, f2, f3, f4);
        }

        public final float getSpeed() {
            return this.speed;
        }

        public final float getSaturation() {
            return this.saturation;
        }

        public final float getBrightness() {
            return this.brightness;
        }

        public final float getOpacity() {
            return this.opacity;
        }

        public final float component1() {
            return this.speed;
        }

        public final float component2() {
            return this.saturation;
        }

        public final float component3() {
            return this.brightness;
        }

        public final float component4() {
            return this.opacity;
        }

        @NotNull
        public final Rainbow copy(float speed, float saturation, float brightness, float opacity) {
            return new Rainbow(speed, saturation, brightness, opacity);
        }

        public static /* synthetic */ Rainbow copy$default(Rainbow rainbow, float f, float f2, float f3, float f4, int n, Object object) {
            if ((n & 1) != 0) {
                f = rainbow.speed;
            }
            if ((n & 2) != 0) {
                f2 = rainbow.saturation;
            }
            if ((n & 4) != 0) {
                f3 = rainbow.brightness;
            }
            if ((n & 8) != 0) {
                f4 = rainbow.opacity;
            }
            return rainbow.copy(f, f2, f3, f4);
        }

        @NotNull
        public String toString() {
            return "Rainbow(speed=" + this.speed + ", saturation=" + this.saturation + ", brightness=" + this.brightness + ", opacity=" + this.opacity + ")";
        }

        public int hashCode() {
            int result = Float.hashCode(this.speed);
            result = result * 31 + Float.hashCode(this.saturation);
            result = result * 31 + Float.hashCode(this.brightness);
            result = result * 31 + Float.hashCode(this.opacity);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Rainbow)) {
                return false;
            }
            Rainbow rainbow = (Rainbow)other;
            if (Float.compare(this.speed, rainbow.speed) != 0) {
                return false;
            }
            if (Float.compare(this.saturation, rainbow.saturation) != 0) {
                return false;
            }
            if (Float.compare(this.brightness, rainbow.brightness) != 0) {
                return false;
            }
            return Float.compare(this.opacity, rainbow.opacity) == 0;
        }

        public Rainbow() {
            this(0.0f, 0.0f, 0.0f, 0.0f, 15, null);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u001a\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\b\u0010\tJ\u001b\u0010\r\u001a\u00020\f2\b\u0010\u000b\u001a\u0004\u0018\u00010\nH\u00d6\u0083\u0004\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0011\u0010\u000f\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u000f\u0010\u0007J\u0011\u0010\u0011\u001a\u00020\u0010H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0011\u0010\u0012R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0013\u001a\u0004\b\u0014\u0010\u0007\u00a8\u0006\u0015"}, d2={"Lorg/cobalt/api/module/setting/impl/ColorMode$Static;", "Lorg/cobalt/api/module/setting/impl/ColorMode;", "", "argb", "<init>", "(I)V", "component1", "()I", "copy", "(I)Lorg/cobalt/api/module/setting/impl/ColorMode$Static;", "", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getArgb", "cobalt"})
    public static final class Static
    extends ColorMode {
        private final int argb;

        public Static(int argb) {
            super(null);
            this.argb = argb;
        }

        public final int getArgb() {
            return this.argb;
        }

        public final int component1() {
            return this.argb;
        }

        @NotNull
        public final Static copy(int argb) {
            return new Static(argb);
        }

        public static /* synthetic */ Static copy$default(Static static_, int n, int n2, Object object) {
            if ((n2 & 1) != 0) {
                n = static_.argb;
            }
            return static_.copy(n);
        }

        @NotNull
        public String toString() {
            return "Static(argb=" + this.argb + ")";
        }

        public int hashCode() {
            return Integer.hashCode(this.argb);
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Static)) {
                return false;
            }
            Static static_ = (Static)other;
            return this.argb == static_.argb;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\r\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\b\u0086\b\u0018\u00002\u00020\u0001B/\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\nJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\nJ\u0010\u0010\r\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\nJ8\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u001b\u0010\u0013\u001a\u00020\u00122\b\u0010\u0011\u001a\u0004\u0018\u00010\u0010H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0016\u001a\u00020\u0015H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0019\u001a\u00020\u0018H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0019\u0010\u001aR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001b\u001a\u0004\b\u001c\u0010\nR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001b\u001a\u0004\b\u001d\u0010\nR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001b\u001a\u0004\b\u001e\u0010\nR\u0017\u0010\u0006\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001b\u001a\u0004\b\u001f\u0010\n\u00a8\u0006 "}, d2={"Lorg/cobalt/api/module/setting/impl/ColorMode$SyncedRainbow;", "Lorg/cobalt/api/module/setting/impl/ColorMode;", "", "speed", "saturation", "brightness", "opacity", "<init>", "(FFFF)V", "component1", "()F", "component2", "component3", "component4", "copy", "(FFFF)Lorg/cobalt/api/module/setting/impl/ColorMode$SyncedRainbow;", "", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "F", "getSpeed", "getSaturation", "getBrightness", "getOpacity", "cobalt"})
    public static final class SyncedRainbow
    extends ColorMode {
        private final float speed;
        private final float saturation;
        private final float brightness;
        private final float opacity;

        public SyncedRainbow(float speed, float saturation, float brightness, float opacity) {
            super(null);
            this.speed = speed;
            this.saturation = saturation;
            this.brightness = brightness;
            this.opacity = opacity;
        }

        public /* synthetic */ SyncedRainbow(float f, float f2, float f3, float f4, int n, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n & 1) != 0) {
                f = 1.0f;
            }
            if ((n & 2) != 0) {
                f2 = 1.0f;
            }
            if ((n & 4) != 0) {
                f3 = 1.0f;
            }
            if ((n & 8) != 0) {
                f4 = 1.0f;
            }
            this(f, f2, f3, f4);
        }

        public final float getSpeed() {
            return this.speed;
        }

        public final float getSaturation() {
            return this.saturation;
        }

        public final float getBrightness() {
            return this.brightness;
        }

        public final float getOpacity() {
            return this.opacity;
        }

        public final float component1() {
            return this.speed;
        }

        public final float component2() {
            return this.saturation;
        }

        public final float component3() {
            return this.brightness;
        }

        public final float component4() {
            return this.opacity;
        }

        @NotNull
        public final SyncedRainbow copy(float speed, float saturation, float brightness, float opacity) {
            return new SyncedRainbow(speed, saturation, brightness, opacity);
        }

        public static /* synthetic */ SyncedRainbow copy$default(SyncedRainbow syncedRainbow, float f, float f2, float f3, float f4, int n, Object object) {
            if ((n & 1) != 0) {
                f = syncedRainbow.speed;
            }
            if ((n & 2) != 0) {
                f2 = syncedRainbow.saturation;
            }
            if ((n & 4) != 0) {
                f3 = syncedRainbow.brightness;
            }
            if ((n & 8) != 0) {
                f4 = syncedRainbow.opacity;
            }
            return syncedRainbow.copy(f, f2, f3, f4);
        }

        @NotNull
        public String toString() {
            return "SyncedRainbow(speed=" + this.speed + ", saturation=" + this.saturation + ", brightness=" + this.brightness + ", opacity=" + this.opacity + ")";
        }

        public int hashCode() {
            int result = Float.hashCode(this.speed);
            result = result * 31 + Float.hashCode(this.saturation);
            result = result * 31 + Float.hashCode(this.brightness);
            result = result * 31 + Float.hashCode(this.opacity);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof SyncedRainbow)) {
                return false;
            }
            SyncedRainbow syncedRainbow = (SyncedRainbow)other;
            if (Float.compare(this.speed, syncedRainbow.speed) != 0) {
                return false;
            }
            if (Float.compare(this.saturation, syncedRainbow.saturation) != 0) {
                return false;
            }
            if (Float.compare(this.brightness, syncedRainbow.brightness) != 0) {
                return false;
            }
            return Float.compare(this.opacity, syncedRainbow.opacity) == 0;
        }

        public SyncedRainbow() {
            this(0.0f, 0.0f, 0.0f, 0.0f, 15, null);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u001a\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\b\u0010\tJ\u001b\u0010\r\u001a\u00020\f2\b\u0010\u000b\u001a\u0004\u0018\u00010\nH\u00d6\u0083\u0004\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0011\u0010\u0010\u001a\u00020\u000fH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0012\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0007R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0013\u001a\u0004\b\u0014\u0010\u0007\u00a8\u0006\u0015"}, d2={"Lorg/cobalt/api/module/setting/impl/ColorMode$ThemeColor;", "Lorg/cobalt/api/module/setting/impl/ColorMode;", "", "propertyName", "<init>", "(Ljava/lang/String;)V", "component1", "()Ljava/lang/String;", "copy", "(Ljava/lang/String;)Lorg/cobalt/api/module/setting/impl/ColorMode$ThemeColor;", "", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getPropertyName", "cobalt"})
    public static final class ThemeColor
    extends ColorMode {
        @NotNull
        private final String propertyName;

        public ThemeColor(@NotNull String propertyName) {
            Intrinsics.checkNotNullParameter((Object)propertyName, (String)"propertyName");
            super(null);
            this.propertyName = propertyName;
        }

        @NotNull
        public final String getPropertyName() {
            return this.propertyName;
        }

        @NotNull
        public final String component1() {
            return this.propertyName;
        }

        @NotNull
        public final ThemeColor copy(@NotNull String propertyName) {
            Intrinsics.checkNotNullParameter((Object)propertyName, (String)"propertyName");
            return new ThemeColor(propertyName);
        }

        public static /* synthetic */ ThemeColor copy$default(ThemeColor themeColor, String string, int n, Object object) {
            if ((n & 1) != 0) {
                string = themeColor.propertyName;
            }
            return themeColor.copy(string);
        }

        @NotNull
        public String toString() {
            return "ThemeColor(propertyName=" + this.propertyName + ")";
        }

        public int hashCode() {
            return this.propertyName.hashCode();
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ThemeColor)) {
                return false;
            }
            ThemeColor themeColor = (ThemeColor)other;
            return Intrinsics.areEqual((Object)this.propertyName, (Object)themeColor.propertyName);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u000f\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000b\b\u0086\b\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0004\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0004\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0004\u0012\b\b\u0002\u0010\b\u001a\u00020\u0004\u00a2\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\u000eJ\u0010\u0010\u0010\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u000eJ\u0010\u0010\u0011\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u000eJB\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00042\b\b\u0002\u0010\b\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u001b\u0010\u0017\u001a\u00020\u00162\b\u0010\u0015\u001a\u0004\u0018\u00010\u0014H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u0011\u0010\u001a\u001a\u00020\u0019H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u0011\u0010\u001c\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001c\u0010\fR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001d\u001a\u0004\b\u001e\u0010\fR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001f\u001a\u0004\b \u0010\u000eR\u0017\u0010\u0006\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001f\u001a\u0004\b!\u0010\u000eR\u0017\u0010\u0007\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001f\u001a\u0004\b\"\u0010\u000eR\u0017\u0010\b\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\b\u0010\u001f\u001a\u0004\b#\u0010\u000e\u00a8\u0006$"}, d2={"Lorg/cobalt/api/module/setting/impl/ColorMode$TweakedTheme;", "Lorg/cobalt/api/module/setting/impl/ColorMode;", "", "propertyName", "", "hueOffset", "saturationMultiplier", "brightnessMultiplier", "opacityMultiplier", "<init>", "(Ljava/lang/String;FFFF)V", "component1", "()Ljava/lang/String;", "component2", "()F", "component3", "component4", "component5", "copy", "(Ljava/lang/String;FFFF)Lorg/cobalt/api/module/setting/impl/ColorMode$TweakedTheme;", "", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Ljava/lang/String;", "getPropertyName", "F", "getHueOffset", "getSaturationMultiplier", "getBrightnessMultiplier", "getOpacityMultiplier", "cobalt"})
    public static final class TweakedTheme
    extends ColorMode {
        @NotNull
        private final String propertyName;
        private final float hueOffset;
        private final float saturationMultiplier;
        private final float brightnessMultiplier;
        private final float opacityMultiplier;

        public TweakedTheme(@NotNull String propertyName, float hueOffset, float saturationMultiplier, float brightnessMultiplier, float opacityMultiplier) {
            Intrinsics.checkNotNullParameter((Object)propertyName, (String)"propertyName");
            super(null);
            this.propertyName = propertyName;
            this.hueOffset = hueOffset;
            this.saturationMultiplier = saturationMultiplier;
            this.brightnessMultiplier = brightnessMultiplier;
            this.opacityMultiplier = opacityMultiplier;
        }

        public /* synthetic */ TweakedTheme(String string, float f, float f2, float f3, float f4, int n, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n & 2) != 0) {
                f = 0.0f;
            }
            if ((n & 4) != 0) {
                f2 = 1.0f;
            }
            if ((n & 8) != 0) {
                f3 = 1.0f;
            }
            if ((n & 0x10) != 0) {
                f4 = 1.0f;
            }
            this(string, f, f2, f3, f4);
        }

        @NotNull
        public final String getPropertyName() {
            return this.propertyName;
        }

        public final float getHueOffset() {
            return this.hueOffset;
        }

        public final float getSaturationMultiplier() {
            return this.saturationMultiplier;
        }

        public final float getBrightnessMultiplier() {
            return this.brightnessMultiplier;
        }

        public final float getOpacityMultiplier() {
            return this.opacityMultiplier;
        }

        @NotNull
        public final String component1() {
            return this.propertyName;
        }

        public final float component2() {
            return this.hueOffset;
        }

        public final float component3() {
            return this.saturationMultiplier;
        }

        public final float component4() {
            return this.brightnessMultiplier;
        }

        public final float component5() {
            return this.opacityMultiplier;
        }

        @NotNull
        public final TweakedTheme copy(@NotNull String propertyName, float hueOffset, float saturationMultiplier, float brightnessMultiplier, float opacityMultiplier) {
            Intrinsics.checkNotNullParameter((Object)propertyName, (String)"propertyName");
            return new TweakedTheme(propertyName, hueOffset, saturationMultiplier, brightnessMultiplier, opacityMultiplier);
        }

        public static /* synthetic */ TweakedTheme copy$default(TweakedTheme tweakedTheme, String string, float f, float f2, float f3, float f4, int n, Object object) {
            if ((n & 1) != 0) {
                string = tweakedTheme.propertyName;
            }
            if ((n & 2) != 0) {
                f = tweakedTheme.hueOffset;
            }
            if ((n & 4) != 0) {
                f2 = tweakedTheme.saturationMultiplier;
            }
            if ((n & 8) != 0) {
                f3 = tweakedTheme.brightnessMultiplier;
            }
            if ((n & 0x10) != 0) {
                f4 = tweakedTheme.opacityMultiplier;
            }
            return tweakedTheme.copy(string, f, f2, f3, f4);
        }

        @NotNull
        public String toString() {
            return "TweakedTheme(propertyName=" + this.propertyName + ", hueOffset=" + this.hueOffset + ", saturationMultiplier=" + this.saturationMultiplier + ", brightnessMultiplier=" + this.brightnessMultiplier + ", opacityMultiplier=" + this.opacityMultiplier + ")";
        }

        public int hashCode() {
            int result = this.propertyName.hashCode();
            result = result * 31 + Float.hashCode(this.hueOffset);
            result = result * 31 + Float.hashCode(this.saturationMultiplier);
            result = result * 31 + Float.hashCode(this.brightnessMultiplier);
            result = result * 31 + Float.hashCode(this.opacityMultiplier);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof TweakedTheme)) {
                return false;
            }
            TweakedTheme tweakedTheme = (TweakedTheme)other;
            if (!Intrinsics.areEqual((Object)this.propertyName, (Object)tweakedTheme.propertyName)) {
                return false;
            }
            if (Float.compare(this.hueOffset, tweakedTheme.hueOffset) != 0) {
                return false;
            }
            if (Float.compare(this.saturationMultiplier, tweakedTheme.saturationMultiplier) != 0) {
                return false;
            }
            if (Float.compare(this.brightnessMultiplier, tweakedTheme.brightnessMultiplier) != 0) {
                return false;
            }
            return Float.compare(this.opacityMultiplier, tweakedTheme.opacityMultiplier) == 0;
        }
    }
}

