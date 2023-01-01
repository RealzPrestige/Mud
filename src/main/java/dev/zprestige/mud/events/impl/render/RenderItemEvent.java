package dev.zprestige.mud.events.impl.render;

import dev.zprestige.mud.events.bus.Event;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;

public class RenderItemEvent extends Event {
    private final ItemCameraTransforms.TransformType transform;

    public RenderItemEvent(ItemCameraTransforms.TransformType transform) {
        this.transform = transform;
    }

    public ItemCameraTransforms.TransformType getTransform() {
        return transform;
    }
}
