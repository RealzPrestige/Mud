package dev.zprestige.mud.module.movement;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.MoveEvent;
import dev.zprestige.mud.events.impl.system.PacketReceiveEvent;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.util.impl.EntityUtil;
import dev.zprestige.mud.util.impl.MathUtil;
import net.minecraft.network.play.server.SPacketEntityVelocity;

public class Ground extends Module {
    private final BooleanSetting boost = setting("Boost", false);
    private final FloatSetting reduction = setting("Reduction", 1.0f, 0.1f, 10.0f).invokeVisibility(z -> boost.getValue());
    private final FloatSetting decelerate = setting("Decelerate", 1.0f, 0.1f, 10.0f).invokeVisibility(z -> boost.getValue());

    private float multiplier;

    @EventListener
    public void onTick(TickEvent event){
        multiplier = MathUtil.lerp(multiplier, 0.0f, decelerate.getValue() / 10.0f);
    }

    @EventListener
    public void onMove(MoveEvent event) {
        invokeAppend("Control");
        if ((mc.player.isInWater() || mc.player.isInLava()) || mc.player.isCreative() || mc.player.isRiding() || mc.player.isElytraFlying()) {
            return;
        }
        if (mc.player.isSneaking() || !EntityUtil.isMoving()) {
            return;
        }

        mc.player.setSprinting(true);

        if (!EntityUtil.isMoving()) {
            event.setMotionX(0.0f);
            event.setMotionZ(0.0f);
        }

        float[] direction = EntityUtil.forward(EntityUtil.getBaseMoveSpeed() * (boost.getValue() ? multiplier + 1.0f : 1.0f));
        event.setMotionX(direction[0]);
        event.setMotionZ(direction[1]);
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
}
