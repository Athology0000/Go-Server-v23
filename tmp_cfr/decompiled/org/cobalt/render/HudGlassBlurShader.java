/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL30
 */
package org.cobalt.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class HudGlassBlurShader {
    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private boolean linked;
    private int uTexture;
    private int uScreenSize;
    private int uRect;
    private int uCornerRadius;
    private int uBlurStrength;
    private static final String VERTEX_SHADER = "#version 150\n\nin vec2 position;\nin vec2 texCoord;\n\nout vec2 fragTexCoord;\n\nvoid main() {\n    gl_Position = vec4(position, 0.0, 1.0);\n    fragTexCoord = texCoord;\n}\n";
    private static final String FRAGMENT_SHADER = "#version 150\n\nin vec2 fragTexCoord;\n\nuniform sampler2D uTexture;\nuniform vec2 uScreenSize;\nuniform vec4 uRect;\nuniform float uCornerRadius;\nuniform float uBlurStrength;\n\nout vec4 fragColor;\n\nfloat roundedBoxSdf(vec2 p, vec2 halfSize, float radius) {\n    vec2 q = abs(p) - halfSize + vec2(radius);\n    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - radius;\n}\n\nvec3 sampleGaussianBlur(vec2 uv, vec2 texel) {\n    float sigma = 2.15;\n    vec3 accum = vec3(0.0);\n    float total = 0.0;\n\n    for (int x = -3; x <= 3; x++) {\n        for (int y = -3; y <= 3; y++) {\n            float dist2 = float(x * x + y * y);\n            float weight = exp(-dist2 / (2.0 * sigma * sigma));\n            vec2 offset = vec2(float(x), float(y)) * texel * uBlurStrength;\n            accum += texture(uTexture, uv + offset).rgb * weight;\n            total += weight;\n        }\n    }\n\n    return accum / max(total, 0.0001);\n}\n\nvoid main() {\n    vec2 pixel = vec2(\n        fragTexCoord.x * uScreenSize.x,\n        (1.0 - fragTexCoord.y) * uScreenSize.y\n    );\n\n    vec2 rectCenter = uRect.xy + uRect.zw * 0.5;\n    vec2 halfSize = uRect.zw * 0.5;\n    float sdf = roundedBoxSdf(pixel - rectCenter, halfSize, uCornerRadius);\n    float mask = 1.0 - smoothstep(0.0, 1.65, sdf);\n    if (mask <= 0.001) {\n        discard;\n    }\n\n    vec2 texel = vec2(1.0 / uScreenSize.x, 1.0 / uScreenSize.y);\n    vec3 blurred = sampleGaussianBlur(fragTexCoord, texel);\n    fragColor = vec4(blurred, mask);\n}\n";

    public HudGlassBlurShader() {
        this.compile();
    }

    private void compile() {
        this.cleanup();
        try {
            this.programId = GL20.glCreateProgram();
            this.vertexShaderId = GL20.glCreateShader((int)35633);
            GL20.glShaderSource((int)this.vertexShaderId, (CharSequence)VERTEX_SHADER);
            GL20.glCompileShader((int)this.vertexShaderId);
            if (GL20.glGetShaderi((int)this.vertexShaderId, (int)35713) == 0) {
                System.err.println("[HudGlassBlurShader] Vertex shader compilation failed:");
                System.err.println(GL20.glGetShaderInfoLog((int)this.vertexShaderId));
                this.cleanup();
                return;
            }
            this.fragmentShaderId = GL20.glCreateShader((int)35632);
            GL20.glShaderSource((int)this.fragmentShaderId, (CharSequence)FRAGMENT_SHADER);
            GL20.glCompileShader((int)this.fragmentShaderId);
            if (GL20.glGetShaderi((int)this.fragmentShaderId, (int)35713) == 0) {
                System.err.println("[HudGlassBlurShader] Fragment shader compilation failed:");
                System.err.println(GL20.glGetShaderInfoLog((int)this.fragmentShaderId));
                this.cleanup();
                return;
            }
            GL20.glAttachShader((int)this.programId, (int)this.vertexShaderId);
            GL20.glAttachShader((int)this.programId, (int)this.fragmentShaderId);
            GL20.glBindAttribLocation((int)this.programId, (int)0, (CharSequence)"position");
            GL20.glBindAttribLocation((int)this.programId, (int)1, (CharSequence)"texCoord");
            GL30.glBindFragDataLocation((int)this.programId, (int)0, (CharSequence)"fragColor");
            GL20.glLinkProgram((int)this.programId);
            if (GL20.glGetProgrami((int)this.programId, (int)35714) == 0) {
                System.err.println("[HudGlassBlurShader] Program linking failed:");
                System.err.println(GL20.glGetProgramInfoLog((int)this.programId));
                this.cleanup();
                return;
            }
            this.uTexture = GL20.glGetUniformLocation((int)this.programId, (CharSequence)"uTexture");
            this.uScreenSize = GL20.glGetUniformLocation((int)this.programId, (CharSequence)"uScreenSize");
            this.uRect = GL20.glGetUniformLocation((int)this.programId, (CharSequence)"uRect");
            this.uCornerRadius = GL20.glGetUniformLocation((int)this.programId, (CharSequence)"uCornerRadius");
            this.uBlurStrength = GL20.glGetUniformLocation((int)this.programId, (CharSequence)"uBlurStrength");
            this.linked = true;
        }
        catch (Exception e) {
            System.err.println("[HudGlassBlurShader] Failed to compile shader: " + e.getMessage());
            e.printStackTrace();
            this.cleanup();
        }
    }

    public boolean use() {
        if (!this.isValid()) {
            return false;
        }
        GL20.glUseProgram((int)this.programId);
        return GL11.glGetInteger((int)35725) == this.programId;
    }

    public void setTexture(int textureUnit) {
        GL20.glUniform1i((int)this.uTexture, (int)textureUnit);
    }

    public void setScreenSize(float width, float height) {
        GL20.glUniform2f((int)this.uScreenSize, (float)width, (float)height);
    }

    public void setRect(float x, float y, float width, float height) {
        GL20.glUniform4f((int)this.uRect, (float)x, (float)y, (float)width, (float)height);
    }

    public void setCornerRadius(float radius) {
        GL20.glUniform1f((int)this.uCornerRadius, (float)radius);
    }

    public void setBlurStrength(float blurStrength) {
        GL20.glUniform1f((int)this.uBlurStrength, (float)blurStrength);
    }

    public boolean isValid() {
        return this.linked && this.programId != 0;
    }

    public void cleanup() {
        if (this.vertexShaderId != 0) {
            GL20.glDeleteShader((int)this.vertexShaderId);
        }
        if (this.fragmentShaderId != 0) {
            GL20.glDeleteShader((int)this.fragmentShaderId);
        }
        if (this.programId != 0) {
            GL20.glDeleteProgram((int)this.programId);
        }
        this.vertexShaderId = 0;
        this.fragmentShaderId = 0;
        this.programId = 0;
        this.linked = false;
    }
}

