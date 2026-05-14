#version 150

in vec2 fragTexCoord;

uniform vec2 u_size;
uniform float u_radius;
uniform vec4 u_base_color;
uniform vec4 u_glow_color;
uniform vec4 u_shine_color;
uniform float u_alpha;
uniform float u_intensity;
uniform float u_time;

out vec4 fragColor;

float roundedMask(vec2 uv) {
    vec2 local = uv * u_size;
    vec2 halfSize = u_size * 0.5;
    vec2 d = abs(local - halfSize) - halfSize + vec2(u_radius);
    return smoothstep(1.0, 0.0, length(max(d, 0.0)) - u_radius + 0.5);
}

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

void main() {
    vec2 uv = fragTexCoord;
    float mask = roundedMask(uv);
    if (mask <= 0.001) {
        discard;
    }

    float vertical = 1.0 - uv.y;
    float diagonal = clamp((uv.x * 0.72 + vertical * 0.55), 0.0, 1.0);
    float pulse = 0.5 + 0.5 * sin(u_time * 1.25 + uv.x * 4.2 + uv.y * 2.4);
    float wave = smoothstep(0.18, 1.0, diagonal) * (0.72 + pulse * 0.28);

    vec3 base = u_base_color.rgb;
    vec3 glow = u_glow_color.rgb;
    vec3 shine = u_shine_color.rgb;

    vec3 color = mix(base, glow, wave * 0.46 * u_intensity);
    color += shine * smoothstep(0.90, 0.18, uv.y) * 0.12 * u_intensity;

    float edgeDistance = min(min(uv.x, 1.0 - uv.x) * u_size.x, min(uv.y, 1.0 - uv.y) * u_size.y);
    float rim = 1.0 - smoothstep(0.0, max(1.0, 2.6 + u_intensity), edgeDistance);
    color = mix(color, shine, rim * 0.42 * u_intensity);

    float scan = sin((uv.y * u_size.y + u_time * 18.0) * 0.52) * 0.5 + 0.5;
    float grain = hash(floor(uv * u_size) + floor(u_time * 24.0)) - 0.5;
    color += (scan * 0.025 + grain * 0.018) * u_intensity;

    float alpha = (u_base_color.a + rim * u_glow_color.a * 0.32 + wave * 0.10) * u_alpha * mask;
    fragColor = vec4(color, alpha);
}
