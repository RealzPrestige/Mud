package dev.zprestige.mud.events.impl.system;

import dev.zprestige.mud.events.bus.Event;

public class RPCDetailsEvent extends Event {
    private String details;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
