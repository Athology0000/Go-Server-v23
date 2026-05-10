#version 150

uniform sampler2D SceneSampler;
uniform sampler2D MaskSampler;

uniform vec2 TexelSize;

uniform vec4 OutlineColor;
uniform vec4 GlowColor;

uniform float OutlineThickness;
uniform float GlowRadius;
uniform float GlowStrength;

in vec2 texCoord;
out vec4 fragColor;

float maskAt(vec2 uv) {
    return texture(MaskSampler, uv).a;
}

float sampleRing(vec2 uv, float radius) {
    vec2 t = TexelSize * radius;

    float s = 0.0;
    s += maskAt(uv + vec2( t.x, 0.0));
    s += maskAt(uv + vec2(-t.x, 0.0));
    s += maskAt(uv + vec2(0.0,  t.y));
    s += maskAt(uv + vec2(0.0, -t.y));

    s += maskAt(uv + vec2( t.x,  t.y));
    s += maskAt(uv + vec2(-t.x,  t.y));
    s += maskAt(uv + vec2( t.x, -t.y));
    s += maskAt(uv + vec2(-t.x, -t.y));

    return s / 8.0;
}

void main() {
    vec4 scene = texture(SceneSampler, texCoord);
    float center = maskAt(texCoord);

    float nearMask = sampleRing(texCoord, OutlineThickness);
    float farMask = sampleRing(texCoord, GlowRadius);

    float outline = 0.0;
    float glow = 0.0;

    if (center < 0.01) {
        outline = smoothstep(0.05, 0.35, nearMask);
        glow = smoothstep(0.02, 0.25, farMask) * GlowStrength;
    }

    vec4 result = scene;
    result.rgb = mix(result.rgb, GlowColor.rgb, glow * GlowColor.a);
    result.rgb = mix(result.rgb, OutlineColor.rgb, outline * OutlineColor.a);

    fragColor = vec4(result.rgb, scene.a);
}