/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10042
 *  net.minecraft.class_11659
 *  net.minecraft.class_11683$class_11792
 *  net.minecraft.class_12075
 *  net.minecraft.class_12249
 *  net.minecraft.class_1921
 *  net.minecraft.class_2960
 *  net.minecraft.class_4587
 *  net.minecraft.class_583
 *  net.minecraft.class_922
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.cobalt.mixin.render;

import net.minecraft.class_10042;
import net.minecraft.class_11659;
import net.minecraft.class_11683;
import net.minecraft.class_12075;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_583;
import net.minecraft.class_922;
import org.cobalt.internal.visual.MobEspModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_922.class})
public abstract class LivingEntityRendererMixin<S extends class_10042, M extends class_583<? super S>> {
    @Shadow
    protected M field_4737;

    @Shadow
    public abstract class_2960 method_3885(S var1);

    @Shadow
    protected abstract float method_23185(S var1);

    @Shadow
    protected abstract int method_62484(S var1);

    @Inject(method={"method_4054"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_4587;method_22909()V")})
    private void cobalt$submitMobEspFill(S state, class_4587 poseStack, class_11659 submitNodeCollector, class_12075 cameraRenderState, CallbackInfo ci) {
        if (!MobEspModule.shouldRenderFill(((class_10042)state).field_58171, ((class_10042)state).field_53333)) {
            return;
        }
        int fillTint = LivingEntityRendererMixin.multiplyArgb(MobEspModule.fillTintArgb(), this.method_62484(state));
        if ((fillTint >>> 24 & 0xFF) <= 0) {
            return;
        }
        int overlay = class_922.method_23622(state, (float)this.method_23185(state));
        class_1921 renderType = class_12249.method_75998((class_2960)this.method_3885(state));
        submitNodeCollector.method_73490(this.field_4737, state, poseStack, renderType, ((class_10042)state).field_61820, overlay, fillTint, null, 0, (class_11683.class_11792)null);
    }

    private static int multiplyArgb(int first, int second) {
        int a = (first >>> 24 & 0xFF) * (second >>> 24 & 0xFF) / 255;
        int r = (first >>> 16 & 0xFF) * (second >>> 16 & 0xFF) / 255;
        int g = (first >>> 8 & 0xFF) * (second >>> 8 & 0xFF) / 255;
        int b = (first & 0xFF) * (second & 0xFF) / 255;
        return a << 24 | r << 16 | g << 8 | b;
    }
}

