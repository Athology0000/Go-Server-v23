package org.cobalt.mixin.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractButton.class)
public abstract class GlassButtonMixin {

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void renderGlass(GuiGraphics gg, int mx, int my, float dt, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        // TitleScreen is handled by TitleScreenRenderer; leave everything else.
        if (mc.screen == null || mc.screen instanceof TitleScreen) return;

        AbstractButton btn = (AbstractButton) (Object) this;
        ci.cancel();

        int x = btn.getX();
        int y = btn.getY();
        int w = btn.getWidth();
        int h = btn.getHeight();
        boolean hov = btn.isHoveredOrFocused();

        // -- Glass fill --------------------------------------------------------
        int fill   = hov ? (0x55 << 24) | 0x7A90B8 : (0x28 << 24) | 0x7A90B8;
        int shine  = 0x1EAACCEE;
        int border = hov ? (0xA0 << 24) | 0x99BBDD : (0x44 << 24) | 0x99BBDD;

        gg.fill(x, y, x + w, y + h, fill);
        // Specular streak across top ~38%
        gg.fill(x, y, x + w, y + (int)(h * 0.38f), shine);

        // -- 1-pixel border ----------------------------------------------------
        gg.fill(x,       y,       x + w, y + 1,     border);   // top
        gg.fill(x,       y + h-1, x + w, y + h,     border);   // bottom
        gg.fill(x,       y,       x + 1, y + h,     border);   // left
        gg.fill(x + w-1, y,       x + w, y + h,     border);   // right

        // -- Label -------------------------------------------------------------
        int textColor = hov ? 0xFFEEF4FF : 0xFFAABBCC;
        gg.drawCenteredString(mc.font, btn.getMessage(), x + w / 2, y + (h - 8) / 2, textColor);
    }
}
