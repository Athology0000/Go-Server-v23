/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.gizmos.GizmoStyle
 *  net.minecraft.gizmos.Gizmos
 *  net.minecraft.util.ARGB
 *  net.minecraft.util.Mth
 *  net.minecraft.util.debug.DebugValueAccess
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector4f
 */
package net.minecraft.client.renderer.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

@Environment(value=EnvType.CLIENT)
public class ChunkCullingDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    public static final Direction[] DIRECTIONS = Direction.values();
    private final Minecraft minecraft;

    public ChunkCullingDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void emitGizmos(double d, double e, double f, DebugValueAccess debugValueAccess, Frustum frustum, float g) {
        Frustum frustum2;
        LevelRenderer levelRenderer = this.minecraft.levelRenderer;
        boolean bl = this.minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.CHUNK_SECTION_PATHS);
        boolean bl2 = this.minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.CHUNK_SECTION_VISIBILITY);
        if (bl || bl2) {
            SectionOcclusionGraph sectionOcclusionGraph = levelRenderer.getSectionOcclusionGraph();
            for (SectionRenderDispatcher.RenderSection renderSection : levelRenderer.getVisibleSections()) {
                int i;
                SectionOcclusionGraph.Node node = sectionOcclusionGraph.getNode(renderSection);
                if (node == null) continue;
                BlockPos blockPos = renderSection.getRenderOrigin();
                if (bl) {
                    i = node.step == 0 ? 0 : Mth.hsvToRgb((float)((float)node.step / 50.0f), (float)0.9f, (float)0.9f);
                    for (int j = 0; j < DIRECTIONS.length; ++j) {
                        if (!node.hasSourceDirection(j)) continue;
                        Direction direction = DIRECTIONS[j];
                        Gizmos.line((Vec3)Vec3.atLowerCornerWithOffset((Vec3i)blockPos, (double)8.0, (double)8.0, (double)8.0), (Vec3)Vec3.atLowerCornerWithOffset((Vec3i)blockPos, (double)(8 - 16 * direction.getStepX()), (double)(8 - 16 * direction.getStepY()), (double)(8 - 16 * direction.getStepZ())), (int)ARGB.opaque((int)i));
                    }
                }
                if (!bl2 || !renderSection.getSectionMesh().hasRenderableLayers()) continue;
                i = 0;
                for (Direction direction2 : DIRECTIONS) {
                    for (Direction direction3 : DIRECTIONS) {
                        boolean bl3 = renderSection.getSectionMesh().facesCanSeeEachother(direction2, direction3);
                        if (bl3) continue;
                        ++i;
                        Gizmos.line((Vec3)Vec3.atLowerCornerWithOffset((Vec3i)blockPos, (double)(8 + 8 * direction2.getStepX()), (double)(8 + 8 * direction2.getStepY()), (double)(8 + 8 * direction2.getStepZ())), (Vec3)Vec3.atLowerCornerWithOffset((Vec3i)blockPos, (double)(8 + 8 * direction3.getStepX()), (double)(8 + 8 * direction3.getStepY()), (double)(8 + 8 * direction3.getStepZ())), (int)ARGB.color((int)255, (int)255, (int)0, (int)0));
                    }
                }
                if (i <= 0) continue;
                float h = 0.5f;
                float k = 0.2f;
                Gizmos.cuboid((AABB)renderSection.getBoundingBox().deflate(0.5), (GizmoStyle)GizmoStyle.fill((int)ARGB.colorFromFloat((float)0.2f, (float)0.9f, (float)0.9f, (float)0.0f)));
            }
        }
        if ((frustum2 = levelRenderer.getCapturedFrustum()) != null) {
            Vec3 vec3 = new Vec3(frustum2.getCamX(), frustum2.getCamY(), frustum2.getCamZ());
            Vector4f[] vector4fs = frustum2.getFrustumPoints();
            this.addFrustumQuad(vec3, vector4fs, 0, 1, 2, 3, 0, 1, 1);
            this.addFrustumQuad(vec3, vector4fs, 4, 5, 6, 7, 1, 0, 0);
            this.addFrustumQuad(vec3, vector4fs, 0, 1, 5, 4, 1, 1, 0);
            this.addFrustumQuad(vec3, vector4fs, 2, 3, 7, 6, 0, 0, 1);
            this.addFrustumQuad(vec3, vector4fs, 0, 4, 7, 3, 0, 1, 0);
            this.addFrustumQuad(vec3, vector4fs, 1, 5, 6, 2, 1, 0, 1);
            this.addFrustumLine(vec3, vector4fs[0], vector4fs[1]);
            this.addFrustumLine(vec3, vector4fs[1], vector4fs[2]);
            this.addFrustumLine(vec3, vector4fs[2], vector4fs[3]);
            this.addFrustumLine(vec3, vector4fs[3], vector4fs[0]);
            this.addFrustumLine(vec3, vector4fs[4], vector4fs[5]);
            this.addFrustumLine(vec3, vector4fs[5], vector4fs[6]);
            this.addFrustumLine(vec3, vector4fs[6], vector4fs[7]);
            this.addFrustumLine(vec3, vector4fs[7], vector4fs[4]);
            this.addFrustumLine(vec3, vector4fs[0], vector4fs[4]);
            this.addFrustumLine(vec3, vector4fs[1], vector4fs[5]);
            this.addFrustumLine(vec3, vector4fs[2], vector4fs[6]);
            this.addFrustumLine(vec3, vector4fs[3], vector4fs[7]);
        }
    }

    private void addFrustumLine(Vec3 vec3, Vector4f vector4f, Vector4f vector4f2) {
        Gizmos.line((Vec3)new Vec3(vec3.x + (double)vector4f.x, vec3.y + (double)vector4f.y, vec3.z + (double)vector4f.z), (Vec3)new Vec3(vec3.x + (double)vector4f2.x, vec3.y + (double)vector4f2.y, vec3.z + (double)vector4f2.z), (int)-16777216);
    }

    private void addFrustumQuad(Vec3 vec3, Vector4f[] vector4fs, int i, int j, int k, int l, int m, int n, int o) {
        float f = 0.25f;
        Gizmos.rect((Vec3)new Vec3((double)vector4fs[i].x(), (double)vector4fs[i].y(), (double)vector4fs[i].z()).add(vec3), (Vec3)new Vec3((double)vector4fs[j].x(), (double)vector4fs[j].y(), (double)vector4fs[j].z()).add(vec3), (Vec3)new Vec3((double)vector4fs[k].x(), (double)vector4fs[k].y(), (double)vector4fs[k].z()).add(vec3), (Vec3)new Vec3((double)vector4fs[l].x(), (double)vector4fs[l].y(), (double)vector4fs[l].z()).add(vec3), (GizmoStyle)GizmoStyle.fill((int)ARGB.colorFromFloat((float)0.25f, (float)m, (float)n, (float)o)));
    }
}

