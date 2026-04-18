/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_757
 *  net.minecraft.class_9779
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.cobalt.mixin.render;

import net.minecraft.class_310;
import net.minecraft.class_757;
import net.minecraft.class_9779;
import org.cobalt.api.event.impl.render.GuiPostRenderEvent;
import org.cobalt.api.event.impl.render.NvgEvent;
import org.cobalt.internal.visual.DarkModeModule;
import org.cobalt.render.DarkModeRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_757.class})
public class GameRendererMixin {
    @Inject(method={"method_3192"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_11228;method_70879()V", shift=At.Shift.AFTER)})
    public void renderNvg(class_9779 counter, boolean tick, CallbackInfo callbackInfo) {
        new NvgEvent().post();
        new GuiPostRenderEvent().post();
    }

    @Inject(method={"method_3188"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_761;method_22710(Lnet/minecraft/class_9922;Lnet/minecraft/class_9779;ZLnet/minecraft/class_4184;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V", shift=At.Shift.AFTER)}, require=0)
    private void renderDarkModeAfterLevel(class_9779 deltaTracker, CallbackInfo ci) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || !mc.field_1690.method_31044().method_31034()) {
            return;
        }
        if (DarkModeModule.INSTANCE.isEnabled()) {
            DarkModeRenderer.renderDarkModeOverlay();
        }
    }
}

