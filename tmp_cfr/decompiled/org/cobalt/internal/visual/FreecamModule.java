/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_1297
 *  net.minecraft.class_1297$class_5529
 *  net.minecraft.class_1531
 *  net.minecraft.class_1937
 *  net.minecraft.class_310
 *  net.minecraft.class_5498
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.visual;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import net.minecraft.class_1297;
import net.minecraft.class_1531;
import net.minecraft.class_1937;
import net.minecraft.class_310;
import net.minecraft.class_5498;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.util.ChatUtils;
import org.cobalt.internal.visual.OrbitFreecamModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0080\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\n\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0017\u0010\n\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\u0007H\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\r\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\fH\u0007\u00a2\u0006\u0004\b\r\u0010\u000eJ\u001f\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u000f\u0010\u0015\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\u0015\u0010\u0003J\u0017\u0010\u0016\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0017\u0010\u001a\u001a\u00020\t2\u0006\u0010\u0019\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u0017\u0010\u001c\u001a\u00020\t2\u0006\u0010\u0019\u001a\u00020\u0018H\u0002\u00a2\u0006\u0004\b\u001c\u0010\u001bJ\u0017\u0010\u001d\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\b\u001d\u0010\u0017J\u0017\u0010 \u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u001eH\u0002\u00a2\u0006\u0004\b \u0010!R\u0014\u0010#\u001a\u00020\"8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b#\u0010$R\u0014\u0010&\u001a\u00020%8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b&\u0010'R\u0014\u0010)\u001a\u00020(8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b)\u0010*R\u0014\u0010+\u001a\u00020(8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b+\u0010*R\u0014\u0010,\u001a\u00020(8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b,\u0010*R\u0014\u0010-\u001a\u00020%8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b-\u0010'R\u0014\u0010/\u001a\u00020.8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b/\u00100R\u0016\u00101\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b1\u00102R\u0018\u00103\u001a\u0004\u0018\u00010\u00188\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b3\u00104R\u0018\u00106\u001a\u0004\u0018\u0001058\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b6\u00107R\u0018\u00109\u001a\u0004\u0018\u0001088\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b9\u0010:R\u0016\u0010<\u001a\u00020;8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b<\u0010=R\u0016\u0010>\u001a\u00020;8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b>\u0010=R\u0016\u0010?\u001a\u00020;8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b?\u0010=R\u0016\u0010@\u001a\u00020\u001e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b@\u0010AR\u0016\u0010B\u001a\u00020\u001e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bB\u0010AR\u0016\u0010C\u001a\u00020\u001e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bC\u0010AR\u0016\u0010D\u001a\u00020\u001e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bD\u0010A\u00a8\u0006E"}, d2={"Lorg/cobalt/internal/visual/FreecamModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "", "isEnabled", "()Z", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "", "onTickStart", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lorg/cobalt/api/event/impl/client/TickEvent$End;", "onTickEnd", "(Lorg/cobalt/api/event/impl/client/TickEvent$End;)V", "Lnet/minecraft/class_638;", "level", "Lnet/minecraft/class_746;", "player", "enableFreecam", "(Lnet/minecraft/class_638;Lnet/minecraft/class_746;)Z", "disableFreecam", "updateFreecamLook", "(Lnet/minecraft/class_746;)V", "Lnet/minecraft/class_1531;", "camera", "applyFreecamRotation", "(Lnet/minecraft/class_1531;)V", "updateFreecamMotion", "freezePlayer", "", "value", "wrapDegrees", "(F)F", "", "FREECAM_ENTITY_ID", "I", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "horizontalSpeed", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "verticalSpeed", "sprintMultiplier", "forceFirstPerson", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "active", "Z", "freecamEntity", "Lnet/minecraft/class_1531;", "Lnet/minecraft/class_1297;", "savedCameraEntity", "Lnet/minecraft/class_1297;", "Lnet/minecraft/class_5498;", "savedCameraType", "Lnet/minecraft/class_5498;", "", "savedPlayerX", "D", "savedPlayerY", "savedPlayerZ", "savedPlayerYaw", "F", "savedPlayerPitch", "freecamYaw", "freecamPitch", "cobalt"})
@SourceDebugExtension(value={"SMAP\nFreecamModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FreecamModule.kt\norg/cobalt/internal/visual/FreecamModule\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,275:1\n1#2:276\n*E\n"})
public final class FreecamModule
extends Module {
    @NotNull
    public static final FreecamModule INSTANCE = new FreecamModule();
    private static final int FREECAM_ENTITY_ID = -910002;
    @NotNull
    private static final CheckboxSetting enabled = new CheckboxSetting("Enabled", "Detach the camera and fly freely.", false);
    @NotNull
    private static final SliderSetting horizontalSpeed = new SliderSetting("Horizontal Speed", "Movement speed for forward/strafe.", 0.8, 0.1, 3.0, 0.0, 32, null);
    @NotNull
    private static final SliderSetting verticalSpeed = new SliderSetting("Vertical Speed", "Movement speed for jump/sneak.", 0.65, 0.05, 3.0, 0.0, 32, null);
    @NotNull
    private static final SliderSetting sprintMultiplier = new SliderSetting("Sprint Multiplier", "Speed multiplier while sprint is held.", 1.75, 1.0, 4.0, 0.0, 32, null);
    @NotNull
    private static final CheckboxSetting forceFirstPerson = new CheckboxSetting("Force First Person", "Use first-person camera mode while freecam is active.", true);
    @NotNull
    private static final class_310 mc;
    private static boolean active;
    @Nullable
    private static class_1531 freecamEntity;
    @Nullable
    private static class_1297 savedCameraEntity;
    @Nullable
    private static class_5498 savedCameraType;
    private static double savedPlayerX;
    private static double savedPlayerY;
    private static double savedPlayerZ;
    private static float savedPlayerYaw;
    private static float savedPlayerPitch;
    private static float freecamYaw;
    private static float freecamPitch;

    private FreecamModule() {
        super("Freecam");
    }

    public final boolean isEnabled() {
        return (Boolean)enabled.getValue();
    }

    @SubscribeEvent
    public final void onTickStart(@NotNull TickEvent.Start event) {
        class_638 level2;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            this.disableFreecam();
            return;
        }
        if (OrbitFreecamModule.INSTANCE.isEnabled()) {
            enabled.setValue(false);
            this.disableFreecam();
            return;
        }
        class_746 player = FreecamModule.mc.field_1724;
        class_638 class_6382 = FreecamModule.mc.field_1687;
        Object object = level2 = class_6382 instanceof class_638 ? class_6382 : null;
        if (player == null || level2 == null) {
            this.disableFreecam();
            return;
        }
        if (!active && !this.enableFreecam(level2, player)) {
            enabled.setValue(false);
            this.disableFreecam();
            ChatUtils.sendMessage("Freecam failed to start.");
            return;
        }
        class_1531 class_15312 = freecamEntity;
        if (class_15312 == null) {
            FreecamModule $this$onTickStart_u24lambda_u240 = this;
            boolean bl = false;
            enabled.setValue(false);
            $this$onTickStart_u24lambda_u240.disableFreecam();
            return;
        }
        class_1531 camera = class_15312;
        if (mc.method_1560() != camera) {
            enabled.setValue(false);
            this.disableFreecam();
            return;
        }
        this.updateFreecamLook(player);
        this.updateFreecamMotion(camera);
        this.applyFreecamRotation(camera);
        this.freezePlayer(player);
    }

    @SubscribeEvent
    public final void onTickEnd(@NotNull TickEvent.End event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!active) {
            return;
        }
        class_746 class_7462 = FreecamModule.mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        this.freezePlayer(player);
    }

    private final boolean enableFreecam(class_638 level2, class_746 player) {
        if (active) {
            return true;
        }
        class_1297 class_12972 = mc.method_1560();
        if (class_12972 == null) {
            class_12972 = (class_1297)player;
        }
        savedCameraEntity = class_12972;
        savedCameraType = FreecamModule.mc.field_1690.method_31044();
        savedPlayerX = player.method_23317();
        savedPlayerY = player.method_23318();
        savedPlayerZ = player.method_23321();
        savedPlayerYaw = player.method_36454();
        savedPlayerPitch = player.method_36455();
        freecamYaw = player.method_36454();
        freecamPitch = player.method_36455();
        level2.method_2945(-910002, class_1297.class_5529.field_26999);
        class_1531 camera = new class_1531((class_1937)level2, player.method_23317(), player.method_23320(), player.method_23321());
        camera.method_5838(-910002);
        camera.method_5875(true);
        camera.method_5648(true);
        camera.field_5960 = true;
        camera.method_5803(true);
        camera.method_36456(freecamYaw);
        camera.method_36457(freecamPitch);
        level2.method_53875((class_1297)camera);
        freecamEntity = camera;
        if (((Boolean)forceFirstPerson.getValue()).booleanValue()) {
            FreecamModule.mc.field_1690.method_31043(class_5498.field_26664);
        }
        mc.method_1504((class_1297)camera);
        active = true;
        return true;
    }

    private final void disableFreecam() {
        class_1297 restore;
        if (!active) {
            return;
        }
        class_1297 class_12972 = savedCameraEntity;
        if (class_12972 == null) {
            class_12972 = (class_1297)FreecamModule.mc.field_1724;
        }
        if ((restore = class_12972) != null) {
            mc.method_1504(restore);
        }
        class_5498 class_54982 = savedCameraType;
        if (class_54982 != null) {
            class_5498 it = class_54982;
            boolean bl = false;
            FreecamModule.mc.field_1690.method_31043(it);
        }
        class_638 class_6382 = FreecamModule.mc.field_1687;
        class_638 level2 = class_6382 instanceof class_638 ? class_6382 : null;
        class_1531 class_15312 = freecamEntity;
        if (class_15312 != null) {
            class_1531 camera = class_15312;
            boolean bl = false;
            class_638 class_6383 = level2;
            if (class_6383 != null) {
                class_6383.method_2945(camera.method_5628(), class_1297.class_5529.field_26999);
            }
        }
        freecamEntity = null;
        savedCameraEntity = null;
        savedCameraType = null;
        active = false;
    }

    private final void updateFreecamLook(class_746 player) {
        float yawDelta = this.wrapDegrees(player.method_36454() - savedPlayerYaw);
        float pitchDelta = player.method_36455() - savedPlayerPitch;
        if (!(yawDelta == 0.0f) || !(pitchDelta == 0.0f)) {
            freecamYaw += yawDelta;
            freecamPitch = RangesKt.coerceIn((float)(freecamPitch + pitchDelta), (float)-89.9f, (float)89.9f);
            player.method_36456(savedPlayerYaw);
            player.method_36457(savedPlayerPitch);
        }
    }

    private final void applyFreecamRotation(class_1531 camera) {
        camera.method_36456(freecamYaw);
        camera.method_36457(freecamPitch);
        camera.field_5982 = freecamYaw;
        camera.field_6004 = freecamPitch;
    }

    private final void updateFreecamMotion(class_1531 camera) {
        double forwardInput = (FreecamModule.mc.field_1690.field_1894.method_1434() ? 1.0 : 0.0) + (FreecamModule.mc.field_1690.field_1881.method_1434() ? -1.0 : 0.0);
        double strafeInput = (FreecamModule.mc.field_1690.field_1849.method_1434() ? 1.0 : 0.0) + (FreecamModule.mc.field_1690.field_1913.method_1434() ? -1.0 : 0.0);
        double moveForward = forwardInput;
        double moveStrafe = strafeInput;
        if (!(moveForward == 0.0) && !(moveStrafe == 0.0)) {
            double scale = 1.0 / Math.sqrt(2.0);
            moveForward *= scale;
            moveStrafe *= scale;
        }
        double yawRad = Math.toRadians(freecamYaw);
        double sinYaw = Math.sin(yawRad);
        double cosYaw = Math.cos(yawRad);
        double speedMul = FreecamModule.mc.field_1690.field_1867.method_1434() ? ((Number)sprintMultiplier.getValue()).doubleValue() : 1.0;
        double hSpeed = ((Number)horizontalSpeed.getValue()).doubleValue() * speedMul;
        double dx = (-sinYaw * moveForward + cosYaw * moveStrafe) * hSpeed;
        double dz = (cosYaw * moveForward + sinYaw * moveStrafe) * hSpeed;
        double dyUp = FreecamModule.mc.field_1690.field_1903.method_1434() ? ((Number)verticalSpeed.getValue()).doubleValue() : 0.0;
        double dyDown = FreecamModule.mc.field_1690.field_1832.method_1434() ? ((Number)verticalSpeed.getValue()).doubleValue() : 0.0;
        double dy = dyUp - dyDown;
        camera.field_5960 = true;
        camera.method_5814(camera.method_23317() + dx, camera.method_23318() + dy, camera.method_23321() + dz);
    }

    private final void freezePlayer(class_746 player) {
        player.method_18800(0.0, 0.0, 0.0);
        player.method_5814(savedPlayerX, savedPlayerY, savedPlayerZ);
        player.method_36456(savedPlayerYaw);
        player.method_36457(savedPlayerPitch);
        player.field_5982 = savedPlayerYaw;
        player.field_6004 = savedPlayerPitch;
        player.method_5847(savedPlayerYaw);
        player.field_6259 = savedPlayerYaw;
        player.field_6283 = savedPlayerYaw;
        player.field_6220 = savedPlayerYaw;
    }

    private final float wrapDegrees(float value) {
        float wrapped = value % 360.0f;
        if (wrapped >= 180.0f) {
            wrapped -= 360.0f;
        }
        if (wrapped < -180.0f) {
            wrapped += 360.0f;
        }
        return wrapped;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        Setting[] settingArray = new Setting[]{enabled, horizontalSpeed, verticalSpeed, sprintMultiplier, forceFirstPerson};
        INSTANCE.addSetting(settingArray);
        EventBus.register(INSTANCE);
    }
}

