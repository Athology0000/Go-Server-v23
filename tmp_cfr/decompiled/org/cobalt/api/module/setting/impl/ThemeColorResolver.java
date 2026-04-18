/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.module.setting.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.ui.theme.Theme;
import org.cobalt.api.ui.theme.ThemeManager;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0013\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00040\t\u00a2\u0006\u0004\b\n\u0010\u000bR)\u0010\u000e\u001a\u0014\u0012\u0004\u0012\u00020\u0004\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\r0\f8\u0006\u00a2\u0006\f\n\u0004\b\u000e\u0010\u000f\u001a\u0004\b\u0010\u0010\u0011R,\u0010\u0014\u001a\u001a\u0012\u0004\u0012\u00020\u0004\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00060\u00120\f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0014\u0010\u000f\u00a8\u0006\u0015"}, d2={"Lorg/cobalt/api/module/setting/impl/ThemeColorResolver;", "", "<init>", "()V", "", "propertyName", "", "resolve", "(Ljava/lang/String;)I", "", "getPropertyNames", "()Ljava/util/Set;", "", "", "groups", "Ljava/util/Map;", "getGroups", "()Ljava/util/Map;", "Lkotlin/Function1;", "Lorg/cobalt/api/ui/theme/Theme;", "resolvers", "cobalt"})
public final class ThemeColorResolver {
    @NotNull
    public static final ThemeColorResolver INSTANCE = new ThemeColorResolver();
    @NotNull
    private static final Map<String, List<String>> groups;
    @NotNull
    private static final Map<String, Function1<Theme, Integer>> resolvers;

    private ThemeColorResolver() {
    }

    @NotNull
    public final Map<String, List<String>> getGroups() {
        return groups;
    }

    public final int resolve(@NotNull String propertyName) {
        Intrinsics.checkNotNullParameter((Object)propertyName, (String)"propertyName");
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        Function1<Theme, Integer> function1 = resolvers.get(propertyName);
        if (function1 == null) {
            Function1<Theme, Integer> function12 = resolvers.get("accent");
            Intrinsics.checkNotNull(function12);
            function1 = function12;
        }
        Function1<Theme, Integer> resolver = function1;
        return ((Number)resolver.invoke((Object)theme)).intValue();
    }

    @NotNull
    public final Set<String> getPropertyNames() {
        return resolvers.keySet();
    }

    private static final int resolvers$lambda$0(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getBackground();
    }

    private static final int resolvers$lambda$1(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getPanel();
    }

    private static final int resolvers$lambda$2(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getInset();
    }

    private static final int resolvers$lambda$3(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getOverlay();
    }

    private static final int resolvers$lambda$4(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getText();
    }

    private static final int resolvers$lambda$5(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getTextPrimary();
    }

    private static final int resolvers$lambda$6(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getTextSecondary();
    }

    private static final int resolvers$lambda$7(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getTextDisabled();
    }

    private static final int resolvers$lambda$8(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getTextPlaceholder();
    }

    private static final int resolvers$lambda$9(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getTextOnAccent();
    }

    private static final int resolvers$lambda$10(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getSelectionText();
    }

    private static final int resolvers$lambda$11(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getSearchPlaceholderText();
    }

    private static final int resolvers$lambda$12(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getAccent();
    }

    private static final int resolvers$lambda$13(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getAccentPrimary();
    }

    private static final int resolvers$lambda$14(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getAccentSecondary();
    }

    private static final int resolvers$lambda$15(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getSelection();
    }

    private static final int resolvers$lambda$16(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getSelectedOverlay();
    }

    private static final int resolvers$lambda$17(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getControlBg();
    }

    private static final int resolvers$lambda$18(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getControlBorder();
    }

    private static final int resolvers$lambda$19(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getInputBg();
    }

    private static final int resolvers$lambda$20(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getInputBorder();
    }

    private static final int resolvers$lambda$21(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getSuccess();
    }

    private static final int resolvers$lambda$22(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getWarning();
    }

    private static final int resolvers$lambda$23(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getError();
    }

    private static final int resolvers$lambda$24(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getInfo();
    }

    private static final int resolvers$lambda$25(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getScrollbarThumb();
    }

    private static final int resolvers$lambda$26(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getScrollbarTrack();
    }

    private static final int resolvers$lambda$27(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getSliderTrack();
    }

    private static final int resolvers$lambda$28(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getSliderFill();
    }

    private static final int resolvers$lambda$29(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getSliderThumb();
    }

    private static final int resolvers$lambda$30(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getTooltipBackground();
    }

    private static final int resolvers$lambda$31(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getTooltipBorder();
    }

    private static final int resolvers$lambda$32(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getTooltipText();
    }

    private static final int resolvers$lambda$33(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getNotificationBackground();
    }

    private static final int resolvers$lambda$34(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getNotificationBorder();
    }

    private static final int resolvers$lambda$35(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getNotificationText();
    }

    private static final int resolvers$lambda$36(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getNotificationTextSecondary();
    }

    private static final int resolvers$lambda$37(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getInfoBackground();
    }

    private static final int resolvers$lambda$38(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getInfoBorder();
    }

    private static final int resolvers$lambda$39(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getInfoIcon();
    }

    private static final int resolvers$lambda$40(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getWarningBackground();
    }

    private static final int resolvers$lambda$41(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getWarningBorder();
    }

    private static final int resolvers$lambda$42(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getWarningIcon();
    }

    private static final int resolvers$lambda$43(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getSuccessBackground();
    }

    private static final int resolvers$lambda$44(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getSuccessBorder();
    }

    private static final int resolvers$lambda$45(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getSuccessIcon();
    }

    private static final int resolvers$lambda$46(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getErrorBackground();
    }

    private static final int resolvers$lambda$47(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getErrorBorder();
    }

    private static final int resolvers$lambda$48(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getErrorIcon();
    }

    private static final int resolvers$lambda$49(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getModuleDivider();
    }

    private static final int resolvers$lambda$50(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getWhite();
    }

    private static final int resolvers$lambda$51(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getBlack();
    }

    private static final int resolvers$lambda$52(Theme it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getTransparent();
    }

    static {
        Pair[] pairArray = new Pair[11];
        Object[] objectArray = new String[]{"background", "panel", "inset", "overlay"};
        pairArray[0] = TuplesKt.to((Object)"Base", (Object)CollectionsKt.listOf((Object[])objectArray));
        objectArray = new String[]{"text", "textPrimary", "textSecondary", "textDisabled", "textPlaceholder", "textOnAccent", "selectionText", "searchPlaceholderText"};
        pairArray[1] = TuplesKt.to((Object)"Text", (Object)CollectionsKt.listOf((Object[])objectArray));
        objectArray = new String[]{"accent", "accentPrimary", "accentSecondary", "selection", "selectedOverlay"};
        pairArray[2] = TuplesKt.to((Object)"Accent", (Object)CollectionsKt.listOf((Object[])objectArray));
        objectArray = new String[]{"controlBg", "controlBorder", "inputBg", "inputBorder"};
        pairArray[3] = TuplesKt.to((Object)"Controls", (Object)CollectionsKt.listOf((Object[])objectArray));
        objectArray = new String[]{"success", "warning", "error", "info"};
        pairArray[4] = TuplesKt.to((Object)"Status", (Object)CollectionsKt.listOf((Object[])objectArray));
        objectArray = new String[]{"scrollbarThumb", "scrollbarTrack"};
        pairArray[5] = TuplesKt.to((Object)"Scrollbar", (Object)CollectionsKt.listOf((Object[])objectArray));
        objectArray = new String[]{"sliderTrack", "sliderFill", "sliderThumb"};
        pairArray[6] = TuplesKt.to((Object)"Slider", (Object)CollectionsKt.listOf((Object[])objectArray));
        objectArray = new String[]{"tooltipBackground", "tooltipBorder", "tooltipText"};
        pairArray[7] = TuplesKt.to((Object)"Tooltip", (Object)CollectionsKt.listOf((Object[])objectArray));
        objectArray = new String[]{"notificationBackground", "notificationBorder", "notificationText", "notificationTextSecondary"};
        pairArray[8] = TuplesKt.to((Object)"Notification", (Object)CollectionsKt.listOf((Object[])objectArray));
        objectArray = new String[]{"infoBackground", "infoBorder", "infoIcon", "warningBackground", "warningBorder", "warningIcon", "successBackground", "successBorder", "successIcon", "errorBackground", "errorBorder", "errorIcon"};
        pairArray[9] = TuplesKt.to((Object)"Status BG", (Object)CollectionsKt.listOf((Object[])objectArray));
        objectArray = new String[]{"moduleDivider", "white", "black", "transparent"};
        pairArray[10] = TuplesKt.to((Object)"Other", (Object)CollectionsKt.listOf((Object[])objectArray));
        groups = MapsKt.linkedMapOf((Pair[])pairArray);
        pairArray = new Pair[]{TuplesKt.to((Object)"background", ThemeColorResolver::resolvers$lambda$0), TuplesKt.to((Object)"panel", ThemeColorResolver::resolvers$lambda$1), TuplesKt.to((Object)"inset", ThemeColorResolver::resolvers$lambda$2), TuplesKt.to((Object)"overlay", ThemeColorResolver::resolvers$lambda$3), TuplesKt.to((Object)"text", ThemeColorResolver::resolvers$lambda$4), TuplesKt.to((Object)"textPrimary", ThemeColorResolver::resolvers$lambda$5), TuplesKt.to((Object)"textSecondary", ThemeColorResolver::resolvers$lambda$6), TuplesKt.to((Object)"textDisabled", ThemeColorResolver::resolvers$lambda$7), TuplesKt.to((Object)"textPlaceholder", ThemeColorResolver::resolvers$lambda$8), TuplesKt.to((Object)"textOnAccent", ThemeColorResolver::resolvers$lambda$9), TuplesKt.to((Object)"selectionText", ThemeColorResolver::resolvers$lambda$10), TuplesKt.to((Object)"searchPlaceholderText", ThemeColorResolver::resolvers$lambda$11), TuplesKt.to((Object)"accent", ThemeColorResolver::resolvers$lambda$12), TuplesKt.to((Object)"accentPrimary", ThemeColorResolver::resolvers$lambda$13), TuplesKt.to((Object)"accentSecondary", ThemeColorResolver::resolvers$lambda$14), TuplesKt.to((Object)"selection", ThemeColorResolver::resolvers$lambda$15), TuplesKt.to((Object)"selectedOverlay", ThemeColorResolver::resolvers$lambda$16), TuplesKt.to((Object)"controlBg", ThemeColorResolver::resolvers$lambda$17), TuplesKt.to((Object)"controlBorder", ThemeColorResolver::resolvers$lambda$18), TuplesKt.to((Object)"inputBg", ThemeColorResolver::resolvers$lambda$19), TuplesKt.to((Object)"inputBorder", ThemeColorResolver::resolvers$lambda$20), TuplesKt.to((Object)"success", ThemeColorResolver::resolvers$lambda$21), TuplesKt.to((Object)"warning", ThemeColorResolver::resolvers$lambda$22), TuplesKt.to((Object)"error", ThemeColorResolver::resolvers$lambda$23), TuplesKt.to((Object)"info", ThemeColorResolver::resolvers$lambda$24), TuplesKt.to((Object)"scrollbarThumb", ThemeColorResolver::resolvers$lambda$25), TuplesKt.to((Object)"scrollbarTrack", ThemeColorResolver::resolvers$lambda$26), TuplesKt.to((Object)"sliderTrack", ThemeColorResolver::resolvers$lambda$27), TuplesKt.to((Object)"sliderFill", ThemeColorResolver::resolvers$lambda$28), TuplesKt.to((Object)"sliderThumb", ThemeColorResolver::resolvers$lambda$29), TuplesKt.to((Object)"tooltipBackground", ThemeColorResolver::resolvers$lambda$30), TuplesKt.to((Object)"tooltipBorder", ThemeColorResolver::resolvers$lambda$31), TuplesKt.to((Object)"tooltipText", ThemeColorResolver::resolvers$lambda$32), TuplesKt.to((Object)"notificationBackground", ThemeColorResolver::resolvers$lambda$33), TuplesKt.to((Object)"notificationBorder", ThemeColorResolver::resolvers$lambda$34), TuplesKt.to((Object)"notificationText", ThemeColorResolver::resolvers$lambda$35), TuplesKt.to((Object)"notificationTextSecondary", ThemeColorResolver::resolvers$lambda$36), TuplesKt.to((Object)"infoBackground", ThemeColorResolver::resolvers$lambda$37), TuplesKt.to((Object)"infoBorder", ThemeColorResolver::resolvers$lambda$38), TuplesKt.to((Object)"infoIcon", ThemeColorResolver::resolvers$lambda$39), TuplesKt.to((Object)"warningBackground", ThemeColorResolver::resolvers$lambda$40), TuplesKt.to((Object)"warningBorder", ThemeColorResolver::resolvers$lambda$41), TuplesKt.to((Object)"warningIcon", ThemeColorResolver::resolvers$lambda$42), TuplesKt.to((Object)"successBackground", ThemeColorResolver::resolvers$lambda$43), TuplesKt.to((Object)"successBorder", ThemeColorResolver::resolvers$lambda$44), TuplesKt.to((Object)"successIcon", ThemeColorResolver::resolvers$lambda$45), TuplesKt.to((Object)"errorBackground", ThemeColorResolver::resolvers$lambda$46), TuplesKt.to((Object)"errorBorder", ThemeColorResolver::resolvers$lambda$47), TuplesKt.to((Object)"errorIcon", ThemeColorResolver::resolvers$lambda$48), TuplesKt.to((Object)"moduleDivider", ThemeColorResolver::resolvers$lambda$49), TuplesKt.to((Object)"white", ThemeColorResolver::resolvers$lambda$50), TuplesKt.to((Object)"black", ThemeColorResolver::resolvers$lambda$51), TuplesKt.to((Object)"transparent", ThemeColorResolver::resolvers$lambda$52)};
        resolvers = MapsKt.mapOf((Pair[])pairArray);
    }
}

