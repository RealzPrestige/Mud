package dev.zprestige.mud.module.visual;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.gui.GuiBackgroundEvent;
import dev.zprestige.mud.events.impl.render.*;
import dev.zprestige.mud.events.impl.system.ConnectEvent;
import dev.zprestige.mud.events.impl.system.PacketReceiveEvent;
import dev.zprestige.mud.events.impl.world.*;
import dev.zprestige.mud.manager.EventManager;
import dev.zprestige.mud.mixins.interfaces.IItemRenderer;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.shader.impl.BlurShader;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.awt.*;

public class NoRender extends Module {
    private final BooleanSetting armor = setting("Armor", false).invokeTab("Remove");
    private final BooleanSetting heldItems = setting("Held Items", false).invokeTab("Remove");
    private final BooleanSetting fire = setting("Fire", false).invokeTab("Remove");
    private final BooleanSetting hurtCam = setting("HurtCam", false).invokeTab("Remove");
    private final BooleanSetting blockOverlays = setting("Block Overlays", false).invokeTab("Remove");
    private final BooleanSetting newAnimations = setting("New Animations", false).invokeTab("Remove");
    private final BooleanSetting boss = setting("Boss", false).invokeTab("Remove");
    private final BooleanSetting vignette = setting("Vignette", false).invokeTab("Remove");
    private final BooleanSetting guiBackground = setting("Gui Background", false).invokeTab("Remove");
    private final BooleanSetting blur = setting("Blur", false).invokeVisibility(z -> guiBackground.getValue()).invokeTab("Remove");

    private final BooleanSetting animations = setting("Animations", false).invokeTab("World Remove");
    private final BooleanSetting sPacketEffects = setting("SPacketEffects", false).invokeTab("World Remove");
    private final BooleanSetting particles = setting("Particles", false).invokeTab("World Remove");
    private final BooleanSetting namePlates = setting("Name Plates", false).invokeTab("World Remove");
    private final BooleanSetting clouds = setting("Clouds", false).invokeTab("World Remove");
    private final BooleanSetting weather = setting("Weather", false).invokeTab("World Remove");
    private final BooleanSetting lightMapUpdates = setting("Light Map Updates", false).invokeTab("World Remove");
    private final BooleanSetting blockOutlines = setting("Block Outlines", false).invokeTab("World Remove");
    private final ColorSetting skyColor = setting("Sky Color", Color.WHITE).invokeTab("World Remove");
    private final BooleanSetting sky = setting("Sky", false).invokeTab("World Remove");

    private float time;
    private long sys;

    @EventListener
    public void onFogColor(FogEvent event){
        if (sky.getValue()){
            return;
        }
        Color color = skyColor.getValue();
        event.setColor(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
    }

    @EventListener
    public void onGuiBackground(GuiBackgroundEvent event) {
        if (mc.player != null && mc.world != null) {
            if (guiBackground.getValue()) {
                event.setCancelled(true);
            }
        }
    }

    @EventListener
    public void onRender2D(Render2DEvent event) {
        if (mc.gameSettings.gammaSetting != 1000.0f){
            mc.gameSettings.gammaSetting = 1000.0f;
        }

        if (mc.currentScreen == null) {
            if (time > 0) {
                time -= EventManager.getDeltaTime() * 10.0f;
                time = Math.max(0.0f, time);
            }
        } else if (!(mc.currentScreen instanceof GuiChat)){
            if (time < 15) {
                time += EventManager.getDeltaTime() * 10.0f;
                time = Math.min(15.0f, time);
            }
        }
        if (guiBackground.getValue() && blur.getValue()) {
            if (time > 1.0f) {
                BlurShader.invokeBlur();
                RenderUtil.rounded(0.0f, 0.0f, event.getScaledResolution().getScaledWidth(), event.getScaledResolution().getScaledHeight(), 0.0f, Color.WHITE);
                BlurShader.releaseBlur(time);
            }
        }
    }

    @EventListener
    public void onConnect(ConnectEvent event) {
        sys = System.currentTimeMillis();
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        if (!newAnimations.getValue()) {
            return;
        }
        IItemRenderer itemRenderer = (IItemRenderer) mc.entityRenderer.itemRenderer;
        if (itemRenderer.getPrevEquippedProgressMainHand() >= 0.9) {
            itemRenderer.setEquippedProgressMainHand(1.0f);
            itemRenderer.setItemStackMainHand(mc.player.getHeldItemMainhand());
        }
        if (itemRenderer.getPrevEquippedProgressOffHand() >= 0.9) {
            itemRenderer.setEquippedProgressOffHand(1.0f);
            itemRenderer.setItemStackOffHand(mc.player.getHeldItemOffhand());
        }
    }

    @EventListener
    public void onRenderOverlay(RenderOverlayEvent event) {
        if (boss.getValue() && (event.getElementType().equals(RenderGameOverlayEvent.ElementType.BOSSHEALTH) || event.getElementType().equals(RenderGameOverlayEvent.ElementType.BOSSINFO))) {
            event.setCancelled(true);
        }
        if (vignette.getValue() && event.getElementType().equals(RenderGameOverlayEvent.ElementType.VIGNETTE)) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onParticle(ParticleEvent event) {
        if (particles.getValue()) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onNameplate(NameplateEvent event) {
        if (namePlates.getValue()) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!sPacketEffects.getValue()) {
            return;
        }
        if (event.getPacket() instanceof SPacketEffect) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onUpdateAnimation(UpdateAnimationEvent event) {
        if (animations.getValue()) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onRenderSky(RenderSkyEvent event) {
        if (sky.getValue()) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onRenderBlockOutline(RenderBlockOutlineEvent event) {
        if (blockOutlines.getValue()) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onUpdateLightMap(UpdateLightMapEvent event) {
        if (lightMapUpdates.getValue() && System.currentTimeMillis() - sys > 5000) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onRenderWeather(RenderWeatherEvent event) {
        if (weather.getValue()) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onRenderClouds(RenderCloudsEvent event) {
        if (clouds.getValue()) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onArmor(ArmorEvent event) {
        if (armor.getValue()) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onHeldItem(HeldItemEvent event) {
        if (heldItems.getValue()) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onFire(FireEvent event) {
        if (fire.getValue()) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onHurtCam(HurtCamEvent event) {
        if (hurtCam.getValue()) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onBlockOverlay(BlockOverlayEvent event) {
        if (blockOverlays.getValue()) {
            event.setCancelled(true);
        }
    }
}
