package org.phantom.mixin.render;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.input.KeyEvent;
import org.phantom.internal.dungeons.gambling.DungeonChestGamblingModule;
import org.phantom.render.LoadingScreenRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(method = "renderWithTooltipAndSubtitles", at = @At("HEAD"), cancellable = true)
    private void phantom$renderDungeonChestGambling(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;
        if (DungeonChestGamblingModule.INSTANCE.renderScreen(screen, guiGraphics)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void phantom$renderRiseBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;
        if (
            !(screen instanceof JoinMultiplayerScreen) &&
            !(screen instanceof ConnectScreen) &&
            !(screen instanceof ProgressScreen)
        ) {
            return;
        }

        LoadingScreenRenderer.renderBackground(1.0f);
        ci.cancel();
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void phantom$cancelDungeonChestGamblingKeys(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        if (DungeonChestGamblingModule.INSTANCE.onKeyPressed(input.key())) {
            cir.setReturnValue(true);
        }
    }
}
