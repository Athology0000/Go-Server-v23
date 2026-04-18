/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_276
 *  net.minecraft.class_310
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL13
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL30
 */
package org.cobalt.render;

import net.minecraft.class_276;
import net.minecraft.class_310;
import org.cobalt.render.HudGlassBlurShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class HudGlassBlurRenderer {
    private static final class_310 MC = class_310.method_1551();
    private static HudGlassBlurShader blurShader;
    private static boolean initialized;
    private static int quadVao;
    private static int quadVbo;
    private static int captureTexture;
    private static int captureWidth;
    private static int captureHeight;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void renderBlurRect(float x, float y, float width, float height, float cornerRadius, float blurStrength) {
        if (width <= 1.0f || height <= 1.0f) {
            return;
        }
        if (!initialized) {
            HudGlassBlurRenderer.init();
        }
        if (!initialized || blurShader == null || !blurShader.isValid()) {
            return;
        }
        class_276 framebuffer = MC.method_1522();
        if (framebuffer == null) {
            return;
        }
        int fbWidth = framebuffer.field_1482;
        int fbHeight = framebuffer.field_1481;
        if (fbWidth <= 0 || fbHeight <= 0) {
            return;
        }
        HudGlassBlurRenderer.ensureCaptureTexture(fbWidth, fbHeight);
        int prevProgram = 0;
        int prevVao = 0;
        int prevActiveTexture = 0;
        int prevBoundTexture = 0;
        int prevBlendSrcRgb = 0;
        int prevBlendDstRgb = 0;
        int prevBlendSrcAlpha = 0;
        int prevBlendDstAlpha = 0;
        int prevBlendEquationRgb = 0;
        int prevBlendEquationAlpha = 0;
        boolean blendEnabled = false;
        boolean depthTestEnabled = false;
        boolean cullFaceEnabled = false;
        boolean scissorEnabled = false;
        boolean depthMaskEnabled = false;
        int[] viewport = new int[4];
        boolean stateCaptured = false;
        try {
            prevProgram = GL11.glGetInteger((int)35725);
            prevVao = GL11.glGetInteger((int)34229);
            prevActiveTexture = GL11.glGetInteger((int)34016);
            prevBoundTexture = GL11.glGetInteger((int)32873);
            prevBlendSrcRgb = GL11.glGetInteger((int)32969);
            prevBlendDstRgb = GL11.glGetInteger((int)32968);
            prevBlendSrcAlpha = GL11.glGetInteger((int)32971);
            prevBlendDstAlpha = GL11.glGetInteger((int)32970);
            prevBlendEquationRgb = GL11.glGetInteger((int)32777);
            prevBlendEquationAlpha = GL11.glGetInteger((int)34877);
            blendEnabled = GL11.glIsEnabled((int)3042);
            depthTestEnabled = GL11.glIsEnabled((int)2929);
            cullFaceEnabled = GL11.glIsEnabled((int)2884);
            scissorEnabled = GL11.glIsEnabled((int)3089);
            depthMaskEnabled = GL11.glGetBoolean((int)2930);
            GL11.glGetIntegerv((int)2978, (int[])viewport);
            stateCaptured = true;
            GL11.glViewport((int)0, (int)0, (int)fbWidth, (int)fbHeight);
            GL13.glActiveTexture((int)33984);
            GL11.glBindTexture((int)3553, (int)captureTexture);
            GL11.glCopyTexSubImage2D((int)3553, (int)0, (int)0, (int)0, (int)0, (int)0, (int)fbWidth, (int)fbHeight);
            if (scissorEnabled) {
                GL11.glDisable((int)3089);
            }
            GL11.glDisable((int)2929);
            GL11.glDepthMask((boolean)false);
            GL11.glDisable((int)2884);
            GL11.glEnable((int)3042);
            GL11.glBlendFunc((int)770, (int)771);
            GL20.glBlendEquationSeparate((int)32774, (int)32774);
            if (!blurShader.use()) {
                initialized = false;
                return;
            }
            blurShader.setTexture(0);
            blurShader.setScreenSize(fbWidth, fbHeight);
            blurShader.setRect(x, y, width, height);
            blurShader.setCornerRadius(Math.max(0.0f, Math.min(cornerRadius, Math.min(width, height) * 0.5f)));
            blurShader.setBlurStrength(Math.max(0.75f, blurStrength));
            GL30.glBindVertexArray((int)quadVao);
            GL11.glDrawArrays((int)4, (int)0, (int)6);
            GL30.glBindVertexArray((int)0);
        }
        catch (Exception e) {
            System.err.println("[HudGlassBlurRenderer] Error during blur rendering: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            if (!stateCaptured) {
                return;
            }
            GL20.glUseProgram((int)prevProgram);
            GL30.glBindVertexArray((int)prevVao);
            GL13.glActiveTexture((int)prevActiveTexture);
            GL11.glBindTexture((int)3553, (int)prevBoundTexture);
            GL30.glBlendFuncSeparate((int)prevBlendSrcRgb, (int)prevBlendDstRgb, (int)prevBlendSrcAlpha, (int)prevBlendDstAlpha);
            GL20.glBlendEquationSeparate((int)prevBlendEquationRgb, (int)prevBlendEquationAlpha);
            if (!blendEnabled) {
                GL11.glDisable((int)3042);
            }
            if (depthTestEnabled) {
                GL11.glEnable((int)2929);
            }
            if (cullFaceEnabled) {
                GL11.glEnable((int)2884);
            }
            if (scissorEnabled) {
                GL11.glEnable((int)3089);
            }
            GL11.glDepthMask((boolean)depthMaskEnabled);
            GL11.glViewport((int)viewport[0], (int)viewport[1], (int)viewport[2], (int)viewport[3]);
        }
    }

    public static void cleanup() {
        if (blurShader != null) {
            blurShader.cleanup();
            blurShader = null;
        }
        if (quadVao != 0) {
            GL30.glDeleteVertexArrays((int)quadVao);
        }
        if (quadVbo != 0) {
            GL20.glDeleteBuffers((int)quadVbo);
        }
        if (captureTexture != 0) {
            GL11.glDeleteTextures((int)captureTexture);
        }
        quadVao = 0;
        quadVbo = 0;
        captureTexture = 0;
        captureWidth = 0;
        captureHeight = 0;
        initialized = false;
    }

    private static void init() {
        try {
            blurShader = new HudGlassBlurShader();
            if (blurShader == null || !blurShader.isValid()) {
                blurShader = null;
                return;
            }
            HudGlassBlurRenderer.createFullscreenQuad();
            initialized = true;
        }
        catch (Exception e) {
            System.err.println("[HudGlassBlurRenderer] Failed to initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createFullscreenQuad() {
        float[] vertices = new float[]{-1.0f, 1.0f, 0.0f, 1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f};
        quadVao = GL30.glGenVertexArrays();
        quadVbo = GL20.glGenBuffers();
        GL30.glBindVertexArray((int)quadVao);
        GL20.glBindBuffer((int)34962, (int)quadVbo);
        GL20.glBufferData((int)34962, (float[])vertices, (int)35044);
        GL30.glEnableVertexAttribArray((int)0);
        GL20.glVertexAttribPointer((int)0, (int)2, (int)5126, (boolean)false, (int)16, (long)0L);
        GL30.glEnableVertexAttribArray((int)1);
        GL20.glVertexAttribPointer((int)1, (int)2, (int)5126, (boolean)false, (int)16, (long)8L);
        GL30.glBindVertexArray((int)0);
    }

    private static void ensureCaptureTexture(int width, int height) {
        if (captureTexture == 0) {
            captureTexture = GL11.glGenTextures();
        }
        if (captureWidth == width && captureHeight == height) {
            return;
        }
        GL11.glBindTexture((int)3553, (int)captureTexture);
        GL11.glTexImage2D((int)3553, (int)0, (int)6408, (int)width, (int)height, (int)0, (int)6408, (int)5121, (long)0L);
        GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
        GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
        GL11.glTexParameteri((int)3553, (int)10242, (int)33071);
        GL11.glTexParameteri((int)3553, (int)10243, (int)33071);
        GL11.glBindTexture((int)3553, (int)0);
        captureWidth = width;
        captureHeight = height;
    }

    static {
        initialized = false;
    }
}

