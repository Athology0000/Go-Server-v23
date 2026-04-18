/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.random.Random
 *  kotlin.random.Random$Default
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_634
 *  net.minecraft.class_640
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.etherwarp;

import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.random.Random;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_640;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.MouseEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.util.TickScheduler;
import org.cobalt.internal.etherwarp.EtherwarpLogic;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000h\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\t\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\n\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\tH\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ\u000f\u0010\f\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\f\u0010\u0003J/\u0010\u0014\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0013\u001a\u00020\u0012H\u0002\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u001b\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u00120\u0016H\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u000f\u0010\u0019\u001a\u00020\u0012H\u0002\u00a2\u0006\u0004\b\u0019\u0010\u001aJ\u0017\u0010\u001c\u001a\u00020\u00102\u0006\u0010\u001b\u001a\u00020\u0012H\u0002\u00a2\u0006\u0004\b\u001c\u0010\u001dJ\u000f\u0010\u001e\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\u001e\u0010\u0003R\u0014\u0010 \u001a\u00020\u001f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b \u0010!R\u0014\u0010#\u001a\u00020\"8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b#\u0010$R\u0016\u0010&\u001a\u00020%8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b&\u0010'R\u0016\u0010(\u001a\u00020\u00108\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b(\u0010)R\u0016\u0010*\u001a\u00020\u00128\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b*\u0010+R\u0017\u0010-\u001a\u00020,8\u0006\u00a2\u0006\f\n\u0004\b-\u0010.\u001a\u0004\b/\u00100R\u0017\u00102\u001a\u0002018\u0006\u00a2\u0006\f\n\u0004\b2\u00103\u001a\u0004\b4\u00105R\u0017\u00106\u001a\u0002018\u0006\u00a2\u0006\f\n\u0004\b6\u00103\u001a\u0004\b7\u00105R\u0017\u00108\u001a\u00020,8\u0006\u00a2\u0006\f\n\u0004\b8\u0010.\u001a\u0004\b9\u00100\u00a8\u0006:"}, d2={"Lorg/cobalt/internal/etherwarp/LeftClickEtherwarpModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/TickEvent$End;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$End;)V", "Lorg/cobalt/api/event/impl/client/MouseEvent$LeftClick;", "onLeftClick", "(Lorg/cobalt/api/event/impl/client/MouseEvent$LeftClick;)V", "runEtherwarpSequence", "Lnet/minecraft/class_304;", "sneakKey", "useKey", "", "processingTicks", "", "id", "executeRightClick", "(Lnet/minecraft/class_304;Lnet/minecraft/class_304;JI)V", "Lkotlin/Pair;", "computeTimings", "()Lkotlin/Pair;", "getPing", "()I", "ms", "msToTicks", "(I)J", "releaseKeys", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lkotlin/random/Random$Default;", "rng", "Lkotlin/random/Random$Default;", "", "sequenceActive", "Z", "lastEtherwarp", "J", "sequenceId", "I", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "getEnabled", "()Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "sneakDelay", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "getSneakDelay", "()Lorg/cobalt/api/module/setting/impl/SliderSetting;", "processingTime", "getProcessingTime", "adaptivePing", "getAdaptivePing", "cobalt"})
public final class LeftClickEtherwarpModule
extends Module {
    @NotNull
    public static final LeftClickEtherwarpModule INSTANCE = new LeftClickEtherwarpModule();
    @NotNull
    private static final class_310 mc;
    @NotNull
    private static final Random.Default rng;
    private static boolean sequenceActive;
    private static long lastEtherwarp;
    private static int sequenceId;
    @NotNull
    private static final CheckboxSetting enabled;
    @NotNull
    private static final SliderSetting sneakDelay;
    @NotNull
    private static final SliderSetting processingTime;
    @NotNull
    private static final CheckboxSetting adaptivePing;

    private LeftClickEtherwarpModule() {
        super("Left Click Etherwarp");
    }

    @NotNull
    public final CheckboxSetting getEnabled() {
        return enabled;
    }

    @NotNull
    public final SliderSetting getSneakDelay() {
        return sneakDelay;
    }

    @NotNull
    public final SliderSetting getProcessingTime() {
        return processingTime;
    }

    @NotNull
    public final CheckboxSetting getAdaptivePing() {
        return adaptivePing;
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.End event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue() && sequenceActive) {
            this.releaseKeys();
            sequenceActive = false;
        }
    }

    @SubscribeEvent
    public final void onLeftClick(@NotNull MouseEvent.LeftClick event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            return;
        }
        if (sequenceActive) {
            return;
        }
        if (LeftClickEtherwarpModule.mc.field_1724 == null) {
            return;
        }
        if (LeftClickEtherwarpModule.mc.field_1687 == null) {
            return;
        }
        if (LeftClickEtherwarpModule.mc.field_1755 != null) {
            return;
        }
        if (!EtherwarpLogic.INSTANCE.holdingEtherwarpItem()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastEtherwarp < 500L) {
            return;
        }
        EtherwarpLogic.EtherPos etherResult = EtherwarpLogic.INSTANCE.getEtherwarpResultSneaking();
        if (etherResult.getPos() == null || !etherResult.getSucceeded()) {
            return;
        }
        lastEtherwarp = now;
        this.runEtherwarpSequence();
    }

    private final void runEtherwarpSequence() {
        if (sequenceActive) {
            return;
        }
        class_304 class_3042 = LeftClickEtherwarpModule.mc.field_1690.field_1832;
        if (class_3042 == null) {
            return;
        }
        class_304 sneakKey = class_3042;
        class_304 class_3043 = LeftClickEtherwarpModule.mc.field_1690.field_1904;
        if (class_3043 == null) {
            return;
        }
        class_304 useKey = class_3043;
        sequenceActive = true;
        int n = sequenceId;
        int id = sequenceId = n + 1;
        Pair<Integer, Integer> pair = this.computeTimings();
        int calculatedSneakDelay = ((Number)pair.component1()).intValue();
        int calculatedProcessingTime = ((Number)pair.component2()).intValue();
        long sneakDelayTicks = this.msToTicks(calculatedSneakDelay);
        long processingTicks = this.msToTicks(calculatedProcessingTime);
        sneakKey.method_23481(true);
        TickScheduler.schedule(sneakDelayTicks, () -> LeftClickEtherwarpModule.runEtherwarpSequence$lambda$0(id, sneakKey, useKey, processingTicks));
    }

    private final void executeRightClick(class_304 sneakKey, class_304 useKey, long processingTicks, int id) {
        if (!sequenceActive || id != sequenceId || !((Boolean)enabled.getValue()).booleanValue()) {
            this.releaseKeys();
            sequenceActive = false;
            return;
        }
        useKey.method_23481(true);
        TickScheduler.schedule(1L, () -> LeftClickEtherwarpModule.executeRightClick$lambda$0(useKey, processingTicks, id, sneakKey));
    }

    private final Pair<Integer, Integer> computeTimings() {
        Pair pair;
        int baseSneak = (int)((Number)sneakDelay.getValue()).doubleValue();
        int baseProcess = (int)((Number)processingTime.getValue()).doubleValue();
        if (((Boolean)adaptivePing.getValue()).booleanValue()) {
            int ping = this.getPing();
            int sneak = Math.max(30, baseSneak + ping / 2) + rng.nextInt(-5, 6);
            int process = Math.max(50, baseProcess + ping) + rng.nextInt(-10, 11);
            pair = TuplesKt.to((Object)sneak, (Object)process);
        } else {
            int sneak = baseSneak + rng.nextInt(-5, 6);
            int process = baseProcess + rng.nextInt(-10, 11);
            pair = TuplesKt.to((Object)sneak, (Object)process);
        }
        return pair;
    }

    private final int getPing() {
        int n;
        try {
            class_746 player = LeftClickEtherwarpModule.mc.field_1724;
            if (player != null && mc.method_1562() != null) {
                class_640 entry;
                class_634 class_6342 = mc.method_1562();
                class_640 class_6402 = entry = class_6342 != null ? class_6342.method_2871(player.method_5667()) : null;
                if (entry != null) {
                    return entry.method_2959();
                }
            }
            n = 50;
        }
        catch (Exception exception) {
            n = 50;
        }
        return n;
    }

    private final long msToTicks(int ms) {
        return Math.max(1L, (long)Math.ceil((double)ms / 50.0));
    }

    private final void releaseKeys() {
        try {
            class_304 class_3042 = LeftClickEtherwarpModule.mc.field_1690.field_1832;
            if (class_3042 != null) {
                class_3042.method_23481(false);
            }
            class_304 class_3043 = LeftClickEtherwarpModule.mc.field_1690.field_1904;
            if (class_3043 != null) {
                class_3043.method_23481(false);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static final void runEtherwarpSequence$lambda$0(int $id, class_304 $sneakKey, class_304 $useKey, long $processingTicks) {
        block3: {
            block2: {
                if (!sequenceActive || $id != sequenceId) break block2;
                if (((Boolean)enabled.getValue()).booleanValue()) break block3;
            }
            INSTANCE.releaseKeys();
            sequenceActive = false;
            return;
        }
        INSTANCE.executeRightClick($sneakKey, $useKey, $processingTicks, $id);
    }

    private static final void executeRightClick$lambda$0$0(int $id, class_304 $sneakKey) {
        if (!sequenceActive || $id != sequenceId) {
            INSTANCE.releaseKeys();
            sequenceActive = false;
            return;
        }
        $sneakKey.method_23481(false);
        sequenceActive = false;
    }

    private static final void executeRightClick$lambda$0(class_304 $useKey, long $processingTicks, int $id, class_304 $sneakKey) {
        $useKey.method_23481(false);
        TickScheduler.schedule($processingTicks, () -> LeftClickEtherwarpModule.executeRightClick$lambda$0$0($id, $sneakKey));
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        rng = Random.Default;
        enabled = new CheckboxSetting("Enabled", "Left click to etherwarp.", false);
        sneakDelay = new SliderSetting("Sneak Delay", "Delay after sneak before right-click (ms).", 50.0, 10.0, 200.0, 0.0, 32, null);
        processingTime = new SliderSetting("Processing Time", "Time to hold sneak after right-click (ms).", 100.0, 20.0, 500.0, 0.0, 32, null);
        adaptivePing = new CheckboxSetting("Adaptive Ping", "Adjust timing based on your ping.", true);
        Setting[] settingArray = new Setting[4];
        settingArray[0] = enabled;
        settingArray[1] = sneakDelay;
        settingArray[2] = processingTime;
        settingArray[3] = adaptivePing;
        INSTANCE.addSetting(settingArray);
        EventBus.register(INSTANCE);
    }
}

