package dev.zprestige.mud.events.impl.render;

import dev.zprestige.mud.events.bus.Event;

public class RenderRotationsEvent extends Event {
    private float yaw, pitch;

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
