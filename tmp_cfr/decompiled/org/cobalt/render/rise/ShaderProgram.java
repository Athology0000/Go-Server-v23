/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL20
 */
package org.cobalt.render.rise;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public final class ShaderProgram {
    private final String label;
    private final int programId;
    private final Map<String, Integer> uniformLocations = new HashMap<String, Integer>();
    private boolean cleanedUp = false;
    private boolean bindFailureLogged = false;

    private ShaderProgram(String label, int programId) {
        this.label = label;
        this.programId = programId;
    }

    public static ShaderProgram fromResources(String vertexPath, String fragmentPath) {
        try {
            String vertexSource = ShaderProgram.readResource(vertexPath);
            String fragmentSource = ShaderProgram.readResource(fragmentPath);
            return new ShaderProgram(fragmentPath, ShaderProgram.compile(vertexSource, fragmentSource, vertexPath, fragmentPath));
        }
        catch (Exception exception) {
            System.out.println("[ShaderProgram] Failed to load resources " + vertexPath + " and " + fragmentPath);
            exception.printStackTrace();
            return new ShaderProgram(fragmentPath, 0);
        }
    }

    private static String readResource(String path) throws IOException {
        try (InputStream inputStream = ShaderProgram.class.getResourceAsStream(path);){
            if (inputStream == null) {
                throw new IOException("Shader resource not found: " + path);
            }
            String source = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String string = ShaderProgram.adaptRiseShaderSource(path, source);
            return string;
        }
    }

    private static String adaptRiseShaderSource(String path, String source) {
        if (!path.startsWith("/assets/minecraft/rise/shader/")) {
            return source;
        }
        String adapted = source.replace("\r\n", "\n");
        adapted = adapted.replaceAll("(?s)#ifdef GL_ES\\s*precision mediump float;\\s*#endif\\s*", "");
        adapted = adapted.replaceAll("(?m)^#extension\\s+GL_OES_standard_derivatives\\s*:\\s*enable\\s*$\\n?", "");
        adapted = adapted.replaceAll("(?m)^#version\\s+\\d+\\s*$\\n?", "");
        adapted = adapted.replace("gl_TexCoord[0].st", "fragTexCoord");
        adapted = adapted.replace("gl_TexCoord[0].s", "fragTexCoord.x");
        adapted = adapted.replace("gl_TexCoord[0].t", "fragTexCoord.y");
        adapted = adapted.replace("texture2D(", "texture(");
        adapted = adapted.replace("gl_FragColor", "fragColor");
        adapted = adapted.replace("u_direction > 0.0", "u_direction > 0");
        adapted = adapted.replace("dot(c,12.9898)", "(c * 12.9898)");
        adapted = adapted.replace("dot(c, 12.9898)", "(c * 12.9898)");
        StringBuilder builder = new StringBuilder("#version 150\n\n");
        if (!path.endsWith("/main_menu/background.frag")) {
            builder.append("in vec2 fragTexCoord;\n");
        }
        builder.append("out vec4 fragColor;\n\n");
        builder.append(adapted.stripLeading());
        return builder.toString();
    }

    private static int compile(String vertexSource, String fragmentSource, String vertexName, String fragmentName) {
        int vertexShader = ShaderProgram.compileShader(35633, vertexSource, vertexName);
        if (vertexShader == 0) {
            return 0;
        }
        int fragmentShader = ShaderProgram.compileShader(35632, fragmentSource, fragmentName);
        if (fragmentShader == 0) {
            GL20.glDeleteShader((int)vertexShader);
            return 0;
        }
        int program = GL20.glCreateProgram();
        GL20.glAttachShader((int)program, (int)vertexShader);
        GL20.glAttachShader((int)program, (int)fragmentShader);
        GL20.glBindAttribLocation((int)program, (int)0, (CharSequence)"position");
        GL20.glBindAttribLocation((int)program, (int)1, (CharSequence)"texCoord");
        GL20.glLinkProgram((int)program);
        GL20.glDeleteShader((int)vertexShader);
        GL20.glDeleteShader((int)fragmentShader);
        if (GL20.glGetProgrami((int)program, (int)35714) == 0) {
            System.out.println("[ShaderProgram] Program link failed for " + fragmentName + ":");
            System.out.println(GL20.glGetProgramInfoLog((int)program));
            GL20.glDeleteProgram((int)program);
            return 0;
        }
        return program;
    }

    private static int compileShader(int shaderType, String source, String name) {
        int shader = GL20.glCreateShader((int)shaderType);
        GL20.glShaderSource((int)shader, (CharSequence)source);
        GL20.glCompileShader((int)shader);
        if (GL20.glGetShaderi((int)shader, (int)35713) == 0) {
            System.out.println("[ShaderProgram] Shader compile failed for " + name + ":");
            System.out.println(GL20.glGetShaderInfoLog((int)shader));
            GL20.glDeleteShader((int)shader);
            return 0;
        }
        return shader;
    }

    public boolean isValid() {
        return this.programId != 0;
    }

    public void bind() {
        if (this.programId != 0) {
            GL20.glUseProgram((int)this.programId);
            if (!this.isCurrentProgram() && !this.bindFailureLogged) {
                this.bindFailureLogged = true;
                System.out.println("[ShaderProgram] Failed to bind " + this.label + " (program " + this.programId + ")");
            }
        }
    }

    public void cleanup() {
        if (!this.cleanedUp && this.programId != 0) {
            GL20.glDeleteProgram((int)this.programId);
            this.cleanedUp = true;
        }
    }

    public int uniform(String name) {
        if (!this.isCurrentProgram()) {
            return -1;
        }
        Integer cached = this.uniformLocations.get(name);
        if (cached != null) {
            return cached;
        }
        int location = GL20.glGetUniformLocation((int)this.programId, (CharSequence)name);
        this.uniformLocations.put(name, location);
        return location;
    }

    public void set1i(String name, int value) {
        int location = this.uniform(name);
        if (location >= 0) {
            GL20.glUniform1i((int)location, (int)value);
        }
    }

    public void set1f(String name, float value) {
        int location = this.uniform(name);
        if (location >= 0) {
            GL20.glUniform1f((int)location, (float)value);
        }
    }

    public void set2f(String name, float x, float y) {
        int location = this.uniform(name);
        if (location >= 0) {
            GL20.glUniform2f((int)location, (float)x, (float)y);
        }
    }

    public void set3f(String name, float x, float y, float z) {
        int location = this.uniform(name);
        if (location >= 0) {
            GL20.glUniform3f((int)location, (float)x, (float)y, (float)z);
        }
    }

    public void set4f(String name, float x, float y, float z, float w) {
        int location = this.uniform(name);
        if (location >= 0) {
            GL20.glUniform4f((int)location, (float)x, (float)y, (float)z, (float)w);
        }
    }

    public void setFloatBuffer(String name, FloatBuffer buffer) {
        int location = this.uniform(name);
        if (location >= 0) {
            buffer.rewind();
            GL20.glUniform1fv((int)location, (FloatBuffer)buffer);
        }
    }

    private boolean isCurrentProgram() {
        return this.programId != 0 && GL11.glGetInteger((int)35725) == this.programId;
    }
}

