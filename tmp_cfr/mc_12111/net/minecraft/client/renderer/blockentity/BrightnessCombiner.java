/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.DoubleBlockCombiner$Combiner
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package net.minecraft.client.renderer.blockentity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;

@Environment(value=EnvType.CLIENT)
public class BrightnessCombiner<S extends BlockEntity>
implements DoubleBlockCombiner.Combiner<S, Int2IntFunction> {
    public Int2IntFunction acceptDouble(S blockEntity, S blockEntity2) {
        return i -> {
            int j = LevelRenderer.getLightColor((BlockAndTintGetter)blockEntity.getLevel(), blockEntity.getBlockPos());
            int k = LevelRenderer.getLightColor((BlockAndTintGetter)blockEntity2.getLevel(), blockEntity2.getBlockPos());
            int l = LightTexture.block(j);
            int m = LightTexture.block(k);
            int n = LightTexture.sky(j);
            int o = LightTexture.sky(k);
            return LightTexture.pack(Math.max(l, m), Math.max(n, o));
        };
    }

    public Int2IntFunction acceptSingle(S blockEntity) {
        return i -> i;
    }

    public Int2IntFunction acceptNone() {
        return i -> i;
    }

    public /* synthetic */ Object acceptNone() {
        return this.acceptNone();
    }
}

