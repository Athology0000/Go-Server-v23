/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function3
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.mining;

import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
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
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J!\u0010\u0007\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u00050\u0004H\u0002\u00a2\u0006\u0004\b\u0007\u0010\bR\u0014\u0010\n\u001a\u00020\t8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\n\u0010\u000bR\u0014\u0010\f\u001a\u00020\t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\f\u0010\u000bR\u0014\u0010\r\u001a\u00020\t8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\r\u0010\u000bR\u0014\u0010\u000e\u001a\u00020\t8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u000e\u0010\u000bR\u0014\u0010\u0012\u001a\u00020\u000f8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011R\u0014\u0010\u0014\u001a\u00020\u000f8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0013\u0010\u0011R\u0014\u0010\u0016\u001a\u00020\u000f8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0015\u0010\u0011R\u0014\u0010\u0018\u001a\u00020\u000f8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0017\u0010\u0011R\u0017\u0010\u001a\u001a\u00020\u00198\u0006\u00a2\u0006\f\n\u0004\b\u001a\u0010\u001b\u001a\u0004\b\u001c\u0010\u001d\u00a8\u0006\u001e"}, d2={"Lorg/cobalt/internal/mining/CommissionHudModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "", "Lkotlin/Pair;", "", "rows", "()Ljava/util/List;", "", "textSize", "F", "lineHeight", "padding", "corner", "", "getLabelColor", "()I", "labelColor", "getValueColor", "valueColor", "getBorderColor1", "borderColor1", "getBorderColor2", "borderColor2", "Lorg/cobalt/api/hud/HudElement;", "commissionHud", "Lorg/cobalt/api/hud/HudElement;", "getCommissionHud", "()Lorg/cobalt/api/hud/HudElement;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nCommissionHudModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CommissionHudModule.kt\norg/cobalt/internal/mining/CommissionHudModule\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,72:1\n1#2:73\n1924#3,3:74\n*S KotlinDebug\n*F\n+ 1 CommissionHudModule.kt\norg/cobalt/internal/mining/CommissionHudModule\n*L\n62#1:74,3\n*E\n"})
public final class CommissionHudModule
extends Module {
    @NotNull
    public static final CommissionHudModule INSTANCE = new CommissionHudModule();
    private static final float textSize = 13.0f;
    private static final float lineHeight = textSize + 5.0f;
    private static final float padding = 10.0f;
    private static final float corner = 10.0f;
    @NotNull
    private static final HudElement commissionHud = HudModuleDSLKt.hudElement(INSTANCE, "commission-hud", "Commission HUD", "Tracks commission macro status", (Function1<? super HudElementBuilder, Unit>)((Function1)CommissionHudModule::commissionHud$lambda$0));

    private CommissionHudModule() {
        super("Commission HUD");
    }

    private final int getLabelColor() {
        return ThemeManager.INSTANCE.getCurrentTheme().getText() & 0xFFFFFF | 0x99000000;
    }

    private final int getValueColor() {
        return ThemeManager.INSTANCE.getCurrentTheme().getAccent();
    }

    private final int getBorderColor1() {
        return ThemeManager.INSTANCE.getCurrentTheme().getAccent();
    }

    private final int getBorderColor2() {
        return ThemeManager.INSTANCE.getCurrentTheme().getAccentSecondary();
    }

    private final List<Pair<String, String>> rows() {
        Object[] objectArray = new Pair[]{TuplesKt.to((Object)"Status", (Object)CommissionMacroModule.INSTANCE.getStatusDisplay()), TuplesKt.to((Object)"Mode", (Object)CommissionMacroModule.INSTANCE.getModeDisplay()), TuplesKt.to((Object)"Commission", (Object)CommissionMacroModule.INSTANCE.getCommissionDisplay()), TuplesKt.to((Object)"Zone", (Object)CommissionMacroModule.INSTANCE.getCurrentZoneDisplay()), TuplesKt.to((Object)"Target", (Object)CommissionMacroModule.INSTANCE.getTargetZoneDisplay())};
        return CollectionsKt.listOf((Object[])objectArray);
    }

    @NotNull
    public final HudElement getCommissionHud() {
        return commissionHud;
    }

    private static final float commissionHud$lambda$0$0() {
        Float f;
        Iterator iterator = ((Iterable)INSTANCE.rows()).iterator();
        if (!iterator.hasNext()) {
            f = null;
        } else {
            Pair pair = (Pair)iterator.next();
            boolean bl = false;
            String l = (String)pair.component1();
            String v = (String)pair.component2();
            float f2 = NVGRenderer.textWidth$default(l + ": " + v, textSize, null, 4, null);
            while (iterator.hasNext()) {
                Pair pair2 = (Pair)iterator.next();
                $i$a$-maxOfOrNull-CommissionHudModule$commissionHud$1$1$1 = false;
                String l2 = (String)pair2.component1();
                String v2 = (String)pair2.component2();
                float f3 = NVGRenderer.textWidth$default(l2 + ": " + v2, textSize, null, 4, null);
                f2 = Math.max(f2, f3);
            }
            f = Float.valueOf(f2);
        }
        return (f != null ? f.floatValue() : 120.0f) + padding * (float)2;
    }

    private static final float commissionHud$lambda$0$1() {
        return lineHeight * (float)INSTANCE.rows().size() + padding * (float)2;
    }

    private static final Unit commissionHud$lambda$0$2(CheckboxSetting $onlyWhenActive, float x, float y, float f) {
        Float f2;
        if (((Boolean)$onlyWhenActive.getValue()).booleanValue() && !CommissionMacroModule.INSTANCE.isRunning()) {
            return Unit.INSTANCE;
        }
        List<Pair<String, String>> r = INSTANCE.rows();
        Iterator iterator = ((Iterable)r).iterator();
        if (!iterator.hasNext()) {
            f2 = null;
        } else {
            Pair pair = (Pair)iterator.next();
            boolean $i$a$-maxOfOrNull-CommissionHudModule$commissionHud$1$3$panelW$322 = false;
            String l = (String)pair.component1();
            String v = (String)pair.component2();
            float f3 = NVGRenderer.textWidth$default(l + ": " + v, textSize, null, 4, null);
            while (iterator.hasNext()) {
                Pair $i$a$-maxOfOrNull-CommissionHudModule$commissionHud$1$3$panelW$322 = (Pair)iterator.next();
                boolean $i$a$-maxOfOrNull-CommissionHudModule$commissionHud$1$3$panelW$2 = false;
                String l2 = (String)$i$a$-maxOfOrNull-CommissionHudModule$commissionHud$1$3$panelW$322.component1();
                String v2 = (String)$i$a$-maxOfOrNull-CommissionHudModule$commissionHud$1$3$panelW$322.component2();
                float $i$a$-maxOfOrNull-CommissionHudModule$commissionHud$1$3$panelW$322 = NVGRenderer.textWidth$default(l2 + ": " + v2, textSize, null, 4, null);
                f3 = Math.max(f3, $i$a$-maxOfOrNull-CommissionHudModule$commissionHud$1$3$panelW$322);
            }
            f2 = Float.valueOf(f3);
        }
        float panelW = (f2 != null ? f2.floatValue() : 120.0f) + padding * (float)2;
        float panelH = lineHeight * (float)r.size() + padding * (float)2;
        long now = System.currentTimeMillis();
        float twoPi = (float)Math.PI * 2;
        NVGRenderer.rect(x, y, panelW, panelH, -16118246, corner);
        NVGRenderer.gradientRect(x, y, panelW, panelH * 0.5f, 0x14FFFFFF, 0, Gradient.TopToBottom, corner);
        float angle = (float)(now % 10000L) / 10000.0f * twoPi;
        float shiftX = (float)Math.cos(angle) * (panelW * 0.42f);
        NVGRenderer.hollowGradientRectShifted(x, y, panelW, panelH, 1.5f, INSTANCE.getBorderColor1(), INSTANCE.getBorderColor2(), Gradient.LeftToRight, corner, shiftX, 0.0f);
        Iterable $this$forEachIndexed$iv = r;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            Pair pair = (Pair)item$iv;
            int i = n;
            boolean bl = false;
            String label = (String)pair.component1();
            String value = (String)pair.component2();
            float ty = y + padding + (float)i * lineHeight;
            String labelStr = label + ": ";
            NVGRenderer.text$default(labelStr, x + padding, ty, textSize, INSTANCE.getLabelColor(), null, 32, null);
            float labelW = NVGRenderer.textWidth$default(labelStr, textSize, null, 4, null);
            NVGRenderer.text$default(value, x + padding + labelW, ty, textSize, INSTANCE.getValueColor(), null, 32, null);
        }
        return Unit.INSTANCE;
    }

    private static final Unit commissionHud$lambda$0(HudElementBuilder $this$hudElement) {
        Intrinsics.checkNotNullParameter((Object)$this$hudElement, (String)"$this$hudElement");
        $this$hudElement.setAnchor(HudAnchor.TOP_LEFT);
        $this$hudElement.setOffsetX(10.0f);
        $this$hudElement.setOffsetY(80.0f);
        CheckboxSetting onlyWhenActive = (CheckboxSetting)$this$hudElement.setting((Setting)new CheckboxSetting("Only When Active", "Hide when macro is off", false));
        $this$hudElement.width((Function0<Float>)((Function0)CommissionHudModule::commissionHud$lambda$0$0));
        $this$hudElement.height((Function0<Float>)((Function0)CommissionHudModule::commissionHud$lambda$0$1));
        $this$hudElement.render((Function3<? super Float, ? super Float, ? super Float, Unit>)((Function3)(arg_0, arg_1, arg_2) -> CommissionHudModule.commissionHud$lambda$0$2(onlyWhenActive, arg_0, arg_1, arg_2)));
        return Unit.INSTANCE;
    }
}

