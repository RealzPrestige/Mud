package dev.zprestige.mud.shader.impl;

import dev.zprestige.mud.shader.ShaderUtil;
import dev.zprestige.mud.util.MC;
import dev.zprestige.mud.util.impl.MathUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import dev.zprestige.mud.util.impl.StencilUtil;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class BlurShader implements MC {
    private static final ShaderUtil blurShader = new ShaderUtil("blur");
    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    public static void invokeBlur() {
        StencilUtil.writeStencil();
    }

    public static void releaseBlur(float radius) {
        StencilUtil.stencil(1);
        renderBlur(radius);
        StencilUtil.disableStencil();
    }

    private static void setupUniforms(float dir1, float dir2, float radius) {
        blurShader.setUniformi("textureIn", 0);
        blurShader.setUniformf("texelSize", 1.0F / (float) mc.displayWidth, 1.0F / (float) mc.displayHeight);
        blurShader.setUniformf("direction", dir1, dir2);
        blurShader.setUniformf("radius", radius);

        final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
        for (int i = 0; i <= radius; i++) {
            weightBuffer.put(MathUtil.gaussian(i, radius / 2));
        }

        weightBuffer.rewind();
        glUniform1(blurShader.getUniform("weights"), weightBuffer);
    }

    private static void renderBlur(float radius) {
        glEnable(GL_BLEND);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        framebuffer = RenderUtil.createFrameBuffer(framebuffer);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        blurShader.attachShader();
        setupUniforms(1, 0, radius);
        glBindTexture(GL_TEXTURE_2D, mc.getFramebuffer().framebufferTexture);
        ShaderUtil.screenTex();

        framebuffer.unbindFramebuffer();
        blurShader.releaseShader();
        mc.getFramebuffer().bindFramebuffer(true);
        blurShader.attachShader();
        setupUniforms(0, 1, radius);
        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        ShaderUtil.screenTex();

        blurShader.releaseShader();

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        framebuffer = RenderUtil.createFrameBuffer(framebuffer);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

}
