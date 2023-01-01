package dev.zprestige.mud.events.impl.render;


import dev.zprestige.mud.events.bus.Event;

public class Render3DPreEvent extends Event {
    private final float partialTicks;

    public Render3DPreEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
