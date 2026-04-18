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
import org.cobalt.render.rise.ShaderPassStage;
import org.cobalt.render.rise.ShaderProgram;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public abstract class AbstractShaderPass {
    protected final ShaderProgram program;
    private final ShaderPassStage stage;

    protected AbstractShaderPass(String fragmentPath, ShaderPassStage stage) {
        this("/assets/cobalt/shaders/rise/quad.vert", fragmentPath, stage);
    }

    protected AbstractShaderPass(String vertexPath, String fragmentPath, ShaderPassStage stage) {
        this.program = ShaderProgram.fromResources(vertexPath, fragmentPath);
        this.stage = stage;
    }

    public boolean isValid() {
        return this.program != null && this.program.isValid();
    }

    public ShaderPassStage getStage() {
        return this.stage;
    }

    public void cleanup() {
        if (this.program != null) {
            this.program.cleanup();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void renderToTarget(RenderTarget output, Runnable uniformBinder) {
        if (!this.isValid() || output == null || !output.isReady()) {
            return;
        }
        int prevFbo = GL11.glGetInteger((int)36006);
        int prevProgram = GL11.glGetInteger((int)35725);
        int prevVao = GL11.glGetInteger((int)34229);
        int prevActiveTexture = GL11.glGetInteger((int)34016);
        int prevTexture = GL11.glGetInteger((int)32873);
        int prevBlendSrcRgb = GL11.glGetInteger((int)32969);
        int prevBlendDstRgb = GL11.glGetInteger((int)32968);
        int prevBlendSrcAlpha = GL11.glGetInteger((int)32971);
        int prevBlendDstAlpha = GL11.glGetInteger((int)32970);
        boolean depthTestEnabled = GL11.glIsEnabled((int)2929);
        boolean blendEnabled = GL11.glIsEnabled((int)3042);
        boolean cullEnabled = GL11.glIsEnabled((int)2884);
        int[] viewport = new int[4];
        GL11.glGetIntegerv((int)2978, (int[])viewport);
        try {
            output.clear(0.0f, 0.0f, 0.0f, 0.0f);
            GL11.glDisable((int)2929);
            GL11.glDisable((int)2884);
            GL11.glEnable((int)3042);
            GL11.glBlendFunc((int)770, (int)771);
            this.program.bind();
            uniformBinder.run();
            FullscreenQuadRenderer.draw();
        }
        finally {
            GL20.glUseProgram((int)prevProgram);
            GL30.glBindVertexArray((int)prevVao);
            GL13.glActiveTexture((int)prevActiveTexture);
            GL11.glBindTexture((int)3553, (int)prevTexture);
            GL30.glBlendFuncSeparate((int)prevBlendSrcRgb, (int)prevBlendDstRgb, (int)prevBlendSrcAlpha, (int)prevBlendDstAlpha);
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
        }
    }
}

