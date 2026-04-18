/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11908
 *  net.minecraft.class_1735
 *  net.minecraft.class_310
 *  net.minecraft.class_364
 *  net.minecraft.class_465
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package org.cobalt.mixin.client;

import net.minecraft.class_11908;
import net.minecraft.class_1735;
import net.minecraft.class_310;
import net.minecraft.class_364;
import net.minecraft.class_465;
import org.cobalt.internal.qol.ItemLockingModule;
import org.cobalt.mixin.client.AbstractContainerScreenAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={class_364.class})
public interface GuiEventListenerMixin {
    @Inject(method={"method_16803"}, at={@At(value="HEAD")}, cancellable=true)
    private void cobalt$handleItemLockKeyReleases(class_11908 input, CallbackInfoReturnable<Boolean> cir) {
        GuiEventListenerMixin listener = this;
        if (!(listener instanceof class_465)) {
            return;
        }
        class_465 containerScreen = (class_465)listener;
        class_310 minecraft = class_310.method_1551();
        if (minecraft.field_1724 == null) {
            return;
        }
        class_1735 hoveredSlot = ((AbstractContainerScreenAccessor)containerScreen).getHoveredSlot();
        if (ItemLockingModule.INSTANCE.handleContainerKeyReleased(hoveredSlot, input)) {
            cir.setReturnValue((Object)true);
        }
    }
}

