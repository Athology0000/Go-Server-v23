/*
 * Decompiled with CFR 0.152.
 */
package org.cobalt.render.rise;

import org.cobalt.render.rise.BlurPass;
import org.cobalt.render.rise.RenderTarget;

public final class BloomPass {
    private final BlurPass horizontalPass;
    private final BlurPass verticalPass;

    public BloomPass(BlurPass horizontalPass, BlurPass verticalPass) {
        this.horizontalPass = horizontalPass;
        this.verticalPass = verticalPass;
    }

    public boolean isValid() {
        return this.horizontalPass != null && this.horizontalPass.isValid() && this.verticalPass != null && this.verticalPass.isValid();
    }

    public void render(RenderTarget input, RenderTarget ping, RenderTarget output, int radius) {
        if (!this.isValid() || ping == null || output == null) {
            return;
        }
        this.horizontalPass.render(input, input, ping, radius, 1.0f, 0.0f);
        this.verticalPass.render(ping, input, output, radius, 0.0f, 1.0f);
    }
}

