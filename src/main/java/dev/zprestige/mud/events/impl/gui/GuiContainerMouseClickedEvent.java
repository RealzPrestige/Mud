package dev.zprestige.mud.events.impl.gui;

import dev.zprestige.mud.events.bus.Event;

public class GuiContainerMouseClickedEvent extends Event {
    private final int mouseX, mouseY, mouseButton;

    public GuiContainerMouseClickedEvent(int mouseX, int mouseY, int mouseButton){
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.mouseButton = mouseButton;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public int getMouseButton() {
        return mouseButton;
    }
}
