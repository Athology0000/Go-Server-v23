/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.gizmos.Gizmos
 *  net.minecraft.gizmos.TextGizmo$Style
 *  net.minecraft.util.ARGB
 *  net.minecraft.util.debug.DebugValueAccess
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.phys.Vec3
 */
package net.minecraft.client.renderer.debug;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

@Environment(value=EnvType.CLIENT)
public class LightDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private final boolean showBlockLight;
    private final boolean showSkyLight;
    private static final int MAX_RENDER_DIST = 10;

    public LightDebugRenderer(Minecraft minecraft, boolean bl, boolean bl2) {
        this.minecraft = minecraft;
        this.showBlockLight = bl;
        this.showSkyLight = bl2;
    }

    @Override
    public void emitGizmos(double d, double e, double f, DebugValueAccess debugValueAccess, Frustum frustum, float g) {
        ClientLevel level = this.minecraft.level;
        BlockPos blockPos = BlockPos.containing((double)d, (double)e, (double)f);
        LongOpenHashSet longSet = new LongOpenHashSet();
        for (BlockPos blockPos2 : BlockPos.betweenClosed((BlockPos)blockPos.offset(-10, -10, -10), (BlockPos)blockPos.offset(10, 10, 10))) {
            int j;
            int i = level.getBrightness(LightLayer.SKY, blockPos2);
            long l = SectionPos.blockToSection((long)blockPos2.asLong());
            if (longSet.add(l)) {
                Gizmos.billboardText((String)level.getChunkSource().getLightEngine().getDebugData(LightLayer.SKY, SectionPos.of((long)l)), (Vec3)new Vec3((double)SectionPos.sectionToBlockCoord((int)SectionPos.x((long)l), (int)8), (double)SectionPos.sectionToBlockCoord((int)SectionPos.y((long)l), (int)8), (double)SectionPos.sectionToBlockCoord((int)SectionPos.z((long)l), (int)8)), (TextGizmo.Style)TextGizmo.Style.forColorAndCentered((int)-65536).withScale(4.8f));
            }
            if (i != 15 && this.showSkyLight) {
                j = ARGB.srgbLerp((float)((float)i / 15.0f), (int)-16776961, (int)-16711681);
                Gizmos.billboardText((String)String.valueOf(i), (Vec3)Vec3.atLowerCornerWithOffset((Vec3i)blockPos2, (double)0.5, (double)0.25, (double)0.5), (TextGizmo.Style)TextGizmo.Style.forColorAndCentered((int)j));
            }
            if (!this.showBlockLight || (j = level.getBrightness(LightLayer.BLOCK, blockPos2)) == 0) continue;
            int k = ARGB.srgbLerp((float)((float)j / 15.0f), (int)-5636096, (int)-256);
            Gizmos.billboardText((String)String.valueOf(level.getBrightness(LightLayer.BLOCK, blockPos2)), (Vec3)Vec3.atCenterOf((Vec3i)blockPos2), (TextGizmo.Style)TextGizmo.Style.forColorAndCentered((int)k));
        }
    }
}

