#version 150

in vec2 fragTexCoord;
uniform vec2 u_size;
uniform float u_radius;
uniform vec4 u_first_color;
uniform vec4 u_second_color;
uniform float u_time;
uniform int u_direction;
uniform vec4 u_edges;
out vec4 fragColor;

float gradientAxis(vec2 uv) {
    if (u_direction == 1) {
        return uv.y;
    }
    if (u_direction == 2) {
        return clamp((uv.x + uv.y) * 0.5, 0.0, 1.0);
    }
    return uv.x;
}

void main() {
    vec2 tex_coord = fragTexCoord;
    float axis = gradientAxis(tex_coord);
    float phase = fract(u_time * 0.18);
    float wave = abs(fract(axis + phase) * 2.0 - 1.0);

    vec4 color = mix(u_first_color, u_second_color, smoothstep(0.0, 1.0, wave));

    if (tex_coord.x < 0.5 && tex_coord.y > 0.5 && u_edges.x == 0.0 ||
        tex_coord.x > 0.5 && tex_coord.y > 0.5 && u_edges.y == 0.0 ||
        tex_coord.x > 0.5 && tex_coord.y < 0.5 && u_edges.z == 0.0 ||
        tex_coord.x < 0.5 && tex_coord.y < 0.5 && u_edges.w == 0.0) {
        fragColor = color;
    } else {
        fragColor = vec4(color.rgb, color.a * smoothstep(1.0, 0.0, length(max((abs(tex_coord - 0.5) + 0.5) * u_size - u_size + u_radius, 0.0)) - u_radius + 0.5));
    }
}
