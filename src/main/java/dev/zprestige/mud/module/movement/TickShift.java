package dev.zprestige.mud.module.movement;

import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.world.TickEvent;
import dev.zprestige.mud.mixins.interfaces.IMinecraft;
import dev.zprestige.mud.mixins.interfaces.ITimer;
import dev.zprestige.mud.module.Module;
import dev.zprestige.mud.setting.impl.BooleanSetting;
import dev.zprestige.mud.setting.impl.FloatSetting;
import dev.zprestige.mud.setting.impl.IntSetting;

public class TickShift extends Module {
    private final IntSetting activeTicks = setting("Active Ticks", 5, 1, 40);
    private final IntSetting thresholdTicks = setting("Threshold Ticks", 20, 1, 100);
    private final FloatSetting timer = setting("Timer", 1.0f, 0.1f, 10.0f);
    private final BooleanSetting whilePacketFly = setting("While Packet Fly", false);
    private int ticks, inactiveTicks;
    private boolean shift;

    @Override
    public void onDisable() {
        if (!Step.isTimer()) {
            ((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50.0f);
        }
        ticks = 0;
        inactiveTicks = 0;
        shift = false;
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (!whilePacketFly.getValue() && PacketFly.isActive()) {
            return;
        }
        if (!shift) {
            if (mc.player.movementInput.moveForward == 0.0f && mc.player.movementInput.moveStrafe == 0.0f) {
                inactiveTicks++;
            } else {
                if (inactiveTicks >= thresholdTicks.getValue()) {
                    shift = true;
                    inactiveTicks = 0;
                }
            }
        }
        if (Blink.isShiftingTicks()){
            shift = false;
        }
        if (shift && ticks > activeTicks.getValue()) {
            if (!Step.isTimer()) {
                ((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50.0f);
            }
            shift = false;
            invokeAppend("");
            return;
        }
        if (shift) {
            if (!Step.isTimer()) {
                ((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50.0f / timer.getValue());
            }
            ticks++;
            invokeAppend(String.valueOf(ticks));
        } else {
            ticks = 0;
        }
    }
}
