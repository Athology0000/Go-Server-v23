/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11908
 *  net.minecraft.class_1703
 *  net.minecraft.class_1713
 *  net.minecraft.class_1735
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_364
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package org.cobalt.mixin.client;

import net.minecraft.class_11908;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_465;
import org.cobalt.internal.qol.AutoStashModule;
import org.cobalt.internal.qol.ItemLockingModule;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={class_465.class})
public abstract class AbstractContainerScreenMixin<T extends class_1703>
extends class_437 {
    @Shadow
    protected int field_2776;
    @Shadow
    protected int field_2800;
    @Shadow
    protected int field_2792;
    @Shadow
    @Final
    protected T field_2797;
    @Shadow
    protected class_1735 field_2787;
    @Unique
    private class_4185 cobalt$autoStashButton;

    protected AbstractContainerScreenMixin(class_2561 title) {
        super(title);
    }

    @Inject(method={"method_25426"}, at={@At(value="TAIL")})
    private void cobalt$addAutoStashButton(CallbackInfo ci) {
        if (!AutoStashModule.INSTANCE.isStashScreen(this)) {
            this.cobalt$autoStashButton = null;
            return;
        }
        int buttonWidth = 92;
        int buttonHeight = 20;
        int x = this.field_2776 + this.field_2792 - buttonWidth;
        int y = Math.max(4, this.field_2800 - buttonHeight - 4);
        this.cobalt$autoStashButton = (class_4185)this.method_37063((class_364)class_4185.method_46430((class_2561)class_2561.method_43470((String)AutoStashModule.INSTANCE.getGuiButtonLabel()), btn -> {
            AutoStashModule.INSTANCE.toggleFromGui();
            btn.method_25355((class_2561)class_2561.method_43470((String)AutoStashModule.INSTANCE.getGuiButtonLabel()));
        }).method_46434(x, y, buttonWidth, buttonHeight).method_46431());
    }

    @Inject(method={"method_37432"}, at={@At(value="TAIL")})
    private void cobalt$syncAutoStashButton(CallbackInfo ci) {
        if (this.cobalt$autoStashButton != null) {
            this.cobalt$autoStashButton.method_25355((class_2561)class_2561.method_43470((String)AutoStashModule.INSTANCE.getGuiButtonLabel()));
        }
    }

    @Inject(method={"method_25404"}, at={@At(value="HEAD")}, cancellable=true)
    private void cobalt$handleItemLockKeybinds(class_11908 input, CallbackInfoReturnable<Boolean> cir) {
        if (this.field_2787 == null || this.field_22787 == null || this.field_22787.field_1724 == null) {
            return;
        }
        if (ItemLockingModule.INSTANCE.handleContainerKeyPressed(this.field_2787, input)) {
            cir.setReturnValue((Object)true);
        }
    }

    @Inject(method={"method_2383"}, at={@At(value="HEAD")}, cancellable=true)
    private void cobalt$preventLockedInteractions(class_1735 slot, int slotId, int button, class_1713 clickType, CallbackInfo ci) {
        if (ItemLockingModule.INSTANCE.shouldCancelContainerClick(this.method_25440().getString(), (class_1703)this.field_2797, slot, slotId, button, clickType)) {
            ci.cancel();
        }
    }

    @Inject(method={"method_64508"}, at={@At(value="TAIL")})
    private void cobalt$renderItemLockOverlays(class_332 graphics, int mouseX, int mouseY, CallbackInfo ci) {
        for (class_1735 slot : ((class_1703)this.field_2797).field_7761) {
            if (!slot.method_7682() || slot.method_55059()) continue;
            ItemLockingModule.INSTANCE.renderContainerSlotOverlay(graphics, slot, this.field_2776 + slot.field_7873, this.field_2800 + slot.field_7872);
        }
    }
}

