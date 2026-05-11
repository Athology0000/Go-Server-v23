package org.cobalt.render;

import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.cobalt.render.rise.ShaderRegistry;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class HudGlassBlurRenderer {

  private static final Minecraft MC = Minecraft.getInstance();

  private static HudGlassBlurShader blurShader;
  private static boolean initialized = false;
  private static final boolean ENABLE_SHADER_BLUR = false;

  private static int quadVao;
  private static int quadVbo;

  private static final org.cobalt.render.rise.RenderTarget sourceTarget = new org.cobalt.render.rise.RenderTarget();
  private static final org.cobalt.render.rise.RenderTarget blurTempTarget = new org.cobalt.render.rise.RenderTarget();
  private static final org.cobalt.render.rise.RenderTarget blurredTarget = new org.cobalt.render.rise.RenderTarget();

  public static void renderBlurRect(float x, float y, float width, float height, float cornerRadius, float blurStrength) {
    if (!ENABLE_SHADER_BLUR) {
      return;
    }

    if (width <= 1f || height <= 1f) {
      return;
    }

    if (!initialized) {
      init();
    }
    if (!initialized || blurShader == null || !blurShader.isValid()) {
      return;
    }

    RenderTarget framebuffer = MC.getMainRenderTarget();
    if (framebuffer == null) {
      return;
    }

    int fbWidth = framebuffer.width;
    int fbHeight = framebuffer.height;
    if (fbWidth <= 0 || fbHeight <= 0) {
      return;
    }

    int screenWidth = MC.getWindow().getScreenWidth();
    int screenHeight = MC.getWindow().getScreenHeight();
    if (screenWidth <= 0 || screenHeight <= 0) {
      return;
    }

    float scaleX = fbWidth / (float) screenWidth;
    float scaleY = fbHeight / (float) screenHeight;
    float framebufferX = x * scaleX;
    float framebufferY = y * scaleY;
    float framebufferWidth = width * scaleX;
    float framebufferHeight = height * scaleY;
    float framebufferRadius = cornerRadius * Math.max(scaleX, scaleY);
    int mainFramebuffer = getMainFramebufferId(framebuffer);
    if (mainFramebuffer == 0) {
      return;
    }

    int prevFramebuffer = 0;
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
      prevFramebuffer = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
      prevProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
      prevVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
      prevActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
      prevBoundTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
      prevBlendSrcRgb = GL11.glGetInteger(GL30.GL_BLEND_SRC_RGB);
      prevBlendDstRgb = GL11.glGetInteger(GL30.GL_BLEND_DST_RGB);
      prevBlendSrcAlpha = GL11.glGetInteger(GL30.GL_BLEND_SRC_ALPHA);
      prevBlendDstAlpha = GL11.glGetInteger(GL30.GL_BLEND_DST_ALPHA);
      prevBlendEquationRgb = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_RGB);
      prevBlendEquationAlpha = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_ALPHA);
      blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
      depthTestEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
      cullFaceEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);
      scissorEnabled = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
      depthMaskEnabled = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
      GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
      stateCaptured = true;

      sourceTarget.ensureSize(fbWidth, fbHeight);
      blurTempTarget.ensureSize(fbWidth, fbHeight);
      blurredTarget.ensureSize(fbWidth, fbHeight);
      GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, prevFramebuffer);

      GL11.glViewport(0, 0, fbWidth, fbHeight);
      copyFramebufferToTarget(mainFramebuffer, sourceTarget, fbWidth, fbHeight);
      int radius = Math.max(8, Math.min(56, Math.round(blurStrength * Math.max(scaleX, scaleY) * 2.6f)));
      ShaderRegistry.BLUR_A.render(sourceTarget, blurTempTarget, radius, 1.0f, 0.0f);
      ShaderRegistry.BLUR_B.render(blurTempTarget, blurredTarget, radius, 0.0f, 1.0f);
      GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mainFramebuffer);

      if (scissorEnabled) {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
      }
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      GL11.glDepthMask(false);
      GL11.glDisable(GL11.GL_CULL_FACE);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GL20.glBlendEquationSeparate(GL20.GL_FUNC_ADD, GL20.GL_FUNC_ADD);

      if (!blurShader.use()) {
        initialized = false;
        return;
      }

      blurShader.setTexture(0);
      GL13.glActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, blurredTarget.getTextureId());
      blurShader.setScreenSize(fbWidth, fbHeight);
      blurShader.setRect(framebufferX, framebufferY, framebufferWidth, framebufferHeight);
      blurShader.setCornerRadius(Math.max(0f, Math.min(framebufferRadius, Math.min(framebufferWidth, framebufferHeight) * 0.5f)));

      GL30.glBindVertexArray(quadVao);
      GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
      GL30.glBindVertexArray(0);
    } catch (Exception e) {
      System.err.println("[HudGlassBlurRenderer] Error during blur rendering: " + e.getMessage());
      e.printStackTrace();
    } finally {
      if (!stateCaptured) {
        return;
      }

      GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, prevFramebuffer);
      GL20.glUseProgram(prevProgram);
      GL30.glBindVertexArray(prevVao);
      GL13.glActiveTexture(prevActiveTexture);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, prevBoundTexture);
      GL30.glBlendFuncSeparate(prevBlendSrcRgb, prevBlendDstRgb, prevBlendSrcAlpha, prevBlendDstAlpha);
      GL20.glBlendEquationSeparate(prevBlendEquationRgb, prevBlendEquationAlpha);

      if (!blendEnabled) GL11.glDisable(GL11.GL_BLEND);
      if (depthTestEnabled) GL11.glEnable(GL11.GL_DEPTH_TEST);
      if (cullFaceEnabled) GL11.glEnable(GL11.GL_CULL_FACE);
      if (scissorEnabled) GL11.glEnable(GL11.GL_SCISSOR_TEST);
      GL11.glDepthMask(depthMaskEnabled);
      GL11.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
    }
  }

  public static void cleanup() {
    if (blurShader != null) {
      blurShader.cleanup();
      blurShader = null;
    }
    if (quadVao != 0) GL30.glDeleteVertexArrays(quadVao);
    if (quadVbo != 0) GL20.glDeleteBuffers(quadVbo);
    sourceTarget.cleanup();
    blurTempTarget.cleanup();
    blurredTarget.cleanup();
    quadVao = 0;
    quadVbo = 0;
    initialized = false;
  }

  private static void init() {
    try {
      blurShader = new HudGlassBlurShader();
      if (blurShader == null || !blurShader.isValid()) {
        blurShader = null;
        return;
      }
      createFullscreenQuad();
      initialized = true;
    } catch (Exception e) {
      System.err.println("[HudGlassBlurRenderer] Failed to initialize: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static void createFullscreenQuad() {
    float[] vertices = {
      -1.0f,  1.0f, 0.0f, 1.0f,
      -1.0f, -1.0f, 0.0f, 0.0f,
       1.0f, -1.0f, 1.0f, 0.0f,
      -1.0f,  1.0f, 0.0f, 1.0f,
       1.0f, -1.0f, 1.0f, 0.0f,
       1.0f,  1.0f, 1.0f, 1.0f
    };

    quadVao = GL30.glGenVertexArrays();
    quadVbo = GL20.glGenBuffers();

    GL30.glBindVertexArray(quadVao);
    GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, quadVbo);
    GL20.glBufferData(GL20.GL_ARRAY_BUFFER, vertices, GL20.GL_STATIC_DRAW);

    GL30.glEnableVertexAttribArray(0);
    GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 0);

    GL30.glEnableVertexAttribArray(1);
    GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 2L * Float.BYTES);

    GL30.glBindVertexArray(0);
  }

  private static int getMainFramebufferId(RenderTarget framebuffer) {
    try {
      if (!(framebuffer.getColorTexture() instanceof GlTexture texture)) {
        return 0;
      }
      return texture.getFbo(((GlDevice) RenderSystem.getDevice()).directStateAccess(), null);
    } catch (Exception exception) {
      System.err.println("[HudGlassBlurRenderer] Failed to resolve main framebuffer: " + exception.getMessage());
      return 0;
    }
  }

  private static void copyFramebufferToTarget(
    int sourceFramebuffer,
    org.cobalt.render.rise.RenderTarget target,
    int width,
    int height
  ) {
    if (target == null || !target.isReady()) {
      return;
    }

    int prevReadFramebuffer = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
    int prevDrawFramebuffer = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
    int prevReadBuffer = GL11.glGetInteger(GL11.GL_READ_BUFFER);
    int prevDrawBuffer = GL11.glGetInteger(GL11.GL_DRAW_BUFFER);

    try {
      GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, sourceFramebuffer);
      GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, target.getFboId());
      GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
      GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
      GL30.glBlitFramebuffer(
        0, 0, width, height,
        0, 0, width, height,
        GL11.GL_COLOR_BUFFER_BIT,
        GL11.GL_NEAREST
      );
    } finally {
      GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, prevReadFramebuffer);
      GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, prevDrawFramebuffer);
      GL11.glReadBuffer(prevReadBuffer);
      GL11.glDrawBuffer(prevDrawBuffer);
    }
  }
}
