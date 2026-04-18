/*
 * Decompiled with CFR 0.152.
 */
package org.cobalt.render.rise;

import java.nio.FloatBuffer;
import org.cobalt.render.rise.AbstractShaderPass;
import org.cobalt.render.rise.GaussianKernelFactory;
import org.cobalt.render.rise.RenderTarget;
import org.cobalt.render.rise.ShaderPassStage;
import org.cobalt.render.rise.UniformHelper;

public final class BlurPass
extends AbstractShaderPass {
    private final int maxRadius;
    private final int kernelSize;

    public BlurPass() {
        this("/assets/minecraft/rise/shader/blur.frag", 127, 128);
    }

    public BlurPass(String fragmentPath) {
        this(fragmentPath, 31, 32);
    }

    public BlurPass(String fragmentPath, int maxRadius, int kernelSize) {
        super(fragmentPath, ShaderPassStage.OVERLAY);
        this.maxRadius = Math.max(0, maxRadius);
        this.kernelSize = Math.max(1, kernelSize);
    }

    public void render(RenderTarget input, RenderTarget output, int radius, float directionX, float directionY) {
        this.render(input, input, output, radius, directionX, directionY);
    }

    public void render(RenderTarget input, RenderTarget other, RenderTarget output, int radius, float directionX, float directionY) {
        if (input == null || !input.isReady()) {
            return;
        }
        int clampedRadius = Math.max(0, Math.min(radius, Math.min(this.maxRadius, this.kernelSize - 1)));
        FloatBuffer kernel = GaussianKernelFactory.getKernel(clampedRadius, this.kernelSize);
        this.renderToTarget(output, () -> {
            input.bindTexture(0);
            this.program.set1i("u_diffuse_sampler", 0);
            if (other != null && other.isReady()) {
                other.bindTexture(1);
            } else {
                input.bindTexture(1);
            }
            this.program.set1i("u_other_sampler", 1);
            this.program.set1f("u_radius", clampedRadius);
            UniformHelper.setTexelSize(this.program, "u_texel_size", input.getWidth(), input.getHeight());
            UniformHelper.setDirection(this.program, "u_direction", directionX, directionY);
            UniformHelper.uploadKernel(this.program, "u_kernel", kernel);
        });
    }
}

