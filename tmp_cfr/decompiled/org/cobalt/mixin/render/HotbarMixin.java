/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_329
 *  net.minecraft.class_332
 *  net.minecraft.class_9779
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.cobalt.mixin.render;

import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_9779;
import org.cobalt.internal.visual.HotbarOverlayModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_329.class})
public class HotbarMixin {
    @Inject(method={"method_1759"}, at={@At(value="HEAD")}, cancellable=true)
    private void cancelVanillaHotbar(class_332 graphics, class_9779 delta, CallbackInfo ci) {
        if (HotbarOverlayModule.INSTANCE.isEnabled()) {
            ci.cancel();
        }
    }
}

