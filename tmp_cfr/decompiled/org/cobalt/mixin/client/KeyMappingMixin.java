/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package org.cobalt.mixin.client;

import net.minecraft.class_304;
import net.minecraft.class_310;
import org.cobalt.api.util.player.MovementManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={class_304.class})
public class KeyMappingMixin {
    @Inject(method={"method_1434"}, at={@At(value="HEAD")}, cancellable=true)
    private void cobalt$lockMovement(CallbackInfoReturnable<Boolean> cir) {
        class_310 mc = class_310.method_1551();
        if (mc == null || mc.field_1690 == null) {
            return;
        }
        class_304 self = (class_304)this;
        if (self == mc.field_1690.field_1886) {
            if (MovementManager.forcedActionsEnabled) {
                cir.setReturnValue((Object)MovementManager.forcedAttack);
            }
            return;
        }
        if (self == mc.field_1690.field_1904) {
            if (MovementManager.forcedActionsEnabled) {
                cir.setReturnValue((Object)MovementManager.forcedUse);
            }
            return;
        }
        if (!MovementManager.isMovementLocked) {
            return;
        }
        if (self == mc.field_1690.field_1894) {
            cir.setReturnValue((Object)(MovementManager.hasForcedMovement && MovementManager.forcedForward ? 1 : 0));
            return;
        }
        if (self == mc.field_1690.field_1881) {
            cir.setReturnValue((Object)(MovementManager.hasForcedMovement && MovementManager.forcedBackward ? 1 : 0));
            return;
        }
        if (self == mc.field_1690.field_1913) {
            cir.setReturnValue((Object)(MovementManager.hasForcedMovement && MovementManager.forcedLeft ? 1 : 0));
            return;
        }
        if (self == mc.field_1690.field_1849) {
            cir.setReturnValue((Object)(MovementManager.hasForcedMovement && MovementManager.forcedRight ? 1 : 0));
            return;
        }
        if (self == mc.field_1690.field_1903) {
            cir.setReturnValue((Object)(MovementManager.hasForcedMovement && MovementManager.forcedJump ? 1 : 0));
            return;
        }
        if (self == mc.field_1690.field_1832) {
            if (MovementManager.hasForcedMovement && MovementManager.forcedShift) {
                cir.setReturnValue((Object)true);
            }
            return;
        }
        if (self == mc.field_1690.field_1867) {
            cir.setReturnValue((Object)(MovementManager.hasForcedMovement && MovementManager.forcedSprint ? 1 : 0));
            return;
        }
    }
}

