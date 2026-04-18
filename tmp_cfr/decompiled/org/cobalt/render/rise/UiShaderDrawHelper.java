/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL30
 */
package org.cobalt.render.rise;

import org.cobalt.api.util.ui.helper.Gradient;
import org.cobalt.render.rise.FullscreenQuadRenderer;
import org.cobalt.render.rise.ShaderProgram;
import org.cobalt.render.rise.ShaderRegistry;
import org.cobalt.render.rise.UniformHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public final class UiShaderDrawHelper {
    private UiShaderDrawHelper() {
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float radius, int color, int framebufferHeight) {
        UiShaderDrawHelper.drawShader(ShaderRegistry.ROUNDED_RECT, x, y, width, height, framebufferHeight, program -> {
            program.set2f("u_size", width, height);
            program.set1f("u_radius", radius);
            UniformHelper.setColor(program, "u_color", color);
            program.set4f("u_edges", 1.0f, 1.0f, 1.0f, 1.0f);
        });
    }

    public static void drawGradientRoundedRect(float x, float y, float width, float height, float radius, int firstColor, int secondColor, Gradient gradient, int framebufferHeight) {
        UiShaderDrawHelper.drawShader(ShaderRegistry.GRADIENT_ROUNDED_RECT, x, y, width, height, framebufferHeight, program -> {
            program.set2f("u_size", width, height);
            program.set1f("u_radius", radius);
            UniformHelper.setColor(program, "u_first_color", firstColor);
            UniformHelper.setColor(program, "u_second_color", secondColor);
            program.set4f("u_edges", 1.0f, 1.0f, 1.0f, 1.0f);
            UiShaderDrawHelper.applyGradientDirection(program, gradient);
        });
    }

    public static void drawAnimatedGradientRoundedRect(float x, float y, float width, float height, float radius, int firstColor, int secondColor, Gradient gradient, float timeSeconds, int framebufferHeight) {
        UiShaderDrawHelper.drawShader(ShaderRegistry.ANIMATED_GRADIENT_ROUNDED_RECT, x, y, width, height, framebufferHeight, program -> {
            program.set2f("u_size", width, height);
            program.set1f("u_radius", radius);
            program.set1f("u_time", timeSeconds);
            UniformHelper.setColor(program, "u_first_color", firstColor);
            UniformHelper.setColor(program, "u_second_color", secondColor);
            program.set4f("u_edges", 1.0f, 1.0f, 1.0f, 1.0f);
            UiShaderDrawHelper.applyGradientDirection(program, gradient);
        });
    }

    public static void drawTriGradientRoundedRect(float x, float y, float width, float height, float radius, int firstColor, int secondColor, int thirdColor, Gradient gradient, float timeSeconds, int framebufferHeight) {
        UiShaderDrawHelper.drawShader(ShaderRegistry.TRI_GRADIENT_ROUNDED_RECT, x, y, width, height, framebufferHeight, program -> {
            program.set2f("u_size", width, height);
            program.set1f("u_radius", radius);
            program.set1f("u_time", timeSeconds);
            UniformHelper.setColor(program, "u_first_color", firstColor);
            UniformHelper.setColor(program, "u_second_color", secondColor);
            UniformHelper.setColor(program, "u_third_color", thirdColor);
            program.set4f("u_edges", 1.0f, 1.0f, 1.0f, 1.0f);
            UiShaderDrawHelper.applyGradientDirection(program, gradient);
        });
    }

    public static void drawRoundedOutline(float x, float y, float width, float height, float radius, float borderSize, int color, int framebufferHeight) {
        UiShaderDrawHelper.drawShader(ShaderRegistry.ROUNDED_OUTLINE, x, y, width, height, framebufferHeight, program -> {
            program.set2f("u_size", width, height);
            program.set1f("u_radius", radius);
            program.set1f("u_border_size", borderSize);
            UniformHelper.setColor(program, "u_color", color);
        });
    }

    public static void drawGradientOutline(float x, float y, float width, float height, float radius, float borderSize, int firstColor, int secondColor, Gradient gradient, int framebufferHeight) {
        UiShaderDrawHelper.drawShader(ShaderRegistry.GRADIENT_OUTLINE, x, y, width, height, framebufferHeight, program -> {
            program.set2f("u_size", width, height);
            program.set1f("u_radius", radius);
            program.set1f("u_border_size", borderSize);
            UniformHelper.setColor(program, "u_color_1", firstColor);
            UniformHelper.setColor(program, "u_color_2", secondColor);
        });
    }

    private static void applyGradientDirection(ShaderProgram program, Gradient gradient) {
        program.set1i("u_direction", gradient == Gradient.TopToBottom ? 1 : 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void drawShader(ShaderProgram program, float x, float y, float width, float height, int framebufferHeight, ShaderUniformBinder uniformBinder) {
        if (program == null || !program.isValid() || width <= 0.0f || height <= 0.0f || framebufferHeight <= 0) {
            return;
        }
        int prevProgram = GL11.glGetInteger((int)35725);
        int prevVao = GL11.glGetInteger((int)34229);
        int prevBlendSrcRgb = GL11.glGetInteger((int)32969);
        int prevBlendDstRgb = GL11.glGetInteger((int)32968);
        int prevBlendSrcAlpha = GL11.glGetInteger((int)32971);
        int prevBlendDstAlpha = GL11.glGetInteger((int)32970);
        boolean depthTestEnabled = GL11.glIsEnabled((int)2929);
        boolean blendEnabled = GL11.glIsEnabled((int)3042);
        boolean cullEnabled = GL11.glIsEnabled((int)2884);
        int[] viewport = new int[4];
        GL11.glGetIntegerv((int)2978, (int[])viewport);
        int viewportX = Math.round(x);
        int viewportY = Math.round((float)framebufferHeight - y - height);
        int viewportWidth = Math.max(1, Math.round(width));
        int viewportHeight = Math.max(1, Math.round(height));
        try {
            GL11.glDisable((int)2929);
            GL11.glDisable((int)2884);
            GL11.glEnable((int)3042);
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glViewport((int)viewportX, (int)viewportY, (int)viewportWidth, (int)viewportHeight);
            program.bind();
            uniformBinder.bind(program);
            FullscreenQuadRenderer.draw();
        }
        finally {
            GL20.glUseProgram((int)prevProgram);
            GL30.glBindVertexArray((int)prevVao);
            GL30.glBlendFuncSeparate((int)prevBlendSrcRgb, (int)prevBlendDstRgb, (int)prevBlendSrcAlpha, (int)prevBlendDstAlpha);
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

    @FunctionalInterface
    private static interface ShaderUniformBinder {
        public void bind(ShaderProgram var1);
    }
}

