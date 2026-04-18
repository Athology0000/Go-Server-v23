/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  net.minecraft.class_1799
 *  net.minecraft.class_1922
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2362
 *  net.minecraft.class_2374
 *  net.minecraft.class_2382
 *  net.minecraft.class_243
 *  net.minecraft.class_2487
 *  net.minecraft.class_2560
 *  net.minecraft.class_2577
 *  net.minecraft.class_2596
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_2708
 *  net.minecraft.class_2724
 *  net.minecraft.class_2741
 *  net.minecraft.class_2769
 *  net.minecraft.class_310
 *  net.minecraft.class_5498
 *  net.minecraft.class_634
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.etherwarp;

import java.util.Locale;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.minecraft.class_1799;
import net.minecraft.class_1922;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2362;
import net.minecraft.class_2374;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_2560;
import net.minecraft.class_2577;
import net.minecraft.class_2596;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2708;
import net.minecraft.class_2724;
import net.minecraft.class_2741;
import net.minecraft.class_2769;
import net.minecraft.class_310;
import net.minecraft.class_5498;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.MouseEvent;
import org.cobalt.api.event.impl.client.PacketEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.util.SkyblockItemUtilsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0096\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u0000\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u0004\u0018\u00010\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0017\u0010\n\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\u0007H\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\r\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\fH\u0007\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0017\u0010\u0010\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\u000fH\u0007\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u000f\u0010\u0012\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\u0012\u0010\u0003J!\u0010\u0017\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0018J'\u0010\u001d\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u001a\u001a\u00020\u00042\u0006\u0010\u001c\u001a\u00020\u001bH\u0002\u00a2\u0006\u0004\b\u001d\u0010\u001eJ1\u0010$\u001a\u0004\u0018\u00010\u00042\u0006\u0010 \u001a\u00020\u001f2\u0006\u0010!\u001a\u00020\u00042\u0006\u0010\"\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b$\u0010%J\u0017\u0010(\u001a\u00020\u00152\u0006\u0010'\u001a\u00020&H\u0002\u00a2\u0006\u0004\b(\u0010)J\u0017\u0010*\u001a\u00020\u00152\u0006\u0010'\u001a\u00020&H\u0002\u00a2\u0006\u0004\b*\u0010)J\u0019\u0010,\u001a\u00020\t2\b\b\u0002\u0010+\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b,\u0010-J\u000f\u0010/\u001a\u00020.H\u0002\u00a2\u0006\u0004\b/\u00100J\u000f\u00101\u001a\u00020.H\u0002\u00a2\u0006\u0004\b1\u00100J!\u00106\u001a\u00020\u001f2\b\u00103\u001a\u0004\u0018\u0001022\u0006\u00105\u001a\u000204H\u0002\u00a2\u0006\u0004\b6\u00107J\u0017\u00109\u001a\u00020\u001b2\u0006\u00108\u001a\u00020\u001bH\u0002\u00a2\u0006\u0004\b9\u0010:J\u0013\u0010;\u001a\u000204*\u000204H\u0002\u00a2\u0006\u0004\b;\u0010<J!\u0010@\u001a\u0004\u0018\u00018\u0000\"\u0004\b\u0000\u0010=2\b\u0010?\u001a\u0004\u0018\u00010>H\u0002\u00a2\u0006\u0004\b@\u0010AR\u0014\u0010B\u001a\u00020\u001f8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bB\u0010CR\u0014\u0010D\u001a\u00020\u001f8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bD\u0010CR\u0014\u0010E\u001a\u00020.8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\bE\u0010FR\u0014\u0010H\u001a\u00020G8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bH\u0010IR\u0014\u0010K\u001a\u00020J8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bK\u0010LR\u0014\u0010M\u001a\u00020J8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bM\u0010LR\u0014\u0010N\u001a\u00020J8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bN\u0010LR\u0014\u0010P\u001a\u00020O8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bP\u0010QR\u0016\u0010R\u001a\u00020.8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bR\u0010FR\u0018\u0010\"\u001a\u0004\u0018\u00010\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\"\u0010SR\u0018\u0010T\u001a\u0004\u0018\u00010\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bT\u0010SR\u0018\u0010U\u001a\u0004\u0018\u00010\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bU\u0010SR\u0016\u0010V\u001a\u00020.8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bV\u0010FR\u0016\u0010W\u001a\u00020\u001f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bW\u0010CR\u0016\u0010X\u001a\u00020.8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bX\u0010FR\u0016\u0010Y\u001a\u00020\u00158\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bY\u0010ZR\u0018\u0010\\\u001a\u0004\u0018\u00010[8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\\\u0010]\u00a8\u0006^"}, d2={"Lorg/cobalt/internal/etherwarp/SmoothAotvModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lnet/minecraft/class_243;", "interpolatedCameraPos", "()Lnet/minecraft/class_243;", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lorg/cobalt/api/event/impl/client/MouseEvent$RightClick;", "onRightClick", "(Lorg/cobalt/api/event/impl/client/MouseEvent$RightClick;)V", "Lorg/cobalt/api/event/impl/client/PacketEvent$Incoming;", "onPacket", "(Lorg/cobalt/api/event/impl/client/PacketEvent$Incoming;)V", "playerTeleported", "Lnet/minecraft/class_1799;", "stack", "", "isSneaking", "resolveTeleport", "(Lnet/minecraft/class_1799;Z)Lnet/minecraft/class_243;", "startEyePos", "rawVector", "", "eyeHeight", "centerTeleportVector", "(Lnet/minecraft/class_243;Lnet/minecraft/class_243;D)Lnet/minecraft/class_243;", "", "distance", "direction", "startPos", "isEtherwarp", "raycastTeleport", "(ILnet/minecraft/class_243;Lnet/minecraft/class_243;Z)Lnet/minecraft/class_243;", "Lnet/minecraft/class_2338;", "blockPos", "canTeleportThrough", "(Lnet/minecraft/class_2338;)Z", "isBlockFloor", "disableFurtherTeleports", "clearTeleportState", "(Z)V", "", "maxAddedLagMs", "()J", "getLatencyMs", "Lnet/minecraft/class_2487;", "attributes", "", "key", "readIntAttribute", "(Lnet/minecraft/class_2487;Ljava/lang/String;)I", "input", "roundToCenter", "(D)D", "normalizeTeleportName", "(Ljava/lang/String;)Ljava/lang/String;", "T", "", "value", "unwrapOptional", "(Ljava/lang/Object;)Ljava/lang/Object;", "DEFAULT_INSTANT_RANGE", "I", "DEFAULT_ETHERWARP_RANGE", "MAX_TELEPORT_TIME_MS", "J", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabledSetting", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "instantTransmission", "etherTransmission", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "maxAddedLag", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "startTimeMs", "Lnet/minecraft/class_243;", "cameraStartPos", "teleportVector", "currentTeleportPingMs", "teleportsAhead", "lastTeleportUpdateMs", "teleportDisabled", "Z", "Lnet/minecraft/class_638;", "lastKnownLevel", "Lnet/minecraft/class_638;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nSmoothAotvModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SmoothAotvModule.kt\norg/cobalt/internal/etherwarp/SmoothAotvModule\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,373:1\n1#2:374\n*E\n"})
public final class SmoothAotvModule
extends Module {
    @NotNull
    public static final SmoothAotvModule INSTANCE = new SmoothAotvModule();
    private static final int DEFAULT_INSTANT_RANGE = 8;
    private static final int DEFAULT_ETHERWARP_RANGE = 57;
    private static final long MAX_TELEPORT_TIME_MS = 2500L;
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final CheckboxSetting enabledSetting;
    @NotNull
    private static final CheckboxSetting instantTransmission;
    @NotNull
    private static final CheckboxSetting etherTransmission;
    @NotNull
    private static final SliderSetting maxAddedLag;
    private static long startTimeMs;
    @Nullable
    private static class_243 startPos;
    @Nullable
    private static class_243 cameraStartPos;
    @Nullable
    private static class_243 teleportVector;
    private static long currentTeleportPingMs;
    private static int teleportsAhead;
    private static long lastTeleportUpdateMs;
    private static boolean teleportDisabled;
    @Nullable
    private static class_638 lastKnownLevel;

    private SmoothAotvModule() {
        super("Smooth AOTV");
    }

    @Nullable
    public final class_243 interpolatedCameraPos() {
        if (!((Boolean)enabledSetting.getValue()).booleanValue()) {
            return null;
        }
        if (SmoothAotvModule.mc.field_1690.method_31044() != class_5498.field_26664) {
            return null;
        }
        class_243 class_2432 = teleportVector;
        if (class_2432 == null) {
            return null;
        }
        class_243 vector = class_2432;
        class_243 class_2433 = cameraStartPos;
        if (class_2433 == null) {
            return null;
        }
        class_243 startCamera = class_2433;
        if (teleportDisabled) {
            return null;
        }
        long now = System.currentTimeMillis();
        long timeoutMs = Math.min(Math.max(this.getLatencyMs(), currentTeleportPingMs) + this.maxAddedLagMs() * (long)RangesKt.coerceAtLeast((int)teleportsAhead, (int)1), 2500L);
        if (now - lastTeleportUpdateMs > timeoutMs) {
            this.clearTeleportState(true);
            return null;
        }
        long estimatedTeleportTime = RangesKt.coerceIn((long)currentTeleportPingMs, (long)40L, (long)2500L);
        long elapsed = now - startTimeMs;
        double percentage = RangesKt.coerceIn((double)((double)elapsed / (double)estimatedTeleportTime), (double)0.0, (double)1.0);
        if (teleportsAhead == 0 && elapsed >= estimatedTeleportTime + this.maxAddedLagMs()) {
            SmoothAotvModule.clearTeleportState$default(this, false, 1, null);
            return null;
        }
        return startCamera.method_1019(vector.method_1021(percentage));
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_638 currentLevel = SmoothAotvModule.mc.field_1687;
        if (currentLevel != lastKnownLevel) {
            SmoothAotvModule.clearTeleportState$default(this, false, 1, null);
            lastKnownLevel = currentLevel;
        }
        if (!((Boolean)enabledSetting.getValue()).booleanValue() || SmoothAotvModule.mc.field_1724 == null || currentLevel == null) {
            SmoothAotvModule.clearTeleportState$default(this, false, 1, null);
            return;
        }
        if (SmoothAotvModule.mc.field_1690.method_31044() != class_5498.field_26664) {
            SmoothAotvModule.clearTeleportState$default(this, false, 1, null);
            return;
        }
        this.interpolatedCameraPos();
    }

    @SubscribeEvent
    public final void onRightClick(@NotNull MouseEvent.RightClick event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabledSetting.getValue()).booleanValue() || teleportDisabled) {
            return;
        }
        class_746 class_7462 = SmoothAotvModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        if (SmoothAotvModule.mc.field_1687 == null) {
            return;
        }
        if (SmoothAotvModule.mc.field_1755 != null) {
            return;
        }
        if (SmoothAotvModule.mc.field_1690.method_31044() != class_5498.field_26664) {
            return;
        }
        class_1799 class_17992 = player.method_6047();
        Intrinsics.checkNotNullExpressionValue((Object)class_17992, (String)"getMainHandItem(...)");
        class_243 class_2432 = this.resolveTeleport(class_17992, player.method_5715());
        if (class_2432 == null) {
            return;
        }
        class_243 teleport = class_2432;
        class_243 nextStartPos = null;
        class_243 nextCameraStartPos = null;
        if (teleportsAhead == 0 || startPos == null || teleportVector == null) {
            class_243 class_2433 = player.method_33571();
            Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"getEyePosition(...)");
            nextStartPos = class_2433;
            class_243 class_2434 = player.method_33571();
            Intrinsics.checkNotNullExpressionValue((Object)class_2434, (String)"getEyePosition(...)");
            nextCameraStartPos = class_2434;
            lastTeleportUpdateMs = System.currentTimeMillis();
        } else {
            class_243 class_2435 = teleportVector;
            Intrinsics.checkNotNull((Object)class_2435);
            class_243 activeVector = class_2435;
            class_243 class_2436 = startPos;
            Intrinsics.checkNotNull((Object)class_2436);
            class_243 class_2437 = class_2436.method_1019(activeVector);
            Intrinsics.checkNotNullExpressionValue((Object)class_2437, (String)"add(...)");
            nextStartPos = class_2437;
            class_243 class_2438 = this.interpolatedCameraPos();
            if (class_2438 == null) {
                class_243 class_2439 = player.method_33571();
                class_2438 = class_2439;
                Intrinsics.checkNotNullExpressionValue((Object)class_2439, (String)"getEyePosition(...)");
            }
            nextCameraStartPos = class_2438;
        }
        startPos = nextStartPos;
        cameraStartPos = nextCameraStartPos;
        teleportVector = teleport;
        startTimeMs = System.currentTimeMillis();
        currentTeleportPingMs = RangesKt.coerceIn((long)this.getLatencyMs(), (long)40L, (long)2500L);
        ++teleportsAhead;
    }

    @SubscribeEvent
    public final void onPacket(@NotNull PacketEvent.Incoming event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_2596<?> class_25962 = event.getPacket();
        if (class_25962 instanceof class_2708) {
            this.playerTeleported();
        } else if (class_25962 instanceof class_2724) {
            SmoothAotvModule.clearTeleportState$default(this, false, 1, null);
        }
    }

    private final void playerTeleported() {
        teleportsAhead = RangesKt.coerceAtLeast((int)(teleportsAhead - 1), (int)0);
        lastTeleportUpdateMs = System.currentTimeMillis();
        teleportDisabled = false;
        if (teleportsAhead == 0) {
            long timeLeft = currentTeleportPingMs - (System.currentTimeMillis() - startTimeMs);
            if (timeLeft > 0L && timeLeft <= this.maxAddedLagMs()) {
                return;
            }
            SmoothAotvModule.clearTeleportState$default(this, false, 1, null);
        }
    }

    /*
     * Unable to fully structure code
     */
    private final class_243 resolveTeleport(class_1799 stack, boolean isSneaking) {
        block12: {
            if (stack.method_7960()) {
                return null;
            }
            attributes = SkyblockItemUtilsKt.getSkyblockExtraAttributes(stack);
            var5_4 = SkyblockItemUtilsKt.getSkyblockId(stack);
            if (StringsKt.isBlank((CharSequence)var5_4)) {
                $i$a$-ifBlank-SmoothAotvModule$resolveTeleport$itemId$1 = false;
                v0 = stack.method_7964().getString();
                Intrinsics.checkNotNullExpressionValue((Object)v0, (String)"getString(...)");
                v1 = SmoothAotvModule.INSTANCE.normalizeTeleportName(v0);
            } else {
                v1 = var5_4;
            }
            itemId = (String)v1;
            tunedTransmission = this.readIntAttribute(attributes, "tuned_transmission");
            hasEthermerge = this.readIntAttribute(attributes, "ethermerge") == 1;
            switch (itemId.hashCode()) {
                case 445185964: {
                    if (!itemId.equals("ASPECT_OF_THE_END")) {
                        break;
                    }
                    ** GOTO lbl26
                }
                case 730657499: {
                    if (!itemId.equals("WARPED_ASPECT_OF_THE_VOID")) {
                        break;
                    }
                    ** GOTO lbl26
                }
                case 916370627: {
                    if (!itemId.equals("ASPECT_OF_THE_VOID")) break;
lbl26:
                    // 3 sources

                    if (!isSneaking || !hasEthermerge || !((Boolean)SmoothAotvModule.etherTransmission.getValue()).booleanValue()) ** GOTO lbl29
                    v2 = 57 + tunedTransmission;
                    break block12;
lbl29:
                    // 1 sources

                    if (!((Boolean)SmoothAotvModule.instantTransmission.getValue()).booleanValue()) ** GOTO lbl32
                    v2 = 8;
                    break block12;
lbl32:
                    // 1 sources

                    return null;
                }
            }
            return null;
        }
        distance = v2;
        v3 = SmoothAotvModule.mc.field_1724;
        if (v3 == null) {
            return null;
        }
        player = v3;
        v4 = player.method_33571();
        Intrinsics.checkNotNullExpressionValue((Object)v4, (String)"getEyePosition(...)");
        startEyePos = v4;
        v5 = player.method_5828(1.0f);
        Intrinsics.checkNotNullExpressionValue((Object)v5, (String)"getViewVector(...)");
        look = v5;
        isEtherwarp = isSneaking != false && hasEthermerge != false;
        v6 = this.raycastTeleport(distance, look, startEyePos, isEtherwarp);
        if (v6 == null) {
            return null;
        }
        rawVector = v6;
        return this.centerTeleportVector(startEyePos, rawVector, player.method_5751());
    }

    private final class_243 centerTeleportVector(class_243 startEyePos, class_243 rawVector, double eyeHeight) {
        class_243 class_2432 = startEyePos.method_1019(rawVector);
        Intrinsics.checkNotNullExpressionValue((Object)class_2432, (String)"add(...)");
        class_243 predictedEnd = class_2432;
        class_243 centered = new class_243(this.roundToCenter(predictedEnd.field_1352), Math.ceil(predictedEnd.field_1351) + eyeHeight - 1.0, this.roundToCenter(predictedEnd.field_1350));
        class_243 class_2433 = centered.method_1020(startEyePos);
        Intrinsics.checkNotNullExpressionValue((Object)class_2433, (String)"subtract(...)");
        return class_2433;
    }

    private final class_243 raycastTeleport(int distance, class_243 direction, class_243 startPos, boolean isEtherwarp) {
        block17: {
            if (SmoothAotvModule.mc.field_1687 == null) {
                return null;
            }
            class_2338 xDiagonalOffset = direction.field_1352 > 0.0 ? new class_2338(1, 0, 0) : new class_2338(-1, 0, 0);
            class_2338 zDiagonalOffset = direction.field_1350 > 0.0 ? new class_2338(0, 0, 1) : new class_2338(0, 0, -1);
            int closeFloorY = Integer.MAX_VALUE;
            int offset = 0;
            if (offset > distance) break block17;
            while (true) {
                block20: {
                    class_2338 checkPos;
                    block22: {
                        class_243 pos;
                        block21: {
                            block18: {
                                block19: {
                                    class_243 justAhead;
                                    Intrinsics.checkNotNullExpressionValue((Object)startPos.method_1019(direction.method_1021((double)offset)), (String)"add(...)");
                                    Intrinsics.checkNotNullExpressionValue((Object)class_2338.method_49638((class_2374)((class_2374)pos)), (String)"containing(...)");
                                    if (!this.canTeleportThrough(checkPos)) {
                                        if (!isEtherwarp && offset == 0) {
                                            return null;
                                        }
                                        return isEtherwarp ? direction.method_1021((double)(offset - 1)).method_1019(direction) : direction.method_1021((double)(offset - 1));
                                    }
                                    class_2338 class_23382 = checkPos.method_10084();
                                    Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"above(...)");
                                    if (this.canTeleportThrough(class_23382) || isEtherwarp) break block18;
                                    if (offset != 0) break block19;
                                    Intrinsics.checkNotNullExpressionValue((Object)startPos.method_1019(direction.method_1021(0.2)), (String)"add(...)");
                                    if (!(justAhead.field_1351 - Math.floor(justAhead.field_1351) <= 0.495)) {
                                        return null;
                                    }
                                    break block20;
                                }
                                return direction.method_1021((double)(offset - 1));
                            }
                            if (offset != 0 && direction.field_1352 < 0.0) {
                                class_2338 class_23383 = checkPos.method_10078();
                                Intrinsics.checkNotNullExpressionValue((Object)class_23383, (String)"east(...)");
                                if (this.isBlockFloor(class_23383)) {
                                    class_2338 class_23384 = class_2338.method_49638((class_2374)((class_2374)pos.method_1020(direction))).method_10081((class_2382)zDiagonalOffset);
                                    Intrinsics.checkNotNullExpressionValue((Object)class_23384, (String)"offset(...)");
                                    if (this.isBlockFloor(class_23384)) {
                                        return direction.method_1021((double)(offset - 1));
                                    }
                                }
                            }
                            if (offset != 0 && direction.field_1350 < 0.0 && direction.field_1352 < 0.0) {
                                class_2338 class_23385 = checkPos.method_10072();
                                Intrinsics.checkNotNullExpressionValue((Object)class_23385, (String)"south(...)");
                                if (this.isBlockFloor(class_23385)) {
                                    class_2338 class_23386 = class_2338.method_49638((class_2374)((class_2374)pos.method_1020(direction))).method_10081((class_2382)xDiagonalOffset);
                                    Intrinsics.checkNotNullExpressionValue((Object)class_23386, (String)"offset(...)");
                                    if (this.isBlockFloor(class_23386)) {
                                        return direction.method_1021((double)(offset - 1));
                                    }
                                }
                            }
                            class_2338 class_23387 = checkPos.method_10074();
                            Intrinsics.checkNotNullExpressionValue((Object)class_23387, (String)"below(...)");
                            if (this.isBlockFloor(class_23387)) break block21;
                            class_2338 class_23388 = checkPos.method_10074().method_10081((class_2382)xDiagonalOffset);
                            Intrinsics.checkNotNullExpressionValue((Object)class_23388, (String)"offset(...)");
                            if (!this.isBlockFloor(class_23388)) break block22;
                            class_2338 class_23389 = checkPos.method_10074().method_10081((class_2382)zDiagonalOffset);
                            Intrinsics.checkNotNullExpressionValue((Object)class_23389, (String)"offset(...)");
                            if (!this.isBlockFloor(class_23389)) break block22;
                        }
                        if (pos.field_1351 - Math.floor(pos.field_1351) < 0.31) {
                            closeFloorY = checkPos.method_10264() - 1;
                        }
                    }
                    if (closeFloorY == checkPos.method_10264()) {
                        return direction.method_1021((double)(offset - 1));
                    }
                }
                if (offset == distance) break;
                ++offset;
            }
        }
        return direction.method_1021((double)distance);
    }

    private final boolean canTeleportThrough(class_2338 blockPos) {
        class_638 class_6382 = SmoothAotvModule.mc.field_1687;
        if (class_6382 == null) {
            return false;
        }
        class_638 level2 = class_6382;
        class_2680 class_26802 = level2.method_8320(blockPos);
        Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
        class_2680 state = class_26802;
        if (state.method_26215()) {
            return true;
        }
        class_2248 class_22482 = state.method_26204();
        Intrinsics.checkNotNullExpressionValue((Object)class_22482, (String)"getBlock(...)");
        class_2248 block = class_22482;
        class_265 class_2652 = state.method_26220((class_1922)level2, blockPos);
        Intrinsics.checkNotNullExpressionValue((Object)class_2652, (String)"getCollisionShape(...)");
        class_265 shape = class_2652;
        return shape.method_1110() || block instanceof class_2577 || block instanceof class_2362 || block instanceof class_2560 || Intrinsics.areEqual((Object)block, (Object)class_2246.field_10477) && ((Number)((Object)state.method_11654((class_2769)class_2741.field_12536))).intValue() <= 3;
    }

    private final boolean isBlockFloor(class_2338 blockPos) {
        class_638 class_6382 = SmoothAotvModule.mc.field_1687;
        if (class_6382 == null) {
            return false;
        }
        class_638 level2 = class_6382;
        class_2680 class_26802 = level2.method_8320(blockPos);
        Intrinsics.checkNotNullExpressionValue((Object)class_26802, (String)"getBlockState(...)");
        class_2680 state = class_26802;
        class_265 class_2652 = state.method_26220((class_1922)level2, blockPos);
        Intrinsics.checkNotNullExpressionValue((Object)class_2652, (String)"getCollisionShape(...)");
        class_265 shape = class_2652;
        if (shape.method_1110()) {
            return false;
        }
        return shape.method_1107().field_1325 >= 1.0 || Intrinsics.areEqual((Object)state.method_26204(), (Object)class_2246.field_37576);
    }

    private final void clearTeleportState(boolean disableFurtherTeleports) {
        startTimeMs = 0L;
        startPos = null;
        cameraStartPos = null;
        teleportVector = null;
        currentTeleportPingMs = 0L;
        teleportsAhead = 0;
        lastTeleportUpdateMs = 0L;
        teleportDisabled = disableFurtherTeleports;
    }

    static /* synthetic */ void clearTeleportState$default(SmoothAotvModule smoothAotvModule, boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            bl = false;
        }
        smoothAotvModule.clearTeleportState(bl);
    }

    private final long maxAddedLagMs() {
        return (long)((Number)maxAddedLag.getValue()).doubleValue();
    }

    private final long getLatencyMs() {
        class_746 class_7462 = SmoothAotvModule.mc.field_1724;
        if (class_7462 == null) {
            return 120L;
        }
        class_746 player = class_7462;
        class_634 class_6342 = mc.method_1562();
        return class_6342 != null && (class_6342 = class_6342.method_2871(player.method_5667())) != null ? RangesKt.coerceAtLeast((long)class_6342.method_2959(), (long)40L) : 120L;
    }

    private final int readIntAttribute(class_2487 attributes, String key) {
        Object object;
        int intValue;
        Object $this$readIntAttribute_u24lambda_u240;
        if (attributes == null || !attributes.method_10545(key)) {
            return 0;
        }
        Object object2 = this;
        SmoothAotvModule smoothAotvModule = this;
        try {
            $this$readIntAttribute_u24lambda_u240 = object2;
            boolean bl = false;
            $this$readIntAttribute_u24lambda_u240 = Result.constructor-impl((Object)attributes.method_10550(key));
        }
        catch (Throwable bl) {
            $this$readIntAttribute_u24lambda_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        object2 = $this$readIntAttribute_u24lambda_u240;
        Integer n = (Integer)smoothAotvModule.unwrapOptional(Result.isFailure-impl((Object)object2) ? null : object2);
        int n2 = intValue = n != null ? n : 0;
        if (intValue != 0) {
            return intValue;
        }
        object2 = this;
        SmoothAotvModule smoothAotvModule2 = this;
        try {
            SmoothAotvModule $this$readIntAttribute_u24lambda_u241 = (SmoothAotvModule)object2;
            boolean bl = false;
            object = Result.constructor-impl((Object)attributes.method_10571(key));
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object2 = object;
        Byte by = (Byte)smoothAotvModule2.unwrapOptional(Result.isFailure-impl((Object)object2) ? null : object2);
        return by != null ? by : (byte)0;
    }

    private final double roundToCenter(double input) {
        return Math.rint(input - 0.5) + 0.5;
    }

    private final String normalizeTeleportName(String $this$normalizeTeleportName) {
        String string = $this$normalizeTeleportName;
        Locale locale = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
        String string2 = string.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
        String stripped = ((Object)StringsKt.trim((CharSequence)string2)).toString();
        return StringsKt.contains$default((CharSequence)stripped, (CharSequence)"aspect of the void", (boolean)false, (int)2, null) ? "ASPECT_OF_THE_VOID" : (StringsKt.contains$default((CharSequence)stripped, (CharSequence)"aspect of the end", (boolean)false, (int)2, null) ? "ASPECT_OF_THE_END" : "");
    }

    private final <T> T unwrapOptional(Object value) {
        if (value == null) {
            return null;
        }
        Object object = value;
        return (T)(object instanceof Optional ? ((Optional)value).orElse(null) : (object instanceof OptionalInt ? (((OptionalInt)value).isPresent() ? (Object)((OptionalInt)value).orElse(0) : null) : (object instanceof OptionalLong ? (((OptionalLong)value).isPresent() ? (Object)((OptionalLong)value).orElse(0L) : null) : (object instanceof OptionalDouble ? (((OptionalDouble)value).isPresent() ? (Object)((OptionalDouble)value).orElse(0.0) : null) : value))));
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        enabledSetting = new CheckboxSetting("Enabled", "Predict and smooth first-person AOTE/AOTV teleports.", true);
        instantTransmission = new CheckboxSetting("Instant Transmission", "Animate normal AOTE/AOTV teleports.", true);
        etherTransmission = new CheckboxSetting("Ether Transmission", "Animate Ethermerge / Etherwarp teleports while sneaking.", true);
        maxAddedLag = new SliderSetting("Maximum Added Lag", "Extra time to keep the camera moving after the server teleport lands.", 90.0, 0.0, 250.0, 1.0);
        Setting[] settingArray = new Setting[]{enabledSetting, instantTransmission, etherTransmission, maxAddedLag};
        INSTANCE.addSetting(settingArray);
        EventBus.register(INSTANCE);
    }
}

