package dev.zprestige.mud.shader.impl;

import dev.zprestige.mud.shader.ShaderUtil;
import dev.zprestige.mud.util.MC;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;

import java.awt.*;

import static dev.zprestige.mud.util.impl.RenderUtil.glColor;
import static org.lwjgl.opengl.GL11.*;

public class AlphaShader implements MC {
    private final static ShaderUtil shader = new ShaderUtil("alpha");
    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    private static void setupUniforms(float opacity) {
        shader.setUniformi("texture", 0);
        shader.setUniformf("opacity", opacity);
    }

    public static void setup(float opacity) {
        glEnable(GL_BLEND);
        glColor(Color.WHITE);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        framebuffer = RenderUtil.createFrameBuffer(framebuffer);

        mc.getFramebuffer().bindFramebuffer(true);
        shader.attachShader();
        setupUniforms(opacity);

        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
    }

    public static void finish() {
        shader.releaseShader();

        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.bindTexture(0);
    }
}
