/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_1041
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_239
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3675
 *  net.minecraft.class_3965
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.routes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import net.minecraft.class_1041;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import net.minecraft.class_3965;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.MouseEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.NvgEvent;
import org.cobalt.api.event.impl.render.WorldRenderContext;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.ui.theme.Theme;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.render.Render3D;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.routes.RoutePoint;
import org.cobalt.internal.routes.RoutePointType;
import org.cobalt.internal.routes.RouteStore;
import org.cobalt.internal.routes.RouteType;
import org.cobalt.internal.routes.RouteTypeKt;
import org.cobalt.internal.routes.SavedRoute;
import org.cobalt.internal.routes.SubRouteKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u008c\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010!\n\u0002\b\u0005\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\b\r\b\u00c0\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J-\u0010\u000b\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\u0004\b\u000b\u0010\fJ5\u0010\u000f\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\r2\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0013\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0017\u0010\u0017\u001a\u00020\t2\u0006\u0010\u0016\u001a\u00020\u0015H\u0007\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u0017\u0010\u001a\u001a\u00020\t2\u0006\u0010\u0016\u001a\u00020\u0019H\u0007\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u0017\u0010\u001d\u001a\u00020\t2\u0006\u0010\u0016\u001a\u00020\u001cH\u0007\u00a2\u0006\u0004\b\u001d\u0010\u001eJ%\u0010 \u001a\u00020\t2\u0006\u0010\u0016\u001a\u00020\u001c2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0002\u00a2\u0006\u0004\b \u0010!J\u0017\u0010#\u001a\u00020\t2\u0006\u0010\u0016\u001a\u00020\"H\u0007\u00a2\u0006\u0004\b#\u0010$J'\u0010(\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010&\u001a\u00020%2\u0006\u0010'\u001a\u00020%H\u0002\u00a2\u0006\u0004\b(\u0010)J\u000f\u0010*\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b*\u0010\u0003J\u000f\u0010+\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b+\u0010\u0003R\u0014\u0010-\u001a\u00020,8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b-\u0010.R$\u00101\u001a\u00020/2\u0006\u00100\u001a\u00020/8\u0006@BX\u0086\u000e\u00a2\u0006\f\n\u0004\b1\u00102\u001a\u0004\b1\u00103R\u0018\u00104\u001a\u0004\u0018\u00010\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b4\u00105R\u0016\u00106\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b6\u00107R\u0016\u00109\u001a\u0002088\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b9\u0010:R\u001c\u0010;\u001a\b\u0012\u0004\u0012\u0002080\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b;\u0010<R\u001a\u0010>\u001a\b\u0012\u0004\u0012\u00020\u00120=8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b>\u0010<R\u0018\u0010?\u001a\u0004\u0018\u00010\r8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b?\u0010@R\u001e\u0010A\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bA\u0010BR \u0010E\u001a\u000e\u0012\u0004\u0012\u000208\u0012\u0004\u0012\u00020D0C8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bE\u0010FR\u0014\u0010G\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bG\u0010HR\u0014\u0010I\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bI\u0010HR\u0014\u0010J\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bJ\u0010HR\u0014\u0010K\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bK\u0010HR\u0014\u0010L\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bL\u0010HR\u0014\u0010M\u001a\u00020%8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bM\u0010HR\u0016\u0010N\u001a\u00020/8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bN\u00102R\u0016\u0010O\u001a\u00020/8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bO\u00102R\u0016\u0010P\u001a\u00020/8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bP\u00102\u00a8\u0006Q"}, d2={"Lorg/cobalt/internal/routes/RouteEditMode;", "", "<init>", "()V", "Lorg/cobalt/internal/routes/SavedRoute;", "route", "Lorg/cobalt/internal/routes/SubRouteKey;", "sub", "Lkotlin/Function0;", "", "onDone", "enterEdit", "(Lorg/cobalt/internal/routes/SavedRoute;Lorg/cobalt/internal/routes/SubRouteKey;Lkotlin/jvm/functions/Function0;)V", "", "afterIndex", "enterInsertMode", "(Lorg/cobalt/internal/routes/SavedRoute;Lorg/cobalt/internal/routes/SubRouteKey;ILkotlin/jvm/functions/Function0;)V", "", "Lorg/cobalt/internal/routes/RoutePoint;", "getPoints", "()Ljava/util/List;", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lorg/cobalt/api/event/impl/render/NvgEvent;", "onNvg", "(Lorg/cobalt/api/event/impl/render/NvgEvent;)V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "onWorldRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "pts", "renderPoints", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;Ljava/util/List;)V", "Lorg/cobalt/api/event/impl/client/MouseEvent$RightClick;", "onRightClick", "(Lorg/cobalt/api/event/impl/client/MouseEvent$RightClick;)V", "", "sw", "sh", "renderHud", "(Lorg/cobalt/internal/routes/SavedRoute;FF)V", "saveCurrentPoints", "finishEdit", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "", "value", "isActive", "Z", "()Z", "activeRoute", "Lorg/cobalt/internal/routes/SavedRoute;", "activeSub", "Lorg/cobalt/internal/routes/SubRouteKey;", "Lorg/cobalt/internal/routes/RoutePointType;", "currentMode", "Lorg/cobalt/internal/routes/RoutePointType;", "validModes", "Ljava/util/List;", "", "points", "insertAfterIndex", "Ljava/lang/Integer;", "onDoneCallback", "Lkotlin/jvm/functions/Function0;", "", "Ljava/awt/Color;", "typeColor", "Ljava/util/Map;", "HUD_W", "F", "HUD_H", "HUD_PAD_BOTTOM", "BTN_H", "BTN_PAD_H", "BTN_GAP", "tabWasDown", "escWasDown", "zWasDown", "cobalt"})
@SourceDebugExtension(value={"SMAP\nRouteEditMode.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RouteEditMode.kt\norg/cobalt/internal/routes/RouteEditMode\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,290:1\n1924#2,3:291\n*S KotlinDebug\n*F\n+ 1 RouteEditMode.kt\norg/cobalt/internal/routes/RouteEditMode\n*L\n182#1:291,3\n*E\n"})
public final class RouteEditMode {
    @NotNull
    public static final RouteEditMode INSTANCE = new RouteEditMode();
    @NotNull
    private static final class_310 mc;
    private static boolean isActive;
    @Nullable
    private static SavedRoute activeRoute;
    @NotNull
    private static SubRouteKey activeSub;
    @NotNull
    private static RoutePointType currentMode;
    @NotNull
    private static List<? extends RoutePointType> validModes;
    @NotNull
    private static final List<RoutePoint> points;
    @Nullable
    private static Integer insertAfterIndex;
    @Nullable
    private static Function0<Unit> onDoneCallback;
    @NotNull
    private static final Map<RoutePointType, Color> typeColor;
    private static final float HUD_W = 500.0f;
    private static final float HUD_H = 66.0f;
    private static final float HUD_PAD_BOTTOM = 28.0f;
    private static final float BTN_H = 24.0f;
    private static final float BTN_PAD_H = 10.0f;
    private static final float BTN_GAP = 6.0f;
    private static boolean tabWasDown;
    private static boolean escWasDown;
    private static boolean zWasDown;

    private RouteEditMode() {
    }

    public final boolean isActive() {
        return isActive;
    }

    public final void enterEdit(@NotNull SavedRoute route, @NotNull SubRouteKey sub, @NotNull Function0<Unit> onDone) {
        Intrinsics.checkNotNullParameter((Object)route, (String)"route");
        Intrinsics.checkNotNullParameter((Object)((Object)sub), (String)"sub");
        Intrinsics.checkNotNullParameter(onDone, (String)"onDone");
        activeRoute = route;
        activeSub = sub;
        validModes = RouteTypeKt.allowedPointTypes(route.getType(), sub);
        RoutePointType routePointType = (RoutePointType)((Object)CollectionsKt.firstOrNull(validModes));
        if (routePointType == null) {
            routePointType = RoutePointType.WALK;
        }
        currentMode = routePointType;
        points.clear();
        points.addAll((Collection<RoutePoint>)route.getSubRoute(sub));
        insertAfterIndex = null;
        onDoneCallback = onDone;
        isActive = true;
        tabWasDown = false;
        escWasDown = false;
        zWasDown = false;
        mc.method_1507(null);
    }

    public static /* synthetic */ void enterEdit$default(RouteEditMode routeEditMode, SavedRoute savedRoute, SubRouteKey subRouteKey, Function0 function0, int n, Object object) {
        if ((n & 4) != 0) {
            function0 = RouteEditMode::enterEdit$lambda$0;
        }
        routeEditMode.enterEdit(savedRoute, subRouteKey, (Function0<Unit>)function0);
    }

    public final void enterInsertMode(@NotNull SavedRoute route, @NotNull SubRouteKey sub, int afterIndex, @NotNull Function0<Unit> onDone) {
        Intrinsics.checkNotNullParameter((Object)route, (String)"route");
        Intrinsics.checkNotNullParameter((Object)((Object)sub), (String)"sub");
        Intrinsics.checkNotNullParameter(onDone, (String)"onDone");
        this.enterEdit(route, sub, onDone);
        insertAfterIndex = afterIndex;
    }

    public static /* synthetic */ void enterInsertMode$default(RouteEditMode routeEditMode, SavedRoute savedRoute, SubRouteKey subRouteKey, int n, Function0 function0, int n2, Object object) {
        if ((n2 & 8) != 0) {
            function0 = RouteEditMode::enterInsertMode$lambda$0;
        }
        routeEditMode.enterInsertMode(savedRoute, subRouteKey, n, (Function0<Unit>)function0);
    }

    @NotNull
    public final List<RoutePoint> getPoints() {
        return CollectionsKt.toList((Iterable)points);
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        boolean ctrlDown;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!isActive) {
            return;
        }
        if (RouteEditMode.mc.field_1755 != null) {
            mc.method_1507(null);
            this.finishEdit();
            return;
        }
        class_1041 class_10412 = mc.method_22683();
        Intrinsics.checkNotNullExpressionValue((Object)class_10412, (String)"getWindow(...)");
        class_1041 window = class_10412;
        boolean escDown = class_3675.method_15987((class_1041)window, (int)256);
        if (escDown && !escWasDown) {
            this.finishEdit();
        }
        escWasDown = escDown;
        boolean tabDown = class_3675.method_15987((class_1041)window, (int)258);
        if (tabDown && !tabWasDown && validModes.size() > 1) {
            int idx = validModes.indexOf((Object)currentMode);
            currentMode = validModes.get((idx + 1) % validModes.size());
        }
        tabWasDown = tabDown;
        boolean zDown = class_3675.method_15987((class_1041)window, (int)90);
        boolean bl = ctrlDown = class_3675.method_15987((class_1041)window, (int)341) || class_3675.method_15987((class_1041)window, (int)345);
        if (zDown && !zWasDown && ctrlDown && !((Collection)points).isEmpty()) {
            points.remove(CollectionsKt.getLastIndex(points));
            this.saveCurrentPoints();
        }
        zWasDown = zDown;
    }

    @SubscribeEvent
    public final void onNvg(@NotNull NvgEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!isActive) {
            return;
        }
        SavedRoute savedRoute = activeRoute;
        if (savedRoute == null) {
            return;
        }
        SavedRoute route = savedRoute;
        class_1041 class_10412 = mc.method_22683();
        Intrinsics.checkNotNullExpressionValue((Object)class_10412, (String)"getWindow(...)");
        class_1041 window = class_10412;
        float sw = window.method_4480();
        float sh = window.method_4507();
        NVGRenderer.INSTANCE.beginFrame(sw, sh);
        this.renderHud(route, sw, sh);
        NVGRenderer.INSTANCE.endFrame();
    }

    @SubscribeEvent
    public final void onWorldRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (isActive) {
            this.renderPoints(event, points);
            return;
        }
        for (RouteType type : RouteType.getEntries()) {
            if (RouteStore.INSTANCE.getLoaded(type) == null) continue;
            for (SubRouteKey sub : RouteTypeKt.subRoutesFor(type)) {
                SavedRoute route;
                List<RoutePoint> pts = route.getSubRoute(sub);
                if (!(!((Collection)pts).isEmpty())) continue;
                this.renderPoints(event, pts);
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    private final void renderPoints(WorldRenderEvent.Last event, List<RoutePoint> pts) {
        Object color;
        int n = pts.size() - 1;
        for (int i = 0; i < n; ++i) {
            RoutePoint a = pts.get(i);
            RoutePoint b = pts.get(i + 1);
            class_243 start = new class_243((double)a.getX() + 0.5, (double)a.getY() + 0.5, (double)a.getZ() + 0.5);
            class_243 end = new class_243((double)b.getX() + 0.5, (double)b.getY() + 0.5, (double)b.getZ() + 0.5);
            Color color2 = typeColor.get((Object)a.getType());
            if (color2 == null) {
                color2 = Color.WHITE;
            }
            color = color2;
            WorldRenderContext worldRenderContext = event.getContext();
            Intrinsics.checkNotNull((Object)color);
            Render3D.drawLine(worldRenderContext, start, end, (Color)color, true, 1.5f);
        }
        Iterable $this$forEachIndexed$iv = pts;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void pt;
            int n2;
            if ((n2 = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            color = (RoutePoint)item$iv;
            int i = n2;
            boolean bl = false;
            Color color3 = typeColor.get((Object)pt.getType());
            if (color3 == null) {
                color3 = Color.WHITE;
            }
            Color color4 = color3;
            class_238 box = new class_238((double)pt.getX(), (double)pt.getY(), (double)pt.getZ(), (double)pt.getX() + 1.0, (double)pt.getY() + 1.0, (double)pt.getZ() + 1.0);
            WorldRenderContext worldRenderContext = event.getContext();
            Intrinsics.checkNotNull((Object)color4);
            Render3D.drawBox(worldRenderContext, box, color4, true);
            class_243 labelPos = new class_243((double)pt.getX() + 0.5, (double)pt.getY() + 1.3, (double)pt.getZ() + 0.5);
            Render3D.drawWorldLabel(event.getContext(), labelPos, "#" + (i + 1) + " " + pt.getType().getLabel(), color4);
        }
    }

    @SubscribeEvent
    public final void onRightClick(@NotNull MouseEvent.RightClick event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!isActive) {
            return;
        }
        class_239 hit = RouteEditMode.mc.field_1765;
        if (!(hit instanceof class_3965) || ((class_3965)hit).method_17783() != class_239.class_240.field_1332) {
            return;
        }
        event.setCancelled(true);
        class_2338 class_23382 = ((class_3965)hit).method_17777();
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"getBlockPos(...)");
        class_2338 pos = class_23382;
        boolean shiftHeld = RouteEditMode.mc.field_1690.field_1832.method_1434();
        if (shiftHeld && validModes.size() > 1) {
            int idx = validModes.indexOf((Object)currentMode);
            currentMode = validModes.get((idx + 1) % validModes.size());
        }
        RoutePoint newPoint = new RoutePoint(currentMode, pos.method_10263(), pos.method_10264(), pos.method_10260(), null, null, null, null, 240, null);
        Integer insertIdx = insertAfterIndex;
        if (insertIdx != null) {
            int clampedIdx = RangesKt.coerceIn((int)(insertIdx + 1), (int)0, (int)points.size());
            points.add(clampedIdx, newPoint);
            insertAfterIndex = null;
        } else {
            points.add(newPoint);
        }
        this.saveCurrentPoints();
    }

    private final void renderHud(SavedRoute route, float sw, float sh) {
        Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
        float hx = sw / 2.0f - 250.0f;
        float hy = sh - 66.0f - 28.0f;
        NVGRenderer.rect(hx, hy, 500.0f, 66.0f, theme.getBackground(), 10.0f);
        NVGRenderer.hollowRect(hx, hy, 500.0f, 66.0f, 1.0f, theme.getControlBorder(), 10.0f);
        String titleText = route.getName() + "  >  " + activeSub.getLabel();
        String ptCountTxt = points.size() + " pts";
        NVGRenderer.text$default(titleText, hx + 14.0f, hy + 14.0f, 11.0f, theme.getText(), null, 32, null);
        float ptCountW = NVGRenderer.textWidth$default(ptCountTxt, 10.0f, null, 4, null);
        NVGRenderer.text$default(ptCountTxt, hx + 500.0f - ptCountW - 14.0f, hy + 14.0f, 10.0f, theme.getTextSecondary(), null, 32, null);
        Integer insertIdx = insertAfterIndex;
        if (insertIdx != null) {
            String hint = "Inserting after #" + (insertIdx + 1);
            NVGRenderer.text$default(hint, hx + 14.0f, hy + 29.0f, 9.0f, theme.getAccent(), null, 32, null);
        }
        float btnY = hy + 66.0f - 24.0f - 10.0f;
        float curX = hx + 14.0f;
        for (RoutePointType routePointType : validModes) {
            String label = routePointType.getIcon() + " " + routePointType.getLabel();
            float bw = NVGRenderer.textWidth$default(label, 10.0f, null, 4, null) + 20.0f;
            boolean isSelected = routePointType == currentMode;
            int bg = isSelected ? theme.getAccent() : theme.getControlBg();
            int textColor = isSelected ? theme.getTextOnAccent() : theme.getText();
            NVGRenderer.rect(curX, btnY, bw, 24.0f, bg, 6.0f);
            NVGRenderer.hollowRect(curX, btnY, bw, 24.0f, 1.0f, theme.getControlBorder(), 6.0f);
            NVGRenderer.text$default(label, curX + 10.0f, btnY + 5.0f, 10.0f, textColor, null, 32, null);
            curX += bw + 6.0f;
        }
        float rightEdge = hx + 500.0f - 14.0f;
        String string = "Tab: cycle   Ctrl+Z: undo   Esc: done";
        float hintsW = NVGRenderer.textWidth$default(string, 9.0f, null, 4, null);
        NVGRenderer.text$default(string, rightEdge - hintsW, btnY + 7.0f, 9.0f, theme.getTextSecondary(), null, 32, null);
    }

    private final void saveCurrentPoints() {
        SavedRoute updated;
        SavedRoute savedRoute = activeRoute;
        if (savedRoute == null) {
            return;
        }
        SavedRoute route = savedRoute;
        activeRoute = updated = route.withSubRoute(activeSub, CollectionsKt.toList((Iterable)points));
        RouteStore.INSTANCE.save(updated);
    }

    private final void finishEdit() {
        block0: {
            isActive = false;
            Function0<Unit> cb = onDoneCallback;
            activeRoute = null;
            onDoneCallback = null;
            Function0<Unit> function0 = cb;
            if (function0 == null) break block0;
            function0.invoke();
        }
    }

    private static final Unit enterEdit$lambda$0() {
        return Unit.INSTANCE;
    }

    private static final Unit enterInsertMode$lambda$0() {
        return Unit.INSTANCE;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        activeSub = SubRouteKey.POINTS;
        currentMode = RoutePointType.WALK;
        validModes = CollectionsKt.emptyList();
        points = new ArrayList();
        Pair[] pairArray = new Pair[]{TuplesKt.to((Object)((Object)RoutePointType.WALK), (Object)new Color(77, 226, 197)), TuplesKt.to((Object)((Object)RoutePointType.WARP), (Object)new Color(255, 224, 79)), TuplesKt.to((Object)((Object)RoutePointType.MINE), (Object)new Color(255, 140, 0)), TuplesKt.to((Object)((Object)RoutePointType.VEIN), (Object)new Color(158, 124, 255)), TuplesKt.to((Object)((Object)RoutePointType.LANTERN), (Object)new Color(255, 215, 0)), TuplesKt.to((Object)((Object)RoutePointType.KILL), (Object)new Color(255, 107, 138))};
        typeColor = MapsKt.mapOf((Pair[])pairArray);
    }
}

