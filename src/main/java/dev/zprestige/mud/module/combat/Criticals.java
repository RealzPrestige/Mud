package dev.zprestige.mud.module.combat;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.system.PacketSendEvent;
import dev.zprestige.mud.mixins.interfaces.IEntity;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.util.impl.EntityUtil;
import dev.zprestige.mud.util.impl.PacketUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;

public class Criticals extends Module {
    private final BooleanSetting whileMoving = setting("While Moving", false);

    @EventListener
    public void onPacketSend(PacketSendEvent event){
        if (event.getPacket() instanceof CPacketUseEntity){
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if (packet.getAction().equals(CPacketUseEntity.Action.ATTACK)&& packet.getEntityFromWorld(mc.world) instanceof EntityPlayer && shouldCritical()){
                if (!whileMoving.getValue() && EntityUtil.isMoving()){
                    return;
                }

                PacketUtil.invoke(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1f, mc.player.posZ, false));
                PacketUtil.invoke(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            }
        }
    }

    private boolean shouldCritical(){
        return mc.player.onGround && !mc.player.isInWater() && !mc.player.isInLava() && !((IEntity) mc.player).isInWeb();
    }
}
