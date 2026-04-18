/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.gizmos.GizmoStyle
 *  net.minecraft.gizmos.Gizmos
 *  net.minecraft.util.debug.DebugValueAccess
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package net.minecraft.client.renderer.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

@Environment(value=EnvType.CLIENT)
public class SolidFaceRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;

    public SolidFaceRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void emitGizmos(double d, double e, double f, DebugValueAccess debugValueAccess, Frustum frustum, float g) {
        Level blockGetter = this.minecraft.player.level();
        BlockPos blockPos = BlockPos.containing((double)d, (double)e, (double)f);
        for (BlockPos blockPos2 : BlockPos.betweenClosed((BlockPos)blockPos.offset(-6, -6, -6), (BlockPos)blockPos.offset(6, 6, 6))) {
            BlockState blockState = blockGetter.getBlockState(blockPos2);
            if (blockState.is(Blocks.AIR)) continue;
            VoxelShape voxelShape = blockState.getShape((BlockGetter)blockGetter, blockPos2);
            for (AABB aABB : voxelShape.toAabbs()) {
                AABB aABB2 = aABB.move(blockPos2).inflate(0.002);
                int i = -2130771968;
                Vec3 vec3 = aABB2.getMinPosition();
                Vec3 vec32 = aABB2.getMaxPosition();
                SolidFaceRenderer.addFaceIfSturdy(blockPos2, blockState, (BlockGetter)blockGetter, Direction.WEST, vec3, vec32, -2130771968);
                SolidFaceRenderer.addFaceIfSturdy(blockPos2, blockState, (BlockGetter)blockGetter, Direction.SOUTH, vec3, vec32, -2130771968);
                SolidFaceRenderer.addFaceIfSturdy(blockPos2, blockState, (BlockGetter)blockGetter, Direction.EAST, vec3, vec32, -2130771968);
                SolidFaceRenderer.addFaceIfSturdy(blockPos2, blockState, (BlockGetter)blockGetter, Direction.NORTH, vec3, vec32, -2130771968);
                SolidFaceRenderer.addFaceIfSturdy(blockPos2, blockState, (BlockGetter)blockGetter, Direction.DOWN, vec3, vec32, -2130771968);
                SolidFaceRenderer.addFaceIfSturdy(blockPos2, blockState, (BlockGetter)blockGetter, Direction.UP, vec3, vec32, -2130771968);
            }
        }
    }

    private static void addFaceIfSturdy(BlockPos blockPos, BlockState blockState, BlockGetter blockGetter, Direction direction, Vec3 vec3, Vec3 vec32, int i) {
        if (blockState.isFaceSturdy(blockGetter, blockPos, direction)) {
            Gizmos.rect((Vec3)vec3, (Vec3)vec32, (Direction)direction, (GizmoStyle)GizmoStyle.fill((int)i));
        }
    }
}

