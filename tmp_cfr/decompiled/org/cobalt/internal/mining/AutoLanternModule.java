/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.SetsKt
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  net.minecraft.class_124
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_634
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.mining;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.minecraft.class_124;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.InfoSetting;
import org.cobalt.api.module.setting.impl.InfoType;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.rotation.RotationExecutor;
import org.cobalt.api.rotation.strategy.BezierTrackingRotationStrategy;
import org.cobalt.api.util.AngleUtils;
import org.cobalt.api.util.InventoryUtils;
import org.cobalt.api.util.MouseUtils;
import org.cobalt.api.util.helper.Rotation;
import org.cobalt.internal.mining.MiningMacroModule;
import org.cobalt.internal.pathfinding.OverlayRenderEngine;
import org.cobalt.internal.visual.DeployableHudModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u00b6\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\b\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\"\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001{B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0006J\r\u0010\u0007\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\u0006J\u0015\u0010\n\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\b\u00a2\u0006\u0004\b\n\u0010\u000bJ\r\u0010\r\u001a\u00020\f\u00a2\u0006\u0004\b\r\u0010\u000eJ\u001d\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u0011\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u0017\u0010\u0018\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u0016H\u0007\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0017\u0010\u001c\u001a\u00020\u00042\u0006\u0010\u001b\u001a\u00020\u001aH\u0002\u00a2\u0006\u0004\b\u001c\u0010\u001dJ\u0017\u0010\u001e\u001a\u00020\u00042\u0006\u0010\u001b\u001a\u00020\u001aH\u0002\u00a2\u0006\u0004\b\u001e\u0010\u001dJ\u000f\u0010\u001f\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u001f\u0010\u0006J\u001d\u0010#\u001a\b\u0012\u0004\u0012\u00020 0\"2\u0006\u0010!\u001a\u00020 H\u0002\u00a2\u0006\u0004\b#\u0010$J\u0019\u0010&\u001a\u0004\u0018\u00010\f2\u0006\u0010%\u001a\u00020 H\u0002\u00a2\u0006\u0004\b&\u0010'J\u001b\u0010)\u001a\u0004\u0018\u00010\f2\b\u0010(\u001a\u0004\u0018\u00010 H\u0002\u00a2\u0006\u0004\b)\u0010'J\u0017\u0010*\u001a\u00020\b2\u0006\u0010\u001b\u001a\u00020\u001aH\u0002\u00a2\u0006\u0004\b*\u0010+J\u0017\u0010-\u001a\u00020\f2\u0006\u0010,\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b-\u0010.J\u000f\u0010/\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b/\u0010\u0003J\u001f\u00102\u001a\u00020\u00042\u0006\u0010\u001b\u001a\u00020\u001a2\u0006\u00101\u001a\u000200H\u0002\u00a2\u0006\u0004\b2\u00103J\u000f\u00104\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b4\u0010\u0003J\u000f\u00105\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b5\u0010\u0003J\u0017\u00106\u001a\u00020\u00132\u0006\u0010\u001b\u001a\u00020\u001aH\u0002\u00a2\u0006\u0004\b6\u00107J\u001f\u0010:\u001a\u00020\u00132\u0006\u0010\u001b\u001a\u00020\u001a2\u0006\u00109\u001a\u000208H\u0002\u00a2\u0006\u0004\b:\u0010;J\u0017\u0010=\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020<H\u0007\u00a2\u0006\u0004\b=\u0010>Jo\u0010L\u001a\u00020\u00132\u0006\u00109\u001a\u0002082\u0006\u0010@\u001a\u00020?2\u0006\u0010A\u001a\u00020?2\u0006\u0010B\u001a\u00020?2\u0006\u0010C\u001a\u00020?2\u0006\u0010D\u001a\u00020\b2\u0006\u0010E\u001a\u00020?2\u0006\u0010F\u001a\u00020?2\u0006\u0010G\u001a\u00020\b2\u0006\u0010I\u001a\u00020H2\u0006\u0010J\u001a\u00020H2\u0006\u0010K\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\bL\u0010MR\u0014\u0010O\u001a\u00020N8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bO\u0010PR\u0014\u0010R\u001a\u00020Q8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bR\u0010SR\u0014\u0010U\u001a\u00020T8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bU\u0010VR\u0014\u0010W\u001a\u00020Q8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bW\u0010SR\u0014\u0010Y\u001a\u00020X8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bY\u0010ZR\u0014\u0010[\u001a\u00020X8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b[\u0010ZR\u0014\u0010\\\u001a\u00020X8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\\\u0010ZR\u0014\u0010]\u001a\u00020X8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b]\u0010ZR\u001a\u0010_\u001a\b\u0012\u0004\u0012\u00020\f0^8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b_\u0010`R\u0016\u0010a\u001a\u00020\u000f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\ba\u0010bR\u0018\u0010c\u001a\u0004\u0018\u00010\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bc\u0010dR\u0016\u0010e\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\be\u0010fR\u0016\u0010g\u001a\u00020\b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bg\u0010hR\u0016\u0010i\u001a\u00020\b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bi\u0010hR\u0018\u0010j\u001a\u0004\u0018\u0001008\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bj\u0010kR\u0018\u0010l\u001a\u0004\u0018\u0001008\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bl\u0010kR\u0018\u0010n\u001a\u0004\u0018\u00010m8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bn\u0010oR\u0016\u0010p\u001a\u00020\b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bp\u0010hR\u0014\u0010r\u001a\u00020q8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\br\u0010sR\u0014\u0010t\u001a\u00020q8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bt\u0010sR\u0014\u0010u\u001a\u00020H8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bu\u0010vR\u0014\u0010w\u001a\u00020\b8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bw\u0010hR\u0014\u0010x\u001a\u00020\b8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bx\u0010hR\u0014\u0010y\u001a\u00020H8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\by\u0010vR\u0014\u0010z\u001a\u00020H8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bz\u0010v\u00a8\u0006|"}, d2={"Lorg/cobalt/internal/mining/AutoLanternModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "", "isLanternBuffActive", "()Z", "isPlacementInProgress", "", "slot", "tryStartLanternPlacement", "(I)Z", "", "getLanternBuffStatus", "()Ljava/lang/String;", "", "levelTick", "Lnet/minecraft/class_2338;", "pos", "", "noteLanternUse", "(JLnet/minecraft/class_2338;)V", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lnet/minecraft/class_1657;", "player", "resolveLanternBuffState", "(Lnet/minecraft/class_1657;)Z", "isNearLastPlacement", "hasLanternBuffInTabList", "", "connection", "", "resolveTabEntries", "(Ljava/lang/Object;)Ljava/util/List;", "entry", "resolveEntryDisplayName", "(Ljava/lang/Object;)Ljava/lang/String;", "value", "coerceText", "findLanternSlot", "(Lnet/minecraft/class_1657;)I", "raw", "normalizeName", "(Ljava/lang/String;)Ljava/lang/String;", "handlePlacementSequence", "Lorg/cobalt/api/util/helper/Rotation;", "target", "hasReachedRotation", "(Lnet/minecraft/class_1657;Lorg/cobalt/api/util/helper/Rotation;)Z", "finishPlacementSequence", "cancelPlacementSequence", "restorePendingSlot", "(Lnet/minecraft/class_1657;)V", "Lnet/minecraft/class_1937;", "level", "useLantern", "(Lnet/minecraft/class_1657;Lnet/minecraft/class_1937;)V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "", "cx", "cy", "cz", "radius", "segments", "rotAngle", "arcHalfSpan", "baseAlpha", "", "pulse", "widthScale", "halo", "drawLanternRing", "(Lnet/minecraft/class_1937;DDDDIDDIFFZ)V", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "info", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "requireMacro", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "cooldownTicks", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "reUseDistance", "preUseDelayTicks", "postUseDelayTicks", "", "LANTERN_NAMES", "Ljava/util/Set;", "lastUseTick", "J", "lastUsePos", "Lnet/minecraft/class_2338;", "lanternBuffActive", "Z", "pendingOriginalSlot", "I", "pendingLanternSlot", "pendingPlaceRotation", "Lorg/cobalt/api/util/helper/Rotation;", "pendingReturnRotation", "Lorg/cobalt/internal/mining/AutoLanternModule$LanternPlacementPhase;", "pendingPlacementPhase", "Lorg/cobalt/internal/mining/AutoLanternModule$LanternPlacementPhase;", "pendingPlacementTicks", "Lorg/cobalt/api/rotation/strategy/BezierTrackingRotationStrategy;", "lanternPlaceStrategy", "Lorg/cobalt/api/rotation/strategy/BezierTrackingRotationStrategy;", "lanternReturnStrategy", "LANTERN_PLACE_PITCH", "F", "LANTERN_TURN_TIMEOUT_TICKS", "LANTERN_RETURN_TIMEOUT_TICKS", "LANTERN_ROTATION_SNAP_YAW", "LANTERN_ROTATION_SNAP_PITCH", "LanternPlacementPhase", "cobalt"})
@SourceDebugExtension(value={"SMAP\nAutoLanternModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AutoLanternModule.kt\norg/cobalt/internal/mining/AutoLanternModule\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,485:1\n1#2:486\n1807#3,2:487\n1807#3,3:489\n1809#3:492\n1807#3,3:503\n1401#4,2:493\n1401#4,2:495\n1401#4,2:497\n1401#4,2:499\n1401#4,2:501\n*S KotlinDebug\n*F\n+ 1 AutoLanternModule.kt\norg/cobalt/internal/mining/AutoLanternModule\n*L\n215#1:487,2\n218#1:489,3\n215#1:492\n296#1:503,3\n229#1:493,2\n245#1:495,2\n258#1:497,2\n263#1:499,2\n278#1:501,2\n*E\n"})
public final class AutoLanternModule
extends Module {
    @NotNull
    public static final AutoLanternModule INSTANCE = new AutoLanternModule();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final CheckboxSetting enabled;
    @NotNull
    private static final InfoSetting info;
    @NotNull
    private static final CheckboxSetting requireMacro;
    @NotNull
    private static final SliderSetting cooldownTicks;
    @NotNull
    private static final SliderSetting reUseDistance;
    @NotNull
    private static final SliderSetting preUseDelayTicks;
    @NotNull
    private static final SliderSetting postUseDelayTicks;
    @NotNull
    private static final Set<String> LANTERN_NAMES;
    private static long lastUseTick;
    @Nullable
    private static class_2338 lastUsePos;
    private static boolean lanternBuffActive;
    private static int pendingOriginalSlot;
    private static int pendingLanternSlot;
    @Nullable
    private static Rotation pendingPlaceRotation;
    @Nullable
    private static Rotation pendingReturnRotation;
    @Nullable
    private static LanternPlacementPhase pendingPlacementPhase;
    private static int pendingPlacementTicks;
    @NotNull
    private static final BezierTrackingRotationStrategy lanternPlaceStrategy;
    @NotNull
    private static final BezierTrackingRotationStrategy lanternReturnStrategy;
    private static final float LANTERN_PLACE_PITCH = 82.0f;
    private static final int LANTERN_TURN_TIMEOUT_TICKS = 18;
    private static final int LANTERN_RETURN_TIMEOUT_TICKS = 16;
    private static final float LANTERN_ROTATION_SNAP_YAW = 4.5f;
    private static final float LANTERN_ROTATION_SNAP_PITCH = 3.5f;

    private AutoLanternModule() {
        super("Auto Lantern");
    }

    public final boolean isLanternBuffActive() {
        return lanternBuffActive;
    }

    public final boolean isPlacementInProgress() {
        return pendingPlacementPhase != null || pendingLanternSlot >= 0;
    }

    public final boolean tryStartLanternPlacement(int slot) {
        block3: {
            class_746 class_7462 = AutoLanternModule.mc.field_1724;
            if (class_7462 == null) {
                return false;
            }
            class_746 player = class_7462;
            if (AutoLanternModule.mc.field_1755 != null) {
                return false;
            }
            if (!(0 <= slot ? slot < 9 : false) || this.isPlacementInProgress()) {
                return false;
            }
            pendingOriginalSlot = player.method_31548().method_67532();
            pendingLanternSlot = slot;
            pendingPlaceRotation = new Rotation(AngleUtils.INSTANCE.normalizeAngle(player.method_36454() + 180.0f), 82.0f);
            pendingReturnRotation = new Rotation(player.method_36454(), player.method_36455());
            pendingPlacementPhase = LanternPlacementPhase.TURN_TO_PLACE;
            pendingPlacementTicks = 0;
            Rotation rotation = pendingPlaceRotation;
            if (rotation == null) break block3;
            Rotation it = rotation;
            boolean bl = false;
            RotationExecutor.INSTANCE.rotateTo(it, lanternPlaceStrategy);
        }
        return true;
    }

    @NotNull
    public final String getLanternBuffStatus() {
        String string = DeployableHudModule.INSTANCE.getLanternStatusLabel();
        if (string == null) {
            string = lanternBuffActive ? "Active" : "Inactive";
        }
        return string;
    }

    public final void noteLanternUse(long levelTick, @NotNull class_2338 pos) {
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        lastUseTick = levelTick;
        lastUsePos = pos.method_10062();
        lanternBuffActive = true;
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (pendingPlacementPhase != null) {
            this.handlePlacementSequence();
            return;
        }
        class_746 player = AutoLanternModule.mc.field_1724;
        if (player == null) {
            lanternBuffActive = false;
            return;
        }
        lanternBuffActive = this.resolveLanternBuffState((class_1657)player);
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            return;
        }
        class_638 class_6382 = AutoLanternModule.mc.field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        if (AutoLanternModule.mc.field_1755 != null) {
            return;
        }
        if (((Boolean)requireMacro.getValue()).booleanValue() && !MiningMacroModule.INSTANCE.isActive()) {
            return;
        }
        if (lanternBuffActive) {
            return;
        }
        if (level2.method_75260() - lastUseTick < (long)((Number)cooldownTicks.getValue()).doubleValue()) {
            return;
        }
        int slot = this.findLanternSlot((class_1657)player);
        if (!(0 <= slot ? slot < 9 : false)) {
            return;
        }
        this.tryStartLanternPlacement(slot);
    }

    private final boolean resolveLanternBuffState(class_1657 player) {
        if (DeployableHudModule.INSTANCE.isLanternActive()) {
            return true;
        }
        if (this.isNearLastPlacement(player)) {
            return true;
        }
        return this.hasLanternBuffInTabList();
    }

    private final boolean isNearLastPlacement(class_1657 player) {
        class_2338 class_23382 = lastUsePos;
        if (class_23382 == null) {
            return false;
        }
        class_2338 last = class_23382;
        class_243 ref = new class_243((double)last.method_10263() + 0.5, player.method_23318(), (double)last.method_10260() + 0.5);
        return player.method_73189().method_1022(ref) <= ((Number)reUseDistance.getValue()).doubleValue();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final boolean hasLanternBuffInTabList() {
        class_634 class_6342 = mc.method_1562();
        if (class_6342 == null) {
            return false;
        }
        class_634 connection = class_6342;
        try {
            boolean bl;
            Iterable $this$any$iv = this.resolveTabEntries(connection);
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                return false;
            }
            Iterator iterator = $this$any$iv.iterator();
            do {
                boolean bl2;
                String string;
                block14: {
                    block13: {
                        String raw;
                        Object element$iv;
                        if (!iterator.hasNext()) return false;
                        Object entry = element$iv = iterator.next();
                        boolean bl3 = false;
                        if (INSTANCE.resolveEntryDisplayName(entry) == null) {
                            bl = false;
                            continue;
                        }
                        string = class_124.method_539((String)raw);
                        if (string == null) break block13;
                        String string2 = string;
                        Locale locale = Locale.ROOT;
                        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
                        String string3 = string2.toLowerCase(locale);
                        Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
                        string = string3;
                        if (string3 != null) break block14;
                    }
                    bl = false;
                    continue;
                }
                String stripped = string;
                Iterable $this$any$iv2 = LANTERN_NAMES;
                boolean $i$f$any2 = false;
                if ($this$any$iv2 instanceof Collection && ((Collection)$this$any$iv2).isEmpty()) {
                    bl2 = false;
                } else {
                    for (Object element$iv : $this$any$iv2) {
                        String it = (String)element$iv;
                        boolean bl4 = false;
                        if (!StringsKt.contains$default((CharSequence)stripped, (CharSequence)it, (boolean)false, (int)2, null)) continue;
                        return true;
                    }
                    bl2 = false;
                }
                if (bl2) return true;
                if (StringsKt.contains$default((CharSequence)stripped, (CharSequence)"will-o'-wisp", (boolean)false, (int)2, null)) {
                    return true;
                }
                bl = false;
            } while (!bl);
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    private final List<Object> resolveTabEntries(Object connection) {
        String[] stringArray = new String[]{"listPlayerEntries", "getListedOnlinePlayers", "getOnlinePlayers"};
        List methodNames = CollectionsKt.listOf((Object[])stringArray);
        for (String name : methodNames) {
            Object object;
            Object object2;
            block5: {
                Method[] methodArray = connection.getClass().getMethods();
                Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"getMethods(...)");
                Object[] $this$firstOrNull$iv = methodArray;
                boolean $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    Method m = (Method)element$iv;
                    boolean bl = false;
                    if (!(Intrinsics.areEqual((Object)m.getName(), (Object)name) && m.getParameterCount() == 0)) continue;
                    object2 = element$iv;
                    break block5;
                }
                object2 = null;
            }
            if ((Method)object2 == null) continue;
            Object object3 = this;
            try {
                Method method;
                AutoLanternModule $this$resolveTabEntries_u24lambda_u241 = object3;
                boolean bl = false;
                object = Result.constructor-impl((Object)method.invoke(connection, new Object[0]));
            }
            catch (Throwable throwable) {
                object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
            }
            object3 = object;
            Object object4 = Result.isFailure-impl((Object)object3) ? null : object3;
            if (object4 == null) continue;
            Object result = object4;
            Object object5 = result;
            if (object5 instanceof Collection) {
                return CollectionsKt.filterNotNull((Iterable)((Iterable)result));
            }
            if (!(object5 instanceof Iterable)) continue;
            return CollectionsKt.filterNotNull((Iterable)((Iterable)result));
        }
        return CollectionsKt.emptyList();
    }

    private final String resolveEntryDisplayName(Object entry) {
        String[] stringArray = new String[]{"getTabListDisplayName", "tabListDisplayName", "getDisplayName", "displayName"};
        List displayMethodNames = CollectionsKt.listOf((Object[])stringArray);
        for (String name : displayMethodNames) {
            Object value;
            String text;
            Object $this$resolveEntryDisplayName_u24lambda_u241;
            Object object;
            Object $this$firstOrNull$iv;
            block11: {
                Method[] methodArray = entry.getClass().getMethods();
                Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"getMethods(...)");
                $this$firstOrNull$iv = methodArray;
                boolean $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    Method m = (Method)element$iv;
                    boolean bl = false;
                    if (!(Intrinsics.areEqual((Object)m.getName(), (Object)name) && m.getParameterCount() == 0)) continue;
                    object = element$iv;
                    break block11;
                }
                object = null;
            }
            if ((Method)object == null) continue;
            $this$firstOrNull$iv = this;
            try {
                Method method;
                $this$resolveEntryDisplayName_u24lambda_u241 = (AutoLanternModule)$this$firstOrNull$iv;
                boolean bl = false;
                $this$resolveEntryDisplayName_u24lambda_u241 = Result.constructor-impl((Object)method.invoke(entry, new Object[0]));
            }
            catch (Throwable bl) {
                $this$resolveEntryDisplayName_u24lambda_u241 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
            }
            if (($this$resolveEntryDisplayName_u24lambda_u241 = (CharSequence)(text = this.coerceText(value = Result.isFailure-impl((Object)($this$firstOrNull$iv = $this$resolveEntryDisplayName_u24lambda_u241)) ? null : $this$firstOrNull$iv))) == null || StringsKt.isBlank((CharSequence)$this$resolveEntryDisplayName_u24lambda_u241)) continue;
            return text;
        }
        String[] stringArray2 = new String[]{"getProfile", "getGameProfile", "profile"};
        List profileMethodNames = CollectionsKt.listOf((Object[])stringArray2);
        for (String name : profileMethodNames) {
            Object object;
            Object object2;
            Object profile;
            block13: {
                Object $this$resolveEntryDisplayName_u24lambda_u243;
                Object object3;
                block12: {
                    Method[] methodArray = entry.getClass().getMethods();
                    Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"getMethods(...)");
                    Object[] $this$firstOrNull$iv = methodArray;
                    boolean $i$f$firstOrNull = false;
                    for (Object element$iv : $this$firstOrNull$iv) {
                        Method m = (Method)element$iv;
                        boolean bl = false;
                        if (!(Intrinsics.areEqual((Object)m.getName(), (Object)name) && m.getParameterCount() == 0)) continue;
                        object3 = element$iv;
                        break block12;
                    }
                    object3 = null;
                }
                if ((Method)object3 == null) continue;
                Object $i$f$firstOrNull = this;
                try {
                    Method method;
                    $this$resolveEntryDisplayName_u24lambda_u243 = $i$f$firstOrNull;
                    boolean bl = false;
                    $this$resolveEntryDisplayName_u24lambda_u243 = Result.constructor-impl((Object)method.invoke(entry, new Object[0]));
                }
                catch (Throwable bl) {
                    $this$resolveEntryDisplayName_u24lambda_u243 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
                }
                $i$f$firstOrNull = $this$resolveEntryDisplayName_u24lambda_u243;
                if ((Result.isFailure-impl((Object)$i$f$firstOrNull) ? null : $i$f$firstOrNull) == null) continue;
                profile = profile;
                Method[] methodArray = profile.getClass().getMethods();
                Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"getMethods(...)");
                Object[] $this$firstOrNull$iv = methodArray;
                boolean $i$f$firstOrNull2 = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    Method m = (Method)element$iv;
                    boolean bl = false;
                    if (!(Intrinsics.areEqual((Object)m.getName(), (Object)"getName") && m.getParameterCount() == 0)) continue;
                    object2 = element$iv;
                    break block13;
                }
                object2 = null;
            }
            if ((Method)object2 == null) continue;
            Object object4 = this;
            try {
                Method nameMethod;
                AutoLanternModule $this$resolveEntryDisplayName_u24lambda_u245 = object4;
                boolean bl = false;
                Object object5 = nameMethod.invoke(profile, new Object[0]);
                object = Result.constructor-impl((Object)(object5 instanceof String ? (String)object5 : null));
            }
            catch (Throwable throwable) {
                object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
            }
            object4 = object;
            String profileName = (String)(Result.isFailure-impl((Object)object4) ? null : object4);
            if ((object4 = (CharSequence)profileName) == null || StringsKt.isBlank((CharSequence)object4)) continue;
            return profileName;
        }
        return null;
    }

    private final String coerceText(Object value) {
        Object object;
        block7: {
            if (value == null) {
                return null;
            }
            if (value instanceof String) {
                return (String)value;
            }
            Method[] methodArray = value.getClass().getMethods();
            Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"getMethods(...)");
            Object[] $this$firstOrNull$iv = methodArray;
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                Method m = (Method)element$iv;
                boolean bl = false;
                if (!(Intrinsics.areEqual((Object)m.getName(), (Object)"getString") && m.getParameterCount() == 0)) continue;
                object = element$iv;
                break block7;
            }
            object = null;
        }
        Method textMethod = (Method)object;
        if (textMethod != null) {
            Object raw;
            Object object2;
            Object object3 = this;
            try {
                AutoLanternModule $this$coerceText_u24lambda_u241 = object3;
                boolean bl = false;
                object2 = Result.constructor-impl((Object)textMethod.invoke(value, new Object[0]));
            }
            catch (Throwable throwable) {
                object2 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
            }
            object3 = object2;
            Object object4 = raw = Result.isFailure-impl((Object)object3) ? null : object3;
            if (raw instanceof String) {
                return (String)raw;
            }
        }
        return value.toString();
    }

    private final int findLanternSlot(class_1657 player) {
        class_1661 class_16612 = player.method_31548();
        Intrinsics.checkNotNullExpressionValue((Object)class_16612, (String)"getInventory(...)");
        class_1661 inventory = class_16612;
        for (int i = 0; i < 9; ++i) {
            boolean bl;
            block4: {
                class_1799 stack;
                Intrinsics.checkNotNullExpressionValue((Object)inventory.method_5438(i), (String)"getItem(...)");
                if (stack.method_7960()) continue;
                String string = stack.method_7964().getString();
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
                String name = this.normalizeName(string);
                Iterable $this$any$iv = LANTERN_NAMES;
                boolean $i$f$any = false;
                if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                    bl = false;
                } else {
                    for (Object element$iv : $this$any$iv) {
                        String it = (String)element$iv;
                        boolean bl2 = false;
                        if (!StringsKt.contains$default((CharSequence)name, (CharSequence)it, (boolean)false, (int)2, null)) continue;
                        bl = true;
                        break block4;
                    }
                    bl = false;
                }
            }
            if (!bl) continue;
            return i;
        }
        return -1;
    }

    private final String normalizeName(String raw) {
        String string = class_124.method_539((String)raw);
        if (string == null) {
            string = raw;
        }
        CharSequence charSequence = string;
        Locale locale = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
        String string2 = charSequence.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
        charSequence = string2;
        Regex regex = new Regex("[^a-z0-9]+");
        String string3 = " ";
        return ((Object)StringsKt.trim((CharSequence)regex.replace(charSequence, string3))).toString();
    }

    private final void handlePlacementSequence() {
        class_746 player = AutoLanternModule.mc.field_1724;
        class_638 level2 = AutoLanternModule.mc.field_1687;
        if (player == null || level2 == null || AutoLanternModule.mc.field_1755 != null) {
            this.cancelPlacementSequence();
            return;
        }
        LanternPlacementPhase lanternPlacementPhase = pendingPlacementPhase;
        switch (lanternPlacementPhase == null ? -1 : WhenMappings.$EnumSwitchMapping$0[lanternPlacementPhase.ordinal()]) {
            case 1: {
                Rotation rotation = pendingPlaceRotation;
                if (rotation == null) {
                    AutoLanternModule $this$handlePlacementSequence_u24lambda_u240 = this;
                    boolean bl = false;
                    $this$handlePlacementSequence_u24lambda_u240.cancelPlacementSequence();
                    return;
                }
                Rotation target = rotation;
                int n = pendingPlacementTicks;
                pendingPlacementTicks = n + 1;
                RotationExecutor.INSTANCE.rotateTo(target, lanternPlaceStrategy);
                if (!this.hasReachedRotation((class_1657)player, target) && pendingPlacementTicks < 18) break;
                int slot = pendingLanternSlot;
                if (!(0 <= slot ? slot < 9 : false)) {
                    this.cancelPlacementSequence();
                    return;
                }
                if (player.method_31548().method_67532() != slot) {
                    InventoryUtils.holdHotbarSlot(slot);
                }
                pendingPlacementPhase = LanternPlacementPhase.WAIT_BEFORE_USE;
                pendingPlacementTicks = 0;
                break;
            }
            case 2: {
                int slot = pendingLanternSlot;
                if (!(0 <= slot ? slot < 9 : false)) {
                    this.cancelPlacementSequence();
                    return;
                }
                if (player.method_31548().method_67532() != slot) {
                    InventoryUtils.holdHotbarSlot(slot);
                }
                if (pendingPlacementTicks < (int)((Number)preUseDelayTicks.getValue()).doubleValue()) {
                    int n = pendingPlacementTicks;
                    pendingPlacementTicks = n + 1;
                    return;
                }
                this.useLantern((class_1657)player, (class_1937)level2);
                pendingPlacementPhase = LanternPlacementPhase.WAIT_AFTER_USE;
                pendingPlacementTicks = 0;
                break;
            }
            case 3: {
                if (pendingPlacementTicks < (int)((Number)postUseDelayTicks.getValue()).doubleValue()) {
                    int slot = pendingPlacementTicks;
                    pendingPlacementTicks = slot + 1;
                    return;
                }
                this.restorePendingSlot((class_1657)player);
                Rotation target = pendingReturnRotation;
                if (target != null && !this.hasReachedRotation((class_1657)player, target)) {
                    pendingPlacementPhase = LanternPlacementPhase.TURN_BACK;
                    pendingPlacementTicks = 0;
                    RotationExecutor.INSTANCE.rotateTo(target, lanternReturnStrategy);
                    break;
                }
                this.finishPlacementSequence();
                break;
            }
            case 4: {
                Rotation rotation = pendingReturnRotation;
                if (rotation == null) {
                    AutoLanternModule $this$handlePlacementSequence_u24lambda_u241 = this;
                    boolean bl = false;
                    $this$handlePlacementSequence_u24lambda_u241.finishPlacementSequence();
                    return;
                }
                Rotation target = rotation;
                int n = pendingPlacementTicks;
                pendingPlacementTicks = n + 1;
                RotationExecutor.INSTANCE.rotateTo(target, lanternReturnStrategy);
                if (!this.hasReachedRotation((class_1657)player, target) && pendingPlacementTicks < 16) break;
                this.finishPlacementSequence();
                break;
            }
            case -1: {
                return;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
    }

    private final boolean hasReachedRotation(class_1657 player, Rotation target) {
        float yawError = Math.abs(AngleUtils.INSTANCE.getRotationDelta(player.method_36454(), target.getYaw()));
        float pitchError = Math.abs(target.getPitch() - player.method_36455());
        return yawError <= 4.5f && pitchError <= 3.5f;
    }

    private final void finishPlacementSequence() {
        class_746 class_7462 = AutoLanternModule.mc.field_1724;
        if (class_7462 != null) {
            class_1657 p0 = (class_1657)class_7462;
            boolean bl = false;
            this.restorePendingSlot(p0);
        }
        pendingPlacementPhase = null;
        pendingPlacementTicks = 0;
        pendingPlaceRotation = null;
        pendingReturnRotation = null;
        pendingOriginalSlot = -1;
        pendingLanternSlot = -1;
        RotationExecutor.INSTANCE.stopRotating();
    }

    private final void cancelPlacementSequence() {
        AutoLanternModule.mc.field_1690.field_1904.method_23481(false);
        this.finishPlacementSequence();
    }

    private final void restorePendingSlot(class_1657 player) {
        int originalSlot = pendingOriginalSlot;
        boolean bl = 0 <= originalSlot ? originalSlot < 9 : false;
        if (bl && player.method_31548().method_67532() != originalSlot) {
            InventoryUtils.holdHotbarSlot(originalSlot);
        }
        pendingOriginalSlot = -1;
    }

    private final void useLantern(class_1657 player, class_1937 level2) {
        AutoLanternModule.mc.field_1690.field_1904.method_23481(false);
        MouseUtils.rightClick();
        long l = level2.method_75260();
        class_2338 class_23382 = player.method_24515();
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"blockPosition(...)");
        this.noteLanternUse(l, class_23382);
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue() && !MiningMacroModule.INSTANCE.isActive()) {
            return;
        }
        if (!lanternBuffActive) {
            return;
        }
        class_2338 class_23382 = lastUsePos;
        if (class_23382 == null) {
            return;
        }
        class_2338 pos = class_23382;
        class_638 class_6382 = AutoLanternModule.mc.field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        double cx = (double)pos.method_10263() + 0.5;
        double cy = (double)pos.method_10264() + 0.5;
        double cz = (double)pos.method_10260() + 0.5;
        double radius = ((Number)reUseDistance.getValue()).doubleValue();
        double glowRadius = radius + 0.18;
        int segments = 72;
        long now = System.currentTimeMillis();
        float pulse = (float)(Math.sin((double)now / 920.0 * Math.PI) * 0.5 + 0.5);
        float glowPulse = (float)(Math.sin((double)now / 650.0 * Math.PI) * 0.5 + 0.5);
        int baseAlpha = RangesKt.coerceIn((int)(92 + (int)(pulse * (float)108)), (int)0, (int)255);
        double rotAngle = (double)(now % 3000L) / 3000.0 * Math.PI * 2.0;
        double arcHalfSpan = 0.5235987755982988;
        this.drawLanternRing((class_1937)level2, cx, cy, cz, glowRadius, segments, rotAngle, arcHalfSpan, baseAlpha, glowPulse, 0.9f, true);
        this.drawLanternRing((class_1937)level2, cx, cy, cz, radius, segments, rotAngle, arcHalfSpan, baseAlpha + 24, pulse, 1.25f, false);
        OverlayRenderEngine.INSTANCE.render(event.getContext());
    }

    private final void drawLanternRing(class_1937 level2, double cx, double cy, double cz, double radius, int segments, double rotAngle, double arcHalfSpan, int baseAlpha, float pulse, float widthScale, boolean halo) {
        double prevX = cx + Math.cos(0.0) * radius;
        double prevZ = cz + Math.sin(0.0) * radius;
        int i = 1;
        if (i <= segments) {
            while (true) {
                int r;
                int alpha;
                boolean inArc;
                double angle = (double)i / (double)segments * Math.PI * 2.0;
                double nextX = cx + Math.cos(angle) * radius;
                double nextZ = cz + Math.sin(angle) * radius;
                double segMid = ((double)i - 0.5) / (double)segments * Math.PI * 2.0;
                double diff = Math.abs((segMid - rotAngle + Math.PI * 3) % (Math.PI * 2) - Math.PI);
                boolean bl = inArc = diff < arcHalfSpan;
                int n = inArc ? 255 : (alpha = RangesKt.coerceIn((int)(baseAlpha + (int)(pulse * (halo ? 30.0f : 18.0f))), (int)0, (int)255));
                int n2 = inArc ? 166 : (r = halo ? 104 : 82);
                int g = inArc ? 244 : (halo ? 220 : 204);
                float width = inArc ? 2.8f * widthScale : 1.4f * widthScale;
                OverlayRenderEngine.INSTANCE.addLine(level2, prevX, cy, prevZ, nextX, cy, nextZ, new OverlayRenderEngine.Color(r, g, 255, alpha), width, 2, "lantern-radius", true);
                prevX = nextX;
                prevZ = nextZ;
                if (i == segments) break;
                ++i;
            }
        }
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        enabled = new CheckboxSetting("Enabled", "Automatically use a lantern when the lantern buff is gone or you moved too far.", false);
        info = new InfoSetting("Auto Lantern", "Uses deployable lantern status first, with tab list fallback, then places a lantern from your hotbar when needed.", InfoType.INFO);
        requireMacro = new CheckboxSetting("Require Macro", "Only activate when Mining Macro is enabled.", true);
        cooldownTicks = new SliderSetting("Cooldown", "Minimum ticks to wait between lantern uses.", 80.0, 20.0, 600.0, 0.0, 32, null);
        reUseDistance = new SliderSetting("Reuse Distance", "Distance from last use spot before reusing regardless of buff status.", 14.0, 4.0, 48.0, 0.0, 32, null);
        preUseDelayTicks = new SliderSetting("Pre-Use Delay", "Ticks to wait after swapping to the lantern before using it.", 3.0, 0.0, 10.0, 1.0);
        postUseDelayTicks = new SliderSetting("Post-Use Delay", "Ticks to wait after using the lantern before restoring your slot.", 3.0, 0.0, 10.0, 1.0);
        Object[] objectArray = new Setting[]{enabled, info, requireMacro, cooldownTicks, reUseDistance, preUseDelayTicks, postUseDelayTicks};
        INSTANCE.addSetting((Setting<?>[])objectArray);
        EventBus.register(INSTANCE);
        objectArray = new String[]{"mithril lantern", "titanium lantern", "glacite lantern", "will o wisp"};
        LANTERN_NAMES = SetsKt.setOf((Object[])objectArray);
        lastUseTick = -1L;
        pendingOriginalSlot = -1;
        pendingLanternSlot = -1;
        lanternPlaceStrategy = new BezierTrackingRotationStrategy(17.0f, 11.0f, 0.18f, 0.9f, 0.24f, 0.45f, null, null, null, null, null, null, 4032, null);
        lanternReturnStrategy = new BezierTrackingRotationStrategy(15.0f, 9.0f, 0.18f, 0.9f, 0.22f, 0.45f, null, null, null, null, null, null, 4032, null);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007\u00a8\u0006\b"}, d2={"Lorg/cobalt/internal/mining/AutoLanternModule$LanternPlacementPhase;", "", "<init>", "(Ljava/lang/String;I)V", "TURN_TO_PLACE", "WAIT_BEFORE_USE", "WAIT_AFTER_USE", "TURN_BACK", "cobalt"})
    private static final class LanternPlacementPhase
    extends Enum<LanternPlacementPhase> {
        public static final /* enum */ LanternPlacementPhase TURN_TO_PLACE = new LanternPlacementPhase();
        public static final /* enum */ LanternPlacementPhase WAIT_BEFORE_USE = new LanternPlacementPhase();
        public static final /* enum */ LanternPlacementPhase WAIT_AFTER_USE = new LanternPlacementPhase();
        public static final /* enum */ LanternPlacementPhase TURN_BACK = new LanternPlacementPhase();
        private static final /* synthetic */ LanternPlacementPhase[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static LanternPlacementPhase[] values() {
            return (LanternPlacementPhase[])$VALUES.clone();
        }

        public static LanternPlacementPhase valueOf(String value) {
            return Enum.valueOf(LanternPlacementPhase.class, value);
        }

        @NotNull
        public static EnumEntries<LanternPlacementPhase> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = lanternPlacementPhaseArray = new LanternPlacementPhase[]{LanternPlacementPhase.TURN_TO_PLACE, LanternPlacementPhase.WAIT_BEFORE_USE, LanternPlacementPhase.WAIT_AFTER_USE, LanternPlacementPhase.TURN_BACK};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[LanternPlacementPhase.values().length];
            try {
                nArray[LanternPlacementPhase.TURN_TO_PLACE.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LanternPlacementPhase.WAIT_BEFORE_USE.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LanternPlacementPhase.WAIT_AFTER_USE.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LanternPlacementPhase.TURN_BACK.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

