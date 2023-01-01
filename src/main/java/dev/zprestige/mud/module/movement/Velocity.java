package dev.zprestige.mud.module.movement;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.BlockPushEvent;
import dev.zprestige.mud.events.impl.player.EntityCollisionEvent;
import dev.zprestige.mud.events.impl.system.PacketReceiveEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;

public class Velocity extends Module {
    private final BooleanSetting explosions = setting("Explosions", false);
    private final BooleanSetting entities = setting("Entities", false);
    private final BooleanSetting blocks = setting("Blocks", false);

    public Velocity(){
        invokeAppend("0%, 0%");
    }

    @EventListener
    public void onPacketReceive(PacketReceiveEvent event){
        if (!explosions.getValue()){
            return;
        }
        if (event.getPacket() instanceof SPacketEntityVelocity){
            if (((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId()){
                event.setCancelled(true);
            }
        }
        if (event.getPacket() instanceof SPacketExplosion){
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onEntityCollide(EntityCollisionEvent event){
        if (entities.getValue()){
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onBlockPush(BlockPushEvent event){
        if (blocks.getValue()){
            event.setCancelled(true);
        }
    }
}
