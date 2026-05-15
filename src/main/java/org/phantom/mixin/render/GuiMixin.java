package org.phantom.mixin.render;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.scores.Objective;
import org.phantom.api.event.impl.render.GuiRenderEvent;
import org.phantom.internal.visual.CustomScoreboardModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

  @Inject(method = "render", at = @At("TAIL"))
  private void phantomRenderGui(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
    org.phantom.api.event.impl.render.GuiRenderContext.set(graphics, delta);
    new GuiRenderEvent(graphics, delta).post();
  }

  @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
  private void phantomRenderCustomScoreboard(GuiGraphics graphics, Objective objective, CallbackInfo ci) {
    if (CustomScoreboardModule.INSTANCE.renderCustomSidebar(graphics, objective)) {
      ci.cancel();
    }
  }
}
