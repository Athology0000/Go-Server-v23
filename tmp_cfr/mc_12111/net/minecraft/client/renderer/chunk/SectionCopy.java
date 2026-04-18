/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.CrashReport
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.ReportedException
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.EmptyLevelChunk
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.PalettedContainer
 *  net.minecraft.world.level.levelgen.DebugLevelSource
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.renderer.chunk;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class SectionCopy {
    private final Map<BlockPos, BlockEntity> blockEntities;
    private final @Nullable PalettedContainer<BlockState> section;
    private final boolean debug;
    private final LevelHeightAccessor levelHeightAccessor;

    SectionCopy(LevelChunk levelChunk, int i) {
        this.levelHeightAccessor = levelChunk;
        this.debug = levelChunk.getLevel().isDebug();
        this.blockEntities = ImmutableMap.copyOf((Map)levelChunk.getBlockEntities());
        if (levelChunk instanceof EmptyLevelChunk) {
            this.section = null;
        } else {
            LevelChunkSection levelChunkSection;
            LevelChunkSection[] levelChunkSections = levelChunk.getSections();
            this.section = i < 0 || i >= levelChunkSections.length ? null : ((levelChunkSection = levelChunkSections[i]).hasOnlyAir() ? null : levelChunkSection.getStates().copy());
        }
    }

    public @Nullable BlockEntity getBlockEntity(BlockPos blockPos) {
        return this.blockEntities.get(blockPos);
    }

    public BlockState getBlockState(BlockPos blockPos) {
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();
        if (this.debug) {
            BlockState blockState = null;
            if (j == 60) {
                blockState = Blocks.BARRIER.defaultBlockState();
            }
            if (j == 70) {
                blockState = DebugLevelSource.getBlockStateFor((int)i, (int)k);
            }
            return blockState == null ? Blocks.AIR.defaultBlockState() : blockState;
        }
        if (this.section == null) {
            return Blocks.AIR.defaultBlockState();
        }
        try {
            return (BlockState)this.section.get(i & 0xF, j & 0xF, k & 0xF);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable((Throwable)throwable, (String)"Getting block state");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Block being got");
            crashReportCategory.setDetail("Location", () -> CrashReportCategory.formatLocation((LevelHeightAccessor)this.levelHeightAccessor, (int)i, (int)j, (int)k));
            throw new ReportedException(crashReport);
        }
    }
}

