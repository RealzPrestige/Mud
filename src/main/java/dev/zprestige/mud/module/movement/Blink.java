package dev.zprestige.mud.module.movement;

import com.mojang.authlib.GameProfile;
import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.render.NameplateEvent;
import dev.zprestige.mud.events.impl.render.Render2DEvent;
import dev.zprestige.mud.events.impl.render.Render2DPostEvent;
import dev.zprestige.mud.events.impl.render.Render3DEvent;
import dev.zprestige.mud.events.impl.system.PacketSendEvent;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.manager.EventManager;
import dev.zprestige.mud.mixins.interfaces.IMinecraft;
import dev.zprestige.mud.mixins.interfaces.ITimer;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.IntSetting;
import dev.zprestige.mud.shader.impl.AlphaShader;
import dev.zprestige.mud.shader.impl.BufferGroup;
import dev.zprestige.mud.shader.impl.GlowShader;
import dev.zprestige.mud.util.impl.MathUtil;
import dev.zprestige.mud.util.impl.PacketUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

import java.awt.*;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Blink extends Module {
    private final BooleanSetting tickShift = setting("Tick Shift", false).invokeTab("Tick Shift");
    private final IntSetting activeTicks = setting("Active Ticks", 5, 1, 40).invokeVisibility(z -> tickShift.getValue()).invokeTab("Tick Shift");
    private final FloatSetting timer = setting("Timer", 1.0f, 0.1f, 5.0f).invokeVisibility(z -> tickShift.getValue()).invokeTab("Tick Shift");

    private final BooleanSetting render = setting("Render", false).invokeTab("Render");
    private final BooleanSetting hud = setting("Hud", false).invokeVisibility(z -> render.getValue()).invokeTab("Render");
    private final FloatSetting speed = setting("Speed", 1.0f, 0.1f, 5.0f).invokeVisibility(z -> render.getValue()).invokeTab("Render");
    private final FloatSetting step = setting("Step", 0.2f, 0.1f, 2.0f).invokeVisibility(z -> render.getValue()).invokeTab("Render");
    private final FloatSetting opacity = setting("Opacity", 150.0f, 0.0f, 255.0f).invokeVisibility(z -> render.getValue()).invokeTab("Render");
    private final FloatSetting lineWidth = setting("Line Width", 1.0f, 0.1f, 5.0f).invokeVisibility(z -> render.getValue()).invokeTab("Render");
    private final ColorSetting color1 = setting("Color 1", new Color(113, 93, 214)).invokeVisibility(z -> render.getValue()).invokeTab("Render");
    private final ColorSetting color2 = setting("Color 2", new Color(113, 220, 214)).invokeVisibility(z -> render.getValue()).invokeTab("Render");

    private final Queue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    private EntityOtherPlayerMP entity;
    private static int ticks;
    private float animation;

    private final BufferGroup bufferGroup = new BufferGroup(this, z -> render.getValue() && entity != null, lineWidth, color1, color2, step, speed, opacity,
            () -> mc.getRenderManager().renderEntityStatic(entity, mc.getRenderPartialTicks(), false)
    );
    public Blink() {
        setAlwaysListening(true);
        Mud.eventBus.registerListener(this);
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        if (isEnabled()) {
            GlowShader.render3D(bufferGroup);
        }
    }

    @EventListener
    public void onRender2D(Render2DPostEvent event) {
        if (isEnabled()) {
            GlowShader.render2D(bufferGroup);
        }

        if (hud.getValue()) {
            if (mc.currentScreen == null) {
                animation = MathUtil.lerp(animation, isEnabled() ? 1.0f : 0.0f, EventManager.getDeltaTime());
                float x = event.getScaledResolution().getScaledWidth() / 2.0f - 20.0f;
                float y = event.getScaledResolution().getScaledHeight() / 2.0f + 15.0f - (10 * animation);
                float width = 40.0f;
                float height = 15.0f;
                if (animation < 0.05f) {
                    return;
                }

                RenderUtil.rounded(x, y, x + width, y + height, 3.0f, new Color(0, 0, 0, 0.3f * animation));

                Mud.fontManager.string(String.valueOf(MathUtil.roundNumber(mc.player.getDistance(entity), 1)), x + 20.0f, y + height / 2.0f - Mud.fontManager.stringHeight() / 2.0f, new Color(1.0f, 1.0f, 1.0f, animation));

                AlphaShader.setup(animation);
                RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().zLevel = 200;
                mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Blocks.GRASS), (int) x, (int) y);
                mc.getRenderItem().zLevel = 0;
                RenderHelper.disableStandardItemLighting();
                AlphaShader.finish();

            }
        }
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (!isEnabled()) {
            return;
        }
        if (tickShift.getValue()) {
            if (ticks > 0) {
                ((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50.0f / timer.getValue());
                ticks--;
            } else {
                ((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50.0f);
            }
        }
    }


    @EventListener
    public void onPacketSend(PacketSendEvent event) {
        if (!isEnabled()) {
            return;
        }
        if (event.getPacket() instanceof CPacketPlayer) {
            event.setCancelled(true);
            packets.add(event.getPacket());
        }
    }

    @EventListener
    public void onNameplate(NameplateEvent event) {
        if (!isEnabled()) {
            return;
        }
        if (render.getValue()) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null || PacketFly.isActive()) {
            toggle();
            return;
        }
        entity = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString(mc.player.getUniqueID().toString()), mc.player.getName()));
        entity.copyLocationAndAnglesFrom(mc.player);
        entity.rotationYawHead = mc.player.rotationYawHead;
        entity.prevRotationYawHead = mc.player.rotationYawHead;
        entity.rotationYaw = mc.player.rotationYaw;
        entity.rotationPitch = mc.player.rotationPitch;
        entity.prevRotationYaw = mc.player.rotationYaw;
        entity.prevRotationPitch = mc.player.rotationPitch;
        ticks = activeTicks.getValue();
    }

    @Override
    public void onDisable() {
        while (!packets.isEmpty()) {
            PacketUtil.invokeNoEvent(packets.poll());
        }
        ((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50.0f);
    }

    public static boolean isShiftingTicks() {
        return ticks > 0;
    }
}
