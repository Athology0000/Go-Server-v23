/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Triple
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function3
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.random.Random
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_1268
 *  net.minecraft.class_1657
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_239
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3965
 *  net.minecraft.class_636
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Triple;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.random.Random;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_636;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.MouseEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.hud.HudAnchor;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.hud.HudElementBuilder;
import org.cobalt.api.hud.HudModuleDSLKt;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.ActionSetting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.InfoSetting;
import org.cobalt.api.module.setting.impl.InfoType;
import org.cobalt.api.module.setting.impl.ModeSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.api.pathfinder.jni.ActionType;
import org.cobalt.api.pathfinder.jni.NativePathfinder;
import org.cobalt.api.pathfinder.jni.PathCommand;
import org.cobalt.api.pathfinder.jni.PathStatus;
import org.cobalt.api.pathfinder.minecraft.MinecraftPathingRules;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ChatUtils;
import org.cobalt.api.util.player.MovementManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.combat.CombatPatrolModule;
import org.cobalt.internal.pathfinding.DebugLog;
import org.cobalt.internal.pathfinding.KillWaypoint;
import org.cobalt.internal.pathfinding.OverlayRenderEngine;
import org.cobalt.internal.pathfinding.PathSplineRenderer;
import org.cobalt.internal.pathfinding.RouteWaypoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00d8\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0002\b\n\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0013\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u000e\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0002\u00a5\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\n\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\tH\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\r\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\fH\u0007\u00a2\u0006\u0004\b\r\u0010\u000eJ-\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u00112\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00110\u0013H\u0002\u00a2\u0006\u0004\b\u0015\u0010\u0016J%\u0010\u0018\u001a\u00020\u00172\u0006\u0010\u0012\u001a\u00020\u00112\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00110\u0013H\u0002\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0015\u0010\u001c\u001a\u00020\u00062\u0006\u0010\u001b\u001a\u00020\u001a\u00a2\u0006\u0004\b\u001c\u0010\u001dJ\r\u0010\u001e\u001a\u00020\u0006\u00a2\u0006\u0004\b\u001e\u0010\u0003J%\u0010#\u001a\u00020\u00062\u0006\u0010 \u001a\u00020\u001f2\u0006\u0010!\u001a\u00020\u001f2\u0006\u0010\"\u001a\u00020\u001f\u00a2\u0006\u0004\b#\u0010$J%\u0010%\u001a\u00020\u00062\u0006\u0010 \u001a\u00020\u001f2\u0006\u0010!\u001a\u00020\u001f2\u0006\u0010\"\u001a\u00020\u001f\u00a2\u0006\u0004\b%\u0010$J\r\u0010&\u001a\u00020\u0006\u00a2\u0006\u0004\b&\u0010\u0003J/\u0010-\u001a\u00020,2\u0006\u0010'\u001a\u00020\u001f2\u0006\u0010(\u001a\u00020\u001f2\u0006\u0010)\u001a\u00020\u001f2\u0006\u0010+\u001a\u00020*H\u0002\u00a2\u0006\u0004\b-\u0010.J\r\u0010/\u001a\u00020\u0006\u00a2\u0006\u0004\b/\u0010\u0003J\u0017\u00101\u001a\u00020\u00062\u0006\u00100\u001a\u00020*H\u0002\u00a2\u0006\u0004\b1\u00102J\r\u00103\u001a\u00020\u0006\u00a2\u0006\u0004\b3\u0010\u0003J\u000f\u00105\u001a\u000204H\u0002\u00a2\u0006\u0004\b5\u00106J\r\u00107\u001a\u00020\u0006\u00a2\u0006\u0004\b7\u0010\u0003J1\u00109\u001a\u00020\u00062\u0006\u0010 \u001a\u00020\u001f2\u0006\u0010!\u001a\u00020\u001f2\u0006\u0010\"\u001a\u00020\u001f2\b\u00108\u001a\u0004\u0018\u00010\u001aH\u0002\u00a2\u0006\u0004\b9\u0010:J\u0017\u0010<\u001a\u00020\u001a2\u0006\u0010;\u001a\u00020\u001fH\u0002\u00a2\u0006\u0004\b<\u0010=J\u0019\u0010>\u001a\u0004\u0018\u00010\u001f2\u0006\u0010;\u001a\u00020\u001aH\u0002\u00a2\u0006\u0004\b>\u0010?J)\u0010@\u001a\u0004\u0018\u00010\u00112\u0006\u0010 \u001a\u00020\u001f2\u0006\u0010!\u001a\u00020\u001f2\u0006\u0010\"\u001a\u00020\u001fH\u0002\u00a2\u0006\u0004\b@\u0010AJ\u001f\u0010C\u001a\u00020\u00062\u0006\u0010B\u001a\u00020\u001a2\u0006\u0010;\u001a\u00020\u001aH\u0002\u00a2\u0006\u0004\bC\u0010DJ'\u0010E\u001a\u00020\u00062\u0006\u0010 \u001a\u00020\u001f2\u0006\u0010!\u001a\u00020\u001f2\u0006\u0010\"\u001a\u00020\u001fH\u0002\u00a2\u0006\u0004\bE\u0010$R\u0014\u0010G\u001a\u00020F8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bG\u0010HR\u0016\u0010I\u001a\u0002048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bI\u0010JR\u0018\u0010L\u001a\u0004\u0018\u00010K8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bL\u0010MR\u001e\u0010N\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bN\u0010OR\u001a\u0010R\u001a\b\u0012\u0004\u0012\u00020Q0P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bR\u0010OR\u001a\u0010S\u001a\b\u0012\u0004\u0012\u00020*0P8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bS\u0010OR\u0016\u0010U\u001a\u00020T8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bU\u0010VR\u0018\u0010W\u001a\u0004\u0018\u00010*8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bW\u0010XR\u0016\u0010Y\u001a\u00020\u00178\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bY\u0010ZR.\u0010\\\u001a\u001a\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020\u001f\u0012\u0004\u0012\u00020\u001f\u0012\u0004\u0012\u00020\u001f0[0\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\\\u0010OR\u0016\u0010]\u001a\u00020\u00178\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b]\u0010ZR\u0014\u0010_\u001a\u00020^8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b_\u0010`R\u0014\u0010a\u001a\u00020^8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\ba\u0010`R\u0014\u0010b\u001a\u00020^8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bb\u0010`R\u0014\u0010c\u001a\u00020^8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bc\u0010`R\u0014\u0010d\u001a\u00020^8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bd\u0010`R\u0014\u0010e\u001a\u00020^8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\be\u0010`R\u0014\u0010f\u001a\u00020^8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bf\u0010`R\u0017\u0010h\u001a\u00020g8\u0006\u00a2\u0006\f\n\u0004\bh\u0010i\u001a\u0004\bj\u0010kR\u0014\u0010m\u001a\u00020l8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bm\u0010nR\u0014\u0010o\u001a\u00020l8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bo\u0010nR\u0017\u0010q\u001a\u00020p8\u0006\u00a2\u0006\f\n\u0004\bq\u0010r\u001a\u0004\bs\u0010tR\u0017\u0010u\u001a\u00020p8\u0006\u00a2\u0006\f\n\u0004\bu\u0010r\u001a\u0004\bv\u0010tR\u0017\u0010w\u001a\u00020p8\u0006\u00a2\u0006\f\n\u0004\bw\u0010r\u001a\u0004\bx\u0010tR\u0017\u0010y\u001a\u00020p8\u0006\u00a2\u0006\f\n\u0004\by\u0010r\u001a\u0004\bz\u0010tR\u0017\u0010{\u001a\u00020g8\u0006\u00a2\u0006\f\n\u0004\b{\u0010i\u001a\u0004\b|\u0010kR\u0019\u0010~\u001a\u00020}8\u0006\u00a2\u0006\u000e\n\u0004\b~\u0010\u007f\u001a\u0006\b\u0080\u0001\u0010\u0081\u0001R\u001b\u0010\u0082\u0001\u001a\u00020}8\u0006\u00a2\u0006\u000f\n\u0005\b\u0082\u0001\u0010\u007f\u001a\u0006\b\u0083\u0001\u0010\u0081\u0001R\u001a\u0010\u0084\u0001\u001a\u00020g8\u0006\u00a2\u0006\u000e\n\u0005\b\u0084\u0001\u0010i\u001a\u0005\b\u0085\u0001\u0010kR\u0016\u0010\u0086\u0001\u001a\u00020g8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0086\u0001\u0010iR\u0018\u0010\u0088\u0001\u001a\u00030\u0087\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0088\u0001\u0010\u0089\u0001R\u0018\u0010\u008b\u0001\u001a\u00030\u008a\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u008b\u0001\u0010\u008c\u0001R\u0018\u0010\u008d\u0001\u001a\u00030\u008a\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u008d\u0001\u0010\u008c\u0001R\u0016\u0010\u008e\u0001\u001a\u00020g8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u008e\u0001\u0010iR\u0018\u0010\u008f\u0001\u001a\u00030\u008a\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u008f\u0001\u0010\u008c\u0001R\u0018\u0010\u0090\u0001\u001a\u00030\u008a\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0090\u0001\u0010\u008c\u0001R\u0016\u0010\u0091\u0001\u001a\u00020g8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0091\u0001\u0010iR\u0018\u0010\u0092\u0001\u001a\u00030\u008a\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0092\u0001\u0010\u008c\u0001R\u0016\u0010\u0093\u0001\u001a\u00020}8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0093\u0001\u0010\u007fR\u0016\u0010\u0094\u0001\u001a\u00020l8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0094\u0001\u0010nR\u0016\u0010\u0095\u0001\u001a\u00020l8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0095\u0001\u0010nR\u0018\u0010\u0096\u0001\u001a\u00030\u008a\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0096\u0001\u0010\u008c\u0001R\u0018\u0010\u0097\u0001\u001a\u00030\u008a\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0097\u0001\u0010\u008c\u0001R\u001d\u0010\u0099\u0001\u001a\u00030\u0098\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u0099\u0001\u0010\u009a\u0001\u001a\u0006\b\u009b\u0001\u0010\u009c\u0001R\u0013\u0010\u009d\u0001\u001a\u0002048F\u00a2\u0006\u0007\u001a\u0005\b\u009d\u0001\u00106R\u0017\u0010\u009e\u0001\u001a\u00020\u001f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u009e\u0001\u0010\u009f\u0001R\u0017\u0010\u00a0\u0001\u001a\u00020\u001f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00a0\u0001\u0010\u009f\u0001R\u0017\u0010\u00a1\u0001\u001a\u00020\u001f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00a1\u0001\u0010\u009f\u0001R\u0016\u0010\u00a2\u0001\u001a\u00020\u00178\u0002X\u0082T\u00a2\u0006\u0007\n\u0005\b\u00a2\u0001\u0010ZR\u0017\u0010\u00a3\u0001\u001a\u00020\u001f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00a3\u0001\u0010\u009f\u0001R\u0017\u0010\u00a4\u0001\u001a\u00020\u001f8\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u00a4\u0001\u0010\u009f\u0001\u00a8\u0006\u00a6\u0001"}, d2={"Lorg/cobalt/internal/pathfinding/PathfindingModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/MouseEvent$RightClick;", "event", "", "onRightClick", "(Lorg/cobalt/api/event/impl/client/MouseEvent$RightClick;)V", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "Lnet/minecraft/class_1937;", "level", "Lnet/minecraft/class_243;", "playerPos", "", "nodes", "renderJumpGuides", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_243;Ljava/util/List;)V", "", "nearestNodeIndex", "(Lnet/minecraft/class_243;Ljava/util/List;)I", "", "source", "ensureEnabledForAutomation", "(Ljava/lang/String;)V", "startFromSettings", "", "x", "y", "z", "setTargetOnly", "(DDD)V", "startTo", "stopPath", "fromX", "fromY", "fromZ", "Lorg/cobalt/internal/pathfinding/KillWaypoint;", "killWp", "", "buildSubRoute", "(DDDLorg/cobalt/internal/pathfinding/KillWaypoint;)[D", "startPatrol", "target", "navigateToKill", "(Lorg/cobalt/internal/pathfinding/KillWaypoint;)V", "stopPatrol", "", "nativeActive", "()Z", "setTargetAtPlayer", "blockName", "setTarget", "(DDDLjava/lang/String;)V", "value", "formatCoord", "(D)Ljava/lang/String;", "parseCoordinate", "(Ljava/lang/String;)Ljava/lang/Double;", "resolvePathTarget", "(DDD)Lnet/minecraft/class_243;", "axis", "invalidTarget", "(Ljava/lang/String;Ljava/lang/String;)V", "invalidPathTarget", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "moduleOwnsPath", "Z", "Lorg/cobalt/internal/pathfinding/PathSplineRenderer$SplineResult;", "cachedSpline", "Lorg/cobalt/internal/pathfinding/PathSplineRenderer$SplineResult;", "lastNodesRef", "Ljava/util/List;", "", "Lorg/cobalt/internal/pathfinding/RouteWaypoint;", "localRouteWaypoints", "localKillWaypoints", "Lorg/cobalt/internal/pathfinding/PathfindingModule$PatrolState;", "patrolState", "Lorg/cobalt/internal/pathfinding/PathfindingModule$PatrolState;", "currentKillWp", "Lorg/cobalt/internal/pathfinding/KillWaypoint;", "dwellTicksRemaining", "I", "Lkotlin/Triple;", "subRouteWaypoints", "subRouteIndex", "Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "COLOR_PATH_NORMAL", "Lorg/cobalt/internal/pathfinding/OverlayRenderEngine$Color;", "COLOR_PATH_AOTV", "COLOR_HEAD_RAY", "COLOR_JUMP_FILL", "COLOR_JUMP_OUTLINE", "COLOR_JUMP_AIR_OUTLINE", "COLOR_NODE_DOT", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "getEnabled", "()Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "info", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "statusInfo", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "targetX", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "getTargetX", "()Lorg/cobalt/api/module/setting/impl/TextSetting;", "targetY", "getTargetY", "targetZ", "getTargetZ", "targetBlock", "getTargetBlock", "cacheHudEnabled", "getCacheHudEnabled", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "cacheHudRadius", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "getCacheHudRadius", "()Lorg/cobalt/api/module/setting/impl/SliderSetting;", "cacheHudCellSize", "getCacheHudCellSize", "cacheHudShowGrid", "getCacheHudShowGrid", "debugFileLogging", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "aotvSlot", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "startAction", "Lorg/cobalt/api/module/setting/impl/ActionSetting;", "stopAction", "patrolModeEnabled", "recordRouteAction", "clearRouteAction", "recordingKillPoints", "clearKillAction", "killDwellTicks", "routeCountInfo", "killCountInfo", "startPatrolAction", "stopPatrolAction", "Lorg/cobalt/api/hud/HudElement;", "cacheHud", "Lorg/cobalt/api/hud/HudElement;", "getCacheHud", "()Lorg/cobalt/api/hud/HudElement;", "isPatrolActive", "JUMP_GUIDE_MIN_RISE", "D", "JUMP_GUIDE_MAX_RISE", "JUMP_GUIDE_MAX_DISTANCE_SQ", "MAX_JUMP_GUIDES", "NODE_DOT_RADIUS", "NODE_DOT_EYE_HEIGHT", "PatrolState", "cobalt"})
@SourceDebugExtension(value={"SMAP\nPathfindingModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathfindingModule.kt\norg/cobalt/internal/pathfinding/PathfindingModule\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,754:1\n777#2:755\n873#2,2:756\n2469#2,14:759\n2469#2,14:773\n1915#2,2:787\n1#3:758\n*S KotlinDebug\n*F\n+ 1 PathfindingModule.kt\norg/cobalt/internal/pathfinding/PathfindingModule\n*L\n372#1:755\n372#1:756,2\n617#1:759,14\n618#1:773,14\n637#1:787,2\n*E\n"})
public final class PathfindingModule
extends Module {
    @NotNull
    public static final PathfindingModule INSTANCE = new PathfindingModule();
    @NotNull
    private static final class_310 mc;
    private static boolean moduleOwnsPath;
    @Nullable
    private static PathSplineRenderer.SplineResult cachedSpline;
    @Nullable
    private static List<? extends class_243> lastNodesRef;
    @NotNull
    private static final List<RouteWaypoint> localRouteWaypoints;
    @NotNull
    private static final List<KillWaypoint> localKillWaypoints;
    @NotNull
    private static PatrolState patrolState;
    @Nullable
    private static KillWaypoint currentKillWp;
    private static int dwellTicksRemaining;
    @NotNull
    private static List<Triple<Double, Double, Double>> subRouteWaypoints;
    private static int subRouteIndex;
    @NotNull
    private static final OverlayRenderEngine.Color COLOR_PATH_NORMAL;
    @NotNull
    private static final OverlayRenderEngine.Color COLOR_PATH_AOTV;
    @NotNull
    private static final OverlayRenderEngine.Color COLOR_HEAD_RAY;
    @NotNull
    private static final OverlayRenderEngine.Color COLOR_JUMP_FILL;
    @NotNull
    private static final OverlayRenderEngine.Color COLOR_JUMP_OUTLINE;
    @NotNull
    private static final OverlayRenderEngine.Color COLOR_JUMP_AIR_OUTLINE;
    @NotNull
    private static final OverlayRenderEngine.Color COLOR_NODE_DOT;
    @NotNull
    private static final CheckboxSetting enabled;
    @NotNull
    private static final InfoSetting info;
    @NotNull
    private static final InfoSetting statusInfo;
    @NotNull
    private static final TextSetting targetX;
    @NotNull
    private static final TextSetting targetY;
    @NotNull
    private static final TextSetting targetZ;
    @NotNull
    private static final TextSetting targetBlock;
    @NotNull
    private static final CheckboxSetting cacheHudEnabled;
    @NotNull
    private static final SliderSetting cacheHudRadius;
    @NotNull
    private static final SliderSetting cacheHudCellSize;
    @NotNull
    private static final CheckboxSetting cacheHudShowGrid;
    @NotNull
    private static final CheckboxSetting debugFileLogging;
    @NotNull
    private static final ModeSetting aotvSlot;
    @NotNull
    private static final ActionSetting startAction;
    @NotNull
    private static final ActionSetting stopAction;
    @NotNull
    private static final CheckboxSetting patrolModeEnabled;
    @NotNull
    private static final ActionSetting recordRouteAction;
    @NotNull
    private static final ActionSetting clearRouteAction;
    @NotNull
    private static final CheckboxSetting recordingKillPoints;
    @NotNull
    private static final ActionSetting clearKillAction;
    @NotNull
    private static final SliderSetting killDwellTicks;
    @NotNull
    private static final InfoSetting routeCountInfo;
    @NotNull
    private static final InfoSetting killCountInfo;
    @NotNull
    private static final ActionSetting startPatrolAction;
    @NotNull
    private static final ActionSetting stopPatrolAction;
    @NotNull
    private static final HudElement cacheHud;
    private static final double JUMP_GUIDE_MIN_RISE = 0.45;
    private static final double JUMP_GUIDE_MAX_RISE = 1.25;
    private static final double JUMP_GUIDE_MAX_DISTANCE_SQ = 144.0;
    private static final int MAX_JUMP_GUIDES = 3;
    private static final double NODE_DOT_RADIUS = 0.08;
    private static final double NODE_DOT_EYE_HEIGHT = 1.62;

    private PathfindingModule() {
        super("Pathfinding");
    }

    @NotNull
    public final CheckboxSetting getEnabled() {
        return enabled;
    }

    @NotNull
    public final TextSetting getTargetX() {
        return targetX;
    }

    @NotNull
    public final TextSetting getTargetY() {
        return targetY;
    }

    @NotNull
    public final TextSetting getTargetZ() {
        return targetZ;
    }

    @NotNull
    public final TextSetting getTargetBlock() {
        return targetBlock;
    }

    @NotNull
    public final CheckboxSetting getCacheHudEnabled() {
        return cacheHudEnabled;
    }

    @NotNull
    public final SliderSetting getCacheHudRadius() {
        return cacheHudRadius;
    }

    @NotNull
    public final SliderSetting getCacheHudCellSize() {
        return cacheHudCellSize;
    }

    @NotNull
    public final CheckboxSetting getCacheHudShowGrid() {
        return cacheHudShowGrid;
    }

    @NotNull
    public final HudElement getCacheHud() {
        return cacheHud;
    }

    @SubscribeEvent
    public final void onRightClick(@NotNull MouseEvent.RightClick event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)recordingKillPoints.getValue()).booleanValue()) {
            return;
        }
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        class_239 class_2392 = mc.field_1765;
        if (class_2392 == null) {
            return;
        }
        class_239 hit = class_2392;
        if (!(hit instanceof class_3965)) {
            return;
        }
        class_2338 class_23382 = ((class_3965)hit).method_17777();
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"getBlockPos(...)");
        class_2338 pos = class_23382;
        KillWaypoint wp = new KillWaypoint((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 1.0, (double)pos.method_10260() + 0.5);
        localKillWaypoints.add(wp);
        ChatUtils.sendMessage("Kill waypoint recorded (" + localKillWaypoints.size() + " total).");
    }

    /*
     * WARNING - void declaration
     */
    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Object object;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        DebugLog.INSTANCE.setDebugFileEnabled((Boolean)debugFileLogging.getValue());
        routeCountInfo.setValue(localRouteWaypoints.size() + " points");
        killCountInfo.setValue(localKillWaypoints.size() + " points");
        if (!NativePathfinder.INSTANCE.isInitialized()) {
            object = "Not initialized";
        } else {
            PathStatus s = NativePathfinder.INSTANCE.getStatus();
            object = !moduleOwnsPath ? s.name() + " (idle)" : s.name();
        }
        statusInfo.setValue(object);
        if (patrolState != PatrolState.IDLE && !CombatPatrolModule.INSTANCE.getPatrolOwnsPathfinder()) {
            switch (WhenMappings.$EnumSwitchMapping$0[patrolState.ordinal()]) {
                case 1: {
                    PathStatus nativeStatus = NativePathfinder.INSTANCE.getStatus();
                    if (nativeStatus != PathStatus.ARRIVED && nativeStatus != PathStatus.FAILED) break;
                    int n = subRouteIndex;
                    if ((subRouteIndex = n + 1) < subRouteWaypoints.size()) {
                        Triple<Double, Double, Double> triple = subRouteWaypoints.get(subRouteIndex);
                        double wx = ((Number)triple.component1()).doubleValue();
                        double wy = ((Number)triple.component2()).doubleValue();
                        double wz = ((Number)triple.component3()).doubleValue();
                        NativePathfinder.INSTANCE.setTarget(wx, wy, wz);
                        break;
                    }
                    dwellTicksRemaining = (int)((Number)killDwellTicks.getValue()).doubleValue();
                    patrolState = PatrolState.AT_KILL;
                    break;
                }
                case 2: {
                    void $this$filterTo$iv$iv;
                    if ((dwellTicksRemaining += -1) > 0) break;
                    List<KillWaypoint> kills = localKillWaypoints;
                    Iterable $this$filter$iv = kills;
                    boolean $i$f$filter = false;
                    Iterable wy = $this$filter$iv;
                    Collection destination$iv$iv = new ArrayList();
                    boolean $i$f$filterTo = false;
                    for (Object element$iv$iv : $this$filterTo$iv$iv) {
                        KillWaypoint it = (KillWaypoint)element$iv$iv;
                        boolean bl = false;
                        if (!(!Intrinsics.areEqual((Object)it, (Object)currentKillWp))) continue;
                        destination$iv$iv.add(element$iv$iv);
                    }
                    List next = (List)destination$iv$iv;
                    if (next.isEmpty()) {
                        this.stopPatrol();
                        ChatUtils.sendMessage("No other kill waypoints available.");
                        break;
                    }
                    this.navigateToKill((KillWaypoint)CollectionsKt.random((Collection)next, (Random)((Random)Random.Default)));
                    break;
                }
                case 3: {
                    break;
                }
                default: {
                    throw new NoWhenBranchMatchedException();
                }
            }
        }
        if (!((Boolean)enabled.getValue()).booleanValue() || !moduleOwnsPath) {
            return;
        }
        PathCommand cmd = NativePathfinder.INSTANCE.tick();
        if (cmd != null) {
            class_746 player;
            cmd.applyToPlayer();
            if (cmd.getActiveAction() == ActionType.AOTV && (player = PathfindingModule.mc.field_1724) != null) {
                int prevSlot = player.method_31548().method_67532();
                player.method_31548().method_61496(((Number)aotvSlot.getValue()).intValue());
                class_636 class_6362 = PathfindingModule.mc.field_1761;
                if (class_6362 != null) {
                    class_6362.method_2919((class_1657)player, class_1268.field_5808);
                }
                player.method_31548().method_61496(prevSlot);
            }
        } else {
            PathStatus s = NativePathfinder.INSTANCE.getStatus();
            if (s == PathStatus.IDLE || s == PathStatus.ARRIVED || s == PathStatus.FAILED) {
                moduleOwnsPath = false;
                MovementManager.setMovementLock(false);
            } else {
                MovementManager.clearForcedMovement();
            }
        }
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            return;
        }
        class_746 class_7462 = PathfindingModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        class_638 class_6382 = PathfindingModule.mc.field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        OverlayRenderEngine.INSTANCE.clearTag("head-ray");
        PathStatus pfStatus = NativePathfinder.INSTANCE.getStatus();
        if (pfStatus == PathStatus.EXECUTING || pfStatus == PathStatus.REPLANNING || pfStatus == PathStatus.RECOVERING) {
            class_243 class_2432 = player.method_33571();
            Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"getEyePosition(...)");
            class_243 eye = class_2432;
            class_243 class_2433 = player.method_5720();
            Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"getLookAngle(...)");
            class_243 look = class_2433;
            OverlayRenderEngine.addLine$default(OverlayRenderEngine.INSTANCE, (class_1937)level2, eye.field_1352, eye.field_1351, eye.field_1350, eye.field_1352 + look.field_1352 * 5.0, eye.field_1351 + look.field_1351 * 5.0, eye.field_1350 + look.field_1350 * 5.0, COLOR_HEAD_RAY, 1.5f, 1, "head-ray", false, 2048, null);
        }
        OverlayRenderEngine.INSTANCE.clearTag("jump-guide");
        List<class_243> nodes = NativePathfinder.INSTANCE.getCachedPathNodes();
        if (nodes != lastNodesRef) {
            lastNodesRef = nodes;
            if (nodes.size() >= 2) {
                cachedSpline = PathSplineRenderer.INSTANCE.buildSpline(nodes);
            }
        }
        class_1937 class_19372 = (class_1937)level2;
        class_243 class_2434 = player.method_73189();
        Intrinsics.checkNotNullExpressionValue((Object)class_2434, (String)"position(...)");
        this.renderJumpGuides(class_19372, class_2434, nodes);
        PathSplineRenderer.SplineResult splineResult = cachedSpline;
        if (splineResult == null) {
            PathfindingModule $this$onRender_u24lambda_u240 = this;
            boolean bl = false;
            OverlayRenderEngine.INSTANCE.render(event.getContext());
            return;
        }
        PathSplineRenderer.SplineResult spline = splineResult;
        List<class_243> pts = spline.getPoints();
        boolean[] isAv = spline.isAotv();
        OverlayRenderEngine.INSTANCE.clearTag("path-spline");
        int n = pts.size() - 1;
        for (int i = 0; i < n; ++i) {
            class_243 a = pts.get(i);
            class_243 b = pts.get(i + 1);
            OverlayRenderEngine.Color color = isAv[i] ? COLOR_PATH_AOTV : COLOR_PATH_NORMAL;
            OverlayRenderEngine.INSTANCE.addLine((class_1937)level2, a.field_1352, a.field_1351 + 0.05, a.field_1350, b.field_1352, b.field_1351 + 0.05, b.field_1350, color, 2.0f, 3, "path-spline", true);
        }
        OverlayRenderEngine.INSTANCE.clearTag("path-nodes");
        double r = 0.08;
        for (class_243 node : nodes) {
            double cx = node.field_1352 + 0.5;
            double cy = node.field_1351 + 1.62;
            double cz = node.field_1350 + 0.5;
            OverlayRenderEngine.INSTANCE.addBox((class_1937)level2, cx - r, cy - r, cz - r, cx + r, cy + r, cz + r, COLOR_NODE_DOT, null, 1.0f, 3, "path-nodes", true);
        }
        OverlayRenderEngine.INSTANCE.render(event.getContext());
    }

    private final void renderJumpGuides(class_1937 level2, class_243 playerPos, List<? extends class_243> nodes) {
        int n;
        if (nodes.size() < 2) {
            return;
        }
        int nearestIndex = this.nearestNodeIndex(playerPos, nodes);
        if (nearestIndex < 0) {
            return;
        }
        int shown = 0;
        int index = Math.max(1, nearestIndex);
        if (index <= (n = CollectionsKt.getLastIndex(nodes))) {
            while (shown < 3) {
                double dz;
                double dy;
                double dx;
                class_243 previous = nodes.get(index - 1);
                class_243 current = nodes.get(index);
                double rise = current.field_1351 - previous.field_1351;
                if (!(rise < 0.45 || rise > 1.25 || (dx = current.field_1352 - playerPos.field_1352) * dx + (dy = current.field_1351 - playerPos.field_1351) * dy + (dz = current.field_1350 - playerPos.field_1350) * dz > 144.0)) {
                    class_2338 landingBlock;
                    class_2338 landingAir;
                    Intrinsics.checkNotNullExpressionValue((Object)class_2338.method_49637((double)current.field_1352, (double)current.field_1351, (double)current.field_1350), (String)"containing(...)");
                    Intrinsics.checkNotNullExpressionValue((Object)landingAir.method_10074(), (String)"below(...)");
                    double pad = 0.01;
                    OverlayRenderEngine.addBox$default(OverlayRenderEngine.INSTANCE, level2, (double)landingBlock.method_10263() - pad, (double)landingBlock.method_10264() - pad, (double)landingBlock.method_10260() - pad, (double)landingBlock.method_10263() + 1.0 + pad, (double)landingBlock.method_10264() + 1.0 + pad, (double)landingBlock.method_10260() + 1.0 + pad, COLOR_JUMP_FILL, COLOR_JUMP_OUTLINE, 2.0f, 3, "jump-guide", false, 4096, null);
                    OverlayRenderEngine.INSTANCE.outlineBlockColor(level2, landingAir, COLOR_JUMP_AIR_OUTLINE, 3, "jump-guide", 1.8f);
                    ++shown;
                }
                if (index == n) break;
                ++index;
            }
        }
    }

    private final int nearestNodeIndex(class_243 playerPos, List<? extends class_243> nodes) {
        int nearestIndex = -1;
        double nearestDistSq = Double.POSITIVE_INFINITY;
        int n = ((Collection)nodes).size();
        for (int index = 0; index < n; ++index) {
            class_243 node = nodes.get(index);
            double dx = node.field_1352 - playerPos.field_1352;
            double dz = node.field_1350 - playerPos.field_1350;
            double distSq = dx * dx + dz * dz;
            if (!(distSq < nearestDistSq)) continue;
            nearestDistSq = distSq;
            nearestIndex = index;
        }
        return nearestIndex;
    }

    public final boolean isPatrolActive() {
        return patrolState != PatrolState.IDLE;
    }

    public final void ensureEnabledForAutomation(@NotNull String source) {
        Intrinsics.checkNotNullParameter((Object)source, (String)"source");
        if (((Boolean)enabled.getValue()).booleanValue()) {
            return;
        }
        enabled.setValue(true);
        ChatUtils.sendMessage("Pathfinding auto-enabled for " + source + ".");
    }

    public final void startFromSettings() {
        CharSequence charSequence;
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            this.ensureEnabledForAutomation("pathfinding");
        }
        Double d = this.parseCoordinate((String)targetX.getValue());
        if (d == null) {
            this.invalidTarget("X", (String)targetX.getValue());
            return;
        }
        double x = d;
        Double d2 = this.parseCoordinate((String)targetY.getValue());
        if (d2 == null) {
            this.invalidTarget("Y", (String)targetY.getValue());
            return;
        }
        double y = d2;
        Double d3 = this.parseCoordinate((String)targetZ.getValue());
        if (d3 == null) {
            this.invalidTarget("Z", (String)targetZ.getValue());
            return;
        }
        double z = d3;
        class_243 class_2432 = this.resolvePathTarget(x, y, z);
        if (class_2432 == null) {
            this.invalidPathTarget(x, y, z);
            return;
        }
        class_243 resolved = class_2432;
        PathfindingModule pathfindingModule = this;
        double d4 = resolved.field_1352;
        double d5 = resolved.field_1351;
        double d6 = resolved.field_1350;
        CharSequence charSequence2 = (CharSequence)targetBlock.getValue();
        if (StringsKt.isBlank((CharSequence)charSequence2)) {
            double d7 = d6;
            double d8 = d5;
            double d9 = d4;
            PathfindingModule pathfindingModule2 = pathfindingModule;
            boolean bl = false;
            Object var17_11 = null;
            pathfindingModule = pathfindingModule2;
            d4 = d9;
            d5 = d8;
            d6 = d7;
            charSequence = var17_11;
        } else {
            charSequence = charSequence2;
        }
        pathfindingModule.setTarget(d4, d5, d6, (String)charSequence);
        NativePathfinder.INSTANCE.setTarget(resolved.field_1352, resolved.field_1351, resolved.field_1350);
        moduleOwnsPath = true;
        ChatUtils.sendMessage("Pathfinding to " + this.formatCoord(resolved.field_1352) + ", " + this.formatCoord(resolved.field_1351) + ", " + this.formatCoord(resolved.field_1350) + ".");
    }

    public final void setTargetOnly(double x, double y, double z) {
        class_243 resolved = this.resolvePathTarget(x, y, z);
        class_243 class_2432 = resolved;
        if (class_2432 == null) {
            class_2432 = new class_243(x, y, z);
        }
        class_243 target = class_2432;
        this.setTarget(target.field_1352, target.field_1351, target.field_1350, null);
        ChatUtils.sendMessage("Target set to " + this.formatCoord(target.field_1352) + ", " + this.formatCoord(target.field_1351) + ", " + this.formatCoord(target.field_1350) + ".");
    }

    public final void startTo(double x, double y, double z) {
        class_243 class_2432 = this.resolvePathTarget(x, y, z);
        if (class_2432 == null) {
            this.invalidPathTarget(x, y, z);
            return;
        }
        class_243 resolved = class_2432;
        this.setTarget(resolved.field_1352, resolved.field_1351, resolved.field_1350, null);
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            this.ensureEnabledForAutomation("pathfinding");
        }
        NativePathfinder.INSTANCE.setTarget(resolved.field_1352, resolved.field_1351, resolved.field_1350);
        moduleOwnsPath = true;
    }

    public final void stopPath() {
        NativePathfinder.INSTANCE.stop();
        moduleOwnsPath = false;
        MovementManager.setMovementLock(false);
        cachedSpline = null;
        lastNodesRef = null;
        OverlayRenderEngine.INSTANCE.clearTag("path-spline");
        OverlayRenderEngine.INSTANCE.clearTag("path-nodes");
    }

    private final double[] buildSubRoute(double fromX, double fromY, double fromZ, KillWaypoint killWp) {
        List list;
        int i;
        Object v2;
        Object v0;
        List<RouteWaypoint> route = localRouteWaypoints;
        if (route.isEmpty()) {
            double[] dArray = new double[]{fromX, fromY, fromZ, killWp.getX(), killWp.getY(), killWp.getZ()};
            return dArray;
        }
        Iterable $this$minByOrNull$iv = (Iterable)CollectionsKt.getIndices((Collection)route);
        boolean $i$f$minByOrNull = false;
        Iterator iterator$iv = $this$minByOrNull$iv.iterator();
        if (!iterator$iv.hasNext()) {
            v0 = null;
        } else {
            Object minElem$iv = iterator$iv.next();
            if (!iterator$iv.hasNext()) {
                v0 = minElem$iv;
            } else {
                int it = ((Number)minElem$iv).intValue();
                boolean bl = false;
                Comparable minValue$iv = Double.valueOf(PathfindingModule.buildSubRoute$dist3(route.get(it), fromX, fromY, fromZ));
                do {
                    Object e$iv = iterator$iv.next();
                    it = ((Number)e$iv).intValue();
                    bl = false;
                    Comparable v$iv = Double.valueOf(PathfindingModule.buildSubRoute$dist3(route.get(it), fromX, fromY, fromZ));
                    if (minValue$iv.compareTo(v$iv) <= 0) continue;
                    minElem$iv = e$iv;
                    minValue$iv = v$iv;
                } while (iterator$iv.hasNext());
                v0 = minElem$iv;
            }
        }
        Integer n = v0;
        int j = n != null ? n : 0;
        Iterable $this$minByOrNull$iv2 = (Iterable)CollectionsKt.getIndices((Collection)route);
        boolean $i$f$minByOrNull2 = false;
        Object[] iterator$iv2 = $this$minByOrNull$iv2.iterator();
        if (!iterator$iv2.hasNext()) {
            v2 = null;
        } else {
            Object minElem$iv = iterator$iv2.next();
            if (!iterator$iv2.hasNext()) {
                v2 = minElem$iv;
            } else {
                int it = ((Number)minElem$iv).intValue();
                boolean bl = false;
                Comparable minValue$iv = Double.valueOf(PathfindingModule.buildSubRoute$dist3(route.get(it), killWp.getX(), killWp.getY(), killWp.getZ()));
                do {
                    Object e$iv = iterator$iv2.next();
                    int it2 = ((Number)e$iv).intValue();
                    $i$a$-minByOrNull-PathfindingModule$buildSubRoute$i$1 = false;
                    Comparable v$iv = Double.valueOf(PathfindingModule.buildSubRoute$dist3(route.get(it2), killWp.getX(), killWp.getY(), killWp.getZ()));
                    if (minValue$iv.compareTo(v$iv) <= 0) continue;
                    minElem$iv = e$iv;
                    minValue$iv = v$iv;
                } while (iterator$iv2.hasNext());
                v2 = minElem$iv;
            }
        }
        Integer n2 = v2;
        int n3 = i = n2 != null ? n2 : 0;
        if (j == i) {
            list = CollectionsKt.emptyList();
        } else {
            List fwd = j <= i ? route.subList(j, i + 1) : CollectionsKt.emptyList();
            List bwd = i <= j ? CollectionsKt.reversed((Iterable)route.subList(i, j + 1)) : CollectionsKt.emptyList();
            list = fwd.isEmpty() ? bwd : (bwd.isEmpty() ? fwd : (fwd.size() <= bwd.size() ? fwd : bwd));
        }
        List slice = list;
        List points = new ArrayList();
        Collection bwd = points;
        iterator$iv2 = new Double[]{fromX, fromY, fromZ};
        CollectionsKt.addAll((Collection)bwd, (Iterable)CollectionsKt.listOf((Object[])iterator$iv2));
        Iterable $this$forEach$iv = slice;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            RouteWaypoint wp = (RouteWaypoint)element$iv;
            boolean bl = false;
            Collection collection = points;
            Object[] objectArray = new Double[]{wp.getX(), wp.getY(), wp.getZ()};
            CollectionsKt.addAll((Collection)collection, (Iterable)CollectionsKt.listOf((Object[])objectArray));
        }
        Collection collection = points;
        Object[] objectArray = new Double[]{killWp.getX(), killWp.getY(), killWp.getZ()};
        CollectionsKt.addAll((Collection)collection, (Iterable)CollectionsKt.listOf((Object[])objectArray));
        return CollectionsKt.toDoubleArray((Collection)points);
    }

    public final void startPatrol() {
        if (patrolState != PatrolState.IDLE) {
            this.stopPatrol();
        }
        if (localKillWaypoints.size() < 2) {
            ChatUtils.sendMessage("Need at least 2 kill waypoints to patrol. Use 'Record Kill Points'.");
            return;
        }
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            this.ensureEnabledForAutomation("patrol");
        }
        KillWaypoint first = (KillWaypoint)CollectionsKt.random((Collection)localKillWaypoints, (Random)((Random)Random.Default));
        this.navigateToKill(first);
    }

    private final void navigateToKill(KillWaypoint target) {
        class_746 class_7462 = PathfindingModule.mc.field_1724;
        if (class_7462 == null) {
            PathfindingModule $this$navigateToKill_u24lambda_u240 = this;
            boolean bl = false;
            patrolState = PatrolState.IDLE;
            return;
        }
        class_746 player = class_7462;
        currentKillWp = target;
        double[] flat = this.buildSubRoute(player.method_23317(), player.method_23318(), player.method_23321(), target);
        List wps = new ArrayList();
        int i = 3;
        while (i + 2 < flat.length) {
            wps.add(new Triple((Object)flat[i], (Object)flat[i + 1], (Object)flat[i + 2]));
            i += 3;
        }
        if (i + 2 == flat.length) {
            wps.add(new Triple((Object)flat[i], (Object)flat[i + 1], (Object)flat[i + 2]));
        }
        subRouteWaypoints = wps;
        subRouteIndex = 0;
        if (wps.isEmpty()) {
            dwellTicksRemaining = (int)((Number)killDwellTicks.getValue()).doubleValue();
            patrolState = PatrolState.AT_KILL;
            return;
        }
        Triple triple = (Triple)wps.get(0);
        double wx = ((Number)triple.component1()).doubleValue();
        double wy = ((Number)triple.component2()).doubleValue();
        double wz = ((Number)triple.component3()).doubleValue();
        NativePathfinder.INSTANCE.setTarget(wx, wy, wz);
        moduleOwnsPath = true;
        patrolState = PatrolState.NAVIGATING;
        ChatUtils.sendMessage("Patrolling to kill spot (" + (localKillWaypoints.indexOf(target) + 1) + "/" + localKillWaypoints.size() + ").");
    }

    public final void stopPatrol() {
        patrolState = PatrolState.IDLE;
        this.stopPath();
    }

    private final boolean nativeActive() {
        PathStatus s = NativePathfinder.INSTANCE.getStatus();
        return s != PathStatus.IDLE && s != PathStatus.ARRIVED && s != PathStatus.FAILED;
    }

    public final void setTargetAtPlayer() {
        class_638 class_6382;
        block5: {
            class_746 player;
            block4: {
                class_746 class_7462 = PathfindingModule.mc.field_1724;
                if (class_7462 == null) {
                    return;
                }
                player = class_7462;
                class_6382 = PathfindingModule.mc.field_1687;
                if (class_6382 == null) break block4;
                class_638 level2 = class_6382;
                boolean bl = false;
                class_1937 class_19372 = (class_1937)level2;
                class_2338 class_23382 = player.method_24515();
                Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"blockPosition(...)");
                class_2338 class_23383 = MinecraftPathingRules.INSTANCE.walkableAt(class_19372, class_23382);
                class_6382 = class_23383;
                if (class_23383 != null) break block5;
            }
            class_2338 class_23384 = player.method_24515();
            class_6382 = class_23384;
            Intrinsics.checkNotNullExpressionValue((Object)class_23384, (String)"blockPosition(...)");
        }
        class_638 targetPos = class_6382;
        double x = (double)targetPos.method_10263() + 0.5;
        double y = targetPos.method_10264();
        double z = (double)targetPos.method_10260() + 0.5;
        this.setTarget(x, y, z, "player");
        ChatUtils.sendMessage("Target set to " + this.formatCoord(x) + ", " + this.formatCoord(y) + ", " + this.formatCoord(z) + ".");
    }

    private final void setTarget(double x, double y, double z, String blockName) {
        targetX.setValue(this.formatCoord(x));
        targetY.setValue(this.formatCoord(y));
        targetZ.setValue(this.formatCoord(z));
        if (blockName != null) {
            targetBlock.setValue(blockName);
        }
    }

    private final String formatCoord(double value) {
        String string;
        int intVal = (int)value;
        if (value == (double)intVal) {
            string = String.valueOf(intVal);
        } else {
            Locale locale = Locale.US;
            String string2 = "%.3f";
            Object[] objectArray = new Object[]{value};
            String string3 = String.format(locale, string2, Arrays.copyOf(objectArray, objectArray.length));
            string = string3;
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"format(...)");
        }
        return string;
    }

    private final Double parseCoordinate(String value) {
        return StringsKt.toDoubleOrNull((String)((Object)StringsKt.trim((CharSequence)value)).toString());
    }

    private final class_243 resolvePathTarget(double x, double y, double z) {
        class_638 class_6382 = PathfindingModule.mc.field_1687;
        if (class_6382 == null) {
            return new class_243(x, y, z);
        }
        class_638 level2 = class_6382;
        class_2338 class_23382 = class_2338.method_49637((double)x, (double)y, (double)z);
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"containing(...)");
        class_2338 raw = class_23382;
        class_2338 class_23383 = MinecraftPathingRules.INSTANCE.resolveTarget((class_1937)level2, raw);
        if (class_23383 == null) {
            return null;
        }
        class_2338 resolved = class_23383;
        return new class_243((double)resolved.method_10263() + 0.5, (double)resolved.method_10264(), (double)resolved.method_10260() + 0.5);
    }

    private final void invalidTarget(String axis, String value) {
        ChatUtils.sendMessage("Invalid " + axis + " coordinate: \"" + value + "\"");
    }

    private final void invalidPathTarget(double x, double y, double z) {
        ChatUtils.sendMessage("Target is not walkable: " + this.formatCoord(x) + ", " + this.formatCoord(y) + ", " + this.formatCoord(z));
    }

    private static final Unit startAction$lambda$0() {
        INSTANCE.startFromSettings();
        return Unit.INSTANCE;
    }

    private static final Unit stopAction$lambda$0() {
        INSTANCE.stopPath();
        return Unit.INSTANCE;
    }

    private static final Unit recordRouteAction$lambda$0() {
        class_746 class_7462 = PathfindingModule.mc.field_1724;
        if (class_7462 == null) {
            return Unit.INSTANCE;
        }
        class_746 player = class_7462;
        localRouteWaypoints.add(new RouteWaypoint(player.method_23317(), player.method_23318(), player.method_23321()));
        ChatUtils.sendMessage("Route point recorded (" + localRouteWaypoints.size() + " total).");
        return Unit.INSTANCE;
    }

    private static final Unit clearRouteAction$lambda$0() {
        localRouteWaypoints.clear();
        ChatUtils.sendMessage("Route cleared.");
        return Unit.INSTANCE;
    }

    private static final Unit clearKillAction$lambda$0() {
        localKillWaypoints.clear();
        ChatUtils.sendMessage("Kill waypoints cleared.");
        return Unit.INSTANCE;
    }

    private static final Unit startPatrolAction$lambda$0() {
        INSTANCE.startPatrol();
        return Unit.INSTANCE;
    }

    private static final Unit stopPatrolAction$lambda$0() {
        INSTANCE.stopPatrol();
        return Unit.INSTANCE;
    }

    private static final float cacheHud$lambda$0$0() {
        int radius = RangesKt.coerceAtLeast((int)((int)((Number)cacheHudRadius.getValue()).doubleValue()), (int)1);
        float cell = (float)((Number)cacheHudCellSize.getValue()).doubleValue();
        return (float)(radius * 2 + 1) * cell + 8.0f;
    }

    private static final float cacheHud$lambda$0$1() {
        int radius = RangesKt.coerceAtLeast((int)((int)((Number)cacheHudRadius.getValue()).doubleValue()), (int)1);
        float cell = (float)((Number)cacheHudCellSize.getValue()).doubleValue();
        return (float)(radius * 2 + 1) * cell + 8.0f;
    }

    private static final Unit cacheHud$lambda$0$2(float screenX, float screenY, float f) {
        if (!((Boolean)cacheHudEnabled.getValue()).booleanValue()) {
            return Unit.INSTANCE;
        }
        class_746 class_7462 = PathfindingModule.mc.field_1724;
        if (class_7462 == null) {
            return Unit.INSTANCE;
        }
        class_746 player = class_7462;
        class_638 class_6382 = PathfindingModule.mc.field_1687;
        if (class_6382 == null) {
            return Unit.INSTANCE;
        }
        class_638 level2 = class_6382;
        int radius = RangesKt.coerceAtLeast((int)((int)((Number)cacheHudRadius.getValue()).doubleValue()), (int)1);
        float cell = (float)((Number)cacheHudCellSize.getValue()).doubleValue();
        int size = radius * 2 + 1;
        float mapW = (float)size * cell;
        float mapH = (float)size * cell;
        NVGRenderer.rect(screenX, screenY, mapW + 8.0f, mapH + 8.0f, ThemeManager.INSTANCE.getCurrentTheme().getPanel(), 6.0f);
        float originX = screenX + 4.0f;
        float originY = screenY + 4.0f;
        int centerChunkX = player.method_31477() >> 4;
        int centerChunkZ = player.method_31479() >> 4;
        int dz = -radius;
        if (dz <= radius) {
            while (true) {
                int dx;
                if ((dx = -radius) <= radius) {
                    while (true) {
                        int chunkX = centerChunkX + dx;
                        int chunkZ = centerChunkZ + dz;
                        boolean cached = MinecraftPathingRules.isChunkCached$default(MinecraftPathingRules.INSTANCE, (class_1937)level2, chunkX, chunkZ, 0L, 8, null);
                        int color = dx == 0 && dz == 0 ? ThemeManager.INSTANCE.getCurrentTheme().getAccentSecondary() : (cached ? ThemeManager.INSTANCE.getCurrentTheme().getAccent() : ThemeManager.INSTANCE.getCurrentTheme().getOverlay());
                        float x = originX + (float)(dx + radius) * cell;
                        float y = originY + (float)(dz + radius) * cell;
                        NVGRenderer.rect(x, y, cell - 1.0f, cell - 1.0f, color);
                        if (dx == radius) break;
                        ++dx;
                    }
                }
                if (dz == radius) break;
                ++dz;
            }
        }
        if (((Boolean)cacheHudShowGrid.getValue()).booleanValue()) {
            NVGRenderer.hollowRect(originX - 0.5f, originY - 0.5f, mapW + 1.0f, mapH + 1.0f, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getModuleDivider(), 4.0f);
        }
        return Unit.INSTANCE;
    }

    private static final Unit cacheHud$lambda$0(HudElementBuilder $this$hudElement) {
        Intrinsics.checkNotNullParameter((Object)$this$hudElement, (String)"$this$hudElement");
        $this$hudElement.setAnchor(HudAnchor.TOP_RIGHT);
        $this$hudElement.setOffsetX(-12.0f);
        $this$hudElement.setOffsetY(12.0f);
        $this$hudElement.width((Function0<Float>)((Function0)PathfindingModule::cacheHud$lambda$0$0));
        $this$hudElement.height((Function0<Float>)((Function0)PathfindingModule::cacheHud$lambda$0$1));
        $this$hudElement.render((Function3<? super Float, ? super Float, ? super Float, Unit>)((Function3)PathfindingModule::cacheHud$lambda$0$2));
        return Unit.INSTANCE;
    }

    private static final double buildSubRoute$dist3(RouteWaypoint wp, double x, double y, double z) {
        double dx = wp.getX() - x;
        double dy = wp.getY() - y;
        double dz = wp.getZ() - z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        localRouteWaypoints = new ArrayList();
        localKillWaypoints = new ArrayList();
        patrolState = PatrolState.IDLE;
        subRouteWaypoints = CollectionsKt.emptyList();
        COLOR_PATH_NORMAL = new OverlayRenderEngine.Color(0, 200, 255, 220);
        COLOR_PATH_AOTV = new OverlayRenderEngine.Color(255, 160, 0, 220);
        COLOR_HEAD_RAY = new OverlayRenderEngine.Color(255, 255, 255, 200);
        COLOR_JUMP_FILL = new OverlayRenderEngine.Color(0, 200, 255, 70);
        COLOR_JUMP_OUTLINE = new OverlayRenderEngine.Color(0, 200, 255, 255);
        COLOR_JUMP_AIR_OUTLINE = new OverlayRenderEngine.Color(140, 210, 255, 220);
        COLOR_NODE_DOT = new OverlayRenderEngine.Color(0, 200, 255, 180);
        enabled = new CheckboxSetting("Enabled", "Enable pathfinding target selection and commands.", false);
        info = new InfoSetting("Target", "Use /cobalt setpos or /cobalt setposhere to set the target.", InfoType.INFO);
        statusInfo = new InfoSetting("Pathfinder Status", "Live status of the native pathfinder engine.", InfoType.INFO);
        targetX = new TextSetting("Target X", "Target X coordinate.", "0");
        targetY = new TextSetting("Target Y", "Target Y coordinate.", "0");
        targetZ = new TextSetting("Target Z", "Target Z coordinate.", "0");
        targetBlock = new TextSetting("Target Block", "Filled from right-click (informational).", "");
        cacheHudEnabled = new CheckboxSetting("Cache HUD", "Show cached chunk map HUD.", false);
        cacheHudRadius = new SliderSetting("Cache Radius", "Chunk radius shown in the cache HUD.", 4.0, 1.0, 12.0, 0.0, 32, null);
        cacheHudCellSize = new SliderSetting("Cache Cell Size", "Cell size for the cache HUD (pixels).", 8.0, 4.0, 16.0, 0.0, 32, null);
        cacheHudShowGrid = new CheckboxSetting("Cache Grid", "Show grid lines on the cache HUD.", true);
        debugFileLogging = new CheckboxSetting("Debug File Logs", "Write path/rotation debug logs to file.", false);
        Object[] objectArray = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        aotvSlot = new ModeSetting("AOTV Slot", "Hotbar slot (1-9) holding your AOTV item.", 0, (String[])objectArray);
        startAction = new ActionSetting("Start", "Start pathfinding to the target coordinates.", "Start", null, PathfindingModule::startAction$lambda$0, 8, null);
        stopAction = new ActionSetting("Stop", "Stop the current path.", "Stop", null, PathfindingModule::stopAction$lambda$0, 8, null);
        patrolModeEnabled = new CheckboxSetting("Patrol Mode", "Randomly patrol between kill waypoints using the recorded route.", false);
        recordRouteAction = new ActionSetting("Record Route Point", "Appends your current position to the patrol route.", "Record", null, PathfindingModule::recordRouteAction$lambda$0, 8, null);
        clearRouteAction = new ActionSetting("Clear Route", "Removes all recorded route waypoints.", "Clear", null, PathfindingModule::clearRouteAction$lambda$0, 8, null);
        recordingKillPoints = new CheckboxSetting("Record Kill Points", "Right-click a block to record a kill waypoint while this is enabled.", false);
        clearKillAction = new ActionSetting("Clear Kill Points", "Removes all recorded kill waypoints.", "Clear", null, PathfindingModule::clearKillAction$lambda$0, 8, null);
        killDwellTicks = new SliderSetting("Kill Dwell Ticks", "Ticks to wait at a kill spot before moving to the next.", 20.0, 0.0, 100.0, 0.0, 32, null);
        routeCountInfo = new InfoSetting("Route Points", "Number of recorded route waypoints.", InfoType.INFO);
        killCountInfo = new InfoSetting("Kill Points", "Number of recorded kill waypoints.", InfoType.INFO);
        startPatrolAction = new ActionSetting("Start Patrol", "Start patrolling between kill waypoints.", "Start Patrol", null, PathfindingModule::startPatrolAction$lambda$0, 8, null);
        stopPatrolAction = new ActionSetting("Stop Patrol", "Stop the patrol loop.", "Stop Patrol", null, PathfindingModule::stopPatrolAction$lambda$0, 8, null);
        cacheHud = HudModuleDSLKt.hudElement(INSTANCE, "path-cache-hud", "Cache Map", "Shows cached chunks around you.", (Function1<? super HudElementBuilder, Unit>)((Function1)PathfindingModule::cacheHud$lambda$0));
        objectArray = new Setting[25];
        objectArray[0] = enabled;
        objectArray[1] = info;
        objectArray[2] = statusInfo;
        objectArray[3] = targetX;
        objectArray[4] = targetY;
        objectArray[5] = targetZ;
        objectArray[6] = targetBlock;
        objectArray[7] = cacheHudEnabled;
        objectArray[8] = cacheHudRadius;
        objectArray[9] = cacheHudCellSize;
        objectArray[10] = cacheHudShowGrid;
        objectArray[11] = debugFileLogging;
        objectArray[12] = aotvSlot;
        objectArray[13] = startAction;
        objectArray[14] = stopAction;
        objectArray[15] = patrolModeEnabled;
        objectArray[16] = recordRouteAction;
        objectArray[17] = clearRouteAction;
        objectArray[18] = recordingKillPoints;
        objectArray[19] = clearKillAction;
        objectArray[20] = killDwellTicks;
        objectArray[21] = routeCountInfo;
        objectArray[22] = killCountInfo;
        objectArray[23] = startPatrolAction;
        objectArray[24] = stopPatrolAction;
        INSTANCE.addSetting((Setting<?>[])objectArray);
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/internal/pathfinding/PathfindingModule$PatrolState;", "", "<init>", "(Ljava/lang/String;I)V", "IDLE", "NAVIGATING", "AT_KILL", "cobalt"})
    private static final class PatrolState
    extends Enum<PatrolState> {
        public static final /* enum */ PatrolState IDLE = new PatrolState();
        public static final /* enum */ PatrolState NAVIGATING = new PatrolState();
        public static final /* enum */ PatrolState AT_KILL = new PatrolState();
        private static final /* synthetic */ PatrolState[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static PatrolState[] values() {
            return (PatrolState[])$VALUES.clone();
        }

        public static PatrolState valueOf(String value) {
            return Enum.valueOf(PatrolState.class, value);
        }

        @NotNull
        public static EnumEntries<PatrolState> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = patrolStateArray = new PatrolState[]{PatrolState.IDLE, PatrolState.NAVIGATING, PatrolState.AT_KILL};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[PatrolState.values().length];
            try {
                nArray[PatrolState.NAVIGATING.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[PatrolState.AT_KILL.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[PatrolState.IDLE.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

