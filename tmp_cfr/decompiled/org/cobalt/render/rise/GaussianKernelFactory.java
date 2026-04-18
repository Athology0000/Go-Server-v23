/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 */
package org.cobalt.render.rise;

import java.nio.FloatBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.lwjgl.BufferUtils;

public final class GaussianKernelFactory {
    private static final Map<Integer, float[]> CACHE = new ConcurrentHashMap<Integer, float[]>();

    private GaussianKernelFactory() {
    }

    public static FloatBuffer getKernel(int radius) {
        return GaussianKernelFactory.getKernel(radius, 32);
    }

    public static FloatBuffer getKernel(int radius, int kernelSize) {
        int clampedKernelSize = Math.max(1, kernelSize);
        int clampedRadius = Math.max(0, Math.min(radius, clampedKernelSize - 1));
        int cacheKey = clampedKernelSize << 16 | clampedRadius;
        float[] cached = CACHE.computeIfAbsent(cacheKey, key -> GaussianKernelFactory.buildKernel(clampedRadius, clampedKernelSize));
        FloatBuffer buffer = BufferUtils.createFloatBuffer((int)cached.length);
        buffer.put(cached);
        buffer.flip();
        return buffer;
    }

    private static float[] buildKernel(int radius, int kernelSize) {
        int i;
        float[] kernel = new float[kernelSize];
        if (radius <= 0) {
            kernel[0] = 1.0f;
            return kernel;
        }
        double sigma = Math.max(1.0, (double)radius / 2.0);
        double sigmaSq = sigma * sigma;
        double sum = 0.0;
        for (i = 0; i <= radius; ++i) {
            double weight = Math.exp((double)(-(i * i)) / (2.0 * sigmaSq));
            kernel[i] = (float)weight;
            sum += i == 0 ? weight : weight * 2.0;
        }
        i = 0;
        while (i <= radius) {
            int n = i++;
            kernel[n] = kernel[n] / (float)sum;
        }
        return kernel;
    }
}

