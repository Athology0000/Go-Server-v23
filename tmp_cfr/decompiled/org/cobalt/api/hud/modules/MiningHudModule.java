/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function3
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.hud.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import org.cobalt.api.hud.HudAnchor;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.hud.HudElementBuilder;
import org.cobalt.api.hud.HudModuleDSLKt;
import org.cobalt.api.hud.modules.CommissionMacroModule;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Gradient;
import org.cobalt.internal.mining.MiningMacroModule;
import org.cobalt.internal.mining.MiningModule;
import org.cobalt.internal.mining.MiningProfitTracker;
import org.cobalt.internal.mining.RoutesModule;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\n\n\u0002\u0010\t\n\u0002\b\u0018\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0002\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0015\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0002\u00a2\u0006\u0004\b\b\u0010\u0007J\u0015\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u0004H\u0002\u00a2\u0006\u0004\b\n\u0010\u0007J\u000f\u0010\f\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\b\f\u0010\rJ\u000f\u0010\u000f\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0017\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\b\u0012\u0010\u0013J?\u0010\u001b\u001a\u00020\u001a2\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\t2\u0006\u0010\u0017\u001a\u00020\u000e2\u0006\u0010\u0018\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0019\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b\u001b\u0010\u001cJ1\u0010\u001d\u001a\u00020\u001a2\u0006\u0010\u0016\u001a\u00020\u00052\u0006\u0010\u0017\u001a\u00020\u000e2\u0006\u0010\u0018\u001a\u00020\u000e2\b\b\u0002\u0010\u0019\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\u001f\u0010!\u001a\u00020\u00052\u0006\u0010\u001f\u001a\u00020\u00052\u0006\u0010 \u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b!\u0010\"J\u000f\u0010#\u001a\u00020\u0005H\u0002\u00a2\u0006\u0004\b#\u0010$J\u0017\u0010'\u001a\u00020\u00052\u0006\u0010&\u001a\u00020%H\u0002\u00a2\u0006\u0004\b'\u0010(J\u0017\u0010*\u001a\u00020\u00052\u0006\u0010)\u001a\u00020%H\u0002\u00a2\u0006\u0004\b*\u0010(R\u0014\u0010+\u001a\u00020\u000e8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b+\u0010,R\u0014\u0010-\u001a\u00020\u000e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b-\u0010,R\u0014\u0010.\u001a\u00020\u000e8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b.\u0010,R\u0014\u0010/\u001a\u00020\u000e8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b/\u0010,R\u0014\u00100\u001a\u00020\u000e8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b0\u0010,R\u0014\u00101\u001a\u00020\u000e8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b1\u0010,R\u0014\u00102\u001a\u00020\u000e8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b2\u0010,R\u0014\u00105\u001a\u00020\u00148BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b3\u00104R\u0014\u00107\u001a\u00020\u00148BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b6\u00104R\u0014\u00108\u001a\u00020\u00148\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b8\u00109R\u0014\u0010;\u001a\u00020\u00148BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b:\u00104R\u0014\u0010=\u001a\u00020\u00148BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b<\u00104R\u0017\u0010?\u001a\u00020>8\u0006\u00a2\u0006\f\n\u0004\b?\u0010@\u001a\u0004\bA\u0010B\u00a8\u0006C"}, d2={"Lorg/cobalt/api/hud/modules/MiningHudModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "", "", "overlayRows", "()Ljava/util/List;", "buffRows", "Lorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionHudRow;", "commissionRows", "", "shouldShow", "()Z", "", "panelWidth", "()F", "showProfit", "panelHeight", "(Z)F", "", "index", "row", "x", "y", "rowColor", "", "renderCommissionRow", "(ILorg/cobalt/api/hud/modules/CommissionMacroModule$CommissionHudRow;FFFI)V", "renderRow", "(Ljava/lang/String;FFI)V", "text", "maxWidth", "ellipsize", "(Ljava/lang/String;F)Ljava/lang/String;", "activeMacroLabel", "()Ljava/lang/String;", "", "ms", "formatRuntime", "(J)Ljava/lang/String;", "cph", "formatCoinsPerHour", "textSize", "F", "lineHeight", "padding", "corner", "sectionGap", "sectionLabelGap", "dividerGap", "getLabelColor", "()I", "labelColor", "getValueColor", "valueColor", "targetedColor", "I", "getBorderColor1", "borderColor1", "getBorderColor2", "borderColor2", "Lorg/cobalt/api/hud/HudElement;", "miningHud", "Lorg/cobalt/api/hud/HudElement;", "getMiningHud", "()Lorg/cobalt/api/hud/HudElement;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nMiningHudModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MiningHudModule.kt\norg/cobalt/api/hud/modules/MiningHudModule\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,239:1\n1596#2:240\n1629#2,4:241\n1924#2,3:246\n1924#2,3:249\n1924#2,3:252\n1#3:245\n*S KotlinDebug\n*F\n+ 1 MiningHudModule.kt\norg/cobalt/api/hud/modules/MiningHudModule\n*L\n47#1:240\n47#1:241,4\n103#1:246,3\n114#1:249,3\n133#1:252,3\n*E\n"})
public final class MiningHudModule
extends Module {
    @NotNull
    public static final MiningHudModule INSTANCE = new MiningHudModule();
    private static final float textSize = 13.0f;
    private static final float lineHeight = textSize + 5.0f;
    private static final float padding = 10.0f;
    private static final float corner = 10.0f;
    private static final float sectionGap = 10.0f;
    private static final float sectionLabelGap = 6.0f;
    private static final float dividerGap = 8.0f;
    private static final int targetedColor = -9508206;
    @NotNull
    private static final HudElement miningHud = HudModuleDSLKt.hudElement(INSTANCE, "mining-hud", "Mining HUD", "Displays mining timing and buff stats", (Function1<? super HudElementBuilder, Unit>)((Function1)MiningHudModule::miningHud$lambda$0));

    private MiningHudModule() {
        super("Mining HUD");
    }

    private final int getLabelColor() {
        return ThemeManager.INSTANCE.getCurrentTheme().getText() & 0xFFFFFF | 0x99000000;
    }

    private final int getValueColor() {
        return ThemeManager.INSTANCE.getCurrentTheme().getText();
    }

    private final int getBorderColor1() {
        return ThemeManager.INSTANCE.getCurrentTheme().getAccent();
    }

    private final int getBorderColor2() {
        return ThemeManager.INSTANCE.getCurrentTheme().getAccentSecondary();
    }

    private final List<String> overlayRows() {
        return MiningModule.INSTANCE.buildOverlayRows();
    }

    private final List<String> buffRows() {
        return MiningModule.INSTANCE.buildBuffStatusRows();
    }

    private final List<CommissionMacroModule.CommissionHudRow> commissionRows() {
        return CollectionsKt.take((Iterable)CommissionMacroModule.INSTANCE.getCommissionRows(), (int)4);
    }

    private final boolean shouldShow() {
        return (Boolean)MiningModule.INSTANCE.getEnabled().getValue() != false || MiningModule.INSTANCE.isNukerActive() || MiningMacroModule.INSTANCE.isActive() || CommissionMacroModule.INSTANCE.isRunning() || !((Collection)this.commissionRows()).isEmpty();
    }

    /*
     * WARNING - void declaration
     */
    private final float panelWidth() {
        Float f;
        void $this$mapIndexedTo$iv$iv;
        void $this$mapIndexed$iv;
        String cph = this.formatCoinsPerHour(MiningProfitTracker.INSTANCE.coinsPerHour());
        String runtime = this.formatRuntime(MiningProfitTracker.INSTANCE.runtimeMs());
        Object[] objectArray = (Object[])this.commissionRows();
        Collection collection = CollectionsKt.plus((Collection)CollectionsKt.plus((Collection)CollectionsKt.plus((Collection)this.overlayRows(), (Iterable)CollectionsKt.listOf((Object)"BUFFS")), (Iterable)this.buffRows()), (Iterable)CollectionsKt.listOf((Object)"COMMISSIONS"));
        boolean $i$f$mapIndexed = false;
        Iterator iterator = $this$mapIndexed$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$mapIndexed$iv, (int)10));
        boolean $i$f$mapIndexedTo = false;
        int index$iv$iv = 0;
        for (Object item$iv$iv : $this$mapIndexedTo$iv$iv) {
            void row;
            void index;
            int n;
            if ((n = index$iv$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            CommissionMacroModule.CommissionHudRow commissionHudRow = (CommissionMacroModule.CommissionHudRow)item$iv$iv;
            int n2 = n;
            Collection collection2 = destination$iv$iv;
            boolean bl = false;
            collection2.add("Commission " + (int)(index + true) + ": " + row.getLabel() + " " + row.getDetail());
        }
        objectArray = new String[]{"MACRO", "Active: " + this.activeMacroLabel(), "Coins/hr: " + cph, "Runtime: " + runtime};
        List lines = CollectionsKt.plus((Collection)CollectionsKt.plus((Collection)collection, (Iterable)((List)destination$iv$iv)), (Iterable)CollectionsKt.listOf((Object[])objectArray));
        iterator = ((Iterable)lines).iterator();
        if (!iterator.hasNext()) {
            f = null;
        } else {
            String it = (String)iterator.next();
            boolean bl = false;
            float f2 = NVGRenderer.textWidth$default(it, textSize, null, 4, null);
            while (iterator.hasNext()) {
                String it2 = (String)iterator.next();
                $i$a$-maxOfOrNull-MiningHudModule$panelWidth$1 = false;
                float f3 = NVGRenderer.textWidth$default(it2, textSize, null, 4, null);
                f2 = Math.max(f2, f3);
            }
            f = Float.valueOf(f2);
        }
        return (f != null ? f.floatValue() : 180.0f) + padding * (float)2;
    }

    private final float panelHeight(boolean showProfit) {
        float rowsHeight = lineHeight * (float)this.overlayRows().size();
        float buffsHeight = lineHeight * (float)this.buffRows().size();
        List<CommissionMacroModule.CommissionHudRow> commissions = this.commissionRows();
        float commissionsHeight = commissions.isEmpty() ? 0.0f : dividerGap + sectionGap + sectionLabelGap + lineHeight * (float)commissions.size();
        float profitHeight = !showProfit ? 0.0f : dividerGap + sectionGap + sectionLabelGap + lineHeight * 3.0f;
        return padding * (float)2 + rowsHeight + dividerGap + sectionGap + sectionLabelGap + buffsHeight + commissionsHeight + profitHeight;
    }

    @NotNull
    public final HudElement getMiningHud() {
        return miningHud;
    }

    private final void renderCommissionRow(int index, CommissionMacroModule.CommissionHudRow row, float x, float y, float panelWidth, int rowColor) {
        String label = "Commission " + index + ":";
        String detail = row.getDetail();
        float detailWidth = NVGRenderer.textWidth$default(detail, textSize, null, 4, null);
        float titleMaxWidth = RangesKt.coerceAtLeast((float)(panelWidth - NVGRenderer.textWidth$default(label, textSize, null, 4, null) - detailWidth - 18.0f), (float)40.0f);
        String title = this.ellipsize(row.getLabel(), titleMaxWidth);
        int appliedLabelColor = rowColor == this.getValueColor() ? this.getLabelColor() : rowColor;
        NVGRenderer.text$default(label, x, y, textSize, appliedLabelColor, null, 32, null);
        float labelWidth = NVGRenderer.textWidth$default(label, textSize, null, 4, null);
        NVGRenderer.text$default(title, x + labelWidth + 6.0f, y, textSize, rowColor, null, 32, null);
        NVGRenderer.text$default(detail, x + panelWidth - detailWidth, y, textSize, rowColor, null, 32, null);
    }

    private final void renderRow(String row, float x, float y, int rowColor) {
        int separator = StringsKt.indexOf$default((CharSequence)row, (char)':', (int)0, (boolean)false, (int)6, null);
        if (separator < 0) {
            NVGRenderer.text$default(row, x, y, textSize, rowColor, null, 32, null);
            return;
        }
        String string = row.substring(0, separator + 1);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"substring(...)");
        String label = string;
        String string2 = row.substring(separator + 1);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"substring(...)");
        String value = ((Object)StringsKt.trimStart((CharSequence)string2)).toString();
        int appliedLabelColor = rowColor == this.getValueColor() ? this.getLabelColor() : rowColor;
        NVGRenderer.text$default(label, x, y, textSize, appliedLabelColor, null, 32, null);
        float labelWidth = NVGRenderer.textWidth$default(label, textSize, null, 4, null);
        NVGRenderer.text$default(value, x + labelWidth + 6.0f, y, textSize, rowColor, null, 32, null);
    }

    static /* synthetic */ void renderRow$default(MiningHudModule miningHudModule, String string, float f, float f2, int n, int n2, Object object) {
        if ((n2 & 8) != 0) {
            n = miningHudModule.getValueColor();
        }
        miningHudModule.renderRow(string, f, f2, n);
    }

    private final String ellipsize(String text, float maxWidth) {
        if (NVGRenderer.textWidth$default(text, textSize, null, 4, null) <= maxWidth) {
            return text;
        }
        for (int end = text.length(); end > 1; --end) {
            String candidate = ((Object)StringsKt.trimEnd((CharSequence)StringsKt.take((String)text, (int)end))).toString() + "...";
            if (!(NVGRenderer.textWidth$default(candidate, textSize, null, 4, null) <= maxWidth)) continue;
            return candidate;
        }
        return "...";
    }

    private final String activeMacroLabel() {
        return RoutesModule.INSTANCE.isRunning() && RoutesModule.INSTANCE.getRouteOwnsMining() ? "Routes" : (CommissionMacroModule.INSTANCE.isRunning() ? "Commission Macro" : (MiningModule.INSTANCE.isNukerActive() ? "Mining Nuker" : (MiningMacroModule.INSTANCE.isActive() ? "Mining Macro" : "\u2014")));
    }

    private final String formatRuntime(long ms) {
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

    private final String formatCoinsPerHour(long cph) {
        Object object;
        String prefix = cph < 0L ? "-" : "";
        long abs = Math.abs(cph);
        if (abs >= 1000000L) {
            String string = "%.1f";
            Object[] objectArray = new Object[]{(double)abs / 1000000.0};
            String string2 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"format(...)");
            object = prefix + string2 + "M";
        } else if (abs >= 1000L) {
            String string = "%.1f";
            Object[] objectArray = new Object[]{(double)abs / 1000.0};
            String string3 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"format(...)");
            object = prefix + string3 + "K";
        } else {
            object = String.valueOf(cph);
        }
        return object;
    }

    private static final float miningHud$lambda$0$0() {
        return INSTANCE.panelWidth();
    }

    private static final float miningHud$lambda$0$1(CheckboxSetting $showProfit) {
        return INSTANCE.panelHeight((Boolean)$showProfit.getValue());
    }

    /*
     * WARNING - void declaration
     */
    private static final Unit miningHud$lambda$0$2(CheckboxSetting $onlyWhenActive, CheckboxSetting $showProfit, float x, float y, float f) {
        int n;
        if (((Boolean)$onlyWhenActive.getValue()).booleanValue() && !INSTANCE.shouldShow()) {
            return Unit.INSTANCE;
        }
        List<String> rows = INSTANCE.overlayRows();
        List<String> buffs = INSTANCE.buffRows();
        List<CommissionMacroModule.CommissionHudRow> commissions = INSTANCE.commissionRows();
        float panelW = INSTANCE.panelWidth();
        float panelH = INSTANCE.panelHeight((Boolean)$showProfit.getValue());
        long now = System.currentTimeMillis();
        float twoPi = (float)Math.PI * 2;
        NVGRenderer.rect(x, y, panelW, panelH, -16118246, corner);
        NVGRenderer.gradientRect(x, y, panelW, panelH * 0.5f, 0x14FFFFFF, 0, Gradient.TopToBottom, corner);
        float angle = (float)(now % 10000L) / 10000.0f * twoPi;
        float shiftX = (float)Math.cos(angle) * (panelW * 0.42f);
        NVGRenderer.hollowGradientRectShifted(x, y, panelW, panelH, 1.5f, INSTANCE.getBorderColor1(), INSTANCE.getBorderColor2(), Gradient.LeftToRight, corner, shiftX, 0.0f);
        Iterable $this$forEachIndexed$iv = rows;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            Iterator row;
            int n2;
            if ((n2 = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            String string = (String)item$iv;
            int index = n2;
            n = 0;
            MiningHudModule.renderRow$default(INSTANCE, (String)((Object)row), x + padding, y + padding + (float)index * lineHeight, 0, 8, null);
        }
        float dividerY = y + padding + (float)rows.size() * lineHeight + dividerGap / 2.0f;
        NVGRenderer.line(x + padding, dividerY, x + panelW - padding, dividerY, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder());
        float buffsLabelY = dividerY + sectionGap;
        NVGRenderer.text$default("BUFFS", x + padding, buffsLabelY, 11.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
        float buffsStartY = buffsLabelY + sectionLabelGap + 2.0f;
        Iterable $this$forEachIndexed$iv2 = buffs;
        boolean $i$f$forEachIndexed2 = false;
        int index$iv2 = 0;
        for (Object item$iv : $this$forEachIndexed$iv2) {
            void row;
            if ((n = index$iv2++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            String string = (String)item$iv;
            int index = n;
            boolean bl = false;
            MiningHudModule.renderRow$default(INSTANCE, (String)row, x + padding, buffsStartY + (float)index * lineHeight, 0, 8, null);
        }
        if (!((Collection)commissions).isEmpty()) {
            float commissionsDividerY = buffsStartY + (float)buffs.size() * lineHeight + dividerGap / 2.0f;
            NVGRenderer.line(x + padding, commissionsDividerY, x + panelW - padding, commissionsDividerY, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder());
            float commissionsLabelY = commissionsDividerY + sectionGap;
            NVGRenderer.text$default("COMMISSIONS", x + padding, commissionsLabelY, 11.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
            float commissionsStartY = commissionsLabelY + sectionLabelGap + 2.0f;
            Iterable $this$forEachIndexed$iv3 = commissions;
            boolean $i$f$forEachIndexed3 = false;
            int index$iv3 = 0;
            for (Object item$iv : $this$forEachIndexed$iv3) {
                void row;
                int n3;
                if ((n3 = index$iv3++) < 0) {
                    CollectionsKt.throwIndexOverflow();
                }
                CommissionMacroModule.CommissionHudRow commissionHudRow = (CommissionMacroModule.CommissionHudRow)item$iv;
                int index = n3;
                boolean bl = false;
                INSTANCE.renderCommissionRow(index + 1, (CommissionMacroModule.CommissionHudRow)row, x + padding, commissionsStartY + (float)index * lineHeight, panelW - padding * (float)2, row.isTargeted() ? targetedColor : INSTANCE.getValueColor());
            }
        }
        if (((Boolean)$showProfit.getValue()).booleanValue()) {
            float macroStartY = y + panelH - padding - lineHeight * 3.0f;
            float macroLabelY = macroStartY - sectionLabelGap - 2.0f;
            float profitDividerY = macroLabelY - sectionGap - dividerGap / 2.0f;
            NVGRenderer.line(x + padding, profitDividerY, x + panelW - padding, profitDividerY, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder());
            NVGRenderer.text$default("MACRO", x + padding, macroLabelY, 11.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
            String cph = INSTANCE.formatCoinsPerHour(MiningProfitTracker.INSTANCE.coinsPerHour());
            String runtime = INSTANCE.formatRuntime(MiningProfitTracker.INSTANCE.runtimeMs());
            MiningHudModule.renderRow$default(INSTANCE, "Active: " + INSTANCE.activeMacroLabel(), x + padding, macroStartY, 0, 8, null);
            MiningHudModule.renderRow$default(INSTANCE, "Coins/hr: " + cph, x + padding, macroStartY + lineHeight, 0, 8, null);
            MiningHudModule.renderRow$default(INSTANCE, "Runtime: " + runtime, x + padding, macroStartY + lineHeight * 2.0f, 0, 8, null);
        }
        return Unit.INSTANCE;
    }

    private static final Unit miningHud$lambda$0(HudElementBuilder $this$hudElement) {
        Intrinsics.checkNotNullParameter((Object)$this$hudElement, (String)"$this$hudElement");
        $this$hudElement.setAnchor(HudAnchor.TOP_LEFT);
        $this$hudElement.setOffsetX(10.0f);
        $this$hudElement.setOffsetY(80.0f);
        CheckboxSetting onlyWhenActive = (CheckboxSetting)$this$hudElement.setting((Setting)new CheckboxSetting("Only When Active", "Hide when mining tracking is idle", false));
        CheckboxSetting showProfit = (CheckboxSetting)$this$hudElement.setting((Setting)new CheckboxSetting("Show Profit", "Show active macro, coins/hr, and runtime.", true));
        $this$hudElement.width((Function0<Float>)((Function0)MiningHudModule::miningHud$lambda$0$0));
        $this$hudElement.height((Function0<Float>)((Function0)() -> MiningHudModule.miningHud$lambda$0$1(showProfit)));
        $this$hudElement.render((Function3<? super Float, ? super Float, ? super Float, Unit>)((Function3)(arg_0, arg_1, arg_2) -> MiningHudModule.miningHud$lambda$0$2(onlyWhenActive, showProfit, arg_0, arg_1, arg_2)));
        return Unit.INSTANCE;
    }
}

