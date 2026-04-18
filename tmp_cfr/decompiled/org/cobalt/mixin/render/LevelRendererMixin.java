/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  net.minecraft.class_11658
 *  net.minecraft.class_12076
 *  net.minecraft.class_3695
 *  net.minecraft.class_4184
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597
 *  net.minecraft.class_4599
 *  net.minecraft.class_4604
 *  net.minecraft.class_761
 *  net.minecraft.class_9779
 *  net.minecraft.class_9922
 *  net.minecraft.class_9925
 *  net.minecraft.class_9975
 *  org.joml.Matrix4f
 *  org.joml.Vector4f
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.cobalt.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.class_11658;
import net.minecraft.class_12076;
import net.minecraft.class_3695;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4599;
import net.minecraft.class_4604;
import net.minecraft.class_761;
import net.minecraft.class_9779;
import net.minecraft.class_9922;
import net.minecraft.class_9925;
import net.minecraft.class_9975;
import org.cobalt.api.event.impl.render.WorldRenderContext;
import org.cobalt.api.event.impl.render.WorldRenderEvent;
import org.cobalt.internal.visual.SkyboxChangerModule;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_761.class})
public class LevelRendererMixin {
    @Unique
    private final WorldRenderContext ctx = new WorldRenderContext();
    @Shadow
    @Final
    private class_4599 field_20951;

    @Inject(method={"method_22710"}, at={@At(value="HEAD")})
    private void render(class_9922 allocator, class_9779 tickCounter, boolean renderBlockOutline, class_4184 camera, Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo callbackInfo) {
        this.ctx.setConsumers((class_4597)this.field_20951.method_23000());
        this.ctx.setCamera(camera);
        new WorldRenderEvent.Start(this.ctx).post();
    }

    @ModifyExpressionValue(method={"method_22710"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_761;method_32133(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/class_243;)Lnet/minecraft/class_4604;")})
    private class_4604 onSetupFrustum(class_4604 frustum) {
        this.ctx.setFrustum(frustum);
        return frustum;
    }

    @Inject(method={"method_62214"}, at={@At(value="RETURN")})
    private void postRender(GpuBufferSlice gpuBufferSlice, class_11658 levelRenderState, class_3695 profilerFiller, Matrix4f matrix4f, class_9925 resourceHandle, class_9925 resourceHandle2, boolean bl, class_9925 resourceHandle3, class_9925 resourceHandle4, CallbackInfo callbackInfo) {
        new WorldRenderEvent.Last(this.ctx).post();
    }

    @ModifyExpressionValue(method={"method_62214"}, at={@At(value="NEW", target="()Lnet/minecraft/class_4587;")})
    private class_4587 setInternalStack(class_4587 original) {
        this.ctx.setMatrixStack(original);
        return original;
    }

    @Inject(method={"method_62215"}, at={@At(value="HEAD")}, cancellable=true)
    private static void renderImportedSkybox(GpuBufferSlice fogBuffer, class_12076 skyRenderState, class_9975 skyRenderer, CallbackInfo callbackInfo) {
        if (SkyboxChangerModule.renderCustomSky(fogBuffer)) {
            callbackInfo.cancel();
        }
    }
}

