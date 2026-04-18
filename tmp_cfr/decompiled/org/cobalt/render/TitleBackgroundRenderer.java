/*
 * Decompiled with CFR 0.152.
 */
package org.cobalt.render;

import org.cobalt.render.rise.ShaderRegistry;

public final class TitleBackgroundRenderer {
    private TitleBackgroundRenderer() {
    }

    public static void init() {
    }

    public static int renderToTexture(int width, int height, float time) {
        return ShaderRegistry.MAIN_MENU_BACKGROUND.renderToTexture(width, height, time);
    }

    public static void renderToScreen(int width, int height, float time) {
        int texture = TitleBackgroundRenderer.renderToTexture(width, height, time);
        if (texture != 0) {
            ShaderRegistry.SCREEN_SPACE.renderTexture(texture, 1.0f);
        }
    }

    public static void cleanup() {
        ShaderRegistry.cleanup();
    }
}

