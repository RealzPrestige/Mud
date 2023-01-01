#version 120

uniform sampler2D texture;
uniform float opacity;

void main() {
    vec4 col = texture2D(texture, gl_TexCoord[0].xy);
    if (col.a != 0f) {
        gl_FragColor = vec4(col.r, col.g, col.b, opacity);
    }
}