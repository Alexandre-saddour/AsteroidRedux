#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_time;
uniform vec4 u_lightningColor; // Color of the lightning
uniform vec4 u_regionBounds; // u, v, u2, v2 of the texture region in the atlas
uniform float u_activation; // 0.0 to 1.0, controls apparition animation

// Simple pseudo-random noise
float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

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

float fbm(vec2 st) {
    float value = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 3; i++) {
        value += amplitude * noise(st);
        st *= 2.0;
        amplitude *= 0.5;
    }
    return value;
}

// Rounded Box SDF
// (Removed as we are using texture alpha masking now)

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    
    // Base color from texture * vertex color
    gl_FragColor = texColor * v_color;
    
    // Calculate local UVs (0.0 to 1.0) within the region
    vec2 localUV = (v_texCoords - u_regionBounds.xy) / (u_regionBounds.zw - u_regionBounds.xy);
    
    // Lightning/Cold Effect
    // Calculate distance to edge (0 at edge, 0.5 at center)
    float distX = min(localUV.x, 1.0 - localUV.x);
    float distY = min(localUV.y, 1.0 - localUV.y);
    float dist = min(distX, distY);
    
    // Only apply near edges
    if (dist < 0.15) {
        float t = u_time * 3.0;
        
        // Calculate direction towards center (0.5, 0.5)
        vec2 center = vec2(0.5, 0.5);
        vec2 dir = normalize(center - localUV);
        
        // Sharper noise for lightning/electric look
        // Use FBM but distort UVs more aggressively
        // Animate along the direction vector (towards center)
        // We subtract dir * t because noise(p - dir*t) moves the pattern in direction 'dir'
        float n = fbm(localUV * 20.0 - dir * t * 2.0);
        
        // Create "strands" or "bolts" using abs()
        // This makes it look less like a cloud and more like energy
        // Animate strand width with activation
        float strandWidth = 0.4 * u_activation;
        float strands = 1.0 - smoothstep(0.0, strandWidth, abs(n - 0.5));
        
        // Fade out as we go inwards
        float fade = smoothstep(0.15, 0.0, dist);
        
        // Combine
        float alpha = strands * fade;
        
        // MASK BY TEXTURE ALPHA to avoid square corners on rounded images
        // This ensures the effect is contained within the card
        alpha *= texColor.a;
        
        // Boost alpha for core strands
        alpha = pow(alpha, 0.8); 
        
        // Apply activation fade
        alpha *= u_activation;
        
        // Color
        vec3 color = u_lightningColor.rgb;
        
        // Add brightness variation
        color += vec3(0.5) * strands;
        
        // Additive blending on top of base color
        gl_FragColor.rgb += color * alpha;
        
        // Clamp
        gl_FragColor.rgb = clamp(gl_FragColor.rgb, 0.0, 1.0);
    }
}
