#version 120

uniform sampler2D textureIn, textureToCheck;
uniform vec2 texelSize, direction;
uniform vec3 color, color2;
uniform bool avoidTexture;
uniform float exposure, radius, frequency, off, opacity;
uniform float weights[256];

#define offset direction * texelSize

void main() {
    bool yesAlpha = false;
    if (direction.y == 1 && avoidTexture) {
        if (texture2D(textureToCheck, gl_TexCoord[0].st).a != 0.0) {
            float distance = sqrt(gl_FragCoord.x * gl_FragCoord.x + gl_FragCoord.y * gl_FragCoord.y) + off;

            distance = distance / frequency;

            distance = ((sin(distance) + 1.0) / 2.0);

            float distanceInv = 1 - distance;
            float r = color.r * distance + color2.r * distanceInv;
            float g = color.g * distance + color2.g * distanceInv;
            float b = color.b * distance + color2.b * distanceInv;

            gl_FragColor = vec4(r, g, b, opacity);
            yesAlpha = true;
        }
    }
    if (!yesAlpha){

        float innerAlpha = texture2D(textureIn, gl_TexCoord[0].st).a * weights[0];

        for (float r = 1.0; r <= radius; r ++) {
            innerAlpha += texture2D(textureIn, gl_TexCoord[0].st + offset * r).a * weights[int(r)];
            innerAlpha += texture2D(textureIn, gl_TexCoord[0].st - offset * r).a * weights[int(r)];
        }

        float distance = sqrt(gl_FragCoord.x * gl_FragCoord.x + gl_FragCoord.y * gl_FragCoord.y) + off;

        distance = distance / frequency;

        distance = ((sin(distance) + 1.0) / 2.0);

        float distanceInv = 1 - distance;
        float r = color.r * distance + color2.r * distanceInv;
        float g = color.g * distance + color2.g * distanceInv;
        float b = color.b * distance + color2.b * distanceInv;

        gl_FragColor = vec4(r, g, b, mix(innerAlpha, 1.0 - exp(-innerAlpha * exposure), step(0.0, direction.y)));
    }
}
