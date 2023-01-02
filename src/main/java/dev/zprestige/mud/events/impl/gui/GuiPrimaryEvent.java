package dev.zprestige.mud.events.impl.gui;

import dev.zprestige.mud.events.bus.Event;

import java.awt.*;

public class GuiPrimaryEvent extends Event {
    private Color color;

    public GuiPrimaryEvent(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
