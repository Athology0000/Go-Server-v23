/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Holder
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.piston.PistonBaseBlock
 *  net.minecraft.world.level.block.piston.PistonHeadBlock
 *  net.minecraft.world.level.block.piston.PistonMovingBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.PistonType
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.PistonHeadRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PistonHeadRenderer
implements BlockEntityRenderer<PistonMovingBlockEntity, PistonHeadRenderState> {
    @Override
    public PistonHeadRenderState createRenderState() {
        return new PistonHeadRenderState();
    }

    @Override
    public void extractRenderState(PistonMovingBlockEntity pistonMovingBlockEntity, PistonHeadRenderState pistonHeadRenderState, float f, Vec3 vec3,  @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(pistonMovingBlockEntity, pistonHeadRenderState, f, vec3, crumblingOverlay);
        pistonHeadRenderState.xOffset = pistonMovingBlockEntity.getXOff(f);
        pistonHeadRenderState.yOffset = pistonMovingBlockEntity.getYOff(f);
        pistonHeadRenderState.zOffset = pistonMovingBlockEntity.getZOff(f);
        pistonHeadRenderState.block = null;
        pistonHeadRenderState.base = null;
        BlockState blockState = pistonMovingBlockEntity.getMovedState();
        Level level = pistonMovingBlockEntity.getLevel();
        if (level != null && !blockState.isAir()) {
            BlockPos blockPos = pistonMovingBlockEntity.getBlockPos().relative(pistonMovingBlockEntity.getMovementDirection().getOpposite());
            Holder holder = level.getBiome(blockPos);
            if (blockState.is(Blocks.PISTON_HEAD) && pistonMovingBlockEntity.getProgress(f) <= 4.0f) {
                blockState = (BlockState)blockState.setValue((Property)PistonHeadBlock.SHORT, (Comparable)Boolean.valueOf(pistonMovingBlockEntity.getProgress(f) <= 0.5f));
                pistonHeadRenderState.block = PistonHeadRenderer.createMovingBlock(blockPos, blockState, (Holder<Biome>)holder, level);
            } else if (pistonMovingBlockEntity.isSourcePiston() && !pistonMovingBlockEntity.isExtending()) {
                PistonType pistonType = blockState.is(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT;
                BlockState blockState2 = (BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue((Property)PistonHeadBlock.TYPE, (Comparable)pistonType)).setValue((Property)PistonHeadBlock.FACING, (Comparable)((Direction)blockState.getValue((Property)PistonBaseBlock.FACING)));
                blockState2 = (BlockState)blockState2.setValue((Property)PistonHeadBlock.SHORT, (Comparable)Boolean.valueOf(pistonMovingBlockEntity.getProgress(f) >= 0.5f));
                pistonHeadRenderState.block = PistonHeadRenderer.createMovingBlock(blockPos, blockState2, (Holder<Biome>)holder, level);
                BlockPos blockPos2 = blockPos.relative(pistonMovingBlockEntity.getMovementDirection());
                blockState = (BlockState)blockState.setValue((Property)PistonBaseBlock.EXTENDED, (Comparable)Boolean.valueOf(true));
                pistonHeadRenderState.base = PistonHeadRenderer.createMovingBlock(blockPos2, blockState, (Holder<Biome>)holder, level);
            } else {
                pistonHeadRenderState.block = PistonHeadRenderer.createMovingBlock(blockPos, blockState, (Holder<Biome>)holder, level);
            }
        }
    }

    @Override
    public void submit(PistonHeadRenderState pistonHeadRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (pistonHeadRenderState.block == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(pistonHeadRenderState.xOffset, pistonHeadRenderState.yOffset, pistonHeadRenderState.zOffset);
        submitNodeCollector.submitMovingBlock(poseStack, pistonHeadRenderState.block);
        poseStack.popPose();
        if (pistonHeadRenderState.base != null) {
            submitNodeCollector.submitMovingBlock(poseStack, pistonHeadRenderState.base);
        }
    }

    private static MovingBlockRenderState createMovingBlock(BlockPos blockPos, BlockState blockState, Holder<Biome> holder, Level level) {
        MovingBlockRenderState movingBlockRenderState = new MovingBlockRenderState();
        movingBlockRenderState.randomSeedPos = blockPos;
        movingBlockRenderState.blockPos = blockPos;
        movingBlockRenderState.blockState = blockState;
        movingBlockRenderState.biome = holder;
        movingBlockRenderState.level = level;
        return movingBlockRenderState;
    }

    @Override
    public int getViewDistance() {
        return 68;
    }

    @Override
    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

