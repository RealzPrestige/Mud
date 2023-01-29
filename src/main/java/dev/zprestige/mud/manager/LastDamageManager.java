package dev.zprestige.mud.manager;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.world.LastDamageUpdateEvent;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;

public class LastDamageManager {
    private final HashMap<EntityPlayer, Float> lastDamageMap = new HashMap<>();

    public LastDamageManager() {
        Mud.eventBus.registerListener(this);
    }

    public float getLastDamage(EntityPlayer entityPlayer){
        for (Map.Entry<EntityPlayer, Float> entry : lastDamageMap.entrySet()){
            if (entry.getKey().equals(entityPlayer)){
                return entry.getValue();
            }
        }
        return 0.0f;
    }

    @EventListener
    public void onLastDamageUpdate(LastDamageUpdateEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            lastDamageMap.put((EntityPlayer) event.getEntity(), event.getAmount());
        }
    }
}
