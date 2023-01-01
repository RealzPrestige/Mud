package dev.zprestige.mud.module.movement;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.MoveEvent;
import dev.zprestige.mud.events.impl.system.DisconnectEvent;
import dev.zprestige.mud.events.impl.system.PacketReceiveEvent;
import dev.zprestige.mud.events.impl.system.PacketSendEvent;
import dev.zprestige.mud.mixins.interfaces.ISPacketPlayerPosLook;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.util.impl.PacketUtil;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PacketFly extends Module {
    private final FloatSetting factor = setting("Factor", 1.3f, 0.1f, 5.0f);
    private final Set<CPacketPlayer> allowedCPacketPlayers = new HashSet<>();
    private final HashMap<Integer, Vec3d> allowedPositionsAndIDs = new HashMap<>();
    private long sys;
    private static boolean active;
    private int tpID = -1;

    @Override
    public void onDisable() {
        allowedCPacketPlayers.clear();
        allowedPositionsAndIDs.clear();
        active = false;
    }

    @Override
    public void onEnable() {
        tpID = -1;
        active = true;
        sys = System.currentTimeMillis();
    }

    @EventListener
    public void onMotion(MoveEvent event) {
        invokeAppend(String.valueOf(Math.ceil((System.currentTimeMillis() - sys) / 1000.0f)).replace(".0", ""));
        double motionX, motionY = 0, motionZ;
        boolean antiKicking = false;
        if (mc.player.ticksExisted % 10 == 0 && !mc.world.collidesWithAnyBlock(mc.player.getEntityBoundingBox())) {
            motionY = -.04;
            antiKicking = true;
        } else {
            if (mc.gameSettings.keyBindJump.isKeyDown())
                motionY = .0624;
            else if (mc.gameSettings.keyBindSneak.isKeyDown())
                motionY = -.0624;

        }
        double motionH;
        boolean walls = mc.world.collidesWithAnyBlock(mc.player.getEntityBoundingBox());

        if (walls) {

            motionH = .0624;

            if (motionY != 0) {
                double multiply = 1 / Math.sqrt(2);

                motionY *= multiply;
                motionH *= multiply;

            }

        } else {
            motionH = .2873;
            boolean movingHorizontally = mc.player.moveForward != 0 || mc.player.moveStrafing != 0;

            if (movingHorizontally)
                motionY = Math.min(0, motionY);
        }
        double[] dir = new double[]{0, 0};

        if (!(mc.player.moveForward == 0 && mc.player.moveStrafing == 0)) {

            int strafing = 0;
            int forward = 0;

            if (mc.player.moveStrafing < 0)
                strafing = -1;
            else if (mc.player.moveStrafing > 0)
                strafing = 1;

            if (mc.player.moveForward < 0)
                forward = -1;
            else if (mc.player.moveForward > 0)
                forward = 1;

            float strafe = 90 * strafing;
            strafe *= (forward != 0F) ? forward * 0.5F : 1;

            float yaw = mc.player.rotationYaw - strafe;
            yaw -= (mc.player.moveForward < 0F) ? 180 : 0;

            yaw *= 1 / (180 / Math.PI);

            double x = (-Math.sin(yaw) * motionH);
            double z = (Math.cos(yaw) * motionH);

            dir = new double[]{x, z};

        }

        motionX = dir[0];
        motionZ = dir[1];

        int factorInt = (int) Math.floor(factor.getValue());

        if (mc.player.ticksExisted % 10 < 10 * (factor.getValue() - factorInt))
            factorInt++;

        Vec3d motion = send(motionX, motionY, motionZ, antiKicking, factorInt);

        event.setMotion(motion.x, motion.y, motion.z);

        mc.player.noClip = true;

    }


    @EventListener
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            if (!allowedCPacketPlayers.contains((CPacketPlayer) event.getPacket())) {
                event.setCancelled(true);
            }
        }

    }

    @EventListener
    public void onPacketReceive(PacketReceiveEvent event) {

        if (event.getPacket() instanceof SPacketPlayerPosLook) {

            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
            int id = packet.getTeleportId();
            if (allowedPositionsAndIDs.containsKey(id)) {
                if (allowedPositionsAndIDs.get(id).equals(new Vec3d(packet.getX(), packet.getY(), packet.getZ()))) {
                    allowedPositionsAndIDs.remove(id);
                    PacketUtil.invoke(new CPacketConfirmTeleport(id));
                    event.setCancelled(true);
                    return;

                }
            }

            tpID = id;

            ((ISPacketPlayerPosLook) packet).setYaw(mc.player.rotationYaw);
            ((ISPacketPlayerPosLook) packet).setPitch(mc.player.rotationPitch);

            PacketUtil.invoke(new CPacketConfirmTeleport(id));

        }

    }

    @EventListener
    public void onDisconnect(DisconnectEvent event) {
        toggle();
    }

    private Vec3d send(double motionX, double motionY, double motionZ, boolean antiKick, int factor) {

        for (int i = 1; i < factor + 1; i++) {
            if (antiKick && factor != 1)
                motionY = 0;

            Vec3d pos = mc.player.getPositionVector().add(motionX * i, motionY * i, motionZ * i);

            CPacketPlayer.Position packet = new CPacketPlayer.Position(pos.x, pos.y, pos.z, true);
            CPacketPlayer.Position bounds = new CPacketPlayer.Position(pos.x, pos.y + 512, pos.z, true);

            allowedCPacketPlayers.add(packet);
            allowedCPacketPlayers.add(bounds);

            PacketUtil.invoke(packet);
            PacketUtil.invoke(bounds);

            if (tpID < 0)
                break;

            tpID++;

            PacketUtil.invoke(new CPacketConfirmTeleport(tpID));

            allowedPositionsAndIDs.put(tpID, pos);
        }

        return new Vec3d(motionX * factor, motionY * (antiKick ? 1 : factor), motionZ * factor);
    }

    public static boolean isActive() {
        return active;
    }
}