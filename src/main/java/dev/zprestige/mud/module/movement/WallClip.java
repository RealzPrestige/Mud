package dev.zprestige.mud.module.movement;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.MotionUpdateEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.util.impl.PacketUtil;
import net.minecraft.network.play.client.CPacketPlayer;

public class WallClip extends Module {

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        double rad = Math.toRadians(mc.player.rotationYaw);

        double x = mc.player.posX + (0.056f * -Math.sin(rad));
        double z = mc.player.posZ + (0.056f * Math.cos(rad));

        PacketUtil.invoke(new CPacketPlayer.Position(x, mc.player.posY, z, true));
        mc.player.setPosition(x, mc.player.posY, z);
    }
}
