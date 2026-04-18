/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.resources.Identifier
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.SimplePreparableReloadListener
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.level.GrassColor
 */
package net.minecraft.client.resources;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.LegacyStuffWrapper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.GrassColor;

@Environment(value=EnvType.CLIENT)
public class GrassColorReloadListener
extends SimplePreparableReloadListener<int[]> {
    private static final Identifier LOCATION = Identifier.withDefaultNamespace((String)"textures/colormap/grass.png");

    protected int[] prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        try {
            return LegacyStuffWrapper.getPixels(resourceManager, LOCATION);
        }
        catch (IOException iOException) {
            throw new IllegalStateException("Failed to load grass color texture", iOException);
        }
    }

    protected void apply(int[] is, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        GrassColor.init((int[])is);
    }

    protected /* synthetic */ Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        return this.prepare(resourceManager, profilerFiller);
    }
}

