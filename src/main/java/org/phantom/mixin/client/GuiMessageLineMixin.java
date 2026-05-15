package org.phantom.mixin.client;

import net.minecraft.client.GuiMessage;
import net.minecraft.util.FormattedCharSequence;
import org.phantom.api.util.ChatUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiMessage.Line.class)
public class GuiMessageLineMixin {

  @Inject(method = "content", at = @At("RETURN"), cancellable = true)
  private void phantom$animateChatLineContent(CallbackInfoReturnable<FormattedCharSequence> cir) {
    FormattedCharSequence replacement = ChatUtils.animatedChatLineContent(cir.getReturnValue());
    if (replacement != null) {
      cir.setReturnValue(replacement);
    }
  }
}
