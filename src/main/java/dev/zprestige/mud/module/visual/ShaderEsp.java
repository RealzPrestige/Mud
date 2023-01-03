package dev.zprestige.mud.module.visual;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.render.*;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.shader.impl.BufferGroup;
import dev.zprestige.mud.shader.impl.GlowShader;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;

public class ShaderEsp extends Module {
    private final BooleanSetting checkFrustum = setting("CheckFrustum", true);

    private final FloatSetting speed = setting("Speed", 1.0f, 0.1f, 5.0f).invokeTab("Shader");
    private final FloatSetting step = setting("Step", 0.2f, 0.1f, 2.0f).invokeTab("Shader");
    private final FloatSetting opacity = setting("Opacity", 150.0f, 0.0f, 255.0f).invokeTab("Shader");
    private final FloatSetting lineWidth = setting("Line Width", 1.0f, 0.1f, 5.0f).invokeTab("Shader");

    private final ColorSetting color1 = setting("Color 1", new Color(113, 93, 214)).invokeTab("Coloring");
    private final ColorSetting color2 = setting("Color 2", new Color(113, 220, 214)).invokeTab("Coloring");

    private final BooleanSetting players = setting("Players", false).invokeTab("Targets");
    private final BooleanSetting crystals = setting("Crystals", false).invokeTab("Targets");
    private boolean active;

    private final BufferGroup bufferGroup = new BufferGroup(this, z -> true, lineWidth, color1, color2, step, speed, opacity,
            () -> mc.world.loadedEntityList.stream().filter(entity -> !entity.equals(mc.player)
                    && (entity instanceof EntityPlayer || entity instanceof EntityEnderCrystal)
                    && !(entity instanceof EntityPlayer && !players.getValue())
                    && !(entity instanceof EntityEnderCrystal && !crystals.getValue())
                    && (!checkFrustum.getValue() || Mud.frustumManager.isInsideFrustum(entity.getEntityBoundingBox()))
                    ).forEach(entity -> mc.getRenderManager().renderEntityStatic(entity, mc.getRenderPartialTicks(), false))
    );

    @EventListener
    public void onRender3D(Render3DEvent event) {
        active = true;
        GlowShader.render3D(bufferGroup);
        active = false;
    }

    @EventListener
    public void onRender2D(Render2DEvent event) {
        active = true;
        GlowShader.render2D(bufferGroup);
        active = false;
    }

    @EventListener
    public void onRenderNameplate(NameplateEvent event) {
        if (active) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onArmor(ArmorEvent event) {
        event.setCancelled(true);
    }

    @EventListener
    public void onHeldItem(HeldItemEvent event) {
        event.setCancelled(true);
    }

    @EventListener
    public void onFire(FireEvent event) {
        event.setCancelled(true);
    }
}
