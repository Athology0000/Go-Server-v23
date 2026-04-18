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

import org.cobalt.render.rise.AbstractShaderPass;
import org.cobalt.render.rise.FullscreenQuadRenderer;
import org.cobalt.render.rise.RenderTarget;
import org.cobalt.render.rise.ShaderPassStage;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public final class ScreenSpacePass
extends AbstractShaderPass {
    public ScreenSpacePass() {
        super("/assets/cobalt/shaders/rise/post/screen_copy.frag", ShaderPassStage.OVERLAY);
    }

    public void renderTexture(int textureId, float alpha) {
        this.renderTexture(textureId, alpha, false);
    }

    public void renderTextureAdditive(int textureId, float alpha) {
        this.renderTexture(textureId, alpha, true);
    }

    public void renderToTarget(RenderTarget output, int textureId, float alpha) {
        if (textureId == 0) {
            return;
        }
        this.renderToTarget(output, () -> this.bindTextureUniforms(textureId, alpha));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderTexture(int textureId, float alpha, boolean additive) {
        if (!this.isValid() || textureId == 0) {
            return;
        }
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
        try {
            GL11.glDisable((int)2929);
            GL11.glDisable((int)2884);
            GL11.glEnable((int)3042);
            if (additive) {
                GL11.glBlendFunc((int)770, (int)1);
            } else {
                GL11.glBlendFunc((int)770, (int)771);
            }
            this.program.bind();
            this.bindTextureUniforms(textureId, alpha);
            FullscreenQuadRenderer.draw();
        }
        finally {
            GL20.glUseProgram((int)prevProgram);
            GL30.glBindVertexArray((int)prevVao);
            GL13.glActiveTexture((int)prevActiveTexture);
            GL11.glBindTexture((int)3553, (int)prevTexture);
            GL30.glBlendFuncSeparate((int)prevBlendSrcRgb, (int)prevBlendDstRgb, (int)prevBlendSrcAlpha, (int)prevBlendDstAlpha);
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

    private void bindTextureUniforms(int textureId, float alpha) {
        GL13.glActiveTexture((int)33984);
        GL11.glBindTexture((int)3553, (int)textureId);
        this.program.set1i("u_texture", 0);
        this.program.set1f("u_alpha", alpha);
    }
}

