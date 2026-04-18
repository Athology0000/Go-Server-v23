/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL15
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL30
 */
package org.cobalt.render.rise;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public final class FullscreenQuadRenderer {
    private static int vaoId;
    private static int vboId;
    private static boolean initialized;

    private FullscreenQuadRenderer() {
    }

    public static void draw() {
        FullscreenQuadRenderer.ensureInitialized();
        GL30.glBindVertexArray((int)vaoId);
        GL11.glDrawArrays((int)4, (int)0, (int)6);
        GL30.glBindVertexArray((int)0);
    }

    public static void cleanup() {
        if (vaoId != 0) {
            GL30.glDeleteVertexArrays((int)vaoId);
            vaoId = 0;
        }
        if (vboId != 0) {
            GL15.glDeleteBuffers((int)vboId);
            vboId = 0;
        }
        initialized = false;
    }

    private static void ensureInitialized() {
        if (initialized) {
            return;
        }
        float[] vertices = new float[]{-1.0f, 1.0f, 0.0f, 1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f};
        vaoId = GL30.glGenVertexArrays();
        vboId = GL15.glGenBuffers();
        GL30.glBindVertexArray((int)vaoId);
        GL15.glBindBuffer((int)34962, (int)vboId);
        GL15.glBufferData((int)34962, (float[])vertices, (int)35044);
        GL20.glEnableVertexAttribArray((int)0);
        GL20.glVertexAttribPointer((int)0, (int)2, (int)5126, (boolean)false, (int)16, (long)0L);
        GL20.glEnableVertexAttribArray((int)1);
        GL20.glVertexAttribPointer((int)1, (int)2, (int)5126, (boolean)false, (int)16, (long)8L);
        GL30.glBindVertexArray((int)0);
        initialized = true;
    }
}

