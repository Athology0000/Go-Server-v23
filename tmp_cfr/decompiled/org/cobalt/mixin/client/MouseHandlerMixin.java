/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11910
 *  net.minecraft.class_312
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package org.cobalt.mixin.client;

import net.minecraft.class_11910;
import net.minecraft.class_312;
import org.cobalt.api.event.impl.client.MouseEvent;
import org.cobalt.api.util.MouseUtils;
import org.cobalt.api.util.player.MovementManager;
import org.cobalt.internal.dungeons.DungeonsModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={class_312.class})
public abstract class MouseHandlerMixin {
    @Shadow
    private boolean field_1783;

    @Shadow
    public abstract void method_1610();

    @Inject(method={"method_1601"}, at={@At(value="HEAD")}, cancellable=true)
    private void onMouseButton(long window, class_11910 input, int action, CallbackInfo ci) {
        if (input.comp_4801() == 0 && action == 1 && DungeonsModule.onLeftClick()) {
            return;
        }
        MouseEvent event = this.cobalt$createMouseEvent(input.comp_4801(), action == 1);
        if (event != null && event.post()) {
            ci.cancel();
        }
    }

    @Unique
    private MouseEvent cobalt$createMouseEvent(int button, boolean isDown) {
        return switch (button) {
            case 0 -> {
                if (isDown) {
                    yield new MouseEvent.LeftClick(button);
                }
                yield new MouseEvent.LeftRelease(button);
            }
            case 1 -> {
                if (isDown) {
                    yield new MouseEvent.RightClick(button);
                }
                yield new MouseEvent.RightRelease(button);
            }
            case 2 -> {
                if (isDown) {
                    yield new MouseEvent.MiddleClick(button);
                }
                yield new MouseEvent.MiddleRelease(button);
            }
            default -> null;
        };
    }

    @Inject(method={"method_1606"}, at={@At(value="HEAD")}, cancellable=true)
    private void onUpdateMouse(CallbackInfo callbackInfo) {
        if (MovementManager.isLookLocked) {
            callbackInfo.cancel();
        }
    }

    @Inject(method={"method_1613"}, at={@At(value="HEAD")}, cancellable=true)
    private void onIsCursorLocked(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (MouseUtils.isMouseUngrabbed()) {
            if (this.field_1783) {
                this.method_1610();
            }
            callbackInfoReturnable.setReturnValue((Object)false);
        }
    }

    @Inject(method={"method_1612"}, at={@At(value="HEAD")}, cancellable=true)
    private void onLockCursor(CallbackInfo callbackInfo) {
        if (MouseUtils.isMouseUngrabbed()) {
            callbackInfo.cancel();
        }
    }
}

