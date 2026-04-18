/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL13
 *  org.lwjgl.opengl.GL30
 */
package org.cobalt.render.rise;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

public final class RenderTarget {
    private final boolean withDepthBuffer;
    private int fboId;
    private int textureId;
    private int depthBufferId;
    private int width;
    private int height;

    public RenderTarget() {
        this(false);
    }

    public RenderTarget(boolean withDepthBuffer) {
        this.withDepthBuffer = withDepthBuffer;
    }

    public void ensureSize(int newWidth, int newHeight) {
        int status;
        if (newWidth <= 0 || newHeight <= 0) {
            return;
        }
        if (newWidth == this.width && newHeight == this.height && this.fboId != 0) {
            return;
        }
        this.cleanup();
        this.width = newWidth;
        this.height = newHeight;
        this.fboId = GL30.glGenFramebuffers();
        this.textureId = GL11.glGenTextures();
        GL11.glBindTexture((int)3553, (int)this.textureId);
        GL11.glTexImage2D((int)3553, (int)0, (int)32856, (int)this.width, (int)this.height, (int)0, (int)6408, (int)5121, (long)0L);
        GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
        GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
        GL11.glTexParameteri((int)3553, (int)10242, (int)33071);
        GL11.glTexParameteri((int)3553, (int)10243, (int)33071);
        GL11.glBindTexture((int)3553, (int)0);
        GL30.glBindFramebuffer((int)36160, (int)this.fboId);
        GL30.glFramebufferTexture2D((int)36160, (int)36064, (int)3553, (int)this.textureId, (int)0);
        if (this.withDepthBuffer) {
            this.depthBufferId = GL30.glGenRenderbuffers();
            GL30.glBindRenderbuffer((int)36161, (int)this.depthBufferId);
            GL30.glRenderbufferStorage((int)36161, (int)35056, (int)this.width, (int)this.height);
            GL30.glFramebufferRenderbuffer((int)36160, (int)33306, (int)36161, (int)this.depthBufferId);
            GL30.glBindRenderbuffer((int)36161, (int)0);
        }
        if ((status = GL30.glCheckFramebufferStatus((int)36160)) != 36053) {
            System.err.println("[RenderTarget] Framebuffer incomplete: 0x" + Integer.toHexString(status));
        }
        GL30.glBindFramebuffer((int)36160, (int)0);
    }

    public void bind() {
        GL30.glBindFramebuffer((int)36160, (int)this.fboId);
        GL11.glViewport((int)0, (int)0, (int)this.width, (int)this.height);
    }

    public void clear(float red, float green, float blue, float alpha) {
        this.bind();
        GL11.glClearColor((float)red, (float)green, (float)blue, (float)alpha);
        int clearMask = 16384;
        if (this.withDepthBuffer) {
            clearMask |= 0x100;
        }
        GL11.glClear((int)clearMask);
    }

    public void bindTexture(int textureUnit) {
        GL13.glActiveTexture((int)(33984 + textureUnit));
        GL11.glBindTexture((int)3553, (int)this.textureId);
    }

    public boolean isReady() {
        return this.fboId != 0 && this.textureId != 0 && this.width > 0 && this.height > 0;
    }

    public int getTextureId() {
        return this.textureId;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void cleanup() {
        if (this.depthBufferId != 0) {
            GL30.glDeleteRenderbuffers((int)this.depthBufferId);
            this.depthBufferId = 0;
        }
        if (this.textureId != 0) {
            GL11.glDeleteTextures((int)this.textureId);
            this.textureId = 0;
        }
        if (this.fboId != 0) {
            GL30.glDeleteFramebuffers((int)this.fboId);
            this.fboId = 0;
        }
        this.width = 0;
        this.height = 0;
    }
}

