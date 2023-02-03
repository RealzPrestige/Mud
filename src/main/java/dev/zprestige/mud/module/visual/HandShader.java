package dev.zprestige.mud.module.visual;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.render.FireEvent;
import dev.zprestige.mud.events.impl.render.Render2DEvent;
import dev.zprestige.mud.events.impl.render.Render3DPostEvent;
import dev.zprestige.mud.events.impl.render.Render3DPreEvent;
import dev.zprestige.mud.mixins.interfaces.IEntityRenderer;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.shader.impl.BufferGroup;
import dev.zprestige.mud.shader.impl.GlowShader;

import java.awt.*;

public class HandShader extends Module {
    private final FloatSetting speed = setting("Speed", 1.0f, 0.1f, 5.0f).invokeTab("Shader");
    private final FloatSetting step = setting("Step", 0.2f, 0.1f, 2.0f).invokeTab("Shader");
    private final FloatSetting opacity = setting("Opacity", 150.0f, 0.0f, 255.0f).invokeTab("Shader");
    private final FloatSetting lineWidth = setting("Line Width", 1.0f, 0.1f, 5.0f).invokeTab("Shader");

    private final ColorSetting color1 = setting("Color 1", new Color(113, 93, 214)).invokeTab("Coloring");
    private final ColorSetting color2 = setting("Color 2", new Color(113, 220, 214)).invokeTab("Coloring");

    private final BufferGroup bufferGroup = new BufferGroup(this, z -> true, lineWidth, color1, color2, step, speed, opacity,
            () -> ((IEntityRenderer) mc.entityRenderer).invokeRenderHand(mc.getRenderPartialTicks(), 2)
    );

    @EventListener
    public void onRender3D(Render3DPostEvent event) {
        GlowShader.render3D(bufferGroup);
    }

    @EventListener
    public void onRender2D(Render2DEvent event) {
        GlowShader.render2D(bufferGroup);
    }

    @EventListener
    public void onFire(FireEvent event) {
        event.setCancelled(true);
    }
}
