/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_128
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.cobalt.mixin.client;

import java.util.stream.Collectors;
import net.minecraft.class_128;
import org.cobalt.api.addon.AddonMetadata;
import org.cobalt.internal.loader.AddonLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_128.class})
public abstract class CrashReportMixin {
    @Inject(method={"method_555"}, at={@At(value="HEAD")})
    private void addAddonInfo(StringBuilder crashReportBuilder, CallbackInfo callbackInfo) {
        String addons = AddonLoader.INSTANCE.getAddons().stream().map(info -> ((AddonMetadata)info.getFirst()).getName() + " v" + ((AddonMetadata)info.getFirst()).getVersion()).collect(Collectors.joining(", "));
        if (addons.isEmpty()) {
            addons = "None";
        }
        crashReportBuilder.append("\n========================================").append("\nDutt Client Addons (").append(AddonLoader.INSTANCE.getAddons().size()).append("): ").append(addons).append("\n========================================\n");
    }
}

