#version 150

in vec2 fragTexCoord;
uniform vec2 u_size;
uniform float u_radius;
uniform float u_border_size;
uniform vec4 u_color_1;
uniform vec4 u_color_2;
uniform int u_direction;
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

void main(void)
{
    float a = gradientAxis(fragTexCoord);
    float b = abs(1. - a * 2.);
    vec4 color = mix(u_color_1, u_color_2, b);

    vec2 position = (abs(fragTexCoord - 0.5) + 0.5) * u_size;
    float distance = length(max(position - u_size + u_radius + u_border_size, 0.0)) - u_radius + 0.5;
    fragColor = vec4(color.rgb, color.a * (smoothstep(0.0, 1.0, distance) - smoothstep(0.0, 1.0, distance - u_border_size)));
}
