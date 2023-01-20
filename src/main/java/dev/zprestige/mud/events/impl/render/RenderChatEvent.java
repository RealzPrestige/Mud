package dev.zprestige.mud.events.impl.render;

import dev.zprestige.mud.events.bus.Event;

public class RenderChatEvent extends Event {
    private int updateCounter;

    public RenderChatEvent(int updateCounter){
        this.updateCounter = updateCounter;
    }

    public int getUpdateCounter() {
        return updateCounter;
    }
}
