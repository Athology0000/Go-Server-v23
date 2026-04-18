/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Holder
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.ColorResolver
 *  net.minecraft.world.level.EmptyBlockAndTintGetter
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.lighting.LevelLightEngine
 *  net.minecraft.world.level.material.FluidState
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.renderer.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.EmptyBlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MovingBlockRenderState
implements BlockAndTintGetter {
    public BlockPos randomSeedPos = BlockPos.ZERO;
    public BlockPos blockPos = BlockPos.ZERO;
    public BlockState blockState = Blocks.AIR.defaultBlockState();
    public @Nullable Holder<Biome> biome;
    public BlockAndTintGetter level = EmptyBlockAndTintGetter.INSTANCE;

    public float getShade(Direction direction, boolean bl) {
        return this.level.getShade(direction, bl);
    }

    public LevelLightEngine getLightEngine() {
        return this.level.getLightEngine();
    }

    public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        if (this.biome == null) {
            return -1;
        }
        return colorResolver.getColor((Biome)this.biome.value(), (double)blockPos.getX(), (double)blockPos.getZ());
    }

    public @Nullable BlockEntity getBlockEntity(BlockPos blockPos) {
        return null;
    }

    public BlockState getBlockState(BlockPos blockPos) {
        if (blockPos.equals((Object)this.blockPos)) {
            return this.blockState;
        }
        return Blocks.AIR.defaultBlockState();
    }

    public FluidState getFluidState(BlockPos blockPos) {
        return this.getBlockState(blockPos).getFluidState();
    }

    public int getHeight() {
        return 1;
    }

    public int getMinY() {
        return this.blockPos.getY();
    }
}

