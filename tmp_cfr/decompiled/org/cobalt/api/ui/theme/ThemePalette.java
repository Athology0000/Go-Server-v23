/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.ui.theme;

import java.awt.Color;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import org.cobalt.api.ui.theme.impl.CustomTheme;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0011\b\u0086\b\u0018\u00002\u00020\u0001BC\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0002\u0012\b\b\u0002\u0010\b\u001a\u00020\u0002\u00a2\u0006\u0004\b\t\u0010\nJ\u0015\u0010\u000e\u001a\u00020\r2\u0006\u0010\f\u001a\u00020\u000b\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0010\u0010\u0010\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0010\u0010\u0012\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0012\u0010\u0011J\u0010\u0010\u0013\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0013\u0010\u0011J\u0010\u0010\u0014\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0014\u0010\u0011J\u0010\u0010\u0015\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0015\u0010\u0011J\u0010\u0010\u0016\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0016\u0010\u0011JL\u0010\u0017\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00022\b\b\u0002\u0010\u0007\u001a\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u001b\u0010\u001b\u001a\u00020\u001a2\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u001b\u0010\u001cJ\u0011\u0010\u001d\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001d\u0010\u0011J\u0011\u0010\u001f\u001a\u00020\u001eH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001f\u0010 R\"\u0010\u0003\u001a\u00020\u00028\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0003\u0010!\u001a\u0004\b\"\u0010\u0011\"\u0004\b#\u0010$R\"\u0010\u0004\u001a\u00020\u00028\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0004\u0010!\u001a\u0004\b%\u0010\u0011\"\u0004\b&\u0010$R\"\u0010\u0005\u001a\u00020\u00028\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0005\u0010!\u001a\u0004\b'\u0010\u0011\"\u0004\b(\u0010$R\"\u0010\u0006\u001a\u00020\u00028\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0006\u0010!\u001a\u0004\b)\u0010\u0011\"\u0004\b*\u0010$R\"\u0010\u0007\u001a\u00020\u00028\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0007\u0010!\u001a\u0004\b+\u0010\u0011\"\u0004\b,\u0010$R\"\u0010\b\u001a\u00020\u00028\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\b\u0010!\u001a\u0004\b-\u0010\u0011\"\u0004\b.\u0010$\u00a8\u0006/"}, d2={"Lorg/cobalt/api/ui/theme/ThemePalette;", "", "", "primary", "background", "surface", "error", "text", "textSecondary", "<init>", "(IIIIII)V", "Lorg/cobalt/api/ui/theme/impl/CustomTheme;", "theme", "", "applyTo", "(Lorg/cobalt/api/ui/theme/impl/CustomTheme;)V", "component1", "()I", "component2", "component3", "component4", "component5", "component6", "copy", "(IIIIII)Lorg/cobalt/api/ui/theme/ThemePalette;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getPrimary", "setPrimary", "(I)V", "getBackground", "setBackground", "getSurface", "setSurface", "getError", "setError", "getText", "setText", "getTextSecondary", "setTextSecondary", "cobalt"})
public final class ThemePalette {
    private int primary;
    private int background;
    private int surface;
    private int error;
    private int text;
    private int textSecondary;

    public ThemePalette(int primary, int background, int surface, int error, int text, int textSecondary) {
        this.primary = primary;
        this.background = background;
        this.surface = surface;
        this.error = error;
        this.text = text;
        this.textSecondary = textSecondary;
    }

    public /* synthetic */ ThemePalette(int n, int n2, int n3, int n4, int n5, int n6, int n7, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n7 & 1) != 0) {
            n = new Color(61, 94, 149).getRGB();
        }
        if ((n7 & 2) != 0) {
            n2 = new Color(18, 18, 18).getRGB();
        }
        if ((n7 & 4) != 0) {
            n3 = new Color(24, 24, 24).getRGB();
        }
        if ((n7 & 8) != 0) {
            n4 = new Color(178, 34, 34).getRGB();
        }
        if ((n7 & 0x10) != 0) {
            n5 = new Color(230, 230, 230).getRGB();
        }
        if ((n7 & 0x20) != 0) {
            n6 = new Color(179, 179, 179).getRGB();
        }
        this(n, n2, n3, n4, n5, n6);
    }

    public final int getPrimary() {
        return this.primary;
    }

    public final void setPrimary(int n) {
        this.primary = n;
    }

    public final int getBackground() {
        return this.background;
    }

    public final void setBackground(int n) {
        this.background = n;
    }

    public final int getSurface() {
        return this.surface;
    }

    public final void setSurface(int n) {
        this.surface = n;
    }

    public final int getError() {
        return this.error;
    }

    public final void setError(int n) {
        this.error = n;
    }

    public final int getText() {
        return this.text;
    }

    public final void setText(int n) {
        this.text = n;
    }

    public final int getTextSecondary() {
        return this.textSecondary;
    }

    public final void setTextSecondary(int n) {
        this.textSecondary = n;
    }

    public final void applyTo(@NotNull CustomTheme theme) {
        Intrinsics.checkNotNullParameter((Object)theme, (String)"theme");
        theme.setBackground(this.background);
        theme.setPanel(this.surface);
        theme.setInset(ThemePalette.applyTo$adjust(this.background, 1.2f));
        theme.setOverlay(ThemePalette.applyTo$alpha(this.background, 230));
        theme.setText(this.text);
        theme.setTextPrimary(ThemePalette.applyTo$adjust(this.text, 1.1f));
        theme.setTextSecondary(this.textSecondary);
        theme.setTextDisabled(ThemePalette.applyTo$alpha(this.text, 100));
        theme.setTextPlaceholder(ThemePalette.applyTo$alpha(this.text, 120));
        theme.setTextOnAccent(ThemePalette.applyTo$adjust(this.text, 1.2f));
        theme.setAccent(this.primary);
        theme.setAccentPrimary(ThemePalette.applyTo$adjust(this.primary, 0.9f));
        theme.setAccentSecondary(ThemePalette.applyTo$adjust(this.primary, 1.1f));
        theme.setSelection(ThemePalette.applyTo$alpha(this.primary, 100));
        theme.setSelectedOverlay(ThemePalette.applyTo$alpha(this.primary, 50));
        theme.setControlBg(ThemePalette.applyTo$alpha(ThemePalette.applyTo$adjust(this.surface, 1.5f), 50));
        theme.setControlBorder(ThemePalette.applyTo$adjust(this.surface, 2.0f));
        theme.setInputBg(ThemePalette.applyTo$alpha(ThemePalette.applyTo$adjust(this.surface, 1.5f), 50));
        theme.setInputBorder(ThemePalette.applyTo$adjust(this.surface, 2.0f));
        theme.setSuccess(new Color(34, 139, 34).getRGB());
        theme.setWarning(new Color(184, 134, 11).getRGB());
        theme.setError(this.error);
        theme.setInfo(this.primary);
        theme.setSuccessBackground(ThemePalette.applyTo$alpha(theme.getSuccess(), 25));
        theme.setSuccessBorder(ThemePalette.applyTo$alpha(theme.getSuccess(), 150));
        theme.setSuccessIcon(theme.getSuccess());
        theme.setWarningBackground(ThemePalette.applyTo$alpha(theme.getWarning(), 25));
        theme.setWarningBorder(ThemePalette.applyTo$alpha(theme.getWarning(), 150));
        theme.setWarningIcon(theme.getWarning());
        theme.setErrorBackground(ThemePalette.applyTo$alpha(this.error, 25));
        theme.setErrorBorder(ThemePalette.applyTo$alpha(this.error, 150));
        theme.setErrorIcon(this.error);
        theme.setInfoBackground(ThemePalette.applyTo$alpha(this.primary, 25));
        theme.setInfoBorder(ThemePalette.applyTo$alpha(this.primary, 150));
        theme.setInfoIcon(this.primary);
        theme.setScrollbarThumb(this.primary);
        theme.setScrollbarTrack(ThemePalette.applyTo$adjust(this.surface, 1.2f));
        theme.setSliderTrack(ThemePalette.applyTo$adjust(this.surface, 2.5f));
        theme.setSliderFill(this.primary);
        theme.setSliderThumb(this.primary);
        theme.setTooltipBackground(ThemePalette.applyTo$alpha(this.background, 240));
        theme.setTooltipBorder(ThemePalette.applyTo$adjust(this.surface, 2.0f));
        theme.setTooltipText(this.text);
        theme.setNotificationBackground(this.background);
        theme.setNotificationBorder(this.primary);
        theme.setNotificationText(this.text);
        theme.setNotificationTextSecondary(theme.getTextDisabled());
        theme.setSelectionText(this.text);
        theme.setSearchPlaceholderText(ThemePalette.applyTo$alpha(this.text, 128));
        theme.setModuleDivider(ThemePalette.applyTo$adjust(this.surface, 2.0f));
        theme.setWhite(Color.WHITE.getRGB());
        theme.setBlack(Color.BLACK.getRGB());
        theme.setTransparent(new Color(0, 0, 0, 0).getRGB());
    }

    public final int component1() {
        return this.primary;
    }

    public final int component2() {
        return this.background;
    }

    public final int component3() {
        return this.surface;
    }

    public final int component4() {
        return this.error;
    }

    public final int component5() {
        return this.text;
    }

    public final int component6() {
        return this.textSecondary;
    }

    @NotNull
    public final ThemePalette copy(int primary, int background, int surface, int error, int text, int textSecondary) {
        return new ThemePalette(primary, background, surface, error, text, textSecondary);
    }

    public static /* synthetic */ ThemePalette copy$default(ThemePalette themePalette, int n, int n2, int n3, int n4, int n5, int n6, int n7, Object object) {
        if ((n7 & 1) != 0) {
            n = themePalette.primary;
        }
        if ((n7 & 2) != 0) {
            n2 = themePalette.background;
        }
        if ((n7 & 4) != 0) {
            n3 = themePalette.surface;
        }
        if ((n7 & 8) != 0) {
            n4 = themePalette.error;
        }
        if ((n7 & 0x10) != 0) {
            n5 = themePalette.text;
        }
        if ((n7 & 0x20) != 0) {
            n6 = themePalette.textSecondary;
        }
        return themePalette.copy(n, n2, n3, n4, n5, n6);
    }

    @NotNull
    public String toString() {
        return "ThemePalette(primary=" + this.primary + ", background=" + this.background + ", surface=" + this.surface + ", error=" + this.error + ", text=" + this.text + ", textSecondary=" + this.textSecondary + ")";
    }

    public int hashCode() {
        int result = Integer.hashCode(this.primary);
        result = result * 31 + Integer.hashCode(this.background);
        result = result * 31 + Integer.hashCode(this.surface);
        result = result * 31 + Integer.hashCode(this.error);
        result = result * 31 + Integer.hashCode(this.text);
        result = result * 31 + Integer.hashCode(this.textSecondary);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ThemePalette)) {
            return false;
        }
        ThemePalette themePalette = (ThemePalette)other;
        if (this.primary != themePalette.primary) {
            return false;
        }
        if (this.background != themePalette.background) {
            return false;
        }
        if (this.surface != themePalette.surface) {
            return false;
        }
        if (this.error != themePalette.error) {
            return false;
        }
        if (this.text != themePalette.text) {
            return false;
        }
        return this.textSecondary == themePalette.textSecondary;
    }

    private static final int applyTo$adjust(int $this$applyTo_u24adjust, float factor) {
        Color c = new Color($this$applyTo_u24adjust, true);
        int r = (int)RangesKt.coerceIn((float)((float)c.getRed() * factor), (float)0.0f, (float)255.0f);
        int g = (int)RangesKt.coerceIn((float)((float)c.getGreen() * factor), (float)0.0f, (float)255.0f);
        int b = (int)RangesKt.coerceIn((float)((float)c.getBlue() * factor), (float)0.0f, (float)255.0f);
        return new Color(r, g, b, c.getAlpha()).getRGB();
    }

    private static final int applyTo$alpha(int $this$applyTo_u24alpha, int alpha) {
        Color c = new Color($this$applyTo_u24alpha, true);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), RangesKt.coerceIn((int)alpha, (int)0, (int)255)).getRGB();
    }

    public ThemePalette() {
        this(0, 0, 0, 0, 0, 0, 63, null);
    }
}

