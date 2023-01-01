package dev.zprestige.mud.events.impl.system;

import dev.zprestige.mud.events.bus.Event;
import dev.zprestige.mud.module.Module;

public class ToggleEvent extends Event {
    private final Module module;

    public ToggleEvent(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return module;
    }

    public boolean isEnable() {
        return module.getEnabled().getValue();
    }
}
