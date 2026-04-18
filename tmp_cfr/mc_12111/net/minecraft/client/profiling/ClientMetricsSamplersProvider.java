/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.util.profiling.ProfileCollector
 *  net.minecraft.util.profiling.metrics.MetricCategory
 *  net.minecraft.util.profiling.metrics.MetricSampler
 *  net.minecraft.util.profiling.metrics.MetricsSamplerProvider
 *  net.minecraft.util.profiling.metrics.profiling.ProfilerSamplerAdapter
 *  net.minecraft.util.profiling.metrics.profiling.ServerMetricsSamplersProvider
 */
package net.minecraft.client.profiling;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.util.profiling.ProfileCollector;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsSamplerProvider;
import net.minecraft.util.profiling.metrics.profiling.ProfilerSamplerAdapter;
import net.minecraft.util.profiling.metrics.profiling.ServerMetricsSamplersProvider;

@Environment(value=EnvType.CLIENT)
public class ClientMetricsSamplersProvider
implements MetricsSamplerProvider {
    private final LevelRenderer levelRenderer;
    private final Set<MetricSampler> samplers = new ObjectOpenHashSet();
    private final ProfilerSamplerAdapter samplerFactory = new ProfilerSamplerAdapter();

    public ClientMetricsSamplersProvider(LongSupplier longSupplier, LevelRenderer levelRenderer) {
        this.levelRenderer = levelRenderer;
        this.samplers.add(ServerMetricsSamplersProvider.tickTimeSampler((LongSupplier)longSupplier));
        this.registerStaticSamplers();
    }

    private void registerStaticSamplers() {
        this.samplers.addAll(ServerMetricsSamplersProvider.runtimeIndependentSamplers());
        this.samplers.add(MetricSampler.create((String)"totalChunks", (MetricCategory)MetricCategory.CHUNK_RENDERING, (Object)this.levelRenderer, LevelRenderer::getTotalSections));
        this.samplers.add(MetricSampler.create((String)"renderedChunks", (MetricCategory)MetricCategory.CHUNK_RENDERING, (Object)this.levelRenderer, LevelRenderer::countRenderedSections));
        this.samplers.add(MetricSampler.create((String)"lastViewDistance", (MetricCategory)MetricCategory.CHUNK_RENDERING, (Object)this.levelRenderer, LevelRenderer::getLastViewDistance));
        SectionRenderDispatcher sectionRenderDispatcher = this.levelRenderer.getSectionRenderDispatcher();
        if (sectionRenderDispatcher != null) {
            this.samplers.add(MetricSampler.create((String)"toUpload", (MetricCategory)MetricCategory.CHUNK_RENDERING_DISPATCHING, (Object)sectionRenderDispatcher, SectionRenderDispatcher::getToUpload));
            this.samplers.add(MetricSampler.create((String)"freeBufferCount", (MetricCategory)MetricCategory.CHUNK_RENDERING_DISPATCHING, (Object)sectionRenderDispatcher, SectionRenderDispatcher::getFreeBufferCount));
            this.samplers.add(MetricSampler.create((String)"compileQueueSize", (MetricCategory)MetricCategory.CHUNK_RENDERING_DISPATCHING, (Object)sectionRenderDispatcher, SectionRenderDispatcher::getCompileQueueSize));
        }
        this.samplers.add(MetricSampler.create((String)"gpuUtilization", (MetricCategory)MetricCategory.GPU, (Object)Minecraft.getInstance(), Minecraft::getGpuUtilization));
    }

    public Set<MetricSampler> samplers(Supplier<ProfileCollector> supplier) {
        this.samplers.addAll(this.samplerFactory.newSamplersFoundInProfiler(supplier));
        return this.samplers;
    }
}

