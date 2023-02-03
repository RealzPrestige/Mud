package dev.zprestige.mud.shader.impl;

import dev.zprestige.mud.shader.ShaderUtil;
import dev.zprestige.mud.util.MC;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.client.shader.Framebuffer;

import java.awt.*;

import static dev.zprestige.mud.util.impl.RenderUtil.glColor;
import static org.lwjgl.opengl.GL11.*;

public class GradientShader implements MC {
    private final static ShaderUtil shader = new ShaderUtil("gradient");
    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    private static void setupUniforms(float step, float speed, Color color, Color color2) {
        shader.setUniformi("texture", 0);
        shader.setUniformf("rgb", color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
        shader.setUniformf("rgb1", color2.getRed() / 255.0f, color2.getGreen() / 255.0f, color2.getBlue() / 255.0f);
        shader.setUniformf("step", 300 * step);
        shader.setUniformf("offset", (float) ((((double) System.currentTimeMillis() * (double) speed) % (mc.displayWidth * mc.displayHeight)) / 10.0f));
    }

    public static void setup(float step, float speed, Color color, Color color2) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glColor(Color.WHITE);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        framebuffer = RenderUtil.createFrameBuffer(framebuffer);

        mc.getFramebuffer().bindFramebuffer(true);
        shader.attachShader();
        setupUniforms(step, speed, color, color2);

        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
    }

    public static void finish() {
        shader.releaseShader();

        glColor(Color.WHITE);
        glBindTexture(GL_TEXTURE_2D, 0);
        glDisable(GL_BLEND);
        glPopMatrix();
    }
}