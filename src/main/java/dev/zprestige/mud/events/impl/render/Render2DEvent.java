package dev.zprestige.mud.events.impl.render;

import dev.zprestige.mud.events.bus.Event;
import net.minecraft.client.gui.ScaledResolution;

public class Render2DEvent extends Event {
    private final ScaledResolution scaledResolution;
    private final float partialTicks;

    public Render2DEvent(float partialTicks, ScaledResolution scaledResolution) {
        this.partialTicks = partialTicks;
        this.scaledResolution = scaledResolution;
    }


    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
