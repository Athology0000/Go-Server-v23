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
import org.cobalt.render.DarkTintShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class DarkModeRenderer {
    private static DarkTintShader tintShader;
    private static boolean initialized;
    private static int quadVAO;
    private static int quadVBO;
    private static int tempTexture;
    private static int tempWidth;
    private static int tempHeight;
    private static long lastValidationTime;
    private static final long VALIDATION_INTERVAL_MS = 5000L;
    private static int lastFbWidth;
    private static int lastFbHeight;
    private static int resizeSkipFrames;
    private static long lastRenderNs;
    private static float tintR;
    private static float tintG;
    private static float tintB;
    private static float tintA;
    private static float intensity;
    private static int blendMode;
    private static float vignetteStrength;
    private static float saturation;
    private static float contrast;
    private static float chromaticAberration;
    private static float brightness;
    private static boolean excludeViewmodel;
    private static float depthThreshold;

    public static void init() {
        if (initialized) {
            return;
        }
        try {
            tintShader = new DarkTintShader();
            if (tintShader == null || !tintShader.isValid()) {
                tintShader = null;
                return;
            }
            DarkModeRenderer.createFullscreenQuad();
            initialized = true;
        }
        catch (Exception e) {
            System.err.println("[DarkModeRenderer] Failed to initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createFullscreenQuad() {
        float[] vertices = new float[]{-1.0f, 1.0f, 0.0f, 1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f};
        quadVAO = GL30.glGenVertexArrays();
        quadVBO = GL20.glGenBuffers();
        GL30.glBindVertexArray((int)quadVAO);
        GL20.glBindBuffer((int)34962, (int)quadVBO);
        GL20.glBufferData((int)34962, (float[])vertices, (int)35044);
        GL30.glEnableVertexAttribArray((int)0);
        GL20.glVertexAttribPointer((int)0, (int)2, (int)5126, (boolean)false, (int)16, (long)0L);
        GL30.glEnableVertexAttribArray((int)1);
        GL20.glVertexAttribPointer((int)1, (int)2, (int)5126, (boolean)false, (int)16, (long)8L);
        GL30.glBindVertexArray((int)0);
    }

    private static void ensureTempTexture(int width, int height) {
        if (tempTexture == 0) {
            tempTexture = GL11.glGenTextures();
        }
        if (tempWidth != width || tempHeight != height) {
            GL11.glBindTexture((int)3553, (int)tempTexture);
            GL11.glTexImage2D((int)3553, (int)0, (int)6408, (int)width, (int)height, (int)0, (int)6408, (int)5121, (long)0L);
            GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
            GL11.glTexParameteri((int)3553, (int)10242, (int)33071);
            GL11.glTexParameteri((int)3553, (int)10243, (int)33071);
            GL11.glBindTexture((int)3553, (int)0);
            tempWidth = width;
            tempHeight = height;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void renderDarkModeOverlay() {
        long nowNs = System.nanoTime();
        if (nowNs - lastRenderNs < 1000000L) {
            return;
        }
        lastRenderNs = nowNs;
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        if (!initialized) {
            DarkModeRenderer.init();
        }
        if (!initialized || tintShader == null || !tintShader.isValid()) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastValidationTime > 5000L) {
            lastValidationTime = currentTime;
            if (quadVAO == 0 || quadVBO == 0) {
                initialized = false;
                DarkModeRenderer.init();
                if (!initialized) {
                    return;
                }
            }
            if (tempTexture != 0 && !GL11.glIsTexture((int)tempTexture)) {
                tempTexture = 0;
                tempWidth = 0;
                tempHeight = 0;
            }
        }
        int prevShaderProgram = 0;
        int prevVAO = 0;
        int prevActiveTexture = 0;
        int prevBoundTexture = 0;
        int prevBlendSrcRGB = 0;
        int prevBlendDstRGB = 0;
        int prevBlendSrcAlpha = 0;
        int prevBlendDstAlpha = 0;
        int prevBlendEquationRGB = 0;
        int prevBlendEquationAlpha = 0;
        boolean blendEnabled = false;
        boolean depthTestEnabled = false;
        boolean cullFaceEnabled = false;
        boolean scissorTestEnabled = false;
        boolean depthMaskEnabled = false;
        int[] viewport = new int[4];
        boolean stateCaptured = false;
        try {
            class_276 mainFramebuffer = mc.method_1522();
            if (mainFramebuffer == null) {
                return;
            }
            int fbWidth = mainFramebuffer.field_1482;
            int fbHeight = mainFramebuffer.field_1481;
            if (fbWidth <= 0 || fbHeight <= 0) {
                return;
            }
            if (fbWidth != lastFbWidth || fbHeight != lastFbHeight) {
                lastFbWidth = fbWidth;
                lastFbHeight = fbHeight;
                resizeSkipFrames = 3;
                tempWidth = 0;
                tempHeight = 0;
            }
            if (resizeSkipFrames > 0) {
                --resizeSkipFrames;
                return;
            }
            DarkModeRenderer.ensureTempTexture(fbWidth, fbHeight);
            prevShaderProgram = GL11.glGetInteger((int)35725);
            prevVAO = GL11.glGetInteger((int)34229);
            prevActiveTexture = GL11.glGetInteger((int)34016);
            prevBoundTexture = GL11.glGetInteger((int)32873);
            prevBlendSrcRGB = GL11.glGetInteger((int)32969);
            prevBlendDstRGB = GL11.glGetInteger((int)32968);
            prevBlendSrcAlpha = GL11.glGetInteger((int)32971);
            prevBlendDstAlpha = GL11.glGetInteger((int)32970);
            prevBlendEquationRGB = GL11.glGetInteger((int)32777);
            prevBlendEquationAlpha = GL11.glGetInteger((int)34877);
            blendEnabled = GL11.glIsEnabled((int)3042);
            depthTestEnabled = GL11.glIsEnabled((int)2929);
            cullFaceEnabled = GL11.glIsEnabled((int)2884);
            scissorTestEnabled = GL11.glIsEnabled((int)3089);
            depthMaskEnabled = GL11.glGetBoolean((int)2930);
            GL11.glGetIntegerv((int)2978, (int[])viewport);
            stateCaptured = true;
            GL11.glViewport((int)0, (int)0, (int)fbWidth, (int)fbHeight);
            GL13.glActiveTexture((int)33984);
            GL11.glBindTexture((int)3553, (int)tempTexture);
            GL11.glCopyTexSubImage2D((int)3553, (int)0, (int)0, (int)0, (int)0, (int)0, (int)fbWidth, (int)fbHeight);
            if (scissorTestEnabled) {
                GL11.glDisable((int)3089);
            }
            GL11.glDisable((int)2929);
            GL11.glDepthMask((boolean)false);
            GL11.glDisable((int)2884);
            GL11.glEnable((int)3042);
            GL11.glBlendFunc((int)770, (int)771);
            GL20.glBlendEquationSeparate((int)32774, (int)32774);
            if (!tintShader.use()) {
                System.err.println("[DarkModeRenderer] Shader program failed to bind; reinitializing.");
                tintShader.cleanup();
                tintShader = null;
                initialized = false;
                return;
            }
            tintShader.setTexture(0);
            tintShader.setExcludeViewmodel(false);
            tintShader.setDepthThreshold(depthThreshold);
            tintShader.setTintColor(tintR, tintG, tintB, tintA);
            tintShader.setIntensity(intensity);
            tintShader.setBlendMode(blendMode);
            tintShader.setVignetteStrength(vignetteStrength);
            tintShader.setSaturation(saturation);
            tintShader.setContrast(contrast);
            tintShader.setChromaticAberration(chromaticAberration);
            tintShader.setBrightness(brightness);
            GL30.glBindVertexArray((int)quadVAO);
            GL11.glDrawArrays((int)4, (int)0, (int)6);
            GL30.glBindVertexArray((int)0);
        }
        catch (Exception e) {
            System.err.println("[DarkModeRenderer] Error during rendering: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            if (!stateCaptured) {
                return;
            }
            GL20.glUseProgram((int)prevShaderProgram);
            GL30.glBindVertexArray((int)prevVAO);
            GL13.glActiveTexture((int)prevActiveTexture);
            GL11.glBindTexture((int)3553, (int)prevBoundTexture);
            GL30.glBlendFuncSeparate((int)prevBlendSrcRGB, (int)prevBlendDstRGB, (int)prevBlendSrcAlpha, (int)prevBlendDstAlpha);
            GL20.glBlendEquationSeparate((int)prevBlendEquationRGB, (int)prevBlendEquationAlpha);
            if (!blendEnabled) {
                GL11.glDisable((int)3042);
            }
            if (depthTestEnabled) {
                GL11.glEnable((int)2929);
            }
            if (cullFaceEnabled) {
                GL11.glEnable((int)2884);
            }
            if (scissorTestEnabled) {
                GL11.glEnable((int)3089);
            }
            GL11.glDepthMask((boolean)depthMaskEnabled);
            GL11.glViewport((int)viewport[0], (int)viewport[1], (int)viewport[2], (int)viewport[3]);
        }
    }

    public static void setTintColor(float r, float g, float b) {
        tintR = r;
        tintG = g;
        tintB = b;
    }

    public static void setIntensity(float value) {
        intensity = Math.max(0.0f, Math.min(1.0f, value));
    }

    public static void setBlendMode(int mode) {
        blendMode = Math.max(0, Math.min(3, mode));
    }

    public static void setVignetteStrength(float strength) {
        vignetteStrength = Math.max(0.0f, Math.min(1.0f, strength));
    }

    public static void setSaturation(float sat) {
        saturation = Math.max(0.0f, Math.min(2.0f, sat));
    }

    public static void setContrast(float con) {
        contrast = Math.max(0.0f, Math.min(2.0f, con));
    }

    public static void setChromaticAberration(float amount) {
        chromaticAberration = Math.max(0.0f, Math.min(0.01f, amount));
    }

    public static void setBrightness(float bright) {
        brightness = Math.max(0.1f, Math.min(5.0f, bright));
    }

    public static void setExcludeViewmodel(boolean exclude) {
        excludeViewmodel = exclude;
    }

    public static void setDepthThreshold(float threshold) {
        depthThreshold = Math.max(0.0f, Math.min(1.0f, threshold));
    }

    public static void cleanup() {
        if (tintShader != null) {
            tintShader.cleanup();
            tintShader = null;
        }
        if (quadVAO != 0) {
            GL30.glDeleteVertexArrays((int)quadVAO);
        }
        if (quadVBO != 0) {
            GL20.glDeleteBuffers((int)quadVBO);
        }
        if (tempTexture != 0) {
            GL11.glDeleteTextures((int)tempTexture);
            tempTexture = 0;
        }
        tempWidth = 0;
        tempHeight = 0;
        initialized = false;
    }

    static {
        initialized = false;
        tempTexture = 0;
        tempWidth = 0;
        tempHeight = 0;
        lastValidationTime = 0L;
        lastFbWidth = 0;
        lastFbHeight = 0;
        resizeSkipFrames = 0;
        lastRenderNs = 0L;
        tintR = 0.2f;
        tintG = 0.1f;
        tintB = 0.3f;
        tintA = 1.0f;
        intensity = 0.6f;
        blendMode = 0;
        vignetteStrength = 0.0f;
        saturation = 1.0f;
        contrast = 1.0f;
        chromaticAberration = 0.0f;
        brightness = 1.5f;
        excludeViewmodel = true;
        depthThreshold = 0.15f;
    }
}

