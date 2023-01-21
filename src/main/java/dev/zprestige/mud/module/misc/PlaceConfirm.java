package dev.zprestige.mud.module.misc;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.PlaceBlockEvent;
import dev.zprestige.mud.module.Module;

public class PlaceConfirm extends Module {

    @EventListener
    public void onPlaceBlock(PlaceBlockEvent event){
        event.setCancelled(true);
    }
}
