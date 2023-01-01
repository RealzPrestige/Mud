package dev.zprestige.mud.events.impl.render;

import dev.zprestige.mud.events.bus.Event;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class RenderOverlayEvent extends Event {
    private final RenderGameOverlayEvent.ElementType elementType;
    private final ScaledResolution scaledResolution;

    public RenderOverlayEvent(RenderGameOverlayEvent.ElementType elementType, ScaledResolution scaledResolution) {
        this.elementType = elementType;
        this.scaledResolution = scaledResolution;
    }

    public RenderGameOverlayEvent.ElementType getElementType() {
        return elementType;
    }

    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }
}
