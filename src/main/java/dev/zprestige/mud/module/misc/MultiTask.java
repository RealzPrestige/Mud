package dev.zprestige.mud.module.misc;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.DoubleInteractEvent;
import dev.zprestige.mud.module.Module;

public class MultiTask extends Module {

    @EventListener
    public void onDoubleInteract(DoubleInteractEvent event){
        event.setCancelled(true);
    }
}
