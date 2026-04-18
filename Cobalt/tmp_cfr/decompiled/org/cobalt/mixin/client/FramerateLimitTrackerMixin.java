/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_9919
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package org.cobalt.mixin.client;

import net.minecraft.class_9919;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={class_9919.class})
public class FramerateLimitTrackerMixin {
    private static final int MIN_FRAMERATE = 60;

    @Inject(method={"method_61937"}, at={@At(value="RETURN")}, cancellable=true)
    private void bypassThrottle(CallbackInfoReturnable<Integer> cir) {
        if ((Integer)cir.getReturnValue() < 60) {
            cir.setReturnValue((Object)60);
        }
    }
}

