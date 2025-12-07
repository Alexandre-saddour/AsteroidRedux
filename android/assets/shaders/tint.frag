#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec3 u_tint; // RGB tint
uniform float u_saturation;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    
    // Apply tint
    vec3 color = texColor.rgb * u_tint;
    
    // Apply saturation
    float gray = dot(color, vec3(0.299, 0.587, 0.114));
    color = mix(vec3(gray), color, u_saturation);
    
    gl_FragColor = vec4(color, texColor.a);
}
