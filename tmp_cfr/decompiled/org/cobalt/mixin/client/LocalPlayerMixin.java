/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_243
 *  net.minecraft.class_746
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package org.cobalt.mixin.client;

import net.minecraft.class_243;
import net.minecraft.class_746;
import org.cobalt.internal.dungeons.DungeonsModule;
import org.cobalt.internal.qol.ItemLockingModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={class_746.class})
public class LocalPlayerMixin {
    @Inject(method={"method_6007"}, at={@At(value="TAIL")})
    private void cobalt$cancelVelocityForBonzoStaff(CallbackInfo ci) {
        if (!DungeonsModule.INSTANCE.shouldCancelVelocity()) {
            return;
        }
        class_746 player = (class_746)this;
        if (!player.method_24828()) {
            return;
        }
        class_243 currentVel = player.method_18798();
        player.method_18800(0.0, currentVel.field_1351, 0.0);
    }

    @Inject(method={"method_7290"}, at={@At(value="HEAD")}, cancellable=true)
    private void cobalt$preventLockedDrops(CallbackInfoReturnable<Boolean> cir) {
        if (ItemLockingModule.INSTANCE.shouldCancelSelectedItemDrop()) {
            cir.setReturnValue((Object)false);
        }
    }
}

