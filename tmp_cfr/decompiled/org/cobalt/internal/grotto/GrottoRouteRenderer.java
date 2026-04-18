/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.opengl.GlStateManager
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1937
 *  net.minecraft.class_2183$class_2184
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_327$class_6415
 *  net.minecraft.class_332
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionfc
 */
package org.cobalt.internal.grotto;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.awt.Color;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2183;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.WorldRenderContext;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.util.InventoryUtils;
import org.cobalt.api.util.TickScheduler;
import org.cobalt.api.util.render.Render3D;
import org.cobalt.internal.grotto.GrottoChat;
import org.cobalt.internal.mining.FairyModule;
import org.cobalt.internal.pathfinding.OverlayRenderEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionfc;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u009a\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010#\n\u0002\u0010\t\n\u0002\b\f\n\u0002\u0010\u000e\n\u0002\b\u0015\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\t\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0017\u0010\b\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006H\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\u0017\u0010\u000b\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\nH\u0007\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0017\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\rH\u0007\u00a2\u0006\u0004\b\u000f\u0010\u0010J)\u0010\u0016\u001a\u00020\u00042\b\u0010\u0012\u001a\u0004\u0018\u00010\u00112\u000e\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0014\u0018\u00010\u0013H\u0007\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u000f\u0010\u0018\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0018\u0010\u0003J\u0019\u0010\u001a\u001a\u00020\u00192\b\u0010\u000e\u001a\u0004\u0018\u00010\rH\u0007\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u0019\u0010\u001c\u001a\u00020\u00192\b\u0010\u000e\u001a\u0004\u0018\u00010\rH\u0007\u00a2\u0006\u0004\b\u001c\u0010\u001bJ\u0019\u0010\u001d\u001a\u00020\u00192\b\u0010\u000e\u001a\u0004\u0018\u00010\rH\u0007\u00a2\u0006\u0004\b\u001d\u0010\u001bJ\u000f\u0010\u001e\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u001e\u0010\u0003J\u000f\u0010\u001f\u001a\u00020\u0019H\u0007\u00a2\u0006\u0004\b\u001f\u0010 J#\u0010#\u001a\u00020\u00192\b\u0010\u0012\u001a\u0004\u0018\u00010\u00112\b\u0010\"\u001a\u0004\u0018\u00010!H\u0002\u00a2\u0006\u0004\b#\u0010$J\u0019\u0010%\u001a\u00020\u00192\b\u0010\"\u001a\u0004\u0018\u00010!H\u0002\u00a2\u0006\u0004\b%\u0010&J\u0017\u0010'\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\rH\u0002\u00a2\u0006\u0004\b'\u0010\u0010J\u0017\u0010)\u001a\u00020(2\u0006\u0010\u000e\u001a\u00020\rH\u0002\u00a2\u0006\u0004\b)\u0010*J)\u0010-\u001a\u00020\u00192\b\u0010\u0012\u001a\u0004\u0018\u00010\u00112\u0006\u0010+\u001a\u00020\u00142\u0006\u0010,\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b-\u0010.J\u0017\u0010/\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\rH\u0002\u00a2\u0006\u0004\b/\u0010\u0010J\u0015\u00102\u001a\u00020\u00042\u0006\u00101\u001a\u000200\u00a2\u0006\u0004\b2\u00103J\u0015\u00106\u001a\u00020\u00042\u0006\u00105\u001a\u000204\u00a2\u0006\u0004\b6\u00107J\u0017\u0010;\u001a\u00020:2\u0006\u00109\u001a\u000208H\u0002\u00a2\u0006\u0004\b;\u0010<J\u0017\u0010?\u001a\u00020>2\u0006\u0010=\u001a\u00020:H\u0002\u00a2\u0006\u0004\b?\u0010@J\u0017\u0010A\u001a\u00020\u00042\u0006\u00101\u001a\u000200H\u0002\u00a2\u0006\u0004\bA\u00103J\u0017\u0010B\u001a\u00020\u00042\u0006\u00101\u001a\u000200H\u0002\u00a2\u0006\u0004\bB\u00103J5\u0010F\u001a\u00020(2\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010+\u001a\u00020\u00142\u0006\u0010,\u001a\u00020\u00142\f\u0010E\u001a\b\u0012\u0004\u0012\u00020D0CH\u0002\u00a2\u0006\u0004\bF\u0010GJ\u001f\u0010J\u001a\u0002082\u0006\u0010H\u001a\u0002082\u0006\u0010I\u001a\u000208H\u0002\u00a2\u0006\u0004\bJ\u0010KJ'\u0010O\u001a\u0002082\u0006\u0010L\u001a\u00020\u00142\u0006\u0010M\u001a\u00020\u00142\u0006\u0010N\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\bO\u0010PR\u0014\u0010R\u001a\u00020Q8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bR\u0010SR\u0014\u0010T\u001a\u00020Q8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bT\u0010SR\u0014\u0010U\u001a\u00020(8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bU\u0010VR\u0014\u0010W\u001a\u00020(8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bW\u0010VR\u0014\u0010X\u001a\u00020(8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bX\u0010VR\u0014\u0010Y\u001a\u0002088\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bY\u0010ZR\u0014\u0010[\u001a\u0002088\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b[\u0010ZR\u0014\u0010\\\u001a\u0002088\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\\\u0010ZR\u0014\u0010]\u001a\u00020(8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b]\u0010VR\u0014\u0010^\u001a\u00020(8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b^\u0010VR\u0014\u0010_\u001a\u0002088\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b_\u0010ZR\u0014\u0010`\u001a\u00020(8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b`\u0010VR\u0014\u0010a\u001a\u00020(8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\ba\u0010VR\u0014\u0010b\u001a\u0002088\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bb\u0010ZR\u0018\u0010c\u001a\u0004\u0018\u00010\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bc\u0010dR\u001c\u0010e\u001a\b\u0012\u0004\u0012\u00020\u00140\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\be\u0010fR$\u0010i\u001a\u0012\u0012\u0004\u0012\u00020D0gj\b\u0012\u0004\u0012\u00020D`h8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bi\u0010jR\u0016\u0010k\u001a\u00020(8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bk\u0010VR\u0016\u0010l\u001a\u00020(8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bl\u0010VR\u0016\u0010m\u001a\u00020\u00198\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bm\u0010nR\u0016\u0010o\u001a\u00020(8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bo\u0010VR\u0016\u0010p\u001a\u00020(8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bp\u0010V\u00a8\u0006q"}, d2={"Lorg/cobalt/internal/grotto/GrottoRouteRenderer;", "", "<init>", "()V", "", "init", "Lorg/cobalt/api/event/impl/client/TickEvent$End;", "event", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$End;)V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "Lnet/minecraft/class_310;", "client", "tick", "(Lnet/minecraft/class_310;)V", "Lnet/minecraft/class_1937;", "level", "", "Lnet/minecraft/class_243;", "points", "setRoute", "(Lnet/minecraft/class_1937;Ljava/util/List;)V", "clear", "", "advanceToNext", "(Lnet/minecraft/class_310;)Z", "canStartAutoRoute", "startAutoRun", "stopAutoRun", "isAutoRunActive", "()Z", "Lnet/minecraft/class_1657;", "player", "canSeeStart", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_1657;)Z", "isNearStart", "(Lnet/minecraft/class_1657;)Z", "handleAutoRun", "", "resolveNextIndex", "(Lnet/minecraft/class_310;)I", "start", "end", "hasObstruction", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_243;Lnet/minecraft/class_243;)Z", "updateObstructionHighlights", "Lorg/cobalt/api/event/impl/render/WorldRenderContext;", "context", "renderLabels", "(Lorg/cobalt/api/event/impl/render/WorldRenderContext;)V", "Lnet/minecraft/class_332;", "graphics", "renderHudLabels", "(Lnet/minecraft/class_332;)V", "", "t", "Ljava/awt/Color;", "gradientColor", "(D)Ljava/awt/Color;", "color", "Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "toOverlayColor", "(Ljava/awt/Color;)Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "renderGradientLines", "renderPointOutlines", "", "", "highlighted", "traceObstructions", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_243;Lnet/minecraft/class_243;Ljava/util/Set;)I", "s", "ds", "intBound", "(DD)D", "p", "a", "b", "distanceToSegmentSq", "(Lnet/minecraft/class_243;Lnet/minecraft/class_243;Lnet/minecraft/class_243;)D", "", "TAG_POINTS", "Ljava/lang/String;", "TAG_BLOCKS", "PERSIST_TICKS", "I", "HOTBAR_SECOND_LAST", "HOTBAR_FIRST", "ARRIVAL_DISTANCE_SQ", "D", "LINE_PROXIMITY_SQ", "MAX_RAY_DISTANCE", "MAX_HIGHLIGHT_BLOCKS", "OBSTRUCTION_AIR_EXIT_TICKS", "START_DISTANCE_SQ", "AUTO_ADVANCE_DELAY_TICKS", "OBSTRUCTION_HIGHLIGHT_TICKS", "ROUTE_RAY_HEIGHT", "lastLevel", "Lnet/minecraft/class_1937;", "routePoints", "Ljava/util/List;", "Ljava/util/HashSet;", "Lkotlin/collections/HashSet;", "routePointKeys", "Ljava/util/HashSet;", "currentIndex", "pendingIndex", "autoRunActive", "Z", "autoRunCooldownTicks", "autoRunLastIndex", "cobalt"})
@SourceDebugExtension(value={"SMAP\nGrottoRouteRenderer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GrottoRouteRenderer.kt\norg/cobalt/internal/grotto/GrottoRouteRenderer\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,648:1\n1401#2,2:649\n*S KotlinDebug\n*F\n+ 1 GrottoRouteRenderer.kt\norg/cobalt/internal/grotto/GrottoRouteRenderer\n*L\n455#1:649,2\n*E\n"})
public final class GrottoRouteRenderer {
    @NotNull
    public static final GrottoRouteRenderer INSTANCE = new GrottoRouteRenderer();
    @NotNull
    private static final String TAG_POINTS = "grotto-route-points";
    @NotNull
    private static final String TAG_BLOCKS = "grotto-route-blocks";
    private static final int PERSIST_TICKS = 630720000;
    private static final int HOTBAR_SECOND_LAST = 7;
    private static final int HOTBAR_FIRST = 0;
    private static final double ARRIVAL_DISTANCE_SQ = 2.25;
    private static final double LINE_PROXIMITY_SQ = 64.0;
    private static final double MAX_RAY_DISTANCE = 96.0;
    private static final int MAX_HIGHLIGHT_BLOCKS = 128;
    private static final int OBSTRUCTION_AIR_EXIT_TICKS = 2;
    private static final double START_DISTANCE_SQ = 2.25;
    private static final int AUTO_ADVANCE_DELAY_TICKS = 100;
    private static final int OBSTRUCTION_HIGHLIGHT_TICKS = 40;
    private static final double ROUTE_RAY_HEIGHT = 2.0;
    @Nullable
    private static class_1937 lastLevel;
    @NotNull
    private static List<? extends class_243> routePoints;
    @NotNull
    private static final HashSet<Long> routePointKeys;
    private static int currentIndex;
    private static int pendingIndex;
    private static boolean autoRunActive;
    private static int autoRunCooldownTicks;
    private static int autoRunLastIndex;

    private GrottoRouteRenderer() {
    }

    @JvmStatic
    public static final void init() {
        EventBus.register(INSTANCE);
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.End event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        GrottoRouteRenderer.tick(class_3102);
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)FairyModule.INSTANCE.getRenderRoutes().getValue()).booleanValue()) {
            OverlayRenderEngine.INSTANCE.clearTag(TAG_POINTS);
            OverlayRenderEngine.INSTANCE.clearTag(TAG_BLOCKS);
            return;
        }
        OverlayRenderEngine.INSTANCE.render(event.getContext());
        this.renderGradientLines(event.getContext());
        this.renderPointOutlines(event.getContext());
        this.renderLabels(event.getContext());
    }

    @JvmStatic
    public static final void tick(@NotNull class_310 client) {
        Intrinsics.checkNotNullParameter((Object)client, (String)"client");
        class_638 level2 = client.field_1687;
        if (!Intrinsics.areEqual((Object)level2, (Object)lastLevel)) {
            GrottoRouteRenderer.clear();
            lastLevel = (class_1937)level2;
        }
        if (client.field_1724 == null || routePoints.size() < 2) {
            return;
        }
        if (pendingIndex >= 0) {
            class_243 target = routePoints.get(pendingIndex);
            class_746 class_7462 = client.field_1724;
            Intrinsics.checkNotNull((Object)class_7462);
            class_243 class_2432 = class_7462.method_73189();
            Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"position(...)");
            class_243 pos = class_2432;
            double dx = pos.field_1352 - target.field_1352;
            double dy = pos.field_1351 - target.field_1351;
            double dz = pos.field_1350 - target.field_1350;
            if (dx * dx + dy * dy + dz * dz <= 2.25) {
                InventoryUtils.holdHotbarSlot(0);
                currentIndex = pendingIndex;
                pendingIndex = -1;
            }
        }
        INSTANCE.handleAutoRun(client);
        if (((Boolean)FairyModule.INSTANCE.getRouteObstructionHighlights().getValue()).booleanValue()) {
            INSTANCE.updateObstructionHighlights(client);
        } else {
            OverlayRenderEngine.INSTANCE.clearTag(TAG_BLOCKS);
        }
    }

    @JvmStatic
    public static final void setRoute(@Nullable class_1937 level2, @Nullable List<? extends class_243> points) {
        GrottoRouteRenderer.clear();
        if (level2 == null || points == null || points.size() < 2) {
            return;
        }
        routePoints = CollectionsKt.toList((Iterable)points);
        routePointKeys.clear();
        for (class_243 class_2432 : routePoints) {
            class_2338 pos;
            Intrinsics.checkNotNullExpressionValue((Object)class_2338.method_49637((double)class_2432.field_1352, (double)class_2432.field_1351, (double)class_2432.field_1350), (String)"containing(...)");
            routePointKeys.add(pos.method_10063());
        }
        currentIndex = 0;
        pendingIndex = -1;
        HashSet<Long> outlined = new HashSet<Long>();
        int n = Math.max(1, points.size() - 1);
        int n2 = points.size() - 1;
        for (int i = 0; i < n2; ++i) {
            class_2338 bPos;
            class_2338 aPos;
            class_243 a = points.get(i);
            class_243 b = points.get(i + 1);
            Color colorA = INSTANCE.gradientColor((double)i / ((double)n - 1.0));
            Color colorB = INSTANCE.gradientColor((double)(i + 1) / ((double)n - 1.0));
            Intrinsics.checkNotNullExpressionValue((Object)class_2338.method_49637((double)a.field_1352, (double)a.field_1351, (double)a.field_1350), (String)"containing(...)");
            Intrinsics.checkNotNullExpressionValue((Object)class_2338.method_49637((double)b.field_1352, (double)b.field_1351, (double)b.field_1350), (String)"containing(...)");
            if (outlined.add(aPos.method_10063())) {
                OverlayRenderEngine.INSTANCE.outlineBlockColor(level2, aPos, INSTANCE.toOverlayColor(colorA), 630720000, TAG_POINTS, 2.2f);
            }
            if (!outlined.add(bPos.method_10063())) continue;
            OverlayRenderEngine.INSTANCE.outlineBlockColor(level2, bPos, INSTANCE.toOverlayColor(colorB), 630720000, TAG_POINTS, 2.2f);
        }
    }

    @JvmStatic
    public static final void clear() {
        routePoints = CollectionsKt.emptyList();
        routePointKeys.clear();
        currentIndex = 0;
        pendingIndex = -1;
        autoRunActive = false;
        autoRunCooldownTicks = 0;
        autoRunLastIndex = 0;
        OverlayRenderEngine.INSTANCE.clearTag(TAG_POINTS);
        OverlayRenderEngine.INSTANCE.clearTag(TAG_BLOCKS);
    }

    @JvmStatic
    public static final boolean advanceToNext(@Nullable class_310 client) {
        if (client == null || client.field_1724 == null) {
            return false;
        }
        if (routePoints.size() < 2) {
            return false;
        }
        if (pendingIndex >= 0) {
            return false;
        }
        if (currentIndex >= routePoints.size() - 1) {
            class_1937 class_19372 = (class_1937)client.field_1687;
            class_746 class_7462 = client.field_1724;
            Intrinsics.checkNotNull((Object)class_7462);
            if (INSTANCE.canSeeStart(class_19372, (class_1657)class_7462)) {
                currentIndex = 0;
            } else {
                GrottoChat.autoRoutes("Route complete.");
                return false;
            }
        }
        class_746 class_7463 = client.field_1724;
        Intrinsics.checkNotNull((Object)class_7463);
        class_746 player = class_7463;
        pendingIndex = currentIndex + 1;
        class_243 next = routePoints.get(pendingIndex);
        player.method_5702(class_2183.class_2184.field_9851, next);
        InventoryUtils.holdHotbarSlot(7);
        class_304 class_3042 = client.field_1690.field_1904;
        if (class_3042 != null) {
            class_3042.method_23481(true);
        }
        TickScheduler.schedule(1L, () -> GrottoRouteRenderer.advanceToNext$lambda$0(client));
        return true;
    }

    @JvmStatic
    public static final boolean canStartAutoRoute(@Nullable class_310 client) {
        if (client == null || client.field_1724 == null) {
            return false;
        }
        if (routePoints.size() < 2) {
            return false;
        }
        if (autoRunActive || pendingIndex >= 0) {
            return false;
        }
        class_746 class_7462 = client.field_1724;
        Intrinsics.checkNotNull((Object)class_7462);
        return INSTANCE.isNearStart((class_1657)class_7462);
    }

    @JvmStatic
    public static final boolean startAutoRun(@Nullable class_310 client) {
        if (!GrottoRouteRenderer.canStartAutoRoute(client)) {
            return false;
        }
        autoRunActive = true;
        autoRunCooldownTicks = 0;
        autoRunLastIndex = currentIndex = 0;
        pendingIndex = -1;
        if (!GrottoRouteRenderer.advanceToNext(client)) {
            autoRunActive = false;
            return false;
        }
        return true;
    }

    @JvmStatic
    public static final void stopAutoRun() {
        autoRunActive = false;
        autoRunCooldownTicks = 0;
        autoRunLastIndex = currentIndex;
    }

    @JvmStatic
    public static final boolean isAutoRunActive() {
        return autoRunActive;
    }

    private final boolean canSeeStart(class_1937 level2, class_1657 player) {
        if (level2 == null || player == null || routePoints.isEmpty()) {
            return false;
        }
        class_243 class_2432 = player.method_5836(1.0f);
        Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"getEyePosition(...)");
        class_243 eye = class_2432;
        class_243 target = routePoints.get(0);
        class_3965 class_39652 = level2.method_17742(new class_3959(eye, target, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)player));
        Intrinsics.checkNotNullExpressionValue((Object)class_39652, (String)"clip(...)");
        class_3965 hit = class_39652;
        return hit.method_17783() == class_239.class_240.field_1333;
    }

    private final boolean isNearStart(class_1657 player) {
        if (player == null || routePoints.isEmpty()) {
            return false;
        }
        class_243 start = routePoints.get(0);
        class_243 class_2432 = player.method_73189();
        Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"position(...)");
        class_243 pos = class_2432;
        double dx = pos.field_1352 - start.field_1352;
        double dy = pos.field_1351 - start.field_1351;
        double dz = pos.field_1350 - start.field_1350;
        return dx * dx + dy * dy + dz * dz <= 2.25;
    }

    private final void handleAutoRun(class_310 client) {
        if (!autoRunActive) {
            return;
        }
        if (client.field_1724 == null) {
            autoRunActive = false;
            return;
        }
        if (routePoints.size() < 2) {
            autoRunActive = false;
            return;
        }
        if (pendingIndex >= 0) {
            return;
        }
        if (autoRunCooldownTicks > 0) {
            int n = autoRunCooldownTicks;
            autoRunCooldownTicks = n + -1;
            return;
        }
        if (currentIndex != autoRunLastIndex) {
            autoRunLastIndex = currentIndex;
            autoRunCooldownTicks = 100;
            return;
        }
        int nextIndex = this.resolveNextIndex(client);
        if (nextIndex < 0) {
            autoRunActive = false;
            return;
        }
        class_746 class_7462 = client.field_1724;
        Intrinsics.checkNotNull((Object)class_7462);
        class_243 class_2432 = class_7462.method_5836(1.0f);
        Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"getEyePosition(...)");
        class_243 start = class_2432;
        class_243 end = routePoints.get(nextIndex);
        if (this.hasObstruction((class_1937)client.field_1687, start, end)) {
            autoRunActive = false;
            return;
        }
        if (!GrottoRouteRenderer.advanceToNext(client)) {
            autoRunActive = false;
        }
    }

    private final int resolveNextIndex(class_310 client) {
        if (client.field_1724 == null) {
            return -1;
        }
        if (routePoints.size() < 2) {
            return -1;
        }
        if (currentIndex >= routePoints.size() - 1) {
            if (this.canSeeStart((class_1937)client.field_1687, (class_1657)client.field_1724)) {
                return 1;
            }
            return -1;
        }
        return currentIndex + 1;
    }

    private final boolean hasObstruction(class_1937 level2, class_243 start, class_243 end) {
        int stepY;
        int stepX;
        if (level2 == null) {
            return false;
        }
        double totalDistance = Math.min(96.0, start.method_1022(end));
        int maxSteps = (int)Math.ceil(totalDistance * 3.0) + 1;
        double dx = end.field_1352 - start.field_1352;
        double dy = end.field_1351 - start.field_1351;
        double dz = end.field_1350 - start.field_1350;
        if (dx == 0.0 && dy == 0.0 && dz == 0.0) {
            return false;
        }
        int x = (int)Math.floor(start.field_1352);
        int y = (int)Math.floor(start.field_1351);
        int z = (int)Math.floor(start.field_1350);
        int endX = (int)Math.floor(end.field_1352);
        int endY = (int)Math.floor(end.field_1351);
        int endZ = (int)Math.floor(end.field_1350);
        int n = dx > 0.0 ? 1 : (stepX = dx < 0.0 ? -1 : 0);
        int n2 = dy > 0.0 ? 1 : (stepY = dy < 0.0 ? -1 : 0);
        int stepZ = dz > 0.0 ? 1 : (dz < 0.0 ? -1 : 0);
        double tMaxX = this.intBound(start.field_1352, dx);
        double tMaxY = this.intBound(start.field_1351, dy);
        double tMaxZ = this.intBound(start.field_1350, dz);
        double tDeltaX = stepX == 0 ? Double.POSITIVE_INFINITY : 1.0 / Math.abs(dx);
        double tDeltaY = stepY == 0 ? Double.POSITIVE_INFINITY : 1.0 / Math.abs(dy);
        double tDeltaZ = stepZ == 0 ? Double.POSITIVE_INFINITY : 1.0 / Math.abs(dz);
        for (int i = 0; i < maxSteps && (x != endX || y != endY || z != endZ); ++i) {
            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    x += stepX;
                    tMaxX += tDeltaX;
                } else {
                    z += stepZ;
                    tMaxZ += tDeltaZ;
                }
            } else if (tMaxY < tMaxZ) {
                y += stepY;
                tMaxY += tDeltaY;
            } else {
                z += stepZ;
                tMaxZ += tDeltaZ;
            }
            class_2338 pos = new class_2338(x, y, z);
            if (level2.method_8320(pos).method_26215()) continue;
            return true;
        }
        return false;
    }

    private final void updateObstructionHighlights(class_310 client) {
        class_243 next;
        if (client.field_1724 == null || client.field_1687 == null) {
            return;
        }
        if (routePoints.size() < 2) {
            OverlayRenderEngine.INSTANCE.clearTag(TAG_BLOCKS);
            return;
        }
        HashSet highlighted = new HashSet();
        class_746 class_7462 = client.field_1724;
        Intrinsics.checkNotNull((Object)class_7462);
        class_243 class_2432 = class_7462.method_73189();
        Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"position(...)");
        class_243 playerPos = class_2432;
        int bestIndex = -1;
        double bestDistSq = Double.POSITIVE_INFINITY;
        int n = routePoints.size() - 1;
        for (int i = 0; i < n; ++i) {
            class_243 b;
            class_243 a = routePoints.get(i);
            double distSq = this.distanceToSegmentSq(playerPos, a, b = routePoints.get(i + 1));
            if (!(distSq < bestDistSq)) continue;
            bestDistSq = distSq;
            bestIndex = i;
        }
        if (bestIndex < 0 || bestIndex >= routePoints.size() - 1) {
            return;
        }
        if (bestDistSq > 64.0) {
            OverlayRenderEngine.INSTANCE.clearTag(TAG_BLOCKS);
            return;
        }
        class_243 prev = routePoints.get(bestIndex);
        if (prev.method_1022(next = routePoints.get(bestIndex + 1)) < 0.01) {
            OverlayRenderEngine.INSTANCE.clearTag(TAG_BLOCKS);
            return;
        }
        class_243 startRay = new class_243(prev.field_1352, prev.field_1351 + 2.0, prev.field_1350);
        class_243 endRay = new class_243(next.field_1352, next.field_1351 + 2.0, next.field_1350);
        OverlayRenderEngine.INSTANCE.clearTag(TAG_BLOCKS);
        class_638 class_6382 = client.field_1687;
        Intrinsics.checkNotNull((Object)class_6382);
        int count = this.traceObstructions((class_1937)class_6382, startRay, endRay, highlighted);
        if (count == 0) {
            OverlayRenderEngine.INSTANCE.clearTag(TAG_BLOCKS);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void renderLabels(@NotNull WorldRenderContext context) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        if (routePoints.isEmpty()) {
            return;
        }
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 client = class_3102;
        if (client.field_1687 == null) {
            return;
        }
        class_4587 class_45872 = context.getMatrixStack();
        if (class_45872 == null) {
            class_45872 = new class_4587();
        }
        class_4587 matrices = class_45872;
        class_327 class_3272 = client.field_1772;
        Intrinsics.checkNotNullExpressionValue((Object)class_3272, (String)"font");
        class_327 font = class_3272;
        class_4597.class_4598 class_45982 = client.method_22940().method_23000();
        Intrinsics.checkNotNullExpressionValue((Object)class_45982, (String)"bufferSource(...)");
        class_4597.class_4598 buffer = class_45982;
        class_243 class_2432 = context.getCamera().method_71156();
        Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"position(...)");
        class_243 cam = class_2432;
        int light = 0xF000F0;
        float scale = 0.06f;
        try {
            int startIndex;
            GlStateManager._enableBlend();
            GlStateManager._blendFuncSeparate((int)770, (int)771, (int)1, (int)771);
            GlStateManager._disableDepthTest();
            GlStateManager._depthMask((boolean)false);
            int n = routePoints.size();
            for (int i = startIndex = Math.min(currentIndex + 1, routePoints.size()); i < n; ++i) {
                class_2338 blockPos;
                class_243 p = routePoints.get(i);
                int labelIndex = i - currentIndex;
                String label = String.valueOf(labelIndex);
                float textWidth = font.method_1727(label);
                Intrinsics.checkNotNullExpressionValue((Object)class_2338.method_49637((double)p.field_1352, (double)p.field_1351, (double)p.field_1350), (String)"containing(...)");
                double labelX = (double)blockPos.method_10263() + 0.5;
                double labelY = (double)blockPos.method_10264() + 1.02;
                double labelZ = (double)blockPos.method_10260() + 0.5;
                matrices.method_22903();
                matrices.method_22904(labelX - cam.field_1352, labelY - cam.field_1351, labelZ - cam.field_1350);
                matrices.method_22907((Quaternionfc)context.getCamera().method_23767());
                matrices.method_22905(-scale, -scale, scale);
                font.method_27521(label, -textWidth / 2.0f, 0.0f, -1, false, matrices.method_23760().method_23761(), (class_4597)buffer, class_327.class_6415.field_33994, 0, light);
                matrices.method_22909();
            }
            buffer.method_22993();
        }
        finally {
            GlStateManager._depthMask((boolean)true);
            GlStateManager._enableDepthTest();
            GlStateManager._disableBlend();
        }
    }

    public final void renderHudLabels(@NotNull class_332 graphics) {
        int startIndex;
        Object object;
        class_310 client;
        block5: {
            Intrinsics.checkNotNullParameter((Object)graphics, (String)"graphics");
            if (routePoints.isEmpty()) {
                return;
            }
            class_310 class_3102 = class_310.method_1551();
            Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
            client = class_3102;
            if (client.field_1687 == null || client.field_1724 == null) {
                return;
            }
            Method[] methodArray = client.field_1773.getClass().getMethods();
            Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"getMethods(...)");
            Object[] $this$firstOrNull$iv = methodArray;
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                Method method = (Method)element$iv;
                boolean bl = false;
                if (!(Intrinsics.areEqual((Object)method.getName(), (Object)"projectPointToScreen") && method.getParameterTypes().length == 1)) continue;
                object = element$iv;
                break block5;
            }
            object = null;
        }
        Method method = (Method)object;
        if (method == null) {
            return;
        }
        Method projectMethod = method;
        int width = graphics.method_51421();
        int height = graphics.method_51443();
        class_327 class_3272 = client.field_1772;
        Intrinsics.checkNotNullExpressionValue((Object)class_3272, (String)"font");
        class_327 font = class_3272;
        int n = routePoints.size();
        for (int i = startIndex = Math.min(currentIndex + 1, routePoints.size()); i < n; ++i) {
            class_243 projected;
            class_2338 blockPos;
            class_243 p = routePoints.get(i);
            Intrinsics.checkNotNullExpressionValue((Object)class_2338.method_49637((double)p.field_1352, (double)p.field_1351, (double)p.field_1350), (String)"containing(...)");
            class_243 world = new class_243((double)blockPos.method_10263() + 0.5, (double)blockPos.method_10264() + 1.02, (double)blockPos.method_10260() + 0.5);
            Object[] objectArray = new Object[]{world};
            Object object2 = projectMethod.invoke((Object)client.field_1773, objectArray);
            if ((object2 instanceof class_243 ? (class_243)object2 : null) == null) continue;
            projected = projected;
            if (!(Math.abs(projected.field_1352) <= Double.MAX_VALUE) || !(Math.abs(projected.field_1351) <= Double.MAX_VALUE)) continue;
            boolean bl = Math.abs(projected.field_1350) <= Double.MAX_VALUE;
            if (!bl || projected.field_1350 < -1.0 || projected.field_1350 > 1.0 || projected.field_1352 < -1.2 || projected.field_1352 > 1.2 || projected.field_1351 < -1.2 || projected.field_1351 > 1.2) continue;
            double sx = (projected.field_1352 * 0.5 + 0.5) * (double)width;
            double sy = (0.5 - projected.field_1351 * 0.5) * (double)height;
            int labelIndex = i - currentIndex;
            String label = String.valueOf(labelIndex);
            int textX = (int)(sx - (double)font.method_1727(label) / 2.0);
            int textY = (int)(sy - (double)font.field_2000 / 2.0);
            graphics.method_51433(font, label, textX, textY, -1, true);
        }
    }

    private final Color gradientColor(double t) {
        double clamped = Math.max(0.0, Math.min(1.0, t));
        int r = (int)(0.0 + (double)255 * clamped);
        int g = (int)((double)255 + (double)-255 * clamped);
        int b = 255;
        return new Color(r, g, b, 255);
    }

    private final OverlayRenderEngine.Color toOverlayColor(Color color) {
        return new OverlayRenderEngine.Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    private final void renderGradientLines(WorldRenderContext context) {
        if (routePoints.size() < 2) {
            return;
        }
        int segments = Math.max(1, routePoints.size() - 1);
        int n = routePoints.size() - 1;
        for (int i = 0; i < n; ++i) {
            class_243 a = routePoints.get(i);
            class_243 b = routePoints.get(i + 1);
            Color color = this.gradientColor((double)i / ((double)segments - 1.0));
            Render3D.drawLine(context, a, b, color, true, 2.0f);
        }
    }

    private final void renderPointOutlines(WorldRenderContext context) {
        if (routePoints.isEmpty()) {
            return;
        }
        int segments = Math.max(1, routePoints.size() - 1);
        int n = ((Collection)routePoints).size();
        for (int i = 0; i < n; ++i) {
            class_2338 pos;
            class_243 p = routePoints.get(i);
            Color color = this.gradientColor((double)i / ((double)segments - 1.0));
            Intrinsics.checkNotNullExpressionValue((Object)class_2338.method_49637((double)p.field_1352, (double)p.field_1351, (double)p.field_1350), (String)"containing(...)");
            class_238 box = new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + 1.0, (double)pos.method_10264() + 1.0, (double)pos.method_10260() + 1.0);
            Render3D.drawBox(context, box, color, true);
        }
    }

    private final int traceObstructions(class_1937 level2, class_243 start, class_243 end, Set<Long> highlighted) {
        int stepY;
        int stepX;
        double totalDistance = Math.min(96.0, start.method_1022(end));
        int maxSteps = (int)Math.ceil(totalDistance * 3.0) + 1;
        double dx = end.field_1352 - start.field_1352;
        double dy = end.field_1351 - start.field_1351;
        double dz = end.field_1350 - start.field_1350;
        if (dx == 0.0 && dy == 0.0 && dz == 0.0) {
            return 0;
        }
        int x = (int)Math.floor(start.field_1352);
        int y = (int)Math.floor(start.field_1351);
        int z = (int)Math.floor(start.field_1350);
        int endX = (int)Math.floor(end.field_1352);
        int endY = (int)Math.floor(end.field_1351);
        int endZ = (int)Math.floor(end.field_1350);
        int n = dx > 0.0 ? 1 : (stepX = dx < 0.0 ? -1 : 0);
        int n2 = dy > 0.0 ? 1 : (stepY = dy < 0.0 ? -1 : 0);
        int stepZ = dz > 0.0 ? 1 : (dz < 0.0 ? -1 : 0);
        double tMaxX = this.intBound(start.field_1352, dx);
        double tMaxY = this.intBound(start.field_1351, dy);
        double tMaxZ = this.intBound(start.field_1350, dz);
        double tDeltaX = stepX == 0 ? Double.POSITIVE_INFINITY : 1.0 / Math.abs(dx);
        double tDeltaY = stepY == 0 ? Double.POSITIVE_INFINITY : 1.0 / Math.abs(dy);
        double tDeltaZ = stepZ == 0 ? Double.POSITIVE_INFINITY : 1.0 / Math.abs(dz);
        boolean foundHit = false;
        int airAfterHit = 0;
        int highlightedCount = 0;
        for (int i = 0; i < maxSteps && (x != endX || y != endY || z != endZ); ++i) {
            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    x += stepX;
                    tMaxX += tDeltaX;
                } else {
                    z += stepZ;
                    tMaxZ += tDeltaZ;
                }
            } else if (tMaxY < tMaxZ) {
                y += stepY;
                tMaxY += tDeltaY;
            } else {
                z += stepZ;
                tMaxZ += tDeltaZ;
            }
            class_2338 pos = new class_2338(x, y, z);
            boolean isAir = level2.method_8320(pos).method_26215();
            if (!isAir) {
                foundHit = true;
                airAfterHit = 0;
                long key = pos.method_10063();
                if (routePointKeys.contains(key) || !highlighted.add(key)) continue;
                OverlayRenderEngine.INSTANCE.highlightBlock(level2, pos, 40, TAG_BLOCKS);
                if (++highlightedCount < 128) continue;
                return highlightedCount;
            }
            if (!foundHit || ++airAfterHit < 2) continue;
            return highlightedCount;
        }
        return highlightedCount;
    }

    private final double intBound(double s, double ds) {
        if (ds > 0.0) {
            return (Math.ceil(s) - s) / ds;
        }
        if (ds < 0.0) {
            return (s - Math.floor(s)) / -ds;
        }
        return Double.POSITIVE_INFINITY;
    }

    private final double distanceToSegmentSq(class_243 p, class_243 a, class_243 b) {
        double abx = b.field_1352 - a.field_1352;
        double aby = b.field_1351 - a.field_1351;
        double abz = b.field_1350 - a.field_1350;
        double apx = p.field_1352 - a.field_1352;
        double apy = p.field_1351 - a.field_1351;
        double apz = p.field_1350 - a.field_1350;
        double abLenSq = abx * abx + aby * aby + abz * abz;
        if (abLenSq <= 1.0E-6) {
            return apx * apx + apy * apy + apz * apz;
        }
        double t = (apx * abx + apy * aby + apz * abz) / abLenSq;
        t = Math.max(0.0, Math.min(1.0, t));
        double cx = a.field_1352 + abx * t;
        double cy = a.field_1351 + aby * t;
        double cz = a.field_1350 + abz * t;
        double dx = p.field_1352 - cx;
        double dy = p.field_1351 - cy;
        double dz = p.field_1350 - cz;
        return dx * dx + dy * dy + dz * dz;
    }

    private static final void advanceToNext$lambda$0(class_310 $client) {
        block0: {
            class_304 class_3042 = $client.field_1690.field_1904;
            if (class_3042 == null) break block0;
            class_3042.method_23481(false);
        }
    }

    static {
        routePoints = CollectionsKt.emptyList();
        routePointKeys = new HashSet();
        pendingIndex = -1;
    }
}

