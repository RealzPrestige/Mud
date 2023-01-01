package dev.zprestige.mud.events.impl.chat;

import dev.zprestige.mud.events.bus.Event;

public class GuiChatTypeEvent extends Event {
    private final String text;

    public GuiChatTypeEvent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
