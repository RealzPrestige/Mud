package dev.zprestige.mud.events.impl.system;

import dev.zprestige.mud.events.bus.Event;

public class KeyEvent extends Event {
    private final int key;

    public KeyEvent(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
