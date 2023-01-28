package dev.zprestige.mud.module.movement;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.MoveEvent;
import dev.zprestige.mud.events.impl.system.PacketReceiveEvent;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.ModeSetting;
import dev.zprestige.mud.util.impl.EntityUtil;
import dev.zprestige.mud.util.impl.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.Objects;

public class Speed extends Module {
    private final ModeSetting mode = setting("Strafe", "Strafe", Arrays.asList("Strafe", "Strict Strafe"));
    private final FloatSetting factor = setting("Factor", 1.0f, 0.1f, 2.0f);
    private final BooleanSetting boost = setting("Boost", false);
    private final FloatSetting reduction = setting("Reduction", 1.0f, 0.1f, 10.0f).invokeVisibility(z -> boost.getValue());
    private final FloatSetting decelerate = setting("Decelerate", 1.0f, 0.1f, 10.0f).invokeVisibility(z -> boost.getValue());

    private float multiplier;
    private double playerSpeed;
    private boolean slowdown;

    @EventListener
    public void onTick(TickEvent event){
        multiplier = MathUtil.lerp(multiplier, 0.0f, decelerate.getValue() / 10.0f);
    }

    @EventListener
    public void onMove(MoveEvent event) {
        invokeAppend(mode.getValue());
        if ((mc.player.isInWater() || mc.player.isInLava()) || mc.player.isCreative() || mc.player.isRiding() || mc.player.isElytraFlying()) {
            return;
        }

        mc.player.setSprinting(true);

        double speedY = mode.getValue().equals("Strafe Strict") ? 0.42f : 0.4f;
        if (mc.player.onGround && EntityUtil.isMoving()) {
            if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                speedY += (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST)).getAmplifier() + 1) * 0.1f;
            }
            event.setMotionY(mc.player.motionY = speedY);
            playerSpeed = EntityUtil.getBaseMoveSpeed() * (isColliding(-0.5f) instanceof BlockLiquid ? 0.9f : 1.9f);
            slowdown = true;
        } else {
            if (slowdown || mc.player.collidedHorizontally) {
                playerSpeed -= (isColliding(-0.8f) instanceof BlockLiquid) ? 0.4f : 0.7f * EntityUtil.getBaseMoveSpeed();
                slowdown = false;
            } else {
                playerSpeed -= playerSpeed / 159.0f;
            }
        }

        playerSpeed = Math.max(playerSpeed, EntityUtil.getBaseMoveSpeed());
        float[] forward = EntityUtil.forward(playerSpeed * factor.getValue() * (boost.getValue() ? multiplier + 1.0f : 1.0f));
        event.setMotionX(forward[0]);
        event.setMotionZ(forward[1]);
    }

    @EventListener
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity packet = (SPacketEntityVelocity) event.getPacket();
            if (packet.getEntityID() == mc.player.getEntityId()) {
                multiplier += ((packet.getMotionX() < 0 ? -packet.getMotionX() : packet.getMotionX()) + (packet.getMotionZ() < 0 ? -packet.getMotionZ() : packet.getMotionZ())) / reduction.getValue() / 10000.0f;
            }
        }
    }

    private Block isColliding(double posY) {
        Block block = null;
        if (mc.player != null) {
            final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(0, posY, 0) : mc.player.getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(0, posY, 0);
            int y = (int) bb.minY;
            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
                    block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                }
            }
        }
        return block;
    }


}
