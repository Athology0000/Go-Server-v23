/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.gizmos.GizmoStyle
 *  net.minecraft.gizmos.Gizmos
 *  net.minecraft.gizmos.TextGizmo$Style
 *  net.minecraft.util.ARGB
 *  net.minecraft.util.Mth
 *  net.minecraft.util.debug.DebugValueAccess
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.client.renderer.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Octree;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableInt;

@Environment(value=EnvType.CLIENT)
public class OctreeDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;

    public OctreeDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void emitGizmos(double d, double e, double f, DebugValueAccess debugValueAccess, Frustum frustum, float g) {
        Octree octree = this.minecraft.levelRenderer.getSectionOcclusionGraph().getOctree();
        MutableInt mutableInt = new MutableInt(0);
        octree.visitNodes((node, bl, i, bl2) -> this.renderNode(node, i, bl, mutableInt, bl2), frustum, 32);
    }

    private void renderNode(Octree.Node node, int i, boolean bl, MutableInt mutableInt, boolean bl2) {
        AABB aABB = node.getAABB();
        double d = aABB.getXsize();
        long l = Math.round(d / 16.0);
        if (l == 1L) {
            mutableInt.add(1);
            int j = bl2 ? -16711936 : -1;
            Gizmos.billboardText((String)String.valueOf(mutableInt.intValue()), (Vec3)aABB.getCenter(), (TextGizmo.Style)TextGizmo.Style.forColorAndCentered((int)j).withScale(4.8f));
        }
        long m = l + 5L;
        Gizmos.cuboid((AABB)aABB.deflate(0.1 * (double)i), (GizmoStyle)GizmoStyle.stroke((int)ARGB.colorFromFloat((float)(bl ? 0.4f : 1.0f), (float)OctreeDebugRenderer.getColorComponent(m, 0.3f), (float)OctreeDebugRenderer.getColorComponent(m, 0.8f), (float)OctreeDebugRenderer.getColorComponent(m, 0.5f))));
    }

    private static float getColorComponent(long l, float f) {
        float g = 0.1f;
        return Mth.frac((float)(f * (float)l)) * 0.9f + 0.1f;
    }
}

