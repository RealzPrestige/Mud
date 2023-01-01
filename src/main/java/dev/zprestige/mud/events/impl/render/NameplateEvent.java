package dev.zprestige.mud.events.impl.render;

import dev.zprestige.mud.events.bus.Event;
import net.minecraft.entity.Entity;

public class NameplateEvent extends Event {
    private final Entity entity;

    public NameplateEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}