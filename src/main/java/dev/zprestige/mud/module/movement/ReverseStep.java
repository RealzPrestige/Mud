package dev.zprestige.mud.module.movement;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.FloatSetting;

public class ReverseStep extends Module {
    private final FloatSetting speed = setting("Speed", 1.0f, 0.1f, 5.0f);

    @EventListener
    public void onTick(TickEvent event){
        if (mc.player.onGround){
            mc.player.motionY = -speed.getValue();
        }
    }
}
