package dev.zprestige.mud.events.impl.player;

import dev.zprestige.mud.events.bus.Event;

public class CameraSetupEvent extends Event {
    private float yaw, pitch;

    public CameraSetupEvent(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
