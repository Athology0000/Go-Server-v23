/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL13
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL30
 */
package org.cobalt.render.rise;

import org.cobalt.render.rise.FullscreenQuadRenderer;
import org.cobalt.render.rise.RenderTarget;
import org.cobalt.render.rise.ShaderProgram;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public final class MainMenuBackgroundPass {
    private final ShaderProgram backgroundProgram = ShaderProgram.fromResources("/assets/cobalt/shaders/rise/quad.vert", "/assets/minecraft/rise/shader/main_menu/background.frag");
    private final ShaderProgram filterProgram = ShaderProgram.fromResources("/assets/cobalt/shaders/rise/quad.vert", "/assets/minecraft/rise/shader/main_menu/filter.glsl");
    private final RenderTarget sceneTarget = new RenderTarget();
    private final RenderTarget filteredTarget = new RenderTarget();
    private boolean filterEnabled = true;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int renderToTexture(int width, int height, float timeSeconds) {
        if (width <= 0 || height <= 0 || !this.backgroundProgram.isValid()) {
            return 0;
        }
        this.sceneTarget.ensureSize(width, height);
        this.filteredTarget.ensureSize(width, height);
        int prevFbo = GL11.glGetInteger((int)36006);
        int prevProgram = GL11.glGetInteger((int)35725);
        int prevVao = GL11.glGetInteger((int)34229);
        int prevActiveTexture = GL11.glGetInteger((int)34016);
        int prevTexture = GL11.glGetInteger((int)32873);
        boolean depthTestEnabled = GL11.glIsEnabled((int)2929);
        boolean blendEnabled = GL11.glIsEnabled((int)3042);
        boolean cullEnabled = GL11.glIsEnabled((int)2884);
        boolean scissorEnabled = GL11.glIsEnabled((int)3089);
        int[] viewport = new int[4];
        GL11.glGetIntegerv((int)2978, (int[])viewport);
        try {
            GL11.glDisable((int)2929);
            GL11.glDisable((int)3042);
            GL11.glDisable((int)2884);
            if (scissorEnabled) {
                GL11.glDisable((int)3089);
            }
            this.sceneTarget.clear(0.0f, 0.0f, 0.0f, 1.0f);
            this.backgroundProgram.bind();
            this.backgroundProgram.set2f("resolution", width, height);
            this.backgroundProgram.set1f("time", timeSeconds);
            FullscreenQuadRenderer.draw();
            if (!this.filterEnabled || !this.filterProgram.isValid()) {
                int n = this.sceneTarget.getTextureId();
                return n;
            }
            this.filteredTarget.clear(0.0f, 0.0f, 0.0f, 1.0f);
            this.filterProgram.bind();
            this.sceneTarget.bindTexture(0);
            this.filterProgram.set1i("u_texture", 0);
            this.filterProgram.set2f("u_texelSize", width, height);
            this.filterProgram.set1f("u_time", timeSeconds);
            FullscreenQuadRenderer.draw();
            int n = this.filteredTarget.getTextureId();
            return n;
        }
        finally {
            GL20.glUseProgram((int)prevProgram);
            GL30.glBindVertexArray((int)prevVao);
            GL13.glActiveTexture((int)prevActiveTexture);
            GL11.glBindTexture((int)3553, (int)prevTexture);
            GL30.glBindFramebuffer((int)36160, (int)prevFbo);
            GL11.glViewport((int)viewport[0], (int)viewport[1], (int)viewport[2], (int)viewport[3]);
            if (depthTestEnabled) {
                GL11.glEnable((int)2929);
            } else {
                GL11.glDisable((int)2929);
            }
            if (blendEnabled) {
                GL11.glEnable((int)3042);
            } else {
                GL11.glDisable((int)3042);
            }
            if (cullEnabled) {
                GL11.glEnable((int)2884);
            } else {
                GL11.glDisable((int)2884);
            }
            if (scissorEnabled) {
                GL11.glEnable((int)3089);
            } else {
                GL11.glDisable((int)3089);
            }
        }
    }

    public void setFilterEnabled(boolean filterEnabled) {
        this.filterEnabled = filterEnabled;
    }

    public void cleanup() {
        this.backgroundProgram.cleanup();
        this.filterProgram.cleanup();
        this.sceneTarget.cleanup();
        this.filteredTarget.cleanup();
    }
}

