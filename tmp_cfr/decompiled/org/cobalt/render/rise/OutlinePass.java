/*
 * Decompiled with CFR 0.152.
 */
package org.cobalt.render.rise;

import org.cobalt.render.rise.AbstractShaderPass;
import org.cobalt.render.rise.RenderTarget;
import org.cobalt.render.rise.ShaderPassStage;
import org.cobalt.render.rise.UniformHelper;

public final class OutlinePass
extends AbstractShaderPass {
    public OutlinePass() {
        super("/assets/minecraft/rise/shader/outline.frag", ShaderPassStage.OVERLAY);
    }

    public void render(RenderTarget input, RenderTarget output, float radius, int color) {
        if (input == null || !input.isReady()) {
            return;
        }
        this.renderToTarget(output, () -> {
            input.bindTexture(0);
            this.program.set1i("u_texture", 0);
            UniformHelper.setTexelSize(this.program, "u_texel_size", input.getWidth(), input.getHeight());
            this.program.set1f("u_radius", radius);
            UniformHelper.setColor(this.program, "u_color", color);
        });
    }
}

