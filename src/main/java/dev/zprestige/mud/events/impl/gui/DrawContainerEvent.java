package dev.zprestige.mud.events.impl.gui;

import dev.zprestige.mud.events.bus.Event;

public class DrawContainerEvent extends Event {
    private final int mouseX, mouseY, guiLeft, guiTop;
    private final float partialTicks;

    public DrawContainerEvent(int mouseX, int mouseY, float partialTicks, int guiLeft, int guiTop){
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.partialTicks = partialTicks;
        this.guiLeft = guiLeft;
        this.guiTop = guiTop;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public int getX() {
        return guiLeft;
    }

    public int getY() {
        return guiTop;
    }
}
