package dev.zprestige.mud.module.misc;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.render.InterpolateEvent;
import dev.zprestige.mud.module.Module;

public class RemoveInterpolation extends Module {

    @EventListener
    public void onInterpolate(InterpolateEvent event){
        event.setCancelled(true);
    }
}
