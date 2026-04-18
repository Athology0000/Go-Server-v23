/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.renderer.blockentity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.CrashReportCategory;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockEntityRenderState {
    public BlockPos blockPos = BlockPos.ZERO;
    public BlockState blockState = Blocks.AIR.defaultBlockState();
    public BlockEntityType<?> blockEntityType = BlockEntityType.TEST_BLOCK;
    public int lightCoords;
    public  @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress;

    public static void extractBase(BlockEntity blockEntity, BlockEntityRenderState blockEntityRenderState,  @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        blockEntityRenderState.blockPos = blockEntity.getBlockPos();
        blockEntityRenderState.blockState = blockEntity.getBlockState();
        blockEntityRenderState.blockEntityType = blockEntity.getType();
        blockEntityRenderState.lightCoords = blockEntity.getLevel() != null ? LevelRenderer.getLightColor((BlockAndTintGetter)blockEntity.getLevel(), blockEntity.getBlockPos()) : 0xF000F0;
        blockEntityRenderState.breakProgress = crumblingOverlay;
    }

    public void fillCrashReportCategory(CrashReportCategory crashReportCategory) {
        crashReportCategory.setDetail("BlockEntityRenderState", (Object)this.getClass().getCanonicalName());
        crashReportCategory.setDetail("Position", (Object)this.blockPos);
        crashReportCategory.setDetail("Block state", () -> ((BlockState)this.blockState).toString());
    }
}

