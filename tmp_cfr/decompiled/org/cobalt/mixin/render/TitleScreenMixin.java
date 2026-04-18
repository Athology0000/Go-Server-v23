/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_332
 *  net.minecraft.class_442
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.cobalt.mixin.render;

import net.minecraft.class_332;
import net.minecraft.class_442;
import org.cobalt.internal.visual.TitleScreenRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_442.class})
public class TitleScreenMixin {
    @Inject(method={"method_25394"}, at={@At(value="HEAD")}, cancellable=true)
    private void customRender(class_332 guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        TitleScreenRenderer.INSTANCE.render(guiGraphics, mouseX, mouseY, partialTick);
        ci.cancel();
    }
}
