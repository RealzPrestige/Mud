package dev.zprestige.mud.events.impl.gui;

import dev.zprestige.mud.events.bus.Event;

public class ScrollEvent extends Event {
    private final int mouseX, mouseY, amount;

    public ScrollEvent(int mouseX, int mouseY, int amount){
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }
}
