package dev.zprestige.mud.shader.impl;

import dev.zprestige.mud.shader.ShaderUtil;
import dev.zprestige.mud.util.MC;
import dev.zprestige.mud.util.impl.MathUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glUniform1;

public class ShadowShader implements MC {
    private final static ShaderUtil shadowShader = new ShaderUtil("shadow");
    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);
    private static Framebuffer buffer = new Framebuffer(1, 1, false);

    public static void invokeShadow(){
        buffer = RenderUtil.createFrameBuffer(buffer);
        buffer.framebufferClear();
        buffer.bindFramebuffer(true);
    }

    public static void releaseShadow(int radius, int offset){
        buffer.unbindFramebuffer();
        render(buffer.framebufferTexture, radius, offset);
    }

    private static void render(int sourceTexture, int radius, int offset) {
        framebuffer = RenderUtil.createFrameBuffer(framebuffer);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(516, 0.0f);
        glEnable(GL_BLEND);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
        for (int i = 0; i <= radius; i++) {
            weightBuffer.put(MathUtil.gaussian(i, radius));
        }
        weightBuffer.rewind();

        RenderUtil.setAlphaLimit(0.0F);

        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        shadowShader.attachShader();
        setupUniforms(radius, offset, 0, weightBuffer);
        glBindTexture(GL_TEXTURE_2D, sourceTexture);
        ShaderUtil.screenTex();
        shadowShader.releaseShader();
        framebuffer.unbindFramebuffer();


        mc.getFramebuffer().bindFramebuffer(true);

        shadowShader.attachShader();
        setupUniforms(radius, 0, offset, weightBuffer);
        glActiveTexture(GL_TEXTURE16);
        glBindTexture(GL_TEXTURE_2D, sourceTexture);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        ShaderUtil.screenTex();
        shadowShader.releaseShader();

        glAlphaFunc(516, 0.1f);
        glEnable(GL_ALPHA_TEST);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private static void setupUniforms(int radius, int directionX, int directionY, FloatBuffer weights) {
        shadowShader.setUniformi("inTexture", 0);
        shadowShader.setUniformi("textureToCheck", 16);
        shadowShader.setUniformf("radius", radius);
        shadowShader.setUniformf("texelSize", 1.0F / (float) mc.displayWidth, 1.0F / (float) mc.displayHeight);
        shadowShader.setUniformf("direction", directionX, directionY);
        glUniform1(shadowShader.getUniform("weights"), weights);
    }
}