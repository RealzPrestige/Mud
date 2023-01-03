package dev.zprestige.mud.module.visual;

import com.mojang.authlib.GameProfile;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.render.Render2DEvent;
import dev.zprestige.mud.events.impl.render.Render3DEvent;
import dev.zprestige.mud.events.impl.system.PacketReceiveEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.IntSetting;
import dev.zprestige.mud.shader.impl.BufferGroup;
import dev.zprestige.mud.shader.impl.GlowShader;
import dev.zprestige.mud.util.impl.BlockUtil;
import dev.zprestige.mud.util.impl.RotationUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChorusEsp extends Module {
    private final IntSetting time = setting("Time", 5, 1, 10);
    private final BooleanSetting rotate = setting("Rotate", false);

    private final FloatSetting speed = setting("Speed", 1.0f, 0.1f, 5.0f).invokeTab("Render");
    private final FloatSetting step = setting("Step", 0.2f, 0.1f, 2.0f).invokeTab("Render");
    private final FloatSetting opacity = setting("Opacity", 150.0f, 0.0f, 255.0f).invokeTab("Render");
    private final FloatSetting lineWidth = setting("Line Width", 1.0f, 0.1f, 5.0f).invokeTab("Render");
    private final ColorSetting color1 = setting("Color 1", new Color(113, 93, 214)).invokeTab("Render");
    private final ColorSetting color2 = setting("Color 2", new Color(113, 220, 214)).invokeTab("Render");

    private final HashMap<EntityOtherPlayerMP, Long> chorusPosses = new HashMap<>();


    private final BufferGroup bufferGroup = new BufferGroup(this, z -> !chorusPosses.isEmpty(), lineWidth, color1, color2, step, speed, opacity,
            () -> {
                for (Map.Entry<EntityOtherPlayerMP, Long> entry : new HashMap<>(chorusPosses).entrySet()) {
                    if (System.currentTimeMillis() - entry.getValue() > time.getValue() * 1000.0f) {
                        continue;
                    }
                    mc.getRenderManager().renderEntityStatic(entry.getKey(), mc.getRenderPartialTicks(), false);
                }
            }

    );

    @EventListener
    public void onRender3D(Render3DEvent event) {
        GlowShader.render3D(bufferGroup);

    }

    @EventListener
    public void onRender2D(Render2DEvent event) {
        GlowShader.render2D(bufferGroup);
    }

    @EventListener
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getSound().equals(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT)) {
                double x = packet.getX(), y = packet.getY(), z = packet.getZ();
                if (mc.player.getDistanceSq(new BlockPos(x, y, z)) > 1.0f) {
                    addChorusESP(x, y, z);
                    if (rotate.getValue() && BlockUtil.isSelfSafe()){
                        RotationUtil.facePos(new BlockPos(x, y + 1.0f, z));
                    }
                }
            }
        }
    }

    private void addChorusESP(double x, double y, double z) {
        EntityOtherPlayerMP entity = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString(mc.player.getUniqueID().toString()), mc.player.getName()));
        entity.posX = x;
        entity.posY = y;
        entity.posZ = z;
        entity.prevPosX = x;
        entity.prevPosY = y;
        entity.prevPosZ = z;
        entity.rotationYawHead = mc.player.rotationYawHead;
        entity.prevRotationYawHead = mc.player.rotationYawHead;
        entity.rotationYaw = mc.player.rotationYaw;
        entity.rotationPitch = mc.player.rotationPitch;
        entity.prevRotationYaw = mc.player.rotationYaw;
        entity.prevRotationPitch = mc.player.rotationPitch;
        chorusPosses.put(entity, System.currentTimeMillis());
    }
}
