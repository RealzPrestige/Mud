package dev.zprestige.mud.module.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.MoveEvent;
import dev.zprestige.mud.events.impl.system.KeyEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.module.client.Notifications;
import dev.zprestige.mud.setting.impl.BindSetting;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.ModeSetting;
import dev.zprestige.mud.util.impl.EntityUtil;
import net.minecraft.init.MobEffects;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.Objects;

public class Speed extends Module {
    private final ModeSetting mode = setting("Strafe", "Strafe", Arrays.asList("Strafe", "Strict Strafe"));
    private final BooleanSetting liquid = setting("Liquid", false);
    private final FloatSetting factor = setting("Factor", 1.0f, 0.1f, 2.0f);
    private final BindSetting toggleControl = setting("Toggle Control", Keyboard.KEY_NONE);
    private String activeMode = "";
    private float previousDistance, motionSpeed;
    private int currentState = 1;

    @EventListener
    public void onKey(KeyEvent event) {
        if (mc.currentScreen != null || PacketFly.isActive()) {
            return;
        }
        if (toggleControl.getValue() != Keyboard.KEY_NONE) {
            if (event.getKey() == toggleControl.getValue()) {
                activeMode = activeMode.equals(mode.getValue()) ? "Control" : mode.getValue();
                Notifications.post("[Mud] " + ChatFormatting.WHITE + "Speed " + ChatFormatting.GRAY + "mode switched to " + ChatFormatting.WHITE + activeMode + ChatFormatting.GRAY + ".");
            }
        }
    }

    @EventListener
    public void onMove(MoveEvent event) {
        if (!liquid.getValue() && (mc.player.isInWater() || mc.player.isInLava())) {
            return;
        }
        invokeAppend(activeMode);
        if (mc.player.isRiding() || mc.player.isElytraFlying()) {
            return;
        }
        if (activeMode.equals("") || (!activeMode.equals(mode.getValue()) && !activeMode.equals("Control"))) {
            activeMode = mode.getValue();
        }
        mc.player.setSprinting(true);
        NoSlow noSlow = (NoSlow) Mud.moduleManager.getModuleByClass(NoSlow.class);
        float f = noSlow.isEnabled() && noSlow.isSlowed() ? 5.0f : 1.0f;
        if (activeMode.equals("Control")) {
            if (mc.player.isSneaking()) {
                return;
            }
            if (!EntityUtil.isMoving()) {
                event.setMotionX(0.0f);
                event.setMotionZ(0.0f);
            }
            float[] direction = EntityUtil.forward(EntityUtil.getBaseMoveSpeed(), f);
            event.setMotionX(direction[0]);
            event.setMotionZ(direction[1]);
        } else {
            float strafeFactor = factor.getValue();
            switch (currentState) {
                case 0:
                    ++currentState;
                    previousDistance = 0.0f;
                    break;
                case 1:
                default:
                    if ((mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically) && currentState > 0) {
                        currentState = mc.player.moveForward == 0.0F && mc.player.moveStrafing == 0.0F ? 0 : 1;
                    }
                    motionSpeed = previousDistance - previousDistance / 160.0f;
                    break;
                case 2:
                    float motionY = mode.getValue().equals("Strict Strafe") ? 0.42f : 0.40f;
                    if ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) && mc.player.onGround) {
                        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                            motionY += (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST)).getAmplifier() + 1) * 0.1f;
                        }
                        event.setMotionY(mc.player.motionY = motionY);
                        motionSpeed *= 2.149;
                    }
                    break;
                case 3:
                    motionSpeed = previousDistance - 0.76f * (previousDistance - EntityUtil.getBaseMoveSpeed() * strafeFactor);
            }
            motionSpeed = Math.max(motionSpeed, EntityUtil.getBaseMoveSpeed() * strafeFactor);
            float forward = mc.player.movementInput.moveForward / f,
                    strafe = mc.player.movementInput.moveStrafe / f,
                    yaw = mc.player.rotationYaw;
            if (forward != 0.0 && strafe != 0.0) {
                forward *= Math.sin(0.7853981633974483);
                strafe *= Math.cos(0.7853981633974483);
            }
            event.setMotionX((forward * motionSpeed * -Math.sin(Math.toRadians(yaw)) + strafe * motionSpeed * Math.cos(Math.toRadians(yaw))) * 0.99);
            event.setMotionZ((forward * motionSpeed * Math.cos(Math.toRadians(yaw)) - strafe * motionSpeed * -Math.sin(Math.toRadians(yaw))) * 0.99);
            ++currentState;
        }
    }
}
