package dev.zprestige.mud.util.impl;

import dev.zprestige.mud.events.impl.player.MotionUpdateEvent;
import dev.zprestige.mud.util.MC;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtil implements MC {


    public static void setClientRotations(float yaw, float pitch) {
        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
    }

    public static void facePos(BlockPos pos) {
        float[] angle = calculateAngle(new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f));
        PacketUtil.invoke(new CPacketPlayer.Rotation(angle[0], angle[1], mc.player.onGround));
    }

    public static void faceAngle(float[] angle, MotionUpdateEvent event) {
        event.setYaw(angle[0]);
        event.setPitch(angle[1]);
    }

    public static void faceEntity(Entity entity) {
        float partialTicks = mc.getRenderPartialTicks();
        float[] angle = calculateAngle(entity.getPositionEyes(partialTicks));
        PacketUtil.invoke(new CPacketPlayer.Rotation(angle[0], angle[1], mc.player.onGround));
    }


    public static void facePos(BlockPos pos, MotionUpdateEvent event) {
        float[] angle = calculateAngle(new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f));
        event.setYaw(angle[0]);
        event.setPitch(angle[1]);
    }


    public static void facePos(Vec3d vec, MotionUpdateEvent event) {
        float[] angle = calculateAngle(vec);
        event.setYaw(angle[0]);
        event.setPitch(angle[1]);
    }

    public static void faceEntity(Entity entity, MotionUpdateEvent event) {
        float partialTicks = mc.getRenderPartialTicks();
        float[] angle = calculateAngle(entity.getPositionEyes(partialTicks));
        event.setYaw(angle[0]);
        event.setPitch(angle[1]);
    }

    public static float[] calculateAngle(Vec3d to) {
        float yaw = (float) (Math.toDegrees(Math.atan2(to.subtract(mc.player.getPositionEyes(1)).z, to.subtract(mc.player.getPositionEyes(1)).x)) - 90);
        float pitch = (float) Math.toDegrees(-Math.atan2(to.subtract(mc.player.getPositionEyes(1)).y, Math.hypot(to.subtract(mc.player.getPositionEyes(1)).x, to.subtract(mc.player.getPositionEyes(1)).z)));
        return new float[]{MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch)};
    }

}

