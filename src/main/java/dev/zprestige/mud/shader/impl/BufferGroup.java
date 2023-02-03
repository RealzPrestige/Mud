package dev.zprestige.mud.shader.impl;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.util.MC;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.client.shader.Framebuffer;

import java.util.function.Predicate;

public class BufferGroup implements MC {
    public Framebuffer framebuffer, outlineFrameBuffer, glowFrameBuffer;
    public boolean shouldRender, cleared;
    public boolean bound;
    public final Module parent;
    public final Predicate<Boolean> booleanPredicate;
    public final ColorSetting color, color2;
    public final FloatSetting lineWidthSetting, step, speed, opacity;
    public final Runnable runnable;
    public boolean predicate = false;
    public Runnable invokable;

    public BufferGroup(Module parent, Predicate<Boolean> booleanPredicate, FloatSetting lineWidth, ColorSetting color, ColorSetting color2, FloatSetting step, FloatSetting speed, FloatSetting opacity, Runnable runnable) {
        this.parent = parent;
        this.booleanPredicate = booleanPredicate;
        this.color = color;
        this.color2 = color2;
        this.lineWidthSetting = lineWidth;
        this.step = step;
        this.speed = speed;
        this.opacity = opacity;
        this.runnable = runnable;
        this.invokable = () -> predicate = booleanPredicate.test(null);
    }


    public void clearBuffers() {
        framebuffer = null;
        outlineFrameBuffer = null;
        glowFrameBuffer = null;
        cleared = true;
    }

    public void renderBuffer() {
        // Pre-Render checks
        Mud.threadManager.invokeThread(invokable);
        boolean flag = !predicate || !parent.getEnabled().getValue();
        if (flag) {
            shouldRender = false;
            if (!cleared) {
                clearBuffers();
            }
            return;
        }

        framebuffer = RenderUtil.createFrameBuffer(framebuffer);
        outlineFrameBuffer = RenderUtil.createFrameBuffer(outlineFrameBuffer);
        glowFrameBuffer = RenderUtil.createFrameBuffer(glowFrameBuffer);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);

        boolean entityShadows = mc.gameSettings.entityShadows;
        mc.gameSettings.entityShadows = false;

        runnable.run();

        mc.gameSettings.entityShadows = entityShadows;
        framebuffer.unbindFramebuffer();
        cleared = false;

        shouldRender = true;
        bound = true;
    }
}