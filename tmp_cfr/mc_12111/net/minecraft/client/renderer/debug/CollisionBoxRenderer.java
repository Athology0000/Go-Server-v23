/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.gizmos.GizmoStyle
 *  net.minecraft.gizmos.Gizmos
 *  net.minecraft.util.Util
 *  net.minecraft.util.debug.DebugValueAccess
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.Util;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

@Environment(value=EnvType.CLIENT)
public class CollisionBoxRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private double lastUpdateTime = Double.MIN_VALUE;
    private List<VoxelShape> shapes = Collections.emptyList();

    public CollisionBoxRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void emitGizmos(double d, double e, double f, DebugValueAccess debugValueAccess, Frustum frustum, float g) {
        double h = Util.getNanos();
        if (h - this.lastUpdateTime > 1.0E8) {
            this.lastUpdateTime = h;
            Entity entity = this.minecraft.gameRenderer.getMainCamera().entity();
            this.shapes = ImmutableList.copyOf((Iterable)entity.level().getCollisions(entity, entity.getBoundingBox().inflate(6.0)));
        }
        for (VoxelShape voxelShape : this.shapes) {
            GizmoStyle gizmoStyle = GizmoStyle.stroke((int)-1);
            for (AABB aABB : voxelShape.toAabbs()) {
                Gizmos.cuboid((AABB)aABB, (GizmoStyle)gizmoStyle);
            }
        }
    }
}

