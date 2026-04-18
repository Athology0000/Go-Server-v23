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
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.combat;

import java.util.Collection;
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
import kotlin.ranges.RangesKt;
import org.cobalt.api.hud.HudAnchor;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.hud.HudElementBuilder;
import org.cobalt.api.hud.HudModuleDSLKt;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Gradient;
import org.cobalt.internal.combat.CombatMacroModule;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J!\u0010\u0007\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u00050\u0004H\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ'\u0010\u000e\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b\u000e\u0010\u000fR\u0014\u0010\u0010\u001a\u00020\f8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u0010\u0010\u0011R\u0014\u0010\u0012\u001a\u00020\f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0011R\u0014\u0010\u0013\u001a\u00020\f8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u0013\u0010\u0011R\u0014\u0010\u0014\u001a\u00020\f8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u0014\u0010\u0011R\u0014\u0010\u0015\u001a\u00020\f8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u0015\u0010\u0011R\u0014\u0010\u0016\u001a\u00020\f8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u0016\u0010\u0011R\u0014\u0010\u0017\u001a\u00020\f8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u0017\u0010\u0011R\u0014\u0010\u0018\u001a\u00020\f8\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0011R\u0014\u0010\u001b\u001a\u00020\t8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0019\u0010\u001aR\u0014\u0010\u001d\u001a\u00020\t8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u001c\u0010\u001aR\u0014\u0010\u001f\u001a\u00020\t8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u001e\u0010\u001aR\u0017\u0010!\u001a\u00020 8\u0006\u00a2\u0006\f\n\u0004\b!\u0010\"\u001a\u0004\b#\u0010$\u00a8\u0006%"}, d2={"Lorg/cobalt/internal/combat/CombatHudModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "", "Lkotlin/Pair;", "", "rows", "()Ljava/util/List;", "", "start", "end", "", "t", "lerpColor", "(IIF)I", "textSize", "F", "lineHeight", "padding", "barHeight", "barLabelGap", "barTopGap", "corner", "minBarWidth", "getLabelColor", "()I", "labelColor", "getBorderColor1", "borderColor1", "getBorderColor2", "borderColor2", "Lorg/cobalt/api/hud/HudElement;", "combatHud", "Lorg/cobalt/api/hud/HudElement;", "getCombatHud", "()Lorg/cobalt/api/hud/HudElement;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nCombatHudModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CombatHudModule.kt\norg/cobalt/internal/combat/CombatHudModule\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,128:1\n1#2:129\n1924#3,3:130\n*S KotlinDebug\n*F\n+ 1 CombatHudModule.kt\norg/cobalt/internal/combat/CombatHudModule\n*L\n83#1:130,3\n*E\n"})
public final class CombatHudModule
extends Module {
    @NotNull
    public static final CombatHudModule INSTANCE = new CombatHudModule();
    private static final float textSize = 13.0f;
    private static final float lineHeight = textSize + 5.0f;
    private static final float padding = 10.0f;
    private static final float barHeight = 12.0f;
    private static final float barLabelGap = 8.0f;
    private static final float barTopGap = 10.0f;
    private static final float corner = 10.0f;
    private static final float minBarWidth = 210.0f;
    @NotNull
    private static final HudElement combatHud = HudModuleDSLKt.hudElement(INSTANCE, "combat-hud", "Combat HUD", "Tracks combat macro status", (Function1<? super HudElementBuilder, Unit>)((Function1)CombatHudModule::combatHud$lambda$0));

    private CombatHudModule() {
        super("Combat HUD");
    }

    private final int getLabelColor() {
        return ThemeManager.INSTANCE.getCurrentTheme().getText() & 0xFFFFFF | 0x99000000;
    }

    private final int getBorderColor1() {
        return ThemeManager.INSTANCE.getCurrentTheme().getAccent();
    }

    private final int getBorderColor2() {
        return ThemeManager.INSTANCE.getCurrentTheme().getAccentSecondary();
    }

    private final List<Pair<String, String>> rows() {
        Object object = new Pair[]{TuplesKt.to((Object)"Status", (Object)CombatMacroModule.INSTANCE.getStatusDisplay()), TuplesKt.to((Object)"Mode", (Object)CombatMacroModule.INSTANCE.getModeDisplay()), TuplesKt.to((Object)"Target", (Object)CombatMacroModule.INSTANCE.getTargetDisplay()), TuplesKt.to((Object)"Slayer", (Object)CombatMacroModule.INSTANCE.getSlayerDisplay())};
        List rows = CollectionsKt.mutableListOf((Object[])object);
        if (CombatMacroModule.INSTANCE.isSlayerHudVisible()) {
            object = rows;
            Object[] objectArray = new Pair[]{TuplesKt.to((Object)"Quest", (Object)CombatMacroModule.INSTANCE.getSlayerQuestLevelDisplay()), TuplesKt.to((Object)"Quest State", (Object)CombatMacroModule.INSTANCE.getSlayerQuestStateDisplay()), TuplesKt.to((Object)"Kills Left", (Object)CombatMacroModule.INSTANCE.getSlayerKillsLeftDisplay()), TuplesKt.to((Object)"Kills/hr", (Object)CombatMacroModule.INSTANCE.getSlayerKillsPerHourDisplay()), TuplesKt.to((Object)"Quests Done", (Object)CombatMacroModule.INSTANCE.getSlayerQuestsCompletedDisplay()), TuplesKt.to((Object)"Quests/hr", (Object)CombatMacroModule.INSTANCE.getSlayerQuestsPerHourDisplay()), TuplesKt.to((Object)"Quests Failed", (Object)CombatMacroModule.INSTANCE.getSlayerQuestsFailedDisplay()), TuplesKt.to((Object)"Fails/hr", (Object)CombatMacroModule.INSTANCE.getSlayerQuestFailsPerHourDisplay())};
            CollectionsKt.addAll((Collection)object, (Iterable)CollectionsKt.listOf((Object[])objectArray));
        }
        return rows;
    }

    @NotNull
    public final HudElement getCombatHud() {
        return combatHud;
    }

    private final int lerpColor(int start, int end, float t) {
        float c = RangesKt.coerceIn((float)t, (float)0.0f, (float)1.0f);
        int a = (start >>> 24 & 0xFF) + (int)((float)((end >>> 24 & 0xFF) - (start >>> 24 & 0xFF)) * c);
        int r = (start >>> 16 & 0xFF) + (int)((float)((end >>> 16 & 0xFF) - (start >>> 16 & 0xFF)) * c);
        int g = (start >>> 8 & 0xFF) + (int)((float)((end >>> 8 & 0xFF) - (start >>> 8 & 0xFF)) * c);
        int b = (start & 0xFF) + (int)((float)((end & 0xFF) - (start & 0xFF)) * c);
        return a << 24 | r << 16 | g << 8 | b;
    }

    private static final float combatHud$lambda$0$0() {
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
                $i$a$-maxOfOrNull-CombatHudModule$combatHud$1$1$1 = false;
                String l2 = (String)pair2.component1();
                String v2 = (String)pair2.component2();
                float f3 = NVGRenderer.textWidth$default(l2 + ": " + v2, textSize, null, 4, null);
                f2 = Math.max(f2, f3);
            }
            f = Float.valueOf(f2);
        }
        float f4 = f != null ? f.floatValue() : 120.0f;
        float f5 = minBarWidth;
        return Math.max(f4, f5) + padding * (float)2;
    }

    private static final float combatHud$lambda$0$1() {
        return lineHeight * (float)INSTANCE.rows().size() + barTopGap + lineHeight + barLabelGap + barHeight + padding * (float)2;
    }

    private static final Unit combatHud$lambda$0$2(CheckboxSetting $onlyWhenActive, float x, float y, float f) {
        Float f2;
        if (((Boolean)$onlyWhenActive.getValue()).booleanValue() && !CombatMacroModule.INSTANCE.isRunning()) {
            return Unit.INSTANCE;
        }
        List<Pair<String, String>> rows = INSTANCE.rows();
        Iterator iterator = ((Iterable)rows).iterator();
        if (!iterator.hasNext()) {
            f2 = null;
        } else {
            Pair pair = (Pair)iterator.next();
            boolean $i$a$-maxOfOrNull-CombatHudModule$combatHud$1$3$panelW$322 = false;
            String l = (String)pair.component1();
            String v = (String)pair.component2();
            float f3 = NVGRenderer.textWidth$default(l + ": " + v, textSize, null, 4, null);
            while (iterator.hasNext()) {
                Pair $i$a$-maxOfOrNull-CombatHudModule$combatHud$1$3$panelW$322 = (Pair)iterator.next();
                boolean $i$a$-maxOfOrNull-CombatHudModule$combatHud$1$3$panelW$2 = false;
                String l2 = (String)$i$a$-maxOfOrNull-CombatHudModule$combatHud$1$3$panelW$322.component1();
                String v2 = (String)$i$a$-maxOfOrNull-CombatHudModule$combatHud$1$3$panelW$322.component2();
                float $i$a$-maxOfOrNull-CombatHudModule$combatHud$1$3$panelW$322 = NVGRenderer.textWidth$default(l2 + ": " + v2, textSize, null, 4, null);
                f3 = Math.max(f3, $i$a$-maxOfOrNull-CombatHudModule$combatHud$1$3$panelW$322);
            }
            f2 = Float.valueOf(f3);
        }
        float f4 = f2 != null ? f2.floatValue() : 120.0f;
        float f5 = minBarWidth;
        float panelW = Math.max(f4, f5) + padding * (float)2;
        float panelH = lineHeight * (float)rows.size() + barTopGap + lineHeight + barLabelGap + barHeight + padding * (float)2;
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
            NVGRenderer.text$default(value, x + padding + labelW, ty, textSize, ThemeManager.INSTANCE.getCurrentTheme().getAccent(), null, 32, null);
        }
        float hpLabelY = y + padding + (float)rows.size() * lineHeight + barTopGap;
        String hpLabel = "Target HP";
        String hpValue = CombatMacroModule.INSTANCE.getTargetHealthDisplay();
        NVGRenderer.text$default(hpLabel + ": ", x + padding, hpLabelY, textSize, INSTANCE.getLabelColor(), null, 32, null);
        float hpLabelW = NVGRenderer.textWidth$default(hpLabel + ": ", textSize, null, 4, null);
        NVGRenderer.text$default(hpValue, x + padding + hpLabelW, hpLabelY, textSize, ThemeManager.INSTANCE.getCurrentTheme().getAccent(), null, 32, null);
        float barX = x + padding;
        float barY = hpLabelY + lineHeight + barLabelGap;
        float barW = panelW - padding * (float)2;
        float barRatio = CombatMacroModule.INSTANCE.getTargetHealthRatio();
        float fillW = barRatio <= 0.0f ? 0.0f : RangesKt.coerceAtMost((float)RangesKt.coerceAtLeast((float)(barW * barRatio), (float)barHeight), (float)barW);
        NVGRenderer.rect(barX, barY, barW, barHeight, -15065024, barHeight / 2.0f);
        NVGRenderer.hollowRect(barX, barY, barW, barHeight, 1.0f, 0x22FFFFFF, barHeight / 2.0f);
        if (fillW > 0.0f) {
            int hpC1 = INSTANCE.lerpColor(-42389, -12852339, barRatio);
            int hpC2 = INSTANCE.lerpColor(-23227, -12130572, barRatio);
            NVGRenderer.gradientRect(barX, barY, fillW, barHeight, hpC1, hpC2, Gradient.LeftToRight, barHeight / 2.0f);
        }
        float hpTextW = NVGRenderer.textWidth$default(hpValue, 11.0f, null, 4, null);
        NVGRenderer.text$default(hpValue, barX + (barW - hpTextW) / 2.0f, barY - 0.5f, 11.0f, -722949, null, 32, null);
        return Unit.INSTANCE;
    }

    private static final Unit combatHud$lambda$0(HudElementBuilder $this$hudElement) {
        Intrinsics.checkNotNullParameter((Object)$this$hudElement, (String)"$this$hudElement");
        $this$hudElement.setAnchor(HudAnchor.TOP_LEFT);
        $this$hudElement.setOffsetX(10.0f);
        $this$hudElement.setOffsetY(140.0f);
        CheckboxSetting onlyWhenActive = (CheckboxSetting)$this$hudElement.setting((Setting)new CheckboxSetting("Only When Active", "Hide when macro is off", false));
        $this$hudElement.width((Function0<Float>)((Function0)CombatHudModule::combatHud$lambda$0$0));
        $this$hudElement.height((Function0<Float>)((Function0)CombatHudModule::combatHud$lambda$0$1));
        $this$hudElement.render((Function3<? super Float, ? super Float, ? super Float, Unit>)((Function3)(arg_0, arg_1, arg_2) -> CombatHudModule.combatHud$lambda$0$2(onlyWhenActive, arg_0, arg_1, arg_2)));
        return Unit.INSTANCE;
    }
}

