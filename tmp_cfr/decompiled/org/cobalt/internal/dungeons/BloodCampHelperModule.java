/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.SetsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_124
 *  net.minecraft.class_1297
 *  net.minecraft.class_1304
 *  net.minecraft.class_1531
 *  net.minecraft.class_1642
 *  net.minecraft.class_1799
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_266
 *  net.minecraft.class_268
 *  net.minecraft.class_269
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_8646
 *  net.minecraft.class_9011
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.dungeons;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1531;
import net.minecraft.class_1642;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_266;
import net.minecraft.class_268;
import net.minecraft.class_269;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_8646;
import net.minecraft.class_9011;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.WorldRenderContext;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.ColorSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.util.SkyblockItemUtilsKt;
import org.cobalt.api.util.render.Render3D;
import org.cobalt.internal.dungeons.map.DoorKind;
import org.cobalt.internal.dungeons.map.DungeonDoor;
import org.cobalt.internal.dungeons.map.DungeonRoom;
import org.cobalt.internal.dungeons.map.DungeonScanState;
import org.cobalt.internal.dungeons.map.RoomKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00ba\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0006\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\"\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001jB\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\n\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\tH\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\u000e\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u000f\u0010\u0010\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u0010\u0010\u0003J\u0011\u0010\u0012\u001a\u0004\u0018\u00010\u0011H\u0002\u00a2\u0006\u0004\b\u0012\u0010\u0013JI\u0010\u001a\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u00142\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00140\u00172\u0012\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00110\u0017H\u0002\u00a2\u0006\u0004\b\u001a\u0010\u001bJ!\u0010\u001d\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\t2\b\u0010\u001c\u001a\u0004\u0018\u00010\u0011H\u0002\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\u0017\u0010 \u001a\u00020\u001f2\u0006\u0010\u001c\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\b \u0010!J\u0017\u0010%\u001a\u00020$2\u0006\u0010#\u001a\u00020\"H\u0002\u00a2\u0006\u0004\b%\u0010&J\u0017\u0010)\u001a\u00020$2\u0006\u0010(\u001a\u00020'H\u0002\u00a2\u0006\u0004\b)\u0010*J\u0017\u0010-\u001a\u00020\u001f2\u0006\u0010,\u001a\u00020+H\u0002\u00a2\u0006\u0004\b-\u0010.J\u000f\u0010/\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b/\u0010\u0003J\u0017\u00103\u001a\u0002022\u0006\u00101\u001a\u000200H\u0002\u00a2\u0006\u0004\b3\u00104J\u001f\u00107\u001a\u0002022\u0006\u00105\u001a\u0002022\u0006\u00106\u001a\u000200H\u0002\u00a2\u0006\u0004\b7\u00108J\u0017\u0010;\u001a\u0002092\u0006\u0010:\u001a\u000209H\u0002\u00a2\u0006\u0004\b;\u0010<J\u0017\u0010=\u001a\u00020$2\u0006\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b=\u0010>R\u0014\u0010@\u001a\u00020?8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b@\u0010AR\u0014\u0010B\u001a\u00020?8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bB\u0010AR\u0014\u0010C\u001a\u00020?8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bC\u0010AR\u0014\u0010D\u001a\u0002008\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bD\u0010ER\u0014\u0010F\u001a\u00020?8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bF\u0010AR\u0014\u0010G\u001a\u00020?8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bG\u0010AR\u0014\u0010H\u001a\u00020?8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bH\u0010AR\u0014\u0010I\u001a\u00020?8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bI\u0010AR\u0014\u0010K\u001a\u00020J8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bK\u0010LR\u0014\u0010M\u001a\u00020J8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bM\u0010LR\u0014\u0010N\u001a\u00020J8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bN\u0010LR\u0014\u0010O\u001a\u00020J8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bO\u0010LR\u0014\u0010P\u001a\u00020J8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bP\u0010LR\u0014\u0010R\u001a\u00020Q8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bR\u0010SR\u0014\u0010T\u001a\u00020Q8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bT\u0010SR\u0014\u0010U\u001a\u00020Q8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bU\u0010SR\u0014\u0010W\u001a\u00020V8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bW\u0010XR\u0014\u0010Y\u001a\u00020V8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bY\u0010XR\u0014\u0010[\u001a\u00020Z8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b[\u0010\\R \u0010_\u001a\u000e\u0012\u0004\u0012\u000200\u0012\u0004\u0012\u00020^0]8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b_\u0010`R\u0016\u0010a\u001a\u0002008\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\ba\u0010ER\u0018\u0010b\u001a\u0004\u0018\u00010\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bb\u0010cR\u0018\u0010d\u001a\u0004\u0018\u00010\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bd\u0010eR\u001a\u0010g\u001a\b\u0012\u0004\u0012\u0002090f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bg\u0010hR\u001a\u0010i\u001a\b\u0012\u0004\u0012\u0002090f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bi\u0010h\u00a8\u0006k"}, d2={"Lorg/cobalt/internal/dungeons/BloodCampHelperModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "Lnet/minecraft/class_638;", "level", "tickTracking", "(Lnet/minecraft/class_638;)V", "updateNextBloodDoor", "Lorg/cobalt/internal/dungeons/map/DungeonDoor;", "findNextDoorTowardBlood", "()Lorg/cobalt/internal/dungeons/map/DungeonDoor;", "Lorg/cobalt/internal/dungeons/map/DungeonRoom;", "startRoom", "targetRoom", "", "previousRoom", "previousDoor", "firstDoorInPath", "(Lorg/cobalt/internal/dungeons/map/DungeonRoom;Lorg/cobalt/internal/dungeons/map/DungeonRoom;Ljava/util/Map;Ljava/util/Map;)Lorg/cobalt/internal/dungeons/map/DungeonDoor;", "door", "renderNextDoorEsp", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;Lorg/cobalt/internal/dungeons/map/DungeonDoor;)V", "Lnet/minecraft/class_238;", "nextDoorBox", "(Lorg/cobalt/internal/dungeons/map/DungeonDoor;)Lnet/minecraft/class_238;", "Lnet/minecraft/class_1642;", "zombie", "", "isWatcher", "(Lnet/minecraft/class_1642;)Z", "Lnet/minecraft/class_1531;", "stand", "isBloodMobStand", "(Lnet/minecraft/class_1531;)Z", "Lnet/minecraft/class_243;", "position", "predictionBoxAt", "(Lnet/minecraft/class_243;)Lnet/minecraft/class_238;", "resetTracking", "", "value", "Ljava/awt/Color;", "colorFromSetting", "(I)Ljava/awt/Color;", "color", "alpha", "withAlpha", "(Ljava/awt/Color;I)Ljava/awt/Color;", "", "text", "stripFormatting", "(Ljava/lang/String;)Ljava/lang/String;", "isInDungeon", "(Lnet/minecraft/class_638;)Z", "", "WATCHER_SCAN_RADIUS", "D", "FIRST_WAVE_DISTANCE", "LATE_WAVE_DISTANCE", "DELTA_SAMPLES", "I", "MOTION_EPSILON_SQ", "MARKER_Y_OFFSET", "MARKER_HALF_WIDTH", "MARKER_HEIGHT", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "currentMarker", "traceLine", "firstWaveOnly", "nextDoorEsp", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "currentColor", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "predictedColor", "nextDoorColor", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "fillOpacity", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "lineWidth", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "", "Lorg/cobalt/internal/dungeons/BloodCampHelperModule$TrackedBloodMob;", "trackedMobs", "Ljava/util/Map;", "predictedMobCount", "nextBloodDoor", "Lorg/cobalt/internal/dungeons/map/DungeonDoor;", "trackedLevel", "Lnet/minecraft/class_638;", "", "watcherTextureIds", "Ljava/util/Set;", "bloodMobTextureIds", "TrackedBloodMob", "cobalt"})
@SourceDebugExtension(value={"SMAP\nBloodCampHelperModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BloodCampHelperModule.kt\norg/cobalt/internal/dungeons/BloodCampHelperModule\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,518:1\n1807#2,3:519\n296#2,2:522\n1#3:524\n*S KotlinDebug\n*F\n+ 1 BloodCampHelperModule.kt\norg/cobalt/internal/dungeons/BloodCampHelperModule\n*L\n253#1:519,3\n294#1:522,2\n*E\n"})
public final class BloodCampHelperModule
extends Module {
    @NotNull
    public static final BloodCampHelperModule INSTANCE = new BloodCampHelperModule();
    private static final double WATCHER_SCAN_RADIUS = 2.0;
    private static final double FIRST_WAVE_DISTANCE = 16.1;
    private static final double LATE_WAVE_DISTANCE = 11.9;
    private static final int DELTA_SAMPLES = 5;
    private static final double MOTION_EPSILON_SQ = 1.0E-4;
    private static final double MARKER_Y_OFFSET = 2.0;
    private static final double MARKER_HALF_WIDTH = 0.5;
    private static final double MARKER_HEIGHT = 2.0;
    @NotNull
    private static final CheckboxSetting enabled = new CheckboxSetting("Enabled", "Predict where Blood Room mobs spawned by The Watcher will land.", true);
    @NotNull
    private static final CheckboxSetting currentMarker = new CheckboxSetting("Current Marker", "Draw a marker on the tracked blood mob before it reaches the prediction.", true);
    @NotNull
    private static final CheckboxSetting traceLine = new CheckboxSetting("Trace Line", "Draw a line from the tracked blood mob to its predicted landing spot.", true);
    @NotNull
    private static final CheckboxSetting firstWaveOnly = new CheckboxSetting("First Wave Only", "Only render the first four blood mob predictions.", false);
    @NotNull
    private static final CheckboxSetting nextDoorEsp = new CheckboxSetting("Next Door ESP", "Render the next dungeon door on the shortest path toward Blood Camp.", true);
    @NotNull
    private static final ColorSetting currentColor = new ColorSetting("Current Color", "Color used for the live blood mob marker and trace.", -8858);
    @NotNull
    private static final ColorSetting predictedColor = new ColorSetting("Predicted Color", "Color used for the predicted blood mob landing box.", -44442);
    @NotNull
    private static final ColorSetting nextDoorColor = new ColorSetting("Door Color", "Color used for the next door ESP toward Blood Camp.", -9708033);
    @NotNull
    private static final SliderSetting fillOpacity = new SliderSetting("Fill Opacity", "Opacity of the rendered blood helper boxes.", 0.18, 0.0, 1.0, 0.0, 32, null);
    @NotNull
    private static final SliderSetting lineWidth = new SliderSetting("Line Width", "Thickness of the blood helper ESP lines.", 2.6, 0.5, 8.0, 0.0, 32, null);
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final Map<Integer, TrackedBloodMob> trackedMobs;
    private static int predictedMobCount;
    @Nullable
    private static DungeonDoor nextBloodDoor;
    @Nullable
    private static class_638 trackedLevel;
    @NotNull
    private static final Set<String> watcherTextureIds;
    @NotNull
    private static final Set<String> bloodMobTextureIds;

    private BloodCampHelperModule() {
        super("Blood Camp Helper");
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_638 class_6382 = BloodCampHelperModule.mc.field_1687;
        if (class_6382 == null) {
            BloodCampHelperModule $this$onTick_u24lambda_u240 = this;
            boolean bl = false;
            $this$onTick_u24lambda_u240.resetTracking();
            trackedLevel = null;
            return;
        }
        class_638 level2 = class_6382;
        if (trackedLevel != level2) {
            this.resetTracking();
            trackedLevel = level2;
        }
        if (!((Boolean)enabled.getValue()).booleanValue() || !this.isInDungeon(level2)) {
            this.resetTracking();
            return;
        }
        this.updateNextBloodDoor();
        this.tickTracking(level2);
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_638 class_6382 = BloodCampHelperModule.mc.field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        if (!((Boolean)enabled.getValue()).booleanValue() || !this.isInDungeon(level2)) {
            return;
        }
        if (((Boolean)nextDoorEsp.getValue()).booleanValue()) {
            this.renderNextDoorEsp(event, nextBloodDoor);
        }
        if (trackedMobs.isEmpty()) {
            return;
        }
        Color currentStroke = this.colorFromSetting(currentColor.getValue());
        Color predictedStroke = this.colorFromSetting(predictedColor.getValue());
        Color currentFill = this.withAlpha(currentStroke, (int)(((Number)fillOpacity.getValue()).doubleValue() * 170.0));
        Color predictedFill = this.withAlpha(predictedStroke, (int)(((Number)fillOpacity.getValue()).doubleValue() * 255.0));
        float outlineWidth = (float)((Number)lineWidth.getValue()).doubleValue();
        float traceWidth = RangesKt.coerceAtLeast((float)(outlineWidth - 0.7f), (float)1.0f);
        for (TrackedBloodMob tracked : trackedMobs.values()) {
            class_243 predicted;
            if (tracked.getPredictedPos() == null || ((Boolean)firstWaveOnly.getValue()).booleanValue() && !tracked.getFirstWave()) continue;
            if (((Boolean)currentMarker.getValue()).booleanValue()) {
                class_238 currentBox;
                Intrinsics.checkNotNullExpressionValue((Object)tracked.getEntity().method_5829().method_989(0.0, 2.0, 0.0).method_1009(0.08, 0.0, 0.08), (String)"inflate(...)");
                Render3D.drawStyledBox(event.getContext(), currentBox, currentStroke, currentFill, true, outlineWidth);
            }
            if (((Boolean)traceLine.getValue()).booleanValue()) {
                class_243 end;
                class_243 start;
                Intrinsics.checkNotNullExpressionValue((Object)tracked.getEntity().method_73189().method_1031(0.0, 2.2, 0.0), (String)"add(...)");
                Intrinsics.checkNotNullExpressionValue((Object)predicted.method_1031(0.0, 2.2, 0.0), (String)"add(...)");
                Render3D.drawLine(event.getContext(), start, end, currentStroke, true, traceWidth);
            }
            WorldRenderContext worldRenderContext = event.getContext();
            class_238 class_2383 = this.predictionBoxAt(predicted);
            Color color = tracked.getFirstWave() ? predictedStroke.brighter() : predictedStroke;
            Intrinsics.checkNotNull((Object)color);
            Render3D.drawStyledBox(worldRenderContext, class_2383, color, predictedFill, true, outlineWidth);
        }
    }

    private final void tickTracking(class_638 level2) {
        List watcherBoxes = new ArrayList();
        List candidateStands = new ArrayList();
        for (Object t : level2.method_18112()) {
            class_1297 entity;
            Intrinsics.checkNotNullExpressionValue(t, (String)"next(...)");
            class_1297 class_12972 = entity = (class_1297)t;
            if (class_12972 instanceof class_1642) {
                if (!this.isWatcher((class_1642)entity)) continue;
                ((Collection)watcherBoxes).add(((class_1642)entity).method_5829().method_1014(2.0));
                continue;
            }
            if (!(class_12972 instanceof class_1531) || !((class_1531)entity).method_5805() || trackedMobs.containsKey(((class_1531)entity).method_5628()) || ((class_1531)entity).method_6118(class_1304.field_6169).method_7960()) continue;
            ((Collection)candidateStands).add(entity);
        }
        if (!((Collection)watcherBoxes).isEmpty()) {
            for (class_1531 stand : candidateStands) {
                boolean bl;
                block10: {
                    Iterable $this$any$iv = watcherBoxes;
                    boolean $i$f$any = false;
                    if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                        bl = false;
                    } else {
                        for (Object element$iv : $this$any$iv) {
                            class_238 it = (class_238)element$iv;
                            boolean bl2 = false;
                            if (!it.method_994(stand.method_5829())) continue;
                            bl = true;
                            break block10;
                        }
                        bl = false;
                    }
                }
                if (!bl || !this.isBloodMobStand(stand)) continue;
                trackedMobs.put(stand.method_5628(), new TrackedBloodMob(stand));
            }
        }
        Iterator<Map.Entry<Integer, TrackedBloodMob>> iterator = trackedMobs.entrySet().iterator();
        while (iterator.hasNext()) {
            class_243 direction;
            TrackedBloodMob tracked = iterator.next().getValue();
            if (!tracked.getEntity().method_5805()) {
                iterator.remove();
                continue;
            }
            if (tracked.updateAndGetDirection() == null) continue;
            tracked.setFirstWave(predictedMobCount < 4);
            tracked.setPredictedPos(tracked.getStartPos().method_1019(direction.method_1021(tracked.getFirstWave() ? 16.1 : 11.9)));
            int n = predictedMobCount;
            predictedMobCount = n + 1;
        }
        if (watcherBoxes.isEmpty() && trackedMobs.isEmpty()) {
            predictedMobCount = 0;
        }
    }

    private final void updateNextBloodDoor() {
        if (!((Boolean)nextDoorEsp.getValue()).booleanValue()) {
            nextBloodDoor = null;
            return;
        }
        DungeonScanState.INSTANCE.tick();
        nextBloodDoor = this.findNextDoorTowardBlood();
    }

    private final DungeonDoor findNextDoorTowardBlood() {
        ArrayDeque<DungeonRoom> it32;
        Object v1;
        Object[] $this$firstOrNull$iv;
        DungeonRoom startRoom;
        block12: {
            if (!DungeonScanState.INSTANCE.isInDungeon()) {
                return null;
            }
            DungeonRoom dungeonRoom = DungeonScanState.INSTANCE.getCurrentRoom();
            if (dungeonRoom == null) {
                return null;
            }
            startRoom = dungeonRoom;
            if (startRoom.getType() == RoomKind.BLOOD) {
                return null;
            }
            $this$firstOrNull$iv = (Object[])startRoom.getDoors();
            boolean $i$f$firstOrNull = false;
            for (Object t : $this$firstOrNull$iv) {
                DungeonDoor it2 = (DungeonDoor)t;
                boolean bl = false;
                if (!(it2.getType() == DoorKind.BLOOD)) continue;
                v1 = t;
                break block12;
            }
            v1 = null;
        }
        DungeonDoor dungeonDoor = v1;
        if (dungeonDoor != null) {
            DungeonDoor it32 = dungeonDoor;
            boolean bl = false;
            return it32;
        }
        $this$firstOrNull$iv = new DungeonRoom[]{startRoom};
        LinkedHashSet visited = SetsKt.linkedSetOf((Object[])$this$firstOrNull$iv);
        ArrayDeque<DungeonRoom> $this$findNextDoorTowardBlood_u24lambda_u242 = it32 = new ArrayDeque<DungeonRoom>();
        boolean bl = false;
        $this$findNextDoorTowardBlood_u24lambda_u242.add(startRoom);
        ArrayDeque<DungeonRoom> queue = it32;
        Map previousRoom = new LinkedHashMap();
        Map previousDoor = new LinkedHashMap();
        while (!((Collection)queue).isEmpty()) {
            Iterator iterator;
            DungeonRoom dungeonRoom = (DungeonRoom)queue.removeFirst();
            Intrinsics.checkNotNullExpressionValue(dungeonRoom.getDoors().iterator(), (String)"iterator(...)");
            while (iterator.hasNext()) {
                Iterator iterator2;
                Object e = iterator.next();
                Intrinsics.checkNotNullExpressionValue(e, (String)"next(...)");
                DungeonDoor door = (DungeonDoor)e;
                if (door.getType() == DoorKind.BLOOD) {
                    DungeonDoor dungeonDoor2;
                    if (dungeonRoom == startRoom) {
                        dungeonDoor2 = door;
                    } else {
                        Intrinsics.checkNotNull((Object)dungeonRoom);
                        dungeonDoor2 = this.firstDoorInPath(startRoom, dungeonRoom, previousRoom, previousDoor);
                    }
                    return dungeonDoor2;
                }
                Intrinsics.checkNotNullExpressionValue(door.getRooms().iterator(), (String)"iterator(...)");
                while (iterator2.hasNext()) {
                    Object e2 = iterator2.next();
                    Intrinsics.checkNotNullExpressionValue(e2, (String)"next(...)");
                    DungeonRoom neighbor = (DungeonRoom)e2;
                    if (neighbor == dungeonRoom || !visited.add(neighbor)) continue;
                    previousRoom.put(neighbor, dungeonRoom);
                    previousDoor.put(neighbor, door);
                    if (neighbor.getType() == RoomKind.BLOOD) {
                        return this.firstDoorInPath(startRoom, neighbor, previousRoom, previousDoor);
                    }
                    queue.addLast(neighbor);
                }
            }
        }
        return null;
    }

    private final DungeonDoor firstDoorInPath(DungeonRoom startRoom, DungeonRoom targetRoom, Map<DungeonRoom, DungeonRoom> previousRoom, Map<DungeonRoom, DungeonDoor> previousDoor) {
        DungeonRoom current = targetRoom;
        while (previousRoom.get(current) != null) {
            DungeonRoom parent;
            if (previousDoor.get(current) == null) {
                return null;
            }
            if (parent == startRoom) {
                DungeonDoor door;
                return door;
            }
            current = parent;
        }
        return null;
    }

    private final void renderNextDoorEsp(WorldRenderEvent.Last event, DungeonDoor door) {
        DungeonDoor dungeonDoor = door;
        if (dungeonDoor == null) {
            return;
        }
        DungeonDoor targetDoor = dungeonDoor;
        Color stroke = this.colorFromSetting(nextDoorColor.getValue());
        Color glow = this.withAlpha(stroke, (int)(((Number)fillOpacity.getValue()).doubleValue() * 105.0));
        Color fill = this.withAlpha(stroke, (int)(((Number)fillOpacity.getValue()).doubleValue() * 185.0));
        class_238 box = this.nextDoorBox(targetDoor);
        WorldRenderContext worldRenderContext = event.getContext();
        class_238 class_2383 = box.method_1009(0.18, 0.1, 0.18);
        Intrinsics.checkNotNullExpressionValue((Object)class_2383, (String)"inflate(...)");
        Color color = stroke.brighter();
        Intrinsics.checkNotNullExpressionValue((Object)color, (String)"brighter(...)");
        Render3D.drawStyledBox(worldRenderContext, class_2383, color, glow, true, (float)(((Number)lineWidth.getValue()).doubleValue() + 1.3));
        Render3D.drawStyledBox(event.getContext(), box, stroke, fill, true, (float)((Number)lineWidth.getValue()).doubleValue());
    }

    private final class_238 nextDoorBox(DungeonDoor door) {
        class_2338 center = door.getComponent().toWorldCenter();
        double minY = 69.0;
        double maxY = 73.0;
        return (door.getComponent().getX() & 1) == 1 ? new class_238((double)center.method_10263() + 0.5 - 0.5, minY, (double)center.method_10260() + 0.5 - 1.5, (double)center.method_10263() + 0.5 + 0.5, maxY, (double)center.method_10260() + 0.5 + 1.5) : new class_238((double)center.method_10263() + 0.5 - 1.5, minY, (double)center.method_10260() + 0.5 - 0.5, (double)center.method_10263() + 0.5 + 1.5, maxY, (double)center.method_10260() + 0.5 + 0.5);
    }

    private final boolean isWatcher(class_1642 zombie) {
        class_1799 class_17992 = zombie.method_6118(class_1304.field_6169);
        Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getItemBySlot(...)");
        class_1799 head = class_17992;
        return CollectionsKt.contains((Iterable)watcherTextureIds, (Object)SkyblockItemUtilsKt.getHeadTextureId(head));
    }

    private final boolean isBloodMobStand(class_1531 stand) {
        class_1799 class_17992 = stand.method_6118(class_1304.field_6169);
        Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getItemBySlot(...)");
        class_1799 head = class_17992;
        return CollectionsKt.contains((Iterable)bloodMobTextureIds, (Object)SkyblockItemUtilsKt.getHeadTextureId(head));
    }

    private final class_238 predictionBoxAt(class_243 position) {
        return new class_238(position.field_1352 - 0.5, position.field_1351 + 2.0, position.field_1350 - 0.5, position.field_1352 + 0.5, position.field_1351 + 2.0 + 2.0, position.field_1350 + 0.5);
    }

    private final void resetTracking() {
        trackedMobs.clear();
        predictedMobCount = 0;
        nextBloodDoor = null;
    }

    private final Color colorFromSetting(int value) {
        return new Color(value, true);
    }

    private final Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), RangesKt.coerceIn((int)alpha, (int)0, (int)255));
    }

    private final String stripFormatting(String text) {
        String string = class_124.method_539((String)text);
        if (string == null) {
            string = text;
        }
        return string;
    }

    private final boolean isInDungeon(class_638 level2) {
        String fullText;
        String score;
        class_269 class_2692 = level2.method_8428();
        Intrinsics.checkNotNullExpressionValue((Object)class_2692, (String)"getScoreboard(...)");
        class_269 scoreboard = class_2692;
        class_266 class_2662 = scoreboard.method_1189(class_8646.field_45157);
        if (class_2662 == null) {
            return false;
        }
        class_266 objective = class_2662;
        StringBuilder allText = new StringBuilder();
        String string = objective.method_1114().getString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        String display = string;
        if (((CharSequence)display).length() > 0) {
            allText.append(display).append(' ');
        }
        try {
            Collection collection = scoreboard.method_1184(objective);
            Intrinsics.checkNotNullExpressionValue((Object)collection, (String)"listPlayerScores(...)");
            Collection scores = collection;
            for (Object e : scores) {
                String ownerName;
                Intrinsics.checkNotNullExpressionValue(e, (String)"next(...)");
                score = (class_9011)e;
                Intrinsics.checkNotNullExpressionValue((Object)score.comp_2127(), (String)"owner(...)");
                class_268 team = scoreboard.method_1164(ownerName);
                String lineText = team != null ? team.method_1144().getString() + ownerName + team.method_1136().getString() : ownerName;
                allText.append(lineText).append(' ');
            }
        }
        catch (Exception scores) {
            // empty catch block
        }
        String string2 = allText.toString();
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toString(...)");
        score = fullText = this.stripFormatting(string2);
        Locale locale = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
        String string3 = score.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
        String lower = string3;
        if (StringsKt.contains$default((CharSequence)lower, (CharSequence)"hub", (boolean)false, (int)2, null)) {
            return false;
        }
        if (StringsKt.contains$default((CharSequence)lower, (CharSequence)"catacombs", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)lower, (CharSequence)"the catacombs", (boolean)false, (int)2, null)) {
            return true;
        }
        if (StringsKt.contains$default((CharSequence)lower, (CharSequence)"(e)", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)lower, (CharSequence)"entrance", (boolean)false, (int)2, null)) {
            return true;
        }
        for (int i = 1; i < 8; ++i) {
            if (StringsKt.contains$default((CharSequence)lower, (CharSequence)("(f" + i + ")"), (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)lower, (CharSequence)("floor " + i), (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)lower, (CharSequence)("f" + i), (boolean)false, (int)2, null)) {
                return true;
            }
            if (!StringsKt.contains$default((CharSequence)lower, (CharSequence)("(m" + i + ")"), (boolean)false, (int)2, null) && !StringsKt.contains$default((CharSequence)lower, (CharSequence)("master " + i), (boolean)false, (int)2, null) && !StringsKt.contains$default((CharSequence)lower, (CharSequence)("m" + i), (boolean)false, (int)2, null)) continue;
            return true;
        }
        return false;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        trackedMobs = new LinkedHashMap();
        Object[] objectArray = new String[]{"2739d7f4e66a7db2ea6cd414e4c4ba41df7a92455c9fc42caab014665c367ad5", "bf6e1e7ed36586c2d98057002bc1adc981e2889f7bd7b5b3852bc55cc7802204", "e5c1dc47a04ce57001a8b726f018cdef40b7ea9d7bd6d835ca495a0ef169f893", "5662b6fb4b8b586dc4cdf803b0444d9b41d245cdf668dab38fa6c064afe8e461", "4cec40008e1c31c1984f4d650abb3410f2037119fd624afc953563b73515a077", "9fd61e8055f6ee97ab5b6196a8d7ec98078ac37e00376157b6b520eaaa2f93af", "b37dd18b5983a767e556dc64424af4b9abdb75d4c9e8b097818afbc431bf0e09", "f5f0d78fe38d1d7f75f08cdcf2a1855d6da0337e114a3c63e3bf3c618bc732b0", "51967db5e3199916252021903cf4e9952ef7cec220faaca1ba79bafe5938bd80"};
        watcherTextureIds = SetsKt.setOf((Object[])objectArray);
        objectArray = new String[]{"fb156cee370706408bb067261f59386f281eaf0bc24d168d9d01b13012946d04", "ac91f9afd84f2365cee8a53b61b9442b28e4f0e25bc6b6b1badbcdafc3e30c49", "7de7bbbdf22bfe17980d4e20687e386f11d59ee1db6f8b4762391b79a5ac532d", "3260325171a7ba8460830c0eea515c757a665e5b16a14207ba1a3182752bee87", "8421ba5b8e3573ef97beb5b40e15d15b20f30631c4c5330c3deda3047df0e92", "ad22772f769045fdc5be819ad68b01a97ac04c60886d2ca7afee39b282f7a383", "fb3973a752b24a2f3abb003427f6dbe6ca3a61db0a1bcf351c6eab27ec27e50", "ad67f97d7f821729beb34a82c3f13592b40439fe5248e72576fde7aa180bf77", "62d8fd3aa5617b1dac0aae9c81f6dd70ad93a59942f460d27e4d55a5cb8918e8", "c1007c5b7114abec734206d4fc613da4f3a0e99f71ff949cedadc99079135a0b", "69198f410a10f99314aa0fbe9a3db10697bbc1c011f019507d96673c64217f5a", "49f7cec00afe9f7c624ae8df5c033cb419f6ea41017021b9befd91970b833a5c", "3b48ec9c3e23a09e8aa2e1efbff9afb25e7315f9390984d01671dd0ae3c469ab", "12716ecbf5b8da00b05f316ec6af61e8bd02805b21eb8e440151468dc656549c", "ff184c19e725623d32828a0a4e741e86f135ac63dbc828ff3c8468338f3683b", "a89f6303af85877610912dc04b8b1e89724752f0a7eea05ab6547e228179c06f", "aa23c8cde2943c84249de8351bc3540be5f8afaaba8b2cb032fc5acad78a269b", "9171f35b8f508142bd8c65417d0f324153ab9147739ee4d10dea733cc80eaa20", "b5ba76e02cab72fa7d8ac54ceec849976ab0b00a01068d68c266766bf70c3997", "7d12b2ade413a6cd7cca3c95e961ba9f0ae7165fa41fc7b5d5f094a01240c609", "67237eddaebdbbdaacfa912885560ccdc65da93b4c3d513532868ec23bb5b448", "f4624a9a8c69ca204504abb043d47456cd9b09749a36357462303f276a229d4", "5cccd53f5191c29a9dc8f0170fbdc4e59e66476aae33de27b468f1de1b7cf3b2", "5a79860aca799407c0faa10b1bbcf42998fad4ebcf31d7a214180826b4ac94e1", "c919e5b8d56f062a21d224de14af771e2f55d09b59e7b099d09daa57540b79cf", "4774871190c878c9a2c4496c1e10257c6c4ea13807d72c15d7ac6ab3a7a9a8dc", "56fc854bb84cf4b7697297973e02b79bc10698460b51a639c60e5e417734e11"};
        bloodMobTextureIds = SetsKt.setOf((Object[])objectArray);
        objectArray = new Setting[]{enabled, currentMarker, traceLine, firstWaveOnly, nextDoorEsp, currentColor, predictedColor, nextDoorColor, fillOpacity, lineWidth};
        INSTANCE.addSetting((Setting<?>[])objectArray);
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0007\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0004\b\u0007\u0010\bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\t\u001a\u0004\b\n\u0010\u000bR\u0017\u0010\f\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\f\u0010\r\u001a\u0004\b\u000e\u0010\bR\"\u0010\u000f\u001a\u00020\u00068\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u000f\u0010\r\u001a\u0004\b\u0010\u0010\b\"\u0004\b\u0011\u0010\u0012R\"\u0010\u0014\u001a\u00020\u00138\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0014\u0010\u0015\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R$\u0010\u001a\u001a\u0004\u0018\u00010\u00068\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u001a\u0010\r\u001a\u0004\b\u001b\u0010\b\"\u0004\b\u001c\u0010\u0012R\u001a\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00060\u001d8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u001f\u00a8\u0006 "}, d2={"Lorg/cobalt/internal/dungeons/BloodCampHelperModule$TrackedBloodMob;", "", "Lnet/minecraft/class_1531;", "entity", "<init>", "(Lnet/minecraft/class_1531;)V", "Lnet/minecraft/class_243;", "updateAndGetDirection", "()Lnet/minecraft/class_243;", "Lnet/minecraft/class_1531;", "getEntity", "()Lnet/minecraft/class_1531;", "startPos", "Lnet/minecraft/class_243;", "getStartPos", "lastPos", "getLastPos", "setLastPos", "(Lnet/minecraft/class_243;)V", "", "firstWave", "Z", "getFirstWave", "()Z", "setFirstWave", "(Z)V", "predictedPos", "getPredictedPos", "setPredictedPos", "Ljava/util/ArrayDeque;", "deltas", "Ljava/util/ArrayDeque;", "cobalt"})
    private static final class TrackedBloodMob {
        @NotNull
        private final class_1531 entity;
        @NotNull
        private final class_243 startPos;
        @NotNull
        private class_243 lastPos;
        private boolean firstWave;
        @Nullable
        private class_243 predictedPos;
        @NotNull
        private final ArrayDeque<class_243> deltas;

        public TrackedBloodMob(@NotNull class_1531 entity) {
            Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
            this.entity = entity;
            class_243 class_2432 = this.entity.method_73189();
            Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"position(...)");
            this.lastPos = this.startPos = class_2432;
            this.deltas = new ArrayDeque();
        }

        @NotNull
        public final class_1531 getEntity() {
            return this.entity;
        }

        @NotNull
        public final class_243 getStartPos() {
            return this.startPos;
        }

        @NotNull
        public final class_243 getLastPos() {
            return this.lastPos;
        }

        public final void setLastPos(@NotNull class_243 class_2432) {
            Intrinsics.checkNotNullParameter((Object)class_2432, (String)"<set-?>");
            this.lastPos = class_2432;
        }

        public final boolean getFirstWave() {
            return this.firstWave;
        }

        public final void setFirstWave(boolean bl) {
            this.firstWave = bl;
        }

        @Nullable
        public final class_243 getPredictedPos() {
            return this.predictedPos;
        }

        public final void setPredictedPos(@Nullable class_243 class_2432) {
            this.predictedPos = class_2432;
        }

        @Nullable
        public final class_243 updateAndGetDirection() {
            if (this.predictedPos != null) {
                return null;
            }
            class_243 class_2432 = this.entity.method_73189();
            Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"position(...)");
            class_243 currentPos = class_2432;
            class_243 class_2433 = currentPos.method_1020(this.lastPos);
            Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"subtract(...)");
            class_243 delta = class_2433;
            this.lastPos = currentPos;
            if (delta.method_1027() <= 1.0E-4) {
                return null;
            }
            if (this.deltas.size() == 5) {
                this.deltas.removeFirst();
            }
            this.deltas.addLast(delta);
            if (this.deltas.size() < 5) {
                return null;
            }
            class_243 class_2434 = class_243.field_1353;
            Intrinsics.checkNotNullExpressionValue((Object)class_2434, (String)"ZERO");
            class_243 totalDelta = class_2434;
            Iterator<class_243> iterator = this.deltas.iterator();
            Intrinsics.checkNotNullExpressionValue(iterator, (String)"iterator(...)");
            Iterator<class_243> iterator2 = iterator;
            while (iterator2.hasNext()) {
                class_243 sample = iterator2.next();
                Intrinsics.checkNotNullExpressionValue((Object)totalDelta.method_1019(sample), (String)"add(...)");
            }
            return totalDelta.method_1027() > 1.0E-4 ? totalDelta.method_1029() : null;
        }
    }
}

