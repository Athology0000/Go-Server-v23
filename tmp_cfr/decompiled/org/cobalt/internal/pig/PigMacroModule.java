/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
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
 *  kotlin.sequences.Sequence
 *  kotlin.sequences.SequencesKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_124
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1531
 *  net.minecraft.class_1657
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2382
 *  net.minecraft.class_243
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.pig;

import java.lang.invoke.LambdaMetafactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
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
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.StringsKt;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.ChatEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.hud.HudAnchor;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.hud.HudElementBuilder;
import org.cobalt.api.hud.HudModuleDSLKt;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.InfoSetting;
import org.cobalt.api.module.setting.impl.InfoType;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.module.setting.impl.TextSetting;
import org.cobalt.api.pathfinder.jni.NativePathfinder;
import org.cobalt.api.pathfinder.jni.PathStatus;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.AngleUtils;
import org.cobalt.api.util.ChatUtils;
import org.cobalt.api.util.InventoryUtils;
import org.cobalt.api.util.helper.Rotation;
import org.cobalt.api.util.player.MovementManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Gradient;
import org.cobalt.internal.pathfinding.OverlayRenderEngine;
import org.cobalt.internal.pig.PigMacroModule$onTick$;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00c8\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010#\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0004\u008d\u0001\u008e\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\n\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\tH\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\r\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\fH\u0007\u00a2\u0006\u0004\b\r\u0010\u000eJ/\u0010\u0015\u001a\u00020\u00112\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u001f\u0010\u001a\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00172\u0006\u0010\u0019\u001a\u00020\u0017H\u0002\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u0011\u0010\u001d\u001a\u0004\u0018\u00010\u001cH\u0002\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\u0017\u0010!\u001a\u00020 2\u0006\u0010\u001f\u001a\u00020\u001cH\u0002\u00a2\u0006\u0004\b!\u0010\"J\u0017\u0010%\u001a\u00020\u00062\u0006\u0010$\u001a\u00020#H\u0002\u00a2\u0006\u0004\b%\u0010&J\u0017\u0010(\u001a\u00020\u00062\u0006\u0010\u001f\u001a\u00020'H\u0002\u00a2\u0006\u0004\b(\u0010)J\u0017\u0010,\u001a\u00020\u00062\u0006\u0010+\u001a\u00020*H\u0002\u00a2\u0006\u0004\b,\u0010-J/\u00100\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010.\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u00112\u0006\u0010/\u001a\u00020*H\u0002\u00a2\u0006\u0004\b0\u00101J\u000f\u00102\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b2\u0010\u0003J\u000f\u00103\u001a\u00020 H\u0002\u00a2\u0006\u0004\b3\u00104J\u0019\u00107\u001a\u00020\u00062\b\u00106\u001a\u0004\u0018\u000105H\u0002\u00a2\u0006\u0004\b7\u00108J\u000f\u00109\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b9\u0010\u0003J\u000f\u0010:\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b:\u0010\u0003J\u0017\u0010=\u001a\u0002052\u0006\u0010<\u001a\u00020;H\u0002\u00a2\u0006\u0004\b=\u0010>R\u0014\u0010@\u001a\u00020?8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b@\u0010AR\u0011\u0010B\u001a\u00020 8F\u00a2\u0006\u0006\u001a\u0004\bB\u00104R\u0016\u0010C\u001a\u00020#8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bC\u0010DR\u0016\u0010E\u001a\u00020 8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bE\u0010FR\u0018\u0010G\u001a\u0004\u0018\u00010\u001c8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bG\u0010HR\u0018\u0010I\u001a\u0004\u0018\u00010*8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bI\u0010JR\u001a\u0010L\u001a\b\u0012\u0004\u0012\u00020\u00170K8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bL\u0010MR\u0016\u0010O\u001a\u00020N8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bO\u0010PR\u0016\u0010Q\u001a\u00020\u00178\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bQ\u0010RR\u0016\u0010S\u001a\u00020\u00178\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bS\u0010RR\u0016\u0010T\u001a\u00020\u00178\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bT\u0010RR\u0016\u0010U\u001a\u00020\u00178\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bU\u0010RR\u0016\u0010V\u001a\u00020\u00178\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bV\u0010RR\u0016\u0010W\u001a\u00020\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bW\u0010XR\u0016\u0010Y\u001a\u00020\u00178\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bY\u0010RR\u0018\u0010[\u001a\u0004\u0018\u00010Z8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b[\u0010\\R\u0018\u0010]\u001a\u0004\u0018\u00010Z8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b]\u0010\\R\u0016\u0010^\u001a\u00020\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b^\u0010XR\u0018\u0010_\u001a\u0004\u0018\u00010*8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b_\u0010JR\u0016\u0010`\u001a\u00020\u00178\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b`\u0010RR\u0016\u0010a\u001a\u00020\u00178\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\ba\u0010RR\u0014\u0010b\u001a\u0002058\u0002X\u0082D\u00a2\u0006\u0006\n\u0004\bb\u0010cR\u0016\u0010d\u001a\u00020;8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bd\u0010eR\u0016\u0010f\u001a\u00020\u00178\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bf\u0010RR\u0014\u0010h\u001a\u00020g8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bh\u0010iR\u0014\u0010k\u001a\u00020j8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bk\u0010lR\u0014\u0010n\u001a\u00020m8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bn\u0010oR\u0014\u0010p\u001a\u00020m8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bp\u0010oR\u0014\u0010q\u001a\u00020m8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bq\u0010oR\u0014\u0010r\u001a\u00020m8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\br\u0010oR\u0014\u0010s\u001a\u00020m8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bs\u0010oR\u0014\u0010u\u001a\u00020t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bu\u0010vR\u0014\u0010w\u001a\u00020t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bw\u0010vR\u0014\u0010x\u001a\u00020t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bx\u0010vR\u0014\u0010y\u001a\u00020t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\by\u0010vR\u0014\u0010z\u001a\u00020t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bz\u0010vR\u0014\u0010{\u001a\u00020t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b{\u0010vR\u0014\u0010|\u001a\u00020t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b|\u0010vR\u0014\u0010}\u001a\u00020t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b}\u0010vR\u0014\u0010~\u001a\u00020t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b~\u0010vR\u0014\u0010\u007f\u001a\u00020t8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u007f\u0010vR\u0016\u0010\u0080\u0001\u001a\u00020g8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0080\u0001\u0010iR\u0016\u0010\u0081\u0001\u001a\u00020g8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0081\u0001\u0010iR\u0018\u0010\u0083\u0001\u001a\u00030\u0082\u00018\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u0083\u0001\u0010\u0084\u0001R\u0018\u0010\u0085\u0001\u001a\u00030\u0082\u00018\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u0085\u0001\u0010\u0084\u0001R\u0018\u0010\u0086\u0001\u001a\u00030\u0082\u00018\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u0086\u0001\u0010\u0084\u0001R\u0018\u0010\u0087\u0001\u001a\u00030\u0082\u00018\u0002X\u0082T\u00a2\u0006\b\n\u0006\b\u0087\u0001\u0010\u0084\u0001R\u001d\u0010\u0089\u0001\u001a\u00030\u0088\u00018\u0006\u00a2\u0006\u0010\n\u0006\b\u0089\u0001\u0010\u008a\u0001\u001a\u0006\b\u008b\u0001\u0010\u008c\u0001\u00a8\u0006\u008f\u0001"}, d2={"Lorg/cobalt/internal/pig/PigMacroModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "event", "", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lorg/cobalt/api/event/impl/client/ChatEvent$Receive;", "onChat", "(Lorg/cobalt/api/event/impl/client/ChatEvent$Receive;)V", "Lnet/minecraft/class_1937;", "level", "", "x", "z", "fromY", "floorYAt", "(Lnet/minecraft/class_1937;DDD)D", "", "min", "max", "humanDelay", "(II)I", "Lnet/minecraft/class_1309;", "livePig", "()Lnet/minecraft/class_1309;", "entity", "", "isPig", "(Lnet/minecraft/class_1309;)Z", "Lorg/cobalt/internal/pig/PigMacroModule$State;", "newState", "transition", "(Lorg/cobalt/internal/pig/PigMacroModule$State;)V", "Lnet/minecraft/class_1297;", "faceEntity", "(Lnet/minecraft/class_1297;)V", "Lnet/minecraft/class_243;", "target", "facePos", "(Lnet/minecraft/class_243;)V", "y", "v", "dist3", "(DDDLnet/minecraft/class_243;)D", "start", "nativeActive", "()Z", "", "reason", "nativeStop", "(Ljava/lang/String;)V", "stop", "releaseKeys", "", "ms", "formatDuration", "(J)Ljava/lang/String;", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "isActive", "state", "Lorg/cobalt/internal/pig/PigMacroModule$State;", "wasEnabled", "Z", "targetPig", "Lnet/minecraft/class_1309;", "orbAnchor", "Lnet/minecraft/class_243;", "", "waterBlacklist", "Ljava/util/Set;", "Lorg/cobalt/internal/pig/PigMacroModule$HerdPhase;", "herdPhase", "Lorg/cobalt/internal/pig/PigMacroModule$HerdPhase;", "walkTicks", "I", "ticksSinceRod", "actionTicks", "actionDelay", "stateTimeout", "lastPigOrbDist", "D", "pigStuckTicks", "Lnet/minecraft/class_2338;", "lastBehindPathPos", "Lnet/minecraft/class_2338;", "lastSprintToPigPos", "stuckAngleOffsetDeg", "pigPrevPos", "pigLandWaitTicks", "walkGraceTicks", "HERDING_TAG", "Ljava/lang/String;", "sessionStartMs", "J", "captureCount", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabledSetting", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "infoSetting", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "statusSetting", "Lorg/cobalt/api/module/setting/impl/TextSetting;", "pigKeywordSetting", "shinyOrbKeywordSetting", "shinyRodKeywordSetting", "aotvKeywordSetting", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "searchRangeSetting", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "deployRangeSetting", "behindDistanceSetting", "aotvHoldTicksSetting", "rodIntervalSetting", "stuckThresholdSetting", "collectRangeSetting", "waitBetweenSetting", "otherPlayerExclusionSetting", "abandonDistanceSetting", "autoLoopSetting", "walkModeSetting", "", "W", "F", "H", "CORNER", "PAD", "Lorg/cobalt/api/hud/HudElement;", "pigHud", "Lorg/cobalt/api/hud/HudElement;", "getPigHud", "()Lorg/cobalt/api/hud/HudElement;", "State", "HerdPhase", "cobalt"})
@SourceDebugExtension(value={"SMAP\nPigMacroModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PigMacroModule.kt\norg/cobalt/internal/pig/PigMacroModule\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,1000:1\n1#2:1001\n479#3:1002\n1944#3,14:1003\n479#3:1017\n1944#3,14:1018\n1944#3,14:1032\n2792#4,3:1046\n*S KotlinDebug\n*F\n+ 1 PigMacroModule.kt\norg/cobalt/internal/pig/PigMacroModule\n*L\n244#1:1002\n253#1:1003,14\n258#1:1017\n266#1:1018,14\n779#1:1032,14\n248#1:1046,3\n*E\n"})
public final class PigMacroModule
extends Module {
    @NotNull
    public static final PigMacroModule INSTANCE = new PigMacroModule();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static State state;
    private static boolean wasEnabled;
    @Nullable
    private static class_1309 targetPig;
    @Nullable
    private static class_243 orbAnchor;
    @NotNull
    private static final Set<Integer> waterBlacklist;
    @NotNull
    private static HerdPhase herdPhase;
    private static int walkTicks;
    private static int ticksSinceRod;
    private static int actionTicks;
    private static int actionDelay;
    private static int stateTimeout;
    private static double lastPigOrbDist;
    private static int pigStuckTicks;
    @Nullable
    private static class_2338 lastBehindPathPos;
    @Nullable
    private static class_2338 lastSprintToPigPos;
    private static double stuckAngleOffsetDeg;
    @Nullable
    private static class_243 pigPrevPos;
    private static int pigLandWaitTicks;
    private static int walkGraceTicks;
    @NotNull
    private static final String HERDING_TAG;
    private static long sessionStartMs;
    private static int captureCount;
    @NotNull
    private static final CheckboxSetting enabledSetting;
    @NotNull
    private static final InfoSetting infoSetting;
    @NotNull
    private static final TextSetting statusSetting;
    @NotNull
    private static final TextSetting pigKeywordSetting;
    @NotNull
    private static final TextSetting shinyOrbKeywordSetting;
    @NotNull
    private static final TextSetting shinyRodKeywordSetting;
    @NotNull
    private static final TextSetting aotvKeywordSetting;
    @NotNull
    private static final SliderSetting searchRangeSetting;
    @NotNull
    private static final SliderSetting deployRangeSetting;
    @NotNull
    private static final SliderSetting behindDistanceSetting;
    @NotNull
    private static final SliderSetting aotvHoldTicksSetting;
    @NotNull
    private static final SliderSetting rodIntervalSetting;
    @NotNull
    private static final SliderSetting stuckThresholdSetting;
    @NotNull
    private static final SliderSetting collectRangeSetting;
    @NotNull
    private static final SliderSetting waitBetweenSetting;
    @NotNull
    private static final SliderSetting otherPlayerExclusionSetting;
    @NotNull
    private static final SliderSetting abandonDistanceSetting;
    @NotNull
    private static final CheckboxSetting autoLoopSetting;
    @NotNull
    private static final CheckboxSetting walkModeSetting;
    private static final float W = 200.0f;
    private static final float H = 90.0f;
    private static final float CORNER = 10.0f;
    private static final float PAD = 10.0f;
    @NotNull
    private static final HudElement pigHud;

    private PigMacroModule() {
        super("Pig Macro");
    }

    public final boolean isActive() {
        return (Boolean)enabledSetting.getValue();
    }

    @NotNull
    public final HudElement getPigHud() {
        return pigHud;
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (state == State.HERDING) {
            OverlayRenderEngine.INSTANCE.render(event.getContext());
        }
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)PigMacroModule.enabledSetting.getValue()).booleanValue()) {
            if (PigMacroModule.wasEnabled) {
                this.stop();
            }
            PigMacroModule.wasEnabled = false;
            return;
        }
        v0 = PigMacroModule.mc.field_1724;
        if (v0 == null) {
            $this$onTick_u24lambda_u240 = this;
            $i$a$-run-PigMacroModule$onTick$player$1 = false;
            $this$onTick_u24lambda_u240.stop();
            PigMacroModule.wasEnabled = false;
            return;
        }
        player = v0;
        v1 = PigMacroModule.mc.field_1687;
        if (v1 == null) {
            $this$onTick_u24lambda_u241 = this;
            $i$a$-run-PigMacroModule$onTick$level$1 = false;
            $this$onTick_u24lambda_u241.stop();
            PigMacroModule.wasEnabled = false;
            return;
        }
        level = v1;
        if (!PigMacroModule.wasEnabled) {
            this.start();
        }
        PigMacroModule.wasEnabled = true;
        if (PigMacroModule.actionDelay > 0) {
            var4_22 = PigMacroModule.actionDelay;
            PigMacroModule.actionDelay = var4_22 + -1;
            return;
        }
        block0 : switch (WhenMappings.$EnumSwitchMapping$0[PigMacroModule.state.ordinal()]) {
            case 7: {
                range = ((Number)PigMacroModule.searchRangeSetting.getValue()).doubleValue();
                keyword = (String)PigMacroModule.pigKeywordSetting.getValue();
                exclusionRadius = ((Number)PigMacroModule.otherPlayerExclusionSetting.getValue()).doubleValue();
                v2 = level.method_18112();
                Intrinsics.checkNotNullExpressionValue((Object)v2, (String)"entitiesForRendering(...)");
                var12_34 = CollectionsKt.asSequence((Iterable)v2);
                $i$f$filterIsInstance = false;
                v3 = SequencesKt.filter((Sequence)$this$filterIsInstance$iv, (Function1)onTick$$inlined$filterIsInstance$1.INSTANCE);
                Intrinsics.checkNotNull((Object)v3, (String)"null cannot be cast to non-null type kotlin.sequences.Sequence<R of kotlin.sequences.SequencesKt___SequencesKt.filterIsInstance>");
                $this$filterIsInstance$iv = SequencesKt.filter((Sequence)v3, (Function1)(Function1)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, onTick$lambda$2(net.minecraft.class_746 double java.lang.String net.minecraft.class_638 double net.minecraft.class_1531 ), (Lnet/minecraft/class_1531;)Ljava/lang/Boolean;)((class_746)player, (double)range, (String)keyword, (class_638)level, (double)exclusionRadius));
                $i$f$minByOrNull = false;
                iterator$iv = $this$minByOrNull$iv.iterator();
                if (!iterator$iv.hasNext()) {
                    v4 = null;
                } else {
                    minElem$iv = iterator$iv.next();
                    if (!iterator$iv.hasNext()) {
                        v4 = minElem$iv;
                    } else {
                        it = (class_1531)minElem$iv;
                        $i$a$-minByOrNull-PigMacroModule$onTick$stand$2 = false;
                        minValue$iv = it.method_5739((class_1297)player);
                        do {
                            e$iv = iterator$iv.next();
                            it = (class_1531)e$iv;
                            $i$a$-minByOrNull-PigMacroModule$onTick$stand$2 = false;
                            v$iv = it.method_5739((class_1297)player);
                            if (Float.compare(minValue$iv, v$iv) <= 0) continue;
                            minElem$iv = e$iv;
                            minValue$iv = v$iv;
                        } while (iterator$iv.hasNext());
                        v4 = minElem$iv;
                    }
                }
                stand = v4;
                if (stand != null) {
                    v5 = level.method_18112();
                    Intrinsics.checkNotNullExpressionValue((Object)v5, (String)"entitiesForRendering(...)");
                    $i$f$minByOrNull = CollectionsKt.asSequence((Iterable)v5);
                    $i$f$filterIsInstance = false;
                    v6 = SequencesKt.filter((Sequence)$this$filterIsInstance$iv, (Function1)onTick$$inlined$filterIsInstance$2.INSTANCE);
                    Intrinsics.checkNotNull((Object)v6, (String)"null cannot be cast to non-null type kotlin.sequences.Sequence<R of kotlin.sequences.SequencesKt___SequencesKt.filterIsInstance>");
                    $this$filterIsInstance$iv = SequencesKt.filter((Sequence)v6, (Function1)(Function1)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, onTick$lambda$4(net.minecraft.class_1531 net.minecraft.class_1309 ), (Lnet/minecraft/class_1309;)Ljava/lang/Boolean;)((class_1531)stand));
                    $i$f$minByOrNull = false;
                    iterator$iv = $this$minByOrNull$iv.iterator();
                    if (!iterator$iv.hasNext()) {
                        v7 = null;
                    } else {
                        minElem$iv = iterator$iv.next();
                        if (!iterator$iv.hasNext()) {
                            v7 = minElem$iv;
                        } else {
                            it = (class_1309)minElem$iv;
                            $i$a$-minByOrNull-PigMacroModule$onTick$pig$2 = false;
                            minValue$iv = it.method_5739((class_1297)stand);
                            do {
                                e$iv = iterator$iv.next();
                                it = (class_1309)e$iv;
                                $i$a$-minByOrNull-PigMacroModule$onTick$pig$2 = false;
                                v$iv = it.method_5739((class_1297)stand);
                                if (Float.compare(minValue$iv, v$iv) <= 0) continue;
                                minElem$iv = e$iv;
                                minValue$iv = v$iv;
                            } while (iterator$iv.hasNext());
                            v7 = minElem$iv;
                        }
                    }
                    pig = v7;
                    if (pig != null) {
                        PigMacroModule.targetPig = pig;
                        this.transition(State.SPRINT_TO_PIG);
                        ChatUtils.sendMessage("Pig macro: found shiny pig, sprinting.");
                    }
                }
                v8 = Unit.INSTANCE;
                break;
            }
            case 5: {
                v9 = this.livePig();
                if (v9 == null) {
                    $this$onTick_u24lambda_u246 = this;
                    $i$a$-run-PigMacroModule$onTick$pig$3 = false;
                    if ($this$onTick_u24lambda_u246.nativeActive()) {
                        $this$onTick_u24lambda_u246.nativeStop(null);
                    }
                    PigMacroModule.lastSprintToPigPos = null;
                    $this$onTick_u24lambda_u246.releaseKeys();
                    $this$onTick_u24lambda_u246.transition(State.IDLE);
                    return;
                }
                pig = v9;
                if ((double)player.method_5739((class_1297)pig) <= ((Number)PigMacroModule.deployRangeSetting.getValue()).doubleValue()) {
                    if (this.nativeActive()) {
                        this.nativeStop(null);
                    }
                    PigMacroModule.lastSprintToPigPos = null;
                    this.releaseKeys();
                    PigMacroModule.actionDelay = this.humanDelay(2, 4);
                    this.transition(State.DEPLOYING_ORB);
                    v8 = Unit.INSTANCE;
                    break;
                }
                pigBlock = new class_2338(pig.method_31477(), pig.method_31478(), pig.method_31479());
                last = PigMacroModule.lastSprintToPigPos;
                v10 = needRepath = last == null || last.method_10262((class_2382)pigBlock) > 25.0;
                if (!this.nativeActive() || needRepath) {
                    if (this.nativeActive()) {
                        this.nativeStop(null);
                    }
                    NativePathfinder.INSTANCE.setTarget((double)pigBlock.method_10263() + 0.5, pigBlock.method_10264(), (double)pigBlock.method_10260() + 0.5);
                    PigMacroModule.lastSprintToPigPos = pigBlock;
                }
                v11 = NativePathfinder.INSTANCE.tick();
                if (v11 != null) {
                    v11.applyToPlayer();
                    v8 = Unit.INSTANCE;
                    break;
                }
                v8 = null;
                break;
            }
            case 4: {
                v12 = this.livePig();
                if (v12 == null) {
                    $this$onTick_u24lambda_u247 = this;
                    $i$a$-run-PigMacroModule$onTick$pig$4 = false;
                    $this$onTick_u24lambda_u247.transition(State.IDLE);
                    return;
                }
                pig = v12;
                orbSlot = InventoryUtils.findItemInHotbar((String)PigMacroModule.shinyOrbKeywordSetting.getValue());
                if (orbSlot == -1) {
                    ChatUtils.sendMessage("Pig macro: Shiny Orb not found in hotbar - stopping.");
                    this.stop();
                    return;
                }
                if (player.method_31548().method_67532() != orbSlot) {
                    InventoryUtils.holdHotbarSlot(orbSlot);
                    this.faceEntity((class_1297)pig);
                    if ((double)player.method_5739((class_1297)pig) > 1.5) {
                        PigMacroModule.mc.field_1690.field_1867.method_23481(true);
                        PigMacroModule.mc.field_1690.field_1894.method_23481(true);
                    } else {
                        PigMacroModule.mc.field_1690.field_1894.method_23481(false);
                        PigMacroModule.mc.field_1690.field_1867.method_23481(false);
                    }
                    PigMacroModule.actionTicks = 0;
                    return;
                }
                this.releaseKeys();
                if ((double)player.method_5739((class_1297)pig) > ((Number)PigMacroModule.deployRangeSetting.getValue()).doubleValue() + 2.0) {
                    PigMacroModule.actionTicks = 0;
                    PigMacroModule.lastSprintToPigPos = null;
                    this.transition(State.SPRINT_TO_PIG);
                    return;
                }
                this.faceEntity((class_1297)pig);
                if (PigMacroModule.actionTicks == 0) {
                    PigMacroModule.actionTicks = 1;
                    return;
                }
                if (PigMacroModule.actionTicks <= 8) {
                    if ((double)player.method_5739((class_1297)pig) > 3.0) {
                        PigMacroModule.mc.field_1690.field_1904.method_23481(false);
                        PigMacroModule.actionTicks = 0;
                        PigMacroModule.lastSprintToPigPos = null;
                        this.transition(State.SPRINT_TO_PIG);
                        return;
                    }
                    this.faceEntity((class_1297)pig);
                    PigMacroModule.mc.field_1690.field_1904.method_23481(false);
                    PigMacroModule.mc.field_1690.field_1904.method_23481(true);
                    if (PigMacroModule.actionTicks == 1) {
                        PigMacroModule.orbAnchor = new class_243(pig.method_23317(), pig.method_23318(), pig.method_23321());
                    }
                    last = PigMacroModule.actionTicks;
                    PigMacroModule.actionTicks = last + 1;
                    return;
                }
                PigMacroModule.mc.field_1690.field_1904.method_23481(false);
                PigMacroModule.actionTicks = 0;
                PigMacroModule.herdPhase = (Boolean)PigMacroModule.walkModeSetting.getValue() != false ? HerdPhase.SPRINT_BEHIND : HerdPhase.AOTV_BEHIND;
                PigMacroModule.walkTicks = 0;
                PigMacroModule.ticksSinceRod = 0;
                PigMacroModule.lastPigOrbDist = 1.7976931348623157E308;
                PigMacroModule.pigStuckTicks = 0;
                PigMacroModule.pigPrevPos = null;
                PigMacroModule.stateTimeout = 200;
                PigMacroModule.actionDelay = this.humanDelay(3, 8);
                this.transition(State.WAIT_FOR_ORB_CHAT);
                v8 = Unit.INSTANCE;
                break;
            }
            case 3: {
                pig = PigMacroModule.stateTimeout;
                PigMacroModule.stateTimeout = pig + -1;
                if (PigMacroModule.stateTimeout <= 0) {
                    ChatUtils.sendMessage("Pig macro: no chat confirmation - proceeding anyway.");
                    PigMacroModule.actionTicks = 0;
                    PigMacroModule.actionDelay = this.humanDelay(5, 12);
                    this.transition(State.HERDING);
                }
                v8 = Unit.INSTANCE;
                break;
            }
            case 2: {
                pig = PigMacroModule.targetPig;
                if (pig != null && pig.method_5805() && pig.method_5799()) {
                    ChatUtils.sendMessage("Pig macro: pig in water - blacklisting.");
                    PigMacroModule.waterBlacklist.add(pig.method_5628());
                    this.releaseKeys();
                    if (this.nativeActive()) {
                        this.nativeStop(null);
                    }
                    OverlayRenderEngine.INSTANCE.clearTag(PigMacroModule.HERDING_TAG);
                    PigMacroModule.lastBehindPathPos = null;
                    PigMacroModule.stuckAngleOffsetDeg = 0.0;
                    PigMacroModule.targetPig = null;
                    PigMacroModule.orbAnchor = null;
                    PigMacroModule.pigPrevPos = null;
                    PigMacroModule.lastPigOrbDist = 1.7976931348623157E308;
                    PigMacroModule.pigStuckTicks = 0;
                    PigMacroModule.actionDelay = this.humanDelay(8, 15);
                    this.transition(State.WAITING);
                    return;
                }
                if (pig == null || pig.method_31481() || !pig.method_5805()) {
                    this.releaseKeys();
                    if (this.nativeActive()) {
                        this.nativeStop(null);
                    }
                    OverlayRenderEngine.INSTANCE.clearTag(PigMacroModule.HERDING_TAG);
                    PigMacroModule.lastBehindPathPos = null;
                    PigMacroModule.pigPrevPos = null;
                    PigMacroModule.lastPigOrbDist = 1.7976931348623157E308;
                    PigMacroModule.pigStuckTicks = 0;
                    PigMacroModule.actionDelay = this.humanDelay(5, 12);
                    PigMacroModule.actionTicks = 0;
                    this.transition(State.COLLECTING);
                    return;
                }
                anchor = PigMacroModule.orbAnchor;
                odx = anchor != null ? pig.method_23317() - anchor.field_1352 : 1.0;
                odz = anchor != null ? pig.method_23321() - anchor.field_1350 : 0.0;
                olen = Math.sqrt(odx * odx + odz * odz);
                nx = olen > 0.1 ? odx / olen : 1.0;
                nz = olen > 0.1 ? odz / olen : 0.0;
                D = ((Number)PigMacroModule.behindDistanceSetting.getValue()).doubleValue();
                v13 = PigMacroModule.pigPrevPos;
                if (v13 != null) {
                    it = v13;
                    $i$a$-let-PigMacroModule$onTick$pigVel$1 = false;
                    v14 = new class_243(pig.method_23317() - it.field_1352, 0.0, pig.method_23321() - it.field_1350);
                } else {
                    v15 = class_243.field_1353;
                    v14 = v15;
                    Intrinsics.checkNotNullExpressionValue((Object)v15, (String)"ZERO");
                }
                pigVel = v14;
                PigMacroModule.pigPrevPos = new class_243(pig.method_23317(), pig.method_23318(), pig.method_23321());
                if (anchor != null && this.dist3(pig.method_23317(), pig.method_23318(), pig.method_23321(), anchor) > ((Number)PigMacroModule.abandonDistanceSetting.getValue()).doubleValue()) {
                    this.releaseKeys();
                    if (this.nativeActive()) {
                        this.nativeStop(null);
                    }
                    OverlayRenderEngine.INSTANCE.clearTag(PigMacroModule.HERDING_TAG);
                    PigMacroModule.lastBehindPathPos = null;
                    PigMacroModule.stuckAngleOffsetDeg = 0.0;
                    PigMacroModule.targetPig = null;
                    PigMacroModule.orbAnchor = null;
                    PigMacroModule.pigPrevPos = null;
                    PigMacroModule.lastPigOrbDist = 1.7976931348623157E308;
                    PigMacroModule.pigStuckTicks = 0;
                    PigMacroModule.actionDelay = this.humanDelay(5, 15);
                    this.transition(State.WAITING);
                    return;
                }
                if (anchor != null && this.dist3(pig.method_23317(), pig.method_23318(), pig.method_23321(), anchor) <= 2.0) {
                    this.releaseKeys();
                    if (this.nativeActive()) {
                        this.nativeStop(null);
                    }
                    OverlayRenderEngine.INSTANCE.clearTag(PigMacroModule.HERDING_TAG);
                    PigMacroModule.lastBehindPathPos = null;
                    PigMacroModule.stuckAngleOffsetDeg = 0.0;
                    PigMacroModule.pigPrevPos = null;
                    PigMacroModule.lastPigOrbDist = 1.7976931348623157E308;
                    PigMacroModule.pigStuckTicks = 0;
                    PigMacroModule.actionDelay = this.humanDelay(8, 20);
                    PigMacroModule.actionTicks = 0;
                    this.transition(State.COLLECTING);
                    ChatUtils.sendMessage("Pig macro: pig reached orb, collecting.");
                    return;
                }
                angleRad = Math.toRadians(PigMacroModule.stuckAngleOffsetDeg);
                rotCos = Math.cos(angleRad);
                rotSin = Math.sin(angleRad);
                rnx = nx * rotCos - nz * rotSin;
                rnz = nx * rotSin + nz * rotCos;
                behindX = pig.method_23317() + rnx * D;
                behindZ = pig.method_23321() + rnz * D;
                behindPos = new class_243(behindX, pig.method_23318(), behindZ);
                behindGroundY = this.floorYAt((class_1937)level, behindX, behindZ, pig.method_23318());
                behindGround = new class_243(behindX, behindGroundY, behindZ);
                if (anchor != null) {
                    pigLineColor = new OverlayRenderEngine.Color(76, 255, 114, 204);
                    behindLinColor = new OverlayRenderEngine.Color(255, 216, 76, 204);
                    orbMarker = new OverlayRenderEngine.Color(255, 216, 76, 170);
                    behindMarker = new OverlayRenderEngine.Color(122, 59, 196, 153);
                    OverlayRenderEngine.addLine$default(OverlayRenderEngine.INSTANCE, (class_1937)level, pig.method_23317(), pig.method_23318() + 0.6, pig.method_23321(), anchor.field_1352, anchor.field_1351 + 0.6, anchor.field_1350, pigLineColor, 2.4f, 3, PigMacroModule.HERDING_TAG, false, 2048, null);
                    OverlayRenderEngine.addBox$default(OverlayRenderEngine.INSTANCE, (class_1937)level, anchor.field_1352 - 0.4, anchor.field_1351 - 0.05, anchor.field_1350 - 0.4, anchor.field_1352 + 0.4, anchor.field_1351 + 0.9, anchor.field_1350 + 0.4, orbMarker.withAlpha(68), orbMarker, 2.2f, 3, PigMacroModule.HERDING_TAG, false, 4096, null);
                    OverlayRenderEngine.addLine$default(OverlayRenderEngine.INSTANCE, (class_1937)level, player.method_23317(), player.method_23318() + 0.5, player.method_23321(), behindPos.field_1352, behindPos.field_1351 + 0.5, behindPos.field_1350, behindLinColor, 1.8f, 3, PigMacroModule.HERDING_TAG, false, 2048, null);
                    OverlayRenderEngine.addBox$default(OverlayRenderEngine.INSTANCE, (class_1937)level, behindPos.field_1352 - 0.25, behindPos.field_1351, behindPos.field_1350 - 0.25, behindPos.field_1352 + 0.25, behindPos.field_1351 + 0.08, behindPos.field_1350 + 0.25, behindMarker, behindMarker.withAlpha(204), 1.6f, 3, PigMacroModule.HERDING_TAG, false, 4096, null);
                    velLen = Math.sqrt(pigVel.field_1352 * pigVel.field_1352 + pigVel.field_1350 * pigVel.field_1350);
                    if (velLen > 0.02) {
                        pigToOrbDot = pigVel.field_1352 / velLen * -nx + pigVel.field_1350 / velLen * -nz;
                        onTrack = pigToOrbDot > 0.5;
                        rayColor = onTrack != false ? new OverlayRenderEngine.Color(76, 255, 114, 238) : new OverlayRenderEngine.Color(255, 76, 76, 238);
                        rayScale = 8.0 / velLen;
                        OverlayRenderEngine.addLine$default(OverlayRenderEngine.INSTANCE, (class_1937)level, pig.method_23317(), pig.method_23318() + 0.6, pig.method_23321(), pig.method_23317() + pigVel.field_1352 * rayScale, pig.method_23318() + 0.6, pig.method_23321() + pigVel.field_1350 * rayScale, rayColor, 2.8f, 3, PigMacroModule.HERDING_TAG, false, 2048, null);
                    }
                }
                distToPlayer = player.method_5739((class_1297)pig);
                switch (WhenMappings.$EnumSwitchMapping$1[PigMacroModule.herdPhase.ordinal()]) {
                    case 1: {
                        aotvSlot = InventoryUtils.findItemInHotbar((String)PigMacroModule.aotvKeywordSetting.getValue());
                        if (aotvSlot == -1) {
                            ChatUtils.sendMessage("Pig macro: AOTV not found - sprinting instead.");
                            PigMacroModule.walkTicks = 0;
                            PigMacroModule.ticksSinceRod = 0;
                            PigMacroModule.herdPhase = HerdPhase.SPRINT_BEHIND;
                            return;
                        }
                        if (player.method_31548().method_67532() != aotvSlot) {
                            InventoryUtils.holdHotbarSlot(aotvSlot);
                            PigMacroModule.actionDelay = this.humanDelay(3, 6);
                            return;
                        }
                        if (!pig.method_24828() && PigMacroModule.pigLandWaitTicks < 60) {
                            velLen = PigMacroModule.pigLandWaitTicks;
                            PigMacroModule.pigLandWaitTicks = velLen + 1;
                            return;
                        }
                        PigMacroModule.pigLandWaitTicks = 0;
                        this.facePos(behindGround);
                        switch (PigMacroModule.actionTicks) {
                            case 0: {
                                PigMacroModule.actionTicks = 1;
                                v8 = Unit.INSTANCE;
                                break block0;
                            }
                            case 1: {
                                PigMacroModule.mc.field_1690.field_1904.method_23481(false);
                                PigMacroModule.mc.field_1690.field_1904.method_23481(true);
                                PigMacroModule.actionTicks = 2;
                                v8 = Unit.INSTANCE;
                                break block0;
                            }
                        }
                        PigMacroModule.mc.field_1690.field_1904.method_23481(false);
                        PigMacroModule.actionTicks = 0;
                        PigMacroModule.walkTicks = 0;
                        PigMacroModule.ticksSinceRod = 0;
                        PigMacroModule.walkGraceTicks = 12;
                        PigMacroModule.actionDelay = this.humanDelay(2, 5);
                        PigMacroModule.herdPhase = HerdPhase.WALK_PRESSURE;
                        v8 = Unit.INSTANCE;
                        break block0;
                    }
                    case 2: {
                        PigMacroModule.mc.field_1690.field_1904.method_23481(false);
                        PigMacroModule.mc.field_1690.field_1886.method_23481(false);
                        if (!((Boolean)PigMacroModule.walkModeSetting.getValue()).booleanValue()) ** GOTO lbl406
                        if (anchor != null) {
                            pigOrbDist = this.dist3(pig.method_23317(), pig.method_23318(), pig.method_23321(), anchor);
                            if (pigOrbDist < PigMacroModule.lastPigOrbDist - 0.3) {
                                PigMacroModule.lastPigOrbDist = pigOrbDist;
                                PigMacroModule.pigStuckTicks = 0;
                                v16 /* !! */  = Unit.INSTANCE;
                            } else {
                                var46_113 = PigMacroModule.pigStuckTicks;
                                PigMacroModule.pigStuckTicks = var46_113 + 1;
                                v16 /* !! */  = Integer.valueOf(var46_113);
                            }
                            if (PigMacroModule.pigStuckTicks >= (int)((Number)PigMacroModule.stuckThresholdSetting.getValue()).doubleValue()) {
                                PigMacroModule.stuckAngleOffsetDeg = (PigMacroModule.stuckAngleOffsetDeg + 45.0) % 360.0;
                                PigMacroModule.pigStuckTicks = 0;
                                PigMacroModule.lastPigOrbDist = pigOrbDist;
                                if (this.nativeActive()) {
                                    this.nativeStop(null);
                                }
                                PigMacroModule.lastBehindPathPos = null;
                                PigMacroModule.herdPhase = HerdPhase.SPRINT_BEHIND;
                                this.releaseKeys();
                                return;
                            }
                        }
                        pigBlock = new class_2338(pig.method_31477(), pig.method_31478(), pig.method_31479());
                        if (PigMacroModule.lastBehindPathPos == null) ** GOTO lbl-1000
                        v17 = PigMacroModule.lastBehindPathPos;
                        Intrinsics.checkNotNull((Object)v17);
                        if (v17.method_10262((class_2382)pigBlock) > 9.0) lbl-1000:
                        // 2 sources

                        {
                            v18 = true;
                        } else {
                            v18 = needRepath = false;
                        }
                        if (!this.nativeActive() || needRepath) {
                            if (this.nativeActive()) {
                                this.nativeStop(null);
                            }
                            NativePathfinder.INSTANCE.setTarget((double)pigBlock.method_10263() + 0.5, pigBlock.method_10264(), (double)pigBlock.method_10260() + 0.5);
                            PigMacroModule.lastBehindPathPos = pigBlock;
                        }
                        v19 = NativePathfinder.INSTANCE.tick();
                        if (v19 != null) {
                            v19.applyToPlayer();
                            v20 = Unit.INSTANCE;
                        } else {
                            v20 = null;
                        }
                        return;
lbl406:
                        // 1 sources

                        if (PigMacroModule.walkGraceTicks > 0) {
                            pigBlock = PigMacroModule.walkGraceTicks;
                            PigMacroModule.walkGraceTicks = pigBlock + -1;
                            v21 = pigBlock;
                        } else {
                            behindSide = (player.method_23317() - pig.method_23317()) * nx + (player.method_23321() - pig.method_23321()) * nz;
                            if (behindSide < -0.3) {
                                this.releaseKeys();
                                PigMacroModule.walkTicks = 0;
                                PigMacroModule.ticksSinceRod = 0;
                                PigMacroModule.walkGraceTicks = 0;
                                PigMacroModule.herdPhase = HerdPhase.SPRINT_BEHIND;
                                return;
                            }
                            v21 = Unit.INSTANCE;
                        }
                        if ((double)distToPlayer > D + 3.0) {
                            this.releaseKeys();
                            PigMacroModule.walkTicks = 0;
                            PigMacroModule.ticksSinceRod = 0;
                            PigMacroModule.actionDelay = this.humanDelay(1, 3);
                            PigMacroModule.herdPhase = HerdPhase.SPRINT_BEHIND;
                            return;
                        }
                        if (anchor != null) {
                            pigOrbDist = this.dist3(pig.method_23317(), pig.method_23318(), pig.method_23321(), anchor);
                            if (pigOrbDist < PigMacroModule.lastPigOrbDist - 0.3) {
                                PigMacroModule.lastPigOrbDist = pigOrbDist;
                                PigMacroModule.pigStuckTicks = 0;
                                v22 /* !! */  = Unit.INSTANCE;
                            } else {
                                var46_114 = PigMacroModule.pigStuckTicks;
                                PigMacroModule.pigStuckTicks = var46_114 + 1;
                                v22 /* !! */  = Integer.valueOf(var46_114);
                            }
                            if (PigMacroModule.pigStuckTicks >= (int)((Number)PigMacroModule.stuckThresholdSetting.getValue()).doubleValue()) {
                                PigMacroModule.stuckAngleOffsetDeg = (PigMacroModule.stuckAngleOffsetDeg + 45.0) % 360.0;
                                PigMacroModule.pigStuckTicks = 0;
                                PigMacroModule.lastPigOrbDist = pigOrbDist;
                                PigMacroModule.walkGraceTicks = 0;
                                PigMacroModule.herdPhase = HerdPhase.SPRINT_BEHIND;
                                this.releaseKeys();
                                return;
                            }
                        }
                        this.faceEntity((class_1297)pig);
                        if ((double)distToPlayer > 2.5) {
                            PigMacroModule.mc.field_1690.field_1867.method_23481(true);
                            PigMacroModule.mc.field_1690.field_1894.method_23481(true);
                        } else {
                            PigMacroModule.mc.field_1690.field_1894.method_23481(false);
                            PigMacroModule.mc.field_1690.field_1867.method_23481(false);
                            pigOrbDist = PigMacroModule.walkTicks;
                            PigMacroModule.walkTicks = pigOrbDist + 1;
                            if (PigMacroModule.walkTicks >= (int)((Number)PigMacroModule.aotvHoldTicksSetting.getValue()).doubleValue()) {
                                PigMacroModule.walkTicks = 0;
                                aotvSlotKb = InventoryUtils.findItemInHotbar((String)PigMacroModule.aotvKeywordSetting.getValue());
                                if (aotvSlotKb != -1) {
                                    if (player.method_31548().method_67532() != aotvSlotKb) {
                                        InventoryUtils.holdHotbarSlot(aotvSlotKb);
                                    }
                                    PigMacroModule.mc.field_1690.field_1886.method_23481(true);
                                    PigMacroModule.actionDelay = 2;
                                    return;
                                }
                                rodSlotKb = InventoryUtils.findItemInHotbar((String)PigMacroModule.shinyRodKeywordSetting.getValue());
                                if (rodSlotKb != -1) {
                                    if (player.method_31548().method_67532() != rodSlotKb) {
                                        InventoryUtils.holdHotbarSlot(rodSlotKb);
                                    }
                                    PigMacroModule.mc.field_1690.field_1904.method_23481(true);
                                    PigMacroModule.actionDelay = 2;
                                    return;
                                }
                            }
                        }
                        v8 = Unit.INSTANCE;
                        break block0;
                    }
                    case 3: {
                        distToTarget = this.dist3(player.method_23317(), player.method_23318(), player.method_23321(), behindPos);
                        if (distToTarget <= 2.0) {
                            if (this.nativeActive()) {
                                this.nativeStop(null);
                            }
                            PigMacroModule.lastBehindPathPos = null;
                            this.releaseKeys();
                            PigMacroModule.walkTicks = 0;
                            PigMacroModule.ticksSinceRod = 0;
                            PigMacroModule.walkGraceTicks = 15;
                            PigMacroModule.actionDelay = this.humanDelay(1, 2);
                            PigMacroModule.herdPhase = HerdPhase.WALK_PRESSURE;
                            v8 = Unit.INSTANCE;
                            break block0;
                        }
                        behindBlock = new class_2338((int)behindPos.field_1352, (int)behindPos.field_1351, (int)behindPos.field_1350);
                        last = PigMacroModule.lastBehindPathPos;
                        v23 = needRepath = last == null || last.method_10262((class_2382)behindBlock) > 16.0;
                        if (!this.nativeActive() || needRepath) {
                            if (this.nativeActive()) {
                                this.nativeStop(null);
                            }
                            NativePathfinder.INSTANCE.setTarget((double)behindBlock.method_10263() + 0.5, behindBlock.method_10264(), (double)behindBlock.method_10260() + 0.5);
                            PigMacroModule.lastBehindPathPos = behindBlock;
                        }
                        v24 = NativePathfinder.INSTANCE.tick();
                        if (v24 != null) {
                            v24.applyToPlayer();
                            v8 = Unit.INSTANCE;
                            break block0;
                        }
                        v8 = null;
                        break block0;
                    }
                }
                throw new NoWhenBranchMatchedException();
            }
            case 1: {
                v25 = PigMacroModule.orbAnchor;
                if (v25 == null) {
                    $this$onTick_u24lambda_u249 = this;
                    $i$a$-run-PigMacroModule$onTick$anchor$1 = false;
                    $this$onTick_u24lambda_u249.transition(State.IDLE);
                    return;
                }
                anchor = v25;
                distToOrb = this.dist3(player.method_23317(), player.method_23318(), player.method_23321(), anchor);
                if (distToOrb > ((Number)PigMacroModule.collectRangeSetting.getValue()).doubleValue()) {
                    if (!this.nativeActive()) {
                        NativePathfinder.INSTANCE.setTarget(anchor.field_1352, anchor.field_1351, anchor.field_1350);
                    }
                    v26 = NativePathfinder.INSTANCE.tick();
                    if (v26 != null) {
                        v26.applyToPlayer();
                        v27 = Unit.INSTANCE;
                    } else {
                        v27 = null;
                    }
                    return;
                }
                if (distToOrb > 3.0) {
                    if (this.nativeActive()) {
                        this.nativeStop(null);
                    }
                    NativePathfinder.INSTANCE.setTarget(anchor.field_1352, anchor.field_1351, anchor.field_1350);
                    v28 = NativePathfinder.INSTANCE.tick();
                    if (v28 != null) {
                        v28.applyToPlayer();
                        v29 = Unit.INSTANCE;
                    } else {
                        v29 = null;
                    }
                    return;
                }
                if (this.nativeActive()) {
                    this.nativeStop(null);
                }
                this.releaseKeys();
                v30 = level.method_18112();
                Intrinsics.checkNotNullExpressionValue((Object)v30, (String)"entitiesForRendering(...)");
                stand = SequencesKt.filter((Sequence)CollectionsKt.asSequence((Iterable)v30), (Function1)(Function1)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, onTick$lambda$10(net.minecraft.class_746 net.minecraft.class_1297 ), (Lnet/minecraft/class_1297;)Ljava/lang/Boolean;)((class_746)player));
                $i$f$minByOrNull = false;
                iterator$iv = $this$minByOrNull$iv.iterator();
                if (!iterator$iv.hasNext()) {
                    v31 = null;
                } else {
                    minElem$iv = iterator$iv.next();
                    if (!iterator$iv.hasNext()) {
                        v31 = minElem$iv;
                    } else {
                        it = (class_1297)minElem$iv;
                        $i$a$-minByOrNull-PigMacroModule$onTick$orbEntity$2 = false;
                        minValue$iv = PigMacroModule.INSTANCE.dist3(it.method_23317(), it.method_23318(), it.method_23321(), anchor);
                        do {
                            e$iv = iterator$iv.next();
                            it = (class_1297)e$iv;
                            $i$a$-minByOrNull-PigMacroModule$onTick$orbEntity$2 = false;
                            v$iv = PigMacroModule.INSTANCE.dist3(it.method_23317(), it.method_23318(), it.method_23321(), anchor);
                            if (Double.compare(minValue$iv, v$iv) <= 0) continue;
                            minElem$iv = e$iv;
                            minValue$iv = v$iv;
                        } while (iterator$iv.hasNext());
                        v31 = minElem$iv;
                    }
                }
                var10_77 = v31;
                if (var10_77 != null) {
                    it = var11_70 = var10_77;
                    $i$a$-takeIf-PigMacroModule$onTick$orbEntity$3 = false;
                    v32 /* !! */  = PigMacroModule.INSTANCE.dist3(it.method_23317(), it.method_23318(), it.method_23321(), anchor) <= 5.0 ? var11_70 : null;
                } else {
                    v32 /* !! */  = null;
                }
                v33 = orbEntity = v32 /* !! */ ;
                if (v33 != null) {
                    this.faceEntity(v33);
                } else {
                    this.facePos(anchor);
                }
                if (PigMacroModule.actionTicks == 0) {
                    PigMacroModule.actionTicks = 1;
                    return;
                }
                if (PigMacroModule.actionTicks <= 8) {
                    v34 = orbEntity;
                    if (v34 != null) {
                        this.faceEntity(v34);
                    } else {
                        this.facePos(anchor);
                    }
                    PigMacroModule.mc.field_1690.field_1904.method_23481(false);
                    PigMacroModule.mc.field_1690.field_1904.method_23481(true);
                    var10_78 = PigMacroModule.actionTicks;
                    PigMacroModule.actionTicks = var10_78 + 1;
                    return;
                }
                PigMacroModule.mc.field_1690.field_1904.method_23481(false);
                PigMacroModule.actionTicks = 0;
                var10_79 = PigMacroModule.captureCount;
                PigMacroModule.captureCount = var10_79 + 1;
                ChatUtils.sendMessage("Pig macro: collected orb - capture #" + PigMacroModule.captureCount + "!");
                PigMacroModule.targetPig = null;
                PigMacroModule.orbAnchor = null;
                if (((Boolean)PigMacroModule.autoLoopSetting.getValue()).booleanValue()) {
                    PigMacroModule.actionDelay = (int)((Number)PigMacroModule.waitBetweenSetting.getValue()).doubleValue() + this.humanDelay(-5, 10);
                    this.transition(State.WAITING);
                } else {
                    this.stop();
                }
                v8 = Unit.INSTANCE;
                break;
            }
            case 6: {
                this.transition(State.IDLE);
                v8 = Unit.INSTANCE;
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        v35 = PigMacroModule.statusSetting;
        v36 = StringsKt.replace$default((String)PigMacroModule.state.name(), (char)'_', (char)' ', (boolean)false, (int)4, null).toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue((Object)v36, (String)"toLowerCase(...)");
        var4_23 = v36;
        if (((CharSequence)var4_23).length() > 0) {
            $this$onTick_u24lambda_u240 = var4_23.charAt(0);
            var54_117 = new StringBuilder();
            var53_118 = v35;
            $i$a$-replaceFirstCharWithCharSequence-PigMacroModule$onTick$1 = false;
            v37 = String.valueOf((char)it);
            Intrinsics.checkNotNull((Object)v37, (String)"null cannot be cast to non-null type java.lang.String");
            v38 = v37.toUpperCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue((Object)v38, (String)"toUpperCase(...)");
            var55_119 = v38;
            v35 = var53_118;
            v39 = var54_117.append((Object)var55_119);
            var5_4 = var4_23;
            var6_14 = 1;
            v40 = var5_4.substring(var6_14);
            Intrinsics.checkNotNullExpressionValue((Object)v40, (String)"substring(...)");
            v41 = v39.append(v40).toString();
        } else {
            v41 = var4_23;
        }
        v35.setValue(v41);
    }

    @SubscribeEvent
    public final void onChat(@NotNull ChatEvent.Receive event) {
        String string;
        block6: {
            block5: {
                Intrinsics.checkNotNullParameter((Object)event, (String)"event");
                if (!((Boolean)enabledSetting.getValue()).booleanValue()) {
                    return;
                }
                String string2 = event.getMessage();
                if (string2 == null) {
                    return;
                }
                String string3 = class_124.method_539((String)string2);
                if (string3 == null) break block5;
                String string4 = string3.toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)string4, (String)"toLowerCase(...)");
                String string5 = string4;
                if (string5 == null) break block5;
                string = ((Object)StringsKt.trim((CharSequence)string5)).toString();
                if (string != null) break block6;
            }
            return;
        }
        String msg = string;
        mc.execute(() -> PigMacroModule.onChat$lambda$0(msg));
    }

    private final double floorYAt(class_1937 level2, double x, double z, double fromY) {
        int startY;
        int bx = (int)x;
        int bz = (int)z;
        int y = startY = RangesKt.coerceIn((int)((int)fromY), (int)-64, (int)319);
        int n = Math.max(startY - 40, -64);
        if (n <= y) {
            while (true) {
                if (!level2.method_8320(new class_2338(bx, y, bz)).method_26215()) {
                    return y + 1;
                }
                if (y == n) break;
                --y;
            }
        }
        return fromY;
    }

    private final int humanDelay(int min, int max) {
        return Random.Default.nextInt(min, max + 1);
    }

    private final class_1309 livePig() {
        Object object;
        class_1309 class_13092 = targetPig;
        if (class_13092 != null) {
            class_1309 class_13093;
            class_1309 it = class_13093 = class_13092;
            boolean bl = false;
            object = !it.method_31481() && it.method_5805() ? class_13093 : null;
        } else {
            object = null;
        }
        return object;
    }

    private final boolean isPig(class_1309 entity) {
        return Intrinsics.areEqual((Object)entity.method_5864().method_5882(), (Object)"entity.minecraft.pig");
    }

    private final void transition(State newState) {
        state = newState;
    }

    private final void faceEntity(class_1297 entity) {
        class_746 class_7462 = PigMacroModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        Rotation rot = AngleUtils.INSTANCE.getRotation(entity);
        player.method_36456(rot.getYaw());
        player.method_36457(rot.getPitch());
        player.field_6241 = rot.getYaw();
    }

    private final void facePos(class_243 target) {
        class_746 class_7462 = PigMacroModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        double dx = target.field_1352 - player.method_23317();
        double dy = target.field_1351 - (player.method_23318() + (double)player.method_5751());
        double dz = target.field_1350 - player.method_23321();
        double hDist = Math.sqrt(dx * dx + dz * dz);
        player.method_36456((float)Math.toDegrees(Math.atan2(-dx, dz)));
        player.method_36457((float)Math.toDegrees(-Math.atan2(dy, hDist)));
        player.field_6241 = player.method_36454();
    }

    private final double dist3(double x, double y, double z, class_243 v) {
        double dx = x - v.field_1352;
        double dy = y - v.field_1351;
        double dz = z - v.field_1350;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private final void start() {
        sessionStartMs = System.currentTimeMillis();
        captureCount = 0;
        targetPig = null;
        orbAnchor = null;
        waterBlacklist.clear();
        herdPhase = HerdPhase.AOTV_BEHIND;
        walkTicks = 0;
        ticksSinceRod = 0;
        actionTicks = 0;
        actionDelay = 0;
        stateTimeout = 0;
        lastPigOrbDist = Double.MAX_VALUE;
        pigStuckTicks = 0;
        lastBehindPathPos = null;
        lastSprintToPigPos = null;
        stuckAngleOffsetDeg = 0.0;
        pigPrevPos = null;
        walkGraceTicks = 0;
        pigLandWaitTicks = 0;
        state = State.IDLE;
        statusSetting.setValue("Idle");
        ChatUtils.sendMessage("Shiny pig macro started.");
    }

    private final boolean nativeActive() {
        PathStatus it = NativePathfinder.INSTANCE.getStatus();
        boolean bl = false;
        return it != PathStatus.IDLE && it != PathStatus.ARRIVED && it != PathStatus.FAILED;
    }

    private final void nativeStop(String reason) {
        NativePathfinder.INSTANCE.stop();
        MovementManager.setMovementLock(false);
        if (reason != null) {
            ChatUtils.sendMessage(reason);
        }
    }

    private final void stop() {
        this.releaseKeys();
        if (this.nativeActive()) {
            this.nativeStop(null);
        }
        OverlayRenderEngine.INSTANCE.clearTag(HERDING_TAG);
        state = State.IDLE;
        targetPig = null;
        orbAnchor = null;
        waterBlacklist.clear();
        herdPhase = HerdPhase.AOTV_BEHIND;
        walkTicks = 0;
        ticksSinceRod = 0;
        actionTicks = 0;
        actionDelay = 0;
        stateTimeout = 0;
        lastPigOrbDist = Double.MAX_VALUE;
        pigStuckTicks = 0;
        lastBehindPathPos = null;
        lastSprintToPigPos = null;
        stuckAngleOffsetDeg = 0.0;
        pigPrevPos = null;
        walkGraceTicks = 0;
        pigLandWaitTicks = 0;
        sessionStartMs = 0L;
        statusSetting.setValue("Idle");
    }

    private final void releaseKeys() {
        PigMacroModule.mc.field_1690.field_1894.method_23481(false);
        PigMacroModule.mc.field_1690.field_1913.method_23481(false);
        PigMacroModule.mc.field_1690.field_1849.method_23481(false);
        PigMacroModule.mc.field_1690.field_1881.method_23481(false);
        PigMacroModule.mc.field_1690.field_1867.method_23481(false);
        PigMacroModule.mc.field_1690.field_1903.method_23481(false);
        PigMacroModule.mc.field_1690.field_1904.method_23481(false);
        PigMacroModule.mc.field_1690.field_1886.method_23481(false);
    }

    private final String formatDuration(long ms) {
        String string;
        long s = RangesKt.coerceAtLeast((long)(ms / 1000L), (long)0L);
        long h = s / 3600L;
        long m = s % 3600L / 60L;
        long sec = s % 60L;
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

    private static final float pigHud$lambda$0$0() {
        return 200.0f;
    }

    private static final float pigHud$lambda$0$1() {
        return 90.0f;
    }

    private static final Unit pigHud$lambda$0$2(float x, float y, float f) {
        long now = System.currentTimeMillis();
        float twoPi = (float)Math.PI * 2;
        int c1 = ThemeManager.INSTANCE.getCurrentTheme().getAccent();
        int c2 = ThemeManager.INSTANCE.getCurrentTheme().getAccentSecondary();
        int dim = -1146105857;
        NVGRenderer.rect(x, y, 200.0f, 90.0f, -16118246, 10.0f);
        NVGRenderer.gradientRect(x, y, 200.0f, 45.0f, 0x14FFFFFF, 0, Gradient.TopToBottom, 10.0f);
        float shiftX = (float)Math.cos((float)(now % 10000L) / 10000.0f * twoPi) * 84.0f;
        NVGRenderer.hollowGradientRectShifted(x, y, 200.0f, 90.0f, 1.5f, c1, c2, Gradient.LeftToRight, 10.0f, shiftX, 0.0f);
        float cy = y + 14.0f;
        NVGRenderer.text$default("SHINY PIG MACRO", x + 10.0f, cy, 11.0f, c1, null, 32, null);
        String stateStr = StringsKt.replace$default((String)state.name(), (char)'_', (char)' ', (boolean)false, (int)4, null);
        int stateColor = switch (WhenMappings.$EnumSwitchMapping$0[state.ordinal()]) {
            case 1 -> -11731086;
            case 2 -> -29620;
            case 3 -> -11745025;
            case 4 -> -11745025;
            case 5 -> -10164;
            case 6 -> -7829368;
            case 7 -> -12303292;
            default -> throw new NoWhenBranchMatchedException();
        };
        float stateW = NVGRenderer.textWidth$default(stateStr, 9.0f, null, 4, null);
        NVGRenderer.rect(x + 200.0f - 10.0f - stateW - 8.0f, cy - 9.0f, stateW + 8.0f, 12.0f, stateColor & 0xFFFFFF | 0x33000000, 3.0f);
        NVGRenderer.text$default(stateStr, x + 200.0f - 10.0f - stateW - 4.0f, cy, 9.0f, stateColor, null, 32, null);
        cy += 14.0f;
        if (sessionStartMs > 0L) {
            NVGRenderer.text$default("Time: " + INSTANCE.formatDuration(now - sessionStartMs), x + 10.0f, cy, 10.0f, dim, null, 32, null);
        } else {
            NVGRenderer.text$default("Time: --:--", x + 10.0f, cy, 10.0f, dim, null, 32, null);
        }
        NVGRenderer.text$default("Captures: " + captureCount, x + 10.0f, cy += 13.0f, 10.0f, dim, null, 32, null);
        cy += 13.0f;
        class_1309 pig = targetPig;
        if (pig != null && pig.method_5805()) {
            String string = "Target: %.1fm";
            Object[] objectArray = new Object[1];
            class_746 class_7462 = PigMacroModule.mc.field_1724;
            objectArray[0] = class_7462 != null ? (Number)Float.valueOf(class_7462.method_5739((class_1297)pig)) : (Number)0.0;
            String string2 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"format(...)");
            NVGRenderer.text$default(string2, x + 10.0f, cy, 10.0f, dim, null, 32, null);
        } else {
            NVGRenderer.text$default("Target: none", x + 10.0f, cy, 10.0f, dim, null, 32, null);
        }
        return Unit.INSTANCE;
    }

    private static final Unit pigHud$lambda$0(HudElementBuilder $this$hudElement) {
        Intrinsics.checkNotNullParameter((Object)$this$hudElement, (String)"$this$hudElement");
        $this$hudElement.setAnchor(HudAnchor.TOP_RIGHT);
        $this$hudElement.setOffsetX(10.0f);
        $this$hudElement.setOffsetY(10.0f);
        $this$hudElement.width((Function0<Float>)((Function0)PigMacroModule::pigHud$lambda$0$0));
        $this$hudElement.height((Function0<Float>)((Function0)PigMacroModule::pigHud$lambda$0$1));
        $this$hudElement.render((Function3<? super Float, ? super Float, ? super Float, Unit>)((Function3)PigMacroModule::pigHud$lambda$0$2));
        return Unit.INSTANCE;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static final boolean onTick$lambda$2(class_746 $player, double $range, String $keyword, class_638 $level, double $exclusionRadius, class_1531 as_) {
        boolean bl;
        Intrinsics.checkNotNullParameter((Object)as_, (String)"as_");
        if (!((double)as_.method_5739((class_1297)$player) <= $range)) return false;
        class_2561 class_25612 = as_.method_5797();
        Object object = class_25612;
        if (class_25612 == null) return false;
        String string = object.getString();
        object = string;
        if (string == null) return false;
        if (!StringsKt.contains((CharSequence)((CharSequence)object), (CharSequence)$keyword, (boolean)true)) return false;
        boolean bl2 = true;
        if (!bl2) return false;
        Iterable iterable = $level.method_18112();
        Intrinsics.checkNotNullExpressionValue((Object)iterable, (String)"entitiesForRendering(...)");
        Iterable $this$none$iv = iterable;
        boolean $i$f$none = false;
        if ($this$none$iv instanceof Collection && ((Collection)$this$none$iv).isEmpty()) {
            return true;
        }
        Iterator iterator = $this$none$iv.iterator();
        do {
            if (!iterator.hasNext()) return true;
            Object element$iv = iterator.next();
            class_1297 other = (class_1297)element$iv;
            boolean bl3 = false;
            if (other instanceof class_1657 && !Intrinsics.areEqual((Object)other, (Object)$player) && (double)other.method_5739((class_1297)as_) <= $exclusionRadius) {
                return false;
            }
            bl = false;
        } while (!bl);
        return false;
    }

    private static final boolean onTick$lambda$4(class_1531 $stand, class_1309 entity) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        return entity.method_5805() && !waterBlacklist.contains(entity.method_5628()) && !entity.method_5799() && INSTANCE.isPig(entity) && (double)entity.method_5739((class_1297)$stand) <= 3.0;
    }

    private static final boolean onTick$lambda$10(class_746 $player, class_1297 it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return !Intrinsics.areEqual((Object)it, (Object)$player);
    }

    private static final void onChat$lambda$0(String $msg) {
        if (!((Boolean)enabledSetting.getValue()).booleanValue()) {
            return;
        }
        if (StringsKt.contains$default((CharSequence)$msg, (CharSequence)"bring the pig back to the shiny orb", (boolean)false, (int)2, null) && (state == State.WAIT_FOR_ORB_CHAT || state == State.DEPLOYING_ORB)) {
            if (orbAnchor == null) {
                class_243 class_2432;
                class_1309 pig = targetPig;
                if (pig != null && pig.method_5805()) {
                    class_2432 = new class_243(pig.method_23317(), pig.method_23318(), pig.method_23321());
                } else {
                    class_746 class_7462 = PigMacroModule.mc.field_1724;
                    if (class_7462 != null) {
                        class_746 it = class_7462;
                        boolean bl = false;
                        class_2432 = new class_243(it.method_23317(), it.method_23318(), it.method_23321());
                    } else {
                        class_2432 = null;
                    }
                }
                orbAnchor = class_2432;
            }
            int delay = INSTANCE.humanDelay(0, 40);
            actionTicks = 0;
            walkTicks = 0;
            ticksSinceRod = 0;
            lastBehindPathPos = null;
            stuckAngleOffsetDeg = 0.0;
            herdPhase = (Boolean)walkModeSetting.getValue() != false ? HerdPhase.SPRINT_BEHIND : HerdPhase.AOTV_BEHIND;
            actionDelay = delay;
            INSTANCE.transition(State.HERDING);
        }
        if ((StringsKt.contains$default((CharSequence)$msg, (CharSequence)"the orb is charged", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)$msg, (CharSequence)"click on it for loot", (boolean)false, (int)2, null)) && (state == State.HERDING || state == State.WAIT_FOR_ORB_CHAT)) {
            INSTANCE.releaseKeys();
            if (INSTANCE.nativeActive()) {
                INSTANCE.nativeStop(null);
            }
            OverlayRenderEngine.INSTANCE.clearTag(HERDING_TAG);
            targetPig = null;
            lastPigOrbDist = Double.MAX_VALUE;
            pigStuckTicks = 0;
            pigPrevPos = null;
            actionTicks = 0;
            actionDelay = INSTANCE.humanDelay(0, 40);
            INSTANCE.transition(State.COLLECTING);
        }
        if (StringsKt.contains$default((CharSequence)$msg, (CharSequence)"shiny orb", (boolean)false, (int)2, null) && StringsKt.contains$default((CharSequence)$msg, (CharSequence)"expired", (boolean)false, (int)2, null)) {
            INSTANCE.releaseKeys();
            if (INSTANCE.nativeActive()) {
                INSTANCE.nativeStop(null);
            }
            OverlayRenderEngine.INSTANCE.clearTag(HERDING_TAG);
            targetPig = null;
            orbAnchor = null;
            pigPrevPos = null;
            lastPigOrbDist = Double.MAX_VALUE;
            pigStuckTicks = 0;
            lastBehindPathPos = null;
            stuckAngleOffsetDeg = 0.0;
            herdPhase = (Boolean)walkModeSetting.getValue() != false ? HerdPhase.SPRINT_BEHIND : HerdPhase.AOTV_BEHIND;
            walkTicks = 0;
            ticksSinceRod = 0;
            actionTicks = 0;
            actionDelay = INSTANCE.humanDelay(8, 20);
            INSTANCE.transition(State.WAITING);
        }
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        state = State.IDLE;
        waterBlacklist = new LinkedHashSet();
        herdPhase = HerdPhase.AOTV_BEHIND;
        lastPigOrbDist = Double.MAX_VALUE;
        HERDING_TAG = "pig-herding";
        enabledSetting = new CheckboxSetting("Enabled", "Start or stop the shiny pig macro.", false);
        infoSetting = new InfoSetting("Shiny Pig", "Sprint to pig -> deploy orb -> AOTV behind -> walk pressure + rod -> punch if stuck -> collect.", InfoType.INFO);
        statusSetting = new TextSetting("Status", "Current macro state.", "Idle");
        pigKeywordSetting = new TextSetting("Pig Keyword", "Name substring for shiny pigs.", "Shiny");
        shinyOrbKeywordSetting = new TextSetting("Shiny Orb Item", "Hotbar keyword for Shiny Orb.", "Shiny Orb");
        shinyRodKeywordSetting = new TextSetting("Shiny Rod Item", "Hotbar keyword for Shiny Rod.", "Shiny Rod");
        aotvKeywordSetting = new TextSetting("AOTV Item", "Hotbar keyword for Aspect of the Void.", "Aspect of the Void");
        searchRangeSetting = new SliderSetting("Search Range", "Radius to search for shiny pigs.", 32.0, 8.0, 64.0, 1.0);
        deployRangeSetting = new SliderSetting("Deploy Range", "Distance from pig to deploy orb.", 2.5, 1.0, 4.0, 0.5);
        behindDistanceSetting = new SliderSetting("Behind Distance", "Blocks behind pig to stand when herding.", 3.0, 1.5, 6.0, 0.5);
        aotvHoldTicksSetting = new SliderSetting("AOTV Hold Ticks", "Ticks to hold right-click for AOTV.", 5.0, 2.0, 10.0, 1.0);
        rodIntervalSetting = new SliderSetting("Rod Interval", "Ticks between rod clicks during walking pressure.", 15.0, 5.0, 40.0, 1.0);
        stuckThresholdSetting = new SliderSetting("Stuck Threshold", "Ticks pig must be stuck before punching it.", 60.0, 20.0, 120.0, 5.0);
        collectRangeSetting = new SliderSetting("Collect Range", "Distance to orb before clicking to collect.", 3.0, 1.0, 8.0, 0.5);
        waitBetweenSetting = new SliderSetting("Wait Between", "Ticks to wait before hunting next pig.", 40.0, 10.0, 200.0, 5.0);
        otherPlayerExclusionSetting = new SliderSetting("Player Exclusion Radius", "Skip pigs within N blocks of another player.", 6.0, 2.0, 20.0, 1.0);
        abandonDistanceSetting = new SliderSetting("Abandon Distance", "Pig->orb distance at which to give up and find a new pig.", 25.0, 10.0, 60.0, 1.0);
        autoLoopSetting = new CheckboxSetting("Auto Loop", "Automatically hunt the next pig after collecting.", true);
        walkModeSetting = new CheckboxSetting("Walk Mode", "Walk toward pig to make it flee to the orb instead of using AOTV knockback.", false);
        pigHud = HudModuleDSLKt.hudElement(INSTANCE, "pig-macro-hud", "Pig Macro", "Shiny pig macro status HUD", (Function1<? super HudElementBuilder, Unit>)((Function1)PigMacroModule::pigHud$lambda$0));
        Setting[] settingArray = new Setting[]{enabledSetting, infoSetting, statusSetting, pigKeywordSetting, shinyOrbKeywordSetting, shinyRodKeywordSetting, aotvKeywordSetting, searchRangeSetting, deployRangeSetting, behindDistanceSetting, aotvHoldTicksSetting, rodIntervalSetting, stuckThresholdSetting, collectRangeSetting, waitBetweenSetting, otherPlayerExclusionSetting, abandonDistanceSetting, autoLoopSetting, walkModeSetting};
        INSTANCE.addSetting(settingArray);
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/internal/pig/PigMacroModule$HerdPhase;", "", "<init>", "(Ljava/lang/String;I)V", "AOTV_BEHIND", "WALK_PRESSURE", "SPRINT_BEHIND", "cobalt"})
    private static final class HerdPhase
    extends Enum<HerdPhase> {
        public static final /* enum */ HerdPhase AOTV_BEHIND = new HerdPhase();
        public static final /* enum */ HerdPhase WALK_PRESSURE = new HerdPhase();
        public static final /* enum */ HerdPhase SPRINT_BEHIND = new HerdPhase();
        private static final /* synthetic */ HerdPhase[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static HerdPhase[] values() {
            return (HerdPhase[])$VALUES.clone();
        }

        public static HerdPhase valueOf(String value) {
            return Enum.valueOf(HerdPhase.class, value);
        }

        @NotNull
        public static EnumEntries<HerdPhase> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = herdPhaseArray = new HerdPhase[]{HerdPhase.AOTV_BEHIND, HerdPhase.WALK_PRESSURE, HerdPhase.SPRINT_BEHIND};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\n\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n\u00a8\u0006\u000b"}, d2={"Lorg/cobalt/internal/pig/PigMacroModule$State;", "", "<init>", "(Ljava/lang/String;I)V", "IDLE", "SPRINT_TO_PIG", "DEPLOYING_ORB", "WAIT_FOR_ORB_CHAT", "HERDING", "COLLECTING", "WAITING", "cobalt"})
    private static final class State
    extends Enum<State> {
        public static final /* enum */ State IDLE = new State();
        public static final /* enum */ State SPRINT_TO_PIG = new State();
        public static final /* enum */ State DEPLOYING_ORB = new State();
        public static final /* enum */ State WAIT_FOR_ORB_CHAT = new State();
        public static final /* enum */ State HERDING = new State();
        public static final /* enum */ State COLLECTING = new State();
        public static final /* enum */ State WAITING = new State();
        private static final /* synthetic */ State[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static State[] values() {
            return (State[])$VALUES.clone();
        }

        public static State valueOf(String value) {
            return Enum.valueOf(State.class, value);
        }

        @NotNull
        public static EnumEntries<State> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = stateArray = new State[]{State.IDLE, State.SPRINT_TO_PIG, State.DEPLOYING_ORB, State.WAIT_FOR_ORB_CHAT, State.HERDING, State.COLLECTING, State.WAITING};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;
        public static final /* synthetic */ int[] $EnumSwitchMapping$1;

        static {
            int[] nArray = new int[State.values().length];
            try {
                nArray[State.COLLECTING.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.HERDING.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.WAIT_FOR_ORB_CHAT.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.DEPLOYING_ORB.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.SPRINT_TO_PIG.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.WAITING.ordinal()] = 6;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.IDLE.ordinal()] = 7;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
            nArray = new int[HerdPhase.values().length];
            try {
                nArray[HerdPhase.AOTV_BEHIND.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[HerdPhase.WALK_PRESSURE.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[HerdPhase.SPRINT_BEHIND.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$1 = nArray;
        }
    }
}

