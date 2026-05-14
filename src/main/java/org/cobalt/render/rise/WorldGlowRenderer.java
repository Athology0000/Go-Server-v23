package org.cobalt.render.rise;

import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;

public final class WorldGlowRenderer {

    private static final int FLOATS_PER_VERTEX = 7; // xyz rgba
    private static final int MAX_VERTICES       = 8192;
    private static final int STRIDE             = FLOATS_PER_VERTEX * Float.BYTES;

    private final int          program;
    private final int          uProjview;
    private final int          vao;
    private final int          vbo;
    private final FloatBuffer  vertexBuf = BufferUtils.createFloatBuffer(MAX_VERTICES * FLOATS_PER_VERTEX);
    private final float[]      matBuf    = new float[16];
    private       int          vertexCount;
    private       boolean      valid;

    public WorldGlowRenderer() {
        int vert = -1, frag = -1, prog = -1;
        int glVao = 0, glVbo = 0;
        int uLoc = -1;
        boolean ok = false;

        try {
            vert = compileShader(GL20.GL_VERTEX_SHADER,   VERT_SRC);
            frag = compileShader(GL20.GL_FRAGMENT_SHADER, FRAG_SRC);
            prog = GL20.glCreateProgram();
            GL20.glAttachShader(prog, vert);
            GL20.glAttachShader(prog, frag);
            GL20.glLinkProgram(prog);
            if (GL20.glGetProgrami(prog, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
                throw new RuntimeException("program link failed: " + GL20.glGetProgramInfoLog(prog));
            }
            uLoc  = GL20.glGetUniformLocation(prog, "u_projview");

            glVao = GL30.glGenVertexArrays();
            glVbo = GL15.glGenBuffers();

            GL30.glBindVertexArray(glVao);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glVbo);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (long) MAX_VERTICES * STRIDE, GL15.GL_DYNAMIC_DRAW);
            GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, STRIDE, 0L);
            GL20.glEnableVertexAttribArray(0);
            GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, STRIDE, 3L * Float.BYTES);
            GL20.glEnableVertexAttribArray(1);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            GL30.glBindVertexArray(0);
            ok = true;
        } catch (Exception e) {
            ok = false;
            Minecraft.getInstance().execute(() ->
                System.err.println("[WorldGlowRenderer] init failed: " + e.getMessage()));
            if (prog > 0) {
                GL20.glDeleteProgram(prog);
                prog = -1;
            }
            if (glVao != 0) {
                GL30.glDeleteVertexArrays(glVao);
                glVao = 0;
            }
            if (glVbo != 0) {
                GL15.glDeleteBuffers(glVbo);
                glVbo = 0;
            }
        } finally {
            if (vert > 0) GL20.glDeleteShader(vert);
            if (frag > 0) GL20.glDeleteShader(frag);
        }

        program   = prog;
        uProjview = uLoc;
        vao       = glVao;
        vbo       = glVbo;
        valid     = ok;
    }

    public void begin() {
        vertexBuf.clear();
        vertexCount = 0;
    }

    public void addLine(float x1, float y1, float z1,
                        float x2, float y2, float z2,
                        float r,  float g,  float b,  float a) {
        if (!valid || vertexCount + 2 > MAX_VERTICES) return;
        vertexBuf.put(x1).put(y1).put(z1).put(r).put(g).put(b).put(a);
        vertexBuf.put(x2).put(y2).put(z2).put(r).put(g).put(b).put(a);
        vertexCount += 2;
    }

    public boolean isEmpty() { return vertexCount == 0; }

    public void renderToTarget(RenderTarget target, Matrix4f projview) {
        if (!valid || vertexCount == 0) return;

        var mc = Minecraft.getInstance();
        int w = mc.getMainRenderTarget().width;
        int h = mc.getMainRenderTarget().height;
        target.ensureSize(w, h);
        if (!target.isReady()) return;

        // save state
        int prevFbo   = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        int prevProg  = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        int prevVao   = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        int prevVbo   = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
        int prevBlendSrcRgb   = GL11.glGetInteger(GL30.GL_BLEND_SRC_RGB);
        int prevBlendDstRgb   = GL11.glGetInteger(GL30.GL_BLEND_DST_RGB);
        int prevBlendSrcAlpha = GL11.glGetInteger(GL30.GL_BLEND_SRC_ALPHA);
        int prevBlendDstAlpha = GL11.glGetInteger(GL30.GL_BLEND_DST_ALPHA);
        boolean depthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        boolean blend     = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean cull      = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);

        try {
            target.clear(0f, 0f, 0f, 0f);

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

            GL20.glUseProgram(program);
            projview.get(matBuf);
            GL20.glUniformMatrix4fv(uProjview, false, matBuf);

            GL30.glBindVertexArray(vao);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
            vertexBuf.flip();
            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0L, vertexBuf);

            GL11.glDrawArrays(GL11.GL_LINES, 0, vertexCount);
        } finally {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, prevFbo);
            GL11.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
            GL20.glUseProgram(prevProg);
            GL30.glBindVertexArray(prevVao);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, prevVbo);
            GL30.glBlendFuncSeparate(prevBlendSrcRgb, prevBlendDstRgb, prevBlendSrcAlpha, prevBlendDstAlpha);
            if (depthTest) GL11.glEnable(GL11.GL_DEPTH_TEST); else GL11.glDisable(GL11.GL_DEPTH_TEST);
            if (!blend)    GL11.glDisable(GL11.GL_BLEND);
            if (cull)      GL11.glEnable(GL11.GL_CULL_FACE); else GL11.glDisable(GL11.GL_CULL_FACE);
        }
    }

    private static int compileShader(int type, String src) {
        int id = GL20.glCreateShader(type);
        GL20.glShaderSource(id, src);
        GL20.glCompileShader(id);
        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(id);
            GL20.glDeleteShader(id);
            throw new RuntimeException("shader compile failed (type=" + type + "): " + log);
        }
        return id;
    }

    // ----- shaders -------------------------------------------------------

    private static final String VERT_SRC =
        "#version 330 core\n" +
        "layout(location = 0) in vec3 a_pos;\n" +
        "layout(location = 1) in vec4 a_color;\n" +
        "uniform mat4 u_projview;\n" +
        "out vec4 v_color;\n" +
        "void main() {\n" +
        "    gl_Position = u_projview * vec4(a_pos, 1.0);\n" +
        "    v_color = a_color;\n" +
        "}\n";

    private static final String FRAG_SRC =
        "#version 330 core\n" +
        "in vec4 v_color;\n" +
        "out vec4 fragColor;\n" +
        "void main() {\n" +
        "    fragColor = v_color;\n" +
        "}\n";
}
