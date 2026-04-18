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

public class DarkTintShader {
    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private boolean linked;
    private int uTexture;
    private int uTintColor;
    private int uIntensity;
    private int uBlendMode;
    private int uVignetteStrength;
    private int uSaturation;
    private int uContrast;
    private int uChromaticAberration;
    private int uBrightness;
    private int uDepthTexture;
    private int uDepthThreshold;
    private int uExcludeViewmodel;
    private static final String VERTEX_SHADER = "#version 150\n\nin vec2 position;\nin vec2 texCoord;\n\nout vec2 fragTexCoord;\n\nvoid main() {\n    gl_Position = vec4(position, 0.0, 1.0);\n    fragTexCoord = texCoord;\n}\n";
    private static final String FRAGMENT_SHADER = "#version 150\n\nin vec2 fragTexCoord;\n\nuniform sampler2D uTexture;\nuniform vec4 uTintColor;\nuniform float uIntensity;\nuniform int uBlendMode;\nuniform float uVignetteStrength;\nuniform float uSaturation;\nuniform float uContrast;\nuniform float uChromaticAberration;\nuniform float uBrightness;\n\nuniform sampler2D uDepthTexture;\nuniform float uDepthThreshold;\nuniform int uExcludeViewmodel;\n\nout vec4 fragColor;\n\nfloat getLuminance(vec3 color) {\n    return dot(color, vec3(0.299, 0.587, 0.114));\n}\n\nvec3 applySaturation(vec3 color, float sat) {\n    float lum = getLuminance(color);\n    return mix(vec3(lum), color, sat);\n}\n\nvec3 applyContrast(vec3 color, float contrast) {\n    return (color - 0.5) * contrast + 0.5;\n}\n\nfloat getVignette(vec2 uv, float strength) {\n    vec2 center = uv - 0.5;\n    float dist = length(center);\n    return 1.0 - smoothstep(0.3, 1.0, dist) * strength;\n}\n\nvoid main() {\n    vec2 uv = fragTexCoord;\n\n    if (uExcludeViewmodel == 1) {\n        float depth = texture(uDepthTexture, uv).r;\n        if (depth < uDepthThreshold) {\n            fragColor = texture(uTexture, uv);\n            return;\n        }\n    }\n\n    vec3 worldColor;\n    if (uChromaticAberration > 0.001) {\n        vec2 dir = (uv - 0.5);\n        float dist = length(dir);\n        vec2 offset = normalize(dir) * dist * uChromaticAberration;\n        worldColor.r = texture(uTexture, uv + offset * 0.5).r;\n        worldColor.g = texture(uTexture, uv).g;\n        worldColor.b = texture(uTexture, uv - offset * 0.5).b;\n    } else {\n        worldColor = texture(uTexture, uv).rgb;\n    }\n\n    worldColor = applySaturation(worldColor, uSaturation);\n    worldColor = applyContrast(worldColor, uContrast);\n\n    vec3 result;\n    if (uBlendMode == 0) {\n        vec3 tint = mix(vec3(1.0), uTintColor.rgb, uIntensity);\n        result = worldColor * tint;\n    } else if (uBlendMode == 1) {\n        vec3 tint = uTintColor.rgb;\n        vec3 base = worldColor;\n        vec3 overlayed;\n        for (int i = 0; i < 3; i++) {\n            if (base[i] < 0.5) {\n                overlayed[i] = 2.0 * base[i] * tint[i];\n            } else {\n                overlayed[i] = 1.0 - 2.0 * (1.0 - base[i]) * (1.0 - tint[i]);\n            }\n        }\n        result = mix(base, overlayed, uIntensity);\n    } else if (uBlendMode == 2) {\n        result = worldColor + uTintColor.rgb * uIntensity;\n    } else {\n        vec3 inverted = vec3(1.0) - (vec3(1.0) - worldColor) * (vec3(1.0) - uTintColor.rgb);\n        result = mix(worldColor, inverted, uIntensity);\n    }\n\n    float vignette = getVignette(uv, uVignetteStrength);\n    result *= vignette;\n    result *= uBrightness;\n\n    fragColor = vec4(result, 1.0);\n}\n";

    public DarkTintShader() {
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
                System.err.println("[DarkTintShader] Vertex shader compilation failed:");
                System.err.println(GL20.glGetShaderInfoLog((int)this.vertexShaderId));
                this.cleanup();
                return;
            }
            this.fragmentShaderId = GL20.glCreateShader((int)35632);
            GL20.glShaderSource((int)this.fragmentShaderId, (CharSequence)FRAGMENT_SHADER);
            GL20.glCompileShader((int)this.fragmentShaderId);
            if (GL20.glGetShaderi((int)this.fragmentShaderId, (int)35713) == 0) {
                System.err.println("[DarkTintShader] Fragment shader compilation failed:");
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
                System.err.println("[DarkTintShader] Program linking failed:");
                System.err.println(GL20.glGetProgramInfoLog((int)this.programId));
                this.cleanup();
                return;
            }
            this.uTexture = GL20.glGetUniformLocation((int)this.programId, (CharSequence)"uTexture");
            this.uTintColor = GL20.glGetUniformLocation((int)this.programId, (CharSequence)"uTintColor");
            this.uIntensity = GL20.glGetUniformLocation((int)this.programId, (CharSequence)"uIntensity");
            this.uBlendMode = GL20.glGetUniformLocation((int)this.programId, (CharSequence)"uBlendMode");
            this.uVignetteStrength = GL20.glGetUniformLocation((int)this.programId, (CharSequence)"uVignetteStrength");
            this.uSaturation = GL20.glGetUniformLocation((int)this.programId, (CharSequence)"uSaturation");
            this.uContrast = GL20.glGetUniformLocation((int)this.programId, (CharSequence)"uContrast");
            this.uChromaticAberration = GL20.glGetUniformLocation((int)this.programId, (CharSequence)"uChromaticAberration");
            this.uBrightness = GL20.glGetUniformLocation((int)this.programId, (CharSequence)"uBrightness");
            this.uDepthTexture = GL20.glGetUniformLocation((int)this.programId, (CharSequence)"uDepthTexture");
            this.uDepthThreshold = GL20.glGetUniformLocation((int)this.programId, (CharSequence)"uDepthThreshold");
            this.uExcludeViewmodel = GL20.glGetUniformLocation((int)this.programId, (CharSequence)"uExcludeViewmodel");
            this.linked = true;
            System.out.println("[DarkTintShader] Dark tint shader compiled successfully!");
        }
        catch (Exception e) {
            System.err.println("[DarkTintShader] Failed to compile shader: " + e.getMessage());
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

    public void setTintColor(float r, float g, float b, float a) {
        GL20.glUniform4f((int)this.uTintColor, (float)r, (float)g, (float)b, (float)a);
    }

    public void setIntensity(float intensity) {
        GL20.glUniform1f((int)this.uIntensity, (float)intensity);
    }

    public void setBlendMode(int mode) {
        GL20.glUniform1i((int)this.uBlendMode, (int)mode);
    }

    public void setVignetteStrength(float strength) {
        GL20.glUniform1f((int)this.uVignetteStrength, (float)strength);
    }

    public void setSaturation(float saturation) {
        GL20.glUniform1f((int)this.uSaturation, (float)saturation);
    }

    public void setContrast(float contrast) {
        GL20.glUniform1f((int)this.uContrast, (float)contrast);
    }

    public void setChromaticAberration(float amount) {
        GL20.glUniform1f((int)this.uChromaticAberration, (float)amount);
    }

    public void setBrightness(float brightness) {
        GL20.glUniform1f((int)this.uBrightness, (float)brightness);
    }

    public void setDepthTexture(int textureUnit) {
        GL20.glUniform1i((int)this.uDepthTexture, (int)textureUnit);
    }

    public void setDepthThreshold(float threshold) {
        GL20.glUniform1f((int)this.uDepthThreshold, (float)threshold);
    }

    public void setExcludeViewmodel(boolean exclude) {
        GL20.glUniform1i((int)this.uExcludeViewmodel, (int)(exclude ? 1 : 0));
    }

    public void unbind() {
        GL20.glUseProgram((int)0);
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

