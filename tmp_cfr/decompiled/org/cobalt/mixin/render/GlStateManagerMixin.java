/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.opengl.GlStateManager
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.cobalt.mixin.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import org.cobalt.api.util.ui.TextureTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GlStateManager.class})
public class GlStateManagerMixin {
    @Inject(method={"_bindTexture"}, at={@At(value="HEAD")}, remap=false)
    private static void onBindTexture(int texture, CallbackInfo callbackInfo) {
        TextureTracker.setPrevBoundTexture(texture);
    }

    @Inject(method={"_activeTexture"}, at={@At(value="HEAD")}, remap=false)
    private static void onActiveTexture(int texture, CallbackInfo callbackInfo) {
        TextureTracker.setPrevActiveTexture(texture);
    }
}

