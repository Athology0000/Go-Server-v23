/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_304
 *  net.minecraft.class_743
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package org.cobalt.mixin.client;

import net.minecraft.class_304;
import net.minecraft.class_743;
import org.cobalt.internal.dungeons.DungeonsModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={class_743.class})
public class KeyboardInputMixin {
    @Redirect(method={"method_3129"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_304;method_1434()Z", ordinal=1), require=0)
    private boolean cobalt$forceBackwardForBonzoStaff(class_304 keyBinding) {
        boolean originalBackward = keyBinding.method_1434();
        if (DungeonsModule.INSTANCE.shouldPressBackward()) {
            return true;
        }
        return originalBackward;
    }
}

