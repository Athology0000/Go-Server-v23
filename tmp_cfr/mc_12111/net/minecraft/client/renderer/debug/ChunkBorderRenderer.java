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
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package net.minecraft.client.renderer.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Environment(value=EnvType.CLIENT)
public class ChunkBorderRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final float THICK_WIDTH = 4.0f;
    private static final float THIN_WIDTH = 1.0f;
    private final Minecraft minecraft;
    private static final int CELL_BORDER = ARGB.color((int)255, (int)0, (int)155, (int)155);
    private static final int YELLOW = ARGB.color((int)255, (int)255, (int)255, (int)0);
    private static final int MAJOR_LINES = ARGB.colorFromFloat((float)1.0f, (float)0.25f, (float)0.25f, (float)1.0f);

    public ChunkBorderRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void emitGizmos(double d, double e, double f, DebugValueAccess debugValueAccess, Frustum frustum, float g) {
        int m;
        int l;
        Entity entity = this.minecraft.gameRenderer.getMainCamera().entity();
        float h = this.minecraft.level.getMinY();
        float i = this.minecraft.level.getMaxY() + 1;
        SectionPos sectionPos = SectionPos.of((BlockPos)entity.blockPosition());
        double j = sectionPos.minBlockX();
        double k = sectionPos.minBlockZ();
        for (l = -16; l <= 32; l += 16) {
            for (m = -16; m <= 32; m += 16) {
                Gizmos.line((Vec3)new Vec3(j + (double)l, (double)h, k + (double)m), (Vec3)new Vec3(j + (double)l, (double)i, k + (double)m), (int)ARGB.colorFromFloat((float)0.5f, (float)1.0f, (float)0.0f, (float)0.0f), (float)4.0f);
            }
        }
        for (l = 2; l < 16; l += 2) {
            m = l % 4 == 0 ? CELL_BORDER : YELLOW;
            Gizmos.line((Vec3)new Vec3(j + (double)l, (double)h, k), (Vec3)new Vec3(j + (double)l, (double)i, k), (int)m, (float)1.0f);
            Gizmos.line((Vec3)new Vec3(j + (double)l, (double)h, k + 16.0), (Vec3)new Vec3(j + (double)l, (double)i, k + 16.0), (int)m, (float)1.0f);
        }
        for (l = 2; l < 16; l += 2) {
            m = l % 4 == 0 ? CELL_BORDER : YELLOW;
            Gizmos.line((Vec3)new Vec3(j, (double)h, k + (double)l), (Vec3)new Vec3(j, (double)i, k + (double)l), (int)m, (float)1.0f);
            Gizmos.line((Vec3)new Vec3(j + 16.0, (double)h, k + (double)l), (Vec3)new Vec3(j + 16.0, (double)i, k + (double)l), (int)m, (float)1.0f);
        }
        for (l = this.minecraft.level.getMinY(); l <= this.minecraft.level.getMaxY() + 1; l += 2) {
            float n = l;
            int o = l % 8 == 0 ? CELL_BORDER : YELLOW;
            Gizmos.line((Vec3)new Vec3(j, (double)n, k), (Vec3)new Vec3(j, (double)n, k + 16.0), (int)o, (float)1.0f);
            Gizmos.line((Vec3)new Vec3(j, (double)n, k + 16.0), (Vec3)new Vec3(j + 16.0, (double)n, k + 16.0), (int)o, (float)1.0f);
            Gizmos.line((Vec3)new Vec3(j + 16.0, (double)n, k + 16.0), (Vec3)new Vec3(j + 16.0, (double)n, k), (int)o, (float)1.0f);
            Gizmos.line((Vec3)new Vec3(j + 16.0, (double)n, k), (Vec3)new Vec3(j, (double)n, k), (int)o, (float)1.0f);
        }
        for (l = 0; l <= 16; l += 16) {
            for (int m2 = 0; m2 <= 16; m2 += 16) {
                Gizmos.line((Vec3)new Vec3(j + (double)l, (double)h, k + (double)m2), (Vec3)new Vec3(j + (double)l, (double)i, k + (double)m2), (int)MAJOR_LINES, (float)4.0f);
            }
        }
        Gizmos.cuboid((AABB)new AABB((double)sectionPos.minBlockX(), (double)sectionPos.minBlockY(), (double)sectionPos.minBlockZ(), (double)(sectionPos.maxBlockX() + 1), (double)(sectionPos.maxBlockY() + 1), (double)(sectionPos.maxBlockZ() + 1)), (GizmoStyle)GizmoStyle.stroke((int)MAJOR_LINES, (float)1.0f)).setAlwaysOnTop();
        for (l = this.minecraft.level.getMinY(); l <= this.minecraft.level.getMaxY() + 1; l += 16) {
            Gizmos.line((Vec3)new Vec3(j, (double)l, k), (Vec3)new Vec3(j, (double)l, k + 16.0), (int)MAJOR_LINES, (float)4.0f);
            Gizmos.line((Vec3)new Vec3(j, (double)l, k + 16.0), (Vec3)new Vec3(j + 16.0, (double)l, k + 16.0), (int)MAJOR_LINES, (float)4.0f);
            Gizmos.line((Vec3)new Vec3(j + 16.0, (double)l, k + 16.0), (Vec3)new Vec3(j + 16.0, (double)l, k), (int)MAJOR_LINES, (float)4.0f);
            Gizmos.line((Vec3)new Vec3(j + 16.0, (double)l, k), (Vec3)new Vec3(j, (double)l, k), (int)MAJOR_LINES, (float)4.0f);
        }
    }
}

