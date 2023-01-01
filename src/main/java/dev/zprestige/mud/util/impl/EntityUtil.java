package dev.zprestige.mud.util.impl;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.util.MC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

import java.util.Objects;

public class EntityUtil implements MC {

    public static boolean isMoving() {
        return mc.player.movementInput.moveForward != 0.0f && mc.player.movementInput.moveStrafe != 0.0f;
    }

    public static void setSpeed( double speed) {
        float[] dir = forward(speed);
        mc.player.motionX = dir[0];
        mc.player.motionZ = dir[1];
    }

    public static float getBaseMoveSpeed() {
        float baseSpeed = 0.2873f;
        if (mc.player != null && mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionById(1)))) {
            int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(Objects.requireNonNull(Potion.getPotionById(1)))).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    public static float[] forward(double speed) {
        float forward = mc.player.movementInput.moveForward,
                strafe = mc.player.movementInput.moveStrafe,
                yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (strafe > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            } else if (strafe < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            strafe = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        double sin = Math.sin(Math.toRadians(yaw + 90.0f)),
                cos = Math.cos(Math.toRadians(yaw + 90.0f)),
                posX = forward * speed * cos + strafe * speed * sin,
                posZ = forward * speed * sin - strafe * speed * cos;
        return new float[]{(float) posX, (float) posZ};
    }

    public static EntityPlayer getEntityPlayer(float range) {
        EntityPlayer lowest = null;
        for (EntityPlayer entityPlayer : mc.world.playerEntities) {
            if (entityPlayer.equals(mc.player)) {
                continue;
            }
            if (entityPlayer.isDead || entityPlayer.getHealth() <= 0.0f) {
                continue;
            }
            if (mc.player.getDistance(entityPlayer) > range) {
                continue;
            }
            if (Mud.friendManager.contains(entityPlayer)) {
                continue;
            }
            if (lowest == null || mc.player.getDistance(entityPlayer) < mc.player.getDistance(lowest)) {
                lowest = entityPlayer;
            }
        }
        return lowest;
    }

    public static float getHealth(EntityPlayer entityPlayer) {
        return entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount();
    }
}
