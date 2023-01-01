package dev.zprestige.mud.module.movement;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.player.InputUpdateEvent;
import dev.zprestige.mud.module.Module;

public class NoSlow extends Module {

    @EventListener
    public void onInputUpdate(InputUpdateEvent event){
        if (slowed()) {
            event.getMovementInput().moveForward /= 0.2f;
            event.getMovementInput().moveStrafe /= 0.2f;
        }
    }

    private boolean slowed() {
        return mc.player.isHandActive() && !mc.player.isRiding() && !mc.player.isElytraFlying();
    }

}
