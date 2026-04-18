/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.ui.theme;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import org.cobalt.api.ui.theme.Theme;
import org.cobalt.api.ui.theme.impl.DarkTheme;
import org.cobalt.api.ui.theme.impl.LightTheme;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\b\u0007\n\u0002\u0010\t\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0015\u0010\t\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\t\u0010\bJ\u0013\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00040\n\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0015\u0010\u000e\u001a\u00020\r2\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0017\u0010\u0012\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u0010\u00a2\u0006\u0004\b\u0012\u0010\u0013J9\u0010\u0018\u001a\u00020\u00172\b\b\u0002\u0010\u0011\u001a\u00020\u00102\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00102\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u00102\b\b\u0002\u0010\u0016\u001a\u00020\u0010\u00a2\u0006\u0004\b\u0018\u0010\u0019R\u001a\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00040\u001a8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u001cR$\u0010\u001e\u001a\u00020\u00042\u0006\u0010\u001d\u001a\u00020\u00048\u0006@BX\u0086\u000e\u00a2\u0006\f\n\u0004\b\u001e\u0010\u001f\u001a\u0004\b \u0010!R\u0014\u0010#\u001a\u00020\"8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b#\u0010$\u00a8\u0006%"}, d2={"Lorg/cobalt/api/ui/theme/ThemeManager;", "", "<init>", "()V", "Lorg/cobalt/api/ui/theme/Theme;", "theme", "", "registerTheme", "(Lorg/cobalt/api/ui/theme/Theme;)V", "setTheme", "", "getThemes", "()Ljava/util/List;", "", "unregisterTheme", "(Lorg/cobalt/api/ui/theme/Theme;)Z", "", "speedMultiplier", "getRainbowHue", "(F)F", "saturationOverride", "brightnessOverride", "opacity", "", "getRainbowColor", "(FLjava/lang/Float;Ljava/lang/Float;F)I", "", "themes", "Ljava/util/List;", "value", "currentTheme", "Lorg/cobalt/api/ui/theme/Theme;", "getCurrentTheme", "()Lorg/cobalt/api/ui/theme/Theme;", "", "rainbowStartTime", "J", "cobalt"})
@SourceDebugExtension(value={"SMAP\nThemeManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ThemeManager.kt\norg/cobalt/api/ui/theme/ThemeManager\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,68:1\n2792#2,3:69\n231#2,2:72\n*S KotlinDebug\n*F\n+ 1 ThemeManager.kt\norg/cobalt/api/ui/theme/ThemeManager\n*L\n21#1:69,3\n40#1:72,2\n*E\n"})
public final class ThemeManager {
    @NotNull
    public static final ThemeManager INSTANCE = new ThemeManager();
    @NotNull
    private static final List<Theme> themes = new ArrayList();
    @NotNull
    private static Theme currentTheme = new DarkTheme();
    private static final long rainbowStartTime = System.currentTimeMillis();

    private ThemeManager() {
    }

    @NotNull
    public final Theme getCurrentTheme() {
        return currentTheme;
    }

    public final void registerTheme(@NotNull Theme theme) {
        boolean bl;
        block4: {
            Intrinsics.checkNotNullParameter((Object)theme, (String)"theme");
            Iterable $this$none$iv = themes;
            boolean $i$f$none = false;
            if ($this$none$iv instanceof Collection && ((Collection)$this$none$iv).isEmpty()) {
                bl = true;
            } else {
                for (Object element$iv : $this$none$iv) {
                    Theme it = (Theme)element$iv;
                    boolean bl2 = false;
                    if (!Intrinsics.areEqual((Object)it.getName(), (Object)theme.getName())) continue;
                    bl = false;
                    break block4;
                }
                bl = true;
            }
        }
        if (bl) {
            themes.add(theme);
        }
    }

    public final void setTheme(@NotNull Theme theme) {
        Intrinsics.checkNotNullParameter((Object)theme, (String)"theme");
        currentTheme = theme;
    }

    @NotNull
    public final List<Theme> getThemes() {
        return themes;
    }

    public final boolean unregisterTheme(@NotNull Theme theme) {
        Intrinsics.checkNotNullParameter((Object)theme, (String)"theme");
        if (Intrinsics.areEqual((Object)theme.getName(), (Object)"Dark") || Intrinsics.areEqual((Object)theme.getName(), (Object)"Light")) {
            return false;
        }
        boolean removed = themes.removeIf(arg_0 -> ThemeManager.unregisterTheme$lambda$1(arg_0 -> ThemeManager.unregisterTheme$lambda$0(theme, arg_0), arg_0));
        if (removed && Intrinsics.areEqual((Object)currentTheme.getName(), (Object)theme.getName())) {
            Object element$iv2;
            block3: {
                Iterable $this$first$iv = themes;
                boolean $i$f$first = false;
                for (Object element$iv2 : $this$first$iv) {
                    Theme it = (Theme)element$iv2;
                    boolean bl = false;
                    if (!Intrinsics.areEqual((Object)it.getName(), (Object)"Dark")) continue;
                    break block3;
                }
                throw new NoSuchElementException("Collection contains no element matching the predicate.");
            }
            currentTheme = (Theme)element$iv2;
        }
        return removed;
    }

    public final float getRainbowHue(float speedMultiplier) {
        float themeSpeed = currentTheme.getRainbowSpeed();
        float effectiveSpeed = themeSpeed * speedMultiplier;
        double elapsed = (double)(System.currentTimeMillis() - rainbowStartTime) / 1000.0;
        return (float)(elapsed * (double)effectiveSpeed % 1.0 + 1.0) % 1.0f;
    }

    public static /* synthetic */ float getRainbowHue$default(ThemeManager themeManager, float f, int n, Object object) {
        if ((n & 1) != 0) {
            f = 1.0f;
        }
        return themeManager.getRainbowHue(f);
    }

    public final int getRainbowColor(float speedMultiplier, @Nullable Float saturationOverride, @Nullable Float brightnessOverride, float opacity) {
        float hue = this.getRainbowHue(speedMultiplier);
        Float f = saturationOverride;
        float sat = f != null ? f.floatValue() : currentTheme.getRainbowSaturation();
        Float f2 = brightnessOverride;
        float bri = f2 != null ? f2.floatValue() : currentTheme.getRainbowBrightness();
        int rgb = Color.HSBtoRGB(hue, sat, bri);
        int alpha = RangesKt.coerceIn((int)((int)(opacity * (float)255)), (int)0, (int)255);
        return alpha << 24 | rgb & 0xFFFFFF;
    }

    public static /* synthetic */ int getRainbowColor$default(ThemeManager themeManager, float f, Float f2, Float f3, float f4, int n, Object object) {
        if ((n & 1) != 0) {
            f = 1.0f;
        }
        if ((n & 2) != 0) {
            f2 = null;
        }
        if ((n & 4) != 0) {
            f3 = null;
        }
        if ((n & 8) != 0) {
            f4 = 1.0f;
        }
        return themeManager.getRainbowColor(f, f2, f3, f4);
    }

    private static final boolean unregisterTheme$lambda$0(Theme $theme, Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return Intrinsics.areEqual((Object)it.getName(), (Object)$theme.getName());
    }

    private static final boolean unregisterTheme$lambda$1(Function1 $tmp0, Object p0) {
        return (Boolean)$tmp0.invoke(p0);
    }

    static {
        INSTANCE.registerTheme(new DarkTheme());
        INSTANCE.registerTheme(new LightTheme());
    }
}

