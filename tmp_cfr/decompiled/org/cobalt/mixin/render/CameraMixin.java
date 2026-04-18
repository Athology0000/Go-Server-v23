/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1937
 *  net.minecraft.class_243
 *  net.minecraft.class_4184
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.cobalt.mixin.render;

import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_4184;
import org.cobalt.internal.etherwarp.SmoothAotvModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_4184.class})
public abstract class CameraMixin {
    @Shadow
    protected abstract void method_19322(class_243 var1);

    @Inject(method={"method_19321"}, at={@At(value="TAIL")})
    private void cobalt$applySmoothAotvCamera(class_1937 level2, class_1297 entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        class_243 override = SmoothAotvModule.INSTANCE.interpolatedCameraPos();
        if (override != null) {
            this.method_19322(override);
        }
    }
}

