#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_time;
uniform vec2 u_resolution;
uniform vec2 u_scroll;        // Camera position for parallax
uniform vec3 u_tint;          // Color grading (RGB multiplier)
uniform float u_intensity;    // Overall opacity control (0.0–1.0)
uniform float u_noiseScale;   // Noise frequency multiplier (default 1.0)
uniform float u_scrollSpeed;  // Scroll speed multiplier (default 1.0)
uniform float u_contrast;     // Contrast enhancement (default 1.0)
uniform vec2 u_lightDir;      // Pseudo-light direction for fake depth

// Pseudo-random
float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

// Value Noise
float noise(vec2 st) {
    vec2 i = floor(st);
    vec2 f = fract(st);
    float a = random(i);
    float b = random(i + vec2(1.0, 0.0));
    float c = random(i + vec2(0.0, 1.0));
    float d = random(i + vec2(1.0, 1.0));
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

// FBM
float fbm(vec2 st) {
    float value = 0.0;
    float amplitude = 0.5;
    float frequency = 1.0;
    for (int i = 0; i < 4; i++) {
        value += amplitude * noise(st * frequency);
        frequency *= 2.0;
        amplitude *= 0.5;
    }
    return value;
}

void main() {
    // Dummy usage of u_texture to prevent optimization (SpriteBatch requires it)
    vec4 dummy = texture2D(u_texture, v_texCoords);

    // Normalize coords
    vec2 st = gl_FragCoord.xy / u_resolution.xy;

    // Aspect ratio correction
    st.x *= u_resolution.x / u_resolution.y;

    // Scroll with configurable speed + gentle UV drift
    float scrollMult = u_scrollSpeed * 0.0025;
    vec2 pos = st + u_scroll * scrollMult + vec2(u_time * 0.002, u_time * 0.001);

    // Apply pseudo-light direction offset for depth illusion
    pos += u_lightDir * 0.1;

    // Time animation
    float t = u_time * 0.1;

    // Generate noise layers with configurable scale
    float scale = u_noiseScale;
    float n1 = fbm(pos * 3.0 * scale + vec2(t * 0.2, t * 0.1));
    float n2 = fbm(pos * 6.0 * scale - vec2(t * 0.3, t * 0.2));

    // Combine noise
    float n = mix(n1, n2, 0.5);

    // Apply contrast enhancement
    n = pow(n, u_contrast);
    float n1c = pow(n1, u_contrast);

    // Color Palette (Cold/Space)
    vec3 colorBg = vec3(0.02, 0.02, 0.05);     // Deep dark blue/black
    vec3 colorNebula1 = vec3(0.1, 0.2, 0.4);   // Blue
    vec3 colorNebula2 = vec3(0.2, 0.1, 0.3);   // Purple

    // Mix colors based on noise
    vec3 color = mix(colorBg, colorNebula1, smoothstep(0.2, 0.6, n));
    color = mix(color, colorNebula2, smoothstep(0.4, 0.8, n1c));

    // Apply pseudo-light direction bias (lighter on one side)
    float lightBias = dot(normalize(st - 0.5), normalize(u_lightDir)) * 0.5 + 0.5;
    color *= 0.8 + 0.4 * lightBias;

    // Apply color grading tint
    color *= u_tint;

    // Vignette
    vec2 uv = v_texCoords;
    float vignette = 1.0 - smoothstep(0.5, 1.5, length(uv - 0.5) * 1.5);
    color *= vignette;

    // Calculate alpha with intensity control
    float baseAlpha = 0.5 + n * 0.2;
    float alpha = baseAlpha * u_intensity;

    gl_FragColor = vec4(color, alpha) + dummy * 0.0001;
}
