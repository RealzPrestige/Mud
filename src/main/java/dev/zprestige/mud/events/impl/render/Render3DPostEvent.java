package dev.zprestige.mud.events.impl.render;

import dev.zprestige.mud.events.bus.Event;

public class Render3DPostEvent extends Event {
    private final float partialTicks;

    public Render3DPostEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
