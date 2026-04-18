/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Pair
 *  kotlin.collections.CollectionsKt
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 *  net.minecraft.class_1309
 *  net.minecraft.class_1937
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.diana;

import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import net.minecraft.class_1309;
import net.minecraft.class_1937;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_638;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.pathfinder.jni.NativePathfinder;
import org.cobalt.api.pathfinder.jni.PathStatus;
import org.cobalt.api.rotation.RotationExecutor;
import org.cobalt.api.rotation.strategy.BezierTrackingRotationStrategy;
import org.cobalt.api.util.AngleUtils;
import org.cobalt.api.util.ChatUtils;
import org.cobalt.api.util.player.MovementManager;
import org.cobalt.internal.diana.DianaParticleTracker;
import org.cobalt.internal.pathfinding.OverlayRenderEngine;
import org.cobalt.internal.rotation.RotationsModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000x\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001=B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u000f\u0010\u0006\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0006\u0010\u0003J\u0017\u0010\t\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u0007H\u0007\u00a2\u0006\u0004\b\t\u0010\nJ\u0017\u0010\f\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u000bH\u0007\u00a2\u0006\u0004\b\f\u0010\rR\u0014\u0010\u000f\u001a\u00020\u000e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0012\u001a\u00020\u00118F\u00a2\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013R\u0016\u0010\u0015\u001a\u00020\u00148\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0015\u0010\u0016R\u0016\u0010\u0017\u001a\u00020\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0017\u0010\u0018R\u0016\u0010\u001a\u001a\u00020\u00198\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u001bR\u0016\u0010\u001c\u001a\u00020\u00198\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u001bR\u0016\u0010\u001d\u001a\u00020\u00198\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u001bR\u0016\u0010\u001e\u001a\u00020\u00198\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u001bR\u0016\u0010\u001f\u001a\u00020\u00198\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001f\u0010\u001bR\u0018\u0010!\u001a\u0004\u0018\u00010 8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b!\u0010\"R\u0016\u0010#\u001a\u00020\u00198\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b#\u0010\u001bR\u0014\u0010%\u001a\u00020$8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b%\u0010&R\u0014\u0010(\u001a\u00020'8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b(\u0010)R\u0014\u0010*\u001a\u00020'8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b*\u0010)R\u0014\u0010+\u001a\u00020'8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b+\u0010)R\u0014\u0010,\u001a\u00020'8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b,\u0010)R\u0014\u0010-\u001a\u00020'8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b-\u0010)R\u0014\u00100\u001a\u00020\u00198BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b.\u0010/R\u0014\u00102\u001a\u00020\u00198BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b1\u0010/R\u001a\u00105\u001a\b\u0012\u0004\u0012\u000204038\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b5\u00106R\u0014\u00108\u001a\u0002078\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b8\u00109R\u0014\u0010;\u001a\u00020:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b;\u0010<\u00a8\u0006>"}, d2={"Lorg/cobalt/internal/diana/DianaMacroModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "", "cleanup", "stop", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;", "onRender", "(Lorg/cobalt/api/event/impl/render/WorldRenderEvent$Last;)V", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "", "isActive", "()Z", "Lorg/cobalt/internal/diana/DianaMacroModule$State;", "state", "Lorg/cobalt/internal/diana/DianaMacroModule$State;", "wasEnabled", "Z", "", "activatingTicksElapsed", "I", "collectTicksElapsed", "pathfindingTicksElapsed", "digTicksElapsed", "waitTicksElapsed", "Lnet/minecraft/class_243;", "burrowPos", "Lnet/minecraft/class_243;", "targetEntityId", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabledSetting", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "spadeSlotSetting", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "weaponSlotSetting", "collectDurationSetting", "postKillWaitSetting", "minParticlesSetting", "getSpadeSlot", "()I", "spadeSlot", "getWeaponSlot", "weaponSlot", "", "", "DIANA_MOB_NAMES", "Ljava/util/List;", "", "COMBAT_ROTATION_STEP_SCALE", "D", "Lorg/cobalt/api/rotation/strategy/BezierTrackingRotationStrategy;", "rotationStrategy", "Lorg/cobalt/api/rotation/strategy/BezierTrackingRotationStrategy;", "State", "cobalt"})
@SourceDebugExtension(value={"SMAP\nDianaModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DianaModule.kt\norg/cobalt/internal/diana/DianaMacroModule\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,306:1\n1#2:307\n296#3:308\n1807#3,3:309\n297#3:312\n*S KotlinDebug\n*F\n+ 1 DianaModule.kt\norg/cobalt/internal/diana/DianaMacroModule\n*L\n243#1:308\n245#1:309,3\n243#1:312\n*E\n"})
public final class DianaMacroModule
extends Module {
    @NotNull
    public static final DianaMacroModule INSTANCE = new DianaMacroModule();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static State state;
    private static boolean wasEnabled;
    private static int activatingTicksElapsed;
    private static int collectTicksElapsed;
    private static int pathfindingTicksElapsed;
    private static int digTicksElapsed;
    private static int waitTicksElapsed;
    @Nullable
    private static class_243 burrowPos;
    private static int targetEntityId;
    @NotNull
    private static final CheckboxSetting enabledSetting;
    @NotNull
    private static final SliderSetting spadeSlotSetting;
    @NotNull
    private static final SliderSetting weaponSlotSetting;
    @NotNull
    private static final SliderSetting collectDurationSetting;
    @NotNull
    private static final SliderSetting postKillWaitSetting;
    @NotNull
    private static final SliderSetting minParticlesSetting;
    @NotNull
    private static final List<String> DIANA_MOB_NAMES;
    private static final double COMBAT_ROTATION_STEP_SCALE = 0.62;
    @NotNull
    private static final BezierTrackingRotationStrategy rotationStrategy;

    private DianaMacroModule() {
        super("Diana Macro");
    }

    public final boolean isActive() {
        return (Boolean)enabledSetting.getValue();
    }

    private final int getSpadeSlot() {
        return (int)((Number)spadeSlotSetting.getValue()).doubleValue() - 1;
    }

    private final int getWeaponSlot() {
        return (int)((Number)weaponSlotSetting.getValue()).doubleValue() - 1;
    }

    private final void cleanup() {
        NativePathfinder.INSTANCE.stop();
        MovementManager.setMovementLock(false);
        RotationExecutor.INSTANCE.stopRotating();
        state = State.IDLE;
        burrowPos = null;
        targetEntityId = -1;
        activatingTicksElapsed = 0;
        collectTicksElapsed = 0;
        pathfindingTicksElapsed = 0;
        digTicksElapsed = 0;
        waitTicksElapsed = 0;
    }

    private final void stop() {
        this.cleanup();
    }

    /*
     * Unable to fully structure code
     */
    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)DianaMacroModule.enabledSetting.getValue()).booleanValue()) {
            if (DianaMacroModule.wasEnabled) {
                this.stop();
            }
            DianaMacroModule.wasEnabled = false;
            return;
        }
        DianaMacroModule.wasEnabled = true;
        v0 = DianaMacroModule.mc.field_1724;
        if (v0 == null) {
            $this$onTick_u24lambda_u240 = this;
            $i$a$-run-DianaMacroModule$onTick$player$1 = false;
            $this$onTick_u24lambda_u240.stop();
            DianaMacroModule.wasEnabled = false;
            return;
        }
        player = v0;
        v1 = DianaMacroModule.mc.field_1687;
        if (v1 == null) {
            $this$onTick_u24lambda_u241 = this;
            $i$a$-run-DianaMacroModule$onTick$level$1 = false;
            $this$onTick_u24lambda_u241.stop();
            DianaMacroModule.wasEnabled = false;
            return;
        }
        level = v1;
        switch (WhenMappings.$EnumSwitchMapping$1[DianaMacroModule.state.ordinal()]) {
            case 1: {
                DianaParticleTracker.INSTANCE.reset();
                player.method_31548().method_61496(this.getSpadeSlot());
                MovementManager.setMovementLock(true);
                MovementManager.setForcedMovement(false, false, false, false, false, false, false);
                MovementManager.forcedActionsEnabled = true;
                MovementManager.forcedUse = true;
                DianaMacroModule.activatingTicksElapsed = 0;
                DianaMacroModule.state = State.ACTIVATING_SPADE;
                break;
            }
            case 2: {
                $this$onTick_u24lambda_u241 = DianaMacroModule.activatingTicksElapsed;
                DianaMacroModule.activatingTicksElapsed = $this$onTick_u24lambda_u241 + 1;
                if (DianaMacroModule.activatingTicksElapsed < 3) {
                    MovementManager.forcedUse = true;
                    return;
                }
                MovementManager.forcedUse = false;
                DianaMacroModule.collectTicksElapsed = 0;
                DianaMacroModule.state = State.COLLECTING_PARTICLES;
                break;
            }
            case 3: {
                $this$onTick_u24lambda_u241 = DianaMacroModule.collectTicksElapsed;
                DianaMacroModule.collectTicksElapsed = $this$onTick_u24lambda_u241 + 1;
                if (DianaParticleTracker.INSTANCE.count() >= (int)((Number)DianaMacroModule.minParticlesSetting.getValue()).doubleValue() && (pos = DianaParticleTracker.INSTANCE.getBurrowPos((class_1937)level)) != null) {
                    DianaMacroModule.burrowPos = pos;
                    DianaMacroModule.pathfindingTicksElapsed = 0;
                    DianaMacroModule.state = State.PATHFINDING;
                    return;
                }
                if (DianaMacroModule.collectTicksElapsed < (int)((Number)DianaMacroModule.collectDurationSetting.getValue()).doubleValue()) break;
                DianaMacroModule.state = State.IDLE;
                break;
            }
            case 4: {
                v2 = DianaMacroModule.burrowPos;
                if (v2 == null) {
                    $this$onTick_u24lambda_u242 = this;
                    $i$a$-run-DianaMacroModule$onTick$bp$1 = false;
                    DianaMacroModule.state = State.IDLE;
                    return;
                }
                bp = v2;
                if (DianaMacroModule.pathfindingTicksElapsed == 0) {
                    NativePathfinder.INSTANCE.setTarget(bp.field_1352, bp.field_1351 + 1.0, bp.field_1350);
                }
                if ((DianaMacroModule.pathfindingTicksElapsed = ($i$a$-run-DianaMacroModule$onTick$level$1 = DianaMacroModule.pathfindingTicksElapsed) + 1) > 300) {
                    NativePathfinder.INSTANCE.stop();
                    MovementManager.setMovementLock(false);
                    ChatUtils.sendMessage("Diana: pathfinding timed out, retrying.");
                    DianaMacroModule.state = State.IDLE;
                    return;
                }
                cmd = NativePathfinder.INSTANCE.tick();
                if (cmd != null) {
                    cmd.applyToPlayer();
                } else {
                    switch (WhenMappings.$EnumSwitchMapping$0[NativePathfinder.INSTANCE.getStatus().ordinal()]) {
                        case 1: {
                            NativePathfinder.INSTANCE.stop();
                            bp2 = DianaMacroModule.burrowPos;
                            if (bp2 != null) {
                                DianaParticleTracker.INSTANCE.removeBurrow((int)Math.floor(bp2.field_1352), (int)Math.floor(bp2.field_1350));
                            }
                            player.method_31548().method_61496(this.getSpadeSlot());
                            DianaMacroModule.digTicksElapsed = 0;
                            DianaMacroModule.state = State.DIGGING;
                            break;
                        }
                        case 2: {
                            NativePathfinder.INSTANCE.stop();
                            MovementManager.setMovementLock(false);
                            ChatUtils.sendMessage("Diana: pathfinding failed, retrying.");
                            DianaMacroModule.state = State.IDLE;
                            break;
                        }
                        case 3: {
                            if (DianaMacroModule.pathfindingTicksElapsed <= 3) break;
                            NativePathfinder.INSTANCE.stop();
                            MovementManager.setMovementLock(false);
                            DianaMacroModule.state = State.IDLE;
                            break;
                        }
                        case 4: {
                            MovementManager.clearForcedMovement();
                        }
                    }
                }
                if (DianaMacroModule.state != State.PATHFINDING) break;
                v3 = new class_243(bp.field_1352, bp.field_1351 + 1.0, bp.field_1350);
                if (!(player.method_73189().method_1022(v3) <= 2.0)) break;
                NativePathfinder.INSTANCE.stop();
                DianaParticleTracker.INSTANCE.removeBurrow((int)Math.floor(bp.field_1352), (int)Math.floor(bp.field_1350));
                player.method_31548().method_61496(this.getSpadeSlot());
                DianaMacroModule.digTicksElapsed = 0;
                DianaMacroModule.state = State.DIGGING;
                break;
            }
            case 5: {
                v4 = DianaMacroModule.burrowPos;
                if (v4 == null) {
                    $this$onTick_u24lambda_u243 = this;
                    $i$a$-run-DianaMacroModule$onTick$bp$2 = false;
                    DianaMacroModule.state = State.IDLE;
                    return;
                }
                bp = v4;
                MovementManager.forcedActionsEnabled = true;
                MovementManager.setForcedMovement(false, false, false, false, false, false, false);
                MovementManager.forcedUse = true;
                cmd = DianaMacroModule.digTicksElapsed;
                DianaMacroModule.digTicksElapsed = cmd + 1;
                v5 = level.method_18467(class_1309.class, new class_238(bp, bp).method_1014(6.0));
                Intrinsics.checkNotNullExpressionValue((Object)v5, (String)"getEntitiesOfClass(...)");
                var7_26 = v5;
                $i$f$firstOrNull = false;
                for (T element$iv : $this$firstOrNull$iv) {
                    e = (class_1309)element$iv;
                    $i$a$-firstOrNull-DianaMacroModule$onTick$mob$1 = false;
                    Intrinsics.checkNotNullExpressionValue((Object)e.method_5476().getString(), (String)"getString(...)");
                    $this$any$iv = DianaMacroModule.DIANA_MOB_NAMES;
                    $i$f$any = false;
                    if (!($this$any$iv instanceof Collection) || !((Collection)$this$any$iv).isEmpty()) ** GOTO lbl141
                    v6 = false;
                    ** GOTO lbl149
lbl141:
                    // 2 sources

                    for (T element$iv : $this$any$iv) {
                        n = (String)element$iv;
                        $i$a$-any-DianaMacroModule$onTick$mob$1$1 = false;
                        if (!StringsKt.contains((CharSequence)name, (CharSequence)n, (boolean)true)) continue;
                        v6 = true;
                        ** GOTO lbl149
                    }
                    v6 = false;
lbl149:
                    // 3 sources

                    if (!v6) continue;
                    v7 = element$iv;
                    ** GOTO lbl153
                }
                v7 = null;
lbl153:
                // 2 sources

                mob = v7;
                if (mob != null) {
                    MovementManager.forcedUse = false;
                    DianaMacroModule.targetEntityId = mob.method_5628();
                    player.method_31548().method_61496(this.getWeaponSlot());
                    DianaMacroModule.state = State.COMBAT;
                    return;
                }
                if (DianaMacroModule.digTicksElapsed < 60) break;
                MovementManager.forcedUse = false;
                DianaParticleTracker.INSTANCE.removeBurrow((int)Math.floor(bp.field_1352), (int)Math.floor(bp.field_1350));
                ChatUtils.sendMessage("Diana: no mob spawned after digging, retrying.");
                DianaMacroModule.state = State.IDLE;
                break;
            }
            case 6: {
                MovementManager.forcedActionsEnabled = true;
                MovementManager.forcedAttack = false;
                target = level.method_8469(DianaMacroModule.targetEntityId);
                if (target == null) ** GOTO lbl178
                var7_27 = target;
                v8 = var7_27 instanceof class_1309 != false ? (class_1309)var7_27 : null;
                if (v8 != null ? v8.method_5805() : false) ** GOTO lbl181
lbl178:
                // 2 sources

                DianaMacroModule.waitTicksElapsed = 0;
                DianaMacroModule.state = State.WAITING;
                return;
lbl181:
                // 1 sources

                RotationExecutor.INSTANCE.rotateTo(AngleUtils.INSTANCE.getRotation(target), DianaMacroModule.rotationStrategy);
                MovementManager.setForcedMovement(false, false, false, false, false, false, false);
                MovementManager.forcedAttack = true;
                break;
            }
            case 7: {
                if (DianaMacroModule.waitTicksElapsed == 0) {
                    MovementManager.setMovementLock(false);
                }
                if ((DianaMacroModule.waitTicksElapsed = (var5_11 = DianaMacroModule.waitTicksElapsed) + 1) < (int)((Number)DianaMacroModule.postKillWaitSetting.getValue()).doubleValue()) break;
                DianaMacroModule.state = State.IDLE;
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
    }

    @SubscribeEvent
    public final void onRender(@NotNull WorldRenderEvent.Last event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (state != State.PATHFINDING && state != State.DIGGING && state != State.COMBAT) {
            return;
        }
        class_638 class_6382 = DianaMacroModule.mc.field_1687;
        if (class_6382 == null) {
            return;
        }
        class_638 level2 = class_6382;
        class_243 class_2432 = burrowPos;
        if (class_2432 == null) {
            return;
        }
        class_243 bp = class_2432;
        OverlayRenderEngine.addBox$default(OverlayRenderEngine.INSTANCE, (class_1937)level2, bp.field_1352 - 0.5, bp.field_1351, bp.field_1350 - 0.5, bp.field_1352 + 0.5, bp.field_1351 + 1.0, bp.field_1350 + 0.5, new OverlayRenderEngine.Color(255, 220, 0, 60), new OverlayRenderEngine.Color(255, 220, 0, 255), 0.0f, 10, "diana-macro-burrow", false, 4608, null);
    }

    private static final float rotationStrategy$lambda$0() {
        return (float)(RotationsModule.INSTANCE.sample((Pair<Double, Double>)((Pair)RotationsModule.INSTANCE.getCombatYawStep().getValue())) * 0.62);
    }

    private static final float rotationStrategy$lambda$1() {
        return (float)(RotationsModule.INSTANCE.sample((Pair<Double, Double>)((Pair)RotationsModule.INSTANCE.getCombatPitchStep().getValue())) * 0.62);
    }

    private static final float rotationStrategy$lambda$2() {
        return (float)((Number)RotationsModule.INSTANCE.getBezierCurveIn().getValue()).doubleValue();
    }

    private static final float rotationStrategy$lambda$3() {
        return (float)((Number)RotationsModule.INSTANCE.getBezierCurveOut().getValue()).doubleValue();
    }

    private static final float rotationStrategy$lambda$4() {
        return (float)((Number)RotationsModule.INSTANCE.getBezierMinScale().getValue()).doubleValue();
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        state = State.IDLE;
        targetEntityId = -1;
        enabledSetting = new CheckboxSetting("Enabled", "Start or stop the Diana macro.", false);
        spadeSlotSetting = new SliderSetting("Spade Slot", "Hotbar slot of the Ancestral Spade (1-9).", 1.0, 1.0, 9.0, 1.0);
        weaponSlotSetting = new SliderSetting("Weapon Slot", "Hotbar slot of your main weapon (1-9).", 2.0, 1.0, 9.0, 1.0);
        collectDurationSetting = new SliderSetting("Collect Duration", "Max ticks to wait for burrow particles before retrying.", 40.0, 5.0, 60.0, 1.0);
        postKillWaitSetting = new SliderSetting("Post-Kill Wait", "Ticks to wait after kill before looping.", 80.0, 20.0, 200.0, 1.0);
        minParticlesSetting = new SliderSetting("Min Particles", "Minimum CRIT packets required before pathfinding to burrow.", 2.0, 1.0, 10.0, 1.0);
        Object[] objectArray = new String[]{"Minotaur", "Minos Hunter", "Minos Champion", "Gaia Construct", "Minos Inquisitor"};
        DIANA_MOB_NAMES = CollectionsKt.listOf((Object[])objectArray);
        rotationStrategy = new BezierTrackingRotationStrategy(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, DianaMacroModule::rotationStrategy$lambda$0, DianaMacroModule::rotationStrategy$lambda$1, DianaMacroModule::rotationStrategy$lambda$2, DianaMacroModule::rotationStrategy$lambda$3, DianaMacroModule::rotationStrategy$lambda$4, null, 2111, null);
        objectArray = new Setting[]{enabledSetting, spadeSlotSetting, weaponSlotSetting, collectDurationSetting, postKillWaitSetting, minParticlesSetting};
        INSTANCE.addSetting((Setting<?>[])objectArray);
        EventBus.register(INSTANCE);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\n\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n\u00a8\u0006\u000b"}, d2={"Lorg/cobalt/internal/diana/DianaMacroModule$State;", "", "<init>", "(Ljava/lang/String;I)V", "IDLE", "ACTIVATING_SPADE", "COLLECTING_PARTICLES", "PATHFINDING", "DIGGING", "COMBAT", "WAITING", "cobalt"})
    private static final class State
    extends Enum<State> {
        public static final /* enum */ State IDLE = new State();
        public static final /* enum */ State ACTIVATING_SPADE = new State();
        public static final /* enum */ State COLLECTING_PARTICLES = new State();
        public static final /* enum */ State PATHFINDING = new State();
        public static final /* enum */ State DIGGING = new State();
        public static final /* enum */ State COMBAT = new State();
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
            $VALUES = stateArray = new State[]{State.IDLE, State.ACTIVATING_SPADE, State.COLLECTING_PARTICLES, State.PATHFINDING, State.DIGGING, State.COMBAT, State.WAITING};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;
        public static final /* synthetic */ int[] $EnumSwitchMapping$1;

        static {
            int[] nArray = new int[PathStatus.values().length];
            try {
                nArray[PathStatus.ARRIVED.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[PathStatus.FAILED.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[PathStatus.IDLE.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[PathStatus.PLANNING.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
            nArray = new int[State.values().length];
            try {
                nArray[State.IDLE.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.ACTIVATING_SPADE.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.COLLECTING_PARTICLES.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.PATHFINDING.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.DIGGING.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.COMBAT.ordinal()] = 6;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[State.WAITING.ordinal()] = 7;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$1 = nArray;
        }
    }
}

