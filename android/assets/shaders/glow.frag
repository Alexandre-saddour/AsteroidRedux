#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec4 u_glowColor;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    
    // Use the texture's alpha to create a solid color silhouette
    gl_FragColor = vec4(u_glowColor.rgb, texColor.a * u_glowColor.a);
}
