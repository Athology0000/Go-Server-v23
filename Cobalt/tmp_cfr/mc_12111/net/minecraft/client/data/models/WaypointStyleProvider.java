/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.PackOutput$PathProvider
 *  net.minecraft.data.PackOutput$Target
 *  net.minecraft.resources.Identifier
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.waypoints.WaypointStyleAsset
 *  net.minecraft.world.waypoints.WaypointStyleAssets
 */
package net.minecraft.client.data.models;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.WaypointStyle;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.waypoints.WaypointStyleAsset;
import net.minecraft.world.waypoints.WaypointStyleAssets;

@Environment(value=EnvType.CLIENT)
public class WaypointStyleProvider
implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public WaypointStyleProvider(PackOutput packOutput) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "waypoint_style");
    }

    private static void bootstrap(BiConsumer<ResourceKey<WaypointStyleAsset>, WaypointStyle> biConsumer) {
        biConsumer.accept((ResourceKey<WaypointStyleAsset>)WaypointStyleAssets.DEFAULT, new WaypointStyle(128, 332, List.of(Identifier.withDefaultNamespace((String)"default_0"), Identifier.withDefaultNamespace((String)"default_1"), Identifier.withDefaultNamespace((String)"default_2"), Identifier.withDefaultNamespace((String)"default_3"))));
        biConsumer.accept((ResourceKey<WaypointStyleAsset>)WaypointStyleAssets.BOWTIE, new WaypointStyle(64, 332, List.of(Identifier.withDefaultNamespace((String)"bowtie"), Identifier.withDefaultNamespace((String)"default_0"), Identifier.withDefaultNamespace((String)"default_1"), Identifier.withDefaultNamespace((String)"default_2"), Identifier.withDefaultNamespace((String)"default_3"))));
    }

    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        HashMap map = new HashMap();
        WaypointStyleProvider.bootstrap((resourceKey, waypointStyle) -> {
            if (map.putIfAbsent(resourceKey, waypointStyle) != null) {
                throw new IllegalStateException("Tried to register waypoint style twice for id: " + String.valueOf(resourceKey));
            }
        });
        return DataProvider.saveAll((CachedOutput)cachedOutput, WaypointStyle.CODEC, arg_0 -> ((PackOutput.PathProvider)this.pathProvider).json(arg_0), map);
    }

    public String getName() {
        return "Waypoint Style Definitions";
    }
}

