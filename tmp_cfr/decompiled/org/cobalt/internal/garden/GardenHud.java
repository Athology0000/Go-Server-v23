/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Ref$FloatRef
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.ranges.RangesKt;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Gradient;
import org.cobalt.internal.garden.GardenState;
import org.cobalt.internal.garden.managers.DynamicRestManager;
import org.cobalt.internal.garden.managers.ProfitManager;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003JM\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u000e\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0017\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0013\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0017\u0010\u0018\u001a\u00020\u00142\u0006\u0010\u0017\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0018\u0010\u0016\u00a8\u0006\u0019"}, d2={"Lorg/cobalt/internal/garden/GardenHud;", "", "<init>", "()V", "", "x", "y", "w", "h", "Lorg/cobalt/internal/garden/GardenState;", "state", "", "showProfit", "showRest", "", "sessionStartTime", "", "render", "(FFFFLorg/cobalt/internal/garden/GardenState;ZZJ)V", "ms", "", "formatDuration", "(J)Ljava/lang/String;", "value", "formatProfit", "cobalt"})
public final class GardenHud {
    @NotNull
    public static final GardenHud INSTANCE = new GardenHud();

    private GardenHud() {
    }

    public final void render(float x, float y, float w, float h, @NotNull GardenState state, boolean showProfit, boolean showRest, long sessionStartTime) {
        Intrinsics.checkNotNullParameter((Object)((Object)state), (String)"state");
        float radius = 10.0f;
        float pad = 10.0f;
        int textColor = -1;
        int dimColor = -1146105857;
        long now = System.currentTimeMillis();
        float twoPi = (float)Math.PI * 2;
        NVGRenderer.rect(x, y, w, h, -16118246, radius);
        NVGRenderer.gradientRect(x, y, w, h * 0.5f, 0x14FFFFFF, 0, Gradient.TopToBottom, radius);
        float angle = (float)(now % 10000L) / 10000.0f * twoPi;
        float shiftX = (float)Math.cos(angle) * (w * 0.42f);
        NVGRenderer.hollowGradientRectShifted(x, y, w, h, 1.5f, ThemeManager.INSTANCE.getCurrentTheme().getAccent(), ThemeManager.INSTANCE.getCurrentTheme().getAccentSecondary(), Gradient.LeftToRight, radius, shiftX, 0.0f);
        Ref.FloatRef cy = new Ref.FloatRef();
        cy.element = y + 14.0f;
        NVGRenderer.text$default("GARDEN MACRO", x + pad, cy.element, 11.0f, ThemeManager.INSTANCE.getCurrentTheme().getAccent(), null, 32, null);
        String stateStr = state.name();
        int stateColor = switch (WhenMappings.$EnumSwitchMapping$0[state.ordinal()]) {
            case 1 -> -11731086;
            case 2 -> -10164;
            case 3 -> -11730976;
            case 4 -> -27572;
            case 5 -> -7829368;
            case 6 -> -46004;
            case 7 -> -12303292;
            default -> throw new NoWhenBranchMatchedException();
        };
        float stateW = NVGRenderer.textWidth$default(stateStr, 9.0f, null, 4, null);
        NVGRenderer.rect(x + w - pad - stateW - 8.0f, cy.element - 9.0f, stateW + 8.0f, 12.0f, stateColor & 0xFFFFFF | 0x33000000, 3.0f);
        NVGRenderer.text$default(stateStr, x + w - pad - stateW - 4.0f, cy.element, 9.0f, stateColor, null, 32, null);
        cy.element += 14.0f;
        String runtime = this.formatDuration(System.currentTimeMillis() - sessionStartTime);
        NVGRenderer.text$default("Runtime: " + runtime, x + pad, cy.element, 10.0f, dimColor, null, 32, null);
        cy.element += 14.0f;
        if (showRest && state == GardenState.FARMING) {
            long restMs = DynamicRestManager.INSTANCE.timeUntilRestMs();
            String restStr = "Next Rest: " + this.formatDuration(restMs);
            NVGRenderer.text$default(restStr, x + pad, cy.element, 10.0f, dimColor, null, 32, null);
            cy.element += 11.0f;
            float barW = w - pad * 2.0f;
            float barH = 5.0f;
            long totalMs = RangesKt.coerceAtLeast((long)DynamicRestManager.INSTANCE.getTargetWorkDurationMs(), (long)1L);
            long elapsed = totalMs - RangesKt.coerceAtLeast((long)restMs, (long)0L);
            float ratio = RangesKt.coerceIn((float)((float)elapsed / (float)totalMs), (float)0.0f, (float)1.0f);
            NVGRenderer.rect(x + pad, cy.element, barW, barH, -15065024, 3.0f);
            if (ratio > 0.0f) {
                NVGRenderer.gradientRect(x + pad, cy.element, barW * ratio, barH, ThemeManager.INSTANCE.getCurrentTheme().getAccent(), ThemeManager.INSTANCE.getCurrentTheme().getAccentSecondary(), Gradient.LeftToRight, 3.0f);
            }
            cy.element += 12.0f;
        }
        if (showProfit) {
            NVGRenderer.rect(x + pad, cy.element, w - pad * 2.0f, 1.0f, 0x33FFFFFF, 0.0f);
            cy.element += 8.0f;
            NVGRenderer.text$default("Profit", x + pad, cy.element, 10.0f, ThemeManager.INSTANCE.getCurrentTheme().getAccent(), null, 32, null);
            cy.element += 13.0f;
            GardenHud.render$profitLine(x, pad, cy, dimColor, w, "Session:", ProfitManager.INSTANCE.getSessionProfit());
            GardenHud.render$profitLine(x, pad, cy, dimColor, w, "Daily:", ProfitManager.INSTANCE.getDailyProfit());
            GardenHud.render$profitLine(x, pad, cy, dimColor, w, "Lifetime:", ProfitManager.INSTANCE.getLifetimeProfit());
        }
    }

    private final String formatDuration(long ms) {
        String string;
        long s = RangesKt.coerceAtLeast((long)(ms / (long)1000), (long)0L);
        long h = s / (long)3600;
        long m = s % (long)3600 / (long)60;
        long sec = s % (long)60;
        if (h > 0L) {
            String string2 = "%02d:%02d:%02d";
            Object[] objectArray = new Object[]{h, m, sec};
            String string3 = String.format(string2, Arrays.copyOf(objectArray, objectArray.length));
            string = string3;
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"format(...)");
        } else {
            String string4 = "%02d:%02d";
            Object[] objectArray = new Object[]{m, sec};
            String string5 = String.format(string4, Arrays.copyOf(objectArray, objectArray.length));
            string = string5;
            Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"format(...)");
        }
        return string;
    }

    private final String formatProfit(long value) {
        String string;
        long abs = Math.abs(value);
        String sign = value < 0L ? "-" : "+";
        if (abs >= 1000000L) {
            String string2 = "%.1f";
            Object[] objectArray = new Object[]{(double)abs / 1000000.0};
            String string3 = String.format(string2, Arrays.copyOf(objectArray, objectArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"format(...)");
            string = sign + string3 + "M";
        } else if (abs >= 1000L) {
            String string4 = "%.1f";
            Object[] objectArray = new Object[]{(double)abs / 1000.0};
            String string5 = String.format(string4, Arrays.copyOf(objectArray, objectArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"format(...)");
            string = sign + string5 + "K";
        } else {
            string = sign + abs;
        }
        return string;
    }

    private static final void render$profitLine(float $x, float pad, Ref.FloatRef cy, int dimColor, float $w, String label, long value) {
        NVGRenderer.text$default(label, $x + pad, cy.element, 9.0f, dimColor, null, 32, null);
        String valStr = INSTANCE.formatProfit(value);
        int valColor = value >= 0L ? -11731086 : -46004;
        float valW = NVGRenderer.textWidth$default(valStr, 9.0f, null, 4, null);
        NVGRenderer.text$default(valStr, $x + $w - pad - valW, cy.element, 9.0f, valColor, null, 32, null);
        cy.element += 12.0f;
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[GardenState.values().length];
            try {
                nArray[GardenState.FARMING.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[GardenState.CLEANING.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[GardenState.VISITING.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[GardenState.AUTOSELLING.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[GardenState.RESTING.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[GardenState.RECOVERING.ordinal()] = 6;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[GardenState.OFF.ordinal()] = 7;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

