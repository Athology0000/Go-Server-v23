/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.resources.FileToIdConverter
 *  net.minecraft.resources.Identifier
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.waypoints.WaypointStyleAsset
 *  net.minecraft.world.waypoints.WaypointStyleAssets
 */
package net.minecraft.client.resources;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.WaypointStyle;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.waypoints.WaypointStyleAsset;
import net.minecraft.world.waypoints.WaypointStyleAssets;

@Environment(value=EnvType.CLIENT)
public class WaypointStyleManager
extends SimpleJsonResourceReloadListener<WaypointStyle> {
    private static final FileToIdConverter ASSET_LISTER = FileToIdConverter.json((String)"waypoint_style");
    private static final WaypointStyle MISSING = new WaypointStyle(0, 1, List.of(MissingTextureAtlasSprite.getLocation()));
    private Map<ResourceKey<WaypointStyleAsset>, WaypointStyle> waypointStyles = Map.of();

    public WaypointStyleManager() {
        super(WaypointStyle.CODEC, ASSET_LISTER);
    }

    protected void apply(Map<Identifier, WaypointStyle> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.waypointStyles = map.entrySet().stream().collect(Collectors.toUnmodifiableMap(entry -> ResourceKey.create((ResourceKey)WaypointStyleAssets.ROOT_ID, (Identifier)((Identifier)entry.getKey())), Map.Entry::getValue));
    }

    public WaypointStyle get(ResourceKey<WaypointStyleAsset> resourceKey) {
        return this.waypointStyles.getOrDefault(resourceKey, MISSING);
    }
}

