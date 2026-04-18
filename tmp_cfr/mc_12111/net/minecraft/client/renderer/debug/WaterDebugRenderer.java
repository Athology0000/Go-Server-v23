/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.gizmos.GizmoStyle
 *  net.minecraft.gizmos.Gizmos
 *  net.minecraft.gizmos.TextGizmo$Style
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.util.ARGB
 *  net.minecraft.util.debug.DebugValueAccess
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.material.FluidState
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
import net.minecraft.core.Vec3i;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Environment(value=EnvType.CLIENT)
public class WaterDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;

    public WaterDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void emitGizmos(double d, double e, double f, DebugValueAccess debugValueAccess, Frustum frustum, float g) {
        FluidState fluidState;
        BlockPos blockPos = this.minecraft.player.blockPosition();
        Level levelReader = this.minecraft.player.level();
        for (BlockPos blockPos2 : BlockPos.betweenClosed((BlockPos)blockPos.offset(-10, -10, -10), (BlockPos)blockPos.offset(10, 10, 10))) {
            fluidState = levelReader.getFluidState(blockPos2);
            if (!fluidState.is(FluidTags.WATER)) continue;
            double h = (float)blockPos2.getY() + fluidState.getHeight((BlockGetter)levelReader, blockPos2);
            Gizmos.cuboid((AABB)new AABB((double)((float)blockPos2.getX() + 0.01f), (double)((float)blockPos2.getY() + 0.01f), (double)((float)blockPos2.getZ() + 0.01f), (double)((float)blockPos2.getX() + 0.99f), h, (double)((float)blockPos2.getZ() + 0.99f)), (GizmoStyle)GizmoStyle.fill((int)ARGB.colorFromFloat((float)0.15f, (float)0.0f, (float)1.0f, (float)0.0f)));
        }
        for (BlockPos blockPos2 : BlockPos.betweenClosed((BlockPos)blockPos.offset(-10, -10, -10), (BlockPos)blockPos.offset(10, 10, 10))) {
            fluidState = levelReader.getFluidState(blockPos2);
            if (!fluidState.is(FluidTags.WATER)) continue;
            Gizmos.billboardText((String)String.valueOf(fluidState.getAmount()), (Vec3)Vec3.atLowerCornerWithOffset((Vec3i)blockPos2, (double)0.5, (double)fluidState.getHeight((BlockGetter)levelReader, blockPos2), (double)0.5), (TextGizmo.Style)TextGizmo.Style.forColorAndCentered((int)-16777216));
        }
    }
}

