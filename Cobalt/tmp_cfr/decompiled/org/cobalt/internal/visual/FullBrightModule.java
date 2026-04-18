/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_1293
 *  net.minecraft.class_1294
 *  net.minecraft.class_310
 *  net.minecraft.class_315
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.visual;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_746;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.ModeSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.bridge.module.IFullBright;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\t\b\u0002\u00a2\u0006\u0004\b\u0003\u0010\u0004J\u0017\u0010\b\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\u0005H\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\u0017\u0010\f\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\f\u0010\rJ\u0017\u0010\u0010\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0017\u0010\u0012\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0012\u0010\u0011J\u000f\u0010\u0014\u001a\u00020\u0013H\u0016\u00a2\u0006\u0004\b\u0014\u0010\u0015R\u0014\u0010\u0017\u001a\u00020\u00168\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0017\u0010\u0018R\u0014\u0010\u001a\u001a\u00020\u00198\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u001bR\u0014\u0010\u001d\u001a\u00020\u001c8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u001eR\u0018\u0010 \u001a\u0004\u0018\u00010\u001f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b \u0010!R\u0016\u0010\"\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\"\u0010#R\u0016\u0010$\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b$\u0010#\u00a8\u0006%"}, d2={"Lorg/cobalt/internal/visual/FullBrightModule;", "Lorg/cobalt/api/module/Module;", "Lorg/cobalt/bridge/module/IFullBright;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/TickEvent$End;", "event", "", "onTick", "(Lorg/cobalt/api/event/impl/client/TickEvent$End;)V", "Lnet/minecraft/class_315;", "options", "restoreGamma", "(Lnet/minecraft/class_315;)V", "Lnet/minecraft/class_310;", "mc", "applyNightVision", "(Lnet/minecraft/class_310;)V", "removeNightVision", "", "isEnabled", "()Z", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "gamma", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "mode", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "", "previousGamma", "Ljava/lang/Double;", "wasEnabled", "Z", "appliedNightVision", "cobalt"})
public final class FullBrightModule
extends Module
implements IFullBright {
    @NotNull
    public static final FullBrightModule INSTANCE = new FullBrightModule();
    @NotNull
    private static final CheckboxSetting enabled = new CheckboxSetting("Enabled", "Force gamma to full bright.", false);
    @NotNull
    private static final SliderSetting gamma = new SliderSetting("Gamma", "Gamma override (0-1).", 1.0, 0.0, 1.0, 0.0, 32, null);
    @NotNull
    private static final ModeSetting mode;
    @Nullable
    private static Double previousGamma;
    private static boolean wasEnabled;
    private static boolean appliedNightVision;

    private FullBrightModule() {
        super("Full Bright");
    }

    @SubscribeEvent
    public final void onTick(@NotNull TickEvent.End event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        class_315 class_3152 = mc.field_1690;
        Intrinsics.checkNotNullExpressionValue((Object)class_3152, (String)"options");
        class_315 options = class_3152;
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            if (wasEnabled) {
                this.restoreGamma(options);
                this.removeNightVision(mc);
                wasEnabled = false;
            }
            return;
        }
        if (!wasEnabled) {
            Object object = options.method_42473().method_41753();
            Double d = object instanceof Double ? (Double)object : null;
            if (d == null) {
                d = 1.0;
            }
            previousGamma = d;
            wasEnabled = true;
        }
        switch (((Number)mode.getValue()).intValue()) {
            case 0: {
                options.method_42473().method_41748((Object)RangesKt.coerceIn((double)((Number)gamma.getValue()).doubleValue(), (double)0.0, (double)1.0));
                this.removeNightVision(mc);
                break;
            }
            case 1: {
                this.restoreGamma(options);
                this.applyNightVision(mc);
                break;
            }
            default: {
                options.method_42473().method_41748((Object)RangesKt.coerceIn((double)((Number)gamma.getValue()).doubleValue(), (double)0.0, (double)1.0));
                this.applyNightVision(mc);
            }
        }
    }

    private final void restoreGamma(class_315 options) {
        Double d = previousGamma;
        if (d == null) {
            return;
        }
        double prev = d;
        options.method_42473().method_41748((Object)RangesKt.coerceIn((double)prev, (double)0.0, (double)1.0));
        previousGamma = null;
    }

    private final void applyNightVision(class_310 mc) {
        class_746 class_7462 = mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        if (player.method_6059(class_1294.field_5925)) {
            appliedNightVision = false;
            return;
        }
        player.method_6092(new class_1293(class_1294.field_5925, 220, 0, true, false, false));
        appliedNightVision = true;
    }

    private final void removeNightVision(class_310 mc) {
        if (!appliedNightVision) {
            return;
        }
        class_746 class_7462 = mc.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        if (player.method_6059(class_1294.field_5925)) {
            player.method_6016(class_1294.field_5925);
        }
        appliedNightVision = false;
    }

    @Override
    public boolean isEnabled() {
        return (Boolean)enabled.getValue();
    }

    static {
        Object[] objectArray = new String[]{"Gamma", "Night Vision", "Both"};
        mode = new ModeSetting("Mode", "FullBright type.", 1, (String[])objectArray);
        objectArray = new Setting[]{enabled, gamma, mode};
        INSTANCE.addSetting((Setting<?>[])objectArray);
        EventBus.register(INSTANCE);
    }
}

