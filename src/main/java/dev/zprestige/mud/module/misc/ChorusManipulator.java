package dev.zprestige.mud.module.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.render.Render2DEvent;
import dev.zprestige.mud.events.impl.render.Render3DEvent;
import dev.zprestige.mud.events.impl.system.KeyEvent;
import dev.zprestige.mud.events.impl.system.PacketReceiveEvent;
import dev.zprestige.mud.events.impl.system.PacketSendEvent;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.mixins.interfaces.ISPacketPlayerPosLook;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.module.client.Notifications;
import dev.zprestige.mud.setting.impl.BindSetting;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.ColorSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.shader.impl.BufferGroup;
import dev.zprestige.mud.shader.impl.GlowShader;
import dev.zprestige.mud.util.impl.PacketUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import dev.zprestige.mud.util.impl.RotationUtil;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class ChorusManipulator extends Module {
    private final BindSetting activateKey = setting("Activate Key", Keyboard.KEY_NONE).invokeTab("Chorus");
    private final BooleanSetting rotateOnChorus = setting("Rotate On Chorus", false).invokeTab("Chorus");

    private final FloatSetting speed = setting("Speed", 1.0f, 0.1f, 5.0f).invokeTab("Render");
    private final FloatSetting step = setting("Step", 0.2f, 0.1f, 2.0f).invokeTab("Render");
    private final FloatSetting opacity = setting("Opacity", 150.0f, 0.0f, 255.0f).invokeTab("Render");
    private final FloatSetting lineWidth = setting("Line Width", 1.0f, 0.1f, 5.0f).invokeTab("Render");
    private final ColorSetting color1 = setting("Color 1", new Color(113, 93, 214)).invokeTab("Render");
    private final ColorSetting color2 = setting("Color 2", new Color(113, 220, 214)).invokeTab("Render");

    private final Queue<CPacketPlayer> packets = new LinkedList<>();
    private final Queue<CPacketConfirmTeleport> tpPackets = new LinkedList<>();
    private boolean chorussing;
    private BlockPos pos;
    private long sys;

    private final BufferGroup bufferGroup = new BufferGroup(this, z -> this.pos != null, lineWidth, color1, color2, step, speed, opacity,
            () -> {
                if (pos != null) {
                    AxisAlignedBB bb = new AxisAlignedBB(pos);
                    RenderUtil.drawBB(bb.minX + 0.1f, bb.minY, bb.minZ + 0.1f, bb.maxX - 0.1f, bb.maxY + 1.0f, bb.maxZ - 0.1f);
                }
            }
    );

    @EventListener
    public void onTick(TickEvent event) {
        if (!mc.player.getHeldItemMainhand().getItem().equals(Items.CHORUS_FRUIT) || !mc.gameSettings.keyBindUseItem.isKeyDown()) {
            sys = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - sys > 500) {
            setChorussing(true);
        }
    }

    @EventListener
    public void onKey(KeyEvent event){
        if (activateKey.getValue().equals(Keyboard.KEY_NONE)){
            return;
        }
        if (isChorussing() && activateKey.getValue() == event.getKey()) {
            Notifications.post("[Mud] " + ChatFormatting.WHITE + "ChorusManipulator " + ChatFormatting.GRAY + "teleported to selected position.");
            invokePackets();
        }
    }

    @EventListener
    public void onPacketSend(PacketSendEvent event) {
        if (isChorussing()) {
            if (event.getPacket() instanceof CPacketPlayer) {
                packets.add((CPacketPlayer) event.getPacket());
                event.setCancelled(true);
            }
            if (event.getPacket() instanceof CPacketConfirmTeleport) {
                tpPackets.add((CPacketConfirmTeleport) event.getPacket());
                event.setCancelled(true);
            }
        }
    }

    @EventListener
    public void onPacketReceive(PacketReceiveEvent event) {
        if (isChorussing()) {
            if (event.getPacket() instanceof SPacketPlayerPosLook) {
                final SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
                pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
                if (rotateOnChorus.getValue()) {
                    float[] rotations = RotationUtil.calculateAngle(new Vec3d(packet.getX(), packet.getY() + 1, packet.getZ()));
                    RotationUtil.setClientRotations(rotations[0], rotations[1]);
                }
                ((ISPacketPlayerPosLook) packet).setYaw(mc.player.rotationYaw);
                ((ISPacketPlayerPosLook) packet).setPitch(mc.player.rotationPitch);
                event.setCancelled(true);
            }
        }
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        GlowShader.render3D(bufferGroup);
    }

    @EventListener
    public void onRender2D(Render2DEvent event) {
        GlowShader.render2D(bufferGroup);
    }

    private void invokePackets() {
        while (!packets.isEmpty()) {
            PacketUtil.invokeNoEvent(packets.poll());
        }
        while (!tpPackets.isEmpty()) {
            PacketUtil.invokeNoEvent(tpPackets.poll());
        }
        setChorussing(false);
        pos = null;
    }

    public boolean isChorussing() {
        return chorussing;
    }

    public void setChorussing(boolean chorussing) {
        this.chorussing = chorussing;
    }
}
