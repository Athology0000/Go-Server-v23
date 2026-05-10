package org.cobalt.render.rise;

import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;

public final class WorldGlowPass {

    private final WorldGlowRenderer renderer  = new WorldGlowRenderer();
    private final RenderTarget       maskTarget = new RenderTarget();
    private final RenderTarget       blurPing   = new RenderTarget();
    private final RenderTarget       blurPong   = new RenderTarget();
    private final BlurPass           blurH      = new BlurPass("/assets/minecraft/rise/shader/bloom.frag", 23, 24);
    private final BlurPass           blurV      = new BlurPass("/assets/minecraft/rise/shader/bloom.frag", 23, 24);
    private final BloomPass          bloom      = new BloomPass(blurH, blurV);

    public void addLine(float x1, float y1, float z1,
                        float x2, float y2, float z2,
                        float r,  float g,  float b,  float a) {
        renderer.addLine(x1, y1, z1, x2, y2, z2, r, g, b, a);
    }

    public boolean isEmpty() {
        return renderer.isEmpty();
    }

    public void flush(Matrix4f proj, Matrix4f modelView) {
        if (renderer.isEmpty()) return;

        int w = Minecraft.getInstance().getMainRenderTarget().width;
        int h = Minecraft.getInstance().getMainRenderTarget().height;
        blurPing.ensureSize(w, h);
        blurPong.ensureSize(w, h);

        Matrix4f projview = new Matrix4f(proj).mul(modelView);
        renderer.renderToTarget(maskTarget, projview);

        if (bloom.isValid()) {
            bloom.render(maskTarget, blurPing, blurPong, 10);
            ShaderRegistry.SCREEN_SPACE.renderTextureAdditive(blurPong.getTextureId(), 1.0f);
        }
        ShaderRegistry.SCREEN_SPACE.renderTextureAdditive(maskTarget.getTextureId(), 0.85f);
        renderer.begin();
    }

    public void cleanup() {
        blurH.cleanup();
        blurV.cleanup();
        maskTarget.cleanup();
        blurPing.cleanup();
        blurPong.cleanup();
    }
}
