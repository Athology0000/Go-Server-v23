/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.gizmos.GizmoStyle
 *  net.minecraft.gizmos.Gizmos
 *  net.minecraft.util.ARGB
 *  net.minecraft.util.debug.DebugValueAccess
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.phys.AABB
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.debug;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class HeightMapRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private static final int CHUNK_DIST = 2;
    private static final float BOX_HEIGHT = 0.09375f;

    public HeightMapRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void emitGizmos(double d, double e, double f, DebugValueAccess debugValueAccess, Frustum frustum, float g) {
        ClientLevel levelAccessor = this.minecraft.level;
        BlockPos blockPos = BlockPos.containing((double)d, (double)0.0, (double)f);
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                ChunkAccess chunkAccess = levelAccessor.getChunk(blockPos.offset(i * 16, 0, j * 16));
                for (Map.Entry entry : chunkAccess.getHeightmaps()) {
                    Heightmap.Types types = (Heightmap.Types)entry.getKey();
                    ChunkPos chunkPos = chunkAccess.getPos();
                    Vector3f vector3f = this.getColor(types);
                    for (int k = 0; k < 16; ++k) {
                        for (int l = 0; l < 16; ++l) {
                            int m = SectionPos.sectionToBlockCoord((int)chunkPos.x, (int)k);
                            int n = SectionPos.sectionToBlockCoord((int)chunkPos.z, (int)l);
                            float h = (float)levelAccessor.getHeight(types, m, n) + (float)types.ordinal() * 0.09375f;
                            Gizmos.cuboid((AABB)new AABB((double)((float)m + 0.25f), (double)h, (double)((float)n + 0.25f), (double)((float)m + 0.75f), (double)(h + 0.09375f), (double)((float)n + 0.75f)), (GizmoStyle)GizmoStyle.fill((int)ARGB.colorFromFloat((float)1.0f, (float)vector3f.x(), (float)vector3f.y(), (float)vector3f.z())));
                        }
                    }
                }
            }
        }
    }

    private Vector3f getColor(Heightmap.Types types) {
        return switch (types) {
            default -> throw new MatchException(null, null);
            case Heightmap.Types.WORLD_SURFACE_WG -> new Vector3f(1.0f, 1.0f, 0.0f);
            case Heightmap.Types.OCEAN_FLOOR_WG -> new Vector3f(1.0f, 0.0f, 1.0f);
            case Heightmap.Types.WORLD_SURFACE -> new Vector3f(0.0f, 0.7f, 0.0f);
            case Heightmap.Types.OCEAN_FLOOR -> new Vector3f(0.0f, 0.0f, 0.5f);
            case Heightmap.Types.MOTION_BLOCKING -> new Vector3f(0.0f, 0.3f, 0.3f);
            case Heightmap.Types.MOTION_BLOCKING_NO_LEAVES -> new Vector3f(0.0f, 0.5f, 0.5f);
        };
    }
}

