/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package org.cobalt.mixin.client;

import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import org.cobalt.api.event.impl.client.BlockChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={class_1937.class})
abstract class ClientPlayerInteractionManagerMixin {
    ClientPlayerInteractionManagerMixin() {
    }

    @Inject(method={"method_30092"}, at={@At(value="HEAD")})
    private void onBlockChange(class_2338 blockPos, class_2680 newBlockState, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (class_310.method_1551().field_1687 != this) {
            return;
        }
        class_2680 oldBlockState = ((class_1937)this).method_8320(blockPos);
        if (oldBlockState.method_26204() != newBlockState.method_26204()) {
            new BlockChangeEvent(blockPos.method_10062(), oldBlockState, newBlockState).post();
        }
    }
}

