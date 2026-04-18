/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Pair
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.cobalt.mixin.client;

import java.util.List;
import kotlin.Pair;
import net.minecraft.class_310;
import org.cobalt.api.addon.Addon;
import org.cobalt.api.addon.AddonMetadata;
import org.cobalt.api.event.impl.client.TickEvent;
import org.cobalt.internal.loader.AddonLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_310.class})
public class MinecraftMixin {
    @Inject(at={@At(value="HEAD")}, method={"method_1574"})
    private void onStartTick(CallbackInfo callbackInfo) {
        TickEvent.Start startTickEvent = new TickEvent.Start();
        startTickEvent.post();
    }

    @Inject(at={@At(value="RETURN")}, method={"method_1574"})
    private void onEndTick(CallbackInfo callbackInfo) {
        TickEvent.End endTickEvent = new TickEvent.End();
        endTickEvent.post();
    }

    @Inject(method={"close"}, at={@At(value="HEAD")})
    public void onClose(CallbackInfo callbackInfo) {
        List<Pair<AddonMetadata, Addon>> addonsList = AddonLoader.INSTANCE.getAddons();
        addonsList.forEach(addon -> ((Addon)addon.getSecond()).onUnload());
    }
}

