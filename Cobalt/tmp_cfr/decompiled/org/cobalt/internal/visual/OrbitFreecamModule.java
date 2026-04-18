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
import org.cobalt.internal.visual.FreecamModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000r\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0006J\u0017\u0010\n\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\u0007H\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0017\u0010\u000e\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u000f\u0010\u0010\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\u0010\u0010\u0003R\u0014\u0010\u0012\u001a\u00020\u00118\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0013R\u0014\u0010\u0015\u001a\u00020\u00148\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0015\u0010\u0016R\u0014\u0010\u0018\u001a\u00020\u00178\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0019R\u0014\u0010\u001a\u001a\u00020\u00178\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u0019R\u0014\u0010\u001b\u001a\u00020\u00178\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u0019R\u0014\u0010\u001c\u001a\u00020\u00178\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u0019R\u0014\u0010\u001d\u001a\u00020\u00178\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u0019R\u0014\u0010\u001e\u001a\u00020\u00148\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u0016R\u0014\u0010 \u001a\u00020\u001f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b \u0010!R\u0018\u0010#\u001a\u0004\u0018\u00010\"8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b#\u0010$R\u0016\u0010%\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b%\u0010&R\u0018\u0010(\u001a\u0004\u0018\u00010'8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b(\u0010)R\u0018\u0010+\u001a\u0004\u0018\u00010*8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b+\u0010,R\u0016\u0010.\u001a\u00020-8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b.\u0010/R\u0016\u00101\u001a\u0002008\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b1\u00102\u00a8\u00063"}, d2={"Lorg/cobalt/internal/visual/OrbitFreecamModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "", "isEnabled", "()Z", "Lorg/cobalt/api/event/impl/client/TickEvent$Start;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$Start;)V", "Lnet/minecraft/class_638;", "level", "enableOrbit", "(Lnet/minecraft/class_638;)Z", "disableOrbit", "", "ORBIT_CAMERA_ID", "I", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "radius", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "height", "speed", "bobAmount", "bobSpeed", "forceFirstPerson", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lnet/minecraft/class_1531;", "orbitCamera", "Lnet/minecraft/class_1531;", "active", "Z", "Lnet/minecraft/class_1297;", "savedCameraEntity", "Lnet/minecraft/class_1297;", "Lnet/minecraft/class_5498;", "savedCameraType", "Lnet/minecraft/class_5498;", "", "angleRad", "D", "", "lastNs", "J", "cobalt"})
@SourceDebugExtension(value={"SMAP\nOrbitFreecamModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OrbitFreecamModule.kt\norg/cobalt/internal/visual/OrbitFreecamModule\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,225:1\n1#2:226\n*E\n"})
public final class OrbitFreecamModule
extends Module {
    @NotNull
    public static final OrbitFreecamModule INSTANCE = new OrbitFreecamModule();
    private static final int ORBIT_CAMERA_ID = -910001;
    @NotNull
    private static final CheckboxSetting enabled = new CheckboxSetting("Enabled", "Orbit a free camera around your player.", false);
    @NotNull
    private static final SliderSetting radius = new SliderSetting("Radius", "Orbit radius around your player.", 5.0, 1.5, 16.0, 0.0, 32, null);
    @NotNull
    private static final SliderSetting height = new SliderSetting("Height", "Vertical camera offset from your eye level.", 1.2, -2.0, 8.0, 0.0, 32, null);
    @NotNull
    private static final SliderSetting speed = new SliderSetting("Speed", "Orbit speed in degrees per second.", 18.0, 2.0, 120.0, 0.0, 32, null);
    @NotNull
    private static final SliderSetting bobAmount = new SliderSetting("Bob Amount", "Subtle vertical bob while orbiting.", 0.25, 0.0, 2.0, 0.0, 32, null);
    @NotNull
    private static final SliderSetting bobSpeed = new SliderSetting("Bob Speed", "Bob oscillation speed.", 0.8, 0.0, 6.0, 0.0, 32, null);
    @NotNull
    private static final CheckboxSetting forceFirstPerson = new CheckboxSetting("Force First Person", "Use first-person camera mode for clean freecam.", true);
    @NotNull
    private static final class_310 mc;
    @Nullable
    private static class_1531 orbitCamera;
    private static boolean active;
    @Nullable
    private static class_1297 savedCameraEntity;
    @Nullable
    private static class_5498 savedCameraType;
    private static double angleRad;
    private static long lastNs;

    private OrbitFreecamModule() {
        super("Orbit Freecam");
    }

    public final boolean isEnabled() {
        return (Boolean)enabled.getValue();
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.Start event) {
        class_638 level2;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            this.disableOrbit();
            return;
        }
        if (FreecamModule.INSTANCE.isEnabled()) {
            enabled.setValue(false);
            this.disableOrbit();
            return;
        }
        class_746 player = OrbitFreecamModule.mc.field_1724;
        class_638 class_6382 = OrbitFreecamModule.mc.field_1687;
        Object object = level2 = class_6382 instanceof class_638 ? class_6382 : null;
        if (player == null || level2 == null) {
            this.disableOrbit();
            return;
        }
        if (!active && !this.enableOrbit(level2)) {
            enabled.setValue(false);
            this.disableOrbit();
            ChatUtils.sendMessage("Orbit freecam failed to start.");
            return;
        }
        class_1531 class_15312 = orbitCamera;
        if (class_15312 == null) {
            OrbitFreecamModule $this$onTick_u24lambda_u240 = this;
            boolean bl = false;
            enabled.setValue(false);
            $this$onTick_u24lambda_u240.disableOrbit();
            return;
        }
        class_1531 camera = class_15312;
        if (mc.method_1560() != camera) {
            enabled.setValue(false);
            this.disableOrbit();
            return;
        }
        long now = System.nanoTime();
        double dt = lastNs == 0L ? 0.05 : RangesKt.coerceIn((double)((double)(now - lastNs) / 1.0E9), (double)0.004166666666666667, (double)0.1);
        lastNs = now;
        if ((angleRad += ((Number)speed.getValue()).doubleValue() * Math.PI / 180.0 * dt) > Math.PI * 2) {
            angleRad -= Math.PI * 2;
        }
        double centerX = player.method_23317();
        double centerY = player.method_23320();
        double centerZ = player.method_23321();
        double orbitY = centerY + ((Number)height.getValue()).doubleValue() + Math.sin(angleRad * ((Number)bobSpeed.getValue()).doubleValue()) * ((Number)bobAmount.getValue()).doubleValue();
        double orbitX = centerX + Math.cos(angleRad) * ((Number)radius.getValue()).doubleValue();
        double orbitZ = centerZ + Math.sin(angleRad) * ((Number)radius.getValue()).doubleValue();
        camera.method_5814(orbitX, orbitY, orbitZ);
        double lookX = centerX - orbitX;
        double lookY = centerY - orbitY;
        double lookZ = centerZ - orbitZ;
        double horiz = RangesKt.coerceAtLeast((double)Math.sqrt(lookX * lookX + lookZ * lookZ), (double)1.0E-4);
        float yaw = (float)Math.toDegrees(Math.atan2(lookZ, lookX)) - 90.0f;
        float pitch = RangesKt.coerceIn((float)((float)(-Math.toDegrees(Math.atan2(lookY, horiz)))), (float)-89.9f, (float)89.9f);
        camera.method_36456(yaw);
        camera.method_36457(pitch);
        camera.field_5982 = yaw;
        camera.field_6004 = pitch;
    }

    private final boolean enableOrbit(class_638 level2) {
        if (active) {
            return true;
        }
        class_746 class_7462 = OrbitFreecamModule.mc.field_1724;
        if (class_7462 == null) {
            return false;
        }
        class_746 player = class_7462;
        class_1297 class_12972 = mc.method_1560();
        if (class_12972 == null) {
            class_12972 = (class_1297)player;
        }
        savedCameraEntity = class_12972;
        savedCameraType = OrbitFreecamModule.mc.field_1690.method_31044();
        level2.method_2945(-910001, class_1297.class_5529.field_26999);
        class_1531 anchor = new class_1531((class_1937)level2, player.method_23317(), player.method_23320(), player.method_23321());
        anchor.method_5838(-910001);
        anchor.method_5875(true);
        anchor.method_5648(true);
        anchor.field_5960 = true;
        anchor.method_5803(true);
        anchor.method_36456(player.method_36454());
        anchor.method_36457(player.method_36455());
        level2.method_53875((class_1297)anchor);
        orbitCamera = anchor;
        if (((Boolean)forceFirstPerson.getValue()).booleanValue()) {
            OrbitFreecamModule.mc.field_1690.method_31043(class_5498.field_26664);
        }
        mc.method_1504((class_1297)anchor);
        active = true;
        lastNs = 0L;
        return true;
    }

    private final void disableOrbit() {
        class_1297 restore;
        if (!active) {
            return;
        }
        class_1297 class_12972 = savedCameraEntity;
        if (class_12972 == null) {
            class_12972 = (class_1297)OrbitFreecamModule.mc.field_1724;
        }
        if ((restore = class_12972) != null) {
            mc.method_1504(restore);
        }
        class_5498 class_54982 = savedCameraType;
        if (class_54982 != null) {
            class_5498 it = class_54982;
            boolean bl = false;
            OrbitFreecamModule.mc.field_1690.method_31043(it);
        }
        class_638 class_6382 = OrbitFreecamModule.mc.field_1687;
        class_638 level2 = class_6382 instanceof class_638 ? class_6382 : null;
        class_1531 class_15312 = orbitCamera;
        if (class_15312 != null) {
            class_1531 camera = class_15312;
            boolean bl = false;
            class_638 class_6383 = level2;
            if (class_6383 != null) {
                class_6383.method_2945(camera.method_5628(), class_1297.class_5529.field_26999);
            }
        }
        orbitCamera = null;
        savedCameraEntity = null;
        savedCameraType = null;
        active = false;
        angleRad = 0.0;
        lastNs = 0L;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        Setting[] settingArray = new Setting[]{enabled, radius, height, speed, bobAmount, bobSpeed, forceFirstPerson};
        INSTANCE.addSetting(settingArray);
        EventBus.register(INSTANCE);
    }
}

