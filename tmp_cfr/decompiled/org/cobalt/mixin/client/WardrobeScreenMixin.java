/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1703
 *  net.minecraft.class_332
 *  net.minecraft.class_465
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.cobalt.mixin.client;

import net.minecraft.class_1703;
import net.minecraft.class_332;
import net.minecraft.class_465;
import org.cobalt.internal.wardrobe.WardrobeModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_465.class})
public abstract class WardrobeScreenMixin<T extends class_1703> {
    @Inject(method={"method_25394"}, at={@At(value="HEAD")}, cancellable=true)
    private void cobalt$suppressWardrobeRender(class_332 graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (WardrobeModule.INSTANCE.shouldSuppressVanillaRender()) {
            ci.cancel();
        }
    }
}

