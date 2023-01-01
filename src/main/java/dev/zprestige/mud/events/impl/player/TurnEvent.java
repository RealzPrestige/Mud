package dev.zprestige.mud.events.impl.player;

import dev.zprestige.mud.events.bus.Event;

public class TurnEvent extends Event {
    private final float yaw, pitch;

    public TurnEvent(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}