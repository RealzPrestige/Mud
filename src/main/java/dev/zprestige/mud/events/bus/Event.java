package dev.zprestige.mud.events.bus;

public class Event {
    private boolean cancelled;

    public final boolean isCancelled() {
        return cancelled;
    }

    public final void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}