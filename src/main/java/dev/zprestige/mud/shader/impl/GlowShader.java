package dev.zprestige.mud.shader.impl;

import dev.zprestige.mud.shader.ShaderUtil;
import dev.zprestige.mud.util.MC;
import dev.zprestige.mud.util.impl.MathUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.awt.*;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glUniform1;

public class GlowShader implements MC {
    private static final ShaderUtil glowShader = new ShaderUtil("glow");
    private static final ShaderUtil outlineShader = new ShaderUtil("outline");


    public static void render3D(BufferGroup bufferGroup) {
        bufferGroup.renderBuffer();
        mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.disableLighting();
    }

    public static void render2D(BufferGroup bufferGroup) {
        if (mc.gameSettings.thirdPersonView != 0) {
            return;
        }
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        if (bufferGroup.bound && bufferGroup.shouldRender) {
            shader(bufferGroup, bufferGroup.lineWidthSetting.getValue());
        }
    }


    public static void shader(BufferGroup group, float lineWidth) {
        if (group.glowFrameBuffer == null || group.outlineFrameBuffer == null || group.framebuffer == null) {
            return;
        }
        Color color = group.color.getValue();

        GlStateManager.alphaFunc(GL11.GL_GREATER, GL11.GL_ZERO);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        group.outlineFrameBuffer.framebufferClear();
        group.outlineFrameBuffer.bindFramebuffer(true);
        outlineShader.attachShader();
        setupOutlineUniforms(0, 1, color, lineWidth);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, group.framebuffer.framebufferTexture);
        ShaderUtil.screenTex();
        outlineShader.attachShader();
        setupOutlineUniforms(1, 0, color, lineWidth);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, group.framebuffer.framebufferTexture);
        ShaderUtil.screenTex();
        outlineShader.releaseShader();
        group.outlineFrameBuffer.unbindFramebuffer();

        GlStateManager.color(1, 1, 1, 1);
        group.glowFrameBuffer.framebufferClear();
        group.glowFrameBuffer.bindFramebuffer(true);
        glowShader.attachShader();
        setupGlowUniforms(1, 0, color, group.color2.getValue(), group.step.getValue(), group.speed.getValue(), group.opacity.getValue() / 255.0f, lineWidth);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, group.outlineFrameBuffer.framebufferTexture);
        ShaderUtil.screenTex();
        glowShader.releaseShader();
        group.glowFrameBuffer.unbindFramebuffer();

        mc.getFramebuffer().bindFramebuffer(true);
        glowShader.attachShader();
        setupGlowUniforms(0, 1, color, group.color2.getValue(), group.step.getValue(), group.speed.getValue(), group.opacity.getValue() / 255.0f, lineWidth);
        glActiveTexture(GL13.GL_TEXTURE16);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, group.framebuffer.framebufferTexture);
        glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, group.glowFrameBuffer.framebufferTexture);
        ShaderUtil.screenTex();

        glowShader.releaseShader();
        group.bound = false;
    }


    public static void setupGlowUniforms(float direction1, float direction2, Color color, Color color2, float step, float speed, float opacity, float lineWidth) {
        glowShader.setUniformi("texture", 0);
        glowShader.setUniformi("textureToCheck", 16);
        glowShader.setUniformf("radius", lineWidth);
        glowShader.setUniformf("texelSize", 1.0f / mc.displayWidth, 1.0f / mc.displayHeight);
        glowShader.setUniformf("direction", direction1, direction2);
        glowShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
        glowShader.setUniformf("color2", color2.getRed() / 255f, color2.getGreen() / 255f, color2.getBlue() / 255f);
        glowShader.setUniformf("exposure", Math.min(15.0f, color.getAlpha() * 0.0588235294117647f));
        glowShader.setUniformf("frequency", 300.0f * step);
        glowShader.setUniformf("off", (float) ((((double) System.currentTimeMillis() * (double) speed) % (mc.displayWidth * mc.displayHeight)) / 10.0f));
        glowShader.setUniformf("opacity", opacity);
        glowShader.setUniformi("avoidTexture", 1);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(256);
        for (int i = 1; i <= lineWidth; i++) {
            buffer.put(MathUtil.gaussian(i, lineWidth / 2));
        }
        buffer.rewind();
        glUniform1(glowShader.getUniform("weights"), buffer);
    }


    public static void setupOutlineUniforms(float dir1, float dir2, Color color, float lineWidth) {
        outlineShader.setUniformi("texture", 0);
        outlineShader.setUniformf("radius", lineWidth);
        outlineShader.setUniformf("texelSize", 1.0f / mc.displayWidth, 1.0f / mc.displayHeight);
        outlineShader.setUniformf("direction", dir1, dir2);
        outlineShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);

    }
}