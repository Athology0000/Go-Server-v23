/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.Grouping
 *  kotlin.collections.GroupingKt
 *  kotlin.collections.MapsKt
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.IntRange
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.garden;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.Grouping;
import kotlin.collections.GroupingKt;
import kotlin.collections.MapsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.SettingKt;
import org.cobalt.api.module.setting.impl.ActionSetting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.InfoSetting;
import org.cobalt.api.module.setting.impl.InfoType;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules;
import org.cobalt.api.util.ChatUtils;
import org.cobalt.internal.pathfinding.OverlayRenderEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00d4\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b'\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u000e\u008b\u0001\u008c\u0001\u008d\u0001\u008e\u0001\u008f\u0001\u0090\u0001\u0091\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\n\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\tH\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ'\u0010\u0012\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u0010H\u0002\u00a2\u0006\u0004\b\u0012\u0010\u0013J!\u0010\u0015\u001a\u0004\u0018\u00010\u00142\u0006\u0010\r\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0015\u0010\u0016J/\u0010\u001d\u001a\u001a\u0012\u0004\u0012\u00020\u001a\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u001b\u0012\u0004\u0012\u00020\u001c0\u00190\u00192\u0006\u0010\u0018\u001a\u00020\u0017H\u0002\u00a2\u0006\u0004\b\u001d\u0010\u001eJ/\u0010\"\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020!0 0 2\u0012\u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00020\u001b\u0012\u0004\u0012\u00020\u001c0\u0019H\u0002\u00a2\u0006\u0004\b\"\u0010#J7\u0010%\u001a\u0004\u0018\u00010\u00142\u0006\u0010\r\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010$\u001a\u00020\u001a2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020!0 H\u0002\u00a2\u0006\u0004\b%\u0010&J\u001d\u0010(\u001a\u00020'2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020!0 H\u0002\u00a2\u0006\u0004\b(\u0010)J\u001d\u0010*\u001a\u00020\u001c2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020!0 H\u0002\u00a2\u0006\u0004\b*\u0010+J;\u0010.\u001a\b\u0012\u0004\u0012\u00020-0 2\u0006\u0010\r\u001a\u00020\f2\u0006\u0010*\u001a\u00020\u001c2\u0006\u0010,\u001a\u00020'2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020!0 H\u0002\u00a2\u0006\u0004\b.\u0010/J?\u00105\u001a\u0002042\u0006\u0010\r\u001a\u00020\f2\u0006\u0010*\u001a\u00020\u001c2\u0006\u0010,\u001a\u00020'2\u0006\u00101\u001a\u0002002\u0006\u00102\u001a\u00020\u001c2\u0006\u00103\u001a\u00020\u001cH\u0002\u00a2\u0006\u0004\b5\u00106J\u001f\u00108\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\f2\u0006\u00107\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\b8\u00109J?\u0010A\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\f2\u0006\u0010:\u001a\u0002042\u0006\u0010<\u001a\u00020;2\u0006\u0010=\u001a\u00020;2\u0006\u0010?\u001a\u00020>2\u0006\u0010@\u001a\u00020>H\u0002\u00a2\u0006\u0004\bA\u0010BJ\u0019\u0010E\u001a\u0004\u0018\u00010\u001a2\u0006\u0010D\u001a\u00020CH\u0002\u00a2\u0006\u0004\bE\u0010FJ?\u0010M\u001a\u00020>2\u0006\u0010G\u001a\u00020>2\u0006\u0010H\u001a\u00020>2\u0006\u0010I\u001a\u00020\u001c2\u0006\u0010J\u001a\u00020\u001c2\u0006\u0010K\u001a\u00020\u001c2\u0006\u0010L\u001a\u00020\u001cH\u0002\u00a2\u0006\u0004\bM\u0010NJ\u0017\u0010P\u001a\u00020O2\u0006\u00107\u001a\u00020\u0014H\u0002\u00a2\u0006\u0004\bP\u0010QJ\u0017\u0010R\u001a\u00020O2\u0006\u0010:\u001a\u000204H\u0002\u00a2\u0006\u0004\bR\u0010SR\u0014\u0010T\u001a\u00020O8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bT\u0010UR\u0014\u0010V\u001a\u00020\u001c8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bV\u0010WR\u0014\u0010X\u001a\u00020\u001c8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bX\u0010WR\u0014\u0010Y\u001a\u00020\u001c8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bY\u0010WR\u0014\u0010[\u001a\u00020Z8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b[\u0010\\R\u0014\u0010^\u001a\u00020]8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b^\u0010_R\u0014\u0010a\u001a\u00020`8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\ba\u0010bR\u0014\u0010d\u001a\u00020c8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bd\u0010eR\u0014\u0010g\u001a\u00020f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bg\u0010hR\u0014\u0010i\u001a\u00020f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bi\u0010hR\u0014\u0010j\u001a\u00020`8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bj\u0010bR\u0014\u0010l\u001a\u00020k8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bl\u0010mR\u0014\u0010n\u001a\u00020k8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bn\u0010mR\u0014\u0010o\u001a\u00020k8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bo\u0010mR\u0014\u0010p\u001a\u00020k8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bp\u0010mR\u0014\u0010q\u001a\u00020k8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bq\u0010mR\u0014\u0010r\u001a\u00020`8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\br\u0010bR\u0014\u0010s\u001a\u00020`8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bs\u0010bR\u0014\u0010t\u001a\u00020`8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bt\u0010bR\u0014\u0010u\u001a\u00020`8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bu\u0010bR\u0014\u0010v\u001a\u00020`8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bv\u0010bR\u0016\u0010w\u001a\u00020\u00108\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bw\u0010xR\u0016\u0010y\u001a\u00020\u00108\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\by\u0010xR\u0016\u0010z\u001a\u00020\u00108\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bz\u0010xR\u0016\u0010{\u001a\u00020Z8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b{\u0010\\R\u0016\u0010|\u001a\u00020Z8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b|\u0010\\R\u0016\u0010}\u001a\u00020O8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b}\u0010UR\u0018\u0010~\u001a\u0004\u0018\u00010\u00148\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b~\u0010\u007fR\u0017\u0010\u0080\u0001\u001a\u00020;8\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0080\u0001\u0010\u0081\u0001R\u0017\u0010\u0082\u0001\u001a\u00020;8\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0082\u0001\u0010\u0081\u0001R\u0017\u0010\u0083\u0001\u001a\u00020;8\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0083\u0001\u0010\u0081\u0001R\u0017\u0010\u0084\u0001\u001a\u00020;8\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0084\u0001\u0010\u0081\u0001R\u0017\u0010\u0085\u0001\u001a\u00020;8\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0085\u0001\u0010\u0081\u0001R\u0017\u0010\u0086\u0001\u001a\u00020;8\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0086\u0001\u0010\u0081\u0001R\u0017\u0010\u0087\u0001\u001a\u00020;8\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0087\u0001\u0010\u0081\u0001R\u0017\u0010\u0088\u0001\u001a\u00020;8\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0088\u0001\u0010\u0081\u0001R\u0017\u0010\u0089\u0001\u001a\u00020;8\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0089\u0001\u0010\u0081\u0001R\u0017\u0010\u008a\u0001\u001a\u00020;8\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u008a\u0001\u0010\u0081\u0001\u00a8\u0006\u0092\u0001"}, d2={"Lorg/cobalt/internal/garden/GardenAnalyzerModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/TickEvent$End;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$End;)V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "Lnet/minecraft/class_1937;", "level", "Lnet/minecraft/class_746;", "player", "", "notify", "performAnalysis", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_746;Z)V", "Lorg/cobalt/internal/garden/GardenAnalyzerModule$FarmAnalysis;", "analyzeNearestFarm", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_746;)Lorg/cobalt/internal/garden/GardenAnalyzerModule$FarmAnalysis;", "Lnet/minecraft/class_2338;", "center", "", "Lorg/cobalt/internal/garden/GardenAnalyzerModule$CropKind;", "Lorg/cobalt/internal/garden/GardenAnalyzerModule$GridCell;", "", "collectCropFootprints", "(Lnet/minecraft/class_2338;)Ljava/util/Map;", "cells", "", "Lorg/cobalt/internal/garden/GardenAnalyzerModule$FootprintCell;", "splitComponents", "(Ljava/util/Map;)Ljava/util/List;", "crop", "buildAnalysis", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_746;Lorg/cobalt/internal/garden/GardenAnalyzerModule$CropKind;Ljava/util/List;)Lorg/cobalt/internal/garden/GardenAnalyzerModule$FarmAnalysis;", "Lorg/cobalt/internal/garden/GardenAnalyzerModule$LaneAxis;", "resolveLaneAxis", "(Ljava/util/List;)Lorg/cobalt/internal/garden/GardenAnalyzerModule$LaneAxis;", "dominantY", "(Ljava/util/List;)I", "laneAxis", "Lorg/cobalt/internal/garden/GardenAnalyzerModule$RowSegment;", "buildRows", "(Lnet/minecraft/class_1937;ILorg/cobalt/internal/garden/GardenAnalyzerModule$LaneAxis;Ljava/util/List;)Ljava/util/List;", "Lkotlin/ranges/IntRange;", "band", "laneEdge", "direction", "Lorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;", "findTurnMarker", "(Lnet/minecraft/class_1937;ILorg/cobalt/internal/garden/GardenAnalyzerModule$LaneAxis;Lkotlin/ranges/IntRange;II)Lorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;", "analysis", "renderAnalysis", "(Lnet/minecraft/class_1937;Lorg/cobalt/internal/garden/GardenAnalyzerModule$FarmAnalysis;)V", "marker", "Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "fill", "outline", "", "beamHeight", "size", "renderMarker", "(Lnet/minecraft/class_1937;Lorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;DD)V", "Lnet/minecraft/class_2680;", "state", "cropKindOf", "(Lnet/minecraft/class_2680;)Lorg/cobalt/internal/garden/GardenAnalyzerModule$CropKind;", "x", "z", "minX", "maxX", "minZ", "maxZ", "horizontalDistanceSqToBounds", "(DDIIII)D", "", "buildSummary", "(Lorg/cobalt/internal/garden/GardenAnalyzerModule$FarmAnalysis;)Ljava/lang/String;", "formatMarker", "(Lorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;)Ljava/lang/String;", "TAG", "Ljava/lang/String;", "COMPONENT_LINK_RADIUS", "I", "ROW_BAND_GAP", "TURN_SCAN_DEPTH", "", "MANUAL_OVERLAY_TICKS", "J", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabledSetting", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "heuristicInfo", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "analyzeNowSetting", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "clearSetting", "autoRefreshSetting", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "refreshTicksSetting", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "scanRadiusSetting", "verticalRangeSetting", "minFarmCellsSetting", "minRowLengthSetting", "showBoundsSetting", "showRowGuidesSetting", "showTurnsSetting", "showStartFinishSetting", "forceRenderSetting", "wasEnabled", "Z", "analyzeRequested", "manualChatRequested", "nextRefreshTick", "manualOverlayUntilTick", "lastSummarySignature", "lastAnalysis", "Lorg/cobalt/internal/garden/GardenAnalyzerModule$FarmAnalysis;", "boundsFill", "Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "macroBoundsOutline", "nonMacroBoundsOutline", "rowGuideColor", "turnFill", "turnOutline", "startFill", "startOutline", "finishFill", "finishOutline", "LaneAxis", "CropKind", "GridCell", "FootprintCell", "MarkerPoint", "RowSegment", "FarmAnalysis", "cobalt"})
@SourceDebugExtension(value={"SMAP\nGardenAnalyzerModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GardenAnalyzerModule.kt\norg/cobalt/internal/garden/GardenAnalyzerModule\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,839:1\n221#2,2:840\n383#3,7:842\n383#3,7:863\n383#3,7:881\n383#3,7:900\n383#3,7:914\n1#4:849\n1068#5:850\n1586#5:851\n1661#5,3:852\n1834#5,4:855\n1512#5:859\n1538#5,3:860\n1541#5,3:870\n1586#5:873\n1661#5,3:874\n1512#5:877\n1538#5,3:878\n1541#5,3:888\n1586#5:891\n1661#5,3:892\n1573#5:895\n1512#5:896\n1538#5,3:897\n1541#5,3:907\n1512#5:910\n1538#5,3:911\n1541#5,3:921\n777#5:924\n873#5,2:925\n1068#5:927\n1915#5,2:928\n1391#5:930\n1480#5,5:931\n1915#5,2:936\n*S KotlinDebug\n*F\n+ 1 GardenAnalyzerModule.kt\norg/cobalt/internal/garden/GardenAnalyzerModule\n*L\n364#1:840,2\n407#1:842,7\n518#1:863,7\n524#1:881,7\n547#1:900,7\n549#1:914,7\n461#1:850\n466#1:851\n466#1:852,3\n469#1:855,4\n518#1:859\n518#1:860,3\n518#1:870,3\n520#1:873\n520#1:874,3\n524#1:877\n524#1:878,3\n524#1:888,3\n526#1:891\n526#1:892,3\n534#1:895\n547#1:896\n547#1:897,3\n547#1:907,3\n549#1:910\n549#1:911,3\n549#1:921,3\n573#1:924\n573#1:925,2\n616#1:927\n678#1:928,2\n716#1:930\n716#1:931,5\n718#1:936,2\n*E\n"})
public final class GardenAnalyzerModule
extends Module {
    @NotNull
    public static final GardenAnalyzerModule INSTANCE = new GardenAnalyzerModule();
    @NotNull
    private static final String TAG = "garden-analyzer";
    private static final int COMPONENT_LINK_RADIUS = 2;
    private static final int ROW_BAND_GAP = 2;
    private static final int TURN_SCAN_DEPTH = 4;
    private static final long MANUAL_OVERLAY_TICKS = 80L;
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final CheckboxSetting enabledSetting;
    @NotNull
    private static final InfoSetting heuristicInfo;
    @NotNull
    private static final ActionSetting analyzeNowSetting;
    @NotNull
    private static final ActionSetting clearSetting;
    @NotNull
    private static final CheckboxSetting autoRefreshSetting;
    @NotNull
    private static final SliderSetting refreshTicksSetting;
    @NotNull
    private static final SliderSetting scanRadiusSetting;
    @NotNull
    private static final SliderSetting verticalRangeSetting;
    @NotNull
    private static final SliderSetting minFarmCellsSetting;
    @NotNull
    private static final SliderSetting minRowLengthSetting;
    @NotNull
    private static final CheckboxSetting showBoundsSetting;
    @NotNull
    private static final CheckboxSetting showRowGuidesSetting;
    @NotNull
    private static final CheckboxSetting showTurnsSetting;
    @NotNull
    private static final CheckboxSetting showStartFinishSetting;
    @NotNull
    private static final CheckboxSetting forceRenderSetting;
    private static boolean wasEnabled;
    private static boolean analyzeRequested;
    private static boolean manualChatRequested;
    private static long nextRefreshTick;
    private static long manualOverlayUntilTick;
    @NotNull
    private static String lastSummarySignature;
    @Nullable
    private static FarmAnalysis lastAnalysis;
    @NotNull
    private static final OverlayRenderEngine.Color boundsFill;
    @NotNull
    private static final OverlayRenderEngine.Color macroBoundsOutline;
    @NotNull
    private static final OverlayRenderEngine.Color nonMacroBoundsOutline;
    @NotNull
    private static final OverlayRenderEngine.Color rowGuideColor;
    @NotNull
    private static final OverlayRenderEngine.Color turnFill;
    @NotNull
    private static final OverlayRenderEngine.Color turnOutline;
    @NotNull
    private static final OverlayRenderEngine.Color startFill;
    @NotNull
    private static final OverlayRenderEngine.Color startOutline;
    @NotNull
    private static final OverlayRenderEngine.Color finishFill;
    @NotNull
    private static final OverlayRenderEngine.Color finishOutline;

    private GardenAnalyzerModule() {
        super("Garden Analyzer");
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.End event) {
        boolean shouldAutoRefresh;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        boolean enabled = (Boolean)enabledSetting.getValue();
        if (enabled && !wasEnabled) {
            analyzeRequested = true;
        }
        if (!enabled && wasEnabled) {
            OverlayRenderEngine.INSTANCE.clearTag(TAG);
        }
        wasEnabled = enabled;
        class_638 class_6382 = GardenAnalyzerModule.mc.field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        class_746 class_7462 = GardenAnalyzerModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        long gameTime = level2.method_75260();
        boolean bl = shouldAutoRefresh = enabled && (Boolean)autoRefreshSetting.getValue() != false && gameTime >= nextRefreshTick;
        if (!analyzeRequested && !shouldAutoRefresh) {
            return;
        }
        boolean manual = analyzeRequested;
        analyzeRequested = false;
        nextRefreshTick = gameTime + RangesKt.coerceAtLeast((long)((long)((Number)refreshTicksSetting.getValue()).doubleValue()), (long)5L);
        if (manual && !enabled) {
            manualOverlayUntilTick = gameTime + 80L;
        }
        this.performAnalysis((class_1937)level2, player, manual || manualChatRequested);
        manualChatRequested = false;
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_638 class_6382 = GardenAnalyzerModule.mc.field_1687;
        if (class_6382 == null) {
            GardenAnalyzerModule $this$onRender_u24lambda_u240 = this;
            boolean bl = false;
            OverlayRenderEngine.INSTANCE.clearTag(TAG);
            return;
        }
        class_638 level2 = class_6382;
        if (!((Boolean)enabledSetting.getValue()).booleanValue() && level2.method_75260() > manualOverlayUntilTick) {
            OverlayRenderEngine.INSTANCE.clearTag(TAG);
            return;
        }
        FarmAnalysis farmAnalysis = lastAnalysis;
        if (farmAnalysis == null) {
            GardenAnalyzerModule $this$onRender_u24lambda_u241 = this;
            boolean bl = false;
            OverlayRenderEngine.INSTANCE.clearTag(TAG);
            return;
        }
        FarmAnalysis analysis = farmAnalysis;
        OverlayRenderEngine.INSTANCE.clearTag(TAG);
        this.renderAnalysis((class_1937)level2, analysis);
        OverlayRenderEngine.INSTANCE.render(event.getContext());
    }

    private final void performAnalysis(class_1937 level2, class_746 player, boolean notify) {
        FarmAnalysis analysis;
        lastAnalysis = analysis = this.analyzeNearestFarm(level2, player);
        if (analysis == null) {
            if (notify || ((CharSequence)lastSummarySignature).length() > 0) {
                ChatUtils.sendMessage("Garden Analyzer: no farm-like crop cluster found within " + (int)((Number)scanRadiusSetting.getValue()).doubleValue() + " blocks.");
            }
            lastSummarySignature = "";
            return;
        }
        String signature = analysis.signature();
        if (notify || !Intrinsics.areEqual((Object)signature, (Object)lastSummarySignature)) {
            ChatUtils.sendMessage(this.buildSummary(analysis));
            lastSummarySignature = signature;
        }
    }

    private final FarmAnalysis analyzeNearestFarm(class_1937 level2, class_746 player) {
        class_2338 class_23382 = player.method_24515();
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"blockPosition(...)");
        Map<CropKind, Map<GridCell, Integer>> cropMaps = this.collectCropFootprints(class_23382);
        if (cropMaps.isEmpty()) {
            return null;
        }
        int minCells = RangesKt.coerceAtLeast((int)((int)((Number)minFarmCellsSetting.getValue()).doubleValue()), (int)1);
        FarmAnalysis bestAnalysis = null;
        Map<CropKind, Map<GridCell, Integer>> $this$forEach$iv = cropMaps;
        boolean $i$f$forEach = false;
        Iterator<Map.Entry<CropKind, Map<GridCell, Integer>>> iterator = $this$forEach$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<CropKind, Map<GridCell, Integer>> element$iv;
            Map.Entry<CropKind, Map<GridCell, Integer>> entry = element$iv = iterator.next();
            boolean bl = false;
            CropKind crop = entry.getKey();
            Map<GridCell, Integer> footprints = entry.getValue();
            List<List<FootprintCell>> components = INSTANCE.splitComponents(footprints);
            for (List<FootprintCell> component : components) {
                FarmAnalysis analysis;
                FarmAnalysis currentBest;
                if (component.size() < minCells || INSTANCE.buildAnalysis(level2, player, crop, component) == null || (currentBest = bestAnalysis) != null && !(analysis.getDistanceSq() < currentBest.getDistanceSq())) continue;
                bestAnalysis = analysis;
            }
        }
        return bestAnalysis;
    }

    /*
     * WARNING - void declaration
     */
    private final Map<CropKind, Map<GridCell, Integer>> collectCropFootprints(class_2338 center) {
        int n;
        class_638 class_6382 = GardenAnalyzerModule.mc.field_1687;
        if (class_6382 == null) {
            return MapsKt.emptyMap();
        }
        class_638 level2 = class_6382;
        int radius = RangesKt.coerceAtLeast((int)((int)((Number)scanRadiusSetting.getValue()).doubleValue()), (int)1);
        int verticalRange = RangesKt.coerceAtLeast((int)((int)((Number)verticalRangeSetting.getValue()).doubleValue()), (int)1);
        int yMin = center.method_10264() - verticalRange;
        int yMax = center.method_10264() + verticalRange;
        Map cellsByCrop = new LinkedHashMap();
        class_2338.class_2339 mutablePos = new class_2338.class_2339();
        int x = center.method_10263() - radius;
        if (x <= (n = center.method_10263() + radius)) {
            while (true) {
                int n2;
                int z;
                if ((z = center.method_10260() - radius) <= (n2 = center.method_10260() + radius)) {
                    while (true) {
                        CropKind bestCrop = null;
                        int bestY = 0;
                        int bestDistance = Integer.MAX_VALUE;
                        int y2 = yMin;
                        if (y2 <= yMax) {
                            while (true) {
                                int distance;
                                mutablePos.method_10103(x, y2, z);
                                class_2680 class_26802 = level2.method_8320((class_2338)mutablePos);
                                Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
                                if (this.cropKindOf(class_26802) != null && (distance = Math.abs(y2 - center.method_10264())) < bestDistance) {
                                    CropKind crop;
                                    bestCrop = crop;
                                    bestY = y2;
                                    bestDistance = distance;
                                }
                                if (y2 == yMax) break;
                                ++y2;
                            }
                        }
                        if (bestCrop != null) {
                            Object object;
                            void $this$getOrPut$iv;
                            Map y2 = cellsByCrop;
                            CropKind key$iv = bestCrop;
                            boolean $i$f$getOrPut = false;
                            Object value$iv = $this$getOrPut$iv.get((Object)key$iv);
                            if (value$iv == null) {
                                boolean bl = false;
                                Map answer$iv = new HashMap();
                                $this$getOrPut$iv.put(key$iv, answer$iv);
                                object = answer$iv;
                            } else {
                                object = value$iv;
                            }
                            Map map = (Map)object;
                            GridCell gridCell = new GridCell(x, z);
                            Integer n3 = bestY;
                            map.put(gridCell, n3);
                        }
                        if (z == n2) break;
                        ++z;
                    }
                }
                if (x == n) break;
                ++x;
            }
        }
        return cellsByCrop;
    }

    private final List<List<FootprintCell>> splitComponents(Map<GridCell, Integer> cells) {
        Set remaining = CollectionsKt.toMutableSet((Iterable)cells.keySet());
        List components = new ArrayList();
        while (!((Collection)remaining).isEmpty()) {
            GridCell start = (GridCell)CollectionsKt.first((Iterable)remaining);
            remaining.remove(start);
            ArrayDeque<GridCell> queue = new ArrayDeque<GridCell>();
            queue.add(start);
            List component = new ArrayList();
            while (!((Collection)queue).isEmpty()) {
                GridCell current = (GridCell)queue.removeFirst();
                Integer n = cells.get(current);
                if (n == null) {
                    continue;
                }
                int y = n;
                ((Collection)component).add(new FootprintCell(current.getX(), current.getZ(), y));
                for (int dx = -2; dx < 3; ++dx) {
                    for (int dz = -2; dz < 3; ++dz) {
                        GridCell neighbor;
                        if (dx == 0 && dz == 0 || !remaining.remove(neighbor = new GridCell(current.getX() + dx, current.getZ() + dz))) continue;
                        queue.add(neighbor);
                    }
                }
            }
            ((Collection)components).add(component);
        }
        return components;
    }

    /*
     * WARNING - void declaration
     */
    private final FarmAnalysis buildAnalysis(class_1937 level2, class_746 player, CropKind crop, List<FootprintCell> cells) {
        MarkerPoint finishMarker;
        int n;
        RowSegment rowSegment;
        Object item$iv$iv2;
        void $this$mapTo$iv$iv;
        void $this$sortedBy$iv;
        LaneAxis laneAxis = this.resolveLaneAxis(cells);
        int dominantY = this.dominantY(cells);
        Iterator iterator = ((Iterable)cells).iterator();
        if (!iterator.hasNext()) {
            throw new NoSuchElementException();
        }
        FootprintCell it422 = (FootprintCell)iterator.next();
        boolean bl = false;
        int it422 = it422.getX();
        while (iterator.hasNext()) {
            FootprintCell it32 = (FootprintCell)iterator.next();
            $i$a$-minOf-GardenAnalyzerModule$buildAnalysis$minX$1 = false;
            int it32 = it32.getX();
            if (it422 <= it32) continue;
            it422 = it32;
        }
        int minX = it422;
        Iterator it422 = ((Iterable)cells).iterator();
        if (!it422.hasNext()) {
            throw new NoSuchElementException();
        }
        FootprintCell it622 = (FootprintCell)it422.next();
        boolean bl2 = false;
        int it622 = it622.getX();
        while (it422.hasNext()) {
            FootprintCell it52 = (FootprintCell)it422.next();
            $i$a$-maxOf-GardenAnalyzerModule$buildAnalysis$maxX$1 = false;
            int it52 = it52.getX();
            if (it622 >= it52) continue;
            it622 = it52;
        }
        int maxX = it622;
        Iterator it622 = ((Iterable)cells).iterator();
        if (!it622.hasNext()) {
            throw new NoSuchElementException();
        }
        FootprintCell it822 = (FootprintCell)it622.next();
        boolean bl3 = false;
        int it822 = it822.getZ();
        while (it622.hasNext()) {
            FootprintCell it72 = (FootprintCell)it622.next();
            $i$a$-minOf-GardenAnalyzerModule$buildAnalysis$minZ$1 = false;
            int it72 = it72.getZ();
            if (it822 <= it72) continue;
            it822 = it72;
        }
        int minZ = it822;
        Iterator it822 = ((Iterable)cells).iterator();
        if (!it822.hasNext()) {
            throw new NoSuchElementException();
        }
        FootprintCell it22 = (FootprintCell)it822.next();
        boolean bl4 = false;
        int it22 = it22.getZ();
        while (it822.hasNext()) {
            FootprintCell it92 = (FootprintCell)it822.next();
            $i$a$-maxOf-GardenAnalyzerModule$buildAnalysis$maxZ$2 = false;
            int it92 = it92.getZ();
            if (it22 >= it92) continue;
            it22 = it92;
        }
        int maxZ = it22;
        it822 = this.buildRows(level2, dominantY, laneAxis, cells);
        boolean $i$f$sortedBy = false;
        List rows = CollectionsKt.sortedWith((Iterable)$this$sortedBy$iv, (Comparator)new Comparator(){

            public final int compare(T a, T b) {
                RowSegment it = (RowSegment)a;
                boolean bl = false;
                Comparable comparable = Double.valueOf(it.getBandCenter());
                it = (RowSegment)b;
                Comparable comparable2 = comparable;
                bl = false;
                return ComparisonsKt.compareValues((Comparable)comparable2, (Comparable)Double.valueOf(it.getBandCenter()));
            }
        });
        if (rows.isEmpty()) {
            return null;
        }
        Iterable $this$map$iv = rows;
        boolean $i$f$map = false;
        Iterable $i$a$-maxOf-GardenAnalyzerModule$buildAnalysis$maxZ$2 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv2 : $this$mapTo$iv$iv) {
            void it;
            rowSegment = (RowSegment)item$iv$iv2;
            Collection collection = destination$iv$iv;
            boolean bl5 = false;
            collection.add(it.getLength());
        }
        List lengths = CollectionsKt.sorted((Iterable)((List)destination$iv$iv));
        int medianRowLength = ((Number)lengths.get(lengths.size() / 2)).intValue();
        int tolerance = Math.max(2, medianRowLength / 6);
        Iterable $this$count$iv = lengths;
        boolean $i$f$count22 = false;
        if ($this$count$iv instanceof Collection && ((Collection)$this$count$iv).isEmpty()) {
            n = 0;
        } else {
            int count$iv = 0;
            for (Object element$iv : $this$count$iv) {
                int it = ((Number)element$iv).intValue();
                boolean bl6 = false;
                if (!(Math.abs(it - medianRowLength) <= tolerance) || ++count$iv >= 0) continue;
                CollectionsKt.throwCountOverflow();
            }
            n = count$iv;
        }
        int consistentRows = n;
        Iterable $i$f$count22 = rows;
        int count$iv = 0;
        item$iv$iv2 = $i$f$count22.iterator();
        while (item$iv$iv2.hasNext()) {
            void row;
            RowSegment it = rowSegment = item$iv$iv2.next();
            int n2 = count$iv;
            boolean bl7 = false;
            int n3 = (row.getStartMarker().getWalkable() ? 1 : 0) + (row.getEndMarker().getWalkable() ? 1 : 0);
            count$iv = n2 + n3;
        }
        int walkableTurns = count$iv;
        List reasons = new ArrayList();
        if (!crop.getSupportsLaneHeuristic()) {
            ((Collection)reasons).add("crop geometry is not lane-friendly");
        }
        if (rows.size() < 3) {
            ((Collection)reasons).add("needs at least 3 rows");
        }
        if (medianRowLength < (int)((Number)minRowLengthSetting.getValue()).doubleValue()) {
            ((Collection)reasons).add("rows are too short");
        }
        if (consistentRows < Math.max(1, (int)((double)rows.size() * 0.75))) {
            ((Collection)reasons).add("row lengths are inconsistent");
        }
        if (walkableTurns < Math.max(2, rows.size())) {
            ((Collection)reasons).add("not enough walkable turn points");
        }
        RowSegment rowSegment2 = (RowSegment)CollectionsKt.firstOrNull((List)rows);
        MarkerPoint startMarker = rowSegment2 != null ? rowSegment2.getStartMarker() : null;
        RowSegment rowSegment3 = (RowSegment)CollectionsKt.lastOrNull((List)rows);
        MarkerPoint markerPoint = finishMarker = rowSegment3 != null ? rowSegment3.getEndMarker() : null;
        if (startMarker == null || finishMarker == null) {
            ((Collection)reasons).add("missing entry or finish marker");
        }
        return new FarmAnalysis(crop, laneAxis, dominantY, minX, maxX, minZ, maxZ, rows, cells.size(), medianRowLength, reasons.isEmpty(), reasons, startMarker, finishMarker, this.horizontalDistanceSqToBounds(player.method_23317(), player.method_23321(), minX, maxX, minZ, maxZ));
    }

    /*
     * WARNING - void declaration
     */
    private final LaneAxis resolveLaneAxis(List<FootprintCell> cells) {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        List list$iv$iv;
        void $this$groupByTo$iv$iv;
        Iterable $this$groupBy$iv;
        Object object;
        Iterable $this$mapTo$iv$iv2;
        void $this$map$iv2;
        List list$iv$iv2;
        Object value$iv$iv$iv;
        Iterator key$iv$iv;
        void $this$groupByTo$iv$iv2;
        Iterable $this$groupBy$iv2;
        Iterable iterable = cells;
        boolean $i$f$groupBy = false;
        void var6_5 = $this$groupBy$iv2;
        Object destination$iv$iv = new LinkedHashMap();
        boolean $i$f$groupByTo = false;
        for (Object element$iv$iv : $this$groupByTo$iv$iv2) {
            Object object2;
            void $this$getOrPut$iv$iv$iv;
            FootprintCell it = (FootprintCell)element$iv$iv;
            boolean bl = false;
            key$iv$iv = it.getZ();
            Map map = destination$iv$iv;
            Integer key$iv$iv$iv = key$iv$iv;
            boolean $i$f$getOrPut = false;
            value$iv$iv$iv = $this$getOrPut$iv$iv$iv.get(key$iv$iv$iv);
            if (value$iv$iv$iv == null) {
                boolean bl2 = false;
                List answer$iv$iv$iv = new ArrayList();
                $this$getOrPut$iv$iv$iv.put(key$iv$iv$iv, answer$iv$iv$iv);
                object2 = answer$iv$iv$iv;
            } else {
                object2 = value$iv$iv$iv;
            }
            list$iv$iv2 = (List)object2;
            list$iv$iv2.add(element$iv$iv);
        }
        $this$groupBy$iv2 = destination$iv$iv.values();
        boolean $i$f$map = false;
        $this$groupByTo$iv$iv2 = $this$map$iv2;
        destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv2, (int)10));
        boolean $i$f$mapTo22 = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv2) {
            int it32;
            void row;
            list$iv$iv2 = (List)item$iv$iv;
            object = destination$iv$iv;
            boolean bl = false;
            key$iv$iv = ((Iterable)row).iterator();
            if (!key$iv$iv.hasNext()) {
                throw new NoSuchElementException();
            }
            FootprintCell it22 = (FootprintCell)key$iv$iv.next();
            boolean bl3 = false;
            int it22 = it22.getX();
            while (key$iv$iv.hasNext()) {
                FootprintCell it32 = (FootprintCell)key$iv$iv.next();
                $i$a$-maxOf-GardenAnalyzerModule$resolveLaneAxis$spanAlongX$2$1 = false;
                it32 = it32.getX();
                if (it22 >= it32) continue;
                it22 = it32;
            }
            value$iv$iv$iv = (Iterable)row;
            int answer$iv$iv$iv = it22;
            key$iv$iv = value$iv$iv$iv.iterator();
            if (!key$iv$iv.hasNext()) {
                throw new NoSuchElementException();
            }
            FootprintCell it42 = (FootprintCell)key$iv$iv.next();
            boolean bl4 = false;
            int it42 = it42.getX();
            while (key$iv$iv.hasNext()) {
                FootprintCell it = (FootprintCell)key$iv$iv.next();
                map = false;
                it32 = it.getX();
                if (it42 <= it32) continue;
                it42 = it32;
            }
            int n = it42;
            object.add(answer$iv$iv$iv - n + 1);
        }
        double spanAlongX = CollectionsKt.averageOfInt((Iterable)((List)destination$iv$iv));
        $this$mapTo$iv$iv2 = cells;
        boolean $i$f$groupBy2 = false;
        void $i$f$mapTo22 = $this$groupBy$iv;
        Object destination$iv$iv2 = new LinkedHashMap();
        boolean $i$f$groupByTo2 = false;
        for (Object element$iv$iv : $this$groupByTo$iv$iv) {
            Object object3;
            void $this$getOrPut$iv$iv$iv;
            FootprintCell it = (FootprintCell)element$iv$iv;
            boolean bl = false;
            Integer key$iv$iv2 = it.getX();
            Map map = destination$iv$iv2;
            Integer key$iv$iv$iv = key$iv$iv2;
            boolean $i$f$getOrPut = false;
            Object value$iv$iv$iv2 = $this$getOrPut$iv$iv$iv.get(key$iv$iv$iv);
            if (value$iv$iv$iv2 == null) {
                boolean bl5 = false;
                List answer$iv$iv$iv = new ArrayList();
                $this$getOrPut$iv$iv$iv.put(key$iv$iv$iv, answer$iv$iv$iv);
                object3 = answer$iv$iv$iv;
            } else {
                object3 = value$iv$iv$iv2;
            }
            list$iv$iv = (List)object3;
            list$iv$iv.add(element$iv$iv);
        }
        $this$groupBy$iv = destination$iv$iv2.values();
        boolean $i$f$map2 = false;
        $this$groupByTo$iv$iv = $this$map$iv;
        destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void row;
            list$iv$iv = (List)item$iv$iv;
            object = destination$iv$iv2;
            boolean bl = false;
            Iterator iterator = ((Iterable)row).iterator();
            if (!iterator.hasNext()) {
                throw new NoSuchElementException();
            }
            FootprintCell it52 = (FootprintCell)iterator.next();
            boolean bl6 = false;
            int it52 = it52.getZ();
            while (iterator.hasNext()) {
                FootprintCell it62 = (FootprintCell)iterator.next();
                $i$a$-maxOf-GardenAnalyzerModule$resolveLaneAxis$spanAlongZ$2$1 = false;
                int it62 = it62.getZ();
                if (it52 >= it62) continue;
                it52 = it62;
            }
            Iterable iterable2 = (Iterable)row;
            int n = it52;
            iterator = iterable2.iterator();
            if (!iterator.hasNext()) {
                throw new NoSuchElementException();
            }
            FootprintCell it = (FootprintCell)iterator.next();
            boolean bl7 = false;
            int n2 = it.getZ();
            while (iterator.hasNext()) {
                FootprintCell it2 = (FootprintCell)iterator.next();
                $i$a$-minOf-GardenAnalyzerModule$resolveLaneAxis$spanAlongZ$2$2 = false;
                int n3 = it2.getZ();
                if (n2 <= n3) continue;
                n2 = n3;
            }
            int n4 = n2;
            object.add(n - n4 + 1);
        }
        double spanAlongZ = CollectionsKt.averageOfInt((Iterable)((List)destination$iv$iv2));
        return spanAlongX >= spanAlongZ ? LaneAxis.X : LaneAxis.Z;
    }

    private final int dominantY(List<FootprintCell> cells) {
        int n;
        Object v0;
        Iterable $this$groupingBy$iv = cells;
        boolean $i$f$groupingBy = false;
        Iterable iterable = GroupingKt.eachCount((Grouping)((Grouping)new Grouping<FootprintCell, Integer>($this$groupingBy$iv){
            final /* synthetic */ Iterable $this_groupingBy;
            {
                this.$this_groupingBy = $receiver;
            }

            public Iterator<FootprintCell> sourceIterator() {
                return this.$this_groupingBy.iterator();
            }

            /*
             * Ignored method signature, as it can't be verified against descriptor
             */
            public Object keyOf(Object element) {
                FootprintCell it = (FootprintCell)element;
                boolean bl = false;
                return it.getY();
            }
        })).entrySet();
        Iterator iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            v0 = null;
        } else {
            Object t = iterator.next();
            if (!iterator.hasNext()) {
                v0 = t;
            } else {
                Map.Entry it = (Map.Entry)t;
                boolean bl = false;
                int n2 = ((Number)it.getValue()).intValue();
                do {
                    Object t2 = iterator.next();
                    Map.Entry it2 = (Map.Entry)t2;
                    $i$a$-maxByOrNull-GardenAnalyzerModule$dominantY$2 = false;
                    int n3 = ((Number)it2.getValue()).intValue();
                    if (n2 >= n3) continue;
                    t = t2;
                    n2 = n3;
                } while (iterator.hasNext());
                v0 = t;
            }
        }
        Map.Entry entry = v0;
        if (entry != null) {
            n = ((Number)entry.getKey()).intValue();
        } else {
            FootprintCell footprintCell = (FootprintCell)CollectionsKt.firstOrNull(cells);
            n = footprintCell != null ? footprintCell.getY() : 0;
        }
        return n;
    }

    private final List<RowSegment> buildRows(class_1937 level2, int dominantY, LaneAxis laneAxis, List<FootprintCell> cells) {
        Map map;
        Object $this$getOrPut$iv$iv$iv;
        if (laneAxis == LaneAxis.X) {
            $this$groupBy$iv = cells;
            $i$f$groupBy = false;
            Iterable iterable = $this$groupBy$iv;
            destination$iv$iv = new LinkedHashMap();
            $i$f$groupByTo = false;
            for (Object element$iv$iv : $this$groupByTo$iv$iv) {
                Object object;
                it = (FootprintCell)element$iv$iv;
                boolean bl = false;
                key$iv$iv = it.getZ();
                Map map2 = destination$iv$iv;
                key$iv$iv$iv = key$iv$iv;
                $i$f$getOrPut = false;
                value$iv$iv$iv = $this$getOrPut$iv$iv$iv.get(key$iv$iv$iv);
                if (value$iv$iv$iv == null) {
                    boolean bl2 = false;
                    answer$iv$iv$iv = new ArrayList();
                    $this$getOrPut$iv$iv$iv.put(key$iv$iv$iv, answer$iv$iv$iv);
                    object = answer$iv$iv$iv;
                } else {
                    object = value$iv$iv$iv;
                }
                list$iv$iv = (List)object;
                list$iv$iv.add(element$iv$iv);
            }
            map = destination$iv$iv;
        } else {
            $this$groupBy$iv = cells;
            $i$f$groupBy = false;
            $this$groupByTo$iv$iv = $this$groupBy$iv;
            destination$iv$iv = new LinkedHashMap();
            $i$f$groupByTo = false;
            for (Object element$iv$iv : $this$groupByTo$iv$iv) {
                Object object;
                it = (FootprintCell)element$iv$iv;
                boolean bl = false;
                key$iv$iv = it.getX();
                $this$getOrPut$iv$iv$iv = destination$iv$iv;
                key$iv$iv$iv = key$iv$iv;
                $i$f$getOrPut = false;
                value$iv$iv$iv = $this$getOrPut$iv$iv$iv.get(key$iv$iv$iv);
                if (value$iv$iv$iv == null) {
                    boolean bl3 = false;
                    answer$iv$iv$iv = new ArrayList();
                    $this$getOrPut$iv$iv$iv.put(key$iv$iv$iv, answer$iv$iv$iv);
                    object = answer$iv$iv$iv;
                } else {
                    object = value$iv$iv$iv;
                }
                list$iv$iv = (List)object;
                list$iv$iv.add(element$iv$iv);
            }
            map = destination$iv$iv;
        }
        Map grouped = map;
        if (grouped.isEmpty()) {
            return CollectionsKt.emptyList();
        }
        List bandKeys = CollectionsKt.sorted((Iterable)grouped.keySet());
        List bands = new ArrayList();
        int bandStart = ((Number)CollectionsKt.first((List)bandKeys)).intValue();
        int previous = ((Number)CollectionsKt.first((List)bandKeys)).intValue();
        int n = bandKeys.size();
        for (int index = 1; index < n; ++index) {
            int current = ((Number)bandKeys.get(index)).intValue();
            if (current - previous > 2) {
                ((Collection)bands).add(new IntRange(bandStart, previous));
                bandStart = current;
            }
            previous = current;
        }
        ((Collection)bands).add(new IntRange(bandStart, previous));
        List rows = new ArrayList();
        for (IntRange band : bands) {
            int laneMax;
            int n2;
            int laneMin;
            int it32;
            Iterator it4;
            Iterator $this$filterTo$iv$iv;
            Iterable $this$filter$iv = cells;
            boolean $i$f$filter = false;
            $this$getOrPut$iv$iv$iv = $this$filter$iv;
            Collection destination$iv$iv = new ArrayList();
            boolean $i$f$filterTo = false;
            Iterator value$iv$iv$iv = $this$filterTo$iv$iv.iterator();
            while (value$iv$iv$iv.hasNext()) {
                Object element$iv$iv = value$iv$iv$iv.next();
                FootprintCell it = (FootprintCell)element$iv$iv;
                boolean bl = false;
                int key = laneAxis == LaneAxis.X ? it.getZ() : it.getX();
                int n3 = band.getFirst();
                boolean bl4 = key <= band.getLast() ? n3 <= key : false;
                if (!bl4) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            List bandCells = (List)destination$iv$iv;
            if (bandCells.isEmpty()) continue;
            if (laneAxis == LaneAxis.X) {
                $this$filterTo$iv$iv = ((Iterable)bandCells).iterator();
                if (!$this$filterTo$iv$iv.hasNext()) {
                    throw new NoSuchElementException();
                }
                it4 = (FootprintCell)$this$filterTo$iv$iv.next();
                boolean bl = false;
                it4 = ((FootprintCell)((Object)it4)).getX();
                while ($this$filterTo$iv$iv.hasNext()) {
                    FootprintCell it32 = (FootprintCell)$this$filterTo$iv$iv.next();
                    $i$a$-minOf-GardenAnalyzerModule$buildRows$laneMin$1 = false;
                    it32 = it32.getX();
                    if (it4 <= it32) continue;
                    it4 = it32;
                }
                v4 = it4;
            } else {
                $this$filterTo$iv$iv = ((Iterable)bandCells).iterator();
                if (!$this$filterTo$iv$iv.hasNext()) {
                    throw new NoSuchElementException();
                }
                it4 = (FootprintCell)$this$filterTo$iv$iv.next();
                boolean bl = false;
                it4 = ((FootprintCell)((Object)it4)).getZ();
                while ($this$filterTo$iv$iv.hasNext()) {
                    FootprintCell it = (FootprintCell)$this$filterTo$iv$iv.next();
                    $i$a$-minOf-GardenAnalyzerModule$buildRows$laneMin$2 = false;
                    it32 = it.getZ();
                    if (it4 <= it32) continue;
                    it4 = it32;
                }
                v4 = laneMin = it4;
            }
            if (laneAxis == LaneAxis.X) {
                it4 = ((Iterable)bandCells).iterator();
                if (!it4.hasNext()) {
                    throw new NoSuchElementException();
                }
                FootprintCell it = (FootprintCell)it4.next();
                boolean bl = false;
                it32 = it.getX();
                while (it4.hasNext()) {
                    FootprintCell it22 = (FootprintCell)it4.next();
                    $i$a$-maxOf-GardenAnalyzerModule$buildRows$laneMax$1 = false;
                    int it22 = it22.getX();
                    if (it32 >= it22) continue;
                    it32 = it22;
                }
                n2 = it32;
            } else {
                it4 = ((Iterable)bandCells).iterator();
                if (!it4.hasNext()) {
                    throw new NoSuchElementException();
                }
                FootprintCell it = (FootprintCell)it4.next();
                boolean bl = false;
                int n4 = it.getZ();
                while (it4.hasNext()) {
                    FootprintCell it2 = (FootprintCell)it4.next();
                    $i$a$-maxOf-GardenAnalyzerModule$buildRows$laneMax$2 = false;
                    int n5 = it2.getZ();
                    if (n4 >= n5) continue;
                    n4 = n5;
                }
                n2 = n4;
            }
            if ((laneMax = n2) - laneMin + 1 < 2) continue;
            MarkerPoint startMarker = this.findTurnMarker(level2, dominantY, laneAxis, band, laneMin, -1);
            MarkerPoint endMarker = this.findTurnMarker(level2, dominantY, laneAxis, band, laneMax, 1);
            ((Collection)rows).add(new RowSegment(band.getFirst(), band.getLast(), laneMin, laneMax, startMarker, endMarker));
        }
        return rows;
    }

    /*
     * WARNING - void declaration
     */
    private final MarkerPoint findTurnMarker(class_1937 level2, int dominantY, LaneAxis laneAxis, IntRange band, int laneEdge, int direction) {
        void $this$sortedBy$iv;
        double bandCenter = (double)(band.getFirst() + band.getLast() + 1) / 2.0;
        Iterable iterable = CollectionsKt.toList((Iterable)((Iterable)band));
        boolean $i$f$sortedBy = false;
        List orderedBands = CollectionsKt.sortedWith((Iterable)$this$sortedBy$iv, (Comparator)new Comparator(bandCenter){
            final /* synthetic */ double $bandCenter$inlined;
            {
                this.$bandCenter$inlined = d;
            }

            public final int compare(T a, T b) {
                int it = ((Number)a).intValue();
                boolean bl = false;
                Comparable comparable = Double.valueOf(Math.abs((double)it + 0.5 - this.$bandCenter$inlined));
                it = ((Number)b).intValue();
                Comparable comparable2 = comparable;
                bl = false;
                return ComparisonsKt.compareValues((Comparable)comparable2, (Comparable)Double.valueOf(Math.abs((double)it + 0.5 - this.$bandCenter$inlined)));
            }
        });
        for (int step = 1; step < 5; ++step) {
            int lane = laneEdge + direction * step;
            Iterator iterator = orderedBands.iterator();
            while (iterator.hasNext()) {
                int bandValue = ((Number)iterator.next()).intValue();
                class_2338 rawPos = laneAxis == LaneAxis.X ? new class_2338(lane, dominantY, bandValue) : new class_2338(bandValue, dominantY, lane);
                class_2338 walkable = MinecraftPathingRules.INSTANCE.walkableAt(level2, rawPos);
                if (walkable == null) continue;
                return new MarkerPoint((double)walkable.method_10263() + 0.5, walkable.method_10264(), (double)walkable.method_10260() + 0.5, true);
            }
        }
        return laneAxis == LaneAxis.X ? new MarkerPoint((double)laneEdge + 0.5 + (double)direction, dominantY, bandCenter, false) : new MarkerPoint(bandCenter, dominantY, (double)laneEdge + 0.5 + (double)direction, false);
    }

    private final void renderAnalysis(class_1937 level2, FarmAnalysis analysis) {
        block9: {
            MarkerPoint marker;
            boolean $i$f$forEach;
            Iterable $this$forEach$iv;
            OverlayRenderEngine.Color boundsOutline;
            boolean forceRender = (Boolean)forceRenderSetting.getValue();
            OverlayRenderEngine.Color color = boundsOutline = analysis.getMacroable() ? macroBoundsOutline : nonMacroBoundsOutline;
            if (((Boolean)showBoundsSetting.getValue()).booleanValue()) {
                OverlayRenderEngine.INSTANCE.addBox(level2, (double)analysis.getMinX() - 0.02, (double)analysis.getDominantY() - 0.02, (double)analysis.getMinZ() - 0.02, (double)analysis.getMaxX() + 1.02, (double)analysis.getDominantY() + 1.02, (double)analysis.getMaxZ() + 1.02, boundsFill, boundsOutline, 2.0f, 3, TAG, forceRender);
            }
            if (((Boolean)showRowGuidesSetting.getValue()).booleanValue()) {
                $this$forEach$iv = analysis.getRows();
                $i$f$forEach = false;
                for (Object element$iv : $this$forEach$iv) {
                    RowSegment row = (RowSegment)element$iv;
                    boolean bl = false;
                    double y = (double)analysis.getDominantY() + 0.08;
                    if (analysis.getLaneAxis() == LaneAxis.X) {
                        OverlayRenderEngine.INSTANCE.addLine(level2, (double)row.getLaneMin() + 0.5, y, row.getBandCenter(), (double)row.getLaneMax() + 0.5, y, row.getBandCenter(), rowGuideColor, 1.6f, 3, TAG, forceRender);
                        continue;
                    }
                    OverlayRenderEngine.INSTANCE.addLine(level2, row.getBandCenter(), y, (double)row.getLaneMin() + 0.5, row.getBandCenter(), y, (double)row.getLaneMax() + 0.5, rowGuideColor, 1.6f, 3, TAG, forceRender);
                }
            }
            if (((Boolean)showTurnsSetting.getValue()).booleanValue()) {
                Iterator $this$flatMapTo$iv$iv;
                Iterable $this$flatMap$iv;
                $this$forEach$iv = analysis.getRows();
                boolean $i$f$flatMap = false;
                Iterator iterator = $this$flatMap$iv;
                Collection destination$iv$iv = new ArrayList();
                boolean $i$f$flatMapTo = false;
                Iterator bl = $this$flatMapTo$iv$iv.iterator();
                while (bl.hasNext()) {
                    Object element$iv$iv = bl.next();
                    RowSegment row = (RowSegment)element$iv$iv;
                    boolean bl2 = false;
                    Object[] objectArray = new MarkerPoint[]{row.getStartMarker(), row.getEndMarker()};
                    Iterable list$iv$iv = CollectionsKt.listOf((Object[])objectArray);
                    CollectionsKt.addAll((Collection)destination$iv$iv, (Iterable)list$iv$iv);
                }
                $this$flatMap$iv = CollectionsKt.distinct((Iterable)((List)destination$iv$iv));
                $i$f$forEach = false;
                for (Object element$iv : $this$forEach$iv) {
                    MarkerPoint marker2 = (MarkerPoint)element$iv;
                    boolean bl3 = false;
                    INSTANCE.renderMarker(level2, marker2, turnFill, turnOutline, 1.5, 0.24);
                }
            }
            if (!((Boolean)showStartFinishSetting.getValue()).booleanValue()) break block9;
            MarkerPoint markerPoint = analysis.getStartMarker();
            if (markerPoint != null) {
                marker = markerPoint;
                boolean bl = false;
                INSTANCE.renderMarker(level2, marker, startFill, startOutline, 4.0, 0.38);
            }
            MarkerPoint markerPoint2 = analysis.getFinishMarker();
            if (markerPoint2 != null) {
                marker = markerPoint2;
                boolean bl = false;
                INSTANCE.renderMarker(level2, marker, finishFill, finishOutline, 4.0, 0.38);
            }
        }
    }

    private final void renderMarker(class_1937 level2, MarkerPoint marker, OverlayRenderEngine.Color fill, OverlayRenderEngine.Color outline, double beamHeight, double size) {
        boolean forceRender = (Boolean)forceRenderSetting.getValue();
        OverlayRenderEngine.INSTANCE.addBox(level2, marker.getX() - size, marker.getY(), marker.getZ() - size, marker.getX() + size, marker.getY() + 1.0, marker.getZ() + size, fill, outline, marker.getWalkable() ? 2.0f : 1.5f, 3, TAG, forceRender);
        OverlayRenderEngine.INSTANCE.addLine(level2, marker.getX(), marker.getY() + 0.05, marker.getZ(), marker.getX(), marker.getY() + beamHeight, marker.getZ(), outline, marker.getWalkable() ? 2.2f : 1.4f, 3, TAG, forceRender);
    }

    private final CropKind cropKindOf(class_2680 state) {
        class_2248 class_22482 = state.method_26204();
        return Intrinsics.areEqual((Object)class_22482, (Object)class_2246.field_10293) ? CropKind.WHEAT : (Intrinsics.areEqual((Object)class_22482, (Object)class_2246.field_10609) ? CropKind.CARROT : (Intrinsics.areEqual((Object)class_22482, (Object)class_2246.field_10247) ? CropKind.POTATO : (Intrinsics.areEqual((Object)class_22482, (Object)class_2246.field_10341) ? CropKind.BEETROOT : (Intrinsics.areEqual((Object)class_22482, (Object)class_2246.field_9974) ? CropKind.NETHER_WART : (Intrinsics.areEqual((Object)class_22482, (Object)class_2246.field_10424) ? CropKind.SUGAR_CANE : (Intrinsics.areEqual((Object)class_22482, (Object)class_2246.field_10029) ? CropKind.CACTUS : (Intrinsics.areEqual((Object)class_22482, (Object)class_2246.field_46283) || Intrinsics.areEqual((Object)class_22482, (Object)class_2246.field_46287) || Intrinsics.areEqual((Object)class_22482, (Object)class_2246.field_46285) ? CropKind.MELON : (Intrinsics.areEqual((Object)class_22482, (Object)class_2246.field_46282) || Intrinsics.areEqual((Object)class_22482, (Object)class_2246.field_46286) || Intrinsics.areEqual((Object)class_22482, (Object)class_2246.field_46284) ? CropKind.PUMPKIN : (Intrinsics.areEqual((Object)class_22482, (Object)class_2246.field_10302) ? CropKind.COCOA : (Intrinsics.areEqual((Object)class_22482, (Object)class_2246.field_10251) || Intrinsics.areEqual((Object)class_22482, (Object)class_2246.field_10559) ? CropKind.MUSHROOM : null))))))))));
    }

    private final double horizontalDistanceSqToBounds(double x, double z, int minX, int maxX, int minZ, int maxZ) {
        double dx = x < (double)minX ? (double)minX - x : (x > (double)(maxX + 1) ? x - (double)(maxX + 1) : 0.0);
        double dz = z < (double)minZ ? (double)minZ - z : (z > (double)(maxZ + 1) ? z - (double)(maxZ + 1) : 0.0);
        return dx * dx + dz * dz;
    }

    private final String buildSummary(FarmAnalysis analysis) {
        StringBuilder stringBuilder;
        Object object;
        Object start;
        block7: {
            block6: {
                block5: {
                    Object object2;
                    block4: {
                        object2 = analysis.getStartMarker();
                        if (object2 == null) break block4;
                        MarkerPoint p0 = object2;
                        boolean bl = false;
                        String string = this.formatMarker(p0);
                        object2 = string;
                        if (string != null) break block5;
                    }
                    object2 = start = "n/a";
                }
                if ((object = analysis.getFinishMarker()) == null) break block6;
                MarkerPoint p0 = object;
                boolean bl = false;
                String string = this.formatMarker(p0);
                object = string;
                if (string != null) break block7;
            }
            object = "n/a";
        }
        Object finish = object;
        String verdict = analysis.getMacroable() ? "macroable" : "not macroable (" + CollectionsKt.joinToString$default((Iterable)CollectionsKt.take((Iterable)analysis.getReasons(), (int)2), (CharSequence)"; ", null, null, (int)0, null, null, (int)62, null) + ")";
        StringBuilder $this$buildSummary_u24lambda_u241 = stringBuilder = new StringBuilder();
        boolean bl = false;
        $this$buildSummary_u24lambda_u241.append("Garden Analyzer: ");
        $this$buildSummary_u24lambda_u241.append(analysis.getCrop().getDisplayName());
        $this$buildSummary_u24lambda_u241.append(" | rows ");
        $this$buildSummary_u24lambda_u241.append(analysis.getRows().size());
        $this$buildSummary_u24lambda_u241.append(" | span ");
        $this$buildSummary_u24lambda_u241.append(analysis.getMedianRowLength());
        $this$buildSummary_u24lambda_u241.append(" | ");
        $this$buildSummary_u24lambda_u241.append(verdict);
        $this$buildSummary_u24lambda_u241.append(" | start ");
        $this$buildSummary_u24lambda_u241.append((String)start);
        $this$buildSummary_u24lambda_u241.append(" | finish ");
        $this$buildSummary_u24lambda_u241.append((String)finish);
        return stringBuilder.toString();
    }

    private final String formatMarker(MarkerPoint marker) {
        return (int)marker.getX() + ", " + (int)marker.getY() + ", " + (int)marker.getZ();
    }

    private static final Unit analyzeNowSetting$lambda$0() {
        analyzeRequested = true;
        manualChatRequested = true;
        return Unit.INSTANCE;
    }

    private static final Unit clearSetting$lambda$0() {
        lastAnalysis = null;
        lastSummarySignature = "";
        manualOverlayUntilTick = 0L;
        OverlayRenderEngine.INSTANCE.clearTag(TAG);
        return Unit.INSTANCE;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        enabledSetting = new CheckboxSetting("Enabled", "Scan the nearest nearby farm footprint and render row turns, start, and finish markers.", false);
        heuristicInfo = new InfoSetting("Heuristic", "Finds the nearest crop cluster, infers lane direction, marks turns, and estimates whether the layout is lane-macroable.", InfoType.INFO);
        analyzeNowSetting = new ActionSetting("Analyze Now", "Analyze the nearest loaded farm immediately and print a summary to chat.", "Analyze", null, GardenAnalyzerModule::analyzeNowSetting$lambda$0, 8, null);
        clearSetting = new ActionSetting("Clear Analysis", "Clear the current farm overlay and cached result.", "Clear", null, GardenAnalyzerModule::clearSetting$lambda$0, 8, null);
        autoRefreshSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Auto Refresh", "Re-scan automatically while the module is enabled.", true), "Scan");
        refreshTicksSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Refresh Ticks", "Ticks between automatic farm scans.", 30.0, 5.0, 200.0, 1.0), "Scan");
        scanRadiusSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Scan Radius", "Horizontal block radius to scan around the player.", 72.0, 24.0, 128.0, 1.0), "Scan");
        verticalRangeSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Vertical Range", "Blocks above and below the player to scan for crops.", 6.0, 2.0, 16.0, 1.0), "Scan");
        minFarmCellsSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Min Farm Cells", "Minimum detected crop footprint cells before a cluster counts as a farm.", 48.0, 8.0, 1024.0, 1.0), "Scan");
        minRowLengthSetting = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Min Row Length", "Minimum row span required for the farm to count as macroable.", 16.0, 6.0, 128.0, 1.0), "Scan");
        showBoundsSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Show Bounds", "Render a bounding box around the detected farm.", true), "Render");
        showRowGuidesSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Show Row Guides", "Render guide lines across the inferred crop rows.", true), "Render");
        showTurnsSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Show Turns", "Render turn markers at both ends of every inferred row.", true), "Render");
        showStartFinishSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Show Start/Finish", "Render highlighted start and finish markers for the detected farm.", true), "Render");
        forceRenderSetting = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Force Render", "Keep the overlay visible through walls and outside the frustum.", true), "Render");
        lastSummarySignature = "";
        boundsFill = new OverlayRenderEngine.Color(45, 226, 255, 34);
        macroBoundsOutline = new OverlayRenderEngine.Color(76, 255, 114, 238);
        nonMacroBoundsOutline = new OverlayRenderEngine.Color(255, 122, 122, 238);
        rowGuideColor = new OverlayRenderEngine.Color(45, 226, 255, 176);
        turnFill = new OverlayRenderEngine.Color(255, 216, 76, 85);
        turnOutline = new OverlayRenderEngine.Color(255, 216, 76, 255);
        startFill = new OverlayRenderEngine.Color(76, 255, 114, 85);
        startOutline = new OverlayRenderEngine.Color(76, 255, 114, 255);
        finishFill = new OverlayRenderEngine.Color(255, 76, 76, 85);
        finishOutline = new OverlayRenderEngine.Color(255, 76, 76, 255);
        Setting[] settingArray = new Setting[]{enabledSetting, heuristicInfo, analyzeNowSetting, clearSetting, autoRefreshSetting, refreshTicksSetting, scanRadiusSetting, verticalRangeSetting, minFarmCellsSetting, minRowLengthSetting, showBoundsSetting, showRowGuidesSetting, showTurnsSetting, showStartFinishSetting, forceRenderSetting};
        INSTANCE.addSetting(settingArray);
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0015\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u001b\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\b\u001a\u0004\b\t\u0010\nR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u000b\u001a\u0004\b\f\u0010\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015j\u0002\b\u0016j\u0002\b\u0017j\u0002\b\u0018\u00a8\u0006\u0019"}, d2={"Lorg/cobalt/internal/garden/GardenAnalyzerModule$CropKind;", "", "", "displayName", "", "supportsLaneHeuristic", "<init>", "(Ljava/lang/String;ILjava/lang/String;Z)V", "Ljava/lang/String;", "getDisplayName", "()Ljava/lang/String;", "Z", "getSupportsLaneHeuristic", "()Z", "WHEAT", "CARROT", "POTATO", "BEETROOT", "NETHER_WART", "SUGAR_CANE", "CACTUS", "MELON", "PUMPKIN", "COCOA", "MUSHROOM", "cobalt"})
    private static final class CropKind
    extends Enum<CropKind> {
        @NotNull
        private final String displayName;
        private final boolean supportsLaneHeuristic;
        public static final /* enum */ CropKind WHEAT = new CropKind("WHEAT", 0, "Wheat", false, 2, null);
        public static final /* enum */ CropKind CARROT = new CropKind("CARROT", 1, "Carrot", false, 2, null);
        public static final /* enum */ CropKind POTATO = new CropKind("POTATO", 2, "Potato", false, 2, null);
        public static final /* enum */ CropKind BEETROOT = new CropKind("BEETROOT", 3, "Beetroot", false, 2, null);
        public static final /* enum */ CropKind NETHER_WART = new CropKind("NETHER_WART", 4, "Nether Wart", false, 2, null);
        public static final /* enum */ CropKind SUGAR_CANE = new CropKind("SUGAR_CANE", 5, "Sugar Cane", false, 2, null);
        public static final /* enum */ CropKind CACTUS = new CropKind("CACTUS", 6, "Cactus", false, 2, null);
        public static final /* enum */ CropKind MELON = new CropKind("MELON", 7, "Melon", false, 2, null);
        public static final /* enum */ CropKind PUMPKIN = new CropKind("PUMPKIN", 8, "Pumpkin", false, 2, null);
        public static final /* enum */ CropKind COCOA = new CropKind("Cocoa Beans", false);
        public static final /* enum */ CropKind MUSHROOM = new CropKind("Mushroom", false);
        private static final /* synthetic */ CropKind[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        private CropKind(String displayName, boolean supportsLaneHeuristic) {
            this.displayName = displayName;
            this.supportsLaneHeuristic = supportsLaneHeuristic;
        }

        /* synthetic */ CropKind(String string, int n, String string2, boolean bl, int n2, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n2 & 2) != 0) {
                bl = true;
            }
            this(string2, bl);
        }

        @NotNull
        public final String getDisplayName() {
            return this.displayName;
        }

        public final boolean getSupportsLaneHeuristic() {
            return this.supportsLaneHeuristic;
        }

        public static CropKind[] values() {
            return (CropKind[])$VALUES.clone();
        }

        public static CropKind valueOf(String value) {
            return Enum.valueOf(CropKind.class, value);
        }

        @NotNull
        public static EnumEntries<CropKind> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = cropKindArray = new CropKind[]{CropKind.WHEAT, CropKind.CARROT, CropKind.POTATO, CropKind.BEETROOT, CropKind.NETHER_WART, CropKind.SUGAR_CANE, CropKind.CACTUS, CropKind.MELON, CropKind.PUMPKIN, CropKind.COCOA, CropKind.MUSHROOM};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b9\b\u0082\b\u0018\u00002\u00020\u0001B\u008f\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\u0006\u0012\u0006\u0010\t\u001a\u00020\u0006\u0012\u0006\u0010\n\u001a\u00020\u0006\u0012\u0006\u0010\u000b\u001a\u00020\u0006\u0012\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\f\u0012\u0006\u0010\u000f\u001a\u00020\u0006\u0012\u0006\u0010\u0010\u001a\u00020\u0006\u0012\u0006\u0010\u0012\u001a\u00020\u0011\u0012\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00130\f\u0012\b\u0010\u0016\u001a\u0004\u0018\u00010\u0015\u0012\b\u0010\u0017\u001a\u0004\u0018\u00010\u0015\u0012\u0006\u0010\u0019\u001a\u00020\u0018\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\r\u0010\u001c\u001a\u00020\u0013\u00a2\u0006\u0004\b\u001c\u0010\u001dJ\u0010\u0010\u001e\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u0010\u0010 \u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b \u0010!J\u0010\u0010\"\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\"\u0010#J\u0010\u0010$\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b$\u0010#J\u0010\u0010%\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b%\u0010#J\u0010\u0010&\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b&\u0010#J\u0010\u0010'\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b'\u0010#J\u0016\u0010(\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u00c6\u0003\u00a2\u0006\u0004\b(\u0010)J\u0010\u0010*\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b*\u0010#J\u0010\u0010+\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b+\u0010#J\u0010\u0010,\u001a\u00020\u0011H\u00c6\u0003\u00a2\u0006\u0004\b,\u0010-J\u0016\u0010.\u001a\b\u0012\u0004\u0012\u00020\u00130\fH\u00c6\u0003\u00a2\u0006\u0004\b.\u0010)J\u0012\u0010/\u001a\u0004\u0018\u00010\u0015H\u00c6\u0003\u00a2\u0006\u0004\b/\u00100J\u0012\u00101\u001a\u0004\u0018\u00010\u0015H\u00c6\u0003\u00a2\u0006\u0004\b1\u00100J\u0010\u00102\u001a\u00020\u0018H\u00c6\u0003\u00a2\u0006\u0004\b2\u00103J\u00b6\u0001\u00104\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\b\u001a\u00020\u00062\b\b\u0002\u0010\t\u001a\u00020\u00062\b\b\u0002\u0010\n\u001a\u00020\u00062\b\b\u0002\u0010\u000b\u001a\u00020\u00062\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\f2\b\b\u0002\u0010\u000f\u001a\u00020\u00062\b\b\u0002\u0010\u0010\u001a\u00020\u00062\b\b\u0002\u0010\u0012\u001a\u00020\u00112\u000e\b\u0002\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00130\f2\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u00152\n\b\u0002\u0010\u0017\u001a\u0004\u0018\u00010\u00152\b\b\u0002\u0010\u0019\u001a\u00020\u0018H\u00c6\u0001\u00a2\u0006\u0004\b4\u00105J\u001b\u00107\u001a\u00020\u00112\b\u00106\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b7\u00108J\u0011\u00109\u001a\u00020\u0006H\u00d6\u0081\u0004\u00a2\u0006\u0004\b9\u0010#J\u0011\u0010:\u001a\u00020\u0013H\u00d6\u0081\u0004\u00a2\u0006\u0004\b:\u0010\u001dR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010;\u001a\u0004\b<\u0010\u001fR\u0017\u0010\u0005\u001a\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010=\u001a\u0004\b>\u0010!R\u0017\u0010\u0007\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010?\u001a\u0004\b@\u0010#R\u0017\u0010\b\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\b\u0010?\u001a\u0004\bA\u0010#R\u0017\u0010\t\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\t\u0010?\u001a\u0004\bB\u0010#R\u0017\u0010\n\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\n\u0010?\u001a\u0004\bC\u0010#R\u0017\u0010\u000b\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\u000b\u0010?\u001a\u0004\bD\u0010#R\u001d\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\f8\u0006\u00a2\u0006\f\n\u0004\b\u000e\u0010E\u001a\u0004\bF\u0010)R\u0017\u0010\u000f\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\u000f\u0010?\u001a\u0004\bG\u0010#R\u0017\u0010\u0010\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\u0010\u0010?\u001a\u0004\bH\u0010#R\u0017\u0010\u0012\u001a\u00020\u00118\u0006\u00a2\u0006\f\n\u0004\b\u0012\u0010I\u001a\u0004\bJ\u0010-R\u001d\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00130\f8\u0006\u00a2\u0006\f\n\u0004\b\u0014\u0010E\u001a\u0004\bK\u0010)R\u0019\u0010\u0016\u001a\u0004\u0018\u00010\u00158\u0006\u00a2\u0006\f\n\u0004\b\u0016\u0010L\u001a\u0004\bM\u00100R\u0019\u0010\u0017\u001a\u0004\u0018\u00010\u00158\u0006\u00a2\u0006\f\n\u0004\b\u0017\u0010L\u001a\u0004\bN\u00100R\u0017\u0010\u0019\u001a\u00020\u00188\u0006\u00a2\u0006\f\n\u0004\b\u0019\u0010O\u001a\u0004\bP\u00103\u00a8\u0006Q"}, d2={"Lorg/cobalt/internal/garden/GardenAnalyzerModule$FarmAnalysis;", "", "Lorg/cobalt/internal/garden/GardenAnalyzerModule$CropKind;", "crop", "Lorg/cobalt/internal/garden/GardenAnalyzerModule$LaneAxis;", "laneAxis", "", "dominantY", "minX", "maxX", "minZ", "maxZ", "", "Lorg/cobalt/internal/garden/GardenAnalyzerModule$RowSegment;", "rows", "cellCount", "medianRowLength", "", "macroable", "", "reasons", "Lorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;", "startMarker", "finishMarker", "", "distanceSq", "<init>", "(Lorg/cobalt/internal/garden/GardenAnalyzerModule$CropKind;Lorg/cobalt/internal/garden/GardenAnalyzerModule$LaneAxis;IIIIILjava/util/List;IIZLjava/util/List;Lorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;Lorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;D)V", "signature", "()Ljava/lang/String;", "component1", "()Lorg/cobalt/internal/garden/GardenAnalyzerModule$CropKind;", "component2", "()Lorg/cobalt/internal/garden/GardenAnalyzerModule$LaneAxis;", "component3", "()I", "component4", "component5", "component6", "component7", "component8", "()Ljava/util/List;", "component9", "component10", "component11", "()Z", "component12", "component13", "()Lorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;", "component14", "component15", "()D", "copy", "(Lorg/cobalt/internal/garden/GardenAnalyzerModule$CropKind;Lorg/cobalt/internal/garden/GardenAnalyzerModule$LaneAxis;IIIIILjava/util/List;IIZLjava/util/List;Lorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;Lorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;D)Lorg/cobalt/internal/garden/GardenAnalyzerModule$FarmAnalysis;", "other", "equals", "(Ljava/lang/Object;)Z", "hashCode", "toString", "Lorg/cobalt/internal/garden/GardenAnalyzerModule$CropKind;", "getCrop", "Lorg/cobalt/internal/garden/GardenAnalyzerModule$LaneAxis;", "getLaneAxis", "I", "getDominantY", "getMinX", "getMaxX", "getMinZ", "getMaxZ", "Ljava/util/List;", "getRows", "getCellCount", "getMedianRowLength", "Z", "getMacroable", "getReasons", "Lorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;", "getStartMarker", "getFinishMarker", "D", "getDistanceSq", "cobalt"})
    private static final class FarmAnalysis {
        @NotNull
        private final CropKind crop;
        @NotNull
        private final LaneAxis laneAxis;
        private final int dominantY;
        private final int minX;
        private final int maxX;
        private final int minZ;
        private final int maxZ;
        @NotNull
        private final List<RowSegment> rows;
        private final int cellCount;
        private final int medianRowLength;
        private final boolean macroable;
        @NotNull
        private final List<String> reasons;
        @Nullable
        private final MarkerPoint startMarker;
        @Nullable
        private final MarkerPoint finishMarker;
        private final double distanceSq;

        public FarmAnalysis(@NotNull CropKind crop, @NotNull LaneAxis laneAxis, int dominantY, int minX, int maxX, int minZ, int maxZ, @NotNull List<RowSegment> rows, int cellCount, int medianRowLength, boolean macroable, @NotNull List<String> reasons, @Nullable MarkerPoint startMarker, @Nullable MarkerPoint finishMarker, double distanceSq) {
            Intrinsics.checkNotNullParameter((Object)((Object)crop), (String)"crop");
            Intrinsics.checkNotNullParameter((Object)((Object)laneAxis), (String)"laneAxis");
            Intrinsics.checkNotNullParameter(rows, (String)"rows");
            Intrinsics.checkNotNullParameter(reasons, (String)"reasons");
            this.crop = crop;
            this.laneAxis = laneAxis;
            this.dominantY = dominantY;
            this.minX = minX;
            this.maxX = maxX;
            this.minZ = minZ;
            this.maxZ = maxZ;
            this.rows = rows;
            this.cellCount = cellCount;
            this.medianRowLength = medianRowLength;
            this.macroable = macroable;
            this.reasons = reasons;
            this.startMarker = startMarker;
            this.finishMarker = finishMarker;
            this.distanceSq = distanceSq;
        }

        @NotNull
        public final CropKind getCrop() {
            return this.crop;
        }

        @NotNull
        public final LaneAxis getLaneAxis() {
            return this.laneAxis;
        }

        public final int getDominantY() {
            return this.dominantY;
        }

        public final int getMinX() {
            return this.minX;
        }

        public final int getMaxX() {
            return this.maxX;
        }

        public final int getMinZ() {
            return this.minZ;
        }

        public final int getMaxZ() {
            return this.maxZ;
        }

        @NotNull
        public final List<RowSegment> getRows() {
            return this.rows;
        }

        public final int getCellCount() {
            return this.cellCount;
        }

        public final int getMedianRowLength() {
            return this.medianRowLength;
        }

        public final boolean getMacroable() {
            return this.macroable;
        }

        @NotNull
        public final List<String> getReasons() {
            return this.reasons;
        }

        @Nullable
        public final MarkerPoint getStartMarker() {
            return this.startMarker;
        }

        @Nullable
        public final MarkerPoint getFinishMarker() {
            return this.finishMarker;
        }

        public final double getDistanceSq() {
            return this.distanceSq;
        }

        @NotNull
        public final String signature() {
            Object[] objectArray = new Object[]{this.crop.name(), this.laneAxis.name(), this.minX, this.maxX, this.minZ, this.maxZ, this.rows.size(), this.medianRowLength, this.macroable};
            return CollectionsKt.joinToString$default((Iterable)CollectionsKt.listOf((Object[])objectArray), (CharSequence)"|", null, null, (int)0, null, null, (int)62, null);
        }

        @NotNull
        public final CropKind component1() {
            return this.crop;
        }

        @NotNull
        public final LaneAxis component2() {
            return this.laneAxis;
        }

        public final int component3() {
            return this.dominantY;
        }

        public final int component4() {
            return this.minX;
        }

        public final int component5() {
            return this.maxX;
        }

        public final int component6() {
            return this.minZ;
        }

        public final int component7() {
            return this.maxZ;
        }

        @NotNull
        public final List<RowSegment> component8() {
            return this.rows;
        }

        public final int component9() {
            return this.cellCount;
        }

        public final int component10() {
            return this.medianRowLength;
        }

        public final boolean component11() {
            return this.macroable;
        }

        @NotNull
        public final List<String> component12() {
            return this.reasons;
        }

        @Nullable
        public final MarkerPoint component13() {
            return this.startMarker;
        }

        @Nullable
        public final MarkerPoint component14() {
            return this.finishMarker;
        }

        public final double component15() {
            return this.distanceSq;
        }

        @NotNull
        public final FarmAnalysis copy(@NotNull CropKind crop, @NotNull LaneAxis laneAxis, int dominantY, int minX, int maxX, int minZ, int maxZ, @NotNull List<RowSegment> rows, int cellCount, int medianRowLength, boolean macroable, @NotNull List<String> reasons, @Nullable MarkerPoint startMarker, @Nullable MarkerPoint finishMarker, double distanceSq) {
            Intrinsics.checkNotNullParameter((Object)((Object)crop), (String)"crop");
            Intrinsics.checkNotNullParameter((Object)((Object)laneAxis), (String)"laneAxis");
            Intrinsics.checkNotNullParameter(rows, (String)"rows");
            Intrinsics.checkNotNullParameter(reasons, (String)"reasons");
            return new FarmAnalysis(crop, laneAxis, dominantY, minX, maxX, minZ, maxZ, rows, cellCount, medianRowLength, macroable, reasons, startMarker, finishMarker, distanceSq);
        }

        public static /* synthetic */ FarmAnalysis copy$default(FarmAnalysis farmAnalysis, CropKind cropKind, LaneAxis laneAxis, int n, int n2, int n3, int n4, int n5, List list, int n6, int n7, boolean bl, List list2, MarkerPoint markerPoint, MarkerPoint markerPoint2, double d, int n8, Object object) {
            if ((n8 & 1) != 0) {
                cropKind = farmAnalysis.crop;
            }
            if ((n8 & 2) != 0) {
                laneAxis = farmAnalysis.laneAxis;
            }
            if ((n8 & 4) != 0) {
                n = farmAnalysis.dominantY;
            }
            if ((n8 & 8) != 0) {
                n2 = farmAnalysis.minX;
            }
            if ((n8 & 0x10) != 0) {
                n3 = farmAnalysis.maxX;
            }
            if ((n8 & 0x20) != 0) {
                n4 = farmAnalysis.minZ;
            }
            if ((n8 & 0x40) != 0) {
                n5 = farmAnalysis.maxZ;
            }
            if ((n8 & 0x80) != 0) {
                list = farmAnalysis.rows;
            }
            if ((n8 & 0x100) != 0) {
                n6 = farmAnalysis.cellCount;
            }
            if ((n8 & 0x200) != 0) {
                n7 = farmAnalysis.medianRowLength;
            }
            if ((n8 & 0x400) != 0) {
                bl = farmAnalysis.macroable;
            }
            if ((n8 & 0x800) != 0) {
                list2 = farmAnalysis.reasons;
            }
            if ((n8 & 0x1000) != 0) {
                markerPoint = farmAnalysis.startMarker;
            }
            if ((n8 & 0x2000) != 0) {
                markerPoint2 = farmAnalysis.finishMarker;
            }
            if ((n8 & 0x4000) != 0) {
                d = farmAnalysis.distanceSq;
            }
            return farmAnalysis.copy(cropKind, laneAxis, n, n2, n3, n4, n5, list, n6, n7, bl, list2, markerPoint, markerPoint2, d);
        }

        @NotNull
        public String toString() {
            return "FarmAnalysis(crop=" + this.crop + ", laneAxis=" + this.laneAxis + ", dominantY=" + this.dominantY + ", minX=" + this.minX + ", maxX=" + this.maxX + ", minZ=" + this.minZ + ", maxZ=" + this.maxZ + ", rows=" + this.rows + ", cellCount=" + this.cellCount + ", medianRowLength=" + this.medianRowLength + ", macroable=" + this.macroable + ", reasons=" + this.reasons + ", startMarker=" + this.startMarker + ", finishMarker=" + this.finishMarker + ", distanceSq=" + this.distanceSq + ")";
        }

        public int hashCode() {
            int result = this.crop.hashCode();
            result = result * 31 + this.laneAxis.hashCode();
            result = result * 31 + Integer.hashCode(this.dominantY);
            result = result * 31 + Integer.hashCode(this.minX);
            result = result * 31 + Integer.hashCode(this.maxX);
            result = result * 31 + Integer.hashCode(this.minZ);
            result = result * 31 + Integer.hashCode(this.maxZ);
            result = result * 31 + ((Object)this.rows).hashCode();
            result = result * 31 + Integer.hashCode(this.cellCount);
            result = result * 31 + Integer.hashCode(this.medianRowLength);
            result = result * 31 + Boolean.hashCode(this.macroable);
            result = result * 31 + ((Object)this.reasons).hashCode();
            result = result * 31 + (this.startMarker == null ? 0 : this.startMarker.hashCode());
            result = result * 31 + (this.finishMarker == null ? 0 : this.finishMarker.hashCode());
            result = result * 31 + Double.hashCode(this.distanceSq);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof FarmAnalysis)) {
                return false;
            }
            FarmAnalysis farmAnalysis = (FarmAnalysis)other;
            if (this.crop != farmAnalysis.crop) {
                return false;
            }
            if (this.laneAxis != farmAnalysis.laneAxis) {
                return false;
            }
            if (this.dominantY != farmAnalysis.dominantY) {
                return false;
            }
            if (this.minX != farmAnalysis.minX) {
                return false;
            }
            if (this.maxX != farmAnalysis.maxX) {
                return false;
            }
            if (this.minZ != farmAnalysis.minZ) {
                return false;
            }
            if (this.maxZ != farmAnalysis.maxZ) {
                return false;
            }
            if (!Intrinsics.areEqual(this.rows, farmAnalysis.rows)) {
                return false;
            }
            if (this.cellCount != farmAnalysis.cellCount) {
                return false;
            }
            if (this.medianRowLength != farmAnalysis.medianRowLength) {
                return false;
            }
            if (this.macroable != farmAnalysis.macroable) {
                return false;
            }
            if (!Intrinsics.areEqual(this.reasons, farmAnalysis.reasons)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.startMarker, (Object)farmAnalysis.startMarker)) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.finishMarker, (Object)farmAnalysis.finishMarker)) {
                return false;
            }
            return Double.compare(this.distanceSq, farmAnalysis.distanceSq) == 0;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0082\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\tJ\u0010\u0010\u000b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000b\u0010\tJ.\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001b\u0010\u0010\u001a\u00020\u000f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0012\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0012\u0010\tJ\u0011\u0010\u0014\u001a\u00020\u0013H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0014\u0010\u0015R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0016\u001a\u0004\b\u0017\u0010\tR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0016\u001a\u0004\b\u0018\u0010\tR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u0016\u001a\u0004\b\u0019\u0010\t\u00a8\u0006\u001a"}, d2={"Lorg/cobalt/internal/garden/GardenAnalyzerModule$FootprintCell;", "", "", "x", "z", "y", "<init>", "(III)V", "component1", "()I", "component2", "component3", "copy", "(III)Lorg/cobalt/internal/garden/GardenAnalyzerModule$FootprintCell;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getX", "getZ", "getY", "cobalt"})
    private static final class FootprintCell {
        private final int x;
        private final int z;
        private final int y;

        public FootprintCell(int x, int z, int y) {
            this.x = x;
            this.z = z;
            this.y = y;
        }

        public final int getX() {
            return this.x;
        }

        public final int getZ() {
            return this.z;
        }

        public final int getY() {
            return this.y;
        }

        public final int component1() {
            return this.x;
        }

        public final int component2() {
            return this.z;
        }

        public final int component3() {
            return this.y;
        }

        @NotNull
        public final FootprintCell copy(int x, int z, int y) {
            return new FootprintCell(x, z, y);
        }

        public static /* synthetic */ FootprintCell copy$default(FootprintCell footprintCell, int n, int n2, int n3, int n4, Object object) {
            if ((n4 & 1) != 0) {
                n = footprintCell.x;
            }
            if ((n4 & 2) != 0) {
                n2 = footprintCell.z;
            }
            if ((n4 & 4) != 0) {
                n3 = footprintCell.y;
            }
            return footprintCell.copy(n, n2, n3);
        }

        @NotNull
        public String toString() {
            return "FootprintCell(x=" + this.x + ", z=" + this.z + ", y=" + this.y + ")";
        }

        public int hashCode() {
            int result = Integer.hashCode(this.x);
            result = result * 31 + Integer.hashCode(this.z);
            result = result * 31 + Integer.hashCode(this.y);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof FootprintCell)) {
                return false;
            }
            FootprintCell footprintCell = (FootprintCell)other;
            if (this.x != footprintCell.x) {
                return false;
            }
            if (this.z != footprintCell.z) {
                return false;
            }
            return this.y == footprintCell.y;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u0082\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\t\u0010\bJ$\u0010\n\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\n\u0010\u000bJ\u001b\u0010\u000e\u001a\u00020\r2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0011\u0010\u0010\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0010\u0010\bJ\u0011\u0010\u0012\u001a\u00020\u0011H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0012\u0010\u0013R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0014\u001a\u0004\b\u0015\u0010\bR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u0014\u001a\u0004\b\u0016\u0010\b\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/internal/garden/GardenAnalyzerModule$GridCell;", "", "", "x", "z", "<init>", "(II)V", "component1", "()I", "component2", "copy", "(II)Lorg/cobalt/internal/garden/GardenAnalyzerModule$GridCell;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getX", "getZ", "cobalt"})
    private static final class GridCell {
        private final int x;
        private final int z;

        public GridCell(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public final int getX() {
            return this.x;
        }

        public final int getZ() {
            return this.z;
        }

        public final int component1() {
            return this.x;
        }

        public final int component2() {
            return this.z;
        }

        @NotNull
        public final GridCell copy(int x, int z) {
            return new GridCell(x, z);
        }

        public static /* synthetic */ GridCell copy$default(GridCell gridCell, int n, int n2, int n3, Object object) {
            if ((n3 & 1) != 0) {
                n = gridCell.x;
            }
            if ((n3 & 2) != 0) {
                n2 = gridCell.z;
            }
            return gridCell.copy(n, n2);
        }

        @NotNull
        public String toString() {
            return "GridCell(x=" + this.x + ", z=" + this.z + ")";
        }

        public int hashCode() {
            int result = Integer.hashCode(this.x);
            result = result * 31 + Integer.hashCode(this.z);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof GridCell)) {
                return false;
            }
            GridCell gridCell = (GridCell)other;
            if (this.x != gridCell.x) {
                return false;
            }
            return this.z == gridCell.z;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0006"}, d2={"Lorg/cobalt/internal/garden/GardenAnalyzerModule$LaneAxis;", "", "<init>", "(Ljava/lang/String;I)V", "X", "Z", "cobalt"})
    private static final class LaneAxis
    extends Enum<LaneAxis> {
        public static final /* enum */ LaneAxis X = new LaneAxis();
        public static final /* enum */ LaneAxis Z = new LaneAxis();
        private static final /* synthetic */ LaneAxis[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static LaneAxis[] values() {
            return (LaneAxis[])$VALUES.clone();
        }

        public static LaneAxis valueOf(String value) {
            return Enum.valueOf(LaneAxis.class, value);
        }

        @NotNull
        public static EnumEntries<LaneAxis> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = laneAxisArray = new LaneAxis[]{LaneAxis.X, LaneAxis.Z};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u000e\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\t\b\u0082\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\u000bJ\u0010\u0010\r\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\r\u0010\u000bJ\u0010\u0010\u000e\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\u000fJ8\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0007\u001a\u00020\u0006H\u00c6\u0001\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u001b\u0010\u0013\u001a\u00020\u00062\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0016\u001a\u00020\u0015H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0011\u0010\u0019\u001a\u00020\u0018H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0019\u0010\u001aR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001b\u001a\u0004\b\u001c\u0010\u000bR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001b\u001a\u0004\b\u001d\u0010\u000bR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001b\u001a\u0004\b\u001e\u0010\u000bR\u0017\u0010\u0007\u001a\u00020\u00068\u0006\u00a2\u0006\f\n\u0004\b\u0007\u0010\u001f\u001a\u0004\b \u0010\u000f\u00a8\u0006!"}, d2={"Lorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;", "", "", "x", "y", "z", "", "walkable", "<init>", "(DDDZ)V", "component1", "()D", "component2", "component3", "component4", "()Z", "copy", "(DDDZ)Lorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;", "other", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "", "toString", "()Ljava/lang/String;", "D", "getX", "getY", "getZ", "Z", "getWalkable", "cobalt"})
    private static final class MarkerPoint {
        private final double x;
        private final double y;
        private final double z;
        private final boolean walkable;

        public MarkerPoint(double x, double y, double z, boolean walkable) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.walkable = walkable;
        }

        public final double getX() {
            return this.x;
        }

        public final double getY() {
            return this.y;
        }

        public final double getZ() {
            return this.z;
        }

        public final boolean getWalkable() {
            return this.walkable;
        }

        public final double component1() {
            return this.x;
        }

        public final double component2() {
            return this.y;
        }

        public final double component3() {
            return this.z;
        }

        public final boolean component4() {
            return this.walkable;
        }

        @NotNull
        public final MarkerPoint copy(double x, double y, double z, boolean walkable) {
            return new MarkerPoint(x, y, z, walkable);
        }

        public static /* synthetic */ MarkerPoint copy$default(MarkerPoint markerPoint, double d, double d2, double d3, boolean bl, int n, Object object) {
            if ((n & 1) != 0) {
                d = markerPoint.x;
            }
            if ((n & 2) != 0) {
                d2 = markerPoint.y;
            }
            if ((n & 4) != 0) {
                d3 = markerPoint.z;
            }
            if ((n & 8) != 0) {
                bl = markerPoint.walkable;
            }
            return markerPoint.copy(d, d2, d3, bl);
        }

        @NotNull
        public String toString() {
            return "MarkerPoint(x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", walkable=" + this.walkable + ")";
        }

        public int hashCode() {
            int result = Double.hashCode(this.x);
            result = result * 31 + Double.hashCode(this.y);
            result = result * 31 + Double.hashCode(this.z);
            result = result * 31 + Boolean.hashCode(this.walkable);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof MarkerPoint)) {
                return false;
            }
            MarkerPoint markerPoint = (MarkerPoint)other;
            if (Double.compare(this.x, markerPoint.x) != 0) {
                return false;
            }
            if (Double.compare(this.y, markerPoint.y) != 0) {
                return false;
            }
            if (Double.compare(this.z, markerPoint.z) != 0) {
                return false;
            }
            return this.walkable == markerPoint.walkable;
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u0006\n\u0002\b\u0006\b\u0082\b\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\rJ\u0010\u0010\u000f\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000f\u0010\rJ\u0010\u0010\u0010\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\rJ\u0010\u0010\u0011\u001a\u00020\u0007H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0010\u0010\u0013\u001a\u00020\u0007H\u00c6\u0003\u00a2\u0006\u0004\b\u0013\u0010\u0012JL\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u0007H\u00c6\u0001\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u001b\u0010\u0018\u001a\u00020\u00172\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0011\u0010\u001a\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001a\u0010\rJ\u0011\u0010\u001c\u001a\u00020\u001bH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001c\u0010\u001dR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001e\u001a\u0004\b\u001f\u0010\rR\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010\u001e\u001a\u0004\b \u0010\rR\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u001e\u001a\u0004\b!\u0010\rR\u0017\u0010\u0006\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001e\u001a\u0004\b\"\u0010\rR\u0017\u0010\b\u001a\u00020\u00078\u0006\u00a2\u0006\f\n\u0004\b\b\u0010#\u001a\u0004\b$\u0010\u0012R\u0017\u0010\t\u001a\u00020\u00078\u0006\u00a2\u0006\f\n\u0004\b\t\u0010#\u001a\u0004\b%\u0010\u0012R\u0011\u0010)\u001a\u00020&8F\u00a2\u0006\u0006\u001a\u0004\b'\u0010(R\u0011\u0010+\u001a\u00020\u00028F\u00a2\u0006\u0006\u001a\u0004\b*\u0010\r\u00a8\u0006,"}, d2={"Lorg/cobalt/internal/garden/GardenAnalyzerModule$RowSegment;", "", "", "bandMin", "bandMax", "laneMin", "laneMax", "Lorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;", "startMarker", "endMarker", "<init>", "(IIIILorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;Lorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;)V", "component1", "()I", "component2", "component3", "component4", "component5", "()Lorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;", "component6", "copy", "(IIIILorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;Lorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;)Lorg/cobalt/internal/garden/GardenAnalyzerModule$RowSegment;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "I", "getBandMin", "getBandMax", "getLaneMin", "getLaneMax", "Lorg/cobalt/internal/garden/GardenAnalyzerModule$MarkerPoint;", "getStartMarker", "getEndMarker", "", "getBandCenter", "()D", "bandCenter", "getLength", "length", "cobalt"})
    private static final class RowSegment {
        private final int bandMin;
        private final int bandMax;
        private final int laneMin;
        private final int laneMax;
        @NotNull
        private final MarkerPoint startMarker;
        @NotNull
        private final MarkerPoint endMarker;

        public RowSegment(int bandMin, int bandMax, int laneMin, int laneMax, @NotNull MarkerPoint startMarker, @NotNull MarkerPoint endMarker) {
            Intrinsics.checkNotNullParameter((Object)startMarker, (String)"startMarker");
            Intrinsics.checkNotNullParameter((Object)endMarker, (String)"endMarker");
            this.bandMin = bandMin;
            this.bandMax = bandMax;
            this.laneMin = laneMin;
            this.laneMax = laneMax;
            this.startMarker = startMarker;
            this.endMarker = endMarker;
        }

        public final int getBandMin() {
            return this.bandMin;
        }

        public final int getBandMax() {
            return this.bandMax;
        }

        public final int getLaneMin() {
            return this.laneMin;
        }

        public final int getLaneMax() {
            return this.laneMax;
        }

        @NotNull
        public final MarkerPoint getStartMarker() {
            return this.startMarker;
        }

        @NotNull
        public final MarkerPoint getEndMarker() {
            return this.endMarker;
        }

        public final double getBandCenter() {
            return (double)(this.bandMin + this.bandMax + 1) / 2.0;
        }

        public final int getLength() {
            return this.laneMax - this.laneMin + 1;
        }

        public final int component1() {
            return this.bandMin;
        }

        public final int component2() {
            return this.bandMax;
        }

        public final int component3() {
            return this.laneMin;
        }

        public final int component4() {
            return this.laneMax;
        }

        @NotNull
        public final MarkerPoint component5() {
            return this.startMarker;
        }

        @NotNull
        public final MarkerPoint component6() {
            return this.endMarker;
        }

        @NotNull
        public final RowSegment copy(int bandMin, int bandMax, int laneMin, int laneMax, @NotNull MarkerPoint startMarker, @NotNull MarkerPoint endMarker) {
            Intrinsics.checkNotNullParameter((Object)startMarker, (String)"startMarker");
            Intrinsics.checkNotNullParameter((Object)endMarker, (String)"endMarker");
            return new RowSegment(bandMin, bandMax, laneMin, laneMax, startMarker, endMarker);
        }

        public static /* synthetic */ RowSegment copy$default(RowSegment rowSegment, int n, int n2, int n3, int n4, MarkerPoint markerPoint, MarkerPoint markerPoint2, int n5, Object object) {
            if ((n5 & 1) != 0) {
                n = rowSegment.bandMin;
            }
            if ((n5 & 2) != 0) {
                n2 = rowSegment.bandMax;
            }
            if ((n5 & 4) != 0) {
                n3 = rowSegment.laneMin;
            }
            if ((n5 & 8) != 0) {
                n4 = rowSegment.laneMax;
            }
            if ((n5 & 0x10) != 0) {
                markerPoint = rowSegment.startMarker;
            }
            if ((n5 & 0x20) != 0) {
                markerPoint2 = rowSegment.endMarker;
            }
            return rowSegment.copy(n, n2, n3, n4, markerPoint, markerPoint2);
        }

        @NotNull
        public String toString() {
            return "RowSegment(bandMin=" + this.bandMin + ", bandMax=" + this.bandMax + ", laneMin=" + this.laneMin + ", laneMax=" + this.laneMax + ", startMarker=" + this.startMarker + ", endMarker=" + this.endMarker + ")";
        }

        public int hashCode() {
            int result = Integer.hashCode(this.bandMin);
            result = result * 31 + Integer.hashCode(this.bandMax);
            result = result * 31 + Integer.hashCode(this.laneMin);
            result = result * 31 + Integer.hashCode(this.laneMax);
            result = result * 31 + this.startMarker.hashCode();
            result = result * 31 + this.endMarker.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof RowSegment)) {
                return false;
            }
            RowSegment rowSegment = (RowSegment)other;
            if (this.bandMin != rowSegment.bandMin) {
                return false;
            }
            if (this.bandMax != rowSegment.bandMax) {
                return false;
            }
            if (this.laneMin != rowSegment.laneMin) {
                return false;
            }
            if (this.laneMax != rowSegment.laneMax) {
                return false;
            }
            if (!Intrinsics.areEqual((Object)this.startMarker, (Object)rowSegment.startMarker)) {
                return false;
            }
            return Intrinsics.areEqual((Object)this.endMarker, (Object)rowSegment.endMarker);
        }
    }
}

