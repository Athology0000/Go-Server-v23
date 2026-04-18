/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_4264
 *  net.minecraft.class_442
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.cobalt.mixin.render;

import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4264;
import net.minecraft.class_442;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_4264.class})
public abstract class GlassButtonMixin {
    @Inject(method={"method_48579"}, at={@At(value="HEAD")}, cancellable=true)
    private void renderGlass(class_332 gg, int mx, int my, float dt, CallbackInfo ci) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1755 == null || mc.field_1755 instanceof class_442) {
            return;
        }
        class_4264 btn = (class_4264)this;
        ci.cancel();
        int x = btn.method_46426();
        int y = btn.method_46427();
        int w = btn.method_25368();
        int h = btn.method_25364();
        boolean hov = btn.method_25367();
        int fill = hov ? 1434095800 : 679121080;
        int shine = 514510062;
        int border = hov ? -1600537635 : 1150925789;
        gg.method_25294(x, y, x + w, y + h, fill);
        gg.method_25294(x, y, x + w, y + (int)((float)h * 0.38f), shine);
        gg.method_25294(x, y, x + w, y + 1, border);
        gg.method_25294(x, y + h - 1, x + w, y + h, border);
        gg.method_25294(x, y, x + 1, y + h, border);
        gg.method_25294(x + w - 1, y, x + w, y + h, border);
        int textColor = hov ? -1116929 : -5588020;
        gg.method_27534(mc.field_1772, btn.method_25369(), x + w / 2, y + (h - 8) / 2, textColor);
    }
}

