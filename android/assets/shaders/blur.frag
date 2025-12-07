#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_resolution;
uniform vec2 u_dir; // Direction (1,0) or (0,1)

void main() {
    vec4 color = vec4(0.0);
    vec2 off1 = vec2(1.3846153846) * u_dir / u_resolution;
    vec2 off2 = vec2(3.2307692308) * u_dir / u_resolution;
    
    color += texture2D(u_texture, v_texCoords) * 0.2270270270;
    color += texture2D(u_texture, v_texCoords + off1) * 0.3162162162;
    color += texture2D(u_texture, v_texCoords - off1) * 0.3162162162;
    color += texture2D(u_texture, v_texCoords + off2) * 0.0702702703;
    color += texture2D(u_texture, v_texCoords - off2) * 0.0702702703;
    
    gl_FragColor = color * v_color;
}
