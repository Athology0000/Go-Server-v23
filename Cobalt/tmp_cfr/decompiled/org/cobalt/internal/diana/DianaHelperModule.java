/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function3
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_1937
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.diana;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.hud.HudAnchor;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.hud.HudElementBuilder;
import org.cobalt.api.hud.HudModuleDSLKt;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.ColorSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.pathfinder.jni.NativePathfinder;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.diana.DianaParticleTracker;
import org.cobalt.internal.pathfinding.OverlayRenderEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bR\u0014\u0010\n\u001a\u00020\t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\n\u0010\u000bR\u0017\u0010\r\u001a\u00020\f8\u0006\u00a2\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0011\u001a\u00020\f8\u0006\u00a2\u0006\f\n\u0004\b\u0011\u0010\u000e\u001a\u0004\b\u0012\u0010\u0010R\u0017\u0010\u0013\u001a\u00020\f8\u0006\u00a2\u0006\f\n\u0004\b\u0013\u0010\u000e\u001a\u0004\b\u0014\u0010\u0010R\u0017\u0010\u0015\u001a\u00020\f8\u0006\u00a2\u0006\f\n\u0004\b\u0015\u0010\u000e\u001a\u0004\b\u0016\u0010\u0010R\u0017\u0010\u0018\u001a\u00020\u00178\u0006\u00a2\u0006\f\n\u0004\b\u0018\u0010\u0019\u001a\u0004\b\u001a\u0010\u001bR\u0017\u0010\u001d\u001a\u00020\u001c8\u0006\u00a2\u0006\f\n\u0004\b\u001d\u0010\u001e\u001a\u0004\b\u001f\u0010 R\u0013\u0010$\u001a\u0004\u0018\u00010!8F\u00a2\u0006\u0006\u001a\u0004\b\"\u0010#R\u0017\u0010&\u001a\u00020%8\u0006\u00a2\u0006\f\n\u0004\b&\u0010'\u001a\u0004\b(\u0010)\u00a8\u0006*"}, d2={"Lorg/cobalt/internal/diana/DianaHelperModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "event", "", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "getEnabled", "()Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "showWaypoint", "getShowWaypoint", "showLine", "getShowLine", "showHud", "getShowHud", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "waypointColor", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "getWaypointColor", "()Lorg/cobalt/api/module/setting/impl/ColorSetting;", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "expireSeconds", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "getExpireSeconds", "()Lorg/cobalt/api/module/setting/impl/SliderSetting;", "Lnet/minecraft/class_243;", "getBurrowPos", "()Lnet/minecraft/class_243;", "burrowPos", "Lorg/cobalt/api/hud/HudElement;", "compassHud", "Lorg/cobalt/api/hud/HudElement;", "getCompassHud", "()Lorg/cobalt/api/hud/HudElement;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nDianaHelperModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DianaHelperModule.kt\norg/cobalt/internal/diana/DianaHelperModule\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,211:1\n2469#2,14:212\n*S KotlinDebug\n*F\n+ 1 DianaHelperModule.kt\norg/cobalt/internal/diana/DianaHelperModule\n*L\n50#1:212,14\n*E\n"})
public final class DianaHelperModule
extends Module {
    @NotNull
    public static final DianaHelperModule INSTANCE = new DianaHelperModule();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final CheckboxSetting enabled;
    @NotNull
    private static final CheckboxSetting showWaypoint;
    @NotNull
    private static final CheckboxSetting showLine;
    @NotNull
    private static final CheckboxSetting showHud;
    @NotNull
    private static final ColorSetting waypointColor;
    @NotNull
    private static final SliderSetting expireSeconds;
    @NotNull
    private static final HudElement compassHud;

    private DianaHelperModule() {
        super("Diana Helper");
    }

    @NotNull
    public final CheckboxSetting getEnabled() {
        return enabled;
    }

    @NotNull
    public final CheckboxSetting getShowWaypoint() {
        return showWaypoint;
    }

    @NotNull
    public final CheckboxSetting getShowLine() {
        return showLine;
    }

    @NotNull
    public final CheckboxSetting getShowHud() {
        return showHud;
    }

    @NotNull
    public final ColorSetting getWaypointColor() {
        return waypointColor;
    }

    @NotNull
    public final SliderSetting getExpireSeconds() {
        return expireSeconds;
    }

    @Nullable
    public final class_243 getBurrowPos() {
        Object v1;
        class_638 class_6382 = DianaHelperModule.mc.field_1687;
        if (class_6382 == null) {
            return null;
        }
        class_638 level2 = class_6382;
        class_746 player = DianaHelperModule.mc.field_1724;
        List<class_243> positions = DianaParticleTracker.INSTANCE.getBurrowPositions((class_1937)level2, (long)((Number)expireSeconds.getValue()).doubleValue() * 1000L);
        if (positions.isEmpty()) {
            return null;
        }
        if (player == null) {
            return (class_243)CollectionsKt.first(positions);
        }
        double px = player.method_23317();
        double pz = player.method_23321();
        Iterable $this$minByOrNull$iv = positions;
        boolean $i$f$minByOrNull = false;
        Iterator iterator$iv = $this$minByOrNull$iv.iterator();
        if (!iterator$iv.hasNext()) {
            v1 = null;
        } else {
            Object minElem$iv = iterator$iv.next();
            if (!iterator$iv.hasNext()) {
                v1 = minElem$iv;
            } else {
                class_243 p = (class_243)minElem$iv;
                boolean bl = false;
                double dx = p.field_1352 - px;
                double dz = p.field_1350 - pz;
                double minValue$iv = dx * dx + dz * dz;
                do {
                    Object e$iv = iterator$iv.next();
                    p = (class_243)e$iv;
                    bl = false;
                    dx = p.field_1352 - px;
                    dz = p.field_1350 - pz;
                    double v$iv = dx * dx + dz * dz;
                    if (Double.compare(minValue$iv, v$iv) <= 0) continue;
                    minElem$iv = e$iv;
                    minValue$iv = v$iv;
                } while (iterator$iv.hasNext());
                v1 = minElem$iv;
            }
        }
        return v1;
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            return;
        }
        class_638 class_6382 = DianaHelperModule.mc.field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        int argb = waypointColor.getValue();
        int alpha = argb >>> 24 & 0xFF;
        int r = argb >> 16 & 0xFF;
        int g = argb >> 8 & 0xFF;
        int blue = argb & 0xFF;
        OverlayRenderEngine.INSTANCE.clearTag("diana-helper");
        List<class_243> positions = DianaParticleTracker.INSTANCE.getBurrowPositions((class_1937)level2, (long)((Number)expireSeconds.getValue()).doubleValue() * 1000L);
        if (((Boolean)showWaypoint.getValue()).booleanValue()) {
            for (class_243 bp : positions) {
                OverlayRenderEngine.addBox$default(OverlayRenderEngine.INSTANCE, (class_1937)level2, bp.field_1352 - 0.5, bp.field_1351, bp.field_1350 - 0.5, bp.field_1352 + 0.5, bp.field_1351 + 1.0, bp.field_1350 + 0.5, new OverlayRenderEngine.Color(r, g, blue, alpha / 4), new OverlayRenderEngine.Color(r, g, blue, alpha), 0.0f, 3, "diana-helper", true, 512, null);
                OverlayRenderEngine.INSTANCE.addLine((class_1937)level2, bp.field_1352, bp.field_1351, bp.field_1350, bp.field_1352, bp.field_1351 + 12.0, bp.field_1350, new OverlayRenderEngine.Color(r, g, blue, alpha * 7 / 10), 3.0f, 3, "diana-helper", true);
            }
        }
        class_243 class_2432 = this.getBurrowPos();
        if (class_2432 == null) {
            return;
        }
        class_243 nearest = class_2432;
        class_243 activationPos = DianaParticleTracker.INSTANCE.getActivationPos();
        if (activationPos != null) {
            OverlayRenderEngine.INSTANCE.addLine((class_1937)level2, activationPos.field_1352, activationPos.field_1351 + 0.05, activationPos.field_1350, nearest.field_1352, nearest.field_1351 + 0.05, nearest.field_1350, new OverlayRenderEngine.Color(r, g, blue, alpha / 3), 1.0f, 3, "diana-helper", true);
        }
        if (((Boolean)showLine.getValue()).booleanValue()) {
            class_746 class_7462 = DianaHelperModule.mc.field_1724;
            if (class_7462 == null) {
                return;
            }
            class_746 player = class_7462;
            class_243 class_2433 = player.method_33571();
            Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"getEyePosition(...)");
            class_243 eye = class_2433;
            List<class_243> nodes = NativePathfinder.INSTANCE.getCachedPathNodes();
            if (nodes.size() >= 2) {
                int n = nodes.size() - 1;
                for (int i = 0; i < n; ++i) {
                    class_243 a = nodes.get(i);
                    class_243 nb = nodes.get(i + 1);
                    OverlayRenderEngine.INSTANCE.addLine((class_1937)level2, a.field_1352, a.field_1351 + 0.05, a.field_1350, nb.field_1352, nb.field_1351 + 0.05, nb.field_1350, new OverlayRenderEngine.Color(r, g, blue, alpha * 3 / 4), 2.0f, 3, "diana-helper", true);
                }
                class_243 last = (class_243)CollectionsKt.last(nodes);
                OverlayRenderEngine.INSTANCE.addLine((class_1937)level2, last.field_1352, last.field_1351 + 0.05, last.field_1350, eye.field_1352, eye.field_1351, eye.field_1350, new OverlayRenderEngine.Color(r, g, blue, alpha / 2), 1.5f, 3, "diana-helper", true);
            }
            OverlayRenderEngine.INSTANCE.addLine((class_1937)level2, eye.field_1352, eye.field_1351, eye.field_1350, nearest.field_1352, nearest.field_1351 + 0.5, nearest.field_1350, new OverlayRenderEngine.Color(r, g, blue, alpha * 2 / 3), 2.0f, 3, "diana-helper", true);
        }
    }

    @NotNull
    public final HudElement getCompassHud() {
        return compassHud;
    }

    private static final float compassHud$lambda$0$0() {
        return 90.0f;
    }

    private static final float compassHud$lambda$0$1() {
        return 90.0f;
    }

    private static final Unit compassHud$lambda$0$2(float x, float y, float f) {
        String string;
        if (!((Boolean)showHud.getValue()).booleanValue()) {
            return Unit.INSTANCE;
        }
        class_746 class_7462 = DianaHelperModule.mc.field_1724;
        if (class_7462 == null) {
            return Unit.INSTANCE;
        }
        class_746 player = class_7462;
        class_243 class_2432 = INSTANCE.getBurrowPos();
        if (class_2432 == null) {
            return Unit.INSTANCE;
        }
        class_243 bp = class_2432;
        int argb = waypointColor.getValue();
        int alpha = argb >>> 24 & 0xFF;
        int rr = argb >> 16 & 0xFF;
        int gg = argb >> 8 & 0xFF;
        int bb = argb & 0xFF;
        float centerX = x + 45.0f;
        float centerY = y + 38.0f;
        float trackRadius = 26.0f;
        NVGRenderer.rect(x, y, 90.0f, 90.0f, ThemeManager.INSTANCE.getCurrentTheme().getPanel(), 8.0f);
        NVGRenderer.hollowRect(x, y, 90.0f, 90.0f, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 8.0f);
        NVGRenderer.hollowRect(centerX - trackRadius, centerY - trackRadius, trackRadius * 2.0f, trackRadius * 2.0f, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getOverlay(), trackRadius);
        double dx = bp.field_1352 - player.method_23317();
        double dz = bp.field_1350 - player.method_23321();
        double targetYawDeg = Math.toDegrees(Math.atan2(-dx, dz));
        double relAngleRad = Math.toRadians(targetYawDeg - (double)player.method_36454());
        float dotX = centerX + (float)(Math.sin(relAngleRad) * (double)trackRadius);
        float dotY = centerY - (float)(Math.cos(relAngleRad) * (double)trackRadius);
        int dimArgb = alpha * 6 / 10 << 24 | rr << 16 | gg << 8 | bb;
        NVGRenderer.line(centerX, centerY, dotX, dotY, 2.0f, dimArgb);
        NVGRenderer.circle(dotX, dotY, 5.0f, argb);
        double dist = player.method_73189().method_1022(bp);
        if (dist >= 100.0) {
            string = (int)dist + "m";
        } else {
            String string2 = "%.1f";
            Object[] objectArray = new Object[]{dist};
            String string3 = String.format(string2, Arrays.copyOf(objectArray, objectArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"format(...)");
            string = string3 + "m";
        }
        String distStr = string;
        float textW = NVGRenderer.textWidth$default(distStr, 11.0f, null, 4, null);
        NVGRenderer.text$default(distStr, centerX - textW / 2.0f, y + 74.0f, 11.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        return Unit.INSTANCE;
    }

    private static final Unit compassHud$lambda$0(HudElementBuilder $this$hudElement) {
        Intrinsics.checkNotNullParameter((Object)$this$hudElement, (String)"$this$hudElement");
        $this$hudElement.setAnchor(HudAnchor.BOTTOM_CENTER);
        $this$hudElement.setOffsetY(-20.0f);
        $this$hudElement.width((Function0<Float>)((Function0)DianaHelperModule::compassHud$lambda$0$0));
        $this$hudElement.height((Function0<Float>)((Function0)DianaHelperModule::compassHud$lambda$0$1));
        $this$hudElement.render((Function3<? super Float, ? super Float, ? super Float, Unit>)((Function3)DianaHelperModule::compassHud$lambda$0$2));
        return Unit.INSTANCE;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        enabled = new CheckboxSetting("Enabled", "Show Diana burrow waypoints and direction HUD.", true);
        showWaypoint = new CheckboxSetting("Show Waypoint", "Render a highlighted box + beam at each detected burrow.", true);
        showLine = new CheckboxSetting("Show Line", "Draw a path line from your position to the nearest burrow.", true);
        showHud = new CheckboxSetting("Show HUD", "Show the burrow direction compass HUD.", true);
        waypointColor = new ColorSetting("Waypoint Color", "Color of the burrow waypoints.", -10496);
        expireSeconds = new SliderSetting("Expire Time", "Seconds without a confirmed particle before a waypoint disappears.", 30.0, 5.0, 120.0, 1.0);
        Setting[] settingArray = new Setting[6];
        settingArray[0] = enabled;
        settingArray[1] = showWaypoint;
        settingArray[2] = showLine;
        settingArray[3] = showHud;
        settingArray[4] = waypointColor;
        settingArray[5] = expireSeconds;
        INSTANCE.addSetting(settingArray);
        EventBus.register(INSTANCE);
        compassHud = HudModuleDSLKt.hudElement$default(INSTANCE, "diana-helper-compass", "Diana Compass", null, DianaHelperModule::compassHud$lambda$0, 4, null);
    }
}

