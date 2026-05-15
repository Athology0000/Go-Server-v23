package org.phantom.mixin.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.phantom.internal.dungeons.gambling.DungeonChestGamblingModule;
import org.phantom.internal.qol.AutoStashModule;
import org.phantom.internal.qol.ItemLockingModule;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen {

    @Shadow protected int leftPos;
    @Shadow protected int topPos;
    @Shadow protected int imageWidth;
    @Shadow @Final protected T menu;
    @Shadow protected Slot hoveredSlot;

    @Unique
    private Button phantom$autoStashButton;

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void phantom$addAutoStashButton(CallbackInfo ci) {
        if (!AutoStashModule.INSTANCE.isStashScreen(this)) {
            phantom$autoStashButton = null;
            return;
        }

        int buttonWidth = 92;
        int buttonHeight = 20;
        int x = this.leftPos + this.imageWidth - buttonWidth;
        int y = Math.max(4, this.topPos - buttonHeight - 4);

        phantom$autoStashButton = this.addRenderableWidget(
            Button.builder(Component.literal(AutoStashModule.INSTANCE.getGuiButtonLabel()), btn -> {
                AutoStashModule.INSTANCE.toggleFromGui();
                btn.setMessage(Component.literal(AutoStashModule.INSTANCE.getGuiButtonLabel()));
            }).bounds(x, y, buttonWidth, buttonHeight).build()
        );
    }

    @Inject(method = "containerTick", at = @At("TAIL"))
    private void phantom$syncAutoStashButton(CallbackInfo ci) {
        if (phantom$autoStashButton != null) {
            phantom$autoStashButton.setMessage(Component.literal(AutoStashModule.INSTANCE.getGuiButtonLabel()));
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void phantom$handleItemLockKeybinds(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        if (DungeonChestGamblingModule.INSTANCE.onKeyPressed(input.key())) {
            cir.setReturnValue(true);
            return;
        }

        if (hoveredSlot == null || minecraft == null || minecraft.player == null) {
            return;
        }

        if (ItemLockingModule.INSTANCE.handleContainerKeyPressed(hoveredSlot, input)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void phantom$cancelDungeonChestGamblingMouseClicked(MouseButtonEvent input, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (DungeonChestGamblingModule.INSTANCE.isRendering()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void phantom$cancelDungeonChestGamblingMouseReleased(MouseButtonEvent input, CallbackInfoReturnable<Boolean> cir) {
        if (DungeonChestGamblingModule.INSTANCE.isRendering()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void phantom$cancelDungeonChestGamblingMouseDragged(MouseButtonEvent input, double dragX, double dragY, CallbackInfoReturnable<Boolean> cir) {
        if (DungeonChestGamblingModule.INSTANCE.isRendering()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void phantom$cancelDungeonChestGamblingMouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY, CallbackInfoReturnable<Boolean> cir) {
        if (DungeonChestGamblingModule.INSTANCE.isRendering()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
        method = "slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ClickType;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void phantom$preventLockedInteractions(Slot slot, int slotId, int button, ClickType clickType, CallbackInfo ci) {
        if (DungeonChestGamblingModule.INSTANCE.isRendering()) {
            ci.cancel();
            return;
        }

        if (ItemLockingModule.INSTANCE.shouldCancelContainerClick(getTitle().getString(), menu, slot, slotId, button, clickType)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderSlots", at = @At("TAIL"))
    private void phantom$renderItemLockOverlays(GuiGraphics graphics, int mouseX, int mouseY, CallbackInfo ci) {
        for (Slot slot : menu.slots) {
            if (!slot.isActive() || slot.isFake()) {
                continue;
            }

            ItemLockingModule.INSTANCE.renderContainerSlotOverlay(
                graphics,
                slot,
                leftPos + slot.x,
                topPos + slot.y
            );
        }
    }
}
