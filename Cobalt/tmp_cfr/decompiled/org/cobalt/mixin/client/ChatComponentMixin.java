/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_338
 *  net.minecraft.class_7469
 *  net.minecraft.class_7591
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.cobalt.mixin.client;

import net.minecraft.class_2561;
import net.minecraft.class_338;
import net.minecraft.class_7469;
import net.minecraft.class_7591;
import org.cobalt.internal.garden.managers.PestCleaningSequencer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_338.class})
public class ChatComponentMixin {
    @Inject(method={"method_1812"}, at={@At(value="TAIL")})
    private void cobalt$handleClientChatMessage(class_2561 message, CallbackInfo ci) {
        ChatComponentMixin.forwardToGarden(message);
    }

    @Inject(method={"method_44811"}, at={@At(value="TAIL")})
    private void cobalt$handleTaggedClientChatMessage(class_2561 message, class_7469 signature, class_7591 tag, CallbackInfo ci) {
        ChatComponentMixin.forwardToGarden(message);
    }

    private static void forwardToGarden(class_2561 message) {
        if (message == null) {
            return;
        }
        PestCleaningSequencer.INSTANCE.onChatMessage(message.getString());
    }
}

